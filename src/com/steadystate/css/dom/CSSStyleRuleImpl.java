/*
 * $Id: CSSStyleRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
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

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.SelectorList;

import com.steadystate.css.parser.CSSOMParser;

/** 
 * Implementation of {@link CSSStyleRule}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSStyleRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class CSSStyleRuleImpl extends AbstractCSSRuleImpl
    implements CSSStyleRule, Serializable {

    private static final long serialVersionUID = -697009251364657426L;

    private SelectorList selectors = null;
    private CSSStyleDeclaration style = null;

    public SelectorList getSelectors()
    {
        return this.selectors;
    }

    public void setSelectors(SelectorList selectors)
    {
        this.selectors = selectors;
    }


    public CSSStyleRuleImpl(CSSStyleSheetImpl parentStyleSheet,
        CSSRule parentRule, SelectorList selectors) {
        super(parentStyleSheet, parentRule);
        this.selectors = selectors;
    }

    public CSSStyleRuleImpl()
    {
    }


    public short getType() {
        return STYLE_RULE;
    }

    public String getCssText() {
        return this.getSelectorText() + " { " + this.getStyle().getCssText() + " }";
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

            // The rule must be a style rule
            if (r.getType() == CSSRule.STYLE_RULE) {
                this.selectors = ((CSSStyleRuleImpl)r).selectors;
                this.style = ((CSSStyleRuleImpl)r).style;
            } else {
                throw new DOMExceptionImpl(
                    DOMException.INVALID_MODIFICATION_ERR,
                    DOMExceptionImpl.EXPECTING_STYLE_RULE);
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

    public String getSelectorText() {
        return this.selectors.toString();
    }

    public void setSelectorText(String selectorText) throws DOMException {
        if (this.parentStyleSheet != null && this.parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl(
                DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMExceptionImpl.READ_ONLY_STYLE_SHEET );
        }

        try {
            InputSource is = new InputSource(new StringReader(selectorText));
            CSSOMParser parser = new CSSOMParser();
            this.selectors = parser.parseSelectors(is);
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

    public void setStyle(CSSStyleDeclaration style) {
        this.style = style;
    }
    
    public String toString() {
        return this.getCssText();
    }
}
