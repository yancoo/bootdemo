/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import hh.bootdemo.CommonConstants;
import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ServiceException;

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

	/**
	 * 根据参数生成签名的url
	 *
	 * @param prefixUrl
	 *            如http://xx/jiajike/api/auth/loginProduct，由contextUrl和具体api部分组成
	 * @param params
	 *            全部请求参数，不包含key
	 * @param key
	 *            验证key
	 * @return 按字母顺序（从A至Z）排列生成token 并返回完整url
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static String generateTokenUrl(String prefixUrl, Map<String, String> params, String key) {
		if (StringUtils.isEmpty(key) || params == null || params.isEmpty()) {
			throw new ServiceException(CommonExceptionType.parameterError);
		}
		if (!params.containsKey("time")) {
			params.put("time", DateUtils.getStrFromDate(new Date(), "yyyyMMddHHmmss"));
		}
		List<String> pkeys = getSortedParamKeys(params);

		// 生成token
		StringBuilder sb = new StringBuilder();
		for (String pkey : pkeys) {
			sb.append(params.get(pkey));
		}
		sb.append(key);
		String token = StringUtils.md5(sb.toString());

		// 拼链接
		sb = new StringBuilder(prefixUrl);
		sb.append("?");
		for (String pkey : pkeys) {
			sb.append(pkey).append("=").append(params.get(pkey)).append("&");
		}
		sb.append("token=" + token);

		return sb.toString();
	}

	/**
	 * 验证请求参数里的token是否正确
	 *
	 * @param params
	 *            请求参数
	 * @param key
	 *            验证key
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static void checkToken(Map<String, String> params, String key) {
		if (StringUtils.isEmpty(key) || params == null || params.isEmpty()) {
			throw new ServiceException(CommonExceptionType.parameterError);
		}
		String token = params.remove("token");
		if (StringUtils.isEmpty(token)) {
			throw new ServiceException(CommonExceptionType.tokenError);
		}

		String time = params.get("time");
		if (StringUtils.isEmpty(time)) {
			throw new ServiceException(CommonExceptionType.parameterError);
		}
		long ptime = DateUtils.getDateFromStr(time, "yyyyMMddHHmmss").getTime();
		if (Math.abs(ptime - System.currentTimeMillis()) > 10 * CommonConstants.aMinute) {
			throw new ServiceException(CommonExceptionType.parameterError, "time error");
		}

		List<String> pkeys = getSortedParamKeys(params);

		// 计算并校验token
		StringBuilder sb = new StringBuilder();
		for (String pkey : pkeys) {
			sb.append(params.get(pkey));
		}
		sb.append(key);
		String realToken = StringUtils.md5(sb.toString());
		if (!realToken.equals(token)) {
			throw new ServiceException(CommonExceptionType.tokenError);
		}
	}

	@SuppressWarnings("rawtypes")
	private static List getSortedParamKeys(Map<String, String> params) {
		List<String> pkeys = new ArrayList<String>(params.keySet());
		// 按字母顺序排序参数键值
		Collections.sort(pkeys);
		return pkeys;
	}

	/**
	 * http get
	 *
	 * @param uri
	 * @param connectTimeOut
	 * @return String
	 */
	public static String httpGet(String uri, int connectTimeOut) {
		return RequestUtils.httpGet(uri, null, 0, connectTimeOut, "UTF-8");
	}

	/**
	 * http get
	 *
	 * @param uri
	 * @param userAgent
	 * @param readTimeOut
	 * @param connectTimeOut
	 * @param charsetName
	 * @return String
	 */
	public static String httpGet(String uri, String userAgent, int readTimeOut, int connectTimeOut,
			String charsetName) {
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (null != userAgent) {
				conn.setRequestProperty("User-Agent", userAgent);
			}
			if (readTimeOut > 0) {
				conn.setReadTimeout(readTimeOut);
			}
			if (connectTimeOut > 0) {
				conn.setConnectTimeout(connectTimeOut);
			}
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.connect();

			InputStream is = null;
			try {
				is = conn.getInputStream();
			} catch (IOException ioe) {
				is = conn.getErrorStream();
			}
			String result = stream2String(is, charsetName);
			return result;
		} catch (Exception e) {
			log.error("get " + uri + " fail", e);
			throw new ServiceException(CommonExceptionType.internalError, e.getMessage());
		}
	}

	/**
	 * http post
	 *
	 * @param uri
	 * @param queryString
	 * @param connectTimeOut
	 * @return String
	 */
	public static String httpPost(String uri, String queryString, int connectTimeOut) {
		return RequestUtils.httpPost(uri, queryString, null, 0, connectTimeOut, "UTF-8");
	}

	/**
	 * http post
	 *
	 * @param uri
	 * @param queryString
	 * @param userAgent
	 * @param readTimeOut
	 * @param connectTimeOut
	 * @param charsetName
	 * @return String
	 */
	public static String httpPost(String uri, String queryString, String userAgent, int readTimeOut, int connectTimeOut,
			String charsetName) {
		try {
			URL url = new URL(uri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (null != userAgent) {
				conn.setRequestProperty("User-Agent", userAgent);
			}
			if (readTimeOut > 0) {
				conn.setReadTimeout(readTimeOut);
			}
			if (connectTimeOut > 0) {
				conn.setConnectTimeout(connectTimeOut);
			}
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			if (log.isInfoEnabled()) {
				log.info("uri " + uri + ", " + queryString);
			}

			conn.connect();

			InputStream is = null;
			try {
				is = conn.getInputStream();
			} catch (IOException ioe) {
				is = conn.getErrorStream();
			}
			String result = stream2String(is, charsetName);
			return result;
		} catch (Exception e) {
			log.error("post " + uri + ", " + queryString + " fail", e);
			throw new ServiceException(CommonExceptionType.internalError, e.getMessage());
		}
	}

	/**
	 * 检查登录合法性及返回sdk返回的用户id或部分用户信息
	 *
	 * @param uri
	 *            uri
	 * @param params
	 *            params
	 * @param connectTimeOut
	 *            connectTimeOut
	 * @return 验证合法 返回true 不合法返回 false
	 */
	public static String httpPost(String uri, Map<String, List<String>> params, int connectTimeOut) {
		String queryString = RequestUtils.buildUrlParamStr(params);
		return RequestUtils.httpPost(uri, queryString, connectTimeOut);
	}

	/**
	 * 获取查询字符串
	 *
	 * @param params
	 *            params
	 * @return String
	 */
	public static String buildUrlParamStr(Map<String, List<String>> params) {
		String queryString = "";
		try {
			for (String key : params.keySet()) {
				List<String> values = params.get(key);
				for (int i = 0; i < values.size(); i++) {
					String value = values.get(i);
					queryString += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
				}
			}
			queryString = queryString.substring(0, queryString.length() - 1);
		} catch (Exception e) {
			log.error("build error", e);
		}
		return queryString;
	}

	private static String stream2String(InputStream is, String charsetName) {
		BufferedReader br = null;
		try {
			if (charsetName != null) {
				br = new BufferedReader(new java.io.InputStreamReader(is, charsetName));
			} else {
				br = new BufferedReader(new java.io.InputStreamReader(is));
			}
			String line = "";
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			return sb.toString();
		} catch (Exception e) {
			log.error("reader error", e);
		} finally {
			tryCloseReader(br);
		}
		return "";
	}

	protected static void tryCloseOutputStream(OutputStream os) {
		try {
			if (null != os) {
				os.close();
				os = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void tryCloseWriter(java.io.Writer writer) {
		try {
			if (null != writer) {
				writer.close();
				writer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static void tryCloseReader(java.io.Reader reader) {
		try {
			if (null != reader) {
				reader.close();
				reader = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
