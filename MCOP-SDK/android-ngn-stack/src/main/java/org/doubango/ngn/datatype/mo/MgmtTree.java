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

//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantaci�n de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perder�n si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.02.05 a las 01:03:33 PM CET 
//


package org.doubango.ngn.datatype.mo;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;






@Root(strict=false, name = "MgmtTree")
public class MgmtTree {

    @Element(required = false , name = "VerDTD")
    protected String verDTD;
    @Element(required = false , name = "Man")
    protected String man;
    @Element(required = false , name = "Mod")
    protected String mod;
    @ElementList(required=false,inline=true,entry = "Node")
    protected List<Node> node;

    /**
     * Obtiene el valor de la propiedad verDTD.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVerDTD() {
        return verDTD;
    }

    /**
     * Define el valor de la propiedad verDTD.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVerDTD(String value) {
        this.verDTD = value;
    }

    /**
     * Obtiene el valor de la propiedad man.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMan() {
        return man;
    }

    /**
     * Define el valor de la propiedad man.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMan(String value) {
        this.man = value;
    }

    /**
     * Obtiene el valor de la propiedad mod.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMod() {
        return mod;
    }

    /**
     * Define el valor de la propiedad mod.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMod(String value) {
        this.mod = value;
    }

    /**
     * Gets the value of the node property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the node property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNode().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Node }
     *
     *
     */
    public List<Node> getNode() {
        if (node == null) {
            node = new ArrayList<Node>();
        }
        return this.node;
    }

}
