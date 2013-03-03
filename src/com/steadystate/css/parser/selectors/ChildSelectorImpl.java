/*
 * $Id: ChildSelectorImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
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
 * @version $Id: ChildSelectorImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
 */
public class ChildSelectorImpl implements DescendantSelector, Serializable {

    private static final long serialVersionUID = -5843289529637921083L;

    private Selector ancestorSelector;
    private SimpleSelector simpleSelector;

    public void setAncestorSelector(Selector ancestorSelector)
    {
        this.ancestorSelector = ancestorSelector;
    }

    public void setSimpleSelector(SimpleSelector simpleSelector)
    {
        this.simpleSelector = simpleSelector;
    }


    public ChildSelectorImpl(Selector parent, SimpleSelector simpleSelector) {
        this.ancestorSelector = parent;
        this.simpleSelector = simpleSelector;
    }

    public ChildSelectorImpl()
    {
    }


    public short getSelectorType() {
        return Selector.SAC_CHILD_SELECTOR;
    }

    public Selector getAncestorSelector() {
        return this.ancestorSelector;
    }

    public SimpleSelector getSimpleSelector() {
        return this.simpleSelector;
    }
    
    public String toString() {
        return this.ancestorSelector.toString() + " > " + this.simpleSelector.toString();
    }
}
