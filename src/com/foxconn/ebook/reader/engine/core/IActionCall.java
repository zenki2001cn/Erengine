/**
 * @file       IActionCall.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-3 上午08:57:35 
 */

package com.foxconn.ebook.reader.engine.core;

/**
 * IAction的回調接口, 用來完成執行動作前後的回調.
 * <p>
 * 
 * Example:
 * 
 * <p> 	顯示頁碼回調
 * <p>	IActionCall iaUpdatePage = new IActionCall() {
 * <p>		@Override
 * <p>		public boolean action() {
 * <p>			// 更新頁碼
 * <p>			int tmp = mBook.getCurPageNumber();
 * <p>			int num = (tmp <= 0) ? 1 : (tmp);
 * <p>			int totalPage = mBook.getTotalPageNumber();
 * <p>
 * <p>			String info = String.format(PAGE_FORMAT, num, totalPage);
 * <p>			mBottomBarLayout.updatePageInfo(info);
 * <p>
 * <p>			return true;
 * <p>		}
 * <p>	};
 * <p>
 * <p>	public void pageDown() {
 * <p>		mIEREngine.getAction().pageDown(null, iaUpdatePage);
 * <p>	}
 */
public interface IActionCall {
	
	/**
	 * 當Action在執行某一動作前後，會調用該接口的方法.
	 * 通常打開書之前的裝載對話框、翻頁後的頁碼變更都可以使用該回調來處理.
	 *
	 * @return true, 回調成功.
	 */
	public boolean action();
}
