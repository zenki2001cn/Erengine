/**
 * @file       ChapterTreeInfo.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-4-27 上午09:48:49 
 */

package com.easyview.ebook.reader.engine.model;

import java.util.ArrayList;
import android.util.Log;

import com.easyview.ebook.reader.engine.model.strategy.IHasChapter;
import com.easyview.ebook.reader.engine.model.strategy.JudgeByChapterList;
import com.easyview.ebook.reader.engine.model.strategy.JudgeByTotalChapterNumber;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 * 目錄樹信息類
 */
public class ChapterTreeInfo {
	private static final String TAG = "ChapterTreeInfo";

	/**
	 * JudgeByChapterList
	 */
	private static final JudgeByChapterList JBCL = new JudgeByChapterList();
	/**
	 * JudgeByTotalChapterNumber
	 */
	private static final JudgeByTotalChapterNumber JBTCN = new JudgeByTotalChapterNumber();
	protected int maxChapterNum = -1;
	protected int curChapterIndex;
	protected ChapterTreeState state;
	protected IHasChapter iHasChapter;
	protected ArrayList<Chapter> list;
	protected String[] titles;
	protected int[] numbers;
	static private int mBookType;

	protected ChapterTreeInfo(int bookType) {
		state = ChapterTreeState.UNKNOWN;
		list = new ArrayList<Chapter>();
		mBookType = bookType;
	}

	protected void add(int index, int chapterIndex, String title) {
		Chapter cpt = new Chapter();
		cpt.index = index;
		cpt.firstDirectoryChapterPageIndex = chapterIndex;
		cpt.title = title;
		list.add(cpt);
	}

	protected int getCurChapterIndex() {
		Logger.dLog(TAG, "get curChapterIndex: " + curChapterIndex);
		return curChapterIndex;
	}

	protected void setCurChapterIndex(int curChapterIndex) {
		this.curChapterIndex = curChapterIndex;
		Logger.dLog(TAG, "set curChapterIndex: " + curChapterIndex);
	}

	protected ChapterTreeState getState() {
		return state;
	}

	protected void setState(ChapterTreeState state) {
		this.state = state;
	}

	protected int getMaxChapterNum() {
		Logger.dLog(TAG, "get maxChapterNum: " + maxChapterNum);
		return maxChapterNum;
	}

	protected void setMaxChapterNum(int maxChapterNum) {
		Logger.dLog(TAG, "set maxChapterNum: " + maxChapterNum);
		this.maxChapterNum = maxChapterNum;
	}

	protected String[] getChapterTitles() {
		return titles;
	}

	protected boolean hasNextChapter(Book book) {
		if (state == ChapterTreeState.CURRENT_LOADED) {
			iHasChapter = JBTCN;
		} else if (state == ChapterTreeState.LIST_LOADED) {
			iHasChapter = JBCL;
		} else {
			iHasChapter = null;
		}
		if (iHasChapter != null) {
			return iHasChapter.hasNextChapter(book);
		}
		return false;
	}

	protected boolean hasPreviousChapter(Book book) {
		if (state == ChapterTreeState.CURRENT_LOADED) {
			iHasChapter = JBTCN;
		} else if (state == ChapterTreeState.LIST_LOADED) {
			iHasChapter = JBCL;
		} else {
			iHasChapter = null;
		}
		if (iHasChapter != null) {
			return iHasChapter.hasPreviousChapter(book);
		}
		return false;
	}

	/**
	 * init初始化章节树结构，并内部维护一个index标识(以0为基数计算)
	 * 
	 * @param indexs
	 *            : 表示该章节的页码序号，该序号用于跳转定位
	 * 
	 * @param chapters
	 *            : 表示该章节标题字符串
	 */
	protected boolean init(int[] indexs, String[] chapters) {
		if ((null == indexs) || (null == chapters)) {
			maxChapterNum = -1;
			return false;
		}

		titles = chapters;
		numbers = new int[chapters.length];
		int chapterIndex = -1;
		int firstDirectoryChapterPageIndex = -1;
		for (int i = 0; i < chapters.length; i++) {
			Chapter cpt = new Chapter();
			cpt.title = chapters[i];
			cpt.pageIndex = indexs[i];
			numbers[i] = cpt.pageIndex;
			cpt.index = i;
			if (cpt.isChapterBorder()) {
				chapterIndex++;
				firstDirectoryChapterPageIndex = cpt.pageIndex;
			}
			cpt.firstDirectoryChapterPageIndex = firstDirectoryChapterPageIndex;
			list.add(cpt);
		}
		maxChapterNum = chapters.length;

		return true;
	}

	protected void clear() {
		state = ChapterTreeState.UNKNOWN;
		list.clear();
		titles = null;
		numbers = null;
		curChapterIndex = 0;
		maxChapterNum = 0;
	}

	/**
	 * Get specified chapter index.
	 * 
	 * @param title
	 *            : chapter's title
	 * @return the index of the chapter title ,-1 returned the title not found
	 */
	protected int getChapterIndex(String title) {
		for (Chapter c : list) {
			if (c.title.indexOf(title) != -1) {
				return c.index;
			}
		}
		return -1;
	}

	/**
	 * 
	 * Get the next chapter's index from the current page's index. Note that
	 * next chapter means chapter 1 , chapter 2... , but it doesn't mean chapter
	 * 1.1. if the current chapter is 1.1, next chapter will return the index of
	 * chapter 2 but chapter 1.2.
	 * 
	 * @param pageIndex
	 *            : the page index of current page
	 * @return next chapter index .
	 */
	protected int getNextChapterIndex(int pageIndex) {
		int nextChapterIndex = 0;
		int size = list.size();
		Chapter c;
		
		for (int i = 0; i < size; i++) {
			c = list.get(i);
			// Logger.dLog(TAG, "c.pageIndex = " + c.pageIndex + "pageIndex = "
			// + pageIndex);
			if (c.pageIndex > pageIndex && c.isChapterBorder()) {
				nextChapterIndex = c.index;
				break;
			}
		}
		// Logger.dLog(TAG, "title next chapter index: " + nextChapterIndex);
		return nextChapterIndex;
	}

	/**
	 * 
	 * Get the previous chapter's index from the current page's index. Note that
	 * previous chapter means chapter 1 , chapter 2... , but it doesn't mean
	 * chapter 1.1. if the current chapter is 1.1, the previous chapter will
	 * return the index of chapter 0 but chapter 1.
	 * 
	 * @param pageIndex
	 *            : the page index of current page
	 * @return previous chapter index .
	 */
	protected int getPreviousChapterIndex(int pageIndex) {
		int previousChapterIndex = 0;
		int curChapterPageIndex = pageIndex;
		int size = list.size();
		Chapter c;
		
		for (int i = size - 1; i >= 0; i--) {
			c = list.get(i);
//			Logger.dLog(TAG, "c.pageIndex = " + c.pageIndex + "pageIndex = "
//					+ pageIndex + "isChapterBorder = " + c.isChapterBorder() + " title = " + c.title);
			if (c.pageIndex <= pageIndex && c.isChapterBorder()) {
				curChapterPageIndex = c.pageIndex;
				break;
			}
		}

		for (int i = size - 1; i >= 0; i--) {
			c = list.get(i);
//			Logger.dLog(TAG, "loop2 pageIndex = " + c.pageIndex
//					+ "curChapterPageIndex = " + curChapterPageIndex + " title = "
//					+ c.title);
			if (c.pageIndex < curChapterPageIndex && c.isChapterBorder()) {
				previousChapterIndex = c.index;
				break;
			}
		}

//		Logger.dLog(TAG, "getPreviousChapterIndex index = "
//				+ previousChapterIndex);

		return previousChapterIndex;
	}

	protected int getPageIndexForChapter(int index) {
		int curPageIndex = 0;
		for (Chapter chapter : list) {
			if (index == chapter.index) {
				curPageIndex = chapter.pageIndex;
			}
		}

		return curPageIndex;
	}

	public static class Chapter {
		public int index;
		public int firstDirectoryChapterPageIndex;
		public int pageIndex;
		public String title;

		public boolean isChapterBorder() {
			// Adobe 分为主章节和副章节，主章节标题以 "-" 开头作为标识
			// 当前只处理主章节的目录结构
			switch (mBookType) {
			case Book.BookType.PDF:
				return (title.startsWith("-") && !title.startsWith("--"));
			case Book.BookType.EPUB:
				return true;
			default:
				return true;
			}
		}
	}

	protected ArrayList<Chapter> getChapterList() {
		return list;
	}

	protected int[] getChapterPageNums() {
		return numbers;
	}

	public static enum ChapterTreeState {
		UNKNOWN, CURRENT_LOADED, LIST_LOADED, LIST_EMPTY
	}
}
