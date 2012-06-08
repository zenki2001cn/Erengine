/**
 * @file       CommentEngine.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-24 下午03:41:52 
 */

package com.foxconn.ebook.reader.engine.module.comments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

//import com.foxconn.ebook.EPD_Library;
//import com.foxconn.ebook.EPDScreenApi;
import com.foxconn.ebook.reader.engine.util.Logger;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public class CommentEngineEPD extends BaseCommentEngine{
	static private final String TAG = "CommentEngine";

	private int DRAW_END_X = 0;
	private int DRAW_END_Y = 0;
	private int DRAW_START_X = 0;
	private int DRAW_START_Y = 0;

//	private EPDScreenApi epdScreen;

	protected CommentEngineEPD(View context, Rect rect) {
		super(context, rect);
		
		init();
	}

	protected void init() {
		super.init();

//		epdScreen = new EPDScreenApi();
	}

	protected void free() {
		super.free();
	}

	private void getEpdRect() {
		DRAW_START_X = getViewLeft();
		DRAW_START_Y = getViewTop();
		DRAW_END_X = getViewRight();
		DRAW_END_Y = getViewBottom();
	}

	private void initEPD() {
		Logger.dLog(TAG, "initEPD");

		getEpdRect();
//		EPD_Library.epd_handwrite_update(1, 2, DRAW_START_X, DRAW_START_Y, DRAW_END_X, DRAW_END_Y);
//		epdScreen.LockScreenUpdateMode(1, 20, 1, DRAW_START_X, DRAW_START_Y, DRAW_END_X, DRAW_END_Y, 1);
	}

	private void freeEPD() {
		Logger.dLog(TAG, "freeEPD");
		
		getEpdRect();
//		EPD_Library.epd_handwrite_update(0, 2, DRAW_START_X, DRAW_START_Y, DRAW_END_X, DRAW_END_Y);
//		epdScreen.LockScreenUpdateMode(0, 20, 1, DRAW_START_X, DRAW_START_Y, DRAW_END_X, DRAW_END_Y, 1);
	}

	protected void enterComment() {
		initEPD();
	}

	protected void exitComment() {
		freeEPD();
	}

	protected void setCommentContent(Bitmap bkbmp) {
		super.setCommentContent(bkbmp);
		
		setDrawing2Epd(getBitmap());
	}

	protected boolean saveComment(final String commentPath, final ICommentHandlerCall call) {
		AsyncTask<Object, Object, Boolean> task = new AsyncTask<Object, Object, Boolean>() {

			@Override
			protected Boolean doInBackground(Object... params) {
				setBitmap(getDrawing4Epd());
				return true;
			}
			
			@Override
			protected void onPostExecute(Boolean result) {
				File file = null;
				String cmd;
				Logger.dLog(TAG, "saveComment path = " + commentPath);
				try {
					file = new File(commentPath);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(file);
					if (!getBitmap().compress(Bitmap.CompressFormat.PNG, 100, fos)) {
						fos.close();
						throw new Exception();
					}
					fos.flush();
					fos.close();

					cmd = "chmod 777 " + commentPath;
					try {
						Runtime.getRuntime().exec(cmd);
					} catch (IOException e) {
						Logger.vLog(TAG, "exec chmod 777 error, path = " + commentPath);
					}
				} catch (Exception e) {
					Logger.vLog(TAG, "io exception" + e);
					if (file != null) {
						file.delete();
					}
				}
				
				if (call != null) {
					call.commentCall();
				}
			}
		};
		
		task.execute();
		
		return true;
	}

	protected boolean deleteComment(String path, ICommentHandlerCall call) {
		File file = null;
		boolean delok = false;

		try {
			file = new File(path);
			if (file.isFile() && file.exists()) {
				delok = file.delete();
			}
		} catch (Exception e) {
			Logger.vLog(TAG, "deleteComment error" + e);
		}

		resetContent();
		
		if (call != null) {
			call.commentCall();
		}

		return delok;
	}

	protected void refresh() {
		setDrawing2Epd(getBitmap());
		
		super.refresh();
	}

	private void setDrawing2Epd(Bitmap bmp) {
		Logger.iLog(TAG, "xxxx set_draw_pic2_epd");
		
		getEpdRect();
//		EPD_Library.epd_fill_2buffer(1, DRAW_START_X, DRAW_START_Y, DRAW_END_X,
//				DRAW_END_Y, bmp, 1, 1, 0, 0);
	}

	private Bitmap getDrawing4Epd() {
		Logger.iLog(TAG, "xxxx getDrawing4Epd");
		
		getEpdRect();
//		Bitmap bitmap = EPD_Library.epd_handwrite_get(1, DRAW_START_X, DRAW_START_Y,
//				DRAW_END_X, DRAW_END_Y, 0);
//
//		return setAlpha(bitmap, 0);
		return null;
	}

	private Bitmap setAlpha(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];
		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,
				sourceImg.getWidth(), sourceImg.getHeight());
		number = number * 255 / 100;
		for (int i = 0; i < argb.length; i++) {
			if (argb[i] == -1) {
				argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);
			}
		}
		
		Bitmap desImg = Bitmap.createBitmap(argb, sourceImg.getWidth(),
				sourceImg.getHeight(), getBitmapConfig());

		return desImg;
	}
}
