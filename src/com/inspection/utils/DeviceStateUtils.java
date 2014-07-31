package com.inspection.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DeviceStateUtils {
	
	/**
	 * 获取当前网络状况
	 * @param context 上下文
	 * @return 网络连接情况
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context!=null) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo!=null) {
				return networkInfo.isAvailable();
			}
		}
		return false;
	}
	
	/**
	 * 查看是否开启了GPS定位
	 * @param context 上下文
	 * @return GPS定位开启状况
	 */
	public static boolean isGpsEnable(Context context) {
		if (context!=null) {
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}		
		return false;
	}
}
