package com.edroid.common.utils;

/**
 * 16进制工具类
 * 
 * @author YYichou 2013-5-8
 *
 */
public class HexUtils {

	public static String bytesToHexString(byte[] data) {
		return bytesToHexString(data, 0, data.length);
	}
	
	public static String bytesToHexString(byte[] data, int offset, int len) {
		char[] str = new char[len*2];
		
		final int END = offset + len;
		for (int j=0, i = offset; i < END; i++) {
			// 将没个数(int)b进行双字节加密
			byte b = data[i];
			
			str[j++] = hexDigitsChr[b >> 4 & 0xf];
			str[j++] = hexDigitsChr[b & 0xf];
		}
		
		return new String(str);
	}
	
	// 十六进制下数字到字符的映射数组
	private final static char[] hexDigitsChr = "0123456789abcdef".toCharArray();

	
	public static void main(String[] args) {
		byte[] data = {0x12, 0x34, 0x56, 0x78};
		
		System.out.println(bytesToHexString(data, 0, 4));
	}
}
