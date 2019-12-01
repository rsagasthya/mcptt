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



package org.doubango.ngn.datatype.mo; 
 import org.simpleframework.xml.Element; 
 import org.simpleframework.xml.Root;






/**
 * 
 */

@Root(strict=false, name = "RTProperties")
public class RTProperties {

    @Element(required = false , name = "ACL")
    protected String acl;
    @Element(required = false , name = "Format")
    protected Format format;
    @Element(required = false , name = "Name")
    protected String name;
    @Element(required = false , name = "Size")
    protected String size;
    @Element(required = false , name = "Title")
    protected String title;
    @Element(required = false , name = "TStamp")
    protected String tStamp;
    @Element(required = false , name = "Type")
    protected Type type;
    @Element(required = false , name = "VerNo")
    protected String verNo;

    /**
     * Obtiene el valor de la propiedad acl.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACL() {
        return acl;
    }

    /**
     * Define el valor de la propiedad acl.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACL(String value) {
        this.acl = value;
    }

    /**
     * Obtiene el valor de la propiedad format.
     * 
     * @return
     *     possible object is
     *     {@link Format }
     *     
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Define el valor de la propiedad format.
     * 
     * @param value
     *     allowed object is
     *     {@link Format }
     *     
     */
    public void setFormat(Format value) {
        this.format = value;
    }

    /**
     * Obtiene el valor de la propiedad name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Define el valor de la propiedad name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Obtiene el valor de la propiedad size.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Define el valor de la propiedad size.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
    }

    /**
     * Obtiene el valor de la propiedad title.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Define el valor de la propiedad title.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Obtiene el valor de la propiedad tStamp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTStamp() {
        return tStamp;
    }

    /**
     * Define el valor de la propiedad tStamp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTStamp(String value) {
        this.tStamp = value;
    }

    /**
     * Obtiene el valor de la propiedad type.
     * 
     * @return
     *     possible object is
     *     {@link Type }
     *     
     */
    public Type getType() {
        return type;
    }

    /**
     * Define el valor de la propiedad type.
     * 
     * @param value
     *     allowed object is
     *     {@link Type }
     *     
     */
    public void setType(Type value) {
        this.type = value;
    }

    /**
     * Obtiene el valor de la propiedad verNo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerNo() {
        return verNo;
    }

    /**
     * Define el valor de la propiedad verNo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerNo(String value) {
        this.verNo = value;
    }

}
