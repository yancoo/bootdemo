package hh.bootdemo.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Service Exception
 *
 * @author yan
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties({ "cause", "localizedMessage", "stackTrace", "suppressed" })
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 6742768240152341377L;

	/**
	 * 有些时候 给客户端不仅仅是message解释，供页面交互使用
	 */
	private String typeName;

	/**
	 * 异常类名
	 */
	private String throwableName;

	/**
	 * 允许提供错误参数供回显
	 */
	private String[] args;

	/**
	 * service exception
	 *
	 * @param type
	 *            通常为 ApplicationResources*.properites 中type
	 */
	public ServiceException(CommonExceptionType type) {
		this(type, null, null, null);
	}

	/**
	 *
	 * @param type
	 *            通常为 ApplicationResources*.properites 中type
	 * @param args
	 *            ApplicationResources*.properites
	 *            中exceptionType对应字符串所需变量值；这里使用Object[]简化调用
	 */
	public ServiceException(CommonExceptionType type, Object[] args) {
		this(type, args, null, null);
	}

	public ServiceException(CommonExceptionType type, String message) {
		this(type, null, null, message);
	}

	public ServiceException(CommonExceptionType type, Throwable root) {
		this(type, null, root, root.getMessage());
	}

	public ServiceException(CommonExceptionType type, Throwable root, String rootMessage) {
		this(type, null, root, rootMessage);
	}

	public ServiceException(CommonExceptionType type, String rootMessage, Throwable root) {
		this(type, null, root, rootMessage);
	}

	public ServiceException(CommonExceptionType type, Object[] args, Throwable root, String message) {
		this(type.name(), args, root, message);
	}

	public ServiceException(String typeName) {
		this(typeName, null, null, null);
	}

	public ServiceException(String typeName, Object[] args) {
		this(typeName, args, null, null);
	}

	public ServiceException(String typeName, String message) {
		this(typeName, null, null, message);
	}

	public ServiceException(String typeName, Throwable root) {
		this(typeName, null, root, root.getMessage());
	}

	public ServiceException(String typeName, Throwable root, String rootMessage) {
		this(typeName, null, root, rootMessage);
	}

	public ServiceException(String typeName, String rootMessage, Throwable root) {
		this(typeName, null, root, rootMessage);
	}

	public ServiceException(String typeName, Object[] args, Throwable root, String message) {
		super(message, root);
		this.typeName = typeName;
		this.throwableName = root == null ? this.getClass().getSimpleName() : root.getClass().getSimpleName();
		if (args != null) {
			String[] newArgs = new String[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					newArgs[i] = args[i].toString();
				}
			}
			this.args = newArgs;
		}
	}

	public ServiceException(Throwable root) {
		super(root);
	}

	public String getTypeName() {
		return typeName;
	}

	public String getThrowableName() {
		return throwableName;
	}

	public String[] getArgs() {
		return args;
	}

	public String toString() {
		return "ServiceException[type=" + this.getTypeName()
				+ (this.getMessage() != null ? ", message=" + this.getMessage() : "") + "]";
	}
}
