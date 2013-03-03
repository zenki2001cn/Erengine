/**
 * @file       ICharsetDetect.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-22 下午03:14:21 
 */

package com.easyview.ebook.reader.engine.util.charsetcodec;

import java.io.InputStream;

public interface ICharsetDetect {
	public String detectCodepage(InputStream in, int length);
}
