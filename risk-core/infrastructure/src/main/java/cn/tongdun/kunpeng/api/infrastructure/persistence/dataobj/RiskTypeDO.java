/*
 * Copyright 2015 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 风险类型对象
 * 
 * @author chenchanglong 2015年1月8日 下午3:13:43
 */
public class RiskTypeDO {

    private String riskTypeName;
    private String riskTypeDisplayName;

    public String getRiskTypeName() {
        return riskTypeName;
    }

    public void setRiskTypeName(String riskTypeName) {
        this.riskTypeName = riskTypeName;
    }

    public String getRiskTypeDisplayName() {
        return riskTypeDisplayName;
    }

    public void setRiskTypeDisplayName(String riskTypeDisplayName) {
        this.riskTypeDisplayName = riskTypeDisplayName;
    }

    private String name;
    private String displayName;
    private String eventTypeName;
    private String eventTypeDisplayName;

    public RiskTypeDO(){
    }

    public static List<String> toSimpleRiskType(List<String> list) {
        List<String> result = new ArrayList<>();
        for (String e : list) {
            result.add(toSimpleRiskType(e));
        }
        return result;
    }

    public static String toSimpleRiskType(String fullRiskType) {
        return parse(fullRiskType).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static RiskTypeDO parse(String fullRiskType) {
        RiskTypeDO result = new RiskTypeDO();
        result.setFullRiskType(fullRiskType);
        return result;
    }

    public void setFullRiskType(String fullRiskType) {
        int pos = fullRiskType.indexOf(".");
        if (pos < 0) {
            throw new IllegalArgumentException("Full risk type format error: " + fullRiskType);
        }
        String eventType = fullRiskType.substring(0, pos);
        String riskType = fullRiskType.substring(pos + 1);
        this.eventTypeName = eventType;
        this.name = riskType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getEventTypeDisplayName() {
        return eventTypeDisplayName;
    }

    public void setEventTypeDisplayName(String eventTypeDisplayName) {
        this.eventTypeDisplayName = eventTypeDisplayName;
    }

    public String getFullName() {
        if (StringUtils.isBlank(eventTypeName) || StringUtils.isBlank(name)) {
            return null;
        }

        return eventTypeName + "." + name;
    }
}
