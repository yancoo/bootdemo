package hh.bootdemo.session;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import hh.bootdemo.CommonConstants;
import hh.bootdemo.auth.UserService;

@WebListener
public class SessionListener implements HttpSessionListener {

	private final Log log = LogFactory.getLog(getClass());

	@Autowired
	UserService userService;

	/**
	 * session created，此时通常未登录
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession httpSession = event.getSession();
		if (httpSession == null) {
			return;
		}
		Session session = SessionCache.getSession(httpSession.getId());
		if (log.isInfoEnabled()) {
			log.info("start " + httpSession.getId() + ", " + session + ", " + event);
		}
	}

	/**
	 * session destroyed
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession httpSession = event.getSession();
		if (httpSession == null) {
			return;
		}
		Session session = SessionCache.removeSession(httpSession.getId());
		if (session != null && session.user != null) {
			userService.onLogout(session, false);
		}

		if (log.isInfoEnabled()) {
			log.info("end " + httpSession.getId() + ", "
					+ (System.currentTimeMillis() - httpSession.getCreationTime()) / CommonConstants.aSecond + "s, "
					+ session + ", " + event);
		}

	}
}
