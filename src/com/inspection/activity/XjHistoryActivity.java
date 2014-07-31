package com.inspection.activity;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;

public class XjHistoryActivity extends Activity {
	
	private static final String TAG = "XjHistoryActivity";
	
	private ListView xjls;
	private File[] files = null;
	//private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate-------------------------");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xjhistory);
		
		xjls = (ListView) findViewById(R.id.xjhistory_lv);
		
		File dir = new File(Environment.getExternalStorageDirectory() + "/inspection/uploaded");
		if (dir.exists()) {
			files = dir.listFiles();
			//data = getData(files);
		}
		//SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.xjhistory_item, new String[]{"textItem", "imageItem"}, new int[]{R.id.xjhistory_item_text, R.id.xjhistory_item_image});
		//adapter.setViewBinder(new MyViewBinder());
		XjHistoryAdapter adapter = new XjHistoryAdapter(this, files, xjls);
		xjls.setAdapter(adapter);
	}
	
	/*private List<Map<String, Object>> getData(File[] files) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		View v = LayoutInflater.from(this).inflate(R.layout.xjhistory_item, null);
		LinearLayout l = (LinearLayout) v.findViewById(R.id.xjhistory_image_layout);
		for (File file : files) {
			Map<String, Object> temp = new HashMap<String, Object>();
			temp.put("textItem", file.getName());
			String fileType = FileUtils.getFileType(file.getAbsolutePath());
			Bitmap bitmap = null;
			if (FileUtils.isImage(file.getAbsolutePath())) {
				bitmap = BitmapUtils.getThumbnail(file.getAbsolutePath(), l.getWidth(), l.getHeight());
				
			} else if (fileType.equals("ini")) {
				bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.iniicon);
			}
			temp.put("imageItem", bitmap);
			data.add(temp);
			bitmap.recycle();
		}
		return data;
	}
	
	class MyViewBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if ((view instanceof ImageView)&&(data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bitmap = (Bitmap) data;
				iv.setImageBitmap(bitmap);
				return true;
			}
			return false;
		}
	}*/
}
