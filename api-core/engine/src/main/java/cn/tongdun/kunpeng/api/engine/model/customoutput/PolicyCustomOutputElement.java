package cn.tongdun.kunpeng.api.engine.model.customoutput;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/16 23:19
 */
@Data
public class PolicyCustomOutputElement extends StatusEntity {
    private static final long serialVersionUID = -368094822366220143L;

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
