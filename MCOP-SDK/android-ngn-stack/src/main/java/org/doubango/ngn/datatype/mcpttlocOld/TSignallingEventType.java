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




package org.doubango.ngn.datatype.mcpttlocOld;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "tSignallingEventType")
public class TSignallingEventType {

    @Element(required=false,name = "InitialLogOn")
    protected TEmptyTypeAttribute initialLogOn;
    @Element(required=false,name = "GroupCallNonEmergency")
    protected TEmptyTypeAttribute groupCallNonEmergency;
    @Element(required=false,name = "PrivateCallNonEmergency")
    protected TEmptyTypeAttribute privateCallNonEmergency;
    @Element(required=false,name = "LocationConfigurationReceived")
    protected TEmptyTypeAttribute locationConfigurationReceived;
    


    public TEmptyTypeAttribute getInitialLogOn() {
        return initialLogOn;
    }

    public void setInitialLogOn(TEmptyTypeAttribute value) {
        this.initialLogOn = value;
    }


    public TEmptyTypeAttribute getGroupCallNonEmergency() {
        return groupCallNonEmergency;
    }


    public void setGroupCallNonEmergency(TEmptyTypeAttribute value) {
        this.groupCallNonEmergency = value;
    }


    public TEmptyTypeAttribute getPrivateCallNonEmergency() {
        return privateCallNonEmergency;
    }


    public void setPrivateCallNonEmergency(TEmptyTypeAttribute value) {
        this.privateCallNonEmergency = value;
    }


    public TEmptyTypeAttribute getLocationConfigurationReceived() {
        return locationConfigurationReceived;
    }


    public void setLocationConfigurationReceived(TEmptyTypeAttribute value) {
        this.locationConfigurationReceived = value;
    }

}
