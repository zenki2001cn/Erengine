/*
 * Created on 26.01.2006
 *
 */
package com.steadystate.css.sac;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.Locator;

public class TestCSSParseException extends CSSParseException
{

    private static final long serialVersionUID = -4892920039949256795L;

    private String testCaseUri;

    public void setTestCaseUri(String testCaseUri)
    {
        this.testCaseUri = testCaseUri;
    }


    public TestCSSParseException(String message, Locator locator,
        String testCaseUri)
    {
        super(message, locator);
        this.setTestCaseUri(testCaseUri);
    }

    public TestCSSParseException(String message, Locator locator, Exception e,
        String testCaseUri)
    {
        super(message, locator, e);
        this.setTestCaseUri(testCaseUri);
    }

    public TestCSSParseException(String message, String uri, int lineNumber,
        int columnNumber, String testCaseUri)
    {
        super(message, uri, lineNumber, columnNumber);
        this.setTestCaseUri(testCaseUri);
    }

    public TestCSSParseException(String message, String uri, int lineNumber,
        int columnNumber, Exception e, String testCaseUri)
    {
        super(message, uri, lineNumber, columnNumber, e);
        this.setTestCaseUri(testCaseUri);
    }


    public String getTestCaseUri()
    {
        return this.testCaseUri;
    }
}
