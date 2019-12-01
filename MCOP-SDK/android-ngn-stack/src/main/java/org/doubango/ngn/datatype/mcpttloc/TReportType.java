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


package org.doubango.ngn.datatype.mcpttloc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;




@Root(strict=false, name = "tReportType")
public class TReportType {

    @ElementList(inline=true,entry="TriggerId",required=false)
    protected List<String> triggerId;
    @Element(required=false,name = "CurrentLocation")
    protected TCurrentLocationType currentLocation;
    
    @Attribute(name = "ReportID",required=false)
    protected String reportID;
    @Attribute(name = "ReportType")
    protected String reportType;
    


    public List<String> getTriggerId() {
        if (triggerId == null) {
            triggerId = new ArrayList<String>();
        }
        return this.triggerId;
    }

    public void setTriggerId(List<String> triggerId) {
        this.triggerId = triggerId;
    }

    public TCurrentLocationType getCurrentLocation() {
        return currentLocation;
    }


    public void setCurrentLocation(TCurrentLocationType value) {
        this.currentLocation = value;
    }


    public String getReportID() {
        return reportID;
    }


    public void setReportID(String value) {
        this.reportID = value;
    }


    public String getReportType() {
        return reportType;
    }


    public void setReportType(String value) {
        this.reportType = value;
    }

}
