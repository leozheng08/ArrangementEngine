package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.BinInfoDO;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.kunpeng.client.data.IRiskResponse;
import cn.tongdun.kunpeng.client.data.RiskRequest;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:43 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSBinInfoService implements BinInfoServiceExtPt{

    @Override
    public BinInfoDO getBinInfo(String binCode){
        return null;
    }

    @Override
    public boolean getBinInfo(AbstractFraudContext context, IRiskResponse response, RiskRequest request) {
        return true;
    }
}
