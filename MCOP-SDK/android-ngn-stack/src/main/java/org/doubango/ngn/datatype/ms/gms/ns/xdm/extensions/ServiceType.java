
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

import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.ExtensionType;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(strict=false, name = "serviceType")
public class ServiceType {



    @ElementList(required=false,inline=true,entry = "group-media")
    @Namespace(prefix = "oxe", reference = "urn:oma:xml:xdm:extensions")
    protected List<ServiceType> groupmedia;

    @Attribute(required=false,name = "enabler")
    @Namespace(prefix = "oxe", reference = "urn:oma:xml:xdm:extensions")
    protected String enabler;

    //MCPTT
    @Element(required = false, name = "mcptt-speech")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected ExtensionType mcpttspeech;

    //MCVideo
    @Element(required = false, name = "mcvideo-video-media")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected ExtensionType mcvideovideomedia;

    /**
     * Obtiene el valor de la propiedad enabler.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnabler() {
        return enabler;
    }

    /**
     * Define el valor de la propiedad enabler.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnabler(String value) {
        this.enabler = value;
    }

    public List<ServiceType> getGroupmedia() {
        return groupmedia;
    }

    public void setGroupmedia(List<ServiceType> groupmedia) {
        this.groupmedia = groupmedia;
    }
}
