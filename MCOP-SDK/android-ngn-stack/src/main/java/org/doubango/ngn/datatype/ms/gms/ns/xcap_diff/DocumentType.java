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
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


/**
 * <p>Clase Java para documentType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="documentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="body-not-changed" type="{urn:ietf:params:xml:ns:xcap-diff}emptyType"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;choice>
 *             &lt;element name="add">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;extension base="{urn:ietf:params:xml:ns:xcap-diff}add">
 *                     &lt;anyAttribute processContents='lax'/>
 *                   &lt;/extension>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="remove">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;extension base="{urn:ietf:params:xml:ns:xcap-diff}remove">
 *                     &lt;anyAttribute processContents='lax'/>
 *                   &lt;/extension>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="replace">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;extension base="{urn:ietf:params:xml:ns:xcap-diff}replace">
 *                     &lt;anyAttribute processContents='lax'/>
 *                   &lt;/extension>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;any processContents='lax' namespace='##other'/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *       &lt;/choice>
 *       &lt;attribute name="sel" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="new-etag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="previous-etag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name =  "documentType")
public class DocumentType {

    @Element(required = false, name = "body-not-changed")
    protected EmptyType bodyNotChanged;
    @ElementList(required=false,inline=true,entry = "remove")
    protected java.util.List<EmptyType> remove;
    @ElementList(required=false,inline=true,entry = "replace")
    protected java.util.List<EmptyType> replace;
    @ElementList(required=false,inline=true,entry = "add")
    protected java.util.List<EmptyType> add;

    @Attribute(required = false , name = "sel")
    protected String sel;
    @Attribute(required = false , name = "new-etag")
    protected String newEtag;
    @Attribute(required = false , name = "previous-etag")
    protected String previousEtag;


    /**
     * Obtiene el valor de la propiedad bodyNotChanged.
     * 
     * @return
     *     possible object is
     *     {@link EmptyType }
     *     
     */
    public EmptyType getBodyNotChanged() {
        return bodyNotChanged;
    }

    /**
     * Define el valor de la propiedad bodyNotChanged.
     * 
     * @param value
     *     allowed object is
     *     {@link EmptyType }
     *     
     */
    public void setBodyNotChanged(EmptyType value) {
        this.bodyNotChanged = value;
    }


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
     * Obtiene el valor de la propiedad newEtag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewEtag() {
        return newEtag;
    }

    /**
     * Define el valor de la propiedad newEtag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewEtag(String value) {
        this.newEtag = value;
    }

    /**
     * Obtiene el valor de la propiedad previousEtag.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviousEtag() {
        return previousEtag;
    }

    /**
     * Define el valor de la propiedad previousEtag.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviousEtag(String value) {
        this.previousEtag = value;
    }

    public List<EmptyType> getRemove() {
        return remove;
    }

    public void setRemove(List<EmptyType> remove) {
        this.remove = remove;
    }

    public List<EmptyType> getReplace() {
        return replace;
    }

    public void setReplace(List<EmptyType> replace) {
        this.replace = replace;
    }

    public List<EmptyType> getAdd() {
        return add;
    }

    public void setAdd(List<EmptyType> add) {
        this.add = add;
    }
}
