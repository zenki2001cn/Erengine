/**
 * @file       IGestureManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-25 上午10:46:16 
 */

package com.foxconn.ebook.reader.engine.util.motion;

import android.view.MotionEvent;

public interface IGestureManager {
	public void onStart(MotionEvent motion);
	public void onStop(MotionEvent motion);
	public void onMove(MotionEvent motion);
	public GestureObject getGesture();
}
