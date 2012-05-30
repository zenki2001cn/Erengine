/**
 * @file       EREngine.java
 *
 * @revision:  0.5 
 *
 * @version    0.5.6
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-06-05 下午05:29:59
 * 
 * @history:
 * 				0.5.6
 * 				1. 修復數據庫部分BUG.
 * 				2. 添加數據庫的若幹接口.
 * 				3. 修改獲取Message部分代碼.
 * 				4. 添加獲取當前頁Summary方法.
 * 
 * 				0.5.5
 * 				1. FBReader內核更新至1.1.9.
 * 				2. 修復數據庫接口的若幹問題.
 * 				3. 添加SDCARD文件瀏覽界面.
 * 
 * 				0.5.4
 * 				1. 优化FBReader的遍历列表的处理方式.
 * 				2. 修正部分EPUB书段落第一行有多余空格导致显示的问题.
 * 
 * 				0.5.3
 * 				1. 修改旋屏的接口方法，添加視圖的寬度和高度作為參數.(setScreenLandMode(boolean land, int viewWidth, int viewHeight))
 * 				2. 調整FBReader對于<tr>、<td>標識的處理.(XHTMLTagTrAction.java, XHTMLTagTdAction.java, FBTextKind.java)
 * 				3. 調整FBReader對于圖片元素的處理.(XHTMLTagImageAction.java)
 * 				4. 調整FBReader處理文字的行間距.(assets/default/styles.xml)
 * 				5. 添加處理CSS文件的庫和類.
 * 
 * 				0.5.2
 * 				1. 修正Adobe翻上一章節無法正確跳轉的問題.
 * 				2. 添加根據字體等級進行縮放的接口pageZoomLevel(int lv).
 * 				3. FBreader添加獲取當前頁文字內容的接口doFetchText();
 * 
 * 				0.5.1
 * 				1. 修改錯誤判斷的邏輯.
 * 				2. 修正Adobe字體最大或最小時，翻頁會調用字體最大或最小的回調問題.
 * 				3. 修正了部分PDF頁碼計算錯誤的問題.
 * 
 * 				0.5.0
 * 				1. 添加數據庫支持的Provider.
 * 				2. 添加數據庫操作的接口IDatabaseService.
 * 				3. 添加IRecorder的數據庫實現類SqliteRecorder.
 * 				4. 添加書籍大小的屬性.
 * 				5. 修改了部分Book的屬性相關方法.
 * 				6. 修改了書簽，高亮信息的部分代碼.
 * 
 * 				0.4.7r2
 * 				1. 可配置支持加载本地共享库.
 * 
 * 				0.4.7
 * 				1. 修正部分注釋.
 * 				2. 修正部分代碼結構.
 * 				3. 修正FBReader畫詞不准確的Bug.
 * 
 * 				0.4.6
 * 				1. EngineConfig添加設置library目錄配置.
 * 				2. EngineConfig添加自動讀取數據庫選項配置.
 * 				3. FBReader修正字體縮小等級計算錯誤的Bug.
 * 				4. Render类添加获取Bitmap.Config的方法.
 * 
 * 				0.4.5r2
 * 				1. 修改Action類名為ReaderWrapper，并獨立IAction接口
 * 				2. 修改個別類結構
 * 				
 * 				0.4.5
 * 				1. FBReader添加画重点功能
 * 				2. Book添加ID属性
 * 				3. 添加注释并导出javadoc 
 * 
 * 				0.4.4
 * 				1. Adobe添加画重点功能
 * 				2. 独立BookMark和BookSearch的类结构
 * 				3. TODO: FBReader添加画重点功能. (Finished in 0.4.5)
 * 				4. TODO: Book添加ID属性. (Finished in 0.4.5)
 * 				5. TODO: 添加注释并导出javadoc. (Finished in 0.4.5)
 * 
 * 				0.4.3
 * 				1. 修正Adobe旋屏bug
 * 				2. 添加FBreader旋屏功能
 * 				3. 添加FBReaderopenLink功能
 * 
 * 				0.4.2r2
 * 				1. 修正讀取Meta信息引起的crash問題
 * 				2. 修正Adobe無法打開600*800以上分辨率的BUG
 * 				3. 修正TaskManager退出調用cancelProcess可能引起的crash
 * 				4. Adobe添加openLink功能
 * 				5. Adobe添加旋屏功能
 * 
 * 				0.4.2
 * 				1. 添加手势操作功能
 * 
 * 				0.4.1
 * 				1. 移除HYF支持模块
 * 				2. FBReader支持打开TXT和HTML文档
 * 				3. TXT支持GBK、UTF-8、UTF-16等编码
 * 				4. HTML通过content标识分辨编码格式
 * 				5. 修改Book的Meta信息结构
 * 
 * 				0.4.0
 * 				1. 更新FBReader代码为1.1.0版本
 * 				2. 加入FBReader fb2格式处理模块
 * 				3. 修改判断上下页机制，修正最后一页向前翻页问题
 * 				4. 修正上一页为章节头时，向上翻页后再向下翻页，导致内容错位
 * 				5. 修改预读上一页和下一页的机制，消除上下翻页内容不正确的问题
 * 
 * 				0.3.3r2
 * 				1. 修正FBReader放大缩小字体后导致无法跳转的问题
 * 				2. 修正FileRecorder无法保存书签信息的问题
 * 
 * 				0.3.3
 * 				1. 添加FBreader书签功能
 * 				2. 添加Adobe书签功能
 * 				3. [UI]:添加书签相关布局
 * 
 * 				0.3.2r2
 * 				1. 调整搜索栏布局
 * 				2. 添加对话框风格
 * 
 * 				0.3.2
 * 				1. 添加Adobe搜索功能
 * 				2. 添加FBreader搜索功能
 * 				3. 独立标题栏和底栏视图类
 * 
 * 				0.3.1
 * 				1. 添加水平和垂直移动机制
 * 				2. [UI]:添加触摸缩放字体大小功能
 * 
 * 				0.3.0
 * 				1. 修改页码计算方法，修正跳页引起的页码问题。
 * 				2. [UI]:添加控制面板，完成目录和跳转功能。
 * 				3. [UI]:添加进度条跳转功能。
 * 				4. 添加手势移动的处理模块。
 * 
 * 				0.2.3
 * 				1. 修正載入後字符重疊問題，并解決定位不准的問題。
 * 				2. 改進TaskManager的性能，保証任務隊列中最多只有五個任務。
 * 				3. FbreaderAdapter添加定位相關方法。
 * 				4. 修改字體縮放的策略，FbreaderAdapter添加獲取字體大小的接口。
 * 				5. Adobe打開epub格式支持DRM。
 * 
 * 				0.2.2r2
 * 				1. 優化isEndParagraph()方法調用，提高翻下頁的速度
 * 
 * 				0.2.2
 * 				1. 修正載入最後頁碼內容時，前幾個文字會疊加的問題，但是不能准確定位。
 * 				2. 修改Action.closeBook()方法，將該代碼放在UI線程做處理以避免程序關閉後無法執行的問題
 * 
 * 				0.2.1r2
 * 				1. 修正FBReader字體縮放的問題
 * 
 * 				0.2.1
 * 				1. FBReader支持目錄瀏覽、目錄跳轉功能
 * 
 * 				0.2.0r2
 * 				1.Modify: 優化翻頁機制，基本解決最後一頁和第一頁的問題
 * 
 * 				0.2.0
 * 				1.Add: 添加FBReader顯示，翻頁，放大，縮小功能支持
 * 				2.Modify: 修正FBReader翻页错乱问题。
 * 				3.TODO: 添加翻页缓存机制。(FIXED in 0.2.3)
 * 
 * 				0.1.4
 * 				1.Modify: 改進Book類中的文件類型識別方法		
 * 				2.Add: FBReader插件
 * 				3.TODO: FBReader顯示，翻頁，放大，縮小功能支持 (FIXED in 0.2.0)	
 * 
 * 				0.1.3
 * 				1.Add: 添加批注功能。(TODO:未完善，保存、删除、加载功能)
 * 
 * 				0.1.2
 * 				1.Modify: 修改Action的todo、predo機制，使用開關控制todo、perdo的執行，并能夠在子類中手動調用。
 * 				2.Add: HYF支持上下翻頁、字體放大和縮小功能。
 * 				3.TODO: HyfAdapter的臨時文件路徑暫時為/sdcard/.hyf_temp。(HYF暂时停止更新)			
 * 
 * 				0.1.1
 * 				1.Modify: 修改Action的todo、predo機制
 * 				2.Add: 支持HYF打開并顯示txt文檔，暫不支持翻頁，字體放大等功能
 * 
 * 				0.1.0
 * 				1.Modify: 线程模型的任务分配机制
 * 				2.Fixed: 任务管理可能没有退出
 * 				3.TODO: 添加清除记录文件功能.(Finished in 0.5.0)
 * 
 * 				0.0.9r3
 * 				1.Fixed: 向下翻页调用错误
 * 				2.Fixed: 执行错误回调后会将错误代码变更为0 
 * 				3.TODO: 任务管理可能没有退出 (FIXED in 0.1.0)
 * 				4.Fixed: 恢復0.0.9r2的變更，Adobe必须固定包的格式
 * 
 * 				0.0.9r2
 * 				1.Modify: Change package's name "com.foxconn.ebook.jni" to "com.foxconn.ebook.reader.lib.adobe" 
 * 
 * 				0.0.9
 * 				1. Add: 添加支持密码验证.
 * 				2. Modify: 修改书籍信息装载机制.
 * 				3. Modify: 修改错误处理机制，避免最后的错误代码可能被覆盖.
 * 
 */

package com.foxconn.ebook.reader.engine.core;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.foxconn.ebook.reader.engine.util.EngineConfig;
import com.foxconn.ebook.reader.engine.core.adobe.AdobeWrapper;
import com.foxconn.ebook.reader.engine.core.fbreader.FbreaderWrapper;
import com.foxconn.ebook.reader.engine.core.EngineCode;
import com.foxconn.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.foxconn.ebook.reader.engine.core.IErrorCall;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.util.Logger;
import com.foxconn.ebook.reader.engine.util.recorder.IRecorder;
import com.foxconn.ebook.reader.engine.util.render.BaseRender;

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
	 * @see com.foxconn.ebook.reader.engine.model.Book
	 */
	public Book getBook() {
		return mBook;
	}

	/**
	 * 獲取最後信息碼.
	 * 
	 * @see com.foxconn.ebook.reader.engine.core.EngineCode
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
	 * @see com.foxconn.ebook.reader.engine.core.EngineCode
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
	 * @see com.foxconn.ebook.reader.engine.core.IEngineService#getVersion()
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
