package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

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

/**
 * @Author: liang.chen
 * @Date: 2020/2/11 下午3:10
 */
public class InterfaceDefinitionManager extends AbstractBizNode {

    /**
     * 接口的详细配置
     */
    private DecisionFlowInterface decisionFlowInterface;

    @Override
    public void parse(NodeDesc nodeDesc) {
        setId(nodeDesc.getId());
        Map<String, String> paramMap = nodeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        String interfaceTaskConfig = paramMap.get("tns:interfaceTask");
        String indexUuids = paramMap.get("tns:indexUuids");
        String fields = paramMap.get("tns:fields");
        if (StringUtils.isBlank(interfaceTaskConfig)) {
            throw new ParseException("Service Node id:" + this.getId() + " tns:interfaceTask is blank!");
        }
        try {
            decisionFlowInterface = new DecisionFlowInterface();
            JSONObject json = JSONObject.parseObject(interfaceTaskConfig);
            decisionFlowInterface.setUuid(json.getString("uuid"));
            decisionFlowInterface.setName(json.getString("name"));
            decisionFlowInterface.setIndexUuids(indexUuids);
            decisionFlowInterface.setFields(fields);
            decisionFlowInterface.setInputParams(buildParaInfo(json.getJSONArray("inputs")));
            decisionFlowInterface.setOutputParams(buildParaInfo(json.getJSONArray("outputs")));
            decisionFlowInterface.setRiskServiceOutput(json.getBoolean("isRiskServiceOutput"));
        } catch (Exception e) {
            throw new ParseException("interfaceTaskConfig parse json error, interfaceTaskConfig : " + interfaceTaskConfig);
        }
    }

    @Override
    public int getRunCost() {
        return 0;
    }

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        IGenericDubboCaller genericDubboCaller;
        try {
            genericDubboCaller = (IGenericDubboCaller)SpringContextHolder.getBean("genericDubboCaller");
            genericDubboCaller.call((AbstractFraudContext) executeContext, decisionFlowInterface);
        } catch (Exception e) {
        }
        nodeResult.putOneResult("Thread", Thread.currentThread().getName());
        return nodeResult;
    }

    /**
     * 解析输入|输出参数
     * @param array
     * @return
     */
    private List<InterfaceDefinitionParamInfo> buildParaInfo(JSONArray array) {
        if (null != array) {
            List<InterfaceDefinitionParamInfo> list = Lists.newArrayList();
            array.stream().forEach(o -> {
                InterfaceDefinitionParamInfo paramInfo = new InterfaceDefinitionParamInfo();
                JSONObject json = (JSONObject)o;
                paramInfo.setInterfaceField(json.getString("interface_field"));
                paramInfo.setInterfaceType(json.getString("interface_type"));

                boolean isNecessary = false;
                if (json.containsKey("necessary")) {
                    isNecessary = json.getBoolean("necessary");
                }
                boolean isArray = false;
                if (json.containsKey("isArray")) {
                    isArray = json.getBoolean("isArray");
                }

                if (json.containsKey("type")) {
                    paramInfo.setType(json.getString("type"));
                }

                if (json.containsKey("selectType")) {
                    paramInfo.setType(json.getString("selectType"));
                }

                paramInfo.setRuleField(json.getString("rule_field"));
                paramInfo.setNecessary(isNecessary);
                paramInfo.setArray(isArray);

                list.add(paramInfo);
            });
            return list;
        }
        return null;
    }

}
