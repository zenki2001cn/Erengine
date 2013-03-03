/**
 * @file       FileBrowser.java
 *
 * @revision:  none 
 *
 * @version    0.0.1
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-8-1 下午01:10:35 
 */

package com.easyview.ebook.reader.ui.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.easyviewer.R;

public class FileBrowser extends Activity {
	static private final String TAG = "FileBrowser";

	private SharedPreferences mSharedPreferences;
	private ListView mLocalListView;
	private ListView mRecentListView;
	private ImageButton mBrowserLocalButton;
	private ImageButton mBrowserRecentButton;
	private String mScanLocalPath;
	private IDatabaseService mIds;

	ArrayList<RecentListItem> mRecentListItems = new ArrayList<FileBrowser.RecentListItem>();
	ArrayList<LocalListItem> mLocalListItems = new ArrayList<FileBrowser.LocalListItem>();
	BrowserRecentListAdapter mBrowserRecentListAdapter;
	BrowserLocalListAdapter mBrowserLocalListAdapter;

	static private final String EXTERNAL_PATH = "/sdcard";
	static private final String UP_DIR = "..";
	private final String KEY_SCAN_PATH = "scan_path";

	private final int MSG_REFRESH_LOCAL_LIST = 1;
	private final int MSG_REFRESH_RECENT_LIST = 2;
	
	private final int ACITVITY_REQUEST_CODE = 0;
	public static String RESULT_UPDATE_RECENT_LIST = "UpdateRecent";

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_LOCAL_LIST:
				refreshLocalList(mScanLocalPath);
				break;
			case MSG_REFRESH_RECENT_LIST:
				mBrowserRecentListAdapter = new BrowserRecentListAdapter(
						FileBrowser.this, mRecentListItems);
				mRecentListView.setAdapter(mBrowserRecentListAdapter);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_browser);

		// 读取配置信息
		mSharedPreferences = getPreferences(MODE_PRIVATE);

		initView();
		// 初始化Engine
		IEngineService ies = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);
		ies.setContext(this);

		// mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LOCAL_LIST, 500);
		refresRecentList();
	}

	@Override
	protected void onResume() {
		loadPreferences();

		registerMediaReceiver();

		super.onResume();
	}

	@Override
	protected void onPause() {
		savePreferences();

		unregisterMediaReceiver();

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == ACITVITY_REQUEST_CODE) {
			Log.e(TAG, "onActivityResult resultCode = " + resultCode);
		}
	}

	private void loadPreferences() {
		mScanLocalPath = mSharedPreferences.getString(KEY_SCAN_PATH,
				EXTERNAL_PATH);
	}
	
	private void savePreferences() {
		SharedPreferences.Editor edit = mSharedPreferences.edit();
		edit.putString(KEY_SCAN_PATH, mScanLocalPath);
		edit.commit();
	}
	
	public void initView() {
		mLocalListView = (ListView) findViewById(R.id.id_file_list);
		mRecentListView = (ListView) findViewById(R.id.id_recent_list);
		mBrowserLocalButton = (ImageButton) findViewById(R.id.id_browser_local_path);
		mBrowserRecentButton = (ImageButton) findViewById(R.id.id_browser_recent_path);

		mLocalListView.setOnItemClickListener(cLocalFileItemClicked);
		mRecentListView.setOnItemClickListener(cRecentFileItemClicked);
		mBrowserLocalButton.setOnClickListener(mRefreshLocalListener);
		mBrowserRecentButton.setOnClickListener(mRefreshRecentListener);
	}

	private OnItemClickListener cLocalFileItemClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;

			Bundle bundle = new Bundle();
			LocalListItem item = (LocalListItem) listView
					.getItemAtPosition(position);
			String pathName = item.getPathName();
			String fileName = item.getFileName();
			File file = new File(pathName);

			Log.d(TAG, "getAbsolutePath " + file.getAbsolutePath());

			// 返回上一级目录
			if (fileName.equals(UP_DIR)) {
				refreshLocalList(file.getParent());
			} else if (file.isFile()) {
				bundle.putString(EasyViewer.INTENT_KEY_FILE_NAME, pathName);
				Log.d(TAG, "openBook = " + pathName);

				Intent i = new Intent();
				ComponentName comp = new ComponentName(
						"com.easyview.ebook.reader.easyviewer",
						"com.easyview.ebook.reader.ui.controller.EasyViewer");
				i.setComponent(comp);
				i.putExtras(bundle);
				// i.setAction("android.intent.action.VIEW");
				startActivityForResult(i, ACITVITY_REQUEST_CODE);
			} else if (file.isDirectory()) {
				Log.d(TAG, "file is dir " + file.getAbsolutePath());
				refreshLocalList(pathName);
			}
		}
	};
	
	private OnItemClickListener cRecentFileItemClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;

			Bundle bundle = new Bundle();
			RecentListItem item = (RecentListItem) listView
					.getItemAtPosition(position);
			String pathName = item.getPathName();
			File file = new File(pathName);

			Log.d(TAG, "getAbsolutePath " + file.getAbsolutePath());

			// 返回上一级目录
			if (!file.exists()) {
				// 当前文件找不到
			} else {
				bundle.putString(EasyViewer.INTENT_KEY_FILE_NAME, pathName);
				Log.d(TAG, "openBook = " + pathName);

				Intent i = new Intent();
				ComponentName comp = new ComponentName(
						"com.easyview.ebook.reader.easyviewer",
						"com.easyview.ebook.reader.ui.controller.EasyViewer");
				i.setComponent(comp);
				i.putExtras(bundle);
				startActivityForResult(i, ACITVITY_REQUEST_CODE);
			}
		}
	};

	private OnClickListener mRefreshLocalListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mLocalListView.setVisibility(View.VISIBLE);
			mRecentListView.setVisibility(View.INVISIBLE);

			refreshLocalList(mScanLocalPath);
		}
	};

	private OnClickListener mRefreshRecentListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mLocalListView.setVisibility(View.INVISIBLE);
			mRecentListView.setVisibility(View.VISIBLE);

//			refresRecentList();
		}
	};

	private void registerMediaReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
		registerReceiver(mMediaReceiver, filter);
	}

	private void unregisterMediaReceiver() {
		unregisterReceiver(mMediaReceiver);
	}

	private ArrayList<String> scanDirctory(String dirPath) {
		File dir = new File(dirPath);
		File[] allFiles;
		String fileName;
		String filePath;
		ArrayList<String> fileList = new ArrayList<String>();

		if ((dir != null) && dir.exists()) {
			allFiles = dir.listFiles();
			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].isDirectory()) {
					filePath = allFiles[i].getAbsolutePath();
					fileList.add(filePath);
				} else if (allFiles[i].isFile()) {
					fileName = allFiles[i].getName().toLowerCase();
					// Remove Adobe RMSDK support
					if (fileName.endsWith("epub") || fileName.endsWith("fb2")
							|| fileName.endsWith("txt")
							|| fileName.endsWith("html")
							|| fileName.endsWith("htm")) {
						filePath = allFiles[i].getAbsolutePath();
						fileList.add(filePath);
					}
				}
			}
		}

		return fileList;
	}

	private void refreshLocalList(String scanPath) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.e(TAG, "SD card is not exist");
			return;
		}

		mScanLocalPath = scanPath;
		ArrayList<String> fileList = scanDirctory(scanPath);

		mLocalListItems.clear();
		// 加入返回上层目录的..路径
		if (!scanPath.equals(EXTERNAL_PATH)) {
			mLocalListItems.add(new LocalListItem(UP_DIR, scanPath));
		}

		for (String filePath : fileList) {
			String fileName = new File(filePath).getName();
			mLocalListItems.add(new LocalListItem(fileName, filePath));
		}

		mBrowserLocalListAdapter = new BrowserLocalListAdapter(this,
				mLocalListItems);
		mLocalListView.setAdapter(mBrowserLocalListAdapter);
	}

	private class RefreshRecentThread extends Thread {
		private Handler mHandler;
		private Context mContext;

		public RefreshRecentThread(Handler handle, Context context) {
			mHandler = handle;
		}

		@Override
		public void run() {
			mIds = (IDatabaseService) ERManager
					.getService(ERManager.DATABASE_SERVICE);
			mRecentListItems.clear();
			int len = 0;
			
			// 查询文件名
			String[] filePaths = mIds.queryBooksPath();
			if (filePaths != null) {
				len = filePaths.length;
			}
			for (int i = 0; i < len; i++) {
				File file = new File(filePaths[i]);
				mRecentListItems.add(new RecentListItem(file.getName(), file.getAbsolutePath(),
					"", "", ""));
			}
			mHandler.sendEmptyMessage(MSG_REFRESH_RECENT_LIST);
			
			// 查询其他信息
			ArrayList<RecentListItem> recentListTemp = new ArrayList<FileBrowser.RecentListItem>();
			len = 0;
			int[] bookIds = mIds.queryBooksId();
			if (bookIds != null) {
				len = bookIds.length;
			}

			for (int i = 0; i < len; i++) {
				Book book = new Book(bookIds[i]);
				if (mIds.queryBook(book)) {
					String fileName = book.getBookName();
					String filePath = book.getBookPath();
					String size = String.format("%.2f KB", (float)(book.getBookSize() / 1000.f));
					String perString = book.getLastLocation();
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
					String lastTime = sdf.format(new Date(book.getLastAccessTime()));
					recentListTemp.add(new RecentListItem(fileName, filePath,
							size, perString, lastTime));
				}
			}

			mRecentListItems = recentListTemp;
			mHandler.sendEmptyMessage(MSG_REFRESH_RECENT_LIST);

			super.run();
		}
	}

	private void refresRecentList() {
		new RefreshRecentThread(mHandler, this).start();
	}

	private final BroadcastReceiver mMediaReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			refreshLocalList(mScanLocalPath);
		}
	};

	protected class LocalListItem {
		String mFileName;
		String mPathName;

		public LocalListItem(String fileName, String pathName) {
			mFileName = fileName;
			mPathName = pathName;
		}

		public String getFileName() {
			return mFileName;
		}

		public String getPathName() {
			return mPathName;
		}
	}

	protected class BrowserLocalListAdapter extends BaseAdapter {
		LayoutInflater factory;
		List<LocalListItem> mListItem;
		Drawable mDrawFile;
		Drawable mDrawFolder;
		Context mContext;

		public BrowserLocalListAdapter(Context context,
				List<LocalListItem> listItems) {

			mListItem = listItems;
			mContext = context;
			factory = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mDrawFile = context.getResources().getDrawable(R.drawable.file);
			mDrawFolder = context.getResources().getDrawable(R.drawable.folder);
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ImageView imageView;
			TextView textView;

			if (view == null) {
				view = factory.inflate(R.layout.browser_file_item, null);
			}

			LocalListItem item = (LocalListItem) getItem(position);
			imageView = (ImageView) view
					.findViewById(R.id.id_browser_filetype_image);
			textView = (TextView) view.findViewById(R.id.id_browser_filename);

			// set content
			textView.setText(item.getFileName());

			File file = new File(item.getPathName());
			if (file.isDirectory()) {
				imageView.setBackgroundDrawable(mDrawFolder);
			} else {
				imageView.setBackgroundDrawable(mDrawFile);
			}

			return view;
		}
	}

	protected class RecentListItem {
		String mFileName;
		String mPathName;
		String mSize;
		String mPercent;
		String mLastAccessTime;

		public RecentListItem(String fileName, String pathName, String size,
				String percent, String lastTime) {
			mFileName = fileName;
			mPathName = pathName;
			mSize = size;
			mPercent = percent;
			mLastAccessTime = lastTime;
		}

		public String getFileName() {
			return mFileName;
		}

		public String getPathName() {
			return mPathName;
		}

		public String getSize() {
			return mSize;
		}

		public String getPercent() {
			return mPercent;
		}

		public String getLastTime() {
			return mLastAccessTime;
		}
	}

	protected class BrowserRecentListAdapter extends BaseAdapter {
		LayoutInflater factory;
		List<RecentListItem> mListItem;
		Drawable mDrawFile;
		Drawable mDrawFolder;
		Context mContext;

		public BrowserRecentListAdapter(Context context,
				List<RecentListItem> listItems) {

			mListItem = listItems;
			mContext = context;
			factory = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mDrawFile = context.getResources().getDrawable(R.drawable.file);
			mDrawFolder = context.getResources().getDrawable(R.drawable.folder);
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ImageView imageView;
			TextView fileNameTv;
			TextView authorTv;
			TextView percentAndTimeTv;

			if (view == null) {
				view = factory.inflate(R.layout.recent_file_item, null);
			}

			RecentListItem item = (RecentListItem) getItem(position);
			imageView = (ImageView) view
					.findViewById(R.id.id_recent_filetype_image);
			fileNameTv = (TextView) view.findViewById(R.id.id_recent_filename);
			authorTv = (TextView) view.findViewById(R.id.id_recent_size);
			percentAndTimeTv = (TextView) view
					.findViewById(R.id.id_recent_time_and_percent);

			// set content
			fileNameTv.setText(item.getFileName());
			authorTv.setText(String.format("大小: %s", item.getSize()));

			String strPercentAndTime = String.format("最后阅读: %s", item.getLastTime());
			percentAndTimeTv.setText(strPercentAndTime);

			imageView.setBackgroundDrawable(mDrawFile);

			return view;
		}
	}
}
