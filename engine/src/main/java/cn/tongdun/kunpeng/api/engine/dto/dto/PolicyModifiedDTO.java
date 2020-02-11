package cn.tongdun.kunpeng.api.engine.dto.dto;

import lombok.Data;

@Data
public class PolicyModifiedDTO {
    private long                    modifiedVersion;

    private String                  policyUuid;
    private String                  partnerCode;
    private String                  appName;
    private String                  eventId;
    private String                  version;

    private boolean                 isDefault;
    private boolean                 status;

}
