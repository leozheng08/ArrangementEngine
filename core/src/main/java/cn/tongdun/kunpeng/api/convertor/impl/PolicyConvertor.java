package cn.tongdun.kunpeng.api.convertor.impl;

import cn.tongdun.kunpeng.api.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.convertor.IConvertor;
import cn.tongdun.kunpeng.api.dataobject.PolicyDO;
import cn.tongdun.kunpeng.api.dataobject.RuleDO;
import cn.tongdun.kunpeng.api.dataobject.SubPolicyDO;
import cn.tongdun.kunpeng.api.policy.Policy;
import cn.tongdun.kunpeng.api.runmode.ParallelSubPolicy;
import cn.tongdun.kunpeng.api.runtime.IExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyConvertor implements IConvertor<PolicyDO,Policy> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        convertorFactory.register(PolicyDO.class,this);
    }

    @Override
    public Policy convert(PolicyDO t){
        Policy policy = new Policy();
        policy.setPolicyUuId(t.getUuid());
        policy.setPartnerCode(t.getPartnerCode());
        policy.setEventType(t.getEventType());
        policy.setEventId(t.getEventId());
        policy.setAppType(t.getAppType());
        policy.setAppDisplayName(t.getAppDisplayName());
        policy.setName(t.getName());
        policy.setRiskType(t.getRiskType());

        //子策略
        List<String> subPolicyList = new ArrayList<>();
        policy.setSubPolicyList(subPolicyList);
        List<SubPolicyDO> subPolicyDOList= t.getSubPolicyList();
        if(subPolicyDOList != null){
            for(SubPolicyDO subPolicyDO:subPolicyDOList){
                subPolicyList.add(subPolicyDO.getUuid());
            }
        }

        //策略运行模式
        ParallelSubPolicy parallelSubPolicy = new ParallelSubPolicy();
        parallelSubPolicy.setPolicyUuid(t.getUuid());
        policy.setRunMode(parallelSubPolicy);

        return policy;
    }



}
