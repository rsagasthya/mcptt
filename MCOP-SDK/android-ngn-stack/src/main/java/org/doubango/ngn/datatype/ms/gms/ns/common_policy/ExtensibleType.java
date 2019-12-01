/*
 *
 *   Copyright (C) 2018, University of the Basque Country (UPV/EHU)
 *
 *  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
 *
 *  This file is part of MCOP MCPTT Client
 *
 *  This is free software: you can redistribute it and/or modify it under the terms of
 *  the GNU General Public License as published by the Free Software Foundation, either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */



package org.doubango.ngn.datatype.ms.gms.ns.common_policy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;




@Root(strict=false, name = "extensibleType")
public class ExtensibleType {

    //Other
    @Element(required = false, name = "join-handling")
    @Namespace(reference = "urn:oma:xml:poc:list-service")
    protected Boolean joinhandling;

    @Element(required = false, name = "allow-initiate-conference")
    @Namespace(reference = "urn:oma:xml:poc:list-service")
    protected Boolean allowinitiateconference;

    //MCS
    @Element(required = false, name = "on-network-allow-getting-member-list")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean onnetworkallowgettingmemberlist;

    //MCPTT
    @Element(required = false, name = "allow-MCPTT-emergency-call")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean allowMCPTTemergencycall;

    @Element(required = false, name = "allow-imminent-peril-call")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean allowimminentperilcall;

    @Element(required = false, name = "allow-MCPTT-emergency-alert")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean allowMCPTTemergencyalert;

    @Element(required = false, name = "on-network-allow-getting-affiliation-list")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean onnetworkallowgettingaffiliationlist;

    @Element(required = false, name = "on-network-allow-conference-state")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean onnetworkallowconferencestate;

    //MCVideo

    @Element(required = false, name = "mcvideo-allow-emergency-call")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoallowemergencycall;

    @Element(required = false, name = "mcvideo-allow-emergency-alert")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoallowemergencyalert;

    @Element(required = false, name = "mcvideo-on-network-allow-conference-state")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoonnetworkallowconferencestate;

    @Element(required = false, name = "mcvideo-on-network-allow-getting-affiliation-list")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoonnetworkallowgettingaffiliationlist;

    @Element(required = false, name = "mcvideo-allow-imminent-peril-call")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoallowimminentperilcall;
    
    //MCData
    @Element(required = false, name = "mcdata-allow-transmit-data-in-this-group")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowtransmitdatainthisgroup;

    @Element(required = false, name = "mcdata-on-network-allow-getting-affiliation-list")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataonnetworkallowgettingaffiliationlist;


    public Boolean getJoinhandling() {
        return joinhandling;
    }

    public void setJoinhandling(Boolean joinhandling) {
        this.joinhandling = joinhandling;
    }

    public Boolean getAllowinitiateconference() {
        return allowinitiateconference;
    }

    public void setAllowinitiateconference(Boolean allowinitiateconference) {
        this.allowinitiateconference = allowinitiateconference;
    }

    public Boolean getOnnetworkallowgettingmemberlist() {
        return onnetworkallowgettingmemberlist;
    }

    public void setOnnetworkallowgettingmemberlist(Boolean onnetworkallowgettingmemberlist) {
        this.onnetworkallowgettingmemberlist = onnetworkallowgettingmemberlist;
    }

    public Boolean getAllowMCPTTemergencycall() {
        return allowMCPTTemergencycall;
    }

    public void setAllowMCPTTemergencycall(Boolean allowMCPTTemergencycall) {
        this.allowMCPTTemergencycall = allowMCPTTemergencycall;
    }

    public Boolean getAllowimminentperilcall() {
        return allowimminentperilcall;
    }

    public void setAllowimminentperilcall(Boolean allowimminentperilcall) {
        this.allowimminentperilcall = allowimminentperilcall;
    }

    public Boolean getAllowMCPTTemergencyalert() {
        return allowMCPTTemergencyalert;
    }

    public void setAllowMCPTTemergencyalert(Boolean allowMCPTTemergencyalert) {
        this.allowMCPTTemergencyalert = allowMCPTTemergencyalert;
    }

    public Boolean getOnnetworkallowgettingaffiliationlist() {
        return onnetworkallowgettingaffiliationlist;
    }

    public void setOnnetworkallowgettingaffiliationlist(Boolean onnetworkallowgettingaffiliationlist) {
        this.onnetworkallowgettingaffiliationlist = onnetworkallowgettingaffiliationlist;
    }

    public Boolean getOnnetworkallowconferencestate() {
        return onnetworkallowconferencestate;
    }

    public void setOnnetworkallowconferencestate(Boolean onnetworkallowconferencestate) {
        this.onnetworkallowconferencestate = onnetworkallowconferencestate;
    }

    public Boolean getMcvideoallowemergencycall() {
        return mcvideoallowemergencycall;
    }

    public void setMcvideoallowemergencycall(Boolean mcvideoallowemergencycall) {
        this.mcvideoallowemergencycall = mcvideoallowemergencycall;
    }

    public Boolean getMcvideoallowemergencyalert() {
        return mcvideoallowemergencyalert;
    }

    public void setMcvideoallowemergencyalert(Boolean mcvideoallowemergencyalert) {
        this.mcvideoallowemergencyalert = mcvideoallowemergencyalert;
    }

    public Boolean getMcvideoonnetworkallowconferencestate() {
        return mcvideoonnetworkallowconferencestate;
    }

    public void setMcvideoonnetworkallowconferencestate(Boolean mcvideoonnetworkallowconferencestate) {
        this.mcvideoonnetworkallowconferencestate = mcvideoonnetworkallowconferencestate;
    }

    public Boolean getMcvideoonnetworkallowgettingaffiliationlist() {
        return mcvideoonnetworkallowgettingaffiliationlist;
    }

    public void setMcvideoonnetworkallowgettingaffiliationlist(Boolean mcvideoonnetworkallowgettingaffiliationlist) {
        this.mcvideoonnetworkallowgettingaffiliationlist = mcvideoonnetworkallowgettingaffiliationlist;
    }

    public Boolean getMcvideoallowimminentperilcall() {
        return mcvideoallowimminentperilcall;
    }

    public void setMcvideoallowimminentperilcall(Boolean mcvideoallowimminentperilcall) {
        this.mcvideoallowimminentperilcall = mcvideoallowimminentperilcall;
    }

    public Boolean getMcdataallowtransmitdatainthisgroup() {
        return mcdataallowtransmitdatainthisgroup;
    }

    public void setMcdataallowtransmitdatainthisgroup(Boolean mcdataallowtransmitdatainthisgroup) {
        this.mcdataallowtransmitdatainthisgroup = mcdataallowtransmitdatainthisgroup;
    }

    public Boolean getMcdataonnetworkallowgettingaffiliationlist() {
        return mcdataonnetworkallowgettingaffiliationlist;
    }

    public void setMcdataonnetworkallowgettingaffiliationlist(Boolean mcdataonnetworkallowgettingaffiliationlist) {
        this.mcdataonnetworkallowgettingaffiliationlist = mcdataonnetworkallowgettingaffiliationlist;
    }
}
