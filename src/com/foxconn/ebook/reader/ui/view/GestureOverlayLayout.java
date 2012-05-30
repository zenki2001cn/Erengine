/**
 * @file       GestureOverlayView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-25 下午05:22:17 
 */

package com.foxconn.ebook.reader.ui.view;

import java.util.ArrayList;

import com.foxconn.ebook.reader.easyviewer.R;
import com.foxconn.ebook.reader.engine.util.Logger;
import com.foxconn.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.Toast;

public class GestureOverlayLayout extends EVBaseRelativeLayout {
	static private final String TAG = "GestureOverlayLayout";

	private GestureOverlayView mGestureView;
	private GestureLibrary mLibrary;
	
	private final String GESTURE_CLOSE = "close";
	private final String GESTURE_BOOKMARK = "bookmark";
	private final String GESTURE_NEXT_CHAPTER = "nc";
	private final String GESTURE_PRVIOUS_CHAPTER = "pc";
	private final String GESTURE_FIND = "find";
	private final String GESTURE_TOPIC = "topic";
	private final String GESTURE_JUMP = "jump";
	private final String GESTURE_5 = "5";
	private final String GESTURE_6 = "6";
	private final String GESTURE_7 = "7";
	private final String GESTURE_8 = "8";
	private final String GESTURE_9 = "9";

	public GestureOverlayLayout(Context context) {
		super(context);
	}

	public GestureOverlayLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GestureOverlayLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		initView();

		super.onFinishInflate();
	}

	private void initView() {
		

		mLibrary = GestureLibraries.fromRawResource(getContext(), R.raw.gestures);
		if (!mLibrary.load()) {
			Logger.eLog(TAG, "GestureLibraries load error");
			return;
		} else {
			Logger.eLog(TAG, "GestureLibraries load ok");
		}

//		mGestureView = (GestureOverlayView) findViewById(R.id.id_gestures);
		mGestureView.addOnGesturePerformedListener(mGPListener);
	}
	
	private boolean doGestureAction(String name) {
		boolean res = false;
		
		if (name.equals(GESTURE_FIND)) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_SEARCH);
			res = true;
		} else if (name.equals(GESTURE_CLOSE)) {
			getEasyViewer().exit();
			res = true;
		} else if (name.equals(GESTURE_NEXT_CHAPTER)) {
			getEasyViewer().chapterDown();
			res = true;
		} else if (name.equals(GESTURE_PRVIOUS_CHAPTER)) {
			getEasyViewer().chapterUp();
			res = true;
		} else if (name.equals(GESTURE_BOOKMARK)) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_BOOKMARK);
			res = true;
		} else if (name.equals(GESTURE_TOPIC)) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_TOPIC);
			res = true;
		} else if (name.equals(GESTURE_JUMP)) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_JUMP);
			res = true;
		} else if (name.equals(GESTURE_5)) {
			Toast.makeText(getContext(), GESTURE_5, Toast.LENGTH_SHORT).show();
			res = true;
		} else if (name.equals(GESTURE_6)) {
			Toast.makeText(getContext(), GESTURE_6, Toast.LENGTH_SHORT).show();
			res = true;
		} else if (name.equals(GESTURE_7)) {
			Toast.makeText(getContext(), GESTURE_7, Toast.LENGTH_SHORT).show();
			res = true;
		} else if (name.equals(GESTURE_8)) {
			Toast.makeText(getContext(), GESTURE_8, Toast.LENGTH_SHORT).show();
			res = true;
		} else if (name.equals(GESTURE_9)) {
			Toast.makeText(getContext(), GESTURE_9, Toast.LENGTH_SHORT).show();
			res = true;
		}
		
		return res;
	}

	private OnGesturePerformedListener mGPListener = new OnGesturePerformedListener() {

		@Override
		public void onGesturePerformed(GestureOverlayView overlay,
				Gesture gesture) {
			
			ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

			if (predictions.size() < 0) {
				return;
			}
			
			for (Prediction prediction : predictions) {
				Logger.eLog(TAG, "onGesturePerformed name = " + prediction.name);
				
				if (prediction.score > 1.0) {
					doGestureAction(prediction.name);
					break;
				}
			}
		}
	};

}
