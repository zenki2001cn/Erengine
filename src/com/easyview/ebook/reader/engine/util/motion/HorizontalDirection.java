/**
 * @file       HorizontalDirection.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-7 下午03:22:19 
 */

package com.easyview.ebook.reader.engine.util.motion;

public class HorizontalDirection extends MotionDirection {

	public void start(int x, int y) {
		start(true, x, y);
	}
	
	public void stop(int x, int y) {
		stop(true, x, y);
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
			direct = MoveDirect.DIRECT_RIGHT_TO_LEFT;
		} else if (moveStart < moveEnd) {
			direct = MoveDirect.DIRECT_LEFT_TO_RIGHT;
		} else {
			direct = MoveDirect.DIRECT_HOLD;
		}

		return direct;
	}
}
