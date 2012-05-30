/*
 * Created on 05.12.2005
 *
 */
package com.steadystate.css.dom;

/**
 * @author koch
 */
public interface CSSOMObject
{

    public Object getUserData(String key);
    
    public Object setUserData(String key, Object data);
    
}
