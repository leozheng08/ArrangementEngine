package cn.tongdun.kunpeng.api.engine.model.adminapplication;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/2/21 下午6:07
 */
@Data
public class AdminApplication extends VersionedEntity {

    private String            name;                                   // 应用标识
    private String            displayName;                            // 应用名称
    private String            appType;                                // 应用类型 web,ios,android
    private String            partnerCode;                            // 合作方名称
    private String            secretKey;                              // 密钥
    private String            version;                                // 版本
    private String            appTypeName;                            // 应用类型展示名 网站,ios,安卓
}
