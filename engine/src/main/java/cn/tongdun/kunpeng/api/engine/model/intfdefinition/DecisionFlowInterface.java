package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import lombok.Data;

import java.util.List;

@Data
public class DecisionFlowInterface {

    /**
     * uuid
     */
    private String  uuid;

    /**
     * uuid
     */
    private String name;

    /**
     * 指标uuid拼接
     */
    private String indexUuids;

    /**
     * 字段拼接
     */
    private String fields;

    /**
     * inputParams
     */
    private List<InterfaceDefinitionParamInfo>  inputParams;

    /**
     * outputParams
     */
    private List<InterfaceDefinitionParamInfo>  outputParams;

    /**
     * 参数类型
     */
    private String[] inputParamTypeMap;

    /**
     * 该决策引擎字段是否直接决策接口输出
     */
    private boolean isRiskServiceOutput;

}
