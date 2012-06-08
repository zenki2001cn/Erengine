/**
 * @file       SearchDialogView.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-9 下午02:14:32 
 */

package com.easyview.ebook.reader.ui.view;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.IBookSearchCursor;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.EasyViewer;
import com.easyview.ebook.reader.ui.controller.IControlCenterService;
import com.easyview.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;
import com.easyview.ebook.reader.easyviewer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class SearchDialogView extends EVBaseRelativeLayout {
	static private final String TAG = "SearchDialogView";
	
	private EditText mEditSearchKeyword;
	private Button 	mBtnSearch;
	private Button 	mBtnSearchNext;
	private Button 	mBtnSearchPrevious;
	private Button 	mBtnSearchExit;
	
	public SearchDialogView(Context context) {
		super(context);
	}

	public SearchDialogView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SearchDialogView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	private void initView() {
		mEditSearchKeyword = (EditText) findViewById(R.id.id_search_edit);
		mBtnSearch = (Button) findViewById(R.id.id_search_button);
		mBtnSearchNext = (Button) findViewById(R.id.id_search_next);
		mBtnSearchPrevious = (Button) findViewById(R.id.id_search_previous);
		mBtnSearchExit = (Button) findViewById(R.id.id_search_exit);
		
		mBtnSearch.setOnClickListener(cBtnSearchListener);
		mBtnSearchNext.setOnClickListener(cBtnSearchNextListener);
		mBtnSearchPrevious.setOnClickListener(cBtnSearchPreviousListener);
		mBtnSearchExit.setOnClickListener(cBtnSearchExitListener);
	}
	
	@Override
	protected void onFinishInflate() {
		initView();
		
		super.onFinishInflate();
	}
	
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(mEditSearchKeyword.getWindowToken(), 0);
		}
	}
	
	private OnClickListener cBtnSearchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String keyword = mEditSearchKeyword.getText().toString().trim();
			EasyViewer ev = SearchDialogView.this.getEasyViewer();
			if (ev != null) {
				IBookSearchCursor bookSearchOps = ev.getBook().getBookSearchCursor();
				bookSearchOps.setSearchKeyword(keyword);
				ev.enterSearchMode();
			}
			
			closeInputMethod();
		}
	};
	
	private OnClickListener cBtnSearchNextListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			EasyViewer ev = SearchDialogView.this.getEasyViewer();
			if (ev != null) {
				ev.searchGoNext();
//				ev.gotoSearch();
			}
			
			closeInputMethod();
		}
	};
	
	private OnClickListener cBtnSearchPreviousListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			EasyViewer ev = SearchDialogView.this.getEasyViewer();
			if (ev != null) {
				ev.searchGoPrevious();
			}
			
			closeInputMethod();
		}
	};
	
	private OnClickListener cBtnSearchExitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			closeInputMethod();
			
			IControlCenterService icc = SearchDialogView.this.getControlCenter();
			EasyViewer ev = SearchDialogView.this.getEasyViewer();
			if (ev != null) {
				ev.quitSearch();
			}
			
			if (icc != null) {
				icc.runCommand(COMMAND_TYPE.CMD_CLOSE_SEARCH);
			}
			
			
		}
	};
}
