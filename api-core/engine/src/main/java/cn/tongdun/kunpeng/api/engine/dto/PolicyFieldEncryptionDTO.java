package cn.tongdun.kunpeng.api.engine.dto;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * @author hls
 * @version 1.0
 * @date 2021/10/22 2:48 下午
 */
@Data
public class PolicyFieldEncryptionDTO extends CommonDTO {

    private static final long serialVersionUID = 2374087728711033475L;

    /**
     * policyDefinitionUuid 策略uuid
     */
    private String policyDefinitionUuid;
    /**
     * fieldCode 字段
     */
    private String fieldCode;
    /**
     * property 字段类型
     */
    private String property;
    /**
     * 加密方式 MD5 SM3 SHA256
     */
    private String fieldEncryptionType;
    /**
     * fieldIdentity 加密标识字段
     */
    private String fieldIdentity;
}
