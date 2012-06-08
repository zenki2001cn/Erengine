/**
 * @file       Logger.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-3 上午10:34:20 
 */

package com.easyview.ebook.reader.engine.util;

import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IEngineService;

import android.util.Log;

/**
 * LOG輸出類.
 */
public class Logger {

	/** The Constant Tag. */
	static private final String Tag = "Logger";

	/** The m config. */
	static EngineConfig mConfig;

	static {
		IEngineService ies = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);

		mConfig = ies.getConfig();
	}

	/**
	 * Free.
	 */
	static public void free() {
		mConfig = null;

		Log.d(Tag, "call free(): mConfig = " + mConfig);
	}

	/**
	 * Update config.
	 * 
	 * @param config
	 *            the config
	 */
	static public void updateFromConfig(EngineConfig config) {
		if (config != null) {
			mConfig = config;
		}
	}

	/**
	 * V log.
	 * 
	 * @param TAG
	 *            the tAG
	 * @param log
	 *            the log
	 */
	static public void vLog(String TAG, String log) {
		if (mConfig != null) {
			if (mConfig.getLogOn()) {
				Log.v(TAG, log);
			}
		}
	}

	/**
	 * D log.
	 * 
	 * @param TAG
	 *            the tAG
	 * @param log
	 *            the log
	 */
	static public void dLog(String TAG, String log) {
		if (mConfig != null) {
			if (mConfig.getLogOn()) {
				Log.d(TAG, log);
			}
		}
	}

	/**
	 * W log.
	 * 
	 * @param TAG
	 *            the tAG
	 * @param log
	 *            the log
	 */
	static public void wLog(String TAG, String log) {
		if (mConfig != null) {
			if (mConfig.getLogOn()) {
				Log.w(TAG, log);
			}
		}
	}

	/**
	 * E log.
	 * 
	 * @param TAG
	 *            the tAG
	 * @param log
	 *            the log
	 */
	static public void eLog(String TAG, String log) {
		if (mConfig != null) {
			if (mConfig.getLogOn()) {
				Log.e(TAG, log);
			}
		}
	}

	/**
	 * I log.
	 * 
	 * @param TAG
	 *            the tAG
	 * @param log
	 *            the log
	 */
	static public void iLog(String TAG, String log) {
		if (mConfig != null) {
			if (mConfig.getLogOn()) {
				Log.i(TAG, log);
			}
		}
	}
}
