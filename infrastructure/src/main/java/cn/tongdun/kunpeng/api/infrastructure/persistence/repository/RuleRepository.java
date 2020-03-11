package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.engine.dto.RuleActionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.api.engine.dto.RuleDTO;
import cn.tongdun.kunpeng.api.engine.dto.WeightedRiskConfigDTO;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleActionElementDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleConditionElementDOMapper;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleDOMapper;
import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleConditionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/19 1:59 PM
 */
@Repository
public class RuleRepository implements IRuleRepository {
    private Logger logger = LoggerFactory.getLogger(RuleRepository.class);

    @Autowired
    private RuleDOMapper ruleDOMapper;

    @Autowired
    private RuleConditionElementDOMapper ruleConditionElementDOMapper;
    @Autowired
    private RuleActionElementDOMapper ruleActionElementDOMapper;


    /**
     * 根据子策略uuid 查询查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param subPolicyUuid
     * @return
     */
    @Override
    public List<RuleDTO> queryFullBySubPolicyUuid(String subPolicyUuid){
        List<RuleDO>  ruleDOList = ruleDOMapper.selectByBizUuidBizType(subPolicyUuid,"sub_policy");

        if(ruleDOList == null) {
            return null;
        }

        List<RuleDTO> result = null;
        result = ruleDOList.stream().map(ruleDO->{
            RuleDTO ruleDTO = new RuleDTO();
            BeanUtils.copyProperties(ruleDO,ruleDTO);
            parseRiskConfig(ruleDTO,ruleDO.getRiskConfig());
            //取得最后更新时间，rule,ruleActionElement,ruleConditionElements 做为整体来刷新
            setRuleModifiedVersion(ruleDTO);
            return ruleDTO;
        }).collect(Collectors.toList());

        for(RuleDTO ruleDTO :result){
            ruleDTO.setRuleConditionElements(queryRuleConditionElementDTOByRuleUuid(ruleDTO.getUuid()));
            ruleDTO.setRuleActionElements(queryRuleActionElementDTOByRuleUuid(ruleDTO.getUuid()));
        }

        return result;
    }


    /**
     * 查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param ruleUuid
     * @return
     */
    @Override
    public RuleDTO queryFullByUuid(String ruleUuid) {

        RuleDO ruleDO = ruleDOMapper.selectByUuid(ruleUuid);
        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyProperties(ruleDO, ruleDTO);
        parseRiskConfig(ruleDTO,ruleDO.getRiskConfig());
        //取得最后更新时间，rule,ruleActionElement,ruleConditionElements 做为整体来刷新
        setRuleModifiedVersion(ruleDTO);

        ruleDTO.setRuleConditionElements(queryRuleConditionElementDTOByRuleUuid(ruleUuid));
        ruleDTO.setRuleActionElements(queryRuleActionElementDTOByRuleUuid(ruleUuid));

        return ruleDTO;
    }

    private void setRuleModifiedVersion(RuleDTO ruleDTO){
        //取得最后更新时间，rule,ruleActionElement,ruleConditionElements 做为整体来刷新
        Long modifiedVersion = ruleDTO.getGmtModify().getTime();
        if(ruleDTO.getRuleActionElements() != null) {
            for (RuleActionElementDTO ruleActionElementDTO : ruleDTO.getRuleActionElements()){
                if(ruleActionElementDTO.getGmtModify().getTime()>modifiedVersion){
                    modifiedVersion = ruleActionElementDTO.getGmtModify().getTime();
                }
            }
        }
        if(ruleDTO.getRuleConditionElements() != null) {
            for (RuleConditionElementDTO ruleConditionElementDTO : ruleDTO.getRuleConditionElements()){
                if(ruleConditionElementDTO.getGmtModify().getTime()>modifiedVersion){
                    modifiedVersion = ruleConditionElementDTO.getGmtModify().getTime();
                }
            }
        }
        ruleDTO.setGmtModify(new Date(modifiedVersion));
    }


    //查询规则条件
    public List<RuleConditionElementDTO> queryRuleConditionElementDTOByRuleUuid(String ruleUuid) {
        List<RuleConditionElementDO> ruleConditionElementDOList = ruleConditionElementDOMapper.selectByBizUuidBizType(ruleUuid, "rule");
        if (ruleConditionElementDOList == null) {
            return null;
        }

        List<RuleConditionElementDTO> result = null;
        result = ruleConditionElementDOList.stream().map(ruleConditionElementDO -> {
            RuleConditionElementDTO ruleConditionElementDTO = new RuleConditionElementDTO();
            BeanUtils.copyProperties(ruleConditionElementDO, ruleConditionElementDTO);
            return ruleConditionElementDTO;
        }).collect(Collectors.toList());

        //创建条件树
        result = buildConditionTree(result);
        return result;
    }

    //查询规则动作
    public List<RuleActionElementDTO> queryRuleActionElementDTOByRuleUuid(String ruleUuid) {
        List<RuleActionElementDO> ruleActionElementDOList = ruleActionElementDOMapper.selectByRuleUuid(ruleUuid);
        if (ruleActionElementDOList == null) {
            return null;
        }

        List<RuleActionElementDTO> result = null;
        result = ruleActionElementDOList.stream().map(ruleActionElementDO -> {
            RuleActionElementDTO ruleActionElementDTO = new RuleActionElementDTO();
            BeanUtils.copyProperties(ruleActionElementDO, ruleActionElementDTO);
            return ruleActionElementDTO;
        }).collect(Collectors.toList());

        return result;
    }

    private List<RuleConditionElementDTO> buildConditionTree(List<RuleConditionElementDTO> ruleConditionElementDTOList) {
        if (ruleConditionElementDTOList == null || ruleConditionElementDTOList.isEmpty()) {
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
     *
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


    /**
     * 风险配置
     * {
     * "mode":"WorstMatch"
     * "riskDecision":"Accept"
     * }
     * 或者
     * {
     * "mode":"Weighted",
     * "riskWeight":10,
     * "weightRatio":20.33,
     * "op":"+",
     * "property":{
     * "type":"indicatrix/field",
     * "name":"指标/字段"
     * },
     * "propertyValue":{
     * "value":"3333333333"
     * },
     * "upperLimitScore":-30,
     * "lowerLimitScore":30
     * }
     */
    private void parseRiskConfig(RuleDTO ruleDTO,String riskConfig){
        if(StringUtils.isBlank(riskConfig)) {
            return;
        }
        try {
            JSONObject jsonObject = JSONObject.parseObject(riskConfig);
            String mode = jsonObject.getString("mode");
            if(StringUtils.isBlank(mode)) {
                return;
            }
            ruleDTO.setMode(mode);
            switch (mode){
                case "FirstMatch":
                case "WorstMatch":
                    ruleDTO.setRiskDecision(jsonObject.getString("riskDecision"));
                    break;
                case "Weighted":
                    WeightedRiskConfigDTO weighted = new WeightedRiskConfigDTO();
                    weighted.setBaseWeight(jsonObject.getDouble("baseWeight"));
                    weighted.setWeightRatio(jsonObject.getDouble("weightRatio"));
                    weighted.setWeightProperty(jsonObject.getString("weightProperty"));
                    weighted.setWeightPropertyValue(jsonObject.getString("weightPropertyValue"));
                    weighted.setLowerLimitScore(jsonObject.getInteger("lowerLimitScore"));
                    weighted.setUpperLimitScore(jsonObject.getInteger("upperLimitScore"));
                    ruleDTO.setWeightedRiskConfigDTO(weighted);
                    break;
                default:
                    logger.warn("buildRiskConfig mode error, riskConfig:{}",riskConfig);
            }
        } catch (Exception e){
            logger.error("buildRiskConfig error,riskConfig:{}",riskConfig,e);
        }
    }

}
