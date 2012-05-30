/*
 * ErrorCode.java
 *
 * Version info
 *
 * Create time
 * 
 * Last modify time
 *
 * Copyright (c) 2010 FOXCONN Technology Group All rights reserved
 */
package com.foxconn.ebook.reader.engine.core;

import java.util.HashMap;

import com.foxconn.ebook.reader.engine.util.Logger;

import android.os.Handler;
import android.os.Message;

/**
 * 代碼類： 該類包含所有Engine處理時產生的异常或錯誤代碼.同時用來處理相關錯誤代碼.
 * <p>
 * 代碼類型包含EngineMsgCode、AdobeMsgCode、FbreaderMsgCode
 * <p>
 * 錯誤處理： 錯誤處理需要實現IErrorCall接口回調，當相應錯誤發生時，Engine將自動處理這些回調.
 * <p>
 * Example: <code>
 * <p>
 * <p> 	IErrorCall inputPdfPassword = new IErrorCall() {
 * <p> 		@Override
 * <p>		public void handleError() {
 * <p>			showPdfInputDialog();
 * <p>		}
 * <p>	};
 * <p>
 * <p>	mIEREngine.setErrorHandler(AdobeMsgCode.STATE_ERROR_PDF_T3_NEED_PASSWORD, inputPdfPassword);
 * </code>
 */
public class EngineCode {

	/** The Constant TAG. */
	static private final String TAG = "EngineCode";

	/**
	 * Engine相關的信息代碼，包含錯誤和异常碼
	 */
	public interface EngineMsgCode {

		/** 無錯誤. */
		public static final int NO_ERROR = 0;

		/** 保存批注失敗. */
		public static final int ENGINE_ERROR_COMMENT_SAVE_FAILED = -9980;

		/** 讀取批注失敗. */
		public static final int ENGINE_ERROR_COMMENT_LOAD_FAILED = -9981;

		/** 刪除批注失敗. */
		public static final int ENGINE_ERROR_COMMENT_DELETE_FAILED = -9982;

		/** 初始化批注模塊失敗. */
		public static final int ENGINE_ERROR_COMMENT_INIT_FAILED = -9983;

		/** 批注模塊尚未初始化. */
		public static final int ENGINE_ERROR_COMMENT_NOT_INIT = -9984;

		/** 任務管理器尚未運行. */
		public static final int ENGINE_ERROR_TASK_SERVER_NOT_RUNNING = -9985;

		/** 任務管理器崩潰. */
		public static final int ENGINE_ERROR_TASK_SERVER_CRASH = -9986;

		/** 任務列表已清除. */
		public static final int ENGINE_NORMAL_TASK_CLEAR = -9987;

		/** 章節跳轉失敗. */
		public static final int ENGINE_WARN_CHAPTER_JUMP_FAILED = -9988;

		/** 跳轉到上一章節失敗. */
		public static final int ENGINE_WARN_CHAPTER_UP_FAILED = -9989;

		/** 跳轉到下一章節失敗. */
		public static final int ENGINE_WARN_CHAPTER_DOWN_FAILED = -9990;

		/** 錯誤的章節序號. */
		public static final int ENGINE_WARN_CHAPTER_INDEX_FAILED = -9991;

		/** 關閉書籍失敗. */
		public static final int ENGINE_ERROR_BOOK_CLOSE_FAILED = -9992;

		/** 書籍不存在. */
		public static final int ENGINE_ERROR_BOOK_NOT_EXISTS = -9993;

		/** 頁碼錯誤. */
		public static final int ENGINE_ERROR_PAGE_NUMBER_INVALID = -9994;

		/** 渲染失敗. */
		public static final int ENGINE_ERROR_RENDER_FAILED = -9995;

		/** Engine錯誤. */
		public static final int ENGINE_ERROR_ENGINE_ERROR = -9996;

		/** 書籍不存在或格式不正確. */
		public static final int ENGINE_ERROR_BOOK_INVALID = -9997;

		/** 讀取數據記錄失敗. */
		public static final int ENGINE_ERROR_RECORDER_LOAD_FAILED = -9998;

		/** 保存數據記錄失敗. */
		public static final int ENGINE_ERROR_RECORDER_SAVE_FAILED = -9999;

		/** 超出內存範圍. */
		public static final int ENGINE_FATAL_OUTOF_MEMORY = -10000;

		/** 代碼錯誤. */
		public static final int ENGINE_ERROR_CODE_ERROR = -10001;

		/** 已到達最小字體. */
		public static final int ENGINE_WARN_MIN_FONT = -10002;

		/** 已到達最大字體. */
		public static final int ENGINE_WARN_MAX_FONT = -10003;

		/** 搜索結果為空. */
		public static final int ENGINE_WARN_SEARCH_RESULT_NULL = -10004;

		/** 搜索關鍵字為空. */
		public static final int ENGINE_WARN_SEARCH_KEY_NULL = -10005;

		/** 搜索下一條結果失敗. */
		public static final int ENGINE_ERROR_SEARCH_GO_NEXT_FAILED = -10006;

		/** 搜索上一條結果失敗. */
		public static final int ENGINE_ERROR_SEARCH_GO_PREVIOUS_FAILED = -10007;

		/** 添加書簽失敗. */
		public static final int ENGINE_ERROR_BOOKMARK_ADD_FAILED = -10008;

		/** 刪除書簽失敗. */
		public static final int ENGINE_ERROR_BOOKMARK_DEL_FAILED = -10009;

		/** 跳轉書簽失敗. */
		public static final int ENGINE_ERROR_BOOKMARK_GOTO_FAILED = -10010;

		/** 關閉之前打開的書失敗. */
		public static final int ENGINE_ERROR_BOOK_CLOSE_PREVIOUS_FAILED = -10011;

		/** 旋屏失敗. */
		public static final int ENGINE_ERROR_BOOK_ROTATE_SCREEN_FAILED = -10012;

		/** 添加高亮選擇失敗. */
		public static final int ENGINE_ERROR_BOOK_ADD_HIGHLIGHT_FAILED = -10013;

		/** 刪除高亮選擇失敗. */
		public static final int ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED = -10014;

		/** 放大字體失敗. */
		public static final int STATE_ERROR_ZOOM_IN_FAILED = -10015;

		/** 縮小字體失敗. */
		public static final int STATE_ERROR_ZOOM_OUT_FAILED = -10016;

		/** 向上翻頁失敗. */
		public static final int STATE_ERROR_PAGE_UP_FAILED = -10017;

		/** 向下翻頁失敗. */
		public static final int STATE_ERROR_PAGE_DOWN_FAILED = -10018;

		/** 獲取總頁碼失敗. */
		public static final int STATE_ERROR_GET_TOTAL_PAGE_NUM_FAILED = -10019;

		/** 獲取當前頁碼失敗. */
		public static final int STATE_ERROR_GET_CURRENT_PAGE_NUM_FAILED = -10020;

		/** 獲取Bitmap對象失敗. */
		public static final int STATE_ERROR_GET_BITMAP_FAILED = -10021;

		/** 跳轉頁碼失敗. */
		public static final int STATE_ERROR_GOTO_PAGE_FAILED = -10022;

		/** 跳轉位置失敗. */
		public static final int STATE_ERROR_GOTO_POSITION_FAILED = -10023;

		/** 保存書籍信息至數據庫失敗. */
		public static final int STATE_ERROR_DB_SAVE_BOOK_FAILED = -10024;

		/** 從數據庫讀取書籍信息失敗. */
		public static final int STATE_ERROR_DB_LOAD_BOOK_FAILED = -10025;

		/** 添加書簽至數據庫失敗或數據庫中已存在重復數據. */
		public static final int STATE_ERROR_DB_ADD_BOOKMARK_FAILED = -10026;

		/** 從數據庫刪除書簽失敗. */
		public static final int STATE_ERROR_DB_DEL_BOOKMARK_FAILED = -10027;

		/** 清空數據庫的書簽信息失敗. */
		public static final int STATE_ERROR_DB_CLEAN_BOOKMARK_FAILED = -10028;

		/** 添加高亮選擇至數據庫失敗或數據庫中已存在重復數據. */
		public static final int STATE_ERROR_DB_ADD_EMPHASIS_FAILED = -10029;

		/** 從數據庫刪除高亮選擇信息失敗. */
		public static final int STATE_ERROR_DB_DEL_EMPHASIS_FAILED = -10030;

		/** 清空數據庫的高亮選擇信息失敗. */
		public static final int STATE_ERROR_DB_CLEAN_EMPHASIS_FAILED = -10031;

		/** 跳转搜索结果页失败. */
		public static final int STATE_ERROR_SEARCH_GOTO_RESULT_FAILED = -10032;
		
		/** 縮放字體失敗. */
		public static final int STATE_ERROR_ZOOM_LEVEL_FAILED = -10033;
	}

	/**
	 * Adobe相關的信息碼.
	 */
	public interface AdobeMsgCode {

		/** 未知FATAL. */
		public static final int STATE_FATAL_UNKNOWN = -1;

		/** 未知ERROR. */
		public static final int STATE_ERROR_UNKNOWN = -2;

		/** The Constant STATE_WARN_UNKNOWN. */
		// public static final int STATE_WARN_UNKNOWN = -3;

		/** 系統初始化錯誤. */
		public static final int STATE_FATAL_SYS_INIT_FAIL = -102;

		/** 前一本書沒有關閉. */
		public static final int STATE_FATAL_PREVIOUS_FILE_NOT_CLOSED = -103;

		/** 文件不存在. */
		public static final int STATE_FATAL_FILE_NOT_EXIST = -110;

		/** The Constant STATE_ERROR_PKG_RESOURCE_TOO_LONG. */
		public static final int STATE_ERROR_PKG_RESOURCE_TOO_LONG = -140;

		/** The Constant STATE_ERROR_PKG_COMPRESSED_SIZE_TOO_BIG. */
		public static final int STATE_ERROR_PKG_COMPRESSED_SIZE_TOO_BIG = -150;

		/** The Constant STATE_ERROR_PKG_ARTIFICIAL_PAGE_BREAKS. */
		public static final int STATE_ERROR_PKG_ARTIFICIAL_PAGE_BREAKS = -160;

		/** PASSHASH沒找到. */
		public static final int STATE_ERROR_PASSHASH_NOT_FOUND = -400;

		/** PDF書需要打開密碼. */
		public static final int STATE_ERROR_PDF_T3_NEED_PASSWORD = -410;

		/** The Constant STATE_ERROR_USER_NOT_ACTIVATED. */
		public static final int STATE_ERROR_USER_NOT_ACTIVATED = -440;

		/** PDF書密碼為空. */
		public static final int STATE_ERROR_PDF_EMPTY_PASSWORD = -420;

		/** 錯誤的PASSHASH. */
		public static final int STATE_ERROR_NULL_PASSHASH = -113;

		/** The Constant STATE_ERROR_PASSHASH_NO_URL. */
		public static final int STATE_ERROR_PASSHASH_NO_URL = -405;

		/** The Constant STATE_ERROR_PASSHASH_OP_FAIL. */
		public static final int STATE_ERROR_PASSHASH_OP_FAIL = -406;

		/** The Constant STATE_FATAL_SYS_NOT_INIT. */
		public static final int STATE_FATAL_SYS_NOT_INIT = -101;

		/** The Constant STATE_FATAL_PDF_T3_DOC_EXCEPTION. */
		public static final int STATE_FATAL_PDF_T3_DOC_EXCEPTION = -200;

		/** The Constant STATE_ERROR_PDF_T3_DOC_EXCEPTION. */
		public static final int STATE_ERROR_PDF_T3_DOC_EXCEPTION = -210;

		/** The Constant STATE_FATAL_PKG_XML_PARSE_ERROR. */
		public static final int STATE_FATAL_PKG_XML_PARSE_ERROR = -300;

		/** The Constant STATE_ERROR_PKG_XML_PARSE_ERROR. */
		public static final int STATE_ERROR_PKG_XML_PARSE_ERROR = -310;

		/** The Constant STATE_ERROR_NULL_BOOKMARK. */
		public static final int STATE_ERROR_NULL_BOOKMARK = -111;

		/** The Constant STATE_WARN_NEGATIVE_POSITION. */
		public static final int STATE_WARN_NEGATIVE_POSITION = -112;

		/** The Constant STATE_WARN_DEVICE_ALREADY_ACTIVATED. */
		public static final int STATE_WARN_DEVICE_ALREADY_ACTIVATED = -121;

		/** The Constant STATE_WARN_DEVICE_NOT_ACTIVATE. */
		public static final int STATE_WARN_DEVICE_NOT_ACTIVATE = -120;

		/** The Constant STATE_ERROR_NO_VALID_LICENSE. */
		public static final int STATE_ERROR_NO_VALID_LICENSE = -450;

		/** The Constant STATE_WARN_DEVICE_ACTIVE_130. */
		public static final int STATE_WARN_DEVICE_ACTIVE_130 = -130;

		/** The Constant STATE_ERROR_CORE_EXPIRED. */
		public static final int STATE_ERROR_CORE_EXPIRED = -451;

		/** The Constant STATE_ERROR_CORE_LOAN_NOT_ON_RECORD. */
		public static final int STATE_ERROR_CORE_LOAN_NOT_ON_RECORD = -452;

		/** The Constant STATE_NORMAL_GET_MORE_SEARCH_RESULT. */
		public static final int STATE_NORMAL_GET_MORE_SEARCH_RESULT = -453;
	}

	/**
	 * FBReader相關的信息碼.
	 */
	public interface FbreaderMsgCode {

		/** 打開書失敗. */
		public static final int STATE_ERROR_OPEN_FILE_FAILED = -7000;

		/** 段落序號錯誤. */
		public static final int STATE_ERROR_PH_INDEX_ERROR = -7001;

		/** FBReader引擎初始化失敗. */
		public static final int STATE_ERROR_APP_INIT_ERROR = -7002;

		/** 到達段落末尾. */
		public static final int STATE_NORMAL_END_OF_PH = -7003;

		/** 到達段落開頭. */
		public static final int STATE_NORMAL_BEGIN_OF_PH = -7004;

		/** 書籍裝載完成. */
		public static final int STATE_NORMAL_LOAD_BOOK_FINISHED = -7005;
	}

	/** The m last error. */
	private int mLastError = EngineMsgCode.NO_ERROR;

	/** The m engine error. */
	static private EngineCode mEngineError = null;

	/** The m handler. */
	static private ErrroHandler mHandler = null;

	/** The m error map. */
	static private HashMap<String, IErrorCall> mErrorMap;

	/** The m code family. */
	static private int[] mCodeFamily = {
			EngineMsgCode.ENGINE_ERROR_COMMENT_SAVE_FAILED,
			EngineMsgCode.ENGINE_ERROR_COMMENT_LOAD_FAILED,
			EngineMsgCode.ENGINE_ERROR_COMMENT_DELETE_FAILED,
			EngineMsgCode.ENGINE_ERROR_COMMENT_INIT_FAILED,
			EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT,
			EngineMsgCode.ENGINE_ERROR_TASK_SERVER_NOT_RUNNING,
			EngineMsgCode.ENGINE_ERROR_TASK_SERVER_CRASH,
			EngineMsgCode.ENGINE_NORMAL_TASK_CLEAR,
			EngineMsgCode.ENGINE_WARN_CHAPTER_JUMP_FAILED,
			EngineMsgCode.ENGINE_WARN_CHAPTER_UP_FAILED,
			EngineMsgCode.ENGINE_WARN_CHAPTER_DOWN_FAILED,
			EngineMsgCode.ENGINE_WARN_CHAPTER_INDEX_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_CLOSE_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_NOT_EXISTS,
			EngineMsgCode.ENGINE_ERROR_PAGE_NUMBER_INVALID,
			EngineMsgCode.ENGINE_ERROR_RENDER_FAILED,
			EngineMsgCode.ENGINE_ERROR_ENGINE_ERROR,
			EngineMsgCode.ENGINE_ERROR_BOOK_INVALID,
			EngineMsgCode.ENGINE_ERROR_RECORDER_LOAD_FAILED,
			EngineMsgCode.ENGINE_ERROR_RECORDER_SAVE_FAILED,
			EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY,
			EngineMsgCode.ENGINE_ERROR_CODE_ERROR,
			EngineMsgCode.ENGINE_WARN_MIN_FONT,
			EngineMsgCode.ENGINE_WARN_MAX_FONT,
			EngineMsgCode.ENGINE_WARN_SEARCH_RESULT_NULL,
			EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL,
			EngineMsgCode.ENGINE_ERROR_SEARCH_GO_NEXT_FAILED,
			EngineMsgCode.ENGINE_ERROR_SEARCH_GO_PREVIOUS_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOKMARK_ADD_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOKMARK_DEL_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOKMARK_GOTO_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_CLOSE_PREVIOUS_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_ROTATE_SCREEN_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_ADD_HIGHLIGHT_FAILED,
			EngineMsgCode.ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED,
			EngineMsgCode.STATE_ERROR_ZOOM_IN_FAILED,
			EngineMsgCode.STATE_ERROR_ZOOM_OUT_FAILED,
			EngineMsgCode.STATE_ERROR_PAGE_DOWN_FAILED,
			EngineMsgCode.STATE_ERROR_PAGE_UP_FAILED,
			EngineMsgCode.STATE_ERROR_GET_TOTAL_PAGE_NUM_FAILED,
			EngineMsgCode.STATE_ERROR_GET_CURRENT_PAGE_NUM_FAILED,
			EngineMsgCode.STATE_ERROR_GET_BITMAP_FAILED,
			EngineMsgCode.STATE_ERROR_GOTO_PAGE_FAILED,
			EngineMsgCode.STATE_ERROR_GOTO_POSITION_FAILED,
			EngineMsgCode.STATE_ERROR_DB_SAVE_BOOK_FAILED,
			EngineMsgCode.STATE_ERROR_DB_LOAD_BOOK_FAILED,
			EngineMsgCode.STATE_ERROR_DB_ADD_BOOKMARK_FAILED,
			EngineMsgCode.STATE_ERROR_DB_DEL_BOOKMARK_FAILED,
			EngineMsgCode.STATE_ERROR_DB_CLEAN_BOOKMARK_FAILED,
			EngineMsgCode.STATE_ERROR_DB_ADD_EMPHASIS_FAILED,
			EngineMsgCode.STATE_ERROR_DB_DEL_EMPHASIS_FAILED,
			EngineMsgCode.STATE_ERROR_DB_CLEAN_EMPHASIS_FAILED,
			EngineMsgCode.STATE_ERROR_SEARCH_GOTO_RESULT_FAILED,
			EngineMsgCode.STATE_ERROR_ZOOM_LEVEL_FAILED,
			AdobeMsgCode.STATE_FATAL_UNKNOWN, AdobeMsgCode.STATE_ERROR_UNKNOWN,
			AdobeMsgCode.STATE_FATAL_SYS_INIT_FAIL,
			AdobeMsgCode.STATE_FATAL_PREVIOUS_FILE_NOT_CLOSED,
			AdobeMsgCode.STATE_FATAL_FILE_NOT_EXIST,
			AdobeMsgCode.STATE_ERROR_PKG_RESOURCE_TOO_LONG,
			AdobeMsgCode.STATE_ERROR_PKG_COMPRESSED_SIZE_TOO_BIG,
			AdobeMsgCode.STATE_ERROR_PKG_ARTIFICIAL_PAGE_BREAKS,
			AdobeMsgCode.STATE_ERROR_PASSHASH_NOT_FOUND,
			AdobeMsgCode.STATE_ERROR_PDF_T3_NEED_PASSWORD,
			AdobeMsgCode.STATE_ERROR_USER_NOT_ACTIVATED,
			AdobeMsgCode.STATE_ERROR_PDF_EMPTY_PASSWORD,
			AdobeMsgCode.STATE_ERROR_NULL_PASSHASH,
			AdobeMsgCode.STATE_ERROR_PASSHASH_NO_URL,
			AdobeMsgCode.STATE_ERROR_PASSHASH_OP_FAIL,
			AdobeMsgCode.STATE_FATAL_SYS_NOT_INIT,
			AdobeMsgCode.STATE_FATAL_PDF_T3_DOC_EXCEPTION,
			AdobeMsgCode.STATE_ERROR_PDF_T3_DOC_EXCEPTION,
			AdobeMsgCode.STATE_FATAL_PKG_XML_PARSE_ERROR,
			AdobeMsgCode.STATE_ERROR_PKG_XML_PARSE_ERROR,
			AdobeMsgCode.STATE_ERROR_NULL_BOOKMARK,
			AdobeMsgCode.STATE_WARN_NEGATIVE_POSITION,
			AdobeMsgCode.STATE_WARN_DEVICE_ALREADY_ACTIVATED,
			AdobeMsgCode.STATE_WARN_DEVICE_NOT_ACTIVATE,
			AdobeMsgCode.STATE_ERROR_NO_VALID_LICENSE,
			AdobeMsgCode.STATE_WARN_DEVICE_ACTIVE_130,
			AdobeMsgCode.STATE_ERROR_CORE_EXPIRED,
			AdobeMsgCode.STATE_ERROR_CORE_LOAN_NOT_ON_RECORD,
			AdobeMsgCode.STATE_NORMAL_GET_MORE_SEARCH_RESULT,
			FbreaderMsgCode.STATE_ERROR_OPEN_FILE_FAILED,
			FbreaderMsgCode.STATE_NORMAL_LOAD_BOOK_FINISHED,
			FbreaderMsgCode.STATE_NORMAL_BEGIN_OF_PH,
			FbreaderMsgCode.STATE_NORMAL_END_OF_PH,
			FbreaderMsgCode.STATE_ERROR_PH_INDEX_ERROR,
			FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR };

	// This is IErrorCall demo code, you can define special call for handling
	// error.
	// static private IErrorCall mDefaultCall = new IErrorCall() {
	// @Override
	// public void handleError() {
	// Logger.dLog(TAG, "Default error called");
	// }
	// };

	/**
	 * Instantiates a new engine code.
	 */
	private EngineCode() {
	}

	/**
	 * 獲取EngineCode單例對象.
	 * 
	 * @return single instance of EngineCode
	 */
	static public EngineCode getInstance() {
		if (null == mEngineError) {
			mEngineError = new EngineCode();
			mHandler = new ErrroHandler(mEngineError);
			initErrorMap();
		}

		return mEngineError;
	}

	/**
	 * Inits the error map.
	 */
	static private void initErrorMap() {
		if (null == mErrorMap) {
			mErrorMap = new HashMap<String, IErrorCall>();
			for (int code : mCodeFamily) {
				mErrorMap.put(String.valueOf(code), null);
			}
		}
	}

	/**
	 * Sets the error handler.
	 * 
	 * @param code
	 *            the code
	 * @param handler
	 *            the handler
	 */
	protected void setErrorHandler(int code, IErrorCall handler) {
		String key = String.valueOf(code);

		if (mErrorMap != null) {
			mErrorMap.put(key, handler);
		}
	}

	/**
	 * Execute handler.
	 * 
	 * @param code
	 *            the code
	 */
	protected void executeHandler(int code) {
		String key = String.valueOf(code);
		IErrorCall call;

		if (mErrorMap != null) {
			call = mErrorMap.get(key);
			if (call != null) {
				call.handleError();
				// setLastError(EngineErrorCode.NO_ERROR);
			} else {
				Logger.dLog(TAG, "ErrorCode " + key + " not set handler");
			}
		}
	}

	/**
	 * Gets the message handler.
	 * 
	 * @return the message handler
	 */
	protected ErrroHandler getMessageHandler() {
		return mHandler;
	}

	/**
	 * 釋放資源.
	 */
	public void free() {
		if (mEngineError != null) {
			mEngineError = null;
		}

		if (mErrorMap != null) {
			mErrorMap.clear();
			mErrorMap = null;
		}
	}

	/**
	 * 後去最後信息碼.
	 * 
	 * @return the last code
	 */
	public int getLastCode() {
		return mLastError;
	}

	/**
	 * 設置最後信息碼.
	 * 
	 * @param error
	 *            the new last code
	 */
	public void setLastCode(int error) {
		Logger.vLog(TAG, "LastError = " + error + " PreviousError = "
				+ mLastError);
		mLastError = error;
	}

	/**
	 * 判斷信息碼是否為錯誤信息.
	 * 
	 * @param code
	 *            信息碼
	 * @return true, 不是錯誤信息碼. false, 是錯誤信息碼
	 */
	public boolean noError(int code) {
		int len = mCodeFamily.length;

		for (int i = 0; i < len; i++) {
			if (mCodeFamily[i] == code) {
				return false;
			}
		}

		return true;
	}

	/**
	 * The Class ErrroHandler.
	 */
	static protected class ErrroHandler extends Handler {

		/** The m error code. */
		private EngineCode mErrorCode = null;

		/** The Constant EXECUTE_HANDLER. */
		static protected final int EXECUTE_HANDLER = 0;

		/**
		 * Instantiates a new errro handler.
		 * 
		 * @param error
		 *            the error
		 */
		public ErrroHandler(EngineCode error) {
			mErrorCode = error;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			int code = 0;

			switch (msg.what) {
			case EXECUTE_HANDLER:
				code = msg.arg1;
				mErrorCode.executeHandler(code);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
}
