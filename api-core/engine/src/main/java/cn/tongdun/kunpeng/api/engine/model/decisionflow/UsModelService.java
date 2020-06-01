package cn.tongdun.kunpeng.api.engine.model.decisionflow;

import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.BizScenario;
import cn.tongdun.tdframework.core.extension.Extension;

/**
 * @Author: liuq
 * @Date: 2020/5/29 5:37 下午
 */
@Extension(tenant = "us",business = BizScenario.DEFAULT,partner = BizScenario.DEFAULT)
public class UsModelService implements ModelService{

    @Override
    public boolean calculate(AbstractFraudContext fraudContext, ModelConfigInfo configInfo) {
        return false;
    }
}
