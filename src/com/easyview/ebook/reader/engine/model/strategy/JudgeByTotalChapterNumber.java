/*
 * JudgeByTotalChapterNumber.java
 *
 * Version info
 *
 * Create time
 * 
 * Last modify time
 *
 * Copyright (c) 2010 easyview Technology Group All rights reserved
 */
package com.easyview.ebook.reader.engine.model.strategy;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 *
 */
public class JudgeByTotalChapterNumber implements IHasChapter {

	/*
	 * @seecom.easyview.ebook.reader.engine2.model.strategy.IHasChapter#
	 * hasPreviousChapter(com.easyview.ebook.reader.engine2.model.Book)
	 */
	public boolean hasPreviousChapter(Book book) {
//		Logger.dLog("!!!!!!!!!!!!!!!!!!!", "book.getCurChapterIndex() = "
//				+ book.getCurChapterIndex() + " book.getMaxChapterNum() = "
//				+ book.getMaxChapterNum());
		if (book != null) {
			if (book.getCurChapterIndex() > 0) {
				return true;
			}
		}

		return false;
	}

	/*
	 * @see
	 * com.easyview.ebook.reader.engine2.model.strategy.IHasChapter#hasNextChapter
	 * (com.easyview.ebook.reader.engine2.model.Book)
	 */
	public boolean hasNextChapter(Book book) {
//		Logger.dLog("!!!!!!!!!!!!!!!!!!!", "book.getCurChapterIndex() = "
//				+ book.getCurChapterIndex() + " book.getMaxChapterNum() = "
//				+ book.getMaxChapterNum());
		if (book != null) {
			if (book.getCurChapterIndex() < book.getMaxChapterNum() - 1) {
				return true;
			}
		}

		return false;
	}

}
