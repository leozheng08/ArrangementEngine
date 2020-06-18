package cn.tongdun.kunpeng.api.application.response.entity;

import cn.tongdun.kunpeng.client.data.*;
import cn.tongdun.kunpeng.client.data.impl.us.SubPolicyResult;
import cn.tongdun.kunpeng.client.data.impl.us.USHitRule;
import org.springframework.stereotype.Component;

/**
 * @author: yuanhang
 * @date: 2020-06-18 10:06
 **/
@Component
public class USRiskResponseFactory implements IRiskResponseFactory {

    @Override
    public IRiskResponse newRiskResponse() {
        USRiskResponse response = new USRiskResponse();
        response.setFactory(this);
        return response;
    }

    @Override
    public IHitRule newHitRule() {
        return new USHitRule();
    }

    @Override
    public IOutputField newOutputField() {
        return null;
    }

    @Override
    public ISubPolicyResult newSubPolicyResult() {
        return new SubPolicyResult();
    }
}
