/*
 * $Id: CSSOMParser.java,v 1.2 2008/03/26 02:08:55 sdanig Exp $
 *
 * CSS Parser Project
 *
 * Copyright (C) 1999-2008 David Schweinsberg.  All rights reserved.
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

import java.util.Properties;
import java.util.Stack;

import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Parser;
import org.w3c.css.sac.SACMediaList;
import org.w3c.css.sac.SelectorList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;

import org.w3c.css.sac.helpers.ParserFactory;

import com.steadystate.css.dom.CSSCharsetRuleImpl;
import com.steadystate.css.dom.CSSFontFaceRuleImpl;
import com.steadystate.css.dom.CSSImportRuleImpl;
import com.steadystate.css.dom.CSSMediaRuleImpl;
import com.steadystate.css.dom.CSSOMObject;
import com.steadystate.css.dom.CSSPageRuleImpl;
import com.steadystate.css.dom.CSSRuleListImpl;
import com.steadystate.css.dom.CSSStyleDeclarationImpl;
import com.steadystate.css.dom.CSSStyleRuleImpl;
import com.steadystate.css.dom.CSSStyleSheetImpl;
import com.steadystate.css.dom.CSSUnknownRuleImpl;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.MediaListImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.sac.DocumentHandlerExt;
import com.steadystate.css.userdata.UserDataConstants;

/** 
 *
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: CSSOMParser.java,v 1.2 2008/03/26 02:08:55 sdanig Exp $
 */
public class CSSOMParser {
    
    private static final String PARSER = "com.steadystate.css.parser.SACParserCSS2";
    
    private static boolean use_internal = false;

    private Parser _parser = null;
    private CSSStyleSheetImpl _parentStyleSheet = null;
    // TODO what is this _parentRule for? It is not read locally.
    //private CSSRule _parentRule = null;

    /** Creates new CSSOMParser */
    public CSSOMParser() {
        try {
            // use the direct method if we already failed once before
            if(use_internal) {
                this._parser = new SACParserCSS2();
            } else {
                setProperty("org.w3c.css.sac.parser", PARSER);
                ParserFactory factory = new ParserFactory();
                this._parser = factory.makeParser();
            }
        } catch (Exception e) {
            use_internal = true;
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.err.println("using the default parser instead");
            this._parser = new SACParserCSS2();
        }
    }

    /**
     * Creates new CSSOMParser
     *
     * @param parser the SAC Parser
     */
    public CSSOMParser(Parser parser)
    {
        this._parser = parser;
    }
    

    public void setErrorHandler(ErrorHandler eh)
    {
        this._parser.setErrorHandler(eh);
    }

    /**
     * Parses a SAC input source into a CSSOM style sheet.
     * 
     * @param source the SAC input source
     * @param ownerNode the owner node (see the definition of
     *   <code>ownerNode</code> in org.w3c.dom.css.StyleSheet)
     * @param href the href (see the definition of <code>href</code> in
     *   org.w3c.dom.css.StyleSheet)
     * @return the CSSOM style sheet
     * @throws IOException if the underlying SAC parser throws an IOException
     */
    public CSSStyleSheet parseStyleSheet(InputSource source, Node ownerNode,
        String href) throws IOException {
        CSSOMHandler handler = new CSSOMHandler();
        handler.setOwnerNode(ownerNode);
        handler.setHref(href);
        this._parser.setDocumentHandler(handler);
        this._parser.parseStyleSheet(source);
        Object o = handler.getRoot();
        if (o instanceof CSSStyleSheet)
        {
            return (CSSStyleSheet) handler.getRoot();
        }
        return null;
    }
    
    /**
     * Parses a SAC input source into a CSSOM style declaration.
     * 
     * @param source the SAC input source
     * @return the CSSOM style declaration
     * @throws IOException if the underlying SAC parser throws an IOException
     */
    public CSSStyleDeclaration parseStyleDeclaration(InputSource source)
            throws IOException {
        CSSStyleDeclarationImpl sd = new CSSStyleDeclarationImpl(null);
        parseStyleDeclaration(sd, source);
        return sd;
    }
    
    public void parseStyleDeclaration(CSSStyleDeclaration sd, InputSource source)
            throws IOException {
        Stack nodeStack = new Stack();
        nodeStack.push(sd);
        CSSOMHandler handler = new CSSOMHandler(nodeStack);
        this._parser.setDocumentHandler(handler);
        this._parser.parseStyleDeclaration(source);
    }
    
    public CSSValue parsePropertyValue(InputSource source) throws IOException {
        CSSOMHandler handler = new CSSOMHandler();
        this._parser.setDocumentHandler(handler);
        return new CSSValueImpl(this._parser.parsePropertyValue(source));
    }
    
    public CSSRule parseRule(InputSource source) throws IOException {
        CSSOMHandler handler = new CSSOMHandler();
        this._parser.setDocumentHandler(handler);
        this._parser.parseRule(source);
        return (CSSRule) handler.getRoot();
    }
    
    public SelectorList parseSelectors(InputSource source) throws IOException {
        HandlerBase handler = new HandlerBase();
        this._parser.setDocumentHandler(handler);
        return this._parser.parseSelectors(source);
    }

    public void setParentStyleSheet(CSSStyleSheetImpl parentStyleSheet) {
        this._parentStyleSheet = parentStyleSheet;
    }
    
    protected CSSStyleSheetImpl getParentStyleSheet()
    {
        return this._parentStyleSheet;
    }
    // See _parentRule
    /*
    public void setParentRule(CSSRule parentRule) {
        _parentRule = parentRule;
    }
    */
    
    class CSSOMHandler implements DocumentHandlerExt {
        
        private Stack _nodeStack;
        private Object _root = null;
        private Node ownerNode;
        private String href;

        private Node getOwnerNode()
        {
            return this.ownerNode;
        }

        private void setOwnerNode(Node ownerNode)
        {
            this.ownerNode = ownerNode;
        }

        private String getHref()
        {
            return this.href;
        }

        private void setHref(String href)
        {
            this.href = href;
        }

        public CSSOMHandler(Stack nodeStack) {
            this._nodeStack = nodeStack;
        }
        
        public CSSOMHandler() {
            this._nodeStack = new Stack();
        }
        
        public Object getRoot() {
            return this._root;
        }
        
        public void startDocument(InputSource source) throws CSSException {
            if (this._nodeStack.empty()) {
                CSSStyleSheetImpl ss = new CSSStyleSheetImpl();
                CSSOMParser.this.setParentStyleSheet(ss);
                ss.setOwnerNode(this.getOwnerNode());
                ss.setBaseUri(source.getURI());
                ss.setHref(this.getHref());
                ss.setMediaText(source.getMedia());
                ss.setTitle(source.getTitle());
                // Create the rule list
                CSSRuleListImpl rules = new CSSRuleListImpl();
                ss.setCssRules(rules);
                this._nodeStack.push(ss);
                this._nodeStack.push(rules);
            } else {
                // Error
            }
        }

        public void endDocument(InputSource source) throws CSSException {

            // Pop the rule list and style sheet nodes
            this._nodeStack.pop();
            this._root = this._nodeStack.pop();
        }

        public void comment(String text) throws CSSException {
        }

        public void ignorableAtRule(String atRule) throws CSSException {

            // Create the unknown rule and add it to the rule list
            CSSUnknownRuleImpl ir = new CSSUnknownRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(),
                atRule);
            this.addLocator(ir);
            if (!this._nodeStack.empty()) {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(ir);
            } else {
//                _nodeStack.push(ir);
                this._root = ir;
            }
        }

        public void namespaceDeclaration(String prefix, String uri)
                throws CSSException {
        }

        public void charset(String characterEncoding) throws CSSException
        {
            CSSCharsetRuleImpl cr = new CSSCharsetRuleImpl(CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(),
                characterEncoding);
            this.addLocator(cr);
            if (!this._nodeStack.empty())
            {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(cr);
            }
            else
            {
                this._root = cr;
            }
        }

        public void importStyle(
                String uri,
                SACMediaList media, 
                String defaultNamespaceURI) throws CSSException {
            // Create the import rule and add it to the rule list
            CSSImportRuleImpl ir = new CSSImportRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(),
                uri,
                new MediaListImpl(media));
            this.addLocator(ir);
            if (!this._nodeStack.empty()) {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(ir);
            } else {
//                _nodeStack.push(ir);
                this._root = ir;
            }
        }

        public void startMedia(SACMediaList media) throws CSSException {

            MediaListImpl ml = new MediaListImpl(media);
            this.addLocator(ml);
            // Create the media rule and add it to the rule list
            CSSMediaRuleImpl mr = new CSSMediaRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(),
                ml);
            this.addLocator(mr);
            if (!this._nodeStack.empty()) {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(mr);
            }

            // Create the rule list
            CSSRuleListImpl rules = new CSSRuleListImpl();
            mr.setRuleList(rules);
            this._nodeStack.push(mr);
            this._nodeStack.push(rules);
        }

        public void endMedia(SACMediaList media) throws CSSException {

            // Pop the rule list and media rule nodes
            this._nodeStack.pop();
            this._root = this._nodeStack.pop();
        }

        public void startPage(String name, String pseudo_page) throws CSSException {

            // Create the page rule and add it to the rule list
            CSSPageRuleImpl pr = new CSSPageRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(), name, pseudo_page);
            this.addLocator(pr);
            if (!this._nodeStack.empty()) {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(pr);
            }

            // Create the style declaration
            CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(pr);
            pr.setStyle(decl);
            this._nodeStack.push(pr);
            this._nodeStack.push(decl);
        }

        public void endPage(String name, String pseudo_page) throws CSSException {

            // Pop both the style declaration and the page rule nodes
            this._nodeStack.pop();
            this._root = this._nodeStack.pop();
        }

        public void startFontFace() throws CSSException {

            // Create the font face rule and add it to the rule list
            CSSFontFaceRuleImpl ffr = new CSSFontFaceRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule());
            this.addLocator(ffr);
            if (!this._nodeStack.empty()) {
                ((CSSRuleListImpl)this._nodeStack.peek()).add(ffr);
            }

            // Create the style declaration
            CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(ffr);
            ffr.setStyle(decl);
            this._nodeStack.push(ffr);
            this._nodeStack.push(decl);
        }

        public void endFontFace() throws CSSException {

            // Pop both the style declaration and the font face rule nodes
            this._nodeStack.pop();
            this._root = this._nodeStack.pop();
        }

        public void startSelector(SelectorList selectors) throws CSSException {

            // Create the style rule and add it to the rule list
            CSSStyleRuleImpl sr = new CSSStyleRuleImpl(
                CSSOMParser.this.getParentStyleSheet(),
                this.getParentRule(), selectors);
            this.addLocator(sr);
            if (!this._nodeStack.empty()) {
                Object o = this._nodeStack.peek();
                ((CSSRuleListImpl)/*this._nodeStack.peek()*/o).add(sr);
            }
            
            // Create the style declaration
            CSSStyleDeclarationImpl decl = new CSSStyleDeclarationImpl(sr);
            sr.setStyle(decl);
            this._nodeStack.push(sr);
            this._nodeStack.push(decl);
        }

        public void endSelector(SelectorList selectors) throws CSSException {

            // Pop both the style declaration and the style rule nodes
            this._nodeStack.pop();
            this._root = this._nodeStack.pop();
        }

        public void property(String name, LexicalUnit value, boolean important)
                throws CSSException {
            CSSStyleDeclarationImpl decl =
                (CSSStyleDeclarationImpl) this._nodeStack.peek();
            try
            {
                Property property = 
                    new Property(name, new CSSValueImpl(value), important);
                this.addLocator(property);
                decl.addProperty(property);
            }
            catch (DOMException e)
            {
                // call ErrorHandler?
            }
        }
        
        private CSSRule getParentRule()
        {
            if (!this._nodeStack.empty() && this._nodeStack.size() > 1)
            {
                Object node = this._nodeStack.get(this._nodeStack.size() - 2);
                if (node instanceof CSSRule)
                {
                    return (CSSRule) node;
                }
            }
            return null;
        }
        
        private void addLocator(CSSOMObject cssomObject)
        {
            cssomObject.setUserData(UserDataConstants.KEY_LOCATOR,
                ((AbstractSACParser) CSSOMParser.this._parser).getLocator());
        }
    }

    public static void setProperty(String key, String val) {
        Properties props = System.getProperties();
        props.put(key, val);
        System.setProperties(props);
    }
}
