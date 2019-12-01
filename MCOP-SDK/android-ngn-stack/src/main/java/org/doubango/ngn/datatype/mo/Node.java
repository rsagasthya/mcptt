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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Root(strict=false, name = "Node")
public class Node {

    @Element(required = false , name = "NodeName")
    protected String nodeName;
    @Element(required = false , name = "Path")
    protected String path;
    @Element(required = false , name = "RTProperties")
    protected RTProperties rtProperties;
    @Element(required = false , name = "DFProperties")
    protected DFProperties dfProperties;


    @ElementList(required=false,inline=true,entry = "Node")
    protected List<Node> node;
    //protected Map<String,Node> nodeMap;
    @ElementList(required=false,inline=true,entry = "Value")
    protected List<Value> value;


    /**
     * Obtiene el valor de la propiedad nodeName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Define el valor de la propiedad nodeName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeName(String value) {
        this.nodeName = value;
    }

    /**
     * Obtiene el valor de la propiedad path.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPath() {
        return path;
    }

    /**
     * Define el valor de la propiedad path.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPath(String value) {
        this.path = value;
    }

    /**
     * Obtiene el valor de la propiedad rtProperties.
     * 
     * @return
     *     possible object is
     *     {@link RTProperties }
     *     
     */
    public RTProperties getRTProperties() {
        return rtProperties;
    }

    /**
     * Define el valor de la propiedad rtProperties.
     * 
     * @param value
     *     allowed object is
     *     {@link RTProperties }
     *     
     */
    public void setRTProperties(RTProperties value) {
        this.rtProperties = value;
    }

    /**
     * Obtiene el valor de la propiedad dfProperties.
     * 
     * @return
     *     possible object is
     *     {@link DFProperties }
     *     
     */
    public DFProperties getDFProperties() {
        return dfProperties;
    }

    public List<Node> getNode() {
        if(node==null)node=new ArrayList<>();
        return node;
    }

    public void setNode(List<Node> node) {

        this.node = node;
    }
/*
    public Map<String,Node> getNodeMap() {
        if(nodeMap==null)nodeMap=generateMapNode(node);
        return nodeMap;
    }
*/
    public void setNode(Map<String,Node> node) {
        this.node = new ArrayList<>(node.values());
    }

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    private static Map<String,Node> generateMapNode(List<Node> nodes){
        Map<String,Node> stringNodeMap=new HashMap<>();
        if(nodes!=null){
            for(Node node:nodes){
                if(node!=null && node.getNodeName()!=null && !node.getNodeName().isEmpty()){
                    stringNodeMap.put(node.getNodeName(),node);
                }
            }

        }

        return stringNodeMap;
    }

}
