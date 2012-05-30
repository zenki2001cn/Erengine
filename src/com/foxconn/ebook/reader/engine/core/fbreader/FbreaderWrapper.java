/**
 * @file       FbreaderWrapper.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-4-27 上午09:48:49 
 */

package com.foxconn.ebook.reader.engine.core.fbreader;

import org.geometerplus.zlibrary.text.model.ZLTextMark;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.foxconn.ebook.reader.engine.core.ReaderWrapper;
import com.foxconn.ebook.reader.engine.core.DecAdapter;
import com.foxconn.ebook.reader.engine.core.EngineCode;
import com.foxconn.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.foxconn.ebook.reader.engine.core.fbreader.FbreaderAdapter.PageType;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.foxconn.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.foxconn.ebook.reader.engine.model.BookSearchInfo;
import com.foxconn.ebook.reader.engine.model.FbreaderPage;
import com.foxconn.ebook.reader.engine.model.IBookEmphasisCursor;
import com.foxconn.ebook.reader.engine.model.IBookSearchCursor;
import com.foxconn.ebook.reader.engine.model.IBookMarkCursor;
import com.foxconn.ebook.reader.engine.model.Page;
import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.ChapterTreeState;
import com.foxconn.ebook.reader.engine.util.Logger;

/**
 * 
 */
public class FbreaderWrapper extends ReaderWrapper {
	static private final String TAG = "FbreaderWrapper";

	static private FbreaderWrapper mAction;
	static private FbreaderAdapter mAdapter;

	private final int MAX_FONT_LEVEL = 5;
	private final int MIN_FONT_LEVEL = 0;
	private final int DEFAULT_FONT_LEVEL = 1;

	private final int MAX_FONT_SIZE = 38;
	private final int MIN_FONT_SIZE = 18;
	private final int FONT_SIZE_DIFF = 4;
	private final int FONT_SIZE_2 = MIN_FONT_SIZE + FONT_SIZE_DIFF;
	private final int FONT_SIZE_3 = MIN_FONT_SIZE + FONT_SIZE_DIFF * 2;
	private final int FONT_SIZE_4 = MIN_FONT_SIZE + FONT_SIZE_DIFF * 3;
	private final int FONT_SIZE_5 = MIN_FONT_SIZE + FONT_SIZE_DIFF * 4;

	private final int ROTATE_ANGLE = 90;

	private int mHighlightStartX = -1;
	private int mHighlightStartY = -1;
	private int mHighlightEndX = -1;
	private int mHighlightEndY = -1;
	private int mHighlightColor = -1;

	private FbreaderWrapper() {
		super();

		if (null == mAdapter) {
			mAdapter = FbreaderAdapter.getInstance();
			mAdapter.setBitmapConfig(getBitmapConfig());
		}
	}

	public static FbreaderWrapper getInstance() {
		if (null == mAction) {
			mAction = new FbreaderWrapper();
		}

		return mAction;
	}

	protected void free() {
		super.free();

		if (mAdapter != null) {
			mAdapter.free();
			mAdapter = null;
		}

		if (mAction != null) {
			mAction = null;
		}

		Logger.dLog(TAG, "ReaderWrapper free mFbreaderAdapter: " + mAdapter
				+ " mAction: " + mAction);
	}

	@Override
	protected DecAdapter getCurAdapter() {
		return mAdapter;
	}

	@Override
	protected void iCloseBook() {
		int code = 0;

		if (!canWork()) {
			Logger.eLog(TAG, "AssertWork == null");
			return;
		}

		Book book = getBook();
		if (book.getOpened()) {
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

	@Override
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
			Logger.eLog(TAG, "Close previous file error, code = " + code);

			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				Logger.eLog(TAG, "Thread.sleep error" + e);
			}

			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_CLOSE_PREVIOUS_FAILED);
		} else {
			Logger.dLog(TAG, "Close previous file ok");
		}

		/*
		 * Step 2 get file path name and open file.
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
			Logger.vLog(TAG, "Open book success");
		}

		/*
		 * Step 3.
		 */
		try {
			// FBReader維持自己的數據庫用來保存最後頁碼、字體大小等等信息。
			mAdapter.setViewPortSize(book.getViewWidth(), book.getViewHeight());
			FbreaderPage page = (FbreaderPage) book.getCurPage();
			FbreaderPage cache = (FbreaderPage) book.getCachePage();

			// 消除最後一頁字符重疊的現象
			eliminateCharDuplication();
			// TXT文档处理时需要多加载一次页码，以免不能跳到首页
			if (Book.BookType.TXT == book.getBookType()) {
				getPage(cache);
			}

			if (book.getLastLocation().equals(Book.VALUE_NULL)) {
				mAdapter.gotoHome();
			} else {
				int[] pos = mAdapter.parseLocation(book.getLastLocation());
				if ((pos[0] == -1) || (pos[1] == -1) || (pos[2] == -1)) {
				} else {
					mAdapter.gotoPosition(pos[0], pos[1], pos[2]);
				}
			}

			// 跳轉到最後頁碼，解決字符重疊問題
			// mTotalNumber = mAdapter.getTotalPageIndex();
			// lastPage = book.getLastPageNumber();
			// if (lastPage > mTotalNumber || lastPage <= 0) {
			// Logger.vLog(TAG, "PageNum is not invalid");
			// setLastCode(EngineMsgCode.ENGINE_ERROR_PAGE_NUMBER_INVALID);
			// } else {
			// mAdapter.gotoPage(lastPage);
			// }

			// goto position
			// if (!book.getLastLocation().equals(Book.VALUE_NULL)) {
			// int[] pos = mAdapter.parseLocation(book.getLastLocation());
			// if ((pos[0] == -1) || (pos[1] == -1) || (pos[2] == -1)) {
			// } else {
			// mAdapter.gotoPosition(pos[0], pos[1], pos[2]);
			// }
			// }

			getPage(page);
			updateView();

			Book.MetaData data = book.getMetaData();

			if (data.isEmpty()) {
				String title = mAdapter.getTitle();
				String author = mAdapter.getAuthor();
				String encoding = mAdapter.getEncoding();
				String language = mAdapter.getLanguage();

				data.setValue(Book.MetaData.META_TITLE,
						((title != null) ? title : Book.VALUE_NULL));
				data.setValue(Book.MetaData.META_AUTHOR,
						((author != null) ? author : Book.VALUE_NULL));
				data.setValue(Book.MetaData.META_ENCODING,
						((encoding != null) ? encoding : Book.VALUE_NULL));
				data.setValue(Book.MetaData.META_LANGUAGE,
						((language != null) ? language : Book.VALUE_NULL));
				book.setMetaData(data);
			}

			int totalNumber = mAdapter.getTotalPageNum();
			setCurFontLevel(fontSize2Level(mAdapter.getFontSize()));
			book.setTotalPageNumber(totalNumber);
			book.setLastAccessTime(System.currentTimeMillis());
			book.setOpened(true);
			updateBookInfo(page);
		} catch (Exception e) {
			Logger.eLog(TAG, "Load book error");
			return false;
		}

		return true;
	}

	@Override
	protected void iLoadChapterInformation() {
		String[] chapters;
		int[] indexs;

		if (!canWork()) {
			Logger.eLog(TAG, "iLoadChapterInformation assertWork failed");
			return;
		}

		Book book = getBook();
		if (!book.chapterIsLoad()) {
			if (mAdapter.getChapterNumber() > 0) {
				chapters = mAdapter.getChapterTitles();
				indexs = mAdapter.getChapterIndexs();
				book.chapterInit(indexs, chapters);
			} else {
				book.chapterInit(null, null);
			}

			Logger.dLog(TAG,
					"loadChapterInfo finished == " + book.chapterIsLoad());
		}
	}

	@Override
	protected void iChapterJumpByTitle(String title) {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterJumpByTitle assertWork failed");
			return;
		}

		int code = mAdapter.goToChapterByTitle(title);
		if (hasError(code)) {
			return;
		}

		Book book = getBook();
		try {
			FbreaderPage page = (FbreaderPage) book.getCurPage();
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

	@Override
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

		int code = mAdapter.goToChapterByIndex(index);
		if (hasError(code)) {
			return;
		}

		try {
			FbreaderPage page = (FbreaderPage) book.getCurPage();
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

	@Override
	protected void iChapterUp() {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterUp assertWork failed");
			return;
		}

		Book book = getBook();
		FbreaderPage curPage = (FbreaderPage) book.getCurPage();
		int curIndex = mAdapter.getCurrentParagraphIndex();
		if (book.hasPreviousChapter()) {
			int preIndex = book.getPreviousChapterIndex(curIndex);

			Logger.eLog(TAG, "iChapterUp preIndex = " + preIndex);
			int code = mAdapter.goToChapterByIndex(preIndex);
			if (hasError(code)) {
				return;
			}

			try {
				curPage = (FbreaderPage) book.getCurPage();
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

	@Override
	protected void iChapterDown() {
		if (!canWork()) {
			Logger.eLog(TAG, "iChapterDown assertWork failed");
			return;
		}

		Book book = getBook();
		FbreaderPage curPage = (FbreaderPage) book.getCurPage();
		int curIndex = mAdapter.getCurrentParagraphIndex();
		if (book.hasNextChapter()) {
			int nextIndex = book.getNextChapterIndex(curIndex);

			Logger.eLog(TAG, "iChapterUp nextIndex = " + nextIndex);
			int code = mAdapter.goToChapterByIndex(nextIndex);
			if (hasError(code)) {
				return;
			}

			try {
				curPage = (FbreaderPage) book.getCurPage();
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

	@Override
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

				// Logger.dLog(
				// TAG,
				// "Tree.getCurChapterIndex(): "
				// + book.getCurChapterIndex()
				// + " Tree.getMaxChapterNum():"
				// + book.getMaxChapterNum());
			}
		}
	}

	@Override
	protected boolean iPageUp() {
		if (!canWork()) {
			Logger.eLog(TAG, "iPageDown assertWork failed");
			return false;
		}

		boolean hasPrePage = false;

		Book book = getBook();

		FbreaderPage curPage = (FbreaderPage) book.getCurPage();
		FbreaderPage prePage = (FbreaderPage) book.getPrePage();

		try {
			hasPrePage = mAdapter.pageUp();

			if (!hasPrePage) {
				setBorder(true);
				book.resetNextPage();
				book.resetPrePage();
				return false;
			} else {
				setBorder(false);
			}

			if (prePage.isLoaded()) {
				Logger.vLog(TAG, "PrePage is loaded");
				// 只有跳到当前页才能得到页码和位置信息
				prePage.setPageNum(mAdapter.getCurrentPageNum());
				prePage.setLocation(mAdapter.getLocation());

				// if (mAdapter.isHeadParagraph()) {
				// nextPage.reset();
				// } else {
				// nextPage.copy(curPage);
				// }

				curPage.copy(prePage);
				updateView();
			} else {
				Logger.vLog(TAG, "PrePage is not loaded");
				curPage.reset();
				if (getPage(curPage)) {
					updateView();
				}
			}

			Logger.vLog(TAG, "Has PrePage = " + hasPrePage);

			// update Book information
			updateBookInfo(curPage);

			// preloadNextPage();
		} catch (Exception e) {
			Logger.eLog(TAG, "TurnPageUp error");
			setLastCode(EngineMsgCode.STATE_ERROR_PAGE_UP_FAILED);
			return false;
		}

		return true;
	}

	@Override
	protected boolean iPageDown() {
		if (!canWork()) {
			Logger.eLog(TAG, "iPageDown assertWork failed");
			return false;
		}

		boolean hasNextPage = false;

		Book book = getBook();

		FbreaderPage curPage = (FbreaderPage) book.getCurPage();
		FbreaderPage nextPage = (FbreaderPage) book.getNextPage();

		try {
			hasNextPage = mAdapter.pageDown();

			if (!hasNextPage) {
				setBorder(true);
				book.resetNextPage();
				book.resetPrePage();
				return false;
			} else {
				setBorder(false);
			}

			if (nextPage.isLoaded()) {
				Logger.vLog(TAG, "NextPage is loaded");
				// 只有跳到当前页才能得到页码和位置信息
				nextPage.setPageNum(mAdapter.getCurrentPageNum());
				nextPage.setLocation(mAdapter.getLocation());

				curPage.copy(nextPage);
				updateView();
			} else {
				Logger.vLog(TAG, "NextPage is not loaded");
				curPage.reset();
				if (getPage(curPage)) {
					updateView();
				}
			}

			Logger.vLog(TAG, "Has nextpage = " + hasNextPage);

			// update Book information
			updateBookInfo(curPage);

			// preloadPreviousPage();
		} catch (Exception e) {
			Logger.eLog(TAG, "TurnPageDown error");
			setLastCode(EngineMsgCode.STATE_ERROR_PAGE_DOWN_FAILED);
			return false;
		}

		return true;
	}

	@Override
	protected boolean iPageJumpTo(int pageNum) {
		if (!canWork()) {
			Logger.eLog(TAG, "iPageJumpTo assertWork failed");
			return false;
		}

		Book book = getBook();

		// 该方法不能正确跳转到最后一页
		// int pageNum = num - 1;
		// if (pageNum >= mTotalNumber) {
		// pageNum = mTotalNumber - 1;
		// } else if (pageNum <= 0) {
		// pageNum = 0;
		// }

		// 该方法不能正确跳转到第一页
		if ((pageNum > book.getTotalPageNumber()) || (pageNum <= 0)) {
			Logger.vLog(TAG, "PageNum is not invalid");
			setLastCode(EngineMsgCode.ENGINE_ERROR_PAGE_NUMBER_INVALID);
			return false;
		}

		try {
			if (pageNum == 1) {
				mAdapter.gotoHome();
			} else {
				mAdapter.gotoPage(pageNum);
			}

			// eliminateCharDuplication();

			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			// Empty the cache, otherwise it would cause an error when render
			book.resetNextPage();
			book.resetPrePage();

			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iPageJumpTo error");
			setLastCode(EngineMsgCode.STATE_ERROR_GOTO_PAGE_FAILED);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.foxconn.ebook.reader.engine.core.ReaderWrapper#iPageZoomLevel(int)
	 */
	@Override
	protected boolean iPageZoomLevel(int lv) {
		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomLevel assertWork failed");
			return false;
		}

		int curFontLev = getCurFontLevel();
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
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();

			int loop;
			if (curFontLev < desFontLev) {
				loop = desFontLev - curFontLev;
				for (int i = 0; i < loop; i++) {
					mAdapter.zoomIn();
					mAdapter.getBitmap(PageType.CurrentPage);
				}
			} else if (curFontLev > desFontLev) {
				loop = curFontLev - desFontLev;
				for (int i = 0; i < loop; i++) {
					mAdapter.zoomOut();
					mAdapter.getBitmap(PageType.CurrentPage);
				}
			}

			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			book.resetPrePage();
			book.resetNextPage();

			// update Book information
			book.setTotalPageNumber(mAdapter.getTotalPageNum());
			setCurFontLevel(fontSize2Level(mAdapter.getFontSize()));
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iPageZoomLevel error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_LEVEL_FAILED);
			return false;
		}

		return true;
	}

	@Override
	protected boolean iPageZoomIn() {
		int fontLevel = fontSize2Level(mAdapter.getFontSize());
		if (fontLevel >= MAX_FONT_LEVEL) {
			setLastCode(EngineMsgCode.ENGINE_WARN_MAX_FONT);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomIn assertWork failed");
			return false;
		}

		Book book = getBook();

		try {
			mAdapter.zoomIn();

			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			// Empty the cache, otherwise it would cause an error when render
			book.resetNextPage();
			book.resetPrePage();

			// update book info
			book.setTotalPageNumber(mAdapter.getTotalPageNum());
			setCurFontLevel(fontSize2Level(mAdapter.getFontSize()));
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iPageZoomIn error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_IN_FAILED);
			return false;
		}

		return true;
	}

	@Override
	protected boolean iPageZoomOut() {
		int fontLevel = fontSize2Level(mAdapter.getFontSize());
		Logger.dLog(TAG, "font = " + fontLevel);
		if (fontLevel <= MIN_FONT_LEVEL) {
			setLastCode(EngineMsgCode.ENGINE_WARN_MIN_FONT);
			return false;
		}

		if (!canWork()) {
			Logger.eLog(TAG, "iPageZoomOut assertWork failed");
			return false;
		}

		Book book = getBook();

		try {
			mAdapter.zoomOut();

			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			// Empty the cache, otherwise it would cause an error when render
			book.resetNextPage();
			book.resetPrePage();

			book.setTotalPageNumber(mAdapter.getTotalPageNum());
			setCurFontLevel(fontSize2Level(mAdapter.getFontSize()));
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "ZoomOut error");
			setLastCode(EngineMsgCode.STATE_ERROR_ZOOM_OUT_FAILED);
			return false;
		}

		return true;
	}

	@Override
	protected void preloadPage() {
		preloadPreviousPage();
		preloadNextPage();
	}

	@Override
	protected boolean preloadPreviousPage() {
		if (!canWork()) {
			Logger.eLog(TAG, "preloadPreviousPage assertWork failed");
			return false;
		}

		Book book = getBook();

		// if (!mAdapter.pageUp()) {
		// book.resetNextPage();
		// book.resetPrePage();
		// // mAdapter.pageDown();
		//
		// return false;
		// }
		//
		// FbreaderPage prePage = (FbreaderPage) book.getPrePage();
		//
		// prePage.reset();
		// getPage(prePage);
		//
		// mAdapter.pageDown();

		FbreaderPage prePage = (FbreaderPage) book.getPrePage();
		getPrePage(prePage);

		return true;
	}

	@Override
	protected boolean preloadNextPage() {
		if (!canWork()) {
			Logger.eLog(TAG, "preloadNextPage assertWork failed");
			return false;
		}

		Book book = getBook();

		// if (!mAdapter.isEndParagraph()) {
		// mAdapter.pageDown();
		// FbreaderPage nextPage = (FbreaderPage) book.getNextPage();
		//
		// nextPage.reset();
		// getPage(nextPage);
		// } else {
		// // mAdapter.nextParagraph();
		// // book.resetNextPage();
		// book.resetPrePage();
		// return false;
		// }
		//
		// mAdapter.pageUp();

		FbreaderPage nextPage = (FbreaderPage) book.getNextPage();
		getNextPage(nextPage);

		return true;
	}

	private boolean getPrePage(FbreaderPage page) {
		if (!canWork()) {
			Logger.eLog(TAG, "getPrePage assertWork failed");
			return false;
		}

		Bitmap bmp = null;

		Book book = getBook();
		int width = book.getViewWidth();
		int height = book.getViewHeight();

		try {
			bmp = Bitmap
					.createBitmap(mAdapter.getBitmap(PageType.PreviousPage));

//			if (true == getLandMode()) {
//				Matrix m = new Matrix();
//				m.postRotate(ROTATE_ANGLE);
//				bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, false);
//			}
			if (null == bmp) {
				Logger.dLog(TAG, "getBitmap bmp == " + bmp);
				return false;
			}
		} catch (OutOfMemoryError e) {
			setLastCode(EngineCode.EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY);
			Logger.dLog(TAG, "getBitmap Exception == " + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		page.setPageContent(bmp);
		int index = mAdapter.getCurrentPageNum();
		page.setPageNum(index);
		page.setLocation(mAdapter.getLocation());
		page.setSummary(mAdapter.getSummary());
		page.setLoaded(true);

		return true;
	}

	private int fontSize2Level(int size) {
		int level = 2;

		if (size <= MIN_FONT_SIZE) {
			level = 0;
		} else if ((size > MIN_FONT_SIZE) && (size <= FONT_SIZE_2)) {
			level = 1;
		} else if ((size > FONT_SIZE_2) && (size <= FONT_SIZE_3)) {
			level = 2;
		} else if ((size > FONT_SIZE_3) && (size <= FONT_SIZE_4)) {
			level = 3;
		} else if ((size > FONT_SIZE_4) && (size <= FONT_SIZE_5)) {
			level = 4;
		} else if (size > FONT_SIZE_5) {
			level = 5;
		}

		return level;
	}

	private int level2FontSize(int lev) {
		int size = 26;

		switch (lev) {
		case 0:
			size = 18;
			break;
		case 1:
			size = 22;
			break;
		case 2:
			size = 26;
			break;
		case 3:
			size = 30;
			break;
		case 4:
			size = 34;
			break;

		default:
			break;
		}

		return size;
	}

	private void resetHighlight() {
		mHighlightStartX = -1;
		mHighlightStartY = -1;
		mHighlightEndX = -1;
		mHighlightEndY = -1;
		mHighlightColor = -1;
	}

	private boolean getNextPage(FbreaderPage page) {
		if (!canWork()) {
			Logger.eLog(TAG, "getNextPage assertWork failed");
			return false;
		}

		Bitmap bmp = null;

		Book book = getBook();
		int width = book.getViewWidth();
		int height = book.getViewHeight();

		try {
			bmp = Bitmap.createBitmap(mAdapter.getBitmap(PageType.NextPage));

//			if (true == getLandMode()) {
//				Matrix m = new Matrix();
//				m.postRotate(ROTATE_ANGLE);
//				bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, false);
//			}

			if (null == bmp) {
				Logger.dLog(TAG, "getBitmap bmp == " + bmp);
				return false;
			}
		} catch (OutOfMemoryError e) {
			setLastCode(EngineCode.EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY);
			Logger.dLog(TAG, "getBitmap Exception == " + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		page.setPageContent(bmp);
		int index = mAdapter.getCurrentPageNum();
		page.setPageNum(index);
		page.setLocation(mAdapter.getLocation());
		page.setSummary(mAdapter.getSummary());
		page.setLoaded(true);

		return true;
	}

	private boolean getPage(FbreaderPage page) {
		if (!canWork()) {
			Logger.eLog(TAG, "getPage assertWork failed");
			return false;
		}

		Bitmap bmp = null;

		Book book = getBook();
		int width = book.getViewWidth();
		int height = book.getViewHeight();

		try {
			bmp = Bitmap.createBitmap(mAdapter.getBitmap(PageType.CurrentPage));

//			if (true == getLandMode()) {
//				Matrix m = new Matrix();
//				m.postRotate(ROTATE_ANGLE);
//				bmp = Bitmap.createBitmap(bmp, 0, 0, width, height, m, false);
//			}

			if (null == bmp) {
				Logger.dLog(TAG, "getBitmap bmp == " + bmp);
				return false;
			}
		} catch (OutOfMemoryError e) {
			setLastCode(EngineCode.EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY);
			Logger.dLog(TAG, "getBitmap Exception == " + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		page.setPageContent(bmp);
		int index = mAdapter.getCurrentPageNum();
		page.setPageNum(index);
		page.setLocation(mAdapter.getLocation());
		page.setSummary(mAdapter.getSummary());
		page.setLoaded(true);

		return true;
	}

	private void updateBookInfo(Page page) {
		Book book = getBook();

		if (book != null) {
			book.updateFromPage(page);
			book.setFontLevel(getCurFontLevel());
		}
	}

	// 通過向上翻頁、再向下翻頁、再向下翻頁、再向上翻頁用來消除定位不准，字符重疊的現象
	private void eliminateCharDuplication() {
		FbreaderPage cache = (FbreaderPage) getBook().getCachePage();

		mAdapter.pageUp();
		mAdapter.getBitmap(PageType.CurrentPage);
		mAdapter.pageDown();
		mAdapter.getBitmap(PageType.CurrentPage);

		mAdapter.pageDown();
		getPage(cache);
		mAdapter.pageUp();
		getPage(cache);

		cache.reset();
	}

	@Override
	protected void iEnterSearchMode() {
		if (!canWork()) {
			Logger.eLog(TAG, "iEnterSearchMode assertWork failed");
			return;
		}

		BookSearchInfo.BookSearchResult[] results;
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iEnterSearchMode key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		mAdapter.quitSearch();
		setLastCode(EngineCode.EngineMsgCode.NO_ERROR);

		mAdapter.gotoHome();
		int code = mAdapter.startSearch(key);
		if (0 == code) {
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_RESULT_NULL);
			return;
		}

		bookSearchOps.resetResults();
		results = mAdapter.getSearchResult();
		if (results != null) {
			bookSearchOps.addResults(results);
		}

		// debug code, can be delete
		// for (BookSearchInfo.BookSearchResult searchResult : results) {
		// Logger.dLog(TAG, "iEnterSearchMode text = " + searchResult.text +
		// " start = "
		// + searchResult.start_pos + " end = " + searchResult.end_pos);
		// }

		iSearchGoNext();

		book.resetNextPage();
		book.resetPrePage();
	}

	@Override
	protected void iSearchGoNext() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoNext assertWork failed");
			return;
		}

		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();

		// check keyword
		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iSearchGoNext key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		int code = mAdapter.searchNext();
		if (hasError(code)) {
			Logger.eLog(TAG, "iSearchGoNext hasError");
			setLastCode(code);
			return;
		}

		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage = (FbreaderPage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iSearchGoNext failed");
		}
	}

	@Override
	protected void iSearchGoPrevious() {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoPrevious assertWork failed");
			return;
		}

		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();

		// check keyword
		String key = bookSearchOps.getSearchKeyword();
		if ((null == key) || key.equalsIgnoreCase("")) {
			Logger.vLog(TAG, "iSearchGoPrevious key == null");
			setLastCode(EngineMsgCode.ENGINE_WARN_SEARCH_KEY_NULL);
			return;
		}

		int code = mAdapter.searchPrevious();
		if (hasError(code)) {
			Logger.eLog(TAG, "iSearchGoPrevious hasError");
			setLastCode(code);
			return;
		}

		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage = (FbreaderPage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);
		} catch (Exception e) {
			Logger.eLog(TAG, "iSearchGoNext failed");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.foxconn.ebook.reader.engine.core.ReaderWrapper#iSearchGoto(com.foxconn
	 * .ebook.reader.engine.model.BookSearchInfo.BookSearchResult)
	 */
	@Override
	protected void iSearchGoto(
			com.foxconn.ebook.reader.engine.model.BookSearchInfo.BookSearchResult result) {
		if (!canWork()) {
			Logger.eLog(TAG, "iSearchGoto assertWork failed");
			return;
		}

		Book book = getBook();

		if ((null == result) || (result.start_pos.equals(Book.VALUE_NULL))) {
			setLastCode(EngineMsgCode.STATE_ERROR_SEARCH_GOTO_RESULT_FAILED);
			return;
		}

		ZLTextMark mark = mAdapter.convertResultToMark(result);
		if (null == mark) {
			setLastCode(EngineMsgCode.STATE_ERROR_SEARCH_GOTO_RESULT_FAILED);
			return;
		}

		try {
			mAdapter.gotoMark(mark);
			FbreaderPage page = (FbreaderPage) book.getCurPage();
			getPage(page);
			setBorder(false);
			updateView();
			updateBookInfo(page);
		} catch (Exception e) {
			Logger.eLog(TAG, "iSearchGoto failed");
		}
	}

	@Override
	protected void iSearchUpdateInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void iQuitSearch() {
		Logger.eLog(TAG, "iQuitSearch >>>");
		mAdapter.quitSearch();

		// clear search keyword
		Book book = getBook();
		IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
		bookSearchOps.setSearchKeyword(null);

		// reset error code
		setLastCode(EngineCode.EngineMsgCode.NO_ERROR);
	}

	@Override
	protected void iAddBookmark() {
		if (!canWork()) {
			Logger.eLog(TAG, "iAddBookmark assertWork failed");
			return;
		}

		try {
			Book book = getBook();
			String location = mAdapter.getLocation();

			// 判斷當前頁是否已存在書簽，該邏輯移到ERSqliteProxy中處理
			// IDatabaseService dbService = (IDatabaseService) ERManager
			// .getService(ERManager.DATABASE_SERVICE);
			// boolean hasBookmark = dbService.queryBookmark(book, location);
			// if (hasBookmark) {
			// Logger.dLog(TAG, "iAddBookmark has bookmark");
			// return;
			// }

			FbreaderPage page = (FbreaderPage) book.getCurPage();
			IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
			Bookmark bookmark = book.makeBookmark();
			int pageNum = (int) mAdapter.getCurrentPageNum();
			String summary = mAdapter.getSummary();
			
			Logger.vLog(TAG, "iAddBookmark summary = " + summary);

			page.setSummary(summary);
			bookmark.setLocation(location);
			bookmark.setPageNum(pageNum);
			bookmark.setSummary(summary);
			bookmark.setTime(System.currentTimeMillis());
			boolean addRes = bookmarkOps.addBookmark(bookmark);

			addBookMarkDB(bookmark);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.eLog(TAG, "iAddBookmark e = " + e);
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_ADD_FAILED);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.foxconn.ebook.reader.engine.core.ReaderWrapper#iGotoBookmark(com.
	 * foxconn.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	protected void iGotoBookmark(Bookmark bookmark) {
		if (!canWork()) {
			Logger.eLog(TAG, "iGotoBookmark2 assertWork failed");
			return;
		}

		if ((null == bookmark)
				|| (bookmark.getLocation().equals(Book.VALUE_NULL))) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOKMARK_GOTO_FAILED);
			return;
		}

		Book book = getBook();
		FbreaderPage page = (FbreaderPage) book.getCurPage();

		int[] pos = mAdapter.parseLocation(bookmark.getLocation());
		if ((pos[0] == -1) || (pos[1] == -1) || (pos[2] == -1)) {
		} else {
			mAdapter.gotoPosition(pos[0], pos[1], pos[2]);
		}
		getPage(page);

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
			Logger.eLog(TAG, "iDeleteBookmark assertWork failed");
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
			Logger.eLog(TAG, "iDelAllBookmark assertWork failed");
			return;
		}

		try {
			Book book = getBook();
			IBookMarkCursor bookmarkOps = book.getBookMarkCursor();
			bookmarkOps.cleanBookmarks();

			cleanBookMarkDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean iOpenLinkByTouch(int x, int y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iOpenLinkByTouch assertWork failed");
			return false;
		}

		mAdapter.openLinkByTouch(x, y);

		Book book = getBook();
		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			// Empty the cache, otherwise it would cause an error when render
			book.resetNextPage();
			book.resetPrePage();
			updateBookInfo(curPage);

		} catch (Exception e) {
			Logger.eLog(TAG, "iOpenLinkByTouch error");
			return false;
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

		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
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
	protected boolean iGotoHighlight(BookEmphasis emphasis) {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelHighlightByXY assertWork failed");
			return false;
		}

		Book book = getBook();
		String location = emphasis.pageLocation;
		int x1 = (int) emphasis.startX;
		int y1 = (int) emphasis.startY;
		int x2 = (int) emphasis.endX;
		int y2 = (int) emphasis.endY;

		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();

			int[] pos = mAdapter.parseLocation(location);
			if ((pos[0] == -1) || (pos[1] == -1) || (pos[2] == -1)) {
			} else {
				mAdapter.gotoPosition(pos[0], pos[1], pos[2]);
			}

			int fontLevel = getCurFontLevel();
			if (fontLevel < emphasis.fontLevel) {
				while (fontLevel < emphasis.fontLevel) {
					mAdapter.zoomIn();
					mAdapter.getBitmap(PageType.CurrentPage);
					fontLevel = fontSize2Level(mAdapter.getFontSize());
				}
			} else if (fontLevel > emphasis.fontLevel) {
				while (fontLevel > emphasis.fontLevel) {
					mAdapter.zoomOut();
					mAdapter.getBitmap(PageType.CurrentPage);
					fontLevel = fontSize2Level(mAdapter.getFontSize());
				}
			}

			mAdapter.startHighlightByXY(x1, y1);
			mAdapter.moveHighlightByXY(x2, y2);
			mAdapter.endHighlightByXY(x2, y2);

			getPage(curPage);
			setBorder(false);
			updateView();
			setCurFontLevel(fontSize2Level(mAdapter.getFontSize()));
			updateBookInfo(curPage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected boolean iDelHighlight(BookEmphasis emphasis) {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelHighlightByXY assertWork failed");
			return false;
		}

		String hitStr1 = emphasis.startCursor;
		String hitStr2 = emphasis.endCursor;
		int color = emphasis.color;
		boolean res = mAdapter.delAllHighlight();
		Logger.dLog(TAG, "iDelHighlightByXY res = " + res + " hit1 = "
				+ hitStr1 + " hit2 = " + hitStr2 + " color = " + color);

		if (!res) {
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_DEL_HIGHLIGHT_FAILED);
			return false;
		}

		Book book = getBook();

		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			getPage(curPage);
			setBorder(false);
			updateView();
			updateBookInfo(curPage);

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
	protected boolean iDelAllHighlight() {
		if (!canWork()) {
			Logger.eLog(TAG, "iDelHighlightByXY assertWork failed");
			return false;
		}

		try {
			Book book = getBook();
			IBookEmphasisCursor beops = book.getBookEmphasisCursor();
			beops.clear();

			mAdapter.delAllHighlight();

			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
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

	@Override
	protected boolean iStartHighlightByXY(double x, double y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iStartHighlightByXY assertWork failed");
			return false;
		}

		mHighlightStartX = (int) x;
		mHighlightStartY = (int) y;
		mHighlightColor = 0;

		mAdapter.startHighlightByXY(mHighlightStartX, mHighlightStartY);

		return true;
	}

	@Override
	protected boolean iMoveHighlightByXY(double x, double y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iMoveHighlightByXY assertWork failed");
			return false;
		}

		mAdapter.moveHighlightByXY((int) x, (int) y);

		Book book = getBook();
		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();
		} catch (Exception e) {
			Logger.eLog(TAG, "iMoveHighlightByXY error");
			return false;
		}

		return true;
	}

	@Override
	protected boolean iEndHighlightByXY(double x, double y) {
		if (!canWork()) {
			Logger.eLog(TAG, "iEndHighlightByXY assertWork failed");
			return false;
		}

		mHighlightEndX = (int) x;
		mHighlightEndY = (int) y;

		mAdapter.endHighlightByXY(mHighlightEndX, mHighlightEndY);

		Book book = getBook();
		try {
			FbreaderPage curPage = (FbreaderPage) book.getCurPage();
			curPage.reset();
			getPage(curPage);
			setBorder(false);
			updateView();

			// Empty the cache, otherwise it would cause an error when render
			book.resetNextPage();
			book.resetPrePage();
			updateBookInfo(curPage);

			BookEmphasis emp = book.makeEmphasis();
			IBookEmphasisCursor beops = book.getBookEmphasisCursor();
			String location = mAdapter.getLocation();
			String summary = mAdapter.getSelectedText();

			emp.setXY(mHighlightStartX, mHighlightStartY, mHighlightEndX,
					mHighlightEndY);
			emp.setCourse(mAdapter.getHighlightStartLoc(),
					mAdapter.getHighlightEndLoc());
			emp.setLocation(location);
			emp.setColor(mHighlightColor);
			emp.setTime(System.currentTimeMillis());
			emp.setSummary(summary);
			emp.setFontLevel(getCurFontLevel());
			boolean addRes = beops.addEmphasis(emp);

			addBookEmphasisDB(emp);
			resetHighlight();
		} catch (Exception e) {
			Logger.eLog(TAG, "iEndHighlightByXY error");
			setLastCode(EngineMsgCode.ENGINE_ERROR_BOOK_ADD_HIGHLIGHT_FAILED);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.foxconn.ebook.reader.engine.core.ReaderWrapper#iDoFetchText()
	 */
	@Override
	protected boolean iDoFetchText() {
		if (!canWork()) {
			Logger.eLog(TAG, "iDoFetchText assertWork failed");
			return false;
		}

		Book book = getBook();
		String text = mAdapter.getPageText();
		if ((null == text)) {
			return false;
		}

		book.setPageText(text);

		return true;
	}

	@Override
	protected void setDefaultVal() {
		setCurFontLevel(DEFAULT_FONT_LEVEL);
	}

}
