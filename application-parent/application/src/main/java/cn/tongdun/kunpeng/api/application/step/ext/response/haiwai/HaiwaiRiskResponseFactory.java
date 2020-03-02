package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

import cn.tongdun.kunpeng.client.data.*;
import org.springframework.stereotype.Component;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午10:11
 */
@Component
public class HaiwaiRiskResponseFactory implements IRiskResponseFactory {

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
