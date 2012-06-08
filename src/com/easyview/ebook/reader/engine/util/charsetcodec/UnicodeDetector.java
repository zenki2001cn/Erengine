/**
 * @file       UnicodeDetector.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-22 下午03:16:27 
 */

package com.easyview.ebook.reader.engine.util.charsetcodec;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class UnicodeDetector implements ICharsetDetect {
	static private final String TAG = "UnicodeDetector";

	public UnicodeDetector() {

	}

	@Override
	public String detectCodepage(InputStream in, int length) {
		byte[] bom = new byte[4];
		try {
			in.read(bom, 0, 4);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00
				&& bom[2] == (byte) 0xFE && bom[3] == (byte) 0xFF) // utf-32BE
			return ("UTF-32BE");
		if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE
				&& bom[2] == (byte) 0x00 && bom[3] == (byte) 0x00) // utf-32LE
			return ("UTF-32LE");
		if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB
				&& bom[2] == (byte) 0xBF) // utf-8
			return ("UTF-8");
		if (bom[0] == (byte) 0xff && bom[1] == (byte) 0xfe) // ucs-2le, ucs-4le,
			return ("UTF-16LE");
		if (bom[0] == (byte) 0xfe && bom[1] == (byte) 0xff) // utf-16 and ucs-2
			return ("UTF-16BE");
		if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00
				&& bom[2] == (byte) 0xfe && bom[3] == (byte) 0xff) // ucs-4
			return ("UCS-4");

		return null;
	}

}
