package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;


/**
 * @Author: liuq
 * @Date: 2020/5/29 2:23 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSMobileInfoService implements MobileInfoServiceExtPt {

    @Override
    public MobileInfoDO getMobileInfo(String phone) {
        return null;
    }
}
