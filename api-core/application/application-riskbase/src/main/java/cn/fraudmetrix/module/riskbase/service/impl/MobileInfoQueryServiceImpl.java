package cn.fraudmetrix.module.riskbase.service.impl;

import cn.fraudmetrix.module.riskbase.dao.MobileInfoDao;
import cn.fraudmetrix.module.riskbase.object.DistrictDO;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.fraudmetrix.module.riskbase.service.intf.DistrictQueryService;
import cn.fraudmetrix.module.riskbase.service.intf.MobileInfoQueryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static cn.fraudmetrix.module.riskbase.constant.MobileInfo.*;

//TODO 兼容过度阶段脚本，线上稳定后删除即可
public class MobileInfoQueryServiceImpl implements MobileInfoQueryService {

    private static final Logger logger = LoggerFactory.getLogger(MobileInfoQueryService.class);

    private ConcurrentMap<String, MobileInfoDO> mobileMap;

    /**
     * 当前数据中的最大时间
     */
    private Timestamp curMaxTime;

    @Autowired
    MobileInfoDao mobileInfoDao;
    @Autowired
    DistrictQueryService districtQueryService;

    @Override
    public void init() {
        mobileMap = new ConcurrentHashMap<String, MobileInfoDO>();
        List<MobileInfoDO> mobileInfoDOs = mobileInfoDao.queryAll();
        putToCache(mobileInfoDOs);
    }

    @Override
    public void reload() {
        List<MobileInfoDO> mobileInfoDOs;
        if (null != curMaxTime) {
            mobileInfoDOs = mobileInfoDao.queryLatest(curMaxTime);
        } else {
            mobileInfoDOs = mobileInfoDao.queryAll();
        }
        putToCache(mobileInfoDOs);
    }

    private void putToCache(List<MobileInfoDO> mobileInfoDOs) {
        if (null != mobileInfoDOs && mobileInfoDOs.size() > 0) {
            for (MobileInfoDO mobileInfoDO : mobileInfoDOs) {
                DistrictDO province, city;
                province = districtQueryService.getDistrictInfo(mobileInfoDO.getProvinceCode());
                if (null != province) {
                    mobileInfoDO.setProvince(province.getName());
                }
                city = districtQueryService.getDistrictInfo(mobileInfoDO.getCityCode());
                if (null != city) {
                    mobileInfoDO.setCity(city.getName());
                }
                mobileMap.put(mobileInfoDO.getPhoneNumber(), mobileInfoDO);
                if (null == curMaxTime || mobileInfoDO.getGmtModified().after(curMaxTime)) {
                    curMaxTime = mobileInfoDO.getGmtModified();
                }
            }
        }
    }

    @Override
    public MobileInfoDO getMobileInfo(String phoneNumber) {
        if (StringUtils.isNotBlank(phoneNumber) && phoneNumber.length() >= 7) {
            /** 1.先由原有的二方包判断，成功会返回正常的结果。
             *  2.当结果为unknown，或者为null的时候，再依据需求方给定的表，第二次判断并返回结果。若仍然失败，则提示改号段未收入。
             **/
            MobileInfoDO mobileInfo = mobileMap.get(phoneNumber.substring(0, 7));
            if (mobileInfo == null) {
                MobileInfoDO mobileInfoDO = new MobileInfoDO();
                mobileInfoDO.setPhoneNumber(phoneNumber.substring(0, 7));
                return setMobileInfoDO(mobileInfoDO, phoneNumber);
            } else if (mobileInfo.getOperators().equals(UNKNOWN.getName())) {
                return setMobileInfoDO(mobileInfo, phoneNumber);
            } else {
                return mobileInfo;
            }
        }
        return null;
    }

    public static MobileInfoDO setMobileInfoDO(MobileInfoDO mobileInfo, String phoneNumber) {
        logger.info("二方包未识别，再次判断！-->{}", phoneNumber);
        if (MOBILE.getSet().contains(phoneNumber.substring(0, 3)) || MOBILE.getSet().contains(phoneNumber.substring(0, 4))) {
            mobileInfo.setOperators(MOBILE.getName());
        } else if (UNICOME.getSet().contains(phoneNumber.substring(0, 3)) || UNICOME.getSet().contains(phoneNumber.substring(0, 4))) {
            mobileInfo.setOperators(UNICOME.getName());
        } else if (TELECOM.getSet().contains(phoneNumber.substring(0, 3)) || TELECOM.getSet().contains(phoneNumber.substring(0, 4))) {
            mobileInfo.setOperators(TELECOM.getName());
        } else {
            mobileInfo.setOperators(UNKNOWN.getName());
            logger.info("二方包未识别，且用户号段暂未添加到MobileInfo中，请添加！-->{}", phoneNumber);
        }
        return mobileInfo;
    }
}
