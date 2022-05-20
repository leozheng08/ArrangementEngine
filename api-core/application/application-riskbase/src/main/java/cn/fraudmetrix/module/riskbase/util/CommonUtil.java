package cn.fraudmetrix.module.riskbase.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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

    public static String formatDate(Long value) {
        return formatDate(value, "yyyy-MM-dd");
    }

    public static String formatDate(Long value, String pattern) {
        if (null == value || StringUtils.isBlank(pattern)) {
            return null;
        }
        try {
            return FastDateFormat.getInstance(pattern).format(value);
        } catch (Exception e) {
            logger.warn("日期格式化错误,详情:[value:" + value + ",pattern:" + pattern + "]");
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

    /**
     * 把驼峰命名的字符转成下划线的方式，比如ipAddress->ip_address
     *
     * @param str
     */
    public static String camel2underline(String str) {
        StringBuffer sb = new StringBuffer();

        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                String s = str.substring(i, i + 1);

                // A-Z
                if (c >= 65 && c <= 90) {
                    sb.append("_").append(s.toLowerCase());
                } else {
                    sb.append(s);
                }
            }
        }

        return sb.toString();
    }

    /**
     * 把下划线命名的字符转成的驼峰方式，比如ip_address->ipAddress
     *
     * @param str
     * @return
     */
    public static String underline2camel(String str) {
        StringBuffer sb = new StringBuffer();

        if (str != null && str.length() > 0) {
            String[] arr = str.split("_");
            for (int i = 0; i < arr.length; i++) {
                String s = arr[i];
                if (s.length() > 0) {
                    // 首字母小写
                    if (i > 0) {
                        sb.append(s.substring(0, 1).toUpperCase());
                    } else {
                        sb.append(s.substring(0, 1));
                    }
                    sb.append(s.substring(1));
                }
            }

        }
        return sb.toString();
    }

    public static Long parseLong(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return parseLong(value.toString());
    }

    public static Long parseLong(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        try {
            return Long.valueOf(str);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    public static Double parseDouble(Object value) {
        if (null == value) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        try {
            return Double.valueOf(value.toString());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获得xml字符串中指定节点的内容
     *
     * @param element 输入：Element对象
     * @param eleName 输入：节点名称
     * @param list    输出：所有满足条件的节点内容
     */
    public void getURLFromXml(Element element, String eleName, List<String> list) {
        for (Iterator its = element.elementIterator(); its.hasNext(); ) {
            Element ele = (Element) its.next();
            if (ele.getName().equals(eleName)) {
                list.add(ele.getTextTrim());
            }
            if (ele.elements().size() > 0) {
                getURLFromXml(ele, eleName, list);
            }
        }
    }

    /**
     * 解析 xml字符串 -> Map<String,String>
     *
     * @param xml 输入：xml字符串
     * @return map 输出：解析结果map
     */
    public static Map<String, String> parseXml(String xml) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(xml)) {
            return map;
        }
        Document document = null;
        try {
            document = DocumentHelper.parseText(xml);
        } catch (DocumentException e) {
            logger.warn("---xml解析失败---", e.getMessage());
        }
        if (null == document) {
            return map;
        }
        Element root = document.getRootElement();
        for (Iterator its = root.elementIterator(); its.hasNext(); ) {
            Element ele = (Element) its.next();
            map.put(ele.getName(), ele.getTextTrim());
        }
        return map;
    }

    /**
     * 生成全局唯一序列号，时间戳+8位随机数
     */
    public static String generateSeqId() {
        long begin = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder();
        sb.append(begin);
        sb.append("-");

        long end = System.currentTimeMillis();
        long step1 = end - begin;

        // 使用这个会有性能问题
        // sb.append(UUID.randomUUID().toString().split("-")[0]);

        Random r = new Random();
        String ri = r.nextInt(100000000) + "";
        if (ri.length() < 8) {
            for (int i = 0; i < 8 - ri.length(); i++) {
                sb.append("9");
            }
        }
        sb.append(ri);

        long step2 = System.currentTimeMillis() - end;

        String seqId = sb.toString();
        if (step1 + step2 > 1000) {
            logger.info("idcard_seq_id:" + seqId + ",step1:" + step1 + ",step2:" + step2);
        }
        return seqId;
    }

    // public static void main(String[] args) {
    // String str = "<?xml version=\"1.0\" encoding=\"GBK\" ?>"+
    // "<response><userId>100073</userId><coopOrderNo>1428564113346</coopOrderNo>"+
    // "<auOrderNo>100061876</auOrderNo><auResultCode>FAILED</auResultCode>"+
    // "<auResultInfo>库无记录</auResultInfo><auSuccessTime>2015-04-09 15:21:54</auSuccessTime><img></img></response>";
    // System.out.println(parseXml(str));
    // }
}
