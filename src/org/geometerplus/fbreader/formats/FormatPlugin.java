/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.formats;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.charsetcodec.CharsetDetectorProxy;

public abstract class FormatPlugin {
	static private final String TAG = "FormatPlugin";

	public abstract boolean acceptsFile(ZLFile file);

	public abstract boolean readMetaInfo(Book book);

	public abstract boolean readModel(BookModel model);

	public abstract ZLImage readCover(ZLFile file);

	public abstract String readAnnotation(ZLFile file);

	// add by zenki-zha-xxx begin
	protected boolean getEncodingAndLanguageForHtml(Book book,
			InputStream stream) {
		boolean getit = false;

		String encoding = null;
		String language = null;

		encoding = getEncodingForHtml(stream);
		if ((encoding != null) && Charset.isSupported(encoding)) {
			getit = true;

			language = getLanguage(encoding);

			book.setEncoding(encoding);
			book.setLanguage(language);
		}

		Logger.dLog(TAG, "getEncodingForHtml = " + encoding + " language = "
				+ language);

		return getit;
	}

	protected void guessEncodingAndLanguage(Book book, InputStream stream) {
		String encoding;
		String language;

		encoding = guessEncoding(book);
		language = getLanguage(encoding);

		book.setEncoding(encoding);
		book.setLanguage(language);

		Logger.dLog(TAG, "guessEncodingAndLanguage = " + book.getEncoding()
				+ " language = " + book.getLanguage());
	}

	private String getEncodingForHtml(InputStream is) {
		String encoding = null;
		int charsetId = -1;
		int endId = -1;

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String buf;

		try {
			while ((buf = reader.readLine()) != null) {
				if (buf.indexOf("<meta") != -1) {
					charsetId = buf.indexOf("charset=");
					if (charsetId != -1) {
						endId = buf.indexOf('"', charsetId);
						encoding = buf.substring(charsetId + 8, endId)
								.toUpperCase();
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return encoding;
	}

	private String guessEncoding(Book book) {
		String encoding = "UTF-8";

		CharsetDetectorProxy detector = new CharsetDetectorProxy(book);
		encoding = detector.getDecodeName();

		return encoding;
	}

	private String getLanguage(String encoding) {
		String language = "en";

		if ((encoding.equals("US-ASCII")) || (encoding.equals("ISO-8859-1"))) {
			language = "en";
		} else if ((encoding.equals("GBK")) || (encoding.equals("GB2312"))
				|| (encoding.equals("BIG5"))) {
			language = "zh";
		} else if ((encoding.equals("EUC-KR"))) {
			language = "kr";
		} else if ((encoding.equals("EUC-JP"))) {
			language = "jp";
		}

		return language;
	}
	// add by zenki-zha-xxx end
}
