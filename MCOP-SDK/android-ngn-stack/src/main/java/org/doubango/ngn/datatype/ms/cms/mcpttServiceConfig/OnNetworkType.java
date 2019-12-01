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


@Root(strict=false, name =  "on-network")
public class OnNetworkType {

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
    @Element(required = false , name = "floor-control-queue")
    protected FloorControlQueueType floorControlQueue;
    @Element(required = false , name = "fc-timers-counters")
    protected FcTimersCountersType fcTimersCounters;
    @Element(required = false , name = "signalling-protection")
    protected SignallingProtectionType signallingProtection;
    @Element(required = false , name = "protection-between-mcptt-servers")
    protected ServerProtectionType protectionBetweenMcpttServers;
    @Element(required = false , name = "emergency-resource-priority")
    protected ResourcePriorityType emergencyResourcePriority;
    @Element(required = false , name = "imminent-peril-resource-priority")
    protected ResourcePriorityType imminentPerilResourcePriority;
    @Element(required = false , name = "normal-resource-priority")
    protected ResourcePriorityType normalResourcePriority;


   


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


    public FloorControlQueueType getFloorControlQueue() {
        return floorControlQueue;
    }


    public void setFloorControlQueue(FloorControlQueueType value) {
        this.floorControlQueue = value;
    }


    public FcTimersCountersType getFcTimersCounters() {
        return fcTimersCounters;
    }


    public void setFcTimersCounters(FcTimersCountersType value) {
        this.fcTimersCounters = value;
    }


    public SignallingProtectionType getSignallingProtection() {
        return signallingProtection;
    }


    public void setSignallingProtection(SignallingProtectionType value) {
        this.signallingProtection = value;
    }


    public ServerProtectionType getProtectionBetweenMcpttServers() {
        return protectionBetweenMcpttServers;
    }


    public void setProtectionBetweenMcpttServers(ServerProtectionType value) {
        this.protectionBetweenMcpttServers = value;
    }


    public ResourcePriorityType getEmergencyResourcePriority() {
        return emergencyResourcePriority;
    }


    public void setEmergencyResourcePriority(ResourcePriorityType value) {
        this.emergencyResourcePriority = value;
    }


    public ResourcePriorityType getImminentPerilResourcePriority() {
        return imminentPerilResourcePriority;
    }


    public void setImminentPerilResourcePriority(ResourcePriorityType value) {
        this.imminentPerilResourcePriority = value;
    }


    public ResourcePriorityType getNormalResourcePriority() {
        return normalResourcePriority;
    }


    public void setNormalResourcePriority(ResourcePriorityType value) {
        this.normalResourcePriority = value;
    }

}
