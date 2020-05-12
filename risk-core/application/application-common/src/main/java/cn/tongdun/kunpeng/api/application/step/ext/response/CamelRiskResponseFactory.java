package cn.tongdun.kunpeng.api.application.step.ext.response;

import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.kunpeng.client.data.impl.camel.HitRule;
import cn.tongdun.kunpeng.client.data.impl.camel.OutputField;
import cn.tongdun.kunpeng.client.data.impl.camel.RiskResponse;
import cn.tongdun.kunpeng.client.data.impl.camel.SubPolicyResult;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午10:11
 */
@Component
public class CamelRiskResponseFactory implements IRiskResponseFactory {

    @Override
    public IRiskResponse newRiskResponse(){
        RiskResponse riskResponse = new RiskResponse();
        riskResponse.setFactory(this);
        return riskResponse;
    }

    @Override
    public IHitRule newHitRule(){
        return new HitRule();
    }

    @Override
    public IOutputField newOutputField(){
        return new OutputField();
    }

    @Override
    public ISubPolicyResult newSubPolicyResult(){
        return new SubPolicyResult();
    }
}
