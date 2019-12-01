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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(strict=false, name = "tRequestedLocationType")
public class TRequestedLocationType {

    @Element(required=false,name = "ServingEcgi")
    protected TEmptyType servingEcgi;
    @ElementList(inline=true,entry = "NeighbouringEcgi",required=false)
    protected List<TEmptyType> neighbouringEcgi;
    @Element(required=false,name = "MbmsSaId")
    protected TEmptyType mbmsSaId;
    @Element(required=false,name = "MbsfnArea")
    protected TEmptyType mbsfnArea;
    @Element(required=false,name = "GeographicalCordinate")
    protected TEmptyType geographicalCordinate;
    @Element(required = false,name = "minimumIntervalLength")
    protected long minimumIntervalLength;




    public TEmptyType getServingEcgi() {
        return servingEcgi;
    }


    public void setServingEcgi(TEmptyType value) {
        this.servingEcgi = value;
    }


    public List<TEmptyType> getNeighbouringEcgi() {
        if (neighbouringEcgi == null) {
            neighbouringEcgi = new ArrayList<TEmptyType>();
        }
        return this.neighbouringEcgi;
    }


    public TEmptyType getMbmsSaId() {
        return mbmsSaId;
    }


    public void setMbmsSaId(TEmptyType value) {
        this.mbmsSaId = value;
    }

    public TEmptyType getMbsfnArea() {
        return mbsfnArea;
    }


    public void setMbsfnArea(TEmptyType value) {
        this.mbsfnArea = value;
    }


    public TEmptyType getGeographicalCordinate() {
        return geographicalCordinate;
    }


    public void setGeographicalCordinate(TEmptyType value) {
        this.geographicalCordinate = value;
    }


    public long getMinimumIntervalLength() {
        return minimumIntervalLength;
    }


    public void setMinimumIntervalLength(long value) {
        this.minimumIntervalLength = value;
    }

}
