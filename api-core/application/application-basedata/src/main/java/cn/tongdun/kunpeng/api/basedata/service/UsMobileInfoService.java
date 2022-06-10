package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:20 下午
 */
@Extension(tenant = "us", business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class UsMobileInfoService implements MobileInfoServiceExtPt {

    @Override
    public PhoneAttrEntity getMobileInfo(String phone, AbstractFraudContext context) {
        return null;
    }
}
