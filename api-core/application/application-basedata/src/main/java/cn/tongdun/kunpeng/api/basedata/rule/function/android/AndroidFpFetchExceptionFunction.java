package cn.tongdun.kunpeng.api.basedata.rule.function.android;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractFpExceptionfunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @author: yuanhang
 * @date: 2020-08-07 14:32
 **/
public class AndroidFpFetchExceptionFunction extends AbstractFpExceptionfunction {
    @Override
    public String getType() {
        return "android";
    }

    @Override
    public String getName() {
        return Constant.Function.ANDROID_FP_EXCEPTION;
    }
}
