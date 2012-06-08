/**
 * @file       Page.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-2 上午10:23:14 
 */

package com.easyview.ebook.reader.engine.model;

import com.easyview.ebook.reader.engine.core.EngineCode;
import com.easyview.ebook.reader.engine.core.EngineCode.EngineMsgCode;

import android.graphics.Bitmap;

/**
 * 
 */
public class Page {
	private int buf[];
    private int number;
    private int linkCount;
    private String location;
    private String summary;
    private boolean loaded;
    private Bitmap pageContent;
    
    protected Page(int width, int height) {
    	try {
    		buf = new int[width * height];
		} catch (OutOfMemoryError e) {
			EngineCode.getInstance().setLastCode(EngineMsgCode.ENGINE_FATAL_OUTOF_MEMORY);
		}
    	
    	reset();
    }
    
	public Bitmap getPageContent() {
        return pageContent;
    }

    public void setPageContent(Bitmap pageContent) {
        this.pageContent = pageContent;
    }
    
    public void copy(Page page) {
        int[] temp = this.buf;
        this.buf = page.buf;
        page.buf = temp;
        this.pageContent = page.pageContent;
        this.number = page.number;
        this.linkCount = page.linkCount;
        this.location = page.location;
        this.summary = page.summary;
        this.loaded = page.loaded;
    }
    
    public int[] getBuf() {
        return buf;
    }

    public void setBuf(int[] buf) {
        this.buf = buf;
    }

    public int getPageNum() {
        return number;
    }

    public void setPageNum(int num) {
        this.number = num;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    public int getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(int linkCount) {
        this.linkCount = linkCount;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
    
    public void reset() {
        number = -1;
        linkCount = -1;
        location = "";
        summary = "";
        loaded = false;
        pageContent = null;
    }
}
