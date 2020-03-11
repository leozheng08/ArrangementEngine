package cn.tongdun.kunpeng.api.application.activity.ext;

import cn.tongdun.kunpeng.api.application.activity.ActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IActitivyMsg;
import cn.tongdun.kunpeng.api.application.activity.IGenerateActivityExtPt;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.data.BizScenario;
import cn.tongdun.kunpeng.common.data.QueueItem;
import cn.tongdun.tdframework.core.extension.Extension;
import com.alibaba.dubbo.common.utils.ConcurrentHashSet;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
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
        actitivy.setSeqId(context.getSeqId());

        actitivy.setRequest(encodeRequest(queueItem.getContext()));
        actitivy.setResponse(queueItem.getResponse());
        actitivy.setSubReasonCodes(context.getSubReasonCodes());
        return actitivy;
    }




    /**
     * 提取FraudContext中所有的系统字段和扩展字段
     */
    private JSONObject encodeRequest(AbstractFraudContext context) {

        //取得上下文中基础的字段
        JSONObject result = getBaseField(context);

        // 获取字段值
        Map<String, Object> fieldValues = context.getFieldValues();
        putAllIfNotExists(result, fieldValues);


        return result;
    }

    private JSONObject getBaseField(AbstractFraudContext context){
        JSONObject result = new JSONObject();
        result.put("partnerCode",context.getPartnerCode());
        result.put("seqId",context.getSeqId());
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
