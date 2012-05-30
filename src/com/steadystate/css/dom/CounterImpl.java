/*
 * $Id: CounterImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
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

import java.io.Serializable;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.Counter;

import org.w3c.css.sac.LexicalUnit;

/** 
 * Implementation of {@link Counter}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CounterImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
 */
public class CounterImpl implements Counter, Serializable {

    private static final long serialVersionUID = 7996279151817598904L;

    private String identifier;
    private String listStyle;
    private String separator;

    public void setIdentifier(String identifier)
    {
        this.identifier = identifier;
    }

    public void setListStyle(String listStyle)
    {
        this.listStyle = listStyle;
    }

    public void setSeparator(String separator)
    {
        this.separator = separator;
    }


    /** Creates new CounterImpl */
    public CounterImpl(boolean separatorSpecified, LexicalUnit lu)
        throws DOMException
    {
        LexicalUnit next = lu;
        this.identifier = next.getStringValue();
        next = next.getNextLexicalUnit();   // ','
        if (next != null)
        {
            if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
            {
                // error
                throw new DOMException(DOMException.SYNTAX_ERR,
                    "Counter parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (separatorSpecified && (next != null)) {
                this.separator = next.getStringValue();
                next = next.getNextLexicalUnit();   // ','
                if (next != null)
                {
                    if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                    {
                        // error
                        throw new DOMException(DOMException.SYNTAX_ERR,
                            "Counter parameters must be separated by ','.");
                    }
                    next = next.getNextLexicalUnit();
                }
            }
            if (next != null) {
                this.listStyle = next.getStringValue();
                if ((next = next.getNextLexicalUnit()) != null)
                {
                    // error
                    throw new DOMException(DOMException.SYNTAX_ERR,
                        "Too many parameters for counter function.");
                }
            }
        }
    }

    public CounterImpl()
    {
    }


    public String getIdentifier() {
        return this.identifier;
    }

    public String getListStyle() {
        return this.listStyle;
    }

    public String getSeparator() {
        return this.separator;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.separator == null) {
            // This is a 'counter()' function
            sb.append("counter(");
        } else {
            // This is a 'counters()' function
            sb.append("counters(");
        }
        sb.append(this.identifier);
        if (this.separator != null) {
            sb.append(", \"").append(this.separator).append("\"");
        }
        if (this.listStyle != null) {
            sb.append(", ").append(this.listStyle);
        }
        sb.append(")");
        return sb.toString();
    }
}