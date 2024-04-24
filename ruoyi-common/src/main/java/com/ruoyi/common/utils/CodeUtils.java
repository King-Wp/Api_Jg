package com.ruoyi.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 编码解码工具类
 */
public class CodeUtils {

	/**
	 *
	 * @param obj 需要编码的数据
	 * @param code 对应的字符编码
	 * @return
	 * @throws UnsupportedEncodingException
	 */

	public static String getBase64Code(String obj, String code) throws UnsupportedEncodingException {
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] textByte = obj.getBytes(code);
		String encodedText = encoder.encodeToString(textByte);
		return encodedText;
	}

	/**
	 * 解码base64
	 * @param obj
	 * @param code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String getBase64DeCode(String obj, String code) throws UnsupportedEncodingException {
		Base64.Decoder decoder = Base64.getDecoder();
		return new String(decoder.decode(obj),code);
	}

	/**
	 * 获取url编码
	 * @param obj
	 * @param code 编码
	 * @return
	 */
	public static String getUrlCode(String obj,String code) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(obj,code);
	}

	/**
	 * 获取url编码
	 * @param obj
	 * @return
	 */
	public static String getUrlCode(String obj) throws UnsupportedEncodingException {
		return java.net.URLEncoder.encode(obj,"utf8");
	}

	/**
	 * 获取url编码
	 * @param obj
	 * @param code 解码
	 * @return
	 */
	public static String getUrlDecode(String obj,String code) throws UnsupportedEncodingException {
		return java.net.URLDecoder.decode(obj,code);
	}

	/**
	 * 获取url编码
	 * @param obj
	 * @return
	 */
	public static String getUrlDecode(String obj) throws UnsupportedEncodingException {
		return java.net.URLDecoder.decode(obj,"utf8");
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url ="运维";
		String date ="2022:01:01";
		System.out.println("url "+getUrlCode(url));
		System.out.println("date "+getUrlCode(date));
	}
}
