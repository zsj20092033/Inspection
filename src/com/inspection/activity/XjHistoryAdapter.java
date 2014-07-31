package com.inspection.activity;

import java.io.File;

import com.inspection.application.InspectionApplication;
import com.inspection.utils.AsyncImageLoader;
import com.inspection.utils.FileUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class XjHistoryAdapter extends BaseAdapter {
	
	private final int maxMemory = (int) Runtime.getRuntime().maxMemory(); // 获取当前应用程序所分配的最大内存
	private final int cacheSize = maxMemory / 8; // 分出1/8的内存用来缓存图片
	private File[] files;
	private LayoutInflater inflater;
	private ListView listView;
	private LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(cacheSize) {

		@Override
		protected int sizeOf(String key, Bitmap bitmap) {
			return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
		}
		
	};
	
	public XjHistoryAdapter(Context context, File[] files, ListView listView) {
		this.files = files;
		inflater = LayoutInflater.from(context);
		this.listView = listView;
	}

	@Override
	public int getCount() {
		if (files != null) {
			return files.length;
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (files == null) {
			return null;
		}
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		File file = files[position];
		String fileName = file.getName();
		String filePath = file.getAbsolutePath();
		String fileType = FileUtils.getFileType(filePath);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.xjhistory_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView = (TextView) convertView.findViewById(R.id.xjhistory_item_text);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.xjhistory_item_image);
			viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.xjhistory_image_layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imageView.setTag(filePath);
		
		if (FileUtils.isImage(filePath)) {
			loadImage(filePath, (int)(50*InspectionApplication.density), (int)(50*InspectionApplication.density), viewHolder.imageView);		
		} else if (fileType.equals("ini")) {
			viewHolder.imageView.setImageResource(R.drawable.iniicon);
		}
		viewHolder.textView.setText(fileName);
		Log.i("getView", position + filePath);
		return convertView;
	}
	
	private void loadImage(String filePath, int width, int height, ImageView imageView) {
		//ImageView iv = (ImageView) listView.findViewWithTag(filePath);
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(imageView, lruCache, filePath);
		Bitmap bitmap = asyncImageLoader.getBitmapFromMemoryCache(filePath);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(null);
			asyncImageLoader.execute(filePath, width, height, false);
		}
	}
	
	static class ViewHolder {
		LinearLayout linearLayout;
		TextView textView;
		ImageView imageView;
	}
}
