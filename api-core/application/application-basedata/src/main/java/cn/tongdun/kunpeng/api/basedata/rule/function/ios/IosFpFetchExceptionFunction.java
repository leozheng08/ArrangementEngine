package cn.tongdun.kunpeng.api.basedata.rule.function.ios;

import cn.tongdun.kunpeng.api.basedata.rule.function.AbstractFpExceptionfunction;
import cn.tongdun.kunpeng.api.common.Constant;

/**
 * @author: yuanhang
 * @date: 2020-08-07 14:31
 **/
public class IosFpFetchExceptionFunction extends AbstractFpExceptionfunction {

    @Override
    public String getType() {
        return "ios";
    }

    @Override
    public String getName() {
        return Constant.Function.IOS_FP_EXCEPTION;
    }
}
