package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import cn.tongdun.kunpeng.common.data.DecisionResultType;
import lombok.Data;

import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class SubPolicy extends VersionedEntity {
    private String name;
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
