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




package org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * <p>Clase Java para regroupedType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="regroupedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="constituent-MCPTT-group-IDs" type="{urn:3gpp:ns:mcpttGroupInfo:1.0}constituentMCPTTgroupTypeIDsType"/>
 *         &lt;element ref="{urn:3gpp:ns:mcpttGroupInfo:1.0}on-network-group-priority" minOccurs="0"/>
 *         &lt;element ref="{urn:3gpp:ns:mcpttGroupInfo:1.0}protect-media" minOccurs="0"/>
 *         &lt;element ref="{urn:3gpp:ns:mcpttGroupInfo:1.0}protect-floor-control-signalling" minOccurs="0"/>
 *         &lt;element ref="{urn:3gpp:ns:mcpttGroupInfo:1.0}require-multicast-floor-control-signalling" minOccurs="0"/>
 *         &lt;element name="anyExt" type="{urn:3gpp:ns:mcpttGroupInfo:1.0}anyExtType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="temporary-MCPTT-group-ID" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="temporary-MCPTT-group-requestor" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "regroupedType")
public class RegroupedType {

    @Element( required = false, name = " constituent-MCPTT-group-IDs")
    protected ConstituentMCPTTgroupTypeIDsType constituentMCPTTGroupIDs;
    @Element( required = false, name = " on-network-group-priority")
    protected Integer onNetworkGroupPriority;
    @Element( required = false, name = " protect-media")
    protected Boolean protectMedia;
    @Element( required = false, name = " protect-floor-control-signalling")
    protected Boolean protectFloorControlSignalling;
    @Element( required = false, name = " require-multicast-floor-control-signalling")
    protected EmptyType requireMulticastFloorControlSignalling;
    @Attribute(name = "temporary-MCPTT-group-ID")
    protected String temporaryMCPTTGroupID;
    @Attribute(name = "temporary-MCPTT-group-requestor")
    protected String temporaryMCPTTGroupRequestor;


    /**
     * Obtiene el valor de la propiedad constituentMCPTTGroupIDs.
     * 
     * @return
     *     possible object is
     *     {@link ConstituentMCPTTgroupTypeIDsType }
     *     
     */
    public ConstituentMCPTTgroupTypeIDsType getConstituentMCPTTGroupIDs() {
        return constituentMCPTTGroupIDs;
    }

    /**
     * Define el valor de la propiedad constituentMCPTTGroupIDs.
     * 
     * @param value
     *     allowed object is
     *     {@link ConstituentMCPTTgroupTypeIDsType }
     *     
     */
    public void setConstituentMCPTTGroupIDs(ConstituentMCPTTgroupTypeIDsType value) {
        this.constituentMCPTTGroupIDs = value;
    }

    /**
     * Obtiene el valor de la propiedad onNetworkGroupPriority.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOnNetworkGroupPriority() {
        return onNetworkGroupPriority;
    }

    /**
     * Define el valor de la propiedad onNetworkGroupPriority.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOnNetworkGroupPriority(Integer value) {
        this.onNetworkGroupPriority = value;
    }

    /**
     * Obtiene el valor de la propiedad protectMedia.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProtectMedia() {
        return protectMedia;
    }

    /**
     * Define el valor de la propiedad protectMedia.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProtectMedia(Boolean value) {
        this.protectMedia = value;
    }

    /**
     * Obtiene el valor de la propiedad protectFloorControlSignalling.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isProtectFloorControlSignalling() {
        return protectFloorControlSignalling;
    }

    /**
     * Define el valor de la propiedad protectFloorControlSignalling.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setProtectFloorControlSignalling(Boolean value) {
        this.protectFloorControlSignalling = value;
    }

    /**
     * Obtiene el valor de la propiedad requireMulticastFloorControlSignalling.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getRequireMulticastFloorControlSignalling() {
        return requireMulticastFloorControlSignalling;
    }

    /**
     * Define el valor de la propiedad requireMulticastFloorControlSignalling.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setRequireMulticastFloorControlSignalling(EmptyType value) {
        this.requireMulticastFloorControlSignalling = value;
    }



    /**
     * Obtiene el valor de la propiedad temporaryMCPTTGroupID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemporaryMCPTTGroupID() {
        return temporaryMCPTTGroupID;
    }

    /**
     * Define el valor de la propiedad temporaryMCPTTGroupID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemporaryMCPTTGroupID(String value) {
        this.temporaryMCPTTGroupID = value;
    }

    /**
     * Obtiene el valor de la propiedad temporaryMCPTTGroupRequestor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTemporaryMCPTTGroupRequestor() {
        return temporaryMCPTTGroupRequestor;
    }

    /**
     * Define el valor de la propiedad temporaryMCPTTGroupRequestor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTemporaryMCPTTGroupRequestor(String value) {
        this.temporaryMCPTTGroupRequestor = value;
    }



}
