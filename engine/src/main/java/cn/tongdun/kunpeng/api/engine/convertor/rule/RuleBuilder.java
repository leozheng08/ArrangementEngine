package cn.tongdun.kunpeng.api.engine.convertor.rule;

import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.Rule;

/**
 * @Author: liuq
 * @Date: 2020/2/14 11:17 AM
 */
public interface RuleBuilder {

    Rule build(RuleDTO ruleDTO);
}
