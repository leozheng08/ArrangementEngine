package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.elfin.biz.intf.BaseDataQueryService;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @Author: liuq
 * @Date: 2020/5/29 2:23 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSMobileInfoService implements MobileInfoServiceExtPt {

    private static final Logger logger = LoggerFactory.getLogger(SaaSMobileInfoService.class);

    @Autowired
    private BaseDataQueryService baseDataQueryService;

    @Override
    public PhoneAttrEntity getMobileInfo(String phone, AbstractFraudContext context) {
        PhoneAttrEntity phoneAttrEntity = null;
        try {
            if (StringUtils.isBlank(phone)) {
                logger.info("get phoneInfo from elfin with params null", "elfinBaseDateService");
                return null;
            }

            phoneAttrEntity = baseDataQueryService.getPhoneInfo(phone);
            if (null != phoneAttrEntity) {
                return phoneAttrEntity;
            }
        } catch (Exception e) {
            logger.warn("get phoneInfo of phone:{} from elfin error", phone, e);
            return null;
        }
        return null;
    }

}
