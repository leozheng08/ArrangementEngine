package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/2/11 下午3:10
 */
public class InterfaceDefinitionManager extends AbstractBizNode {

    /**
     * 接口的详细配置
     */
    private InterfaceDefinitionInfo interfaceDefinitionInfo;

    /**
     * 指标uuid拼接
     */
    private String indexUuids;

    /**
     * 字段拼接
     */
    private String fields;

    @Override
    public void parse(NodeDesc nodeDesc) {
        setId(nodeDesc.getId());
        Map<String, String> paramMap = nodeDesc.getParamList().stream().collect(Collectors.toMap(a -> a.getName(), b -> b.getValue()));
        String interfaceTaskConfig = paramMap.get("tns:interfaceTask");
        indexUuids = paramMap.get("tns:indexUuids");
        fields = paramMap.get("tns:fields");
        if (StringUtils.isBlank(interfaceTaskConfig)) {
            throw new ParseException("Service Node id:" + this.getId() + " tns:interfaceTask is blank!");
        }
        try {
            interfaceDefinitionInfo = new InterfaceDefinitionInfo();
            JSONObject json = JSONObject.parseObject(interfaceTaskConfig);
            interfaceDefinitionInfo.setUuid(json.getString("uuid"));
            interfaceDefinitionInfo.setName(json.getString("name"));
            interfaceDefinitionInfo.setInputParams(buildParaInfo(json.getJSONArray("inputs"), "input"));
            interfaceDefinitionInfo.setOutputParams(buildParaInfo(json.getJSONArray("outputs"), "output"));
            interfaceDefinitionInfo.setRiskServiceOutput(json.getBoolean("isRiskServiceOutput"));
        } catch (Exception e) {

        }
    }

    @Override
    public int getRunCost() {
        return 0;
    }

    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        ServiceLoader<IGenericDubboCaller> serviceLoader = ServiceLoader.load(IGenericDubboCaller.class);
        Iterator<IGenericDubboCaller> iterator = serviceLoader.iterator();
        IGenericDubboCaller genericDubboCaller;
        while (iterator.hasNext()) {
            genericDubboCaller = iterator.next();
            //调用
            genericDubboCaller.call((AbstractFraudContext) executeContext, interfaceDefinitionInfo, indexUuids, fields);
        }
        try {
            Thread.sleep(80);
        } catch (Exception e) {
        }
        nodeResult.putOneResult("Thread", Thread.currentThread().getName());
        return nodeResult;
    }

    /**
     * 解析输入|输出参数
     * @param array
     * @param arrayType
     * @return
     */
    private List<InterfaceDefinitionParamInfo> buildParaInfo(JSONArray array, String arrayType) {
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
