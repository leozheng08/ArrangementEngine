package cn.tongdun.kunpeng.api.application.response.ext;

import cn.tongdun.kunpeng.api.application.context.FraudContext;
import cn.tongdun.kunpeng.api.application.response.entity.USRiskResponseFactory;
import cn.tongdun.kunpeng.api.application.step.ext.ICreateRiskResponseExtPt;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: yuanhang
 * @date: 2020-06-18 13:33
 **/
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = "globalegrow")
public class USCreateRiskResponseExt implements ICreateRiskResponseExtPt {

    @Autowired
    private USRiskResponseFactory riskResponseFactory;

    @Override
    public IRiskResponse createRiskResponse(FraudContext context) {
        return riskResponseFactory.newRiskResponse();
    }
}
