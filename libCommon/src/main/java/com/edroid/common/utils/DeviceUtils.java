package com.edroid.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

import android.Manifest.permission;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * 设备信息获取工具
 * 
 * @author Yichou 2013-12-16
 *
 */
public final class DeviceUtils {
	public static final int NET_ID_MOBILE = 0, // 移动
		NET_ID_CN = 1, // 联通gsm
		NET_ID_CDMA = 2, // 联通CDMA
		NET_ID_NONE = 3, // 未插卡
		NET_ID_OTHER = 4; /* 其他网络 */

	public static final int	NET_OPT_UNKNOW = 0, 
		NET_OPT_MOBILE = 1, // 中国移动
		NET_OPT_TELECOM = 2, // 中国电信
		NET_OPT_UNICOM = 3;// 中国联通


	/**
	 * 电话状态是否可读
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPhoneStateReadable(Context context) {
		return (PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(
				permission.READ_PHONE_STATE, context.getPackageName()));
	}

	/**
	 * 获取手机IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		if (isPhoneStateReadable(context)) {
			TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			if (phoneManager != null) {
				String ret = phoneManager.getSubscriberId();
				if (ret != null && ret.length() > 0)
					return ret;
			}
		}

		return null;
	}

	/**
	 * 获取网络运营商
	 * 
	 * @param context
	 * @return
	 */
	public static int getNetOpt(Context context) {
		if (!isPhoneStateReadable(context))
			return NET_OPT_UNKNOW;

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (TextUtils.isEmpty(imsi) || imsi.length() < 8)
			return NET_OPT_UNKNOW;

		int mcc = context.getResources().getConfiguration().mcc;
		if (mcc == 0)
			mcc = Integer.valueOf(imsi.substring(0, 3));

		int mnc = context.getResources().getConfiguration().mnc;
		if (mnc == 0)
			mnc = Integer.valueOf(imsi.substring(4, 5));

		if (mcc != 460)
			return NET_OPT_UNKNOW;

		switch (mnc) {
		case 0:
		case 2:
		case 7:
			return NET_OPT_MOBILE;
		case 1:
			return NET_OPT_UNICOM;
		case 3:
			return NET_OPT_TELECOM;
		}

		return NET_OPT_UNKNOW;
	}
	
	/**
	 * @return SD卡是否已挂载
	 */
	public static boolean isSDMounted() {
		return Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * @return [0]:total bytes [1]:available bytes [2]:free bytes
	 */
	public static long[] getSdCardSizeInfo() {
		if (isSDMounted()) {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory() + File.separator);
			if(stat != null) {
				long[] ret = new long[5];
				ret[0] = stat.getBlockCount() * stat.getBlockSize();
				ret[1] = stat.getAvailableBlocks() * stat.getBlockSize();
				ret[2] = stat.getFreeBlocks() * stat.getBlockSize();
			}
		} 
		
		return null;
	}
	
	/**
	 * 获取本机本地 ip （非外网）ip v4
	 * 
	 * @return 失败返回 null
	 */
//	public static String getLocalIpV4() {
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()) {
//						String ip = inetAddress.getHostAddress();
//						if(InetAddressUtils.isIPv4Address(ip))
//							return ip;
//					}
//				}
//			}
//		} catch (SocketException ex) {
//			ex.printStackTrace();
//		}
//		
//		return null;
//	}
	
	/**
	 * 获取系统总内存
	 * @param context
	 * @return
	 */
	public static long getRamTotal() {
		BufferedReader br = null;
		try {
			// 系统内存信息文件
			br = new BufferedReader(new FileReader("/proc/meminfo"), 8192);
			
			// 读取第一行 系统总内存大小
			String str2 = br.readLine().split("\\s+")[1];
			// 获得系统总内存 单位是KB 乘以1024转换为Byte
			return Integer.valueOf(str2).intValue() * 1024;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
			}
		}
		
		return 0;
	}

	/**
	 * 获取当前可用内存
	 * @param context
	 * @return
	 */
	public static long getRamFree(Context context) {
		try{
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			MemoryInfo memInfo = new MemoryInfo();
			am.getMemoryInfo(memInfo);
			return memInfo.availMem;
		} catch (Exception e) {
		}
		
		return 0;
	}

	/**
	 * 模拟器判断
	 * @param tm
	 * @return
	 */
	public static boolean isEmulator(TelephonyManager tm) {
		return ("000000000000000".equals(tm.getDeviceId()) 
				|| Build.MODEL.equals("sdk")) 
				|| (Build.MODEL.equals("google_sdk"));
	}
	
	/**
	 * 获取CPU最大频率(单位KHZ)
	 * @return
	 */
//	public static String getMaxCpuFreq() {
//		final String[] args = { 
//				//命令行
//				"/system/bin/cat", 
//				//存储最大频率的文件的路径
//				"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" 
//			};
//
//		InputStream is = null;
//		try {
//			ProcessBuilder cmd = new ProcessBuilder(args);
//			Process process = cmd.start();
//			
//			is = process.getInputStream();
//			byte[] buf = new byte[128];
//			ByteArrayBuffer ba = new ByteArrayBuffer(256);
//			int read = 0;
//			
//			while ((read = is.read(buf)) != -1) {
//				ba.append(buf, 0, read);
//			}
//			
//			return new String(ba.buffer(), 0, ba.length());
//		} catch (Exception ex) {
//		} finally {
//			try {
//				is.close();
//			} catch (Exception e) {
//			}
//		}
//
//		return "N/A";
//	}

	/**
	 * 获取CPU名字
	 */
	public static String getCpuName() {
		try {
			FileReader fr = new FileReader("/proc/cpuinfo");
			BufferedReader br = new BufferedReader(fr);
			String text = br.readLine();
			String[] array = text.split(":\\s+", 2);
			return array[1];
		} catch (Exception e) {
		}
		
		return "N/A";
	}

	/**
	 * 获取核心数
	 * @return
	 */
	public static int getNumCores() {
		try {
			File dir = new File("/sys/devices/system/cpu/");
			File[] files = dir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return Pattern.matches("cpu[0-9]", pathname.getName());
				}
			});
			return files.length;
		} catch (Exception e) {
		}

		return 1;
	}
}
