/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用对象
 * 
 * @author kai.zhang 2014年2月20日 上午10:22:38
 */
@Data
public class AdminApplicationDO extends CommonDO {

    private static final long serialVersionUID = 3006486291037638678L;
    private String            name;                                   // 应用标识
    private String            displayName;                            // 应用名称
    private String            appType;                                // 应用类型 web,ios,android
    private String            partnerCode;                            // 合作方名称
    private String            secretKey;                              // 密钥
    private String            version;                                // 版本
    private String            createdBy;                              // 创建者
    private String            updatedBy;                              // 修改者
    private String            description;                            // 描述
    private String            appTypeName;                            // 应用类型展示名 网站,ios,安卓
    private String            productCode;                            // 产品标识
    private String            productTag;                             // 产品标签

    private List<EventTypeDO> eventTypeList    = new ArrayList<>();

    public AdminApplicationDO(){
        super();
    }

    public AdminApplicationDO(String name, String displayName){
        super();
        this.name = name;
        this.displayName = displayName;
    }

    /**
     * All:全部应用
     */
    public static AdminApplicationDO theAll() {
        AdminApplicationDO result = new AdminApplicationDO();
        result.setName("All");
        result.setDisplayName("全部应用");
        return result;
    }

}
