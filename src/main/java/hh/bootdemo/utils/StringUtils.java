/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * String Utils
 * 
 * @author yan
 */
public class StringUtils {

	private final static Log log = LogFactory.getLog(StringUtils.class);

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	@SuppressWarnings("rawtypes")
	public static String toListString(List list) {
		StringBuilder sb = new StringBuilder();
		if (list == null || list.isEmpty()) {
			return "";
		}
		int size = list.size();
		for (int i = 0; i < size; i++) {
			sb.append(list.get(i));
			if (i < size - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String toArrayString(String[] strings) {
		StringBuilder sb = new StringBuilder();
		if (strings == null || strings.length == 0) {
			return "";
		}
		int length = strings.length;
		for (int i = 0; i < length; i++) {
			sb.append(strings[i]);
			if (i < length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String toArrayString(Object[] objects) {
		StringBuilder sb = new StringBuilder();
		if (objects == null || objects.length == 0) {
			return "";
		}
		int length = objects.length;
		for (int i = 0; i < length; i++) {
			sb.append(objects[i]);
			if (i < length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static String md5(String str) {
		return encode(str, "md5");
	}

	public static String sha(String str) {
		return encode(str, "sha");
	}

	private static String encode(String string, String algorithm) {
		String encode = "utf8";
		byte[] unencodedPassword = null;
		try {
			unencodedPassword = string.getBytes(encode);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			log.error("algorithm " + string, e);
			return string;
		}

		md.reset();
		md.update(unencodedPassword);
		byte[] encodedPassword = md.digest();

		StringBuilder sb = new StringBuilder();
		for (byte anEncodedPassword : encodedPassword) {
			if ((anEncodedPassword & 0xff) < 0x10) {
				sb.append("0");
			}
			sb.append(Long.toString(anEncodedPassword & 0xff, 16));
		}

		return sb.toString();
	}

}
