package cn.tongdun.kunpeng.client.api;

import cn.tongdun.ddd.common.result.SingleResult;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;

/**
 * @Author: liang.chen
 * @Date: 2020/3/17 下午6:38
 */
public interface IPolicyParseCheck {

    /**
     * 规则检查
     * @param ruleDTO
     * @return
     * @throws Exception
     */
    SingleResult<Boolean> checkRule(RuleDTO ruleDTO);

    /**
     * 决策流检查
     * @param content
     * @return
     * @throws Exception
     */
    SingleResult<Boolean> checkFlow(String content);
}
