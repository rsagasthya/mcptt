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
 * <p>Clase Java para remove complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="remove">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="sel" use="required" type="{urn:ietf:params:xml:ns:xcap-diff}xpath" />
 *       &lt;attribute name="ws" type="{urn:ietf:params:xml:ns:xcap-diff}ws" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "remove")
public class Remove {

    @Attribute(required = false , name = "sel")
    protected String sel;
    @Attribute(required = false , name = "ws")
    protected Ws ws;

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
     * Obtiene el valor de la propiedad ws.
     * 
     * @return
     *     possible object is
     *     {@link Ws }
     *     
     */
    public Ws getWs() {
        return ws;
    }

    /**
     * Define el valor de la propiedad ws.
     * 
     * @param value
     *     allowed object is
     *     {@link Ws }
     *     
     */
    public void setWs(Ws value) {
        this.ws = value;
    }

}
