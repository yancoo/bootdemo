package hh.bootdemo.session;

import java.util.Date;

import javax.servlet.http.HttpSession;

import hh.bootdemo.CommonConstants;
import hh.bootdemo.auth.User;

/**
 * logined session
 * 
 * @author yan
 */
public class Session {

	public String id;
	public String ip;
	public HttpSession httpSession;
	public User user;
	public Date createDate;
	public Date lastRequestDate;
	public int requestCount;

	public SessionDTO createDTO() {
		SessionDTO dto = new SessionDTO();
		dto.createDate = this.createDate;
		dto.id = this.id;
		dto.ip = this.ip;
		if (this.user != null) {
			dto.username = this.user.getName();
			dto.userId = this.user.getId();
		}
		dto.lastRequestDate = this.lastRequestDate;
		dto.requestCount = this.requestCount;
		return dto;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastRequestDate() {
		return lastRequestDate;
	}

	public void setLastRequestDate(Date lastRequestDate) {
		this.lastRequestDate = lastRequestDate;
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(int requestCount) {
		this.requestCount = requestCount;
	}

	public long getOnlineDuration() {
		long duration = 0;
		if (this.createDate == null) {
			return duration;
		}
		duration = (System.currentTimeMillis() - this.createDate.getTime()) / CommonConstants.aSecond;
		if (duration < 0) {
			duration = 0;
		}
		return duration;
	}

	public String toString() {
		return "Session(id=" + id + ", ip=" + ip + ", createDate=" + createDate + ", " + this.getOnlineDuration() + "s"
				+ (user != null ? ", " + user.getName() + "(" + user.getId() + ")" : "") + ")";
	}
}
