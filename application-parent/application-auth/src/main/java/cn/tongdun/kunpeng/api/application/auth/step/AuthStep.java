package cn.tongdun.kunpeng.api.application.auth.step;

import cn.tongdun.kunpeng.api.application.step.IRiskStep;
import cn.tongdun.kunpeng.api.application.step.Risk;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.tdframework.core.pipeline.Step;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 合作方认证
 * @Author: liang.chen
 * @Date: 2020/2/17 下午11:09
 */
@Component
@Step(pipeline = Risk.NAME, phase = Risk.CHECK, order = 200)
public class AuthStep implements IRiskStep {



    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {

        String partnerCode = request.getPartnerCode();
        String secretKey = request.getSecretKey();


        if(StringUtils.isAnyBlank(partnerCode,secretKey)){
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }

        context.setPartnerCode(partnerCode);
        context.setSecretKey(secretKey);
        return true;
    }
}
