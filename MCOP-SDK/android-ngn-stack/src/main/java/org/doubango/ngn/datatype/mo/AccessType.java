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


package org.doubango.ngn.datatype.mo; 
 import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * 
 */
@Root(strict=false, name = "AccessType")
public class AccessType {

    @Element(required = false , name = "Add")
    protected Add add;
    @Element(required = false , name = "Copy")
    protected Copy copy;
    @Element(required = false , name = "Delete")
    protected Delete delete;
    @Element(required = false , name = "Exec")
    protected Exec exec;
    @Element(required = false , name = "Get")
    protected Get get;
    @Element(required = false , name = "Replace")
    protected Replace replace;

    /**
     * Obtiene el valor de la propiedad add.
     * 
     * @return
     *     possible object is
     *     {@link Add }
     *     
     */
    public Add getAdd() {
        return add;
    }

    /**
     * Define el valor de la propiedad add.
     * 
     * @param value
     *     allowed object is
     *     {@link Add }
     *     
     */
    public void setAdd(Add value) {
        this.add = value;
    }

    /**
     * Obtiene el valor de la propiedad copy.
     * 
     * @return
     *     possible object is
     *     {@link Copy }
     *     
     */
    public Copy getCopy() {
        return copy;
    }

    /**
     * Define el valor de la propiedad copy.
     * 
     * @param value
     *     allowed object is
     *     {@link Copy }
     *     
     */
    public void setCopy(Copy value) {
        this.copy = value;
    }

    /**
     * Obtiene el valor de la propiedad delete.
     * 
     * @return
     *     possible object is
     *     {@link Delete }
     *     
     */
    public Delete getDelete() {
        return delete;
    }

    /**
     * Define el valor de la propiedad delete.
     * 
     * @param value
     *     allowed object is
     *     {@link Delete }
     *     
     */
    public void setDelete(Delete value) {
        this.delete = value;
    }

    /**
     * Obtiene el valor de la propiedad exec.
     * 
     * @return
     *     possible object is
     *     {@link Exec }
     *     
     */
    public Exec getExec() {
        return exec;
    }

    /**
     * Define el valor de la propiedad exec.
     * 
     * @param value
     *     allowed object is
     *     {@link Exec }
     *     
     */
    public void setExec(Exec value) {
        this.exec = value;
    }

    /**
     * Obtiene el valor de la propiedad get.
     * 
     * @return
     *     possible object is
     *     {@link Get }
     *     
     */
    public Get getGet() {
        return get;
    }

    /**
     * Define el valor de la propiedad get.
     * 
     * @param value
     *     allowed object is
     *     {@link Get }
     *     
     */
    public void setGet(Get value) {
        this.get = value;
    }

    /**
     * Obtiene el valor de la propiedad replace.
     * 
     * @return
     *     possible object is
     *     {@link Replace }
     *     
     */
    public Replace getReplace() {
        return replace;
    }

    /**
     * Define el valor de la propiedad replace.
     * 
     * @param value
     *     allowed object is
     *     {@link Replace }
     *     
     */
    public void setReplace(Replace value) {
        this.replace = value;
    }

}
