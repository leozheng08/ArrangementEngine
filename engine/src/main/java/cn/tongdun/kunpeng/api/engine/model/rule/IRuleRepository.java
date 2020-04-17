package cn.tongdun.kunpeng.api.engine.model.rule;

import cn.tongdun.kunpeng.client.dto.RuleDTO;

import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/10 下午1:46
 */
public interface IRuleRepository {


    /**
     * 查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param ruleUuid
     * @return
     */
    RuleDTO queryFullByUuid(String ruleUuid);


    List<RuleDTO> queryByUuids(List<String> ruleUuids);


    /**
     * 根据子策略uuid 查询查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param subPolicyUuid
     * @return
     */
    List<RuleDTO> queryFullBySubPolicyUuid(String subPolicyUuid);


    /**
     * 根据子策略uuid 查询查询Rule列表
     * @param subPolicyUuid
     * @return
     */
    List<RuleDTO> queryBySubPolicyUuid(String subPolicyUuid);
}
