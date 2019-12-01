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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name =  "off-networkType")
public class OffNetworkType {

    @Element(required = false , name = "emergency-call")
    protected EmergencyCallType emergencyCall;
    @Element(required = false , name = "private-call")
    protected PrivateCallType privateCall;
    @Element(required = false , name = "num-levels-priority-hierarchy")
    protected Integer numLevelsPriorityHierarchy;
    @Element(required = false , name = "transmit-time")
    protected TransmitTimeType transmitTime;
    @Element(required = false , name = "hang-time-warning")
    protected String hangTimeWarning;
    @Element(required = false , name = "default-prose-per-packet-priority")
    protected DefaultProsePerPacketPriorityType defaultProsePerPacketPriority;
    @Element(required = false , name = "allow-log-metadata")
    protected Boolean allowLogMetadata;





    public EmergencyCallType getEmergencyCall() {
        return emergencyCall;
    }


    public void setEmergencyCall(EmergencyCallType value) {
        this.emergencyCall = value;
    }


    public PrivateCallType getPrivateCall() {
        return privateCall;
    }


    public void setPrivateCall(PrivateCallType value) {
        this.privateCall = value;
    }


    public Integer getNumLevelsPriorityHierarchy() {
        return numLevelsPriorityHierarchy;
    }


    public void setNumLevelsPriorityHierarchy(Integer value) {
        this.numLevelsPriorityHierarchy = value;
    }


    public TransmitTimeType getTransmitTime() {
        return transmitTime;
    }


    public void setTransmitTime(TransmitTimeType value) {
        this.transmitTime = value;
    }


    public String getHangTimeWarning() {
        return hangTimeWarning;
    }


    public void setHangTimeWarning(String value) {
        this.hangTimeWarning = value;
    }


    public DefaultProsePerPacketPriorityType getDefaultProsePerPacketPriority() {
        return defaultProsePerPacketPriority;
    }


    public void setDefaultProsePerPacketPriority(DefaultProsePerPacketPriorityType value) {
        this.defaultProsePerPacketPriority = value;
    }


    public Boolean isAllowLogMetadata() {
        return allowLogMetadata;
    }


    public void setAllowLogMetadata(Boolean value) {
        this.allowLogMetadata = value;
    }

}
