package hh.bootdemo.user;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import hh.bootdemo.auth.User;
import hh.bootdemo.utils.StringUtils;

public class UserTest {

	@Test
	public void readJson() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = "{\"id\": 3, \"roles\":[{\"id\":3}, {\"id\":2}]}";
		User user = (User) mapper.readValue(json, User.class);
		System.out.println(user);
	}

	@Test
	public void encodePassword() throws Exception {
		System.out.println("dev, " + StringUtils.sha("dev"));
		System.out.println("admin, " + StringUtils.sha("admin"));
		System.out.println("user, " + StringUtils.sha("user"));
	}
}
