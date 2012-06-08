/**
 * @file       Book.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-2 上午09:58:55 
 */

package com.foxconn.ebook.reader.engine.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.foxconn.ebook.reader.engine.core.IAction;
import com.foxconn.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.Chapter;
import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.ChapterTreeState;
import com.foxconn.ebook.reader.engine.util.EngineConfig;
import com.foxconn.ebook.reader.engine.util.Logger;

/**
 * 書籍類，包含打開後的書籍的相關信息.
 * <p>
 * 當IAction的每一個交互動作執行後，會將相應的書籍信息更新到該類的對象，如頁碼，章節信息，書簽等等.
 * <p>
 * 保存數據庫信息時，主要根據該類提供的字段信息進行保存.
 */
public class Book implements IBookChapterCursor {

	/** The Constant TAG. */
	static private final String TAG = "Book";

	/**
	 * 書籍的META信息標識類.
	 */
	static public class MetaData {

		/** 書籍的標題. */
		static public final int META_TITLE = 0;

		/** 書籍的作者. */
		static public final int META_AUTHOR = 1;

		/** 書籍的發布者. */
		static public final int META_PUBLISHER = 2;

		/** 書籍的編碼. */
		static public final int META_ENCODING = 3;

		/** 書籍的語言. */
		static public final int META_LANGUAGE = 4;

		/** 該結構中信息的數量，通常為最後標識+1. */
		static public final int DATA_SIZE = 5;

		/** The m data. */
		private String[] mData;

		/**
		 * Instantiates a new meta data.
		 */
		protected MetaData() {
			mData = new String[DATA_SIZE];
			mData[META_TITLE] = VALUE_NULL;
			mData[META_AUTHOR] = VALUE_NULL;
			mData[META_PUBLISHER] = VALUE_NULL;
			mData[META_ENCODING] = VALUE_NULL;
			mData[META_LANGUAGE] = VALUE_NULL;
		}

		/**
		 * 根據type值獲取字段信息.
		 * 
		 * @param type
		 *            字段標識
		 * @return 字段信息值
		 */
		public String getValue(int type) {
			return mData[type];
		}

		/**
		 * 設置對應字段信息的值.
		 * 
		 * @param type
		 *            字段信息
		 * @param value
		 *            字段信息值
		 */
		public void setValue(int type, String value) {
			mData[type] = value;
		}

		/**
		 * 檢查META信息是否為空.
		 *
		 * @return true, if is empty
		 */
		public boolean isEmpty() {
			if (mData[META_TITLE].equals(VALUE_NULL)
					&& mData[META_AUTHOR].equals(VALUE_NULL)
					&& mData[META_PUBLISHER].equals(VALUE_NULL)
					&& mData[META_ENCODING].equals(VALUE_NULL)
					&& mData[META_LANGUAGE].equals(VALUE_NULL)) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 書籍信息類.
	 */
	static public class BookInfo {
		/** The Book id. */
		public int BookId;

		/** The Book type，如.pdf、.epub. */
		public int BookType;

		/** The Book path. */
		public String BookPath;

		/** The Book name. */
		public String BookName;

		/** 對與加密書籍，用于設置用戶名. */
		public String UserName;

		/** 對與加密書籍，用于設置密碼. */
		public String PassWord;

		/** 顯示的寬度. */
		public int ViewWidth;

		/** 顯示的高度. */
		public int ViewHeight;

		/** 退出時的頁碼. */
		public int LastPageNumber;

		/** 書籍的總頁碼. */
		public int TotalPageNumber;

		/** 退出時的位置，相對與頁碼更加精確. */
		public String LastLocation;

		/** 字體等級. */
		public int FontLevel;

		/** 最後打開時間. */
		public long LastAccessTime;
		
		/** 书籍文件大小，单位为字节. */
		public long BookSize;

		// MetaData[0] -> title.
		// MetaData[1] -> creator.
		// MetaData[2] -> publisher.
		// MetaData[3] -> encoding.
		// MetaData[4] -> language.
		/** The m meta data. @see MetaData */
		public MetaData MetaData;

		/**
		 * Instantiates a new book info.
		 */
		protected BookInfo() {
			init();
		}

		/**
		 * Inits the.
		 */
		protected void init() {
			BookId = -1;
			MetaData = new MetaData();
			BookType = Book.BookType.UNKNOW;
			BookPath = "";
			BookName = "";
			UserName = "";
			PassWord = "";
			ViewWidth = 600;
			ViewHeight = 800;
			LastPageNumber = 1;
			TotalPageNumber = 1;
			FontLevel = -1;
			LastLocation = VALUE_NULL;
			LastAccessTime = -1;
			BookSize = 0;
		}
	}

	/**
	 * 閱讀器的類型.
	 */
	static public interface ReaderType {

		/** Adobe Reader. */
		public final int READER_TYPE_ADOBE = 1;

		/** FBReader. */
		public final int READER_TYPE_FBREADER = 2;
	}

	/**
	 * 書籍格式類型.
	 */
	static public interface BookType {

		/** 未知類型. */
		public final int UNKNOW = 0;

		/** PDF類型. */
		public final int PDF = 1;

		/** EPUB類型. */
		public final int EPUB = 2;

		/** TXT類型. */
		public final int TXT = 3;

		/** FB2類型. */
		public final int FB2 = 4;

		/** HTML類型. */
		public final int HTML = 5;

		/** HTM類型. */
		public final int HTM = 6;

	}

	/** 空值標識符號. */
	static public final String VALUE_NULL = "null";

	/** The m cur page. */
	protected Page mCurPage;

	/** The m pre page. */
	protected Page mPrePage;

	/** The m next page. */
	protected Page mNextPage;

	/** The m cache page. */
	protected Page mCachePage;

	/** The m chapter tree. */
	protected ChapterTreeInfo mChapterTree;

	/** The m book info. */
	protected BookInfo mBookInfo = new BookInfo();

	/** The m book search info. */
	protected BookSearchInfo mBookSearchInfo;

	/** The m bookmark info. */
	protected BookMarkInfo mBookmarkInfo;

	/** The m book emphasis info. */
	protected BookEmphasisInfo mBookEmphasisInfo;

	/** The m is opened. */
	protected boolean mIsOpened = false;

	/** The m is exists. */
	protected boolean mIsExists = false;
	
	/** The m page text. */
	protected String mPageText = "";

	/**
	 * 通過文件的完整路徑構造書籍對象.如果當前的書籍和數據庫的ID無任何關聯時，可以使用該構造函數，否則使用 {@link #Book(int)}
	 * 
	 * @param path
	 *            書籍文件的完整路徑名
	 */
	public Book(String path) {
		createFromPath(path);
	}
	
	/**
	 * 通過數據庫中的BookId構造書籍對象.
	 *
	 * @param id 數據庫中的BookId
	 */
	public Book(int id) {
		init();
		setBookId(id);
	}

	/**
	 * 檢查書籍是否存在.
	 * 
	 * @return true, 存在. false, 不存在.
	 */
	public boolean isExists() {
		return mIsExists;
	}

	/**
	 * 檢查書籍格式是否符合.
	 * 
	 * @return true, 符合. false, 不符合.
	 */
	public boolean isValid() {
		if ((null == mBookInfo) || (mBookInfo.BookType == BookType.UNKNOW)) {
			return false;
		}

		return true;
	}

	/**
	 * 獲取當前的頁對象.
	 * 
	 * @return 當前頁對象
	 */
	public Page getCurPage() {
		if (null == mCurPage) {
			Logger.vLog(TAG, "curPage is null");
		}
		return mCurPage;
	}

	/**
	 * 設置當前頁對象.
	 * 
	 * @param page
	 *            頁對象
	 */
	public void setCurPage(Page page) {
		mCurPage = page;
	}

	/**
	 * 獲取前一頁對象.
	 * 
	 * @return 前一頁對象.
	 */
	public Page getPrePage() {
		if (null == mPrePage) {
			Logger.vLog(TAG, "prePage is null");
		}
		return mPrePage;
	}

	/**
	 * 設置前一頁對象.
	 * 
	 * @param page
	 *            頁對象
	 */
	public void setPrePage(Page page) {
		mPrePage = page;
	}

	/**
	 * 獲取下一頁對象.
	 * 
	 * @return 頁對象.
	 */
	public Page getNextPage() {
		if (null == mNextPage) {
			Logger.vLog(TAG, "nextPage is null");
		}
		return mNextPage;
	}

	/**
	 * 設置後一頁對象.
	 * 
	 * @param page
	 *            頁對象.
	 */
	public void setNextPage(Page page) {
		mNextPage = page;
	}

	/**
	 * 獲取緩存頁對象.
	 * 
	 * @return 緩存頁對象.
	 */
	public Page getCachePage() {
		if (null == mCachePage) {
			Logger.vLog(TAG, "mCachePage is null");
		}
		return mCachePage;
	}

	/**
	 * 設置緩存頁對象.
	 * 
	 * @param page
	 *            頁對象
	 */
	public void setCachePage(Page page) {
		mCachePage = page;
	}

	/**
	 * 重置當前頁內容.
	 */
	public void resetCurPage() {
		Page page = getCurPage();

		if (page != null) {
			page.reset();
		}
	}

	/**
	 * 重置前一頁內容.
	 */
	public void resetPrePage() {
		Page page = getPrePage();

		if (page != null) {
			page.reset();
		}
	}

	/**
	 * 重置下一頁內容.
	 */
	public void resetNextPage() {
		Page page = getNextPage();

		if (page != null) {
			page.reset();
		}
	}

	/**
	 * 設置書籍已經被打開.
	 * 
	 * @param opened
	 *            true, 已經被打開. false, 未被打開.
	 */
	public void setOpened(boolean opened) {
		mIsOpened = opened;
	}

	/**
	 * 獲取書籍是否被打開.
	 * 
	 * @return true, 已經被打開. false, 未被打開.
	 */
	public boolean getOpened() {
		return mIsOpened;
	}

	/**
	 * 獲取書籍文件完整路徑.
	 * 
	 * @return 文件完整路徑
	 */
	public String getBookPath() {
		return (mBookInfo != null) ? mBookInfo.BookPath : "";
	}

	/**
	 * 設置書籍文件完整路徑.
	 *
	 * @param path 文件完整路徑
	 */
	public void setBookPath(String path) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.BookPath = path;
	}
	
	/**
	 * 獲取書籍文件名，不包含完整路徑.
	 * 
	 * @return 書籍文件名
	 */
	public String getBookName() {
		return (mBookInfo != null) ? mBookInfo.BookName : "";
	}

	/**
	 * Sets the book name.
	 *
	 * @param name the new book name
	 */
	public void setBookName(String name) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.BookName = name;
	}
	
	/**
	 * 获取文件大小.
	 *
	 * @return 文件大小，单位为字节.
	 */
	public long getBookSize() {
		return (mBookInfo != null) ? mBookInfo.BookSize : 0;
	}
	
	/**
	 * 设置文件大小，单位为字节.
	 *
	 * @param size 文件大小，单位为字节
	 */
	public void setBookSize(long size) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.BookSize = size;
	}
	
	/**
	 * 獲取Reader類型.
	 * 
	 * @return Reader類型
	 * @see com.foxconn.ebook.reader.engine.model.Book.ReaderType
	 */
	public int getReaderType() {
		int type = getBookType();

		switch (type) {
		// Remove Adobe RMSDK support
//		case BookType.PDF:
//			return ReaderType.READER_TYPE_ADOBE;
		case BookType.EPUB:
		case BookType.TXT:
		case BookType.FB2:
		case BookType.HTM:
		case BookType.HTML:
			return ReaderType.READER_TYPE_FBREADER;
		default:
			return ReaderType.READER_TYPE_FBREADER;
		}
	}

	/**
	 * 獲取書籍類型標識.
	 * 
	 * @return 書籍類型標識
	 * @see com.foxconn.ebook.reader.engine.model.Book.BookType
	 */
	public int getBookType() {
		return (mBookInfo != null) ? mBookInfo.BookType : BookType.UNKNOW;
	}

	/**
	 * 設置書籍類型標識.
	 * 
	 * @param bookName
	 *            書籍文件名
	 */
	public void setBookType(String bookName) {
		if (null == mBookInfo) {
			return;
		}
		
		// Remove Adobe RMSDK support
//		if (bookName.endsWith(".pdf")) {
//			mBookInfo.BookType = BookType.PDF;
//		}
		if (bookName.endsWith(".epub")) {
			mBookInfo.BookType = BookType.EPUB;
		} else if (bookName.endsWith(".fb2")) {
			mBookInfo.BookType = BookType.FB2;
		} else if (bookName.endsWith(".txt")) {
			mBookInfo.BookType = BookType.TXT;
		} else if (bookName.endsWith(".htm")) {
			mBookInfo.BookType = BookType.HTM;
		} else if (bookName.endsWith(".html")) {
			mBookInfo.BookType = BookType.HTML;
		} else {
			mBookInfo.BookType = BookType.UNKNOW;
		}
	}

	/**
	 * 獲取書籍的ID標識.
	 * 
	 * @return 書籍的ID標識
	 */
	public int getBookId() {
		return (mBookInfo != null) ? mBookInfo.BookId : -1;
	}

	/**
	 * 設置書籍的ID標識.
	 * 
	 * @param id
	 *            書籍的ID標識
	 */
	public void setBookId(int id) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.BookId = id;
	}

	/**
	 * 獲取書籍的META信息.
	 * 
	 * @return 書籍的META信息
	 */
	public MetaData getMetaData() {
		return (mBookInfo != null) ? mBookInfo.MetaData : null;
	}

	/**
	 * 設置META信息.
	 * 
	 * @param data
	 *            META信息對象
	 */
	public void setMetaData(MetaData data) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.MetaData = data;
	}

	/**
	 * 獲取書籍的標題.
	 * 
	 * @return 書籍的標題
	 */
	public String getTitle() {
		return (mBookInfo != null) ? mBookInfo.MetaData.getValue(MetaData.META_TITLE) : Book.VALUE_NULL;
	}

	/**
	 * 獲取書籍的作者.
	 * 
	 * @return 獲取書籍的作者
	 */
	public String getAuthor() {
		return (mBookInfo != null) ? mBookInfo.MetaData.getValue(MetaData.META_AUTHOR) : Book.VALUE_NULL;
	}

	/**
	 * 獲取書籍的發布者.
	 * 
	 * @return 書籍發布者
	 */
	public String getPublisher() {
		return (mBookInfo != null) ? mBookInfo.MetaData.getValue(MetaData.META_PUBLISHER) : Book.VALUE_NULL;
	}

	/**
	 * 獲取書籍的編碼格式.
	 * 
	 * @return 編碼格式字符串，如UTF-8、GBK等
	 */
	public String getEncoding() {
		return (mBookInfo != null) ? mBookInfo.MetaData.getValue(MetaData.META_ENCODING) : Book.VALUE_NULL;
	}

	/**
	 * 獲取語言標識.
	 * 
	 * @return 語言標識，如zh、en等
	 */
	public String getLanguage() {
		return (mBookInfo != null) ? mBookInfo.MetaData.getValue(MetaData.META_LANGUAGE) : Book.VALUE_NULL;
	}

	/**
	 * 設置書籍的用戶名.
	 * 
	 * @param userName
	 *            用戶名字符串
	 */
	public void setUserName(String userName) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.UserName = userName;
	}

	/**
	 * 獲取書籍的用戶名.
	 * 
	 * @return 用戶名字符串
	 */
	public String getUserName() {
		return (mBookInfo != null) ? mBookInfo.UserName : "";
	}

	/**
	 * 設置書籍的密碼.
	 * 
	 * @param password
	 *            密碼字符串
	 */
	public void setPassword(String password) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.PassWord = password;
	}

	/**
	 * 獲取書籍的密碼.
	 * 
	 * @return 密碼字符串
	 */
	public String getPassword() {
		return (mBookInfo != null) ? mBookInfo.PassWord : "";
	}

	/**
	 * 獲取視圖顯示寬度.
	 * 
	 * @return 顯示寬度
	 */
	public int getViewWidth() {
		return (mBookInfo != null) ? mBookInfo.ViewWidth : 0;
	}

	/**
	 * 設置視圖顯示寬度.
	 * 
	 * @param width
	 *            顯示寬度
	 */
	public void setViewWidth(int width) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.ViewWidth = width;
	}

	/**
	 * 獲取視圖的顯示高度.
	 * 
	 * @return 顯示高度
	 */
	public int getViewHeight() {
		return (mBookInfo != null) ? mBookInfo.ViewHeight : 0;
	}

	/**
	 * 設置視圖的顯示高度.
	 * 
	 * @param height
	 *            顯示高度
	 */
	public void setViewHeight(int height) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.ViewHeight = height;
	}

	/**
	 * 獲取書籍的總頁碼.
	 * 
	 * @return 書籍總頁碼
	 */
	public int getTotalPageNumber() {
		return (mBookInfo != null) ? mBookInfo.TotalPageNumber : 1;
	}

	/**
	 * 設置書籍的總頁碼.
	 * 
	 * @param number
	 *            書籍總頁碼
	 */
	public void setTotalPageNumber(int number) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.TotalPageNumber = number;
	}

	/**
	 * 獲取當前頁碼.
	 * 
	 * @return 當前頁碼
	 */
	public int getCurPageNumber() {
		return (mBookInfo != null) ? mBookInfo.LastPageNumber : 1;
	}

	/**
	 * 設置當前頁碼.
	 * 
	 * @param number
	 *            當前頁碼
	 */
	public void setCurPageNumber(int number) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.LastPageNumber = number < 1 ? 1 : number;
	}

	/**
	 * 獲取退出時的頁碼.
	 * 
	 * @return 退出時的頁碼
	 */
	public int getLastPageNumber() {
		return (mBookInfo != null) ? mBookInfo.LastPageNumber : 1;
	}

	/**
	 * 設置退出時的頁碼.
	 * 
	 * @param number
	 *            退出時的頁碼
	 */
	public void setLastPageNumber(int number) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.LastPageNumber = number < 1 ? 1 : number;
	}

	/**
	 * 獲取字體大小等級.
	 * 
	 * @return 字體大小等級
	 */
	public int getFontLevel() {
		return (mBookInfo != null) ? mBookInfo.FontLevel : -1;
	}

	/**
	 * 設置字體大小等級.
	 * 
	 * @param fontSize
	 *            字體大小等級
	 */
	public void setFontLevel(int fontSize) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.FontLevel = fontSize;
	}

	/**
	 * 設置最後打開書籍的時間，為long類型.
	 * 
	 * @param time
	 *            最後打開書籍的時間
	 */
	public void setLastAccessTime(long time) {
		if (null == mBookInfo) {
			return;
		}
		
		mBookInfo.LastAccessTime = time;
	}

	/**
	 * 獲取最後打開書籍的時間，為long類型.
	 * 
	 * @return 最後打開書籍的時間
	 */
	public long getLastAccessTime() {
		return (mBookInfo != null) ? mBookInfo.LastAccessTime : 0;
	}

	/**
	 * 設置當前頁的字符串內容.
	 *
	 * @param text 當前頁顯示的字符串內容.
	 */
	public void setPageText(String text) {
		mPageText = text;
	}
	
	/**
	 * 獲取當前頁的摘要信息.
	 *
	 * @return 當前頁的摘要信息
	 */
	public String getPageSummary() {
		if (mCurPage != null) {
			return mCurPage.getSummary();
		}
		
		return "";
	}
	
	/**
	 * 獲取當前頁的字符串內容，需要執行{@link IAction.#fetchText()}.
	 *
	 * @return 當前頁的字符串內容.
	 * @see IAction#fetchText().
	 */
	public String getPageText() {
		return mPageText;
	}
	
	/**
	 * 獲取IBookMarkCursor接口.
	 * 
	 * @return IBookMarkCursor接口
	 */
	public IBookMarkCursor getBookMarkCursor() {
		return mBookmarkInfo;
	}

	/**
	 * 獲取IBookSearchCursor接口.
	 * 
	 * @return IBookSearchCursor接口
	 */
	public IBookSearchCursor getBookSearchCursor() {
		return mBookSearchInfo;
	}

	/**
	 * 獲取IBookEmphasisCursor接口.
	 * 
	 * @return IBookEmphasisCursor接口
	 */
	public IBookEmphasisCursor getBookEmphasisCursor() {
		return mBookEmphasisInfo;
	}

	/**
	 * Make meta data.
	 * 
	 * @return the meta data
	 */
	public MetaData makeMetaData() {
		return new Book.MetaData();
	}

	/**
	 * 創建一個書簽對象.
	 * 
	 * @return 書簽對象
	 */
	public BookMarkInfo.Bookmark makeBookmark() {
		BookMarkInfo.Bookmark bookmark =  new BookMarkInfo.Bookmark();
		bookmark.setBookId(getBookId());
		
		return bookmark;
	}

	/**
	 * 創建一個高亮顯示對象.
	 * 
	 * @return 高亮顯示對象
	 */
	public BookEmphasis makeEmphasis() {
		BookEmphasisInfo.BookEmphasis emphasis =  new BookEmphasisInfo.BookEmphasis();
		emphasis.setBookId(getBookId());
		
		return emphasis;
	}

	/**
	 * 创建搜索结果的对象.
	 * 
	 * @return 搜索结果的对象
	 */
	public BookSearchInfo.BookSearchResult makeSearchResult() {
		return new BookSearchInfo.BookSearchResult();
	}

	/**
	 * 创建搜索结果的对象的數組.
	 * 
	 * @param size
	 *            數組的大小
	 * @return 搜索结果的对象的數組
	 */
	public BookSearchInfo.BookSearchResult[] makeSearchResults(int size) {
		BookSearchInfo.BookSearchResult[] results = new BookSearchInfo.BookSearchResult[size];
		for (int i = 0; i < results.length; i++) {
			results[i] = new BookSearchInfo.BookSearchResult();
		}

		return results;
	}

	/**
	 * 獲取當前顯示的頁位置，相比頁碼更加精確.
	 * 
	 * @return 當前的頁位置
	 */
	public String getLastLocation() {
		return mBookInfo.LastLocation;
	}

	/**
	 * 設置當前顯示的頁位置.
	 * 
	 * @param location
	 *            當前的頁位置
	 */
	public void setLastLocation(String location) {
		if ((null == location) || (location.equals(""))) {
			mBookInfo.LastLocation = VALUE_NULL;
		} else {
			mBookInfo.LastLocation = location;
		}
	}

	/**
	 * 從頁碼更新書籍的信息.
	 * 
	 * @param page
	 *            頁對象
	 */
	public void updateFromPage(Page page) {
		if (page != null) {
			setCurPageNumber(page.getPageNum());
			setLastLocation(page.getLocation());
		}
	}

	/**
	 * 從配置對象更新書籍信息.
	 * 
	 * @param config
	 *            配置對象
	 */
	public void updateFromConfig(EngineConfig config) {
		if (config != null) {
			setFontLevel(config.getFontLevel());
			setViewWidth(config.getViewWidth());
			setViewHeight(config.getViewHeight());
			initPage();
		}
	}

	/**
	 * 釋放資源，通常關閉書籍的動作執行後，會自動調用該方法.
	 */
	public void free() {
		if (mChapterTree != null) {
			mChapterTree.clear();
			mChapterTree = null;
		}

		if (mBookSearchInfo != null) {
			mBookSearchInfo.resetResults();
			mBookSearchInfo = null;
		}

		if (mBookmarkInfo != null) {
			mBookmarkInfo = null;
		}

		if (mBookEmphasisInfo != null) {
			mBookEmphasisInfo = null;
		}

		if (mBookInfo != null) {
			mBookInfo = null;
		}

		if (mCurPage != null) {
			mCurPage.reset();
			mCurPage = null;
		}

		if (mPrePage != null) {
			mPrePage.reset();
			mPrePage = null;
		}

		if (mNextPage != null) {
			mNextPage.reset();
			mNextPage = null;
		}

		if (mCachePage != null) {
			mCachePage.reset();
			mCachePage = null;
		}

		Logger.dLog(TAG, "Book free");
	}

	// Chapter part ----------------------------------------------

	/**
	 * 获取目录树操作接口.
	 * 
	 * @return 目录树操作接口
	 */
	public IBookChapterCursor getBookChapterCursor() {
		return this;
	}

	/**
	 * 获取目录标题列表.
	 * 
	 * @return 目录标题的列表，为String[]类型
	 */
	public String[] getChapterTitles() {
		if (mChapterTree != null) {
			return mChapterTree.getChapterTitles();
		}

		return null;
	}

	/**
	 * 根据目录标题获取目录的序号.
	 * 
	 * @param title
	 *            目录标题
	 * @return 该标题的目录序号
	 */
	public int getChapterIndexByTitle(String title) {
		if (mChapterTree != null) {
			return mChapterTree.getChapterIndex(title);
		}

		return -1;
	}

	/**
	 * 获取每个目录标题对应的页码.
	 * 
	 * @return 目录标题对应的页码，为int[]类型.
	 */
	public int[] getChapterPageNums() {
		if (mChapterTree != null) {
			return mChapterTree.getChapterPageNums();
		}

		return null;
	}

	/**
	 * 获取当前目录的序号.
	 * 
	 * @return 当前目录的序号
	 */
	public int getCurChapterIndex() {
		if (mChapterTree != null) {
			return mChapterTree.getCurChapterIndex();
		}

		return -1;
	}

	/**
	 * 获取最大的目录序号.
	 * 
	 * @return 最大的目录序号
	 */
	public int getMaxChapterNum() {
		if (mChapterTree != null) {
			return mChapterTree.getMaxChapterNum();
		}

		return 0;
	}

	/**
	 * 检查是否存在前一章节.
	 * 
	 * @return true, 存在. false, 不存在
	 */
	public boolean hasPreviousChapter() {
		if (mChapterTree != null) {
			return mChapterTree.hasPreviousChapter(this);
		}

		return false;
	}

	/**
	 * 检查是否存在下一章节.
	 * 
	 * @return true, 存在. false, 不存在
	 */
	public boolean hasNextChapter() {
		if (mChapterTree != null) {
			return mChapterTree.hasNextChapter(this);
		}

		return false;
	}

	/**
	 * 根据页码序号获取前一章节的序号.
	 * 
	 * @param pageIndex
	 *            页码序号
	 * @return 前一章节的序号
	 */
	public int getPreviousChapterIndex(int pageIndex) {
		if (mChapterTree != null) {
			return mChapterTree.getPreviousChapterIndex(pageIndex);
		}

		return -1;
	}

	/**
	 * 根据页码序号获取后一章节的序号.
	 * 
	 * @param pageIndex
	 *            页码序号
	 * @return 后一章节的序号
	 */
	public int getNextChapterIndex(int pageIndex) {
		if (mChapterTree != null) {
			return mChapterTree.getNextChapterIndex(pageIndex);
		}

		return -1;
	}

	/**
	 * 獲取對應章節序號的頁碼.
	 * 
	 * @param index
	 *            章節序號
	 * @return 頁碼序號
	 */
	public int getPageIndexByChapterIndex(int index) {
		if (mChapterTree != null) {
			return mChapterTree.getPageIndexForChapter(index);
		}

		return -1;
	}

	/**
	 * 設置章節狀態.
	 * 
	 * @param state
	 *            章節加載的狀態標識
	 */
	public void setChapterState(ChapterTreeState state) {
		if (mChapterTree != null) {
			mChapterTree.setState(state);
		}
	}

	/**
	 * 設置當前章節的序號.
	 * 
	 * @param index
	 *            當前章節的序號
	 */
	public void setCurChapterIndex(int index) {
		if (mChapterTree != null) {
			mChapterTree.setCurChapterIndex(index);
		}
	}

	/**
	 * 獲取章節對象的列表.
	 * 
	 * @return 章節對象列表
	 */
	public List<Chapter> getChapterList() {
		if (mChapterTree != null) {
			return mChapterTree.getChapterList();
		}

		return null;
	}

	/**
	 * 章節是否已經加載.
	 * 
	 * @return true, 已加載. false, 未加載.
	 */
	public boolean chapterIsLoad() {
		if (null == mChapterTree) {
			return false;
		}

		ChapterTreeState state = mChapterTree.getState();
		if (state == ChapterTreeState.UNKNOWN) {
			return false;
		}

		return true;
	}

	/**
	 * 章節列表是否為空.
	 * 
	 * @return true, 列表為空. false, 列表非空.
	 */
	public boolean chapterIsEmpty() {
		if (null == mChapterTree) {
			return false;
		}

		ChapterTreeState state = mChapterTree.getState();
		if (state == ChapterTreeState.LIST_EMPTY) {
			return true;
		}

		return false;
	}

	/**
	 * 章節初始化.
	 * 
	 * @param indexs
	 *            章節序號列表
	 * @param chapters
	 *            章節標題列表
	 */
	public void chapterInit(int[] indexs, String[] chapters) {
		if (null == mChapterTree) {
			return;
		}

		boolean res = mChapterTree.init(indexs, chapters);

		if (res) {
			mChapterTree.setState(ChapterTreeState.LIST_LOADED);
		} else {
			mChapterTree.setState(ChapterTreeState.LIST_EMPTY);
		}
	}

	/**
	 * 檢查文件是否存在.
	 * 
	 * @param path
	 *            文件全路徑名
	 * @return true, 文件存在. false, 文件不存在.
	 */
	private boolean checkFileExists(String path) {
		try {
			if (null == path) {
				return false;
			}
			File file = new File(path);
			if (!file.isFile() || !file.exists()) {
				return false;
			}

			mBookInfo.BookPath = path;
			mBookInfo.BookName = file.getName();
			mBookInfo.BookSize = file.length();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		mIsExists = true;
		setBookType(getBookName().toLowerCase());

		return true;
	}

	/**
	 * 检查文件路径，并根据结果来执行具体的初始化过程.
	 *
	 * @param path the path
	 */
	public void createFromPath(String path) {
		if (checkFileExists(path)) {
			init();
		}
	}
	
	/**
	 * Inits the.
	 */
	private void init() {
		initPage();
		initChapterTree();
		initBookSearch();
		initBookMarkInfo();
		initBookEmphasisInfo();
	}
	
	/**
	 * 根據閱讀器的類型，重新初始化頁對象.當閱讀器類型改變後，需要重新初始化.
	 */
	public void initPage() {
		int width;
		int height;

		width = getViewWidth();
		height = getViewHeight();

		/*
		 * determine the book type based on extension of file, and generate the
		 * pageNum .pdf, .epub -> new AdobePage()
		 */
		switch (getReaderType()) {
		case ReaderType.READER_TYPE_ADOBE:
			mCurPage = new AdobePage(width, height);
			mPrePage = new AdobePage(width, height);
			mNextPage = new AdobePage(width, height);
			mCachePage = new AdobePage(width, height);
			break;
		case ReaderType.READER_TYPE_FBREADER:
			mCurPage = new FbreaderPage(width, height);
			mPrePage = new FbreaderPage(width, height);
			mNextPage = new FbreaderPage(width, height);
			mCachePage = new FbreaderPage(width, height);
			break;

		default:
			break;
		}

		if (mCurPage != null) {
			mCurPage.reset();
		}

		if (mPrePage != null) {
			mPrePage.reset();
		}

		if (mNextPage != null) {
			mNextPage.reset();
		}

		if (mCachePage != null) {
			mCachePage.reset();
		}
	}

	/**
	 * Inits the chapter tree.
	 */
	private void initChapterTree() {
		mChapterTree = new ChapterTreeInfo(getBookType());
	}

	// Search Result part -----------------------------------

	/**
	 * Inits the book search.
	 */
	private void initBookSearch() {
		mBookSearchInfo = new BookSearchInfo();
	}

	/**
	 * Inits the book mark info.
	 */
	private void initBookMarkInfo() {
		mBookmarkInfo = new BookMarkInfo();
	}

	/**
	 * Inits the book emphasis info.
	 */
	private void initBookEmphasisInfo() {
		mBookEmphasisInfo = new BookEmphasisInfo();
	}

}
