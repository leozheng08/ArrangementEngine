/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.fraudmetrix.module.riskbase.dao;

import cn.fraudmetrix.module.riskbase.common.CommonDao;
import cn.fraudmetrix.module.riskbase.object.DistrictDO;

import java.util.List;

/**
 * 类DistrictDao.java的实现描述：TODO 类实现描述
 *
 * @author zhaohuabin 2014年6月20日 下午5:29:06
 */
public interface DistrictDao extends CommonDao<DistrictDO> {

    List<DistrictDO> queryByType(List<Integer> type);
}
