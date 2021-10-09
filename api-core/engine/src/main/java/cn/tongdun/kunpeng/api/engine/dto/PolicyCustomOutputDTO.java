package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

import java.util.List;

/**
 * @author mengtao
 * @version 1.0
 * @date 2021/9/12 21:47
 */
@Data
public class PolicyCustomOutputDTO extends CommonDTO {
    private static final long serialVersionUID = 1876325723006261546L;
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
    private List<PolicyCustomOutputElementDTO> policyCustomOutputElementDTOS;

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
}
