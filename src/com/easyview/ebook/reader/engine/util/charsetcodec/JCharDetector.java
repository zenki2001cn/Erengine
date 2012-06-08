/**
 * @file       JCharDetector.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-22 下午03:19:22 
 */

package com.easyview.ebook.reader.engine.util.charsetcodec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;

import com.easyview.ebook.reader.engine.util.Logger;

public class JCharDetector implements ICharsetDetect {
	static private final String TAG = "JCharDetector";

	private ArrayList<Charset> mCharsetList;
	private final String[] CHARSET_LIST = { "GBK", "BIG5", "EUC-KR",
			"EUC-JP", "UTF-8", "US-ASCII" };

	public JCharDetector() {
		initCharsetList();
	}

	@Override
	public String detectCodepage(InputStream is, int length) {
		String decode = null;

		for (Charset charset : mCharsetList) {
			if (canDecode(is, charset)) {
				decode = charset.name();
				break;
			} else {
				Logger.eLog(TAG, "detectCodepage failed = " + charset.name());
			}
		}

		return decode;
	}

	private boolean canDecode(InputStream is, Charset charset) {
		ReadableByteChannel channel = Channels.newChannel(is);
		CharsetDecoder decoder = charset.newDecoder();

		ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
		CharBuffer charBuffer = CharBuffer.allocate(1024);

		boolean endOfInput = false;
		int n = -1;
		try {
			n = channel.read(byteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		byteBuffer.flip();

		endOfInput = (n == -1);

		CoderResult coderResult = CoderResult.OVERFLOW;
		try {
			coderResult = decoder.decode(byteBuffer, charBuffer, endOfInput);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		charBuffer.clear();
		if (coderResult.isOverflow()) {
			while (coderResult.isOverflow()) {
				try {
					coderResult = decoder.decode(byteBuffer, charBuffer,
							endOfInput);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
				charBuffer.clear();
			}
		}
		if (coderResult.isError() || coderResult.isUnmappable()
				|| coderResult.isMalformed()) {
			Logger.eLog(TAG, "canDecode " + charset.name() + " isError");
			return false;
		}
		byteBuffer.compact();

		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void initCharsetList() {
		mCharsetList = new ArrayList<Charset>();
		for (String charset : CHARSET_LIST) {
			mCharsetList.add(Charset.forName(charset));
		}

		// list all charsets
		// Map map = Charset.availableCharsets();
		// Iterator it = map.keySet().iterator();
		// while (it.hasNext()) {
		// String charsetName = (String) it.next();
		// Logger.dLog(TAG, "charset name = " + charsetName);
		// }
	}
}
