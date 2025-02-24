/*
 * Copyright [2025] [Harbortek Corp.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harbortek.helm.util;

import java.text.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

public class DateUtils {
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
	public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String SHORT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String ALIGN_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DEFAULT_SQUOTE_DATE_FORMAT = "yyyy/MM/dd";
	public static final String DEFAULT_NOSQUOTE_DATE_FORMAT = "yyyyMMdd";
	public static final String DEFAULT_MONTH_DAY = "MM.dd";

	private static final double[] LIMITS = { 0, 1, 2 };

	private static final String[] MINUTES_PART = { "", "1 minute ", "{0,number} minutes " };

	private static final String[] SECONDS_PART = { "0 seconds", "1 second", "{1,number} seconds" };

	private static final ChoiceFormat MINUTES_FORMAT = new ChoiceFormat(LIMITS, MINUTES_PART);

	private static final ChoiceFormat SECONDS_FORMAT = new ChoiceFormat(LIMITS, SECONDS_PART);

	private static final MessageFormat MINUTE_SECONDS = new MessageFormat("{0}{1}");

	static {
		MINUTE_SECONDS.setFormat(0, MINUTES_FORMAT);
		MINUTE_SECONDS.setFormat(1, SECONDS_FORMAT);
	}

	public static final long ONE_SECOND = 1000;
	public static final long ONE_MINUTE = 60 * ONE_SECOND;
	public static final long ONE_HOUR = 60 * ONE_MINUTE;
	public static final long ONE_DAY = 24 * ONE_HOUR;
	public static final long ONE_WEEK = 7 * ONE_DAY;

	public static final SimpleDateFormat _defDateTimeFmt = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);

	public static final SimpleDateFormat _defDateFmt = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	public static String toString(Date date, String format) {

		SimpleDateFormat formatter;

		if ((date == null) || (format == null) || (format.length() == 0)) {
			return null;
		}
		formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}

	public static Date toDate(String str, String format) {
		if ((str == null) || (str.length() == 0) || (format == null) || (format.length() == 0)) {
			return null;
		}

		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setLenient(false);
		ParsePosition pos = new ParsePosition(0);
		return formatter.parse(str, pos);
	}

	public static boolean compare(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return true;
		}
		if (date1 == null || date2 == null)
			return false;
		else
			return date1.getTime() == date2.getTime();
	}

	public static Date toDate(String str) {
		try {
			if (str.indexOf(':') > 0) {
				return toDate(str, DEFAULT_DATETIME_FORMAT);
			} else {
				return toDate(str, DEFAULT_DATE_FORMAT);
			}
		} catch (Exception ex) {
			return null;
		}
	}

	public static String timeStampToString(long timeStamp, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(timeStamp);
	}

	public static String currentDateToString(String format) {
		Date date = new Date();
		return toString(date, format);
	}

	public static String curDateStr() {
		return _defDateFmt.format(new Date());
	}

	public static String curDateTimeStr() {
		return _defDateTimeFmt.format(new Date());
	}

	public static String formatElapsedTime(long millis) {
		long seconds = millis / 1000;
		long minutes = seconds / 60;
		Object[] args = { new Long(minutes), new Long(seconds % 60) };
		return MINUTE_SECONDS.format(args);
	}

	public static String toDefDateString(Date date) {
		return toString(date, DEFAULT_DATE_FORMAT);
	}

	public static String toDefDateString(Date date, String format) {
		return toString(date, format);
	}

	public static String toDefDatetimeString(Date date) {
		return toString(date, DEFAULT_DATETIME_FORMAT);
	}

	public static String toDefTimeString(Date date) {
		return toString(date, DEFAULT_TIME_FORMAT);
	}

	public static String convertSecondsToTime(Long seconds) {
		long s;// 秒
		long h;// 小时
		long m;// 分钟
		if (0 == seconds) {
			return "00:00:00";
		}
		seconds = seconds * 1000;
		h = seconds / 1000 / 60 / 60;
		m = (seconds - h * 60 * 60 * 1000) / 1000 / 60;
		s = seconds / 1000 - h * 60 * 60 - m * 60;
		return (h < 10 ? ("0" + h) : h) + ":" + (m < 10 ? ("0" + m) : m) + ":" + (s < 10 ? ("0" + s) : s);
	}

	public static Date getTodayStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static Date getYearStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.set(Calendar.DAY_OF_YEAR, 0);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static String getSmartDateString(Date date, String formatString) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		SimpleDateFormat sdf1 = new SimpleDateFormat(formatString);
		Long dayLong = 24 * 3600 * 1000l;
		Long todayStart = DateUtils.getTodayStartTime().getTime();
		Long last = todayStart - date.getTime();
		String time = sdf1.format(date);
		if (last <= 0) {
			return "今天" + time;
		} else if (last / dayLong >= 1) {
			return sdf.format(date) + " " + time;
		} else if (last / dayLong == 0) {
			return "昨天" + time;
		} else {
			return "";
		}
	}

	public static String getSmartDateString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		Long dayLong = 24 * 3600 * 1000l;
		Long todayStart = DateUtils.getTodayStartTime().getTime();
		Long last = todayStart - date.getTime();
		if (last <= 0) {
			Long time = new Date().getTime() - date.getTime();
			int s = (int) (time / 1000);
			if (s >= 3600) {
				return s / 3600 + "小时前";
			} else if (s >= 60) {
				return s / 60 + "分钟前";
			} else {
				return "刚刚";
			}
		} else if (last / dayLong > 10) {
			return sdf.format(date);
		} else if (last / dayLong >= 1) {
			return last / dayLong + 1 + "天前";
		} else if (last / dayLong == 0) {
			return "昨天";
		} else {
			return "";
		}
	}

	public static int getDays(Date startTime, Date endTime) {
		Long time = endTime.getTime() - startTime.getTime();
		int days = (int) (time / (3600000 * 24));
		if (endTime.getTime() == toDate(toDefDateString(endTime)).getTime()) {
			return days;
		}
		return days + 1;
	}

	public static String getSmartLeftTime(Long leftTime) {
		if (leftTime > ONE_DAY) {
			return leftTime / ONE_DAY + "天以上";
		} else if (leftTime >= ONE_HOUR) {
			return leftTime / ONE_HOUR + "小时以上";
		} else if (leftTime >= ONE_MINUTE) {
			return leftTime / ONE_HOUR + 1 + "分钟";
		} else if (leftTime > 0) {
			return "一分钟";
		} else {
			return "";
		}
	}

	/**
	 * 距离目标日期相应天数的日期
	 *
	 * @param date 目标日期
	 * @param to   距离天数
	 * @return
	 */
	public static String getDateToToday(String date, int to) {
		String str = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date myDate = formatter.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(myDate);
			c.add(Calendar.DATE, to);
			myDate = c.getTime();
			str = formatter.format(myDate);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return str;
	}

	public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

	public static final String YMD = "yyyy-MM-dd";

	public static final long ONE_DAY_MillIS = 24 * 60 * 60 * 1000;

	private static final ConcurrentMap<Pattern, String> regPatternMap = new ConcurrentHashMap<Pattern, String>();

	static {
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$"), "yyyy-MM-dd");
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$"),
				"yyyy-MM-dd HH:mm:ss");
		regPatternMap.put(Pattern.compile("^\\d{4}\\d{1,2}\\d{1,2}$"), "yyyyMMdd");
		regPatternMap.put(Pattern.compile("^\\d{4}\\d{1,2}$"), "yyyyMM");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$"), "yyyy/MM/dd");
		regPatternMap.put(Pattern.compile("^\\d{4}年\\d{1,2}月\\d{1,2}日$"), "yyyy年MM月dd日");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$"),
				"yyyy/MM/dd HH:mm:ss");
		regPatternMap.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"),
				"yyyy/MM/dd HH:mm:ss.S");
		regPatternMap.put(Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"),
				"yyyy-MM-dd HH:mm:ss.S");
	}

	public static Date convert(Long timestamp) {
		return new Date(timestamp);
	}

	/**
	 * 方法描述：获取当前时间的
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * 返回当前时间 格式：yyyy-MM-dd hh:mm:ss
	 *
	 * @return String
	 */
	public static String GetCurrTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.format(date);
		return sdf.format(date);
	}

	/**
	 * 返回当前时间 格式：yyyy-MM-dd
	 *
	 * @return String
	 */
	public static String getCurrDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.format(date);
		return sdf.format(date);
	}

	public static Long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static Date addMonth(Date date, int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, interval);
		return calendar.getTime();
	}

	public static Date addDay(Date date, int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, interval);
		return calendar.getTime();
	}

	public static Date addHour(Date date, int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR_OF_DAY, interval);
		return calendar.getTime();
	}

	public static Date addMinute(Date date, int interval) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, interval);
		return calendar.getTime();
	}

	/**
	 * 返回2个日期之间的天数组成的list
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> list2daysBetween(String startTime, String endTime) {
		List<String> list = new ArrayList<String>();
		int days = daysBetween(DateUtils.toDate(startTime), DateUtils.toDate(endTime)) + 1;
		for (int i = 0; i < days; i++) {
			Long L_time = DateUtils.toDate(startTime).getTime() + new Long(i) * 24 * 60 * 60 * 1000;
			String time = DateUtils.toDefDateString(new Date(L_time));
			list.add(time);
		}
		return list;
	}

	/**
	 * 返回2个日期之间的天数组成的list
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> list2daysBetween(String startTime, String endTime, String format) {
		List<String> list = new ArrayList<String>();
		int days = daysBetween(DateUtils.toDate(startTime), DateUtils.toDate(endTime)) + 1;
		for (int i = 0; i < days; i++) {
			Long L_time = DateUtils.toDate(startTime).getTime() + new Long(i) * 24 * 60 * 60 * 1000;
			String time = DateUtils.toDefDateString(new Date(L_time), format);
			list.add(time);
		}
		return list;
	}

	/**
	 * 计算2个日期之间相差的天数
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetween(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		long time1 = cal.getTimeInMillis();
		cal.setTime(date2);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 获取一个月之前的日期
	 *
	 * @param fortmat
	 * @return
	 */
	public static String getDayOfLastMonth(String fortmat) {
		SimpleDateFormat sdf = new SimpleDateFormat(fortmat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// 取得上一个时间
		calendar.set(Calendar.MONDAY, calendar.get(Calendar.MONDAY) - 1);
		return sdf.format(calendar.getTime());
	}

	/**
	 * 去掉时分秒，只保留日期
	 *
	 * @param date
	 * @return
	 */
	public static Date extractDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s = sdf.format(date);
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Date strToDate(String dateStr) {
		DateFormat format = new SimpleDateFormat("MM-dd");
		try {
			return format.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得该月第一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最小天数
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最小天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String firstDayOfMonth = sdf.format(cal.getTime());
		return firstDayOfMonth;
	}

	/**
	 * 获得该月最后一天
	 *
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		// 设置年份
		cal.set(Calendar.YEAR, year);
		// 设置月份
		cal.set(Calendar.MONTH, month - 1);
		// 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		// 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	// 比较两个时间大小
	public static int compareDate(Date dt1, Date dt2) {
		if (dt1.getTime() > dt2.getTime()) {
			return 1;
		} else if (dt1.getTime() < dt2.getTime()) {
			return -1;
		} else {// 相等
			return 0;
		}
	}

	/**
	 * 这里共有2个时间段（b1-----e1）【b2-----e2】，4个时间点； 相当于两条线段(b代表起点，e代表端点，b<=e)，4个端点。
	 * 可分3种情况： 1.不相交。（b1-----e1）【b2-----e2】（b1-----e1）。if(e1<b2||b1>e2)此时，重合天数为零。
	 * 2.相交。 情况一：（b1---【b2---e1）----e2】 if(b1<b2&&e1<e2&&e1>b2)
	 * 情况二：【b2---(b1---e2】----e1) if(b1>b2&&b1<e2&&e2<e1) 3.包含：计算较短的时间段日期长度。
	 * （b1---【b2-----e2】--e1） if(b1<b2&&e1>e2) 【b2---（b1-----e1）--e2】
	 * if(b1>b2&&e1<e2)
	 *
	 * @param begindate1 开始日期
	 * @param enddate1   结束日期
	 * @param begindate2 开始日期
	 * @param enddate2   结束日期
	 * @return
	 */
	public static Integer getDayCoincidence(Date begindate1, Date enddate1, Date begindate2, Date enddate2) {
		long b1 = begindate1.getTime();
		long e1 = enddate1.getTime();
		long b2 = begindate2.getTime();
		long e2 = enddate2.getTime();
		assert (b1 < e1 && b2 < e2);
		Integer coincidenceday;
		if (b1 <= b2 && e1 >= e2) {// （b1---【b2-----e2】--e1）
			coincidenceday = daysBetween(begindate2, enddate2);
		} else if (b1 >= b2 && e1 <= e2) {// 【b2---（b1-----e1）--e2】
			coincidenceday = daysBetween(begindate1, enddate1);
		} else if (b1 >= b2 && b1 <= e2 && e2 <= e1) {// 【b2---(b1---e2】----e1)
			coincidenceday = daysBetween(begindate1, enddate2);
		} else if (b1 <= b2 && e1 <= e2 && e1 >= b2) {// （b1---【b2---e1）----e2】
			coincidenceday = daysBetween(begindate2, enddate1);
		} else if (e1 <= b2 || b1 >= e2) {
			coincidenceday = 0;
		} else {
			coincidenceday = null;
			System.out.println("意料外的日期组合，无法计算重合天数！");
		}
		return coincidenceday;
	}

	// 获得本周一与当前日期相差的天数
	public static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	// 获得当前周- 第一天的日期
	public static String getCurrentMonday(String date) {
		LocalDate inputDate = LocalDate.parse(date);
		TemporalAdjuster FIRST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
				localDate -> localDate.minusDays(localDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
		return inputDate.with(FIRST_OF_WEEK).format(formatters);
	}

	// 获得当前周- 周日 的日期
	public static String getPreviousSunday(String date) {
		LocalDate inputDate = LocalDate.parse(date);
		TemporalAdjuster LAST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
				localDate -> localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT);
		return inputDate.with(LAST_OF_WEEK).format(formatters);
	}

	/**
	 * 转换为时间（天,时:分:秒.毫秒）
	 *
	 * @param timeMillis
	 * @return
	 */
	public static String formatDateTime(long timeMillis) {
		long day = timeMillis / (24 * 60 * 60 * 1000);
		long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
		long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
		return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
	}

	public static Date parseISO8601Date(String utcString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  //获取时区
		Date isoStringToDate = null;
		try {
			isoStringToDate = sdf.parse(utcString);
		} catch (ParseException e) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			try {
				isoStringToDate = formatter.parse(utcString);
			} catch (ParseException ignored) {
			}
		}


		return isoStringToDate;
    }

	public static String toISODate(Date value) {
		if (value==null){
			value = DateUtils.now();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  //获取时区
        return sdf.format(value);
	}
}
