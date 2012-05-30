/**
 * @file       IMotionManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-4 下午05:28:59 
 */

package com.foxconn.ebook.reader.engine.util.motion;

import android.view.MotionEvent;

/**
 *
 */
public interface IMotionManager {
	public interface MOTION_TYPE {
		public final int MOTION_NONE = 0;
//		public final int MOTION_LEFT_DOWN = 0;
//		public final int MOTION_LEFT_UP = 1;
//		public final int MOTION_RIGHT_DOWN = 2;
//		public final int MOTION_RIGHT_UP = 3;
//		public final int MOTION_LEFT_MOVE_RIGHT = 4;
//		public final int MOTION_RIGHHT_MOVE_LEFT = 5;
//		public final int MOTION_CENTER_DOWN = 6;
//		public final int MOTION_CENTER_UP = 7;
		public final int FINGER_DOUBLE_TAP = 1;
		public final int FINGER_RELEASE_AFTER_LONG_PRESS = 2;
		public final int FINGER_SINGLE_TAP = 3;
		public final int FINGER_SINGLE_RELEASE = 4;
		public final int FINGER_MOVE_AFTER_LONG_PRESS = 5;
		public final int FINGER_PRESS = 6;
		public final int FINGER_MOVE = 7;
		public final int FINGER_LONG_PRESS = 8;
		
		// this member must be the size of the type
		public final int MOTION_TYPE_SIZE = 8;
	}
	
	public int doRunMode(MotionEvent event);
	
	public void registerMotionCall(int type, IMotionCall call);
	
	public void setRunMode(boolean run);
	
	public void setDoubleTap(boolean doubleTap);
}
