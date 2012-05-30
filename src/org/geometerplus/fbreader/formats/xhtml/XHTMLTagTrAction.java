/**
 * @file       XHTMLTagTrAction.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-28 上午10:06:02 
 */

package org.geometerplus.fbreader.formats.xhtml;

import org.geometerplus.zlibrary.core.xml.ZLStringMap;

import com.foxconn.ebook.reader.engine.util.Logger;

public class XHTMLTagTrAction extends XHTMLTagAction {
	static private final String TAG = "XHTMLTagTrAction";

	@Override
	protected void doAtStart(XHTMLReader reader, ZLStringMap xmlattributes) {
		reader.getModelReader().beginParagraph();
	}

	@Override
	protected void doAtEnd(XHTMLReader reader) {
		reader.getModelReader().endParagraph();
	}

}
