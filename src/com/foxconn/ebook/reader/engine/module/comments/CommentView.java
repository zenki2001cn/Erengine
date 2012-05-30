/**
 * @file       CommentView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-23 下午04:12:51 
 */

package com.foxconn.ebook.reader.engine.module.comments;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public class CommentView extends View {
	static private final String TAG = "CommentView";

	private Context mContext;
	
	public CommentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
	}
	
//	public void popCommentTextDialog() {
//		commentTextDlg = new EPDSearchWidget(mContext, false);
//		commentTextDlg.setEPDSearchListener(commentTextListener);
//		commentTextDlg.setTitle(this.getResources().getString(
//				R.string.r_publicWidget_search));
//		commentTextDlg.setConfirmButtonText(this.getResources().getString(
//				R.string.search));
//		commentTextDlg.show( ((AdobeReader)mContext).getWindowManager(), Gravity.BOTTOM);
//	}
//	
//	private EPDSearchListener commentTextListener = new EPDSearchListener() {
//
//		public void onConfirmButtonClick(String inputText) {
//			commentTextDlg.dismiss();
//			if (TextUtils.isEmpty(inputText.trim())) {
//				Log.w(TAG, "inputText: is null or empty!");
//				return;
//			}
//			
//			String commentText = inputText.trim();
//			Log.d(TAG, "comment Text = " + commentText);
//		}
//	};
//
//	public boolean onKeyLongPress(int keyCode, android.view.KeyEvent event) {
//		Log.d(TAG, "onKeyLongPress code = " + keyCode + " event = " + event);
//		
//		return super.onKeyLongPress(keyCode, event);
//	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return CommentHandler.onTouchEvent(event);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		CommentHandler.onDraw(canvas);
	}
}
