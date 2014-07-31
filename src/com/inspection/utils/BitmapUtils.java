package com.inspection.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapUtils {
	
	/**
	 * ����߻�ȡ����ͼ
	 * @param filePath ͼƬ·��
	 * @return ����ͼ
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
		// ȡ����Ҫ���ŵľ���
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// ��ȡ�µ�ͼƬ
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, realWidth, realHeight, matrix, true);
		return bitmap;
	}
	
	/**
	 * �ȱ�����ȡ����ͼ
	 * @param filePath ͼƬ·��
	 * @param width ���ŵĿ�ȷ�Χ
	 * @param height ���ŵĸ߶ȷ�Χ
	 * @param flag �Խϴ����ű�������ͼƬΪtrue����֮Ϊfalse
	 * @return ���ź��ͼƬ
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
