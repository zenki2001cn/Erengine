/*
 * $Id: CSSFontFaceRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 *
 * CSS Parser Project
 *
 * Copyright (C) 1999-2005 David Schweinsberg.  All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * To contact the authors of the library:
 *
 * http://cssparser.sourceforge.net/
 * mailto:davidsch@users.sourceforge.net
 */

package com.steadystate.css.dom;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import org.w3c.dom.DOMException;

import org.w3c.dom.css.CSSFontFaceRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Implementation of {@link CSSFontFaceRule}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSFontFaceRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class CSSFontFaceRuleImpl extends AbstractCSSRuleImpl implements CSSFontFaceRule, Serializable {

    private static final long serialVersionUID = -3604191834588759088L;

    private CSSStyleDeclarationImpl style = null;

    public CSSFontFaceRuleImpl(CSSStyleSheetImpl parentStyleSheet, CSSRule parentRule) {
        super(parentStyleSheet, parentRule);
    }

    public CSSFontFaceRuleImpl()
    {
    }


    public short getType() {
        return FONT_FACE_RULE;
    }

    public String getCssText() {
        return "@font-face {" + getStyle().getCssText() + "}";
    }

    public void setCssText(String cssText) throws DOMException {
        if (this.parentStyleSheet != null && this.parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl(
                DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMExceptionImpl.READ_ONLY_STYLE_SHEET);
        }

        try {
            InputSource is = new InputSource(new StringReader(cssText));
            CSSOMParser parser = new CSSOMParser();
            CSSRule r = parser.parseRule(is);

            // The rule must be a font face rule
            if (r.getType() == CSSRule.FONT_FACE_RULE) {
                this.style = ((CSSFontFaceRuleImpl)r).style;
            } else {
                throw new DOMExceptionImpl(
                    DOMException.INVALID_MODIFICATION_ERR,
                    DOMExceptionImpl.EXPECTING_FONT_FACE_RULE);
            }
        } catch (CSSException e) {
            throw new DOMExceptionImpl(
                DOMException.SYNTAX_ERR,
                DOMExceptionImpl.SYNTAX_ERROR,
                e.getMessage());
        } catch (IOException e) {
            throw new DOMExceptionImpl(
                DOMException.SYNTAX_ERR,
                DOMExceptionImpl.SYNTAX_ERROR,
                e.getMessage());
        }
    }

    public CSSStyleDeclaration getStyle() {
        return this.style;
    }

    public void setStyle(CSSStyleDeclarationImpl style) {
        this.style = style;
    }
}
