/**
 * @file       EasyViewerApplication.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-15 下午03:32:24 
 */

package com.easyview.ebook.reader.easyviewer;

import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.ControlCenterStyleFlat;
import com.easyview.ebook.reader.ui.controller.ControlCenterStyleGrid;
import com.easyview.ebook.reader.ui.controller.IControlCenterService;

import android.app.Application;

public class EasyViewerApplication extends Application {
	static private final String TAG = "EasyViewerApplication";
	private IControlCenterService mIccs;
	private IEngineService mIees;

	@Override
	public void onCreate() {
		getErengineService();
		getControlCenterService();
		
		super.onCreate();
	}

	@Override
	public void onTerminate() {
		Logger.dLog(TAG, "onTerminate");
		free();
		
		super.onTerminate();
	}

	public IEngineService getErengineService() {
		if (null == mIees) {
			initEngine();
		}

		return mIees;
	}

	public IControlCenterService getControlCenterService() {
		if (null == mIccs) {
			initControlCenter();
		}

		return mIccs;
	}

	public void free() {
		ERManager.free();
		// grid style
		// ControlCenterStyleGrid.getInstance().free();
		
		// flat style
		ControlCenterStyleFlat.getInstance().free();

		mIees = null;
		mIccs = null;
	}

	private void initEngine() {
		mIees = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);
		mIees.setApplication(this);
		mIees.setContext(this);
	}

	private void initControlCenter() {
		// grid style
		// mIccs = ControlCenterStyleGrid.getInstance();
		
		// flat style
		mIccs = ControlCenterStyleFlat.getInstance();
	}

}
