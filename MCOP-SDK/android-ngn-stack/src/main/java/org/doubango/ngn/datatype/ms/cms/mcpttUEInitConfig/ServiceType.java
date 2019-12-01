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




package org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "ServiceType")
public class ServiceType {

    @Element(required = false , name = "MCPTT-to-con-ref")
    protected String mcpttToConRef;
    @Element(required = false , name = "MC-common-core-to-con-ref")
    protected String mcCommonCoreToConRef;
    @Element(required = false , name = "MC-ID-to-con-ref")
    protected String mcidToConRef;


    public String getMCPTTToConRef() {
        return mcpttToConRef;
    }


    public void setMCPTTToConRef(String value) {
        this.mcpttToConRef = value;
    }


    public String getMCCommonCoreToConRef() {
        return mcCommonCoreToConRef;
    }


    public void setMCCommonCoreToConRef(String value) {
        this.mcCommonCoreToConRef = value;
    }


    public String getMCIDToConRef() {
        return mcidToConRef;
    }


    public void setMCIDToConRef(String value) {
        this.mcidToConRef = value;
    }

}
