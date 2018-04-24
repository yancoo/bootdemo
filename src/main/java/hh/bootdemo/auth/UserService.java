package hh.bootdemo.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ServiceException;
import hh.bootdemo.journal.CommonJournalType;
import hh.bootdemo.journal.Journal;
import hh.bootdemo.journal.JournalService;
import hh.bootdemo.session.Session;
import hh.bootdemo.session.SessionCache;
import hh.bootdemo.utils.RequestUtils;

@Service
public class UserService implements UserDetailsService {

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	UserRepository userRepository;

	@Autowired
	JournalService journalService;

	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<User> users = userRepository.findByName(username);
		if (users.size() == 0) {
			return null;
		}
		User user = users.get(0);
		user.getAuthorities(); // @Transactional事务下提前getAuthorities，避免外部 lazy init exception
		return user;
	}

	@Transactional
	public void onLoginSuccess(HttpServletRequest request, User user) {
		if (user == null) {
			throw new ServiceException(CommonExceptionType.internalError, "null user");
		}
		HttpSession httpSession = request.getSession();
		Session session = SessionCache.getSession(httpSession.getId());
		String ip = RequestUtils.getRemoteAddr(request);
		boolean kickoff = false;
		if (session == null) {// new session
			session = SessionCache.addSession(httpSession, user, ip);
		} else {
			if (session.user != null && !session.user.getName().equals(user.getName())) {// 切换不同用户
				// 触发原有用户下线
				kickoff = true;
				this.onLogout(session, true);

				// 新用户session重新计时
				session.createDate = new Date();
				session.user = user;
			}
		}

		Map<String, Object> jmap = new HashMap<String, Object>();
		jmap.put("login", "success");
		if (kickoff) {
			jmap.put("relogin", true);
		}

		try {
			journalService.save(new Journal("auth", null, CommonJournalType.LOGIN.name(), "user", user.getName(), ip,
					httpSession.getId(), new ObjectMapper().writeValueAsString(jmap)));
		} catch (JsonProcessingException e) {
			log.error("writeValueAsString error, " + jmap, e);
		}

		log.info("login succcess " + user);
	}

	@Transactional
	public void onLoginFail(HttpServletRequest request, AuthenticationException authException) {
		HttpSession httpSession = request.getSession();
		String username = request.getParameter("username");

		Map<String, Object> jmap = new HashMap<String, Object>();
		jmap.put("login", "fail");
		jmap.put("throwableName", authException.getClass().getName());

		try {
			journalService.save(new Journal("auth", null, CommonJournalType.LOGIN.name(), "user", username,
					RequestUtils.getRemoteMap().get("ip"), httpSession.getId(),
					new ObjectMapper().writeValueAsString(jmap)));
		} catch (JsonProcessingException e) {
			log.error("writeValueAsString error, " + jmap, e);
		}

		log.info("login fail " + username + ", throwableName " + authException.getClass().getName());
	}

	@Transactional
	public void onLogout(Session session, boolean kickoff) {
		if (session == null || session.user == null) {
			throw new ServiceException(CommonExceptionType.internalError, "null user");
		}

		Map<String, Object> jmap = new HashMap<String, Object>();
		jmap.put("duration", session.getOnlineDuration());
		if (kickoff) {
			jmap.put("kickoff", true);
		}

		try {
			journalService.save(new Journal("auth", null, CommonJournalType.LOGOUT.name(), "user",
					session.user.getName(), session.ip, session.id, new ObjectMapper().writeValueAsString(jmap)));
		} catch (JsonProcessingException e) {
			log.error("writeValueAsString error, " + jmap, e);
		}
	}
}
