package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldDTO;
import cn.tongdun.kunpeng.api.engine.model.policyfield.PolicyField;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zeyuan.zheng@tongdun.cn
 * @date 2/17/22 11:51 AM
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyFieldConvertor implements IConvertor<PolicyFieldDTO, PolicyField> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init(){
        convertorFactory.register(PolicyFieldDTO.class,this);
    }

    @Override
    public PolicyField convert(PolicyFieldDTO policyFieldDTO) {
        PolicyField policyField = new PolicyField();
        BeanUtils.copyProperties(policyFieldDTO, policyField);
        return policyField;
    }
}
