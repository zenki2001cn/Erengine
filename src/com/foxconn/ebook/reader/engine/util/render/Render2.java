/**
 * @file       Render2.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-2-9 下午01:59:43 
 */

package com.foxconn.ebook.reader.engine.util.render;

import com.foxconn.ebook.reader.engine.core.EngineCode;
import com.foxconn.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.Page;
import com.foxconn.ebook.reader.engine.util.Logger;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * 2D圖像渲染引擎.
 */
public class Render2 extends BaseRender {

	/** The Constant TAG. */
	static private final String TAG = "Render2";
	
	/** The m view forground. */
	private ImageView mViewForground;
	
	/** The m view background. */
	private ImageView mViewBackground;
	
	private Bitmap.Config mBitmapConfig = Bitmap.Config.RGB_565;
	
	/**
	 * Instantiates a new render2.
	 */
	public Render2() {
		init();
	}
	
	/**
	 * Instantiates a new render2.
	 *
	 * @param forgroundView the view
	 */
	public Render2(ImageView forgroundView) {
		init();
		
		mViewForground = forgroundView;
	}
	
	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.util.render.BaseRender#init()
	 */
	public void init() {
		Logger.vLog(TAG, "Render2.init()");
	}
	
	/**
	 * Sets the forground view.
	 *
	 * @param view the new forground view
	 */
	public void setForgroundView(ImageView view) {
		mViewForground = view;
	}
	
	/**
	 * Sets the background view.
	 *
	 * @param view the new background view
	 */
	public void setBackgroundView(ImageView view) {
		mViewBackground = view;
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.util.render.BaseRender#showContent(com.foxconn.ebook.reader.engine.model.Book)
	 */
	@Override
	public void showContent(Book book) {
		if (null == book) {
			EngineCode.getInstance().setLastCode(EngineMsgCode.ENGINE_ERROR_RENDER_FAILED);
			return;
		}
		
		Page page = book.getCurPage();
		if (null == page) {
			EngineCode.getInstance().setLastCode(EngineMsgCode.ENGINE_ERROR_RENDER_FAILED);
			return;
		}
		
		Bitmap content = page.getPageContent();
		
		if ( (null == mViewForground) || (null == content) ) {
			Logger.eLog(TAG, "View = " + mViewForground + "content = " + content);
			EngineCode.getInstance().setLastCode(EngineMsgCode.ENGINE_ERROR_RENDER_FAILED);
			return;
		}
		
		try {
			mViewForground.setImageBitmap(content);
			Logger.vLog(TAG, "showContent()");
		} catch (OutOfMemoryError ooe) {
			Logger.eLog(TAG, "Render2.showContent()->OutOfMemoryError");
		} catch (IllegalArgumentException illae) {
			Logger.eLog(TAG, "Render2.showContent()->IllegalArgumentException");
		}
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.util.render.BaseRender#free()
	 */
	public void free() {
		Logger.vLog(TAG, "Render2.free()");
	}

	/* (non-Javadoc)
	 * @see com.foxconn.ebook.reader.engine.util.render.BaseRender#getBitmapConfig()
	 */
	@Override
	public Bitmap.Config getBitmapConfig() {
		return mBitmapConfig;
	}
}
