package cn.tongdun.kunpeng.api.function;

import cn.fraudmetrix.module.tdrule.context.ExecuteContext;
import cn.fraudmetrix.module.tdrule.eval.Evaluable;

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

        if(n1.getClass() == n2.getClass() && n1 instanceof Comparable && n1 instanceof Comparable){
            if(((Comparable) n1).compareTo((Comparable) n2) >0){
                return n2;
            } else {
                return n1;
            }
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return new Integer(MathUtil.intValue(n1) * MathUtil.intValue(n2));

            case 2:
                return new Long(MathUtil.longValue(n1) * MathUtil.longValue(n2));

            case 3:
                return new Double(MathUtil.doubleValue(n1) * MathUtil.doubleValue(n2));

            case 4:
                return MathUtil.toBigDecimal(n1).multiply(MathUtil.toBigDecimal(n2));

            default:
                return MathUtil.toBigDecimal(n1).multiply(MathUtil.toBigDecimal(n2));
        }
    }

    public static Number max(Number n1, Number n2) {
        if (n1 == null && n2 == null) {
            return null;
        }
        if (n1 == null || n2 == null) {
            return n1 == null ? n2 : n1;
        }

        if(n1.getClass() == n2.getClass() && n1 instanceof Comparable && n1 instanceof Comparable){
            if(((Comparable) n1).compareTo((Comparable) n2) >=0){
                return n1;
            } else {
                return n2;
            }
        }

        int type = MathUtil.getMaxNumberType(n1, n2);
        switch (type) {
            case 1:
                return new Integer(MathUtil.intValue(n1) * MathUtil.intValue(n2));

            case 2:
                return new Long(MathUtil.longValue(n1) * MathUtil.longValue(n2));

            case 3:
                return new Double(MathUtil.doubleValue(n1) * MathUtil.doubleValue(n2));

            case 4:
                return MathUtil.toBigDecimal(n1).multiply(MathUtil.toBigDecimal(n2));

            default:
                return MathUtil.toBigDecimal(n1).multiply(MathUtil.toBigDecimal(n2));
        }
    }



    public static int getMaxNumberType(Number n1, Number n2) {
        int type1 = getNumberType(n1);
        int type2 = getNumberType(n2);
        return type1 < type2 ? type2 : type1;
    }

    public static int getNumberType(Number n) {
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

    public static int intValue(Object o) {
        if (o instanceof Number) {
            return ((Number) o).intValue();
        } else {
            throw new RuntimeException("不能取整数");
        }
    }

    /**
     * object转为long数值
     * @param o
     * @return
     */
    public static long longValue(Object o) {
        if (o instanceof Number) {
            return ((Number) o).longValue();
        } else {
            throw new RuntimeException("不能取长整数");
        }
    }

    /**
     * object转为double数值
     * @param o
     * @return
     */
    public static double doubleValue(Object o) {
        if(o == null) {
            throw new RuntimeException("不能取双精度浮点数");
        } if (o instanceof Number) {
            return ((Number) o).doubleValue();
        } else {
            try {
                return Double.parseDouble(o.toString());
            } catch (Exception e) {
                throw new RuntimeException("不能取双精度浮点数");
            }
        }

    }

    /**
     * object转为BigDecimal数值
     * @param o
     * @return
     */
    public static BigDecimal toBigDecimal(Object o) {
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } if (o instanceof BigInteger) {
            return new BigDecimal((BigInteger) o);
        } else {
            return new BigDecimal(o.toString());
        }
    }
}
