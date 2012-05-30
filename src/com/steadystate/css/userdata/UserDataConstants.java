/*
 * Created on 13.04.2006
 *
 */
package com.steadystate.css.userdata;

public class UserDataConstants
{

    private static final String KEY_PREFIX =
        UserDataConstants.class.getPackage().getName();
    
    private UserDataConstants()
    {
    }

    public static final String KEY_LOCATOR = KEY_PREFIX + ".locator";
    
}
