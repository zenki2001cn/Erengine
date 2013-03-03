/**
 * @file       GestureObject.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-25 上午10:47:05 
 */

package com.easyview.ebook.reader.engine.util.motion;

import com.easyview.ebook.reader.engine.util.Logger;

public class GestureObject {
	static private final String TAG = "GestureObject";
	private String mGestureElement;
	
	public interface GestureDirection {
		public final int DIRECT_UNKNOW = 0;
		public final int DIRECT_NE = 1;
		public final int DIRECT_N = 2;
		public final int DIRECT_NW = 3;
		public final int DIRECT_E = 4;
		public final int DIRECT_C = 5;
		public final int DIRECT_W = 6;
		public final int DIRECT_SE = 7;
		public final int DIRECT_S = 8;
		public final int DIRECT_SW = 9;
	}
	
	public interface GestureElement {
		public final String LEFT_TO_RIGHT_A = "123";
		public final String LEFT_TO_RIGHT_B = "12";
		public final String LEFT_TO_RIGHT_C = "23";
		public final String LEFT_TO_RIGHT_D = "13";
		
		public final String RIGHT_TO_LEFT_A = "321";
		public final String RIGHT_TO_LEFT_B = "32";
		public final String RIGHT_TO_LEFT_C = "21";
		public final String RIGHT_TO_LEFT_D = "31";
		
		public final String DOWN_TO_UP_A = "741";
		public final String DOWN_TO_UP_B = "74";
		public final String DOWN_TO_UP_C = "41";
		public final String DOWN_TO_UP_D = "71";
		
		public final String UP_TO_DOWN_A = "147";
		public final String UP_TO_DOWN_B = "14";
		public final String UP_TO_DOWN_C = "47";
		public final String UP_TO_DOWN_D = "17";
	}
	
	public interface GestureType {
		public final int UNKNOW_GESTURE = 0;
		public final int LEFT_TO_RIGHT = 1;
		public final int RIGHT_TO_LEFT = 2;
		public final int DOWN_TO_UP = 3;
		public final int UP_TO_DOWN = 4;
	}
	
	public GestureObject() {
		
	}
	
	public void setGestureElement(String element) {
//		Logger.dLog(TAG, "setGestureElement = " + element);
		mGestureElement = element;
	}
	
	public int getGestureType() {
		if (mGestureElement.equals(GestureElement.LEFT_TO_RIGHT_A) ||
				mGestureElement.equals(GestureElement.LEFT_TO_RIGHT_B) ||
				mGestureElement.equals(GestureElement.LEFT_TO_RIGHT_C) ||
				mGestureElement.equals(GestureElement.LEFT_TO_RIGHT_D)) {
			return GestureType.LEFT_TO_RIGHT;
		} else if (mGestureElement.equals(GestureElement.RIGHT_TO_LEFT_A) ||
				mGestureElement.equals(GestureElement.RIGHT_TO_LEFT_B) ||
				mGestureElement.equals(GestureElement.RIGHT_TO_LEFT_C) ||
				mGestureElement.equals(GestureElement.RIGHT_TO_LEFT_D)) {
			return GestureType.RIGHT_TO_LEFT;
		} else if (mGestureElement.equals(GestureElement.DOWN_TO_UP_A) ||
				mGestureElement.equals(GestureElement.DOWN_TO_UP_B) ||
				mGestureElement.equals(GestureElement.DOWN_TO_UP_C) ||
				mGestureElement.equals(GestureElement.DOWN_TO_UP_D)) {
			return GestureType.DOWN_TO_UP;
		} else if (mGestureElement.equals(GestureElement.UP_TO_DOWN_A) ||
				mGestureElement.equals(GestureElement.UP_TO_DOWN_B) ||
				mGestureElement.equals(GestureElement.UP_TO_DOWN_C) ||
				mGestureElement.equals(GestureElement.UP_TO_DOWN_D)) {
			return GestureType.UP_TO_DOWN;
		} else {
			return GestureType.UNKNOW_GESTURE;
		}
	}
}
