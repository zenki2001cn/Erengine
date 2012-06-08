/**
 * @file       ReaderWrapper.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-1 下午05:23:45 
 */

package com.easyview.ebook.reader.engine.core;

import android.graphics.Bitmap;
import android.os.Message;

import com.easyview.ebook.reader.engine.core.EREngine.EngineHandler;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.core.TaskManager.ActionTask;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookSearchInfo;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.easyview.ebook.reader.engine.model.BookSearchInfo.BookSearchResult;
import com.easyview.ebook.reader.engine.util.EngineConfig;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.recorder.IRecorder;

/**
 * 閱讀器包裹類，該類為抽象類，子類為AdobeWrapper和FbreaderWrapper.
 * <p>
 * 該類實現IAction接口，提供閱讀器操作接口.
 */
public abstract class ReaderWrapper implements IAction {

	/** The Constant TAG. */
	static private final String TAG = "ReaderWrapper";

	/** The m 錯誤代碼類對象. */
	private EngineCode mEngineError;

	/** The m 任務管理對象. */
	private TaskManager mTaskManager;

	/** The m EREngine. */
	private EREngine mEngine;

	/** The m 需要打開的Book對象. */
	private Book mOpenedBook;

	/** The m 渲染邊界標識，為false表示可以渲染，為true則停止渲染. */
	private boolean mIsBorder = false;

	/** The m 觸控屏幕時加鎖，如果為true，則禁止觸發相關方法. */
	private boolean mTouchLinkLock = false;

	/** The m 最後傳入的Todo回調接口. */
	private IActionCall mLastTodoAction;

	/** The m 最後傳入的Predo回調接口. */
	private IActionCall mLastPredoAction;

	/** The m 是否自動執行回調接口. */
	private boolean mAutoExecuteTodoAction = true;

	/** The m 是否橫屏模式，true為橫屏，false為豎屏. */
	private boolean mLandMode = false;

	/** The m 當前字體等級，為2. */
	private int mCurFontLevel = 2;

	/** The m bitmap config. */
	private Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#openBook()
	 */
	public void openBook() {
		openBook(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#openBook(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void openBook(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			public boolean action() {
				try {
					loadBookDB();
				} catch (Exception e) {
					e.printStackTrace();
					Logger.eLog(TAG, "load book info error" + e);
				}

				iLoadBook();

				// debug for load chapter information, can be delete if no need
				// iLoadChapterInformation();
				// iLoadAllHighlight();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageJumpTo(int)
	 */
	public void pageJumpTo(final int pageNum) {
		pageJumpTo(pageNum, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageJumpTo(int,
	 * com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageJumpTo(final int pageNum, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			public boolean action() {
				iPageJumpTo(pageNum);

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageUp()
	 */
	public void pageUp() {
		pageUp(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#pageUp(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageUp(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iPageUp();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageDown()
	 */
	public void pageDown() {
		pageDown(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#pageDown(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageDown(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iPageDown();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageZoomLevel(int)
	 */
	public void pageZoomLevel(int lv) {
		pageZoomLevel(lv, null, null);
	}
	
	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageZoomLevel(int, com.easyview.ebook.reader.engine.core.IActionCall, com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageZoomLevel(final int lv, IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iPageZoomLevel(lv);

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageZoomIn()
	 */
	public void pageZoomIn() {
		pageZoomIn(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#pageZoomIn(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageZoomIn(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iPageZoomIn();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#pageZoomOut()
	 */
	public void pageZoomOut() {
		pageZoomOut(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#pageZoomOut(com.easyview.
	 * ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void pageZoomOut(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iPageZoomOut();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#enterSearchMode()
	 */
	public void enterSearchMode() {
		enterSearchMode(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#enterSearchMode(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void enterSearchMode(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iEnterSearchMode();
				iSearchUpdateInfo();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#searchGoNext()
	 */
	public void searchGoNext() {
		searchGoNext(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#searchGoNext(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void searchGoNext(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iSearchGoNext();
				iSearchUpdateInfo();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#searchGoPrevious()
	 */
	public void searchGoPrevious() {
		searchGoPrevious(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#searchGoPrevious(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void searchGoPrevious(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iSearchGoPrevious();
				iSearchUpdateInfo();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#searchGotoByResult(
	 * BookSearchInfo.BookSearchResult result)
	 */
	public void searchGotoResult(BookSearchInfo.BookSearchResult result) {
		searchGotoResult(result, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#searchGotoByResult(
	 * BookSearchInfo.BookSearchResult result, IActionCall preDo, IActionCall
	 * toDo)
	 */
	public void searchGotoResult(final BookSearchInfo.BookSearchResult result,
			IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iSearchGoto(result);
				iSearchUpdateInfo();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#quitSearch()
	 */
	public void quitSearch() {
		quitSearch(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#quitSearch(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void quitSearch(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iQuitSearch();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#addBookmark()
	 */
	public void addBookmark() {
		addBookmark(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#addBookmark(com.easyview.
	 * ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void addBookmark(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iAddBookmark();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#gotoBookmark(com.easyview
	 * .ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	public void gotoBookmark(Bookmark bookmark) {
		gotoBookmark(bookmark, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#gotoBookmark(com.easyview
	 * .ebook.reader.engine.model.BookMarkInfo.Bookmark, com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void gotoBookmark(final Bookmark bookmark, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iGotoBookmark(bookmark);

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#gotoBookmark()
	 */
	public void gotoBookmark() {
		gotoBookmark(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#gotoBookmark(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void gotoBookmark(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iGotoBookmark();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#deleteBookmark(com.easyview
	 * .ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	public void deleteBookmark(Bookmark bookmark) {
		deleteBookmark(bookmark, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#deleteBookmark(com.easyview
	 * .ebook.reader.engine.model.BookMarkInfo.Bookmark,
	 * com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void deleteBookmark(final Bookmark bookmark, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iDeleteBookmark(bookmark);

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#delAllBookmark()
	 */
	public void delAllBookmark() {
		delAllBookmark(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#delAllBookmark(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void delAllBookmark(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iDelAllBookmark();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#loadChapterInformation()
	 */
	public void loadChapterInformation() {
		loadChapterInformation(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#loadChapterInformation(com
	 * .easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void loadChapterInformation(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iLoadChapterInformation();
				iChapterUpdateInfo();

				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#chapterJumpByTitle(java.
	 * lang.String)
	 */
	public void chapterJumpByTitle(final String title) {
		chapterJumpByTitle(title, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#chapterJumpByTitle(java.
	 * lang.String, com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void chapterJumpByTitle(final String title, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iChapterJumpByTitle(title);
				iChapterUpdateInfo();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#chapterJumpByIndex(int)
	 */
	public void chapterJumpByIndex(final int index) {
		chapterJumpByIndex(index, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#chapterJumpByIndex(int,
	 * com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void chapterJumpByIndex(final int index, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iChapterJumpByIndex(index);
				iChapterUpdateInfo();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#chapterUp()
	 */
	public void chapterUp() {
		chapterUp(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#chapterUp(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void chapterUp(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iChapterUp();
				iChapterUpdateInfo();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#chapterDown()
	 */
	public void chapterDown() {
		chapterDown(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#chapterDown(com.easyview.
	 * ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void chapterDown(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iChapterDown();
				iChapterUpdateInfo();

				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#closeBook()
	 */
	public void closeBook() {
		closeBook(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#closeBook(com.easyview.ebook
	 * .reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void closeBook(IActionCall preDo, IActionCall toDo) {
		// 關閉書籍通常在onStop或onDestroy時調用，該操作
		// 放在線程處理會造成程序無法調用，故直接放在UI線程處理
		// IActionCall action = new IActionCall() {
		// @Override
		// public boolean action() {
		// Logger.vLog(TAG, "prepare close Book");
		// return true;
		// }
		// };
		//
		// IActionCall todo = closeAction(toDo);
		// addTask(getCurAdapter(), action, preDo, todo);

		closeAction(preDo, toDo).action();

		Book book = getBook();
		if (book != null) {
			book.free();
		}
	}

	
	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#setScreenLandMode(boolean, int, int)
	 */
	public void resizeScreen(int viewWidth, int viewHeight) {
		resizeScreen(viewWidth, viewHeight, null, null);
	}

	
	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#setScreenLandMode(boolean, int, int, com.easyview.ebook.reader.engine.core.IActionCall, com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void resizeScreen(final int viewWidth, final int viewHeight, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iResizeScreen(viewWidth, viewHeight);
				handleError();

				return true;
			}
		};

		IActionCall preload = preloadAction(toDo);
		addTask(getCurAdapter(), action, preDo, preload);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#openLinkByTouch(int,
	 * int)
	 */
	public void openLinkByTouch(int x, int y) {
		openLinkByTouch(x, y, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#openLinkByTouch(int,
	 * int, com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void openLinkByTouch(final int x, final int y, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iOpenLinkByTouch(x, y);
				handleError();
				setTouchLock(false);

				return true;
			}
		};

		if (!mTouchLinkLock) {
			IActionCall preload = preloadAction(toDo);
			addTask(getCurAdapter(), action, preDo, preload);
			setTouchLock(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#onTouchDown(double,
	 * double)
	 */
	public void onTouchDown(final double x, final double y) {
		Book book = getBook();

		if (book.getReaderType() == Book.ReaderType.READER_TYPE_ADOBE) {
			IActionCall action = new IActionCall() {
				@Override
				public boolean action() {
					iStartHighlightByXY(x, y);
					handleError();

					return true;
				}
			};

			addTask(getCurAdapter(), action, null, null);
		} else if (book.getReaderType() == Book.ReaderType.READER_TYPE_FBREADER) {
			iStartHighlightByXY(x, y);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#onTouchMove(double,
	 * double)
	 */
	public void onTouchMove(final double x, final double y) {
		Book book = getBook();

		if (book.getReaderType() == Book.ReaderType.READER_TYPE_ADOBE) {
			IActionCall action = new IActionCall() {
				@Override
				public boolean action() {
					iMoveHighlightByXY(x, y);
					handleError();

					return true;
				}
			};

			addTask(getCurAdapter(), action, null, null);
		} else if (book.getReaderType() == Book.ReaderType.READER_TYPE_FBREADER) {
			iMoveHighlightByXY(x, y);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#onTouchUp(double,
	 * double)
	 */
	public void onTouchUp(final double x, final double y) {
		Book book = getBook();

		if (book.getReaderType() == Book.ReaderType.READER_TYPE_ADOBE) {
			IActionCall action = new IActionCall() {
				@Override
				public boolean action() {
					iEndHighlightByXY(x, y);
					handleError();

					return true;
				}
			};

			addTask(getCurAdapter(), action, null, null);
		} else if (book.getReaderType() == Book.ReaderType.READER_TYPE_FBREADER) {
			iEndHighlightByXY(x, y);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#delHighlight(com.easyview
	 * .ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	public void delHighlight(BookEmphasis emphasis) {
		delHighlight(emphasis, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#delHighlight(com.easyview
	 * .ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis,
	 * com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void delHighlight(final BookEmphasis emp, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iDelHighlight(emp);
				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#delAllHighlight()
	 */
	public void delAllHighlight() {
		delAllHighlight(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#delAllHighlight(com.easyview
	 * .ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void delAllHighlight(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iDelAllHighlight();
				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#gotoHighlight(com.easyview
	 * .ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	public void gotoHighlight(BookEmphasis emphasis) {
		gotoHighlight(emphasis, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IAction#gotoHighlight(com.easyview
	 * .ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis,
	 * com.easyview.ebook.reader.engine.core.IActionCall,
	 * com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void gotoHighlight(final BookEmphasis emphasis, IActionCall preDo,
			IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iGotoHighlight(emphasis);
				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#doFetchText()
	 */
	public void fetchText() {
		fetchText(null, null);
	}
	
	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.IAction#doFetchText(com.easyview.ebook.reader.engine.core.IActionCall, com.easyview.ebook.reader.engine.core.IActionCall)
	 */
	public void fetchText(IActionCall preDo, IActionCall toDo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				iDoFetchText();
				handleError();

				return true;
			}
		};

		addTask(getCurAdapter(), action, preDo, toDo);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IAction#cancelActions()
	 */
	public void cancelActions() {
		if (mTaskManager != null) {
			Logger.dLog(TAG, "Cancel all exists action");
			mTaskManager.clearTask();
			setLastCode(EngineMsgCode.ENGINE_NORMAL_TASK_CLEAR);
		}
	}

	/**
	 * Sets the touch lock.
	 * 
	 * @param lock
	 *            the new touch lock
	 */
	protected void setTouchLock(boolean lock) {
		mTouchLinkLock = lock;
	}

	/**
	 * Gets the touch lock.
	 * 
	 * @return the touch lock
	 */
	protected boolean getTouchLock() {
		return mTouchLinkLock;
	}

	/**
	 * Preload action.
	 * 
	 * @param todo
	 *            the todo
	 * @return the i action call
	 */
	protected IActionCall preloadAction(final IActionCall todo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				IActionCall preload = new IActionCall() {
					@Override
					public boolean action() {
						boolean preload = mEngine.getConfig().getPreloadMode();

						if (preload) {
							preloadPage();
						}

						return true;
					}
				};

				addTask(getCurAdapter(), preload, null, null);

				if (todo != null) {
					todo.action();
				}

				return true;
			}
		};

		return action;
	}

	/**
	 * Preload previous page action.
	 * 
	 * @param todo
	 *            the todo
	 * @return the i action call
	 */
	protected IActionCall preloadPreviousPageAction(final IActionCall todo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				IActionCall preload = new IActionCall() {
					@Override
					public boolean action() {
						boolean preload = mEngine.getConfig().getPreloadMode();

						if (preload) {
							preloadPreviousPage();
						}

						return true;
					}
				};
				addTask(getCurAdapter(), preload, null, null);

				if (todo != null) {
					todo.action();
				}

				return true;
			}
		};

		return action;
	}

	/**
	 * Preload next page action.
	 * 
	 * @param todo
	 *            the todo
	 * @return the i action call
	 */
	protected IActionCall preloadNextPageAction(final IActionCall todo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				IActionCall preload = new IActionCall() {
					@Override
					public boolean action() {
						boolean preload = mEngine.getConfig().getPreloadMode();

						if (preload) {
							preloadNextPage();
						}

						return true;
					}
				};
				addTask(getCurAdapter(), preload, null, null);

				if (todo != null) {
					todo.action();
				}

				return true;
			}
		};

		return action;
	}

	/**
	 * Close action.
	 * 
	 * @param preDo
	 *            the pre do
	 * @param todo
	 *            the todo
	 * @return the i action call
	 */
	protected IActionCall closeAction(final IActionCall preDo,
			final IActionCall todo) {
		IActionCall action = new IActionCall() {
			@Override
			public boolean action() {
				Logger.vLog(TAG, "call closeAction()");

				if (preDo != null) {
					preDo.action();
				}

				iCloseBook();
				handleError();

				if (todo != null) {
					todo.action();
				}

				return true;
			}
		};

		return action;
	}

	/**
	 * Instantiates a new reader wrapper.
	 */
	protected ReaderWrapper() {
		if (null == mEngine) {
			mEngine = EREngine.getInterface();
		}

		if (null == mTaskManager) {
			mTaskManager = TaskManager.getInstance();
		}

		if (null == mEngineError) {
			mEngineError = EngineCode.getInstance();
		}

		setBitmapConfig(EREngine.getInterface().getRender().getBitmapConfig());
		setDefaultVal();
	}

	/**
	 * Free.
	 */
	protected void free() {
		if (mTaskManager != null) {
			mTaskManager.free();
			mTaskManager = null;
		}

		if (mEngineError != null) {
			mEngineError.free();
			mEngineError = null;
		}

		if (mEngine != null) {
			mEngine = null;
		}

		Logger.dLog(TAG, "ReaderWrapper free mTaskManager: " + mTaskManager
				+ " mErrorCode: " + mEngineError);
	}

	/**
	 * Sets the book.
	 * 
	 * @param book
	 *            the new book
	 */
	protected void setBook(Book book) {
		mOpenedBook = book;

		updateFromConfig(mEngine.getConfig());
	}

	/**
	 * Gets the book.
	 * 
	 * @return the book
	 */
	protected Book getBook() {
		return mOpenedBook;
	}

	/**
	 * Gets the engine.
	 * 
	 * @return the engine
	 */
	protected EREngine getEngine() {
		return mEngine;
	}

	/**
	 * Checks if is border.
	 * 
	 * @return true, if is border
	 */
	protected boolean isBorder() {
		return mIsBorder;
	}

	/**
	 * Sets the border.
	 * 
	 * @param border
	 *            the new border
	 */
	protected void setBorder(boolean border) {
		mIsBorder = border;
	}

	/**
	 * Can work.
	 * 
	 * @return true, if successful
	 */
	protected boolean canWork() {
		if ((null == getCurAdapter()) || (null == mOpenedBook)) {
			Logger.eLog(TAG, "mOpenedBook: " + mOpenedBook);
			setLastCode(EngineMsgCode.ENGINE_ERROR_ENGINE_ERROR);
			return false;
		}

		if (!mOpenedBook.isExists()) {
			Logger.dLog(TAG, "mOpenedBook is not exists ");
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_NOT_EXISTS);
			return false;
		}

		if (!mOpenedBook.isValid()) {
			Logger.dLog(TAG, "mOpenedBook is not valid");
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_INVALID);
			return false;
		}

		return true;
	}

	/**
	 * Save book db.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean saveBookDB() throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.saveBookDb(mOpenedBook);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_SAVE_BOOK_FAILED);
		}

		return res;
	}

	/**
	 * Load book db.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean loadBookDB() throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();
		EngineConfig config = mEngine.getConfig();
		if ((config != null) && !config.getAutoLoadDB()) {
			return false;
		}

		if (recorder != null) {
			res = recorder.loadBookDb(mOpenedBook);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_LOAD_BOOK_FAILED);
		}

		return res;
	}

	/**
	 * Adds the book mark db.
	 * 
	 * @param bookmark
	 *            the bookmark
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean addBookMarkDB(Bookmark bookmark) throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.addBookMarkDb(bookmark);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_ADD_BOOKMARK_FAILED);
		}

		return res;
	}

	/**
	 * Del book mark db.
	 * 
	 * @param bookmark
	 *            the bookmark
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean delBookMarkDB(Bookmark bookmark) throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.delBookMarkDb(bookmark);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_DEL_BOOKMARK_FAILED);
		}

		return res;
	}

	/**
	 * Clean book mark db.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean cleanBookMarkDB() throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.cleanBookMarkDb(mOpenedBook);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_CLEAN_BOOKMARK_FAILED);
		}

		return res;
	}

	/**
	 * Adds the book emphasis db.
	 * 
	 * @param emphasis
	 *            the emphasis
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean addBookEmphasisDB(BookEmphasis emphasis) throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.addBookEmphasisDb(emphasis);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_ADD_EMPHASIS_FAILED);
		}

		return res;
	}

	/**
	 * Del book emphasis db.
	 * 
	 * @param emphasis
	 *            the emphasis
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean delBookEmphasisDB(BookEmphasis emphasis) throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.delBookEmphasisDb(emphasis);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_DEL_EMPHASIS_FAILED);
		}

		return res;
	}

	/**
	 * Clean book emphasis db.
	 * 
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	protected boolean cleanBookEmphasisDB() throws Exception {
		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		boolean res = false;
		IRecorder recorder = mEngine.getRecorder();

		if (recorder != null) {
			res = recorder.cleanBookEmphasisDb(mOpenedBook);
		}

		if (!res) {
			setLastCode(EngineMsgCode.STATE_ERROR_DB_CLEAN_EMPHASIS_FAILED);
		}

		return res;
	}

	/**
	 * Update view enabled.
	 * 
	 * @return true, if successful
	 */
	protected boolean updateViewEnabled() {
		return mEngine.getUpdateViewEnabled();
	}

	/**
	 * Update view.
	 */
	protected void updateView() {
		if (updateViewEnabled() && !isBorder()) {
			mEngine.getMessageHandler().sendMessage(EngineHandler.UPDATE_VIEW);
		}
	}

	/**
	 * Update config.
	 * 
	 * @param config
	 *            the config
	 */
	protected void updateFromConfig(EngineConfig config) {
		if (mOpenedBook != null) {
			mOpenedBook.updateFromConfig(config);
		}
	}

	/**
	 * Gets the task manager.
	 * 
	 * @return the task manager
	 */
	protected TaskManager getTaskManager() {
		return mTaskManager;
	}

	/**
	 * Gets the engine error.
	 * 
	 * @return the engine error
	 */
	protected EngineCode getEngineError() {
		return mEngineError;
	}

	/**
	 * Checks for error.
	 * 
	 * @param code
	 *            the code
	 * @return true, if successful
	 */
	protected boolean hasError(int code) {
		if (!mEngineError.noError(code)) {
			return true;
		}

		return false;
	}

	/**
	 * Gets the last code.
	 * 
	 * @return the last code
	 */
	protected int getLastCode() {
		if (mEngineError != null) {
			return mEngineError.getLastCode();
		}

		return EngineCode.EngineMsgCode.ENGINE_ERROR_CODE_ERROR;
	}

	/**
	 * Sets the last code.
	 * 
	 * @param code
	 *            the new last code
	 */
	protected void setLastCode(int code) {
		if (mEngineError != null) {
			mEngineError.setLastCode(code);
		}
	}

	/**
	 * Handle error.
	 */
	protected void handleError() {
		if (mEngineError != null) {
			int code = mEngineError.getLastCode();

			if (!mEngineError.noError(code)) {
				Message msg = Message.obtain();
				msg.what = EngineCode.ErrroHandler.EXECUTE_HANDLER;
				msg.arg1 = code;
				mEngineError.getMessageHandler().sendMessage(msg);
			}
		}
	}

	/**
	 * Adds the task.
	 * 
	 * @param adapter
	 *            the adapter
	 * @param action
	 *            the action
	 * @param predo
	 *            the predo
	 * @param todo
	 *            the todo
	 */
	protected void addTask(DecAdapter adapter, IActionCall action,
			IActionCall predo, IActionCall todo) {
		ActionTask task;
		mLastTodoAction = todo;
		mLastPredoAction = predo;

		if (mAutoExecuteTodoAction) {
			task = new ActionTask(action, predo, todo);
		} else {
			task = new ActionTask(action, null, null);
		}

		task.setDecAdapter(adapter);
		mTaskManager.put(task);
	}

	/**
	 * Exec last todo action.
	 */
	protected void execLastTodoAction() {
		if (mLastTodoAction != null) {
			mLastTodoAction.action();
			handleError();
		}

		mLastTodoAction = null;
	}

	/**
	 * Exec last predo action.
	 */
	protected void execLastPredoAction() {
		if (mLastPredoAction != null) {
			mLastPredoAction.action();
			handleError();
		}

		mLastPredoAction = null;
	}

	/**
	 * Gets the last todo action.
	 * 
	 * @return the last todo action
	 */
	protected IActionCall getLastTodoAction() {
		return mLastTodoAction;
	}

	/**
	 * Gets the last predo action.
	 * 
	 * @return the last predo action
	 */
	protected IActionCall getLastPredoAction() {
		return mLastPredoAction;
	}

	/**
	 * Sets the auto exec todo action.
	 * 
	 * @param auto
	 *            the new auto exec todo action
	 */
	protected void setAutoExecTodoAction(boolean auto) {
		mAutoExecuteTodoAction = auto;
	}

	/**
	 * Gets the auto exec todo action.
	 * 
	 * @return the auto exec todo action
	 */
	protected boolean getAutoExecTodoAction() {
		return mAutoExecuteTodoAction;
	}

	/**
	 * Sets the land mode.
	 * 
	 * @param land
	 *            the new land mode
	 */
	protected void setLandMode(boolean land) {
		mLandMode = land;
	}

	/**
	 * Gets the land mode.
	 * 
	 * @return the land mode
	 */
	protected boolean getLandMode() {
		return mLandMode;
	}

	/**
	 * Sets the cur font level.
	 * 
	 * @param level
	 *            the new cur font level
	 */
	protected void setCurFontLevel(int level) {
		mCurFontLevel = level;
	}

	/**
	 * Gets the cur font level.
	 * 
	 * @return the cur font level
	 */
	protected int getCurFontLevel() {
		return mCurFontLevel;
	}

	/**
	 * Sets the bitmap config.
	 *
	 * @param config the new bitmap config
	 */
	protected void setBitmapConfig(Bitmap.Config config) {
		mBitmapConfig = config;
	}

	/**
	 * Gets the bitmap config.
	 *
	 * @return the bitmap config
	 */
	protected Bitmap.Config getBitmapConfig() {
		return mBitmapConfig;
	}

	/**
	 * Sets the default val.
	 */
	abstract protected void setDefaultVal();

	/**
	 * Gets the cur adapter.
	 * 
	 * @return the cur adapter
	 */
	abstract protected DecAdapter getCurAdapter();

	/**
	 * I close book.
	 */
	abstract protected void iCloseBook();

	/**
	 * I load book.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iLoadBook();

	/**
	 * I enter search mode.
	 */
	abstract protected void iEnterSearchMode();

	/**
	 * I search go next.
	 */
	abstract protected void iSearchGoNext();

	/**
	 * I search goto.
	 *
	 * @param result the result
	 */
	abstract protected void iSearchGoto(BookSearchResult result);

	/**
	 * I search go previous.
	 */
	abstract protected void iSearchGoPrevious();

	/**
	 * I search update info.
	 */
	abstract protected void iSearchUpdateInfo();

	/**
	 * I quit search.
	 */
	abstract protected void iQuitSearch();

	/**
	 * I add bookmark.
	 */
	abstract protected void iAddBookmark();

	/**
	 * I goto bookmark.
	 *
	 * @param bookmark the bookmark
	 */
	abstract protected void iGotoBookmark(Bookmark bookmark);
	
	/**
	 * I goto bookmark.
	 */
	abstract protected void iGotoBookmark();

	/**
	 * I delete bookmark.
	 * 
	 * @param bookmark
	 *            the bookmark
	 */
	abstract protected void iDeleteBookmark(Bookmark bookmark);

	/**
	 * I del all bookmark.
	 */
	abstract protected void iDelAllBookmark();

	/**
	 * I load chapter information.
	 */
	abstract protected void iLoadChapterInformation();

	/**
	 * I chapter jump by title.
	 * 
	 * @param title
	 *            the title
	 */
	abstract protected void iChapterJumpByTitle(String title);

	/**
	 * I chapter jump by index.
	 * 
	 * @param index
	 *            the index
	 */
	abstract protected void iChapterJumpByIndex(int index);

	/**
	 * I chapter up.
	 */
	abstract protected void iChapterUp();

	/**
	 * I chapter down.
	 */
	abstract protected void iChapterDown();

	/**
	 * I chapter update info.
	 */
	abstract protected void iChapterUpdateInfo();

	/**
	 * I page up.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iPageUp();

	/**
	 * I page down.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iPageDown();

	/**
	 * I page jump to.
	 * 
	 * @param pageNum
	 *            the page num
	 * @return true, if successful
	 */
	abstract protected boolean iPageJumpTo(int pageNum);

	/**
	 * I page zoom level.
	 *
	 * @param lv the lv
	 * @return true, if successful
	 */
	abstract protected boolean iPageZoomLevel(int lv);
	
	/**
	 * I page zoom in.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iPageZoomIn();

	/**
	 * I page zoom out.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iPageZoomOut();

	/**
	 * I open link by touch.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	abstract protected boolean iOpenLinkByTouch(int x, int y);

	/**
	 * I set screen land mode.
	 * 
	 * @param land
	 *            the land
	 * @return true, if successful
	 */
	abstract protected boolean iResizeScreen(int width, int height);

	/**
	 * I start highlight by xy.
	 * 
	 * @param x1
	 *            the x1
	 * @param y1
	 *            the y1
	 * @return true, if successful
	 */
	abstract protected boolean iStartHighlightByXY(double x1, double y1);

	/**
	 * I move highlight by xy.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	abstract protected boolean iMoveHighlightByXY(double x, double y);

	/**
	 * I end highlight by xy.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	abstract protected boolean iEndHighlightByXY(double x, double y);

	/**
	 * I del highlight.
	 * 
	 * @param emphasis
	 *            the emphasis
	 * @return true, if successful
	 */
	abstract protected boolean iDelHighlight(BookEmphasis emphasis);

	/**
	 * I goto highlight.
	 * 
	 * @param emphasis
	 *            the emphasis
	 * @return true, if successful
	 */
	abstract protected boolean iGotoHighlight(BookEmphasis emphasis);

	/**
	 * I do fetch text.
	 *
	 * @return true, if successful
	 */
	abstract protected boolean iDoFetchText();
	
	/**
	 * I del all highlight.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean iDelAllHighlight();

	/**
	 * Preload page.
	 */
	abstract protected void preloadPage();

	/**
	 * Preload previous page.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean preloadPreviousPage();

	/**
	 * Preload next page.
	 * 
	 * @return true, if successful
	 */
	abstract protected boolean preloadNextPage();
}
