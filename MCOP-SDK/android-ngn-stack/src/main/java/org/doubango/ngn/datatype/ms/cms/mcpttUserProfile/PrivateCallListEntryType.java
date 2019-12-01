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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "PrivateCallListEntryType")
public class PrivateCallListEntryType {


    @ElementList(required=false,inline=true,entry = "PrivateCallURI")
    protected List<EntryType> privateCallURI;
    @ElementList(required=false,inline=true,entry = "PrivateCallProSeUser")
    protected List<ProSeUserEntryType> privateCallProSeUser;
    @ElementList(required=false,inline=true,entry = "PrivateCallKMSURI")
    protected List<EntryType> privateCallKMSURIS;

    @Attribute(required = false , name = "index")
    protected String index;

    public List<EntryType> getPrivateCallURI() {
        return privateCallURI;
    }

    public void setPrivateCallURI(List<EntryType> privateCallURI) {
        this.privateCallURI = privateCallURI;
    }

    public List<ProSeUserEntryType> getPrivateCallProSeUser() {
        return privateCallProSeUser;
    }

    public void setPrivateCallProSeUser(List<ProSeUserEntryType> privateCallProSeUser) {
        this.privateCallProSeUser = privateCallProSeUser;
    }

    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }

    public List<EntryType> getPrivateCallKMSURIS() {
        return privateCallKMSURIS;
    }

    public void setPrivateCallKMSURIS(List<EntryType> privateCallKMSURIS) {
        this.privateCallKMSURIS = privateCallKMSURIS;
    }
}
