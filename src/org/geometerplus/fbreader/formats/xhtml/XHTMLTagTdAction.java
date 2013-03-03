/**
 * @file       XHTMLTagTdAction.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-28 下午01:57:56 
 */

package org.geometerplus.fbreader.formats.xhtml;

import org.geometerplus.fbreader.bookmodel.BookReader;
import org.geometerplus.fbreader.bookmodel.FBTextKind;
import org.geometerplus.zlibrary.core.xml.ZLStringMap;

public class XHTMLTagTdAction extends XHTMLTagAction {
	static private final String TAG = "XHTMLTagTdAction";
	private byte mControl = -1;
	
	protected void doAtStart(XHTMLReader reader, ZLStringMap xmlattributes) {
//		int size = xmlattributes.getSize();
//		for (int i = 0; i < size; i++) {
//			Logger.dLog(TAG, "attr getval = " + xmlattributes.getKey(i));
//		}
		String align = xmlattributes.getValue("align");
		if ((null != align)) {
			if (align.equals("left")) {
				mControl = FBTextKind.LEFT;
			} else if (align.equals("center")) {
				mControl = FBTextKind.CENTER;
			} else if (align.equals("right")) {
				mControl = FBTextKind.RIGHT;
			} else {
				mControl = -1;
			}
		}
		
		if (mControl != -1) {
			final BookReader modelReader = reader.getModelReader();
			modelReader.pushKind(mControl);
			modelReader.addControl(mControl, true);
		}
	}

	protected void doAtEnd(XHTMLReader reader) {
		if (mControl != -1) {
			final BookReader modelReader = reader.getModelReader();
			modelReader.addControl(mControl, false);
			modelReader.popKind();
		}
	}
}
