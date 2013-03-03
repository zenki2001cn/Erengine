package com.easyview.ebook.reader.engine.util.provider;

import android.provider.BaseColumns;

/**
 * The Interface BookEmphasisColumns.
 */
public interface BookEmphasisColumns extends BaseColumns {
	
	/** The Constant BOOK_ID. */
	public static final String BOOK_ID = "book_id";
	
	/** The Constant COLOR. */
	public static final String COLOR = "color";
	
	/** The Constant START_CURSOR. */
	public static final String START_CURSOR = "start_cursor";
	
	/** The Constant END_CURSOR. */
	public static final String END_CURSOR = "end_cursor";
	
	/** The Constant START_X. */
	public static final String START_X = "start_x";
	
	/** The Constant START_Y. */
	public static final String START_Y = "start_y";
	
	/** The Constant END_X. */
	public static final String END_X = "end_x";
	
	/** The Constant END_Y. */
	public static final String END_Y = "end_y";
	
	/** The Constant LOCATION. */
	public static final String LOCATION = "location";
	
	/** The Constant CREATE_TIME. */
	public static final String CREATE_TIME = "create_time";
	
	/** The Constant SUMMARY. */
	public static final String SUMMARY = "summary";
	
	/** The Constant FONT_LEVEL. */
	public static final String FONT_LEVEL = "font_level";
	
	//SET DEFAULT SORT
	/** The Constant DEFAULT_SORT. */
	public static final String DEFAULT_SORT = "create_time DESC";

	/** The Constant ID_INDEX. */
	public static final int ID_INDEX = 0;
	
	/** The Constant BOOK_ID_INDEX. */
	public static final int BOOK_ID_INDEX = 1;
	
	/** The Constant COLOR_INDEX. */
	public static final int COLOR_INDEX = 2;
	
	/** The Constant START_CURSOR_INDEX. */
	public static final int START_CURSOR_INDEX = 3;
	
	/** The Constant END_CURSOR_INDEX. */
	public static final int END_CURSOR_INDEX = 4;
	
	/** The Constant START_X_INDEX. */
	public static final int START_X_INDEX = 5;
	
	/** The Constant START_Y_INDEX. */
	public static final int START_Y_INDEX = 6;
	
	/** The Constant END_X_INDEX. */
	public static final int END_X_INDEX = 7;
	
	/** The Constant END_Y_INDEX. */
	public static final int END_Y_INDEX = 8;
	
	/** The Constant LOCATION_INDEX. */
	public static final int LOCATION_INDEX = 9;
	
	/** The Constant CREATE_TIME_INDEX. */
	public static final int CREATE_TIME_INDEX = 10;
	
	/** The Constant SUMMARY_INDEX. */
	public static final int SUMMARY_INDEX = 11;
	
	/** The Constant FONT_LEVEL_INDEX. */
	public static final int FONT_LEVEL_INDEX = 12;
	
}
