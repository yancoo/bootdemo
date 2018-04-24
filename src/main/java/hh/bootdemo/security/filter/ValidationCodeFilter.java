package hh.bootdemo.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.filter.GenericFilterBean;

import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ExceptionHelper;
import hh.bootdemo.exception.ServiceException;

public class ValidationCodeFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		HttpSession session = request.getSession(false);

		if (session != null) {
			String validationCode = (String) session.getAttribute("validationCode");
			if (logger.isDebugEnabled()) {
				logger.debug("Requested session ID " + request.getRequestedSessionId() + ", " + validationCode);
			}
			boolean match = true;
			if (!match) {
				response.setContentType("text/html; charset=UTF-8");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().append(ExceptionHelper
						.generateException(new ServiceException(CommonExceptionType.validationCodeError)));
				response.getWriter().flush();
				return;
			}
		}

		chain.doFilter(request, response);
	}

}
