package cn.tongdun.kunpeng.api.dataobject;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jian.li
 */
@Data
public class SubPolicyDO extends CommonDO {

    private static final long            serialVersionUID         = 1L;
    private String                       name;
    private String                       riskEventId;
    private String                       riskEventType;                                     // 事件类型
    private String                       mode;
    private Integer                      denyThreshold;
    private Integer                      reviewThreshold;
    private Integer                      beginThreshold;
    private Integer                      endThreshold;
    private String                       partner;
    private String                       appName;
    private String                       description              = "";
    private String                       createBy;
    private String                       updatedBy;
    private String                       appDisplayName;
    private String                       appuuid;
    private boolean                      isPolicyTemplate;
    private String                       businessType;                                      // 行业类型
    private String                       appType;                                           // 应用类型
    private String                       tdAllPartner;                                      // 同盾用户所有合作方标志
    private String                       userAllApp;                                        // 普通用户所有应用标志
    private String                       tdPartnerisNull;                                   // 同盾用户没有数据
    private List<String>                 appList;
    private List<String>                 partnerList;
    private String                       riskType;
    private Integer                      level;
    private String                       fkPolicySetUuid;
    private String                       policySetName;                                     //策略集名
    private String                       partnerType;                                       // 合作类型
    private String                       policyType;

    private String                       partnerTypeDisplayName;
    private String                       appTypeDisplayName;
    private String                       eventTypeDisplayName;
    private String                       riskTypeDisplayName;
    private String                       businessTypeDisplayName;
    private String                       policyModeDisplayName;
    private String                       eventId;                                           // 事件ID
    private List<RuleDO>                 rules                    = new ArrayList<RuleDO>();

    private Map<String, List<String>>    ruleParamsCheckMap       = new HashMap<>();

    private String                       eventType;
    private List<RuleConditionElementDO> ruleConditionElementList = new ArrayList<>();


}
