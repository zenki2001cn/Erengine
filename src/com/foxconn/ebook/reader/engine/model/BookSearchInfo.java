/**
 * @file       BookSearchInfo.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-1 上午10:43:59 
 */

package com.foxconn.ebook.reader.engine.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索信息類.
 */
public class BookSearchInfo implements IBookSearchCursor {
	
	/** The m search result list. */
	private ArrayList<BookSearchResult> mSearchResultList;
	
	/** The m search history. */
	private ArrayList<String> mSearchHistory;
	
	/** The m cur search pos. */
	private int mCurSearchPos = -1;
	
	/** The m has results. */
	private boolean mHasResults = false;
	
	/** The m last search key. */
	private String mLastSearchKey;

	/** The Constant SEARCH_HISTORY. */
	static public final int SEARCH_HISTORY = 10;
	
	/**
	 * Instantiates a new book search info.
	 */
	protected BookSearchInfo() {
		init();
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		mSearchResultList = new ArrayList<BookSearchResult>();
		mSearchHistory = new ArrayList<String>();
		mCurSearchPos = -1;
		mLastSearchKey = null;
	}

	/**
	 * The Class BookSearchResult.
	 */
	static public class BookSearchResult {
		
		/** The text. */
		public String text;
		
		/** The start_pos. */
		public String start_pos;
		
		/** The end_pos. */
		public String end_pos;
		
		/**
		 * Instantiates a new search result.
		 */
		protected BookSearchResult() {
			text = "";
			start_pos = Book.VALUE_NULL;
			end_pos = Book.VALUE_NULL;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#setSearchKeyword(java.lang.String)
	 */
	public void setSearchKeyword(String key) {
		mLastSearchKey = key;

		if (mSearchHistory != null) {
			if (mSearchHistory.size() >= SEARCH_HISTORY) {
				mSearchHistory.remove(0);
			}
			mSearchHistory.add(key);
		}
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#getSearchKeyword()
	 */
	public String getSearchKeyword() {
		return mLastSearchKey;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#getSeachHistory()
	 */
	public List<String> getSeachHistory() {
		return mSearchHistory;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#resetResults()
	 */
	public void resetResults() {
		if (mSearchResultList != null) {
			mSearchResultList.clear();
		}

		mCurSearchPos = -1;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#addResults(com.foxconn.ebook.jni.SearchResult[])
	 */
	public void addResults(BookSearchResult[] results) {
		if ((results != null) && results.length > 0) {
			for (BookSearchResult searchResult : results) {
				mSearchResultList.add(searchResult);
			}
		}

		if (mSearchResultList.size() > 0) {
			mHasResults = true;
		} else {
			mHasResults = false;
		}
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#getResults()
	 */
	public List<BookSearchResult> getResults() {
		return mSearchResultList;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#hasResults()
	 */
	public boolean hasResults() {
		return mHasResults;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#getNextResult()
	 */
	public BookSearchResult getNextResult() {
		int nextPos = mCurSearchPos + 1;
		BookSearchResult result = null;

		if (hasResults() && (nextPos < mSearchResultList.size())) {
			result = mSearchResultList.get(nextPos);
			mCurSearchPos++;
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#getPreResult()
	 */
	public BookSearchResult getPreResult() {
		int prePos = mCurSearchPos - 1;
		BookSearchResult result = null;

		if (hasResults() && (prePos >= 0)) {
			result = mSearchResultList.get(prePos);
			mCurSearchPos--;
		} else {
			mCurSearchPos = 0;
		}

		return result;
	}
	

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.model.IBookSearchCursor#moveCursorToEnd()
	 */
	@Override
	public void moveCursorToEnd() {
		if (hasResults()) {
			mCurSearchPos = mSearchResultList.size() - 1;
		}
	}
}
