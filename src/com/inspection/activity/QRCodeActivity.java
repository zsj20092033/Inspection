package com.inspection.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class QRCodeActivity extends Activity {
	
	private static final String TAG = "QRCodeActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate---------------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);
	}
	
}
