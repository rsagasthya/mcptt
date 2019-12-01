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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

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
 *       &lt;sequence minOccurs="0">
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *           &lt;choice>
 *             &lt;element name="document" type="{urn:ietf:params:xml:ns:xcap-diff}documentType"/>
 *             &lt;element name="element" type="{urn:ietf:params:xml:ns:xcap-diff}elementType"/>
 *             &lt;element name="attribute" type="{urn:ietf:params:xml:ns:xcap-diff}attributeType"/>
 *           &lt;/choice>
 *         &lt;/sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="xcap-root" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name =  "xcap-diff")
@Namespace(reference = "urn:ietf:params:xml:ns:xcap-diff")
public class XcapDiff {

    @ElementList(required=false,inline=true,entry = "document")
    protected java.util.List<DocumentType> document;
    @ElementList(required=false,inline=true,entry = "element")
    protected java.util.List<ElementType> element;
    @ElementList(required=false,inline=true,entry = "attribute")
    protected java.util.List<AttributeType> attribute;

    @Attribute(required = false , name = "xcap-root")
    protected String xcapRoot;

    public List<DocumentType> getDocument() {
        return document;
    }

    public void setDocument(List<DocumentType> document) {
        this.document = document;
    }

    public List<ElementType> getElement() {
        return element;
    }

    public void setElement(List<ElementType> element) {
        this.element = element;
    }

    public List<AttributeType> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<AttributeType> attribute) {
        this.attribute = attribute;
    }

    /**
     * Obtiene el valor de la propiedad xcapRoot.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXcapRoot() {
        return xcapRoot;
    }

    /**
     * Define el valor de la propiedad xcapRoot.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXcapRoot(String value) {
        this.xcapRoot = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */

}
