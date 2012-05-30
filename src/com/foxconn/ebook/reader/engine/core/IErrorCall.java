/**
 * @file       IErrorCall.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-3 上午08:56:46 
 */

package com.foxconn.ebook.reader.engine.core;

/**
 * 錯誤信息碼的回調接口.
 * 
 * 錯誤處理：
 * 錯誤處理需要實現IErrorCall接口回調，當相應錯誤發生時，Engine將自動處理這些回調.
 * <p>
 * Example:
 * <code>
 * <p>
 * <p> 	IErrorCall inputPdfPassword = new IErrorCall() {
 * <p> 		@Override
 * <p>		public void handleError() {
 * <p>			showPdfInputDialog();
 * <p>		}
 * <p>	};
 * <p>
 * <p>	mIEREngine.setErrorHandler(AdobeMsgCode.STATE_ERROR_PDF_T3_NEED_PASSWORD, inputPdfPassword);
 * </code>
 */
public interface IErrorCall {
	
	/**
	 * Handle error.
	 */
	void handleError();
}
