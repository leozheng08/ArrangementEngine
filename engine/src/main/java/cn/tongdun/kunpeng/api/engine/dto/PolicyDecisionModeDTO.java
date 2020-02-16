package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.ddd.common.domain.ConcurrencySafeEntity;
import lombok.Data;

@Data
public class PolicyDecisionModeDTO extends CommonDTO {

    private static final long serialVersionUID = -1571715795868199570L;

    /**
     * 决策方式类型 flow：决策流 table：决策表 tree：决策树  decision_mode_type
     */
    private String decisionModeType;

    /**
     * 决策方式uuid decision_mode_uuid
     */
    private String decisionModeUuid;

    /**
     * 是否当前决策模式
     */
    private boolean currDecisionMode;

    /**
     * 是否启用 1:已启用 0:未启用
     * 表示对应的决策方式是否被开启或者关闭
     */
    private Integer status;

    /**
     * 策略uuid policy_uuid
     */
    private String policyUuid;

    private boolean deleted;


}