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



package org.doubango.ngn.datatype.mcpttinfo;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "contentType")
@Namespace(reference = "urn:3gpp:ns:mcpttInfo:1.0") // Add your reference here!
public class ContentType {

    @Element(required=false ,name="mcpttURI")
    protected String mcpttURI;
    @Element(required=false ,name="mcpttString")
    protected String mcpttString;
    @Element(required=false ,name="mcpttBoolean")
    protected Boolean mcpttBoolean;
    @Attribute(required=false ,name = "type")
    protected ProtectionType type;


    public String getMcpttURI() {
        return mcpttURI;
    }


    public void setMcpttURI(String value) {
        this.mcpttURI = value;
    }


    public String getMcpttString() {
        return mcpttString;
    }


    public void setMcpttString(String value) {
        this.mcpttString = value;
    }



    public Boolean isMcpttBoolean() {
        return mcpttBoolean;
    }


    public void setMcpttBoolean(Boolean value) {
        this.mcpttBoolean = value;
    }

    public ProtectionType getType() {
        return type;
    }

    public void setType(ProtectionType type) {
        this.type = type;
    }
}
