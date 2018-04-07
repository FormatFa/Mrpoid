package com.edroid.common.utils;

/**
 * 异常处理工具类
 * 
 * @author Yichou 2013-9-2
 *
 */
public final class ExceptionUtils {
	public static String exceptionToString(Throwable e) {
		StackTraceElement[] stes = e.getStackTrace();
		
		StringBuilder sb = new StringBuilder("ex: ");
		sb.append(e.getClass().getSimpleName());
		sb.append(": ");
		sb.append(e.getMessage());
		sb.append("\r\n");
		
		for(StackTraceElement se : stes) {
			sb.append("  at: ");
			sb.append(se.getClassName());
			sb.append('.');
			sb.append(se.getMethodName());
			sb.append("() ");
			sb.append(se.getLineNumber());
			
			sb.append("\r\n");
		}
		
		return sb.toString();
	}
}
