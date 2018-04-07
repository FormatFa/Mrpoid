package com.edroid.common.utils;

/**
 * 字符串处理工具
 * 
 * @author Yichou 2013-8-7
 *
 */
public final class StringUtils {

	public static boolean isEmpty(String s) {
		return (s == null || s.length() == 0);
	}

	public static boolean isEmpty(CharSequence s) {
		return (s == null || s.length() == 0);
	}
	
	public static int toInt(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
		}
		return 0;
	}
	
	/**
	 * . 替换为 /
	 * 
	 * @param pkg
	 * @return
	 */
	public static String converPackgeName(String pkg) {
		char[] chars = new char[pkg.length()+2];
		pkg.getChars(0, pkg.length(), chars, 1);
		for(int i=0; i<chars.length; ++i){
			if(chars[i] == '.')
				chars[i] = '/';
		}
		chars[0] = '"';
		chars[chars.length - 1] = '"';
		
		return new String(chars);
	}
}
