/**
 * @file       BookmarkPopupDialogView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-14 下午05:06:27 
 */

package com.easyview.ebook.reader.ui.view.stylegrid;


import com.easyview.ebook.reader.easyviewer.R;
import com.easyview.ebook.reader.ui.view.EVBaseRelativeLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 *
 */
public class BookmarkPopupDialogView extends EVBaseRelativeLayout {

	private TextView mTvGotoBookmark;
	private TextView mTvDelBookmark;
	
	public BookmarkPopupDialogView(Context context) {
		super(context);
	}

	public BookmarkPopupDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BookmarkPopupDialogView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		initView();
		
		super.onFinishInflate();
	}
	
	private void initView() {
		mTvGotoBookmark = (TextView) findViewById(R.id.id_dlg_goto_bookmark);
		mTvDelBookmark = (TextView) findViewById(R.id.id_dlg_del_bookmark);
		
		mTvGotoBookmark.setOnClickListener(cTvGotoBookmarkListener);
		mTvDelBookmark.setOnClickListener(cTvDelBookmarkListener);
	}
	
	private OnClickListener cTvGotoBookmarkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	private OnClickListener cTvDelBookmarkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};

}
