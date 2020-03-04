package cn.tongdun.kunpeng.api.infrastructure.persistence.repository;


import cn.tongdun.kunpeng.api.engine.dto.*;
import cn.tongdun.kunpeng.api.engine.model.policy.IPolicyRepository;
import cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng.PolicyDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Repository
public class MockPolicyRepository implements IPolicyRepository{

    @Autowired
    private PolicyDOMapper policyDOMapper;

    //取得所有策略清单
    @Override
    public List<PolicyModifiedDTO> queryDefaultPolicyByPartners(Set<String> partners){

        //mock
        List<PolicyModifiedDTO> list = new ArrayList();
        PolicyModifiedDTO policyModifiedDO = new PolicyModifiedDTO();
        policyModifiedDO.setPolicyUuid("123456789");
        policyModifiedDO.setDefaultVersion(true);
        policyModifiedDO.setStatus(1);
        policyModifiedDO.setEventId("eventId");
        policyModifiedDO.setPartnerCode("demo");
        policyModifiedDO.setGmtModify(new Date());
        policyModifiedDO.setVersion("v1.0");
        list.add(policyModifiedDO);
        return list;
    }


    //取得所有策略清单
    public List<PolicyModifiedDTO> queryDefaultPolicyByPartner(String partners){

        //mock
        List<PolicyModifiedDTO> list = new ArrayList();
        PolicyModifiedDTO policyModifiedDO = new PolicyModifiedDTO();
        policyModifiedDO.setPolicyUuid("123456789");
        policyModifiedDO.setDefaultVersion(true);
        policyModifiedDO.setStatus(1);
        policyModifiedDO.setEventId("eventId");
        policyModifiedDO.setPartnerCode("demo");
        policyModifiedDO.setGmtModify(new Date());
        policyModifiedDO.setVersion("v1.0");
        list.add(policyModifiedDO);
        return list;
    }


    @Override
//    @Cacheable("policyDOCache")
    public PolicyDTO queryByUuid(String uuid){

        //策略
        PolicyDTO policyDO = new PolicyDTO();
        policyDO.setUuid("123456789");
        policyDO.setStatus(1);
        policyDO.setEventId("eventId");
        policyDO.setPartnerCode("demo");
        policyDO.setVersion("v1.0");
        policyDO.setName("policy name");

        //子策略
        List<SubPolicyDTO> subPolicyDOList = new ArrayList<>();
        policyDO.setSubPolicyList(subPolicyDOList);
        SubPolicyDTO subPolicyDO = new SubPolicyDTO();
        subPolicyDOList.add(subPolicyDO);
        subPolicyDO.setUuid("2343241342123");
        subPolicyDO.setName("sub policy name");
        subPolicyDO.setMode("Weighted");

        String attribute = "{\n" +
                "\"riskThreshold\":[\n" +
                "    {\n" +
                "        \"start\":0,\n" +
                "        \"end\":30,\n" +
                "        \"riskDecision\":\"Accept\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"start\":30,\n" +
                "        \"end\":60,\n" +
                "        \"riskDecision\":\"Review\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"start\":60,\n" +
                "        \"end\":100,\n" +
                "        \"riskDecision\":\"Reject\"\n" +
                "    }\n" +
                "]\n" +
                "}";
        subPolicyDO.setAttribute(attribute);


        //规则
        List<RuleDTO> ruleDOList = new ArrayList<>();
        subPolicyDO.setRules(ruleDOList);
        RuleDTO ruleDO = new RuleDTO();
        ruleDOList.add(ruleDO);
        ruleDO.setId(56935914L);
        ruleDO.setUuid("b94dbf4dfe6d41b5a62789d0a8400240");
        ruleDO.setName("正则匹配");
        ruleDO.setMode("WorstMatch");
        ruleDO.setRiskDecision("Accept");
        ruleDO.setTemplate("pattern/regex");
//        ruleDO.setTemplate("regex");
        ruleDO.setRuleCustomId("56935914");

        WeightedRiskConfigDTO weightedRiskConfigDTO = new WeightedRiskConfigDTO();
        weightedRiskConfigDTO.setLowerLimitScore(-30);
        weightedRiskConfigDTO.setUpperLimitScore(30);
        weightedRiskConfigDTO.setBaseWeight(10D);
        weightedRiskConfigDTO.setWeightRatio(5D);
        weightedRiskConfigDTO.setWeightProperty("index");
        weightedRiskConfigDTO.setWeightPropertyValue("123456");
        ruleDO.setWeightedRiskConfigDTO(weightedRiskConfigDTO);

        //规则条件
        List<RuleConditionElementDTO> ruleConditionElementDOList = new ArrayList<>();
        ruleDO.setRuleConditionElements(ruleConditionElementDOList);
        RuleConditionElementDTO ruleConditionElementDO = new RuleConditionElementDTO();
        ruleConditionElementDOList.add(ruleConditionElementDO);

        ruleConditionElementDO.setId(146804004L);
        ruleConditionElementDO.setUuid("b956811644594b3cb7c8fda00fbf0d38");
        ruleConditionElementDO.setLogicOperator("&&");
        ruleConditionElementDO.setLeftProperty("pattern/regex");
        ruleConditionElementDO.setLeftPropertyType("alias");
//        ruleConditionElementDO.setProperty("regex");
        ruleConditionElementDO.setOp("operator");
        ruleConditionElementDO.setRightValue("1");
        ruleConditionElementDO.setParams("[{\"name\":\"property\",\"type\":\"string\",\"value\":\"partnerCode\"},{\"name\":\"result\",\"type\":\"int\",\"value\":\"1\"},{\"name\":\"ignoreCase\",\"type\":\"\",\"value\":\"1\"},{\"name\":\"regex\",\"type\":\"string\",\"value\":\".*\"},{\"name\":\"iterateType\",\"type\":\"string\",\"value\":\"any\"}]");
        ruleConditionElementDO.setDescription("正则表达式");
        ruleConditionElementDO.setLeftUseOriginValue(false);


        //action
        List<RuleActionElementDTO> ruleActionElementDOList = new ArrayList<>();
        ruleDO.setRuleActionElements(ruleActionElementDOList);
        RuleActionElementDTO ruleActionElementDO = new RuleActionElementDTO();
        ruleActionElementDOList.add(ruleActionElementDO);
        String actions ="[{\"leftProperty\":\"accountLogin\",\"leftPropertyType\":\"\",\"operator\":\"==\",\"rightValue\":\"abc\",\"rightValueType\":\"input\"},{\"leftProperty\":\"ip3\",\"leftPropertyType\":\"\",\"operator\":\"==\",\"rightValue\":\"accountMobile\",\"rightValueType\":\"context\"}]";
        ruleActionElementDO.setActions(actions);
        ruleActionElementDO.setRuleUuid(ruleDO.getUuid());
        ruleActionElementDO.setId(234232L);

        return policyDO;
    }

    //根据合作列表，取得挑战者版本的策略清单
    @Override
    public List<PolicyModifiedDTO> queryChallengerPolicyByPartners(Set<String> partners){
        return null;
    }



}
