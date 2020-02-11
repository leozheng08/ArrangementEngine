package cn.tongdun.kunpeng.api.ruledetail;


import lombok.Data;


/**
 * 四则运算详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class FourCalculationDetail extends ConditionDetail {

    private Number left;
    private Number right;

    private Double result;

    public FourCalculationDetail(){
        super("four_calculation");
    }

}
