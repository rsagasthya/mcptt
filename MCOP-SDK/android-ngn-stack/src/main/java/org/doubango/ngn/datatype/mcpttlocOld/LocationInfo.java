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
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "location-info")
@Namespace(reference = "urn:3gpp:ns:mcpttLocationInfo:1.0") // Add your reference here!
public class LocationInfo {

    @Element(required=false,name="Configuration")
    protected TConfigurationType configuration;
    @Element(required=false,name = "Request")
    protected TRequestType request;
    @Element(required=false,name = "Report")
    protected TReportType report;



    public TConfigurationType getConfiguration() {
        return configuration;
    }


    public void setConfiguration(TConfigurationType value) {
        this.configuration = value;
    }


    public TRequestType getRequest() {
        return request;
    }


    public void setRequest(TRequestType value) {
        this.request = value;
    }


    public TReportType getReport() {
        return report;
    }


    public void setReport(TReportType value) {
        this.report = value;
    }

}
