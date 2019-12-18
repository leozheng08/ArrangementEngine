package cn.tongdun.kunpeng.api.dataobject;

import lombok.Data;

@Data
public class PolicyModifiedDO {
    private long                    modifiedVersion;

    private String                  policyUuid;
    private String                  partnerCode;
    private String                  appName;
    private String                  eventId;
    private String                  version;

    private boolean                 isDefault;
    private boolean                 status;

}
