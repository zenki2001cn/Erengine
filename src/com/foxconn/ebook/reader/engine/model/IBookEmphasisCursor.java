/**
 * @file       IBookEmphasisCursor.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-1 上午11:07:58 
 */

package com.foxconn.ebook.reader.engine.model;

import java.util.List;


/**
 * 高亮選擇操作的接口.
 * <p>
 * 
 * Example:
 * <p>
 * <p>	ArrayList<BookEmphasis> list = mBook.getBookEmphasisCursor().getEmphasisList();
 * <p>	if (list.isEmpty()) {
 * <p>		return;
 * <p>	}
 * <p>		
 * <p>	mIEREngine.getAction().gotoHighlight(list.get(0));
 */
public interface IBookEmphasisCursor {
	
	/**
	 * 獲取高亮選擇的列表.
	 *
	 * @return 高亮選擇的列表
	 */
	public List<BookEmphasisInfo.BookEmphasis> getEmphasisList();
	
	/**
	 * 添加一個高亮選擇.
	 *
	 * @param emphasis 高亮選擇對象
	 * @return true, 添加成功. false, 添加失敗.
	 */
	public boolean addEmphasis(BookEmphasisInfo.BookEmphasis emphasis);
	
	/**
	 * 刪除一個高亮選擇.
	 *
	 * @param emphasis 高亮選擇對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean delEmphasis(BookEmphasisInfo.BookEmphasis emphasis);
	
	/**
	 * 清空高亮列表.
	 *
	 * @return true, 清空成功. false, 清空失敗.
	 */
	public boolean clear();
}
