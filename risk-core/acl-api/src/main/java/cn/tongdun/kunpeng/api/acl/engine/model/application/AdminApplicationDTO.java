package cn.tongdun.kunpeng.api.acl.engine.model.application;

import cn.tongdun.kunpeng.client.dto.CommonDTO;
import lombok.Data;

/**
 * 应用对象
 */
@Data
public class AdminApplicationDTO extends CommonDTO {

    private String            name;                                   // 应用标识
    private String            displayName;                            // 应用名称
    private String            appType;                                // 应用类型 web,ios,android
    private String            partnerCode;                            // 合作方名称
    private String            secretKey;                              // 密钥
    private String            version;                                // 版本
    private String            createdBy;                              // 创建者
    private String            updatedBy;                              // 修改者
    private String            description;                            // 描述
    private String            appTypeName;                            // 应用类型展示名 网站,ios,安卓
    private String            productCode;                            // 产品标识
    private String            productTag;                             // 产品标签

}
