package com.inspection.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.inspection.utils.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class HandlePhotoActivity extends Activity implements OnClickListener{
	
	private static final String TAG = "HandlePhotoActivity";
	
	private ImageView photo;
	private Button photo_finish_btn;
	private Button photo_camera_btn;
	private String filePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate--------------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		
		photo = (ImageView) findViewById(R.id.photo_image);
		photo_camera_btn = (Button) findViewById(R.id.photo_camera);
		photo_finish_btn = (Button) findViewById(R.id.photo_finish);
		
	    filePath = getIntent().getStringExtra("filePath");
	    Uri uri = Uri.fromFile(new File(filePath));
		try {
			photo.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		photo_camera_btn.setOnClickListener(this);
		photo_finish_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.photo_camera:
			FileUtils.deleteFile(filePath);
			intent.putExtra("isReCamera", true);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;

		case R.id.photo_finish:
			intent.putExtra("isReCamera", false);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		FileUtils.deleteFile(filePath);
		finish();
	}
	
}
