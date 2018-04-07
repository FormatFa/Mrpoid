package com.edroid.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * 网络操作工具集合
 * 
 * @author Yichou
 *
 */
public final class NetUtils {
	public static final int NETTYPE_WIFI=0,
		NETTYPE_WAP=1, //代理
		NETTYPE_NET=2, //直连
		NETTYPE_UNKNOW=3;
	
	public static final int NET_ID_MOBILE=0,                  //移动
	   NET_ID_CN=1,          // 联通gsm
	   NET_ID_CDMA=2,       //联通CDMA
	   NET_ID_NONE=3,       //未插卡
	   NET_ID_OTHER=4;     /*其他网络*/

	public static boolean isNetConnectivity(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
        return (connectManager.getActiveNetworkInfo() != null);
	}
	
	public static boolean isWIFI(Context context) {
		return NetUtils.getNetworkType(context) == NetUtils.NETTYPE_WIFI;
	}
	
	public static int getNetworkType(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null) { 
			// 获取网络连接管理的对象
			NetworkInfo info = connectivity.getActiveNetworkInfo();

			if (info != null && info.isConnected()) {
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					if(info.getType() == ConnectivityManager.TYPE_WIFI){
						Log.d("", "getNetworkType is WIFI.");
						return NETTYPE_WIFI;
					}else if(info.getType() == ConnectivityManager.TYPE_MOBILE){
						String extInfo = info.getExtraInfo();
						if(extInfo != null && extInfo.toLowerCase().contains("wap")){
							Log.d("", "getNetworkType is WAP."); 
							return NETTYPE_WAP;
						}else {
							Log.d("", "getNetworkType is NET.");
							return NETTYPE_NET;
						}
					}
				}
			}
		}
		
		return NETTYPE_UNKNOW;
	}
	
	public static int getNetworkID(Context context) {
		String str = ((TelephonyManager) context.getSystemService("phone"))
				.getSubscriberId();

		if (str == null)
			return NET_ID_OTHER; //返回 NULL 会导致未插卡不能运行

		if ((str.regionMatches(0, "46000", 0, 5))
				|| (str.regionMatches(0, "46002", 0, 5))
				|| (str.regionMatches(0, "46007", 0, 5)))
			return NET_ID_MOBILE;
		else if (str.regionMatches(0, "46001", 0, 5))
			return NET_ID_CN;
		else if (str.regionMatches(0, "46003", 0, 5))
			return NET_ID_CDMA;
		else
			return NET_ID_MOBILE; //返回 NULL 会导致未插卡不能运行
	}
}
