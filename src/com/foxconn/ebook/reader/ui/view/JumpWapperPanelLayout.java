/**
 * @file       InputNumPanel.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-4 上午08:47:24 
 */

package com.foxconn.ebook.reader.ui.view;

import java.util.ArrayList;

import com.foxconn.ebook.reader.easyviewer.R;
import com.foxconn.ebook.reader.engine.core.IActionCall;
import com.foxconn.ebook.reader.engine.core.IEngineService;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.Chapter;
import com.foxconn.ebook.reader.engine.util.Logger;
import com.foxconn.ebook.reader.ui.controller.EasyViewer;
import com.foxconn.ebook.reader.ui.controller.IControlCenterService;
import com.foxconn.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 */
public class JumpWapperPanelLayout extends EVBaseRelativeLayout {
	static private final String TAG = "JumpWapperPanelLayout";

	private EditText mEditTextJumpNum;
	private Button mBtnJump;
	private Button mBtnNum0;
	private Button mBtnNum1;
	private Button mBtnNum2;
	private Button mBtnNum3;
	private Button mBtnNum4;
	private Button mBtnNum5;
	private Button mBtnNum6;
	private Button mBtnNum7;
	private Button mBtnNum8;
	private Button mBtnNum9;
	private Button mBtnDel;
	private Button mBtnClean;
	private Button mBtnPreChapter;
	private Button mBtnListChapter;
	private Button mBtnNextChapter;
	private ActionDialog mMessageDlg;
	static final String PAGE_NUM_DEFAULT = "";

	public JumpWapperPanelLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public JumpWapperPanelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JumpWapperPanelLayout(Context context) {
		super(context);
	}

	@Override
	protected void onFinishInflate() {
		initView();

		super.onFinishInflate();
	}

	private void initView() {
		mEditTextJumpNum = (EditText) findViewById(R.id.id_jump_page_number);
		mEditTextJumpNum.setInputType(EditorInfo.TYPE_NULL);
		mEditTextJumpNum.setCursorVisible(false);
		mBtnJump = (Button) findViewById(R.id.id_jump_page_btn);
		mBtnNum0 = (Button) findViewById(R.id.id_jump_num_0);
		mBtnNum1 = (Button) findViewById(R.id.id_jump_num_1);
		mBtnNum2 = (Button) findViewById(R.id.id_jump_num_2);
		mBtnNum3 = (Button) findViewById(R.id.id_jump_num_3);
		mBtnNum4 = (Button) findViewById(R.id.id_jump_num_4);
		mBtnNum5 = (Button) findViewById(R.id.id_jump_num_5);
		mBtnNum6 = (Button) findViewById(R.id.id_jump_num_6);
		mBtnNum7 = (Button) findViewById(R.id.id_jump_num_7);
		mBtnNum8 = (Button) findViewById(R.id.id_jump_num_8);
		mBtnNum9 = (Button) findViewById(R.id.id_jump_num_9);
		mBtnDel = (Button) findViewById(R.id.id_jump_del);
		mBtnClean = (Button) findViewById(R.id.id_jump_clean);
		mBtnPreChapter = (Button) findViewById(R.id.id_jump_pre_chapter);
		mBtnListChapter = (Button) findViewById(R.id.id_jump_chapter_list);
		mBtnNextChapter = (Button) findViewById(R.id.id_jump_next_chapter);

		mBtnJump.setOnClickListener(cBtnJumpListener);
		mBtnNum0.setOnClickListener(cBtnNum0Listener);
		mBtnNum1.setOnClickListener(cBtnNum1Listener);
		mBtnNum2.setOnClickListener(cBtnNum2Listener);
		mBtnNum3.setOnClickListener(cBtnNum3Listener);
		mBtnNum4.setOnClickListener(cBtnNum4Listener);
		mBtnNum5.setOnClickListener(cBtnNum5Listener);
		mBtnNum6.setOnClickListener(cBtnNum6Listener);
		mBtnNum7.setOnClickListener(cBtnNum7Listener);
		mBtnNum8.setOnClickListener(cBtnNum8Listener);
		mBtnNum9.setOnClickListener(cBtnNum9Listener);
		mBtnDel.setOnClickListener(cBtnNumDelListener);
		mBtnClean.setOnClickListener(cBtnNumCleanListener);
		mBtnPreChapter.setOnClickListener(cBtnPreChapterListener);
		mBtnListChapter.setOnClickListener(cBtnListChapterListener);
		mBtnNextChapter.setOnClickListener(cBtnNextChapterListener);

		mMessageDlg = getActionDialog();
	}

	private void loadChapterInfo() {
		Book book = getEasyViewer().getBook();
		if (!book.chapterIsLoad()) {
			getErengine().getAction().loadChapterInformation(iaPreloadChapter,
					iaLoadedChapter);
		}
	}

	IActionCall iaPreloadChapter = new IActionCall() {
		@Override
		public boolean action() {
			Logger.dLog(TAG, "iaPreloadChapter action");
			mMessageDlg.setMessage(getContext()
					.getString(R.string.load_chapter));
			mMessageDlg.showDialog();
			return true;
		}
	};

	IActionCall iaLoadedChapter = new IActionCall() {
		@Override
		public boolean action() {
			mMessageDlg.closeDialog();
			Logger.dLog(TAG, "iaLoadedChapter action finished");
			return true;
		}
	};

	private OnClickListener cBtnJumpListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int textVal;
			Book book = getEasyViewer().getBook();
			String text = mEditTextJumpNum.getText().toString();
			if (text.length() > 0) {
				textVal = Integer.valueOf(text);
			} else {
				Logger.eLog(TAG, "cBtnJumpListener jumpPage num == null");
				return;
			}

			int jumpPage = 1;
			if (textVal >= book.getTotalPageNumber()) {
				jumpPage = book.getTotalPageNumber();
			} else if (textVal < 1) {
				jumpPage = 1;
			} else {
				jumpPage = textVal;
			}

			Logger.dLog(TAG, "cBtnJumpListener page = " + jumpPage);

			mEditTextJumpNum.setText(PAGE_NUM_DEFAULT);

			// getControlCenter()
			// .runCommand(COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER);

			getEasyViewer().pageJumpTo(jumpPage);
		}
	};

	private OnClickListener cBtnPreChapterListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			loadChapterInfo();
			// getControlCenter()
			// .runCommand(COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER);
			getEasyViewer().chapterUp();
		}
	};

	private OnClickListener cBtnListChapterListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_TOPIC);
		}
	};

	private OnClickListener cBtnNextChapterListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			loadChapterInfo();
			// getControlCenter().runCommand(COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER);
			getEasyViewer().chapterDown();
		}
	};

	private OnClickListener cBtnNum0Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum1Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum2Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum3Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum4Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum5Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum6Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum7Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum8Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNum9Listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = mEditTextJumpNum.getText().toString()
					+ v.getTag().toString();
			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNumDelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			String temp = PAGE_NUM_DEFAULT;
			String text = mEditTextJumpNum.getText().toString();
			int len = text.length();
			if (len > 1) {
				temp = text.substring(0, len - 1);
			}

			mEditTextJumpNum.setText(temp);
		}
	};

	private OnClickListener cBtnNumCleanListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mEditTextJumpNum.setText(PAGE_NUM_DEFAULT);
		}
	};
}
