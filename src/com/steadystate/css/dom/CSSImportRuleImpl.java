/*
 * $Id: CSSImportRuleImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
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

import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;

import com.steadystate.css.parser.CSSOMParser;

/**
 * Implementation of {@link CSSImportRule}.
 * 
 * TODO: Implement getStyleSheet()
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSImportRuleImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
 */
public class CSSImportRuleImpl extends AbstractCSSRuleImpl implements CSSImportRule, Serializable {

    private static final long serialVersionUID = 7807829682009179339L;

    String href = null;
    MediaList media = null;

    public void setHref(String href)
    {
        this.href = href;
    }

    public void setMedia(MediaList media)
    {
        this.media = media;
    }


    public CSSImportRuleImpl(
            CSSStyleSheetImpl parentStyleSheet,
            CSSRule parentRule,
            String href,
            MediaList media) {
        super(parentStyleSheet, parentRule);
        this.href = href;
        this.media = media;
    }

    public CSSImportRuleImpl()
    {
    }


    public short getType() {
        return IMPORT_RULE;
    }

    public String getCssText() {
        StringBuilder sb = new StringBuilder();
        sb.append("@import url(")
            .append(getHref())
            .append(")");
        if (getMedia().getLength() > 0) {
            sb.append(" ").append(getMedia().toString());
        }
        sb.append(";");
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

            // The rule must be an import rule
            if (r.getType() == CSSRule.IMPORT_RULE) {
                this.href = ((CSSImportRuleImpl)r).href;
                this.media = ((CSSImportRuleImpl)r).media;
            } else {
                throw new DOMExceptionImpl(
                    DOMException.INVALID_MODIFICATION_ERR,
                    DOMExceptionImpl.EXPECTING_IMPORT_RULE);
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

    public String getHref() {
        return this.href;
    }

    public MediaList getMedia() {
        return this.media;
    }

    public CSSStyleSheet getStyleSheet() {
        return null;
    }
    
    public String toString() {
        return this.getCssText();
    }
}
