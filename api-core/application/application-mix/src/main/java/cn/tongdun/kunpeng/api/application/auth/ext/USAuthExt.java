package cn.tongdun.kunpeng.api.application.auth.ext;

import cn.tongdun.kunpeng.api.application.step.ext.IAuthExtPt;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.api.common.data.ReasonCode;
import cn.tongdun.kunpeng.api.engine.model.partner.Partner;
import cn.tongdun.kunpeng.api.engine.model.partner.PartnerCache;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: yuanhang
 * @date: 2020-07-28 13:41
 **/
@Extension(business = BizScenario.DEFAULT,tenant = "us",partner = "globalegrow")
public class USAuthExt implements IAuthExtPt {

    @Autowired
    private PartnerCache partnerCache;

    @Override
    public boolean invoke(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        String partnerCode = request.getPartnerCode();
        String secretKey = request.getSecretKey();

        if (StringUtils.isAnyBlank(partnerCode, secretKey)) {
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }

        Partner partner = partnerCache.get(partnerCode);
        if (null == partner) {
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }
        String partnerKey = partner.getPartnerKey();
        if (StringUtils.isNotEmpty(partnerKey) && partner.getPartnerKey().equalsIgnoreCase(partnerKey)) {
            context.setAppName(request.getAppName());
            context.setPartnerCode(partnerCode);
            context.setSecretKey(secretKey);
            return true;
        } else {
            response.setReasonCode(ReasonCode.AUTH_FAILED.toString());
            return false;
        }
    }
}
