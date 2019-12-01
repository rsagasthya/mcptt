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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "mcptt-user-profile")
@NamespaceList({
        @Namespace(reference = "urn:3gpp:mcptt:user-profile:1.0"),
        @Namespace(prefix = "xml", reference = "http://www.w3.org/XML/1998/namespace")
})
public class McpttUserProfile {


    @ElementList(required=false,inline=true,entry = "ProfileName")
    protected List<NameType> profileName;
    @ElementList(required=false,inline=true,entry = "Name")
    protected List<NameType> name;
    @ElementList(required=false,inline=true,entry = "Pre-selected-indication")
    protected List<EmptyType> preSelectedIndication;
    @ElementList(required=false,inline=true,entry = "Common")
    protected List<CommonType> common;
    @ElementList(required=false,inline=true,entry = "OnNetwork")
    protected List<OnNetworkType> onNetwork;
    @ElementList(required=false,inline=true,entry = "OffNetwork")
    protected List<OffNetworkType> offNetwork;
    @Element(required=false,name = "Status")
    protected boolean status;

    @Element(required=false,name = "ruleset")
    protected Ruleset ruleset;

    @Attribute(required = false , name = "XUI-URI")
    protected String xuiuri;
    @Attribute(required = false , name = "user-profile-index")
    protected Short userProfileIndex;



    public List<NameType> getProfileName() {
        return profileName;
    }

    public void setProfileName(List<NameType> profileName) {
        this.profileName = profileName;
    }

    public List<NameType> getName() {
        return name;
    }

    public void setName(List<NameType> name) {
        this.name = name;
    }

    public List<EmptyType> getPreSelectedIndication() {
        return preSelectedIndication;
    }

    public void setPreSelectedIndication(List<EmptyType> preSelectedIndication) {
        this.preSelectedIndication = preSelectedIndication;
    }

    public List<CommonType> getCommon() {
        return common;
    }

    public void setCommon(List<CommonType> common) {
        this.common = common;
    }

    public List<OnNetworkType> getOnNetwork() {
        return onNetwork;
    }

    public void setOnNetwork(List<OnNetworkType> onNetwork) {
        this.onNetwork = onNetwork;
    }

    public List<OffNetworkType> getOffNetwork() {
        return offNetwork;
    }

    public void setOffNetwork(List<OffNetworkType> offNetwork) {
        this.offNetwork = offNetwork;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getXUIURI() {
        return xuiuri;
    }


    public void setXUIURI(String value) {
        this.xuiuri = value;
    }


    public Short getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(Short value) {
        this.userProfileIndex = value;
    }


    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }
}
