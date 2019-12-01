
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




package org.doubango.ngn.datatype.ms.gms.ns.xdm.extensions;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;




@Root(strict=false, name = "serviceListType")
public class ServiceListType {

    @Element(required = false, name = "all-services-except")
    protected AllServicesExceptType allServicesExcept;
    @ElementList(required=false,inline=true,entry = "service")
    @Namespace(prefix = "oxe", reference = "urn:oma:xml:xdm:extensions")
    protected List<ServiceType> service;

    /**
     * Obtiene el valor de la propiedad allServicesExcept.
     * 
     * @return
     *     possible object is
     *     {@link AllServicesExceptType }
     *     
     */
    public AllServicesExceptType getAllServicesExcept() {
        return allServicesExcept;
    }

    /**
     * Define el valor de la propiedad allServicesExcept.
     * 
     * @param value
     *     allowed object is
     *     {@link AllServicesExceptType }
     *     
     */
    public void setAllServicesExcept(AllServicesExceptType value) {
        this.allServicesExcept = value;
    }

    public List<ServiceType> getService() {
        return service;
    }

    public void setService(List<ServiceType> service) {
        this.service = service;
    }
}
