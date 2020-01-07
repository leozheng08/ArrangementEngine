package cn.tongdun.kunpeng.api.subpolicy;

import cn.tongdun.ddd.common.domain.Entity;
import cn.tongdun.kunpeng.common.data.DecisionResultType;
import cn.tongdun.kunpeng.common.data.PolicyMode;
import lombok.Data;

import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class SubPolicy extends Entity {
    private String subPolicyUuid;
    private String subPolicyName;
    private String policyUuid;
    private PolicyMode policyMode;
    private String riskType;

    private int denyThreshold;
    private int reviewThreshold;

    private List<String> ruleUuidList;

    private DecisionResultType defaultDecisionResultType;
    private Map<String,DecisionResultType> decisionResultMap = new LinkedHashMap<>();

    public void addDecisionResultType(String code,DecisionResultType type){
        if(defaultDecisionResultType == null){
            defaultDecisionResultType = type;
        }
        decisionResultMap.put(code,type);
    }

    public DecisionResultType getDecisionResultType(String code){
        return decisionResultMap.get(code);
    }


}
