package com.edroid.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加解密工具类
 * 
 * @author Yichou
 * 
 *         <p>
 *         创建日期：2013-4-26 10:19:29
 *         </p>
 */
public final class CipherUtils {
	// 十六进制下数字到字符的映射数组
	private final static char[] hexDigitsChr = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 计算MD5
	 * 
	 * @param args
	 * @return
	 */
	public static String Md5Enc(byte args[]) {
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
	
	public static String Md5Enc16(byte args[]) {
		return Md5Enc(args).substring(8, 24);
	}
}
