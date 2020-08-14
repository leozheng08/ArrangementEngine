package cn.tongdun.kunpeng.api.basedata.rule.function.anomaly;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractFpExceptionfunction;
import cn.tongdun.kunpeng.api.common.Constant;

public class WebFpFetchExceptionFunction extends AbstractFpExceptionfunction {


    @Override
    public String getType() {
        return "web";
    }

    @Override
    public String getName() {
        return Constant.Function.ANOMALY_FP_EXCEPTION;
    }
}
