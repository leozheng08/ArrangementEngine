package cn.tongdun.kunpeng.api.ruledetail;

import lombok.Data;

import java.util.List;

/**
 * 函数工具箱详情
 * @Author: liang.chen
 * @Date: 2020/2/6 下午4:37
 */
@Data
public class FunctionKitDetail extends ConditionDetail {

    //所有变量
    private List<Number> variables;

    //计算结果
    private Double result;

    public FunctionKitDetail(){
        super("function_kit");
    }



}
