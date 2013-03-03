/*
 * $Id: RectImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
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
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.Rect;

import org.w3c.css.sac.LexicalUnit;

/** 
 * Implementation of {@link Rect}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: RectImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class RectImpl implements Rect, Serializable {
    
    private static final long serialVersionUID = -7031248513917920621L;

    private CSSPrimitiveValue left;
    private CSSPrimitiveValue top;
    private CSSPrimitiveValue right;
    private CSSPrimitiveValue bottom;

    public void setLeft(CSSPrimitiveValue left)
    {
        this.left = left;
    }

    public void setTop(CSSPrimitiveValue top)
    {
        this.top = top;
    }

    public void setRight(CSSPrimitiveValue right)
    {
        this.right = right;
    }

    public void setBottom(CSSPrimitiveValue bottom)
    {
        this.bottom = bottom;
    }


    /** Creates new RectImpl */
    public RectImpl(LexicalUnit lu) throws DOMException {
        LexicalUnit next = lu;
        this.left = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();  // ,
        if (next != null)
        {
            if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
            {
                // error
                throw new DOMException(DOMException.SYNTAX_ERR,
                    "Rect parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (next != null)
            {
                this.top = new CSSValueImpl(next, true);
                next = next.getNextLexicalUnit();   // ,
                if (next != null)
                {
                    if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                    {
                        // error
                        throw new DOMException(DOMException.SYNTAX_ERR,
                            "Rect parameters must be separated by ','.");
                    }
                    next = next.getNextLexicalUnit();
                    if (next != null)
                    {
                        this.right = new CSSValueImpl(next, true);
                        next = next.getNextLexicalUnit();   // ,
                        if (next != null)
                        {
                            if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                            {
                                // error
                                throw new DOMException(DOMException.SYNTAX_ERR,
                                    "Rect parameters must be separated by ','.");
                            }
                            next = next.getNextLexicalUnit();
                            if (next != null)
                            {
                                this.bottom = new CSSValueImpl(next, true);
                                if ((next = next.getNextLexicalUnit()) != null)
                                {
                                    // error
                                    throw new DOMException(DOMException.SYNTAX_ERR,
                                        "Too many parameters for rect function.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public RectImpl()
    {
    }


    public CSSPrimitiveValue getTop() {
        return this.top;
    }

    public CSSPrimitiveValue getRight() {
        return this.right;
    }

    public CSSPrimitiveValue getBottom() {
        return this.bottom;
    }

    public CSSPrimitiveValue getLeft() {
        return this.left;
    }
    
    public String toString() {
        return new StringBuffer("rect(")
            .append(this.left).append(", ")
            .append(this.top).append(", ")
            .append(this.right).append(", ")
            .append(this.bottom).append(")").toString();
    }
}