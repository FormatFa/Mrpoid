package com.edroid.common.utils;

import java.io.File;

import android.content.Context;

/**
 * 数据本地存储工具
 * 
 * @author Yichou 2013-5-8
 *
 */
public final class LocalDataUtils {
	
	public static String genName(String key) {
		return SecurityUtils.md516(key.getBytes());
	}
	
	public static void toPrivate(Context context, String name, String string) {
		File file = context.getFileStreamPath(name);
		FileUtils.bytesToFile(file, string.getBytes());
	}
	
	public static String getStringFromPrivate(Context context, String name) {
		File file = context.getFileStreamPath(name);
		byte[] buf = FileUtils.fileToBytes(file);
		if(buf != null)
			return new String(buf);
		return null;
	}
}
