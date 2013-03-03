/**
 * @file       BaseRender.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-2-9 下午01:57:23 
 */

package com.easyview.ebook.reader.engine.util.render;

import android.graphics.Bitmap;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.util.Logger;

/**
 * 顯示圖像的基類.
 */
public abstract class BaseRender {
	
	/** The Constant TAG. */
	static private final String TAG = "BaseRender";
	
	/**
	 * 初始化渲染引擎.
	 */
	abstract public void init();
	
	/**
	 * 釋放資源.
	 */
	abstract public void free();
	
	/**
	 * 返回Bitmap的配置類型.
	 *
	 * @return Bitmap的配置類型，如RGB_565、ARGB_4444、ARGB_8888，引擎將根據該配置生成相應的Bitmap.
	 */
	abstract public Bitmap.Config getBitmapConfig();
	
	/**
	 * 顯示圖像內容.
	 *
	 * @param book Book對象
	 * @see com.easyview.ebook.reader.engine.model.Book
	 */
	public void showContent(Book book) {
		Logger.vLog(TAG, "BaseRender.showContent()");
	}
}
