/**
 * @file       GestureBoard.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-25 下午12:14:35 
 */

package com.easyview.ebook.reader.engine.util.motion;

import java.util.ArrayList;
import java.util.List;

import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.motion.GestureObject.GestureDirection;

import android.graphics.PointF;
import android.graphics.RectF;

public class GestureBoard {
	static private final String TAG = "GestureBoard";

	private RectF mBoardRange;
	private List<PointF> mPointList;
	private RectF[] mRectList;
	private GestureObject mGesture;

	private final int BOARD_H_SPLIT = 3;
	private final int BOARD_V_SPLIT = 3;

	public GestureBoard(RectF rect, List<PointF> pl) {
		mBoardRange = rect;
		mPointList = pl;
		mGesture = new GestureObject();

		initBoard();
	}

	private void initBoard() {
		mRectList = new RectF[BOARD_H_SPLIT * BOARD_V_SPLIT];

		float left;
		float top;
		float right;
		float bottom;

		float leftT = mBoardRange.left;
		float topT = mBoardRange.top;
		float rightT = mBoardRange.right;
		float bottomT = mBoardRange.bottom;

//		Logger.dLog(TAG, " leftT = " + leftT + " topT = " + topT + " rightT = "
//				+ rightT + " bottomT = " + bottomT);

		float w = mBoardRange.width() / BOARD_H_SPLIT;
		float h = mBoardRange.height() / BOARD_V_SPLIT;

		for (int i = 0; i < BOARD_H_SPLIT; i++) {
			for (int j = 0; j < BOARD_V_SPLIT; j++) {
				left = leftT + i * w;
				top = topT + j * h;
				right = left + w;
				bottom = top + h;
//				Logger.dLog(TAG, "id = " + (j * BOARD_V_SPLIT + i) + " left = "
//						+ left + " top = " + top + " right = " + right
//						+ " bottom = " + bottom);
				
				mRectList[j * BOARD_V_SPLIT + i] = new RectF();
				mRectList[j * BOARD_V_SPLIT + i].set(left, top, right, bottom);
			}
		}
	}

	private int getDirection(PointF pt) {
		int direct = GestureDirection.DIRECT_UNKNOW;

//		Logger.dLog(TAG, "getDirection x= " + pt.x + " y= " + pt.y);
		
		if (mRectList[0].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_NE;
		} else if (mRectList[1].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_N;
		} else if (mRectList[2].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_NW;
		} else if (mRectList[3].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_E;
		} else if (mRectList[4].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_C;
		} else if (mRectList[5].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_W;
		} else if (mRectList[6].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_SE;
		} else if (mRectList[7].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_S;
		} else if (mRectList[8].contains(pt.x, pt.y)) {
			direct = GestureDirection.DIRECT_SW;
		}

		return direct;
	}

	private String getGestureType() {
		int lastDirect = GestureDirection.DIRECT_UNKNOW;
		int curDirect;
		StringBuilder builder = new StringBuilder();

		for (PointF pt : mPointList) {
			curDirect = getDirection(pt);
//			Logger.dLog(TAG, "getGestureType curDirect = " + curDirect);
			
			if ((curDirect == lastDirect)
					|| (GestureDirection.DIRECT_UNKNOW == curDirect)) {
				continue;
			} else {
				lastDirect = curDirect;
				builder.append(curDirect);
			}
		}

		return builder.toString();
	}

	public GestureObject getGesture() {
		mGesture.setGestureElement(getGestureType());

		return mGesture;
	}
}
