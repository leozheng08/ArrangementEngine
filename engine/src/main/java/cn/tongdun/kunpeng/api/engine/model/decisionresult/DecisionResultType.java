package cn.tongdun.kunpeng.api.engine.model.decisionresult;

import lombok.Data;

/**
 * 决策结果，如Accept、Review、Reject, 但不限这三个结果，可能自定义;
 * @Author: liang.chen
 * @Date: 2019/12/17 下午2:07
 */
@Data
public class DecisionResultType implements Comparable<DecisionResultType>{
    private String code;
    private String name;

    //顺序，Accept、Review、Reject 顺序为1、2、3, 序号越大，为最坏结果。
    private Integer order = 0;

    public DecisionResultType(){

    }

    public DecisionResultType(String code, String name){
        this.code = code;
        this.name = name;
    }


    public DecisionResultType(String code, String name,Integer order){
        this.code = code;
        this.name = name;
        this.order = order;
    }

    @Override
    public int compareTo(DecisionResultType o){
        return this.order.compareTo(o.getOrder());
    }
}
