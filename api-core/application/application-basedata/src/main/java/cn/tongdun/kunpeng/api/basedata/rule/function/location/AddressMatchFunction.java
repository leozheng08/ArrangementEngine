package cn.tongdun.kunpeng.api.basedata.rule.function.location;

import cn.fraudmetrix.module.riskbase.geoip.GeoipEntity;
import cn.fraudmetrix.module.riskbase.object.BinInfoDO;
import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.fraudmetrix.module.riskbase.object.MobileInfoDO;
import cn.fraudmetrix.module.riskbase.service.intf.BinInfoQueryService;
import cn.fraudmetrix.module.riskbase.service.intf.IdInfoQueryService;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.basedata.service.elfin.ElfinBaseDataService;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class AddressMatchFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(AddressMatchFunction.class);

    private String addressA;
    private String addressB;
    private String scope;
    private int isMatch;

    private ElfinBaseDataService elfinBaseDataService = SpringContextHolder.getBean("elfinBaseDataService", ElfinBaseDataService.class);
    private IdInfoQueryService idInfoQueryService = SpringContextHolder.getBean("idInfoQueryService", IdInfoQueryService.class);
    private BinInfoQueryService binInfoQueryService = SpringContextHolder.getBean("binInfoQueryService", BinInfoQueryService.class);

    @Override
    public String getName() {
        return Constant.Function.LOCATION_ADDRESS_MATCH;
    }

    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("AddressMatch function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("addressA", param.getName())) {
                addressA = param.getValue();
            } else if (StringUtils.equals("addressB", param.getName())) {
                addressB = param.getValue();
            } else if (StringUtils.equals("scope", param.getName())) {
                scope = param.getValue();
            } else if (StringUtils.equals("isMatch", param.getName())) {
                isMatch = Integer.parseInt(param.getValue());
            }
        });
    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        String addrOne = getAddress(context, addressA, scope);
        String addrTwo = getAddress(context, addressB, scope);
        if (StringUtils.isBlank(addrOne) || StringUtils.isBlank(addrTwo)) {
            return new FunctionResult(false);
        }
        return new FunctionResult(!StringUtils.equalsIgnoreCase(addrOne, addrTwo));
    }


    private String getAddress(AbstractFraudContext context, String address, String scope) {
        String lowCaseAddress = address.toLowerCase();
        String lowCaseScope = scope.toLowerCase();
        // IP地理位置 OR True IP地理位置
        if (BasedataConstant.IP_ADDRESS.equals(lowCaseAddress) || BasedataConstant.TRUE_IP_ADDRESS.equals(lowCaseAddress)) {
            // FIXME: 2/13/20 hanle geoip
            GeoipEntity geoInfo;
            String trueIp;

            if (BasedataConstant.TRUE_IP_ADDRESS.equals(lowCaseAddress)) {
                trueIp = (String) context.getDeviceInfo().get("trueIp");
                if (StringUtils.isBlank(trueIp)) {
                    return null;
                }
                geoInfo = elfinBaseDataService.getIpInfo(trueIp);
            } else {
                geoInfo = context.getExternalReturnObj(BasedataConstant.EXTERNAL_OBJ_GEOIP_ENTITY, GeoipEntity.class);
            }

            if (null == geoInfo) {
                return null;
            }

            switch (lowCaseScope) {
                case "country":
                    return geoInfo.getCountry();
                case "province":
                    String province = geoInfo.getProvince();
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String city = geoInfo.getCity();
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
            }
        // 手机位置
        } else if (BasedataConstant.MOBILE_ADDRESS.equalsIgnoreCase(address)) {

            MobileInfoDO mobileInfo;
            if (StringUtils.isBlank((String) context.get("accountMobileArea"))) {
                mobileInfo = elfinBaseDataService.getMobileInfo((String) context.get("accountMobile"));
            } else {
                mobileInfo = elfinBaseDataService.getMobileInfo((String) context.get("accountMobileArea"));
            }
            if (null == mobileInfo) {
                return null;
            }

            switch (lowCaseScope) {
                case "country":
                    return "中国";
                case "province":
                    String province = mobileInfo.getProvince();
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String city = mobileInfo.getCity();
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
            }
        // 身份证归属地
        } else if (BasedataConstant.SN_ADDRESS.equals(lowCaseAddress)) {
            if (StringUtils.isBlank(context.getFieldToString("IdNumber"))) {
                return null;
            }
            IdInfo idInfo;
            switch (lowCaseScope) {
                case "country":
                    return "中国";
                case "province":
                    String provinceCode = getDiffCode(scope, context.getFieldToString("IdNumber"));
                    idInfo = idInfoQueryService.getIdInfo(provinceCode);
                    if (null == idInfo) {
                        return null;
                    }
                    String province = idInfo.getProvince();
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String cityCode = getDiffCode(scope, context.getFieldToString("IdNumber"));
                    idInfo = idInfoQueryService.getIdInfo(cityCode);
                    if (null == idInfo) {
                        return null;
                    }
                    String city = idInfo.getCity();
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
            }
        // BIN卡发卡地
        } else if (BasedataConstant.BIN_ADDRESS.equalsIgnoreCase(address)) {
            BinInfoDO binInfo = binInfoQueryService.getBinInfo((String) context.get("ccBin"));
            if (null == binInfo) {
                return null;
            }
            return null == binInfo || !"country".equals(lowCaseScope) ? null : binInfo.getCountry();
            // 账单地址
        } else if (BasedataConstant.BILL_ADDRESS.equals(lowCaseAddress)) {
            switch (lowCaseScope) {
                case "country":
                    return (String) context.get("billingAddressCountry");
                case "province":
                    String province = (String) context.get("billingAddressProvince");
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String city = (String) context.get("billingAddressCity");
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
            }
            // 收货地址
        } else if (BasedataConstant.DELIVERY_ADDRESS.equalsIgnoreCase(address)) {
            switch (lowCaseScope) {
                case "country":
                    return (String) context.get("deliverAddressCountry");
                case "province":
                    String province = (String) context.get("deliverAddressProvince");
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String city = (String) context.get("deliverAddressCity");
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
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
