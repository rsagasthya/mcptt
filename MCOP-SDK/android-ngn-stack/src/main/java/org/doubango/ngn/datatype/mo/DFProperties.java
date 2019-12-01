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



package org.doubango.ngn.datatype.mo; 
 import org.simpleframework.xml.Element; 
 import org.simpleframework.xml.Root;






/**
 * 
 */

@Root(strict=false, name = "DFProperties")
public class DFProperties {

    @Element(required = false , name = "AccessType")
    protected AccessType accessType;
    @Element(required = false , name = "DefaultValue")
    protected String defaultValue;
    @Element(required = false , name = "Description")
    protected String description;
    @Element(required = false , name = "DFFormat")
    protected DFFormat dfFormat;
    @Element(required = false , name = "Occurrence")
    protected Occurrence occurrence;
    @Element(required = false , name = "Scope")
    protected Scope scope;
    @Element(required = false , name = "DFTitle")
    protected String dfTitle;
    @Element(required = false , name = "DFType")
    protected DFType dfType;
    @Element(required = false , name = "CaseSense")
    protected CaseSense caseSense;

    /**
     * Obtiene el valor de la propiedad accessType.
     * 
     * @return
     *     possible object is
     *     {@link AccessType }
     *     
     */
    public AccessType getAccessType() {
        return accessType;
    }

    /**
     * Define el valor de la propiedad accessType.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessType }
     *     
     */
    public void setAccessType(AccessType value) {
        this.accessType = value;
    }

    /**
     * Obtiene el valor de la propiedad defaultValue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Define el valor de la propiedad defaultValue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad dfFormat.
     * 
     * @return
     *     possible object is
     *     {@link DFFormat }
     *     
     */
    public DFFormat getDFFormat() {
        return dfFormat;
    }

    /**
     * Define el valor de la propiedad dfFormat.
     * 
     * @param value
     *     allowed object is
     *     {@link DFFormat }
     *     
     */
    public void setDFFormat(DFFormat value) {
        this.dfFormat = value;
    }

    /**
     * Obtiene el valor de la propiedad occurrence.
     * 
     * @return
     *     possible object is
     *     {@link Occurrence }
     *     
     */
    public Occurrence getOccurrence() {
        return occurrence;
    }

    /**
     * Define el valor de la propiedad occurrence.
     * 
     * @param value
     *     allowed object is
     *     {@link Occurrence }
     *     
     */
    public void setOccurrence(Occurrence value) {
        this.occurrence = value;
    }

    /**
     * Obtiene el valor de la propiedad scope.
     * 
     * @return
     *     possible object is
     *     {@link Scope }
     *     
     */
    public Scope getScope() {
        return scope;
    }

    /**
     * Define el valor de la propiedad scope.
     * 
     * @param value
     *     allowed object is
     *     {@link Scope }
     *     
     */
    public void setScope(Scope value) {
        this.scope = value;
    }

    /**
     * Obtiene el valor de la propiedad dfTitle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFTitle() {
        return dfTitle;
    }

    /**
     * Define el valor de la propiedad dfTitle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFTitle(String value) {
        this.dfTitle = value;
    }

    /**
     * Obtiene el valor de la propiedad dfType.
     * 
     * @return
     *     possible object is
     *     {@link DFType }
     *     
     */
    public DFType getDFType() {
        return dfType;
    }

    /**
     * Define el valor de la propiedad dfType.
     * 
     * @param value
     *     allowed object is
     *     {@link DFType }
     *     
     */
    public void setDFType(DFType value) {
        this.dfType = value;
    }

    /**
     * Obtiene el valor de la propiedad caseSense.
     * 
     * @return
     *     possible object is
     *     {@link CaseSense }
     *     
     */
    public CaseSense getCaseSense() {
        return caseSense;
    }

    /**
     * Define el valor de la propiedad caseSense.
     * 
     * @param value
     *     allowed object is
     *     {@link CaseSense }
     *     
     */
    public void setCaseSense(CaseSense value) {
        this.caseSense = value;
    }

}
