/**
 * @file       Config.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-1-28 下午05:27:26 
 */

package com.foxconn.ebook.reader.engine.util;

/**
 * 自定義配置類.
 */
public class EngineConfig {
	
	/** The m view width. */
	private int 	mViewWidth;
	
	/** The m view height. */
	private int 	mViewHeight;
	
	/** The m font size. */
	private int 	mFontLevel;
	
	/** The m display dpi. */
	private int 	mDisplayDpi;
	
	/** The m library path. */
	private String 	mLibraryPath;
	
	/** The m auto load db. */
	private boolean mAutoLoadDb;
	
	/** The m log on. */
	private boolean mLogOn;
	
	/** The m preload mode. */
	private boolean mPreloadMode;
	
	/** 顯示寬度，單位為px。默認為600. */
	static public final int 	DEFAULT_VIEW_WIDTH = 600;
	
	/** 顯示高度，單位為px。默認為800. */
	static public final int 	DEFAULT_VIEW_HEIGHT = 800;
	
	/** 字體等級，範圍為0-3，默認為2. */
	static public final int 	DEFAULT_FONT_LEVEL = 2;
	
	/** 設備顯示DPI，默認為160. */
	static public final int 	DEFAULT_DISPLAY_DPI = 160;
	
	/** 如果當前書籍被打開過，自動從數據庫中讀取本書籍的信息。默認為true. */
	static public final boolean DEFAULT_AUTO_LOAD_DB = true;
	
	/** The Constant DEFAULT_PRELOAD_PAGE. */
	static public final boolean DEFAULT_PRELOAD_PAGE = true;
	
	/** library目錄，不包含檔案名。默認路径为local，则表示so被打包进APK中或在/system/lib中. */
	static public final String 	DEFAULT_LIBRARY_PATH = "local";
	
	/** 当so打包进APK或者为系统库目录/system/lib时，library的目录为local. */
	static public final String 	DEFAULT_LIBRARY_LOCAL = "local";
	
	/**
	 * Instantiates a new engine config.
	 */
	public EngineConfig() {
		mViewWidth = DEFAULT_VIEW_WIDTH;
		mViewHeight = DEFAULT_VIEW_HEIGHT;
		mFontLevel = DEFAULT_FONT_LEVEL;
		mDisplayDpi = DEFAULT_DISPLAY_DPI;
		mLibraryPath = DEFAULT_LIBRARY_PATH;
		mAutoLoadDb = DEFAULT_AUTO_LOAD_DB;
		mPreloadMode = DEFAULT_PRELOAD_PAGE;
		
		mLogOn = false;
	}
	
	/**
	 * 設置預讀緩存模式.開啟該模式時，引擎會自動預讀前一頁和後一頁內容.
	 * 可以提高翻頁的速度.
	 *
	 * @param preload true, 開啟預讀模式. false, 關閉預讀模式.
	 */
	public void setPreloadMode(boolean preload) {
		mPreloadMode = preload;
	}
	
	/**
	 * 獲取是否開啟預讀模式.
	 *
	 * @return 是否開啟預讀模式.
	 */
	public boolean getPreloadMode() {
		return mPreloadMode;
	}
	
	/**
	 * 設置是否自動讀取數據庫信息.
	 *
	 * @param load true, 讀取數據庫信息，false，不讀取數據庫信息.
	 */
	public void setAutoLoadDB(boolean load) {
		mAutoLoadDb = load;
	}
	
	/**
	 * 獲取是否自動讀取數據庫信息.
	 *
	 * @return 是否自動讀取數據庫信息.
	 */
	public boolean getAutoLoadDB() {
		return mAutoLoadDb;
	}
	
	/**
	 * 設置共享庫目錄.
	 *
	 * @param path 共享庫目錄的完整路徑名
	 */
	public void setLibraryPath(String path) {
		mLibraryPath = path;
	}
	
	/**
	 * 獲取共享庫的目錄.
	 *
	 * @return 共享庫目錄的完整路徑名
	 */
	public String getLibraryPath() {
		return mLibraryPath;
	}
	
	/**
	 * 獲取顯示寬度.
	 *
	 * @return 顯示寬度
	 */
	public int getViewWidth() {
		return mViewWidth;
	}
	
	/**
	 * 設置顯示寬度.
	 *
	 * @param width 顯示寬度
	 */
	public void setViewWidth(int width) {
		mViewWidth = width;
	}
	
	/**
	 * 獲取顯示高度.
	 *
	 * @return 顯示高度
	 */
	public int getViewHeight() {
		return mViewHeight;
	}
	
	/**
	 * 設置顯示高度.
	 *
	 * @param height 顯示高度
	 */
	public void setViewHeight(int height) {
		mViewHeight = height;
	}
	
	/**
	 * 獲取字體等級.
	 *
	 * @return 字體等級
	 */
	public int getFontLevel() {
		return mFontLevel;
	}
	
	/**
	 * 設置字體等級.
	 *
	 * @param fontSize 字體等級
	 */
	public void setFontLevel(int fontSize) {
		mFontLevel = fontSize;
	}
	
	/**
	 * 設置顯示DPI.
	 *
	 * @param dpi 顯示DPI
	 */
	public void setDisplayDpi(int dpi) {
		mDisplayDpi = dpi;
	}
	
	/**
	 * 獲取顯示DPI.
	 *
	 * @return 顯示DPI
	 */
	public int getDisplayDpi() {
		return mDisplayDpi;
	}
	
	/**
	 * 設置是否打印LOG.
	 *
	 * @param on true, 打印LOG輸出. false, 不打印LOG輸出.
	 */
	public void setLogOn(boolean on) {
		mLogOn = on;
	}
	
	/**
	 * 獲取是否打印LOG.
	 *
	 * @return true, 打印LOG輸出. false, 不打印LOG輸出.
	 */
	public boolean getLogOn() {
		return mLogOn;
	}
}
