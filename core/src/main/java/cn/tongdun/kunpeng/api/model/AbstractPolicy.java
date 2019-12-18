package cn.tongdun.kunpeng.api.model;

import cn.tongdun.ddd.domain.Entity;
import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public abstract class AbstractPolicy extends Entity {


    private String            policyUuId;
    private String            name;
    private String            partnerCode;
    private String            appName;
    private String            appType;
    private String            appDisplayName;
    private String            eventId;
    private String            eventType;
    private String            version;
    private String            antlrCode;
    private boolean           selfOutput;
    private String            dataPermission;
}
