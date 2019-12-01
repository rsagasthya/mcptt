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




package org.doubango.ngn.datatype.ms.cms.mcpttUEConfig;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(strict=false, name = "MCPTTUEIDType")
@Namespace(reference = "urn:3gpp:mcptt:mcpttUEConfig:1.0") // Add your reference here!
public class MCPTTUEIDType {

    @ElementList(required=false,inline=true,entry = "IMEI-range")
    protected List<IMEIRangeType> IMEIRange;
    @ElementList(required=false,inline=true,entry = "Instance-ID-URN")
    protected List<String> instanceIDURN;
    @Attribute(required = false , name = "index")
    protected String index;


    public List<IMEIRangeType> getIMEIRange() {
        return IMEIRange;
    }

    public void setIMEIRange(List<IMEIRangeType> IMEIRange) {
        this.IMEIRange = IMEIRange;
    }

    public List<String> getInstanceIDURN() {
        return instanceIDURN;
    }

    public void setInstanceIDURN(List<String> instanceIDURN) {
        this.instanceIDURN = instanceIDURN;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }



}
