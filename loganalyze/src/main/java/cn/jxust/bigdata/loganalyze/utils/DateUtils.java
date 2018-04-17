package cn.jxust.bigdata.loganalyze.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import clojure.main;

/*
 * ��ʽ��ʱ��-��ȡ��ǰʱ��
 */
public class DateUtils {

	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = df.format(new Date());//��ʽ����ǰʱ��
		return date;
	}
	
	public static String getDataTime(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(calendar.getTime());
    }

    public static String before30Minute(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.add(Calendar.MINUTE, -30);//30����ǰ
        return formatter.format(calendar.getTime());
    }

}
