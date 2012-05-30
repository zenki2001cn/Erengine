package com.foxconn.ebook.reader.engine.util.provider;

import android.provider.BaseColumns;

/**
 * The Interface BookmarkColumns.
 */
public interface BookmarkColumns extends BaseColumns {
	
	/** The Constant BOOK_ID. */
	public static final String BOOK_ID = "book_id";
	
	/** The Constant LOCATION. */
	public static final String LOCATION = "location";
	
	/** The Constant CREATE_TIME. */
	public static final String CREATE_TIME = "create_time";
	
	/** The Constant PAGE_NUM. */
	public static final String PAGE_NUM = "page_num";
	
	/** The Constant SUMMARY. */
	public static final String SUMMARY = "summary";
	
	//SET DEFAULT SORT
	/** The Constant DEFAULT_SORT. */
	public static final String DEFAULT_SORT = "create_time DESC";

	/** The Constant ID_INDEX. */
	public static final int ID_INDEX = 0;
	
	/** The Constant BOOK_ID_INDEX. */
	public static final int BOOK_ID_INDEX = 1;
	
	/** The Constant LOCATION_INDEX. */
	public static final int LOCATION_INDEX = 2;
	
	/** The Constant CREATE_TIME_INDEX. */
	public static final int CREATE_TIME_INDEX = 3;
	
	/** The Constant PAGE_NUM_INDEX. */
	public static final int PAGE_NUM_INDEX = 4;
	
	/** The Constant SUMMARY_INDEX. */
	public static final int SUMMARY_INDEX = 5;
}
