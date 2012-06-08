/**
 * @file       ControlCenter.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-1 上午10:15:05 
 */

package com.easyview.ebook.reader.ui.controller;

import com.easyview.ebook.reader.ui.view.ActionDialog;
import com.easyview.ebook.reader.ui.view.ControlPanelLayout;
import com.easyview.ebook.reader.easyviewer.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ControlCenter implements IControlCenterService {
	static private ControlCenter mControlCenter;
	private Context mContext;
	private Dialog mControlDlg;
	private ControlPanelLayout mPanelLayout;
	private ActionDialog mSearchDialog;

	private ControlCenter() {
	}

	static public ControlCenter getInstance() {
		if (null == mControlCenter) {
			mControlCenter = new ControlCenter();
		}

		return mControlCenter;
	}

	public void setContext(Context context) {
		mContext = context;
	}

	@Override
	public void runCommand(int cmd) {
		switch (cmd) {
		case COMMAND_TYPE.CMD_SHOW_CONTROL_CENTER:
			showControlPanelDialog();
			break;
		case COMMAND_TYPE.CMD_HIDE_CONTROL_CENTER:
			hideControlPanelDlg();
			break;
		case COMMAND_TYPE.CMD_CLOSE_CONTROL_CENTER:
			closeControlPanelDlg();
			break;
		case COMMAND_TYPE.CMD_SHOW_TOPIC:
		case COMMAND_TYPE.CMD_SHOW_BOOKMARK:
		case COMMAND_TYPE.CMD_SHOW_JUMP:
			showControlPanelForMode(cmd);
			break;
		case COMMAND_TYPE.CMD_SHOW_SEARCH:
			showSearchDialog();
			break;
		case COMMAND_TYPE.CMD_CLOSE_SEARCH:
			closeSearchDialog();
			break;
			
		default:
			break;
		}
	}

	public void free() {
		closeControlPanelDlg();
		closeSearchDialog();
		
		mControlCenter = null;
	}
	
	private void showSearchDialog() {
		closeControlPanelDlg();
		
		LayoutInflater factory = LayoutInflater.from(mContext);
		final RelativeLayout searchLayout = (RelativeLayout) factory.inflate(R.layout.search_dialog_view, null);
		int x = 0;
		int y = (mContext.getWallpaperDesiredMinimumHeight() / 2) - 30;
		mSearchDialog = new ActionDialog(mContext);
		mSearchDialog.setDialogType(ActionDialog.TYPE_CUSTOM_DIALOG);
		mSearchDialog.setContentView(searchLayout);
		mSearchDialog.setPositionX(x);
		mSearchDialog.setPositionY(y);
		mSearchDialog.showDialog();
	}
	
	private void closeSearchDialog() {
		if (mSearchDialog != null) {
			mSearchDialog.closeDialog();
			mSearchDialog = null;
		}
	}

	private void createControlPanelDialog(int mode) {
		LayoutInflater factory = LayoutInflater.from(mContext);
		final LinearLayout panelLayout = (LinearLayout) factory.inflate(
				R.layout.control_panel, null);
		
		mControlDlg = new Dialog(mContext);
		mControlDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mControlDlg.setContentView(panelLayout);
		mControlDlg.setCancelable(true);
		mControlDlg.setCanceledOnTouchOutside(true);
		
		mPanelLayout = (ControlPanelLayout) panelLayout.findViewById(R.id.id_panel_wrapper);
		mPanelLayout.setPanelMode(mode);
	}
	
	private void showControlPanelDialog() {
		closeControlPanelDlg();

		if (null == mControlDlg) {
			createControlPanelDialog(COMMAND_TYPE.CMD_SHOW_CONTROL_CENTER);
			mControlDlg.show();
		} else if (mControlDlg.isShowing()) {
			return;
		} else {
			mControlDlg.show();
		}
	}

	private void hideControlPanelDlg() {
		if (mControlDlg != null) {
			mControlDlg.dismiss();
		}
	}

	private void closeControlPanelDlg() {
		if (mControlDlg != null) {
			mControlDlg.dismiss();
			mControlDlg = null;
		}
	}
	
	private void showControlPanelForMode(int mode) {
		if (null == mControlDlg) {
			createControlPanelDialog(mode);
			mControlDlg.show();
		} else if (mControlDlg.isShowing()) {
			mPanelLayout.setPanelMode(mode);
		} else {
			mPanelLayout.setPanelMode(mode);
			mControlDlg.show();
		}
	}
}
