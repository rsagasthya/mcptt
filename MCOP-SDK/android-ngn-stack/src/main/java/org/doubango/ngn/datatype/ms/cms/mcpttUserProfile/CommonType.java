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



@Root(strict=false, name = "Common")
public class CommonType {


    @ElementList(required=false,inline=true,entry = "MCPTTUserID")
    protected List<EntryType> mCPTTUserID;
    @ElementList(required=false,inline=true,entry = "MissionCriticalOrganization")
    protected List<String> missionCriticalOrganization;
    @ElementList(required=false,inline=true,entry = "PrivateCall")
    protected List<MCPTTPrivateCallType> privateCall;
    @ElementList(required=false,inline=true,entry = "UserAlias")
    protected List<UserAliasType> userAlias;
    @ElementList(required=false,inline=true,entry = "MCPTT-group-call")
    protected List<MCPTTGroupCallType> mCPTTGroupCall;
    @ElementList(required=false,inline=true,entry = "ParticipantType")
    protected List<String> participantType;
    @Attribute(required = false , name = "index")
    protected String index;



    public List<EntryType> getmCPTTUserID() {
        return mCPTTUserID;
    }

    public void setmCPTTUserID(List<EntryType> mCPTTUserID) {
        this.mCPTTUserID = mCPTTUserID;
    }

    public List<String> getMissionCriticalOrganization() {
        return missionCriticalOrganization;
    }

    public void setMissionCriticalOrganization(List<String> missionCriticalOrganization) {
        this.missionCriticalOrganization = missionCriticalOrganization;
    }

    public List<MCPTTPrivateCallType> getPrivateCall() {
        return privateCall;
    }

    public void setPrivateCall(List<MCPTTPrivateCallType> privateCall) {
        this.privateCall = privateCall;
    }

    public List<UserAliasType> getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(List<UserAliasType> userAlias) {
        this.userAlias = userAlias;
    }

    public List<MCPTTGroupCallType> getMCPTTGroupCall() {
        return mCPTTGroupCall;
    }

    public void setMCPTTGroupCall(List<MCPTTGroupCallType> mCPTTGroupCall) {
        this.mCPTTGroupCall = mCPTTGroupCall;
    }

    public List<String> getParticipantType() {
        return participantType;
    }

    public void setParticipantType(List<String> participantType) {
        this.participantType = participantType;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }

}
