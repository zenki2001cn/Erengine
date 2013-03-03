/*
 * $Id: CSSMediaRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
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

import org.w3c.dom.stylesheets.MediaList;

import org.w3c.dom.css.CSSMediaRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Implementation of {@link CSSMediaRule}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSMediaRuleImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class CSSMediaRuleImpl extends AbstractCSSRuleImpl implements CSSMediaRule, Serializable {

    private static final long serialVersionUID = 6603734096445214651L;

    private MediaList media = null;
    private CSSRuleList cssRules = null;

    public void setMedia(MediaList media)
    {
        this.media = media;
    }

    public void setCssRules(CSSRuleList cssRules)
    {
        this.cssRules = cssRules;
    }


    public CSSMediaRuleImpl(
            CSSStyleSheetImpl parentStyleSheet,
            CSSRule parentRule,
            MediaList media) {
        super(parentStyleSheet, parentRule);
        this.media = media;
    }

    public CSSMediaRuleImpl()
    {
    }


    public short getType() {
        return MEDIA_RULE;
    }

    public String getCssText() {
        StringBuffer sb = new StringBuffer("@media ");
        sb.append(getMedia().toString()).append(" {");
        for (int i = 0; i < getCssRules().getLength(); i++) {
            CSSRule rule = getCssRules().item(i);
            sb.append(rule.getCssText()).append(" ");
        }
        sb.append("}");
        return sb.toString();
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

            // The rule must be a media rule
            if (r.getType() == CSSRule.MEDIA_RULE) {
                this.media = ((CSSMediaRuleImpl)r).media;
                this.cssRules = ((CSSMediaRuleImpl)r).cssRules;
            } else {
                throw new DOMExceptionImpl(
                    DOMException.INVALID_MODIFICATION_ERR,
                    DOMExceptionImpl.EXPECTING_MEDIA_RULE);
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

    public MediaList getMedia() {
        return this.media;
    }

    public CSSRuleList getCssRules() {
        if (this.cssRules == null)
        {
            this.cssRules = new CSSRuleListImpl();
        }
        return this.cssRules;
    }

    public int insertRule(String rule, int index) throws DOMException {
        if (this.parentStyleSheet != null && this.parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl(
                DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMExceptionImpl.READ_ONLY_STYLE_SHEET);
        }

        try {
            InputSource is = new InputSource(new StringReader(rule));
            CSSOMParser parser = new CSSOMParser();
            parser.setParentStyleSheet(this.parentStyleSheet);
            // parser._parentRule is never read
            //parser.setParentRule(_parentRule);
            CSSRule r = parser.parseRule(is);

            // Insert the rule into the list of rules
            ((CSSRuleListImpl)getCssRules()).insert(r, index);

        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(
                DOMException.INDEX_SIZE_ERR,
                DOMExceptionImpl.ARRAY_OUT_OF_BOUNDS,
                e.getMessage());
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
        return index;
    }

    public void deleteRule(int index) throws DOMException {
        if (this.parentStyleSheet != null && this.parentStyleSheet.isReadOnly()) {
            throw new DOMExceptionImpl(
                DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMExceptionImpl.READ_ONLY_STYLE_SHEET);
        }
        try {
            ((CSSRuleListImpl)getCssRules()).delete(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(
                DOMException.INDEX_SIZE_ERR,
                DOMExceptionImpl.ARRAY_OUT_OF_BOUNDS,
                e.getMessage());
        }
    }

    public void setRuleList(CSSRuleListImpl rules) {
        this.cssRules = rules;
    }
    
    public String toString() {
        return this.getCssText();
    }
}
