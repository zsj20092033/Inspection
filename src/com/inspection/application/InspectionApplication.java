package com.inspection.application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Application;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class InspectionApplication extends Application {
	
	private static final String TAG = "InspectionApplication";
	
	public static float density;
	public static String imei;
	public static double longitude; //经度
	public static double latitude;  //纬度
	public static String time;
	public static LocationClient mLocationClient = null;
	public BDLocationListener bdLocationListener = new MyLocationListener();
			
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate--------------------");
		// 获取屏幕密度
		DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
		density = dm.density;
		
		imei = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId(); 
		mLocationClient = new LocationClient(getApplicationContext());
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setLocationMode(LocationMode.Hight_Accuracy);
		clientOption.setOpenGps(true);
		clientOption.setCoorType("bd09ll");
		clientOption.setScanSpan(2000); //2秒钟定位一次
		mLocationClient.setLocOption(clientOption);
		mLocationClient.registerLocationListener(bdLocationListener);
	}


	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if(location!=null) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
				time = location.getTime();
			}			
		}		
	}
	
}
