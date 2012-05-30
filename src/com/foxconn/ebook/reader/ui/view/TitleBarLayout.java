/**
 * @file       TitleBarLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-10 上午11:12:58 
 */

package com.foxconn.ebook.reader.ui.view;


import com.foxconn.ebook.reader.easyviewer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitleBarLayout extends EVBaseRelativeLayout {
	private TextView mBookTitleView;
	
	public TitleBarLayout(Context context) {
		super(context);
	}

	public TitleBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TitleBarLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		initView();
		
		super.onFinishInflate();
	}

	private void initView() {
		mBookTitleView = (TextView) findViewById(R.id.id_book_title);
	}
	
	public void setBookTitle(String title) {
		mBookTitleView.setText(title);
	}
	
}
