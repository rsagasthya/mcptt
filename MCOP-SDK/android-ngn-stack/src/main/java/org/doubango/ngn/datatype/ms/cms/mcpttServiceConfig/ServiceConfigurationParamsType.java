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




package org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(strict=false, name =  "service-configuration-params-Type")
public class ServiceConfigurationParamsType {
    @ElementList(required=false,inline=true,entry = "common")
    protected List<CommonType> common;
    @ElementList(required=false,inline=true,entry = "on-network")
    protected List<OnNetworkType> onNetwork;
    @ElementList(required=false,inline=true,entry = "off-network")
    protected List<OffNetworkType> offNetwork;


    @Attribute(required = false , name = "domain")
    protected String domain;



    public List<CommonType> getCommon() {
        if (common == null) {
            common = new ArrayList<CommonType>();
        }
        return this.common;
    }


    public List<OnNetworkType> getOnNetwork() {
        if (onNetwork == null) {
            onNetwork = new ArrayList<OnNetworkType>();
        }
        return this.onNetwork;
    }


    public List<OffNetworkType> getOffNetwork() {
        if (offNetwork == null) {
            offNetwork = new ArrayList<OffNetworkType>();
        }
        return this.offNetwork;
    }


    public String getDomain() {
        return domain;
    }


    public void setDomain(String value) {
        this.domain = value;
    }

    public void setCommon(List<CommonType> common) {
        this.common = common;
    }

    public void setOnNetwork(List<OnNetworkType> onNetwork) {
        this.onNetwork = onNetwork;
    }

    public void setOffNetwork(List<OffNetworkType> offNetwork) {
        this.offNetwork = offNetwork;
    }
}
