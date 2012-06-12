/**
 * @file       AdobeSampleDemo.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-10 下午03:31:40 
 */

package com.easyview.ebook.reader.ui.controller;

import java.util.List;

import com.easyview.ebook.reader.easyviewer.EasyViewerApplication;
import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IActionCall;
import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.core.IErrorCall;
import com.easyview.ebook.reader.engine.core.EngineCode.AdobeMsgCode;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.IBookSearchCursor;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookSearchInfo.BookSearchResult;
import com.easyview.ebook.reader.engine.module.comments.CommentView;
import com.easyview.ebook.reader.engine.module.comments.ICommentHandlerCall;
import com.easyview.ebook.reader.engine.util.EngineConfig;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.recorder.SqliteRecorder;
import com.easyview.ebook.reader.engine.util.render.Render2;
import com.easyview.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;
import com.easyview.ebook.reader.ui.view.ActionDialog;
import com.easyview.ebook.reader.ui.view.BottomBarLayout;
import com.easyview.ebook.reader.ui.view.RenderView;
import com.easyview.ebook.reader.ui.view.TitleBarLayout;
import com.easyview.ebook.reader.easyviewer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 */
public class EasyViewer extends Activity {
	static private final String TAG = "EasyViewer";

	// engine
	private EasyViewerApplication mEvApplication;
	private IEngineService mIEREngine;
	private IControlCenterService mIcc;

	// UI
	private RenderView mRenderView;
	private CommentView mCommentView;
	private TitleBarLayout mTitleBarLayout;
	private BottomBarLayout mBottomBarLayout;
	private ActionDialog mMessageDlg;

	// book
	private Book mBook = null;
	private boolean mIsBlock = false;
	private String mBookTitle;
	private final String PAGE_FORMAT = "%d/%d";

	private final int MSG_INIT_ENGINE = 0;
	private final int MSG_OPEN_BOOK = 1;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_INIT_ENGINE:
				// 初始化Engine
				initEngine();
				// 初始化ControlCenter
				initControlCenter();
				// 设置错误处理回调
				setErrorHandler();
				// 打開書籍
				mHandler.sendEmptyMessage(MSG_OPEN_BOOK);
				break;
			case MSG_OPEN_BOOK:
				// String fileName = "/sdcard/demo.pdf";
				String fileName = getIntent().getExtras().getString("fileName");
				if (fileName != null) {
					openBook(fileName);
					break;
				}
				
				int fileID = getIntent().getExtras().getInt("bookId");
				if (fileID != 0) {
					openBook(fileID);
					break;
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.main);

		// application
		mEvApplication = (EasyViewerApplication) getApplication();
		// 初始化View
		initView();
		// 加載裝載對話框
		mMessageDlg.setMessage(getString(R.string.loading));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mMessageDlg.showDialog();
		// 初始化Engine
		mHandler.sendEmptyMessageDelayed(MSG_INIT_ENGINE, 500);
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.d(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		closeBook();
		free();

		super.onDestroy();
	}

	private void initView() {
		mBookTitle = getResources().getString(R.string.book_title);
		mTitleBarLayout = (TitleBarLayout) findViewById(R.id.id_title_bar);
		mBottomBarLayout = (BottomBarLayout) findViewById(R.id.id_bottom_bar);
		mRenderView = (RenderView) findViewById(R.id.id_book_content);

		mMessageDlg = new ActionDialog(EasyViewer.this);
		// mCommentView = (CommentView) findViewById(R.id.comment_view);
	}

	private void initEngine() {
		EngineConfig config = initConfig();

		mIEREngine = mEvApplication.getErengineService();
//		mIEREngine.setApplication(this.getApplication());
//		mIEREngine.setContext(this);
		mIEREngine.setConfig(config);
		mIEREngine.setRender(new Render2(mRenderView));
		// mIEREngine.setRecorder(new FileRecorder(this));
		mIEREngine.setRecorder(new SqliteRecorder());
		mIEREngine.setUpdateViewEnabled(true);
	}

	private void initControlCenter() {
		mIcc = mEvApplication.getControlCenterService();
		mIcc.setContext(this);
	}

	// 錯誤與异常處理
	private void setErrorHandler() {
		// 輸入密碼的回調
		IErrorCall inputPdfPassword = new IErrorCall() {

			@Override
			public void handleError() {
				showPdfInputDialog();
			}
		};

		IErrorCall inputEpubPassword = new IErrorCall() {

			@Override
			public void handleError() {
				showEpubInputDialog();
			}
		};

		// 提示書籍不存在的回調
		IErrorCall bookNotExists = new IErrorCall() {
			@Override
			public void handleError() {
				// 退出
				finish();
			}
		};

		IErrorCall commentNotInit = new IErrorCall() {
			@Override
			public void handleError() {
				Logger.eLog(TAG, "comment not init");
			}
		};

		IErrorCall commentSaveError = new IErrorCall() {
			@Override
			public void handleError() {
				Logger.eLog(TAG, "comment save error");
			}
		};

		IErrorCall commentDelError = new IErrorCall() {
			@Override
			public void handleError() {
				Logger.eLog(TAG, "comment delete error");
			}
		};

		IErrorCall maxFontError = new IErrorCall() {
			@Override
			public void handleError() {
				mMessageDlg.setMessage(getString(R.string.max_font));
				mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
				mMessageDlg.showDialog();
			}
		};

		IErrorCall minFontError = new IErrorCall() {
			@Override
			public void handleError() {
				mMessageDlg.setMessage(getString(R.string.min_font));
				mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
				mMessageDlg.showDialog();
			}
		};

		IErrorCall searchKeyError = new IErrorCall() {
			@Override
			public void handleError() {
				mMessageDlg.setMessage(getString(R.string.search_key_error));
				mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
				mMessageDlg.showDialog();
			}
		};

		IErrorCall searchValueNullError = new IErrorCall() {
			@Override
			public void handleError() {
				Logger.dLog(TAG, "searchValueNullError");
				ActionDialog dlg = new ActionDialog(EasyViewer.this);
				dlg.setMessage(getString(R.string.search_value_null));
				dlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
				dlg.postDialog(2000);
			}
		};

		IErrorCall searchFinishedError = new IErrorCall() {
			@Override
			public void handleError() {
				mMessageDlg.setMessage(getString(R.string.search_finished));
				mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
				mMessageDlg.showDialog();
			}
		};

		IErrorCall searchMoreError = new IErrorCall() {
			@Override
			public void handleError() {
				mMessageDlg.setMessage(getString(R.string.search_loading));
				mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
				mMessageDlg.showDialog();
			}
		};

		// 針對不同的錯誤碼設置響應的錯誤處理
		if (mIEREngine != null) {
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_ERROR_BOOK_NOT_EXISTS, bookNotExists);
			mIEREngine.setErrorHandler(
					AdobeMsgCode.STATE_ERROR_PDF_T3_NEED_PASSWORD,
					inputPdfPassword);
			mIEREngine.setErrorHandler(
					AdobeMsgCode.STATE_ERROR_PASSHASH_NOT_FOUND,
					inputEpubPassword);
			mIEREngine
					.setErrorHandler(
							EngineMsgCode.ENGINE_ERROR_COMMENT_NOT_INIT,
							commentNotInit);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_ERROR_COMMENT_SAVE_FAILED,
					commentSaveError);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_ERROR_COMMENT_DELETE_FAILED,
					commentDelError);
			mIEREngine.setErrorHandler(EngineMsgCode.ENGINE_WARN_MAX_FONT,
					maxFontError);
			mIEREngine.setErrorHandler(EngineMsgCode.ENGINE_WARN_MIN_FONT,
					minFontError);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL, searchKeyError);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_WARN_SEARCH_RESULT_NULL,
					searchValueNullError);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_ERROR_SEARCH_GO_NEXT_FAILED,
					searchFinishedError);
			mIEREngine.setErrorHandler(
					EngineMsgCode.ENGINE_ERROR_SEARCH_GO_PREVIOUS_FAILED,
					searchFinishedError);
			mIEREngine.setErrorHandler(
					AdobeMsgCode.STATE_NORMAL_GET_MORE_SEARCH_RESULT,
					searchMoreError);
		}
	}

	// 修改默认配置
	private EngineConfig initConfig() {
		EngineConfig config = new EngineConfig();
		config.setLogOn(true);
		config.setViewWidth(mRenderView.getWidth());
		config.setViewHeight(mRenderView.getHeight());
		config.setFontLevel(1);
		config.setAutoLoadDB(true);

		return config;
	}

	private void free() {
		mEvApplication.free();
	}

	public Book getBook() {
		return mBook;
	}

	private void openBook(final int bookId) {
		IActionCall toDo = new IActionCall() {
			@Override
			public boolean action() {
				mMessageDlg.closeDialog();
				iaUpdatePage.action();

				// update book title
				mBookTitle = mBook.getTitle();
				if (!mBookTitle.equals(Book.VALUE_NULL)) {
					mTitleBarLayout.setBookTitle(mBookTitle);
				}

				return true;
			}
		};

		mBook = new Book(bookId);
		// mBook = new Book(1);
		mIEREngine.setBook(mBook);
		mIEREngine.getAction().openBook(null, toDo);

		// init comment function
		// mEngine.setCommentManager(new Comments(this, mCommentView, new
		// Rect(0,
		// 0, 560, 720), CommentType.TYPE_CPU_MODE, mBook));
		// mEngine.enterComment(enterCommentCall);
	}
	
	private void openBook(final String bookname) {
		IActionCall toDo = new IActionCall() {
			@Override
			public boolean action() {
				mMessageDlg.closeDialog();
				iaUpdatePage.action();

				// update book title
				mBookTitle = mBook.getTitle();
				if (!mBookTitle.equals(Book.VALUE_NULL)) {
					mTitleBarLayout.setBookTitle(mBookTitle);
				} else {
					mTitleBarLayout.setBookTitle(bookname);
				}

				return true;
			}
		};

		mBook = new Book(bookname);
		// mBook = new Book(1);
		mIEREngine.setBook(mBook);
		mIEREngine.getAction().openBook(null, toDo);

		// init comment function
		// mEngine.setCommentManager(new Comments(this, mCommentView, new
		// Rect(0,
		// 0, 560, 720), CommentType.TYPE_CPU_MODE, mBook));
		// mEngine.enterComment(enterCommentCall);
	}

	private void closeBook() {
		mIEREngine.getAction().closeBook();
	}

	ICommentHandlerCall enterCommentCall = new ICommentHandlerCall() {
		@Override
		public void commentCall() {
			mCommentView.setVisibility(View.VISIBLE);
		}
	};

	// 顯示頁碼回調
	IActionCall iaUpdatePage = new IActionCall() {
		@Override
		public boolean action() {
			resetBlock();

			mMessageDlg.closeDialog();

			// 更新頁碼
			int tmp = mBook.getCurPageNumber();
			int num = (tmp <= 0) ? 1 : (tmp);
			int totalPage = mBook.getTotalPageNumber();

			String info = String.format(PAGE_FORMAT, num, totalPage);
			mBottomBarLayout.updatePageInfo(info);
			mBottomBarLayout
					.updatePageProgress(((float) num / (float) totalPage));
			
			Logger.dLog(TAG, "Summary = " + mBook.getPageSummary());
			return true;
		}
	};

	// 上下翻頁，字體放大縮小
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Logger.dLog(TAG, "onKeyDown keyCode = " + keyCode);

		switch (keyCode) {
		// ek1 code
		case 93:
			pageDown();
			// gotoEmphasis(true);
			// gotoSearch();
//			 deleteBook();
			// zoomLevel(1);
			break;
		case 92:
			pageUp();
			// gotoEmphasis(false);
			// zoomLevel(0);
			break;
		case 95:
			zoomOut();
			// setScreenLandMode(true);
			// zoomLevel(2);
			return true;
		case 94:
			zoomIn();
			// setScreenLandMode(false);
			// zoomLevel(3);
			testPageText();
			return true;

			// em1 code
			// case KeyEvent.KEYCODE_DPAD_DOWN:
			// pageDown();
			// break;
			// case KeyEvent.KEYCODE_DPAD_UP:
			// pageUp();
			// break;
			// case KeyEvent.KEYCODE_VOLUME_DOWN:
			// zoomOut();
			// return true;
			// case KeyEvent.KEYCODE_VOLUME_UP:
			// zoomIn();
			// return true;

		case 82:
			showControlCenterDlg();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	};

	private boolean isBlocked() {
		if (mIsBlock) {
			return true;
		} else {
			mIsBlock = true;
		}

		return false;
	}

	private void resetBlock() {
		mIsBlock = false;
	}

	public void exit() {
		finish();
	}

	public void zoomLevel(int lev) {
		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().pageZoomLevel(lev, null, iaUpdatePage);
	}

	public void testPageText() {
		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().fetchText(null, new IActionCall() {
			@Override
			public boolean action() {
				Logger.eLog(TAG, "fetchText = " + mBook.getPageText());
				resetBlock();
				return true;
			}
		});
	}

	public void deleteBook() {
		IDatabaseService dbs = (IDatabaseService) ERManager
				.getService(ERManager.DATABASE_SERVICE);
		// dbs.deleteBook(mBook);
		dbs.deleteAllBooks();
	}

	private int mEmphasisId = 0;

	public void gotoEmphasis(boolean down) {
		List<BookEmphasis> list = mBook.getBookEmphasisCursor()
				.getEmphasisList();
		if (list.isEmpty()) {
			return;
		}

		int size = list.size();

		if (down) {
			mEmphasisId++;
			if (mEmphasisId >= size) {
				mEmphasisId = size - 1;
			}
		} else {
			mEmphasisId--;
			if (mEmphasisId < 0) {
				mEmphasisId = 0;
			}
		}

		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().gotoHighlight(list.get(mEmphasisId), null,
				iaUpdatePage);

		// mIEREngine.getAction().delHighlight(list.get(mEmphasisId), null,
		// iaUpdatePage);

		// mIEREngine.getAction().delAllHighlight();
	}

	private boolean mLandMode = false;

	public void setScreenLandMode(boolean land) {
		if (mLandMode == land) {
			return;
		}

		if (isBlocked()) {
			return;
		}

		mLandMode = land;
		int height = mBook.getViewHeight();
		int width = mBook.getViewWidth();

		mIEREngine.setRender(new Render2(mRenderView));
		mIEREngine.getAction().resizeScreen(height, width, null,
				iaUpdatePage);
	}

	public void touchLink(int x, int y) {
		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().openLinkByTouch(x, y, null, iaUpdatePage);
	}

	// 跳頁
	public void pageJumpTo(int page) {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.jump_page));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().pageJumpTo(page, mMessageDlg, iaUpdatePage);
	}

	// 章節跳轉
	public void chapterJumpTo(int index) {
		if (isBlocked()) {
			return;
		}

		String title = mBook.getChapterTitles()[index];
		mMessageDlg.setMessage(getString(R.string.jump_chapter) + title);
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().chapterJumpByIndex(index, mMessageDlg,
				iaUpdatePage);
	}

	public void chapterJumpTo(String title) {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.jump_chapter) + title);
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().chapterJumpByTitle(title, mMessageDlg,
				iaUpdatePage);
	}

	public void pageUp() {
		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().pageUp(null, iaUpdatePage);
		// mEngine.saveComment(null);
	}

	public void pageDown() {
		if (isBlocked()) {
			return;
		}

		mIEREngine.getAction().pageDown(null, iaUpdatePage);
	}

	public void chapterUp() {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.chapter_up));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().chapterUp(mMessageDlg, iaUpdatePage);
	}

	public void chapterDown() {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.chapter_down));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().chapterDown(mMessageDlg, iaUpdatePage);
	}

	public void zoomIn() {
		if (isBlocked()) {
			return;
		}

		// 字體放大
		// mMessageDlg.setMessage(getString(R.string.zoom_in));
		// mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);

		mIEREngine.getAction().pageZoomIn(null, iaUpdatePage);
	}

	public void zoomOut() {
		if (isBlocked()) {
			return;
		}

		// 字體縮小
		// mMessageDlg.setMessage(getString(R.string.zoom_out));
		// mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
		mIEREngine.getAction().pageZoomOut(null, iaUpdatePage);
	}

	// 进入搜索模式
	public void enterSearchMode() {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.search_loading));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().enterSearchMode(mMessageDlg, iaUpdatePage);
	}

	public void searchGoNext() {
		if (isBlocked()) {
			return;
		}

		// mMessageDlg.setMessage(getString(R.string.search_loading));
		// mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().searchGoNext(null, iaUpdatePage);
	}

	public void searchGoPrevious() {
		if (isBlocked()) {
			return;
		}

		// mMessageDlg.setMessage(getString(R.string.search_loading));
		// mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
		mIEREngine.getAction().searchGoPrevious(null, iaUpdatePage);
	}

	int i = 0;

	public void gotoSearch() {
		IBookSearchCursor cursor = mBook.getBookSearchCursor();
		List<BookSearchResult> results = cursor.getResults();
		if (results.isEmpty()) {
			return;
		}

		if (isBlocked()) {
			return;
		}

		i++;

		// 對PDF書的特別處理
		if (i >= results.size()) {
			cursor.moveCursorToEnd();
			resetBlock();
			searchGoNext();
			return;
		}

		Logger.dLog(TAG, "text = " + results.get(i).text + " start = "
				+ results.get(i).start_pos + " end = " + results.get(i).end_pos);
		mIEREngine.getAction().searchGotoResult(results.get(i), null,
				iaUpdatePage);

	}

	public void quitSearch() {
		mIEREngine.getAction().quitSearch();
	}

	public void gotoBookmark() {
		if (isBlocked()) {
			return;
		}

		mMessageDlg.setMessage(getString(R.string.jump_page));
		mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);

		mIEREngine.getAction().gotoBookmark(mMessageDlg, iaUpdatePage);
	}

	// public void delBookmark(Bookmark bookmark) {
	// if (isBlocked()) {
	// return;
	// }
	//
	// ActionDialog dlg = new ActionDialog(EasyViewer.this);
	// dlg.setMessage(getString(R.string.bookmark_del));
	// dlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
	//
	// getErengine().getAction().deleteBookmark(bookmark, dlg,
	// iaUpdateBookmarkList);
	//
	// resetBlock();
	// }

	private AlertDialog mInputDlg;

	private void showPdfInputDialog() {
		closeInputDlg();

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.pdf_input_dialog,
				null);
		mInputDlg = new AlertDialog.Builder(EasyViewer.this)
				.setTitle(R.string.password).setView(textEntryView).create();

		Button buttonSure = (Button) textEntryView.findViewById(R.id.sure);
		final EditText editPassword = (EditText) textEntryView
				.findViewById(R.id.password);

		final IActionCall toDo = new IActionCall() {
			@Override
			public boolean action() {
				// 关闭装载对话框
				mMessageDlg.closeDialog();

				// 從Book對象獲取總頁數
				iaUpdatePage.action();

				return true;
			}
		};

		buttonSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBook.setPassword(String.valueOf(editPassword.getText()));
				mMessageDlg.setMessage(getString(R.string.loading));
				mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
				mIEREngine.getAction().openBook(mMessageDlg, toDo);
				mInputDlg.dismiss();
			}
		});

		mInputDlg.show();
	}

	private void showEpubInputDialog() {
		closeInputDlg();

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.epub_input_dialog,
				null);
		mInputDlg = new AlertDialog.Builder(EasyViewer.this)
				.setTitle(R.string.password).setView(textEntryView).create();

		Button buttonSure = (Button) textEntryView.findViewById(R.id.sure);
		final EditText editPassword = (EditText) textEntryView
				.findViewById(R.id.password);
		final EditText editUserName = (EditText) textEntryView
				.findViewById(R.id.username);

		final IActionCall toDo = new IActionCall() {
			@Override
			public boolean action() {
				// 关闭装载对话框
				mMessageDlg.closeDialog();

				// 從Book對象獲取總頁數
				iaUpdatePage.action();

				return true;
			}
		};

		buttonSure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBook.setUserName(String.valueOf(editUserName.getText()));
				mBook.setPassword(String.valueOf(editPassword.getText()));
				mMessageDlg.setMessage(getString(R.string.loading));
				mMessageDlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);
				mIEREngine.getAction().openBook(mMessageDlg, toDo);
				mInputDlg.dismiss();
			}
		});

		mInputDlg.show();
	}

	// 关闭密码输入对话框
	private void closeInputDlg() {
		if (mInputDlg != null) {
			mInputDlg.dismiss();
			mInputDlg = null;
		}
	}

	private void showControlCenterDlg() {
		if (mIcc != null) {
			mIcc.runCommand(COMMAND_TYPE.CMD_SHOW_CONTROL_CENTER);
		}
	}

}
