/**
 * @file       TxtPlugin.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-20 上午09:30:28 
 */

package org.geometerplus.fbreader.formats.txt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.fbreader.formats.PluginCollection;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

import com.easyview.ebook.reader.engine.util.Logger;
import com.easyview.ebook.reader.engine.util.charsetcodec.CharsetDetectorProxy;

import android.util.Log;

public class TxtPlugin extends FormatPlugin {
	static private final String TAG = "TxtPlugin";

	@Override
	public boolean acceptsFile(ZLFile file) {
		return "txt".equals(file.getExtension());
	}

	@Override
	public boolean readMetaInfo(Book book) {
		ZLFile zlFile = book.File;
		InputStream is = null;

		try {
			is = zlFile.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null == is) {
			Log.e(TAG, "zlFile inputstream == null");
			return false;
		}

		return true;
	}

	@Override
	public boolean readModel(BookModel model) {
		Book book = model.Book;
		InputStream is = null;

		try {
			is = book.File.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null == is) {
			return false;
		}

		guessEncodingAndLanguage(book, is);

		return new TxtBookReader(model).readDocument(is);
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
