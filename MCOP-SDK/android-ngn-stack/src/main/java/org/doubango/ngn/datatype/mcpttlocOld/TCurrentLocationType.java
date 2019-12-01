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


@Root(strict=false, name = "tCurrentLocationType")
public class TCurrentLocationType {

    @Element(required=false,name = "CurrentServingEcgi")
    protected String currentServingEcgi;
    @ElementList(inline=true,entry="NeighbouringEcgi",required=false)
    protected List<String> neighbouringEcgi;
    @Element(required=false,name = "MbmsSaId")
    protected int mbmsSaId;
    @Element(required=false,name = "MbsfnArea")
    protected int mbsfnArea;
    @Element(required=false,name = "CurrentCoordinate")
    protected TPointCoordinate currentCoordinate;




    public String getCurrentServingEcgi() {
        return currentServingEcgi;
    }


    public void setCurrentServingEcgi(String value) {
        this.currentServingEcgi = value;
    }


    public List<String> getNeighbouringEcgi() {
        if (neighbouringEcgi == null) {
            neighbouringEcgi = new ArrayList<String>();
        }
        return this.neighbouringEcgi;
    }

    public void setNeighbouringEcgi(List<String> neighbouringEcgi) {
        this.neighbouringEcgi = neighbouringEcgi;
    }


    public int getMbmsSaId() {
        return mbmsSaId;
    }


    public void setMbmsSaId(int value) {
        this.mbmsSaId = value;
    }


    public int getMbsfnArea() {
        return mbsfnArea;
    }


    public void setMbsfnArea(int value) {
        this.mbsfnArea = value;
    }


    public TPointCoordinate getCurrentCoordinate() {
        return currentCoordinate;
    }


    public void setCurrentCoordinate(TPointCoordinate value) {
        this.currentCoordinate = value;
    }

}
