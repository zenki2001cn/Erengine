/**
 * @file       GestureManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-25 上午10:44:49 
 */

package com.easyview.ebook.reader.engine.util.motion;

import java.util.ArrayList;

import com.easyview.ebook.reader.engine.util.Logger;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

public class GestureManager implements IGestureManager {
	static private final String TAG = "GestureManager";

	private ArrayList<PointF> mPointList;
	private RectF mMoveRange;
	private float mMinX;
	private float mMinY;
	private float mMaxX;
	private float mMaxY;
	private GestureBoard mGestureBoard;
	private final float mMinWidth = 100;
	private final float mMinHeight = 100;

	public GestureManager() {
		init();
	}

	private void init() {
		mPointList = new ArrayList<PointF>();
		mMoveRange = new RectF();
	}

	private void adjust(float x, float y) {
		if (x < mMinX) {
			mMinX = x;
		} else if (x > mMaxX) {
			mMaxX = x;
		}

		if (y < mMinY) {
			mMinY = y;
		} else if (y > mMaxY) {
			mMaxY = y;
		}
	}

	private void addPoint(float x, float y) {
		PointF p = new PointF();
		p.x = x;
		p.y = y;
		mPointList.add(p);
	}

	private boolean createBoard() {
		if ((mMaxX - mMinX) < mMinWidth) {
			mMaxX = mMinX + mMinWidth;
		}

		if ((mMaxY - mMinY) < mMinHeight) {
			mMaxY = mMinY + mMinHeight;
		}

		mMoveRange = new RectF(mMinX, mMinY, mMaxX, mMaxY);
		mGestureBoard = new GestureBoard(mMoveRange, mPointList);

		return true;
	}

	@Override
	public void onStart(MotionEvent motion) {
		mMinX = mMaxX = motion.getX();
		mMinY = mMaxY = motion.getY();
		mPointList.clear();
		addPoint(motion.getX(), motion.getY());

//		Logger.dLog(TAG,
//				" onStart x = " + motion.getX() + " y = " + motion.getY());
	}

	@Override
	public void onStop(MotionEvent motion) {
//		Logger.dLog(TAG,
//				" onStop x = " + motion.getX() + " y = " + motion.getY());

		addPoint(motion.getX(), motion.getY());
		adjust(motion.getX(), motion.getY());
		createBoard();
	}

	@Override
	public void onMove(MotionEvent motion) {
//		Logger.dLog(TAG,
//				" onMove x = " + motion.getX() + " y = " + motion.getY());

		addPoint(motion.getX(), motion.getY());
		adjust(motion.getX(), motion.getY());
	}

	@Override
	public GestureObject getGesture() {
		return (null == mGestureBoard) ? null : mGestureBoard.getGesture();
	}
}
