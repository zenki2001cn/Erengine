/**
 * @file       TxtBookReader.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-6-20 下午01:51:35 
 */

package org.geometerplus.fbreader.formats.txt;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReader;
import org.geometerplus.fbreader.bookmodel.FBTextKind;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph;

import com.easyview.ebook.reader.engine.util.Logger;

public class TxtBookReader extends TxtReader {
	static private final String TAG = "TxtBookReader";
	private final char[] SPACE = { ' ' };

	public TxtBookReader(BookModel model) {
		super(model);
		
		Logger.dLog(TAG, "mEncoding = " + mEncoding);
	}

	@Override
	protected void startDocumentHandler() {
		setMainTextModel();
		pushKind(FBTextKind.REGULAR);
		beginParagraph();
	}

	@Override
	protected boolean spaceDataHandler(int num) {
		addFixedHSpace((short) num);
		
		return true;
	}

	// add data to buffer
	private boolean regularDataHandler(char[] data) {
		addData(data);

		return true;
	}
	
	// add data and flush
	private boolean regularDataHandlerFlush(char[] data) {
		addData(data, 0, data.length, true);

		return true;
	}

	@Override
	protected boolean characterDataHandler(String str) {
		regularDataHandlerFlush(str.toCharArray());
		return true;
	}

	@Override
	protected boolean newLineHandler() {
		addControl(FBTextKind.CODE, false);
		endParagraph();
		beginParagraph();
		addControl(FBTextKind.CODE, true);
		
		return true;
	}
	
	@Override
	protected boolean newEmptyLineHandler() {
		beginParagraph(ZLTextParagraph.Kind.EMPTY_LINE_PARAGRAPH);
		endParagraph();
		beginParagraph();
		return true;
	}

	@Override
	protected void endDocumentHandler() {
		endParagraph();
	}
}
