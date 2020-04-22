package cn.tongdun.kunpeng.api.consumer.es;

import cn.fraudmetrix.module.elasticsearch.SearchService;
import cn.fraudmetrix.module.elasticsearch.basic.ElasticSearchRetry;
import cn.fraudmetrix.module.elasticsearch.exception.ElasticsearchClientException;
import cn.fraudmetrix.module.elasticsearch.vo.FetchAction;
import cn.fraudmetrix.module.elasticsearch.vo.FetchResult;
import cn.fraudmetrix.module.elasticsearch.vo.InsertAction;
import cn.fraudmetrix.module.kafka.object.RetryLaterException;
import cn.tongdun.kunpeng.api.consumer.common.AbstractConsumer;
import cn.tongdun.kunpeng.api.consumer.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class ActivityEsConsumer extends AbstractConsumer {
    private final static Logger logger      = LoggerFactory.getLogger(ActivityEsConsumer.class);

    @Value("${activity.es.retry.times}")
    private int retryTimes;

    @Value("${activity.field}")
    private String activityField;

    @Value("${response.field}")
    private String responseField;

    @Value("${es.switch}")
    private boolean esSwitch;

    @Autowired
    private SearchService searchService;

    @Override
    protected boolean batchConsume() {
        return true;
    }

    @Override
    public void onBulkMessage(String topic, List<Map> messages) {
        List<InsertAction> insertActions = new ArrayList<>();
        for (Map item : messages) {
            String sequenceId = JsonUtil.getString(item,"sequenceId");
            insertActions.add(getAction(sequenceId, item));
        }
        WriteResult writeResult = writeOnce(insertActions, topic);
        try {
            keepWritingEs(writeResult, topic);
        } catch (Exception e) {
            // 将消息保存下来，后面再发
            throw new RetryLaterException();
        }
    }

    private InsertAction getAction(String sequenceId, Map jsonObject) {
        InsertAction insertAction = new InsertAction();
        insertAction.setId(sequenceId);
        insertAction.setTable("activity");
        insertAction.setJson(jsonObject);
        return insertAction;
    }

    // 组装ES批量写入数据结构
    private WriteResult writeOnce(List<InsertAction> actions, String topic) {

        BulkResponse responseEs;
        List<InsertAction> actionEs = new ArrayList<>();
        /********查询入库数据***********/
        Map<String, Long> map = new HashMap<>();
        List<String> seqIds = new ArrayList<>();
        try {
            for (InsertAction action : actions) {
                Map translate = action.getJson();
                // 业务保留最近1年的调用数据（注意区别事件时间(eventOccurTime)不一定是一年内）
                if (translate.get("request") != null) {
                    Object eventOccurTime = ((Map)translate.get("request")).get("eventOccurTime");
                    if (eventOccurTime instanceof Long) {
                        GregorianCalendar gc = new GregorianCalendar() {
                            {
                                setTime(new Date());
                                add(Calendar.YEAR, -1);// 减1年
                            }
                        };
                        if (gc.getTimeInMillis() > (Long) eventOccurTime) {
                            logger.warn("事件时间(eventOccurTime)超过一年,将不存储ES,请通过详情接口查询(hbase) sequenceId:{} topic:{} translate:{}", action.getId(), topic, translate);
                            continue;
                        }
                    }
                }
                //处理action中ES需要的json数据
                Map json = new HashMap();
                Map activity = (Map)translate.get("request");
                Map response = (Map)translate.get("response");
                String sequenceId = action.getId();
                if (esSwitch) {
                    activity = dealJson(activity, activityField);
                    response = dealJson(response, responseField);
                }
                json.put("sequenceId", sequenceId);
                json.put("request", activity);
                json.put("response", response);

                action.setJson(json);
                actionEs.add(action);
                /********查询入库数据***********/
                Long eventOccurTime = JsonUtil.getLong( (Map)translate.get("request"),"eventOccurTime");
                map.put(sequenceId, eventOccurTime);
                seqIds.add(sequenceId);
            }
            responseEs = searchService.bulkInsert(actionEs);

            /********查询入库数据***********/
            for(String k : map.keySet()){
                Map<String,Object> param = new HashMap<>();
                param.put("defaultPartition", map.get(k));

                List<FetchAction> fetchActions = new ArrayList<>();
                fetchActions.add(new FetchAction(k, "activity", param));  // 注意时间戳跟eventOccurTime一样、
                List<FetchResult> result = null;
                try {
                    result = searchService.fetchById(fetchActions);
                    for(FetchResult e : result) {
                        logger.error("es写入数据：sequenceId {}, data : {}", k, JSON.toJSONString(result));
                    }
                } catch (ElasticsearchClientException e) {
                    e.printStackTrace();
                }
            }
            /********查询入库数据结束***********/
        } catch (Exception ex) {
            logger.error("topic write es once failed", ex);
            if (shouldRetry(ex)) {
                return new WriteResult(null, null, actions);
            }
            return new WriteResult(null, actions, null);
        }

        // 汇总结果(hbase无BulkResponse结构出参,无法与es结果汇总,只能全量失败.)
        WriteResult result = new WriteResult(null, null, null);
        processResult(responseEs, actionEs, result);

        // 返回
        return result;
    }


    private void processResult(BulkResponse response, List<InsertAction> actions, WriteResult result) {
        // 没有返回东西，说明全部写入失败了
        if (response == null) {
            return;
        }

        // 找出写入被拒绝的数据、出错的数据、写入成功的数据
        // WriteResult result = new WriteResult(null, null, null);
        int i = 0;
        for (BulkItemResponse e : response) {
            InsertAction action = actions.get(i);
            i++;
            if (!e.isFailed()) {
                result.successActions.add(action);
                continue;
            }
            String failureMessage = e.getFailureMessage();
            logger.error("topic write es/es-hbase once failure: {}", failureMessage);
            if (shouldRetry(failureMessage)) {
                result.rejectedActions.add(action);
            } else {
                result.failedActions.add(action);
            }
        }
    }

    private void keepWritingEs(WriteResult writeResult, String topic) {
        keepWritingEs(writeResult, topic, 0);
    }

    private void keepWritingEs(WriteResult writeResult, String topic, int times) {
        if (times > retryTimes) {
            if (!writeResult.failedActions.isEmpty() || !writeResult.rejectedActions.isEmpty()) {
                // 进入后台模式
                for (InsertAction item : writeResult.failedActions) {
                    logger.error("topic:{}, {}次重试写主库失败了，数据内容: \n {} ", topic, retryTimes,
                            JSON.toJSONString(item.getJson(), true));
                }

                for (InsertAction item : writeResult.rejectedActions) {
                    logger.error("topic:{}, {}次重试写主库被拒绝了，数据内容: \n {} ", topic, retryTimes,
                            JSON.toJSONString(item.getJson(), true));
                }
                throw new RetryLaterException();
            }
            return;
        }
        if (!writeResult.failedActions.isEmpty()) {
            // 写入失败的，继续写入
            sleep();
            WriteResult one = writeOnce(writeResult.failedActions, topic);
            keepWritingEs(one, topic, ++times);
        }

        if (!writeResult.rejectedActions.isEmpty()) {
            // 写入拒绝的，继续写入
            sleep();
            WriteResult one = writeOnce(writeResult.rejectedActions, topic);
            keepWritingEs(one, topic, ++times);
        }
    }

    private boolean shouldRetry(Exception ex) {
        return ElasticSearchRetry.shouldRetry(ex);
    }

    public boolean shouldRetry(String failureMessage) {
        return ElasticSearchRetry.shouldRetry(failureMessage);
    }


    private static class WriteResult {

        private List<InsertAction> successActions  = new ArrayList<>();
        private List<InsertAction> failedActions   = new ArrayList<>();
        private List<InsertAction> rejectedActions = new ArrayList<>();

        public WriteResult(List<InsertAction> successActions, List<InsertAction> failedActions,
                           List<InsertAction> rejectedActions){
            if (successActions == null) {
                successActions = new ArrayList<>();
            }
            if (failedActions == null) {
                failedActions = new ArrayList<>();
            }
            if (rejectedActions == null) {
                rejectedActions = new ArrayList<>();
            }
            this.successActions = successActions;
            this.failedActions = failedActions;
            this.rejectedActions = rejectedActions;
        }

        public int success() {
            return successActions.size();
        }

        public int failed() {
            return failedActions.size();
        }

        public int reject() {
            return rejectedActions.size();
        }

    }

    /**
     * 过滤字段
     * @param json
     * @param fieldStr
     * @return
     */
    private Map dealJson(Map json, String fieldStr){
        if (null != json) {
            Map result = new HashMap();
            String[] keys = fieldStr.split(",");
            for (String key : keys) {
                if (json.containsKey(key)) {
                    result.put(key, json.get(key));
                }
            }
            return result;
        }
        return null;
    }

}
