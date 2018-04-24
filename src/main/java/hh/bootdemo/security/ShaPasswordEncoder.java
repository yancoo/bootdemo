package hh.bootdemo.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import hh.bootdemo.utils.StringUtils;

public class ShaPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(CharSequence password) {
		return StringUtils.sha(password.toString());
	}

	@Override
	public boolean matches(CharSequence input, String expected) {
		return expected.equals(encode(input));
	}

}