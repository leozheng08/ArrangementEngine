package cn.tongdun.kunpeng.api.engine.model.intfdefinition;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class InterfaceDefinitionInfo {

    /**
     * uuid
     */
    private String  uuid;

    /**
     * uuid
     */
    private String name;

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
