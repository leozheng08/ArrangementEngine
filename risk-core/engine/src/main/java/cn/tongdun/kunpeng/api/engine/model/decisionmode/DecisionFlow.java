package cn.tongdun.kunpeng.api.engine.model.decisionmode;

import cn.fraudmetrix.module.tdflow.model.graph.Graph;
import lombok.Data;

/**
 * 决策流
 *
 * @Author: liang.chen
 * @Date: 2019/12/16 下午3:22
 */
@Data
public class DecisionFlow extends AbstractDecisionMode {

    String decisionFlowUuid;
    /**
     * 控制策略执行的图
     */
    private Graph graph;
}
