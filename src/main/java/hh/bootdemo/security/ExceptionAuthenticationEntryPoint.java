package hh.bootdemo.security;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ExceptionHelper;
import hh.bootdemo.exception.ServiceException;

/**
 * 异常转换为json格式输出
 * 
 * @author yan
 */
@ControllerAdvice
public class ExceptionAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		// 401
		// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication
		// Failed"); // html

		// 对于JPA抛出的异常，由StandardWrapper.exception()作为RequestDispatcher.ERROR_EXCEPTION属性放入request
		// StandardHostValve.invoke()取出throwable -> throwable() -> custom() ->
		// ApplicationDispatcher.forward() -> FilterSecurityInterceptor.invoke()
		// -> AffirmativeBased.decide()
		// 内部经AuthenticationTrustResolverImpl.isAnonymous()判断为未认证，vote为ACCESS_DENIED，decide()方法抛出AccessDeniedException
		// 并被ExceptionTranslationFilter.handleSpringSecurityException()转换为InsufficientAuthenticationException
		// 这里找出真正的JPA异常，并通知到前端
		Throwable t = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
		if (t == null) {
			t = authException;
		}
		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().append(
				ExceptionHelper.generateException(new ServiceException(CommonExceptionType.httpUnauthorized, t)));
		response.getWriter().flush();
	}

	@ExceptionHandler(value = { AuthenticationServiceException.class })
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationServiceException authServiceException) throws IOException {
		// 403
		// response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization Failed :
		// " + accessDeniedException.getMessage()); // html

		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().append(ExceptionHelper
				.generateException(new ServiceException(CommonExceptionType.httpUnauthorized, authServiceException)));
		response.getWriter().flush();
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		// 403
		// response.sendError(HttpServletResponse.SC_FORBIDDEN, "Authorization Failed :
		// " + accessDeniedException.getMessage()); // html

		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.getWriter().append(ExceptionHelper
				.generateException(new ServiceException(CommonExceptionType.httpForbidden, accessDeniedException)));
		response.getWriter().flush();
	}

	@ExceptionHandler(value = { Exception.class })
	public void commence(HttpServletRequest request, HttpServletResponse response, Exception exception)
			throws IOException {
		// 500
		// response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal
		// Server Error : " + exception.getMessage()); // html

		response.setContentType("text/html; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		ServiceException se = null;
		if (exception instanceof ServiceException) {
			se = (ServiceException) exception;
		} else {
			se = new ServiceException(CommonExceptionType.internalError, exception);
		}
		response.getWriter().append(ExceptionHelper.generateException(se));
		response.getWriter().flush();
	}

}