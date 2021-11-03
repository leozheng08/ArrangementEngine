package cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import lombok.Data;

/**
 * @author hls
 * @version 1.0
 * @date 2021/10/20 10:11 上午
 */
@Data
public class PolicyFieldNecessary extends StatusEntity {

    private static final long serialVersionUID = -223962062224910433L;

    /**
     * policyDefinitionUuid 策略uuid
     */
    private String policyDefinitionUuid;

    /**
     * fieldCode 字段
     */
    private String fieldCode;

    /**
     * fieldName 字段名称
     */
    private String fieldName;

    /**
     * property 字段属性
     */
    private String property;

    /**
     * fieldType 字段类型
     */
    private String fieldType;

    /**
     * necessary 字段是否必须
     */
    private boolean necessary;

}
