package cn.tongdun.kunpeng.api.consumer.customlist;

import cn.fraudmetrix.module.kafka.object.RetryLaterException;
import cn.tongdun.kunpeng.api.consumer.common.AbstractConsumer;
import cn.tongdun.kunpeng.api.consumer.customlist.completion.RuleConditionCache;
import cn.tongdun.kunpeng.api.consumer.infrastructure.persistence.repository.intf.CustomListDataService;
import cn.tongdun.kunpeng.api.consumer.util.JsonUtil;
import cn.tongdun.kunpeng.share.dataobject.CustomListDO;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public class RuleTriggerConsumer extends AbstractConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RuleTriggerConsumer.class);

    @Autowired
    private RuleConditionCache ruleConditionCache;

    @Autowired
    private CustomListDataService customListDataService;

    @Autowired
    private IScoreKVRepository scoreKVRepository;

    @Override
    protected boolean batchConsume() {
        return false;
    }

    @Override
    public void onMessage(String topic, Map activity) {
        try{
            logger.info("自定义列表规则监听接收到数据为{}", activity.toString());
            Map response = (Map)activity.get("response");
            if (response == null) {
                return;
            }
            List<Map> subPolicys = (List<Map>)response.get("subPolicys");
            if (subPolicys == null) {
                return;
            }

            Map request = (Map)activity.get("request");
            for (Map subJson : subPolicys) {
                List hitRules = (List)subJson.get("hitRules");
                if (hitRules == null) {
                    return;
                }
                for (Object hit : hitRules) {
                    if (!(hit instanceof Map)) {
                        continue;
                    }
                    Map hitRule = new HashMap((Map) hit);
                    String uuid = JsonUtil.getString(hitRule,"uuid");
                    runRuleTrigger(request, uuid);
                }
            }
        }
        catch (Exception e){
            throw new RetryLaterException();
        }
    }


    private void runRuleTrigger(Map request, String ruleUuid) {
        // 获取触发器。没有触发器就退出
        List<Map> elements = getElementWithCache(ruleUuid);
        if (elements == null) {
            return;
        }

        for (Map json : elements) {
            AddCustomListAction action = AddCustomListAction.parse(json);
            runAddCustomListAction(request, action);
        }
    }


    private List<Map> getElementWithCache(String ruleUuid) {
        return ruleConditionCache.getActionElements(ruleUuid);
    }

    private void runAddCustomListAction(Map request, AddCustomListAction action) {
        // 获取需要添加的数据值
        List matchField = action.getMatchField();
        if (null == matchField) {
            return;
        }
        String fieldList = "";
        int count=0;
        for (Object obj : matchField) {
            String field = (String)obj;
            Object fieldValue = request.get(field);
            if (fieldValue == null) {
                return;
            }
            fieldList += fieldValue.toString();
            if (count < matchField.size()-1) {
                fieldList += ",";
            }
            count++;
        }

        // 添加到自定义列表
        String listUuid = action.getCustomListUuid();
        String partnerCode = JsonUtil.getString(request,"partnerCode");
        CustomListDO customListDO = customListDataService.selectByUuid(listUuid);
        if (null == customListDO) {
            return;
        }
        String appName = customListDO.getAppName();
        String description = "系统自动添加";
        String createBy = "default";
        Date effectTime = new Date();
        Date expireTime = null;

        if(action.getPeriod() != null){

            int gcField = 0;
            switch (action.getUnit()) {
                case "h" :
                    gcField = Calendar.HOUR;
                    break;
                case "d" :
                    gcField = Calendar.DATE;
                    break;
                case "w" :
                    gcField = Calendar.WEEK_OF_YEAR;// 周数: WEEK_OF_MONTH,WEEK_OF_YEAR 效果一样
                    break;
                case "m" :
                    gcField = Calendar.MONTH;
                    break;
                default:
                    break;
            }

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            gc.add(gcField, action.getPeriod());
            expireTime = gc.getTime();

        }else if (action.getExtension() != null) {
            // expireTime = new Date(effectTime.getTime()+action.getExtension()*3600*1000); // bug: int溢出会为负数,导致时间倒退

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(new Date());
            gc.add(Calendar.HOUR, action.getExtension());// 加N小时
            expireTime = gc.getTime();
        }
        String operationType = customListDataService.expireTimeExist(partnerCode,fieldList,listUuid,expireTime);
        if (StringUtils.equals("ignore",operationType)) {
            return;
        } else if (StringUtils.equals("replace",operationType)) {
            customListDataService.replace(partnerCode,listUuid,fieldList,createBy,expireTime);
        } else if (StringUtils.equals("insert",operationType)) {
            customListDataService.insert(partnerCode, appName, listUuid, fieldList, description,
                    createBy,effectTime,expireTime);
        } else {
            logger.warn("ruletrigger has error data {}",fieldList);
        }

        //设置redis缓存
        putCustomListValueData(listUuid,fieldList,expireTime);
    }


    public void putCustomListValueData(String listUuid, String listDataVal,Date expireTime){
        scoreKVRepository.zadd(listUuid, expireTime.getTime(), listDataVal);
    }
}
