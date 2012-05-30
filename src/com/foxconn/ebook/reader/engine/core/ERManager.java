/**
 * @file       ERManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-12 上午08:28:09 
 */

package com.foxconn.ebook.reader.engine.core;

import android.content.Context;

/**
 * Reader的管理類，將來擴展的服務類需要在這裏注冊.
 * <p>該類的 {@link #getService(int)} 方法可以獲取相應的接口.<p>
 * 
 * Example:<p>
 * IEngineService iEngineService = (IEngineService) ERManager.getService(EREngine.ERENGINE_SERVICE);
 * 
 */
public class ERManager {
	
	/** The m er engine. */
	static private EREngine mEREngine;
	
	/** The m sqlite proxy. */
	static private ERSqliteProxy mSqliteProxy;
	
		
	/** EREngine Service標識. */
	public final static int ERENGINE_SERVICE = 0;
	
	/** ERSqliteProxy Service標識. */
	public final static int DATABASE_SERVICE = 1;
	
	/**
	 * 根據類型標識獲取相應的Service接口對象.
	 *
	 * @param serviceType Service標識
	 * @return Service對象
	 */
	static public Object getService(int serviceType) {
		init();
		
		switch (serviceType) {
		case ERENGINE_SERVICE:
			return mEREngine;
		case DATABASE_SERVICE:
			return mSqliteProxy;

		default:
			break;
		}
		
		return null;
	}
	
	/**
	 * 釋放資源，通常在onDestroy()中調用.
	 */
	static public void free() {
		if (mEREngine != null) {
			mEREngine.free();
			mEREngine = null;
		}
	}
	
	/**
	 * Inits the.
	 */
	static private void init() {
		initErengine();
		initSqliteProxy();
	}
	
	/**
	 * Inits the erengine.
	 */
	static private void initErengine() {
		if (null == mEREngine) {
			mEREngine = EREngine.getInterface();
		}
	}
	
	/**
	 * Inits the sqlite proxy.
	 */
	static private void initSqliteProxy() {
		Context ctx;
		
		if (null == mSqliteProxy) {
			ctx = mEREngine.getContext();
			if (ctx != null) {
				mSqliteProxy = new ERSqliteProxy(ctx);
			}
		}
	}
}
