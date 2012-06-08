/**
 * @file       BookMarkInfo.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-1 上午10:40:01 
 */

package com.easyview.ebook.reader.engine.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.easyview.ebook.reader.engine.util.Logger;

/**
 * 書簽信息相關類.
 */
public class BookMarkInfo implements IBookMarkCursor {

	/** The Constant TAG. */
	static private final String TAG = "BookMarkInfo";

	/** The m bookmark to go. */
	private Bookmark mBookmarkToGo;

	/** The m bookmark list. */
	private LinkedList<Bookmark> mBookmarkList;

	/**
	 * Instantiates a new book mark info.
	 */
	protected BookMarkInfo() {
		mBookmarkList = new LinkedList<Bookmark>();
	}

	/**
	 * 書簽數據類.
	 */
	static public class Bookmark {

		/** The bookId. */
		public int bookId;

		/** The location. */
		public String location;

		/** The page num. */
		public int pageNum;

		/** The summary. */
		public String summary;

		/** The make time. */
		public long createTime;

		/**
		 * Instantiates a new bookmark.
		 */
		protected Bookmark() {
			bookId = -1;
			location = Book.VALUE_NULL;
			pageNum = -1;
			summary = Book.VALUE_NULL;
			createTime = -1;
		}

		/**
		 * 設置書籍ID.
		 * 
		 * @param id
		 *            書籍ID
		 */
		public void setBookId(int id) {
			bookId = id;
		}

		/**
		 * 獲取書籍ID.
		 * 
		 * @return 書籍ID
		 */
		public int getBookId() {
			return bookId;
		}

		/**
		 * 設置書簽位置.
		 * 
		 * @param lc
		 *            書簽位置
		 */
		public void setLocation(String lc) {
			location = lc;
		}

		/**
		 * 獲取書簽位置.
		 * 
		 * @return 書簽位置
		 */
		public String getLocation() {
			return location;
		}

		/**
		 * 設置書簽頁碼.
		 * 
		 * @param num
		 *            書簽頁碼
		 */
		public void setPageNum(int num) {
			pageNum = num;
		}

		/**
		 * 獲取書簽頁碼.
		 * 
		 * @return 書簽頁碼.
		 */
		public int getPageNum() {
			return pageNum;
		}

		/**
		 * 設置書簽頁的摘要信息.
		 * 
		 * @param sumy
		 *            當前書簽頁的摘要信息.
		 */
		public void setSummary(String sumy) {
			summary = sumy;
		}

		/**
		 * 獲取當前書簽的摘要信息.
		 * 
		 * @return 當前書簽頁的摘要信息.
		 */
		public String getSummary() {
			return summary;
		}

		/**
		 * 設置書簽的時間戳.
		 * 
		 * @param time
		 *            時間戳，long類型
		 */
		public void setTime(long time) {
			createTime = time;
		}

		/**
		 * 獲取書簽的時間戳.
		 * 
		 * @return 書簽的時間戳.
		 */
		public long getTime() {
			return createTime;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#addBookmark(com
	 * .easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean addBookmark(Bookmark bookmark) {
		if ((mBookmarkList != null) && (bookmark != null)
				&& (bookmark.getLocation() != null)
				&& (!bookmark.getLocation().equals(Book.VALUE_NULL))) {

			Iterator<Bookmark> it = mBookmarkList.iterator();
			while (it.hasNext()) {
				if (it.next().getLocation().equals(bookmark.getLocation())) {
					Logger.eLog(TAG, "addBookmark has duplicate ");
					return false;
				}
			}

			mBookmarkList.add(bookmark);
		} else {
			Logger.eLog(TAG, "addBookmark null ");
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#delBookmark(com
	 * .easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public boolean delBookmark(Bookmark bookmark) {
		Bookmark bmk;
		
		if ((mBookmarkList != null) && (bookmark != null)) {
			Iterator<Bookmark> it = mBookmarkList.iterator();
			while (it.hasNext()) {
				bmk = it.next();
				if ((bmk.getBookId() == bookmark.getBookId())
						&& bmk.getLocation().equals(bookmark.getLocation())) {
					mBookmarkList.remove(bookmark);
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#getBookmarkList()
	 */
	@Override
	public List<Bookmark> getBookmarkList() {
		return mBookmarkList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#getBookmarkTogo()
	 */
	@Override
	public Bookmark getBookmarkTogo() {
		return mBookmarkToGo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#setBookmarkTogo
	 * (com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark)
	 */
	@Override
	public void setBookmarkTogo(Bookmark bookmark) {
		mBookmarkToGo = bookmark;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.easyview.ebook.reader.engine.model.IBookMarkCursor#cleanBookmarks()
	 */
	@Override
	public boolean cleanBookmarks() {
		if (mBookmarkList != null) {
			mBookmarkList.clear();
			if (!mBookmarkList.isEmpty()) {
				return false;
			}
		}

		mBookmarkToGo = null;

		return true;
	}
}
