package cn.tongdun.kunpeng.api.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Evaluable;

/**
 * @Author: liang.chen
 * @Date: 2019/12/19 上午10:15
 */
public class Min extends ArithmeticOperator{


    @Override
    protected Number doEval(ExecuteContext executeContext){
        Number result = null;
        try{
            for(Evaluable<Object> operand :operandList){
                if(result == null){
                    result = (Number)operand.eval(executeContext);
                } else {
                    result = MathUtil.min(result , (Number)operand.eval(executeContext));
                }
            }
        } catch (Exception e) {
            return MathUtil.NaN;
        }
        return result;
    }

    @Override
    public String getName(){
        return "min";
    }




}
