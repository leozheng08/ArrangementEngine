package cn.tongdun.kunpeng.api.core.rule.function.arithmetic;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @Author: liang.chen
 * @Date: 2019/12/19 上午10:15
 */
public class MathUtil {

    public final static Number NaN = Double.NaN;


    public static Number min(Number n1, Number n2) {
        if (n1 == null && n2 == null) {
            return null;
        }
        if (n1 == null || n2 == null) {
            return n1 == null ? n2 : n1;
        }

        if(n1.getClass() == n2.getClass() && n1 instanceof Comparable && n2 instanceof Comparable){
            return ((Comparable) n1).compareTo((Comparable) n2) <= 0 ? n1 : n2;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() <= n2.intValue()? n1 : n2;
            case 2:
                return n1.longValue() <= n2.longValue()? n1 : n2;
            case 3:
                return n1.intValue() <= n2.intValue()? n1 : n2;
            default:
                BigDecimal b1 = toBigDecimal(n1);
                BigDecimal b2 = toBigDecimal(n2);
                return b1.compareTo(b2) <=0 ? n1 : n2;
        }
    }

    public static Number max(Number n1, Number n2) {
        if (n1 == null && n2 == null) {
            return null;
        }
        if (n1 == null || n2 == null) {
            return n1 == null ? n2 : n1;
        }

        if(n1.getClass() == n2.getClass() && n1 instanceof Comparable && n2 instanceof Comparable){
            return ((Comparable) n1).compareTo((Comparable) n2) >= 0 ? n1 : n2;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() >= n2.intValue()? n1 : n2;
            case 2:
                return n1.longValue() >= n2.longValue()? n1 : n2;
            case 3:
                return n1.intValue() >= n2.intValue()? n1 : n2;
            default:
                BigDecimal b1 = toBigDecimal(n1);
                BigDecimal b2 = toBigDecimal(n2);
                return b1.compareTo(b2) >=0 ? n1 : n2;
        }
    }


    /**
     * 乘法运算
     *
     * @param n1
     * @param n2
     * @return
     */
    public static Number multiply(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return null;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() * n2.intValue();

            case 2:
                return n1.longValue() * n1.longValue();

            case 3:
                return n1.doubleValue() * n2.doubleValue();

            default:
                return MathUtil.toBigDecimal(n1).multiply(MathUtil.toBigDecimal(n2));
        }
    }

    /**
     * 除法运算
     *
     * @param n1
     * @param n2
     * @return
     */
    public static Number divide(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return null;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() * n2.intValue();

            case 2:
                return n1.longValue() * n1.longValue();

            case 3:
                return n1.doubleValue() * n2.doubleValue();

            default:
                return MathUtil.toBigDecimal(n1).divide(MathUtil.toBigDecimal(n2));
        }
    }

    /**
     * 加法运算
     *
     * @param n1
     * @param n2
     * @return
     */
    public static Number addition(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return null;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() + n2.intValue();

            case 2:
                return n1.longValue() + n1.longValue();

            case 3:
                return n1.doubleValue() + n2.doubleValue();

            default:
                return MathUtil.toBigDecimal(n1).add(MathUtil.toBigDecimal(n2));
        }
    }

    /**
     * 减法运算
     *
     * @param n1
     * @param n2
     * @return
     */
    public static Number subtract(Number n1, Number n2) {
        if (n1 == null || n2 == null) {
            return null;
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return n1.intValue() - n2.intValue();

            case 2:
                return n1.longValue() - n1.longValue();

            case 3:
                return n1.doubleValue() - n2.doubleValue();

            default:
                return MathUtil.toBigDecimal(n1).subtract(MathUtil.toBigDecimal(n2));
        }
    }


    private static int getMaxNumberType(Number n1, Number n2) {
        int type1 = getNumberType(n1);
        int type2 = getNumberType(n2);
        return type1 < type2 ? type2 : type1;
    }

    private static int getNumberType(Number n) {
        if (n instanceof Byte) {
            return 1;
        }
        if (n instanceof Short) {
            return 1;
        }
        if (n instanceof Integer) {
            return 1;
        }
        if (n instanceof Long) {
            return 2;
        }
        if (n instanceof Float) {
            return 3;
        }
        if (n instanceof Double) {
            return 3;
        }
        if (n instanceof BigInteger) {
            return 4;
        }
        if (n instanceof BigDecimal) {
            return 4;
        }

        return 4;
    }


    /**
     * object转为BigDecimal数值
     * @param o
     * @return
     */
    private static BigDecimal toBigDecimal(Object o) {
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } if (o instanceof BigInteger) {
            return new BigDecimal((BigInteger) o);
        } else {
            return new BigDecimal(o.toString());
        }
    }
}
