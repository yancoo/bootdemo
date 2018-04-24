package hh.bootdemo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hh.bootdemo.auth.User;
import hh.bootdemo.auth.UserService;
import hh.bootdemo.security.filter.AccessLogFilter;
import hh.bootdemo.security.filter.ValidationCodeFilter;

/**
 * http://www.jb51.net/article/131234.htm,
 * http://www.jb51.net/article/110528.htm
 * 
 * @author yan
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	UserService userService;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// 认证授权
		http.authorizeRequests().antMatchers("/login*", "/signup").permitAll() // 未登录可访问
				.antMatchers("/admin/**").hasRole("ADMIN") // admin路径要求有ADMIN权限
				.anyRequest().authenticated() // 其他请求均要求先登录
				.and().formLogin()// 支持form登录，form action可通过loginProcessingUrl("url")设置
				.successHandler(new AuthenticationSuccessHandler() {// 登录成功
					public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
							Authentication authentication) throws IOException, ServletException {
						response.getWriter().append("{\"login\":success}");
						response.getWriter().flush();

						User user = (User) authentication.getPrincipal();
						userService.onLoginSuccess(request, user);
					}
				}).failureHandler(new AuthenticationFailureHandler() {// 登录失败
					public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
							AuthenticationException authException) throws IOException, ServletException {
						// 不redirect到/login?error，直接throw，由ExceptionAuthticationEntryPoint响应异常json
						userService.onLoginFail(request, authException);
						throw authException;
					}
				}).and().httpBasic() // 支持basic http登录， "BAISC base64(username:password)"格式，通常用不到
				// .and().logout().logoutRequestMatcher(new
				// AntPathRequestMatcher("/logout")).permitAll()//
				// 可访问登出，logoutRequestMatcher允许GET访问/logout
				.and().logout().permitAll()// 可访问登出，disable csrf后允许GET访问/logout
				.and().exceptionHandling().authenticationEntryPoint(new ExceptionAuthenticationEntryPoint())// 自行控制异常（未登录时不显示登录界面、登录后跳转取消）
				.and().csrf().disable();// disable csrf，post操作不依赖csrf

		// add filter
		http.addFilterAt(new AccessLogFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterAt(new ValidationCodeFilter(), AccessLogFilter.class);

	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// 内存认证
		// auth.inMemoryAuthentication().passwordEncoder(new
		// ShaPasswordEncoder()).withUser("user").password("password")
		// .roles("USER");

		// dao认证
		// DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		// provider.setPasswordEncoder(new ShaPasswordEncoder());
		// provider.setUserDetailsService(userService);
		// auth.authenticationProvider(provider);
		// 等价于下面的：
		auth.userDetailsService(userService).passwordEncoder(new ShaPasswordEncoder());

	}

}