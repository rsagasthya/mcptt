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




package org.doubango.ngn.datatype.ms.gms.ns.common_policy;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;



/**
 * <p>Clase Java para identityType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="identityType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="one" type="{urn:ietf:params:xml:ns:common-policy}oneType"/>
 *         &lt;element name="many" type="{urn:ietf:params:xml:ns:common-policy}manyType"/>
 *         &lt;any processContents='lax' namespace='##other'/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "identityType")
public class IdentityType {



    @ElementList(required=false,inline=true,entry = "many")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected List<ManyType> many;

    @ElementList(required=false,inline=true,entry = "one")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected List<OneType> one;


    public List<ManyType> getMany() {
        return many;
    }

    public void setMany(List<ManyType> many) {
        this.many = many;
    }

    public List<OneType> getOne() {
        return one;
    }

    public void setOne(List<OneType> one) {
        this.one = one;
    }
}
