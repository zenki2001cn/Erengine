/**
 * @file       BookmarkPanelLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-14 下午01:57:49 
 */

package com.foxconn.ebook.reader.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.foxconn.ebook.reader.easyviewer.R;
import com.foxconn.ebook.reader.engine.core.IActionCall;
import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.BookMarkInfo.Bookmark;
import com.foxconn.ebook.reader.engine.model.IBookMarkCursor;
import com.foxconn.ebook.reader.engine.util.Logger;
import com.foxconn.ebook.reader.ui.controller.EasyViewer;
import com.foxconn.ebook.reader.ui.controller.IControlCenterService;
import com.foxconn.ebook.reader.ui.controller.IControlCenterService.COMMAND_TYPE;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class BookmarkPanelLayout extends EVBaseRelativeLayout {
	static private final String TAG = "BookmarkPanelLayout";
	private ListView mLvBookmark;
	private Button mBtnAddBookmark;
	private Button mBtnCleanBookmark;
	private View mBookmarkOpsLayout;
	private TextView mTvBookmarkSummary;
	private Button mBtnGotoBookmark;
	private Button mBtnDelBookmark;
	private BookmarkListAdapter mBookmarkAapter;
	private ActionDialog mBookmarkOpsDlg;
	private float mCurrentPosX = 0;
	private float mCurrentPosY = 0;
	private int mPosOffsetX = 170;
	private int mPosOffsetY = 160;
	private Bookmark mBookmarkTodo;

	public BookmarkPanelLayout(Context context) {
		super(context);
	}

	public BookmarkPanelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BookmarkPanelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		initView();
		initBookmarkList();

		super.onFinishInflate();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mBookmarkOpsLayout = LayoutInflater.from(getContext()).inflate(
				R.layout.bookmark_operate_dialog, null);
		mPosOffsetX = (w / 2 + (mBookmarkOpsLayout.getRight() - mBookmarkOpsLayout.getLeft()));
		mPosOffsetY = (h / 2 - (mBookmarkOpsLayout.getBottom() - mBookmarkOpsLayout
				.getTop()));

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mCurrentPosX = ev.getX();
		mCurrentPosY = ev.getY();

		return super.dispatchTouchEvent(ev);
	}

	private void initView() {
		mLvBookmark = (ListView) findViewById(R.id.id_bookmark_list);
		mBtnAddBookmark = (Button) findViewById(R.id.id_bookmark_add);
		mBtnCleanBookmark = (Button) findViewById(R.id.id_bookmark_clean);

		mLvBookmark.setOnItemClickListener(cListBookmarkListener);
		mBtnAddBookmark.setOnClickListener(cBtnAddBookmark);
		mBtnCleanBookmark.setOnClickListener(cBtnCleanBookmark);
	}

	private void initBookmarkList() {
		List<Bookmark> bookmarkList = getEasyViewer().getBook()
				.getBookMarkCursor().getBookmarkList();
		mBookmarkAapter = new BookmarkListAdapter(getContext(), bookmarkList);
		mLvBookmark.setAdapter(mBookmarkAapter);
	}

	private void addBookmark() {
		ActionDialog dlg = getActionDialog();
		dlg.setMessage(getContext().getString(R.string.bookmark_add));
		dlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);

		getErengine().getAction().addBookmark(dlg, iaUpdateBookmarkList);
	}
	
	private void gotoBookmark() {
		IControlCenterService icc = getControlCenter();
		if (icc != null) {
			icc.runCommand(COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER);
		}
		
		EasyViewer ev = getEasyViewer();
		if (ev != null) {
			ev.gotoBookmark();
		}
	}
	
	private void delBookmark() {
		ActionDialog dlg = getActionDialog();
		dlg.setMessage(getContext().getString(R.string.bookmark_del));
		dlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);

		getErengine().getAction().deleteBookmark(mBookmarkTodo, dlg, iaUpdateBookmarkList);
	}

	IActionCall iaUpdateBookmarkList = new IActionCall() {
		@Override
		public boolean action() {
			getActionDialog().closeDialog();
			mBookmarkAapter.notifyDataSetChanged();
			return true;
		}
	};

	private void cleanBookmark() {
		ActionDialog dlg = getActionDialog();
		dlg.setMessage(getContext().getString(R.string.bookmark_clean));
		dlg.setDialogType(ActionDialog.TYPE_ALERT_DIALOG);

		getErengine().getAction().delAllBookmark(dlg, iaUpdateBookmarkList);
	}

	private void showBookmarkOpsDialog() {
		mBookmarkOpsLayout = LayoutInflater.from(getContext()).inflate(
				R.layout.bookmark_operate_dialog, null);
		mTvBookmarkSummary = (TextView) mBookmarkOpsLayout.findViewById(R.id.id_dlg_bookmark_summary);
		mBtnGotoBookmark = (Button) mBookmarkOpsLayout.findViewById(R.id.id_dlg_goto_bookmark);
		mBtnDelBookmark = (Button) mBookmarkOpsLayout.findViewById(R.id.id_dlg_del_bookmark);
		
		mTvBookmarkSummary.setText(mBookmarkTodo.getSummary());
		mBtnGotoBookmark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoBookmark();
				mBookmarkOpsDlg.closeDialog();
			}
		});

		mBtnDelBookmark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delBookmark();
				mBookmarkOpsDlg.closeDialog();
			}
		});

		mBookmarkOpsDlg = new ActionDialog(getContext());
		mBookmarkOpsDlg.setDialogType(ActionDialog.TYPE_CUSTOM_DIALOG);
		mBookmarkOpsDlg.setContentView(mBookmarkOpsLayout);
		mBookmarkOpsDlg.setCancelable(true);
		mBookmarkOpsDlg.setCanceledOnTouchOutside(true);
//		mBookmarkOpsDlg.setPositionX((int) mCurrentPosX - mPosOffsetX);
//		mBookmarkOpsDlg.setPositionY((int) mCurrentPosY - mPosOffsetY);
		mBookmarkOpsDlg.showDialog();
	}

	private OnItemClickListener cListBookmarkListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mBookmarkTodo = (Bookmark) mLvBookmark.getItemAtPosition(position);
			Book book = getEasyViewer().getBook();
			book.getBookMarkCursor().setBookmarkTogo(mBookmarkTodo);
			showBookmarkOpsDialog();
		}
	};

	private OnClickListener cBtnAddBookmark = new OnClickListener() {
		@Override
		public void onClick(View v) {
			addBookmark();
		}
	};

	private OnClickListener cBtnCleanBookmark = new OnClickListener() {
		@Override
		public void onClick(View v) {
			cleanBookmark();
		}
	};

	public class BookmarkListAdapter extends BaseAdapter {
		private LayoutInflater mFactory;
		private List<Bookmark> mListItem;
		private Context mContext;
		private String PAGE_FORMAT;

		public BookmarkListAdapter(Context context, List<Bookmark> objects) {
			mContext = context;
			mListItem = objects;
			mFactory = LayoutInflater.from(context);
			PAGE_FORMAT = mContext
					.getString(R.string.bookmark_item_page_format);
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Bookmark getItem(int position) {
			return mListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;

			if (view == null) {
				view = mFactory.inflate(R.layout.bookmark_item, null);
			}

			Bookmark item = (Bookmark) getItem(position);
			TextView tvSummary = (TextView) view
					.findViewById(R.id.id_item_summary);
			TextView tvPageNum = (TextView) view
					.findViewById(R.id.id_item_page_num);

			tvSummary.setText(item.getSummary());
			tvPageNum.setText(String.format(PAGE_FORMAT, item.getPageNum()));

			return view;
		}
	}

}
