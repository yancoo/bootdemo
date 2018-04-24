package hh.bootdemo.auth.project;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import hh.bootdemo.auth.UserService;
import hh.bootdemo.exception.CommonExceptionType;
import hh.bootdemo.exception.ServiceException;
import hh.bootdemo.utils.StringUtils;

/**
 * 各类项目与第三方接口相关的登录Controller
 * 
 * @author yan
 */
// 改用@RestController，这里有个坑，使用 @Controller时，访问/loginProject会报错"This may be the
// result of an unspecified view"
@RestController
public class ProjectAuthController {
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	UserService userService;

	@GetMapping("loginProject")
	public ModelAndView loginProject(HttpServletRequest request, String username, String password) {
		if (username == null) {
			throw new ServiceException(CommonExceptionType.httpUnauthorized, "null username");
		}
		if (password == null) {
			throw new ServiceException(CommonExceptionType.httpUnauthorized, "null password");
		}

		// load user
		UserDetails userDetails = userService.loadUserByUsername(username);// persisted user
		if (userDetails == null) {
			throw new ServiceException(CommonExceptionType.httpUnauthorized, "null user");
		}

		// validate password
		String encodePassword = StringUtils.sha(password);
		if (!userDetails.getPassword().equals(encodePassword)) {
			AuthenticationException authException = new BadCredentialsException(
					SpringSecurityMessageSource.getAccessor()
							.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
			userService.onLoginFail(request, authException);
			throw new ServiceException(CommonExceptionType.httpUnauthorized, authException);
		}

		// set authentication
		User securityUser = new User(username, encodePassword, userDetails.getAuthorities()); // spring security user
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(securityUser,
				encodePassword, userDetails.getAuthorities());
		result.setDetails(new WebAuthenticationDetails(request));
		SecurityContextHolder.getContext().setAuthentication(result);
		// 是否需要参考AbstractAuthenticationProcessingFilter.successfulAuthentication()广播事件？

		userService.onLoginSuccess(request, (hh.bootdemo.auth.User) userDetails);

		// redirect
		return new ModelAndView("/"); // 重定向到首页、指定的界面、或用于分发的界面
	}

	@GetMapping("exceptionTest")
	public void exceptionTest() {
		throw new ServiceException(CommonExceptionType.configError, new String[] { "a", "b" });

	}
}
