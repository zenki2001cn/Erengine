/**
 * @file       CommentManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-4-6 下午03:15:18 
 */

package com.foxconn.ebook.reader.engine.module;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import com.foxconn.ebook.reader.engine.core.EngineCode;
import com.foxconn.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.Page;
import com.foxconn.ebook.reader.engine.module.comments.CommentHandler;
import com.foxconn.ebook.reader.engine.module.comments.ICommentHandlerCall;
import com.foxconn.ebook.reader.engine.util.Logger;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public class Comments {
	static private final String TAG = "Comments";
	private File mCommentDirFile;
	private Book mOpenedBook;
	private Context mContext;
	private View mCommentView;
	private Rect mViewRect;
	private int mType;
	private EngineCode mEngineError;

	public Comments(Context context, View commentView, Rect viewRect, int type,
			Book book) {
		mContext = context;
		mOpenedBook = book;
		mCommentView = commentView;
		mViewRect = viewRect;
		mType = type;
		mEngineError = EngineCode.getInstance();
	}

	public void initComment() {
		String cmd;
		File homeDir = mContext.getDir(CommentHandler.COMMENT_DIR,
				Context.MODE_WORLD_WRITEABLE);
		String commentsDir = homeDir.getAbsolutePath() + File.separator
				+ mOpenedBook.getBookName();

		try {
			Logger.dLog(TAG, "initCommentDir commentsDir = " + commentsDir);
			mCommentDirFile = new File(commentsDir);

			if (!mCommentDirFile.exists()) {
				if (!mCommentDirFile.mkdirs()) {
					Logger.eLog(TAG, "initCommentDir failed");
				}
			}

			cmd = "chmod 777 " + homeDir.getAbsolutePath();
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				Logger.vLog(
						TAG,
						"exec chmod 777 error, path = "
								+ homeDir.getAbsolutePath());
			}

			cmd = "chmod 777 " + commentsDir;
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				Logger.vLog(TAG, "exec chmod 777 error, path = " + commentsDir);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.eLog(TAG, "initCommentDir error" + e);
			mEngineError
					.setLastCode(EngineMsgCode.ENGINE_ERROR_COMMENT_INIT_FAILED);
			return;
		}

		CommentHandler.initCommentHandler(mCommentView, mViewRect, mType, null);
	}

	public void enterComment(ICommentHandlerCall enterCall) {
		CommentHandler.enterComment(enterCall);
	}

	public void exitComment(ICommentHandlerCall exitCall,
			ICommentHandlerCall reloadCall) {
		CommentHandler.exitComment(exitCall);
		loadComment(reloadCall);
	}

	public boolean loadComment(ICommentHandlerCall refreshCall) {
		boolean loadOk = CommentHandler.loadComment(mContext, mOpenedBook);
		CommentHandler.refresh(refreshCall);

		return loadOk;
	}

	public boolean saveComment(final ICommentHandlerCall saveCall) {
		boolean loadOk = false;
		Page curPage = mOpenedBook.getCurPage();
		ICommentHandlerCall call = new ICommentHandlerCall() {
			@Override
			public void commentCall() {
				exitComment(null, null);
				saveCall.commentCall();
			}
		};
		
		if (curPage != null) {
			String path = mCommentDirFile.getAbsolutePath() + File.separator
					+ mOpenedBook.getTitle() + "_" + curPage.getLocation()
					+ CommentHandler.COMMENT_EXT;
			loadOk = CommentHandler.saveComment(mContext, mOpenedBook, path,
					call);
		}


		return loadOk;
	}

	public boolean deleteComment(final ICommentHandlerCall delCall) {
		boolean loadOk = false;
		Page curPage = mOpenedBook.getCurPage();
		ICommentHandlerCall call = new ICommentHandlerCall() {
			@Override
			public void commentCall() {
				exitComment(null, null);
				delCall.commentCall();
			}
		};
		
		if (curPage != null) {
			String path = mCommentDirFile.getAbsolutePath() + File.separator
					+ mOpenedBook.getTitle() + "_" + curPage.getLocation()
					+ CommentHandler.COMMENT_EXT;
			loadOk = CommentHandler.deleteComment(mContext, mOpenedBook, path,
					call);
		}

		return loadOk;
	}

	public void free() {
		exitComment(null, null);

		CommentHandler.free();
		
		if (mEngineError != null) {
			mEngineError.free();
			mEngineError = null;
		}
	}
}
