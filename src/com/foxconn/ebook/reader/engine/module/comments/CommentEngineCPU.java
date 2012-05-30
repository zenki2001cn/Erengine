/**
 * @file       CommentEngine.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-24 下午03:41:52 
 */

package com.foxconn.ebook.reader.engine.module.comments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.foxconn.ebook.reader.engine.util.Logger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public class CommentEngineCPU extends BaseCommentEngine {
	static private final String TAG = "CommentEngine";

	private Canvas mCanvas;
	private float mCurveEndX;
	private float mCurveEndY;
	private int mInvalidateExtraBorder = 10;
	private float mX, mY;
	private final Rect mInvalidRect = new Rect();
	private final Path mPath = new Path();

	protected CommentEngineCPU(View context, Rect rect) {
		super(context, rect);
		init();
	}

	protected void enterComment() {
	}

	protected void exitComment() {
	}
	
	protected void init() {
		super.init();
		
		initCanvas();
		
		Logger.vLog(TAG, "init");
	}

	private void initCanvas() {
		Bitmap bmp = getBitmap();
		if (!bmp.isMutable()) {
			Logger.dLog(TAG, "setCommentContent bmp is not mutable ");
			mCanvas.drawBitmap(bmp, new Matrix(), getPaint());
		} else {
			Logger.dLog(TAG, "setCommentContent bmp is mutable ");
			mCanvas = new Canvas(bmp);
		}
	}

	protected void setCommentContent(Bitmap bkbmp) {
		super.setCommentContent(bkbmp);

		initCanvas();
	}

	protected void resetContent() {
		super.resetContent();

		if (mPath != null) {
			mPath.reset();
		}
		
		initCanvas();
	}

	protected boolean saveComment(String path, ICommentHandlerCall call) {
		File file = null;
		String cmd;

		Logger.dLog(TAG, "saveComment path = " + path);
		try {
			file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			Bitmap bmp = getBitmap();
			FileOutputStream fos = new FileOutputStream(file);
			if (!bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
				Logger.eLog(TAG, "compress error");
				fos.close();
				throw new Exception();
			}
			fos.flush();
			fos.close();

			cmd = "chmod 777 " + path;
			try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e) {
				Logger.eLog(TAG, "exec chmod 777 error");
			}
		} catch (Exception e) {
			Logger.eLog(TAG, "io exception" + e);
			if (file != null) {
				file.delete();
			}
			return false;
		}
		
		if (call != null) {
			call.commentCall();
		}

		return true;
	}

	protected boolean deleteComment(String path, ICommentHandlerCall call) {
		File file = null;
		boolean delok = false;

		try {
			file = new File(path);
			if (file.isFile() && file.exists()) {
				delok = file.delete();
			}
		} catch (Exception e) {
			Logger.vLog(TAG, "deleteComment error" + e);
		}

		resetContent();
		
		if (call != null) {
			call.commentCall();
		}

		return delok;
	}

	protected void draw(Canvas canvas) {
		super.draw(canvas);

		Paint paint = getPaint();
		if (mPath != null) {
			canvas.drawPath(mPath, paint);
			mCanvas.drawPath(mPath, paint);
		}
	}

	private void refresh(Rect rect) {
		View view = getPaintView();
		
		if (view != null) {
			view.invalidate(rect);
		}
	}
	
	protected boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Logger.dLog(TAG, "ReaderWrapper down x = " + x + " y = " + y);
			touchDown(x, y);
			refresh();
			break;
		case MotionEvent.ACTION_MOVE:
			Logger.dLog(TAG, "ReaderWrapper move x = " + x + " y = " + y);
			Rect rect = touchMove(x, y);
			refresh(rect);
			break;
		case MotionEvent.ACTION_UP:
			Logger.dLog(TAG, "ReaderWrapper up x = " + x + " y = " + y);
			refresh();
			touchUp(x, y);
			break;
		}

		return true;
	}

	private void touchDown(float x, float y) {
		mCurveEndX = mX = x;
		mCurveEndY = mY = y;
		mPath.moveTo(mX, mY);
	}
	
	private Rect touchMove(float x, float y) {
		Rect areaToRefresh = null;

		final float previousX = mX;
		final float previousY = mY;

		final float dx = Math.abs(x - previousX);
		final float dy = Math.abs(y - previousY);

		areaToRefresh = mInvalidRect;

		// start with the curve end
		final int border = mInvalidateExtraBorder;
		areaToRefresh.set((int) mCurveEndX - border, (int) mCurveEndY - border,
				(int) mCurveEndX + border, (int) mCurveEndY + border);

		float cX = mCurveEndX = (x + previousX) / 2;
		float cY = mCurveEndY = (y + previousY) / 2;

		mPath.quadTo(previousX, previousY, cX, cY);

		// union with the control point of the new curve
		areaToRefresh.union((int) previousX - border, (int) previousY - border,
				(int) previousX + border, (int) previousY + border);

		// union with the end point of the new curve
		areaToRefresh.union((int) cX - border, (int) cY - border, (int) cX
				+ border, (int) cY + border);

		mX = x;
		mY = y;

		return areaToRefresh;
	}
	
	private void touchUp(float x, float y) {
		if (mPath != null) {
			mPath.reset();
		}
	}
}
