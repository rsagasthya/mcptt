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

import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import javax.xml.datatype.Duration;



@Root(strict=false, name = "entryType")
public class EntryType {

    @Element(required = false, name =  "display-name")
    @Namespace(prefix = "rl", reference = "urn:ietf:params:xml:ns:resource-lists")
    protected DisplayNameType displayName;

    //MCS
    @Element(required = false, name = "user-priority")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer userpriority;

    @Element(required = false, name = "participant-type")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String participanttype;

    //MCPTT
    @Element(required = false, name = "on-network-required")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EmptyType onnetworkrequired;

    @Element(required = false, name = "on-network-recvonly")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EmptyType onnetworkrecvonly;

    //MCVideo
    @Element(required = false, name = "mcvideo-on-network-required")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EmptyType mcvideoonnetworkrequired;

    @Element(required = false, name = "mcvideo-mcvideo-id")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EntryType mcvideomcvideoid;

    //MCData
    @Element(required = false, name = "mcdata-max-data-in-single-request")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcdatamaxdatainsinglerequest;

    @Element(required = false, name = "mcdata-max-time-in-single-request")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration mcdatamaxtimeinsinglerequest;

    @Element(required = false, name = "mcdata-mcdata-id")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EntryType mcdatamcdataid;


    @Attribute(required = false , name =  "uri")
    protected String uri;


    public Integer getUserpriority() {
        return userpriority;
    }

    public void setUserpriority(Integer userpriority) {
        this.userpriority = userpriority;
    }

    public String getParticipanttype() {
        return participanttype;
    }

    public void setParticipanttype(String participanttype) {
        this.participanttype = participanttype;
    }

    public EmptyType getOnnetworkrequired() {
        return onnetworkrequired;
    }

    public void setOnnetworkrequired(EmptyType onnetworkrequired) {
        this.onnetworkrequired = onnetworkrequired;
    }

    public EmptyType getOnnetworkrecvonly() {
        return onnetworkrecvonly;
    }

    public void setOnnetworkrecvonly(EmptyType onnetworkrecvonly) {
        this.onnetworkrecvonly = onnetworkrecvonly;
    }

    public EmptyType getMcvideoonnetworkrequired() {
        return mcvideoonnetworkrequired;
    }

    public void setMcvideoonnetworkrequired(EmptyType mcvideoonnetworkrequired) {
        this.mcvideoonnetworkrequired = mcvideoonnetworkrequired;
    }

    public EntryType getMcvideomcvideoid() {
        return mcvideomcvideoid;
    }

    public void setMcvideomcvideoid(EntryType mcvideomcvideoid) {
        this.mcvideomcvideoid = mcvideomcvideoid;
    }

    public int getMcdatamaxdatainsinglerequest() {
        return mcdatamaxdatainsinglerequest;
    }

    public void setMcdatamaxdatainsinglerequest(int mcdatamaxdatainsinglerequest) {
        this.mcdatamaxdatainsinglerequest = mcdatamaxdatainsinglerequest;
    }

    public Duration getMcdatamaxtimeinsinglerequest() {
        return mcdatamaxtimeinsinglerequest;
    }

    public void setMcdatamaxtimeinsinglerequest(Duration mcdatamaxtimeinsinglerequest) {
        this.mcdatamaxtimeinsinglerequest = mcdatamaxtimeinsinglerequest;
    }

    public EntryType getMcdatamcdataid() {
        return mcdatamcdataid;
    }

    public void setMcdatamcdataid(EntryType mcdatamcdataid) {
        this.mcdatamcdataid = mcdatamcdataid;
    }

    /**
     * Obtiene el valor de la propiedad displayName.
     * 
     * @return
     *     possible object is
     *     {@link String }
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
     *     {@link String }
     *     
     */
    public void setDisplayName(DisplayNameType value) {
        this.displayName = value;
    }



    /**
     * Obtiene el valor de la propiedad uri.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUri() {
        return uri;
    }

    /**
     * Define el valor de la propiedad uri.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUri(String value) {
        this.uri = value;
    }


    @Root(strict=false, name = "")
    public static class DisplayName
        extends DisplayNameType
    {


    }

}
