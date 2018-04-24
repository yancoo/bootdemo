/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.security.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import hh.bootdemo.auth.User;
import hh.bootdemo.session.Session;
import hh.bootdemo.session.SessionCache;
import hh.bootdemo.utils.RequestUtils;

/**
 * 输出request/response数据
 *
 * @author yan
 * @version $Author: yanfangjun $ $Revision: 1.16 $ $Date: 2012/07/18 10:44:35 $
 */
public class AccessLogFilter implements Filter {

	private final Log log = LogFactory.getLog(getClass());

	protected final static boolean ignoreFilterUri = true;

	protected final static String[] filterUri = { ".css", ".jpg", ".png", ".gif", ".js", ".ico", ".html" };

	protected static Long totalCount = 0l;
	protected static Long filterCount = 0l;
	protected static Long mainCount = 0l;

	protected static final int statisticsCount = 18000;// about 600s*30p/s
	protected static final int statisticsTimer = 600;// 600s
	protected static long lastStatisticsTimer = 0;
	protected static final int longResponse = 3;// 3s

	protected static final Object lock = new Object();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		long requestTime = System.currentTimeMillis();

		boolean isFilterUri = false;
		String uri = null;
		Session session = null;
		String ip = null;
		if (request instanceof HttpServletRequest) {
			HttpSession httpSession = ((HttpServletRequest) request).getSession();
			ip = this.getRemoteAddr((HttpServletRequest) request);
			
			session = SessionCache.getSession(httpSession.getId());
			if (session != null) {
				session.lastRequestDate = new Date();
				session.requestCount++;
			}
			
			// --- ThreadLocal记录用户信息，便于controller层记录journal
			Map<String, String> remoteMap = new HashMap<String, String>();
			remoteMap.put("ip", ip);
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				remoteMap.put("username", authentication.getName());
			}
			remoteMap.put("sessionId", httpSession.getId());
			RequestUtils.setRemoteMap(remoteMap);
			// ---
			
			if (log.isInfoEnabled()) {
				uri = ((HttpServletRequest) request).getRequestURI();
				for (int i = 0; i < filterUri.length; i++) {
					if (uri.endsWith(filterUri[i])) {
						isFilterUri = true;
					}
				}
				synchronized (lock) {
					totalCount++;
					if (isFilterUri) {
						filterCount++;
					} else {
						mainCount++;
					}
					if (lastStatisticsTimer == 0) {
						lastStatisticsTimer = requestTime;
					}
					long statisticsSecond = (requestTime - lastStatisticsTimer) / 1000;
					if (statisticsSecond > 0 && (statisticsSecond > statisticsTimer || totalCount >= statisticsCount)) {
						lastStatisticsTimer = requestTime;
						// 使用warn输出统计信息
						log.warn("statistics total/filter/main : count " + totalCount + "/" + filterCount + "/"
								+ mainCount + ", pps " + (long) (totalCount / statisticsSecond) + "/"
								+ (long) (filterCount / statisticsSecond) + "/" + (long) (mainCount / statisticsSecond)
								+ ", duration " + statisticsSecond + "s");
						totalCount = filterCount = mainCount = 0l;
					}
				}
			}
		}

		// chain filter
		chain.doFilter(request, response);

		if (log.isInfoEnabled() && request instanceof HttpServletRequest) {
			if (!(ignoreFilterUri && isFilterUri)) {
				long processDuration = System.currentTimeMillis() - requestTime;
				String queryString = getParameter((HttpServletRequest) request);
				String logString = ((HttpServletRequest) request).getMethod() + " " + uri
						+ (queryString != null ? "?" + queryString : "") + "\t" + ip + "\t" + appendLog(session);
				log.info("process request: " + processDuration + " ms\t " + logString);
				if (processDuration > longResponse * 1000) {// 3s
					log.info(processDuration + " > " + longResponse + "s\t" + logString);
				}
			}
		}
	}

	/**
	 * 获取客户端地址
	 *
	 * @param request
	 * @return String
	 */
	private String getRemoteAddr(HttpServletRequest request) {
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

	private String appendLog(Session session) {
		StringBuilder sb = new StringBuilder();
		if (session != null && session.user != null) {
			User user = session.user;
			sb.append(user.getName()).append("(").append(user.getId()).append(")");
		}
		return sb.toString();
	}

	@SuppressWarnings({ "rawtypes" })
	private String getParameter(HttpServletRequest request) {
		if (request.getRequestURI().contains("auth/login")) {// 避免第一次登录request.getParameterNames(); IOException
			return null;
		}
		Enumeration parameterNames = null;
		try {
			parameterNames = request.getParameterNames();
		} catch (Exception e) {
			return null;
		}
		if (parameterNames == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		while (parameterNames.hasMoreElements()) {
			String parameterName = (String) parameterNames.nextElement();
			sb.append(parameterName);
			// checkbox多选等存在name相同的多个值
			String[] parameterValues = request.getParameterValues(parameterName);
			if (parameterValues != null) {// 对于无值的情况，仍输出name
				if (parameterValues.length > 0) {
					sb.append("=");
				}
				for (int i = 0; i < parameterValues.length; i++) {
					String parameterValue = parameterValues[i];
					sb.append(parameterValue);
					if (i < parameterValues.length - 1) {
						sb.append("&").append(parameterName).append("=");
					}
				}
			}
			if (parameterNames.hasMoreElements()) {
				sb.append("&");
			}
		}
		if (sb.length() == 0) {
			return null;
		}
		return sb.toString();
	}

	@Override
	public void destroy() {
	}

}
