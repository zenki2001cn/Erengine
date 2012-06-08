/**
 * @file       MotionManager.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-4 下午05:28:42 
 */

package com.easyview.ebook.reader.engine.util.motion;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import com.easyview.ebook.reader.engine.util.Logger;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 *
 */
public class MotionManager implements IMotionManager {
	static private final String TAG = "MotionManager";

	private Context mContext;
	private View mAttachedView;
	private volatile ShortClickRunnable myPendingShortClickRunnable;
	private volatile LongClickRunnable myPendingLongClickRunnable;

	private boolean mRunMode = false;
	private boolean mSupportDoubleTap = false;
	private volatile boolean myLongClickPerformed;
	private volatile boolean myPendingPress;
	private volatile boolean myPendingDoubleTap;
	private int myPressedX, myPressedY;
	private boolean myScreenIsTouched;
	
	private final int MSG_LONG_PRESS = 1;

	private HashMap<String, IMotionCall> mMotionCallList = new HashMap<String, IMotionCall>();
	private Handler H = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LONG_PRESS:
				Logger.dLog(TAG, "handleMessage FINGER_LONG_PRESS ");
				doMotionCall(MOTION_TYPE.FINGER_LONG_PRESS, msg.arg1, msg.arg2);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public MotionManager(Context ctx, View attachedView) {
		mContext = ctx;
		mAttachedView = attachedView;

		init();
	}

	private void init() {
		for (int i = 0; i < MOTION_TYPE.MOTION_TYPE_SIZE + 1; i++) {
			mMotionCallList.put(String.valueOf(i), new IMotionCall() {
				@Override
				public void doMotion(int x, int y) {
					Logger.dLog(TAG, "doMotionCall motion  x = " + x + " y = "
							+ y);
				}
			});
		}
	}

	private class LongClickRunnable implements Runnable {
		public void run() {
			if (mAttachedView != null) {
				if (mAttachedView.performLongClick()) {
					myLongClickPerformed = true;
					Message msg = Message.obtain();
					msg.arg1 = mX;
					msg.arg2 = mY;
					msg.what = MSG_LONG_PRESS;
					H.sendMessage(msg);
				}
			}
		}
	}

	private void postLongClickRunnable() {
		// Logger.dLog(
		// TAG,
		// "postLongClickRunnable timeout = "
		// + ViewConfiguration.getLongPressTimeout());
		myLongClickPerformed = false;
		myPendingPress = false;
		if (myPendingLongClickRunnable == null) {
			myPendingLongClickRunnable = new LongClickRunnable();
		}
		if (mAttachedView != null) {
			mAttachedView.postDelayed(myPendingLongClickRunnable,
					2 * ViewConfiguration.getLongPressTimeout());
		}
	}

	private class ShortClickRunnable implements Runnable {
		public void run() {
			Logger.dLog(TAG, "ShortClickRunnable run");
			// mAttachedView.onFingerSingleTap(myPressedX, myPressedY);
			myPendingPress = false;
			myPendingShortClickRunnable = null;
		}
	}

	private int mCurMotionType;
	int mX;
	int mY;
	
	@Override
	public int doRunMode(MotionEvent event) {
		mX = (int) event.getX();
		mY = (int) event.getY();
		int action = event.getAction();
		// Logger.dLog(TAG, "action = " + action);

		switch (action) {
		case MotionEvent.ACTION_UP:
			Logger.eLog(TAG, "ACTION_UP ^^^^^^^^^^^^^ ");
			if (myPendingDoubleTap) {
				// view.onFingerDoubleTap(x, y);
				mCurMotionType = MOTION_TYPE.FINGER_DOUBLE_TAP;
				doMotionCall(mCurMotionType, mX, mY);
			}
			if (myLongClickPerformed) {
				// view.onFingerReleaseAfterLongPress(x, y);
				mCurMotionType = MOTION_TYPE.FINGER_RELEASE_AFTER_LONG_PRESS;
				doMotionCall(mCurMotionType, mX, mY);
			} else {
				if (myPendingLongClickRunnable != null) {
					if (mAttachedView != null) {
						mAttachedView
								.removeCallbacks(myPendingLongClickRunnable);
					}
					myPendingLongClickRunnable = null;
				}
				if (myPendingPress) {

					if (supportDoubleTap() && (mAttachedView != null)) {
						if (myPendingShortClickRunnable == null) {
							myPendingShortClickRunnable = new ShortClickRunnable();
						}
						mAttachedView.postDelayed(myPendingShortClickRunnable,
								ViewConfiguration.getDoubleTapTimeout());
					} else {
						// view.onFingerSingleTap(x, y);
						mCurMotionType = MOTION_TYPE.FINGER_SINGLE_TAP;
						doMotionCall(mCurMotionType, mX, mY);
					}
				} else {
					// view.onFingerRelease(x, y);
					mCurMotionType = MOTION_TYPE.FINGER_SINGLE_RELEASE;
					doMotionCall(mCurMotionType, mX, mY);
				}
			}
			myPendingDoubleTap = false;
			myPendingPress = false;
			myScreenIsTouched = false;
			break;
		case MotionEvent.ACTION_DOWN:
			if (myPendingShortClickRunnable != null) {
				Logger.eLog(TAG, "ACTION_DOWN DOUBLE VVVVVVVVVVVVV");
				if (mAttachedView != null) {
					mAttachedView.removeCallbacks(myPendingShortClickRunnable);
				}
				myPendingShortClickRunnable = null;
				myPendingDoubleTap = true;
			} else {
				Logger.eLog(TAG, "ACTION_DOWN PRESS VVVVVVVVVVVVV");
				postLongClickRunnable();
				myPendingPress = true;
			}
			myScreenIsTouched = true;
			myPressedX = mX;
			myPressedY = mY;
			break;
		case MotionEvent.ACTION_MOVE: {
			final int slop = ViewConfiguration.get(
					mContext.getApplicationContext()).getScaledTouchSlop();
//			Logger.eLog(TAG, "ACTION_MOVE >>>>>>>>>>>>>> slop = " + slop);
			final boolean isAMove = Math.abs(myPressedX - mX) > slop
					|| Math.abs(myPressedY - mY) > slop;
			if (isAMove) {
				myPendingDoubleTap = false;
			}
			if (myLongClickPerformed) {
				mCurMotionType = MOTION_TYPE.FINGER_MOVE_AFTER_LONG_PRESS;
				doMotionCall(mCurMotionType, mX, mY);
			} else {
				if (myPendingPress) {
					if (isAMove) {
						if (myPendingShortClickRunnable != null) {
							if (mAttachedView != null) {
								mAttachedView
										.removeCallbacks(myPendingShortClickRunnable);
							}
							myPendingShortClickRunnable = null;
						}
						if (myPendingLongClickRunnable != null) {
							if (mAttachedView != null) {
								mAttachedView
										.removeCallbacks(myPendingLongClickRunnable);
							}
						}
						
						mCurMotionType = MOTION_TYPE.FINGER_PRESS;
						doMotionCall(mCurMotionType, myPressedX, myPressedY);
						myPendingPress = false;
					}
				}
				if (!myPendingPress) {
					mCurMotionType = MOTION_TYPE.FINGER_MOVE;
					doMotionCall(mCurMotionType, mX, mY);
				}
			}
			break;
		}
		}

		Logger.eLog(TAG, "motion type =========== " + mCurMotionType);

		return mCurMotionType;
	}

	public void setDoubleTap(boolean doubleTap) {
		mSupportDoubleTap = doubleTap;
	}

	public boolean supportDoubleTap() {
		return mSupportDoubleTap;
	}

	private void doMotionCall(int type, int x, int y) {
		if (mRunMode && (mMotionCallList != null)
				&& mMotionCallList.containsKey(String.valueOf(type))) {
			IMotionCall call = mMotionCallList.get(String.valueOf(type));
			if (call != null) {
				call.doMotion(x, y);
			}
		}
	}

	public boolean getScreenTouched() {
		return myScreenIsTouched;
	}

	@Override
	public void registerMotionCall(int type, IMotionCall call) {
		mMotionCallList.put(String.valueOf(type), call);
	}

	@Override
	public void setRunMode(boolean run) {
		mRunMode = run;
	}

}
