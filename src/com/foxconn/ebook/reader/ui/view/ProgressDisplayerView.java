/**
 * @file       ProgressDisplayerView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-5-30 上午10:05:18 
 */

package com.foxconn.ebook.reader.ui.view;

import org.geometerplus.fbreader.fbreader.ActionCode;

import com.foxconn.ebook.reader.engine.util.Logger;
import com.foxconn.ebook.reader.ui.controller.IProgressControllerCallback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 *
 */
public class ProgressDisplayerView extends View {
	static private final String TAG = "ProgressDisplayerView";
	
	private Paint mPaint;
	private float mProgress = 0;
	private RectF mRectDraw;
	private int mWidth = 0;
	private int mHeight = 0;
	private IProgressControllerCallback mControlCallback = null;
	
	private final float mRectLeft = 0;
	private final float mRectTop = 15;
	private final float mRectRight = 450;
	private final float mRectBottom = 25;
	
	private final int mColorA = 128;
	private final int mColorR = 77;
	private final int mColorG = 77;
	private final int mColorB = 77;
	
	public ProgressDisplayerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initPaint();
	}

	public ProgressDisplayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initPaint();
	}

	public ProgressDisplayerView(Context context) {
		super(context);
		
		initPaint();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawRect(mRectDraw, mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			gotoPage(event);
			break;
		case MotionEvent.ACTION_UP:
			// ek1 can't repos ACTION_UP
//			gotoPage(event);
			break;
		case MotionEvent.ACTION_MOVE:
//			movePage(event);
			break;
			
		default:
			break;
		}
		
		return super.onTouchEvent(event);
	}
	
	public void update(float percent) {
		mWidth = getWidth();
		mHeight = getHeight();
		
		mProgress = ((float) mWidth * percent);
		mRectDraw.set(mRectLeft, mRectTop, mProgress, mRectBottom);
		
		Logger.dLog(TAG, "mProgress = " + mProgress + " percent = " + percent);
		
		invalidate();
	}
	
	public void setProgressControllerCallback(IProgressControllerCallback callback) {
		mControlCallback = callback;
	}
	
	private void movePage(MotionEvent event) {
		float px = event.getX();
		float percent = px / (float) mWidth;
		
		Logger.dLog(TAG, "px = " + px + " percent = " + percent);
		
	}
	
	private void gotoPage(MotionEvent event) {
		float px = event.getX();
		float percent = px / (float) mWidth;
		
		Logger.dLog(TAG, "px = " + px + " percent = " + percent);
		
		if (mControlCallback != null) {
			mControlCallback.gotoPage(percent);
		}
	}
	
	private void initPaint() {
		if (null == mPaint) {
			mPaint = new Paint();
			mPaint.setColor(Color.argb(mColorA, mColorR, mColorG, mColorB));
		}
		
		if (null == mRectDraw) {
			mRectDraw = new RectF(mRectLeft, mRectTop, mProgress, mRectBottom);
		}
	}
}
