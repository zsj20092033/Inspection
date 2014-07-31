package com.inspection.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.inspection.application.InspectionApplication;
import com.inspection.utils.DeviceStateUtils;
import com.inspection.utils.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class XjUploadActivity extends Activity implements OnClickListener {

	private static final String TAG = "XjUploadActivity";
	private static final String photo_preupload_path = "/inspection/preupload";
	private static final String photo_uploaded_path = "/inspection/uploaded";

	private Button btn_camera;
	private Button btn_upload;
	private TextView tv_photo;
	private EditText et_wzsm;
	private RadioGroup rg_condtion;
	private String filePath;
	private Uri uri;
	private Date date;
	private double longitude;
	private double latitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate-----------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xjupload);

		btn_camera = (Button) findViewById(R.id.xjupload_camera_btn);
		btn_upload = (Button) findViewById(R.id.xjupload_upload_btn);
		tv_photo = (TextView) findViewById(R.id.xjupload_photo_txt);
		et_wzsm = (EditText) findViewById(R.id.xjupload_wzsm_txt);
		rg_condtion = (RadioGroup) findViewById(R.id.xjupload_importance_rg);
		et_wzsm = (EditText) findViewById(R.id.xjupload_wzsm_txt);

		btn_camera.setOnClickListener(this);
		btn_upload.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xjupload_camera_btn:
			takePicture();
			break;

		case R.id.xjupload_upload_btn:
			if (!DeviceStateUtils.isNetworkConnected(this)) {
				Toast.makeText(this, "�������Ӵ���", Toast.LENGTH_SHORT).show();
				return;
			}
			String wzsm = et_wzsm.getText().toString();
			String condition = ((RadioButton) findViewById(rg_condtion
					.getCheckedRadioButtonId())).getText().toString();
			String pic_file = tv_photo.getText().toString();
			String pic_name = new File(pic_file).getName();

			File dir = null;
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				dir = new File(Environment.getExternalStorageDirectory()
						+ photo_uploaded_path);
				if (!dir.exists()) {
					boolean r = dir.mkdirs();
					Log.i(TAG, "�������ϴ�ͼƬĿ¼�����" + r);
				}
			}
			// �����ϴ��ļ����д���ͼƬ
			if (FileUtils.copyFiles(pic_file, dir.getAbsolutePath()) == -1) {
				Log.i(TAG, "����ͼƬ�ļ�ʧ�ܣ�");
			}
			String content = "γ�ȣ�"
					+ latitude
					+ "\n"
					+ "���ȣ�"
					+ longitude
					+ "\n"
					+ "ʱ�䣺"
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.getDefault()).format(date) + "\n" + "״̬��"
					+ condition + "\n" + "IMEI��" + InspectionApplication.imei
					+ "\n" + "���ݣ�" + wzsm;
			// �����ϴ��ļ��д���ͼƬ�����ļ�
			int createConfig = FileUtils.createFile(
					dir.getAbsolutePath() + File.separator
							+ pic_name.substring(0, pic_name.length() - 5)
							+ ".ini", content);
			if (createConfig == -1) {
				Log.i(TAG, "�����ϴ��ļ��д���ͼƬ�����ļ�ʧ�ܣ�");
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == Activity.RESULT_OK) {
				date = new Date();
				longitude = InspectionApplication.longitude;
				latitude = InspectionApplication.latitude;

				// ����ͼƬ������ͼƬ����ת��ˮӡ
				int degree = readPhotoDegree(filePath);
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inJustDecodeBounds = true;
				options.inSampleSize = 8;
				Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
				bitmap = rotaingImage(degree, bitmap);
				String time = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss",
						Locale.getDefault()).format(date);
				String title = "�����գ� " + time.split("\\.")[0] + "\n" + "ʱ���룺 "
						+ time.split("\\.")[1] + "\n" + "���ȣ� " + longitude
						+ "\n" + "γ�ȣ� " + latitude;
				bitmap = waterBitmap(bitmap, null, title);

				String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_",
						Locale.getDefault()).format(date)
						+ InspectionApplication.imei;
				String oldPath = filePath;
				filePath = filePath.replace("temp", fileName);
				uri = Uri.fromFile(new File(filePath));

				saveBitmap(bitmap, filePath); // ���洦����ͼƬ
				bitmap.recycle(); // ����ͼƬ�ڴ棬��ֹOOM
				// ɾ���ݴ�ͼƬ
				File oldFile = new File(oldPath);
				if (oldFile.exists()) {
					oldFile.delete();
				}

				// ��ת��ͼƬԤ������
				Intent intent = new Intent(this, HandlePhotoActivity.class);
				intent.putExtra("filePath", filePath);
				startActivityForResult(intent, 1);
			}
			break;
		case 1:
			if (resultCode == Activity.RESULT_OK) {
				boolean isCamera = data.getBooleanExtra("isReCamera", false);
				if (isCamera) {
					takePicture();
				} else {
					tv_photo = (TextView) findViewById(R.id.xjupload_photo_txt);
					tv_photo.setText(filePath);
				}
			}
			break;
		}
	}

	/**
	 * ����ϵͳ�������
	 */
	private void takePicture() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ photo_preupload_path);
			if (!dir.exists()) {
				boolean s = dir.mkdirs();
				Log.i(TAG, "�������ϴ�ͼƬĿ¼���:" + s);
			}
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File photo = new File(dir, "temp.jpeg");
			filePath = photo.getAbsolutePath();
			uri = Uri.fromFile(photo);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, 0);
		}
	}

	/**
	 * ��ȡͼƬ���ԣ���ת�ĽǶ�
	 * 
	 * @param filePath
	 *            ͼƬ��·��
	 * @return degree ��ת�ĽǶ�
	 */
	public static int readPhotoDegree(String filePath) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(filePath);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * ��ȡ��תָ���ǶȺ��ͼƬ��Դ
	 * 
	 * @param degree
	 *            ��ת�ĽǶ�
	 * @param bitmap
	 *            Ҫ��ת��ͼƬ��Դ
	 * @return ��תָ���ǶȺ��ͼƬ��Դ
	 */
	public static Bitmap rotaingImage(int degree, Bitmap bitmap) {
		Log.i(TAG, "������תͼƬ------------");
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (resizeBitmap != null) {
			bitmap.recycle();
			bitmap = resizeBitmap;
		}
		return bitmap;
	}

	/**
	 * ����ͼƬ��ָ��·��
	 * 
	 * @param bitmap
	 *            ��Ҫ�����ͼƬ
	 * @param filePath
	 *            �����ͼƬ·��
	 */
	public static void saveBitmap(Bitmap bitmap, String filePath) {
		Log.i(TAG, "���ڱ���ͼƬ------------");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��ͼƬ��ˮӡ
	 * 
	 * @param bitmap
	 *            ԭͼƬ
	 * @param water
	 *            ˮӡͼƬ
	 * @param title
	 *            ˮӡ����
	 * @return ��ˮӡ��ͼƬ
	 */
	public static Bitmap waterBitmap(Bitmap bitmap, Bitmap water, String title) {
		Log.i(TAG, "��ͼƬ��ˮӡ------------");
		if (bitmap == null) {
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);

		Paint photoPaint = new Paint(); // ��������
		photoPaint.setDither(true); // ��ȡ��������ͼ�����
		photoPaint.setFilterBitmap(true);// ����һЩ

		canvas.drawBitmap(bitmap, 0, 0, photoPaint);

		// ��ˮӡͼƬ
		if (water != null) {

		}
		// ��ˮӡ����
		if (!TextUtils.isEmpty(title)) {
			String familyName = "����";
			Typeface font = Typeface.create(familyName, Typeface.BOLD);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.YELLOW);
			textPaint.setTypeface(font);
			textPaint.setTextSize(10);
			StaticLayout layout = new StaticLayout(title, textPaint, 3 * w / 4,
					Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
			canvas.translate(40, 40);
			layout.draw(canvas);
		}
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newBitmap;
	}

}
