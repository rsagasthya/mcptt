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

package org.doubango.ngn.datatype.ms.gms.ns.xcap_diff;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;


/**
 * <p>Clase Java para elementType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="elementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sel" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="exists" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name =  "elementType")
public class ElementType {


    @Attribute(required = false , name = "sel")
    protected String sel;
    @Attribute(required = false , name = "exists")
    protected Boolean exists;


    /**
     * Obtiene el valor de la propiedad sel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSel() {
        return sel;
    }

    /**
     * Define el valor de la propiedad sel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSel(String value) {
        this.sel = value;
    }

    /**
     * Obtiene el valor de la propiedad exists.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExists() {
        return exists;
    }

    /**
     * Define el valor de la propiedad exists.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExists(Boolean value) {
        this.exists = value;
    }


}
