/**
 * @file       TxtReader.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-20 下午01:52:00 
 */

package org.geometerplus.fbreader.formats.txt;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReader;

abstract public class TxtReader extends BookReader {
	static private final String TAG = "TxtReader";
	protected final int BUFSIZE = 65535;
	protected final int TAB_EQUAL_SPACE = 4;
	protected final String mEncoding;

	public TxtReader(BookModel model) {
		super(model);

		mEncoding = model.Book.getEncoding();
	}

	abstract protected void startDocumentHandler();

	abstract protected void endDocumentHandler();

	abstract protected boolean characterDataHandler(String str);

	abstract protected boolean spaceDataHandler(int num);

	abstract protected boolean newLineHandler();

	abstract protected boolean newEmptyLineHandler();

	public boolean readDocument(InputStream stream) {
		if (null == stream) {
			return false;
		}

		startDocumentHandler();

		byte[] buffer = new byte[BUFSIZE];
		String str = null;
		int length = 0;
		int start = 0;
		int charsetLen = 0;
		boolean skipNewLine = false;

		do {
			start = 0;

			try {
				length = stream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < length; i++) {
				skipNewLine = false;

				switch (buffer[i]) {
				// new line
				case '\n':
				case '\r': {
					if ((buffer[i] == '\r') && ((i + 1) < length)
							&& (buffer[i + 1] == '\n')) {
						skipNewLine = true;
					}

					if (charsetLen > 0) {
						try {
							str = new String(buffer, start, charsetLen,
									mEncoding);
							characterDataHandler(str);
							charsetLen = 0;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						newLineHandler();
					} else {
						newEmptyLineHandler();
					}

					if (skipNewLine) {
						++i;
					}

					start = i + 1;
				}
					break;

				// space char handler
				case 0x08:
				case 0x0B:
				case 0x0C:
				case ' ': {
					if (charsetLen > 0) {
						try {
							str = new String(buffer, start, charsetLen,
									mEncoding);
							characterDataHandler(str);
							charsetLen = 0;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}

					spaceDataHandler(1);
					start = i + 1;
				}
					break;

				// horizontal tab handler
				case '\t': {
					int lack = TAB_EQUAL_SPACE;

					if (charsetLen > 0) {
						try {
							str = new String(buffer, start, charsetLen,
									mEncoding);
							characterDataHandler(str);
							charsetLen = 0;
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						int left = charsetLen % TAB_EQUAL_SPACE;
						if (left > 0) {
							lack = TAB_EQUAL_SPACE - left;
						} else {
							lack = TAB_EQUAL_SPACE;
						}
					}

					spaceDataHandler(lack);
					start = i + 1;
				}
					break;

				// normal char handler
				default: {
					charsetLen++;
				}
					break;
				}
			}
		} while (length == BUFSIZE);

		endDocumentHandler();

		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}
}
