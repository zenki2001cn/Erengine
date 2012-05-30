/**
 * @file       IBookSearchCursor.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-13 上午11:27:09 
 */

package com.foxconn.ebook.reader.engine.model;

import java.util.List;

import com.foxconn.ebook.reader.engine.core.IAction;
import com.foxconn.ebook.reader.engine.model.BookSearchInfo.BookSearchResult;

/**
 * 搜索操作接口.
 * <p>
 * 
 * Example:
 * <p>
 * <p>	IBookSearchCursor bookSearchOps = book.getBookSearchCursor();
 * <p>	// 設置搜索關鍵字
 * <p>	bookSearchOps.setSearchKeyword(keyword);
 * <p>	// 進入搜索模式
 * <p>	mIEREngine.getAction().enterSearchMode();
 * <p> 	// 搜索下一個結果
 * <p>	mIEREngine.getAction().searchGoNext();
 * <p>	// 搜索上一個結果
 * <p>	mIEREngine.getAction().searchGoPrevious();
 */
public interface IBookSearchCursor {
	
	/**
	 * 設置搜索關鍵字.
	 *
	 * @param key 搜索關鍵字
	 */
	public void setSearchKeyword(String key);
	
	/**
	 * 獲取搜索關鍵字.
	 *
	 * @return 搜索關鍵字
	 */
	public String getSearchKeyword();
	
	/**
	 * 添加搜索結果.
	 *
	 * @param results 搜索結果列表.
	 */
	public void addResults(BookSearchResult[] results);
	
	/**
	 * 獲取搜索結果列表.
	 *
	 * @return 搜索結果列表
	 */
	public List<BookSearchResult> getResults();
	
	/**
	 * 獲取搜索歷史列表.
	 *
	 * @return 索歷史列表， List<String>類型
	 */
	public List<String> getSeachHistory();
	
	/**
	 * 重置搜索結果.
	 */
	public void resetResults();
	
	/**
	 * 檢查是否有搜索結果.
	 *
	 * @return true, 結果不為空. false, 搜索為空.
	 */
	public boolean hasResults();
	
	/**
	 * 獲取下一個搜索結果.
	 *
	 * @return 下一個搜索結果
	 */
	public BookSearchResult getNextResult();
	
	/**
	 * 獲取上一個搜索結果.
	 *
	 * @return 上一個搜索結果
	 */
	public BookSearchResult getPreResult();
	
	/**
	 * 移動當前的搜索結果光標到搜索結果的末尾.<p>
	 * 由于PDF書的搜索模式不是一次性搜索出所有結果，而是采用分段式，因此調用該方法後，<p>
	 * 再調用 {@link IAction.searchGoNext} 方法會繼續搜索出更多結果.<p>
	 * 其他格式書籍暫時無需考慮該方法.
	 */
	public void moveCursorToEnd();
}
