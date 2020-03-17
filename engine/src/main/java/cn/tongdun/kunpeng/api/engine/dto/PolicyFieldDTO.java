package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

@Data
public class PolicyFieldDTO extends CommonDTO {
    private static final long serialVersionUID = -3719392846896702054L;

    /**
     * 策略uuid policy_uuid
     */
    private String policyUuid;

    /**
     * 字段名称 field_name
     */
    private String fieldName;

    /**
     * 是否必须 is_required
     */
    private boolean required;

    /**
     * 状态
     * 1：有效的
     * 0：无效的
     */
    private Integer status;

}