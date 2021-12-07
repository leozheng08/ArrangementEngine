package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/15 10:52
 */
@Data
public class PolicyCustomOutputElementDTO extends CommonDTO {
    private static final long serialVersionUID = 6616273325972725593L;
    private String policyCustomOutputUuid;
    private String leftProperty;
    private String leftPropertyType;
    private String leftPropertyDataType;
    private String rightValue;
    private String rightType;
    private String rightDataType;
    /**
     * 是否对右变量赋值
     */
    private boolean rightConfig;
}
