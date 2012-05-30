package com.foxconn.ebook.reader.engine.util.provider;

import android.net.Uri;

/**
 * The Class ERUris.
 */
public class ERUris {
	
	/** The Constant AUTHORITY. */
	public static final String AUTHORITY = "com.foxconn.ebook.reader.engine.util.provider";
	
	/** The Constant CONTENT_URI. */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	/** The Constant BOOK_CONTENT_URI. */
	public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/book");
	
	/** The Constant BOOKMARK_CONTENT_URI. */
	public static final Uri BOOKMARK_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/bookmark");
	
	/** The Constant BOOKEMPHASIS_CONTENT_URI. */
	public static final Uri BOOKEMPHASIS_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/bookemphasis");
	
	/** The Constant BOOKMARK_CONTENT_SP_URI. */
	public static final Uri BOOKMARK_CONTENT_SP_URI = Uri.parse("content://"
			+ AUTHORITY + "/bookmark_sp");
	
	/** The Constant BOOKEMPHASIS_CONTENT_SP_URI. */
	public static final Uri BOOKEMPHASIS_CONTENT_SP_URI = Uri.parse("content://"
			+ AUTHORITY + "/bookemphasis_sp");
}
