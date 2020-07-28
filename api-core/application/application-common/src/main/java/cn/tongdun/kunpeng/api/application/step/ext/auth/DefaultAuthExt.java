package cn.tongdun.kunpeng.api.application.step.ext.auth;

import cn.tongdun.kunpeng.api.application.step.ext.IAuthExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplication;
import cn.tongdun.kunpeng.api.engine.model.application.AdminApplicationCache;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: yuanhang
 * @date: 2020-07-28 13:37
 **/
@Extension(business = BizScenario.DEFAULT,tenant = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class DefaultAuthExt implements IAuthExtPt {

    @Autowired
    private AdminApplicationCache adminApplicationCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String partnerCode = request.getPartnerCode();
        String secretKey = request.getSecretKey();
        if(StringUtils.isAnyBlank(partnerCode,secretKey)){
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }

        AdminApplication adminApplication = adminApplicationCache.getBySecretKey(secretKey);

        if(adminApplication == null){
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }
        context.setAppName(adminApplication.getCode());

        context.setPartnerCode(partnerCode);
        context.setSecretKey(secretKey);
        return true;
    }
}
