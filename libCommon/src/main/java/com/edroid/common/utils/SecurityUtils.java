package com.edroid.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 安全相关工具类 各种加解密
 * 
 * @author Yichou 2013-4-26
 * 
 */
public final class SecurityUtils {
	private final static char[] hexDigitsChr = "0123456789abcdef".toCharArray();

	/**
	 * 计算MD5 32位
	 */
	public static String md532(byte args[]) {
		// 使用MD5创建MessageDigest对象
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(args);

			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;

			for (int i = 0; i < j; i++) {
				byte b = md[i];

				// 将没个数(int)b进行双字节加密
				str[k++] = hexDigitsChr[b >> 4 & 0xf];
				str[k++] = hexDigitsChr[b & 0xf];
			}

			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * 计算MD5 16位 取32位的 8-24位
	 */
	public static String md516(byte args[]) {
		return md532(args).substring(8, 24);
	}
}
