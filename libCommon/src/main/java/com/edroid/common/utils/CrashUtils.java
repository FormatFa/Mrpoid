package com.edroid.common.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.annotation.SuppressLint;
import android.app.ApplicationErrorReport;
import android.app.ApplicationErrorReport.CrashInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.StringBuilderPrinter;
import android.widget.Toast;


/**
 * 异常处理器
 * 
 * @author Yichou 2013-10-25
 *
 */
public final class CrashUtils {
	private CrashUtils() {
	}
	
	static Context mContext;
	static Object mAM;
	
	
	private static final class AMProxy implements InvocationHandler {
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			method.setAccessible(true);
			String name = method.getName();
			
			if("handleApplicationCrash".equals(name)) {
				/* public void handleApplicationCrash(IBinder app,
	            ApplicationErrorReport.CrashInfo crashInfo) throws RemoteException*/
				
				System.err.println("app crash! tid=" + Thread.currentThread().getId());
				
				final ApplicationErrorReport.CrashInfo info = (CrashInfo) args[1];

				crashToFile(info);
				
				exit();
			}
			
			return method.invoke(mAM, args);
		}
	}
	
	private static void exit() {
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext,
						"~~~>_<~~~ 又崩溃了，即将退出。", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		
		// 退出程序
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
	
	/**
	 * 开启异常捕获
	 */
	public static void enableCrashHandle(Context context) {
		mContext = context.getApplicationContext();
		
		proxyAM();
	}
	
	private static void proxyAM() {
		try {
			Class<?> class1 = Class.forName("android.app.ActivityManagerNative");
			
			Method method = class1.getDeclaredMethod("getDefault", (Class[])null);
			method.setAccessible(true);
			mAM = method.invoke(null, (Object[])null); //获取原始的 

			Class<?> class2 = mAM.getClass();
			
			Object proxy = Proxy.newProxyInstance(mAM.getClass().getClassLoader(),
					mAM.getClass().getInterfaces(),
					new AMProxy());
			
			//static final Singleton<IActivityManager> gDefault
			Field field = class1.getDeclaredField("gDefault"); 
			field.setAccessible(true);
			Object object = field.get(null);
			
			if (object.getClass() == class2) { // 2.2.2~2.3.7
				field.set(null, proxy);
			} else { //4.0 +
				Field field2 = object.getClass().getSuperclass().getDeclaredField("mInstance");
				field2.setAccessible(true);
				field2.set(object, proxy);
			}
			
			System.out.println("crash handler enable success!");
		} catch (Exception e) {
		}
	}

	/**
	 * 收集系统硬件信息
	 * 
	 * @param context
	 * @return
	 */
	public static String collectDeviceInfo(Context context) {
		StringBuilder sb = new StringBuilder(512);
		
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			sb.append("name:").append(context.getResources().getString(pi.applicationInfo.labelRes)).append('\n');
			sb.append("pkg:").append(pi.packageName).append('\n');
			sb.append("versionCode:").append(pi.versionCode).append('\n');
			sb.append("versionName:").append(pi.versionName).append('\n');
		} catch (Exception e) {
		}

		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				sb.append(field.getName()).append('=').append(field.get(null).toString()).append('\n');
			} catch (Exception e) {
			}
		}
		
		return sb.toString();
	}

	/***
	 * 保存错误信息到文件中
	 */
	@SuppressLint("NewApi")
	private static void crashToFile(ApplicationErrorReport.CrashInfo info) {
		StringBuilder sb = new StringBuilder(1024);
		File saveFile = mContext.getFileStreamPath("crash.txt");

		
		if(saveFile.length() == 0) { //第一次创建
			sb.append("-------- device info ---------------\n");
			sb.append(collectDeviceInfo(mContext));
			sb.append('\n');
		}
		
		sb.append("-------- ");
		sb.append(TimeUtils.getDateTimeNow());
		sb.append(" ------------\n");
		
//		try {
//			sb.append('\n');
//			sb.append(info.exceptionClassName);
//			sb.append('\n');
//			sb.append(info.exceptionMessage);
//			sb.append('\n');
//			sb.append(info.stackTrace);
//			sb.append('\n');
//			sb.append(info.throwClassName);
//			sb.append('\n');
//			sb.append("at  ");
//			sb.append(info.throwFileName).append(' ').append(info.throwLineNumber).append(' ').append(info.throwMethodName);
//			sb.append('\n');
//		} catch (Exception e) {
//		}

		info.dump(new StringBuilderPrinter(sb), null);
		
		byte[] data = sb.toString().getBytes();
		
		FileUtils.bytesToFile(saveFile, data, true);
	}
}
