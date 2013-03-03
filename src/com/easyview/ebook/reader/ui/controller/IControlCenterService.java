/**
 * @file       IControlCenterService.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-1 上午10:24:49 
 */

package com.easyview.ebook.reader.ui.controller;

import android.content.Context;

/**
 *
 */
public interface IControlCenterService {
	public interface COMMAND_TYPE {
		public int CMD_SHOW_CONTROL_CENTER = 0;
		public int CMD_HIDE_CONTROL_CENTER = 1;
		public int CMD_CLOSE_CONTROL_CENTER = 2;
		public int CMD_SHOW_TOPIC = 3;
		public int CMD_SHOW_BOOKMARK = 4;
		public int CMD_SHOW_JUMP = 5;
		
		public int CMD_SHOW_SEARCH = 10;
		public int CMD_CLOSE_SEARCH = 11;
	}
	
	public void setContext(Context ctx);
	public void runCommand(int cmd);
}
