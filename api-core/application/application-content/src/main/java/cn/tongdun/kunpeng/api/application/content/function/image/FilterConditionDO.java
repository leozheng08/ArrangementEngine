/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.application.content.function.image;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hao.zhang 2014年4月5日 下午3:24:00
 */
@Data
public class FilterConditionDO implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 左变量名称 */
    private String leftPropertyName;
    /**  操作符 */
    private String operator;
     /**  右变量 */
    private String rightValue;
     /**  右变量类型，可以是input，也可以是context */
    private String rightValueType;
     /**  左变量类型，如string，number等 */
    private String type;
     /**  该过滤条件与其他过滤条件之间的关系，可以是&&，|| */
    private String logicRelation;
    private String iterateType = "any";

}
