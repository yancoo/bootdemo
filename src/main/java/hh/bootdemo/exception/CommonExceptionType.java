package hh.bootdemo.exception;

/**
 *
 * <pre>
 * | "200"   ; OK
 * | "201"   ; Created
 * | "202"   ; Accepted
 * | "204"   ; No Content
 * | "301"   ; Moved Permanently
 * | "302"   ; Moved Temporarily
 * | "304"   ; Not Modified
 * | "400"   ; Bad Request
 * | "401"   ; Unauthorized
 * | "403"   ; Forbidden
 * | "404"   ; Not Found
 * | "500"   ; Internal Server Error
 * | "501"   ; Not Implemented
 * | "502"   ; Bad Gateway
 * | "503"   ; Service Unavailable
 * </pre>
 *
 * @author yan
 */
public enum CommonExceptionType {

    /**
     * 401 Bad Request
     */
    /**
     * 401 Bad Request
     */
    /**
     * 401 Bad Request
     */
    /**
     * 401 Bad Request
     */
    httpBadRequest,
    /**
     * 401 未授权
     */
    httpUnauthorized,
    /**
     * 403 拒绝访问
     */
    httpForbidden,
    /**
     * 404 页面未找到
     */
    httpNotFound,
    /**
     * 405 http方法不允许
     */
    httpMethodNotAllowed,
    /**
     * 415 请求方法所需的资源不支持请求实体的格式
     */
    httpUnsupportedMediaType,
    /**
     * 参数错误
     */
    parameterError,
    /**
     * 逻辑不允许
     */
    permissionDenied,
    /**
     * null exception 异常
     */
    nullException,
    /**
     * 内部错误
     */
    internalError,
    /**
     * 配置错误
     */
    configError,
    /**
     * 未知错误
     */
    unknownError,
    /**
     * token验证错误
     */
    tokenError,
    /**
     * db连接异常
     */
    dbConnectException,
    /**
     * 角色在使用
     */
    groupIsUsed,
    /**
     * 用户名已存在
     */
    userNameIsExist,
    /**
     * 角色名已存在
     */
    groupNameIsExist,
    /**
     * 验证码错误
     */
    validationCodeError,
}
