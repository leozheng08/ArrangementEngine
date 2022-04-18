/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.fraudmetrix.module.riskbase.service.intf;

import cn.fraudmetrix.module.riskbase.common.DistrictType;
import cn.fraudmetrix.module.riskbase.object.DistrictDO;

import java.util.List;

/**
 * 地区信息查询接口
 *
 * @author zhaohuabin 2014年6月23日 上午11:22:23
 */
public interface DistrictQueryService extends RiskBaseQueryService {

    DistrictDO getDistrictInfo(String code);

    DistrictDO getDistrictInfo(String code, DistrictType type);

    List<DistrictDO> getDistrictInfoFromMysql(List<Integer> type);
}
