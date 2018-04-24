/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Date Utils
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

}
