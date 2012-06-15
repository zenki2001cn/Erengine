/**
 * @file       ControlPanelLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-5-30 下午03:55:46 
 */

package com.easyview.ebook.reader.ui.view.styleflat;

import java.util.List;

import com.easyview.ebook.reader.engine.core.IActionCall;
import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.IBookChapterCursor;
import com.easyview.ebook.reader.engine.model.ChapterTreeInfo.Chapter;
import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;
import com.easyview.ebook.reader.ui.view.ActionDialog;
import com.easyview.ebook.reader.ui.view.EVBaseRelativeLayout;
import com.easyview.ebook.reader.easyviewer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 */
public class ControlPanelLayout extends EVBaseRelativeLayout {
	static private final String TAG = "ControlPanelLayout";

	static final int PANEL_LEVEL_ONE = 1;
	static final int PANEL_LEVEL_TWO = 2;

	private int mCurrentLevel = PANEL_LEVEL_ONE;
	private Button mBtnPanelTopic;
	private Button mBtnPanelSearch;
	private Button mBtnPanelBookmark;
	private Button mBtnPanelJump;
	private Button mBtnPanelExit;
	private ListView mTopicListView;
	private RelativeLayout mLayoutControlPanelWapper;
	private RelativeLayout mLayoutJumpWapper;
	private RelativeLayout mLayoutTitleWapper;
	private View mCurrentEnterView;
	private ActionDialog mMessageDlg;

	// Flat style
	private String[] mFuncTitleArray;
	private ListView mFuncTitleList;
	private TextView mTextTitle;
	private TextView mTextBack;
	private RelativeLayout mLayoutTopicWapper;
	private RelativeLayout mLayoutBookmarkWapper;

	public ControlPanelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ControlPanelLayout(Context context) {
		super(context);
	}

	public void setPanelMode(int mode) {
		switch (mode) {
		case COMMAND_TYPE.CMD_SHOW_CONTROL_CENTER:
		case COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER:
		case COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER:
			break;
		case COMMAND_TYPE.CMD_SHOW_TOPIC:
			mBtnPanelTopic.performClick();
			break;
		case COMMAND_TYPE.CMD_SHOW_BOOKMARK:
			mBtnPanelBookmark.performClick();
			break;
		case COMMAND_TYPE.CMD_SHOW_JUMP:
			mBtnPanelJump.performClick();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onFinishInflate() {
		initView();
		Logger.dLog(TAG, "onFinishInflate flat >>>>>>>>>>>>>>>>>>>> ");

		super.onFinishInflate();
	}

	private void initView() {
		// 标题和返回
		mTextTitle = (TextView) findViewById(R.id.id_panel_title_flat);
		mTextBack = (TextView) findViewById(R.id.id_back_text_flat);
		mTextBack.setOnClickListener(cTextBackListener);
		
		// 初始化功能列表
		mFuncTitleArray = getContext().getResources().getStringArray(
				R.array.func_title_array);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1);
		for (int i = 0; i < mFuncTitleArray.length; i++) {
			adapter.add(mFuncTitleArray[i]);
		}
		mFuncTitleList = (ListView) findViewById(R.id.id_func_menu_flat);
		mFuncTitleList.setAdapter(adapter);
		mFuncTitleList.setOnItemClickListener(cFuncTitleListClicked);
		
		mLayoutControlPanelWapper = (RelativeLayout) findViewById(R.id.id_control_panel_wrapper_flat);
		
		// 初始化目录列表
		mLayoutTopicWapper = (RelativeLayout) findViewById(R.id.id_topic_wrapper_flat);
		mTopicListView = (ListView) findViewById(R.id.id_topic_list_flat);
		mTopicListView.setOnItemSelectedListener(cListTopicSelected);
		mTopicListView.setOnItemClickListener(cListTopicClicked);
		
		// 书签
		mLayoutBookmarkWapper = (RelativeLayout) findViewById(R.id.id_bookmark_wrapper_flat);
		
		// mLayoutJumpWapper = (RelativeLayout)
		// findViewById(R.id.id_jump_wrapper);
		// 
		// mLayoutTitleWapper = (RelativeLayout)
		// findViewById(R.id.id_title_wrapper);
		//
		// mBtnPanelTopic = (Button) findViewById(R.id.id_col1_topic);
		// mBtnPanelJump = (Button) findViewById(R.id.id_col2_jump);
		// mBtnPanelSearch = (Button) findViewById(R.id.id_col3_search);
		// mBtnPanelBookmark = (Button) findViewById(R.id.id_col4_bookmark);
		// mBtnPanelExit = (Button) findViewById(R.id.id_col9_exit);
		//
		// mBtnPanelTopic.setOnClickListener(cBtnTopicListener);
		// mBtnPanelExit.setOnClickListener(cBtnExitListener);
		// mBtnPanelJump.setOnClickListener(cBtnJumpListener);
		// mBtnPanelSearch.setOnClickListener(cBtnSearchListener);
		// mBtnPanelBookmark.setOnClickListener(cBtnBookmarkListener);

		mMessageDlg = getActionDialog();
	}

	private void initTopicContent() {
		Book book = getEasyViewer().getBook();
		IBookChapterCursor bcOps = book.getBookChapterCursor();

		if (!bcOps.chapterIsLoad()) {
			getErengine().getAction().loadChapterInformation(iaPreloadChapter,
					iaLoadedChapter);
		} else {
			iaLoadedChapter.action();
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

			Book book = getEasyViewer().getBook();
			IBookChapterCursor bcOps = book.getBookChapterCursor();

			List<Chapter> chapterList = bcOps.getChapterList();
			fillTopicListView(chapterList);

			Logger.dLog(TAG, "iaLoadedChapter action finished");
			return true;
		}
	};

	private void fillTopicListView(List<Chapter> list) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_list_item_1);

		for (Chapter chapter : list) {
			adapter.add(chapter.title);
		}

		mTopicListView.setAdapter(adapter);
	}

	private void jumpChapter(int index) {
		getEasyViewer().chapterJumpTo(index);
	}

	private void jumpChapter(String title) {
		getEasyViewer().chapterJumpTo(title);
	}

	private void exitControlPanel() {
		getControlCenter().runCommand(COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER);
	}

	private void hideAllSubLayout() {
//		if (mLayoutJumpWapper != null) {
//			mLayoutJumpWapper.setVisibility(View.INVISIBLE);
//		}

		if (mLayoutTopicWapper != null) {
			mLayoutTopicWapper.setVisibility(View.INVISIBLE);
		}

		if (mLayoutBookmarkWapper != null) {
			mLayoutBookmarkWapper.setVisibility(View.INVISIBLE);
		}
	}

	private void enterLevel2() {
		mCurrentLevel = PANEL_LEVEL_TWO;

		hideAllSubLayout();

		if (mLayoutControlPanelWapper != null) {
			mLayoutControlPanelWapper.setVisibility(View.INVISIBLE);
		}

		if (mCurrentEnterView != null) {
			mCurrentEnterView.setVisibility(View.VISIBLE);
			mTextTitle.setText(mCurrentEnterView.getTag().toString());
		}
	}

	private void enterLevel1() {
		mCurrentLevel = PANEL_LEVEL_ONE;

		hideAllSubLayout();

		if (mLayoutControlPanelWapper != null) {
			mLayoutControlPanelWapper.setVisibility(View.VISIBLE);
			mTextTitle.setText(mLayoutControlPanelWapper.getTag().toString());
		}
	}

	private OnItemClickListener cFuncTitleListClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				enterTopicLayout();
				break;
			case 1:
				enterBookmarkLayout();
				break;
			case 2:
				enterSearchLayout();
				break;

			default:
				break;
			}
		}
	};

	private void enterTopicLayout() {
		mCurrentEnterView = mLayoutTopicWapper;
		enterLevel2();
		initTopicContent();
	}
	
	public void enterBookmarkLayout() {
		mCurrentEnterView = mLayoutBookmarkWapper;
		enterLevel2();
	}

	public void enterSearchLayout() {
		getControlCenter().runCommand(COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER);
		getControlCenter().runCommand(COMMAND_TYPE.CMD_SHOW_SEARCH);
	}
	
	private OnClickListener cBtnExitListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			exitControlPanel();
		}
	};

	private OnClickListener cBtnJumpListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCurrentEnterView = mLayoutJumpWapper;
			enterLevel2();
		}
	};

	private OnClickListener cTextBackListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mCurrentLevel == PANEL_LEVEL_ONE) {
				exitControlPanel();
			} else if (mCurrentLevel == PANEL_LEVEL_TWO) {
				enterLevel1();
			}
		}
	};

	private OnItemSelectedListener cListTopicSelected = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER);
			jumpChapter(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private OnItemClickListener cListTopicClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			getControlCenter().runCommand(COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER);
			jumpChapter(position);
		}
	};
}
