/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hh.bootdemo.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author yan
 */
public class StringUtils {

	private final static Log log = LogFactory.getLog(StringUtils.class);

	private final static String symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

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

	/**
	 * 指定串 按规则 匹配出<b>单一行</b>第一个内容
	 * <li>需要匹配多个内容单独使用Pattern解析</li>
	 *
	 * @param content
	 *            字符串
	 * @param regex
	 *            必须携带( )的正则表达式
	 * @return 匹配到的单一行第一个内容，null未匹配到
	 */
	public static String findRegexString(String content, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			String match = matcher.group(1);
			return match;
		}
		return null;
	}

	/**
	 * 指定串 按规则 匹配出<b>跨多行</b>第一个内容
	 * <li>需要匹配多个内容单独使用Pattern解析</li>
	 *
	 * @param content
	 *            字符串
	 * @param regex
	 *            必须携带( )的正则表达式，需要注意限定结束符（别多匹配了）
	 * @return 匹配到的跨多行第一个内容，null未匹配到
	 */
	public static String findMultiLineRegexString(String content, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			String match = matcher.group(1);
			return match;
		}
		return null;
	}

	/**
	 * 解析xml 为document对象
	 *
	 * @param xmlStr
	 * @return Document
	 */
	public static Document xmlStrToDocument(String xmlStr) {
		StringReader sr = new StringReader(xmlStr);
		InputSource is = new InputSource(sr);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
		} catch (Exception ex) {
			Logger.getLogger(StringUtils.class.getName()).log(Level.SEVERE, null, "解析xml错误");
		}
		return doc;
	}

	/**
	 * map转xml格式字符串
	 *
	 * @param map
	 *            map
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String MapToXML(Map map) {
		StringBuffer sb = new StringBuffer();
		mapToXML2(map, sb);
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	private static void mapToXML2(Map map, StringBuffer sb) {
		Set set = map.keySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = map.get(key);
			if (null == value) {
				value = "";
			}
			if (value.getClass().getName().equals("java.util.ArrayList")) {
				ArrayList list = (ArrayList) map.get(key);
				for (int i = 0; i < list.size(); i++) {
					sb.append("<" + key + ">");
					HashMap hm = (HashMap) list.get(i);
					mapToXML2(hm, sb);
					sb.append("</" + key + ">");
				}
			} else if (value instanceof HashMap) {
				sb.append("<" + key + ">");
				mapToXML2((HashMap) value, sb);
				sb.append("</" + key + ">");
			} else {
				sb.append("<" + key + ">" + value + "</" + key + ">");
			}

		}
	}

	/**
	 * jdk方法 对象转Xml
	 *
	 * @param obj
	 *            传入对象
	 * @param cla
	 *            对象.class
	 * @return String
	 */
	@SuppressWarnings("rawtypes")
	public static String entityToXml(Object obj, Class cla) {
		// 字符流，可以用其回收在字符串缓冲区中的输出来构造字符串。
		StringWriter sw = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(cla);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(obj, sw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	/**
	 * 字符串首字母大写
	 *
	 * @param string
	 * @return String
	 */
	public static String firstLetterToUpperCase(String string) {
		if (StringUtils.isEmpty(string)) {
			return string;
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * 字符串首字母小写
	 *
	 * @param string
	 * @return String
	 */
	public static String firstLetterToLowerCase(String string) {
		if (StringUtils.isEmpty(string)) {
			return string;
		}
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	/**
	 * 如果为空 返回-1
	 *
	 * @param id
	 * @return Long
	 */
	public static Long ifNullReturnNegativeOne(Long id) {
		if (id == null || id == -1) {
			id = new Long(-1);
		}
		return id;
	}

	/**
	 * 获取一个x位的随机字符串
	 */
	public static String getRandomString(int size) {
		int length = symbols.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			int index = new Random().nextInt(length);
			sb.append(symbols.charAt(index));
		}
		return sb.toString();
	}

	/**
	 * 判断字符是否是中文
	 *
	 * @param c
	 *            字符
	 * @return 是否是中文
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 判断字符串是否是乱码
	 * <p>
	 * 对于Character.isLetterOrDigit 对于增补字符判断有误
	 * </p>
	 *
	 * @param strName
	 *            字符串
	 * @return 是否是乱码 只要该字符串中包含有乱码则直接判断该字符串为乱码
	 */
	public static boolean isMessyCode(String strName) {
		Pattern p = Pattern.compile("\\s*|t*|r*|n*");
		Matcher m = p.matcher(strName);
		String after = m.replaceAll("");
		String temp = after.replaceAll("\\p{P}", "");
		char[] ch = temp.trim().toCharArray();
		float chLength = ch.length;
		float count = 0;
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];

			if (!Character.isLetterOrDigit(c)) {
				if (!isChinese(c)) {
					count = count + 1;
				}
			}
		}
		float result = count / chLength;
		if (result > 0.4) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查字符串中是否匹配规则
	 *
	 * @param rule
	 *            正则表达式
	 * @param str
	 *            要检查的字符串
	 * @return
	 */
	public static boolean matchRule(String rule, String str) {
		if (rule == null || rule.length() == 0) {
			return true;
		}
		rule = unescapeJava(rule);
		Pattern pattern = Pattern.compile(rule, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String unescapeJava(String str) {
		if (str == null) {
			return null;
		}
		try {
			StringWriter writer = new StringWriter(str.length());
			unescapeJava(writer, str);
			return writer.toString();
		} catch (IOException ioe) {
			// this should never ever happen while writing to a StringWriter
			throw new RuntimeException(ioe);
		}
	}

	public static void unescapeJava(Writer out, String str) throws IOException {
		if (out == null) {
			throw new IllegalArgumentException("The Writer must not be null");
		}
		if (str == null) {
			return;
		}
		int sz = str.length();
		StringBuffer unicode = new StringBuffer(4);
		boolean hadSlash = false;
		boolean inUnicode = false;
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			if (inUnicode) {
				// if in unicode, then we're reading unicode
				// values in somehow
				unicode.append(ch);
				if (unicode.length() == 4) {
					// unicode now contains the four hex digits
					// which represents our unicode character
					try {
						int value = Integer.parseInt(unicode.toString(), 16);
						out.write((char) value);
						unicode.setLength(0);
						inUnicode = false;
						hadSlash = false;
					} catch (NumberFormatException nfe) {
						throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
					}
				}
				continue;
			}
			if (hadSlash) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
				case '\\':
					out.write('\\');
					break;
				case '\'':
					out.write('\'');
					break;
				case '\"':
					out.write('"');
					break;
				case 'r':
					out.write('\r');
					break;
				case 'f':
					out.write('\f');
					break;
				case 't':
					out.write('\t');
					break;
				case 'n':
					out.write('\n');
					break;
				case 'b':
					out.write('\b');
					break;
				case 'u': {
					// uh-oh, we're in unicode country....
					inUnicode = true;
					break;
				}
				default:
					out.write(ch);
					break;
				}
				continue;
			} else if (ch == '\\') {
				hadSlash = true;
				continue;
			}
			out.write(ch);
		}
		if (hadSlash) {
			// then we're in the weird case of a \ at the end of the
			// string, let's output it anyway.
			out.write('\\');
		}
	}
}
