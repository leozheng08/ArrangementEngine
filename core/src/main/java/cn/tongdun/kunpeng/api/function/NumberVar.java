package cn.tongdun.kunpeng.api.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Variable;

/**
 * @Author: liuq
 * @Date: 2019/12/5 5:47 PM
 */
public class NumberVar implements Variable {

    private Number value;

    public NumberVar(Number value) {
        this.value = value;
    }

    @Override
    public Number eval(ExecuteContext executeContext) {
        return value;
    }
}
