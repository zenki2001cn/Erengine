/**
 * @file       IBookMarkCursor.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-13 上午11:01:19 
 */

package com.foxconn.ebook.reader.engine.model;

import java.util.List;

import com.foxconn.ebook.reader.engine.model.BookMarkInfo.Bookmark;

/**
 *  書簽的操作接口.
 *  <p>
 *  
 *  Example:
 *  <p> // 初始化書簽列表
 *  <p> private void initBookmarkList() {
 *  <p>		List<Bookmark> bookmarkList = book.getBookMarkCursor().getBookmarkList();
 *  <p>		mBookmarkAapter = new BookmarkListAdapter(getContext(), bookmarkList);
 *  <p>		mListView.setAdapter(mBookmarkAapter);
 *  <p>	}
 *  <p>	
 *  <p> // 跳轉書簽
 *  <p>	private OnItemClickListener cListBookmarkListener = new OnItemClickListener() {
 *  <p>		@Override
 *  <p>		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 *  <p>			mBookmarkTogo = (Bookmark) mListView.getItemAtPosition(position);
 *  <p>			book.getBookmarkOps().setBookmarkTogo(mBookmarkTogo);
 *  <p>			mIEREngine.getAction().gotoBookmark();
 *  <p>		}
 *  <p>	};
 */
public interface IBookMarkCursor {
	
	/**
	 * 添加一個書簽.
	 *
	 * @param bookmark 書簽數據對象
	 * @return true, 添加成功. false, 添加失敗.
	 */
	public boolean addBookmark(Bookmark bookmark);
	
	/**
	 * 刪除一個書簽.
	 *
	 * @param bookmark 書簽數據對象
	 * @return true, 刪除成功. false, 刪除失敗.
	 */
	public boolean delBookmark(Bookmark bookmark);
	
	/**
	 * 獲取書簽列表.
	 *
	 * @return 返回書簽列表.
	 */
	public List<Bookmark> getBookmarkList();
	
	/**
	 * 獲取將要跳轉的書簽數據對象.<p>
	 * 通常從書簽頁打開書籍時，需要設置該書簽數據對象.
	 *
	 * @return 將要跳轉的書簽數據對象.
	 */
	public Bookmark getBookmarkTogo();
	
	/**
	 * 設置將要跳轉的書簽數據對象.<p>
	 * 通常從書簽頁打開書籍時，需要設置該書簽數據對象.
	 *
	 * @param bookmark 將要跳轉的書簽數據對象.
	 */
	public void setBookmarkTogo(Bookmark bookmark);
	
	/**
	 * 清空書簽列表.
	 *
	 * @return true, 清空成功. false, 清空失敗.
	 */
	public boolean cleanBookmarks();
}
