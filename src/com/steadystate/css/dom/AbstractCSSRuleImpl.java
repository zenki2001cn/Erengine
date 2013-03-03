/*
 * Created on 05.12.2005
 *
 */
package com.steadystate.css.dom;

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

/**
 * @author koch
 */
public abstract class AbstractCSSRuleImpl extends CSSOMObjectImpl
{

    protected CSSStyleSheetImpl parentStyleSheet = null;
    protected CSSRule parentRule = null;

    public void setParentStyleSheet(CSSStyleSheetImpl parentStyleSheet)
    {
        this.parentStyleSheet = parentStyleSheet;
    }

    public void setParentRule(CSSRule parentRule)
    {
        this.parentRule = parentRule;
    }


    public AbstractCSSRuleImpl(CSSStyleSheetImpl parentStyleSheet,
        CSSRule parentRule)
    {
        super();
        this.parentStyleSheet = parentStyleSheet;
        this.parentRule = parentRule;
    }

    public AbstractCSSRuleImpl()
    {
    }


    public CSSStyleSheet getParentStyleSheet() {
        return this.parentStyleSheet;
    }

    public CSSRule getParentRule() {
        return this.parentRule;
    }

}
