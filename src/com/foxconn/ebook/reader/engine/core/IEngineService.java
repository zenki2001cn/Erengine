/**
 * @file       IEngineService.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-1 下午02:53:39 
 */

package com.foxconn.ebook.reader.engine.core;

import android.app.Application;
import android.content.Context;

import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.util.EngineConfig;
import com.foxconn.ebook.reader.engine.util.recorder.IRecorder;
import com.foxconn.ebook.reader.engine.util.render.BaseRender;

/**
 * EREngine的服務類接口.通過該接口進行EREngine的初始化并獲取功能組件.
 * 
 * Example:<p>
 * {@code IEngineService iEngineService = (IEngineService) ERManager.getService(EREngine.ERENGINE_SERVICE);}
 */
public interface IEngineService {
	
	/** 版本號. */
	public String VERSION_NUMBER = "00.04.0600";
	
	/**
	 * 設置自定義配置信息.
	 *
	 * @param config EngineConfig對象
	 */
	public void setConfig(EngineConfig config);
	
	/**
	 * 獲取配置對象實例.
	 *
	 * @return the config
	 */
	public EngineConfig getConfig();
	
	/**
	 * 設置渲染對象.
	 *
	 * @param render the new render
	 */
	public void setRender(BaseRender render);
	
	/**
	 * 獲取渲染對象實例.
	 *
	 * @return the render
	 */
	public BaseRender getRender();
	
	/**
	 * 設置數據存儲對象.
	 *
	 * @param recorder the new recorder
	 */
	public void setRecorder(IRecorder recorder);
	
	/**
	 * 獲取數據存儲接口對象.
	 *
	 * @return the recorder
	 */
	public IRecorder getRecorder();

	/**
	 * 設置Application對象.
	 *
	 * @param app the new application
	 */
	public void setApplication(Application app);
	
	/**
	 * 獲取Application對象實例.
	 *
	 * @return the application
	 */
	public Application getApplication();
	
	/**
	 * 設置Context對象.
	 *
	 * @param context the new context
	 */
	public void setContext(Context context);
	
	/**
	 * 獲取Context對象實例.
	 *
	 * @return the context
	 */
	public Context getContext();
	
	/**
	 * 手動渲染.當getUpdateViewEnabled()為false時，才需要調用該方法.
	 */
	public void render();
	
	/**
	 * 設置是否自動更新顯示.
	 *
	 * @param enabled true, 開啟自動更新. false, 關閉自動更新
	 */
	public void setUpdateViewEnabled(boolean enabled);
	
	/**
	 * 獲取當前是否為自動更新.
	 *
	 * @return the update view enabled
	 */
	public boolean getUpdateViewEnabled();
	
	/**
	 * 獲取Action對象實例.
	 *
	 * @return the action
	 */
	public IAction getAction();
	
	/**
	 * 設置錯誤處理回調.
	 *
	 * @param code 信息代碼
	 * @param handler 錯誤處理的回調接口
	 * @see com.foxconn.ebook.reader.engine.core.EngineCode
	 */
	public void setErrorHandler(int code, IErrorCall handler);
	
	/**
	 * 設置Book對象.
	 *
	 * @param book Book對象實例
	 */
	public void setBook(Book book);
	
	/**
	 * 獲取Book對象.
	 *
	 * @return the book
	 * @see com.foxconn.ebook.reader.engine.model.Book
	 */
	public Book getBook();
	
	/**
	 * 獲取最後信息碼.
	 *
	 * @return the last error
	 * @see com.foxconn.ebook.reader.engine.core.EngineCode
	 */
	public int getLastError();
	
	/**
	 * 獲取當前EREngine的版本號.
	 *
	 * @return 版本號的字符串
	 */
	public String getVersion();
}
