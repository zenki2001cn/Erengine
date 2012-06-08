/**
 * @file       CharsetDetectorProxy.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-22 上午10:06:10 
 */

package com.easyview.ebook.reader.engine.util.charsetcodec;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.geometerplus.fbreader.library.Book;

import com.easyview.ebook.reader.engine.util.Logger;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class CharsetDetector2 {
	static private final String TAG = "CharsetDetector2";

	private InputStream mStream;
	CodepageDetectorProxy mDetector;

	public CharsetDetector2(Book book) {
		try {
			File file = new File(book.File.getPath());
			mStream = new BufferedInputStream(new FileInputStream(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		initDetector();
	}

	public String getDecodeName() {
		String name = "UTF-8";
		Charset charset = null;

		Logger.eLog(TAG, "getDecodeName start");
		try {
			charset = mDetector.detectCodepage(mStream, 65535);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (charset != null) {
			name = charset.name();
			Logger.eLog(TAG, "getDecodeName = " + name);
		} else {
			Logger.eLog(TAG, "getDecodeName null");
		}
		
		return name;
	}
	
	private void initDetector() {
		mDetector = CodepageDetectorProxy.getInstance();

		mDetector.add(new ParsingDetector(true));
		mDetector.add(JChardetFacade.getInstance());
		mDetector.add(ASCIIDetector.getInstance());
		mDetector.add(UnicodeDetector.getInstance());
	}
}
