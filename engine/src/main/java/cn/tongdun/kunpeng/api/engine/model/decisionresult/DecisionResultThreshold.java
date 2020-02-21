package cn.tongdun.kunpeng.api.engine.model.decisionresult;

import lombok.Data;

/**
 * @Author: liang.chen
 * @Date: 2020/2/20 下午1:48
 */
@Data
public class DecisionResultThreshold {

    private DecisionResultType decisionResultType;

    //例：startThreshold <= 分数 < endThreshold 则决策结果为review
    private int startThreshold;
    private int endThreshold;
}
