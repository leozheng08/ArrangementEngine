package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldEncryptionDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyFieldNecessaryDTO;
import cn.tongdun.kunpeng.api.engine.model.policyfieldencryption.PolicyFieldEncryption;
import cn.tongdun.kunpeng.api.engine.model.policyfieldnecessary.PolicyFieldNecessary;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author hls
 * @version 1.0
 * @date 2021/11/3 2:07 下午
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class PolicyFieldNecessaryConvertor implements IConvertor<PolicyFieldNecessaryDTO, PolicyFieldNecessary> {
    @Autowired
    DefaultConvertorFactory convertorFactory;

    // 将PolicyFieldNecessaryDTO.class和PolicyFieldNecessaryConvertor注册进DefaultConvertorFactory.convertorMap,便于load的时候去取值
    @PostConstruct
    public void init() {
        convertorFactory.register(PolicyFieldNecessaryDTO.class, this);
    }

    @Override
    public PolicyFieldNecessary convert(PolicyFieldNecessaryDTO policyFieldNecessaryDTO) {
        PolicyFieldNecessary fieldNecessary = new PolicyFieldNecessary();
        BeanUtils.copyProperties(policyFieldNecessaryDTO, fieldNecessary);
        return fieldNecessary;
    }
}
