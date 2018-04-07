package com.edroid.common.utils;

import java.lang.reflect.Method;

/**
 * 反射工具
 * 
 * @author Yichou 2013-7-15
 * 
 * @date 2013-8-13
 */
public final class ReflectUtils {
	public static final int SUCCESS = 1;
	public static final int FAILUE = 0;
	
	
	/**
	 * 调用不带返回值的方法
	 * 
	 * @param clazz
	 * @param object
	 * @param name
	 * @param paramTypes 参数类型列表，无传 null
	 * @param params 参数列表，无传 null
	 * 
	 * @return 成功返回 SUCCESS， 失败返回 FAILUE
	 */
	public static final int tryCallVoidMethod(Class<?> clazz, Object object, String name, Class<?>[] paramTypes, Object[] params) {
		try {
			Method method = clazz.getDeclaredMethod(name, (Class[])paramTypes);
			method.setAccessible(true);
			method.invoke(object, (Object[])params);
			
			return SUCCESS;
		} catch (Exception e) {
			System.err.println("tryCallVoidMethod Err:" 
					+ e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n"
					+ name);
		}
		
		return FAILUE;
	}

	public static final int tryCallVoidMethod(Object object, String name, Class<?>[] paramTypes, Object[] params) {
		return tryCallVoidMethod(object.getClass(), object, name, paramTypes, params);
	}
	
	public static final int tryCallStaticVoidMethod(Class<?> clazz, String name, Class<?>[] paramTypes, Object[] params) {
		return tryCallVoidMethod(clazz, null, name, paramTypes, params);
	}

	/**
	 * 调用方法并获取返回值
	 * 
	 * @param clazz 
	 * @param object 
	 * @param name
	 * @param paramTypes 参数类型列表，无传 null
	 * @param params 参数列表，无传 null
	 * 
	 * @return 成功返回 object，失败返回 null
	 */
	public static final Object tryCallMethod(Class<?> clazz, Object object, String name, Class<?>[] paramTypes, Object[] params) {
		try {
			Method method = clazz.getDeclaredMethod(name, (Class[])paramTypes);
			method.setAccessible(true);
			return method.invoke(object, (Object[])params);
		} catch (Exception e) {
			System.err.println("tryCallVoidMethod Err:" 
					+ e.getClass().getCanonicalName() + ": " + e.getMessage() + "\n"
					+ name);
		}
		
		return null;
	}
	
	public static final Object tryCallMethod(Object object, String name, Class<?>[] paramTypes, Object[] params){
		return tryCallMethod(object.getClass(), object, name, paramTypes, params);
	}
	
	public static final Object tryCallStaticMethod(Class<?> clazz, String name, Class<?>[] paramTypes, Object[] params){
		return tryCallMethod(clazz, null, name, paramTypes, params);
	}
}
