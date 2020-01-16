package cn.tongdun.kunpeng.api.repository;

import cn.tongdun.kunpeng.api.dataobject.*;
import cn.tongdun.kunpeng.api.policy.IPolicyRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: liang.chen
 * @Date: 2019/12/18 上午11:42
 */
@Component
public class PolicyRepository implements IPolicyRepository{


    //取得所有策略清单
    @Override
    public List<PolicyModifiedDO> queryByPartners(Set<String> partners){

        //mock
        List<PolicyModifiedDO> list = new ArrayList();
        PolicyModifiedDO policyModifiedDO = new PolicyModifiedDO();
        policyModifiedDO.setPolicyUuid("123456789");
        policyModifiedDO.setDefault(true);
        policyModifiedDO.setStatus(true);
        policyModifiedDO.setEventId("eventId");
        policyModifiedDO.setPartnerCode("demo");
        policyModifiedDO.setAppName("ios");
        policyModifiedDO.setModifiedVersion(1);
        policyModifiedDO.setVersion("v1.0");
        list.add(policyModifiedDO);
        return list;
    }


    //取得所有策略清单
    @Override
    public PolicyModifiedDO queryByPartner(String partners){

        //mock
        PolicyModifiedDO policyModifiedDO = new PolicyModifiedDO();
        policyModifiedDO.setPolicyUuid("123456789");
        policyModifiedDO.setDefault(true);
        policyModifiedDO.setStatus(true);
        policyModifiedDO.setEventId("eventId");
        policyModifiedDO.setPartnerCode("demo");
        policyModifiedDO.setAppName("ios");
        policyModifiedDO.setModifiedVersion(1);
        policyModifiedDO.setVersion("v1.0");
        return policyModifiedDO;
    }


    @Override
    @Cacheable("policyDOCache")
    public PolicyDO queryByUuid(String uuid){

        //策略
        PolicyDO policyDO = new PolicyDO();
        policyDO.setUuid("123456789");
        policyDO.setDefault(true);
        policyDO.setStatus(true);
        policyDO.setEventId("eventId");
        policyDO.setPartnerCode("demo");
        policyDO.setAppName("ios");
        policyDO.setVersion("v1.0");
        policyDO.setModifiedVersion(1);
        policyDO.setName("policy name");

        //子策略
        List<SubPolicyDO> subPolicyDOList = new ArrayList<>();
        policyDO.setSubPolicyList(subPolicyDOList);
        SubPolicyDO subPolicyDO = new SubPolicyDO();
        subPolicyDOList.add(subPolicyDO);
        subPolicyDO.setUuid("2343241342123");
        subPolicyDO.setName("sub policy name");
        subPolicyDO.setMode("Weighted");
        subPolicyDO.setReviewThreshold(30);
        subPolicyDO.setDenyThreshold(60);


        //规则
        List<RuleDO> ruleDOList = new ArrayList<>();
        subPolicyDO.setRules(ruleDOList);
        RuleDO ruleDO = new RuleDO();
        ruleDOList.add(ruleDO);
        ruleDO.setId(56935914L);
        ruleDO.setUuid("b94dbf4dfe6d41b5a62789d0a8400240");
        ruleDO.setName("正则匹配");
        ruleDO.setOperateCode("Accept");
        ruleDO.setTemplate("pattern/regex");
//        ruleDO.setTemplate("regex");
        ruleDO.setRuleCustomId("56935914");
        ruleDO.setDownLimitScore(-30D);
        ruleDO.setUpLimitScore(30D);
        ruleDO.setBaseWeight(10);
        ruleDO.setWeightRatio(5);
        ruleDO.setWeightIndex("123456");

        //规则条件
        List<RuleConditionElementDO> ruleConditionElementDOList = new ArrayList<>();
        ruleDO.setRuleConditionElements(ruleConditionElementDOList);
        RuleConditionElementDO ruleConditionElementDO = new RuleConditionElementDO();
        ruleConditionElementDOList.add(ruleConditionElementDO);

        ruleConditionElementDO.setId(146804004L);
        ruleConditionElementDO.setUuid("b956811644594b3cb7c8fda00fbf0d38");
        ruleConditionElementDO.setLogicOperator("&&");
        ruleConditionElementDO.setProperty("pattern/regex");
//        ruleConditionElementDO.setProperty("regex");
        ruleConditionElementDO.setOperator("operator");
        ruleConditionElementDO.setValue("1");
        ruleConditionElementDO.setType("alias");
        ruleConditionElementDO.setParams("[{\"name\":\"property\",\"type\":\"string\",\"value\":\"partnerCode\"},{\"name\":\"result\",\"type\":\"int\",\"value\":\"1\"},{\"name\":\"ignoreCase\",\"type\":\"\",\"value\":\"1\"},{\"name\":\"regex\",\"type\":\"string\",\"value\":\".*\"},{\"name\":\"iterateType\",\"type\":\"string\",\"value\":\"any\"}]");
        ruleConditionElementDO.setDescripe("正则表达式");
        ruleConditionElementDO.setPropertyUseOriginValue(false);


        //action
        List<RuleActionElementDO> ruleActionElementDOList = new ArrayList<>();
        ruleDO.setRuleActionElements(ruleActionElementDOList);
        RuleActionElementDO ruleActionElementDO = new RuleActionElementDO();
        ruleActionElementDOList.add(ruleActionElementDO);
        String actions ="[{\"leftProperty\":\"accountLogin\",\"leftPropertyType\":\"\",\"operator\":\"==\",\"rightValue\":\"abc\",\"rightValueType\":\"input\"},{\"leftProperty\":\"ip3\",\"leftPropertyType\":\"\",\"operator\":\"==\",\"rightValue\":\"accountMobile\",\"rightValueType\":\"context\"}]";
        ruleActionElementDO.setActions(actions);
        ruleActionElementDO.setFkRuleUuid(ruleDO.getUuid());
        ruleActionElementDO.setId(234232L);

        return policyDO;
    }
}
