/**
 * @file       IBookChapterCursor.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-7 上午08:31:57 
 */

package com.foxconn.ebook.reader.engine.model;

import java.util.List;

import com.foxconn.ebook.reader.engine.model.ChapterTreeInfo.Chapter;

/**
 * 章節處理接口
 * <p>
 * 
 * Example:
 * <p>
 * 
 * <p>  void loadChapters() {
 * <p>		Book book = getEasyViewer().getBook();
 * <p>		IBookChapterCursor bcOps = book.getBookChapterCursor();
 * <p>		
 * <p>		// 獲取章節列表
 * <p>		ArrayList<Chapter> chapterList = bcOps.getChapterList();
 * <p>
 * <p>		// 填充listView
 * <p>		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
 * <p>				android.R.layout.simple_list_item_1);
 * <p>		for (Chapter chapter : list) {
 * <p>			adapter.add(chapter.title);
 * <p>		}
 * <p>
 * <p>		mTopicListView.setAdapter(adapter);
 * <p>	}
 *
 */
public interface IBookChapterCursor {
	
	/**
	 * 章節是否已經加載.
	 * 
	 * @return true, 已加載. false, 未加載.
	 */
	public boolean chapterIsLoad();
	
	/**
	 * 章節列表是否為空.
	 * 
	 * @return true, 列表為空. false, 列表非空.
	 */
	public boolean chapterIsEmpty();
	
	/**
	 * 獲取章節對象的列表.
	 * 
	 * @return 章節對象列表
	 */
	public List<Chapter> getChapterList();
	
	/**
	 * 獲取對應章節序號的頁碼.
	 * 
	 * @param index
	 *            章節序號
	 * @return 頁碼序號
	 */
	public int getPageIndexByChapterIndex(int index);
	
	/**
	 * 根据页码序号获取后一章节的序号.
	 * 
	 * @param pageIndex
	 *            页码序号
	 * @return 后一章节的序号
	 */
	public int getNextChapterIndex(int pageIndex);
	
	/**
	 * 根据页码序号获取前一章节的序号.
	 * 
	 * @param pageIndex
	 *            页码序号
	 * @return 前一章节的序号
	 */
	public int getPreviousChapterIndex(int pageIndex);
	
	/**
	 * 检查是否存在下一章节.
	 * 
	 * @return true, 存在. false, 不存在
	 */
	public boolean hasNextChapter();
	
	/**
	 * 检查是否存在前一章节.
	 * 
	 * @return true, 存在. false, 不存在
	 */
	public boolean hasPreviousChapter();
	
	/**
	 * 获取最大的目录序号.
	 * 
	 * @return 最大的目录序号
	 */
	public int getMaxChapterNum();
	
	/**
	 * 获取当前目录的序号.
	 * 
	 * @return 当前目录的序号
	 */
	public int getCurChapterIndex();
	
	/**
	 * 获取每个目录标题对应的页码.
	 * 
	 * @return 目录标题对应的页码，为int[]类型.
	 */
	public int[] getChapterPageNums();
	
	/**
	 * 根据目录标题获取目录的序号.
	 * 
	 * @param title
	 *            目录标题
	 * @return 该标题的目录序号
	 */
	public int getChapterIndexByTitle(String title);
	
	/**
	 * 获取目录标题列表.
	 * 
	 * @return 目录标题的列表，为String[]类型
	 */
	public String[] getChapterTitles();
}
