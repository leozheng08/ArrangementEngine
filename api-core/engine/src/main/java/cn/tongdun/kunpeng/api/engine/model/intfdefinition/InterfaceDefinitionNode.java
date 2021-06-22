package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import cn.fraudmetrix.module.tdflow.definition.NodeDesc;
import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.fraudmetrix.module.tdflow.model.NodeResult;
import cn.fraudmetrix.module.tdflow.model.node.AbstractBizNode;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liang.chen
 * @Date: 2020/2/11 下午3:10
 */

/**
 * @author: yuanhang
 * @date: 2021-02-23 13:52
 **/
public class InterfaceDefinitionNode extends AbstractBizNode {

    private static final Logger logger = LoggerFactory.getLogger(InterfaceDefinitionNode.class);

    /**
     * 接口的详细配置
     */
    private DecisionFlowInterface decisionFlowInterface;


    @Override
    protected NodeResult run(ExecuteContext executeContext) {
        NodeResult nodeResult = new NodeResult();
        IGenericDubboCaller genericDubboCaller;
        try {
            genericDubboCaller = (IGenericDubboCaller) SpringContextHolder.getBean("genericDubboCaller");
            genericDubboCaller.call((AbstractFraudContext) executeContext, decisionFlowInterface);
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace() + "ruleInterface run error,interfaceUuid:" + decisionFlowInterface.getUuid(), e);
        }
        nodeResult.putOneResult("Thread", Thread.currentThread().getName());
        return nodeResult;
    }

    @Override
    public int getRunCost() {
        return 30;
    }

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
            Map json = JSON.parseObject(interfaceTaskConfig, HashMap.class);
            decisionFlowInterface.setUuid(JsonUtil.getString(json,"uuid"));
            decisionFlowInterface.setName(JsonUtil.getString(json,"name"));
            decisionFlowInterface.setIndexUuids(indexUuids);
            decisionFlowInterface.setFields(fields);
            decisionFlowInterface.setInputParams(buildParaInfo((List<Map>)json.get("inputs")));
            decisionFlowInterface.setOutputParams(buildParaInfo((List<Map>)json.get("outputs")));
            decisionFlowInterface.setRiskServiceOutput(JsonUtil.getBoolean(json,"isRiskServiceOutput") == null ? false : JsonUtil.getBoolean(json,"isRiskServiceOutput"));
        } catch (Exception e) {
            throw new ParseException("interfaceTaskConfig parse json error, interfaceTaskConfig : " + interfaceTaskConfig);
        }
    }

    /**
     * 解析输入|输出参数
     * @param array
     * @return
     */
    private List<InterfaceDefinitionParamInfo> buildParaInfo(List<Map> array) {
        if (null != array) {
            List<InterfaceDefinitionParamInfo> list = Lists.newArrayList();
            array.stream().forEach(json -> {
                InterfaceDefinitionParamInfo paramInfo = new InterfaceDefinitionParamInfo();
                paramInfo.setInterfaceField(JsonUtil.getString(json,"interface_field"));
                paramInfo.setInterfaceType(JsonUtil.getString(json,"interface_type"));

                if (json.containsKey("type")) {
                    paramInfo.setType(JsonUtil.getString(json,"type"));
                }

                if (json.containsKey("selectType")) {
                    paramInfo.setType(JsonUtil.getString(json,"selectType"));
                }

                paramInfo.setRuleField(JsonUtil.getString(json,"rule_field"));
                paramInfo.setNecessary(BooleanUtils.toBoolean(JsonUtil.getBoolean(json,"necessary")));
                paramInfo.setArray(BooleanUtils.toBoolean(JsonUtil.getBoolean(json,"isArray")));

                list.add(paramInfo);
            });
            return list;
        }
        return null;
    }

}
