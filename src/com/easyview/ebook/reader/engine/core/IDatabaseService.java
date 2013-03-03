/**
 * @file       IDatabaseService.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-19 上午08:35:52 
 */

package com.easyview.ebook.reader.engine.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo;
import com.easyview.ebook.reader.engine.model.BookMarkInfo;

/**
 * 數據庫的操作接口.<p>
 * 該接口針對Engine提供的數據庫Provider進行操作，需要在XML中配置以下Provider:<p>
 * <provider android:authorities="com.easyview.ebook.reader.engine.util.provider"
 *		android:name="com.easyview.ebook.reader.engine.util.provider.ERSqliteProvider"> <p>
 * </provider> <p>
 * 
 * 通過ERManager來獲取該數據庫接口:<p>
 * IDatabaseService dbs = (IDatabaseService) ERManager.getService(ERManager.DATABASE_SERVICE);<p>
 * dbs.addBook(book);<p>
 * dbs.queryBook(book);<p>
 * dbs.addBookmark(bookmark);<p>
 */
public interface IDatabaseService {
	
	/** 书籍信息的表名. */
	public static final String BOOKINFO_TABLE_NAME = "bookinfo";
	
	/** 书签信息的表名. */
	public static final String BOOKMARK_TABLE_NAME = "bookmark";
	
	/** 高亮信息的表名. */
	public static final String BOOKEMPHASIS_TABLE_NAME = "bookemphasis";
	
	/** 数据库名称. */
	public static final String DATABASE_NAME = "ERProvider.db";
	
	/**
	 * 根据ID检查该书籍是否存在数据库中.
	 *
	 * @param book 书籍对象
	 * @return true, 存在. false, 不存在.
	 */
	public boolean checkBookInDb(Book book);
	
	/**
	 * 添加书籍到数据库中.
	 *
	 * @param book Book对象
	 * @return true, 添加成功. false, 添加失败.
	 */
	public boolean addBook(Book book);

	/**
	 * 添加书籍到数据库中.
	 *
	 * @param filePath 文件完整路径
	 * @return true, 添加成功. false, 添加失败.
	 */
	public boolean addBook(String filePath);
	
	/**
	 * 批量添加书籍到数据库中.
	 *
	 * @param filePath 文件完整路徑的列表
	 * @return true, 添加成功. false, 添加失败.
	 */
	public boolean addBook(String[] filePath);
	
	/**
	 * 删除书籍信息.
	 *
	 * @param book Book对象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBook(Book book);
	
	/**
	 * 刪除書籍信息.
	 *
	 * @param filePath 書籍文件完整路徑
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBook(String filePath);
	
	/**
	 * 清空所有書籍信息，同時將書簽等其他相關信息清除.
	 *
	 * @return true, 清空成功. 清空失敗.
	 */
	public boolean deleteAllBooks();

	/**
	 * 更新書籍的信息.
	 *
	 * @param book Book對象
	 * @return true, 更新成功. false, 更新失敗.
	 */
	public boolean updateBook(Book book);

	/**
	 * 查詢對應路徑名的書籍ID.
	 *
	 * @param filePath 書籍的完整路徑.
	 * @return 書籍的ID.
	 */
	public int queryBook(String filePath);
	
	/**
	 * 查詢所有書籍ID.
	 *
	 * @return 書籍ID的列表
	 */
	public int[] queryBooksId();
	
	/**
	 * 查詢所有書籍完整路徑.
	 *
	 * @return 數據庫中的書籍路徑列表.
	 */
	public String[] queryBooksPath();
	
	/**
	 * 根據書籍的ID查詢書籍信息.
	 *
	 * @param book Book對象
	 * @return true, 查詢成功. false, 查詢失敗或沒有對應該書籍ID的信息.
	 */
	public boolean queryBook(Book book);

	/**
	 * 添加書簽信息.
	 *
	 * @param bookmark 書簽對象
	 * @return true, 添加靄成功. false, 添加失敗.
	 */
	public boolean addBookmark(BookMarkInfo.Bookmark bookmark);

	/**
	 * 刪除書簽信息.
	 *
	 * @param bookmark 書簽對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBookmark(BookMarkInfo.Bookmark bookmark);

	/**
	 * 更新書簽信息.
	 *
	 * @param bookmark 書簽對象
	 * @return true, 更新成功. false, 更新失敗.
	 */
	public boolean updateBookmark(BookMarkInfo.Bookmark bookmark);

	/**
	 * 根據頁碼位置，刪除該書籍相應的書簽信息.
	 *
	 * @param book Book對象
	 * @param location 頁碼位置
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBookmark(Book book, String location);
	
	/**
	 * 根據相應的書籍ID, 刪除所有書籍信息.
	 *
	 * @param book Book對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteAllBookmark(Book book);
	
	/**
	 * 根據頁碼位置，查詢書簽信息，并將信息更新到Book對象.
	 *
	 * @param book Book對象
	 * @param location 頁碼位置
	 * @return true, 該位置已存在書簽. false, 該位置不存在書簽.
	 */
	public boolean queryBookmark(Book book, String location);
	
	/**
	 * 查詢對應書籍的所有書簽信息，并將信息更新到Book對象.
	 *
	 * @param book Book對象
	 * @return true, 查詢成功. false, 查詢失敗或該書籍沒有任何書簽.
	 */
	public boolean queryAllBookmarks(Book book);

	/**
	 * 添加高亮對象.
	 *
	 * @param emphasis 高亮對象
	 * @return true, 添加成功. false, 添加失敗.
	 */
	public boolean addBookEmphasis(BookEmphasisInfo.BookEmphasis emphasis);

	/**
	 * 根據頁碼位置，刪除該頁的所有高亮對象.
	 *
	 * @param book Book對象
	 * @param location 頁碼位置
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBookEmphasis(Book book, String location);
	
	/**
	 * 刪除單個高亮對象.
	 *
	 * @param emphasis 高亮對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteBookEmphasis(BookEmphasisInfo.BookEmphasis emphasis);

	/**
	 * 更新對應的高亮對象信息.
	 *
	 * @param emphasis 高亮對象
	 * @return true, 更新成功. false, 更新失敗.
	 */
	public boolean updateBookEmphasis(BookEmphasisInfo.BookEmphasis emphasis);

	/**
	 * 根據書籍ID，刪除所有對應該書籍的高亮對象.
	 *
	 * @param book Book對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean deleteAllBookEmphasis(Book book);
	
	/**
	 * 根據頁位置，查詢該頁中的所有高亮對象，并將結果更新到書籍對象中.
	 *
	 * @param book Book對象
	 * @param location 頁位置
	 * @return true, 查詢成功. false, 查詢失敗或該頁位置無任何高亮對象.
	 */
	public boolean queryBookEmphasis(Book book, String location);
	
	/**
	 * 根據書籍ID查詢該書所有的高亮對象，并更新至書籍對象中.
	 *
	 * @param book Book對象
	 * @return true, 查詢成功. false, 查詢失敗或該書籍無任何高亮信息.
	 */
	public boolean queryAllBookEmphasis(Book book);
	
	/**
	 * 通過自定義的SQL語句進行數據庫操作，如插入、刪除和更新等操作.
	 *
	 * @param sql the sql
	 */
	public void executeSQL(String sql); 
	
	/**
	 * 獲取書籍信息的表名.
	 *
	 * @return 書籍信息的表名
	 */
	public String getBookTableName();
	
	/**
	 * 獲取書簽信息的表名.
	 *
	 * @return the book mark table name
	 */
	public String getBookMarkTableName();
	
	/**
	 * 獲取高亮信息的表名.
	 *
	 * @return 獲取高亮信息的表名
	 */
	public String getBookEmphasisTableName();
	
	/**
	 * 獲取數據庫名.
	 *
	 * @return 數據庫名
	 */
	public String getDatabaseName();
}
