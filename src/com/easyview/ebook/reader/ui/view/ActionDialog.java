/**
 * @file       MessageDialogForAction.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-2 下午03:38:00 
 */

package com.easyview.ebook.reader.ui.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import com.easyview.ebook.reader.engine.core.IActionCall;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.EasyViewer;
import com.easyview.ebook.reader.easyviewer.R;

public class ActionDialog implements IActionCall {
	static private final String TAG = "ActionDialog";
	private String mMessage;
	private AlertDialog mAlertDlg;
	private Dialog mCustomDlg;
	private Context mContext;
	private int mPosX = 0;
	private int mPosY = 0;
	private int mDefaultStyle = R.style.BlackToast;
	private boolean mDefaultCancelable = false;
	private boolean mDefaultCanceledOnTouchOutside = false;

	private final int CLOSE_DIALOG = 1;
	
	static public final int TYPE_ALERT_DIALOG = 1;
	static public final int TYPE_TOAST = 2;
	static public final int TYPE_CUSTOM_DIALOG = 3;

	private int mDefaultType = TYPE_ALERT_DIALOG;
	private View mContentView;

	private Handler H = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CLOSE_DIALOG:
				closeDialog();
				break;

			default:
				break;
			}
		};
	};

	public ActionDialog(Context context) {
		mContext = context;
	}

	public boolean action() {
		showDialog();
		return true;
	}

	public void setMessage(String msg) {
		mMessage = msg;
	}

	public void setDialogType(int type) {
		mDefaultType = type;
	}

	public void setContentView(View view) {
		mContentView = view;
	}
	
	public void setPositionX(int x) {
		mPosX = x;
	}
	
	public void setPositionY(int y) {
		mPosY = y;
	}
	
	public void setDialogStyle(int style) {
		mDefaultStyle = style;
	}
	
	public void setCancelable(boolean cancel) {
		mDefaultCancelable = cancel;
	}
	
	public void setCanceledOnTouchOutside(boolean cancel) {
		mDefaultCanceledOnTouchOutside = cancel;
	}
	
	// 關閉消息對話框
	public void closeDialog() {
		if (mAlertDlg != null) {
			mAlertDlg.dismiss();
			mAlertDlg = null;
		}
		
		if (mCustomDlg != null) {
			mCustomDlg.dismiss();
			mCustomDlg = null;
			mContentView = null;
		}
	}

	// 顯示消息對話框
	public void showDialog() {
		closeDialog();

		switch (mDefaultType) {
		case TYPE_ALERT_DIALOG:
			Logger.eLog(TAG, "createAlertDialog TYPE_ALERT_DIALOG");
			createAlertDialog();
			break;
		case TYPE_TOAST:
			Logger.eLog(TAG, "createAlertDialog TYPE_TOAST");
			createToast();
			break;
		case TYPE_CUSTOM_DIALOG:
			Logger.eLog(TAG, "createAlertDialog TYPE_CUSTOM_DIALOG");
			createCustomDialog();
			break;

		default:
			break;
		}
	}

	private void createAlertDialog() {
		mAlertDlg = new AlertDialog.Builder(mContext).setTitle(mMessage)
				.create();
		mAlertDlg.setCancelable(mDefaultCancelable);
		mAlertDlg.setCanceledOnTouchOutside(mDefaultCanceledOnTouchOutside);
		mAlertDlg.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				closeDialog();
			}
		});
		
		WindowManager.LayoutParams params = mAlertDlg.getWindow().getAttributes();
		params.width = LayoutParams.WRAP_CONTENT;
		params.height = LayoutParams.WRAP_CONTENT;
		mAlertDlg.getWindow().setAttributes(params);
		mAlertDlg.show();
	}

	private void createToast() {
		Toast.makeText(mContext, mMessage, Toast.LENGTH_SHORT).show();
	}

	private void createCustomDialog() {
		if (null == mContentView) {
			return;
		}
		
		mCustomDlg = new Dialog(mContext, mDefaultStyle);
		mCustomDlg.setContentView(mContentView);
		mCustomDlg.setCancelable(mDefaultCancelable);
		mCustomDlg.setCanceledOnTouchOutside(mDefaultCanceledOnTouchOutside);
		mCustomDlg.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				closeDialog();
			}
		});
		
		Window w = mCustomDlg.getWindow();
		WindowManager.LayoutParams wl = w.getAttributes();
		wl.x = mPosX;
		wl.y = mPosY;
		wl.softInputMode = LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
		Logger.dLog(TAG, "x = " + wl.x + " y = " + wl.y);
		w.setAttributes(wl);
		mCustomDlg.show();
	}

	public void postDialog(long sec) {
		showDialog();
		new AutoCloseThread(sec).start();
	}

	class AutoCloseThread extends Thread {
		private long mSecond;

		public AutoCloseThread(long sec) {
			mSecond = sec;
		}

		public void run() {
			try {
				Thread.sleep(mSecond);
				H.sendEmptyMessage(CLOSE_DIALOG);
			} catch (Exception e) {
			}
		}
	}
}
