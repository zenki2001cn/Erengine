/**
 * @file       VerticalDirection.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-7 下午03:25:02 
 */

package com.foxconn.ebook.reader.engine.util.motion;


public class VerticalDirection extends MotionDirection {

	public void start(int x, int y) {
		super.start(false, x, y);
	}

	public void stop(int x, int y) {
		super.stop(false, x, y);
	}

	@Override
	public int getDirect() {
		int direct = MoveDirect.DIRECT_HOLD;
		int moveStart = getMoveStart();
		int moveEnd = getMoveEnd();

		if ((DirectPoint.OUTSIDE == moveStart)
				|| (DirectPoint.OUTSIDE == moveEnd)) {
			return direct;
		}

		if (moveStart > moveEnd) {
			direct = MoveDirect.DIRECT_UP_TO_DOWN;
		} else if (moveStart < moveEnd) {
			direct = MoveDirect.DIRECT_DOWN_TO_UP;
		} else {
			direct = MoveDirect.DIRECT_HOLD;
		}

		return direct;
	}
}
