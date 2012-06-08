package com.easyview.ebook.jni;

import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.easyview.ebook.jni.SearchResult;
import com.easyview.ebook.reader.engine.core.ERManager;
import com.easyview.ebook.reader.engine.core.IEngineService;
import com.easyview.ebook.reader.engine.util.EngineConfig;
import com.easyview.ebook.reader.engine.util.Logger;

/*
 * PDF處理流程
 * 一、初始化Adobe
 * 打開書：	openFile(String url)
 * 設置視圖：	setViewportSize(int width, int height)
 * 設置字體： setFontSize(int size)
 * 
 * 
 * 二、顯示內容
 * 跳轉頁面：	goToPosition(int position)
 * 獲取內容：	getCurrentPage(int[] buf) 			// buf為數據內存
 * 保存內容：	Bitmap.createBitmap(buf, 			// buf為數據內存
 * 								PDF_WIDTH, 
 * 								PDF_HEIGHT, 
 * 								Config.RGB_565)
 * 顯示內容：通過ImageView將Bitmap對象顯示出來
 * 
 * 三、附加功能
 * 
 * 
 * 
 * 
 * 
 */

public class Adobe {
	public static String password = "ABCDEFGHIJKLMNOPQRSTUVWXYZ6789";

	protected String filterFileName(String filename) {
		String[] unSafeChar = { "#", "<", ">", "\\", "~", "%", "/", "^", "$" };
		for (String str : unSafeChar) {
			filename = filename.replace(str, "");
		}
		return filename;
	}

	public File renameFile(File file) {
		String dir = file.getParent();
		String filename = filterFileName(file.getName());
		String name = filename.substring(0, filename.lastIndexOf("."));
		String suffix = filename.substring(filename.lastIndexOf("."));
		if (dir == null)
			dir = "";
		String newpath = dir + "/" + filename;
		if (file.getAbsolutePath().equals(newpath)) {
			return file;
		} else {
			File dfile = new File(newpath);
			int copy = 1;
			while (dfile.exists()) {
				dfile = new File(dir + "/" + name + "(" + copy + ")" + suffix);
				copy++;
			}
			try {
				dfile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			boolean flag = true;
			try {
				flag = file.renameTo(dfile);
			} catch (Exception ee) {
				flag = false;
			}
			Log.i("AdobeNativeInterface", "renameTO()" + flag);
			if (flag)
				return dfile;
			else
				return file;
		}
	}

	// 打開文件 *
	public native int openFile(String url);

	// 取消處理 *
	public native void cancelProcessing();

	// 獲取當前高亮的頁
	public native void getCurrentHighlightedPage(int[] pagebuf);

	// 跳轉到某章節
	public native int goToChapterIndex(int chapterIndex);

	// 獲取章節頁的位置
	public native double getChapterPagePositionFromIndex(int idx);

	// 查找下一個文本
	public native boolean findNextText(String text);

	// 查找上一個文本
	public native boolean findPreviousText(String text);

	// 重置查找
	public native void resetFindText();

	// 設置顯示內容的視圖大小
	public native void setViewportSize(int w, int h);

	// 關閉文件 *
	public native int closeFile();

	// 獲取文件總頁數 *
	public native int getNumPages();

	// 獲取指定頁的內容，需要傳入int數組以獲得返回的數據 *
	public native int getPage(int pageno, int[] mypage_buf);

	// 獲取下一頁內容，adobe會自動處理下頁內容 *
	public native boolean getNextPage();

	// 獲取上一頁內容，adobe會自動處理上一頁內容 *
	public native boolean getPreviousPage();

	// 獲取當前頁內容，需傳入int數組以獲得返回的數據 *
	public native int getCurrentPage(int[] pagebuf);

	// 獲取Meta數據 *
	public native String getMetaData(String metaQuery);

	// 獲取章節數
	public native int getNumChapters();

	// 獲取章節列表
	public native String[] getChapterList();

	// 根據章節名跳轉章節
	public native int goToChapter(String chapter);

	// 根據章節名獲取章節頁的位置
	public native double getChapterPagePosition(String title);

	// 獲取當前的位置信息
	public native String getCurrentLocation();

	// 根據書簽信息跳轉到其位置
	public native int goToLocation(String bookmark);

	// 跳轉指定頁數 *
	public native int goToPosition(double position);

	// 設置字體大小 *
	public native void setFontSize(int size);

	// 獲取頁位置 *
	public native double getPagePosition(String bookmark);

	public native int setPassHash(String username, String cc);

	public native int setHash(String hash);

	public native String getHash();

	// 設置橫屏
	public native int setScreenlandscape(boolean scape);

	// 設置是否顯示背景
	public native void setBackground(boolean background);

	// 根據坐標位置設置高亮
	public native String setHighlightbyxy(double x1, double y1, double x2,
			double y2, int z);

	// 根據字符位置設置高亮
	public native boolean setHighlightbyloc(String start, String end, int z);

	// 根據字符位置刪除高亮
	public native boolean delHighlight(String start, String end, int z);

	// 刪除所有高亮
	public native void delAllHighlight();

	// 跳轉到下個文本
	public native boolean goText(String netext);

	// 是否包含下個文本
	public native boolean hasNextText(String netext);

	// 是否包含上個文本
	public native boolean hasPreviousText(String pretext);

	// 查找下個書簽
	public native String findNextMark();

	// 查找上個書簽
	public native String findPreviousMark();

	// 書簽比較
	public native int compare(String bookmark1, String bookmark2);

	// 點擊測試
	public native String hitTest(double x1, double y1);

	// 獲取搜索結果
	public native SearchResult[] getSearchResult(String text, int max, int cont);

	// 獲取當前章節索引
	public native int getCurrentChapterIndex();

	// 通過位置獲取Summary信息
	public native String getSummaryByLocation(String bookmark);

	// 獲取鏈接數量
	public native int getLinkCount();

	// 高亮顯示鏈接
	public native void highlightLink(int index);

	// 選擇鏈接
	public native void selectLink(int index);

	// 設置PDF密碼
	public native int setPDFPassword(String pwd);

	// 鏈接測試
	public native int linkTest(double x, double y, String[] urlStrings);

	// 設置權限
	public native int setUserActivation(String username, String password);

	public native int setDeviceDeactivation();

	public native int run(String[] parameters);

	public native String getScreenStart();

	public native String getScreenEnd();

	public native boolean checkContinuous(String previousEnd,
			String currentStart);

	static {
		// Remove Adobe RMSDK support
//		IEngineService ies = (IEngineService) ERManager
//				.getService(ERManager.ERENGINE_SERVICE);
//
//		String libPath = ies.getConfig().getLibraryPath();
//		String soPath = libPath + File.separator + "libadobehost.so";
//		
//		try {
//			if (libPath.equals(EngineConfig.DEFAULT_LIBRARY_LOCAL)) {
//				System.loadLibrary("adobehost");
//			} else {
//				System.load(soPath);
//			}
//		} catch (Exception e) {
//			Logger.eLog("EREngine", "load libraryerror = " + e.getMessage());
//		}
	}
}
