/*
 * $Id: MediaListImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
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

package com.steadystate.css.dom;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.DOMException;

import org.w3c.dom.stylesheets.MediaList;

import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.SACMediaList;

import com.steadystate.css.parser.SACParserCSS2;

/**
 * Implements {@link MediaList}.
 * 
 * @author <a href="mailto:davidsch@users.sourceforge.net">David Schweinsberg</a>
 * @version $Id: MediaListImpl.java,v 1.3 2008/03/26 02:17:24 sdanig Exp $
 */
public class MediaListImpl extends CSSOMObjectImpl implements MediaList, Serializable {

    private static final long serialVersionUID = 6662784733573034870L;

    private List<String> media = new ArrayList<String>();

    public void setMedia(List<String> media)
    {
        this.media = media;
    }

    private void setMediaList(SACMediaList mediaList)
    {
        for (int i = 0; i < mediaList.getLength(); i++)
        {
            this.media.add(mediaList.item(i));
        }
    }

    public MediaListImpl(SACMediaList mediaList)
    {
        this.setMediaList(mediaList);
    }

    public MediaListImpl()
    {
    }


    public String getMediaText() {
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < this.media.size(); i++) {
            sb.append(this.media.get(i));
            if (i < this.media.size() - 1) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }

    public void setMediaText(String mediaText) throws DOMException {
        InputSource source = new InputSource(new StringReader(mediaText));
        try
        {
            // TODO get SAC Parser version from System property?
            SACMediaList sml = new SACParserCSS2().parseMedia(source);
            this.setMediaList(sml);
        }
        catch (CSSParseException e)
        {
            throw new DOMException(DOMException.SYNTAX_ERR,
                e.getLocalizedMessage());
        }
        catch (IOException e)
        {
            throw new DOMException(DOMException.NOT_FOUND_ERR,
                e.getLocalizedMessage());
        }
    }

    public int getLength() {
        return this.media.size();
    }

    public String item(int index) {
        return (index < this.media.size()) ? this.media.get(index) : null;
    }

    public void deleteMedium(String oldMedium) throws DOMException {
        for (int i = 0; i < this.media.size(); i++) {
            String str = this.media.get(i);
            if (str.equalsIgnoreCase(oldMedium)) {
                this.media.remove(i);
                return;
            }
        }
        throw new DOMExceptionImpl(
            DOMException.NOT_FOUND_ERR,
            DOMExceptionImpl.NOT_FOUND);
    }

    public void appendMedium(String newMedium) throws DOMException {
        this.media.add(newMedium);
    }

    public String toString() {
        return this.getMediaText();
    }
}
