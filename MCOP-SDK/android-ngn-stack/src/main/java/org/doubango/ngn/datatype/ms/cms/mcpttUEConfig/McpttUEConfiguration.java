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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "mcptt-UE-configuration")
@NamespaceList({
        @Namespace(reference = "urn:3gpp:mcptt:mcpttUEConfig:1.0"), // Add your reference here!
        @Namespace(prefix = "xml", reference = "http://www.w3.org/XML/1998/namespace")
})
public class McpttUEConfiguration {
    @ElementList(required=false,inline=true,entry = "mcptt-UE-id")
    protected List<MCPTTUEIDType> mcpttUEId;
    @ElementList(required=false,inline=true,entry = "name")
    protected List<NameType> name;
    @Element(required = true,name = "common")
    protected CommonType common;
    @Element(required = false , name = "on-network")
    protected OnNetworkType onNetwork;
    @Attribute(required = false , name = "domain")
    protected String domain;
    @Attribute(required = false , name = "XUI-URI")
    protected String xuiuri;
    @Attribute(required = false , name = "Instance-ID-URN")
    protected String instanceIDURN;




    public CommonType getCommon() {
        return common;
    }


    public void setCommon(CommonType value) {
        this.common = value;
    }


    public OnNetworkType getOnNetwork() {
        return onNetwork;
    }


    public void setOnNetwork(OnNetworkType value) {
        this.onNetwork = value;
    }


    public String getDomain() {
        return domain;
    }


    public void setDomain(String value) {
        this.domain = value;
    }


    public String getXUIURI() {
        return xuiuri;
    }


    public void setXUIURI(String value) {
        this.xuiuri = value;
    }


    public String getInstanceIDURN() {
        return instanceIDURN;
    }


    public void setInstanceIDURN(String value) {
        this.instanceIDURN = value;
    }

    public List<MCPTTUEIDType> getMcpttUEId() {
        return mcpttUEId;
    }

    public void setMcpttUEId(List<MCPTTUEIDType> mcpttUEId) {
        this.mcpttUEId = mcpttUEId;
    }

    public List<NameType> getName() {
        return name;
    }

    public void setName(List<NameType> name) {
        this.name = name;
    }
}
