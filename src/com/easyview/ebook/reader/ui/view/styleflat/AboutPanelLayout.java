/**
 * @file       BookmarkPanelLayout.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-14 下午01:57:49 
 */

package com.easyview.ebook.reader.ui.view.styleflat;

import java.util.ArrayList;
import java.util.List;

import com.easyview.ebook.reader.ui.view.EVBaseRelativeLayout;
import com.easyview.ebook.reader.easyviewer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AboutPanelLayout extends EVBaseRelativeLayout {
	static private final String TAG = "AboutPanelLayout";
	private Context mContext;
	private ListView mAboutListView;
	ArrayList<AboutListItem> mAboutListItems = new ArrayList<AboutListItem>();
	AboutListAdapter mAboutListAdapter;
	String[] mAboutTitleStrList;
	String[] mAboutDescStrList;

	public AboutPanelLayout(Context context) {
		super(context);
		mContext = context;
	}

	public AboutPanelLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public AboutPanelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		initView();

		super.onFinishInflate();
	}

	private void initView() {
		mAboutListView = (ListView) findViewById(R.id.id_about_listview_flat);

		mAboutListItems.clear();
		mAboutTitleStrList = getContext().getResources().getStringArray(
				R.array.about_title_array);
		mAboutDescStrList = getContext().getResources().getStringArray(
				R.array.about_desc_array);

		int len = mAboutTitleStrList.length;
		for (int i = 0; i < len; i++) {
			AboutListItem item = new AboutListItem(mAboutTitleStrList[i], mAboutDescStrList[i]);
			mAboutListItems.add(item);
		}

		mAboutListAdapter = new AboutListAdapter(mContext, mAboutListItems);
		mAboutListView.setAdapter(mAboutListAdapter);
	}

	protected class AboutListItem {
		String mTitle;
		String mLongDesc;

		public AboutListItem(String title, String desc) {
			mTitle = title;
			mLongDesc = desc;
		}

		public String getTitle() {
			return mTitle;
		}

		public String getDescription() {
			return mLongDesc;
		}
	}

	protected class AboutListAdapter extends BaseAdapter {
		LayoutInflater factory;
		List<AboutListItem> mListItem;
		Context mContext;

		public AboutListAdapter(Context context, List<AboutListItem> listItems) {

			mListItem = listItems;
			mContext = context;
			factory = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mListItem.size();
		}

		@Override
		public Object getItem(int position) {
			return mListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			TextView titleTv;
			TextView descTv;

			if (view == null) {
				view = factory.inflate(R.layout.about_item_flat, null);
			}

			AboutListItem item = (AboutListItem) getItem(position);
			titleTv = (TextView) view.findViewById(R.id.id_about_title_flat);
			descTv = (TextView) view.findViewById(R.id.id_about_desc_flat);

			// set content
			titleTv.setText(item.getTitle());
			descTv.setText(item.getDescription());

			return view;
		}
	}
}
