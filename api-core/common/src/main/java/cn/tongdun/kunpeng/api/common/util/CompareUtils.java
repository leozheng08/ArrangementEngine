package cn.tongdun.kunpeng.api.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;


/**
 * @author caipeichao
 * update by huiyi.jin 增加集合类型比较 2016/08/23
 */
public class CompareUtils {

    private static Logger log = LoggerFactory.getLogger(CompareUtils.class);

    public static boolean compare(Object leftValue, Object rightValue, String dataType, String operator) {
        if (StringUtils.isBlank(dataType) || StringUtils.isBlank(operator)) {
            return false;
        }

        if ("isnull".equals(operator)) {
            return leftValue == null;
        } else if ("notnull".equals(operator)) {
            return leftValue != null;
        }

        if ("null".equals(rightValue)) {
            boolean isNull = ((null == leftValue) || StringUtils.isBlank(leftValue.toString()));
            switch (operator) {
                case "==":
                    return isNull;
                case "!=":
                    return !isNull;
                default:
                    return false;
            }
        }

        if (null == leftValue) {
            return false;
        }

        if ("isinteger".equals(operator)) {
            return isInteger(leftValue.toString());
        } else if ("notinteger".equals(operator)) {
            return !isInteger(leftValue.toString());
        }

        if (null == rightValue) {
            return false;
        }

        switch (dataType.toUpperCase()) {
            case "INT":
            case "FLOAT":
            case "DOUBLE":
            case "GAEA_INDICATRIX":
                double leftNum = parseDouble(leftValue);
                if (Double.isNaN(leftNum)) {
                    return false;
                }
                double rightNum = parseDouble(rightValue);
                if (Double.isNaN(rightNum)) {
                    return false;
                }
                switch (operator) {
                    case "==":
                        return leftNum == rightNum;
                    case "!=":
                        return leftNum != rightNum;
                    case ">":
                        return leftNum > rightNum;
                    case ">=":
                        return leftNum >= rightNum;
                    case "<":
                        return leftNum < rightNum;
                    case "<=":
                        return leftNum <= rightNum;
                    case "isdivisible":
                        return leftNum % rightNum == 0;
                    default:
                        return false;
                }
            case "STRING":
            case "ARRAY":
                switch (operator) {
                    case "==":
                        return leftValue.toString().equals(rightValue.toString());
                    case "!=":
                        return !leftValue.toString().equals(rightValue.toString());
                    case "prefix":
                        return leftValue.toString().startsWith(rightValue.toString());
                    case "suffix":
                        return leftValue.toString().endsWith(rightValue.toString());
                    case "include":
                        return leftValue.toString().contains(rightValue.toString());
                    case "exclude":
                        return !leftValue.toString().contains(rightValue.toString());
                    default:
                        return false;
                }
            case "DATETIME":
                Date leftDate = parseDate(leftValue);
                if (null == leftDate) {
                    return false;
                }
                //事件发生时间处于星期几的过滤，增加两个操作符：^(为星期几)、!^(不为星期几)
                try {
                    if (operator.equalsIgnoreCase("^")) {
                        return DateUtil.changeweek(leftDate).equalsIgnoreCase(rightValue.toString());
                    } else if (operator.equalsIgnoreCase("!^")) {
                        return !DateUtil.changeweek(leftDate).equalsIgnoreCase(rightValue.toString());
                    }
                } catch (NumberFormatException e) {
                    log.warn("事件发生星期几过滤条件配置错误");
                    return false;
                }
                Date rightDate = parseDate(rightValue);
                if (null == rightDate) {
                    return false;
                }
                switch (operator) {
                    case "==":
                        return leftDate.equals(rightDate);
                    case "!=":
                        return !leftDate.equals(rightDate);
                    case ">":
                        return leftDate.compareTo(rightDate) > 0;
                    case ">=":
                        return leftDate.compareTo(rightDate) >= 0;
                    case "<":
                        return leftDate.compareTo(rightDate) < 0;
                    case "<=":
                        return leftDate.compareTo(rightDate) <= 0;
                    default:
                        return false;
                }
            case "BOOLEAN":
                Boolean leftBool = parseBoolean(leftValue);
                if (null == leftBool) {
                    return false;
                }
                Boolean rightBool = parseBoolean(rightValue);
                if (null == rightBool) {
                    return false;
                }
                switch (operator) {
                    case "==":
                        return leftBool.equals(rightBool);
                    case "!=":
                        return !leftBool.equals(rightBool);
                    default:
                        return false;
                }
            default:
                return false;
        }
    }


    private static Boolean parseBoolean(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            return Boolean.valueOf(value.toString());
        }
    }

    private static double parseDouble(Object value) {
        if (!isNumeric(value)) {
            return Double.NaN;
        }
        return Double.parseDouble(value.toString());
    }

    private static Date parseDate(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof Long) {
            return new Date((long) value);
        } else if (StringUtils.isNumeric(value.toString())) {
            return new Date(Long.parseLong(value.toString()));
        } else {
            try {
                return DateUtil.parseDateTime(value.toString());
            } catch (ParseException e) {
                return null;
            }
        }
    }

    public static boolean isInteger(String x) {
        try {
            double d = Double.parseDouble(x);
            return isInteger(d);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNumeric(Object target) {
        return null != target && (target instanceof Number || target.toString().matches("([\\-\\+]?\\d*\\.?\\d+)|(-?\\d+\\.?\\d*)"));

    }

    public static boolean isInteger(double x) {
        return Math.floor(x) == x;
    }

}
