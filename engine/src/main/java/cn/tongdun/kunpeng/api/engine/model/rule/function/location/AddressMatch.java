package cn.tongdun.kunpeng.api.engine.model.rule.function.location;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.fraudmetrix.module.riskbase.object.BinInfoDO;
import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.fraudmetrix.module.riskbase.service.intf.BinInfoQueryService;
import cn.fraudmetrix.module.riskbase.service.intf.IdInfoQueryService;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.engine.service.ElfinBaseDataService;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.util.List;

public class AddressMatch extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(AddressMatch.class);
//[
//  {
//    "name": "addressA",
//    "type": "string",
//    "value": "ipAddress"
//  },
//  {
//    "name": "addressB",
//    "type": "string",
//    "value": "ipAddress"
//  },
//  {
//    "name": "scope",
//    "type": "string",
//    "value": "country"
//  },
//  {
//    "name": "isMatch",
//    "type": "int",
//    "value": "0"
//  }
//]


    private String addressA;
    private String addressB;
    private String scope;
    private int isMatch;


    @Override
    public String getName() {
        return Constant.Function.LOCATION_ADDRESS_MATCH;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("addressA", functionParam.getName())) {
                addressA = functionParam.getValue();
            }
            else if (StringUtils.equals("addressB", functionParam.getName())) {
                addressB = functionParam.getValue();
            }
            else if (StringUtils.equals("scope", functionParam.getName())) {
                scope = functionParam.getValue();
            }
            else if (StringUtils.equals("isMatch", functionParam.getName())) {
                isMatch = Integer.parseInt(functionParam.getValue());
            }
        });
    }

    @Override
    public CalculateResult run(ExecuteContext executeContext) {
        FraudContext context = (FraudContext) executeContext;

        String addrOne = getAddress(context, addressA, scope);
        String addrTwo = getAddress(context, addressB, scope);
        if (StringUtils.isBlank(addrOne) || StringUtils.isBlank(addrTwo)) {
            return new CalculateResult(false, null);
        }

        boolean result = false;
        if (StringUtils.equalsIgnoreCase(addrOne, addrTwo)) {
            result = true;
        }
        else {
            result = false;
        }

        return new CalculateResult(result, null);
    }

    private String getAddress(AbstractFraudContext context, String address, String scope) {
        ElfinBaseDataService elfinBaseDataService = SpringContextHolder.getBean("elfinBaseDataService", ElfinBaseDataService.class);
        IdInfoQueryService idInfoQueryService = SpringContextHolder.getBean("idInfoQueryService", IdInfoQueryService.class);
        BinInfoQueryService binInfoQueryService = SpringContextHolder.getBean("binInfoQueryService", BinInfoQueryService.class);

        if ("ipAddress".equalsIgnoreCase(address)) {// IP地理位置
            GeoipEntity geoInfo = null;             // FIXME: 2/12/20 how to get
            if (null == geoInfo) {
                return null;
            }

            if ("country".equalsIgnoreCase(scope)) {
                return geoInfo.getCountry();
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String province = geoInfo.getProvince();
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String city = geoInfo.getCity();

                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }

        }
        else if ("trueIpAddress".equalsIgnoreCase(address)) {// True IP地理位置
            String trueIp = (String) context.getDeviceInfo().get("trueIp");
            if (StringUtils.isBlank(trueIp)) {
                return null;
            }

            GeoipEntity geoipInfo = elfinBaseDataService.getIpInfo(trueIp);
            if (null == geoipInfo) {
                return null;
            }

            if ("country".equalsIgnoreCase(scope)) {
                return geoipInfo.getCountry();
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String province = geoipInfo.getProvince();
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String city = geoipInfo.getCity();
                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }
        }
        else if ("mobileAddress".equalsIgnoreCase(address)) {// 手机归属地
            MobileInfoDO mobileInfo = null;
            if (StringUtils.isBlank((String) context.get("accountMobileArea"))) {
                mobileInfo = elfinBaseDataService.getMobileInfo(context.getAccountMobile());
            }
            else {
                mobileInfo = elfinBaseDataService.getMobileInfo((String) context.get("accountMobileArea"));
            }
            if (null == mobileInfo) {
                return null;
            }
            if ("country".equalsIgnoreCase(scope)) {
                return "中国";
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String province = mobileInfo.getProvince();
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String city = mobileInfo.getCity();
                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }

        }
        else if ("snAddress".equalsIgnoreCase(address)) {// 身份证归属地
            if (StringUtils.isBlank(context.getIdNumber())) {
                return null;
            }
            IdInfo idInfo = null;
            if ("country".equalsIgnoreCase(scope)) {
                return "中国";
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String provinceCode = getDiffCode(scope, context.getIdNumber());
                idInfo = idInfoQueryService.getIdInfo(provinceCode);
                if (null == idInfo) {
                    return null;
                }
                String province = idInfo.getProvince();
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String cityCode = getDiffCode(scope, context.getIdNumber());
                idInfo = idInfoQueryService.getIdInfo(cityCode);
                if (null == idInfo) {
                    return null;
                }
                String city = idInfo.getCity();
                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }
        }
        else if ("binAddress".equalsIgnoreCase(address)) {// BIN卡发卡地
            BinInfoDO binInfo = binInfoQueryService.getBinInfo((String) context.get("ccBin"));
            if (null == binInfo) {
                return null;
            }

            if ("country".equalsIgnoreCase(scope)) {
                return binInfo.getCountry();
            }
            else {
                return null;
            }
        }
        else if ("billAddress".equalsIgnoreCase(address)) {// 账单地址
            if ("country".equalsIgnoreCase(scope)) {
                return (String) context.get("billingAddressCountry");
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String province = (String) context.get("billingAddressProvince");
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String city = (String) context.get("billingAddressCity");
                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }
        }
        else if ("deliveryAddress".equalsIgnoreCase(address)) {// 收货地址
            if ("country".equalsIgnoreCase(scope)) {
                return (String) context.get("deliverAddressCountry");
            }
            else if ("province".equalsIgnoreCase(scope)) {
                String province = (String) context.get("deliverAddressProvince");
                if (StringUtils.isBlank(province)) {
                    return null;
                }

                if (!province.endsWith("省")) {
                    province += "省";
                }
                return province;
            }
            else { // city
                String city = (String) context.get("deliverAddressCity");
                if (StringUtils.isBlank(city)) {
                    return null;
                }

                if (!city.endsWith("市")) {
                    city += "市";
                }
                return city;
            }
        }

        return null;
    }


    private String getDiffCode(String scope, String idCode) {
        Assert.notNull(idCode);
        if (StringUtils.equals(scope, "province")) {
            return idCode.substring(0, 2).concat("0000");
        }
        if (idCode.length() < 4) {
            return idCode + "00";
        }
        //如果idCode小于四位,就会出现exception
        return idCode.substring(0, 4).concat("00");
    }


}
