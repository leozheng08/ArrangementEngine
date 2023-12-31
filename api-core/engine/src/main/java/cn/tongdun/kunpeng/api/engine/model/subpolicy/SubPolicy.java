package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.model.StatusEntity;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import lombok.Data;

import java.util.*;

/**
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:21
 */
@Data
public class SubPolicy extends StatusEntity {
    private String name;
    private String policyUuid;
    private PolicyMode policyMode;
    private String riskType;

    //按执行顺序放
    private List<String> ruleUuidList;

    //决策结果的类型，如Accept、Review、Reject 对应的阈值
    private List<DecisionResultThreshold> riskThresholds;

}
