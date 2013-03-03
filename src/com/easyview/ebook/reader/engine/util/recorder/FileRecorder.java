/**
 * @file       FileRecorder.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-2-18 上午09:19:24 
 */

package com.easyview.ebook.reader.engine.util.recorder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 * FileRecorder is save the opened book information that include path name,
 * title, author, font size, last page, last location .etc. It save the
 * informations in the local directory of this package. Each book is to generate
 */
public class FileRecorder implements IRecorder {
	
	/** The Constant TAG. */
	static private final String TAG = "FileRecorder";

	/** The m save writer. */
	private BufferedWriter mSaveWriter;
	
	/** The m load reader. */
	private BufferedReader mLoadReader;
	
	/** The m record dir. */
	private File mRecordDir;
	
	/** The m record file path. */
	private String mRecordFilePath;

	/** The FIL e_ encoding. */
	private final String FILE_ENCODING = "UTF-8";
	
	/** The Constant FILE_RECORD_DIR. */
	static public final String FILE_RECORD_DIR = "record";
	
	/** The Constant FILE_RECORD_EXT. */
	static public final String FILE_RECORD_EXT = ".info";

	/** The TA g_ boo k_ id. */
	private final String TAG_BOOK_ID = "BookId";
	
	/** The TA g_ boo k_ type. */
	private final String TAG_BOOK_TYPE = "BookType";
	
	/** The TA g_ book_ path. */
	private final String TAG_Book_PATH = "BookPath";
	
	/** The TA g_ boo k_ name. */
	private final String TAG_BOOK_NAME = "BookName";
	
	/** The TA g_ boo k_ username. */
	private final String TAG_BOOK_USERNAME = "UserName";
	
	/** The TA g_ boo k_ password. */
	private final String TAG_BOOK_PASSWORD = "PassWord";
	
	/** The TA g_ boo k_ title. */
	private final String TAG_BOOK_TITLE = "Title";
	
	/** The TA g_ boo k_ author. */
	private final String TAG_BOOK_AUTHOR = "Author";
	
	/** The TA g_ boo k_ publisher. */
	private final String TAG_BOOK_PUBLISHER = "Publisher";
	
	/** The TA g_ boo k_ encoding. */
	private final String TAG_BOOK_ENCODING = "Encoding";
	
	/** The TA g_ boo k_ language. */
	private final String TAG_BOOK_LANGUAGE = "Language";
	
	/** The TA g_ vie w_ width. */
	private final String TAG_VIEW_WIDTH = "ViewWidth";
	
	/** The TA g_ vie w_ height. */
	private final String TAG_VIEW_HEIGHT = "ViewHeight";
	
	/** The TA g_ fon t_ level. */
	private final String TAG_FONT_LEVEL = "FontLevel";
	
	/** The TA g_ tota l_ pag e_ num. */
	private final String TAG_TOTAL_PAGE_NUM = "TotalPageNumber";
	
	/** The TA g_ las t_ pag e_ num. */
	private final String TAG_LAST_PAGE_NUM = "LastPageNumber";
	
	/** The TA g_ las t_ location. */
	private final String TAG_LAST_LOCATION = "LastLocation";
	
	/** The TA g_ las t_ acces s_ time. */
	private final String TAG_LAST_ACCESS_TIME = "LastAccessTime";
	
	/** The TA g_ boo k_ mark. */
	private final String TAG_BOOK_MARK = "BookMark";
	
	/** The TA g_ boo k_ emphasis. */
	private final String TAG_BOOK_EMPHASIS = "Emphasis";
	
	/** The TA g_ sep. */
	private final String TAG_SEP = ":";
	
	/** The BUIL d_ sep. */
	private final String BUILD_SEP = "$";
	
	/** The SPLI t_ sep. */
	private final String SPLIT_SEP = "\\$";

	/**
	 * Instantiates a new file recorder.
	 *
	 * @param context the context
	 */
	public FileRecorder(Context context) {
		mSaveWriter = null;
		mLoadReader = null;

		mRecordDir = context.getDir(FILE_RECORD_DIR,
				Context.MODE_WORLD_WRITEABLE);
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#cleanBookMarkDb(com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean cleanBookMarkDb(Book book) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#cleanBookEmphasisDb(com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean cleanBookEmphasisDb(Book book) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#addBookMarkDb(com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean addBookMarkDb(Bookmark bookmark) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#addBookEmphasisDb(com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean addBookEmphasisDb(BookEmphasis emphasis) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#delBookMarkDb(com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean delBookMarkDb(Bookmark bookmark) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#delBookEmphasisDb(com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis)
	 */
	@Override
	public boolean delBookEmphasisDb(BookEmphasis emphasis) {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#saveBookDb(com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean saveBookDb(Book book) throws Exception {
		if (!book.isValid() || !mRecordDir.exists()) {
			Logger.dLog(TAG, "save error 1");
			return false;
		}

		if (!saveBook(book)) {
			EngineCode.getInstance().setLastCode(
					EngineMsgCode.ENGINE_ERROR_RECORDER_SAVE_FAILED);
			return false;
		}

		Logger.dLog(TAG, "save path = " + mRecordFilePath);

		// debug, can be delete
		showBookInfo(book);

		return true;
	}

	/**
	 * Save book.
	 *
	 * @param book the book
	 * @return true, if successful
	 */
	private boolean saveBook(Book book) {
		mRecordFilePath = mRecordDir.getAbsolutePath() + File.separator
				+ book.getBookName() + FILE_RECORD_EXT;

		try {
			File file = new File(mRecordFilePath);

			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (Exception e) {
					throw e;
				}
			}

			FileOutputStream os = new FileOutputStream(file);
			os.write(new String("").getBytes());

			mSaveWriter = new BufferedWriter(new OutputStreamWriter(os,
					FILE_ENCODING));

			doSave(book);

			mSaveWriter.flush();
			mSaveWriter.close();
		} catch (Exception e) {
			Logger.eLog(TAG, "getSaveFile error: " + e);
			return false;
		}

		return true;
	}

	/**
	 * Do save.
	 *
	 * @param book the book
	 * @throws Exception the exception
	 */
	private void doSave(Book book) throws Exception {
		writeLine(TAG_BOOK_ID, String.valueOf(book.getBookId()));
		writeLine(TAG_BOOK_TYPE, String.valueOf(book.getBookType()));
		writeLine(TAG_Book_PATH, book.getBookPath());
		writeLine(TAG_BOOK_NAME, book.getBookName());
		writeLine(TAG_BOOK_USERNAME, book.getUserName());
		writeLine(TAG_BOOK_PASSWORD, book.getPassword());
		writeLine(TAG_BOOK_TITLE, book.getTitle());
		writeLine(TAG_BOOK_AUTHOR, book.getAuthor());
		writeLine(TAG_BOOK_PUBLISHER, book.getPublisher());
		writeLine(TAG_BOOK_ENCODING, book.getEncoding());
		writeLine(TAG_BOOK_LANGUAGE, book.getLanguage());
		// 因为旋屏机制的加入，不保存这两个变量
		// writeLine(TAG_VIEW_WIDTH, String.valueOf(book.getViewWidth()));
		// writeLine(TAG_VIEW_HEIGHT, String.valueOf(book.getViewHeight()));
		writeLine(TAG_FONT_LEVEL, String.valueOf(book.getFontLevel()));
		writeLine(TAG_TOTAL_PAGE_NUM, String.valueOf(book.getTotalPageNumber()));
		writeLine(TAG_LAST_PAGE_NUM, String.valueOf(book.getLastPageNumber()));
		writeLine(TAG_LAST_LOCATION, book.getLastLocation());
		writeLine(TAG_LAST_ACCESS_TIME,
				String.valueOf(book.getLastAccessTime()));
		for (Bookmark bookmark : book.getBookMarkCursor().getBookmarkList()) {
			writeLine(TAG_BOOK_MARK, buildBookmarkInfo(bookmark));
		}
		for (BookEmphasis emp : book.getBookEmphasisCursor().getEmphasisList()) {
			writeLine(TAG_BOOK_EMPHASIS, buildBookEmphasisInfo(emp));
		}
	}

	/**
	 * Write line.
	 *
	 * @param tag the tag
	 * @param value the value
	 * @throws Exception the exception
	 */
	private void writeLine(String tag, String value) throws Exception {
		String info;

		info = tag + TAG_SEP + value;
		mSaveWriter.write(info);
		mSaveWriter.newLine();
	}

	/**
	 * Builds the bookmark info.
	 *
	 * @param bookmark the bookmark
	 * @return the string
	 */
	private String buildBookmarkInfo(Bookmark bookmark) {
		String info = "";
		info = bookmark.getBookId() + BUILD_SEP + bookmark.getLocation()
				+ BUILD_SEP + bookmark.getPageNum() + BUILD_SEP
				+ bookmark.getTime() + BUILD_SEP + bookmark.getSummary();
		return info;
	}

	/* (non-Javadoc)
	 * @see com.easyview.ebook.reader.engine.util.recorder.IRecorder#loadBookDb(com.easyview.ebook.reader.engine.model.Book)
	 */
	@Override
	public boolean loadBookDb(Book book) {
		if (!book.isValid() || !mRecordDir.exists()) {
			Logger.eLog(TAG, "load error 1");
			return false;
		}

		if (!loadBook(book)) {
			EngineCode.getInstance().setLastCode(
					EngineMsgCode.ENGINE_ERROR_RECORDER_LOAD_FAILED);
			return false;
		}

		Logger.dLog(TAG, "load path = " + mRecordFilePath);

		// debug, can be delete
		showBookInfo(book);

		return true;
	}

	/**
	 * Load book.
	 *
	 * @param book the book
	 * @return true, if successful
	 */
	private boolean loadBook(Book book) {
		mRecordFilePath = mRecordDir.getAbsolutePath() + File.separator
				+ book.getBookName() + FILE_RECORD_EXT;

		try {
			File file = new File(mRecordFilePath);

			if (!file.exists()) {
				return false;
			}

			FileInputStream is = new FileInputStream(file);
			mLoadReader = new BufferedReader(new InputStreamReader(is,
					FILE_ENCODING));

			doLoad(book);

			mLoadReader.close();
		} catch (Exception e) {
			Logger.eLog(TAG, "getLoadFile error: " + e);
			return false;
		}

		return true;
	}

	/**
	 * Do load.
	 *
	 * @param book the book
	 * @throws Exception the exception
	 */
	private void doLoad(Book book) throws Exception {
		String value;
		String[] temp = new String[2];
		int id;

		while ((value = mLoadReader.readLine()) != null) {
			id = value.indexOf(':');
			if (id < 0) {
				Logger.eLog(TAG, "Load error value = " + value);
				continue;
			}

			temp[0] = value.substring(0, id);
			temp[1] = value.substring(id + 1);
			// temp = value.split(":");
			// if ((null == temp) || (temp.length < 2)) {
			// Logger.eLog(TAG, "Load " + temp[0] + " no value");
			// continue;
			// }

			if (temp[0].equals(TAG_BOOK_ID)) {
				if (Integer.valueOf(temp[1]) != book.getBookId()) {
					Logger.vLog(TAG, "getBookId different");
					mLoadReader.close();
					throw new Exception();
				}
			}

			if (temp[0].equals(TAG_BOOK_TYPE)) {
				if (Integer.valueOf(temp[1]) != book.getBookType()) {
					Logger.vLog(TAG, "getBookType different");
					mLoadReader.close();
					throw new Exception();
				}
			}

			if (temp[0].equals(TAG_BOOK_TITLE)) {
			}

			if (temp[0].equals(TAG_BOOK_AUTHOR)) {
			}

			if (temp[0].equals(TAG_BOOK_PUBLISHER)) {
			}

			if (temp[0].equals(TAG_BOOK_ENCODING)) {
			}

			if (temp[0].equals(TAG_BOOK_LANGUAGE)) {
			}

			if (temp[0].equals(TAG_BOOK_USERNAME)) {
				book.setUserName(temp[1]);
			}

			if (temp[0].equals(TAG_BOOK_PASSWORD)) {
				book.setPassword(temp[1]);
			}

			// 因为旋屏机制的加入，不保存这两个变量
			// if (temp[0].equals(TAG_VIEW_WIDTH)) {
			// book.setViewWidth(Integer.valueOf(temp[1]));
			// }
			//
			// if (temp[0].equals(TAG_VIEW_HEIGHT)) {
			// book.setViewHeight(Integer.valueOf(temp[1]));
			// }

			if (temp[0].equals(TAG_FONT_LEVEL)) {
				book.setFontLevel(Integer.valueOf(temp[1]));
			}

			if (temp[0].equals(TAG_TOTAL_PAGE_NUM)) {
				book.setTotalPageNumber(Integer.valueOf(temp[1]));
			}

			if (temp[0].equals(TAG_LAST_PAGE_NUM)) {
				book.setLastPageNumber(Integer.valueOf(temp[1]));
			}

			if (temp[0].equals(TAG_LAST_LOCATION)) {
				book.setLastLocation(temp[1]);
			}

			if (temp[0].equals(TAG_LAST_ACCESS_TIME)) {
				book.setLastAccessTime(Long.valueOf(temp[1]));
			}

			if (temp[0].equals(TAG_BOOK_MARK)) {
				Bookmark bookmark = splitBookmarkInfo(book, temp[1]);
				book.getBookMarkCursor().addBookmark(bookmark);
			}

			if (temp[0].equals(TAG_BOOK_EMPHASIS)) {
				BookEmphasis emphasis = splitBookEmphasisInfo(book, temp[1]);
				book.getBookEmphasisCursor().addEmphasis(emphasis);
			}
		}
	}

	/**
	 * Split bookmark info.
	 *
	 * @param book the book
	 * @param info the info
	 * @return the bookmark
	 */
	private Bookmark splitBookmarkInfo(Book book, String info) {
		if (info.equals("")) {
			return null;
		}

		Bookmark bookmark = book.makeBookmark();
		String[] temp = info.toLowerCase().split(SPLIT_SEP);
		// for (String string : temp) {
		// Logger.eLog(TAG, "temp string =  " + string);
		// }

		if (5 == temp.length) {
			bookmark.setBookId(Integer.valueOf(temp[0]));
			bookmark.setLocation(temp[1]);
			bookmark.setPageNum(Integer.valueOf(temp[2]));
			bookmark.setTime(Long.valueOf(temp[3]));
			bookmark.setSummary(temp[4]);
		} else {
			Logger.eLog(TAG, "splitBookmarkInfo format error");
			return null;
		}

		return bookmark;
	}

	/**
	 * Builds the book emphasis info.
	 *
	 * @param emphasis the emphasis
	 * @return the string
	 */
	private String buildBookEmphasisInfo(BookEmphasis emphasis) {
		String info = "";
		info = emphasis.bookId + BUILD_SEP + emphasis.startX + BUILD_SEP
				+ emphasis.startY + BUILD_SEP + emphasis.endX + BUILD_SEP
				+ emphasis.endY + BUILD_SEP + emphasis.color + BUILD_SEP
				+ emphasis.startCursor + BUILD_SEP + emphasis.endCursor + BUILD_SEP
				+ emphasis.pageLocation + BUILD_SEP + emphasis.createTime
				+ BUILD_SEP + emphasis.summary + BUILD_SEP
				+ emphasis.fontLevel;
		return info;
	}

	/**
	 * Split book emphasis info.
	 *
	 * @param book the book
	 * @param info the info
	 * @return the book emphasis
	 */
	private BookEmphasis splitBookEmphasisInfo(Book book, String info) {
		if (info.equals("")) {
			return null;
		}

		BookEmphasis emp = book.makeEmphasis();
		String[] temp = info.toLowerCase().split(SPLIT_SEP);
		// for (String string : temp) {
		// Logger.eLog(TAG, "temp string =  " + string);
		// }

		if (12 == temp.length) {
			emp.setBookId(Integer.valueOf(temp[0]));
			emp.setXY(Double.valueOf(temp[1]), Double.valueOf(temp[2]),
					Double.valueOf(temp[3]), Double.valueOf(temp[4]));
			emp.setColor(Integer.valueOf(temp[5]));
			emp.setCourse(temp[6], temp[7]);
			emp.setLocation(temp[8]);
			emp.setTime(Long.valueOf(temp[9]));
			emp.setSummary(temp[10]);
			emp.setFontLevel(Integer.valueOf(temp[11]));
		} else {
			Logger.eLog(TAG, "splitBookmarkInfo format error");
			return null;
		}

		return emp;
	}

	/**
	 * Show book info.
	 *
	 * @param book the book
	 */
	private void showBookInfo(Book book) {
		Logger.iLog(TAG, "Id: " + book.getBookType());
		Logger.iLog(TAG, "Type: " + book.getBookType());
		Logger.iLog(TAG, "Path: " + book.getBookPath());
		Logger.iLog(TAG, "Name: " + book.getBookName());
		Logger.iLog(TAG, "UserName: " + book.getUserName());
		Logger.iLog(TAG, "Password: " + book.getPassword());
		Logger.iLog(TAG, "Title: " + book.getTitle());
		Logger.iLog(TAG, "Author: " + book.getAuthor());
		Logger.iLog(TAG, "Publisher: " + book.getPublisher());
		Logger.iLog(TAG, "Encoding: " + book.getEncoding());
		Logger.iLog(TAG, "Language: " + book.getLanguage());
		Logger.iLog(TAG, "ViewWidth: " + book.getViewWidth());
		Logger.iLog(TAG, "ViewHeight: " + book.getViewHeight());
		Logger.iLog(TAG, "FontLevel: " + book.getFontLevel());
		Logger.iLog(TAG, "TotalPage: " + book.getTotalPageNumber());
		Logger.iLog(TAG, "LastPage: " + book.getLastPageNumber());
		Logger.iLog(TAG, "Location: " + book.getLastLocation());
		Logger.iLog(TAG, "LastAccessTime: " + book.getLastAccessTime());
		for (Bookmark bookmark : book.getBookMarkCursor().getBookmarkList()) {
			Logger.iLog(TAG, "Bookmark: " + buildBookmarkInfo(bookmark));
		}
		for (BookEmphasis emphasis : book.getBookEmphasisCursor()
				.getEmphasisList()) {
			Logger.iLog(TAG, "BookEmphasis: " + buildBookEmphasisInfo(emphasis));
		}
	}

}
