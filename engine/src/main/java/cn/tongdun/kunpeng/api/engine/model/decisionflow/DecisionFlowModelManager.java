package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DecisionFlowModelManager extends AbstractBizNode {


    private DecisionFlowModel decisionFlowModel;


    @Override
    public void parse(NodeDesc nodeDesc) {
        this.setId(nodeDesc.getId());
        Map<String, String> paramMap = (Map)nodeDesc.getParamList().stream().collect(Collectors.toMap((a) -> {
            return a.getName();
        }, (b) -> {
            return b.getValue();
        }));
        String modelConfig = (String)paramMap.get("tns:modelTask");
        if (StringUtils.isBlank(modelConfig)) {
            throw new ParseException("Model Node id:" + this.getId() + " tns:modelTask is blank!");
        }
        try {
            decisionFlowModel = new DecisionFlowModel();
            Map json = JSON.parseObject(modelConfig, HashMap.class);
            decisionFlowModel.setUuid(JsonUtil.getString(json,"uuid"));
            decisionFlowModel.setInputList(buildParaInfo((List)json.get("inputs")));
            decisionFlowModel.setOutputList(buildParaInfo((List)json.get("outputs")));
            boolean isNewModel = false;
            if (json.containsKey("newModel")) {
                isNewModel = JsonUtil.getBoolean(json,"newModel");
            }
            decisionFlowModel.setNewModel(isNewModel);

            boolean isRiskServiceOutput = false;
            if (json.containsKey("isRiskServiceOutput")) {
                isRiskServiceOutput = JsonUtil.getBoolean(json,"isRiskServiceOutput");
            }

            decisionFlowModel.setRiskServiceOutput(isRiskServiceOutput);
            decisionFlowModel.setModelVersion(JsonUtil.getString(json,"modelVersion"));
        } catch (Exception e) {
            throw new ParseException("modelConfig parse json error, modelConfig : " + modelConfig);
        }
    }

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        IHolmes holmes;
        try {
            holmes = (IHolmes) SpringContextHolder.getBean("holmes");
            holmes.calculate((AbstractFraudContext) executeContext, decisionFlowModel);
        } catch (Exception e) {
        }
        nodeResult.putOneResult("Thread", Thread.currentThread().getName());
        return nodeResult;
    }

    @Override
    public int getRunCost() {
        return 0;
    }

    /**
     * 解析输入|输出参数
     * @param array
     * @return
     */
    private List<ModelParamInfo> buildParaInfo(List<Map> array) {
        if (null != array) {
            List<ModelParamInfo> list = Lists.newArrayList();
            array.stream().forEach(json -> {
                ModelParamInfo paramInfo = new ModelParamInfo();
                paramInfo.setField(JsonUtil.getString(json,"field"));
                paramInfo.setRightField(JsonUtil.getString(json,"rightField"));
                paramInfo.setRightFieldType(JsonUtil.getString(json,"rightFieldType"));
                paramInfo.setRightFieldName(JsonUtil.getString(json,"rightFieldName"));
                list.add(paramInfo);
            });
            return list;
        }
        return null;
    }


}
