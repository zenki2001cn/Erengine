/*
 * $Id: BeginHyphenAttributeConditionImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
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

package com.steadystate.css.parser.selectors;

import java.io.Serializable;
import org.w3c.css.sac.*;

/**
 *
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: BeginHyphenAttributeConditionImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
 */
public class BeginHyphenAttributeConditionImpl implements AttributeCondition, Serializable {

    private static final long serialVersionUID = 6552118983276681650L;

    private String localName;
    private String value;

    public void setLocaleName(String localName)
    {
        this.localName = localName;
    }

    public void setValue(String value)
    {
        this.value = value;
    }


    public BeginHyphenAttributeConditionImpl(String localName, String value) {
        this.localName = localName;
        this.value = value;
    }

    public BeginHyphenAttributeConditionImpl()
    {
    }


    public short getConditionType() {
        return Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION;
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getLocalName() {
        return this.localName;
    }

    public boolean getSpecified() {
        return true;
    }

    public String getValue() {
        return this.value;
    }
    
    public String toString() {
        return "[" + this.getLocalName() + "|=\"" + this.getValue() + "\"]";
    }
}

