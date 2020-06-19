package cn.tongdun.kunpeng.api.application.response.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.access.AccessParam;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-06-16 11:26
 **/
@Component
@Step(pipeline = Risk.NAME, phase = Risk.OUTPUT, order = 3500)
public class ResponseAdjustStep implements IRiskStep {

    private Logger logger = LoggerFactory.getLogger(ResponseAdjustStep.class);

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String appName = context.getAppName();
        Map<String, AccessBusiness> uuidAccessMap = accessBusinessCache.getAccessBusinessMap();
        if (null == appName) {
            logger.info(TraceUtils.getFormatTrace() + "null appName, skip response adjust");
            return true;
        }
        AccessBusiness access = uuidAccessMap.get(appName);
        if (null == access) {
            logger.info(TraceUtils.getFormatTrace() + " access :{} not exits, use default response", appName);
            return true;
        }
        List<AccessParam> accessParams = access.getAccessParams().stream().filter(r -> r.getInputOutput().equals("output")).collect(Collectors.toList());
        // 确定不输出的数据量，少的话，可以用枚举，反射耗时太长
        accessParams.stream().forEach(accessParam -> {
            if ("1".equals(accessParam.getIsMust())) {
                switch (accessParam.getFieldName()) {
                    case "subPolicies":
                        response.setSubPolicys(null);
                        break;
                    case "hitRules":
                        List<ISubPolicyResult> subPolicies = response.getSubPolicys();
                        subPolicies.stream().forEach(r -> r.setHitRules(null));
                        response.setSubPolicys(subPolicies);
                        break;
                    default:
                        break;
                }
            } else {
                Map customPolicyResult = response.getCustomPolicyResult();
                injectExtraOutput(customPolicyResult,accessParam, context);
                response.setCustomPolicyResult(customPolicyResult);
            }
        });
        return true;
    }

    /**
     * TODO 优化代码逻辑
     */
    private void injectExtraOutput(Map customPolicyResult,AccessParam accessParam,AbstractFraudContext context) {
        if (accessParam.getFieldName().equals("eventType")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getEventType());
        }else if (accessParam.getFieldName().equals("eventId")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getEventId());
        }else if (accessParam.getFieldName().equals("eventOccurTime")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getEventOccurTime());
        }else if (accessParam.getFieldName().equals("seqId")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getSeqId());
        }else if (accessParam.getFieldName().equals("appType")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getAppType());
        }else if (accessParam.getFieldName().equals("serviceType")) {
            customPolicyResult.put(accessParam.getAccessParam(), context.getServiceType());
        }
    }
}
