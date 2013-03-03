/**
 * @file       RenderView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-4 下午10:39:25 
 */

package com.easyview.ebook.reader.ui.view;

import java.util.ArrayList;

import com.easyview.ebook.reader.easyviewer.EasyViewerApplication;
import com.easyview.ebook.reader.engine.core.IActionCall;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.motion.GestureManager;
import com.easyview.ebook.reader.engine.util.motion.GestureObject;
import com.easyview.ebook.reader.engine.util.motion.HorizontalDirection;
import com.easyview.ebook.reader.engine.util.motion.IGestureManager;
import com.easyview.ebook.reader.engine.util.motion.IMotionCall;
import com.easyview.ebook.reader.engine.util.motion.IMotionManager;
import com.easyview.ebook.reader.engine.util.motion.MotionDirection;
import com.easyview.ebook.reader.engine.util.motion.MotionManager;
import com.easyview.ebook.reader.engine.util.motion.VerticalDirection;
import com.easyview.ebook.reader.engine.util.motion.GestureObject.GestureType;
import com.easyview.ebook.reader.engine.util.motion.IMotionManager.MOTION_TYPE;
import com.easyview.ebook.reader.engine.util.motion.MotionDirection.IMotionDirectionPolicy;
import com.easyview.ebook.reader.engine.util.motion.MotionDirection.MoveDirect;
import com.easyview.ebook.reader.ui.controller.EasyViewer;
import com.easyview.ebook.reader.ui.controller.IControlCenterService;
import com.easyview.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;
import com.easyview.ebook.reader.easyviewer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class RenderView extends ImageView {
	static private final String TAG = "RenderView";
	private Context mContext;
	private IEngineService mIee;
	private IControlCenterService mIcc;
	private EasyViewer mEV;
	private EasyViewerApplication mApp;
	private ActionDialog mMessageDlg;
	private IMotionManager mIMotionManager;
	private IGestureManager mIGestureManager;
	ArrayList<Rect> mRectList;
	private HorizontalDirection mHoriDirection;
	private VerticalDirection mVerDirection;
	private int mMotionStartY = 0;
	private final int DISTANCE_Y = 20;

	public RenderView(Context context) {
		super(context);

		init(context);
	}

	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init(context);
	}

	public RenderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init(context);
	}

	private void init(Context ctx) {
		mContext = ctx;

		if (mContext instanceof EasyViewer) {
			mEV = (EasyViewer) mContext;
			mApp = (EasyViewerApplication) mEV.getApplication();
			if (null == mIee) {
				mIee = mApp.getErengineService();
			}

			initMotion();
			initGesture();
		} else {
			Logger.eLog(TAG, "get EasyViewer context failed");
		}

		mMessageDlg = new ActionDialog(mContext);

		// 通过HorizontalDirection和VerticalDirection进行手势判断
		mHoriDirection = new HorizontalDirection();
		mVerDirection = new VerticalDirection();
		mHoriDirection.setImotionDirectCall(mDirectionCall);
		mVerDirection.setImotionDirectCall(mDirectionCall);
	}

	private void initRect() {
		int w = getWidth();
		int h = getHeight();

		int[] lefts = { 0, w / 3, w * 2 / 3 };
		int[] rights = { w / 3, w * 2 / 3, w };

		mRectList = new ArrayList<Rect>();
		for (int i = 0; i < 3; i++) {
			mRectList.add(new Rect(lefts[i], 0, rights[i], h));
		}
	}

	private void initMotion() {
		if (null == mIMotionManager) {
			mIMotionManager = new MotionManager(mContext, this);
		}

		mIMotionManager.setRunMode(true);
		mIMotionManager.setDoubleTap(true);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_DOUBLE_TAP,
				imFingerDoubleTap);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_PRESS,
				imFingerPress);
		mIMotionManager.registerMotionCall(
				MOTION_TYPE.FINGER_RELEASE_AFTER_LONG_PRESS,
				imFingerReleaseAfterLongPress);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_SINGLE_TAP,
				imFingerSingleTap);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_SINGLE_RELEASE,
				imFingerSingleRelease);
		mIMotionManager.registerMotionCall(
				MOTION_TYPE.FINGER_MOVE_AFTER_LONG_PRESS,
				imFingerMoveAfterLongPress);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_MOVE,
				imFingerMove);
		mIMotionManager.registerMotionCall(MOTION_TYPE.FINGER_LONG_PRESS,
				imFingerLongPress);
	}

	private void initGesture() {
		if (null == mIGestureManager) {
			mIGestureManager = new GestureManager();
		}
	}

	@Override
	protected void onFinishInflate() {
		((View) RenderView.this).setOnLongClickListener(mLongClickListener);

		super.onFinishInflate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null == mRectList) {
			initRect();
		}

		super.onDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();

		// Logger.dLog(TAG, "onTouchEvent x = " + x + " y = " + y);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mRectList.get(0).contains(x, y)) {
				mEV.pageUp();
			} else if (mRectList.get(2).contains(x, y)) {
				mEV.pageDown();
			}
			
//			mIee.getAction().onTouchDown(x, y);
			
			// 九格手勢判斷
			// mIGestureManager.onStart(event);

//			if (mEV != null) {
//				mEV.touchLink(x, y);
//			}
			
			break;
		case MotionEvent.ACTION_MOVE:
//			mIee.getAction().onTouchMove(x, y);
			
			// 九格手勢判斷
			// mIGestureManager.onMove(event);
			break;
		case MotionEvent.ACTION_UP:
//			mIee.getAction().onTouchUp(x, y);
			
			// 九格手勢判斷
			// mIGestureManager.onStop(event);
			// doGesutreAction();
			break;

		default:
			break;
		}

		// 通过HorizontalDirection和VerticalDirection进行手势判断
//		mIMotionManager.doRunMode(event);

		return true;
	}

	private void doGesutreAction() {
		GestureObject go = mIGestureManager.getGesture();
		if (null == go) {
			Logger.eLog(TAG, "doGesutreAction go = null");
			return;
		}

		int type = go.getGestureType();
		Logger.eLog(TAG, "doGesutreAction type = " + type);
		switch (type) {
		case GestureType.LEFT_TO_RIGHT: {
			Logger.dLog(TAG, "doGesutreAction = LEFT_TO_RIGHT");

			if (null == mEV) {
				return;
			}

			mEV.pageUp();
		}
			break;
		case GestureType.RIGHT_TO_LEFT: {
			Logger.dLog(TAG, "doGesutreAction = RIGHT_TO_LEFT");

			if (null == mEV) {
				return;
			}

			mEV.pageDown();
		}
			break;

		case GestureType.DOWN_TO_UP: {
			if (null == mEV) {
				return;
			}

			mEV.zoomIn();
		}
			break;

		case GestureType.UP_TO_DOWN: {
			if (null == mEV) {
				return;
			}

			mEV.zoomOut();
		}
			break;

		default:
			break;
		}
	}

	private IMotionDirectionPolicy mDirectionCall = new IMotionDirectionPolicy() {
		@Override
		public void setStartDirection(boolean horizontal, int x, int y) {
			if (horizontal) {
				if (mRectList.get(0).contains(x, y)) {
					Logger.dLog(TAG, "fingerPress left");
					mHoriDirection
							.setMoveStart(MotionDirection.DirectPoint.LEFT);
				} else if (mRectList.get(2).contains(x, y)) {
					Logger.dLog(TAG, "fingerPress right");
					mHoriDirection
							.setMoveStart(MotionDirection.DirectPoint.RIGHT);
				} else {
					Logger.dLog(TAG, "fingerPress center");
					mHoriDirection
							.setMoveStart(MotionDirection.DirectPoint.CENTER);
				}
			} else {
				mMotionStartY = y;
				mVerDirection.setMoveStart(MotionDirection.DirectPoint.CENTER);
			}
		}

		@Override
		public void setStopDirection(boolean horizontal, int x, int y) {
			if (horizontal) {
				if (mRectList.get(0).contains(x, y)) {
					Logger.dLog(TAG, "fingerPress left");
					mHoriDirection.setMoveEnd(MotionDirection.DirectPoint.LEFT);
				} else if (mRectList.get(2).contains(x, y)) {
					Logger.dLog(TAG, "fingerPress right");
					mHoriDirection
							.setMoveEnd(MotionDirection.DirectPoint.RIGHT);
				} else {
					Logger.dLog(TAG, "fingerPress center");
					mHoriDirection
							.setMoveEnd(MotionDirection.DirectPoint.CENTER);
				}
			} else {
				Logger.dLog(TAG, "y = " + y + " startY = " + mMotionStartY);
				int distance = Math.abs(y - mMotionStartY);
				if ((y > mMotionStartY) && (distance > DISTANCE_Y)) {
					mVerDirection.setMoveEnd(MotionDirection.DirectPoint.UP);
				} else if ((y < mMotionStartY) && (distance > DISTANCE_Y)) {
					mVerDirection.setMoveEnd(MotionDirection.DirectPoint.DOWN);
				} else {
					mVerDirection
							.setMoveEnd(MotionDirection.DirectPoint.CENTER);
				}
			}
		}
	};

	private void showControlCenterDlg() {
		if (null == mIcc) {
			mIcc = mApp.getControlCenterService();
		}

		mIcc.runCommand(COMMAND_TYPE.CMD_SHOW_CONTROL_CENTER);
	}

	private OnLongClickListener mLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {

			return true;
		}
	};

	private IMotionCall imFingerLongPress = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.eLog(TAG, "imFingerLongPress");

			// 通过HorizontalDirection和VerticalDirection进行手势判断
			// mVerDirection.start(x, y);
			//
			// mMessageDlg.setMessage(mContext.getResources().getString(R.string.zoom_tip));
			// mMessageDlg.setDialogType(ActionDialog.TYPE_TOAST);
			// mMessageDlg.showDialog();
		}
	};

	private IMotionCall imFingerMove = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerMove");

			// 通过HorizontalDirection和VerticalDirection进行手势判断
			if (null == mEV) {
				return;
			}

			mHoriDirection.stop(x, y);
			int direct = mHoriDirection.getDirect();
			if (MoveDirect.DIRECT_LEFT_TO_RIGHT == direct) {
				mEV.pageUp();
				mHoriDirection.reset();
			} else if (MoveDirect.DIRECT_RIGHT_TO_LEFT == direct) {
				mEV.pageDown();
				mHoriDirection.reset();
			}
		}
	};

	private IMotionCall imFingerMoveAfterLongPress = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerMoveAfterLongPress");

			// 通过HorizontalDirection和VerticalDirection进行手势判断
//			if (null == mEV) {
//				return;
//			}
//
//			mVerDirection.stop(x, y);
//			int direct = mVerDirection.getDirect();
//			if (MoveDirect.DIRECT_DOWN_TO_UP == direct) {
//				mEV.zoomIn();
//				mVerDirection.reset();
//			} else if (MoveDirect.DIRECT_UP_TO_DOWN == direct) {
//				mEV.zoomOut();
//				mVerDirection.reset();
//			}
		}
	};

	private IMotionCall imFingerSingleRelease = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerSingleRelease");
		}
	};

	private IMotionCall imFingerSingleTap = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerSingleTap");
		}
	};

	private IMotionCall imFingerReleaseAfterLongPress = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerReleaseAfterLongPress");
		}
	};

	private IMotionCall imFingerDoubleTap = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			Logger.dLog(TAG, "finger imFingerDoubleTap");
			showControlCenterDlg();
		}
	};

	private IMotionCall imFingerPress = new IMotionCall() {
		@Override
		public void doMotion(int x, int y) {
			// 通过HorizontalDirection和VerticalDirection进行手势判断
			mHoriDirection.start(x, y);
		}
	};

}
