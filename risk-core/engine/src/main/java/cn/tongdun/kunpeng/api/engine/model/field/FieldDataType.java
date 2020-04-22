/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.engine.model.field;

/**
 * 类ExtendFieldType.java的实现描述：TODO 类实现描述
 * 
 * @author zhaoyao 2014年3月10日 下午7:03:19
 */
public enum FieldDataType {
    INT("整数"),
    DOUBLE("小数"),
    STRING("字符串"),
    BOOLEAN("布尔类型"),
    ARRAY("数组类型"),
    DATETIME("日期类型（格式：2014-03-01 08:16:30）"),
    OBJECT("自定义对象");

    private String displayName;

    FieldDataType(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
