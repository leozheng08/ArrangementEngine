package cn.fraudmetrix.module.riskbase.service.impl;

import cn.fraudmetrix.module.riskbase.common.DistrictType;
import cn.fraudmetrix.module.riskbase.object.DistrictDO;
import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.fraudmetrix.module.riskbase.service.intf.DistrictQueryService;
import cn.fraudmetrix.module.riskbase.service.intf.IdInfoQueryService;
import cn.fraudmetrix.module.riskbase.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IdInfoQueryServiceImpl implements IdInfoQueryService {

    private static Logger logger = LoggerFactory.getLogger(IdInfoQueryServiceImpl.class);

    @Autowired
    DistrictQueryService districtQueryService;

    @Override
    public void init() {
    }

    @Override
    public void reload() {
    }

    @Override
    public IdInfo getIdInfo(String idNumber) {
        if (StringUtils.isNotBlank(idNumber) && idNumber.length() >= 6) {
            IdInfo idInfo = new IdInfo();
            // 区或县
            String countyCode = idNumber.substring(0, 6);
            setDistrict(countyCode, idInfo);
            // 城市
            if (StringUtils.isBlank(idInfo.getCity())) {
                String cityCode = idNumber.substring(0, 4).concat("00");
                setDistrict(cityCode, idInfo);
            }
            // 省份
            if (StringUtils.isBlank(idInfo.getProvince())) {
                String provinceCode = idNumber.substring(0, 2).concat("0000");
                setDistrict(provinceCode, idInfo);
            }
            if (idNumber.length() == 15) {
                idNumber = CommonUtil.convertIdNumber(idNumber);
            }
            if (null != idNumber && idNumber.length() == 18) {
                String birthdayStr = idNumber.substring(6, 14);
                idInfo.setBirthday(CommonUtil.parse(birthdayStr));
                char sexFlag = idNumber.charAt(16);
                if ((sexFlag - '0') % 2 == 0) {
                    idInfo.setSex(0);
                } else {
                    idInfo.setSex(1);
                }
            }
            return idInfo;
        }
        return null;
    }

    private void setDistrict(String idCode, IdInfo idInfo) {
        DistrictDO districtDO = districtQueryService.getDistrictInfo(idCode);
        if (districtDO == null) {
            logger.info("can't find district, idCode: " + idCode);
            return;
        }
        DistrictType districtType = DistrictType.getDistrictType(districtDO.getType());
        if (districtType == null) {
            logger.info("can't find districtType, type: " + districtDO.getType());
            return;
        }
        switch (districtType) {
            case PROVINCE:
                idInfo.setProvince(districtDO.getName());
                break;
            case CITY:
                String city = districtDO.getName();
                idCode = idCode.substring(0, 2).concat("0000");
                districtDO = districtQueryService.getDistrictInfo(idCode);
                if (null != districtDO) {
                    idInfo.setProvince(districtDO.getName());
                }
                if ("县".equals(city) || "市".equals(city)) { // 如果库里面存的数据为“县”或“市”，则城市赋值省份一级的结果
                    idInfo.setCity(districtDO.getName());
                } else {
                    idInfo.setCity(city);
                }
                break;
            case COUNTY:
                if (!"?".equals(districtDO.getName())) {
                    idInfo.setCounty(districtDO.getName());
                }
                idCode = idCode.substring(0, 4).concat("00");
                districtDO = districtQueryService.getDistrictInfo(idCode);
                String city2 = null;
                if (null != districtDO) {
                    city2 = districtDO.getName();
                }
                idCode = idCode.substring(0, 2).concat("0000");
                districtDO = districtQueryService.getDistrictInfo(idCode);
                if (null != districtDO) {
                    idInfo.setProvince(districtDO.getName());
                }
                if ("县".equals(city2) || "市".equals(city2)) { // 如果库里面存的数据为“县”或“市”，则城市赋值省份一级的结果
                    idInfo.setCity(districtDO.getName());
                } else {
                    idInfo.setCity(city2);
                }
                break;
            default:
                break;
        }
    }

}
