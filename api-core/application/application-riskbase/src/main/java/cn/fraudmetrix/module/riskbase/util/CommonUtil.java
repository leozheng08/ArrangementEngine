package cn.fraudmetrix.module.riskbase.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 公共工具类
 *
 * @author chenchanglong 2014年6月19日 上午10:45:22
 * @edit kai.zhang 2015年4月8日 下午2:31:40
 */
public class CommonUtil {

    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    /**
     * <p>
     * 支持常用格式转换
     * </p>
     * <ul>
     * <li>yyyyMMdd</li>
     * <li>yyyy.MM.dd</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy年MM月dd日</li>
     * </ul>
     */
    public static Date parse(String dateStr) {
        Date date = null;
        date = parseDate(dateStr, "yyyyMMdd");
        if (null != date) {
            return date;
        }
        date = parseDate(dateStr, "yyyy.MM.dd");
        if (null != date) {
            return date;
        }
        date = parseDate(dateStr, "yyyy-MM-dd");
        if (null != date) {
            return date;
        }
        date = parseDate(dateStr, "yyyy/MM/dd");
        if (null != date) {
            return date;
        }
        date = parseDate(dateStr, "yyyy年MM月dd日");
        if (null != date) {
            return date;
        }
        return null;
    }


    public static Date parseDate(String value, String pattern) {
        if (StringUtils.isAnyBlank(value, pattern)) {
            return null;
        }
        try {
            return FastDateFormat.getInstance(pattern).parse(value);
        } catch (Exception e) {
            logger.warn("日期格式化错误,详情:[value:" + value + ",pattern:" + pattern + "]");
        }
        return null;
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idNumber 15位身份证号
     * @return 如果idCard不为15位则返回Null
     */
    public static String convertIdNumber(String idNumber) {
        if (StringUtils.isNumeric(idNumber) && 15 == idNumber.length()) {
            String newIdNumber = idNumber.substring(0, 6);
            newIdNumber = newIdNumber + "19" + idNumber.substring(6);
            newIdNumber = newIdNumber + getVerifyCode(newIdNumber);
            return newIdNumber;
        }
        return null;
    }

    /**
     * 获取校验码
     *
     * @param idCardNumber 不带校验位的身份证号码(17位)
     * @return 返回值为0-9或 X
     */
    private static String getVerifyCode(String idCardNumber) {
        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String[] vefifyCodes = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        char[] idCardArray = idCardNumber.toCharArray();
        int sum = 0;
        for (int i = 0; i < weight.length; i++) {
            sum += weight[i] * (idCardArray[i] - '0');
        }
        int mod = sum % 11;
        return vefifyCodes[mod];
    }


}
