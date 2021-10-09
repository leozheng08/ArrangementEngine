package cn.tongdun.kunpeng.api.engine.model.customoutput;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;

import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/14 21:53
 */
@Data
public class PolicyCustomOutput extends StatusEntity {
    private static final long serialVersionUID = 9010246010132409241L;

    /**
     * 策略uuid
     */
    private String policyUuid;

    /**
     * 策略定义uuid
     */
    private String policyDefinitionUuid;

    /**
     * 合作方code
     */
    private String partnerCode;

    /**
     * 自定义输出名称
     */
    private String name;


    /**
     * 是否进行条件配置
     * 0:false
     * 1:true
     */
    private boolean conditionConfig;

    /**
     * 输出配置
     */
    private List<PolicyCustomOutputElement> policyCustomOutputElements;

    /**
     * 状态
     1:已启用
     0:未启用
     */
    private Integer status;

    /**
     * 是否删除
     */
    private boolean deleted;

    /**
     * 规则uuid
     */
    private String ruleUuid;

    private Rule rule;
}
