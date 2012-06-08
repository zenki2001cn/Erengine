/**
 * @file       BottomBarLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-10 上午11:30:58 
 */

package com.easyview.ebook.reader.ui.view;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.EasyViewer;
import com.easyview.ebook.reader.ui.controller.IControlCenterService;
import com.easyview.ebook.reader.ui.controller.IProgressControllerCallback;
import com.easyview.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;
import com.easyview.ebook.reader.easyviewer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class BottomBarLayout extends EVBaseRelativeLayout {
	static private final String TAG = "BottomBarLayout";
	
	private TextView mPageNumView;
	private ProgressDisplayerView mProgressDisplayer;
	
	public BottomBarLayout(Context context) {
		super(context);
	}

	public BottomBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomBarLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate() {
		initView();
		
		super.onFinishInflate();
	}
	
	private void initView() {
		mPageNumView = (TextView) findViewById(R.id.id_page_info);
		mProgressDisplayer = (ProgressDisplayerView) findViewById(R.id.id_page_progress);
		
		mProgressDisplayer.setProgressControllerCallback(ipGotoPage);
		mPageNumView.setOnClickListener(cTvPageNum);
	}
	
	public void updatePageInfo(String info) {
		mPageNumView.setText(info);
	}
	
	public void updatePageProgress(float progress) {
		mProgressDisplayer.update(progress);
	}
	
	IProgressControllerCallback ipGotoPage = new IProgressControllerCallback() {
		@Override
		public void gotoPage(float percent) {
			EasyViewer ev = getEasyViewer();
			Book book = ev.getBook();
			
			int totalPage = book.getTotalPageNumber();
			int temp = (int) (((float) totalPage) * percent);

			int page;
			if (temp <= 0) {
				page = 1;
			} else if (temp > totalPage) {
				page = totalPage;
			} else {
				page = temp;
			}

			Logger.dLog(TAG, "ipGotoPage page = " + page);
			ev.pageJumpTo(page);
		}
	};

	private OnClickListener cTvPageNum = new OnClickListener() {
		@Override
		public void onClick(View v) {
			IControlCenterService icc = getControlCenter();
			
			if (icc != null) {
				icc.runCommand(COMMAND_TYPE.CMD_SHOW_JUMP);
			}
		}
	};
}
