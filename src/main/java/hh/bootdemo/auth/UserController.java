package hh.bootdemo.auth;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	private final Log log = LogFactory.getLog(getClass());

	@GetMapping("currentUser")
	public UserDetails user(Principal user) {
		if (user instanceof UsernamePasswordAuthenticationToken) {
			return (UserDetails) (((UsernamePasswordAuthenticationToken) user).getPrincipal());
		}
		return null;
	}

	@PostMapping("admin/addRole")
	public void addRole(@RequestBody User user) {
		log.info(user);
	}
}
