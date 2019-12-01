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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.math.BigInteger;



@Root(strict=false,  name = "mcptt-mbms-usage-info")
@Namespace(reference = "urn:3gpp:ns:mcpttMbmsUsage:1.0") // Add your reference here!
public class McpttMbmsUsageInfoType {

    @Element(required = false, name = "mbms-listening-status")
    protected MbmsListeningStatusType mbmsListeningStatus;
    @Element(required = false, name = "announcement")
    protected AnnouncementTypeParams announcement;
    @Element(required = false, name = "GPMS")
    protected BigInteger gpms;
    @Element(required = false, name = "version")
    protected BigInteger version;


    public MbmsListeningStatusType getMbmsListeningStatus() {
        return mbmsListeningStatus;
    }


    public void setMbmsListeningStatus(MbmsListeningStatusType value) {
        this.mbmsListeningStatus = value;
    }


    public AnnouncementTypeParams getAnnouncement() {
        return announcement;
    }


    public void setAnnouncement(AnnouncementTypeParams value) {
        this.announcement = value;
    }


    public BigInteger getGPMS() {
        return gpms;
    }


    public void setGPMS(BigInteger value) {
        this.gpms = value;
    }


    public BigInteger getVersion() {
        return version;
    }


    public void setVersion(BigInteger value) {
        this.version = value;
    }


}
