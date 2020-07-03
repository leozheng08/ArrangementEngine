package cn.tongdun.kunpeng.api.acl.engine.model.application;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * 应用对象
 */
@Data
public class AdminApplicationDTO extends CommonDTO {
    private String code;
    private String uuid;
    private String displayName;
    private String partnerCode;
    private String secretKey;
    private String description;

}
