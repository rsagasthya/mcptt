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
import org.simpleframework.xml.Root;


@Root(strict=false, name = "On-networkType")
public class OnNetworkType {

    @Element(required = false , name = "IPv6Preferred")
    protected Boolean iPv6Preferred;
    @Element(required = false , name = "Relay-Service")
    protected Boolean relayService;
    @Element(required = false , name = "Relayed-MCPTT-Groups")
    protected RelayedMCPTTGroupType relayedMCPTTGroup;


    @Attribute(required = false , name = "index")
    protected String index;



    public Boolean isIPv6Preferred() {
        return iPv6Preferred;
    }


    public void setIPv6Preferred(boolean value) {
        this.iPv6Preferred = value;
    }


    public Boolean isRelayService() {
        return relayService;
    }


    public void setRelayService(boolean value) {
        this.relayService = value;
    }


    public RelayedMCPTTGroupType getRelayedMCPTTGroup() {
        return relayedMCPTTGroup;
    }


    public void setRelayedMCPTTGroup(RelayedMCPTTGroupType value) {
        this.relayedMCPTTGroup = value;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }

}
