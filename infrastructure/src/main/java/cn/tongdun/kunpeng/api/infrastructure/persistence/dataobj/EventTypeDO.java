/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件类型
 * 
 * @author du 2014年9月25日 下午3:21:48
 */

public class EventTypeDO extends CommonDO {

    private static final long serialVersionUID = -4778972339196365935L;

    private String            name;

    private String            displayName;

    private List<RiskTypeDO>  riskTypes        = new ArrayList<>();    // 风险类型

    public List<RiskTypeDO> getRiskTypes() {
        return riskTypes;
    }

    public void setRiskTypes(List<RiskTypeDO> riskTypes) {
        this.riskTypes = riskTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return this.name + ":" + this.displayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EventTypeDO other = (EventTypeDO) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * All:全部事件类型
     */
    public static EventTypeDO theAll() {
        EventTypeDO result = new EventTypeDO();
        result.setName("All");
        result.setDisplayName("全部事件");
        return result;
    }
}
