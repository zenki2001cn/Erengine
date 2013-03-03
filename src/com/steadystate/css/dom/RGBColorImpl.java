/*
 * $Id: RGBColorImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
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

import org.w3c.css.sac.LexicalUnit;

import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.RGBColor;

/**
 * Implementation of {@link RGBColor}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: RGBColorImpl.java,v 1.2 2008/03/26 02:17:24 sdanig Exp $
 */
public class RGBColorImpl implements RGBColor, Serializable {

    private static final long serialVersionUID = 8152675334081993160L;
    private CSSPrimitiveValue red = null;
    private CSSPrimitiveValue green = null;
    private CSSPrimitiveValue blue = null;

    protected RGBColorImpl(LexicalUnit lu) throws DOMException {
        LexicalUnit next = lu;
        this.red = new CSSValueImpl(next, true);
        next = next.getNextLexicalUnit();   // ,
        if (next != null)
        {
            if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
            {
                // error
                throw new DOMException(DOMException.SYNTAX_ERR,
                    "rgb parameters must be separated by ','.");
            }
            next = next.getNextLexicalUnit();
            if (next != null)
            {
                this.green = new CSSValueImpl(next, true);
                next = next.getNextLexicalUnit();   // ,
                if (next != null)
                {
                    if (next.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                    {
                        // error
                        throw new DOMException(DOMException.SYNTAX_ERR,
                            "rgb parameters must be separated by ','.");
                    }
                    next = next.getNextLexicalUnit();
                    this.blue = new CSSValueImpl(next, true);
                    if ((next = next.getNextLexicalUnit()) != null)
                    {
                        // error
                        throw new DOMException(DOMException.SYNTAX_ERR,
                            "Too many parameters for rgb function.");
                    }
                }
            }
        }
    }

    public RGBColorImpl() {
    }
    
    public CSSPrimitiveValue getRed() {
        return this.red;
    }

    public void setRed(CSSPrimitiveValue red) {
        this.red = red;
    }

    public CSSPrimitiveValue getGreen() {
        return this.green;
    }

    public void setGreen(CSSPrimitiveValue green) {
        this.green = green;
    }

    public CSSPrimitiveValue getBlue() {
        return this.blue;
    }

    public void setBlue(CSSPrimitiveValue blue) {
        this.blue = blue;
    }

    public String toString() {
        return new StringBuffer("rgb(")
            .append(this.red.toString()).append(", ")
            .append(this.green.toString()).append(", ")
            .append(this.blue.toString()).append(")").toString();
    }
}
