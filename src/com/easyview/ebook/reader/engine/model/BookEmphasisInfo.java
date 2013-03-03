/**
 * @file       BookEmphasisInfo.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-1 上午10:56:25 
 */

package com.easyview.ebook.reader.engine.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The Class BookEmphasisInfo.
 */
public class BookEmphasisInfo implements IBookEmphasisCursor {

	/** The Constant TAG. */
	static private final String TAG = "BookEmphasisInfo";

	/** The m emphasis list. */
	private LinkedList<BookEmphasis> mEmphasisList;

	/**
	 * Instantiates a new book emphasis info.
	 */
	public BookEmphasisInfo() {
		mEmphasisList = new LinkedList<BookEmphasisInfo.BookEmphasis>();
	}

	/**
	 * 高亮選擇數據類.
	 */
	static public class BookEmphasis {

		/** The BOO k_ id. */
		public int bookId;

		/** 高亮選擇的起始x坐標. */
		public double startX;

		/** 高亮選擇的起始y坐標. */
		public double startY;

		/** 高亮選擇的結束x坐標. */
		public double endX;

		/** 高亮選擇的結束y坐標. */
		public double endY;

		/** 高亮選擇的顏色標識. */
		public int color;

		/** 當前高亮選擇的字體大小. */
		public int fontLevel;

		/** 高亮選擇的時間戳. */
		public long createTime;

		/** 高亮選擇起始位置. */
		public String startCursor;

		/** 高亮選擇的結束位置. */
		public String endCursor;

		/** 高亮選擇對應的頁碼位置. */
		public String pageLocation;

		/** 高亮選擇的文本信息. */
		public String summary;

		/**
		 * Instantiates a new book emphasis.
		 */
		protected BookEmphasis() {
			bookId = -1;
			startX = -1;
			startY = -1;
			endX = -1;
			endY = -1;
			startCursor = Book.VALUE_NULL;
			endCursor = Book.VALUE_NULL;
			color = -1;
			fontLevel = -1;
			createTime = -1;
			pageLocation = Book.VALUE_NULL;
			summary = Book.VALUE_NULL;
		}

		/**
		 * Gets the book id.
		 * 
		 * @return the book id
		 */
		public int getBookId() {
			return bookId;
		}

		/**
		 * Sets the book id.
		 * 
		 * @param id
		 *            the new book id
		 */
		public void setBookId(int id) {
			bookId = id;
		}

		/**
		 * Gets the x1.
		 * 
		 * @return the x1
		 */
		public double getX1() {
			return startX;
		}

		/**
		 * Gets the y1.
		 * 
		 * @return the y1
		 */
		public double getY1() {
			return startY;
		}

		/**
		 * Gets the x2.
		 * 
		 * @return the x2
		 */
		public double getX2() {
			return endX;
		}

		/**
		 * Gets the y2.
		 * 
		 * @return the y2
		 */
		public double getY2() {
			return endY;
		}

		/**
		 * Sets the xy.
		 * 
		 * @param x1
		 *            the x1
		 * @param y1
		 *            the y1
		 * @param x2
		 *            the x2
		 * @param y2
		 *            the y2
		 */
		public void setXY(double x1, double y1, double x2, double y2) {
			startX = x1;
			startY = y1;
			endX = x2;
			endY = y2;
		}

		/**
		 * Gets the course.
		 * 
		 * @return the course
		 */
		public String[] getCourse() {
			return new String[] { startCursor, endCursor };
		}

		/**
		 * 設置選擇的起始和結束位置.
		 * 
		 * @param start
		 *            起始位置
		 * @param end
		 *            結束位置
		 */
		public void setCourse(String start, String end) {
			startCursor = start;
			endCursor = end;
		}

		/**
		 * Gets the color.
		 * 
		 * @return the color
		 */
		public int getColor() {
			return color;
		}

		/**
		 * 設置顏色標識.
		 * 
		 * @param c
		 *            顏色標識
		 */
		public void setColor(int c) {
			color = c;
		}

		/**
		 * Gets the time.
		 * 
		 * @return the time
		 */
		public long getTime() {
			return createTime;
		}

		/**
		 * 設置高亮選擇的時間戳.
		 * 
		 * @param time
		 *            時間戳，long類型
		 */
		public void setTime(long time) {
			createTime = time;
		}

		/**
		 * Gets the summary.
		 * 
		 * @return the summary
		 */
		public String getSummary() {
			return summary;
		}

		/**
		 * 設置高亮選擇的文本信息.
		 * 
		 * @param summary
		 *            高亮選擇的文本.
		 */
		public void setSummary(String sum) {
			summary = sum;
		}

		/**
		 * Gets the location.
		 * 
		 * @return the location
		 */
		public String getLocation() {
			return pageLocation;
		}

		/**
		 * 設置當前高亮頁碼位置.
		 * 
		 * @param location
		 *            頁碼位置.
		 */
		public void setLocation(String location) {
			pageLocation = location;
		}

		/**
		 * Gets the font level.
		 * 
		 * @return the font level
		 */
		public int getFontLevel() {
			return fontLevel;
		}

		/**
		 * 設置當前高亮選擇時的字體等級.
		 * 
		 * @param lev
		 *            字體等級.
		 */
		public void setFontLevel(int lev) {
			fontLevel = lev;
		}
	}

	/**
	 * 獲取高亮選擇的數量.
	 * 
	 * @return 高亮選擇的數量
	 */
	public int getCount() {
		if (null == mEmphasisList) {
			return 0;
		}

		return mEmphasisList.size();
	}

	/**
	 * 獲取高亮選擇的列表.
	 * 
	 * @return 高亮選擇的列表
	 */
	@Override
	public List<BookEmphasis> getEmphasisList() {
		return mEmphasisList;
	}

	/**
	 * 添加一個高亮選擇.
	 * 
	 * @param emphasis
	 *            高亮選擇對象
	 * @return true, 添加成功. false, 添加失敗.
	 */
	@Override
	public boolean addEmphasis(BookEmphasis emphasis) {
		if ((null == emphasis) || (null == mEmphasisList)) {
			return false;
		}
		
		BookEmphasis temp;
		Iterator<BookEmphasis> it = mEmphasisList.iterator();
		while (it.hasNext()) {
			temp = it.next();
			if ((temp.bookId == emphasis.bookId)
					&& (temp.startCursor.equals(emphasis.startCursor))
					&& (temp.endCursor.equals(emphasis.endCursor))
					&& (temp.color == emphasis.color)) {
				return false;
			}
		}

		mEmphasisList.add(emphasis);
		
		return true;
	}

	/**
	 * 刪除一個高亮選擇.
	 * 
	 * @param emphasis
	 *            高亮選擇對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	@Override
	public boolean delEmphasis(BookEmphasis emphasis) {
		if ((null == emphasis) || (null == mEmphasisList)) {
			return false;
		}

		boolean res = false;

		// res = mEmphasisList.contains(emphasis);

		BookEmphasis temp;
		Iterator<BookEmphasis> it = mEmphasisList.iterator();
		while (it.hasNext()) {
			temp = it.next();
			if ((temp.bookId == emphasis.bookId)
					&& (temp.startCursor.equals(emphasis.startCursor))
					&& (temp.endCursor.equals(emphasis.endCursor))
					&& (temp.color == emphasis.color)) {
				mEmphasisList.remove(temp);
				res = true;
				break;
			}
		}

		return res;
	}

	/**
	 * 清空高亮列表.
	 * 
	 * @return true, 清空成功. false, 清空失敗.
	 */
	@Override
	public boolean clear() {
		if (null == mEmphasisList) {
			return false;
		}

		mEmphasisList.clear();

		return true;
	}

}
