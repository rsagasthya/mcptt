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



package org.doubango.ngn.datatype.mcpttinfo;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "mcptt-Params")
@Namespace(reference = "urn:3gpp:ns:mcpttInfo:1.0") // Add your reference here!
public class McpttParamsType {

    @Element(required=false, name = "mcptt-access-token")
    protected ContentType mcpttAccessToken;
    @Element(required=false, name = "session-type")
    protected String sessionType;
    @Element(required=false, name = "mcptt-request-uri")
    protected ContentType mcpttRequestUri;
    @Element(required=false, name = "mcptt-calling-user-id")
    protected ContentType mcpttCallingUserId;
    @Element(required=false, name = "mcptt-called-party-id")
    protected ContentType mcpttCalledPartyId;
    @Element(required=false, name = "mcptt-calling-group-id")
    protected ContentType mcpttCallingGroupId;
    protected ContentType required;
    @Element(required=false, name = "emergency-ind")
    protected ContentType emergencyInd;
    @Element(required=false, name = "alert-ind")
    protected ContentType alertInd;
    @Element(required=false, name = "imminentperil-ind")
    protected Boolean imminentperilInd;
    @Element(required=false, name = "broadcast-ind")
    protected Boolean broadcastInd;
    @Element(required=false, name = "mc-org")
    protected String mcOrg;
    @Element(required=false, name = "floor-state")
    protected String floorState;
    @Element(required=false, name = "associated-group-id")
    protected String associatedGroupId;
    @Element(required=false, name = "originated-by")
    protected ContentType originatedBy;
    @Element(required=false, name = "MKFC-GKTPs")
    protected SingleTypeGKTPsType mkfcgktPs;
    @Element(required=false, name = "mcptt-client-id")
    protected ContentType mcpttClientId;



    public ContentType getMcpttAccessToken() {
        return mcpttAccessToken;
    }


    public void setMcpttAccessToken(ContentType value) {
        this.mcpttAccessToken = value;
    }


    public String getSessionType() {
        return sessionType;
    }


    public void setSessionType(String value) {
        this.sessionType = value;
    }


    public ContentType getMcpttRequestUri() {
        return mcpttRequestUri;
    }


    public void setMcpttRequestUri(ContentType value) {
        this.mcpttRequestUri = value;
    }


    public ContentType getMcpttCallingUserId() {
        return mcpttCallingUserId;
    }


    public void setMcpttCallingUserId(ContentType value) {
        this.mcpttCallingUserId = value;
    }


    public ContentType getMcpttCalledPartyId() {
        return mcpttCalledPartyId;
    }


    public void setMcpttCalledPartyId(ContentType value) {
        this.mcpttCalledPartyId = value;
    }


    public ContentType getMcpttCallingGroupId() {
        return mcpttCallingGroupId;
    }


    public void setMcpttCallingGroupId(ContentType value) {
        this.mcpttCallingGroupId = value;
    }


    public ContentType getRequired() {
        return required;
    }


    public void setRequired(ContentType value) {
        this.required = value;
    }


    public ContentType getEmergencyInd() {
        return emergencyInd;
    }


    public void setEmergencyInd(ContentType value) {
        this.emergencyInd = value;
    }


    public ContentType getAlertInd() {
        return alertInd;
    }


    public void setAlertInd(ContentType value) {
        this.alertInd = value;
    }


    public Boolean isImminentperilInd() {
        return imminentperilInd;
    }


    public void setImminentperilInd(Boolean value) {
        this.imminentperilInd = value;
    }


    public Boolean isBroadcastInd() {
        return broadcastInd;
    }


    public void setBroadcastInd(Boolean value) {
        this.broadcastInd = value;
    }


    public String getMcOrg() {
        return mcOrg;
    }


    public void setMcOrg(String value) {
        this.mcOrg = value;
    }


    public String getFloorState() {
        return floorState;
    }


    public void setFloorState(String value) {
        this.floorState = value;
    }


    public String getAssociatedGroupId() {
        return associatedGroupId;
    }


    public void setAssociatedGroupId(String value) {
        this.associatedGroupId = value;
    }


    public ContentType getOriginatedBy() {
        return originatedBy;
    }


    public void setOriginatedBy(ContentType value) {
        this.originatedBy = value;
    }


    public SingleTypeGKTPsType getMKFCGKTPs() {
        return mkfcgktPs;
    }


    public void setMKFCGKTPs(SingleTypeGKTPsType value) {
        this.mkfcgktPs = value;
    }


    public ContentType getMcpttClientId() {
        return mcpttClientId;
    }


    public void setMcpttClientId(ContentType value) {
        this.mcpttClientId = value;
    }

}
