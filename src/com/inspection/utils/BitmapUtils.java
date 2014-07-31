package com.inspection.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapUtils {
	
	/**
	 * 按宽高获取缩略图
	 * @param filePath 图片路径
	 * @return 缩略图
	 */
	public static Bitmap getThumbnail(String filePath, int width, int height) {
		int realHeight = 0;
		int realWidth = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);		
		realHeight = bitmap.getHeight();
		realWidth = bitmap.getWidth();
		float scaleHeight = ((float)height) / realHeight;
		float scaleWidth = ((float)width) / realWidth;
		// 取得想要缩放的矩阵
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 获取新的图片
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, realWidth, realHeight, matrix, true);
		return bitmap;
	}
	
	/**
	 * 等比例获取缩略图
	 * @param filePath 图片路径
	 * @param width 缩放的宽度范围
	 * @param height 缩放的高度范围
	 * @param flag 以较大缩放比例缩放图片为true，反之为false
	 * @return 缩放后的图片
	 */
	public static Bitmap getThumbnail1(String filePath, int width, int height, boolean flag) {
		float realHeight = 0;
		float realWidth = 0;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);		
		realHeight = options.outHeight;
		realWidth = options.outWidth;
		int scale = (int) (realHeight / (float)height);
		int scale1 = (int) (realWidth / (float)width);
		if (scale1 >= scale && flag) {
			scale = scale1;
		} else if (scale1 < scale && !flag) {
			scale = scale1;
		}
		if (scale <= 0) {
			scale = 1;
		}
		options.inSampleSize = scale;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(filePath, options);
		return bitmap;
	}
}
