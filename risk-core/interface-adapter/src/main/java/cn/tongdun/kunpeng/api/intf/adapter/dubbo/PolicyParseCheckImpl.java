package cn.tongdun.kunpeng.api.intf.adapter.dubbo;

import cn.fraudmetrix.module.tdflow.exception.ParseException;
import cn.tongdun.ddd.common.result.SingleResult;
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
    public SingleResult<Boolean> checkRule(RuleDTO ruleDTO) {
        try{
            ruleConvertor.convert(ruleDTO);
            return SingleResult.success(true);
        }catch (Exception e){
            SingleResult singleResult = SingleResult.failure(500,e.getMessage());
            singleResult.setData(false);
            return singleResult;
        }
    }


    @Override
    public SingleResult<Boolean> checkFlow(String content) {
        try{
            DecisionFlowDTO decisionFlowDTO = new DecisionFlowDTO();
            decisionFlowDTO.setProcessContent(content);
            decisionFlowConvertor.convert(decisionFlowDTO,true);
            return SingleResult.success(true);
        }catch (Exception e){
            SingleResult singleResult = SingleResult.failure(500,e.getMessage());
            singleResult.setData(false);
            return singleResult;
        }
    }
}
