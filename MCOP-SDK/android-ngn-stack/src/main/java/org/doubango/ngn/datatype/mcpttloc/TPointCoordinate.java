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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root(strict=false, name = "tPointCoordinate")
public class TPointCoordinate {

    @Element(required=false,name = "longitude")
    protected TCoordinateType longitude;
    @Element(required=false,name = "latitude")
    protected TCoordinateType latitude;


    public TPointCoordinate() {
    }

    public TPointCoordinate(TCoordinateType latitude, TCoordinateType longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public TPointCoordinate(long latitude, long longitude) {
        this.latitude = new TCoordinateType(latitude,ProtectionType.Normal);
        this.longitude = new TCoordinateType(longitude,ProtectionType.Normal);
    }

    public TCoordinateType getLongitude() {
        return longitude;
    }

    public long getLongitudeLong() {
        if(longitude!=null)return -1;
        return longitude.getThreebytes();
    }

    public void setLongitude(TCoordinateType longitude) {
        this.longitude = longitude;
    }

    public TCoordinateType getLatitude() {
        return latitude;
    }

    public long getLatitudeLong() {
        if(latitude!=null)return -1;
        return latitude.getThreebytes();
    }

    public void setLatitude(TCoordinateType latitude) {
        this.latitude = latitude;
    }
}
