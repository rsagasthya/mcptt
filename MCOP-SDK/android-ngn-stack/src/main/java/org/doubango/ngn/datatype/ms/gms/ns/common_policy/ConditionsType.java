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

import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.List;



/**
 * <p>Clase Java para conditionsType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="conditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="identity" type="{urn:ietf:params:xml:ns:common-policy}identityType" minOccurs="0"/>
 *         &lt;element name="sphere" type="{urn:ietf:params:xml:ns:common-policy}sphereType" minOccurs="0"/>
 *         &lt;element name="validity" type="{urn:ietf:params:xml:ns:common-policy}validityType" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "conditionsType")
public class ConditionsType {



    @ElementList(required=false,inline=true,entry = "identity")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected List<IdentityType> identity;
    @ElementList(required=false,inline=true,entry = "validity")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected List<ValidityType> validity;
    @ElementList(required=false,inline=true,entry = "sphere")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected List<SphereType> sphere;

    @Element(required = false, name = "is-list-member")
    @Namespace(reference = "urn:oma:xml:poc:list-service")
    protected EmptyType islistmember;


    public List<IdentityType> getIdentity() {
        return identity;
    }

    public void setIdentity(List<IdentityType> identity) {
        this.identity = identity;
    }

    public List<ValidityType> getValidity() {
        return validity;
    }

    public void setValidity(List<ValidityType> validity) {
        this.validity = validity;
    }

    public List<SphereType> getSphere() {
        return sphere;
    }

    public void setSphere(List<SphereType> sphere) {
        this.sphere = sphere;
    }

    public EmptyType getIslistmember() {
        return islistmember;
    }

    public void setIslistmember(EmptyType islistmember) {
        this.islistmember = islistmember;
    }
}
