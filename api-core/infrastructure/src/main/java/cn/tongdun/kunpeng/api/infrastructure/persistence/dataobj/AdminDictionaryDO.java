/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminDictionaryDO extends CommonDO implements Serializable {

    private static final long serialVersionUID = 3151001191137443801L;
    // 键
    private String key;
    // 值
    private String value;
    // 描述
    private String description;

}
