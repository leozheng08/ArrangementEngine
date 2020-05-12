package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
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
     * 策略uuid policy_uuid
     */
    private String policyUuid;


}