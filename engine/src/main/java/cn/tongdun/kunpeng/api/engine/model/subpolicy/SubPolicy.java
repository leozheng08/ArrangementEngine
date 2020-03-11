package cn.tongdun.kunpeng.api.engine.model.subpolicy;

import cn.tongdun.kunpeng.api.engine.model.VersionedEntity;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultThreshold;
import cn.tongdun.kunpeng.client.data.PolicyMode;
import cn.tongdun.kunpeng.api.engine.model.decisionresult.DecisionResultType;
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

    //决策结果的类型，如Accept、Review、Reject 对应的阈值
    private List<DecisionResultThreshold> riskThresholds;

}
