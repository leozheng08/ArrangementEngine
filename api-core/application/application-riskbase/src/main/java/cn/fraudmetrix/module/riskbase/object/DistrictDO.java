/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.fraudmetrix.module.riskbase.object;

import cn.fraudmetrix.module.riskbase.common.CommonDO;

/**
 * 类DistrictDO.java的实现描述：TODO 类实现描述
 *
 * @author zhaohuabin 2014年6月20日 下午5:22:38
 */
public class DistrictDO extends CommonDO {

    private static final long serialVersionUID = -1823902697034251200L;

    private String code;
    private short type;
    private String name;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the type
     */
    public short getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(short type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
