package com.fubon.robot.batch.thread.post;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

/**
 * Java实现的日期处理工具类（DateUtils），主要实现了： <br>
 * 
 * 1、给指定的日期加上(减去)月份； <br>
 * 2、给制定的时间加上(减去)天； <br>
 * 3、获取系统当前时间； <br>
 * 4、获取系统当前时间(指定返回类型)； <br>
 * 5、使用预设格式提取字符串日期； <br>
 * 6、指定指定日期字符串； <br>
 * 7、两个时间比较； <br>
 * 8、两个时间比较(时间戳比较)； <br>
 * 9、将指定的日期转换成Unix时间戳； <br>
 * 10、将指定的日期转换成Unix时间戳； <br>
 * 11、将当前日期转换成Unix时间戳； <br>
 * 12、将Unix时间戳转换成日期； <br>
 * 13、日期相减 <br>
 * 14、日期相减(字串) <br>
 * 15、取得系統日期近一年年月(內容格式: "2014-01") <br>
 * 
 * @date 2013-12-18 11:22
 */
public class DateUtils {

	/** 定义常量* */
	public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_LONG_STR = "yyyy-MM-dd kk:mm:ss.SSS";
	public static final String DATE_SMALL_STR = "yyyy-MM-dd";
	public static final String DATE_KEY_STR = "yyMMddHHmmss";
	public static final String DATE_All_KEY_STR = "yyyyMMddHHmmss";
	public static final String DATE_WORKDAY_STR = "yyyyMMdd";
	private static final Logger log = Logger.getLogger(DateUtils.class);

	/**
	 * 给指定的日期加上(减去)月份
	 * 
	 * @param date
	 * @param pattern
	 * @param num
	 * @return
	 */
	public static String addMoth(Date date, String pattern, int num) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.MONTH, num);
		return simpleDateFormat.format(calender.getTime());
	}

	/**
	 * 给制定的时间加上(减去)天
	 * 
	 * @param date
	 * @param pattern
	 * @param num
	 * @return
	 */
	public static String addDay(Date date, String pattern, int num) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Calendar calender = Calendar.getInstance();
		calender.setTime(date);
		calender.add(Calendar.DATE, num);
		return simpleDateFormat.format(calender.getTime());
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getNowTime() {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FULL_STR);
		return df.format(new Date());
	}

	/**
	 * 获取系统当前时间(指定返回类型)
	 * 
	 * @return
	 */
	public static String getNowTime(String type) {
		SimpleDateFormat df = new SimpleDateFormat(type);
		return df.format(new Date());
	}

	/**
	 * 使用预设格式提取字符串日期
	 * 
	 * @param date
	 *            日期字符串
	 * @return
	 */
	public static Date parse(String date) {
		return parse(date, DATE_FULL_STR);
	}

	/**
	 * 指定指定日期字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parse(String date, String pattern) {
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 两个时间比较
	 * 
	 * @param
	 * @return
	 */
	public static int compareDateWithNow(Date date) {
		Date now = new Date();
		int rnum = date.compareTo(now);
		return rnum;
	}

	/**
	 * 两个时间比较(时间戳比较)
	 * 
	 * @param
	 * @return
	 */
	public static int compareDateWithNow(long date) {
		long now = dateToUnixTimestamp();
		if (date > now) {
			return 1;
		} else if (date < now) {
			return -1;
		} else {
			return 0;
		}
	}

	/**
	 * 将指定的日期转换成Unix时间戳
	 * 
	 * @param date
	 *            需要转换的日期 yyyy-MM-dd HH:mm:ss
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp(String date) {
		long timestamp = 0;
		try {
			timestamp = new SimpleDateFormat(DATE_FULL_STR).parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return timestamp;
	}

	/**
	 * 将指定的日期转换成Unix时间戳
	 * 
	 * @param date
	 *            需要转换的日期 yyyy-MM-dd
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp(String date, String dateFormat) {
		long timestamp = 0;
		try {
			timestamp = new SimpleDateFormat(dateFormat).parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * 将当前日期转换成Unix时间戳
	 * 
	 * @return long 时间戳
	 */
	public static long dateToUnixTimestamp() {
		long timestamp = new Date().getTime();
		return timestamp;
	}

	/**
	 * 将Unix时间戳转换成日期
	 * 
	 * @param timestamp
	 *            时间戳
	 * @return String 日期字符串
	 */
	public static String unixTimestampToDate(long timestamp) {
		SimpleDateFormat sd = new SimpleDateFormat(DATE_FULL_STR);
		sd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		return sd.format(new Date(timestamp));
	}

	/**
	 * 将Unix时间戳转换成日期
	 * 
	 * @param timestamp
	 *            时间戳
	 * @return String 日期字符串
	 */
	public static String TimeStamp2Date(long timestamp, String dateFormat) {
		String date = new SimpleDateFormat(dateFormat).format(new Date(timestamp));
		return date;
	}

	/**
	 * 将Unix时间戳转换成日期
	 * 
	 * @param timestamp
	 *            时间戳
	 * @return String 日期字符串
	 */
	public static String TimeStamp2Date(long timestamp) {
		String date = new SimpleDateFormat(DATE_FULL_STR).format(new Date(timestamp));
		return date;
	}

	/**
	 * 日期相减
	 * 
	 * @param date
	 *            日期
	 * @param date1
	 *            日期
	 * @return 返回相减后的毫秒數
	 */
	public static long diffDate(java.util.Date date, java.util.Date date1) {
		return getMillis(date) - getMillis(date1);
	}

	/**
	 * 日期相减(字串)
	 * 
	 * @param date
	 *            日期
	 * @param date1
	 *            日期
	 * @return 返回相减后的毫秒數
	 */
	public static long diffStringDate(String date, String date1, String patterm1, String patterm2) {
		java.util.Date date3 = parse(date, patterm1);
		java.util.Date date4 = parse(date1, patterm2);
		return (getMillis(date3) - getMillis(date4));
	}

	/**
	 * 返回毫秒
	 * 
	 * @param date
	 *            日期
	 * @return 返回毫秒
	 */
	public static long getMillis(java.util.Date date) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	/**
	 * 取得系統日期近一年年月(內容格式: "2014-01")
	 * 
	 * @return
	 */
	public static List<String> getNowYearMonth() {
		List<String> dateList = new ArrayList<String>();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		// 首先取得系統日期
		Calendar cal = Calendar.getInstance();

		// 一年份
		for (int i = 0; i < 12; i++) {
			if (i != 0) {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			}
			String date = dateFormat.format(cal.getTime());
			dateList.add(0, date);
		}

		return dateList;
	}

	/**
	 * 將民國轉為西元，並依照個人需求加入格式參數，如此將可轉換多種格式
	 * 
	 * @param AD
	 * @param beforeFormat
	 * @param afterFormat
	 * @return
	 * @throws ParseException
	 */
	public static String convertTWDate(String twDate) {// 轉年月格式
		String addate = "";
		if (twDate.length() != 7) { // 可用到民國999年,到天荒地老
			return "";
		}

		try {
			int yy = Integer.valueOf(twDate.substring(0, 3)).intValue() + 1911;
			String year = String.valueOf(yy);
			String month = twDate.substring(3, 5);
			String day = twDate.substring(5, 7);
			addate = year + month + day;
		} catch (Exception ignore) {
			addate = "";
		}

		return addate;
	}
}