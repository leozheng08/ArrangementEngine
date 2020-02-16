package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.SubPolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import cn.tongdun.kunpeng.api.engine.model.runmode.ParallelSubPolicy;
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
public class PolicyConvertor implements IConvertor<PolicyDTO,Policy> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        convertorFactory.register(PolicyDTO.class,this);
    }

    @Override
    public Policy convert(PolicyDTO t){
        Policy policy = new Policy();
        policy.setUuid(t.getUuid());
        policy.setPartnerCode(t.getPartnerCode());
        policy.setEventType(t.getEventType());
        policy.setEventId(t.getEventId());
        policy.setAppType(t.getAppType());
        policy.setName(t.getName());

        //子策略
        List<String> subPolicyList = new ArrayList<>();
        policy.setSubPolicyList(subPolicyList);
        List<SubPolicyDTO> subPolicyDOList= t.getSubPolicyList();
        if(subPolicyDOList != null){
            for(SubPolicyDTO subPolicyDO:subPolicyDOList){
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
