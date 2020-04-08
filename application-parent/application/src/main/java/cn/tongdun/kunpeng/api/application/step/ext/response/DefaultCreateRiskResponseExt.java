package cn.tongdun.kunpeng.api.application.step.ext.response;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liang.chen
 * @Date: 2020/3/2 上午2:44
 */
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class DefaultCreateRiskResponseExt implements ICreateRiskResponseExtPt{

    @Autowired
    private DefaultRiskResponseFactory riskResponseFactory;

    @Override
    public IRiskResponse createRiskResponse(FraudContext context ){
        return riskResponseFactory.newRiskResponse();
    }
}
