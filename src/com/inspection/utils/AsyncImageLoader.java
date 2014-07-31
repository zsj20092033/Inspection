package com.inspection.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class AsyncImageLoader extends AsyncTask<Object, Void, Bitmap> {
	
	private ImageView imageView;
	private LruCache<String, Bitmap> lruCache;
	private String filePath;
	
	public AsyncImageLoader(ImageView imageView, LruCache<String, Bitmap> lruCache, String filePath) {
		super();
		this.imageView = imageView;
		this.lruCache = lruCache;
		this.filePath = filePath;
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		Bitmap bitmap = null;
		bitmap = BitmapUtils.getThumbnail1((String)params[0], ((Integer)params[1]).intValue(), ((Integer)params[2]).intValue(), ((Boolean)params[3]).booleanValue());
		addBitmapToMemoryCache((String)params[0], bitmap);
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageView.getTag().toString().equals(filePath)) {
			imageView.setImageBitmap(result);
		}			
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if(getBitmapFromMemoryCache(key) == null) {
			lruCache.put(key, bitmap);
		}		
	}
	
	public Bitmap getBitmapFromMemoryCache(String key) {
		return lruCache.get(key);
	}

}
