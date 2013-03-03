/*
 * $Id: CSSValueImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
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
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;

import org.w3c.dom.DOMException;

import org.w3c.dom.css.Counter;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.Rect;
import org.w3c.dom.css.RGBColor;

import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.LexicalUnitImpl;
import com.steadystate.css.userdata.UserDataConstants;

/**
 * The <code>CSSValueImpl</code> class can represent either a
 * <code>CSSPrimitiveValue</code> or a <code>CSSValueList</code> so that
 * the type can successfully change when using <code>setCssText</code>.
 *
 * TODO:
 * Float unit conversions,
 * A means of checking valid primitive types for properties
 *
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSValueImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
 */
public class CSSValueImpl extends CSSOMObjectImpl implements CSSPrimitiveValue, CSSValueList, Serializable {

    private static final long serialVersionUID = 406281136418322579L;

    private Object _value = null;

    public Object getValue()
    {
        return this._value;
    }

    public void setValue(Object value)
    {
        this._value = value;
    }
    /**
     * Constructor
     */
    public CSSValueImpl(LexicalUnit value, boolean forcePrimitive) {
        if (!forcePrimitive && (value.getNextLexicalUnit() != null))
        {
            this._value = this.getValues(value);
        }
        else if (value.getParameters() != null) {
            if (value.getLexicalUnitType() == LexicalUnit.SAC_RECT_FUNCTION) {
                // Rect
                this._value = new RectImpl(value.getParameters());
            } else if (value.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR) {
                // RGBColor
                this._value = new RGBColorImpl(value.getParameters());
            } else if (value.getLexicalUnitType() == LexicalUnit.SAC_COUNTER_FUNCTION) {
                // Counter
                this._value = new CounterImpl(false, value.getParameters());
            } else if (value.getLexicalUnitType() == LexicalUnit.SAC_COUNTERS_FUNCTION) {
                // Counter
                this._value = new CounterImpl(true, value.getParameters());
            } else {
                this._value = value;
            }
        } else {
            // We need to be a CSSPrimitiveValue
            this._value = value;
        }
        if (value instanceof LexicalUnitImpl)
        {
            Locator locator = ((LexicalUnitImpl) value).getLocator();
            if (locator != null)
            {
                this.setUserData(UserDataConstants.KEY_LOCATOR, locator);
            }
        }
    }

    public CSSValueImpl()
    {
    }


    private List<CSSValueImpl> getValues(LexicalUnit value)
    {
        // We need to be a CSSValueList
        // Values in an "expr" can be seperated by "operator"s, which are
        // either '/' or ',' - ignore these operators
        List<CSSValueImpl> values = new ArrayList<CSSValueImpl>();
        LexicalUnit lu = value;
        while (lu != null) {
            if ((lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                && (lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_SLASH)) {
                values.add(new CSSValueImpl(lu, true));
            }
            lu = lu.getNextLexicalUnit();
        }
        return values;
    }

    public CSSValueImpl(LexicalUnit value) {
        this(value, false);
    }

    public String getCssText() {
        if (getCssValueType() == CSS_VALUE_LIST) {
            
            // Create the string from the LexicalUnits so we include the correct
            // operators in the string
            StringBuilder sb = new StringBuilder();
            List list = (List) this._value;
            java.util.Iterator it = list.iterator();
            while (it.hasNext())
            {
                Object o = it.next();
                if (o instanceof LexicalUnit)
                {
                    LexicalUnit lu = (LexicalUnit) ((CSSValueImpl) list.get(0))._value;
                    while (lu != null) {
                        sb.append(lu.toString());
                        
                        // Step to the next lexical unit, determining what spacing we
                        // need to put around the operators
                        LexicalUnit prev = lu;
                        lu = lu.getNextLexicalUnit();
                        if ((lu != null)
                            && (lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_COMMA)
                            && (lu.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_SLASH)
                            && (prev.getLexicalUnitType() != LexicalUnit.SAC_OPERATOR_SLASH)) {
                            sb.append(" ");
                        }
                    }
                }
                else
                {
                    sb.append(o);
                }
                if (it.hasNext())
                {
                    sb.append(" ");
                }
            }
            return sb.toString();
        }
        return this._value.toString();
    }

    public void setCssText(String cssText) throws DOMException {
        try {
            InputSource is = new InputSource(new StringReader(cssText));
            CSSOMParser parser = new CSSOMParser();
            CSSValueImpl v2 = (CSSValueImpl) parser.parsePropertyValue(is);
            this._value = v2._value;
        } catch (Exception e) {
            throw new DOMExceptionImpl(
                DOMException.SYNTAX_ERR,
                DOMExceptionImpl.SYNTAX_ERROR,
                e.getMessage() );
        }
    }

    public short getCssValueType() {
        if (this._value instanceof List)
        {
            return CSS_VALUE_LIST;
        }
        else if ((this._value instanceof LexicalUnit) &&
            (((LexicalUnit) this._value).getLexicalUnitType() == LexicalUnit.SAC_INHERIT))
        {
            return CSS_INHERIT;
        }
        return CSS_PRIMITIVE_VALUE;
    }

    public short getPrimitiveType() {
        if (this._value instanceof LexicalUnit) {
            LexicalUnit lu = (LexicalUnit) this._value;
            switch (lu.getLexicalUnitType()) {
            case LexicalUnit.SAC_INHERIT:
                return CSS_IDENT;
            case LexicalUnit.SAC_INTEGER:
            case LexicalUnit.SAC_REAL:
                return CSS_NUMBER;
            case LexicalUnit.SAC_EM:
                return CSS_EMS;
            case LexicalUnit.SAC_EX:
                return CSS_EXS;
            case LexicalUnit.SAC_PIXEL:
                return CSS_PX;
            case LexicalUnit.SAC_INCH:
                return CSS_IN;
            case LexicalUnit.SAC_CENTIMETER:
                return CSS_CM;
            case LexicalUnit.SAC_MILLIMETER:
                return CSS_MM;
            case LexicalUnit.SAC_POINT:
                return CSS_PT;
            case LexicalUnit.SAC_PICA:
                return CSS_PC;
            case LexicalUnit.SAC_PERCENTAGE:
                return CSS_PERCENTAGE;
            case LexicalUnit.SAC_URI:
                return CSS_URI;
//            case LexicalUnit.SAC_COUNTER_FUNCTION:
//            case LexicalUnit.SAC_COUNTERS_FUNCTION:
//                return CSS_COUNTER;
//            case LexicalUnit.SAC_RGBCOLOR:
//                return CSS_RGBCOLOR;
            case LexicalUnit.SAC_DEGREE:
                return CSS_DEG;
            case LexicalUnit.SAC_GRADIAN:
                return CSS_GRAD;
            case LexicalUnit.SAC_RADIAN:
                return CSS_RAD;
            case LexicalUnit.SAC_MILLISECOND:
                return CSS_MS;
            case LexicalUnit.SAC_SECOND:
                return CSS_S;
            case LexicalUnit.SAC_HERTZ:
                return CSS_KHZ;
            case LexicalUnit.SAC_KILOHERTZ:
                return CSS_HZ;
            case LexicalUnit.SAC_IDENT:
                return CSS_IDENT;
            case LexicalUnit.SAC_STRING_VALUE:
                return CSS_STRING;
            case LexicalUnit.SAC_ATTR:
                return CSS_ATTR;
//            case LexicalUnit.SAC_RECT_FUNCTION:
//                return CSS_RECT;
            case LexicalUnit.SAC_UNICODERANGE:
            case LexicalUnit.SAC_SUB_EXPRESSION:
            case LexicalUnit.SAC_FUNCTION:
                return CSS_STRING;
            case LexicalUnit.SAC_DIMENSION:
                return CSS_DIMENSION;
            }
        } else if (this._value instanceof RectImpl) {
            return CSS_RECT;
        } else if (this._value instanceof RGBColorImpl) {
            return CSS_RGBCOLOR;
        } else if (this._value instanceof CounterImpl) {
            return CSS_COUNTER;
        }
        return CSS_UNKNOWN;
    }

    public void setFloatValue(short unitType, float floatValue) throws DOMException {
        this._value = LexicalUnitImpl.createNumber(null, floatValue);
    }

    public float getFloatValue(short unitType) throws DOMException {
        if (this._value instanceof LexicalUnit) {
            LexicalUnit lu = (LexicalUnit) this._value;
            return lu.getFloatValue();
        }
        throw new DOMExceptionImpl(
            DOMException.INVALID_ACCESS_ERR,
            DOMExceptionImpl.FLOAT_ERROR);

        // We need to attempt a conversion
//        return 0;
    }

    public void setStringValue(short stringType, String stringValue) throws DOMException {
        switch (stringType) {
        case CSS_STRING:
            this._value = LexicalUnitImpl.createString(null, stringValue);
            break;
        case CSS_URI:
            this._value = LexicalUnitImpl.createURI(null, stringValue);
            break;
        case CSS_IDENT:
            this._value = LexicalUnitImpl.createIdent(null, stringValue);
            break;
        case CSS_ATTR:
//            this._value = LexicalUnitImpl.createAttr(null, stringValue);
//            break;
            throw new DOMExceptionImpl(
                DOMException.NOT_SUPPORTED_ERR,
                DOMExceptionImpl.NOT_IMPLEMENTED);
        default:
            throw new DOMExceptionImpl(
                DOMException.INVALID_ACCESS_ERR,
                DOMExceptionImpl.STRING_ERROR);
        }
    }

    /**
     * TODO: return a value for a list type
     */
    public String getStringValue() throws DOMException {
        if (this._value instanceof LexicalUnit) {
            LexicalUnit lu = (LexicalUnit) this._value;
            if ((lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT)
                || (lu.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE)
                || (lu.getLexicalUnitType() == LexicalUnit.SAC_URI)
                || (lu.getLexicalUnitType() == LexicalUnit.SAC_INHERIT)) {
                return lu.getStringValue();
            }
            else if (lu.getLexicalUnitType() == LexicalUnit.SAC_ATTR)
            {
                return lu.getParameters().getStringValue();
            }
        } else if (this._value instanceof List) {
            return null;
        }

        throw new DOMExceptionImpl(
            DOMException.INVALID_ACCESS_ERR,
            DOMExceptionImpl.STRING_ERROR);
    }

    public Counter getCounterValue() throws DOMException {
        if ((this._value instanceof Counter) == false) {
            throw new DOMExceptionImpl(
                DOMException.INVALID_ACCESS_ERR,
                DOMExceptionImpl.COUNTER_ERROR);
        }
        return (Counter) this._value;
    }

    public Rect getRectValue() throws DOMException {
        if ((this._value instanceof Rect) == false) {
            throw new DOMExceptionImpl(
                DOMException.INVALID_ACCESS_ERR,
                DOMExceptionImpl.RECT_ERROR);
        }
        return (Rect) this._value;
    }

    public RGBColor getRGBColorValue() throws DOMException {
        if ((this._value instanceof RGBColor) == false) {
            throw new DOMExceptionImpl(
                DOMException.INVALID_ACCESS_ERR,
                DOMExceptionImpl.RGBCOLOR_ERROR);
        }
        return (RGBColor) this._value;
    }

    public int getLength() {
        return (this._value instanceof List) ? ((List)this._value).size() : 0;
    }

    public CSSValue item(int index) {
        return (this._value instanceof List)
            ? ((CSSValue)((List)this._value).get(index))
            : null;
    }

    public String toString() {
        return getCssText();
    }
}
