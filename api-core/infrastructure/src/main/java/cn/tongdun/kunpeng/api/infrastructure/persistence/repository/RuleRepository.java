package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;

import cn.tongdun.kunpeng.api.common.util.JsonUtil;
import cn.tongdun.kunpeng.api.engine.model.rule.IRuleRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleActionElementDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleConditionElementDAO;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.RuleDAO;
import cn.tongdun.kunpeng.client.dto.RuleActionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleConditionElementDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import cn.tongdun.kunpeng.client.dto.WeightedRiskConfigDTO;
import cn.tongdun.kunpeng.share.dataobject.RuleActionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleConditionElementDO;
import cn.tongdun.kunpeng.share.dataobject.RuleDO;
import cn.tongdun.kunpeng.share.json.JSON;
import cn.tongdun.kunpeng.share.utils.TraceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: liuq
 * @Date: 2020/2/19 1:59 PM
 */
@Repository
public class RuleRepository implements IRuleRepository {
    private Logger logger = LoggerFactory.getLogger(RuleRepository.class);

    @Autowired
    private RuleDAO ruleruleDAO;
    @Autowired
    private RuleConditionElementDAO ruleConditionElementDAO;
    @Autowired
    private RuleActionElementDAO ruleActionElementDAO;

    @Override
    public List<RuleDTO> queryByUuids(List<String> ruleUuids) {
        List<RuleDO>  ruleDOList = ruleruleDAO.selectByUuids(ruleUuids);
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

        //按下下级关系创建树
        result = buildRuleTree(result);
        return result;
    }


    /**
     * 根据子策略uuid 查询查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param subPolicyUuid
     * @return
     */
    @Override
    public List<RuleDTO> queryFullBySubPolicyUuid(String subPolicyUuid){
        List<RuleDO>  ruleDOList = ruleruleDAO.selectByBizTypeBizUuid("sub_policy",subPolicyUuid);

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

        //按下下级关系创建树
        result = buildRuleTree(result);

        for(RuleDTO ruleDTO :result){
            ruleDTO.setRuleConditionElements(queryRuleConditionElementDTOByRuleUuid(ruleDTO.getUuid()));
            ruleDTO.setRuleActionElements(queryRuleActionElementDTOByRuleUuid(ruleDTO.getUuid()));
        }

        return result;
    }


    private List<RuleDTO> buildRuleTree(List<RuleDTO> ruleDTOList) {
        if (ruleDTOList == null || ruleDTOList.isEmpty()) {
            return ruleDTOList;
        }

        List<RuleDTO> result = new ArrayList<>();

        // 查找根节点
        for (RuleDTO element : ruleDTOList) {
            if (StringUtils.isBlank(element.getParentUuid())) {
                result.add(element);
                // 递归查找每个根节点下的元素,
                findRuleChildren(element, ruleDTOList,result);
            }
        }

        return result;
    }

    @Override
    public List<RuleDTO> queryBySubPolicyUuid(String subPolicyUuid){
        List<RuleDO>  ruleDOList = ruleruleDAO.selectByBizTypeBizUuid("sub_policy",subPolicyUuid);

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

        //按上下级关系创建树
        result = buildRuleTree(result);

        return result;
    }


    /**
     * 查询子节点
     *
     * @param parent
     * @param source
     * @param target
     */
    private void findRuleChildren(RuleDTO parent, List<RuleDTO> source, List<RuleDTO> target) {
        for (RuleDTO re : source) {
            if (parent.getUuid().equals(re.getParentUuid())) {
                target.add(re);
                findRuleChildren(re, source,target);
            }
        }
    }


    /**
     * 查询Rule完整信息，包含下级的ruleConditionElements,ruleActionElements
     * @param ruleUuid
     * @return
     */
    @Override
    public RuleDTO queryFullByUuid(String ruleUuid) {

        // TODO 此处有加载已删除规则问题，后续调整
        RuleDO ruleDO = ruleruleDAO.selectByUuid(ruleUuid);
        if(ruleDO == null){
            return null;
        }
        RuleDTO ruleDTO = new RuleDTO();
        BeanUtils.copyProperties(ruleDO, ruleDTO);
        parseRiskConfig(ruleDTO,ruleDO.getRiskConfig());
        //取得最后更新时间，rule,ruleActionElement,ruleConditionElements 做为整体来刷新
        setRuleModifiedVersion(ruleDTO);

        ruleDTO.setRuleConditionElements(queryRuleConditionElementDTOByRuleUuid(ruleUuid));
        ruleDTO.setRuleActionElements(queryRuleActionElementDTOByRuleUuid(ruleUuid));

        return ruleDTO;
    }

    /**
     * 根据业务类型和业务编码
     * @param bizType
     * @param bizUuid
     * @return
     */
    @Override
    public List<RuleDTO> queryByBizTypeAndBizUuid(String bizType, String bizUuid) {
        List<RuleDO> ruleDO = ruleruleDAO.selectByBizTypeBizUuid(bizType, bizUuid);
        List<RuleDTO> ruleDTOS = ruleDO.stream().map(r -> {
            RuleDTO ruleDTO = new RuleDTO();
            BeanUtils.copyProperties(r, ruleDTO);
            parseRiskConfig(ruleDTO,r.getRiskConfig());
            ruleDTO.setRuleConditionElements(queryRuleConditionElementDTOByRuleUuid(r.getUuid()));
            ruleDTO.setRuleActionElements(queryRuleActionElementDTOByRuleUuid(r.getUuid()));
            return ruleDTO;
        }).collect(Collectors.toList());
        return ruleDTOS;
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
        List<RuleConditionElementDO> ruleConditionElementDOList = ruleConditionElementDAO.selectByBizUuidBizType(ruleUuid, "rule");
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
        List<RuleActionElementDO> ruleActionElementDOList = ruleActionElementDAO.selectByRuleUuid(ruleUuid);
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
        if(StringUtils.isBlank(riskConfig) || "null".equalsIgnoreCase(riskConfig)) {
            return;
        }
        try {
            Map jsonObject = JSON.parseObject(riskConfig, HashMap.class);
            String mode = JsonUtil.getString(jsonObject,"mode");
            if(StringUtils.isBlank(mode)) {
                return;
            }
            ruleDTO.setMode(mode);
            switch (mode){
                case "FirstMatch":
                case "WorstMatch":
                    ruleDTO.setRiskDecision(JsonUtil.getString(jsonObject,"riskDecision"));
                    break;
                case "Weighted":
                    WeightedRiskConfigDTO weighted = new WeightedRiskConfigDTO();
                    weighted.setBaseWeight(JsonUtil.getDouble(jsonObject,"baseWeight"));
                    weighted.setWeightRatio(JsonUtil.getDouble(jsonObject,"weightRatio"));
                    weighted.setWeightProperty(JsonUtil.getString(jsonObject,"weightProperty"));
                    weighted.setWeightPropertyValue(JsonUtil.getString(jsonObject,"weightPropertyValue"));
                    weighted.setLowerLimitScore(JsonUtil.getInteger(jsonObject,"lowerLimitScore"));
                    weighted.setUpperLimitScore(JsonUtil.getInteger(jsonObject,"upperLimitScore"));
                    ruleDTO.setWeightedRiskConfigDTO(weighted);
                    break;
                default:
                    logger.warn(TraceUtils.getFormatTrace()+"buildRiskConfig mode error, riskConfig:{}",riskConfig);
            }
        } catch (Exception e){
            logger.error(TraceUtils.getFormatTrace()+"buildRiskConfig error,riskConfig:{}",riskConfig,e);
        }
    }

}
