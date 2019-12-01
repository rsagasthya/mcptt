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

package org.doubango.ngn.datatype.mbms;

public class MbmsData {
    private McpttMbmsUsageInfoType mcpttMbmsUsageInfoType;
    private int portMBMSManager;
    private String ipMBMSManager;
    private String pAssertedIdentity;
    private String ipMulticastMedia;
    private String groupID;
    private int portMulticastMedia;
    private int portControlMulticastMedia;
    private String localInterface;
    private int localInterfaceIndex;

    public MbmsData(McpttMbmsUsageInfoType mcpttMbmsUsageInfoType, int portMBMSManager, String ipMBMSManager, String pAssertedIdentity) {
        this.mcpttMbmsUsageInfoType = mcpttMbmsUsageInfoType;
        this.portMBMSManager = portMBMSManager;
        this.ipMBMSManager = ipMBMSManager;
        this.pAssertedIdentity = pAssertedIdentity;
        this.ipMulticastMedia = null;
        this.portMulticastMedia = -1;
        this.portControlMulticastMedia = -1;
        this.localInterface = null;
        this.localInterfaceIndex = -1;
        this.groupID = null;

    }



    public MbmsData(McpttMbmsUsageInfoType mcpttMbmsUsageInfoType, int portMBMSManager, String ipMBMSManager, String pAssertedIdentity, String ipMulticastMedia, int portMulticastMedia, int portControlMulticastMedia) {
        this.mcpttMbmsUsageInfoType = mcpttMbmsUsageInfoType;
        this.portMBMSManager = portMBMSManager;
        this.ipMBMSManager = ipMBMSManager;
        this.pAssertedIdentity = pAssertedIdentity;
        this.ipMulticastMedia = ipMulticastMedia;
        this.portMulticastMedia = portMulticastMedia;
        this.portControlMulticastMedia = portControlMulticastMedia;
        this.localInterface = null;
        this.localInterfaceIndex = -1;
    }

    public McpttMbmsUsageInfoType getMcpttMbmsUsageInfoType() {
        return mcpttMbmsUsageInfoType;
    }

    public void setMcpttMbmsUsageInfoType(McpttMbmsUsageInfoType mcpttMbmsUsageInfoType) {
        this.mcpttMbmsUsageInfoType = mcpttMbmsUsageInfoType;
    }

    public int getPortMBMSManager() {
        return portMBMSManager;
    }

    public void setPortMBMSManager(int portMBMSManager) {
        this.portMBMSManager = portMBMSManager;
    }

    public String getIpMBMSManager() {
        return ipMBMSManager;
    }

    public void setIpMBMSManager(String ipMBMSManager) {
        this.ipMBMSManager = ipMBMSManager;
    }

    public String getpAssertedIdentity() {
        return pAssertedIdentity;
    }

    public void setpAssertedIdentity(String pAssertedIdentity) {
        this.pAssertedIdentity = pAssertedIdentity;
    }

    public String getIpMulticastMedia() {
        return ipMulticastMedia;
    }

    public void setIpMulticastMedia(String ipMulticastMedia) {
        this.ipMulticastMedia = ipMulticastMedia;
    }

    public int getPortMulticastMedia() {
        return portMulticastMedia;
    }

    public void setPortMulticastMedia(int portMulticastMedia) {
        this.portMulticastMedia = portMulticastMedia;
    }

    public int getPortControlMulticastMedia() {
        return portControlMulticastMedia;
    }

    public void setPortControlMulticastMedia(int portControlMulticastMedia) {
        this.portControlMulticastMedia = portControlMulticastMedia;
    }

    public String getLocalInterface() {
        return localInterface;
    }

    public void setLocalInterface(String localInterface) {
        this.localInterface = localInterface;
    }

    public int getLocalInterfaceIndex() {
        return localInterfaceIndex;
    }

    public void setLocalInterfaceIndex(int localInterfaceIndex) {
        this.localInterfaceIndex = localInterfaceIndex;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }
}
