/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;


import cn.tongdun.ddd.common.domain.ConcurrencySafeEntity;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 合作方对象
 * 
 * @author kai.zhang 2014年2月20日 上午10:22:38
 */
@Data
public class AdminApplicationDO extends ConcurrencySafeEntity {

    private String            name;                                   // 应用标识
    private String            displayName;                            // 应用名称
    private String            appType;                                // 应用类型 web,ios,android
    private String            partnerCode;                            // 合作方名称
    private String            secretKey;                              // 密钥
    private String            version;                                // 版本
    private String            appTypeName;                            // 应用类型展示名 网站,ios,安卓

}
