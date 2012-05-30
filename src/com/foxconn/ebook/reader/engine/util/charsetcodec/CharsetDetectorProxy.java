/**
 * @file       CharsetDetectorProxy.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-22 上午10:06:10 
 */

package com.foxconn.ebook.reader.engine.util.charsetcodec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.geometerplus.fbreader.library.Book;

import com.foxconn.ebook.reader.engine.util.Logger;

public class CharsetDetectorProxy {
	static private final String TAG = "CharsetDetectorProxy";

	private InputStream mStream;
	private ArrayList<ICharsetDetect> mDetectorList;
	private final String DEFAULT_CHARSET = "GB2312";

	public CharsetDetectorProxy(Book book) {
		try {
			mStream = new FileInputStream(new File(book.File.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		initDetector();
	}

	public String getDecodeName() {
		String decode = DEFAULT_CHARSET;

		String temp = null;
		for (ICharsetDetect detect : mDetectorList) {
			if (detect != null) {
				temp = detect.detectCodepage(mStream, 2048);
				if (temp != null) {
					decode = temp;
					Logger.dLog(TAG, "getDecodeName = " + decode);
					break;
				}
			}
		}

		return decode;
	}

	private void initDetector() {
		mDetectorList = new ArrayList<ICharsetDetect>();

		mDetectorList.add(new UnicodeDetector());
		mDetectorList.add(new JCharDetector());
	}
}
