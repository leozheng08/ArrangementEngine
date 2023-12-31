package cn.tongdun.kunpeng.api.basedata.rule.function.location;

import cn.fraudmetrix.elfin.biz.entity.PhoneAttrEntity;
import cn.fraudmetrix.module.riskbase.object.IdInfo;
import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.spring.SpringContextHolder;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.basedata.BasedataConstant;
import cn.tongdun.kunpeng.api.basedata.service.GeoIpServiceExtPt;
import cn.tongdun.kunpeng.api.basedata.service.IdInfoServiceExtPt;
import cn.tongdun.kunpeng.api.basedata.service.MobileInfoServiceExtPt;
import cn.tongdun.kunpeng.api.basedata.util.AddressDisplayNameUtils;
import cn.tongdun.kunpeng.api.common.Constant;
import cn.tongdun.kunpeng.api.common.data.AbstractFraudContext;
import cn.tongdun.kunpeng.api.common.data.GeoipEntity;
import cn.tongdun.kunpeng.api.ruledetail.MatchAddressDetail;
import cn.tongdun.tdframework.core.extension.ExtensionExecutor;
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

    private ExtensionExecutor extensionExecutor = SpringContextHolder.getBean("extensionExecutor", ExtensionExecutor.class);

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

        boolean ret = StringUtils.equalsIgnoreCase(addrOne, addrTwo);
        DetailCallable detailCallable = null;

        int result = ret ? 1 : 0;
        if (isMatch == result) {
            detailCallable = () -> {
                MatchAddressDetail detail = new MatchAddressDetail();
                detail.setConditionUuid(this.conditionUuid);
                detail.setRuleUuid(this.ruleUuid);
                detail.setDescription(this.description);
                detail.setAddressA(addressA);
                detail.setAddressAValue(addrOne);
                detail.setAddressADisplayName(AddressDisplayNameUtils.getAddressDisplayName(addressA));
                detail.setAddressB(addressB);
                detail.setAddressBValue(addrTwo);
                detail.setAddressBDisplayName(AddressDisplayNameUtils.getAddressDisplayName(addressB));
                return detail;
            };
            return new FunctionResult(true, detailCallable);
        }

        return new FunctionResult(false);
    }


    private String getAddress(AbstractFraudContext context, String address, String scope) {
        String lowCaseAddress = address.toLowerCase();
        String lowCaseScope = scope.toLowerCase();
        // IP地理位置 OR True IP地理位置
        if (BasedataConstant.IP_ADDRESS.equalsIgnoreCase(lowCaseAddress) || BasedataConstant.TRUE_IP_ADDRESS.equalsIgnoreCase(lowCaseAddress)) {
            GeoipEntity geoInfo;
            String trueIp;

            if (BasedataConstant.TRUE_IP_ADDRESS.equalsIgnoreCase(lowCaseAddress)) {
                trueIp = (String) context.getDeviceInfo().get("trueIp");
                if (StringUtils.isBlank(trueIp)) {
                    return null;
                }
                geoInfo = extensionExecutor.execute(GeoIpServiceExtPt.class, context.getBizScenario(), extension -> extension.getIpInfo(trueIp, context));
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

            PhoneAttrEntity mobileInfo;
            String mobileInfoStr = (String) context.get("accountMobileArea");
            if (StringUtils.isBlank(mobileInfoStr)) {
                mobileInfoStr = (String) context.get("accountMobile");
            }
            final String mobileStr = mobileInfoStr;
            mobileInfo = extensionExecutor.execute(MobileInfoServiceExtPt.class, context.getBizScenario(), extension -> extension.getMobileInfo(mobileStr, context));
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
        } else if (BasedataConstant.SN_ADDRESS.equalsIgnoreCase(lowCaseAddress)) {
            if (StringUtils.isBlank(context.getFieldToString("idNumber"))) {
                return null;
            }
            IdInfo idInfo;
            switch (lowCaseScope) {
                case "country":
                    return "中国";
                case "province":
                    String provinceCode = getDiffCode(scope, context.getFieldToString("idNumber"));
                    idInfo = extensionExecutor.execute(IdInfoServiceExtPt.class, context.getBizScenario(), extension -> extension.getIdInfo(provinceCode));
                    if (null == idInfo) {
                        return null;
                    }
                    String province = idInfo.getProvince();
                    return StringUtils.isBlank(province) ? null : province.endsWith("省") ? province : province + "省";
                default:
                    String cityCode = getDiffCode(scope, context.getFieldToString("idNumber"));
                    idInfo = extensionExecutor.execute(IdInfoServiceExtPt.class, context.getBizScenario(), extension -> extension.getIdInfo(cityCode));
                    if (null == idInfo) {
                        return null;
                    }
                    String city = idInfo.getCity();
                    return StringUtils.isBlank(city) ? null : city.endsWith("市") ? city : city + "市";
            }
        } else if (BasedataConstant.BILL_ADDRESS.equalsIgnoreCase(lowCaseAddress)) {
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
