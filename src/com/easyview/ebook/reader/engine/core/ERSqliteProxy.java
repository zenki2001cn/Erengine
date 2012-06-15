/**
 * @file       ERSqliteProxy.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-18 下午05:17:42 
 */

package com.easyview.ebook.reader.engine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo;
import com.easyview.ebook.reader.engine.model.BookMarkInfo;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.provider.BookColumns;
import com.easyview.ebook.reader.engine.util.provider.BookEmphasisColumns;
import com.easyview.ebook.reader.engine.util.provider.BookmarkColumns;
import com.easyview.ebook.reader.engine.util.provider.ERSqliteProvider;
import com.easyview.ebook.reader.engine.util.provider.ERUris;

/**
 * The Class ERSqliteProxy.
 */
public class ERSqliteProxy implements IDatabaseService {

	/** The Constant TAG. */
	static private final String TAG = "ERSqliteProxy";

	/** The m context. */
	private Context mContext;

	/** 所有書籍信息列的字段標識列表，為String[]類型. */
	static public String ALL_PROJECTION_OF_BOOKINFO[] = new String[] {
			BookColumns.FILE_PATH, BookColumns.FILE_NAME,
			BookColumns.FILE_TYPE, BookColumns.LAST_LOCATION,
			BookColumns.LAST_PAGE_NUM, BookColumns.TOTAL_PAGE_NUM,
			BookColumns.LAST_ACCESS_TIME, BookColumns.META_TITLE,
			BookColumns.META_AUTHOR, BookColumns.META_PUBLISHER,
			BookColumns.META_ENCODING, BookColumns.META_LANGUAGE,
			BookColumns.USER_NAME, BookColumns.PASSWORD,
			BookColumns.VIEW_WIDTH, BookColumns.VIEW_HEIGHT,
			BookColumns.FONT_LEVEL, BookColumns.FILE_SIZE };

	/** 所有書簽信息列的字段標識列表，為String[]類型. */
	static public String ALL_PROJECTION_OF_BOOKMARK[] = new String[] {
			BookmarkColumns.BOOK_ID, BookmarkColumns.LOCATION,
			BookmarkColumns.CREATE_TIME, BookmarkColumns.PAGE_NUM,
			BookmarkColumns.SUMMARY };

	/** 所有高亮信息列的字段標識列表，為String[]類型. */
	static public String ALL_PROJECTION_OF_BOOKEMPHASIS[] = new String[] {
			BookEmphasisColumns.BOOK_ID, BookEmphasisColumns.COLOR,
			BookEmphasisColumns.START_CURSOR, BookEmphasisColumns.END_CURSOR,
			BookEmphasisColumns.START_X, BookEmphasisColumns.START_Y,
			BookEmphasisColumns.END_X, BookEmphasisColumns.END_Y,
			BookEmphasisColumns.LOCATION, BookEmphasisColumns.CREATE_TIME,
			BookEmphasisColumns.SUMMARY, BookEmphasisColumns.FONT_LEVEL };

	/**
	 * Instantiates a new eR sqlite request.
	 * 
	 * @param context
	 *            the context
	 */
	protected ERSqliteProxy(Context context) {
		mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#insertBookInfo(
	 * com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean addBook(Book book) {
		if (!canWork()) {
			return false;
		}

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				new String[] { BookColumns.FILE_PATH },
				BookColumns.FILE_PATH + "=?",
				new String[] { book.getBookPath() }, null);

		if ((c != null) && (c.getCount() > 0)) {
			c.close();
			return false;
		}

		if (c != null) {
			c.close();
		}

		try {
			ContentValues values = new ContentValues();
			values.put(BookColumns.FILE_PATH, book.getBookPath());
			values.put(BookColumns.FILE_NAME, book.getBookName());
			values.put(BookColumns.FILE_TYPE, book.getBookType());
			values.put(BookColumns.LAST_LOCATION, book.getLastLocation());
			values.put(BookColumns.LAST_PAGE_NUM, book.getLastPageNumber());
			values.put(BookColumns.TOTAL_PAGE_NUM, book.getTotalPageNumber());
			values.put(BookColumns.LAST_ACCESS_TIME, book.getLastAccessTime());
			values.put(BookColumns.META_TITLE, book.getTitle());
			values.put(BookColumns.META_AUTHOR, book.getAuthor());
			values.put(BookColumns.META_PUBLISHER, book.getPublisher());
			values.put(BookColumns.META_ENCODING, book.getEncoding());
			values.put(BookColumns.META_LANGUAGE, book.getLanguage());
			values.put(BookColumns.USER_NAME, book.getUserName());
			values.put(BookColumns.PASSWORD, book.getPassword());
			values.put(BookColumns.VIEW_WIDTH, book.getViewWidth());
			values.put(BookColumns.VIEW_HEIGHT, book.getViewHeight());
			values.put(BookColumns.FONT_LEVEL, book.getFontLevel());
			values.put(BookColumns.FILE_SIZE, book.getBookSize());

			Uri uri = mContext.getContentResolver().insert(
					ERUris.BOOK_CONTENT_URI, values);
		} catch (Exception e) {
			Logger.eLog(TAG, "insertBookInfo error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#addBook(java.lang
	 * .String)
	 */
	@Override
	public boolean addBook(String filePath) {
		Book book = new Book(filePath);
		return addBook(book);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#addBook(java.lang
	 * .String[])
	 */
	@Override
	public boolean addBook(String[] filePath) {
		if (!canWork()) {
			return false;
		}

		Book book;
		ContentValues value;
		Cursor c = null;
		int len = filePath.length;
		LinkedList<ContentValues> valuesList = new LinkedList<ContentValues>();

		for (int i = 0; i < len; i++) {
			c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
					new String[] { BookColumns.FILE_PATH },
					BookColumns.FILE_PATH + "=?", new String[] { filePath[i] },
					null);
			if ((c != null) && (c.getCount() > 0)) {
				c.close();
				continue;
			}

			if (c != null) {
				c.close();
			}

			book = new Book(filePath[i]);
			value = new ContentValues();

			value.put(BookColumns.FILE_PATH, book.getBookPath());
			value.put(BookColumns.FILE_NAME, book.getBookName());
			value.put(BookColumns.FILE_TYPE, book.getBookType());
			value.put(BookColumns.LAST_LOCATION, book.getLastLocation());
			value.put(BookColumns.LAST_PAGE_NUM, book.getLastPageNumber());
			value.put(BookColumns.TOTAL_PAGE_NUM, book.getTotalPageNumber());
			value.put(BookColumns.LAST_ACCESS_TIME, book.getLastAccessTime());
			value.put(BookColumns.META_TITLE, book.getTitle());
			value.put(BookColumns.META_AUTHOR, book.getAuthor());
			value.put(BookColumns.META_PUBLISHER, book.getPublisher());
			value.put(BookColumns.META_ENCODING, book.getEncoding());
			value.put(BookColumns.META_LANGUAGE, book.getLanguage());
			value.put(BookColumns.USER_NAME, book.getUserName());
			value.put(BookColumns.PASSWORD, book.getPassword());
			value.put(BookColumns.VIEW_WIDTH, book.getViewWidth());
			value.put(BookColumns.VIEW_HEIGHT, book.getViewHeight());
			value.put(BookColumns.FONT_LEVEL, book.getFontLevel());
			value.put(BookColumns.FILE_SIZE, book.getBookSize());

			valuesList.add(value);
		}

		try {
			if (valuesList.isEmpty()) {
				Logger.eLog(TAG, "addBooks is empty");
				return false;
			}

			ContentValues[] values = valuesList.toArray(new ContentValues[] {});
			int num = mContext.getContentResolver().bulkInsert(
					ERUris.BOOK_CONTENT_URI, values);

			if (num <= 0) {
				return false;
			}
		} catch (Exception e) {
			Logger.eLog(TAG, "bulkInsert error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBookInfo(
	 * com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean deleteBook(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOK_CONTENT_URI, BookColumns._ID + "=?",
					new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBookInfo error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteAllBook()
	 */
	@Override
	public boolean deleteAllBooks() {
		if (!canWork()) {
			return false;
		}

		try {
			String sqlDelBook = "DELETE FROM "
					+ IDatabaseService.BOOKINFO_TABLE_NAME;
			String sqlDelBookmark = "DELETE FROM "
					+ IDatabaseService.BOOKMARK_TABLE_NAME;
			String sqlDelBookEmphasis = "DELETE FROM "
					+ IDatabaseService.BOOKEMPHASIS_TABLE_NAME;
			String sqlResetBookSeq = "DELETE FROM sqlite_sequence WHERE name='"
					+ IDatabaseService.BOOKINFO_TABLE_NAME + "'";
			String sqlResetBookmarkSeq = "DELETE FROM sqlite_sequence WHERE name='"
					+ IDatabaseService.BOOKMARK_TABLE_NAME + "'";
			String sqlResetBookEmphasisSeq = "DELETE FROM sqlite_sequence WHERE name='"
					+ IDatabaseService.BOOKEMPHASIS_TABLE_NAME + "'";

			executeSQL(sqlDelBook);
			executeSQL(sqlDelBookmark);
			executeSQL(sqlDelBookEmphasis);
			executeSQL(sqlResetBookSeq);
			executeSQL(sqlResetBookmarkSeq);
			executeSQL(sqlResetBookEmphasisSeq);
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteAllBook error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#updateBookInfo(
	 * com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean updateBook(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentValues values = new ContentValues();
			values.put(BookColumns.FILE_PATH, book.getBookPath());
			values.put(BookColumns.FILE_NAME, book.getBookName());
			values.put(BookColumns.FILE_TYPE, book.getBookType());
			values.put(BookColumns.LAST_LOCATION, book.getLastLocation());
			values.put(BookColumns.LAST_PAGE_NUM, book.getLastPageNumber());
			values.put(BookColumns.TOTAL_PAGE_NUM, book.getTotalPageNumber());
			values.put(BookColumns.LAST_ACCESS_TIME, book.getLastAccessTime());
			values.put(BookColumns.META_TITLE, book.getTitle());
			values.put(BookColumns.META_AUTHOR, book.getAuthor());
			values.put(BookColumns.META_PUBLISHER, book.getPublisher());
			values.put(BookColumns.META_ENCODING, book.getEncoding());
			values.put(BookColumns.META_LANGUAGE, book.getLanguage());
			values.put(BookColumns.USER_NAME, book.getUserName());
			values.put(BookColumns.PASSWORD, book.getPassword());
			values.put(BookColumns.VIEW_WIDTH, book.getViewWidth());
			values.put(BookColumns.VIEW_HEIGHT, book.getViewHeight());
			values.put(BookColumns.FONT_LEVEL, book.getFontLevel());
			values.put(BookColumns.FILE_SIZE, book.getBookSize());

			int rows = mContext.getContentResolver().update(
					ERUris.BOOK_CONTENT_URI, values, BookColumns._ID + "=?",
					new String[] { String.valueOf(id) });

			if (rows <= 0) {
				return false;
			}
		} catch (Exception e) {
			Logger.eLog(TAG, "updateBookInfo error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBookInfo(com
	 * .easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean queryBook(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKINFO, BookColumns._ID + "=" + id, null,
				BookColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryBookInfo cursor == null");
			return false;
		}

		if (c.getCount() <= 0) {
			c.close();
			return false;
		}

		if (c.moveToFirst()) {
			String filePath = c.getString(0);
			String fileName = c.getString(1);
			String fileType = c.getString(2);
			String lastLocation = c.getString(3);
			int lastPageNum = c.getInt(4);
			int totalPageNum = c.getInt(5);
			int lastAccessTime = c.getInt(6);
			String title = c.getString(7);
			String author = c.getString(8);
			String publisher = c.getString(9);
			String encoding = c.getString(10);
			String language = c.getString(11);
			String username = c.getString(12);
			String password = c.getString(13);
			int viewWidth = c.getInt(14);
			int viewHeight = c.getInt(15);
			int fontLevel = c.getInt(16);
			long fileSize = c.getInt(17);

			// debug info, can be delete
			// Log.d(TAG, "path: " + filePath + "  fileName:" + fileName
			// + "  type:" + fileType + "  location:" + lastLocation
			// + "  lastPage:" + lastPageNum + "  totalpage:"
			// + totalPageNum + "  lastTime:" + lastAccessTime + " title:"
			// + title + " author: " + author + " publisher: " + publisher
			// + " encoding: " + encoding + " language: " + language
			// + " username: " + username + " password: " + password
			// + " viewWidth: " + viewWidth + " viewHeight: " + viewHeight);

			book.setBookPath(filePath);
			book.setBookName(fileName);
			book.setBookType(fileName);
			book.setFontLevel(fontLevel);
			book.setUserName(username);
			book.setPassword(password);
			// book.setViewWidth(viewWidth);
			// book.setViewHeight(viewHeight);
			book.setLastLocation(lastLocation);
			book.setLastPageNumber(lastPageNum);
			book.setTotalPageNumber(totalPageNum);
			book.setLastAccessTime(lastAccessTime);
			book.setBookSize(fileSize);

			Book.MetaData data = book.getMetaData();
			data.setValue(Book.MetaData.META_AUTHOR, author);
			data.setValue(Book.MetaData.META_TITLE, title);
			data.setValue(Book.MetaData.META_PUBLISHER, publisher);
			data.setValue(Book.MetaData.META_ENCODING, encoding);
			data.setValue(Book.MetaData.META_LANGUAGE, language);

			if (c != null) {
				c.close();
			}
		} else {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBook(java.
	 * lang.String)
	 */
	@Override
	public int queryBook(String filePath) {
		if (!canWork()) {
			return -1;
		}

		int bookId = -1;

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				new String[] { BookColumns._ID },
				BookColumns.FILE_PATH + "='" + filePath + "'", null, null);

		if (null == c) {
			Logger.eLog(TAG, "queryBookInfo cursor == null");
			return -1;
		}

		if (c.getCount() <= 0) {
			c.close();
			return -1;
		}

		c.moveToFirst();
		bookId = c.getInt(0);

		if (c != null) {
			c.close();
		}

		return bookId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IDatabaseService#queryBooksId()
	 */
	@Override
	public int[] queryBooksId() {
		if (!canWork()) {
			return null;
		}

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				new String[] { BookColumns._ID }, null, null,
				BookColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryBook cursor == null");
			return null;
		}

		int count = c.getCount();
		if (count <= 0) {
			c.close();
			return null;
		}

		int id;
		int[] bookIds = new int[count];
		int i = 0;
		
		c.moveToFirst();
		while (!c.isAfterLast()) {
			id = c.getInt(0);
			bookIds[i] = id;

			c.moveToNext();
			i++;
		}

		if (c != null) {
			c.close();
		}

		return bookIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IDatabaseService#queryBook()
	 */
	@Override
	public String[] queryBooksPath() {
		LinkedList<String> bookList = new LinkedList<String>();

		if (!canWork()) {
			return null;
		}

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				new String[] { BookColumns.FILE_PATH }, null, null,
				BookColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryBook cursor == null");
			return null;
		}

		if (c.getCount() <= 0) {
			c.close();
			return null;
		}

		String filePath;

		c.moveToFirst();
		while (!c.isAfterLast()) {
			filePath = c.getString(0);
			bookList.add(filePath);

			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return bookList.toArray(new String[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#insertBookmark(
	 * com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean addBookmark(Bookmark bookmark) {
		if (!canWork()) {
			return false;
		}

		int id = bookmark.getBookId();
		String location = bookmark.getLocation();
		long createTime = bookmark.getTime();
		int pageNum = bookmark.getPageNum();
		String summary = bookmark.getSummary();

		Cursor c = mContext.getContentResolver().query(
				ERUris.BOOKMARK_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKMARK,
				BookmarkColumns.BOOK_ID + "=? and " + BookmarkColumns.LOCATION
						+ "=?", new String[] { String.valueOf(id), location },
				null);

		if (c.getCount() > 0) {
			c.close();
			return false;
		}

		if (c != null) {
			c.close();
		}

		try {
			ContentValues values = new ContentValues();
			values.put(BookmarkColumns.BOOK_ID, id);
			values.put(BookmarkColumns.LOCATION, location);
			values.put(BookmarkColumns.CREATE_TIME, createTime);
			values.put(BookmarkColumns.PAGE_NUM, pageNum);
			values.put(BookmarkColumns.SUMMARY, summary);

			Uri uri = mContext.getContentResolver().insert(
					ERUris.BOOKMARK_CONTENT_URI, values);
		} catch (Exception e) {
			Logger.eLog(TAG, "insertBookInfo error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBookmark(
	 * com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean deleteBookmark(Bookmark bookmark) {
		if (!canWork()) {
			return false;
		}

		int id = bookmark.getBookId();
		String location = bookmark.getLocation();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKMARK_CONTENT_URI,
					BookmarkColumns.BOOK_ID + "=? and "
							+ BookmarkColumns.LOCATION + "=?", new String[] {
							String.valueOf(id), location });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBookmark error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBookmark(
	 * java.lang.String)
	 */
	@Override
	public boolean deleteBookmark(Book book, String location) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKMARK_CONTENT_URI,
					BookmarkColumns.BOOK_ID + "=? and "
							+ BookmarkColumns.LOCATION + "=?", new String[] {
							String.valueOf(id), location });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBookmarkByLocation error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBookmark(com
	 * .easyview.ebook.reader.engine.model.Book, java.lang.String)
	 */
	@Override
	public boolean queryBookmark(Book book, String location) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(
				ERUris.BOOKMARK_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKMARK,
				BookmarkColumns.BOOK_ID + "=? and " + BookmarkColumns.LOCATION
						+ "=?", new String[] { String.valueOf(id), location },
				null);

		if (null == c) {
			Logger.eLog(TAG, "queryAllBookmarks cursor == null");
			return false;
		}

		if ((c.getCount() <= 0) || !c.moveToFirst()) {
			c.close();
			return false;
		}

		while (!c.isAfterLast()) {
			int bookId = c.getInt(0);
			String loc = c.getString(1);
			int createTime = c.getInt(2);
			int pageNum = c.getInt(3);
			String summary = c.getString(4);

			// debug info, can be delete
			// Log.d(TAG, "c.getcount = " + c.getCount() + " bookid: " + bookId
			// + "  location:" + location
			// + "  createTime:" + createTime + "  pageNum:" + pageNum
			// + "  summary:" + summary);

			BookMarkInfo.Bookmark bookmark = book.makeBookmark();
			bookmark.setBookId(bookId);
			bookmark.setLocation(loc);
			bookmark.setTime(createTime);
			bookmark.setPageNum(pageNum);
			bookmark.setSummary(summary);
			book.getBookMarkCursor().addBookmark(bookmark);

			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#updateBookmark(
	 * com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean updateBookmark(Bookmark bookmark) {
		if (!canWork()) {
			return false;
		}

		int id = bookmark.getBookId();
		String location = bookmark.getLocation();

		try {
			ContentValues values = new ContentValues();
			values.put(BookmarkColumns.BOOK_ID, bookmark.getBookId());
			values.put(BookmarkColumns.LOCATION, bookmark.getLocation());
			values.put(BookmarkColumns.CREATE_TIME, bookmark.getTime());
			values.put(BookmarkColumns.PAGE_NUM, bookmark.getPageNum());
			values.put(BookmarkColumns.SUMMARY, bookmark.getSummary());

			int rows = mContext.getContentResolver().update(
					ERUris.BOOKMARK_CONTENT_URI,
					values,
					BookmarkColumns.BOOK_ID + "=? and "
							+ BookmarkColumns.LOCATION + "=?",
					new String[] { String.valueOf(id), location });

			if (rows <= 0) {
				return false;
			}
		} catch (Exception e) {
			Logger.eLog(TAG, "updateBookmark error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteAllBookmark
	 * (com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean deleteAllBookmark(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKMARK_CONTENT_URI,
					BookmarkColumns.BOOK_ID + "=?",
					new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteAllBookmark error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBookmark(com
	 * .easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean queryAllBookmarks(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(
				ERUris.BOOKMARK_CONTENT_URI, ALL_PROJECTION_OF_BOOKMARK,
				BookmarkColumns.BOOK_ID + "=" + id, null,
				BookmarkColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryAllBookmarks cursor == null");
			return false;
		}

		if ((c.getCount() <= 0) || !c.moveToFirst()) {
			c.close();
			return false;
		}

		while (!c.isAfterLast()) {
			int bookId = c.getInt(0);
			String location = c.getString(1);
			int createTime = c.getInt(2);
			int pageNum = c.getInt(3);
			String summary = c.getString(4);

			// debug info, can be delete
			// Log.d(TAG, "c.getcount = " + c.getCount() + " bookid: " + bookId
			// + "  location:" + location + "  createTime:" + createTime
			// + "  pageNum:" + pageNum + "  summary:" + summary);

			BookMarkInfo.Bookmark bookmark = book.makeBookmark();
			bookmark.setBookId(bookId);
			bookmark.setLocation(location);
			bookmark.setTime(createTime);
			bookmark.setPageNum(pageNum);
			bookmark.setSummary(summary);
			book.getBookMarkCursor().addBookmark(bookmark);

			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#insertBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean addBookEmphasis(BookEmphasis emphasis) {
		if (!canWork()) {
			return false;
		}

		int id = emphasis.getBookId();
		String start = emphasis.getCourse()[0];
		String end = emphasis.getCourse()[1];

		try {
			// 如果有重復的高亮，則不加入數據庫
			ContentResolver resolver = mContext.getContentResolver();
			Cursor c = resolver.query(ERUris.BOOKEMPHASIS_CONTENT_URI,
					ALL_PROJECTION_OF_BOOKEMPHASIS,
					BookEmphasisColumns.BOOK_ID + "=? and "
							+ BookEmphasisColumns.START_CURSOR + "=? and "
							+ BookEmphasisColumns.END_CURSOR + "=?",
					new String[] { String.valueOf(id), start, end }, null);

			if (c.getCount() > 0) {
				c.close();
				return false;
			}

			if (c != null) {
				c.close();
			}

			ContentValues values = new ContentValues();
			values.put(BookEmphasisColumns.BOOK_ID, id);
			values.put(BookEmphasisColumns.COLOR, emphasis.getColor());
			values.put(BookEmphasisColumns.START_CURSOR, start);
			values.put(BookEmphasisColumns.END_CURSOR, end);
			values.put(BookEmphasisColumns.START_X, (int) emphasis.getX1());
			values.put(BookEmphasisColumns.START_Y, (int) emphasis.getY1());
			values.put(BookEmphasisColumns.END_X, (int) emphasis.getX2());
			values.put(BookEmphasisColumns.END_Y, (int) emphasis.getY2());
			values.put(BookEmphasisColumns.LOCATION, emphasis.getLocation());
			values.put(BookEmphasisColumns.CREATE_TIME, emphasis.getTime());
			values.put(BookEmphasisColumns.SUMMARY, emphasis.getSummary());
			values.put(BookEmphasisColumns.FONT_LEVEL, emphasis.getFontLevel());

			Uri uri = mContext.getContentResolver().insert(
					ERUris.BOOKEMPHASIS_CONTENT_URI, values);
		} catch (Exception e) {
			Logger.eLog(TAG, "insertBookEmphasis error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean deleteBookEmphasis(BookEmphasis emphasis) {
		if (!canWork()) {
			return false;
		}

		int id = emphasis.getBookId();
		String start = emphasis.getCourse()[0];
		String end = emphasis.getCourse()[1];

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKEMPHASIS_CONTENT_URI,
					BookEmphasisColumns.BOOK_ID + "=? and "
							+ BookEmphasisColumns.START_CURSOR + "=? and "
							+ BookEmphasisColumns.END_CURSOR + "=?",
					new String[] { String.valueOf(id), start, end });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBookEmphasis error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.Book, java.lang.String)
	 */
	@Override
	public boolean deleteBookEmphasis(Book book, String location) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKEMPHASIS_CONTENT_URI,
					BookEmphasisColumns.BOOK_ID + "=? and "
							+ BookEmphasisColumns.LOCATION + "=?",
					new String[] { String.valueOf(id), location });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBookEmphasisByLocation error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.Book, java.lang.String)
	 */
	@Override
	public boolean queryBookEmphasis(Book book, String location) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(
				ERUris.BOOKEMPHASIS_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKEMPHASIS,
				BookEmphasisColumns.BOOK_ID + "=? and "
						+ BookEmphasisColumns.LOCATION + "=?",
				new String[] { String.valueOf(id), location }, null);

		if (null == c) {
			Logger.eLog(TAG, "queryAllBookEmphasisByLocation cursor == null");
			return false;
		}

		if ((c.getCount() <= 0) || !c.moveToFirst()) {
			c.close();
			return false;
		}

		while (!c.isAfterLast()) {
			int bookId = c.getInt(0);
			int color = c.getInt(1);
			String startCursor = c.getString(2);
			String endCursor = c.getString(3);
			double startX = c.getInt(4);
			double startY = c.getInt(5);
			double endX = c.getInt(6);
			double endY = c.getInt(7);
			String loc = c.getString(8);
			int createTime = c.getInt(9);
			String summary = c.getString(10);
			int fontLevel = c.getInt(11);

			// debug info, can be delete
			// Log.d(TAG, "bookid: " + bookId + "  color:" + color
			// + "  startCursor:" + startCursor + "  endCursor:"
			// + endCursor + "  startX:" + startX + "  startY:" + startY
			// + "  endX:" + endX + "  endY:" + endY + "  location:"
			// + location + "  createTime:" + createTime + "  summary:"
			// + summary + "  fontLevel:" + fontLevel);

			BookEmphasisInfo.BookEmphasis emphasis = book.makeEmphasis();
			emphasis.setBookId(bookId);
			emphasis.setCourse(startCursor, endCursor);
			emphasis.setXY(startX, startY, endX, endY);
			emphasis.setLocation(loc);
			emphasis.setTime(createTime);
			emphasis.setSummary(summary);
			emphasis.setFontLevel(fontLevel);
			emphasis.setColor(color);
			book.getBookEmphasisCursor().addEmphasis(emphasis);

			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#updateBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean updateBookEmphasis(BookEmphasis emphasis) {
		if (!canWork()) {
			return false;
		}

		int id = emphasis.getBookId();
		String start = emphasis.getCourse()[0];
		String end = emphasis.getCourse()[1];

		try {
			ContentValues values = new ContentValues();
			values.put(BookEmphasisColumns.BOOK_ID, emphasis.getBookId());
			values.put(BookEmphasisColumns.COLOR, emphasis.getColor());
			values.put(BookEmphasisColumns.START_CURSOR,
					emphasis.getCourse()[0]);
			values.put(BookEmphasisColumns.END_CURSOR, emphasis.getCourse()[1]);
			values.put(BookEmphasisColumns.START_X, emphasis.getX1());
			values.put(BookEmphasisColumns.START_Y, emphasis.getY1());
			values.put(BookEmphasisColumns.END_X, emphasis.getX2());
			values.put(BookEmphasisColumns.END_Y, emphasis.getY2());
			values.put(BookEmphasisColumns.LOCATION, emphasis.getLocation());
			values.put(BookEmphasisColumns.CREATE_TIME, emphasis.getTime());
			values.put(BookEmphasisColumns.SUMMARY, emphasis.getSummary());
			values.put(BookEmphasisColumns.FONT_LEVEL, emphasis.getFontLevel());

			int rows = mContext.getContentResolver().update(
					ERUris.BOOKEMPHASIS_CONTENT_URI,
					values,
					BookEmphasisColumns.BOOK_ID + "=? and "
							+ BookEmphasisColumns.START_CURSOR + "=? and "
							+ BookEmphasisColumns.END_CURSOR + "=?",
					new String[] { String.valueOf(id), start, end });

			if (rows <= 0) {
				return false;
			}
		} catch (Exception e) {
			Logger.eLog(TAG, "insertBookEmphasis error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteAllBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean deleteAllBookEmphasis(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOKEMPHASIS_CONTENT_URI,
					BookEmphasisColumns.BOOK_ID + "=?",
					new String[] { String.valueOf(id) });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteAllBookEmphasis error = " + e);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#queryBookEmphasis
	 * (com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean queryAllBookEmphasis(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(
				ERUris.BOOKEMPHASIS_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKEMPHASIS,
				BookEmphasisColumns.BOOK_ID + "=" + id, null,
				BookEmphasisColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryAllBookEmphasis cursor == null");
			return false;
		}

		if ((c.getCount() <= 0) || !c.moveToFirst()) {
			c.close();
			return false;
		}

		while (!c.isAfterLast()) {
			int bookId = c.getInt(0);
			int color = c.getInt(1);
			String startCursor = c.getString(2);
			String endCursor = c.getString(3);
			double startX = c.getInt(4);
			double startY = c.getInt(5);
			double endX = c.getInt(6);
			double endY = c.getInt(7);
			String location = c.getString(8);
			int createTime = c.getInt(9);
			String summary = c.getString(10);
			int fontLevel = c.getInt(11);

			// debug info, can be delete
			// Log.d(TAG, "bookid: " + bookId + "  color:" + color
			// + "  startCursor:" + startCursor + "  endCursor:"
			// + endCursor + "  startX:" + startX + "  startY:" + startY
			// + "  endX:" + endX + "  endY:" + endY + "  location:"
			// + location + "  createTime:" + createTime + "  summary:"
			// + summary + "  fontLevel:" + fontLevel);

			BookEmphasisInfo.BookEmphasis emphasis = book.makeEmphasis();
			emphasis.setBookId(bookId);
			emphasis.setCourse(startCursor, endCursor);
			emphasis.setXY(startX, startY, endX, endY);
			emphasis.setLocation(location);
			emphasis.setTime(createTime);
			emphasis.setSummary(summary);
			emphasis.setFontLevel(fontLevel);
			emphasis.setColor(color);
			book.getBookEmphasisCursor().addEmphasis(emphasis);

			c.moveToNext();
		}

		if (c != null) {
			c.close();
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#executeSQL(java
	 * .lang.String)
	 */
	@Override
	public void executeSQL(String sql) {
		if (!canWork()) {
			return;
		}

		ContentResolver resolver = mContext.getContentResolver();
		ERSqliteProvider provider = null;

		if (resolver != null) {
			ContentProviderClient client = resolver
					.acquireContentProviderClient(ERUris.AUTHORITY);
			if (client != null) {
				provider = (ERSqliteProvider) client.getLocalContentProvider();
			} else {
				Logger.eLog(TAG, "executeSQL provider == null");
			}
		} else {
			Logger.eLog(TAG, "executeSQL resolver == null");
		}

		if (provider != null) {
			provider.executeSQL(sql);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#getBookInfoTableName
	 * ()
	 */
	@Override
	public String getBookTableName() {
		return BOOKINFO_TABLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#getBookMarkTableName
	 * ()
	 */
	@Override
	public String getBookMarkTableName() {
		return BOOKMARK_TABLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.easyview.ebook.reader.engine.core.IDatabaseService#
	 * getBookEmphasisTableName()
	 */
	@Override
	public String getBookEmphasisTableName() {
		return BOOKEMPHASIS_TABLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#getDatabaseName()
	 */
	@Override
	public String getDatabaseName() {
		return DATABASE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#checkBookInDb(com
	 * .easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean checkBookInDb(Book book) {
		if (!canWork()) {
			return false;
		}

		int id = book.getBookId();

		Cursor c = mContext.getContentResolver().query(ERUris.BOOK_CONTENT_URI,
				ALL_PROJECTION_OF_BOOKINFO, BookColumns._ID + " = " + id, null,
				BookColumns.DEFAULT_SORT);

		if (null == c) {
			Logger.eLog(TAG, "queryBookInfo cursor == null");
			return false;
		}

		if ((c.getCount() > 0)) {
			c.close();
			return true;
		}

		if (c != null) {
			c.close();
		}

		return false;
	}

	/**
	 * Can work.
	 * 
	 * @return true, if successful
	 */
	private boolean canWork() {
		if (mContext != null) {
			return true;
		}

		Logger.eLog(TAG, "!canWork mContext == null");

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.core.IDatabaseService#deleteBook(java
	 * .lang.String)
	 */
	@Override
	public boolean deleteBook(String filePath) {
		if (!canWork()) {
			return false;
		}

		try {
			ContentResolver resolver = mContext.getContentResolver();
			resolver.delete(ERUris.BOOK_CONTENT_URI, BookColumns.FILE_PATH
					+ "=?", new String[] { filePath });
		} catch (Exception e) {
			Logger.eLog(TAG, "deleteBook error = " + e);
			return false;
		}

		return true;
	}

}
