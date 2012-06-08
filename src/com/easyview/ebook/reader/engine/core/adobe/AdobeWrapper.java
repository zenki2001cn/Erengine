/**
 * @file       ReaderWrapper.java
 *
 * @revision:  none 
 *
 * @version    0.0.1
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-1-26 下午04:03:37 
 */

package com.easyview.ebook.reader.engine.core.adobe;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.easyview.ebook.jni.SearchResult;
import com.easyview.ebook.reader.engine.core.DecAdapter;
import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.core.ReaderWrapper;
import com.easyview.ebook.reader.engine.core.EngineCode.AdobeMsgCode;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.core.adobe.AdobeAdapter;
import com.easyview.ebook.reader.engine.model.AdobePage;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookSearchInfo;
import com.easyview.ebook.reader.engine.model.IBookEmphasisCursor;
import com.easyview.ebook.reader.engine.model.IBookMarkCursor;
import com.easyview.ebook.reader.engine.model.IBookSearchCursor;
import com.easyview.ebook.reader.engine.model.Page;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.easyview.ebook.reader.engine.model.ChapterTreeInfo.ChapterTreeState;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 * 
 */
public class AdobeWrapper extends ReaderWrapper {
	static private final String TAG = "AdobeWrapper";

	static private AdobeWrapper mAction;
	static private AdobeAdapter mAdapter;

	private int mTotalNumber = 1;

	private final int MAX_FONT_LEVEL = 3;
	private final int MIN_FONT_LEVEL = 0;
	private final int DEFAULT_FONT_LEVEL = 1;

	// 对全文内容做连续搜索
	private final int SEARCH_MODE_CONTINUE = 1;
	// 只对目录搜索一次
	private final int SEARCH_MODE_ONCE = 0;
	// 每次搜索的数量
	private final int SEARCH_PER_SIZE = 10;
	// 横竖屏标志
	private final int ROTATE_ANGLE = 90;

	private double mHighlightStartX = -1;
	private double mHighlightStartY = -1;
	private double mHighlightEndX = -1;
	private double mHighlightEndY = -1;
	private int mHighlightColor = -1;

	public interface ADOBE_META_TYPE {
		static public final String META_TITLE = "DC.title";
		static public final String META_AUTHOR = "DC.creator";
		static public final String META_PUBLISHER = "DC.publisher";
	}

	private AdobeWrapper() {
		super();

		if (null == mAdapter) {
			mAdapter = AdobeAdapter.getInstance();
		}
	}

	public static AdobeWrapper getInstance() {
		if (null == mAction) {
			mAction = new AdobeWrapper();
		}

		return mAction;
	}

	protected void free() {
		super.free();

		// TaskManager must stop, or AdobeAdapter may crash
		// if (getTaskManager() != null) {
		// getTaskManager().free();
		// }

		if (mAdapter != null) {
			mAdapter.free();
			mAdapter = null;
		}

		if (mAction != null) {
			mAction = null;
		}

		Logger.dLog(TAG, "ReaderWrapper free mAdapter: " + mAdapter
				+ " mAction: " + mAction);
	}

	private boolean getPageByNumber(AdobePage page, int number) {
		if (null == page) {
			Logger.eLog(TAG, "getPage is null");
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		int code = -1;
		if (number > mTotalNumber) {
			code = mAdapter.goToPosition(mTotalNumber - 1);
		} else if (number <= 1) {
			code = mAdapter.goToPosition(0);
		} else {
			code = mAdapter.goToPosition(number - 1);
		}

		if (hasError(code)) {
			return false;
		}

		return getPage(page);
	}

	private boolean getPageByLocation(AdobePage page, String location) {
		if (null == page) {
			Logger.eLog(TAG, "getPage = " + page);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		int code = mAdapter.goToLocation(location);
		if (hasError(code)) {
			return false;
		}

		return getPage(page);
	}

	private boolean getPageBySearchResult(AdobePage page,
			BookSearchInfo.BookSearchResult result) {
		mAdapter.goToLocation(result.start_pos);
		mAdapter.delAllHighlight();

		boolean highlighted = mAdapter.setHighlightbyloc(result.start_pos,
				result.end_pos, 0);

		if (highlighted) {
		}

		return getPage(page);
	}

	private boolean getPage(AdobePage page) {
		Bitmap bmp = null;
		Book book = getBook();

		if (null == page) {
			Logger.eLog(TAG, "getPage = " + page);
			setLastCode(EngineMsgCode.ENGINE_ERROR_CODE_ERROR);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "assertWork == null");
			return false;
		}

		int buf[] = page.getBuf();
		mAdapter.getCurrentPage(buf);

		int width = book.getViewWidth();
		int height = book.getViewHeight();
		try {
			bmp = Bitmap.createBitmap(buf, width, height, getBitmapConfig());
//			if (true == getLandMode()) {
//				Matrix m = new Matrix();
//				m.postRotate(ROTATE_ANGLE);
//				bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, false);
//			}
		} catch (OutOfMemoryError e) {
			Logger.eLog(TAG, "CreateBitmap error " + e);
			bmp = null;
			setLastCode(EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		page.setPageContent(bmp);
		String location = mAdapter.getCurrentLocation();
		int num = (int) mAdapter.getPagePosition(location) + 1;
		page.setPageNum(num);
		page.setLocation(location);
		page.setSummary(mAdapter.getSummaryByLocation(location));
		page.setLoaded(true);

		return true;
	}

	private void updateBookInfo(Page page) {
		Book book = getBook();

		if (book != null) {
			Logger.iLog(TAG, "updateBookInfo pageNum = " + page.getPageNum());
			book.updateFromPage(page);
			book.setFontLevel(getCurFontLevel());
		}
	}

	private int checkPassword() {
		int code = EngineMsgCode.NO_ERROR;
		Book book = getBook();
		String username = book.getUserName();
		String password = book.getPassword();

		if (book.getBookType() == Book.BookType.PDF) {
			if ((password != null) && (!TextUtils.isEmpty(password))) {
				code = mAdapter.setPDFPassword(password);
				Logger.dLog(TAG, "checkPassword code = " + code);
			}
		} else if (book.getBookType() == Book.BookType.EPUB) {
			if ((password != null) && (!TextUtils.isEmpty(password))
					&& (username != null) && (!TextUtils.isEmpty(username))) {
				code = mAdapter.setPassHash(username, password);
				Logger.dLog(TAG, "checkPassword code = " + code);
			}
		}

		return code;
	}

	private void resetHighlight() {
		mHighlightStartX = -1;
		mHighlightStartY = -1;
		mHighlightEndX = -1;
		mHighlightEndY = -1;
		mHighlightColor = -1;
	}

	private BookSearchInfo.BookSearchResult[] convertAdobeResult(
			SearchResult[] results) {
		Book book = getBook();
		BookSearchInfo.BookSearchResult[] bookResults = book
				.makeSearchResults(results.length);
		for (int i = 0; i < results.length; i++) {
			bookResults[i].text = results[i].text;
			bookResults[i].start_pos = results[i].start_pos;
			bookResults[i].end_pos = results[i].end_pos;
		}

		return bookResults;
	}

	protected void iCloseBook() {
		int code = 0;

		if (!canWork()) {
			Logger.eLog(TAG, "AssertWork == null");
			return;
		}

		Book book = getBook();
		if (book.getOpened()
				|| (getLastCode() == AdobeMsgCode.STATE_FATAL_PREVIOUS_FILE_NOT_CLOSED)) {
			code = mAdapter.closeFile();

			if (hasError(code)) {
				Logger.eLog(TAG, "Close book error");
			} else {
				Logger.vLog(TAG, "Close book success");
				book.setOpened(false);
			}

			try {
				saveBookDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			Logger.vLog(TAG, "Book is not opened");
		}
	}

	protected boolean iLoadBook() {
		String bookPath;

		if (!canWork()) {
			Logger.eLog(TAG, "iLoadBook assertWork failed");
			return false;
		}

		/*
		 * Step 1. ensure close previous file.
		 */
		int code = mAdapter.closeFile();
		if (hasError(code)) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_CLOSE_PREVIOUS_FAILED);
			Logger.eLog(TAG, "Close previous file error, code = " + code);
		} else {
			Logger.dLog(TAG, "Close previous file ok");
		}

		/*
		 * Step 2. 2.1 check password
		 */
		code = checkPassword();
		if (hasError(code)) {
			Logger.eLog(TAG, "checkPassword error");
			return false;
		}

		/*
		 * 2.2 get file path name and open file.
		 */
		Book book = getBook();
		bookPath = book.getBookPath();
		code = mAdapter.openFile(bookPath);
		if (hasError(code)) {
			Logger.eLog(TAG, "Open book error, code = " + code);

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				Logger.eLog(TAG, "Thread.sleep error" + e);
			}

			return false;
		} else {
			/*
			 * Step 4. get meta data about opened book, set total page number.
			 */
			Book.MetaData data = book.getMetaData();

			if (data.isEmpty()) {
				String title = mAdapter.getMetaData(ADOBE_META_TYPE.META_TITLE);
				String author = mAdapter
						.getMetaData(ADOBE_META_TYPE.META_AUTHOR);
				String publisher = mAdapter
						.getMetaData(ADOBE_META_TYPE.META_PUBLISHER);

				data.setValue(Book.MetaData.META_TITLE,
						((title != null) ? title : Book.VALUE_NULL));
				data.setValue(Book.MetaData.META_AUTHOR,
						((author != null) ? author : Book.VALUE_NULL));
				data.setValue(Book.MetaData.META_PUBLISHER,
						((publisher != null) ? publisher : Book.VALUE_NULL));
				book.setMetaData(data);
			}

			mTotalNumber = mAdapter.getNumPages();
			book.setTotalPageNumber(mTotalNumber);
			book.setLastAccessTime(System.currentTimeMillis());
			book.setOpened(true);

			Logger.vLog(TAG, "Open book success");
		}

		/*
		 * Step 5.
		 */
		try {
			// 5.1 set view size.
			mAdapter.setViewPortSize(book.getViewWidth(), book.getViewHeight());
			// 5.2 set font size.
			setCurFontLevel(book.getFontLevel());
			mAdapter.setFontSize(getCurFontLevel());

			// 5.3 set last page location or page index.
			AdobePage page = (AdobePage) book.getCurPage();
			if (book.getLastLocation().equals(Book.VALUE_NULL)) {
				getPageByNumber(page, book.getLastPageNumber());
			} else {
				getPageByLocation(page, book.getLastLocation());
			}

			/*
			 * Step 6. 6.1 if needed, update and show the content.
			 */
			updateView();
			// 6.2 update book information.
			updateBookInfo(page);
		} catch (Exception e) {
			Logger.eLog(TAG, "Load book error");
			return false;
		}

		return true;
	}

	protected void iLoadChapterInformation() {
		String[] chapters;
		int[] indexs;

		if (!canWork()) {
			Logger.eLog(TAG, "iLoadChapterInformation assertWork failed");
			return;
		}

		Book book = getBook();
		if (!book.chapterIsLoad()) {
			if (mAdapter.getNumChapters() > 0) {
				chapters = mAdapter.getChapterList();
				indexs = new int[chapters.length];
				for (int i = 0; i < chapters.length; i++) {
					if (null == chapters[i]) {
						chapters[i] = "";
						indexs[i] = 0;
					} else {
						indexs[i] = (int) mAdapter
								.getChapterPagePosition(chapters[i]);
					}
				}
				book.chapterInit(indexs, chapters);
			} else {
				book.chapterInit(null, null);
			}

			Logger.dLog(TAG,
					"loadChapterInfo finished == " + book.chapterIsLoad());
		}

		// debug code can be delete
		// String chapterList[] = mOpenedBook.getChapterTree().getChapterList();
		// int[] nums = mOpenedBook.getChapterTree().getChapterPageNums();
		// for (int i = 0; i < chapterList.length; i++) {
		// Logger.iLog(TAG,
		// "chapter: "
		// + i
		// + " "
		// + chapterList[i]
		// + " page: "
		// + nums[i]
		// + " index: "
		// + mOpenedBook.getChapterTree().getChapterIndex(
		// chapterList[i]));
		// }
	}

	protected void iChapterJumpByTitle(String title) {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterJumpByTitle assertWork failed");
			return;
		}

		int code = mAdapter.goToChapter(title);
		if (hasError(code)) {
			return;
		}

		Book book = getBook();
		try {
			AdobePage page = (AdobePage) book.getCurPage();
			getPage(page);
			setBorder(false);
			updateView();
			updateBookInfo(page);

			book.resetPrePage();
			book.resetNextPage();
		} catch (Exception e) {
			Logger.eLog(TAG, "ichapterJumpTo failed");
		}

		Logger.vLog(TAG, "ichapterJumpTo finished");
	}

	protected void iChapterJumpByIndex(int index) {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterJumpByIndex assertWork failed");
			return;
		}

		Book book = getBook();

		if (index < 0 || index >= book.getMaxChapterNum()) {
			Logger.dLog(TAG, "Chapter index out of bounds!");
			setLastCode(EngineMsgCode.ENGINE_WARN_CHAPTER_JUMP_FAILED);
			return;
		}

		int code = mAdapter.goToChapterIndex(index);
		if (hasError(code)) {
			return;
		}

		try {
			AdobePage page = (AdobePage) book.getCurPage();
			getPage(page);
			setBorder(false);
			updateView();
			updateBookInfo(page);

			book.resetPrePage();
			book.resetNextPage();
		} catch (Exception e) {
			Logger.eLog(TAG, "ichapterJumpTo failed");
		}

		Logger.vLog(TAG, "ichapterJumpTo finished");
	}

	protected void iChapterUp() {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterUp assertWork failed");
			return;
		}

		Book book = getBook();
		AdobePage curPage = (AdobePage) book.getCurPage();
		int curPageIndex = curPage.getPageNum() - 1;
		if (book.hasPreviousChapter()) {
			int preIndex = book.getPreviousChapterIndex(curPageIndex);

			int code = mAdapter.goToChapterIndex(preIndex);
			if (hasError(code)) {
				return;
			}

			try {
				curPage = (AdobePage) book.getCurPage();
				getPage(curPage);
				setBorder(false);
				updateView();
				updateBookInfo(curPage);

				book.resetPrePage();
				book.resetNextPage();
			} catch (Exception e) {
				Logger.eLog(TAG, "ichapterUp failed ");
			}

			// debug code, can be delete
			// int curIndex = mOpenedBook.getCurChapterIndex();
			// int pageIndex = mOpenedBook.chapterGetCurPageIndex(curIndex);
			// Logger.vLog(TAG, "ichapterUp finished " + curIndex + " -- " +
			// pageIndex);
		} else {
			Logger.vLog(TAG, "hasPreviousChapter = false");
			setLastCode(EngineMsgCode.ENGINE_WARN_CHAPTER_UP_FAILED);
		}
	}

	protected void iChapterDown() {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterDown assertWork failed");
			return;
		}

		Book book = getBook();
		AdobePage curPage = (AdobePage) book.getCurPage();
		int curPageIndex = curPage.getPageNum() - 1;
		if (book.hasNextChapter()) {
			int nextIndex = book.getNextChapterIndex(curPageIndex);

			int code = mAdapter.goToChapterIndex(nextIndex);
			if (hasError(code)) {
				return;
			}

			try {
				curPage = (AdobePage) book.getCurPage();
				getPage(curPage);
				setBorder(false);
				updateView();
				updateBookInfo(curPage);

				book.resetPrePage();
				book.resetNextPage();
			} catch (Exception e) {
				Logger.eLog(TAG, "ichapterUp failed");
			}

			// debug code, can be delete
			// int curIndex = mOpenedBook.getCurChapterIndex();
			// int pageIndex = mOpenedBook.chapterGetCurPageIndex(curIndex);
			// Logger.vLog(TAG, "ichapterUp finished " + curIndex + " -- " +
			// pageIndex);
		} else {
			Logger.vLog(TAG, "hasNextChapter = false");
			setLastCode(EngineMsgCode.ENGINE_WARN_CHAPTER_DOWN_FAILED);
		}
	}

	protected void iChapterUpdateInfo() {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterUpdateInfo assertWork failed");
			return;
		}

		Book book = getBook();
		if (book.chapterIsLoad()) {
			if (book.chapterIsEmpty()) {
				book.setCurChapterIndex(-1);
			} else {
				book.setCurChapterIndex(mAdapter.getCurrentChapterIndex());
				book.setChapterState(ChapterTreeState.CURRENT_LOADED);

				// Logger.d(TAG,
				// "Tree.getCurChapterIndex(): "
				// + mOpenedBook.getCurChapterIndex()
				// + " Tree.getMaxChapterNum():"
				// + mOpenedBook.getMaxChapterNum());
			}
		}
	}

	protected boolean iPageUp() {
		boolean hasPrePage = false;

		if (!canWork()) {
			Logger.eLog(TAG, "iPageUp assertWork failed");
			return false;
		}

		Book book = getBook();
		AdobePage curPage = (AdobePage) book.getCurPage();
		AdobePage prePage = (AdobePage) book.getPrePage();
		AdobePage nextPage = (AdobePage) book.getNextPage();

		try {
			if (prePage.isLoaded()) {
				Logger.vLog(TAG, "PrePage is loaded");
				nextPage.copy(curPage);
				curPage.copy(prePage);
				setBorder(false);
				updateView();
				prePage.reset();

				hasPrePage = mAdapter.getPreviousPage();
				setBorder(!hasPrePage);
			} else {
				Logger.vLog(TAG, "PrePage is not loaded");
				hasPrePage = mAdapter.getPreviousPage();
				setBorder(!hasPrePage);

				if (hasPrePage) {
					nextPage.copy(curPage);
					getPage(curPage);
					updateView();
				}
			}

			Logger.vLog(TAG, "Has prepage = " + hasPrePage);
			// update Book information
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "TurnPageUp error");
			setLastCode(EngineMsgCode.STATE_ERROR_PAGE_UP_FAILED);
			return false;
		}

		return hasPrePage;
	}

	protected boolean iPageDown() {
		boolean hasNextPage = false;

		if (!canWork()) {
			Logger.eLog(TAG, "iPageDown assertWork failed");
			return false;
		}

		Book book = getBook();
		AdobePage curPage = (AdobePage) book.getCurPage();
		AdobePage prePage = (AdobePage) book.getPrePage();
		AdobePage nextPage = (AdobePage) book.getNextPage();

		try {
			if (nextPage.isLoaded()) {
				Logger.vLog(TAG, "NextPage is loaded");
				prePage.copy(curPage);
				curPage.copy(nextPage);
				setBorder(false);
				updateView();
				nextPage.reset();

				hasNextPage = mAdapter.getNextPage();
				setBorder(!hasNextPage);
			} else {
				Logger.vLog(TAG, "NextPage is not loaded");
				hasNextPage = mAdapter.getNextPage();
				setBorder(!hasNextPage);

				if (hasNextPage) {
					prePage.copy(curPage);
					getPage(curPage);
					updateView();
				}
			}

			Logger.vLog(TAG, "Has nextpage = " + hasNextPage);
			// update Book information
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "TurnPageDown error");
			setLastCode(EngineMsgCode.STATE_ERROR_PAGE_DOWN_FAILED);
			return false;
		}

		return hasNextPage;
	}

	protected boolean iPageJumpTo(int pageNum) {
		Logger.vLog(TAG, "iPageJumpTo pageNum = " + pageNum + " totalnum = "
				+ mTotalNumber);
		if ((pageNum > mTotalNumber) || (pageNum <= 0)) {
			Logger.vLog(TAG, "PageNum is not invalid");
			setLastCode(EngineMsgCode.ENGINE_ERROR_PAGE_NUMBER_INVALID);
			return false;
		}

		if (!canWork()) {
			Logger.vLog(TAG, "iPageJumpTo assertWork failed");
			return false;
		}

		Book book = getBook();
		try {
			AdobePage curPage = (AdobePage) book.getCurPage();
			mAdapter.goToPosition(pageNum - 1);

			// if (hasError(code)) {
			// return false;
			// }

			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			book.resetPrePage();
			book.resetNextPage();

			// update Book information
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.vLog(TAG, "PageJump error");
			setLastCode(EngineMsgCode.STATE_ERROR_GOTO_PAGE_FAILED);
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.ReaderWrapper#iPageZoomLevel(int)
	 */
	@Override
	protected boolean iPageZoomLevel(int lv) {
		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomLevel assertWork failed");
			return false;
		}
		
		int desFontLev;
		if (lv > MAX_FONT_LEVEL) {
			desFontLev = MAX_FONT_LEVEL;
		} else if (lv < MIN_FONT_LEVEL) {
			desFontLev = MIN_FONT_LEVEL;
		} else {
			desFontLev = lv;
		}
		
		Book book = getBook();
		try {
			AdobePage curPage = (AdobePage) book.getCurPage();

			mAdapter.setFontSize(desFontLev);
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			book.resetPrePage();
			book.resetNextPage();

			// update Book information
			setCurFontLevel(desFontLev);
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iPageZoomLevel error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_LEVEL_FAILED);
			return false;
		}
		
		return true;
	}
	
	protected boolean iPageZoomIn() {
		int fontLevel = getCurFontLevel();
		if (fontLevel < MAX_FONT_LEVEL) {
			fontLevel++;
			setCurFontLevel(fontLevel);
		} else {
			Logger.vLog(TAG, "iPageZoomIn max");
			setLastCode(EngineMsgCode.ENGINE_WARN_MAX_FONT);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomIn assertWork failed");
			return false;
		}

		Logger.vLog(TAG, "ZoomIn level = " + fontLevel);

		Book book = getBook();
		try {
			AdobePage curPage = (AdobePage) book.getCurPage();

			mAdapter.setFontSize(fontLevel);
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			book.resetPrePage();
			book.resetNextPage();

			// update Book information
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "ZoomIn error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_IN_FAILED);
			return false;
		}

		return true;
	}

	protected boolean iPageZoomOut() {
		int fontLevel = getCurFontLevel();
		if (fontLevel > MIN_FONT_LEVEL) {
			fontLevel--;
			setCurFontLevel(fontLevel);
		} else {
			Logger.vLog(TAG, "iPageZoomOut min");
			setLastCode(EngineMsgCode.ENGINE_WARN_MIN_FONT);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomOut assertWork failed");
			return false;
		}

		Logger.vLog(TAG, "ZoomOut level = " + fontLevel);

		Book book = getBook();
		try {
			AdobePage curPage = (AdobePage) book.getCurPage();

			mAdapter.setFontSize(fontLevel);
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			book.resetPrePage();
			book.resetNextPage();

			// update Book information
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "ZoomOut error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_OUT_FAILED);
			return false;
		}

		return true;
	}

	protected void preloadPage() {
		preloadPreviousPage();
		preloadNextPage();
	}

	protected boolean preloadPreviousPage() {
		if (!canWork()) {
			Logger.eLog(TAG, "preloadPreviousPage assertWork failed");
			return false;
		}

		if (hasError(getLastCode())) {
			if ((getLastCode() == EngineMsgCode.ENGINE_WARN_MIN_FONT)
					|| (getLastCode() == EngineMsgCode.ENGINE_WARN_MAX_FONT)) {
				setLastCode(EngineMsgCode.NO_ERROR);
			} else {
				Logger.eLog(TAG,
						"Has error no handle, exit preloadPreviousPage");
				return false;
			}
		}

		Book book = getBook();
		AdobePage prePage = (AdobePage) book.getPrePage();
		if (null == prePage) {
			Logger.eLog(TAG, "GetPage = " + prePage);
			setLastCode(EngineMsgCode.ENGINE_ERROR_CODE_ERROR);
			return false;
		}

		if (!prePage.isLoaded() && mAdapter.getPreviousPage()) {
			getPage(prePage);
			mAdapter.getNextPage();
			Logger.vLog(TAG, "preloadPreviousPage finished");
		}

		return true;
	}

	protected boolean preloadNextPage() {
		if (!canWork()) {
			Logger.eLog(TAG, "preloadNextPage assertWork failed");
			return false;
		}

		if (hasError(getLastCode())) {
			if ((getLastCode() == EngineMsgCode.ENGINE_WARN_MIN_FONT)
					|| (getLastCode() == EngineMsgCode.ENGINE_WARN_MAX_FONT)) {
				setLastCode(EngineMsgCode.NO_ERROR);
			} else {
				Logger.eLog(TAG, "Has error no handle, exit preloadNextPage");
				return false;
			}
		}

		Book book = getBook();
		AdobePage nextPage = (AdobePage) book.getNextPage();
		if (null == nextPage) {
			Logger.eLog(TAG, "getPage = " + nextPage);
			setLastCode(EngineMsgCode.ENGINE_ERROR_CODE_ERROR);
			return false;
		}

		if (!nextPage.isLoaded() && mAdapter.getNextPage()) {
			getPage(nextPage);
			mAdapter.getPreviousPage();
			Logger.vLog(TAG, "preloadNextPage finished");
		}

		return true;
	}

	@Override
	protected DecAdapter getCurAdapter() {
		return mAdapter;
	}

	@Override
	protected void iEnterSearchMode() {
		if (!canWork()) {
			Logger.eLog(TAG, "iEnterSearchMode assertWork failed");
			return;
		}

		SearchResult[] results;
		BookSearchInfo.BookSearchResult[] bookResults;
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();

		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iEnterSearchMode key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		mAdapter.delAllHighlight();
		mAdapter.resetFindText();

		// record current location
		String curLocation = mAdapter.getCurrentLocation();
		// goto cover and search
		mAdapter.goToPosition(1);
		while (mAdapter.getPreviousPage()) {
		}

		bookSearchOps.resetResults();
		results = doSearch(key, SEARCH_MODE_CONTINUE);
		if (null == results) {
			bookSearchOps.setSearchKeyword(null);
			mAdapter.goToLocation(curLocation);
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_RESULT_NULL);
			return;
		}

		bookResults = convertAdobeResult(results);
		bookSearchOps.addResults(bookResults);

		// debug, it can be deleted
		// ArrayList<BookSearchResult> res = book.getSearchResults();
		// for (BookSearchResult searchResult : res) {
		// Logger.dLog(TAG, "search key = " + searchResult.text + " start = "
		// + searchResult.start_pos + " end = " + searchResult.end_pos);
		// }

		AdobePage page = (AdobePage) book.getCurPage();
		BookSearchInfo.BookSearchResult result = bookSearchOps.getNextResult();
		if (result != null) {
			getPageBySearchResult(page, result);
		} else {
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_RESULT_NULL);
			return;
		}

		updateView();
		updateBookInfo(page);

		book.resetNextPage();
		book.resetPrePage();
	}

	@Override
	protected void iSearchGoNext() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoNext assertWork failed");
			return;
		}

		SearchResult[] results;
		BookSearchInfo.BookSearchResult result;
		BookSearchInfo.BookSearchResult[] bookResults;
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
		AdobePage page = (AdobePage) book.getCurPage();

		// check keyword
		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iSearchGoNext key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		result = bookSearchOps.getNextResult();
		if (result != null) {
			getPageBySearchResult(page, result);
		} else {
			setLastCode(AdobeMsgCode.STATE_NORMAL_GET_MORE_SEARCH_RESULT);
			handleError();

			results = doSearch(key, SEARCH_MODE_CONTINUE);
			if (null == results) {
				Logger.eLog(TAG, "iSearchGoNext failed");
				setLastCode(EngineMsgCode.ENGINE_ERROR_SEARCH_GO_NEXT_FAILED);
				return;
			}

			bookResults = convertAdobeResult(results);
			bookSearchOps.addResults(bookResults);
			result = bookSearchOps.getNextResult();
			getPageBySearchResult(page, result);
		}

		updateView();
		updateBookInfo(page);
	}

	@Override
	protected void iSearchGoPrevious() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		BookSearchInfo.BookSearchResult result;
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
		AdobePage page = (AdobePage) book.getCurPage();

		// check keyword
		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iSearchGoPrevious key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		result = bookSearchOps.getPreResult();
		if (result != null) {
			getPageBySearchResult(page, result);
		} else {
			Logger.eLog(TAG, "iSearchGoPrevious failed");
			setLastCode(EngineMsgCode.ENGINE_ERROR_SEARCH_GO_PREVIOUS_FAILED);
		}

		updateView();
		updateBookInfo(page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.ReaderWrapper#iSearchGoto(com.easyview
	 * .ebook.reader.engine.model.BookSearchInfo.SearchResult)
	 */
	@Override
	protected void iSearchGoto(
			com.easyview.ebook.reader.engine.model.BookSearchInfo.BookSearchResult result) {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoto assertWork failed");
			return;
		}

		Book book = getBook();
		AdobePage page = (AdobePage) book.getCurPage();

		if ((result != null) && (!result.start_pos.equals(Book.VALUE_NULL))) {
			getPageBySearchResult(page, result);
		} else {
			Logger.eLog(TAG, "iSearchGoto failed");
			setLastCode(EngineMsgCode.STATE_ERROR_SEARCH_GOTO_RESULT_FAILED);
		}

		updateView();
		updateBookInfo(page);
	}

	@Override
	protected void iSearchUpdateInfo() {
	}

	@Override
	protected void iQuitSearch() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		Logger.eLog(TAG, "iQuitSearch >>>>");
		mAdapter.delAllHighlight();
		mAdapter.resetFindText();

		// clear search keyword
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
		bookSearchOps.setSearchKeyword(null);

		// reset error code
		setLastCode(EngineCode.EngineMsgCode.NO_ERROR);
	}

	private SearchResult[] doSearch(String key, int cont) {
		SearchResult[] results;
		results = mAdapter.getSearchResult(key, SEARCH_PER_SIZE, cont);

		return results;
	}

	@Override
	protected void iAddBookmark() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		try {
			Book book = getBook();
			String location = mAdapter.getCurrentLocation();

			// 判斷當前頁是否已存在書簽，該邏輯移到ERSqliteProxy中處理
			// IDatabaseService dbService = (IDatabaseService) ERManager
			// .getService(ERManager.DATABASE_SERVICE);
			// boolean hasBookmark = dbService.queryBookmark(book, location);
			// if (hasBookmark) {
			// Logger.dLog(TAG, "iAddBookmark has bookmark");
			// return;
			// }

			AdobePage page = (AdobePage) book.getCurPage();
			IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
			Bookmark bookmark = book.makeBookmark();
			int pageNum = (int) mAdapter.getPagePosition(location) + 1;
			String summary = mAdapter.getSummaryByLocation(location);

			page.setSummary(summary);
			bookmark.setBookId(book.getBookId());
			bookmark.setLocation(location);
			bookmark.setPageNum(pageNum);
			bookmark.setSummary(summary);
			bookmark.setTime(System.currentTimeMillis());
			boolean addRes = bookmarkOps.addBookmark(bookmark);

			addBookMarkDB(bookmark);
		} catch (Exception e) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_ADD_FAILED);
		}
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.ReaderWrapper#iGotoBookmark(com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	protected void iGotoBookmark(Bookmark bookmark) {
		if (!canWork()) {
			Logger.eLog(TAG, "iGotoBookmark assertWork failed");
			return;
		}

		if (null == bookmark) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_GOTO_FAILED);
			return;
		}

		Book book = getBook();
		AdobePage page = (AdobePage) book.getCurPage();
		getPageByLocation(page, bookmark.getLocation());

		updateView();
		updateBookInfo(page);
	}
	
	@Override
	protected void iGotoBookmark() {
		if (!canWork()) {
			Logger.eLog(TAG, "iGotoBookmark assertWork failed");
			return;
		}

		Book book = getBook();
		IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
		Bookmark bookmark = bookmarkOps.getBookmarkTogo();
		
		iGotoBookmark(bookmark);
	}

	@Override
	protected void iDeleteBookmark(Bookmark bookmark) {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		if (null == bookmark) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_DEL_FAILED);
			return;
		}

		try {
			Book book = getBook();
			IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
			bookmarkOps.delBookmark(bookmark);

			delBookMarkDB(bookmark);
		} catch (Exception e) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_DEL_FAILED);
		}
	}

	@Override
	protected void iDelAllBookmark() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		try {
			Book book = getBook();
			IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
			bookmarkOps.cleanBookmarks();

			cleanBookMarkDB();
		} catch (Exception e) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_DEL_FAILED);
		}
	}

	@Override
	protected boolean iOpenLinkByTouch(int x, int y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iTouchLinkToOpen assertWork failed");
			return false;
		}

		Book book = getBook();
		AdobePage curPage = (AdobePage) book.getCurPage();

		if (curPage.getLinkCount() == -1) {
			int mCount = mAdapter.getLinkCount();
			curPage.setLinkCount(mCount);
		}

		if (book.getCurPage().getLinkCount() > 0) {
			String[] urlStrings = new String[1];
			int code = mAdapter.linkTest(x, y, urlStrings);
			Log.d(TAG, "iTouchLinkToOpen code: " + code);
			if (code == 1) {
				Log.d(TAG, "iTouchLinkToOpen External link, url= "
						+ urlStrings[0]);
			} else if (code == 2) {
				Log.d(TAG, "iTouchLinkToOpen Internal link");
				try {
					getPage(curPage);
					setBorder(false);
					updateView();
					updateBookInfo(curPage);

					book.resetPrePage();
					book.resetNextPage();
				} catch (Exception e) {
					Logger.eLog(TAG, "iTouchLinkToOpen failed");
				}
			}
		}

		return true;
	}

	@Override
	protected boolean iResizeScreen(int width, int height) {
		if (!canWork()) {
			Logger.eLog(TAG, "iSetScreenLandMode assertWork failed");
			return false;
		}

//		if (getLandMode() == land) {
//			Logger.dLog(TAG, "iSetScreenLandMode land mode is " + land);
//			return false;
//		}
//
//		setLandMode(land);
		Book book = getBook();
		book.setViewWidth(width);
		book.setViewHeight(height);
		book.initPage();

		Logger.dLog(TAG, "width = " + width + " height = " + height);
		mAdapter.setViewPortSize(width, height);
		mAdapter.setFontSize(getCurFontLevel());

		try {
			AdobePage curPage = (AdobePage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);

			book.resetPrePage();
			book.resetNextPage();
		} catch (Exception e) {
			Logger.eLog(TAG, "iSetScreenLandMode failed");
		}

		return true;
	}

	@Override
	protected boolean iStartHighlightByXY(double x1, double y1) {
		if (!canWork()) {
			Logger.eLog(TAG, "iAddHighlightByXY assertWork failed");
			return false;
		}

		mHighlightStartX = x1;
		mHighlightStartY = y1;
		mHighlightColor = 0;

		return true;
	}

	@Override
	protected boolean iMoveHighlightByXY(double x, double y) {
		return true;
	}

	@Override
	protected boolean iEndHighlightByXY(double x, double y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelHighlightByXY assertWork failed");
			return false;
		}

		mHighlightEndX = x;
		mHighlightEndY = y;

		mAdapter.delAllHighlight();
		String hitStr1 = mAdapter.hitTest(mHighlightStartX, mHighlightStartY);
		String hitStr2 = mAdapter.hitTest(mHighlightEndX, mHighlightEndY);
		boolean res = mAdapter.setHighlightbyloc(hitStr1, hitStr2,
				mHighlightColor);
		Logger.dLog(TAG, "iAddHighlightByXY res = " + res + " hit1 = "
				+ hitStr1 + " hit2 = " + hitStr2);

		// String hlStr = mAdapter.setHighlightbyxy(x1, y1, x2, y2, z);
		// Logger.dLog(TAG, "iSetHighlightByXY hlStr = " + hlStr);

		if (!res) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_ADD_HIGHLIGHT_FAILED);
			return false;
		}

		Book book = getBook();
		BookEmphasis emp = book.makeEmphasis();
		IBookEmphasisCursor beops = book.getBookEmphasisCursor();
		String location = mAdapter.getCurrentLocation();
		String summary = mAdapter.getSummaryByLocation(location);
		emp.setBookId(book.getBookId());
		emp.setXY(mHighlightStartX, mHighlightStartY, mHighlightEndX,
				mHighlightEndY);
		emp.setCourse(hitStr1, hitStr2);
		emp.setLocation(location);
		emp.setColor(mHighlightColor);
		emp.setTime(System.currentTimeMillis());
		emp.setSummary(summary);
		emp.setFontLevel(getCurFontLevel());

		boolean addRes = beops.addEmphasis(emp);

		try {
			AdobePage curPage = (AdobePage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);
			resetHighlight();

			addBookEmphasisDB(emp);
		} catch (Exception e) {
			Logger.eLog(TAG, "iAddHighlightByXY failed");
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_ADD_HIGHLIGHT_FAILED);
		}

		return true;
	}

	@Override
	protected boolean iDelHighlight(BookEmphasis emphasis) {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelHighlightByXY assertWork failed");
			return false;
		}

		Book book = getBook();

		String hitStr1 = emphasis.startCursor;
		String hitStr2 = emphasis.endCursor;
		int color = emphasis.color;
		boolean res = mAdapter.delHighlight(hitStr1, hitStr2, color);
		Logger.dLog(TAG, "iDelHighlightByXY res = " + res + " hit1 = "
				+ hitStr1 + " hit2 = " + hitStr2 + " color = " + color);

		if (!res) {
			// setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED);
			// return false;
		}

		try {
			String location = emphasis.pageLocation;
			if (mAdapter.getCurrentLocation().equals(location)) {
				AdobePage curPage = (AdobePage) book.getCurPage();
				getPage(curPage);
				setBorder(false);
				updateView();
				updateBookInfo(curPage);
			}

			IBookEmphasisCursor beops = book.getBookEmphasisCursor();
			beops.delEmphasis(emphasis);

			delBookEmphasisDB(emphasis);
		} catch (Exception e) {
			Logger.eLog(TAG, "iDelHighlightByXY failed");
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED);
		}

		return true;
	}

	@Override
	protected boolean iGotoHighlight(BookEmphasis emphasis) {
		if (!canWork()) {
			Logger.eLog(TAG, "iGotoHighlight assertWork failed");
			return false;
		}

		Book book = getBook();

		mAdapter.setFontSize(emphasis.fontLevel);
		mAdapter.delAllHighlight();
		boolean res = mAdapter.setHighlightbyloc(emphasis.startCursor,
				emphasis.endCursor, emphasis.color);

		// Logger.dLog(TAG, "iGotoHighlight res = " + res + " start = "
		// + emphasis.startCursor + " end = " + emphasis.endCursor);

		try {
			String location = emphasis.pageLocation;
			AdobePage curPage = (AdobePage) book.getCurPage();
			getPageByLocation(curPage, location);
			setBorder(false);
			updateView();
			
			setCurFontLevel(emphasis.fontLevel);
			updateBookInfo(curPage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected boolean iDelAllHighlight() {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelAllHighlight assertWork failed");
			return false;
		}

		try {
			Book book = getBook();
			IBookEmphasisCursor beops = book.getBookEmphasisCursor();
			beops.clear();

			mAdapter.delAllHighlight();

			AdobePage curPage = (AdobePage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);

			cleanBookEmphasisDB();
		} catch (Exception e) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED);
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.core.ReaderWrapper#iDoFetchText()
	 */
	@Override
	protected boolean iDoFetchText() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void setDefaultVal() {
		setCurFontLevel(DEFAULT_FONT_LEVEL);
	}

}