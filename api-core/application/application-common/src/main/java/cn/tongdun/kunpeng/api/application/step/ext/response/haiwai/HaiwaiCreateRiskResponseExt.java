package cn.tongdun.kunpeng.api.application.step.ext.response.haiwai;

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
@Extension(business = BizScenario.DEFAULT,tenant = "sea",partner = BizScenario.DEFAULT)
public class HaiwaiCreateRiskResponseExt implements ICreateRiskResponseExtPt{

    @Autowired
    private UnderlineRiskResponseFactory riskResponseFactory;

    @Override
    public IRiskResponse createRiskResponse(FraudContext context ){
        return riskResponseFactory.newRiskResponse();
    }
}
