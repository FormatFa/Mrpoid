package com.edroid.common.utils;

import java.lang.reflect.Field;

/**
 * android 内部ID
 * 
 * @author Yichou 2013-8-16
 *
 */
public final class InternalID {
	public static int id_icon, id_text, id_title;


	static {
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$id");
			
			Field field = clazz.getField("icon");
			field.setAccessible(true);
			id_icon = field.getInt(null);

			field = clazz.getField("text");
			field.setAccessible(true);
			id_text = field.getInt(null);
			
			field = clazz.getField("title");
			field.setAccessible(true);
			id_title = field.getInt(null);
		} catch (Exception e) {
		}
	}
}
