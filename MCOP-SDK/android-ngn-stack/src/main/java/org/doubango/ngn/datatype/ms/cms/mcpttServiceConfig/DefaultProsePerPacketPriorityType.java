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


@Root(strict=false, name =  "default-prose-per-packet-priorityType")
public class DefaultProsePerPacketPriorityType {

    @Element(required = false , name = "mcptt-private-call-signalling")
    protected Integer mcpttPrivateCallSignalling;
    @Element(required = false , name = "mcptt-private-call-media")
    protected Integer mcpttPrivateCallMedia;
    @Element(required = false , name = "mcptt-emergency-private-call-signalling")
    protected Integer mcpttEmergencyPrivateCallSignalling;
    @Element(required = false , name = "mcptt-emergency-private-call-media")
    protected Integer mcpttEmergencyPrivateCallMedia;





    public Integer getMcpttPrivateCallSignalling() {
        return mcpttPrivateCallSignalling;
    }


    public void setMcpttPrivateCallSignalling(Integer value) {
        this.mcpttPrivateCallSignalling = value;
    }


    public Integer getMcpttPrivateCallMedia() {
        return mcpttPrivateCallMedia;
    }


    public void setMcpttPrivateCallMedia(Integer value) {
        this.mcpttPrivateCallMedia = value;
    }


    public Integer getMcpttEmergencyPrivateCallSignalling() {
        return mcpttEmergencyPrivateCallSignalling;
    }


    public void setMcpttEmergencyPrivateCallSignalling(Integer value) {
        this.mcpttEmergencyPrivateCallSignalling = value;
    }


    public Integer getMcpttEmergencyPrivateCallMedia() {
        return mcpttEmergencyPrivateCallMedia;
    }


    public void setMcpttEmergencyPrivateCallMedia(Integer value) {
        this.mcpttEmergencyPrivateCallMedia = value;
    }
}
