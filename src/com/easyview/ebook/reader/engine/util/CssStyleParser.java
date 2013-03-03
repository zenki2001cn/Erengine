/**
 * @file       CssStyleParser.java
 *
 * @revision:  none 
 *
 * @version    0.0.01
 * @author:    Zenki (zhajun), zenki2001cn@163.com
 * @date:      2011-7-27 下午05:36:10 
 */

package com.easyview.ebook.reader.engine.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.parser.CSSOMParser;

public class CssStyleParser {
	static private final String TAG = "CssStyleParser";
	private InputStream mStream;
	private HashMap<String, HashMap<String, String>> mCssStyleList;

	public CssStyleParser(InputStream stream) {
		mStream = stream;
		mCssStyleList = new HashMap<String, HashMap<String, String>>();
	}

	public HashMap<String, HashMap<String, String>> getCssStyleList() {
		return mCssStyleList;
	}
	
	public boolean read() {
		boolean rtn = false;

		try {
			InputSource source = new InputSource(new InputStreamReader(mStream));
			CSSOMParser parser = new CSSOMParser();
			CSSStyleSheet stylesheet = parser.parseStyleSheet(source, null,
					null);
			CSSRuleList ruleList = stylesheet.getCssRules();

			String selector;
			String property;
			String value;
			HashMap<String, String> propHash;

			for (int i = 0; i < ruleList.getLength(); i++) {
				CSSRule rule = ruleList.item(i);
				if (rule instanceof CSSStyleRule) {
					CSSStyleRule styleRule = (CSSStyleRule) rule;
					selector = styleRule.getSelectorText();

					Logger.eLog(TAG, "selector:" + i + ": " + selector);

					CSSStyleDeclaration styleDeclaration = styleRule.getStyle();
					propHash = new HashMap<String, String>();

					for (int j = 0; j < styleDeclaration.getLength(); j++) {
						property = styleDeclaration.item(j);
						value = styleDeclaration.getPropertyCSSValue(property)
								.getCssText();
						propHash.put(property, value);

						Logger.dLog(TAG, "property: " + property + " value: " + value);
					}
					
					mCssStyleList.put(selector, propHash);
				}
			}

			if (mStream != null) {
				mStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		rtn = true;

		return rtn;
	}
}
