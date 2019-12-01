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


package org.doubango.ngn.datatype.pocsettings;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(strict=false, name = "entityType")
public class EntityType {

    @Element(required=false, name = "isb-settings")
    protected IsbSettingType isbSettings;
    @Element(required=false,name = "am-settings")
    protected AmSettingType amSettings;
    @Element(required=false,name = "ipab-settings")
    protected IpabSettingType ipabSettings;
    @Element(required=false,name = "sss-settings")
    protected SssSettingType sssSettings;
    @Attribute(name = "id")
    protected String id;

    @Element(required=false,name = "selected-user-profile-index")
    @Namespace(prefix = "mcs10Set", reference = "urn:3gpp:mcsSettings:1.0")
    protected SelectedUserProfileIndex selectedUserProfileIndex;


    public SelectedUserProfileIndex getSelectedUserProfileIndex() {
        return selectedUserProfileIndex;
    }

    public void setSelectedUserProfileIndex(SelectedUserProfileIndex selectedUserProfileIndex) {
        this.selectedUserProfileIndex = selectedUserProfileIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public IsbSettingType getIsbSettings() {
        return isbSettings;
    }


    public void setIsbSettings(IsbSettingType value) {
        this.isbSettings = value;
    }


    public AmSettingType getAmSettings() {
        return amSettings;
    }


    public void setAmSettings(AmSettingType value) {
        this.amSettings = value;
    }


    public IpabSettingType getIpabSettings() {
        return ipabSettings;
    }


    public void setIpabSettings(IpabSettingType value) {
        this.ipabSettings = value;
    }


    public SssSettingType getSssSettings() {
        return sssSettings;
    }


    public void setSssSettings(SssSettingType value) {
        this.sssSettings = value;
    }

}
