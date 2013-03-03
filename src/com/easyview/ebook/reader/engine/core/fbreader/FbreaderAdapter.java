/**
 * @file       FbreaderAdapter.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-4-27 下午01:37:24 
 */

package com.easyview.ebook.reader.engine.core.fbreader;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.List;

import org.geometerplus.android.fbreader.library.SQLiteBooksDatabase;
import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.fbreader.library.Author;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.library.Library;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.sqliteconfig.ZLSQLiteConfig;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.model.ZLTextMark;
import org.geometerplus.zlibrary.text.model.ZLTextModel;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextParagraphCursor;
import org.geometerplus.zlibrary.text.view.ZLTextPosition;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;
import org.geometerplus.zlibrary.ui.android.image.ZLAndroidImageManager;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;
import org.geometerplus.zlibrary.ui.android.view.BitmapManager;

import android.graphics.Bitmap;

import com.easyview.ebook.reader.engine.core.DecAdapter;
import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.core.EngineCode.FbreaderMsgCode;
import com.easyview.ebook.reader.engine.model.BookSearchInfo;
import com.easyview.ebook.reader.engine.model.BookSearchInfo.BookSearchResult;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 * 
 */
public class FbreaderAdapter extends DecAdapter {
	static private final String TAG = "FbreaderAdapter";

	private static FbreaderAdapter mFbreaderAdapter;
	private static EngineCode mEngineCode;

	private IEngineService mIes;
	private ZLFile mZlfile;
	private FBReaderApp mZlApplication;
	private BitmapManager mBitmapManager;
	private ZLSQLiteConfig mZlSqlConfig;
	private SQLiteBooksDatabase mSqlBooksDb;
	private Book mFBook;
	private int mViewWidth = 0;
	private int mViewHeight = 0;
	private Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
	private int[] mChapterIndexs;
	private String[] mChapterTitles;
	private int SUMMARY_MAX_NUM = 50;
	public static final String LOCATION_PREFIX_HEADER = "#fbloc";
	public static final String LOCATION_PREFIX = LOCATION_PREFIX_HEADER
			+ "(%d,%d,%d)";

	protected enum PageType {
		PreviousPage, CurrentPage, NextPage
	}

	protected static FbreaderAdapter getInstance() {
		if (null == mFbreaderAdapter) {
			mFbreaderAdapter = new FbreaderAdapter();
			mEngineCode = EngineCode.getInstance();
		}

		return mFbreaderAdapter;
	}

	private FbreaderAdapter() {
		initFbreader();
	}

	@Override
	protected void cancelProcessing() {
		Logger.dLog(TAG, "cancelProcessing");
	}

	@Override
	protected void free() {
		resetCache();

		if (mFbreaderAdapter != null) {
			mFbreaderAdapter = null;
		}

		if (mEngineCode != null) {
			mEngineCode.free();
			mEngineCode = null;
		}

		if (mBitmapManager != null) {
			mBitmapManager = null;
		}

		if (mZlApplication != null) {
			mZlApplication = null;
		}

		if (mZlSqlConfig != null) {
			mZlSqlConfig.closeDatabase();
			mZlSqlConfig = null;
		}

		if (mIes != null) {
			mIes = null;
		}

		Logger.vLog(TAG, "AdobeAdapter free adapter: " + mFbreaderAdapter
				+ " errorCode: " + mEngineCode);
	}

	protected int openFile(String url) {
		Logger.vLog(TAG, "openFile enter>>>");

		int code = EngineCode.EngineMsgCode.NO_ERROR;

		// step 1: create zlfile.
		mZlfile = ZLFile.createFileByPath(url);

		if (mZlfile != null) {
			// step 2: create zlapplication.
			mZlApplication = createApplication(mZlfile);
			if (mZlApplication != null) {
				// step 3: open file.
				mZlApplication.openFile(mZlfile);
				mFBook = mZlApplication.Model.Book;
				// step 4: reset view
				resetBitmap();
				// step 5: computer text size
				computerTextSize();
			} else {
				code = EngineCode.FbreaderMsgCode.STATE_ERROR_OPEN_FILE_FAILED;
			}
		} else {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_OPEN_FILE_FAILED;
		}

		mEngineCode.setLastCode(code);

		Logger.vLog(TAG, "openFile exit>>>");
		return code;
	}

	protected int closeFile() {
		Logger.vLog(TAG, "closeFile enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
		} else {
			mFBook.storePosition(mZlApplication.BookTextView.getStartCursor());
			mZlApplication.doAction(ActionCode.EXIT);
			resetCache();
		}

		Logger.vLog(TAG, "closeFile exit.");
		return code;
	}

	protected void setViewPortSize(int w, int h) {
		Logger.vLog(TAG, "setViewportSize enter>>> width = " + w + " height = "
				+ h);

		mViewWidth = w;
		mViewHeight = h;
		mBitmapManager.setSize(w, h);

		Logger.vLog(TAG, "setViewportSize exit.");
	}

	protected Bitmap getBitmap(PageType pageType) {
		resetBitmap();

		Bitmap bmp = null;

		if (pageType == PageType.CurrentPage) {
			bmp = mBitmapManager.getBitmap(ZLView.PageIndex.current,
					mViewWidth, mViewHeight);
		} else if (pageType == PageType.PreviousPage) {
			bmp = mBitmapManager.getBitmap(ZLView.PageIndex.previous,
					mViewWidth, mViewHeight);
		} else if (pageType == PageType.NextPage) {
			bmp = mBitmapManager.getBitmap(ZLView.PageIndex.next, mViewWidth,
					mViewHeight);
		} else {
			bmp = mBitmapManager.getBitmap(ZLView.PageIndex.current,
					mViewWidth, mViewHeight);
		}

		if (null == bmp) {
			mEngineCode
					.setLastCode(EngineMsgCode.STATE_ERROR_GET_BITMAP_FAILED);
			Logger.eLog(TAG, "getBitmap == null");
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		return bmp;
	}

	protected void resetBitmap() {
		if (mBitmapManager != null) {
			mBitmapManager.reset();
		}
	}

	protected int getTotalPageNum() {
		Logger.vLog(TAG, "getTotalPageNum enter>>>");

		int totalpageNum = 1;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			mEngineCode
					.setLastCode(EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR);
		} else {
			totalpageNum = view.computePageNumber();
			// if (totalpageNum > 1) {
			// totalpageNum--;
			// }
		}

		Logger.vLog(TAG, "getTotalPageNum exit>>>");

		return totalpageNum;
	}

	protected int getCurrentPageNum() {
		Logger.vLog(TAG, "getCurrentPageNum enter>>>");

		int curPageNum = 1;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			mEngineCode
					.setLastCode(EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR);
		} else {
			curPageNum = view.computeCurrentPage();
			Logger.eLog(TAG, "curPageNum = " + curPageNum);
			// if (curPageIndex > 0) {
			// curPageIndex--;
			// }
		}

		Logger.vLog(TAG, "getCurrentPageNum exit>>>");

		return curPageNum;
	}

	protected String getCoverUri() {
		ZLImage image = Library.getCover(mZlfile);
		
		if (image != null) {
			return image.getURI();
		} else {
			return "null";
		}
	}
	
	protected String getAuthor() {
		Logger.vLog(TAG, "getAuthor enter>>>");

		int code = EngineMsgCode.NO_ERROR;
		String author = "null";

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			List<Author> authors = mFBook.authors();
			if (authors.size() > 1) {
				author = authors.get(0).DisplayName;
			}
		}

		Logger.vLog(TAG, "getAuthor exit.");

		return author;
	}

	protected String getTitle() {
		Logger.vLog(TAG, "getTitle enter>>>");

		int code = EngineMsgCode.NO_ERROR;
		String title = "null";

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			String encoding = mFBook.getEncoding();
			String temp;
			title = mFBook.getTitle();
			if (encoding != null) {
				temp = convertString(title, encoding, "UTF-8");
				if (temp != null) {
					title = temp;
				}
			}

			// title = mFBook.getTitle();
		}

		Logger.vLog(TAG, "getTitle exit.");

		return title;
	}

	protected String getEncoding() {
		Logger.vLog(TAG, "getEncoding enter>>>");

		int code = EngineMsgCode.NO_ERROR;
		String encoding = "null";

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			encoding = mFBook.getEncoding();
		}

		Logger.vLog(TAG, "getEncoding exit.");

		return encoding;
	}

	protected String getLanguage() {
		Logger.vLog(TAG, "getLanguage enter>>>");

		int code = EngineMsgCode.NO_ERROR;
		String language = "null";

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			language = mFBook.getLanguage();
		}

		Logger.vLog(TAG, "getLanguage exit.");

		return language;
	}

	protected int zoomIn() {
		Logger.vLog(TAG, "zoomIn enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			mZlApplication.doAction(ActionCode.INCREASE_FONT);
			resetBitmap();
			computerTextSize();

			code = mEngineCode.getLastCode();
		}
		Logger.vLog(TAG, "zoomIn exit>>>");

		return code;
	}

	protected int zoomOut() {
		Logger.vLog(TAG, "zoomOut enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			mZlApplication.doAction(ActionCode.DECREASE_FONT);
			resetBitmap();
			computerTextSize();
		}
		Logger.vLog(TAG, "zoomOut exit>>>");

		return code;
	}

	protected int getFontSize() {
		int fontSize;
		ZLIntegerRangeOption option = ZLTextStyleCollection.Instance()
				.getBaseStyle().FontSizeOption;
		fontSize = option.getValue();

		return fontSize;
	}

	protected boolean pageUp() {
		Logger.vLog(TAG, "pageUp enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			// if (!previousParagraph()) {
			// code = EngineCode.FbreaderMsgCode.STATE_NORMAL_BEGIN_OF_PH;
			// mEngineCode.setLastCode(code);
			// Logger.vLog(TAG, "pageUp AT BEGIN exit>>>");
			// // return false;
			// }
			// nextParagraph();

			if (isHomeParagraph()) {
				// previousParagraph(false);
				return false;
			}

			previousParagraph(false);
		}

		Logger.vLog(TAG, "pageUp exit>>>");

		return true;
	}

	protected boolean pageDown() {
		Logger.vLog(TAG, "pageDown enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			// if (!nextParagraph()) {
			// code = EngineCode.FbreaderMsgCode.STATE_NORMAL_END_OF_PH;
			// mEngineCode.setLastCode(code);
			// Logger.vLog(TAG, "pageDown AT END exit>>>");
			// // return false;
			// }
			// previousParagraph();

			if (isEndParagraph()) {
				return false;
			}

			nextParagraph(true);
		}

		Logger.vLog(TAG, "pageDown exit>>>");

		return true;
	}

	protected int gotoPage(int page) {
		Logger.vLog(TAG, "gotoPage enter>>>");

		int code = 0;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			view.gotoPage(page);
		}

		Logger.vLog(TAG, "gotoPage exit>>>");

		return code;
	}

	protected int gotoHome() {
		Logger.vLog(TAG, "gotoHome enter>>>");

		int code = 0;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			view.gotoHome();
		}

		Logger.vLog(TAG, "gotoHome exit>>>");

		return code;
	}

	protected int gotoPosition(int paragraphIndex, int wordIndex, int charIndex) {
		Logger.vLog(TAG, "gotoPosition enter>>>");

		int code = 0;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			view.gotoPosition(paragraphIndex, wordIndex, charIndex);
		}

		Logger.vLog(TAG, "gotoPosition exit>>>");

		return code;
	}

	protected int gotoPosition(ZLTextPosition postion) {
		Logger.vLog(TAG, "gotoPosition enter>>>");

		int code = 0;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			view.gotoPosition(postion);
		}

		Logger.vLog(TAG, "gotoPosition exit>>>");

		return code;
	}
	
	protected int gotoMark(ZLTextMark mark) {
		Logger.vLog(TAG, "gotoMark enter>>>");

		int code = 0;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			code = EngineCode.FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			view.gotoMark(mark);
		}

		Logger.vLog(TAG, "gotoMark exit>>>");

		return code;
	}

	protected boolean nextParagraph(boolean shift) {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			return false;
		} else {
			mZlApplication.doAction(ActionCode.TURN_PAGE_FORWARD);
			mBitmapManager.shift(shift);
			view.onScrollingFinished(ZLView.PageIndex.next);
		}

		return true;
	}

	protected boolean previousParagraph(boolean shift) {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			return false;
		} else {
			mZlApplication.doAction(ActionCode.TURN_PAGE_BACK);
			mBitmapManager.shift(shift);
			view.onScrollingFinished(ZLView.PageIndex.previous);
		}

		return true;
	}

	protected boolean isHeadParagraph() {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		final ZLTextWordCursor cursor = view.getStartCursor();

		// Logger.vLog(
		// TAG,
		// "cursor.isNull() = " + cursor.isNull()
		// + " cursor.isStartOfParagraph() = "
		// + cursor.isStartOfParagraph()
		// + " cursor.getParagraphIndex() = "
		// + cursor.getParagraphIndex());

		if (cursor.isStartOfParagraph()) {
			return true;
		}

		return false;
	}

	protected boolean isTailParagraph() {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		final ZLTextWordCursor cursor = view.getEndCursor();

		// Logger.vLog(TAG, "cursor.isNull() = " + cursor.isNull()
		// + " isEndOfParagraph() = " + cursor.isEndOfParagraph()
		// + " cursor.getParagraphIndex() = " + cursor.getParagraphIndex());

		if (cursor.isEndOfParagraph()) {
			return true;
		}

		return false;
	}

	protected boolean isHomeParagraph() {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		// final ZLTextWordCursor cursor = view.getStartCursor();

		// Logger.vLog(
		// TAG,
		// "cursor.isNull() = " + cursor.isNull()
		// + " cursor.isStartOfParagraph() = "
		// + cursor.isStartOfParagraph()
		// + " cursor.getParagraphIndex() = "
		// + cursor.getParagraphIndex());

		// if (!cursor.isNull() && cursor.isStartOfParagraph()
		// && cursor.getParagraphIndex() == 0) {
		//
		// Logger.vLog(TAG, "pageUp AT BEGIN exit>>>");
		// return true;
		// }

		ZLView.PageIndex pageIndex = FBView.PageIndex.previous;
		if ((pageIndex == ZLView.PageIndex.current)
				|| !view.canScroll(pageIndex)) {
			return true;
		}

		return false;
	}

	protected boolean isEndParagraph() {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		// final ZLTextWordCursor cursor = view.getStartCursor();
		// ZLTextModel myModel = view.getModel();
		// int curParagraphIndex = cursor.getParagraphIndex();
		//
		// // Logger.vLog(
		// // TAG,
		// // "cursor.isNull() = " + cursor.isNull()
		// // + " cursor.isStartOfParagraph() = "
		// // + cursor.isStartOfParagraph()
		// // + " cursor.getParagraphIndex() = "
		// // + cursor.getParagraphIndex());
		// if (null == myModel) {
		// return false;
		// }
		//
		// int paragraphIndex = myModel.findParagraphByTextLength(mTextSize);
		//
		// // Logger.vLog(TAG, "isEndParagraph paragraphIndex = " +
		// // paragraphIndex);
		//
		// if (paragraphIndex == curParagraphIndex) {
		// Logger.vLog(TAG, "pageDown AT END exit>>>");
		// return true;
		// }

		ZLView.PageIndex pageIndex = FBView.PageIndex.next;
		if ((pageIndex == ZLView.PageIndex.current)
				|| !view.canScroll(pageIndex)) {
			return true;
		}

		return false;
	}

	// private final HashSet<ZLTree<?>> myOpenItems = new HashSet<ZLTree<?>>();
	// private ZLTree<?>[] myItems;

	protected String[] getChapterTitles() {
		Logger.vLog(TAG, "getChapterList enter>>>");

		int chapters = getChapterNumber();
		if (chapters < 1) {
			Logger.vLog(TAG, "getChapterList chapter is empty>>>");
			return null;
		} else if (mChapterTitles != null) {
			Logger.vLog(TAG, "getChapterList chapter alredy loaded>>>");
			return mChapterTitles;
		}

		final TOCTree root = mZlApplication.Model.TOCTree;
		mChapterTitles = new String[chapters];
		mChapterIndexs = new int[chapters];

		int id = 0;
		for (TOCTree tree : root) {
			final TOCTree.Reference reference = tree.getReference();

			if (reference == null) {
				continue;
			}

			mChapterTitles[id] = tree.getText();
			mChapterIndexs[id] = reference.ParagraphIndex;
			id++;

			// Logger.dLog(TAG, "tree.getText() = " + tree.getText()
			// + " reference.ParagraphIndex = " + reference.ParagraphIndex
			// + " reference.Model.getParagraphsNumber() = "
			// + reference.Model.getParagraphsNumber()
			// + " root.getSize() = " + root.getSize());
		}

		Logger.vLog(TAG, "getChapterList exit>>>");

		return mChapterTitles;
	}

	protected int[] getChapterIndexs() {
		Logger.vLog(TAG, "getChapterIndexs enter>>>");

		Logger.vLog(TAG, "getChapterIndexs exit>>>");

		return mChapterIndexs;
	}

	protected int getChapterNumber() {
		Logger.vLog(TAG, "getChapterNumber enter>>>");

		int num = -1;
		final TOCTree root = mZlApplication.Model.TOCTree;
		if (null == root) {

		} else {
			num = root.getSize() - 1;
		}

		Logger.vLog(TAG, "getChapterNumber exit>>>");

		return num;
	}

	protected int goToChapterByTitle(String title) {
		Logger.vLog(TAG, "goToChapter enter>>>");

		int index = searchChapter(title);
		if (!mEngineCode.noError(index)) {
			mEngineCode.setLastCode(index);
		} else {
			gotoPosition(index, 0, 0);
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "goToChapter exit.");
		return index;
	}

	protected int goToChapterByIndex(int id) {
		Logger.vLog(TAG, "goToChapterIndex enter>>>");

		int code = gotoPosition(mChapterIndexs[id], 0, 0);

		Logger.vLog(TAG, "goToChapterIndex exit.");
		return code;
	}

	protected int goToChapterByParagraphIndex(int paragraphIndex) {
		Logger.vLog(TAG, "goToChapterByParagraphIndex enter>>>");

		int code = gotoPosition(paragraphIndex, 0, 0);

		Logger.vLog(TAG, "goToChapterByParagraphIndex exit.");
		return code;
	}

	protected int getCurrentChapterIndex() {
		Logger.vLog(TAG, "getCurrentChapterIndex enter>>>");

		int index = EngineMsgCode.ENGINE_WARN_CHAPTER_INDEX_FAILED;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			mEngineCode.setLastCode(index);
		} else {
			index = searchChapter(view.getStartCursor().getParagraphIndex());
		}

		Logger.vLog(TAG, "getCurrentChapterIndex exit>>>" + " index = " + index);

		return index;
	}

	protected int getCurrentParagraphIndex() {
		Logger.vLog(TAG, "getCurrentParagraphIndex enter>>>");

		int index = FbreaderMsgCode.STATE_ERROR_PH_INDEX_ERROR;
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();

		if (null == view) {
			mEngineCode.setLastCode(index);
		} else {
			index = view.getEndCursor().getParagraphIndex();
		}

		Logger.vLog(TAG, "getCurrentParagraphIndex exit>>>" + " index = "
				+ index);

		return index;
	}

	protected String getLocation() {
		String location = "null";

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		final ZLTextWordCursor cursor = view.getStartCursor();

		int pi = cursor.getParagraphIndex();
		int wi = cursor.getElementIndex();
		int ci = cursor.getCharIndex();

		location = String.format(LOCATION_PREFIX, pi, wi, ci);

		return location;
	}

	protected String getLocation(int pi, int wi, int ci) {
		String location = "null";

		location = String.format(LOCATION_PREFIX, pi, wi, ci);

		return location;
	}

	protected int[] parseLocation(String location) {
		Logger.vLog(TAG, "parseLocation enter>>> location = " + location);
		int[] indexs = { -1, -1, -1 };

		try {
			if (location.startsWith(LOCATION_PREFIX_HEADER)) {
				String[] temp = location.substring(
						LOCATION_PREFIX_HEADER.length() + 1,
						location.length() - 1).split(",");
				indexs[0] = Integer.valueOf(temp[0]);
				indexs[1] = Integer.valueOf(temp[1]);
				indexs[2] = Integer.valueOf(temp[2]);
			}
		} catch (Exception e) {
			Logger.vLog(TAG, "e =>>>" + e);
		}

		Logger.vLog(TAG, "parseLocation exit>>>");

		return indexs;
	}

	// search part begin
	protected int startSearch(String keyword) {
		Logger.vLog(TAG, "getSearchResult enter>>>");

		int code = 0;

		if (null == mZlApplication) {
		} else {
			mZlApplication.TextSearchPatternOption.setValue(keyword);
			code = mZlApplication.getTextView().search(keyword, true, false,
					false, false);
		}

		Logger.vLog(TAG, "getSearchResult exit.");

		return code;
	}

	protected int searchNext() {
		Logger.vLog(TAG, "searchNext enter>>>");

		int code = 0;

		if (null == mZlApplication) {
			code = FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
		} else {
			if (!mZlApplication.isActionEnabled(ActionCode.FIND_NEXT)) {
				code = EngineMsgCode.ENGINE_ERROR_SEARCH_GO_NEXT_FAILED;
			} else {
				mZlApplication.doAction(ActionCode.FIND_NEXT);
			}
		}

		Logger.vLog(TAG, "searchNext exit.");

		return code;
	}

	protected int searchPrevious() {
		Logger.vLog(TAG, "searchPrevious enter>>>");

		int code = 0;
		if (null == mZlApplication) {
			code = FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
		} else {
			if (!mZlApplication.isActionEnabled(ActionCode.FIND_PREVIOUS)) {
				code = EngineMsgCode.ENGINE_ERROR_SEARCH_GO_PREVIOUS_FAILED;
			} else {
				mZlApplication.doAction(ActionCode.FIND_PREVIOUS);
			}
		}

		Logger.vLog(TAG, "searchPrevious exit.");

		return code;
	}

	protected BookSearchInfo.BookSearchResult[] getSearchResult() {
		Logger.vLog(TAG, "getSearchResult enter>>>");
		
		BookSearchInfo.BookSearchResult[] results = null;
		List<ZLTextMark> marks;
		ZLTextMark tempMark;
		IEngineService ies = (IEngineService) ERManager.getService(ERManager.ERENGINE_SERVICE);
		com.easyview.ebook.reader.engine.model.Book book = ies.getBook();
		
		int code = 0;

		if (null == mZlApplication) {
			code = FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
			mEngineCode.setLastCode(code);
		} else {
			final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
			ZLTextModel model = view.getModel();
			if (!view.findResultsAreEmpty()) {
				marks = model.getMarks();
				results = book.makeSearchResults(marks.size());
				for (int i = 0; i < results.length; i++) {
					tempMark = marks.get(i);
					if (null == results[i]) {
						results[i] = book.makeSearchResult();
					}
					convertMarkToSearchResult(tempMark, results[i]);
				}
			}
		}

		Logger.vLog(TAG, "getSearchResult exit.");
		
		return results;
	}

	protected int quitSearch() {
		Logger.vLog(TAG, "quitSearch enter>>>");

		int code = 0;
		if (null == mZlApplication) {
			return FbreaderMsgCode.STATE_ERROR_APP_INIT_ERROR;
		} else {
			mZlApplication.doAction(ActionCode.CLEAR_FIND_RESULTS);
		}

		Logger.vLog(TAG, "quitSearch exit.");

		return code;
	}

	protected String getSummary() {
		Logger.vLog(TAG, "getSummary enter>>>");

		String summary = "";

		if (null == mZlApplication) {
		} else {
			final ZLTextView view = (ZLTextView) mZlApplication
					.getCurrentView();
			final ZLTextWordCursor cursor = view.getStartCursor();
			summary = getBookmarkText(cursor, SUMMARY_MAX_NUM);
		}

		Logger.vLog(TAG, "getSummary exit.");

		return summary.trim().replace("\n", " ");
	}

	protected void openLinkByTouch(int x, int y) {
		Logger.vLog(TAG, "openLinkByTouch enter>>>");

		final ZLView view = mZlApplication.getCurrentView();
		view.onFingerSingleTap(x, y);

		Logger.vLog(TAG, "openLinkByTouch exit.");
	}

	protected void startHighlightByXY(int x, int y) {
		Logger.vLog(TAG, "startHighlightByXY enter>>>");

		final FBView view = (FBView) mZlApplication.getCurrentView();
		// view.onFingerPress(x, y);
		view.onFingerLongPress(x, y);

		Logger.vLog(TAG, "startHighlightByXY exit.");
	}

	protected void moveHighlightByXY(int x, int y) {
		Logger.vLog(TAG, "moveHighlightByXY enter>>>");

		final FBView view = (FBView) mZlApplication.getCurrentView();
		view.onFingerMove(x, y);

		Logger.vLog(TAG, "moveHighlightByXY exit.");
	}

	protected void endHighlightByXY(int x, int y) {
		Logger.vLog(TAG, "endHighlightByXY enter>>>");

		final FBView view = (FBView) mZlApplication.getCurrentView();
		view.onFingerRelease(x, y);

		Logger.vLog(TAG, "endHighlightByXY exit.");
	}

	protected String getSelectedText() {
		Logger.vLog(TAG, "getSelectedText enter>>>");

		final FBView view = (FBView) mZlApplication.getCurrentView();
		String selStr = view.getSelectedText();
		Logger.dLog(TAG, "iEndHighlightByXY selStr = " + selStr);

		Logger.vLog(TAG, "getSelectedText exit.");

		return selStr;
	}

	protected String getHighlightStartLoc() {
		Logger.vLog(TAG, "getHighlightStartLoc enter>>>");

		String loc = com.easyview.ebook.reader.engine.model.Book.VALUE_NULL;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		ZLTextPosition position = view.getSelectionStartPosition();

		if (null == position) {
			return loc;
		}

		int pi = position.getParagraphIndex();
		int wi = position.getElementIndex();
		int ci = position.getCharIndex();

		loc = String.format(LOCATION_PREFIX, pi, wi, ci);

		Logger.vLog(TAG, "getHighlightStartLoc exit.");

		return loc;
	}

	protected String getHighlightEndLoc() {
		Logger.vLog(TAG, "getHighlightEndLoc enter>>>");

		String loc = com.easyview.ebook.reader.engine.model.Book.VALUE_NULL;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		ZLTextPosition position = view.getSelectionStartPosition();

		if (null == position) {
			return loc;
		}

		int pi = position.getParagraphIndex();
		int wi = position.getElementIndex();
		int ci = position.getCharIndex();

		loc = String.format(LOCATION_PREFIX, pi, wi, ci);

		Logger.vLog(TAG, "getHighlightEndLoc exit.");

		return loc;
	}

	protected boolean delAllHighlight() {
		Logger.vLog(TAG, "delAllHighlight enter>>>");

		boolean res = true;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		view.clearSelection();

		Logger.vLog(TAG, "delAllHighlight exit.");

		return res;
	}

	protected String getPageText() {
		Logger.vLog(TAG, "getPageText enter>>>");

		String text;

		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		text = view.getCurrentPageText();

		Logger.vLog(TAG, "getPageText exit.");

		return text;
	}
	
	protected void setBitmapConfig(Bitmap.Config config) {
		mBitmapConfig = config;
	}

	protected Bitmap.Config getBitmapConfig() {
		return mBitmapConfig;
	}

	protected void convertMarkToSearchResult(ZLTextMark srcMark, BookSearchResult desResult) {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		ZLTextModel model = view.getModel();
		ZLTextWordCursor wordCursor;
		
		desResult.start_pos = getLocation(srcMark.ParagraphIndex, srcMark.Offset, srcMark.Length);
		desResult.end_pos = desResult.start_pos;
		wordCursor = new ZLTextWordCursor(ZLTextParagraphCursor.cursor(model, srcMark.ParagraphIndex));
		desResult.text = getBookmarkText(wordCursor, SUMMARY_MAX_NUM);
	}
	
	protected ZLTextMark convertResultToMark(BookSearchResult desResult) {
		if ( (null == desResult) || desResult.equals(com.easyview.ebook.reader.engine.model.Book.VALUE_NULL)) {
			return null;
		}
		int[] pos = parseLocation(desResult.start_pos);
		ZLTextMark mark = new ZLTextMark(pos[0], pos[1], pos[2]);
		
		return mark;
	}
	
	// return desString
	private String convertString(String srcString, String srcCharset,
			String desCharset) {
		Charset charsetSrc = Charset.forName(srcCharset);
		Charset charsetDes = Charset.forName(desCharset);
		CharsetDecoder decoder = charsetSrc.newDecoder()
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);
		CharsetEncoder encoder = charsetDes.newEncoder()
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);

		ByteBuffer bbuf = null;
		CharBuffer cbuf = null;

		try {
			bbuf = encoder.encode(CharBuffer.wrap(srcString));
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			return null;
		}

		try {
			cbuf = decoder.decode(bbuf);
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			return null;
		}

		return cbuf.toString();
	}

	// get bookmark summary
	private String getBookmarkText(ZLTextWordCursor cursor, int maxWords) {
		cursor = new ZLTextWordCursor(cursor);

		final StringBuilder builder = new StringBuilder();
		final StringBuilder sentenceBuilder = new StringBuilder();
		final StringBuilder phraseBuilder = new StringBuilder();

		int wordCounter = 0;
		int sentenceCounter = 0;
		int storedWordCounter = 0;
		boolean lineIsNonEmpty = false;
		boolean appendLineBreak = false;
mainLoop: 
		while (wordCounter < maxWords && sentenceCounter < 3) {
			while (cursor.isEndOfParagraph()) {
				if (!cursor.nextParagraph()) {
					break mainLoop;
				}
				if ((builder.length() > 0)
						&& cursor.getParagraphCursor().isEndOfSection()) {
					break mainLoop;
				}
				if (phraseBuilder.length() > 0) {
					sentenceBuilder.append(phraseBuilder);
					phraseBuilder.delete(0, phraseBuilder.length());
				}
				if (sentenceBuilder.length() > 0) {
					if (appendLineBreak) {
						builder.append("\n");
					}
					builder.append(sentenceBuilder);
					sentenceBuilder.delete(0, sentenceBuilder.length());
					++sentenceCounter;
					storedWordCounter = wordCounter;
				}
				lineIsNonEmpty = false;
				if (builder.length() > 0) {
					appendLineBreak = true;
				}
			}
			final ZLTextElement element = cursor.getElement();
			// add zenki-zha-xxx, some books may crash
			if (element == null) {
				break mainLoop;
			}
			
			if (element instanceof ZLTextWord) {
				final ZLTextWord word = (ZLTextWord) element;
				if (lineIsNonEmpty) {
					phraseBuilder.append(" ");
				}
				phraseBuilder.append(word.Data, word.Offset, word.Length);
				++wordCounter;
				lineIsNonEmpty = true;
				switch (word.Data[word.Offset + word.Length - 1]) {
				case ',':
				case ':':
				case ';':
				case ')':
					sentenceBuilder.append(phraseBuilder);
					phraseBuilder.delete(0, phraseBuilder.length());
					break;
				case '.':
				case '!':
				case '?':
					++sentenceCounter;
					if (appendLineBreak) {
						builder.append("\n");
						appendLineBreak = false;
					}
					sentenceBuilder.append(phraseBuilder);
					phraseBuilder.delete(0, phraseBuilder.length());
					builder.append(sentenceBuilder);
					sentenceBuilder.delete(0, sentenceBuilder.length());
					storedWordCounter = wordCounter;
					break;
				}
			}
			cursor.nextWord();
		}
		if (storedWordCounter < 4) {
			if (sentenceBuilder.length() == 0) {
				sentenceBuilder.append(phraseBuilder);
			}
			if (appendLineBreak) {
				builder.append("\n");
			}
			builder.append(sentenceBuilder);
		}
		return builder.toString();
	}

	// search part end

	private void initFbreader() {
		mIes = (IEngineService) ERManager
				.getService(ERManager.ERENGINE_SERVICE);

		new ZLAndroidLibrary(mIes.getApplication());
		new ZLAndroidImageManager();

		// 初始化Config數據庫
//		mZlSqlConfig = new ZLSQLiteConfig(mIes.getContext());

		// 內容渲染模塊
		mBitmapManager = new BitmapManager();
		mBitmapManager.setBitmapConfig(getBitmapConfig());
		mBitmapManager.reset();

		// create in ZLAndroidApplication.java
		// new ZLAndroidDialogManager();
		// new ZLAndroidLibrary(this);
	}

	private FBReaderApp createApplication(ZLFile file) {
		if (SQLiteBooksDatabase.Instance() == null) {
			mSqlBooksDb = new SQLiteBooksDatabase(mIes.getContext(), "READER");
		}
		return new FBReaderApp(file != null ? file.getPath() : null);
	}

	private int searchChapter(String title) {
		int id = EngineMsgCode.ENGINE_WARN_CHAPTER_JUMP_FAILED;

		for (int i = 0; i < mChapterTitles.length; i++) {
			if (mChapterTitles[i].equals(title)) {
				id = i;
				break;
			}
		}

		return id;
	}

	private int searchChapter(int paragraphIndex) {
		int id = EngineMsgCode.ENGINE_WARN_CHAPTER_JUMP_FAILED;

		for (int i = 0; i < mChapterTitles.length; i++) {
			if (mChapterIndexs[i] == paragraphIndex) {
				id = i;
				break;
			}
		}

		Logger.dLog(TAG, "paragraphIndex = " + paragraphIndex + " id = " + id);

		return id;
	}

	private void resetCache() {
		mChapterIndexs = null;
		mChapterTitles = null;

		if (mBitmapManager != null) {
			mBitmapManager.reset();
		}

		if (mZlApplication != null) {
			mZlApplication.clearTextCaches();
		}

//		if (mZlSqlConfig != null) {
//			mZlSqlConfig.closeDatabase();
//		}
//		
//		if (mSqlBooksDb != null) {
//			mSqlBooksDb.closeDatabase();
//		}

		Logger.dLog(TAG, "resetCache finished");
	}

	private boolean computerTextSize() {
		final ZLTextView view = (ZLTextView) mZlApplication.getCurrentView();
		ZLTextModel myModel = view.getModel();
		if (myModel == null || myModel.getParagraphsNumber() == 0) {
			return false;
		}

		final float factor = view.computeCharsPerPage();
		final float textSize = view.computePageNumber() * factor;

		return true;
	}
}
