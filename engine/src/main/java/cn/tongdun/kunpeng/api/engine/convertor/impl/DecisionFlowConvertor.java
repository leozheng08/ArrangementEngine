package cn.tongdun.kunpeng.api.engine.convertor.impl;

import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.tongdun.kunpeng.api.engine.convertor.DefaultConvertorFactory;
import cn.tongdun.kunpeng.api.engine.convertor.IConvertor;
import cn.tongdun.kunpeng.api.engine.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.api.engine.dto.PolicyDTO;
import cn.tongdun.kunpeng.api.engine.model.decisionmode.DecisionFlow;
import cn.tongdun.kunpeng.api.engine.model.policy.Policy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author: liang.chen
 * @Date: 2019/12/13 上午10:16
 */
@Component
@DependsOn(value = "defaultConvertorFactory")
public class DecisionFlowConvertor implements IConvertor<DecisionFlowDTO, DecisionFlow> {

    @Autowired
    DefaultConvertorFactory convertorFactory;

    @PostConstruct
    public void init() {
        convertorFactory.register(DecisionFlowDTO.class, this);
    }

    @Override
    public DecisionFlow convert(DecisionFlowDTO t) {
        if (StringUtils.isBlank(t.getProcessContent())) {
            throw new ParseException("DecisionFlowConvertor convert error,decisionFlowUuid:" + t.getUuid());
        }

        DecisionFlow decisionFlow = new DecisionFlow();
        decisionFlow.setDecisionFlowUuid(t.getUuid());


        return decisionFlow;
    }
}
