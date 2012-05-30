/*
 * $Id: LexicalUnitImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
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

package com.steadystate.css.parser;

import java.io.Serializable;
import org.w3c.css.sac.*;

/** 
 * Implementation of {@link LexicalUnit}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: LexicalUnitImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
 */
public class LexicalUnitImpl implements LexicalUnit, Serializable {

/*
    public static final short SAC_OPERATOR_COMMA	= 0;
    public static final short SAC_OPERATOR_PLUS		= 1;
    public static final short SAC_OPERATOR_MINUS	= 2;
    public static final short SAC_OPERATOR_MULTIPLY	= 3;
    public static final short SAC_OPERATOR_SLASH	= 4;
    public static final short SAC_OPERATOR_MOD		= 5;
    public static final short SAC_OPERATOR_EXP		= 6;
    public static final short SAC_OPERATOR_LT		= 7;
    public static final short SAC_OPERATOR_GT		= 8;
    public static final short SAC_OPERATOR_LE		= 9;
    public static final short SAC_OPERATOR_GE		= 10;
    public static final short SAC_OPERATOR_TILDE	= 11;
    public static final short SAC_INHERIT		= 12;
    public static final short SAC_INTEGER		= 13;
    public static final short SAC_REAL		        = 14;
    public static final short SAC_EM		= 15;
    public static final short SAC_EX		= 16;
    public static final short SAC_PIXEL		= 17;
    public static final short SAC_INCH		= 18;
    public static final short SAC_CENTIMETER	= 19;
    public static final short SAC_MILLIMETER	= 20;
    public static final short SAC_POINT		= 21;
    public static final short SAC_PICA		= 22;
    public static final short SAC_PERCENTAGE		= 23;
    public static final short SAC_URI		        = 24;
    public static final short SAC_COUNTER_FUNCTION	= 25;
    public static final short SAC_COUNTERS_FUNCTION	= 26;
    public static final short SAC_RGBCOLOR		= 27;
    public static final short SAC_DEGREE		= 28;
    public static final short SAC_GRADIAN		= 29;
    public static final short SAC_RADIAN		= 30;
    public static final short SAC_MILLISECOND		= 31;
    public static final short SAC_SECOND		= 32;
    public static final short SAC_HERTZ		        = 33;
    public static final short SAC_KILOHERTZ		= 34;
    public static final short SAC_IDENT		        = 35;
    public static final short SAC_STRING_VALUE		= 36;
    public static final short SAC_ATTR		        = 37;
    public static final short SAC_RECT_FUNCTION		= 38;
    public static final short SAC_UNICODERANGE		= 39;
    public static final short SAC_SUB_EXPRESSION	= 40;
    public static final short SAC_FUNCTION		= 41;
    public static final short SAC_DIMENSION		= 42;
*/

    private static final long serialVersionUID = -7260032046960116891L;

    private short lexicalUnitType;
    private LexicalUnit nextLexicalUnit;
    private LexicalUnit previousLexicalUnit;
//    private int _intVal;
    private float floatValue;
    private String dimension;
    private String functionName;
    private LexicalUnit parameters;
    private String stringValue;
    private Locator locator;

    public Locator getLocator()
    {
        return this.locator;
    }

    public void setLocator(Locator locator)
    {
        this.locator = locator;
    }

    public void setLexicalUnitType(short type)
    {
        this.lexicalUnitType = type;
    }

    public void setNextLexicalUnit(LexicalUnit next)
    {
        this.nextLexicalUnit = next;
    }

    public void setPreviousLexicalUnit(LexicalUnit prev)
    {
        this.previousLexicalUnit = prev;
    }

    public void setFloatValue(float floatVal)
    {
        this.floatValue = floatVal;
    }

    public String getDimension()
    {
        return this.dimension;
    }

    public void setDimension(String dimension)
    {
        this.dimension = dimension;
    }

    public void setFunctionName(String function)
    {
        this.functionName = function;
    }

    public void setParameters(LexicalUnit params)
    {
        this.parameters = params;
    }

    public void setStringValue(String stringVal)
    {
        this.stringValue = stringVal;
    }


    protected LexicalUnitImpl(LexicalUnit previous, short type) {
        this.lexicalUnitType = type;
        this.previousLexicalUnit = previous;
        if (this.previousLexicalUnit != null) {
            ((LexicalUnitImpl)this.previousLexicalUnit).nextLexicalUnit = this;
        }
    }

    /**
     * Integer
     */
    protected LexicalUnitImpl(LexicalUnit previous, int value) {
        this(previous, SAC_INTEGER);
//        _intVal = value;
        this.floatValue = value;
    }

    /**
     * Dimension
     */
    protected LexicalUnitImpl(LexicalUnit previous, short type, float value) {
        this(previous, type);
        this.floatValue = value;
    }

    /**
     * Unknown dimension
     */
    protected LexicalUnitImpl(
            LexicalUnit previous,
            short type,
            String dimension,
            float value) {
        this(previous, type);
        this.dimension = dimension;
        this.floatValue = value;
    }

    /**
     * String
     */
    protected LexicalUnitImpl(LexicalUnit previous, short type, String value) {
        this(previous, type);
        this.stringValue = value;
    }

    /**
     * Function
     */
    protected LexicalUnitImpl(
            LexicalUnit previous,
            short type,
            String name,
            LexicalUnit params) {
        this(previous, type);
        this.functionName = name;
        this.parameters = params;
    }

    public LexicalUnitImpl()
    {
    }


    public short getLexicalUnitType() {
        return this.lexicalUnitType;
    }
    
    public LexicalUnit getNextLexicalUnit() {
        return this.nextLexicalUnit;
    }
    
    public LexicalUnit getPreviousLexicalUnit() {
        return this.previousLexicalUnit;
    }
    
    public int getIntegerValue() {
//        return _intVal;
        return (int) this.floatValue;
    }
    
    public float getFloatValue() {
        return this.floatValue;
    }
    
    public String getDimensionUnitText() {
        switch (this.lexicalUnitType) {
        case SAC_EM:
            return "em";
        case SAC_EX:
            return "ex";
        case SAC_PIXEL:
            return "px";
        case SAC_INCH:
            return "in";
        case SAC_CENTIMETER:
            return "cm";
        case SAC_MILLIMETER:
            return "mm";
        case SAC_POINT:
            return "pt";
        case SAC_PICA:
            return "pc";
        case SAC_PERCENTAGE:
            return "%";
        case SAC_DEGREE:
            return "deg";
        case SAC_GRADIAN:
            return "grad";
        case SAC_RADIAN:
            return "rad";
        case SAC_MILLISECOND:
            return "ms";
        case SAC_SECOND:
            return "s";
        case SAC_HERTZ:
            return "Hz";
        case SAC_KILOHERTZ:
            return "kHz";
        case SAC_DIMENSION:
            return this.dimension;
        }
        return "";
    }
    
    public String getFunctionName() {
        return this.functionName;
    }
    
    public LexicalUnit getParameters() {
        return this.parameters;
    }

    public String getStringValue() {
        return this.stringValue;
    }

    public LexicalUnit getSubValues() {
        return this.parameters;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        switch (this.lexicalUnitType) {
        case SAC_OPERATOR_COMMA:
            sb.append(",");
            break;
        case SAC_OPERATOR_PLUS:
            sb.append("+");
            break;
        case SAC_OPERATOR_MINUS:
            sb.append("-");
            break;
        case SAC_OPERATOR_MULTIPLY:
            sb.append("*");
            break;
        case SAC_OPERATOR_SLASH:
            sb.append("/");
            break;
        case SAC_OPERATOR_MOD:
            sb.append("%");
            break;
        case SAC_OPERATOR_EXP:
            sb.append("^");
            break;
        case SAC_OPERATOR_LT:
            sb.append("<");
            break;
        case SAC_OPERATOR_GT:
            sb.append(">");
            break;
        case SAC_OPERATOR_LE:
            sb.append("<=");
            break;
        case SAC_OPERATOR_GE:
            sb.append(">=");
            break;
        case SAC_OPERATOR_TILDE:
            sb.append("~");
            break;
        case SAC_INHERIT:
            sb.append("inherit");
            break;
        case SAC_INTEGER:
            sb.append(String.valueOf(this.getIntegerValue()));
            break;
        case SAC_REAL:
            sb.append(this.trimFloat(this.getFloatValue()));
            break;
        case SAC_EM:
        case SAC_EX:
        case SAC_PIXEL:
        case SAC_INCH:
        case SAC_CENTIMETER:
        case SAC_MILLIMETER:
        case SAC_POINT:
        case SAC_PICA:
        case SAC_PERCENTAGE:
        case SAC_DEGREE:
        case SAC_GRADIAN:
        case SAC_RADIAN:
        case SAC_MILLISECOND:
        case SAC_SECOND:
        case SAC_HERTZ:
        case SAC_KILOHERTZ:
        case SAC_DIMENSION:
            sb.append(this.trimFloat(this.getFloatValue()))
              .append(this.getDimensionUnitText());
            break;
        case SAC_URI:
            sb.append("url(").append(this.getStringValue()).append(")");
            break;
        case SAC_COUNTER_FUNCTION:
            sb.append("counter(");
            appendParams(sb, this.parameters);
            sb.append(")");
            break;
        case SAC_COUNTERS_FUNCTION:
            sb.append("counters(");
            appendParams(sb, this.parameters);
            sb.append(")");
            break;
        case SAC_RGBCOLOR:
            sb.append("rgb(");
            appendParams(sb, this.parameters);
            sb.append(")");
            break;
        case SAC_IDENT:
            sb.append(this.getStringValue());
            break;
        case SAC_STRING_VALUE:
            sb.append("\"").append(this.getStringValue()).append("\"");
            break;
        case SAC_ATTR:
            sb.append("attr(");
            appendParams(sb, this.parameters);
            sb.append(")");
            break;
        case SAC_RECT_FUNCTION:
            sb.append("rect(");
            appendParams(sb,this. parameters);
            sb.append(")");
            break;
        case SAC_UNICODERANGE:
            sb.append(this.getStringValue());
            break;
        case SAC_SUB_EXPRESSION:
            sb.append(this.getStringValue());
            break;
        case SAC_FUNCTION:
            sb.append(this.getFunctionName()).append('(');
            appendParams(sb, this.parameters);
            sb.append(")");
            break;
        }
        return sb.toString();
    }

    public String toDebugString() {
        StringBuilder sb = new StringBuilder();
        switch (this.lexicalUnitType) {
        case SAC_OPERATOR_COMMA:
            sb.append("SAC_OPERATOR_COMMA");
            break;
        case SAC_OPERATOR_PLUS:
            sb.append("SAC_OPERATOR_PLUS");
            break;
        case SAC_OPERATOR_MINUS:
            sb.append("SAC_OPERATOR_MINUS");
            break;
        case SAC_OPERATOR_MULTIPLY:
            sb.append("SAC_OPERATOR_MULTIPLY");
            break;
        case SAC_OPERATOR_SLASH:
            sb.append("SAC_OPERATOR_SLASH");
            break;
        case SAC_OPERATOR_MOD:
            sb.append("SAC_OPERATOR_MOD");
            break;
        case SAC_OPERATOR_EXP:
            sb.append("SAC_OPERATOR_EXP");
            break;
        case SAC_OPERATOR_LT:
            sb.append("SAC_OPERATOR_LT");
            break;
        case SAC_OPERATOR_GT:
            sb.append("SAC_OPERATOR_GT");
            break;
        case SAC_OPERATOR_LE:
            sb.append("SAC_OPERATOR_LE");
            break;
        case SAC_OPERATOR_GE:
            sb.append("SAC_OPERATOR_GE");
            break;
        case SAC_OPERATOR_TILDE:
            sb.append("SAC_OPERATOR_TILDE");
            break;
        case SAC_INHERIT:
            sb.append("SAC_INHERIT");
            break;
        case SAC_INTEGER:
            sb.append("SAC_INTEGER(")
                .append(String.valueOf(this.getIntegerValue()))
                .append(")");
            break;
        case SAC_REAL:
            sb.append("SAC_REAL(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(")");
            break;
        case SAC_EM:
            sb.append("SAC_EM(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_EX:
            sb.append("SAC_EX(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_PIXEL:
            sb.append("SAC_PIXEL(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_INCH:
            sb.append("SAC_INCH(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_CENTIMETER:
            sb.append("SAC_CENTIMETER(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_MILLIMETER:
            sb.append("SAC_MILLIMETER(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_POINT:
            sb.append("SAC_POINT(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_PICA:
            sb.append("SAC_PICA(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_PERCENTAGE:
            sb.append("SAC_PERCENTAGE(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_DEGREE:
            sb.append("SAC_DEGREE(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_GRADIAN:
            sb.append("SAC_GRADIAN(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_RADIAN:
            sb.append("SAC_RADIAN(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_MILLISECOND:
            sb.append("SAC_MILLISECOND(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_SECOND:
            sb.append("SAC_SECOND(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_HERTZ:
            sb.append("SAC_HERTZ(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_KILOHERTZ:
            sb.append("SAC_KILOHERTZ(")
                .append(trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_DIMENSION:
            sb.append("SAC_DIMENSION(")
                .append(this.trimFloat(this.getFloatValue()))
                .append(this.getDimensionUnitText())
                .append(")");
            break;
        case SAC_URI:
            sb.append("SAC_URI(url(")
                .append(this.getStringValue())
                .append("))");
            break;
        case SAC_COUNTER_FUNCTION:
            sb.append("SAC_COUNTER_FUNCTION(counter(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        case SAC_COUNTERS_FUNCTION:
            sb.append("SAC_COUNTERS_FUNCTION(counters(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        case SAC_RGBCOLOR:
            sb.append("SAC_RGBCOLOR(rgb(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        case SAC_IDENT:
            sb.append("SAC_IDENT(")
                .append(this.getStringValue())
                .append(")");
            break;
        case SAC_STRING_VALUE:
            sb.append("SAC_STRING_VALUE(\"")
                .append(this.getStringValue())
                .append("\")");
            break;
        case SAC_ATTR:
            sb.append("SAC_ATTR(attr(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        case SAC_RECT_FUNCTION:
            sb.append("SAC_RECT_FUNCTION(rect(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        case SAC_UNICODERANGE:
            sb.append("SAC_UNICODERANGE(")
                .append(this.getStringValue())
                .append(")");
            break;
        case SAC_SUB_EXPRESSION:
            sb.append("SAC_SUB_EXPRESSION(")
                .append(this.getStringValue())
                .append(")");
            break;
        case SAC_FUNCTION:
            sb.append("SAC_FUNCTION(")
                .append(this.getFunctionName())
                .append("(");
            appendParams(sb, this.parameters);
            sb.append("))");
            break;
        }
        return sb.toString();
    }

    private void appendParams(StringBuilder sb, LexicalUnit first) {
        LexicalUnit l = first;
        while (l != null) {
            sb.append(l.toString());
            l = l.getNextLexicalUnit();
        }
    }
    
    private String trimFloat(float f) {
        String s = String.valueOf(this.getFloatValue());
        return (f - (int) f != 0) ? s : s.substring(0, s.length() - 2);
    }

    // TODO what is this method for? It is not used locally.
    /*
    private static float value(char op, String s) {
        return ((op == '-') ? -1 : 1) * Float.valueOf(s).floatValue();
    }
    */
    
    public static LexicalUnit createNumber(LexicalUnit prev, int i) {
        return new LexicalUnitImpl(prev, i);
    }
    
    public static LexicalUnit createNumber(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_REAL, f);
    }
    
    public static LexicalUnit createPercentage(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_PERCENTAGE, f);
    }
    
    public static LexicalUnit createPixel(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_PIXEL, f);
    }
    
    public static LexicalUnit createCentimeter(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_CENTIMETER, f);
    }
    
    public static LexicalUnit createMillimeter(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_MILLIMETER, f);
    }
    
    public static LexicalUnit createInch(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_INCH, f);
    }
    
    public static LexicalUnit createPoint(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_POINT, f);
    }
    
    public static LexicalUnit createPica(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_PICA, f);
    }
    
    public static LexicalUnit createEm(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_EM, f);
    }
    
    public static LexicalUnit createEx(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_EX, f);
    }
    
    public static LexicalUnit createDegree(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_DEGREE, f);
    }
    
    public static LexicalUnit createRadian(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_RADIAN, f);
    }
    
    public static LexicalUnit createGradian(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_GRADIAN, f);
    }
    
    public static LexicalUnit createMillisecond(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_MILLISECOND, f);
    }
    
    public static LexicalUnit createSecond(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_SECOND, f);
    }
    
    public static LexicalUnit createHertz(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_HERTZ, f);
    }
    
    public static LexicalUnit createDimension(LexicalUnit prev, float f, String dim) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_DIMENSION, dim, f);
    }
    
    public static LexicalUnit createKiloHertz(LexicalUnit prev, float f) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_KILOHERTZ, f);
    }
    
    public static LexicalUnit createCounter(LexicalUnit prev, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_COUNTER_FUNCTION, "counter", params);
    }
    
    public static LexicalUnit createCounters(LexicalUnit prev, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_COUNTERS_FUNCTION, "counters", params);
    }
    
    public static LexicalUnit createAttr(LexicalUnit prev, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_ATTR, "attr", params);
    }
    
    public static LexicalUnit createRect(LexicalUnit prev, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_RECT_FUNCTION, "rect", params);
    }
    
    public static LexicalUnit createRgbColor(LexicalUnit prev, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_RGBCOLOR, "rgb", params);
    }
    
    public static LexicalUnit createFunction(LexicalUnit prev, String name, LexicalUnit params) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_FUNCTION, name, params);
    }

    public static LexicalUnit createString(LexicalUnit prev, String value) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_STRING_VALUE, value);
    }
    
    public static LexicalUnit createIdent(LexicalUnit prev, String value) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_IDENT, value);
    }
    
    public static LexicalUnit createURI(LexicalUnit prev, String value) {
        return new LexicalUnitImpl(prev, LexicalUnit.SAC_URI, value);
    }
    
    public static LexicalUnit createComma(LexicalUnit prev) {
        return new LexicalUnitImpl(prev, SAC_OPERATOR_COMMA);
    }
}
