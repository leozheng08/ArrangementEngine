package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.common.ActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.common.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.common.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: liang.chen
 * @Date: 2020/3/4 下午4:00
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class GenerateActivityExt implements IGenerateActivityExtPt{

    private Map<String, Method> systemFieldGetter;


    /**
     * 根据出入参、上下文生成Activity消息
     * @param queueItem
     * @return
     */
    @Override
    public IActitivyMsg generateActivity(QueueItem queueItem){
        AbstractFraudContext context = queueItem.getContext();

        ActitivyMsg actitivy = new ActitivyMsg();
        actitivy.setProduceTime(System.currentTimeMillis());
        actitivy.setSequenceId(context.getSeqId());

        actitivy.setRequest(encodeRequest(queueItem.getContext()));
        actitivy.setResponse(queueItem.getResponse());
        actitivy.setSubReasonCodes(context.getSubReasonCodes());
        return actitivy;
    }




    /**
     * 提取FraudContext中所有的系统字段和扩展字段
     */
    private Map encodeRequest(AbstractFraudContext context) {

        //取得上下文中基础的字段
        Map result = getBaseField(context);

        // 获取字段值
        Map<String, Object> fieldValues = context.getFieldValues();
        putAllIfNotExists(result, fieldValues);


        return result;
    }

    private Map getBaseField(AbstractFraudContext context){
        Map result = new HashMap();
        result.put("partnerCode",context.getPartnerCode());
        result.put("sequenceId",context.getSeqId());
        result.put("appName",context.getAppName());
        result.put("appType",context.getAppType());
        result.put("eventId",context.getEventId());
        result.put("eventType",context.getEventType());
        result.put("policyUuid",context.getPolicyUuid());
        result.put("eventOccurTime",context.getEventOccurTime());
        result.put("policyVersion",context.getPolicyVersion());
        result.put("requestId",context.getRequestId());
        return result;
    }

    private void putAllIfNotExists(Map<String, Object> result, Map<String, Object> map) {
        for (String fieldName : map.keySet()) {
            if (result.containsKey(fieldName)) {
                continue;
            }
            Object fieldValue = map.get(fieldName);
            if (fieldValue == null) {
                continue;
            }

            result.put(fieldName, fieldValue);
        }
    }
}
