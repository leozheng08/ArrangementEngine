package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.holmes.bean.PredictionInputDTO;
import cn.fraudmetrix.holmes.bean.PredictionOutputDTO;
import cn.fraudmetrix.holmes.service.intf.ICalculateService;
import cn.fraudmetrix.holmes.service.object.ModelCalResponse;
import cn.fraudmetrix.holmes.service.object.ModelResponse;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.KunpengStringUtils;
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
public class Holmes implements IHolmes{
    private static final Logger logger = LoggerFactory.getLogger(Holmes.class);

    @Autowired
    private ICalculateService holmesService;

    @Override
    public boolean calculate(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel) {

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
            logger.error("[Holmes] calculate error {}", e.getMessage());
            return false;
        }
    }


    private boolean runModelV1(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel) {
        Map<String, String> reqParams = generateV1InputParams(fraudContext, decisionFlowModel);
        ModelCalResponse modelCalResponse;
        String modelUuid = reqParams.get("model_uuid");
        try {
            modelCalResponse = holmesService.calculate(reqParams);
        } catch (Exception e) {
            logger.error("[Holmes] calculate catch error,modelUuid : {} ", modelUuid, e);
            return false;
        }

        if (modelCalResponse == null) {
            logger.warn("[Holmes] modelCalResponse is null");
            return false;
        }

        if (!modelCalResponse.isSuccess()) {
            logger.warn("[Holmes] modelCalResponse is failed");
            return false;
        }

        Map<String, Object> result = modelCalResponse.getData();
        mapOutputs(fraudContext, decisionFlowModel, result);
        return true;
    }

    private boolean runModelV2(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel) {
        PredictionInputDTO predictionInputDTO = generateV2InputParam(fraudContext, decisionFlowModel);
        String modelUuid = predictionInputDTO.getModelUuid();
        ModelResponse<PredictionOutputDTO> modelResponse;
        try {
            modelResponse = holmesService.predict(predictionInputDTO);
        } catch (Exception e) {
            logger.error("[Holmes] predict catch error, modelUuid : {}", modelUuid, e);
            return false;
        }

        if (modelResponse == null) {
            logger.error("[Holmes] modelResponse is null" );
            return false;
        }

        if (!modelResponse.isSuccess()) {
            logger.warn("[Holmes] modelCalResponse is failed");
            return false;
        }

        PredictionOutputDTO predictionOutputDTO = modelResponse.getData();
        Map<String, Object> result = predictionOutputDTO.getData();
        mapOutputs(fraudContext, decisionFlowModel, result);
        return true;
    }

    private Map<String, String> generateV1InputParams(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel) {

        Map<String, String> reqParams = new HashMap<>();
        String modelUuid = decisionFlowModel.getUuid();
        String seqId = fraudContext.getSeqId();

        reqParams.put("model_uuid", modelUuid);
        reqParams.put("seq_id", seqId);
        reqParams.put("partner_code", fraudContext.getPartnerCode());
        Map<String, String> inputsMap = mapInputs(fraudContext, decisionFlowModel.getInputList());
        reqParams.putAll(inputsMap);

        return reqParams;
    }

    private Map<String, String> mapInputs(AbstractFraudContext fraudContext, List<ModelParamInfo> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        Map<String, String> reqParams = new HashMap<>();
        Double indexResult;
        String value;

        for (ModelParamInfo paramInfo : list) {
            String rightFieldType = paramInfo.getRightFieldType();
            String rightField = paramInfo.getRightField();
            // 指标需要去缓存中取对应信息
            if (StringUtils.equals(rightFieldType, RightFieldType.INDEX.getName())) {
               Object obj =fraudContext.getPolicyIndex(rightField);
               if (obj == null) {
                   value = null;
               } else {
                   value = String.valueOf(obj);
               }
            }
            // 平台指标去缓存中取对应信息
            else if (StringUtils.equalsIgnoreCase(rightFieldType, RightFieldType.GAEA_INDICATRIX.getName())) {
                indexResult = fraudContext.getPlatformIndex(rightField);
                if (indexResult == null || Double.isNaN(indexResult)) {
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


    private void mapOutputs(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel, Map<String, Object> result) {

        String modelUuid = decisionFlowModel.getUuid();
        Map<String, Object> resultOutputMap = Maps.newHashMap(result);
        resultOutputMap.put("policy_set_output_model", modelUuid);
        List<ModelParamInfo> list = decisionFlowModel.getOutputList();
        if (null == list) {
            return;
        }
        for(ModelParamInfo paramInfo : list) {
            String fieldValue = paramInfo.getField();
            String rightFieldValue = paramInfo.getRightField();
            if (result.get(fieldValue) == null) {
                logger.error("[Holmes] calculate holmes calculate result is null");
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

    private PredictionInputDTO generateV2InputParam(AbstractFraudContext fraudContext, DecisionFlowModel decisionFlowModel) {

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
