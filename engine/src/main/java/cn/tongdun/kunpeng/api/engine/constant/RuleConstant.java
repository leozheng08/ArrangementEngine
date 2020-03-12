package cn.tongdun.kunpeng.api.engine.constant;

/**
 * @Author: liuq
 * @Date: 2020/3/12 3:14 PM
 */
public class RuleConstant {

    /**
     * 标识当前函数是否全部规则参数都放在params中。
     * 有三个地方需要使用到函数
     * 1.策略指标
     * 2.独立规则
     * 3.自定义规则中的作为其中条件的规则模板
     * 一般情况下，函数的逻辑只会计算数值，不会进行判断比较，但是时间点规则有上下限，不能作为一个右变量，需要把所有参数放在函数的params中，
     * 所以在函数中，需要根据不同的场景，可能需要进行计算，也可能需要进行计算，然后进行判断。
     */
    public static final String FUNC_DESC_PARAMS_ALL = "__func_desc_params_all_";
}
