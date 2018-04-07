package com.edroid.common.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络操作工具集合
 * 
 * @author Yichou
 * 
 */
public final class SysUtils {
	public static final int NETTYPE_WIFI = 0, NETTYPE_WAP = 1, // 代理
			NETTYPE_NET = 2, // 直连
			NETTYPE_UNKNOW = 3;

	public static final int NET_ID_MOBILE = 0, // 移动
			NET_ID_CN = 1, // 联通gsm
			NET_ID_CDMA = 2, // 联通CDMA
			NET_ID_NONE = 3, // 未插卡
			NET_ID_OTHER = 4; /* 其他网络 */

	public static final int NETWORK_TYPE_GSM = 1;
	public static final int NETWORK_TYPE_CDMA = 2;
	public static final int NETWORK_TYPE_CDMA2000 = 3;
	public static final int NETWORK_TYPE_WCDMA = 4;
	public static final int NETWORK_TYPE_TDSCDMA = 5;
	public static final int NERWORK_TYPE_WIFI = 0;

	enum Operator {
		UNKNOW, MOBILE, // 中国移动
		TELECOM, // 中国电信
		UNICOM // 中国联通
	}

	enum Standard {
		TYPE_XX, // 未知网络
		TYPE_2G, // 2G网络
		TYPE_3G // 3G网络
	}

	public static NetworkInfo getActiveNetworkInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) {
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			
			if (info != null && info.isAvailable()) // 有联网 且 可以联网
				return info;
			
			// if (info != null
			// && info.isConnected() //这样是判断当前活动网络是不是正在联网数据收发
			// && info.getState() == NetworkInfo.State.CONNECTED)
			// {
			// return info;
			// }
		}
		
		return null;
	}
	
	public static boolean isNetAvailable(Context context) {
		return (getActiveNetworkInfo(context) != null);
	}

	public static boolean isWifi(Context context) {
		return SysUtils.getNetworkType(context) == SysUtils.NETTYPE_WIFI;
	}

	public static int getNetworkType(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);

		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return NETTYPE_WIFI;
			} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				String extInfo = info.getExtraInfo();
				if (extInfo != null && extInfo.toLowerCase(Locale.getDefault()).contains("wap")) {
					return NETTYPE_WAP;
				} else {
					return NETTYPE_NET;
				}
			}
		}

		return NETTYPE_UNKNOW;
	}

	/**
	 * 判断当前连接的网络是 wifi或者3g
	 * 
	 * @return true 你不用担心用户流量了
	 */
	public static boolean isWifiOr3g(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);

		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			} else {
				return (getNetworkStandard(context) == Standard.TYPE_3G);
			}
		}

		return false;
	}

	public static boolean is3g(Context context) {
		return (getNetworkStandard(context) == Standard.TYPE_3G);
	}

	/**
	 * 获取网络类型，返回字符串
	 * 
	 * <li>wifi</li> <li>wap</li> <li>net</li> <li>non</li>
	 * 
	 * @param context
	 * @return
	 */
	public static String getStringNetworkType(Context context) {
		int ret = getNetworkType(context);
		if (ret == NETTYPE_WIFI)
			return "wifi";
		else if (ret == NETTYPE_WAP)
			return "wap";
		else if (ret == NETTYPE_NET)
			return "net";
		else
			return "non";
	}

	/**
	 * 获取当前连接的网络的 apn 名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkApn(Context context) {
		NetworkInfo info = getActiveNetworkInfo(context);
		if (info != null) {
			String apn = info.getExtraInfo();
			if (apn != null && apn.length() > 0) {
				return apn.toLowerCase(Locale.getDefault());
			}
		}

		return null;
	}

	public static int getNetworkID(Context context) {
		String str = getImsi(context);

		if (str == null)
			return NET_ID_OTHER; // 返回 NULL 会导致未插卡不能运行

		if ((str.regionMatches(0, "46000", 0, 5)) || (str.regionMatches(0, "46002", 0, 5))
				|| (str.regionMatches(0, "46007", 0, 5)))
			return NET_ID_MOBILE;
		else if (str.regionMatches(0, "46001", 0, 5))
			return NET_ID_CN;
		else if (str.regionMatches(0, "46003", 0, 5))
			return NET_ID_CDMA;
		else
			return NET_ID_MOBILE; // 返回 NULL 会导致未插卡不能运行
	}

	/**
	 * 获取手机IMSI
	 * 
	 * @param context
	 * @return
	 */
	public static String getImsi(Context context) {
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (phoneManager != null) {
			String ret = phoneManager.getSubscriberId();
			if (ret != null && ret.length() > 0)
				return ret;
		}

		return null;
	}

	public static String getPhoneNumber(Context context) {
		TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		if (phoneManager != null) {
			String ret = phoneManager.getLine1Number();
			if (ret != null && ret.length() > 0)
				return ret;
		}

		return null;
	}

	/**
	 * 电话状态是否可读
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isPhoneStateReadable(Context context) {
		PackageManager pm = context.getPackageManager();
		String pkgName = context.getPackageName();
		int readable = pm.checkPermission(permission.READ_PHONE_STATE, pkgName);

		return readable == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 获取网络运营商
	 * 
	 * @param context
	 * @return
	 */
	private static Operator getNetworkOperator(Context context) {
		if (!isPhoneStateReadable(context))
			return Operator.UNKNOW;

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi == null || imsi.length() < 10)
			return Operator.UNKNOW;

		int mcc = context.getResources().getConfiguration().mcc;
		if (mcc == 0)
			mcc = Integer.valueOf(imsi.substring(0, 3));
		int mnc = context.getResources().getConfiguration().mnc;
		if (mnc == 0)
			mnc = Integer.valueOf(imsi.substring(4, 5));
		if (mcc != 460)
			return Operator.UNKNOW;

		switch (mnc) {
		case 0:
		case 2:
		case 7:
			return Operator.MOBILE;
		case 1:
			return Operator.UNICOM;
		case 3:
			return Operator.TELECOM;
		default:
			return Operator.UNKNOW;
		}
	}

	/**
	 * 获取网络选项
	 * 
	 * @param context
	 * @return
	 */
	private static Standard getNetworkStandard(Context context) {
		if (!isPhoneStateReadable(context))
			return Standard.TYPE_XX;

		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		switch (tm.getNetworkType()) {
		case TelephonyManager.NETWORK_TYPE_UMTS:
		case TelephonyManager.NETWORK_TYPE_HSDPA:
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
		case TelephonyManager.NETWORK_TYPE_HSUPA:
		case TelephonyManager.NETWORK_TYPE_HSPA:
		case 15:
			return Standard.TYPE_3G;

		case TelephonyManager.NETWORK_TYPE_GPRS:
		case TelephonyManager.NETWORK_TYPE_EDGE:
		case TelephonyManager.NETWORK_TYPE_CDMA:
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			return Standard.TYPE_2G;

		default:
			return Standard.TYPE_XX;
		}
	}

	/**
	 * 获取网络类型 = 运营商 + 选项
	 * 
	 * @param context
	 * @return
	 */
	public static final int getNetWorkType(Context context) {
		Operator operator = getNetworkOperator(context);
		Standard standard = getNetworkStandard(context);
		if (standard == Standard.TYPE_2G && (operator == Operator.MOBILE || operator == Operator.UNICOM))
			return NETWORK_TYPE_GSM;
		if (standard == Standard.TYPE_2G && operator == Operator.TELECOM)
			return NETWORK_TYPE_CDMA;
		if (standard == Standard.TYPE_3G && operator == Operator.MOBILE)
			return NETWORK_TYPE_TDSCDMA;
		if (standard == Standard.TYPE_3G && operator == Operator.UNICOM)
			return NETWORK_TYPE_WCDMA;
		if (standard == Standard.TYPE_3G && operator == Operator.TELECOM)
			return NETWORK_TYPE_CDMA2000;
		return 0;
	}
	
	public static Address getAddress(Context context) {
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		// 取得效果最好的criteria
		String provider = manager.getBestProvider(criteria, true);
		if (provider == null) return null;
		
		// 得到坐标相关的信息
		Location location = manager.getLastKnownLocation(provider);
		if (location == null) return null;

		// 更具地理环境来确定编码
		Geocoder gc = new Geocoder(context, Locale.getDefault());
		try {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			// 取得地址相关的一些信息\经度、纬度
			List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
			if (addresses.size() > 0) {
				return addresses.get(0);
			}
		} catch (IOException e) {
		}
		
		return null;
	}
}
