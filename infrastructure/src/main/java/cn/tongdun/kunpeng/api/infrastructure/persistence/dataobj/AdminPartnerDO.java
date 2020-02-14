/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 合作方对象
 * 
 * @author kai.zhang 2014年2月20日 上午10:22:38
 */
public class AdminPartnerDO extends CommonDO {

    private static final long serialVersionUID = -7913471839482766539L;
    private String            partnerCode;                             // 合作标识
    private String            partnerKey;                              // 合作方密钥
    private String            displayName;                             // 合作方公司名称
    private String            admin;                                   // 合作方管理员名称
    private String            domain;                                  // 应用标识
    private Boolean           status;                                  // 状态
    private Boolean           creditcloudStatus;                       // 是否开启信贷云,默认未激活
    private String            version;                                 // 版本
    private String            createdBy;                               // 创建者
    private String            updatedBy;                               // 修改者
    private String            description;                             // 描述
    private Boolean           testAccount;                             // 是否是测试账号
    private Timestamp         endTime;                                 // 测试账号的截止时间
    private String            holder;                                  // 拥有该合作方的同盾用户
    private String            industryType;                            // 行业类型
    private String            partnerType;                             // 合作类型
    private String            channelCode;                             // 所属渠道
    private String            secondIndustryType;                      // 二级行业类型
    private String            labels;                                  // 标签

    private String            industryTypeDisplayName;
    private String            partnerTypeDisplayName;
    private String            channelDisplayName;
    private String            secondIndustryDisplayName;

    private String            passwordPlainText;                       // 合作方管理员登录密码明文

    private String            alterMsg;                                // 提示信息

    private List<AdminApplicationDO> applications     = new ArrayList<AdminApplicationDO>();
}
