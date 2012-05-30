/**
 * @file       MotionDirection.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-7 下午03:20:48 
 */

package com.foxconn.ebook.reader.engine.util.motion;


/*
 * | L/U |  U | R/U |
 * |  L  |  U |  R  |
 * |  L  |  C |  R  |
 * |  L  |  D |  R  |
 * | L/D |  D | R/D |
 */
public abstract class MotionDirection {
	public interface DirectPoint {
		public int OUTSIDE = -1;
		public int LEFT = 0;
		public int UP = 1;
		public int CENTER = 2;
		public int RIGHT = 3;
		public int DOWN = 4;
	}

	public interface MoveDirect {
		public int DIRECT_HOLD = 0;
		public int DIRECT_LEFT_TO_RIGHT = 1;
		public int DIRECT_RIGHT_TO_LEFT = 2;
		public int DIRECT_UP_TO_DOWN = 3;
		public int DIRECT_DOWN_TO_UP = 4;
		public int DIRECT_LEFT = 5;
		public int DIRECT_RIGHT = 6;
	}
	
	public interface IMotionDirectionPolicy {
		public void setStartDirection(boolean horizontal, int x, int y);
		public void setStopDirection(boolean horizontal, int x, int y);
	}

	public int mMoveStart = DirectPoint.OUTSIDE;
	public int mMoveEnd = DirectPoint.OUTSIDE;

	private IMotionDirectionPolicy mCall;

	public void setImotionDirectCall(IMotionDirectionPolicy call) {
		mCall = call;
	}
	
	public IMotionDirectionPolicy getImotionDirectCall() {
		return mCall;
	}

	protected void start(boolean direct, int x, int y) {
		if (mCall != null) {
			mCall.setStartDirection(direct, x, y);
		}
	}
	
	protected void stop(boolean direct, int x, int y) {
		if (mCall != null) {
			mCall.setStopDirection(direct, x, y);
		}
	}
	
	public void setMoveStart(int direct) {
		mMoveStart = direct;
	}

	public void setMoveEnd(int direct) {
		mMoveEnd = direct;
	}

	public int getMoveStart() {
		return mMoveStart;
	}

	public int getMoveEnd() {
		return mMoveEnd;
	}

	abstract public int getDirect();

	public void reset() {
		mMoveStart = DirectPoint.OUTSIDE;
		mMoveEnd = DirectPoint.OUTSIDE;
	}
}
