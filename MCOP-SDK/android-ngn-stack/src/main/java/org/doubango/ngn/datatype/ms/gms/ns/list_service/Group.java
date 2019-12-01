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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;



/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="list-service" type="{urn:oma:xml:poc:list-service}list-service-type"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@Root(strict=false, name = "group")
@NamespaceList({
        @Namespace(reference = "urn:oma:xml:poc:list-service"),
        @Namespace(prefix = "rl", reference = "urn:ietf:params:xml:ns:resource-lists"),
        @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy"),
        @Namespace(prefix = "ocp", reference = "urn:oma:xml:xdm:common-policy"),
        @Namespace(prefix = "oxe", reference = "urn:oma:xml:xdm:extensions"),
        @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
})
public class Group {

    @ElementList(required=false,inline=true,entry = "list-service")
    protected List<ListServiceType> listService;

    /**
     * Gets the value of the listService property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the listService property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getListService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListServiceType }
     * 
     * 
     */
    public List<ListServiceType> getListService() {
        if (listService == null) {
            listService = new ArrayList<ListServiceType>();
        }
        return this.listService;
    }

    public void setListService(List<ListServiceType> listService) {
        this.listService = listService;
    }
}
