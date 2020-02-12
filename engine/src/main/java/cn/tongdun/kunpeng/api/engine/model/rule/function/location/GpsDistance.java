package cn.tongdun.kunpeng.api.engine.model.rule.function.location;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.function.AbstractFunction;
import cn.fraudmetrix.module.tdrule.function.CalculateResult;
import cn.fraudmetrix.module.tdrule.model.FunctionParam;
import cn.tongdun.kunpeng.common.Constant;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GpsDistance extends AbstractFunction {
    private static final Logger logger = LoggerFactory.getLogger(GpsDistance.class);

//[
//  {
//    "name": "gpsA",
//    "type": "string",
//    "value": "startingPointGpsAddress"
//  },
//  {
//    "name": "gpsB",
//    "type": "string",
//    "value": "endingPointGpsAddress"
//  },
//  {
//    "name": "distanceOperator",
//    "type": "string",
//    "value": "<"
//  },
//  {
//    "name": "distanceSlice",
//    "type": "string",
//    "value": "5"
//  }
//]


    private String gpsA;
    private String gpsB;
    private String distanceOperator;
    private String distanceSlice;


    @Override
    public String getName() {
        return Constant.Function.LOCATION_GPS_DISTANCE;
    }

    @Override
    public void parse(List<FunctionParam> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(functionParam -> {
            if (StringUtils.equals("gpsA", functionParam.getName())) {
                gpsA = functionParam.getValue();
            }
            else if (StringUtils.equals("gpsB", functionParam.getName())) {
                gpsB = functionParam.getValue();
            }
            else if (StringUtils.equals("distanceOperator", functionParam.getName())) {
                distanceOperator = functionParam.getValue();
            }
            else if (StringUtils.equals("distanceSlice", functionParam.getName())) {
                distanceSlice = functionParam.getValue();
            }
        });

    }

    @Override
    public CalculateResult run(ExecuteContext context) {
        GpsEntity gps1 = getGpsEntity(context.getField(gpsA));
        GpsEntity gps2 = getGpsEntity(context.getField(gpsB));

        if (null == gps1 || null == gps2 || null == distanceOperator) {
            return new CalculateResult(Double.NaN, null);
        }

        int difference = 0;
        if (null != distanceSlice) {
            try {
                difference = Integer.parseInt(distanceSlice);
            }
            catch (NumberFormatException e) {
                logger.error("distanceSlice格式错误 value {}", distanceSlice, e);
            }
        }

        double distance = getDistance(gps1, gps2, false);



        List<Double> result = Lists.newArrayList(distanceDiffResult(distanceOperator, difference, distance));

        return null;

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
        }
        catch (ClassCastException e) {
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
        }
        catch (Exception e) {
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

    private double distanceDiffResult(String distanceOperator, int difference, double distance) {
        if ("none".equalsIgnoreCase(distanceOperator)) {// 为指标数据返回原始计算值
            return distance;
        } else if (distanceOperator.equals(">")) {
            return (distance > difference ? 1 : 0);
        } else if (distanceOperator.equals(">=")) {
            return (distance >= difference ? 1 : 0);
        } else if (distanceOperator.equals("<")) {
            return (distance < difference ? 1 : 0);
        } else if (distanceOperator.equals("<=")) {
            return (distance <= difference ? 1 : 0);
        } else if (distanceOperator.equals("==")) {
            return (distance == difference ? 1 : 0);
        }
        return 0;
    }


}
