/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hh.bootdemo.CommonConstants;
import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ServiceException;

/**
 *
 * @author yan
 */
public class DateUtils {

    private final static String defaultDatePattern = "yyyy-MM-dd HH:mm:ss.SSS";

    public static Date getDateFromStr(String str) {
        return getDateFromStr(str, defaultDatePattern);
    }

    public static Date getDateFromStr(String str, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStrFromDate(Date date) {
        return getStrFromDate(date, defaultDatePattern);
    }

    public static String getStrFromDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 两个日期为同一天
     *
     * @param calendar1
     * @param calendar2
     * @return boolean
     */
    public static boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null || calendar2 == null) {
            return false;
        }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获得指定日期的下一天
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar nextDay(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.setTime(calendar.getTime());
        nextCalendar.add(Calendar.DAY_OF_YEAR, 1);
        return nextCalendar;
    }

    /**
     * 获得指定日期的下一小时
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar nextHour(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.setTime(calendar.getTime());
        nextCalendar.add(Calendar.HOUR_OF_DAY, 1);
        return nextCalendar;
    }

    /**
     * 获得指定日期的下一个5分钟
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar next5Min(Calendar calendar) {
        return DateUtils.nextXMin(calendar, 5);
    }

    /**
     * 获得指定日期的下一个10分钟
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar next10Min(Calendar calendar) {
        return DateUtils.nextXMin(calendar, 10);
    }

    /**
     * 获得指定日期的下一个15分钟
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar next15Min(Calendar calendar) {
        return DateUtils.nextXMin(calendar, 15);
    }

    /**
     * 获得指定日期的下一个30分钟
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar next30Min(Calendar calendar) {
        return DateUtils.nextXMin(calendar, 30);
    }

    /**
     * 获得指定日期的下一个30分钟
     *
     * @param calendar
     * @return Calendar
     */
    public static Calendar nextXMin(Calendar calendar, int x) {
        if (calendar == null) {
            return null;
        }
        Calendar nextCalendar = Calendar.getInstance();
        nextCalendar.setTime(calendar.getTime());
        nextCalendar.add(Calendar.MINUTE, x);
        return nextCalendar;
    }

    /**
     * 得到几天前的时间
     *
     * @param d 指定日期
     * @param day 指定前几天
     * @return Date
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d 指定日期
     * @param day 指定后几天
     * @return Date
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * @param day 指定的天数
     * @return List<Date> 返回不包括今天的前Day天日期
     */
    public static List<Date> getLatestDay(int day) {
        List<Date> dates = new ArrayList<Date>();
//        dates.add(new Date());
        for (int i = day; i > 0; i--) {
            dates.add(getDateBefore(new Date(), i));
        }
        return dates;
    }

    /**
     * @param day 指定的天数
     * @return List<Date> 返回包括今天的前Day天日期
     */
    public static List<Date> getLatesEqualNowDay(int day) {
        List<Date> dates = new ArrayList<Date>();
        for (int i = day - 1; i > 0; i--) {
            dates.add(getDateBefore(new Date(), i));
        }
        dates.add(new Date());
        return dates;
    }

    /**
     * 返回指定日期月份的最后一天
     *
     * @param date
     * @return Date
     */
    public static Date getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getMaximum(Calendar.DATE));
        return calendar.getTime();
    }

    /**
     * ps -e -o pid,lstart,etime,cmd 可以查看进程开始时间、时长，转换etime格式为秒值
     *
     * @param etime ELAPSED elapsed time since the process was started, in the
     * form [[dd-]hh:]mm:ss
     * @return 秒值
     */
    public static long etimeToSeconds(String etime) {
        if (etime == null || etime.length() == 0) {
            return 0;
        }
        int d = 0, h = 0, m = 0, s = 0;
        String[] split = etime.split("-");
        if (split.length > 1) {
            d = Integer.valueOf(split[0]);
            etime = split[1];
        }
        split = etime.split(":");
        if (split.length == 3) {
            h = Integer.valueOf(split[0]);
            m = Integer.valueOf(split[1]);
            s = Integer.valueOf(split[2]);
        } else if (split.length == 2) {
            m = Integer.valueOf(split[0]);
            s = Integer.valueOf(split[1]);
        } else if (split.length == 1) {
            s = Integer.valueOf(split[0]);
        } else {
            throw new ServiceException(CommonExceptionType.internalError, "unsupported etime " + etime + ", length " + split.length);
        }
        long seconds = (d * CommonConstants.aDay + h * CommonConstants.aHour + m * CommonConstants.aMinute + s * CommonConstants.aSecond) / CommonConstants.aSecond;
        return seconds;
    }
}
