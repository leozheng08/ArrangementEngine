/*
 * Copyright 2014 FraudMetrix.cn All right reserved. This software is the
 * confidential and proprietary information of FraudMetrix.cn ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with FraudMetrix.cn.
 */
package cn.tongdun.kunpeng.common.util;

import cn.tongdun.kunpeng.common.data.WeekEnum;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 类DateUtils.java的实现描述：时间工具类
 * 
 * @author toruneko 2014年12月31日 上午10:36:42
 */
public class DateUtil {

	private static final Logger logger = LoggerFactory
			.getLogger(DateUtil.class);

	private static final FastDateFormat dateFormat = createDateFormat("yyyy-MM-dd");
	private static final FastDateFormat datetimeFormat = createDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final FastDateFormat yyyyMMddHHmmssSSS = createDateFormat("yyyyMMddHHmmssSSS");
    private static final FastDateFormat yyyyMMdd = createDateFormat("yyyyMMdd");

	/**
	 * 返回自定义时间格式化工具
	 * 
	 * @param pattern
	 * @return
	 */
	public static FastDateFormat createDateFormat(String pattern) {
		return FastDateFormat.getInstance(pattern);
	}

	/**
	 * 解析日期字符串为Date对象
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(final String source) throws ParseException {
		return dateFormat.parse(source);
	}

	/**
	 * 解析时间日期字符串为Date对象
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDateTime(final String source) throws ParseException {
		return datetimeFormat.parse(source);
	}

	/**
	 * 将时间戳转换为Date对象
	 * 
	 * @param timestamp
	 * @return
	 */
	public static Date parse(final long timestamp) {
		return new Date(timestamp);
	}

	public static Long parseLong(Object obj) {
		if (obj instanceof Long) {
			return (long) obj;
		}
		if (obj instanceof Date) {
			return ((Date) obj).getTime();
		}
		if (null == obj) {
			return null;
		}
		String dateStr = obj.toString();
		try {
			if (dateStr.contains("-")) {
				return parseDateTime(dateStr).getTime();
			} else {
				return Long.parseLong(dateStr);
			}
		} catch (Exception e) {
            LogUtil.logError(logger, "时间转时间戳异常", null, e.getMessage());
			return null;
		}
	}

	/**
	 * 格式化Date对象为日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(final Date date) {
		return dateFormat.format(date);
	}

	/**
	 * 将时间戳转换为日期字符串
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String formatDate(final long timestamp) {
		return dateFormat.format(timestamp);
	}

    /**
     * 格式化Date对象为日期字符串
     * 
     * @param date
     * @return
     */
    public static String formatShortDate(final Date date) {
        return yyyyMMdd.format(date);
    }

	/**
	 * 格式化时间日期字符串为日期字符串
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static String formatDate(final String source) throws ParseException {
		return formatDate(parseDateTime(source));
	}

	/**
	 * 格式化Date对象为时间日期字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateTime(final Date date) {
		return datetimeFormat.format(date);
	}

	/**
	 * 将时间戳转换为时间日期字符串
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String formatDateTime(final long timestamp) {
		return datetimeFormat.format(timestamp);
	}

	/**
	 * 格式化日期字符串为时间日期字符串
	 * 
	 * @param source
	 * @return
	 * @throws ParseException
	 */
	public static String formatDateTime(final String source)
			throws ParseException {
		return formatDateTime(parseDate(source));
	}

	/**
	 * 比较两个字符串时间大小，arg1 compareTo arg2
	 * 
	 * @param arg1
	 * @param arg2
	 * @return arg1>arg1 return 1; arg1==arg2 return 0; arg1<arg2 return -1
	 * @throws ParseException
	 */
	public static int compare(final String arg1, final String arg2)
			throws ParseException {
		try {
			return parseDateTime(arg1).compareTo(parseDateTime(arg2));
		} catch (ParseException e) {
			return parseDate(arg1).compareTo(parseDate(arg2));
		}
	}

	/**
	 * 得到当前时间
	 */
	public static String getTimeStampString() {
		return yyyyMMddHHmmssSSS.format(System.currentTimeMillis());
	}

	/***
	 * 将String格式转成Timestamp类型
	 * 
	 * @param dateStr
	 * @return
	 */
	public static Timestamp formatTimestampString(String dateStr) {
		Calendar c = Calendar.getInstance();
		// c.set(2099, 11, 20, 15, 30, 40);
		long time = 4133865600000L;
		c.setTimeInMillis(time);
		if (dateStr == null) {
			return new Timestamp(c.getTimeInMillis());
		}
		try {
			return new Timestamp(DateUtil.parseDate(dateStr).getTime());
		} catch (Exception e) {
			return new Timestamp(c.getTimeInMillis());
		}
	}

	/**
	 * 去掉时间中的小时、分钟、秒、毫秒。
	 */
	public static long floorDay(long day) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(day);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime().getTime();
	}

	/**
	 * 通过时间获取星期几
	 * @param sourceDate
	 * @return
	 */
	public static String changeweek(Date sourceDate) {
		String week = "";
		try {
			week = new SimpleDateFormat("EEEE").format(sourceDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (WeekEnum.containKey(week)){ //如果是英文星期的则转成中文。
			week = WeekEnum.getWeekZh(week);
		}
		return week;

	}

	private static final ThreadLocal<Calendar> calendarLocal = new ThreadLocal<Calendar>() {
		@Override
		protected Calendar initialValue() {
			return Calendar.getInstance();
		};
	};

	public static String getLastMinute() {
		Calendar cal = getSysCalendar();
		cal.set(Calendar.MINUTE,cal.get(Calendar.MINUTE)-1);
		return getCalStr(cal);
	}

	public static final String getYYYYMMDDHHMMStr() {
		Calendar cal = getSysCalendar();
		return getCalStr(cal);
	}
	public static final Calendar getSysCalendar() {
		Calendar cal = calendarLocal.get();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
	}
	public static final long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static final String getCalStr(Calendar cal) {
		return String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf((cal.get(Calendar.MONTH) + 1)) + String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) +
				String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + String.valueOf(cal.get(Calendar.MINUTE));
	}
}
