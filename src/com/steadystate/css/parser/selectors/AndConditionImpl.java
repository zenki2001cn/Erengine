/*
 * $Id: AndConditionImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
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
 * @version $Id: AndConditionImpl.java,v 1.1 2008/03/20 01:20:17 sdanig Exp $
 */
public class AndConditionImpl implements CombinatorCondition, Serializable {

    private static final long serialVersionUID = -3180583860092672742L;

    private Condition firstCondition;
    private Condition secondCondition;

    public void setFirstCondition(Condition c1)
    {
        this.firstCondition = c1;
    }

    public void setSecondCondition(Condition c2)
    {
        this.secondCondition = c2;
    }


    public AndConditionImpl(Condition c1, Condition c2) {
        this.firstCondition = c1;
        this.secondCondition = c2;
    }

    public AndConditionImpl()
    {
    }


    public short getConditionType() {
        return Condition.SAC_AND_CONDITION;
    }

    public Condition getFirstCondition() {
        return this.firstCondition;
    }

    public Condition getSecondCondition() {
        return this.secondCondition;
    }
    
    public String toString() {
        return this.getFirstCondition().toString() + this.getSecondCondition().toString();
    }
}
