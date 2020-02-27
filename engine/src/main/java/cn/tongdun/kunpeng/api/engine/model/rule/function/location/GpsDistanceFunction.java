package cn.tongdun.kunpeng.api.engine.model.rule.function.location;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.exception.ParseException;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.FunctionDesc;
import cn.fraudmetrix.module.tdrule.function.FunctionResult;
import cn.fraudmetrix.module.tdrule.util.DetailCallable;
import cn.tongdun.kunpeng.api.ruledetail.GpsDistanceDetail;
import cn.tongdun.kunpeng.common.Constant;
import cn.tongdun.kunpeng.common.data.AbstractFraudContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GpsDistanceFunction extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(GpsDistanceFunction.class);

    private String gpsA;
    private String gpsB;
    private String distanceSlice;

    @Override
    public String getName() {
        return Constant.Function.LOCATION_GPS_DISTANCE;
    }


    @Override
    public void parseFunction(FunctionDesc functionDesc) {
        if (null == functionDesc || CollectionUtils.isEmpty(functionDesc.getParamList())) {
            throw new ParseException("GpsDistance function parse error,no params!");
        }

        functionDesc.getParamList().forEach(param -> {
            if (StringUtils.equals("gpsA", param.getName())) {
                gpsA = param.getValue();
            } else if (StringUtils.equals("gpsB", param.getName())) {
                gpsB = param.getValue();
            } else if (StringUtils.equals("distanceSlice", param.getName())) {
                distanceSlice = param.getValue();
            }
        });

    }

    @Override
    public FunctionResult run(ExecuteContext executeContext) {
        AbstractFraudContext context = (AbstractFraudContext) executeContext;

        double result = Double.NaN;
        GpsEntity gps1 = getGpsEntity(context.getField(gpsA));
        GpsEntity gps2 = getGpsEntity(context.getField(gpsB));
        if (null == gps1 || null == gps2) {
            return new FunctionResult(result);
        }

        int difference = 0;
        if (null != distanceSlice) {
            try {
                difference = Integer.parseInt(distanceSlice);
            } catch (NumberFormatException e) {
                logger.error("distanceSlice {} 格式错误", distanceSlice, e);
            }
        }

        result = getDistance(gps1, gps2, false);
        double ret = result;
        DetailCallable detailCallable = () -> {
            GpsDistanceDetail detail = null;
            detail = new GpsDistanceDetail();
            detail.setConditionUuid(conditionUuid);
            detail.setRuleUuid(ruleUuid);
            detail.setDescription(description);
            detail.setGpsA(gpsA);
            detail.setGpsB(gpsB);
            detail.setResult(ret);
            detail.setUnit("m");
            return detail;
        };
        return new FunctionResult(result, detailCallable);
    }


    private GpsEntity getGpsEntity(Object obj) {
        GpsEntity gps = null;
        try {
            String gpsAddress = (String) obj;
            if (StringUtils.isBlank(gpsAddress)) {
                return null;
            }

            String[] latitudeNLongitude = gpsAddress.split("/");
            gps = getGpsEntity(latitudeNLongitude);

            if (null != gps) {
                return gps;
            }

            latitudeNLongitude = gpsAddress.split(",");
            return getGpsEntity(latitudeNLongitude);
        } catch (ClassCastException e) {
            logger.error("getGpsEntity class cast error {}", obj, e);
            return null;
        }
    }

    private GpsEntity getGpsEntity(String[] latitudeNLongitude) {
        if (null == latitudeNLongitude || latitudeNLongitude.length != 2) {
            return null;
        }

        GpsEntity gps = null;
        try {
            double latitude = Double.parseDouble(latitudeNLongitude[0].trim());
            double longitude = Double.parseDouble(latitudeNLongitude[1].trim());
            gps = new GpsEntity(longitude, latitude);
        } catch (Exception e) {
            logger.error("经纬度格式转换错误, lat-lon {}", latitudeNLongitude, e);
        }
        return gps;
    }

    public static double getDistance(GpsEntity gps1, GpsEntity gps2, boolean kilometre) {
        return getDistance(gps1.getLongitude(), gps1.getLatitude(), gps2.getLongitude(), gps2.getLatitude(), kilometre);
    }

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离
     */
    public static double getDistance(double lng1, double lat1, double lng2, double lat2, boolean kilometre) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * Constant.EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        if (kilometre) {
            s = s / 1000;
        }
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }


}
