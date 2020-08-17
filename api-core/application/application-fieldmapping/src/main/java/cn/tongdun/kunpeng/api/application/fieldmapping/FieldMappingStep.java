package cn.tongdun.kunpeng.api.application.fieldmapping;

import cn.hutool.core.util.ObjectUtil;
import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusiness;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessBusinessCache;
import cn.tongdun.kunpeng.api.engine.model.fieldmapping.AccessParam;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: yuanhang
 * @date: 2020-06-12 18:01
 **/
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 1000)
public class FieldMappingStep implements IRiskStep {

    private Logger logger = LoggerFactory.getLogger(FieldMappingStep.class);

    @Autowired
    AccessBusinessCache accessBusinessCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String appName = context.getAppName();
        String partnerCode = context.getPartnerCode();
        Map<String, AccessBusiness> uuidAccessMap = accessBusinessCache.getAccessBusinessMap();
        if (null == uuidAccessMap) {
            return true;
        }

        AccessBusiness access = uuidAccessMap.get(partnerCode + ":" + appName);
        if (null == access) {
            logger.info(TraceUtils.getFormatTrace() + "access :{} not exits, use default parameters", appName);
            return true;
        }

        List<AccessParam> accessParams = access.getAccessParams().stream().filter(r -> r.getInputOutput().equals("input")).collect(Collectors.toList());
        Map<String, Object> requestParams = request.getFieldValues();
        for (AccessParam accessParam : accessParams) {
            if (requestParams.containsKey(accessParam.getAccessParam())) {
                requestParams.put(accessParam.getFieldName(), requestParams.get(accessParam.getAccessParam()));
            }
            // 常量参数类型
            if ("constant".equals(accessParam.getParamType())) {
                requestParams.put(accessParam.getFieldName(), accessParam.getAccessParam());
            }
        }
        return true;
    }
}
