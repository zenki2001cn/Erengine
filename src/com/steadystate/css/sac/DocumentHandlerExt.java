/*
 * Created on 04.12.2007
 *
 */
package com.steadystate.css.sac;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.DocumentHandler;

public interface DocumentHandlerExt extends DocumentHandler
{

    /**
     * Receive notification of a charset at-rule.
     * 
     * @param characterEncoding the character encoding
     * @throws CSSException Any CSS exception, possibly wrapping another exception.
     */
    public void charset(String characterEncoding) throws CSSException;

}
