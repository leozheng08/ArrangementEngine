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

    private String            uuid;
    private String            key;                                    // 键
    private String            value;                                  // 值
    private String            description;                            // 描述
    private String            updatedBy;

}
