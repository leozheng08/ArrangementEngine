package cn.tongdun.kunpeng.api.intf.adapter.dubbo;

import cn.tongdun.kunpeng.api.engine.convertor.impl.DecisionFlowConvertor;
import cn.tongdun.kunpeng.api.engine.convertor.impl.RuleConvertor;
import cn.tongdun.kunpeng.client.api.IPolicyParseCheck;
import cn.tongdun.kunpeng.client.dto.DecisionFlowDTO;
import cn.tongdun.kunpeng.client.dto.RuleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: liang.chen
 * @Date: 2020/3/17 下午4:59
 */
@Service
public class PolicyParseCheckImpl implements IPolicyParseCheck{


    @Autowired
    private RuleConvertor ruleConvertor;

    @Autowired
    private DecisionFlowConvertor decisionFlowConvertor;

    @Override
    public boolean checkRule(RuleDTO ruleDTO) throws Exception{

        ruleConvertor.convert(ruleDTO);

        return true;
    }


    @Override
    public boolean checkFlow(DecisionFlowDTO decisionFlowDTO) throws Exception{

        decisionFlowConvertor.convert(decisionFlowDTO);
        return true;
    }
}
