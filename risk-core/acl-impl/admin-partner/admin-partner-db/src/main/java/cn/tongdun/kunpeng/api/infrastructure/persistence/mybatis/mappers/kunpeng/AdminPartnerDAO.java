/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.infrastructure.persistence.mybatis.mappers.kunpeng;


import cn.tongdun.kunpeng.api.infrastructure.persistence.dataobj.AdminPartnerDO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AdminPartnerDAO {

    public AdminPartnerDO selectByPartnerCode(String partnerCode);

    public List<AdminPartnerDO> selectEnabledByPartners(Set<String> partners);

    List<String> selectAllEnabledPartnerCodes();
}
