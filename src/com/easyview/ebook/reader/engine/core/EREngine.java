/**
 * @file       EREngine.java
 *
 * @revision:  0.5 
 *
 * @version    0.5.8
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-06-05 下午05:29:59
 */

package com.easyview.ebook.reader.engine.core;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.IErrorCall;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.core.adobe.AdobeWrapper;
import com.easyview.ebook.reader.engine.core.fbreader.FbreaderWrapper;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.util.EngineConfig;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.recorder.IRecorder;
import com.easyview.ebook.reader.engine.util.render.BaseRender;

/**
 * 引擎控制類： 該類需要最先初始化，加載所有可用資源.
 * 
 * <p>
 * 
 * Example:
 * <p>
 * 自定義配置:
 * <p>
 * private EngineConfig initConfig() {
 * <p>
 * EngineConfig config = new EngineConfig();
 * <p>
 * <p>
 * config.setLogOn(true);
 * <p>
 * config.setViewWidth(600);
 * <p>
 * config.setViewHeight(800);
 * <p>
 * config.setFontLevel(2);
 * <p>
 * <p>
 * return config;
 * <p>
 * <p>
 * <p>
 * 初始化EREngine
 * <p>
 * private void initEngine() {
 * <p>
 * mIEREngine = (IEngineService)
 * ERManager.getService(ERManager.ERENGINE_SERVICE);
 * <p>
 * <p>
 * mIEREngine.setApplication(this.getApplication());
 * <p>
 * mIEREngine.setContext(this);
 * <p>
 * mIEREngine.setConfig(initConfig());
 * <p>
 * mIEREngine.setRender(new Render2(mRenderView));
 * <p>
 * mIEREngine.setRecorder(new FileRecorder(this));
 * <p>
 * mIEREngine.setUpdateViewEnabled(true);
 * <p>
 * <p>
 * <p>
 * 打開書籍
 * <p>
 * private void openBook(String pathname) {
 * <p>
 * mIEREngine.setBook(new Book(pathname));
 * <p>
 * mIEREngine.getAction().openBook();
 * <p>
 */
public class EREngine implements IEngineService {

	/** The Constant TAG. */
	static private final String TAG = "EREngine";

	/** The m engine. */
	static private EREngine mEngine;

	/** The m book. */
	private Book mBook;

	/** The m action. */
	private ReaderWrapper mReaderWrapper;

	/** The m config. */
	private EngineConfig mConfig;

	/** The m render. */
	private BaseRender mRender;

	/** The m engine handler. */
	private EngineHandler mEngineHandler;

	/** The m recorder. */
	private IRecorder mRecorder;

	/** The m engine error. */
	private EngineCode mEngineError;

	/** The m comments. */
	// private Comments mComments;

	/** The m application. */
	private Application mApplication;

	/** The m context. */
	private Context mContext;

	/** The m update view. */
	private boolean mUpdateView = true;

	/**
	 * 獲取單例對象.
	 * 
	 * @return EREngine對象
	 */
	static protected EREngine getInterface() {
		if (null == mEngine) {
			mEngine = new EREngine();
			mEngine.initEngine();
		}

		return mEngine;
	}

	/**
	 * 設置自定義配置信息
	 * 
	 * @param config
	 *            EngineConfig對象
	 */
	public void setConfig(EngineConfig config) {
		mConfig = config;

		if (mReaderWrapper != null) {
			mReaderWrapper.updateFromConfig(config);
		}

		Logger.updateFromConfig(config);
	}

	/**
	 * 獲取配置對象實例
	 */
	public EngineConfig getConfig() {
		return mConfig;
	}

	/**
	 * 設置渲染對象
	 */
	public void setRender(BaseRender render) {
		mRender = render;
	}

	/**
	 * 獲取渲染對象實例
	 */
	public BaseRender getRender() {
		return mRender;
	}

	/**
	 * 設置數據存儲對象
	 */
	public void setRecorder(IRecorder recorder) {
		mRecorder = recorder;
	}

	/**
	 * 獲取數據存儲對象實例
	 */
	public IRecorder getRecorder() {
		return mRecorder;
	}

	/**
	 * 設置Application對象
	 */
	public void setApplication(Application app) {
		mApplication = app;
	}

	/**
	 * 獲取Application對象實例
	 */
	public Application getApplication() {
		return mApplication;
	}

	/**
	 * 設置Context對象
	 */
	public void setContext(Context context) {
		mContext = context;
	}

	/**
	 * 獲取Context對象實例
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * 設置是否自動更新顯示
	 * 
	 * @param enabled
	 *            true, 開啟自動更新. false, 關閉自動更新
	 */
	public void setUpdateViewEnabled(boolean enabled) {
		mUpdateView = enabled;
	}

	/**
	 * 獲取當前是否為自動更新
	 */
	public boolean getUpdateViewEnabled() {
		return mUpdateView;
	}

	/**
	 * 獲取IAction接口對象實例
	 */
	public IAction getAction() {
		if (null == mReaderWrapper) {
			selectWrapper();

			mReaderWrapper.setBook(mBook);
			mReaderWrapper.updateFromConfig(mConfig);
		}

		return mReaderWrapper;
	}

	/*
	 * about comment begin
	 */

	// /**
	// * @deprecated
	// * Sets the comment manager.
	// *
	// * @param commentManager the new comment manager
	// */
	// public void setCommentManager(Comments commentManager) {
	// mComments = commentManager;
	//
	// if (mComments != null) {
	// mComments.initComment();
	// }
	// }
	//
	// /**
	// * @deprecated
	// * Gets the comment manager.
	// *
	// * @return the comment manager
	// */
	// public Comments getCommentManager() {
	// if (null == mComments) {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	//
	// return mComments;
	// }
	//
	// /**
	// * @deprecated
	// * Enter comment.
	// *
	// * @param enterCall the enter call
	// */
	// public void enterComment(ICommentHandlerCall enterCall) {
	// ICommentHandlerCall call = commentDefHandler(enterCall);
	//
	// if (mComments != null) {
	// mComments.enterComment(commentDefHandler(call));
	// } else {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	// }
	//
	// /**
	// * @deprecated
	// * Exit comment.
	// *
	// * @param exitCall the exit call
	// * @param reloadCall the reload call
	// */
	// public void exitComment(ICommentHandlerCall exitCall,
	// ICommentHandlerCall reloadCall) {
	// ICommentHandlerCall call1 = commentDefHandler(exitCall);
	// ICommentHandlerCall call2 = commentDefHandler(reloadCall);
	//
	// if (mComments != null) {
	// mComments.exitComment(call1, call2);
	// } else {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	// }
	//
	// /**
	// * @deprecated
	// * Load comment.
	// *
	// * @param refreshCall the refresh call
	// * @return true, if successful
	// */
	// public boolean loadComment(ICommentHandlerCall refreshCall) {
	// ICommentHandlerCall call = commentDefHandler(refreshCall);
	// boolean res = false;
	//
	// if (mComments != null) {
	// res = mComments.loadComment(call);
	// } else {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	//
	// return res;
	// }
	//
	// /**@deprecated
	// * Save comment.
	// *
	// * @param saveCall the save call
	// * @return true, if successful
	// */
	// public boolean saveComment(ICommentHandlerCall saveCall) {
	// ICommentHandlerCall call = commentDefHandler(saveCall);
	// boolean res = false;
	//
	// if (mComments != null) {
	// res = mComments.saveComment(call);
	// } else {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	//
	// return res;
	// }
	//
	// /**
	// * @deprecated
	// * Delete comment.
	// *
	// * @param delCall the del call
	// * @return true, if successful
	// */
	// public boolean deleteComment(ICommentHandlerCall delCall) {
	// ICommentHandlerCall call = commentDefHandler(delCall);
	// boolean res = false;
	//
	// if (mComments != null) {
	// res = mComments.deleteComment(call);
	// } else {
	// if (mEngineError != null) {
	// mEngineError
	// .setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT);
	// handleError();
	// }
	// }
	//
	// return res;
	// }
	//
	// /**
	// * @deprecated
	// * Comment def handler.
	// *
	// * @param call the call
	// * @return the i comment handler call
	// */
	// private ICommentHandlerCall commentDefHandler(final ICommentHandlerCall
	// call) {
	// ICommentHandlerCall handler = new ICommentHandlerCall() {
	// @Override
	// public void commentCall() {
	// if (call != null) {
	// call.commentCall();
	// }
	//
	// handleError();
	// }
	// };
	//
	// return handler;
	// }

	/*
	 * about comment end
	 */

	/**
	 * 設置Book對象.
	 * 
	 * @param book
	 *            Book對象實例
	 */
	public void setBook(Book book) {
		if (null == book) {
			return;
		}

		// 如果Book由ID構造,則先從數據庫中查詢書籍的全路徑，再構造Book
		// 如果Book由路徑構造，則查詢BookId，再設置BookId.
		IDatabaseService dbs = (IDatabaseService) ERManager
				.getService(ERManager.DATABASE_SERVICE);
		int id = book.getBookId();
		if (!book.isExists() && (id != -1)) {
			if (dbs.queryBook(book)) {
				String path = book.getBookPath();
				book.createFromPath(path);
				book.setBookId(id);
			}
		} else if (book.isExists() && (-1 == id)) {
			id = dbs.queryBook(book.getBookPath());
			if (id != -1) {
				book.setBookId(id);
			}
		}

		mBook = book;
		selectWrapper();

		if (mReaderWrapper != null) {
			mReaderWrapper.setBook(mBook);
			mReaderWrapper.updateFromConfig(mConfig);
		}
	}

	/**
	 * 獲取Book對象.
	 * 
	 * @see com.easyview.ebook.reader.engine.model.Book
	 */
	public Book getBook() {
		return mBook;
	}

	/**
	 * 獲取最後信息碼.
	 * 
	 * @see com.easyview.ebook.reader.engine.core.EngineCode
	 */
	public int getLastError() {
		EngineCode error = EngineCode.getInstance();

		if (error != null) {
			return error.getLastCode();
		}

		return EngineMsgCode.ENGINE_ERROR_CODE_ERROR;
	}

	/**
	 * 設置錯誤處理回調.
	 * 
	 * @param code
	 *            信息代碼
	 * @see com.easyview.ebook.reader.engine.core.EngineCode
	 * @param handler
	 *            錯誤處理的回調接口
	 */
	public void setErrorHandler(int code, IErrorCall handler) {
		mEngineError.setErrorHandler(code, handler);
	}

	/**
	 * 手動渲染.當getUpdateViewEnabled()為false時，才需要調用該方法.
	 */
	public void render() {
		if (mRender != null) {
			mRender.showContent(mBook);
		} else {
			Logger.dLog(TAG, "ERDEngine: mRender = " + mRender);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IEngineService#getVersion()
	 */
	@Override
	public String getVersion() {
		return IEngineService.VERSION_NUMBER;
	}

	/**
	 * 釋放資源.通常由EReaderManager調用
	 */
	protected void free() {
		if (mReaderWrapper != null) {
			mReaderWrapper.free();
			mReaderWrapper = null;
		}

		if (mRender != null) {
			mRender.free();
			mRender = null;
		}

		if (mEngineError != null) {
			mEngineError.free();
			mEngineError = null;
		}

		// if (mComments != null) {
		// mComments.free();
		// mComments = null;
		// }

		if (mEngine != null) {
			mEngine = null;
		}

		Logger.dLog(TAG, "ERDEngine: call free() -> mReaderWrapper = "
				+ mReaderWrapper + " mErrorCode = " + mEngineError
				+ " mRender = " + mRender + " mEngine = " + mEngine);

		Logger.free();
	}

	/**
	 * Gets the message handler.
	 * 
	 * @return the message handler
	 */
	protected EngineHandler getMessageHandler() {
		return mEngineHandler;
	}

	/**
	 * Instantiates a new eR engine.
	 */
	private EREngine() {
	}

	/**
	 * Inits the adobe engine.
	 */
	private void initEngine() {
		mConfig = new EngineConfig();
		mEngineHandler = new EngineHandler();
		mEngineError = EngineCode.getInstance();
	}

	/**
	 * Select action.
	 */
	private void selectWrapper() {
		// if (mReaderWrapper != null) {
		// mReaderWrapper.free();
		// }

		if ((mBook != null)) {
			switch (mBook.getReaderType()) {
			case Book.ReaderType.READER_TYPE_ADOBE:
				// select AdobeWrapper to handle
				mReaderWrapper = AdobeWrapper.getInstance();
				break;
			case Book.ReaderType.READER_TYPE_FBREADER:
				// select FbreaderWrapper to handle
				mReaderWrapper = FbreaderWrapper.getInstance();
				break;
			default:
				// default choose FbreaderWrapper
				mReaderWrapper = FbreaderWrapper.getInstance();
				break;
			}
		}
	}

	/**
	 * Handle error.
	 */
	private void handleError() {
		if (mEngineError != null) {
			int code = mEngineError.getLastCode();

			Logger.dLog(TAG, "handle error code = " + code);
			if (!mEngineError.noError(code)) {
				Message msg = Message.obtain();
				msg.what = EngineCode.ErrroHandler.EXECUTE_HANDLER;
				msg.arg1 = code;
				mEngineError.getMessageHandler().sendMessage(msg);
			}
		}
	}

	/**
	 * The Class EngineHandler.
	 */
	public class EngineHandler {

		/** The Constant UPDATE_VIEW. */
		static public final int UPDATE_VIEW = 0;

		/**
		 * Instantiates a new engine handler.
		 */
		public EngineHandler() {
		}

		/**
		 * Update view.
		 */
		private void updateView() {
			if (getUpdateViewEnabled()) {
				render();
			}
		}

		/** The m handler. */
		private Handler mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_VIEW:
					updateView();
					break;
				}

				super.handleMessage(msg);
			}
		};

		/**
		 * Send message.
		 * 
		 * @param what
		 *            the what
		 */
		public void sendMessage(int what) {
			mHandler.sendEmptyMessage(what);
		}
	}
}
