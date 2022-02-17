package cn.tongdun.kunpeng.api.engine.model.policyfield;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;

/**
 * @author zeyuan.zheng@tongdun.cn
 * @date 2/17/22 9:46 AM
 */
@Data
public class PolicyField extends StatusEntity {

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
}
