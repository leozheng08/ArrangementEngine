package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.dto.RuleActionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleActionElementDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleConditionElementDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleDOMapper;
import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleConditionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/19 1:59 PM
 */
@Repository
public class RuleRepository implements IRuleRepository {

    @Autowired
    private RuleDOMapper ruleDOMapper;

    @Autowired
    private RuleConditionElementDOMapper ruleConditionElementDOMapper;
    @Autowired
    private RuleActionElementDOMapper ruleActionElementDOMapper;

    @Override
    public RuleDTO queryByUuid(String ruleUuid) {

        RuleDO ruleDO = ruleDOMapper.selectByUuid(ruleUuid);

        return null;
    }

    //查询规则条件
    public List<RuleConditionElementDTO> queryRuleConditionElementDTOByRuleUuid(String ruleUuid){
        List<RuleConditionElementDO> ruleConditionElementDOList = ruleConditionElementDOMapper.selectByBizUuidBizType(ruleUuid,"rule");
        if(ruleConditionElementDOList == null) {
            return null;
        }

        List<RuleConditionElementDTO> result = null;
        result = ruleConditionElementDOList.stream().map(ruleConditionElementDO->{
            RuleConditionElementDTO ruleConditionElementDTO = new RuleConditionElementDTO();
            BeanUtils.copyProperties(ruleConditionElementDO,ruleConditionElementDTO);
            return ruleConditionElementDTO;
        }).collect(Collectors.toList());

        //创建条件树
        result = buildConditionTree(result);
        return result;
    }

    //查询规则动作
    public List<RuleActionElementDTO> queryRuleActionElementDTOByRuleUuid(String ruleUuid){
        List<RuleActionElementDO> ruleActionElementDOList = ruleActionElementDOMapper.selectByRuleUuid(ruleUuid);
        if(ruleActionElementDOList == null) {
            return null;
        }

        List<RuleActionElementDTO> result = null;
        result = ruleActionElementDOList.stream().map(ruleActionElementDO->{
            RuleActionElementDTO ruleActionElementDTO = new RuleActionElementDTO();
            BeanUtils.copyProperties(ruleActionElementDO,ruleActionElementDTO);
            return ruleActionElementDTO;
        }).collect(Collectors.toList());

        return result;
    }

    private List<RuleConditionElementDTO> buildConditionTree(List<RuleConditionElementDTO> ruleConditionElementDTOList){
        if(ruleConditionElementDTOList == null || ruleConditionElementDTOList.isEmpty()){
            return ruleConditionElementDTOList;
        }

        List<RuleConditionElementDTO> result = new ArrayList<>();

        // 查找根节点
        for (RuleConditionElementDTO element : ruleConditionElementDTOList) {
            if (StringUtils.isBlank(element.getParentUuid())) {
                // 递归查找每个根节点下的元素,
                findChildren(element, ruleConditionElementDTOList);
                result.add(element);
            }
        }

        return result;
    }


    /**
     * 查询子节点
     * @param parent
     * @param conditionElements
     */
    private void findChildren(RuleConditionElementDTO parent, List<RuleConditionElementDTO> conditionElements) {
        List<RuleConditionElementDTO> children = new ArrayList<RuleConditionElementDTO>();
        for (RuleConditionElementDTO re : conditionElements) {
            if (parent.getUuid().equals(re.getParentUuid())) {
                children.add(re);
            }
        }

        parent.setSubConditions(children);

        for (RuleConditionElementDTO element : children) {
            findChildren(element, conditionElements);
        }
    }
}
