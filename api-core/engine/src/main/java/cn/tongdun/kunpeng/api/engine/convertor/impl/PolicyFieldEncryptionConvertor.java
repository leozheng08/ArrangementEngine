package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldEncryptionDTO;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.PolicyFieldEncryption;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/3 11:18 上午
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyFieldEncryptionConvertor implements IConvertor<PolicyFieldEncryptionDTO, PolicyFieldEncryption> {
    @Autowired
    DefaultConvertorFactory convertorFactory;

    // 将PolicyFieldEncryptionDTO.class和PolicyFieldEncryptionConvertor注册进DefaultConvertorFactory.convertorMap,便于load的时候去取值
    @PostConstruct
    public void init(){
        convertorFactory.register(PolicyFieldEncryptionDTO.class,this);
    }

    @Override
    public PolicyFieldEncryption convert(PolicyFieldEncryptionDTO policyFieldEncryptionDTO) {
        PolicyFieldEncryption policyFieldEncryption = new PolicyFieldEncryption();
        BeanUtils.copyProperties(policyFieldEncryptionDTO,policyFieldEncryption);
        return policyFieldEncryption;
    }
}
