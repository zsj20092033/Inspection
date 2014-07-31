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
				Toast.makeText(this, "网络连接错误！", Toast.LENGTH_SHORT).show();
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
					Log.i(TAG, "创建已上传图片目录结果：" + r);
				}
			}
			// 在已上传文件夹中创建图片
			if (FileUtils.copyFiles(pic_file, dir.getAbsolutePath()) == -1) {
				Log.i(TAG, "拷贝图片文件失败！");
			}
			String content = "纬度："
					+ latitude
					+ "\n"
					+ "经度："
					+ longitude
					+ "\n"
					+ "时间："
					+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
							Locale.getDefault()).format(date) + "\n" + "状态："
					+ condition + "\n" + "IMEI：" + InspectionApplication.imei
					+ "\n" + "内容：" + wzsm;
			// 在已上传文件中创建图片属性文件
			int createConfig = FileUtils.createFile(
					dir.getAbsolutePath() + File.separator
							+ pic_name.substring(0, pic_name.length() - 5)
							+ ".ini", content);
			if (createConfig == -1) {
				Log.i(TAG, "在已上传文件中创建图片属性文件失败！");
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

				// 处理图片，包括图片的旋转和水印
				int degree = readPhotoDegree(filePath);
				BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inJustDecodeBounds = true;
				options.inSampleSize = 8;
				Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
				bitmap = rotaingImage(degree, bitmap);
				String time = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss",
						Locale.getDefault()).format(date);
				String title = "年月日： " + time.split("\\.")[0] + "\n" + "时分秒： "
						+ time.split("\\.")[1] + "\n" + "经度： " + longitude
						+ "\n" + "纬度： " + latitude;
				bitmap = waterBitmap(bitmap, null, title);

				String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss_",
						Locale.getDefault()).format(date)
						+ InspectionApplication.imei;
				String oldPath = filePath;
				filePath = filePath.replace("temp", fileName);
				uri = Uri.fromFile(new File(filePath));

				saveBitmap(bitmap, filePath); // 保存处理后的图片
				bitmap.recycle(); // 回收图片内存，防止OOM
				// 删除暂存图片
				File oldFile = new File(oldPath);
				if (oldFile.exists()) {
					oldFile.delete();
				}

				// 跳转到图片预览界面
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
	 * 调用系统相机拍照
	 */
	private void takePicture() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory()
					+ photo_preupload_path);
			if (!dir.exists()) {
				boolean s = dir.mkdirs();
				Log.i(TAG, "创建待上传图片目录结果:" + s);
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
	 * 读取图片属性：旋转的角度
	 * 
	 * @param filePath
	 *            图片的路径
	 * @return degree 旋转的角度
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
	 * 获取旋转指定角度后的图片资源
	 * 
	 * @param degree
	 *            旋转的角度
	 * @param bitmap
	 *            要旋转的图片资源
	 * @return 旋转指定角度后的图片资源
	 */
	public static Bitmap rotaingImage(int degree, Bitmap bitmap) {
		Log.i(TAG, "正在旋转图片------------");
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
	 * 保存图片到指定路径
	 * 
	 * @param bitmap
	 *            需要保存的图片
	 * @param filePath
	 *            保存的图片路径
	 */
	public static void saveBitmap(Bitmap bitmap, String filePath) {
		Log.i(TAG, "正在保存图片------------");
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
	 * 给图片加水印
	 * 
	 * @param bitmap
	 *            原图片
	 * @param water
	 *            水印图片
	 * @param title
	 *            水印文字
	 * @return 加水印的图片
	 */
	public static Bitmap waterBitmap(Bitmap bitmap, Bitmap water, String title) {
		Log.i(TAG, "给图片加水印------------");
		if (bitmap == null) {
			return null;
		}
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap newBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);

		Paint photoPaint = new Paint(); // 建立画笔
		photoPaint.setDither(true); // 获取跟清晰的图像采样
		photoPaint.setFilterBitmap(true);// 过滤一些

		canvas.drawBitmap(bitmap, 0, 0, photoPaint);

		// 加水印图片
		if (water != null) {

		}
		// 加水印文字
		if (!TextUtils.isEmpty(title)) {
			String familyName = "宋体";
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
