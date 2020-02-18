package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

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
            JSONObject json = JSONObject.parseObject(modelConfig);
            decisionFlowModel.setUuid(json.getString("uuid"));
            decisionFlowModel.setInputList(buildParaInfo(json.getJSONArray("inputs")));
            decisionFlowModel.setOutputList(buildParaInfo(json.getJSONArray("outputs")));
            boolean isNewModel = false;
            if (json.containsKey("newModel")) {
                isNewModel = json.getBoolean("newModel");
            }
            decisionFlowModel.setNewModel(isNewModel);

            boolean isRiskServiceOutput = false;
            if (json.containsKey("isRiskServiceOutput")) {
                isRiskServiceOutput = json.getBoolean("isRiskServiceOutput");
            }

            decisionFlowModel.setRiskServiceOutput(isRiskServiceOutput);
            decisionFlowModel.setModelVersion(json.getString("modelVersion"));
        } catch (Exception e) {

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
    private List<ModelParamInfo> buildParaInfo(JSONArray array) {
        if (null != array) {
            List<ModelParamInfo> list = Lists.newArrayList();
            array.stream().forEach(o -> {
                ModelParamInfo paramInfo = new ModelParamInfo();
                JSONObject json = (JSONObject)o;
                paramInfo.setField(json.getString("field"));
                paramInfo.setRightField(json.getString("rightField"));
                paramInfo.setRightFieldType(json.getString("rightFieldType"));
                paramInfo.setRightFieldName(json.getString("rightFieldName"));
                list.add(paramInfo);
            });
            return list;
        }
        return null;
    }


}
