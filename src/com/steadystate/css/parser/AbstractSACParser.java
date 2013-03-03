/*
 * $Id: AbstractSACParser.java,v 1.4 2008/03/26 02:17:24 sdanig Exp $
 *
 * CSS Parser Project
 *
 * Copyright (C) 2005-2008 David Schweinsberg.  All rights reserved.
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ConditionFactory;
import org.w3c.css.sac.DocumentHandler;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Locator;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorFactory;
import org.w3c.css.sac.SelectorList;

import com.steadystate.css.parser.selectors.ConditionFactoryImpl;
import com.steadystate.css.parser.selectors.SelectorFactoryImpl;
import com.steadystate.css.sac.DocumentHandlerExt;
import com.steadystate.css.sac.TestCSSParseException;

/**
 * Base implementation of {@link Parser}.
 * 
 * @author koch
 */
abstract class AbstractSACParser implements Parser
{
    private DocumentHandlerExt documentHandler = null;
    private ErrorHandler errorHandler = null;
    private InputSource source = null;
    private Locale locale = null;
    private SelectorFactory selectorFactory = null;
    private ConditionFactory conditionFactory = null;
    private ResourceBundle sacParserMessages;
    protected abstract Token getToken();

    protected DocumentHandlerExt getDocumentHandler()
    {
        if (this.documentHandler == null)
        {
            this.setDocumentHandler(new HandlerBase());
        }
        return this.documentHandler;
    }

    public void setDocumentHandler(DocumentHandler handler)
    {
        if (handler instanceof DocumentHandlerExt)
        {
            this.documentHandler = (DocumentHandlerExt) handler;
        }
    }

    protected ErrorHandler getErrorHandler()
    {
        if (this.errorHandler == null)
        {
            this.setErrorHandler(new HandlerBase());
        }
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler eh)
    {
        this.errorHandler = eh;
    }

    protected InputSource getInputSource()
    {
        return this.source;
    }

    public void setLocale(Locale locale)
    {
        if (this.locale != locale)
        {
            this.sacParserMessages = null;
        }
        this.locale = locale;
    }

    protected Locale getLocale()
    {
        if (this.locale == null)
        {
            this.setLocale(Locale.getDefault());
        }
        return this.locale;
    }
    
    protected SelectorFactory getSelectorFactory()
    {
        if (this.selectorFactory == null)
        {
            this.selectorFactory = new SelectorFactoryImpl();
        }
        return this.selectorFactory;
    }

    public void setSelectorFactory(SelectorFactory selectorFactory)
    {
        this.selectorFactory = selectorFactory;
    }
    
    protected ConditionFactory getConditionFactory()
    {
        if (this.conditionFactory == null)
        {
            this.conditionFactory = new ConditionFactoryImpl();
        }
        return this.conditionFactory;
    }

    public void setConditionFactory(ConditionFactory conditionFactory)
    {
        this.conditionFactory = conditionFactory;
    }

    protected ResourceBundle getSACParserMessages()
    {
        if (this.sacParserMessages == null)
        {
            this.sacParserMessages = ResourceBundle.getBundle(
                "com.steadystate.css.parser.SACParserMessages",
                this.getLocale());
        }
        return this.sacParserMessages;
    }

    public Locator getLocator()
    {
        return new LocatorImpl(this.getInputSource().getURI(),
            this.getToken() == null ? 0 : this.getToken().beginLine,
            this.getToken() == null ? 0 : this.getToken().beginColumn);//this.locator;
    }

    protected String add_escapes(String str)
    {
        StringBuilder retval = new StringBuilder();
        char ch;
        for (int i = 0; i < str.length(); i++)
        {
            switch (str.charAt(i))
            {
                case 0 :
                    continue;
                case '\b':
                    retval.append("\\b");
                    continue;
                case '\t':
                    retval.append("\\t");
                    continue;
                case '\n':
                    retval.append("\\n");
                    continue;
                case '\f':
                    retval.append("\\f");
                    continue;
                case '\r':
                    retval.append("\\r");
                    continue;
                case '\"':
                    retval.append("\\\"");
                    continue;
                case '\'':
                    retval.append("\\\'");
                    continue;
                case '\\':
                    retval.append("\\\\");
                    continue;
                default:
                    if ((ch = str.charAt(i)) < 0x20 || ch > 0x7e)
                    {
                       String s = "0000" + Integer.toString(ch, 16);
                       retval.append("\\u"
                           + s.substring(s.length() - 4, s.length()));
                    }
                    else
                    {
                       retval.append(ch);
                    }
                    continue;
            }
        }
        return retval.toString();
    }

    protected CSSParseException toCSSParseException(String key, ParseException e)
    {
        String messagePattern1 =
            this.getSACParserMessages().getString("invalidExpectingOne");
        String messagePattern2 =
            this.getSACParserMessages().getString("invalidExpectingMore");
        int maxSize = 0;
        StringBuilder expected = new StringBuilder();
        for (int i = 0; i < e.expectedTokenSequences.length; i++)
        {
            if (maxSize < e.expectedTokenSequences[i].length)
            {
                maxSize = e.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < e.expectedTokenSequences[i].length; j++)
            {
                expected.append(e.tokenImage[e.expectedTokenSequences[i][j]]);
            }
            //if (e.expectedTokenSequences[i][e.expectedTokenSequences[i].length - 1] != 0)
            if (i < e.expectedTokenSequences.length - 1)
            {
                expected.append(", ");
            }
        }
        StringBuilder invalid = new StringBuilder();
        Token tok = e.currentToken.next;
        for (int i = 0; i < maxSize; i++)
        {
            if (i != 0)
            {
                invalid.append(" ");
            }
            if (tok.kind == 0)
            {
                invalid.append(e.tokenImage[0]);
                break;
            }
            invalid.append(this.add_escapes(tok.image));
            tok = tok.next;
        }
        String s = null;
        try
        {
            s = this.getSACParserMessages().getString(key);
        }
        catch (MissingResourceException ex)
        {
            s = key;
        }
        StringBuffer message = new StringBuffer(s);
        message.append(' ');
        if (e.expectedTokenSequences.length == 1)
        {
            message.append(MessageFormat.format(
                messagePattern1, new Object[] {invalid, expected}));
        }
        else
        {
            message.append(MessageFormat.format(
                messagePattern2, new Object[] {invalid, expected}));
        }
        return new TestCSSParseException(message.toString(),
            this.getInputSource().getURI(), e.currentToken.next.beginLine,
            e.currentToken.next.beginColumn, this.getGrammarUri());
    }

    protected CSSParseException toCSSParseException(TokenMgrError e)
    {
        String messagePattern =
            this.getSACParserMessages().getString("tokenMgrError");
        return new TestCSSParseException(messagePattern,
            this.getInputSource().getURI(), 1, 1, this.getGrammarUri());
    }

    protected CSSParseException createSkipWarning(String key, CSSParseException e)
    {
        String s = null;
        try
        {
            s = this.getSACParserMessages().getString(key);
        }
        catch (MissingResourceException ex)
        {
            s = key;
        }
        return new TestCSSParseException(s, e.getURI(), e.getLineNumber(),
            e.getColumnNumber(), this.getGrammarUri());
    }

    public void parseStyleSheet(InputSource source)
        throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        try
        {
            this.styleSheet();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidStyleSheet", e));
        }
        catch (TokenMgrError e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException(e));
        }
        catch (CSSParseException e)
        {
            this.getErrorHandler().error(e);
        }
    }
    
    public void parseStyleSheet(String uri) throws IOException
    {
        this.parseStyleSheet(new InputSource(uri));
    }

    public void parseStyleDeclaration(InputSource source)
        throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        try
        {
            this.styleDeclaration();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidStyleDeclaration", e));
        }
    }
    
    public void parseRule(InputSource source) throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        try
        {
            this.styleSheetRuleSingle();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidRule", e));
        }
    }
    
    public SelectorList parseSelectors(InputSource source)
        throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        SelectorList sl = null;
        try
        {
            sl = this.selectorList();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidSelectorList", e));
        }
        return sl;
    }
    
    public LexicalUnit parsePropertyValue(InputSource source)
        throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        LexicalUnit lu = null;
        try
        {
            lu = expr();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidExpr", e));
        }
        return lu;
    }
    
    public boolean parsePriority(InputSource source)
        throws IOException
    {
        this.source = source;
        this.ReInit(getCharStream(source));
        boolean b = false;
        try
        {
            b = prio();
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidPrio", e));
        }
        return b;
    }
    
    public SACMediaList parseMedia(InputSource source) throws IOException
    {
        this.source = source;
        this.ReInit(this.getCharStream(source));
        SACMediaListImpl ml = new SACMediaListImpl();
        try
        {
            this.mediaList(ml);
        }
        catch (ParseException e)
        {
            this.getErrorHandler().error(
                this.toCSSParseException("invalidMediaList", e));
        }
        catch (CSSParseException e)
        {
            this.getErrorHandler().error(e);
        }
        return ml;
    }
    
    private CharStream getCharStream(InputSource source)
        throws IOException
    {
        if (source.getCharacterStream() != null)
        {
            return new ASCII_CharStream(
                source.getCharacterStream(), 1, 1);
        }
        else if (source.getByteStream() != null)
        {
            return new ASCII_CharStream(new InputStreamReader(
                source.getByteStream()), 1, 1);
        }
        else if (source.getURI() != null)
        {
            return new ASCII_CharStream(new InputStreamReader(
                new URL(source.getURI()).openStream()), 1, 1);
        }
        return null;
    }

    public abstract String getParserVersion();
    protected abstract String getGrammarUri();
    protected abstract void ReInit(CharStream charStream);
    protected abstract void styleSheet() throws CSSParseException, ParseException;
    protected abstract void styleDeclaration() throws ParseException;
    protected abstract void styleSheetRuleSingle() throws ParseException;
    protected abstract SelectorList selectorList() throws ParseException;
    protected abstract LexicalUnit expr() throws ParseException;
    protected abstract boolean prio() throws ParseException;
    protected abstract void mediaList(SACMediaListImpl ml) throws ParseException;

    protected void handleStartDocument()
    {
        this.getDocumentHandler().startDocument(this.getInputSource());
    }

    protected void handleEndDocument()
    {
        this.getDocumentHandler().endDocument(this.getInputSource());
    }

    protected void handleIgnorableAtRule(String s)
    {
        this.getDocumentHandler().ignorableAtRule(s);
    }

    protected void handleCharset(String characterEncoding)
    {
        this.getDocumentHandler().charset(characterEncoding);
    }

    protected void handleImportStyle(String uri, SACMediaList media,
        String defaultNamespaceURI)
    {
        this.getDocumentHandler().importStyle(uri, media, defaultNamespaceURI);
    }

    protected void handleStartMedia(SACMediaList media)
    {
        this.getDocumentHandler().startMedia(media);
    }

    protected void handleMedium(String medium)
    {
    }

    protected void handleEndMedia(SACMediaList media)
    {
        this.getDocumentHandler().endMedia(media);
    }

    protected void handleStartPage(String name, String pseudo_page)
    {
        this.getDocumentHandler().startPage(name, pseudo_page);
    }

    protected void handleEndPage(String name, String pseudo_page)
    {
        this.getDocumentHandler().endPage(name, pseudo_page);
    }

    protected void handleStartFontFace()
    {
        this.getDocumentHandler().startFontFace();
    }

    protected void handleEndFontFace()
    {
        this.getDocumentHandler().endFontFace();
    }

    protected void handleSelector(Selector selector)
    {
    }

    protected void handleStartSelector(SelectorList selectors)
    {
        this.getDocumentHandler().startSelector(selectors);
    }

    protected void handleEndSelector(SelectorList selectors)
    {
        this.getDocumentHandler().endSelector(selectors);
    }

    protected void handleProperty(String name, LexicalUnit value,
        boolean important)
    {
        this.getDocumentHandler().property(name, value, important);
    }

    protected LexicalUnit functionInternal(LexicalUnit prev, Token t,
        LexicalUnit params)
    {
        if (t.image.equalsIgnoreCase("counter(")) {
            return LexicalUnitImpl.createCounter(prev, params);
         } else if (t.image.equalsIgnoreCase("counters(")) {
            return LexicalUnitImpl.createCounters(prev, params);
         } else if (t.image.equalsIgnoreCase("attr(")) {
            return LexicalUnitImpl.createAttr(prev, params);
         } else if (t.image.equalsIgnoreCase("rect(")) {
            return LexicalUnitImpl.createRect(prev, params);
         }
         return LexicalUnitImpl.createFunction(
             prev,
             t.image.substring(0, t.image.length() - 1),
             params);
    }

    protected LexicalUnit hexcolorInternal(LexicalUnit prev, Token t)
    {
        // Step past the hash at the beginning
        int i = 1;
        int r = 0;
        int g = 0;
        int b = 0;
        int len = t.image.length() - 1;
        String pattern = this.getSACParserMessages().getString("invalidColor");
        try
        {
            if (len == 3) {
                r = Integer.parseInt(t.image.substring(i + 0, i + 1), 16);
                g = Integer.parseInt(t.image.substring(i + 1, i + 2), 16);
                b = Integer.parseInt(t.image.substring(i + 2, i + 3), 16);
                r = (r << 4) | r;
                g = (g << 4) | g;
                b = (b << 4) | b;
            } else if (len == 6) {
                r = Integer.parseInt(t.image.substring(i + 0, i + 2), 16);
                g = Integer.parseInt(t.image.substring(i + 2, i + 4), 16);
                b = Integer.parseInt(t.image.substring(i + 4, i + 6), 16);
            } else {
                throw new TestCSSParseException(MessageFormat.format(
                    pattern, new Object[] {t}),
                    this.getInputSource().getURI(), t.beginLine,
                    t.beginColumn, this.getGrammarUri());
            }

            // Turn into an "rgb()"
            LexicalUnit lr = LexicalUnitImpl.createNumber(null, r);
            LexicalUnit lc1 = LexicalUnitImpl.createComma(lr);
            LexicalUnit lg = LexicalUnitImpl.createNumber(lc1, g);
            LexicalUnit lc2 = LexicalUnitImpl.createComma(lg);
            LexicalUnit lb = LexicalUnitImpl.createNumber(lc2, b);

            return LexicalUnitImpl.createRgbColor(prev, lr);
        }
        catch (NumberFormatException ex)
        {
            throw new TestCSSParseException(MessageFormat.format(
                pattern, new Object[] {t}),
                this.getInputSource().getURI(), t.beginLine,
                t.beginColumn, ex, this.getGrammarUri());
        }
    }

    int intValue(char op, String s)
    {
        return ((op == '-') ? -1 : 1) * Integer.parseInt(s);
    }

    float floatValue(char op, String s) {
        return ((op == '-') ? -1 : 1) * Float.parseFloat(s);
    }

    int getLastNumPos(String s) {
        int i;
        for (i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) {
                break;
            }
        }
        return i - 1;
    }

    /**
     * Unescapes escaped characters in the specified string, according to the
     * <a href="http://www.w3.org/TR/CSS21/syndata.html#escaped-characters">CSS specification</a>.
     * 
     * This could be done directly in the parser, but portions of the lexer would have to be moved
     * to the parser, meaning that the grammar would no longer match the standard grammar specified
     * by the W3C. This would make the parser and lexer much less maintainable.
     */
    String unescape(String s) {
        int len = s.length();
        StringBuffer buf = new StringBuffer(len);
        int index = 0;

        while (index < len) {
            char c = s.charAt(index);
            if (c == '\\') {
                if (++index < len) {
                    c = s.charAt(index);
                    switch (c) {
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                        int numValue = Character.digit(c, 16);
                        int count = 0;
                        int p = 16;

                        while (index + 1 < len && count < 6) {
                            c = s.charAt(index+1);

                            if (Character.digit(c, 16) != -1) {
                                numValue = (numValue * 16) + Character.digit(c, 16);
                                p *= 16;
                                index++;
                            } else {
                                if (Character.isWhitespace(c)) {
                                    // skip the latest white space
                                    index++;
                                }
                                break;
                            }
                        }
                        buf.append((char) numValue);
                        break;
                        case '\n':
                        case '\f':
                        break;
                        case '\r':
                        if (index + 1 < len) {
                            if (s.charAt(index + 1) == '\n') {
                                index ++;
                            }
                        }
                        break;
                    default:
                        buf.append(c);
                    }
                } else {
                    throw new TestCSSParseException("invalid string " + s,
                        getLocator(), this.getGrammarUri());
                }
            } else {
                buf.append(c);
            }
            index++;
        }

        return buf.toString();
    }

}
