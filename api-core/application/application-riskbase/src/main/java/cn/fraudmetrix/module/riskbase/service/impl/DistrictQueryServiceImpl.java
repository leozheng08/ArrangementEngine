/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.fraudmetrix.module.riskbase.service.impl;

import cn.fraudmetrix.module.riskbase.common.DistrictType;
import cn.fraudmetrix.module.riskbase.dao.DistrictDao;
import cn.fraudmetrix.module.riskbase.object.DistrictDO;
import cn.fraudmetrix.module.riskbase.service.intf.DistrictQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类DistrictQueryServiceImpl.java的实现描述：TODO 类实现描述
 *
 * @author zhaohuabin 2014年6月23日 上午11:41:13
 */
public class DistrictQueryServiceImpl implements DistrictQueryService {

    private static final Map<String, DistrictDO> districtMap = new HashMap<String, DistrictDO>();

    private Timestamp curMaxTime;

    @Autowired
    private DistrictDao districtDao;

    public void init() {
        List<DistrictDO> districtList = districtDao.queryAll();
        putToCache(districtList);
    }

    public void reload() {
        List<DistrictDO> districtList = null;
        if (null != curMaxTime) {
            districtList = districtDao.queryLatest(curMaxTime);
        } else {
            districtList = districtDao.queryAll();
        }
        putToCache(districtList);
    }

    public DistrictDO getDistrictInfo(String code) {
        return districtMap.get(code);
    }

    private void putToCache(List<DistrictDO> list) {
        if (null != list && list.size() > 0) {
            for (DistrictDO districtDO : list) {
                districtMap.put(districtDO.getCode(), districtDO);
                if (null == curMaxTime || districtDO.getGmtModified().after(curMaxTime)) {
                    curMaxTime = districtDO.getGmtModified();
                }
            }
        }
    }

    @Override
    public DistrictDO getDistrictInfo(String code, DistrictType type) {
        if (StringUtils.isBlank(code) || null == type) {
            return null;
        }
        DistrictDO districtInfo = getDistrictInfo(code);
        if (null == districtInfo) {
            return null;
        }
        return DistrictType.getDistrictType(districtInfo.getType()) == type ? districtInfo : null;
    }

    @Override
    public List<DistrictDO> getDistrictInfoFromMysql(List<Integer> type) {
        if (type.isEmpty()) return Collections.emptyList();
        return districtDao.queryByType(type);
    }
}
