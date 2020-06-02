package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelNode extends AbstractBizNode {


    private ModelConfigInfo modelConfigInfo;


    @Override
    public void parse(NodeDesc nodeDesc) {
        this.setId(nodeDesc.getId());
        Map<String, String> paramMap = nodeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        String modelConfig = (String) paramMap.get("tns:modelTask");
        if (StringUtils.isBlank(modelConfig)) {
            throw new ParseException("Model Node id:" + this.getId() + " tns:modelTask is blank!");
        }
        try {
            modelConfigInfo = new ModelConfigInfo();
            Map json = JSON.parseObject(modelConfig, HashMap.class);
            modelConfigInfo.setUuid(JsonUtil.getString(json, "uuid"));
            modelConfigInfo.setInputList(buildParaInfo((List) json.get("inputs")));
            modelConfigInfo.setOutputList(buildParaInfo((List) json.get("outputs")));
            boolean isNewModel = false;
            if (json.containsKey("newModel")) {
                isNewModel = JsonUtil.getBoolean(json, "newModel");
            }
            modelConfigInfo.setNewModel(isNewModel);

            boolean isRiskServiceOutput = false;
            if (json.containsKey("isRiskServiceOutput")) {
                isRiskServiceOutput = JsonUtil.getBoolean(json, "isRiskServiceOutput");
            }

            modelConfigInfo.setRiskServiceOutput(isRiskServiceOutput);
            modelConfigInfo.setModelVersion(JsonUtil.getString(json, "modelVersion"));
        } catch (Exception e) {
            throw new ParseException("modelConfig parse json error, modelConfig : " + modelConfig);
        }
    }

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        ModelServiceExtPt holmes;
        try {
            holmes = (ModelServiceExtPt) SpringContextHolder.getBean("holmes");
            holmes.calculate((AbstractFraudContext) executeContext, modelConfigInfo);
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
     *
     * @param array
     * @return
     */
    private List<ModelParam> buildParaInfo(List<Map> array) {
        if (null != array) {
            List<ModelParam> list = Lists.newArrayList();
            array.stream().forEach(json -> {
                ModelParam paramInfo = new ModelParam();
                paramInfo.setField(JsonUtil.getString(json, "field"));
                paramInfo.setRightField(JsonUtil.getString(json, "rightField"));
                paramInfo.setRightFieldType(JsonUtil.getString(json, "rightFieldType"));
                paramInfo.setRightFieldName(JsonUtil.getString(json, "rightFieldName"));
                list.add(paramInfo);
            });
            return list;
        }
        return null;
    }


}
