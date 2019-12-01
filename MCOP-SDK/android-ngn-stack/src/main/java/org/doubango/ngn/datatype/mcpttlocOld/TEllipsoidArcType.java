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


@Root(strict=false, name = "tEllipsoidArcType")
public class TEllipsoidArcType {

    @Element(required=false,name = "Center")
    protected TPointCoordinate center;
    @Element(required=false,name = "Radius")
    protected int radius;
    @Element(required=false,name = "OffsetAngle")
    protected short offsetAngle;
    @Element(required=false,name = "IncludedAngle")
    protected short includedAngle;
    
    


    public TPointCoordinate getCenter() {
        return center;
    }


    public void setCenter(TPointCoordinate value) {
        this.center = value;
    }


    public int getRadius() {
        return radius;
    }


    public void setRadius(int value) {
        this.radius = value;
    }


    public short getOffsetAngle() {
        return offsetAngle;
    }


    public void setOffsetAngle(short value) {
        this.offsetAngle = value;
    }


    public short getIncludedAngle() {
        return includedAngle;
    }


    public void setIncludedAngle(short value) {
        this.includedAngle = value;
    }

}
