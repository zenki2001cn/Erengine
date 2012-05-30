/**
 * @file       BaseRecorder.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-2-18 上午09:13:14 
 */

package com.foxconn.ebook.reader.engine.util.recorder;

import com.foxconn.ebook.reader.engine.model.Book;
import com.foxconn.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.foxconn.ebook.reader.engine.model.BookMarkInfo;
import com.foxconn.ebook.reader.engine.model.IBookEmphasisCursor;
import com.foxconn.ebook.reader.engine.model.IBookMarkCursor;

/**
 * 數據記錄的操作接口.<p>
 * 
 * 當需要保存已打開的書籍信息時，需要實現該接口，并將對象傳遞給EREngine.<p>
 * 所有的書籍信息可以從Book類中獲取，具體的信息相關接口如下：<p>
 * 
 * 書籍屬性信息: 參見 {@link Book}.get*方法.<p>
 * 書簽相關信息: 參見 {@link IBookMarkCursor}接口.<p>
 * 高亮選擇信息: 參見 {@link IBookEmphasisCursor}接口.<p>
 */
public interface IRecorder {
	
	/**
	 * 保存書籍的信息，包括屬性信息、書簽列表、高亮選擇列表.
	 *
	 * @param book Book對象
	 * @return true, 保存成功. false, 保存失敗
	 * @throws Exception the exception
	 */
	public boolean saveBookDb(Book book) throws Exception;
	
	/**
	 * 讀取書籍的信息，包括屬性信息、書簽列表、高亮選擇列表.
	 *
	 * @param book the book
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean loadBookDb(Book book) throws Exception;
	
	/**
	 * 添加書簽到數據庫記錄.
	 *
	 * @param bookmark 書簽對象
	 * @return true, 添加成功. false, 添加失敗.
	 * @throws Exception the exception
	 */
	public boolean addBookMarkDb(BookMarkInfo.Bookmark bookmark) throws Exception;
	
	/**
	 * 刪除書簽到數據庫記錄.
	 *
	 * @param bookmark 書簽對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 * @throws Exception the exception
	 */
	public boolean delBookMarkDb(BookMarkInfo.Bookmark bookmark) throws Exception;
	
	/**
	 * 清空書簽記錄.
	 *
	 * @return true, 清空成功. false, 清空失敗.
	 * @throws Exception the exception
	 */
	public boolean cleanBookMarkDb(Book book) throws Exception;
	
	/**
	 * 添加高亮選擇到數據庫.
	 *
	 * @param emphasis 高亮選擇對象
	 * @return true, 添加成功. false, 添加失敗
	 * @throws Exception the exception
	 */
	public boolean addBookEmphasisDb(BookEmphasis emphasis) throws Exception;
	
	/**
	 * 刪除高亮選擇對象.
	 *
	 * @param emphasis 高亮選擇對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 * @throws Exception the exception
	 */
	public boolean delBookEmphasisDb(BookEmphasis emphasis) throws Exception;
	
	/**
	 * 清空高亮選擇列表.
	 *
	 * @return true, 清空成功. false, 清空失敗.
	 * @throws Exception the exception
	 */
	public boolean cleanBookEmphasisDb(Book book) throws Exception;
}
