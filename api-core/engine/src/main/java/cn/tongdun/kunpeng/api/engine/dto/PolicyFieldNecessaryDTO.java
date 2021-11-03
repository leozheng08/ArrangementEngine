package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @author hls
 * @version 1.0
 * @date 2021/10/20 10:29 上午
 */
@Data
public class PolicyFieldNecessaryDTO extends CommonDTO {

    private static final long serialVersionUID = -8445304627752895132L;

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
