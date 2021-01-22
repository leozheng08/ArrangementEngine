package cn.tongdun.kunpeng.api.basedata.service;

import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.fraudmetrix.module.riskbase.service.intf.IdInfoQueryService;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: liuq
 * @Date: 2020/5/29 2:34 下午
 */
@Extension(tenant = BizScenario.DEFAULT, business = BizScenario.DEFAULT, partner = BizScenario.DEFAULT)
public class SaaSIdInfoService implements IdInfoServiceExtPt{

    @Autowired
    private IdInfoQueryService idInfoQueryService;

    @Override
    public IdInfo getIdInfo(String id) {
        return idInfoQueryService.getIdInfo(id);
    }
}
