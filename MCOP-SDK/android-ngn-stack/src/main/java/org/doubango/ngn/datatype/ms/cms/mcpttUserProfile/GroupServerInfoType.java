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



package org.doubango.ngn.datatype.ms.cms.mcpttUserProfile;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict=false, name = "GroupServerInfoType")
public class GroupServerInfoType {

    @Element(required = false, name = "GMS-Serv-Id")
    protected ListEntryType gmsServId;
    @Element(required = false, name = "IDMS-token-endpoint")
    protected ListEntryType idmsTokenEndpoint;
    @Element(required = false, name = "KMS-URI")
    protected ListEntryType kmsuri;

    /**
     * Obtiene el valor de la propiedad gmsServId.
     * 
     * @return
     *     possible object is
     *     {@link ListEntryType }
     *     
     */
    public ListEntryType getGMSServId() {
        return gmsServId;
    }

    /**
     * Define el valor de la propiedad gmsServId.
     * 
     * @param value
     *     allowed object is
     *     {@link ListEntryType }
     *     
     */
    public void setGMSServId(ListEntryType value) {
        this.gmsServId = value;
    }

    /**
     * Obtiene el valor de la propiedad idmsTokenEndpoint.
     * 
     * @return
     *     possible object is
     *     {@link ListEntryType }
     *     
     */
    public ListEntryType getIDMSTokenEndpoint() {
        return idmsTokenEndpoint;
    }

    /**
     * Define el valor de la propiedad idmsTokenEndpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link ListEntryType }
     *     
     */
    public void setIDMSTokenEndpoint(ListEntryType value) {
        this.idmsTokenEndpoint = value;
    }

    /**
     * Obtiene el valor de la propiedad kmsuri.
     * 
     * @return
     *     possible object is
     *     {@link ListEntryType }
     *     
     */
    public ListEntryType getKMSURI() {
        return kmsuri;
    }

    /**
     * Define el valor de la propiedad kmsuri.
     * 
     * @param value
     *     allowed object is
     *     {@link ListEntryType }
     *     
     */
    public void setKMSURI(ListEntryType value) {
        this.kmsuri = value;
    }



}
