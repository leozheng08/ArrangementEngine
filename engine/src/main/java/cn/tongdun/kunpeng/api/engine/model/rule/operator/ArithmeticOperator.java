package cn.tongdun.kunpeng.api.engine.model.rule.operator;

import cn.fraudmetrix.module.tdrule.eval.Evaluable;
import cn.fraudmetrix.module.tdrule.operator.AbstractOperator;

import java.math.BigDecimal;

/**
 * @Author: liang.chen
 * @Date: 2019/12/19 下午11:58
 */

public abstract class ArithmeticOperator extends AbstractOperator<Evaluable,Number> {

    protected Number toNumber(Object obj){
        if(obj == null){
            return null;
        }
        if(obj instanceof Number){
            return (Number)obj;
        }

        if(obj instanceof Boolean){
            return (Boolean)obj ? 1 : 0;
        }

        //不带小数点的数字，转为Integer
        String txt = obj.toString();
        if(isDigit(txt)){
           try{
               Integer.valueOf(txt);
           } catch (Exception e){
           }
        }

        //其他转为BigDecimal
        try{
            return new BigDecimal(txt);
        } catch (Exception e){
            return Double.NaN;
        }
    }

    /**
     * 是否为数字
     * @param str
     * @return
     */
    private boolean isDigit(String str) {
        boolean isNumber = true;
        if (str != null && str.trim().length() > 0) {
            char c[] = str.toCharArray();
            for (int i = 0; i < c.length; i++) {
                if (c[i] < 48 || c[i] > 57) {
                    isNumber = false;
                    break;
                }
            }
        } else {
            isNumber = false;
        }
        return isNumber;
    }
}
