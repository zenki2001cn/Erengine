/**
 * @file       CommentModel.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-28 上午11:06:58 
 */

package com.easyview.ebook.reader.engine.module.comments;

/**

 * * * ----------------------------------------------
 * * * ----------------------------------------------
 * * * ---------------------------------------------- *
 */
public class CommentModel {
	public CommentModel() {
		
	}
	
	private String mCommentPath;
	private String mCommentLocation;
	
	public void setCommentPath(String path) {
		mCommentPath = path;
	}
	
	public String getCommentPath() {
		return mCommentPath;
	}
	
	public void setCommentLocation(String location) {
		mCommentLocation = location;
	}
	
	public String getCommentLocation() {
		return mCommentLocation;
	}
}
