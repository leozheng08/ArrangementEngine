package cn.tongdun.kunpeng.api.application.model;

import cn.fraudmetrix.holmes.service.intf.IDubboHolmesApi;
import cn.fraudmetrix.holmes.service.object.ModelCalResponse;
import cn.fraudmetrix.module.tdrule.constant.FieldTypeEnum;
import cn.tongdun.kunpeng.api.application.util.ApplicationContextProvider;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelConfigInfo;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelParam;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelServiceExtPt;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import cn.tongdun.tdframework.core.metrics.IMetrics;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Author: liuq
 * @Date: 2020/5/29 5:37 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsModelService implements ModelServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(UsModelService.class);

    @Resource(
            name = "enterPriseHolmesApi"
    )
    private IDubboHolmesApi enterPriseHolmesApi;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext, ModelConfigInfo configInfo) {

        if (null == configInfo) {
            logger.error(TraceUtils.getFormatTrace() + "IDubboHolmesApi configInfo is null!");
            return false;
        }

        Map<String, Object> modelRequest = buildModelInputParam(fraudContext, configInfo.getInputList());
        modelRequest.put("modelUuid", configInfo.getUuid());
        modelRequest.put("modelType", configInfo.getModelType());
        modelRequest.put("version", configInfo.getModelVersion());
        modelRequest.put("seqId",fraudContext.getSeqId());

        ModelCalResponse modelCalResponse = null;
        try {
            try {
                IMetrics metrics = (IMetrics) ApplicationContextProvider.getBean("prometheusMetricsImpl");
                String[] tags = {
                        "dubbo_qps","holmes.dubbo.IDubboHolmesApi"};
                metrics.counter("kunpeng.api.dubbo.qps",tags);
            }catch (Exception e){

            }
            modelCalResponse = enterPriseHolmesApi.calculate(modelRequest);
            if (null == modelCalResponse) {
                logger.error(TraceUtils.getFormatTrace() + "UsModelService IDubboHolmesApi calculate error,modelCalResponse is null!modelUuid:" + configInfo.getUuid());
                return false;
            }
            if (modelCalResponse.isSuccess()) {
                logger.info(TraceUtils.getFormatTrace() + "holmes result:" + JSON.toJSONString(modelCalResponse));
                mapModelOutput2Context(fraudContext, configInfo.getOutputList(), modelCalResponse.getData());
            } else {
                logger.error(TraceUtils.getFormatTrace() + "IDubboHolmesApi calculate failed,code:" + modelCalResponse.getCode() + ",message:" + modelCalResponse.getMessage());
            }
            return true;
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_TIMEOUT, "holmes");
            } else {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_ERROR, "holmes");
            }
            logger.error(TraceUtils.getFormatTrace() + "UsModelService IDubboHolmesApi calculate error!modelUuid:" + configInfo.getUuid(), e);
        }
        return false;
    }

    private Map<String, Object> buildModelInputParam(AbstractFraudContext fraudContext, List<ModelParam> inputList) {

        Map<String, Object> param = Maps.newHashMap();
        for (ModelParam modelParam : inputList) {
            param.put(modelParam.getField(), getContextDataByType(fraudContext, modelParam));
        }
        return param;
    }

    private Object getContextDataByType(AbstractFraudContext fraudContext, ModelParam modelParam) {
        if (StringUtils.isBlank(modelParam.getRightFieldType()) || StringUtils.isBlank(modelParam.getRightField())) {
            return null;
        }

        String lowerType = modelParam.getRightFieldType().toLowerCase();
        FieldTypeEnum fieldTypeEnum = FieldTypeEnum.getFieldType(lowerType);
        switch (fieldTypeEnum) {
            case CONTEXT:
                return fraudContext.getField(modelParam.getRightField());
            case PLATFORM_INDEX:
                return fraudContext.getPlatformIndexByDataType(modelParam.getRightField(), modelParam.getRightDataType());
            case POLICY_INDEX:
                return fraudContext.getPolicyIndex(modelParam.getRightField());
            case INPUT:
                return modelParam.getRightField();
            default:
                return fraudContext.getField(modelParam.getRightField());
        }
    }

    private void mapModelOutput2Context(AbstractFraudContext context, List<ModelParam> modelOutput, Map<String, Object> responseData) {
        if (CollectionUtils.isEmpty(modelOutput) || MapUtils.isEmpty(responseData)) {
            return;
        }
        for (ModelParam param : modelOutput) {
            context.set(param.getRightField(), responseData.get(param.getField()));
        }
    }


}
