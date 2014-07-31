package com.inspection.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class DeviceStateUtils {
	
	/**
	 * ��ȡ��ǰ����״��
	 * @param context ������
	 * @return �����������
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
	 * �鿴�Ƿ�����GPS��λ
	 * @param context ������
	 * @return GPS��λ����״��
	 */
	public static boolean isGpsEnable(Context context) {
		if (context!=null) {
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}		
		return false;
	}
}
