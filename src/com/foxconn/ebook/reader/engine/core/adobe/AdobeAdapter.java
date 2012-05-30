package com.foxconn.ebook.reader.engine.core.adobe;

import com.foxconn.ebook.jni.Adobe;
import com.foxconn.ebook.jni.SearchResult;
import com.foxconn.ebook.reader.engine.core.DecAdapter;
import com.foxconn.ebook.reader.engine.core.EngineCode;
import com.foxconn.ebook.reader.engine.core.EngineCode.EngineMsgCode;
import com.foxconn.ebook.reader.engine.util.Logger;

public class AdobeAdapter extends DecAdapter {
	private static final String TAG = "AdobeAdapter";
	private static Adobe mAdobe;
	private static AdobeAdapter mAdobeAdapter;
	private static EngineCode mEngineCode;

	protected static AdobeAdapter getInstance() {
		if (null == mAdobeAdapter) {
			mAdobeAdapter = new AdobeAdapter();
			mAdobe = new Adobe();
			mEngineCode = EngineCode.getInstance();
		}
		return mAdobeAdapter;
	}

	private AdobeAdapter() {
	}

	protected int openFile(String url) {
		Logger.vLog(TAG, "openFile enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.openFile(url);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		return code;
	}

	protected void cancelProcessing() {
		Logger.vLog(TAG, "cancelProcessing enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.cancelProcessing();
		Logger.vLog(TAG, "cancelProcessing exit.");
	}

	protected void getCurrentHighlightedPage(int[] pagebuf) {
		Logger.vLog(TAG, "getCurrentHighlightedPage>>>");
		if (isStop()) {
			return;
		}
		mAdobe.getCurrentHighlightedPage(pagebuf);
		Logger.vLog(TAG, "getCurrentHighlightedPage exit.");
	}

	protected int goToChapterIndex(int chapterIndex) {
		Logger.vLog(TAG, "goToChapterIndex enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.goToChapterIndex(chapterIndex);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "goToChapterIndex exit.");
		return code;
	}

	protected double getChapterPagePositionFromIndex(int idx) {
		Logger.vLog(TAG, "getChapterPagePositionFromIndex enter>>>");
		if (isStop()) {
			return -1;
		}
		double value = mAdobe.getChapterPagePositionFromIndex(idx);
		Logger.vLog(TAG, "getChapterPagePositionFromIndex exit.");
		return value;
	}

	protected boolean findNextText(String text) {
		Logger.vLog(TAG, "findNextText enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.findNextText(text);
		Logger.vLog(TAG, "findNextText exit.");
		return value;
	}

	protected boolean findPreviousText(String text) {
		Logger.vLog(TAG, "findPreviousText enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.findPreviousText(text);
		Logger.vLog(TAG, "findPreviousText exit.");
		return value;
	}

	protected void resetFindText() {
		Logger.vLog(TAG, "resetFindText enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.resetFindText();
		Logger.vLog(TAG, "resetFindText exit.");
	}

	protected void setViewPortSize(int w, int h) {
		Logger.vLog(TAG, "setViewportSize enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.setViewportSize(w, h);
		Logger.vLog(TAG, "setViewportSize exit.");
	}

	protected int closeFile() {
		Logger.vLog(TAG, "closeFile enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.closeFile();
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "closeFile exit.");
		return code;
	}

	protected int getNumPages() {
		Logger.vLog(TAG, "getNumPages enter>>>");
		if (isStop()) {
			return 1;
		}
		int value = mAdobe.getNumPages();
		Logger.vLog(TAG, "getNumPages exit.");
		return value;
	}

	protected int getPage(int pageno, int[] mypage_buf) {
		Logger.vLog(TAG, "getPage enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.getPage(pageno, mypage_buf);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "getPage exit.");
		return code;
	}

	protected boolean getNextPage() {
		Logger.vLog(TAG, "getNextPage enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.getNextPage();
		Logger.vLog(TAG, "getNextPage exit.");
		return value;
	}

	protected boolean getPreviousPage() {
		Logger.vLog(TAG, "getPreviousPage enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.getPreviousPage();
		Logger.vLog(TAG, "getPreviousPage exit.");
		return value;
	}

	protected int getCurrentPage(int[] pagebuf) {
		Logger.vLog(TAG, "getCurrentPage enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.getCurrentPage(pagebuf);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "getCurrentPage exit. code = " + code);
		return code;
	}

	protected String getMetaData(String metaQuery) {
		Logger.vLog(TAG, "getMetaData enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.getMetaData(metaQuery);
		Logger.vLog(TAG, "getMetaData exit.");
		return value;
	}

	protected int getNumChapters() {
		Logger.vLog(TAG, "getNumChapters enter>>>");
		if (isStop()) {
			return 0;
		}
		int value = mAdobe.getNumChapters();
		Logger.vLog(TAG, "getNumChapters exit.");
		return value;
	}

	protected String[] getChapterList() {
		Logger.vLog(TAG, "getChapterList enter>>>");
		if (isStop()) {
			return null;
		}
		String[] value = mAdobe.getChapterList();
		Logger.vLog(TAG, "getChapterList exit.");
		return value;
	}

	protected int goToChapter(String chapter) {
		Logger.vLog(TAG, "goToChapter enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.goToChapter(chapter);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "goToChapter exit.");
		return code;
	}

	protected double getChapterPagePosition(String title) {
		Logger.vLog(TAG, "getChapterPagePosition enter>>>");
		if (isStop()) {
			return 0;
		}
		double value = mAdobe.getChapterPagePosition(title);
		Logger.vLog(TAG, "getChapterPagePosition exit.");
		return value;
	}

	protected String getCurrentLocation() {
		Logger.vLog(TAG, "getCurrentLocation enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.getCurrentLocation();
		Logger.vLog(TAG, "getCurrentLocation exit.");
		return value;
	}

	protected int goToLocation(String bookmark) {
		long cur = System.currentTimeMillis();
		Logger.vLog(TAG, "goToLocation enter>>>");
		if (isStop()) {
			return 0;
		}
		int code = mAdobe.goToLocation(bookmark);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG,
				"goToLocation exit. cost time : "
						+ (System.currentTimeMillis() - cur));
		return code;
	}

	protected int goToPosition(double position) {
		long cur = System.currentTimeMillis();
		Logger.vLog(TAG, "goToPosition enter>>>");
		if (isStop()) {
			return 0;
		}
		int code = mAdobe.goToPosition(position);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG,
				"goToPosition exit. cost time: "
						+ (System.currentTimeMillis() - cur));
		return code;
	}

	protected void setFontSize(int size) {
		Logger.vLog(TAG, "setFontSize enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.setFontSize(size);
		Logger.vLog(TAG, "setFontSize exit.");
	}

	protected double getPagePosition(String bookmark) {
		Logger.vLog(TAG, "getPagePosition enter>>>");
		if (isStop()) {
			return 0;
		}
		double value = mAdobe.getPagePosition(bookmark);
		Logger.vLog(TAG, "getPagePosition exit value = " + value);
		return value;
	}

	protected int setPassHash(String username, String cc) {
		Logger.vLog(TAG, "setPassHash enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setPassHash(username, cc);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "setPassHash exit.");
		return code;
	}

	protected int setHash(String hash) {
		Logger.vLog(TAG, "setHash enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setHash(hash);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "setHash exit.");
		return code;
	}

	protected String getHash() {
		Logger.vLog(TAG, "setHash enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.getHash();
		Logger.vLog(TAG, "setHash exit.");
		return value;
	}

	// TODO need to delete
	protected int setScreenlandscape(boolean scape) {
		Logger.vLog(TAG, "setScreenlandscape enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setScreenlandscape(scape);
//		if (!mEngineCode.noError(code)) {
//			mEngineCode.setLastCode(code);
//			setStop(true);
//		} else {
//			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
//		}

		Logger.vLog(TAG, "setScreenlandscape exit.");
		return code;
	}

	protected void setBackground(boolean background) {
		Logger.vLog(TAG, "setBackground enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.setBackground(background);
		Logger.vLog(TAG, "setBackground exit.");
	}

	// (x,y):point
	// z:color value(0~5):light grey，yellow light，
	// blue dark ，grey tangerine ，green
	protected String setHighlightbyxy(double x1, double y1, double x2,
			double y2, int z) {
		Logger.vLog(TAG, "setHighlightbyxy enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.setHighlightbyxy(x1, y1, x2, y2, z);
		Logger.vLog(TAG, "setHighlightbyxy exit.");
		return value;
	}

	protected boolean setHighlightbyloc(String start, String end, int z) {
		Logger.vLog(TAG, "setHighlightbyloc enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.setHighlightbyloc(start, end, z);
		Logger.vLog(TAG, "setHighlightbyloc exit.");
		return value;
	}

	protected boolean delHighlight(String start, String end, int z) {
		Logger.vLog(TAG, "delHighlight enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.delHighlight(start, end, z);
		Logger.vLog(TAG, "delHighlight exit.");
		return value;
	}

	protected void delAllHighlight() {
		Logger.vLog(TAG, "delAllHighlight enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.delAllHighlight();
		Logger.vLog(TAG, "delAllHighlight exit.");
	}

	protected boolean goText(String netext) {
		Logger.vLog(TAG, "goText enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.goText(netext);
		Logger.vLog(TAG, "goText exit.");
		return value;
	}

	protected boolean hasNextText(String netext) {
		Logger.vLog(TAG, "hasNextText enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.hasNextText(netext);
		Logger.vLog(TAG, "hasNextText exit.");
		return value;
	}

	protected boolean hasPreviousText(String pretext) {
		Logger.vLog(TAG, "hasPreviousText enter>>>");
		if (isStop()) {
			return false;
		}
		boolean value = mAdobe.hasPreviousText(pretext);
		Logger.vLog(TAG, "hasPreviousText exit.");
		return value;
	}

	protected String findNextMark() {
		Logger.vLog(TAG, "findNextMark enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.findPreviousMark();
		Logger.vLog(TAG, "findNextMark exit.");
		return value;
	}

	protected String findPreviousMark() {
		Logger.vLog(TAG, "findPreviousMark enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.findNextMark();
		Logger.vLog(TAG, "findPreviousMark exit.");
		return value;
	}

	protected int compare(String bookmark1, String bookmark2) {
		Logger.vLog(TAG, "compare enter>>>");
		if (isStop()) {
			return -1;
		}
		int value = mAdobe.compare(bookmark1, bookmark2);
		Logger.vLog(TAG, "compare exit.");
		return value;
	}

	protected String hitTest(double x1, double y1) {
		Logger.vLog(TAG, "hitTest enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.hitTest(x1, y1);
		Logger.vLog(TAG, "hitTest exit.");
		return value;
	}

	protected SearchResult[] getSearchResult(String text, int max, int cont) {
		Logger.vLog(TAG, "getSearchResult enter>>> text = " + text + " max = "
				+ max + " cont = " + cont);
		if (isStop()) {
			return null;
		}
		SearchResult[] value = mAdobe.getSearchResult(text, max, cont);
		Logger.vLog(TAG, "getSearchResult exit. value = " + value);
		return value;
	}

	protected int getCurrentChapterIndex() {
		Logger.vLog(TAG, "getCurrentChapterIndex enter>>>");
		if (isStop()) {
			return 0;
		}
		int index = mAdobe.getCurrentChapterIndex();
		Logger.vLog(TAG, "getCurrentChapterIndex exit.");
		return index;
	}

	protected String getSummaryByLocation(String bookmark) {
		Logger.vLog(TAG, "getSummaryByLocation enter>>>");
		if (isStop()) {
			return null;
		}
		String value = mAdobe.getSummaryByLocation(bookmark);
		Logger.vLog(TAG, "getSummaryByLocation exit.");
		return value;
	}

	protected int getLinkCount() {
		Logger.vLog(TAG, "getLinkCount enter>>>");
		if (isStop()) {
			return 0;
		}
		int value = mAdobe.getLinkCount();
		Logger.vLog(TAG, "getLinkCount exit.");
		return value;
	}

	protected void highlightLink(int index) {
		Logger.vLog(TAG, "highlightLink enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.highlightLink(index);
		Logger.vLog(TAG, "highlightLink exit.");
	}

	protected void selectLink(int index) {
		Logger.vLog(TAG, "selectLink enter>>>");
		if (isStop()) {
			return;
		}
		mAdobe.selectLink(index);
		Logger.vLog(TAG, "selectLink exit.");
	}

	protected int setPDFPassword(String pwd) {
		Logger.vLog(TAG, "setPDFPassword enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setPDFPassword(pwd);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			// mAdobeError.setLastError(EngineErrorCode.NO_ERROR);
		}

		Logger.vLog(TAG, "setPDFPassword exit. code = " + code);
		return code;
	}

	protected int linkTest(double x, double y, String[] urlStrings) {
		Logger.vLog(TAG, "linkTest enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.linkTest(x, y, urlStrings);
//		if (!mEngineCode.noError(code)) {
//			mEngineCode.setLastCode(code);
//			setStop(true);
//		} else {
//			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
//		}

		Logger.vLog(TAG, "linkTest exit.");
		return code;
	}

	protected int setUserActivation(String username, String password) {
		Logger.vLog(TAG, "setUserActivation enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setUserActivation(username, password);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "setUserActivation exit.");
		return code;
	}

	protected int setDeviceDeactivation() {
		Logger.vLog(TAG, "setDeviceDeactivation enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.setDeviceDeactivation();
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "setDeviceDeactivation exit.");
		return code;
	}

	// TODO need to be checked of doc
	protected int run(String[] parameters) {
		Logger.vLog(TAG, "run enter>>>");
		if (isStop()) {
			return Integer.MAX_VALUE;
		}
		int code = mAdobe.run(parameters);
		if (!mEngineCode.noError(code)) {
			mEngineCode.setLastCode(code);
			setStop(true);
		} else {
			mEngineCode.setLastCode(EngineMsgCode.NO_ERROR);
		}

		Logger.vLog(TAG, "run exit.");
		return code;
	}

	protected void free() {
		if (mAdobeAdapter != null) {
			mAdobeAdapter = null;
		}

		if (mAdobe != null) {
			mAdobe = null;
		}

		if (mEngineCode != null) {
			mEngineCode.free();
			mEngineCode = null;
		}

		Logger.vLog(TAG, "AdobeAdapter free adapter: " + mAdobeAdapter
				+ " adobe: " + mAdobe + " errorCode: " + mEngineCode);
	}
}
