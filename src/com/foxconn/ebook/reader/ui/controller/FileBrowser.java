/**
 * @file       FileBrowser.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-8-1 下午01:10:35 
 */

package com.foxconn.ebook.reader.ui.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.foxconn.ebook.reader.easyviewer.R;
import com.foxconn.ebook.reader.engine.core.ERManager;
import com.foxconn.ebook.reader.engine.core.IDatabaseService;
import com.foxconn.ebook.reader.engine.core.IEngineService;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.util.Logger;

public class FileBrowser extends Activity {
	static private final String TAG = "FileBrowser";
	private ListView mFileListView;
	static private final String EXTERNAL_PATH = "/sdcard/external_sd/books/epub";
	private IDatabaseService mIds;

	private final int MSG_REFRESH_LIST = 1;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REFRESH_LIST:
				refreshList();
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

		initView();
		// 初始化Engine
		IEngineService ies = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);
		ies.setContext(this);

		mHandler.sendEmptyMessageDelayed(MSG_REFRESH_LIST, 500);
	}

	@Override
	protected void onResume() {
		registerMediaReceiver();

		super.onResume();
	}

	@Override
	protected void onPause() {
		unregisterMediaReceiver();

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void initView() {
		mFileListView = (ListView) findViewById(R.id.id_file_list);

		mFileListView.setOnItemClickListener(cListFileClicked);
	}

	private OnItemClickListener cListFileClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView listView = (ListView) parent;

			Bundle bundle = new Bundle();
			String path = listView.getItemAtPosition(position).toString();
			bundle.putString("fileName", path);
			Log.d(TAG, "openBook = " + path);

			Intent i = new Intent();
			ComponentName comp = new ComponentName(
					"com.foxconn.ebook.reader.easyviewer",
					"com.foxconn.ebook.reader.ui.controller.EasyViewer");
			i.setComponent(comp);
			i.putExtras(bundle);
			i.setAction("android.intent.action.VIEW");
			startActivityForResult(i, 0);
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

	private void refreshList() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				FileBrowser.this, android.R.layout.simple_list_item_1);
		mIds = (IDatabaseService) ERManager
				.getService(ERManager.DATABASE_SERVICE);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Logger.dLog(TAG, "SD card is not exist");
			mFileListView.setAdapter(adapter);
			// 清空數據庫中的信息
			mIds.deleteAllBooks();
			return;
		}

		File dir = new File(EXTERNAL_PATH);
		File[] allFiles;
		String fileName;
		String filePath;
		ArrayList<String> fileList = new ArrayList<String>();

		if ((dir != null) && dir.exists()) {
			allFiles = dir.listFiles();
			for (int i = 0; i < allFiles.length; i++) {
				if (allFiles[i].isDirectory()) {
					continue;
				} else if (allFiles[i].isFile()) {
					fileName = allFiles[i].getName().toLowerCase();

					if (fileName.endsWith("epub") || fileName.endsWith("fb2")
							|| fileName.endsWith("pdf")
							|| fileName.endsWith("txt")
							|| fileName.endsWith("html")
							|| fileName.endsWith("htm")) {
						filePath = allFiles[i].getAbsolutePath();
						fileList.add(filePath);
					}
				}
			}
		}

		mIds.addBook(fileList.toArray(new String[] {}));

		String[] bookList = mIds.queryBooksPath();
		int len = 0;
		if (bookList != null) {
			len = bookList.length;
		}
		for (int i = 0; i < len; i++) {
			adapter.add(bookList[i]);
		}
		mFileListView.setAdapter(adapter);
	}

	private final BroadcastReceiver mMediaReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			refreshList();
		}
	};
}
