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


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root(strict=false, name = "ruleType")
public class RuleType {
    @Element(required = false, name = "conditions")
    protected ConditionsType conditions;
    @Element(required = false, name = "actions")
    protected ExtensibleType actions;
    @Element(required = false, name = "transformations")
    protected ExtensibleType transformations;
    @Attribute(required = false , name =  "id")
    protected String id;

    /**
     * Obtiene el valor de la propiedad conditions.
     * 
     * @return
     *     possible object is
     *     {@link ConditionsType }
     *     
     */
    public ConditionsType getConditions() {
        return conditions;
    }

    /**
     * Define el valor de la propiedad conditions.
     * 
     * @param value
     *     allowed object is
     *     {@link ConditionsType }
     *     
     */
    public void setConditions(ConditionsType value) {
        this.conditions = value;
    }

    /**
     * Obtiene el valor de la propiedad actions.
     * 
     * @return
     *     possible object is
     *     {@link ExtensibleType }
     *     
     */
    public ExtensibleType getActions() {
        return actions;
    }

    /**
     * Define el valor de la propiedad actions.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensibleType }
     *     
     */
    public void setActions(ExtensibleType value) {
        this.actions = value;
    }

    /**
     * Obtiene el valor de la propiedad transformations.
     * 
     * @return
     *     possible object is
     *     {@link ExtensibleType }
     *     
     */
    public ExtensibleType getTransformations() {
        return transformations;
    }

    /**
     * Define el valor de la propiedad transformations.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensibleType }
     *     
     */
    public void setTransformations(ExtensibleType value) {
        this.transformations = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
