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


@Root(strict=false, name = "TriggeringCriteriaType")
public class TriggeringCriteriaType {

    @Element(required=false,name = "CellChange")
    protected TCellChange cellChange;
    @Element(required=false,name = "TrackingAreaChange")
    protected TTrackingAreaChangeType trackingAreaChange;
    @Element(required=false,name = "PlmnChange")
    protected TPlmnChangeType plmnChange;
    @Element(required=false,name = "MbmsSaChange")
    protected TMbmsSaChangeType mbmsSaChange;
    @Element(required=false,name = "MbsfnAreaChange")
    protected TMbsfnAreaChangeType mbsfnAreaChange;
    @Element(required=false,name = "PeriodicReport")
    protected TIntegerAttributeType periodicReport;
    @Element(required=false,name = "TravelledDistance")
    protected TIntegerAttributeType travelledDistance;
    @Element(required=false,name = "McpttSignallingEvent")
    protected TSignallingEventType mcpttSignallingEvent;
    @Element(required=false,name = "GeographicalAreaChange")
    protected TGeographicalAreaChange geographicalAreaChange;




    public TCellChange getCellChange() {
        return cellChange;
    }


    public void setCellChange(TCellChange value) {
        this.cellChange = value;
    }


    public TTrackingAreaChangeType getTrackingAreaChange() {
        return trackingAreaChange;
    }

    public void setTrackingAreaChange(TTrackingAreaChangeType value) {
        this.trackingAreaChange = value;
    }


    public TPlmnChangeType getPlmnChange() {
        return plmnChange;
    }


    public void setPlmnChange(TPlmnChangeType value) {
        this.plmnChange = value;
    }


    public TMbmsSaChangeType getMbmsSaChange() {
        return mbmsSaChange;
    }


    public void setMbmsSaChange(TMbmsSaChangeType value) {
        this.mbmsSaChange = value;
    }


    public TMbsfnAreaChangeType getMbsfnAreaChange() {
        return mbsfnAreaChange;
    }


    public void setMbsfnAreaChange(TMbsfnAreaChangeType value) {
        this.mbsfnAreaChange = value;
    }


    public TIntegerAttributeType getPeriodicReport() {
        return periodicReport;
    }


    public void setPeriodicReport(TIntegerAttributeType value) {
        this.periodicReport = value;
    }


    public TIntegerAttributeType getTravelledDistance() {
        return travelledDistance;
    }


    public void setTravelledDistance(TIntegerAttributeType value) {
        this.travelledDistance = value;
    }


    public TSignallingEventType getMcpttSignallingEvent() {
        return mcpttSignallingEvent;
    }


    public void setMcpttSignallingEvent(TSignallingEventType value) {
        this.mcpttSignallingEvent = value;
    }


    public TGeographicalAreaChange getGeographicalAreaChange() {
        return geographicalAreaChange;
    }

    public void setGeographicalAreaChange(TGeographicalAreaChange value) {
        this.geographicalAreaChange = value;
    }

}
