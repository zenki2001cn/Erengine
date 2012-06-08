/*
 * BookMarkHandler.java
 * 
 * Version info
 * 
 * Create time
 * 
 * Last modify time
 * 
 * Copyright (c) 2010 FOXCONN Technology Group All rights reserved
 */
package com.easyview.ebook.reader.engine.module.comments;

import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.util.Logger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**

 * BookCommentsHandler
 * <p> * ----------------------------------------------
 * BookCommentsHandler
 * <p> * ----------------------------------------------
 * BookCommentsHandler
 * <p> * ---------------------------------------------- * BookCommentsHandler
 * <p>
 */
/**
 * description
 * 
 * @author zenki
 */
public class CommentHandler {
	private static final String TAG = "BookCommentsHandler";
	private static boolean mCommentMode = false;
	private static BaseCommentEngine mCommentEngine;
	private static CommentModel mCommentModel;
	private static EngineCode mEngineError;

	static public String COMMENT_DIR = "comments";
	static public String COMMENT_EXT = ".png";

	static public class CommentType {
		static public int TYPE_EPD_MODE = 0;
		static public int TYPE_CPU_MODE = 1;
	}

	public static void initCommentHandler(View view, Rect viewRect, int mode,
			ICommentHandlerCall call) {
		Logger.dLog(TAG, "init comment");

		mEngineError = EngineCode.getInstance();

		if (mode == CommentType.TYPE_EPD_MODE) {
			mCommentEngine = new CommentEngineEPD(view, viewRect);
			Logger.vLog(TAG, "initCommentHandler EPD mode");
		} else if (mode == CommentType.TYPE_CPU_MODE) {
			mCommentEngine = new CommentEngineCPU(view, viewRect);
			Logger.vLog(TAG, "initCommentHandler CPU mode");
		}

		if (call != null) {
			call.commentCall();
		}
	}

	public static void enterComment(ICommentHandlerCall call) {
		Logger.dLog(TAG, "enter comment");
		mCommentMode = true;

		if (mCommentEngine != null) {
			mCommentEngine.enterComment();
		}

		if (call != null) {
			call.commentCall();
		}
	}

	public static void exitComment(ICommentHandlerCall call) {
		Logger.dLog(TAG, "exit comment");
		mCommentMode = false;

		if (mCommentEngine != null) {
			mCommentEngine.exitComment();
		}

		if (call != null) {
			call.commentCall();
		}
	}

	public static boolean getCommentMode() {
		return mCommentMode;
	}

	public static boolean saveComment(final Context context, final Book book,
			final String commentPath, final ICommentHandlerCall call) {
		// 保存数据库
		// 保存图片文件
		// AdobeReader.whetherMenuViewClick = false;
		//
		// final Page curPage = book.getCurPage();
		// ReadingManager.getInstance().submitTask(new ReadingTask() {
		// @Override
		// public void doInBackground() {
		// ReadingWorker.getInstance().getSummary(curPage);
		// }
		//
		// @Override
		// public void onPostExcuted() {
		// ContentValues values = new ContentValues();
		// int id = (int) book.getId();
		// String location = book.getCurPage().getLocation();
		// int curPageNumber = curPage.getNumber();
		// String summary = curPage.getSummary();
		//
		// values.put(BookCommentsColumns.BOOK_ID, id);
		// values.put(BookCommentsColumns.PAGE_NUMBER, curPageNumber);
		// values.put(BookCommentsColumns.XPOINT, 0);
		// values.put(BookCommentsColumns.YPOINT, 0);
		// values.put(BookCommentsColumns.LOCATION, location);
		// values.put(BookCommentsColumns.CREATED,
		// System.currentTimeMillis());
		// values.put(BookCommentsColumns.SUMMARY, summary);
		// if (book.getTotalPageNum() > 0) {
		// values.put(BookCommentsColumns.PROGRESS,
		// (curPageNumber + 1) * 100 / book.getTotalPageNum());
		// }
		// values.put(BookCommentsColumns.COMMENT_PATH, commentPath);
		//
		// mCommentModel = new CommentModel();
		// mCommentModel.setCommentPath(commentPath);
		// mCommentModel.setCommentLocation(location);
		//
		// try {
		// ContentResolver resolver = context.getContentResolver();
		// resolver.delete(EK1Uris.BOOK_COMMENTS_CONTENT_URI,
		// BookCommentsColumns.BOOK_ID + "=? and "
		// + BookCommentsColumns.LOCATION + "=?",
		// new String[] { String.valueOf(id), location });
		//
		// resolver.insert(EK1Uris.BOOK_COMMENTS_CONTENT_URI, values);
		//
		// mCommentEngine.saveComment(commentPath, call);
		// mCommentEngine.refresh();
		// } catch (Exception e) {
		// e.printStackTrace();
		// Log.e(TAG, "saveComment error");
		// }
		//
		// AdobeReader.whetherMenuViewClick = true;
		// mCommentMode = false;
		// }
		// });

		mEngineError
				.setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_SAVE_FAILED);

		return false;
	}

	public static boolean deleteComment(Context context, Book book,
			String path, ICommentHandlerCall call) {
		// 删除数据库信息
		// 删除图片文件
		// int id = (int) book.getId();
		// String location = book.getCurPage().getLocation();
		//
		// try {
		// ContentResolver resolver = context.getContentResolver();
		// resolver.delete(EK1Uris.BOOK_COMMENTS_CONTENT_URI,
		// BookCommentsColumns.BOOK_ID + "=? and "
		// + BookCommentsColumns.LOCATION + "=?",
		// new String[] { String.valueOf(id), location });
		//
		// mCommentEngine.deleteComment(path, call);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		mEngineError
				.setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_DELETE_FAILED);

		return false;
	}

	public static int clearComment(Context context) {
		// int result = context.getContentResolver().delete(
		// EK1Uris.BOOK_COMMENTS_CONTENT_URI, null, null);
		// return result;
		return 0;
	}

	public static boolean loadComment(Context context, Book book) {
		// 读取数据库信息，构建CommentModel对象
		// 通过refresh刷新数据
		// int id = (int) book.getId();
		// String location = book.getCurPage().getLocation();
		// Log.e(TAG, "loadCOmment id = " + id + " location = " + location);
		// if (id < 0 || TextUtils.isEmpty(location)) {
		// Log.w(TAG, "error: getbookmark is faild.");
		// return false;
		// }
		// Cursor cursor = null;
		// try {
		// cursor = context.getContentResolver().query(
		// EK1Uris.BOOK_COMMENTS_CONTENT_URI,
		// new String[] { BookCommentsColumns.COMMENT_PATH,
		// BookCommentsColumns.LOCATION },
		// BookCommentsColumns.BOOK_ID + "=? and "
		// + BookCommentsColumns.LOCATION + "=?",
		// new String[] { String.valueOf(id), location }, null);
		// if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst())
		// {
		// Log.w(TAG, "loadComment success");
		// mCommentModel = new CommentModel();
		// mCommentModel.setCommentPath(cursor.getString(0));
		// mCommentModel.setCommentLocation(cursor.getString(1));
		// } else {
		// Log.w(TAG, "loadComment empty");
		// mCommentModel = null;
		// }
		// } finally {
		// if (cursor != null) {
		// cursor.close();
		// }
		// }

		mEngineError
				.setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_LOAD_FAILED);

		return hasComment();
	}

	public static boolean hasComment() {
		if (mCommentModel != null) {
			return true;
		} else {
			return false;
		}
	}

	public static void refresh(ICommentHandlerCall call) {
		if (mCommentModel != null) {
			Bitmap bitmap = BitmapFactory.decodeFile(mCommentModel
					.getCommentPath());
			if (bitmap != null) {
				Logger.dLog(TAG, "refresh step load bitmap success");
				mCommentEngine.setCommentContent(bitmap);
				mCommentEngine.refresh();
			} else {
				Logger.dLog(TAG, "refresh step load bitmap failed");
				mCommentEngine.resetContent();
				mCommentEngine.refresh();
			}
		} else {
			Logger.dLog(TAG, "refresh step no comment");
			mCommentEngine.resetContent();
			mCommentEngine.refresh();
		}

		if (call != null) {
			call.commentCall();
		}
	}
	
	public static void free() {
		if (mEngineError != null) {
			mEngineError.free();
			mEngineError = null;
		}
	}

	public static CommentModel getCommentModel() {
		return mCommentModel;
	}

	public static boolean onTouchEvent(MotionEvent event) {
		if (getCommentMode()) {
			if (mCommentEngine != null) {
				return mCommentEngine.onTouchEvent(event);
			}
		}

		return false;
	}

	protected static void onDraw(Canvas canvas) {
		if (mCommentEngine != null) {
			mCommentEngine.draw(canvas);
		}
	}
}
