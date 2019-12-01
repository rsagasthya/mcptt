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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.List;



@Root(strict=false, name = "MCPTTGroupCallType")
public class MCPTTGroupCallType {


    @ElementList(required=false,inline=true,entry = "MaxSimultaneousCallsN6")
    protected List<BigInteger> maxSimultaneousCallsN6;
    @ElementList(required=false,inline=true,entry = "EmergencyCall")
    protected List<EmergencyCallType> emergencyCall;
    @ElementList(required=false,inline=true,entry = "ImminentPerilCall")
    protected List<ImminentPerilCallType> imminentPerilCall;
    @ElementList(required=false,inline=true,entry = "EmergencyAlert")
    protected List<EmergencyAlertType> emergencyAlert;
    @ElementList(required=false,inline=true,entry = "Priority")
    protected List<Integer> priority;

    public List<BigInteger> getMaxSimultaneousCallsN6() {
        return maxSimultaneousCallsN6;
    }

    public void setMaxSimultaneousCallsN6(List<BigInteger> maxSimultaneousCallsN6) {
        this.maxSimultaneousCallsN6 = maxSimultaneousCallsN6;
    }

    public List<EmergencyCallType> getEmergencyCall() {
        return emergencyCall;
    }

    public void setEmergencyCall(List<EmergencyCallType> emergencyCall) {
        this.emergencyCall = emergencyCall;
    }

    public List<ImminentPerilCallType> getImminentPerilCall() {
        return imminentPerilCall;
    }

    public void setImminentPerilCall(List<ImminentPerilCallType> imminentPerilCall) {
        this.imminentPerilCall = imminentPerilCall;
    }

    public List<EmergencyAlertType> getEmergencyAlert() {
        return emergencyAlert;
    }

    public void setEmergencyAlert(List<EmergencyAlertType> emergencyAlert) {
        this.emergencyAlert = emergencyAlert;
    }

    public List<Integer> getPriority() {
        return priority;
    }

    public void setPriority(List<Integer> priority) {
        this.priority = priority;
    }
}
