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
public class AdminPartnerDO extends ConcurrencySafeEntity {

    private static final long serialVersionUID = -7913471839482766539L;
    private String            partnerCode;                             // 合作标识
    private String            partnerKey;                              // 合作方密钥
    private String            displayName;                             // 合作方公司名称
    private Boolean           status;                                  // 状态
    private String            version;                                 // 版本
    private Boolean           testAccount;                             // 是否是测试账号
    private Timestamp         endTime;                                 // 测试账号的截止时间
    private String            industryType;                            // 行业类型
    private String            partnerType;                             // 合作类型
    private String            channelCode;                             // 所属渠道
    private String            secondIndustryType;                      // 二级行业类型
    private String            labels;                                  // 标签
}
