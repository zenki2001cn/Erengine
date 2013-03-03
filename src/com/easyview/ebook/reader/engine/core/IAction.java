/**
 * @file       IAction.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-7 下午02:00:28 
 */

package com.easyview.ebook.reader.engine.core;

import com.easyview.ebook.reader.engine.model.Book;
import com.easyview.ebook.reader.engine.model.BookSearchInfo;
import com.easyview.ebook.reader.engine.model.IBookChapterCursor;
import com.easyview.ebook.reader.engine.model.IBookMarkCursor;
import com.easyview.ebook.reader.engine.model.BookEmphasisInfo.BookEmphasis;
import com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark;

/**
 * 閱讀器操作接口.所有交互行為的方法通過該接口來提供.
 */
public interface IAction {
	/**
	 * 打開書籍，不帶執行參數. 打開書籍前，需要調用IEngineService.setBook(book)方法進行初始化
	 * <p>
	 * 如果從書簽位置打開，需要調用 {@link IBookMarkCursor#setBookmarkTogo(Bookmark)} 來設置書簽頁
	 * <p>
	 */
	public void openBook();

	/**
	 * 打開書籍，帶執行參數. 打開書籍前，需要調用IEngineService.setBook(Book book)方法進行初始化
	 * <p>
	 * 如果從書簽位置打開，需要調用 {@link IBookMarkCursor#setBookmarkTogo(Bookmark)} 來設置書簽頁
	 * <p>
	 * 
	 * @param preDo
	 *            開書之前需要執行的回調接口
	 * @param toDo
	 *            開書之後需要執行的回調接口
	 */
	public void openBook(IActionCall preDo, IActionCall toDo);

	/**
	 * Page 跳轉頁碼.
	 * 
	 * @param pageNum
	 *            需要跳轉的頁碼
	 */
	public void pageJumpTo(final int pageNum);

	/**
	 * Page 跳轉頁碼.
	 * 
	 * @param pageNum
	 *            需要跳轉的頁碼
	 * @param preDo
	 *            跳轉頁碼之前需要執行的回調接口
	 * @param toDo
	 *            跳轉頁碼之後需要執行的回調接口
	 */
	public void pageJumpTo(final int pageNum, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 向上翻頁，不帶回調接口.
	 */
	public void pageUp();

	/**
	 * 向上翻頁，帶回調接口.
	 * 
	 * @param preDo
	 *            向上翻頁之前需要執行的回調接口
	 * @param toDo
	 *            向上翻頁之後需要執行的回調接口
	 */
	public void pageUp(IActionCall preDo, IActionCall toDo);

	/**
	 * 向下翻頁，不帶回調接口.
	 */
	public void pageDown();

	/**
	 * 向下翻頁，帶回調接口.
	 * 
	 * @param preDo
	 *            向下翻頁之前需要執行的回調接口
	 * @param toDo
	 *            向下翻頁之後需要執行的回調接口
	 */
	public void pageDown(IActionCall preDo, IActionCall toDo);

	/**
	 * 根據縮放等級調整字體大小，不帶回調接口.
	 *
	 * @param lv 字體等級大小
	 */
	public void pageZoomLevel(int lv);
	
	/**
	 * 根據縮放等級調整字體大小，帶回調接口.
	 *
	 * @param lv 字體等級大小
	 * @param preDo
	 *            調整字體大小之前需要執行的回調接口
	 * @param toDo
	 *            調整字體大小之後需要執行的回調接口
	 */
	public void pageZoomLevel(int lv, IActionCall preDo, IActionCall toDo);
	
	/**
	 * 放大一級字體，不帶回調接口.
	 */
	public void pageZoomIn();

	/**
	 * 放大一級字體，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void pageZoomIn(IActionCall preDo, IActionCall toDo);

	/**
	 * 縮小一級字體，不帶回調接口.
	 */
	public void pageZoomOut();

	/**
	 * 縮小一級字體，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void pageZoomOut(IActionCall preDo, IActionCall toDo);

	/**
	 * 進入搜索模式，不帶回調接口.
	 */
	public void enterSearchMode();

	/**
	 * 進入搜索模式，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void enterSearchMode(IActionCall preDo, IActionCall toDo);

	/**
	 * 搜索下一個結果，不帶回調接口.
	 */
	public void searchGoNext();

	/**
	 * 搜索下一個結果，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void searchGoNext(IActionCall preDo, IActionCall toDo);

	/**
	 * 搜索前一個結果，不帶回調接口.
	 */
	public void searchGoPrevious();

	/**
	 * 搜索前一個結果，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void searchGoPrevious(IActionCall preDo, IActionCall toDo);

	/**
	 * 根據搜索結果，跳轉到相應頁面.不帶回調接口.
	 *
	 * @param result 搜索結果.
	 */
	public void searchGotoResult(BookSearchInfo.BookSearchResult result);

	/**
	 * 根據搜索結果，跳轉到相應頁面.帶回調接口.
	 *
	 * @param result 搜索結果.
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void searchGotoResult(BookSearchInfo.BookSearchResult result,
			IActionCall preDo, IActionCall toDo);

	/**
	 * 退出搜索，不帶回調接口.
	 */
	public void quitSearch();

	/**
	 * 退出搜索，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void quitSearch(IActionCall preDo, IActionCall toDo);

	/**
	 * 添加書簽，不帶回調接口.
	 */
	public void addBookmark();

	/**
	 * 添加書簽，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void addBookmark(IActionCall preDo, IActionCall toDo);

	/**
	 * 跳轉至書簽位置，不帶回調接口.
	 * <p>
	 * 需要調用 {@link IBookMarkCursor#setBookmarkTogo(Bookmark)} 來設置書簽頁
	 * <p>
	 */
	public void gotoBookmark();

	/**
	 * 跳轉至書簽位置，帶回調接口.
	 * <p>
	 * 需要調用 {@link IBookMarkCursor#setBookmarkTogo(Bookmark)} 來設置書簽頁
	 * <p>
	 * 
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void gotoBookmark(IActionCall preDo, IActionCall toDo);

	/**
	 * 跳轉至書簽位置，不帶回調接口.
	 *
	 * @param bookmark 將要跳轉至的書簽
	 */
	public void gotoBookmark(Bookmark bookmark);
	
	/**
	 * 跳轉至書簽位置，帶回調接口.
	 *
	 * @param bookmark 將要跳轉至的書簽
	 * @param preDo
	 *            執行動作之前的調用的回調接口
	 * @param toDo
	 *            執行動作之後的調用的回調接口
	 */
	public void gotoBookmark(Bookmark bookmark, IActionCall preDo, IActionCall toDo);
	
	/**
	 * 刪除書簽，不帶回調接口.
	 * 
	 * @param bookmark
	 *            需要刪除的書簽
	 * @see com.easyview.ebook.reader.engine.model.BookMarkInfo.Bookmark
	 */
	public void deleteBookmark(Bookmark bookmark);

	/**
	 * 刪除書簽，帶回調接口.
	 * 
	 * @param bookmark
	 *            需要刪除的書簽
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void deleteBookmark(final Bookmark bookmark, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 刪除所有書簽，不帶回調接口.
	 */
	public void delAllBookmark();

	/**
	 * 刪除所有書簽，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void delAllBookmark(IActionCall preDo, IActionCall toDo);

	/**
	 * 裝載章節信息，不帶回調接口. 章節越多，加載時間越長.
	 */
	public void loadChapterInformation();

	/**
	 * 裝載章節信息，帶回調接口. 章節越多，加載時間越長.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void loadChapterInformation(IActionCall preDo, IActionCall toDo);

	/**
	 * 通過章節的標題進行跳轉，不帶回調接口.
	 * <p>
	 *
	 * @param title 需要跳轉的章節標題名稱
	 * {@link IBookChapterCursor#getChapterList()} 獲取章節標題列表.
	 */
	public void chapterJumpByTitle(final String title);

	/**
	 * 通過章節的標題進行跳轉，帶回調接口.
	 * <p>
	 *
	 * @param title 需要跳轉的章節標題名稱
	 * @param preDo 執行動作之前調用的回調接口
	 * @param toDo 執行動作之後調用的回調接口
	 * {@link IBookChapterCursor#getChapterList()} 獲取章節標題列表.
	 */
	public void chapterJumpByTitle(final String title, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 通過章節序號進行跳轉，不帶回調接口.
	 * <p>
	 *
	 * @param index 章節序號
	 * {@link IBookChapterCursor#getChapterIndexByTitle(String)} 獲取章節序號.
	 */
	public void chapterJumpByIndex(final int index);

	/**
	 * 通過章節序號進行跳轉，帶回調接口.
	 * <p>
	 *
	 * @param index 章節序號
	 * @param preDo 執行動作之前調用的回調接口
	 * @param toDo 執行動作之後調用的回調接口
	 * {@link IBookChapterCursor#getChapterIndexByTitle(String)} 獲取章節序號.
	 */
	public void chapterJumpByIndex(final int index, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 跳轉至上一章節，不帶回調接口.
	 */
	public void chapterUp();

	/**
	 * 跳轉至上一章節，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void chapterUp(IActionCall preDo, IActionCall toDo);

	/**
	 * 跳轉至下一章節，不帶回調接口.
	 */
	public void chapterDown();

	/**
	 * 跳轉至上一章節，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void chapterDown(IActionCall preDo, IActionCall toDo);

	/**
	 * 關閉圖書，不帶回調接口. 退出應用時，需要關閉圖書，釋放資源，通常在onStop中調用.
	 */
	public void closeBook();

	/**
	 * 關閉圖書，帶回調接口. 退出應用時，需要關閉圖書，釋放資源，通常在onStop中調用.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void closeBook(IActionCall preDo, IActionCall toDo);

	/**
	 * 設置橫豎屏模式，不帶回調接口.
	 * <p>
	 * 设置横屏模式，当前屏幕變化後，調用該方法.
	 * <p>
	 * 该方法是用来通知Engine对变化后的视图做出处理，需要重新設置變化後的視圖寬度和高度.
	 * <p>
	 * 必要时重新初始化并载入Render对象.
	 *
	 * @param viewWidth 變化後的視圖寬度.
	 * @param viewHeight 變化後的視圖高度.
	 */
	public void resizeScreen(int viewWidth, int viewHeight);

	/**
	 * 設置橫豎屏模式，帶回調接口.
	 * <p>
	 * 设置横屏模式，当前屏幕變化後，調用該方法.
	 * <p>
	 * 该方法是用来通知Engine对变化后的视图做出处理，需要重新設置變化後的視圖寬度和高度.
	 * <p>
	 * 必要时重新初始化并载入Render对象.
	 *
	 * @param viewWidth 變化後的視圖寬度.
	 * @param viewHeight 變化後的視圖高度.
	 * @param preDo 執行動作之前調用的回調接口
	 * @param toDo 執行動作之後調用的回調接口
	 */
	public void resizeScreen(int viewWidth, int viewHeight, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 通過點擊屏幕坐標打開鏈接，不帶回調接口.
	 * 
	 * @param x
	 *            x坐標
	 * @param y
	 *            y坐標
	 */
	public void openLinkByTouch(int x, int y);

	/**
	 * 通過點擊屏幕坐標打開鏈接，帶回調接口.
	 * 
	 * @param x
	 *            x坐標
	 * @param y
	 *            y坐標
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void openLinkByTouch(final int x, final int y, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 處理TouchDown事件. 該事件目前只能處理字符高亮.
	 * 
	 * @param x
	 *            x坐標
	 * @param y
	 *            y坐標
	 */
	public void onTouchDown(final double x, final double y);

	/**
	 * 處理TouchMove事件. 該事件目前只能處理字符高亮.
	 * 
	 * @param x
	 *            x坐標
	 * @param y
	 *            y坐標
	 */
	public void onTouchMove(final double x, final double y);

	/**
	 * 處理TouchUp事件. 該事件目前只能處理字符高亮.
	 * 
	 * @param x
	 *            x坐標
	 * @param y
	 *            y坐標
	 */
	public void onTouchUp(final double x, final double y);

	/**
	 * 刪除高亮選擇，不帶回調接口.
	 * 
	 * @param emphasis
	 *            高亮選擇對象
	 */
	public void delHighlight(BookEmphasis emphasis);

	/**
	 * 刪除高亮選擇，帶回調接口.
	 * 
	 * @param emp
	 *            高亮選擇對象
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void delHighlight(final BookEmphasis emp, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 刪除所有高亮選擇，不帶回調接口.
	 */
	public void delAllHighlight();

	/**
	 * 刪除所有高亮選擇，帶回調接口.
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void delAllHighlight(IActionCall preDo, IActionCall toDo);

	/**
	 * 跳转至高亮选择，不带回调接口.
	 * 
	 * @param emphasis
	 *            高亮選擇對象
	 */
	public void gotoHighlight(BookEmphasis emphasis);

	/**
	 * 跳转至高亮选择，带回调接口.
	 * 
	 * @param emphasis
	 *            高亮選擇對象
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void gotoHighlight(final BookEmphasis emphasis, IActionCall preDo,
			IActionCall toDo);

	/**
	 * 抓取當前頁的文本內容，不带回调接口.
	 * 抓取的文本內容通過Book對象獲取.
	 * @see Book#getPageText().
	 */
	public void fetchText();
	
	/**
	 * 抓取當前頁的文本內容，带回调接口.
	 * 抓取的文本內容通過Book對象獲取.
	 * @see Book#getPageText().
	 * 
	 * @param preDo
	 *            執行動作之前調用的回調接口
	 * @param toDo
	 *            執行動作之後調用的回調接口
	 */
	public void fetchText(IActionCall preDo, IActionCall toDo);
	
	/**
	 * 取消所有尚未执行的动作.
	 */
	public void cancelActions();
}
