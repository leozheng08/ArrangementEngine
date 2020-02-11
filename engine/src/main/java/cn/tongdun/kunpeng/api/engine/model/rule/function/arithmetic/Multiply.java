package cn.tongdun.kunpeng.api.engine.model.rule.function.arithmetic;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Evaluable;
import cn.tongdun.kunpeng.api.engine.model.rule.operator.ArithmeticOperator;

/**
 * @Author: liang.chen
 * @Date: 2019/12/19 上午10:15
 */
public class Multiply extends ArithmeticOperator {


    @Override
    protected Number doEval(ExecuteContext executeContext){
        Number result = null;
        try{
            for(Evaluable operand :operandList){
                if(result == null){
                    result = toNumber(operand.eval(executeContext));
                } else {
                    result = MathUtil.multiply(result , toNumber(operand.eval(executeContext)));
                }
            }
        } catch (Exception e) {
            return MathUtil.NaN;
        }
        return result;
    }

    @Override
    public String getName(){
        return "multiply";
    }




}
