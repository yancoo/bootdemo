package hh.bootdemo.journal;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * journal
 *
 * @author yan
 */
@Entity
@Table(name = "journal", schema = "journal")
public class Journal implements Serializable {

	private static final long serialVersionUID = -367874293927289971L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(length = 128)
	private String menu;
	@Column(length = 128)
	private String subMenu;
	@Column(length = 128)
	private String type;
	@Column(length = 128)
	private String subType;
	@Column(length = 128)
	private String objType;
	@Column(length = 255)
	private String userName;
	@Column(length = 128)
	private String userIp;
	@Column(length = 128)
	private String sessionId;
	@Column(length = 40960)
	private String content;
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	public Journal() {
	}

	public Journal(Long id) {
		this.id = id;
	}

	public Journal(Long id, String type) {
		this.id = id;
		this.type = type;
	}

	public Journal(String menu, String subMenu, String type, String objType, String userName, String userIp,
			String sessionId, String content) {
		this.menu = menu;
		this.subMenu = subMenu;
		this.type = type;
		this.objType = objType;
		this.userName = userName;
		this.userIp = userIp;
		this.sessionId = sessionId;
		this.content = content;
		this.createDate = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(String subMenu) {
		this.subMenu = subMenu;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Journal)) {
			return false;
		}
		Journal other = (Journal) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(menu=" + menu + (subMenu == null ? "" : ", subMenu=" + subMenu)
				+ ", type=" + type + ", objType=" + objType + ", userName=" + userName + ", content=" + content + ")";
	}

}
