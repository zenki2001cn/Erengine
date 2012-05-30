/*
 * $Id: CSSCharsetRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
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

import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSRule;

import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.CSSException;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Implementation of {@link CSSCharsetRule}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSCharsetRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class CSSCharsetRuleImpl extends AbstractCSSRuleImpl implements CSSCharsetRule, Serializable {

    private static final long serialVersionUID = -2472209213089007127L;

    String encoding = null;

    public CSSCharsetRuleImpl(
            CSSStyleSheetImpl parentStyleSheet,
            CSSRule parentRule,
            String encoding) {
        super(parentStyleSheet, parentRule);
        this.encoding = encoding;
    }

    public CSSCharsetRuleImpl()
    {
    }


    public short getType() {
        return CHARSET_RULE;
    }

    public String getCssText() {
        return "@charset \"" + this.getEncoding() + "\";";
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

            // The rule must be a charset rule
            if (r.getType() == CSSRule.CHARSET_RULE) {
                this.encoding = ((CSSCharsetRuleImpl)r).encoding;
            } else {
                throw new DOMExceptionImpl(
                    DOMException.INVALID_MODIFICATION_ERR,
                    DOMExceptionImpl.EXPECTING_CHARSET_RULE);
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

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) throws DOMException {
        this.encoding = encoding;
    }
}
