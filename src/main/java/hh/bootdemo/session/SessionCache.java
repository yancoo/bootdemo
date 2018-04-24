package hh.bootdemo.session;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import hh.bootdemo.auth.User;

/**
 * logined session cache
 * 
 * @author yan
 */
public class SessionCache {

	private final static Log log = LogFactory.getLog(SessionCache.class);

	private static Map<String, Session> sessions = new ConcurrentHashMap<String, Session>();

	public static Session addSession(HttpSession httpSession, User user, String ip) {
		Session session = sessions.get(httpSession.getId());
		if (session == null) {
			session = new Session();
			session.id = httpSession.getId();
			session.createDate = new Date();
			session.httpSession = httpSession;
			session.user = user;
			session.ip = ip;

			sessions.put(session.id, session);
			if (log.isInfoEnabled()) {
				log.info("add new " + session);
			}
		} else {
			session.user = user;
			if (log.isInfoEnabled()) {
				log.info("re-add " + session);
			}
		}
		return session;
	}

	public static Session removeSession(String sessionId) {
		Session session = sessions.remove(sessionId);
		if (log.isInfoEnabled()) {
			log.info("remove " + (session != null ? session : sessionId + ", null session object"));
		}
		return session;
	}

	public static Session getSession(String sessionId) {
		return sessions.get(sessionId);
	}

	public static Map<String, Session> getSessions() {
		return sessions;
	}

	public static void getStatus(StringBuilder sb, boolean detail) {
		sb.append("sessions: " + sessions.size()).append("\n");
		if (detail) {
			for (Session session : sessions.values()) {
				sb.append(session).append("\n");
			}
		}
	}
}
