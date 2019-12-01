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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "mbms-listening-status")
public class MbmsListeningStatusType {

    @Element(required = false, name = "mbms-listening-status")
    protected String mbmsListeningStatus;
    @Element(required = false, name = "session-id")
    protected String sessionId;
    @Element(required = false, name = "general-purpose")
    protected Boolean generalPurpose;
    @ElementList(entry = "TMGI", required=false, inline=true)
    protected List<String> tmgi;



    public String getMbmsListeningStatus() {
        return mbmsListeningStatus;
    }


    public void setMbmsListeningStatus(String value) {
        this.mbmsListeningStatus = value;
    }


    public String getSessionId() {
        return sessionId;
    }


    public void setSessionId(String value) {
        this.sessionId = value;
    }


    public Boolean isGeneralPurpose() {
        return generalPurpose;
    }


    public void setGeneralPurpose(Boolean value) {
        this.generalPurpose = value;
    }


    public List<String> getTmgi() {
        return tmgi;
    }

    public void setTmgi(List<String> tmgi) {
        this.tmgi = tmgi;
    }
}
