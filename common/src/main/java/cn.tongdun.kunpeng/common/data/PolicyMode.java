/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.common.data;

/**
 * 策略模式,如首次匹配、最坏匹配、权重模式
 *
 */
public enum PolicyMode {

    FirstMatch("首次匹配"),

    WorstMatch("最坏匹配"),

    Weighted("权重模式");

    private String displayName;

    PolicyMode(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
