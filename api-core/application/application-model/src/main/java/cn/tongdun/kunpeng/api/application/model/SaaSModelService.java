package cn.tongdun.kunpeng.api.application.model;

import cn.fraudmetrix.holmes.bean.PredictionInputDTO;
import cn.fraudmetrix.holmes.bean.PredictionOutputDTO;
import cn.fraudmetrix.holmes.service.intf.ICalculateService;
import cn.fraudmetrix.holmes.service.object.ModelCalResponse;
import cn.fraudmetrix.holmes.service.object.ModelResponse;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
import cn.tongdun.kunpeng.api.common.util.ReasonCodeUtil;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelConfigInfo;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelParam;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.ModelServiceExtPt;
import cn.tongdun.kunpeng.api.engine.model.decisionflow.RightFieldType;
import cn.tongdun.kunpeng.api.engine.model.dictionary.DictionaryManager;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.Extension;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service("holmes")
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSModelService implements ModelServiceExtPt {
    private static final Logger logger = LoggerFactory.getLogger(SaaSModelService.class);

    @Autowired
    private ICalculateService holmesService;

    @Autowired
    private DictionaryManager dictionaryManager;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel) {

        //测试调用不调用三方接口，则屏蔽接口
        if (fraudContext.isTestFlag()) {
            logger.warn("SaaSModelService calculate of uuid:{} model testFlag is true",
                    decisionFlowModel.getUuid());
            return true;
        }

        boolean newModel = decisionFlowModel.isNewModel();
        try {
            boolean result;
            if (!newModel) {
                result = runModelV1(fraudContext, decisionFlowModel);
            } else {
                result = runModelV2(fraudContext, decisionFlowModel);
            }
            return result;
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "[Holmes] calculate error {}", e.getMessage());
            return false;
        }
    }


    private boolean runModelV1(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel) {
        Map<String, String> reqParams = generateV1InputParams(fraudContext, decisionFlowModel);
        ModelCalResponse modelCalResponse;
        String modelUuid = reqParams.get("model_uuid");
        try {
            modelCalResponse = holmesService.calculate(reqParams);
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_TIMEOUT, "holmes-api");
            } else {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_ERROR, "holmes-api");
            }
            logger.error(TraceUtils.getFormatTrace() + "[Holmes] calculate catch error,modelUuid : {} ", modelUuid, e);
            return false;
        }

        if (modelCalResponse == null) {
            logger.warn(TraceUtils.getFormatTrace() + "[Holmes] modelCalResponse is null");
            ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_ERROR, "holmes-api");
            return false;
        }

        if (!modelCalResponse.isSuccess()) {
            logger.warn(TraceUtils.getFormatTrace() + "[Holmes] modelCalResponse is failed");
            String subReasonCode = dictionaryManager.getReasonCode("holmes", modelCalResponse.getReasonCode());
            if (StringUtils.isNotEmpty(subReasonCode)) {
                String subReasonCodeMessage = dictionaryManager.getMessage(subReasonCode);
                ReasonCodeUtil.addExtCode(fraudContext, subReasonCode, subReasonCodeMessage, "holmes", "calculate", modelCalResponse.getReasonCode(), modelCalResponse.getReasonMsg());
            }
            return false;
        }

        Map<String, Object> result = modelCalResponse.getData();
        mapOutputs(fraudContext, decisionFlowModel, result);
        return true;
    }

    private boolean runModelV2(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel) {
        PredictionInputDTO predictionInputDTO = generateV2InputParam(fraudContext, decisionFlowModel);
        predictionInputDTO.setAppCode(fraudContext.getAppName());
        String modelUuid = predictionInputDTO.getModelUuid();
        ModelResponse<PredictionOutputDTO> modelResponse;
        try {
            modelResponse = holmesService.predict(predictionInputDTO);
        } catch (Exception e) {
            if (ReasonCodeUtil.isTimeout(e)) {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_TIMEOUT, "holmes-api");
            } else {
                ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_ERROR, "holmes-api");
            }
            logger.error(TraceUtils.getFormatTrace() + "[Holmes] predict catch error, modelUuid : {}", modelUuid, e);
            return false;
        }

        if (modelResponse == null) {
            ReasonCodeUtil.add(fraudContext, ReasonCode.MODEL_RUN_ERROR, "holmes-api");
            logger.error(TraceUtils.getFormatTrace() + "[Holmes] modelResponse is null");
            return false;
        }

        if (!modelResponse.isSuccess()) {
            logger.warn(TraceUtils.getFormatTrace() + "[Holmes] modelCalResponse is failed");
            String subReasonCode = dictionaryManager.getReasonCode("holmes", modelResponse.getReasonCode());
            if (StringUtils.isNotEmpty(subReasonCode)) {
                String subReasonCodeMessage = dictionaryManager.getMessage(subReasonCode);
                ReasonCodeUtil.addExtCode(fraudContext, subReasonCode, subReasonCodeMessage, "holmes", "predict", modelResponse.getReasonCode(), modelResponse.getReasonMsg());
            }
            return false;
        }

        PredictionOutputDTO predictionOutputDTO = modelResponse.getData();
        Map<String, Object> result = predictionOutputDTO.getData();
        mapOutputs(fraudContext, decisionFlowModel, result);
        return true;
    }

    private Map<String, String> generateV1InputParams(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel) {

        Map<String, String> reqParams = new HashMap<>();
        String modelUuid = decisionFlowModel.getUuid();
        String seqId = fraudContext.getSeqId();

        reqParams.put("model_uuid", modelUuid);
        reqParams.put("seq_id", seqId);
        reqParams.put("partner_code", fraudContext.getPartnerCode());
        reqParams.put("app_code", fraudContext.getAppName());
        Map<String, String> inputsMap = mapInputs(fraudContext, decisionFlowModel.getInputList());
        reqParams.putAll(inputsMap);

        return reqParams;
    }

    private Map<String, String> mapInputs(AbstractFraudContext fraudContext, List<ModelParam> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Map<String, String> reqParams = new HashMap<>();
        Object indexResult;
        String value;

        for (ModelParam paramInfo : list) {
            String rightFieldType = paramInfo.getRightFieldType();
            String rightField = paramInfo.getRightField();
            // 指标需要去缓存中取对应信息
            if (StringUtils.equals(rightFieldType, RightFieldType.INDEX.getName())) {
                Object obj = fraudContext.getPolicyIndex(rightField);
                if (obj == null) {
                    value = null;
                } else {
                    value = String.valueOf(obj);
                }
            }
            // 平台指标去缓存中取对应信息
            else if (StringUtils.equalsIgnoreCase(rightFieldType, RightFieldType.GAEA_INDICATRIX.getName())) {
                indexResult = fraudContext.getPlatformIndexByDataType(rightField, paramInfo.getRightDataType());
                if (indexResult == null) {
                    value = null;
                } else {
                    value = String.valueOf(indexResult);
                }
            }
            // 字段
            else {
                value = String.valueOf(fraudContext.getField(rightField));
            }

            reqParams.put(rightField, value);
        }
        return reqParams;
    }


    private void mapOutputs(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel, Map<String, Object> result) {

        String modelUuid = decisionFlowModel.getUuid();
        Map<String, Object> resultOutputMap = Maps.newHashMap(result);
        resultOutputMap.put("policy_set_output_model", modelUuid);
        List<ModelParam> list = decisionFlowModel.getOutputList();
        if (null == list) {
            return;
        }
        for (ModelParam paramInfo : list) {
            String fieldValue = paramInfo.getField();
            String rightFieldValue = paramInfo.getRightField();
            if (result.get(fieldValue) == null) {
                logger.error(TraceUtils.getFormatTrace() + "[Holmes] calculate holmes calculate result is null");
                continue;
            }
            fraudContext.set(rightFieldValue, result.get(fieldValue));
            String ruleFiled = KunpengStringUtils.camel2underline(rightFieldValue);
            if (!resultOutputMap.keySet().contains(ruleFiled)) {
                resultOutputMap.put(ruleFiled, result.get(fieldValue));
                resultOutputMap.remove(fieldValue);
            }
        }

        Iterator<Map.Entry<String, Object>> it = resultOutputMap.entrySet().iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (value == null) {
                it.remove();
            }
        }
        if (decisionFlowModel.isRiskServiceOutput()) {
            fraudContext.appendModelOutputFields(resultOutputMap);
        }

    }

    private PredictionInputDTO generateV2InputParam(AbstractFraudContext fraudContext, ModelConfigInfo decisionFlowModel) {

        String modelUuid = decisionFlowModel.getUuid();
        String seqId = fraudContext.getSeqId();

        Map<String, String> inputsMap = mapInputs(fraudContext, decisionFlowModel.getInputList());
        String modelVersion = decisionFlowModel.getModelVersion();

        PredictionInputDTO predictionInputDTO = new PredictionInputDTO();
        predictionInputDTO.setModelUuid(modelUuid);
        predictionInputDTO.setSeqId(seqId);
        predictionInputDTO.setPartnerCode(fraudContext.getPartnerCode());
        predictionInputDTO.setModelVersion(modelVersion);
        predictionInputDTO.setParams(inputsMap);

        return predictionInputDTO;

    }
}
