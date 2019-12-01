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

import java.math.BigInteger;
import java.util.List;



@Root(strict=false, name = "OnNetworkType")
public class OnNetworkType {


    @ElementList(required=false,inline=true,entry = "MCPTTGroupInfo")
    protected List<ListEntryType> mCPTTGroupInfo;
    @ElementList(required=false,inline=true,entry = "MaxSimultaneousTransmissionsN7")
    protected List<BigInteger> maxSimultaneousTransmissionsN7;
    @ElementList(required=false,inline=true,entry = "ImplicitAffiliations")
    protected List<ListEntryType> implicitAffiliations;
    @ElementList(required=false,inline=true,entry = "PrivateEmergencyAlert")
    protected List<EmergencyAlertType> privateEmergencyAlert;
    @ElementList(required=false,inline=true,entry = "MaxAffiliationsN2")
    protected List<BigInteger> maxAffiliationsN2;
    /*
    The RemoteGroupSelectionURIList list indicates which MCPTT user you can send a message to remotely configure the group by default.
    Upon receiving this configuration message by the user, he must verify that he belongs to that group and that he can be affiliated.
     */
    @ElementList(required=false,inline=true,entry = "RemoteGroupSelectionURIList")
    protected List<ListEntryType> remoteGroupSelectionURIList;
    @ElementList(required=false,inline=true,entry = "GroupServerInfo")
    protected List<GroupServerInfoType> groupServerInfo;
    @Attribute(required = false , name = "index")
    protected String index;


    public List<ListEntryType> getmCPTTGroupInfo() {
        return mCPTTGroupInfo;
    }

    public void setmCPTTGroupInfo(List<ListEntryType> mCPTTGroupInfo) {
        this.mCPTTGroupInfo = mCPTTGroupInfo;
    }

    public List<BigInteger> getMaxSimultaneousTransmissionsN7() {
        return maxSimultaneousTransmissionsN7;
    }

    public void setMaxSimultaneousTransmissionsN7(List<BigInteger> maxSimultaneousTransmissionsN7) {
        this.maxSimultaneousTransmissionsN7 = maxSimultaneousTransmissionsN7;
    }

    public List<ListEntryType> getImplicitAffiliations() {
        return implicitAffiliations;
    }

    public void setImplicitAffiliations(List<ListEntryType> implicitAffiliations) {
        this.implicitAffiliations = implicitAffiliations;
    }

    public List<EmergencyAlertType> getPrivateEmergencyAlert() {
        return privateEmergencyAlert;
    }

    public void setPrivateEmergencyAlert(List<EmergencyAlertType> privateEmergencyAlert) {
        this.privateEmergencyAlert = privateEmergencyAlert;
    }

    public List<BigInteger> getMaxAffiliationsN2() {
        return maxAffiliationsN2;
    }

    public void setMaxAffiliationsN2(List<BigInteger> maxAffiliationsN2) {
        this.maxAffiliationsN2 = maxAffiliationsN2;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }


    public List<ListEntryType> getRemoteGroupSelectionURIList() {
        return remoteGroupSelectionURIList;
    }

    public void setRemoteGroupSelectionURIList(List<ListEntryType> remoteGroupSelectionURIList) {
        this.remoteGroupSelectionURIList = remoteGroupSelectionURIList;
    }

    public List<GroupServerInfoType> getGroupServerInfo() {
        return groupServerInfo;
    }

    public void setGroupServerInfo(List<GroupServerInfoType> groupServerInfo) {
        this.groupServerInfo = groupServerInfo;
    }
}
