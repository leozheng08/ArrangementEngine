/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.api.dao;

import cn.tongdun.kunpeng.api.dataobj.AdminDictionaryDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 类AdminDictionaryDao.java的实现描述：TODO 类实现描述
 */
@Repository
public interface AdminDictionaryDao extends CommonDao<AdminDictionaryDO> {

    String queryByKey(@Param("key") String key);
}
