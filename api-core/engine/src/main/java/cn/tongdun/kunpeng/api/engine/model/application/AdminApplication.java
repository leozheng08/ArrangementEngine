package cn.tongdun.kunpeng.api.engine.model.application;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.api.engine.reload.dataobject.EventDO;
import lombok.Data;

/**
 * 应用对象
 */
@Data
public class AdminApplication extends EventDO {

    // 应用标识
    private String code;
    // uuid
    private String uuid;
    // 应用名称
    private String displayName;
    // 合作方名称
    private String partnerCode;
    // 密钥
    private String secretKey;
    // 描述
    private String description;

}
