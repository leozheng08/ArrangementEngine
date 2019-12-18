package cn.tongdun.kunpeng.api.dataobject;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.List;


@Data
public class PolicyDO extends CommonDO {

    private static final long serialVersionUID = 1456486484654131346L;

    private String                  partnerCode;
    private String                  appName;
    private String                  eventId;
    private String                  version;

    private boolean                 isDefault;
    private boolean                 status;

    private long                    modifiedVersion;

    private String                  name;
    private String                  serviceType;
    private String                  appType;
    private String                  eventType;
    private String                  description;
    private String                  createBy;
    private String                  updatedBy;
    private List<SubPolicyDO>       subPolicyList;
    private String                  appDisplayName;
    private String                  antlrCode;
    private boolean                 selfOutput;
    private String                  challengedUuid;
    private Timestamp               challengingStart;
    private Timestamp               challengingEnd;
    private String                  dataPermission;
    private String                  riskType;



}
