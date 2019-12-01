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



package org.doubango.ngn.datatype.mcpttinfo;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "allTypeGKTPsType")
public class AllTypeGKTPsType {

    @Element(required=false,name = "GMK-GKTPs")
    protected SingleTypeGKTPsType gmkgktPs;
    @Element(required=false,name = "MKFC-GKTPs")
    protected SingleTypeGKTPsType mkfcgktPs;


    public SingleTypeGKTPsType getGMKGKTPs() {
        return gmkgktPs;
    }


    public void setGMKGKTPs(SingleTypeGKTPsType value) {
        this.gmkgktPs = value;
    }


    public SingleTypeGKTPsType getMKFCGKTPs() {
        return mkfcgktPs;
    }


    public void setMKFCGKTPs(SingleTypeGKTPsType value) {
        this.mkfcgktPs = value;
    }
}
