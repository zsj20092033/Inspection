package com.inspection.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inspection.application.InspectionApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity implements OnItemClickListener{

	private static final String TAG = "MainActivity";

	private GridView gridView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gridView = (GridView) findViewById(R.id.mian_gridview);
		SimpleAdapter adapter = new SimpleAdapter(this, getData(),
				R.layout.mainmenu_item,
				new String[] { "imageItem", "textItem" }, new int[] {
						R.id.mainmenu_item_image, R.id.mainmenu_item_text });
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(this);
		InspectionApplication.mLocationClient.start();//启动gps定位
		if(InspectionApplication.mLocationClient != null && InspectionApplication.mLocationClient.isStarted()) {
			InspectionApplication.mLocationClient.requestLocation();
		}
	}

	private List<Map<String, Object>> getData() {
		String[] titles = { "巡检上传", "巡检历史", "二维码", "工作计划", "账号管理", "系统设置",
				"巡检笔记", "录音", "帮助" };
		int[] images = { R.drawable.upload, R.drawable.uploadhistory,
				R.drawable.erweima, R.drawable.work, R.drawable.accunt,
				R.drawable.setting, R.drawable.biji, R.drawable.luyin,
				R.drawable.help };
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < images.length; i++) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("imageItem", images[i]);
			item.put("textItem", titles[i]);
			items.add(item);
		}
		return items;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = null;
		switch (position) {
		case 0:
			intent = new Intent(this, XjUploadActivity.class);
			startActivity(intent);
			break;
		case 1:
			intent = new Intent(this, XjHistoryActivity.class);
			startActivity(intent);
			break;
		case 2:
			intent = new Intent(this, QRCodeActivity.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InspectionApplication.mLocationClient.stop();
	}
	
}
