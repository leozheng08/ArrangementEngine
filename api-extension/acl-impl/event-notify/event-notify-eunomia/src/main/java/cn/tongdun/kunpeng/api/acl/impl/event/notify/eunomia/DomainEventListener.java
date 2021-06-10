package cn.tongdun.kunpeng.api.acl.impl.event.notify.eunomia;

import cn.fraudmetrix.eunomia.client.listener.EunomiaListener;
import cn.fraudmetrix.eunomia.client.message.RowData;
import cn.fraudmetrix.eunomia.recipes.message.Row;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.kv.IScoreKVRepository;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * Created by liupei on 2021/2/18.
 * 数据总线监听domain_event表，并写入redis缓存
 */
@Slf4j
public class DomainEventListener implements EunomiaListener {


    @Autowired
    private IScoreKVRepository scoreKVRepository;

    //取最近几分钟数据
    protected static final int LAST_MINUTES = 3;

    //放到redis缓存上的超期时间
    private static final long EXPIRE_TIME = (LAST_MINUTES + 1) * 60 * 1000L;

    /**
     * 公共配置，登录相关
     */
    @Value("${open.domain.event.log:false}")
    private boolean openDomainEventLog;


    @Override
    public boolean onEvent(RowData rowData) throws Exception {

        if (openDomainEventLog) {
            log.info("DomainEventListener start.................................rowData={}", JSON.toJSONString(rowData));
        }


        if (rowData == null) {
            log.error("eunomia(DomainEventListener) client rowData not allowed null");
            return true;
        }

        Row row = new Row(rowData);
        String tableName = row.getTableName();
        if (StringUtils.isBlank(tableName) || !"domain_event".equalsIgnoreCase(tableName)) {
            log.error("eunomia(DomainEventListener) client table name not allowed null");
            return true;

        }

        if (StringUtils.isBlank(tableName) || !"domain_event".equalsIgnoreCase(tableName)) {
            log.error("eunomia(DomainEventListener) client table name not allowed null");
            return true;

        }

        if (!"INSERT".equals(row.getEventType())) {
            log.error("eunomia(DomainEventListener) client eventType not allowed !insert");
            return true;
        }

        //{"eventType":"deactivate","data":[{"uuid":"7d6ef5a5a3b946129691af608916f225"}],"occurredTime":1586433141469,"entity":"policy_definition"}
        String eventData = getRowValue(row, "event_data");
        Map<String, Object> messageMap = JSON.parseObject(eventData, Map.class);
        long occurredTime = getLong(messageMap, "occurredTime");
        List<Map> jsonArray = (List<Map>) messageMap.get("data");

        Map redisMsg = Maps.newHashMap();
        redisMsg.put("eventType", getRowValue(row, "event_type"));
        redisMsg.put("entity", getRowValue(row, "entity"));
        redisMsg.put("occurredTime", occurredTime);
        redisMsg.put("data", jsonArray);

        putEventMsgToRemoteCache(JSON.toJSONString(redisMsg), occurredTime);

        return true;
    }

    /**
     * 领域事件写入redis
     */
    private void putEventMsgToRemoteCache(String eventMsg, Long occurredTime) {
        String currentKey = DateUtil.getYYYYMMDDHHMMStr();
        if (openDomainEventLog) {
            log.info("DomainEventListener start...............write redis. currentKey={}, occurredTime={}, eventMsg={}", currentKey, occurredTime, eventMsg);
        }
        scoreKVRepository.zadd(currentKey, occurredTime, eventMsg);
        scoreKVRepository.setTtl(currentKey, EXPIRE_TIME);
//        Set<IScoreValue> scoreValueSet = new LinkedHashSet<>();
//        try {
//            for (int i = LAST_MINUTES - 1; i >= 0; i--) {
//                String lastKey = DateUtil.getLastMinuteStr(i);
//                Set<IScoreValue> scoreValueSetTmp = scoreKVRepository.zrangeByScoreWithScores(lastKey, 0, Long.MAX_VALUE);
//                if (scoreValueSetTmp.isEmpty()) {
//                    continue;
//                }
//
//                scoreValueSet.addAll(scoreValueSetTmp);
//            }
//
//            log.info("DomainEventListener start...............write redis end. currentKey={}, scoreValueSet={}", currentKey, JSON.toJSONString(scoreValueSet));
//        } catch (Exception e) {
//            log.error(TraceUtils.getFormatTrace() + "update rule form redis error!", e);
//        }


    }


    private String getRowValue(Row row, String key) {
        if (row == null) {
            return null;
        }
        if (row.getReader().getField(key) == null) {
            return null;
        }
        return StringUtils.isBlank(row.getReader().getField(key).getBeforeValue()) ?
                row.getReader().getField(key).getAfterValue() :
                row.getReader().getField(key).getBeforeValue();
    }

    public static Long getLong(Map map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        return Double.valueOf(value.toString()).longValue();
    }


}
