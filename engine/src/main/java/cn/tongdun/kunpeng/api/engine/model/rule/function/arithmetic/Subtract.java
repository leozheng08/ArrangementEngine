package cn.tongdun.kunpeng.api.engine.model.rule.function.arithmetic;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Evaluable;
import cn.tongdun.kunpeng.api.engine.model.rule.operator.ArithmeticOperator;

/**
 * @Author: liang.chen
 * @Date: 2019/12/20 下午1:47
 */
public class Subtract extends ArithmeticOperator {

    @Override
    protected Number doEval(ExecuteContext executeContext){
        Number result = null;
        try{
            for(Evaluable operand :operandList){
                if(result == null){
                    result = toNumber(operand.eval(executeContext));
                } else {
                    result = MathUtil.subtract(result , toNumber(operand.eval(executeContext)));
                }
            }
        } catch (Exception e) {
            return MathUtil.NaN;
        }
        return result;
    }

    @Override
    public String getName(){
        return "subtract";
    }

}
