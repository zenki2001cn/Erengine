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

package org.geometerplus.fbreader.formats.html;

import java.io.IOException;
import java.io.InputStream;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

public class HtmlPlugin extends FormatPlugin {

	@Override
	public boolean acceptsFile(ZLFile file) {
		return "htm".equals(file.getExtension())
				|| "html".equals(file.getExtension());
	}

	// modify by zenki-zha-xxx
	@Override
	public boolean readMetaInfo(Book book) {
		InputStream is = null;
		try {
			is = book.File.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (null == is) {
			return false;
		}

		if (!getEncodingAndLanguageForHtml(book, is)) {
			guessEncodingAndLanguage(book, is);
		}

		return new HtmlMetaInfoReader(book).readMetaInfo();
	}

	// modify by zenki-zha-xxx
	@Override
	public boolean readModel(BookModel model) {
		InputStream is = null;
		try {
			is = model.Book.File.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (null == is) {
			return false;
		}

		try {
			return new HtmlReader(model).readBook();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public ZLImage readCover(ZLFile file) {
		return null;
	}

	@Override
	public String readAnnotation(ZLFile file) {
		return null;
	}
}
