package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.common.config.ILocalEnvironment;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelNode extends AbstractBizNode {

    private static Logger logger = LoggerFactory.getLogger(ModelNode.class);
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

            modelConfigInfo.setModelVersion(JsonUtil.getString(json, "version"));
            modelConfigInfo.setModelType(JsonUtil.getString(json,"modelType"));
        } catch (Exception e) {
            logger.error("ModelNode parse error,nodeId:" + this.getId() + ",modelConfig:" + modelConfig, e);
            throw new ParseException("modelConfig parse json error, modelConfig : " + modelConfig);
        }
        //必填检验
        if (StringUtils.isBlank(modelConfigInfo.getUuid())) {
            throw new ParseException("Node id:" + this.getId() + ",model uuid cann't be blank!");
        }
        if (StringUtils.isBlank(modelConfigInfo.getModelVersion())) {
            throw new ParseException("Node id:" + this.getId() + ",model version cann't be blank!");
        }
        if (CollectionUtils.isEmpty(modelConfigInfo.getInputList())) {
            throw new ParseException("Node id:" + this.getId() + ",model input  cann't be empty!");
        }
        if (CollectionUtils.isEmpty(modelConfigInfo.getOutputList())) {
            throw new ParseException("Node id:" + this.getId() + ",model output  cann't be empty!");
        }

        //有点问题，把租户的概念引入到这里，后面有时间再简单重构下，把模型的解析和模型的执行，跟业务身份关联起来
        ILocalEnvironment localEnvironment= (ILocalEnvironment) SpringContextHolder.getBean("localEnvironment");
        if (StringUtils.equalsIgnoreCase(localEnvironment.getTenant(),"us")&&StringUtils.isBlank(modelConfigInfo.getModelType())){
            throw new ParseException("Node id:" + this.getId() + ",model type  cann't be empty!");
        }


    }

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        AbstractFraudContext context = (AbstractFraudContext) executeContext;
        try {
            ExtensionExecutor extensionExecutor = (ExtensionExecutor) SpringContextHolder.getBean("extensionExecutor");
            boolean ret = extensionExecutor.execute(ModelServiceExtPt.class, context.getBizScenario(), extension -> extension.calculate(context, modelConfigInfo));
            nodeResult.putOneResult(modelConfigInfo.getUuid(), ret);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "Model run error,modelUuid:" + modelConfigInfo.getUuid(), e);
        }

        return nodeResult;
    }

    @Override
    public int getRunCost() {
        return 30;
    }

    /**
     * 解析输入|输出参数
     *
     * @param array
     * @return
     */
    private List<ModelParam> buildParaInfo(List<Map> array) {
        if (!CollectionUtils.isEmpty(array)) {
            List<ModelParam> modelParamList = Lists.newArrayList();
            array.forEach(json -> {
                ModelParam paramInfo = new ModelParam();
                paramInfo.setField(JsonUtil.getString(json, "field"));
                paramInfo.setRightField(JsonUtil.getString(json, "rightField"));
                paramInfo.setRightFieldType(JsonUtil.getString(json, "rightFieldType"));
                paramInfo.setRightFieldName(JsonUtil.getString(json, "rightFieldName"));
                paramInfo.setRightDataType(JsonUtil.getString(json, "rightDataType"));
                modelParamList.add(paramInfo);
            });
            return modelParamList;
        }
        return null;
    }


}
