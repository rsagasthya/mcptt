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




package org.doubango.ngn.datatype.ms.cms.mcpttUEConfig;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "IMEI-rangeType")
public class IMEIRangeType {

    @Element(required = false , name = "TAC")
    protected TacType tac;
    @ElementList(required=false,inline=true,entry = "SNR", type = SnrType.class)
    protected List<SnrType> snr;
    @ElementList(required=false,inline=true,entry = "SNR-range", type = SNRRangeType.class)
    protected List<SnrType> sNRRange;



    @Attribute(required = false , name = "index")
    protected String index;



    public TacType getTAC() {
        return tac;
    }


    public void setTAC(TacType value) {
        this.tac = value;
    }

    public List<SnrType> getSnr() {
        return snr;
    }

    public void setSnr(List<SnrType> snr) {
        this.snr = snr;
    }

    public List<SnrType> getsNRRange() {
        return sNRRange;
    }

    public void setsNRRange(List<SnrType> sNRRange) {
        this.sNRRange = sNRRange;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }



}
