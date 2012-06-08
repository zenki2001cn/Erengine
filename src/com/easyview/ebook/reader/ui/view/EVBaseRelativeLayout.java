/**
 * @file       EVBaseRelativeLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-9 下午02:19:46 
 */

package com.easyview.ebook.reader.ui.view;

import com.easyview.ebook.reader.easyviewer.EasyViewerApplication;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.EasyViewer;
import com.easyview.ebook.reader.ui.controller.IControlCenterService;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class EVBaseRelativeLayout extends RelativeLayout{
	static private final String TAG = "EVBaseRelativeLayout";

	private Context mContext;
	private IEngineService mIee;
	private IControlCenterService mIcc;
	private EasyViewer mEV;
	private ActionDialog mMessageDlg;
	EasyViewerApplication mApp;
	
	public EVBaseRelativeLayout(Context context) {
		super(context);
		
		init(context);
	}

	public EVBaseRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}

	public EVBaseRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}
	
	private void init(Context ctx) {
		mContext = ctx;
		
		if (mContext instanceof EasyViewer) {
			mEV = (EasyViewer) mContext;
			mApp = (EasyViewerApplication) mEV.getApplication();
			if (null == mIee) {
				mIee = mApp.getErengineService();
			}

			if (null == mIcc) {
				mIcc = mApp.getControlCenterService();
			}
		} else {
			Logger.eLog(TAG, "get EasyViewer context failed");
		}

		mMessageDlg = new ActionDialog(mContext);
	}
	
	protected Context getSuperContext() {
		return mContext;
	}
	
	protected IEngineService getErengine() {
		if (null == mIee) {
			mIee = mApp.getErengineService();
		}
		return mIee;
	}
	
	protected IControlCenterService getControlCenter() {
		if (null == mIcc) {
			mIcc = mApp.getControlCenterService();
		}
		return mIcc;
	}
	
	protected EasyViewer getEasyViewer() {
		return mEV;
	}
	
	protected ActionDialog getActionDialog() {
		return mMessageDlg;
	}
}
