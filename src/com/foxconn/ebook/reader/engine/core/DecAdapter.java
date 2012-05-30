/**
 * @file       DecAdapter.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-3-8 下午02:22:50 
 */

package com.foxconn.ebook.reader.engine.core;

/**
 * 閱讀器的適配器類，繼承該抽象類實現具體的閱讀器功能.
 */
abstract public class DecAdapter {
	private boolean mAvailable = false;

	/**
	 * Mark the adapter can work or not, when api calling error, set stop mark *
	 * true. TaskManager may set it false when it call api again.
	 */
	protected void setStop(boolean stop) {
		this.mAvailable = stop;
	}

	/**
	 * Get the adapter status, can work or not.
	 */
	protected boolean isStop() {
		return mAvailable;
	}

	/**
	 * Cancel api processing, in particular, the handling thread must be
	 * canceled.
	 */
	abstract protected void cancelProcessing();

	abstract protected void free();
}
