/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * http request utils
 *
 * @author yan
 */
public class RequestUtils {
	protected final static Log log = LogFactory.getLog(RequestUtils.class);

	/**
	 * 脱离http request，thread local形式保存ip，方便使用
	 */
	public static ThreadLocal<Map<String, String>> ipThreadLocal = new ThreadLocal<Map<String, String>>();

	/**
	 * 获取remote map
	 *
	 * @return
	 * @see #setRemoteMap(Map) 需filter中提前设置
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getRemoteMap() {
		Map<String , String> map  = ipThreadLocal.get();
		if( map == null) {
			return Collections.EMPTY_MAP;
		}
		return map;
	}

	/**
	 * 设置remote map，通常在filter中提前设置
	 *
	 * @param map
	 */
	public static void setRemoteMap(Map<String, String> map) {
		ipThreadLocal.set(map);
	}

	/**
	 * 获取客户端地址
	 *
	 * @param request
	 * @return String
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String address = request.getHeader("X-Forwarded-For");
		if (address != null && address.length() > 0 && !"unknown".equalsIgnoreCase(address)) {
			return address;
		}
		String realIP = request.getHeader("X-Real-IP");
		if (realIP == null) {
			realIP = request.getRemoteAddr();
		}
		if ("0:0:0:0:0:0:0:1".equals(realIP)) {
			realIP = "localhost";
		}
		return realIP;
	}

}
