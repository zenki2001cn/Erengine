/*
 * $Id: SelectorListImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
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
import java.util.*;
import org.w3c.css.sac.*;

/**
 * Implementation of {@link SelectorList}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: SelectorListImpl.java,v 1.3 2008/03/26 02:08:55 sdanig Exp $
 */
public class SelectorListImpl implements SelectorList, Serializable {

    private static final long serialVersionUID = 7313376916207026333L;

    private List<Selector> selectors = new ArrayList<Selector>(10);

    public List<Selector> getSelectors()
    {
        return this.selectors;
    }

    public void setSelectors(List<Selector> selectors)
    {
        this.selectors = selectors;
    }

    public int getLength() {
        return this.selectors.size();
    }

    public Selector item(int index) {
        return this.selectors.get(index);
    }

    public void add(Selector sel) {
        this.selectors.add(sel);
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int len = getLength();
        for (int i = 0; i < len; i++) {
            sb.append(item(i).toString());
            if (i < len - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
