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




package org.doubango.ngn.datatype.ms.gms.ns.list_service;

import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ExternalType;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para list-type complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="list-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" type="{urn:ietf:params:xml:ns:resource-lists}entryType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="external" type="{urn:ietf:params:xml:ns:resource-lists}externalType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "list-type")
public class ListType {

    @ElementList(required=false,inline=true,entry = "entry")
    protected List<EntryType> entry;
    @ElementList(required=false,inline=true,entry = "external")
    protected List<ExternalType> external;



    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EntryType }
     * 
     * 
     */
    public List<EntryType> getEntry() {
        if (entry == null) {
            entry = new ArrayList<EntryType>();
        }
        return this.entry;
    }

    /**
     * Gets the value of the external property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the external property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExternal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExternalType }
     * 
     * 
     */
    public List<ExternalType> getExternal() {
        if (external == null) {
            external = new ArrayList<ExternalType>();
        }
        return this.external;
    }

    public void setEntry(List<EntryType> entry) {
        this.entry = entry;
    }

    public void setExternal(List<ExternalType> external) {
        this.external = external;
    }
}
