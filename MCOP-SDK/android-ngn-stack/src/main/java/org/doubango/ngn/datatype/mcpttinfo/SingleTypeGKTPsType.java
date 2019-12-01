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


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(strict=false, name = "singleTypeGKTPsType")
public class SingleTypeGKTPsType {

    @ElementList(inline=true,entry="GKTP",required=false)
    protected List<GKTPType> gktp;
    @ElementList(inline=true,required=false, name = "on-network-regrouped-GKTPs")
    protected List<OnNetworkRegroupedGKTPsType> onNetworkRegroupedGKTPs;




    public List<GKTPType> getGKTP() {
        if (gktp == null) {
            gktp = new ArrayList<GKTPType>();
        }
        return this.gktp;
    }


    public List<OnNetworkRegroupedGKTPsType> getOnNetworkRegroupedGKTPs() {
        if (onNetworkRegroupedGKTPs == null) {
            onNetworkRegroupedGKTPs = new ArrayList<OnNetworkRegroupedGKTPsType>();
        }
        return this.onNetworkRegroupedGKTPs;
    }

}
