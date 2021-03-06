/*

*  Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* This file is part of MCOP MCPTT Client
*
* This is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/




package org.doubango.ngn.datatype.ms.cms.mcpttUserProfile;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root(strict=false, name = "entry")
public class EntryType {

    @Element(required = false , name = "uri-entry")
    protected String uriEntry;
    @Element(required = false , name = "display-name")
    protected DisplayNameElementType displayName;

    @Attribute(required = false , name = "entry-info")
    protected EntryInfoTypeList entryInfo;
    @Attribute(required = false , name = "index")
    protected String index;



    public String getUriEntry() {
        return uriEntry;
    }


    public void setUriEntry(String value) {
        this.uriEntry = value;
    }


    public DisplayNameElementType getDisplayName() {
        return displayName;
    }


    public void setDisplayName(DisplayNameElementType value) {
        this.displayName = value;
    }




    public EntryInfoTypeList getEntryInfo() {
        return entryInfo;
    }


    public void setEntryInfo(EntryInfoTypeList value) {
        this.entryInfo = value;
    }

    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }

}
