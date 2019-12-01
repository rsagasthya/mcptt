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


@Root(strict=false, name = "OffNetworkType")
public class OffNetworkType {


    @ElementList(required=false,inline=true,entry = "User-Info-ID")
    protected List<String> UserInfoID;
    @ElementList(required=false,inline=true,entry = "MCPTTGroupInfo")
    protected List<ListEntryType> mcpttGroupInfo;
    @ElementList(required=false,inline=true,entry = "GroupServerInfo")
    protected List<GroupServerInfoType> groupServerInfo;
    @Attribute(required = false , name = "index")
    protected String index;


    public List<String> getUserInfoID() {
        return UserInfoID;
    }

    public void setUserInfoID(List<String> userInfoID) {
        UserInfoID = userInfoID;
    }

    public List<ListEntryType> getMcpttGroupInfo() {
        return mcpttGroupInfo;
    }

    public void setMcpttGroupInfo(List<ListEntryType> mcpttGroupInfo) {
        this.mcpttGroupInfo = mcpttGroupInfo;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String value) {
        this.index = value;
    }

    public List<GroupServerInfoType> getGroupServerInfo() {
        return groupServerInfo;
    }

    public void setGroupServerInfo(List<GroupServerInfoType> groupServerInfo) {
        this.groupServerInfo = groupServerInfo;
    }
}
