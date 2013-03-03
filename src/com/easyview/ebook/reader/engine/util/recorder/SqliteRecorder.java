/**
 * @file       SqliteRecorder.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-19 下午04:38:26 
 */

package com.easyview.ebook.reader.engine.util.recorder;

import java.util.List;

import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo;
import com.easyview.ebook.reader.engine.model.BookMarkInfo;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.easyview.ebook.reader.engine.util.Logger;

public class SqliteRecorder implements IRecorder {
	static private final String TAG = "SqliteRecorder";

	private IDatabaseService mDbService = null;

	public SqliteRecorder() {
		mDbService = (IDatabaseService) ERManager
				.getService(ERManager.DATABASE_SERVICE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#saveBookDb(com
	 * .easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean saveBookDb(Book book) throws Exception {
		if (null == mDbService) {
			return false;
		}

		List<BookMarkInfo.Bookmark> bmList;
		List<BookEmphasisInfo.BookEmphasis> emList;
		bmList = book.getBookMarkCursor().getBookmarkList();
		emList = book.getBookEmphasisCursor().getEmphasisList();

		boolean res = false;
		boolean hasOld = mDbService.checkBookInDb(book);
		int bmSize = bmList.size();
		int emSize = emList.size();

		if (hasOld) {
			res = mDbService.updateBook(book);

			for (int i = 0; i < bmSize; i++) {
				mDbService.addBookmark(bmList.get(i));
			}

			for (int i = 0; i < emSize; i++) {
				mDbService.addBookEmphasis(emList.get(i));
			}
		} else {
			res = mDbService.addBook(book);

			for (int i = 0; i < bmSize; i++) {
				mDbService.addBookmark(bmList.get(i));
			}

			for (int i = 0; i < emSize; i++) {
				mDbService.addBookEmphasis(emList.get(i));
			}
		}

		Logger.dLog(TAG, "saveBookDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#loadBookDb(com
	 * .easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean loadBookDb(Book book) throws Exception {
		if (null == mDbService) {
			Logger.dLog(TAG, "DBService == null");
			return false;
		}

		boolean res = false;

		res = mDbService.queryBook(book);
		mDbService.queryAllBookmarks(book);
		mDbService.queryAllBookEmphasis(book);

		Logger.dLog(TAG, "loadBookDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#addBookMarkDb
	 * (com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean addBookMarkDb(Bookmark bookmark) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		// 判斷當前頁是否已存在書簽，該邏輯移到ERSqliteProxy中處理
//		IEngineService ies = (IEngineService) ERManager.getService(ERManager.ERENGINE_SERVICE);
//		Book book = ies.getBook();
//		boolean hasBookmark = mDbService.queryBookmark(book, bookmark.getLocation());
//		
//		if (hasBookmark) {
//			Logger.dLog(TAG, "addBookMarkDb has bookmark");
//			return res;
//		}

		res = mDbService.addBookmark(bookmark);

		Logger.dLog(TAG, "addBookMarkDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#delBookMarkDb
	 * (com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean delBookMarkDb(Bookmark bookmark) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		res = mDbService.deleteBookmark(bookmark);

		Logger.dLog(TAG, "delBookMarkDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#cleanBookMarkDb()
	 */
	@Override
	public boolean cleanBookMarkDb(Book book) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		res = mDbService.deleteAllBookmark(book);

		Logger.dLog(TAG, "cleanBookMarkDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#addBookEmphasisDb
	 * (com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean addBookEmphasisDb(BookEmphasis emphasis) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		res = mDbService.addBookEmphasis(emphasis);

		Logger.dLog(TAG, "addBookEmphasisDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#delBookEmphasisDb
	 * (com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean delBookEmphasisDb(BookEmphasis emphasis) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		res = mDbService.deleteBookEmphasis(emphasis);

		Logger.dLog(TAG, "delBookEmphasisDb res = " + res);

		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.util.recorder.IRecorder#cleanBookEmphasisDb
	 * ()
	 */
	@Override
	public boolean cleanBookEmphasisDb(Book book) throws Exception {
		if (null == mDbService) {
			return false;
		}

		boolean res = false;

		res = mDbService.deleteAllBookEmphasis(book);

		Logger.dLog(TAG, "cleanBookEmphasisDb res = " + res);

		return res;
	}
}
