package cn.tongdun.kunpeng.api.application.response.step;

import cn.tongdun.kunpeng.api.application.response.entity.USRiskResponse;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.access.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.access.AccessParam;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.ISubPolicyResult;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.client.data.impl.us.PolicyResult;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-06-16 11:26
 * 根据用户配置调整输出结果
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
            // 不输出的情况
            if (Byte.valueOf("0").equals(accessParam.getIsMust())) {
                switch (accessParam.getFieldName()) {
                    case "subPolicies":
                        if (response instanceof USRiskResponse) {
                            PolicyResult policyResult = (PolicyResult) response.getPolicyDetailResult();
                            policyResult.setPolicySet(null);
                            response.setPolicyDetailResult(policyResult);
                        } else {
                            response.setSubPolicys(null);
                        }
                        break;
                    case "hitRules":
                        if (response instanceof USRiskResponse) {
                            PolicyResult policyResult = (PolicyResult) response.getPolicyDetailResult();
                            if (null != policyResult && CollectionUtils.isNotEmpty(policyResult.getPolicySet())) {
                                policyResult.getPolicySet().stream().forEach(sub -> sub.setHitRules(null));
                                response.setPolicyDetailResult(policyResult);
                            }
                        } else {
                            List<ISubPolicyResult> subPolicyResults = response.getSubPolicys();
                            if (CollectionUtils.isNotEmpty(subPolicyResults)) {
                                subPolicyResults.stream().forEach(sub -> sub.setHitRules(null));
                                response.setSubPolicys(subPolicyResults);
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else {
                injectExtraOutput(response, accessParam, context, request);
            }
        });
        return true;
    }

    /**
     * TODO 优化代码逻辑
     */
    private void injectExtraOutput(IRiskResponse response, AccessParam accessParam, AbstractFraudContext context, RiskRequest request) {
        Map customPolicyResult = response.getCustomPolicyResult();
        if (null == customPolicyResult) {
            customPolicyResult = Maps.newHashMap();
        }
        String param = accessParam.getAccessParam();
        // 现在context的fieldValues中找，找不到再去request中找
        customPolicyResult.put(param, context.getFieldValues().get(accessParam.getFieldName()));
        if (null == customPolicyResult.get(accessParam.getAccessParam())) {
            customPolicyResult.put(param,request.get(accessParam.getFieldName()));
        }
        response.setCustomPolicyResult(customPolicyResult);
    }
}
