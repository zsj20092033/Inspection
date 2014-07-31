package com.inspection.activity;

import com.inspection.utils.DeviceStateUtils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements android.view.View.OnClickListener{

	private static final String TAG = "LoginActivity";
	private Button btn_login = null;
	private EditText et_username = null;
	private EditText et_pwd = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate-----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		btn_login = (Button) findViewById(R.id.btnLogin);
		et_username = (EditText) findViewById(R.id.etUserName);
		et_pwd = (EditText) findViewById(R.id.etPwd);
		
		SharedPreferences sp = this.getSharedPreferences("user", Context.MODE_PRIVATE);
		String username = sp.getString("username", "");
		String password = sp.getString("password", "");
		
		et_username.setText(username);
		et_pwd.setText(password);
		btn_login.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "onResume-----------------------");
		super.onResume();
		if(!DeviceStateUtils.isGpsEnable(this)) {
			Builder builder = new Builder(this);
			builder.setMessage("GPS��λδ�򿪣�����ȷ����ϵͳ���ò˵��н������ã�").setTitle("��ʾ");
			builder.setPositiveButton("ȷ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent intent = new Intent();
					intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			}).setNegativeButton("ȡ��", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();			
		}
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause-----------------------");
		super.onPause();
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			String username = et_username.getText().toString().trim();
			String password = et_pwd.getText().toString().trim();
			if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				Toast.makeText(this, "�û��������벻��Ϊ��", Toast.LENGTH_SHORT).show();
			} else {
				if("admin".equals(username) && "123".equals(password)) {
					Log.i(TAG, "��½�ɹ������ڱ����û���������----------------");
					SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
					Editor editor = sp.edit();
					editor.putString("username", username);
					editor.putString("password", password);
					editor.commit();
					Toast.makeText(this, "��½�ɹ�", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(this, "��¼ʧ��", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}
	}
	
}
