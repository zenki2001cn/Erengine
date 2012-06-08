package com.easyview.ebook.reader.engine.util.provider;

import com.easyview.ebook.reader.engine.core.IDatabaseService;
import com.easyview.ebook.reader.engine.util.Logger;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class ERSqliteProvider extends ContentProvider {
	private static final String TAG = "ERSqliteProvider";
	private static final String BOOKINFO_TABLE_NAME = IDatabaseService.BOOKINFO_TABLE_NAME;
	private static final String BOOKMARK_TABLE_NAME = IDatabaseService.BOOKMARK_TABLE_NAME;
	private static final String BOOKEMPHASIS_TABLE_NAME = IDatabaseService.BOOKEMPHASIS_TABLE_NAME;
	private static final String DATABASE_NAME = IDatabaseService.DATABASE_NAME;
	public static final int DATABASE_VERSION = 1;
	private static final int BOOKINFO_BASE = 0;
	private static final int BOOKINFO = BOOKINFO_BASE;
	private static final int BOOKINFO_ID = BOOKINFO_BASE + 1;
	private static final int BOOKMARK_BASE = 0x1000;
	private static final int BOOKMARK = BOOKMARK_BASE;
	private static final int BOOKMARK_ID = BOOKMARK_BASE + 1;
	private static final int BOOKEMPHASIS_BASE = 0x2000;
	private static final int BOOKEMPHASIS = BOOKEMPHASIS_BASE;
	private static final int BOOKEMPHASIS_ID = BOOKEMPHASIS_BASE + 1;
	private static final int BOOKMARK_SP_BASE = 0x3000;
	private static final int BOOKMARK_SP = BOOKMARK_SP_BASE;
	private static final int BOOKEMPHASIS_SP_BASE = 0x4000;
	private static final int BOOKEMPHASIS_SP = BOOKEMPHASIS_SP_BASE;
	private SQLiteDatabase mSQLiteDatabase;
	private static final String[] TABLE_NAMES = { BOOKINFO_TABLE_NAME,
			BOOKMARK_TABLE_NAME, BOOKEMPHASIS_TABLE_NAME };
	private static final int BASE_SHIFT = 12;
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final String ID_EQUALS = BaseColumns._ID + "=?";

	static {
		UriMatcher matcher = URI_MATCHER;
		matcher.addURI(ERUris.AUTHORITY, "book", BOOKINFO);
		matcher.addURI(ERUris.AUTHORITY, "book/#", BOOKINFO_ID);
		matcher.addURI(ERUris.AUTHORITY, "bookmark", BOOKMARK);
		matcher.addURI(ERUris.AUTHORITY, "bookmark/#", BOOKMARK_ID);
		matcher.addURI(ERUris.AUTHORITY, "bookemphasis", BOOKEMPHASIS);
		matcher.addURI(ERUris.AUTHORITY, "bookemphasis/#", BOOKEMPHASIS_ID);
		matcher.addURI(ERUris.AUTHORITY, "bookmark_sp", BOOKMARK_SP);
		matcher.addURI(ERUris.AUTHORITY, "bookemphasis_sp", BOOKEMPHASIS_SP);
	}

	private void createBookTable(SQLiteDatabase db) {
		String createString;
		String bookColumns;

		bookColumns = BookColumns.FILE_PATH + " TEXT, "
				+ BookColumns.FILE_NAME + " TEXT, "
				+ BookColumns.FILE_TYPE + " TEXT ,"
				+ BookColumns.LAST_LOCATION + " TEXT, "
				+ BookColumns.LAST_PAGE_NUM + " INTEGER, "
				+ BookColumns.TOTAL_PAGE_NUM + " INTEGER, "
				+ BookColumns.LAST_ACCESS_TIME + " INTEGER, "
				+ BookColumns.META_TITLE + " TEXT, "
				+ BookColumns.META_AUTHOR + " TEXT, "
				+ BookColumns.META_PUBLISHER + " TEXT, "
				+ BookColumns.META_ENCODING + " TEXT, "
				+ BookColumns.META_LANGUAGE + " TEXT, "
				+ BookColumns.USER_NAME + " TEXT, "
				+ BookColumns.PASSWORD + " TEXT, "
				+ BookColumns.VIEW_WIDTH + " INTEGER, "
				+ BookColumns.VIEW_HEIGHT + " INTEGER, " 
				+ BookColumns.FONT_LEVEL + " INTEGER, "
				+ BookColumns.FILE_SIZE + " INTEGER " + ");";

		createString = " (" + BookColumns._ID
				+ " integer primary key autoincrement, " + bookColumns;
		db.execSQL("create table " + BOOKINFO_TABLE_NAME + createString);

		// set index
		String indexColumns[] = { BookColumns.FILE_PATH,
				BookColumns.FILE_NAME, BookColumns.META_AUTHOR };

		// add index to book table
		for (String columnName : indexColumns) {
			db.execSQL(createIndex(BOOKINFO_TABLE_NAME, columnName));
		}

		// add trigger to book table
		// when book delete, in bookMark table,bookMark.book_id = book._id this
		// record is delete
		
		try {
			db.execSQL("CREATE TRIGGER delete_book_table_trigger AFTER DELETE ON "
					+ BOOKINFO_TABLE_NAME + " for each row BEGIN "
					+ " DELETE FROM " + BOOKMARK_TABLE_NAME
					+ " WHERE book_id = OLD._id;" + " END;");

			db.execSQL("CREATE TRIGGER delete_book_table_trigger2 AFTER DELETE ON "
					+ BOOKINFO_TABLE_NAME + " for each row BEGIN "
					+ " DELETE FROM " + BOOKEMPHASIS_TABLE_NAME
					+ " WHERE book_id = OLD._id;" + " END;");
		} catch (SQLException e) {
			Logger.eLog(TAG, "create trigger exception = " + e);
		}
	}

	private void createBookmarkTable(SQLiteDatabase db) {
		String createString;
		String bookmarkColumns = BookmarkColumns.BOOK_ID
				+ " INTEGER NOT NULL, " + BookmarkColumns.LOCATION
				+ " TEXT NOT NULL, " + BookmarkColumns.CREATE_TIME
				+ " INTEGER, " + BookmarkColumns.PAGE_NUM + " INTEGER, "
				+ BookmarkColumns.SUMMARY + " TEXT " + ");";

		createString = " (" + BookmarkColumns._ID
				+ " integer primary key autoincrement, " + bookmarkColumns;
		db.execSQL("create table " + BOOKMARK_TABLE_NAME + createString);

		String indexColumns[] = { BookmarkColumns.LOCATION };
		for (String columnName : indexColumns) {
			db.execSQL(createIndex(BOOKMARK_TABLE_NAME, columnName));
		}
	}

	private void createEmphasisTables(SQLiteDatabase db) {
		String createString;
		String commentsColumns = BookEmphasisColumns.BOOK_ID
				+ " INTEGER NOT NULL, " + BookEmphasisColumns.COLOR
				+ " INTEGER, " + BookEmphasisColumns.START_CURSOR + " TEXT, "
				+ BookEmphasisColumns.END_CURSOR + " TEXT, "
				+ BookEmphasisColumns.START_X + " INTEGER, "
				+ BookEmphasisColumns.START_Y + " INTEGER, "
				+ BookEmphasisColumns.END_X + " INTEGER, "
				+ BookEmphasisColumns.END_Y + " INTEGER, "
				+ BookEmphasisColumns.LOCATION + " TEXT NOT NULL, "
				+ BookEmphasisColumns.CREATE_TIME + " INTEGER, "
				+ BookEmphasisColumns.SUMMARY + " TEXT, "
				+ BookEmphasisColumns.FONT_LEVEL + " INTEGER " + ");";

		createString = " (" + BookEmphasisColumns._ID
				+ " integer primary key autoincrement, " + commentsColumns;
		db.execSQL("create table " + BOOKEMPHASIS_TABLE_NAME + createString);

		// set index columns in BookMark
		// String indexColumns[] = { BookmarkColumns.LOCATION };
		// add index columns to BookMark
		// for (String columnName : indexColumns) {
		// db.execSQL(createIndex(BOOKMARK_TABLE_NAME, columnName));
		// }
	}

	private void createAllTables(SQLiteDatabase db) {
		this.createBookTable(db);
		this.createBookmarkTable(db);
		this.createEmphasisTables(db);
	}

	private String createIndex(String tableName, String columnName) {
		return "create index " + tableName.toLowerCase() + '_' + columnName
				+ " on " + tableName + " (" + columnName + ");";
	}

	private void dropTable(SQLiteDatabase db, String tableName, int oldVersion,
			int newVersion) {
		try {
			db.execSQL("drop table " + tableName);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	private void dropBookTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		this.dropTable(db, BOOKINFO_TABLE_NAME, oldVersion, newVersion);
	}

	private void dropBookMarkTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		this.dropTable(db, BOOKMARK_TABLE_NAME, oldVersion, newVersion);
	}

	private void dropBookEmphasisTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		this.dropTable(db, BOOKEMPHASIS_TABLE_NAME, oldVersion, newVersion);
	}

	private void dropAllTables(SQLiteDatabase db, int oldVersion, int newVersion) {
		this.dropBookMarkTable(db, oldVersion, newVersion);
		this.dropBookEmphasisTable(db, oldVersion, newVersion);
		this.dropBookTable(db, oldVersion, newVersion);
	}

	private SQLiteDatabase getDatabaseByDefaultPath(Context context) {
		if (null != this.mSQLiteDatabase) {
			return mSQLiteDatabase;
		}
		
		createDatabase(context);

		return this.mSQLiteDatabase;
	}
	
	private boolean createDatabase(Context context) {
		SQLiteDatabase sqliteDatabase = null;
		DatabaseHelper helper;
		
		helper = new DatabaseHelper(context, DATABASE_NAME, DATABASE_VERSION);
		sqliteDatabase = helper.getWritableDatabase();
		this.mSQLiteDatabase = sqliteDatabase;
		
		return (null == this.mSQLiteDatabase) ? false : true;
	}

	public String getType(Uri uri) {
		int match = URI_MATCHER.match(uri);
		switch (match) {
		case BOOKINFO_ID:
			return "vnd.android.cursor.item/book";
		case BOOKINFO:
			return "vnd.android.cursor.dir/book";
		case BOOKMARK_ID:
			return "vnd.android.cursor.item/bookmark";
		case BOOKMARK:
			return "vnd.android.cursor.dir/bookmark";
		case BOOKEMPHASIS_ID:
			return "vnd.android.cursor.dir/bookemphasis";
		case BOOKEMPHASIS:
			return "vnd.android.cursor.dir/bookemphasis";
		default:
			throw new IllegalArgumentException(TAG + ":Unknown URI " + uri);
		}
	}

	@Override
	public boolean onCreate() {
		createDatabase(getContext());
		
		return true;
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {

		int match = URI_MATCHER.match(uri);
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		int i = 0;
		int table = match >> BASE_SHIFT;
		try {
			db.beginTransaction();
			for (ContentValues value : values) {
				switch (match) {
				case BOOKINFO:
				case BOOKMARK:
				case BOOKEMPHASIS:
					db.insert(TABLE_NAMES[table], "", value);
					break;
				default:
					throw new IllegalArgumentException(TAG + ".insert()"
							+ ":Unknown URI" + uri);
				}

				i++;
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			i = -1;
		} finally {
			db.endTransaction();
		}
		return i;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int match = URI_MATCHER.match(uri);
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		int table = match >> BASE_SHIFT;
		String id = "0";
		int result = -1;

		switch (match) {
		case BOOKINFO_ID:
		case BOOKMARK_ID:
		case BOOKEMPHASIS_ID:
			try {
				db.beginTransaction();
				id = uri.getPathSegments().get(1);
				result = db.delete(TABLE_NAMES[table],
						whereWithId(id, selection), selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;

		case BOOKINFO:
		case BOOKMARK:
		case BOOKEMPHASIS:
			try {
				db.beginTransaction();
				result = db
						.delete(TABLE_NAMES[table], selection, selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return result;
	}

	public Uri insert(Uri uri, ContentValues values) {
		int match = URI_MATCHER.match(uri);
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		Uri resultUri = null;
		int table = match >> BASE_SHIFT;

		switch (match) {
		case BOOKINFO:
			final long id = db.insert(TABLE_NAMES[table], "", values);
			resultUri = ContentUris.withAppendedId(uri, id);
			break;
		case BOOKMARK:
			final long bookMarkid = db.insert(TABLE_NAMES[table], "", values);
			resultUri = ContentUris.withAppendedId(uri, bookMarkid);
			break;
		case BOOKEMPHASIS:
			final long cid = db.insert(TABLE_NAMES[table], "", values);
			resultUri = ContentUris.withAppendedId(uri, cid);
			break;
		default:
			throw new IllegalArgumentException(TAG + ".insert()"
					+ ":Unknown URI" + uri);
		}

		return resultUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = URI_MATCHER.match(uri);
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		int table = match >> BASE_SHIFT;
		String id;
		Cursor c = null;

		switch (match) {
		case BOOKINFO:
		case BOOKMARK:
		case BOOKEMPHASIS:
			c = db.query(TABLE_NAMES[table], projection, selection,
					selectionArgs, null, null, sortOrder);
			break;

		case BOOKINFO_ID:
		case BOOKMARK_ID:
		case BOOKEMPHASIS_ID:
			id = uri.getPathSegments().get(1);
			c = db.query(TABLE_NAMES[table], projection,
					whereWithId(id, selection), selectionArgs, null, null,
					sortOrder);
			break;
			
		case BOOKMARK_SP:
		case BOOKEMPHASIS_SP:
			c = db.rawQuery(selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException(TAG + ".query()"
					+ ":Unknown URI" + uri);
		}

		if ((c != null) && !isTemporary()) {
			c.setNotificationUri(getContext().getContentResolver(),
					ERUris.CONTENT_URI);
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int match = URI_MATCHER.match(uri);
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		int table = match >> BASE_SHIFT;
		int result = 0;

		switch (match) {
		case BOOKINFO:
		case BOOKMARK:
		case BOOKEMPHASIS:
			try {
				db.beginTransaction();
				result = db.update(TABLE_NAMES[table], values, selection,
						selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;

		case BOOKINFO_ID:
		case BOOKMARK_ID:
		case BOOKEMPHASIS_ID:
			try {
				db.beginTransaction();
				String id = uri.getPathSegments().get(1);
				result = db.update(TABLE_NAMES[table], values,
						whereWithId(id, selection), selectionArgs);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			break;

		default:
			throw new IllegalArgumentException(TAG + ".update()"
					+ ":Unknown URI" + uri);
		}

		return result;
	}
	
	public void executeSQL(String sql) {
		Context context = getContext();
		final SQLiteDatabase db = getDatabaseByDefaultPath(context);
		SQLiteStatement statement = db.compileStatement(sql);
		if (statement != null) {
			statement.execute();
		}
	}

	private String whereWith(String where, String selection) {
		if (selection == null) {
			return where;
		}

		StringBuilder sb = new StringBuilder(where);
		sb.append(" AND (");
		sb.append(selection);
		sb.append(')');

		return sb.toString();
	}

	private String whereWithId(String id, String selection) {
		StringBuilder sb = new StringBuilder(256);
		sb.append("_id=");
		sb.append(id);

		if (selection != null) {
			sb.append(" AND (");
			sb.append(selection);
			sb.append(')');
		}

		return sb.toString();
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		public void onCreate(SQLiteDatabase db) {
			ERSqliteProvider.this.createAllTables(db);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "=============== upgrade database" + "oldVersion is:"
					+ oldVersion + "newVersion is:" + newVersion
					+ "DATABASE_VERSION is:" + DATABASE_VERSION);
			upgradeBookTable(db, oldVersion, newVersion);
			upgradeBookMarkTable(db, oldVersion, newVersion);
		}
	}

	private static final String BOOKINFO_TABLE_NAME_OBSOLETE = "book_Obsolete";
	private static final String BOOKMARK_TABLE_NAME_OBSOLETE = "bookmark_Obsolete";
	private static final String BOOKEMPHASIS_TABLE_NAME_OBSOLETE = "bookmark_Obsolete";

	private void upgradeBookMarkTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		switch (newVersion) {
		case 2:
			break;
		default:
			break;
		}
	}

	private void upgradeBookTable(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		switch (newVersion) {
		case 2:
			break;
		default:
			break;
		}
	}
}
