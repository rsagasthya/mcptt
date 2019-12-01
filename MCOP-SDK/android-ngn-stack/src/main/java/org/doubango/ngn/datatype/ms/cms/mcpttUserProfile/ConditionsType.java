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




package org.doubango.ngn.datatype.ms.cms.mcpttUserProfile;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(strict=false, name = "conditionsType")
public class ConditionsType {


    @ElementList(required=false,inline=true,entry = "sphere")
    protected List<SphereType> sphere;
    @ElementList(required=false,inline=true,entry = "identity")
    protected List<IdentityType> identity;
    @ElementList(required=false,inline=true,entry = "validity")
    protected List<ValidityType> validity;

    public List<SphereType> getSphere() {
        return sphere;
    }

    public void setSphere(List<SphereType> sphere) {
        this.sphere = sphere;
    }

    public List<IdentityType> getIdentity() {
        return identity;
    }

    public void setIdentity(List<IdentityType> identity) {
        this.identity = identity;
    }

    public List<ValidityType> getValidity() {
        return validity;
    }

    public void setValidity(List<ValidityType> validity) {
        this.validity = validity;
    }
}
