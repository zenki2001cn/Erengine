/*
 * JudgeByChapterList.java
 *
 * Version info
 *
 * Create time
 * 
 * Last modify time
 *
 * Copyright (c) 2010 FOXCONN Technology Group All rights reserved
 */
package com.foxconn.ebook.reader.engine.model.strategy;

import java.util.List;

import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.Chapter;
import com.foxconn.ebook.reader.engine.model.Page;

/**
 * 
 */
public class JudgeByChapterList implements IHasChapter {

	/*
	 * @see
	 * com.foxconn.ebook.reader.engine2.model.strategy.IHasChapter#hasNextChapter
	 * (com.foxconn.ebook.reader.engine2.model.Book)
	 */
	public boolean hasNextChapter(Book book) {
		if (null == book) {
			return false;
		}
		
		Page page = book.getCurPage();
		int index = page.getPageNum();
		List<Chapter> list = book.getChapterList();
		if (list != null) {
			if (index < list.get(list.size() - 1).firstDirectoryChapterPageIndex) {
				return true;
			}
		}

		return false;
	}

	/*
	 * @seecom.foxconn.ebook.reader.engine2.model.strategy.IHasChapter#
	 * hasPreviousChapter(com.foxconn.ebook.reader.engine2.model.Book)
	 */
	public boolean hasPreviousChapter(Book book) {
		if (null == book) {
			return false;
		}
		
		Page page = book.getCurPage();
		int index = page.getPageNum();
		List<Chapter> list = book.getChapterList();
		
//		Logger.dLog("!!!!!!!!!!!!!!!!!!!", "book.getCurChapterIndex() = "
//				+ book.getCurChapterIndex() + " book.getMaxChapterNum() = "
//				+ book.getMaxChapterNum() + " index = " + index + " pageIndex = " + list.get(0).pageIndex);
		
		if (list != null) {
			if (index > list.get(0).pageIndex) {
				return true;
			}
		}

		return false;
	}

}
