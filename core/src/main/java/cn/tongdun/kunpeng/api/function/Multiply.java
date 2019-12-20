package cn.tongdun.kunpeng.api.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Evaluable;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: liang.chen
 * @Date: 2019/12/19 上午10:15
 */
public class Multiply extends ArithmeticOperator{


    @Override
    protected Number doEval(ExecuteContext executeContext){
        Number result = null;
        try{
            for(Evaluable<Object> operand :operandList){
                if(result == null){
                    result = (Number)operand.eval(executeContext);
                } else {
                    result = multiply(result , (Number)operand.eval(executeContext));
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


    /**
     * 乘法运算
     *
     * @param o1
     * @param o2
     * @return
     */
    protected Number multiply(Number o1, Number o2) {
        if (o1 == null || o2 == null) {
            return null;
        }

        int type = MathUtil.getMaxNumberType(o1, o2);
        switch (type) {
            case 1:
                return new Integer(MathUtil.intValue(o1) * MathUtil.intValue(o2));

            case 2:
                return new Long(MathUtil.longValue(o1) * MathUtil.longValue(o2));

            case 3:
                return new Double(MathUtil.doubleValue(o1) * MathUtil.doubleValue(o2));

            case 4:
                return MathUtil.toBigDecimal(o1).multiply(MathUtil.toBigDecimal(o2));

            default:
                return MathUtil.toBigDecimal(o1).multiply(MathUtil.toBigDecimal(o2));
        }
    }

}
