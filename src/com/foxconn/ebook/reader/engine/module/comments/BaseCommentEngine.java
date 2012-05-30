/**
 * @file       BaseCommentEngine.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-4-1 下午12:34:45 
 */

package com.foxconn.ebook.reader.engine.module.comments;

import com.foxconn.ebook.reader.engine.util.Logger;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public abstract class BaseCommentEngine {
	static private final String TAG = "BaseCommentEngine";
	private View mPaintView;
	private Paint mPaint;
	private Bitmap mBitmap;
	private int mViewRight = 0;
	private int mViewBottom = 0;
	private int mViewLeft = 0;
	private int mViewTop = 0;
	private Config mBitmapConfig = Config.ARGB_4444;

	private int paintStrokeWidth = 2;

	abstract protected void enterComment();

	abstract protected void exitComment();

	abstract protected boolean saveComment(String path, ICommentHandlerCall call);

	abstract protected boolean deleteComment(String path,
			ICommentHandlerCall call);

	protected BaseCommentEngine(View view, Rect rect) {
		mPaintView = view;

		if (rect != null) {
			mViewLeft = rect.left;
			mViewTop = rect.top;
			mViewRight = rect.right;
			mViewBottom = rect.bottom;
		} else {
			mViewLeft = 0;
			mViewTop = 0;
			mViewRight = 560;
			mViewBottom = 720;
		}
		
		Logger.vLog(TAG, "BaseCommentEngine mViewLeft = " + mViewLeft
				+ " mViewRight = " + mViewRight + " mViewTop = " + mViewTop
				+ " mViewBottom = " + mViewBottom + " view = " + mPaintView);
	}

	protected void init() {
		initPaint();
		initBitmap();
	}

	protected void free() {
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	protected void setCommentContent(Bitmap bkbmp) {
		// resetContent();

		if (null == bkbmp) {
			Logger.dLog(TAG, "setCommentContent bkbmp = " + bkbmp);
			return;
		}

		mBitmap = bkbmp.copy(mBitmapConfig, true);
	}

	protected void resetContent() {
		if (mBitmap != null) {
			mBitmap.recycle();
			initBitmap();
		}
	}

	protected void refresh() {
		if (mPaintView != null) {
			mPaintView.invalidate();
		}
	}

	protected void draw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, new Matrix(), mPaint);
		}
	}

	protected boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	private void initPaint() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(paintStrokeWidth);
	}

	private void initBitmap() {
		int width = mViewRight - mViewLeft;
		int height = mViewBottom - mViewTop;

		mBitmap = Bitmap.createBitmap(width, height, mBitmapConfig);
		mBitmap.eraseColor(Color.TRANSPARENT);
	}

	protected void setBitmap(Bitmap bmp) {
		mBitmap = bmp;
	}

	protected Bitmap getBitmap() {
		return mBitmap;
	}

	protected View getPaintView() {
		return mPaintView;
	}

	protected Paint getPaint() {
		return mPaint;
	}

	protected void setViewRect(int left, int right, int top, int bottom) {
		mViewLeft = left;
		mViewRight = right;
		mViewTop = top;
		mViewBottom = bottom;
	}

	protected void setViewRect(Rect rect) {
		mViewLeft = rect.left;
		mViewRight = rect.right;
		mViewTop = rect.top;
		mViewBottom = rect.bottom;
	}

	protected int getViewLeft() {
		return mViewLeft;
	}

	protected int getViewRight() {
		return mViewRight;
	}

	protected int getViewTop() {
		return mViewTop;
	}

	protected int getViewBottom() {
		return mViewBottom;
	}

	protected Config getBitmapConfig() {
		return mBitmapConfig;
	}
}
