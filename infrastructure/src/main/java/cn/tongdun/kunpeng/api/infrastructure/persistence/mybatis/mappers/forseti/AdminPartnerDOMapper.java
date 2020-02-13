/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.forseti;


import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminPartnerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdminPartnerDOMapper {

    public List<AdminPartnerDO> queryByParams(Map<String, Object> map);

    List<String> queryAllEnabledPartnerCodes();
}
