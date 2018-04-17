package cn.jxust.bigdata.loganalyze.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import clojure.main;

/*
 * 格式化时间-获取当前时间
 */
public class DateUtils {

	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(new Date());//格式化当前时间
		return date;
	}
	
	public static String getDataTime(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    public static String before30Minute(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.add(Calendar.MINUTE, -30);//30分钟前
        return formatter.format(calendar.getTime());
    }

}
