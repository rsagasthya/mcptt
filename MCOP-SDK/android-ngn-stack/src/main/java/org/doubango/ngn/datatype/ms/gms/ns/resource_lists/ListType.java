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




package org.doubango.ngn.datatype.ms.gms.ns.resource_lists;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


/**
 * <p>Clase Java para listType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="listType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="display-name" type="{urn:ietf:params:xml:ns:resource-lists}display-nameType" minOccurs="0"/>
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;choice>
 *             &lt;element name="list">
 *               &lt;complexType>
 *                 &lt;complexContent>
 *                   &lt;extension base="{urn:ietf:params:xml:ns:resource-lists}listType">
 *                     &lt;anyAttribute processContents='lax' namespace='##other'/>
 *                   &lt;/extension>
 *                 &lt;/complexContent>
 *               &lt;/complexType>
 *             &lt;/element>
 *             &lt;element name="external" type="{urn:ietf:params:xml:ns:resource-lists}externalType"/>
 *             &lt;element name="entry" type="{urn:ietf:params:xml:ns:resource-lists}entryType"/>
 *             &lt;element name="entry-ref" type="{urn:ietf:params:xml:ns:resource-lists}entry-refType"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "listType")

public class ListType {

    @Element(required = false, name =  "display-name")
    protected DisplayNameType displayName;
    @ElementList(required=false,inline=true,entry = "list")
    protected java.util.List<ListType> list;
    @ElementList(required=false,inline=true,entry = "external")
    protected java.util.List<ExternalType> external;
    @ElementList(required=false,inline=true,entry = "entry")
    protected java.util.List<EntryType> entry;
    @ElementList(required=false,inline=true,entry = "entry-ref")
    protected java.util.List<EntryRefType> entryRef;

    @Attribute(required = false , name =  "name")
    protected String name;


    /**
     * Obtiene el valor de la propiedad displayName.
     * 
     * @return
     *     possible object is
     *     {@link DisplayNameType }
     *     
     */
    public DisplayNameType getDisplayName() {
        return displayName;
    }

    /**
     * Define el valor de la propiedad displayName.
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayNameType }
     *     
     */
    public void setDisplayName(DisplayNameType value) {
        this.displayName = value;
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


    public List<ListType> getList() {
        return list;
    }

    public void setList(List<ListType> list) {
        this.list = list;
    }

    public List<ExternalType> getExternal() {
        return external;
    }

    public void setExternal(List<ExternalType> external) {
        this.external = external;
    }

    public List<EntryType> getEntry() {
        return entry;
    }

    public void setEntry(List<EntryType> entry) {
        this.entry = entry;
    }

    public List<EntryRefType> getEntryRef() {
        return entryRef;
    }

    public void setEntryRef(List<EntryRefType> entryRef) {
        this.entryRef = entryRef;
    }
}
