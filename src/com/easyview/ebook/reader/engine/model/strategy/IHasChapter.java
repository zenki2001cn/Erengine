/*
 * IHasChapter.java
 *
 * Version info
 *
 * Create time
 * 
 * Last modify time
 *
 * Copyright (c) 2010 FOXCONN Technology Group All rights reserved
 */
package com.easyview.ebook.reader.engine.model.strategy;

import com.easyview.ebook.reader.engine.model.Book;

/**
 *
 */
public interface IHasChapter {
    boolean hasPreviousChapter(Book book);

    boolean hasNextChapter(Book book);
}
