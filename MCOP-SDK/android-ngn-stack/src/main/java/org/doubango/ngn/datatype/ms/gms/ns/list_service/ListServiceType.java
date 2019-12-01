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

import org.doubango.ngn.datatype.ms.gms.ns.common_policy.Ruleset;
import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.*;
import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.DisplayNameType;
import org.doubango.ngn.datatype.ms.gms.ns.xdm.extensions.ServiceListType;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.List;

import javax.xml.datatype.Duration;


/**
 * <p>Clase Java para list-service-type complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="list-service-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="display-name" type="{urn:ietf:params:xml:ns:resource-lists}display-nameType" minOccurs="0"/>
 *         &lt;element name="list" type="{urn:oma:xml:poc:list-service}list-type" minOccurs="0"/>
 *         &lt;element name="invite-members" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="max-participant-count" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="uri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "list-service-type")
public class ListServiceType {

    @Element(required = false, name =  "display-name")
    protected DisplayNameType displayName;
    @Element(required = false, name =  "list")
    protected ListType list;
    @Element(required = false, name =  "invite-members")
    protected Boolean inviteMembers;
    @Element(required = false, name =  "max-participant-count")
    protected BigInteger maxParticipantCount;

    @Element(required = false, name = "ruleset")
    @Namespace(prefix = "cp", reference = "urn:ietf:params:xml:ns:common-policy")
    protected Ruleset ruleset;

    @ElementList(required=false,inline=true,entry = "supported-services")
    @Namespace(prefix = "oxe", reference = "urn:oma:xml:xdm:extensions")
    protected List<ServiceListType> supportedservices;

    @Attribute(required = false , name =  "uri")
    protected String uri;

    //MCS
    @Element(required = false, name = "on-network-disabled")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType onnetworkdisabled;

    @Element(required = false, name = "on-network-temporary")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected TemporaryType onNetworkTemporary;

    @Element(required = false, name = "on-network-regrouped")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected RegroupedType onnetworkregrouped;

    @Element(required = false, name = "off-network-ProSe-layer-2-group-id")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSelayer2groupid;

    @Element(required = false, name = "off-network-IP-multicast-address")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkIPmulticastaddress;

    @Element(required = false, name = "off-network-PDN-type")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkPDNtype;

    @Element(required = false, name = "off-network-ProSe-relay-service-code")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSerelayservicecode;

    @Element(required = false, name = "owner")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String owner;

    @Element(required = false, name = "level-within-group-hierarchy")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer levelwithingrouphierarchy;

    @Element(required = false, name = "level-within-user-hierarchy")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer levelwithinuserhierarchy;

    //MCPTT

    @Element(required = false, name = "on-network-group-priority")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer onnetworkgrouppriority;

    @Element(required = false, name = "off-network-ProSe-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSesignallingPPPP;

    @Element(required = false, name = "off-network-ProSe-emergency-call-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSeemergencycallsignallingPPPP;

    @Element(required = false, name = "off-network-ProSe-imminent-peril-call-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSeimminentperilcallsignallingPPPP;

    @Element(required = false, name = "off-network-ProSe-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSemediaPPPP;

    @Element(required = false, name = "off-network-ProSe-emergency-call-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSeemergencycallmediaPPPP;

    @Element(required = false, name = "off-network-ProSe-imminent-peril-call-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String offnetworkProSeimminentperilcallmediaPPPP;

    @Element(required = false, name = "on-network-max-participant-count")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String onnetworkmaxparticipantcount;

    @Element(required = false, name = "on-network-invite-members")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean onnetworkinvitemembers;

    @Element(required = false, name = "preferred-voice-encodings")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EncodingsType preferredvoiceencodings;

    @Element(required = false, name = "on-network-in-progress-emergency-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration onnetworkinprogressemergencystatecancellationtimeout;

    @Element(required = false, name = "on-network-in-progress-imminent-peril-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration onnetworkinprogressimminentperilstatecancellationtimeout;

    @Element(required = false, name = "off-network-in-progress-emergency-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration offnetworkinprogressemergencystatecancellationtimeout;

    @Element(required = false, name = "off-network-in-progress-imminent-peril-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration offnetworkinprogressimminentperilstatecancellationtimeout;

    @Element(required = false, name = "on-network-hang-timer")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration onnetworkhangtimer;

    @Element(required = false, name = "on-network-maximum-duration")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration onnetworkmaximumduration;

    @Element(required = false, name = "off-network-hang-timer")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration offnetworkhangtimer;

    @Element(required = false, name = "off-network-maximum-duration")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration offnetworkmaximumduration;

    @Element(required = false, name = "on-network-minimum-number-to-start")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer onnetworkminimumnumbertostart;

    @Element(required = false, name = "on-network-timeout-for-acknowledgement-of-required-members")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration onnetworktimeoutforacknowledgementofrequiredmembers;

    @Element(required = false, name = "on-network-action-upon-expiration-of-timeout-for-acknowledgement-of-required-members")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String onnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers;

    @Element(required = false, name = "protect-media")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean protectmedia;

    @Element(required = false, name = "protect-floor-control-signalling")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean protectfloorcontrolsignalling;

    @Element(required = false, name = "require-multicast-floor-control-signalling")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType requiremulticastfloorcontrolsignalling;

    @Element(required = false, name = "off-network-queue-usage")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean offnetworkqueueusage;

    @Element(required = false, name = "mcptt-on-network-audio-cut-in")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcpttonnetworkaudiocutin;

    //MCVideo
    @Element(required = false, name = "mcvideo-on-network-invite-members")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoonnetworkinvitemembers;

    @Element(required = false, name = "mcvideo-on-network-maximum-duration")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration mcvideoonnetworkmaximumduration;

    @Element(required = false, name = "mcvideo-protect-media")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoprotectmedia;

    @Element(required = false, name = "mcvideo-protect-transmission-control")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideoprotecttransmissioncontrol;

    @Element(required = false, name = "mcvideo-preferred-audio-encodings")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EncodingType mcvideopreferredaudioencodings;

    @Element(required = false, name = "mcvideo-preferred-video-encodings")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected EncodingType mcvideopreferredvideoencodings;

    @Element(required = false, name = "mcvideo-preferred-video-resolutions")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideopreferredvideoresolutions;

    @Element(required = false, name = "mcvideo-preferred-video-frame-rate")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideopreferredvideoframerate;

    @Element(required = false, name = "mcvideo-urgent-real-time-video-mode")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideourgentrealtimevideomode;

    @Element(required = false, name = "mcvideo-non-urgent-real-time-video-mode")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideononurgentrealtimevideomode;

    @Element(required = false, name = "mcvideo-non-real-time-video-mode")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcvideononrealtimevideomode;

    @Element(required = false, name = "mcvideo-active-real-time-video-mode")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideoactiverealtimevideomode;

    @Element(required = false, name = "mcvideo-maximum-simultaneous-mcvideo-transmitting-group-members")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcvideomaximumsimultaneousmcvideotransmittinggroupmembers;

    @Element(required = false, name = "mcvideo-on-network-minimum-number-to-start")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcvideoonnetworkminimumnumbertostart;

    @Element(required = false, name = "mcvideo-on-network-group-priority")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcvideoonnetworkgrouppriority;

    @Element(required = false, name = "mcvideo-off-network-arbitration-approach")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkarbitrationapproach;

    @Element(required = false, name = "mcvideo-off-network-maximum-simultaneous-transmissions")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcvideooffnetworkmaximumsimultaneoustransmissions;

    @Element(required = false, name = "mcvideo-off-network-ProSe-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSesignallingPPPP;

    @Element(required = false, name = "mcvideo-off-network-ProSe-emergency-call-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSeemergencycallsignallingPPPP;

    @Element(required = false, name = "mcvideo-off-network-ProSe-imminent-peril-call-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSeimminentperilcallsignallingPPPP;

    @Element(required = false, name = "mcvideo-off-network-ProSe-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSemediaPPPP;

    @Element(required = false, name = "mcvideo-off-network-ProSe-emergency-call-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSeemergencycallmediaPPPP;

    @Element(required = false, name = "mcvideo-off-network-ProSe-imminent-peril-call-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcvideooffnetworkProSeimminentperilcallmediaPPPP;

    @Element(required = false, name = "mcvideo-off-network-in-progress-emergency-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration mcvideooffnetworkinprogressemergencystatecancellationtimeout;

    @Element(required = false, name = "mcvideo-off-network-in-progress-imminent-peril-state-cancellation-timeout")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration mcvideooffnetworkinprogressimminentperilstatecancellationtimeout;

    @Element(required = false, name = "mcvideo-off-network-maximum-duration")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Duration mcvideooffnetworkmaximumduration;

    //MCData


    @Element(required = false, name = "mcdata-protect-media")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataprotectmedia;

    @Element(required = false, name = "mcdata-protect-transmission-control")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataprotecttransmissioncontrol;

    @Element(required = false, name = "mcdata-allow-short-data-service")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowshortdataservice;

    @Element(required = false, name = "mcdata-allow-file-distribution")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowfiledistribution;

    @Element(required = false, name = "mcdata-allow-conversation-management")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowconversationmanagement;

    @Element(required = false, name = "mcdata-allow-tx-control")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowtxcontrol;

    @Element(required = false, name = "mcdata-allow-rx-control")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowrxcontrol;

    @Element(required = false, name = "mcdata-allow-enhanced-status")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Boolean mcdataallowenhancedstatus;

    @Element(required = false, name = "mcdata-enhanced-status-operational-values")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcdataenhancedstatusoperationalvalues;

    @Element(required = false, name = "mcdata-on-network-max-data-size-for-SDS")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcdataonnetworkmaxdatasizeforSDS;

    @Element(required = false, name = "mcdata-on-network-max-data-size-for-FD")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcdataonnetworkmaxdatasizeforFD;

    @Element(required = false, name = "mcdata-on-network-max-data-size-auto-recv")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcdataonnetworkmaxdatasizeautorecv;

    @Element(required = false, name = "mcdata-on-network-group-priority")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected Integer mcdataonnetworkgrouppriority;

    @Element(required = false, name = "mcdata-off-network-ProSe-signalling-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcdataoffnetworkProSesignallingPPPP;

    @Element(required = false, name = "mcdata-off-network-ProSe-media-PPPP")
    @Namespace(prefix = "mcpttgi", reference = "urn:3gpp:ns:mcpttGroupInfo:1.0")
    protected String mcdataoffnetworkProSemediaPPPP;


    public List<ServiceListType> getSupportedservices() {
        return supportedservices;
    }

    public void setSupportedservices(List<ServiceListType> supportedservices) {
        this.supportedservices = supportedservices;
    }

    public Boolean getInviteMembers() {
        return inviteMembers;
    }

    public Ruleset getRuleset() {
        return ruleset;
    }

    public void setRuleset(Ruleset ruleset) {
        this.ruleset = ruleset;
    }

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
     * Obtiene el valor de la propiedad list.
     * 
     * @return
     *     possible object is
     *     {@link ListType }
     *     
     */
    public ListType getList() {
        return list;
    }

    /**
     * Define el valor de la propiedad list.
     * 
     * @param value
     *     allowed object is
     *     {@link ListType }
     *     
     */
    public void setList(ListType value) {
        this.list = value;
    }

    /**
     * Obtiene el valor de la propiedad inviteMembers.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isInviteMembers() {
        return inviteMembers;
    }

    /**
     * Define el valor de la propiedad inviteMembers.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setInviteMembers(Boolean value) {
        this.inviteMembers = value;
    }

    /**
     * Obtiene el valor de la propiedad maxParticipantCount.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxParticipantCount() {
        return maxParticipantCount;
    }

    /**
     * Define el valor de la propiedad maxParticipantCount.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxParticipantCount(BigInteger value) {
        this.maxParticipantCount = value;
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

    public org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EmptyType getOnnetworkdisabled() {
        return onnetworkdisabled;
    }

    public void setOnnetworkdisabled(EmptyType onnetworkdisabled) {
        this.onnetworkdisabled = onnetworkdisabled;
    }

    public TemporaryType getOnNetworkTemporary() {
        return onNetworkTemporary;
    }

    public void setOnNetworkTemporary(TemporaryType onNetworkTemporary) {
        this.onNetworkTemporary = onNetworkTemporary;
    }

    public RegroupedType getOnnetworkregrouped() {
        return onnetworkregrouped;
    }

    public void setOnnetworkregrouped(RegroupedType onnetworkregrouped) {
        this.onnetworkregrouped = onnetworkregrouped;
    }

    public String getOffnetworkProSelayer2groupid() {
        return offnetworkProSelayer2groupid;
    }

    public void setOffnetworkProSelayer2groupid(String offnetworkProSelayer2groupid) {
        this.offnetworkProSelayer2groupid = offnetworkProSelayer2groupid;
    }

    public String getOffnetworkIPmulticastaddress() {
        return offnetworkIPmulticastaddress;
    }

    public void setOffnetworkIPmulticastaddress(String offnetworkIPmulticastaddress) {
        this.offnetworkIPmulticastaddress = offnetworkIPmulticastaddress;
    }

    public String getOffnetworkPDNtype() {
        return offnetworkPDNtype;
    }

    public void setOffnetworkPDNtype(String offnetworkPDNtype) {
        this.offnetworkPDNtype = offnetworkPDNtype;
    }

    public String getOffnetworkProSerelayservicecode() {
        return offnetworkProSerelayservicecode;
    }

    public void setOffnetworkProSerelayservicecode(String offnetworkProSerelayservicecode) {
        this.offnetworkProSerelayservicecode = offnetworkProSerelayservicecode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getLevelwithingrouphierarchy() {
        return levelwithingrouphierarchy;
    }

    public void setLevelwithingrouphierarchy(Integer levelwithingrouphierarchy) {
        this.levelwithingrouphierarchy = levelwithingrouphierarchy;
    }

    public Integer getLevelwithinuserhierarchy() {
        return levelwithinuserhierarchy;
    }

    public void setLevelwithinuserhierarchy(Integer levelwithinuserhierarchy) {
        this.levelwithinuserhierarchy = levelwithinuserhierarchy;
    }

    public Integer getOnnetworkgrouppriority() {
        return onnetworkgrouppriority;
    }

    public void setOnnetworkgrouppriority(Integer onnetworkgrouppriority) {
        this.onnetworkgrouppriority = onnetworkgrouppriority;
    }

    public String getOffnetworkProSesignallingPPPP() {
        return offnetworkProSesignallingPPPP;
    }

    public void setOffnetworkProSesignallingPPPP(String offnetworkProSesignallingPPPP) {
        this.offnetworkProSesignallingPPPP = offnetworkProSesignallingPPPP;
    }

    public String getOffnetworkProSeemergencycallsignallingPPPP() {
        return offnetworkProSeemergencycallsignallingPPPP;
    }

    public void setOffnetworkProSeemergencycallsignallingPPPP(String offnetworkProSeemergencycallsignallingPPPP) {
        this.offnetworkProSeemergencycallsignallingPPPP = offnetworkProSeemergencycallsignallingPPPP;
    }

    public String getOffnetworkProSeimminentperilcallsignallingPPPP() {
        return offnetworkProSeimminentperilcallsignallingPPPP;
    }

    public void setOffnetworkProSeimminentperilcallsignallingPPPP(String offnetworkProSeimminentperilcallsignallingPPPP) {
        this.offnetworkProSeimminentperilcallsignallingPPPP = offnetworkProSeimminentperilcallsignallingPPPP;
    }

    public String getOffnetworkProSemediaPPPP() {
        return offnetworkProSemediaPPPP;
    }

    public void setOffnetworkProSemediaPPPP(String offnetworkProSemediaPPPP) {
        this.offnetworkProSemediaPPPP = offnetworkProSemediaPPPP;
    }

    public String getOffnetworkProSeemergencycallmediaPPPP() {
        return offnetworkProSeemergencycallmediaPPPP;
    }

    public void setOffnetworkProSeemergencycallmediaPPPP(String offnetworkProSeemergencycallmediaPPPP) {
        this.offnetworkProSeemergencycallmediaPPPP = offnetworkProSeemergencycallmediaPPPP;
    }

    public String getOffnetworkProSeimminentperilcallmediaPPPP() {
        return offnetworkProSeimminentperilcallmediaPPPP;
    }

    public void setOffnetworkProSeimminentperilcallmediaPPPP(String offnetworkProSeimminentperilcallmediaPPPP) {
        this.offnetworkProSeimminentperilcallmediaPPPP = offnetworkProSeimminentperilcallmediaPPPP;
    }

    public String getOnnetworkmaxparticipantcount() {
        return onnetworkmaxparticipantcount;
    }

    public void setOnnetworkmaxparticipantcount(String onnetworkmaxparticipantcount) {
        this.onnetworkmaxparticipantcount = onnetworkmaxparticipantcount;
    }

    public Boolean getOnnetworkinvitemembers() {
        return onnetworkinvitemembers;
    }

    public void setOnnetworkinvitemembers(Boolean onnetworkinvitemembers) {
        this.onnetworkinvitemembers = onnetworkinvitemembers;
    }

    public EncodingsType getPreferredvoiceencodings() {
        return preferredvoiceencodings;
    }

    public void setPreferredvoiceencodings(EncodingsType preferredvoiceencodings) {
        this.preferredvoiceencodings = preferredvoiceencodings;
    }

    public Duration getOnnetworkinprogressemergencystatecancellationtimeout() {
        return onnetworkinprogressemergencystatecancellationtimeout;
    }

    public void setOnnetworkinprogressemergencystatecancellationtimeout(Duration onnetworkinprogressemergencystatecancellationtimeout) {
        this.onnetworkinprogressemergencystatecancellationtimeout = onnetworkinprogressemergencystatecancellationtimeout;
    }

    public Duration getOnnetworkinprogressimminentperilstatecancellationtimeout() {
        return onnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public void setOnnetworkinprogressimminentperilstatecancellationtimeout(Duration onnetworkinprogressimminentperilstatecancellationtimeout) {
        this.onnetworkinprogressimminentperilstatecancellationtimeout = onnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public Duration getOffnetworkinprogressemergencystatecancellationtimeout() {
        return offnetworkinprogressemergencystatecancellationtimeout;
    }

    public void setOffnetworkinprogressemergencystatecancellationtimeout(Duration offnetworkinprogressemergencystatecancellationtimeout) {
        this.offnetworkinprogressemergencystatecancellationtimeout = offnetworkinprogressemergencystatecancellationtimeout;
    }

    public Duration getOffnetworkinprogressimminentperilstatecancellationtimeout() {
        return offnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public void setOffnetworkinprogressimminentperilstatecancellationtimeout(Duration offnetworkinprogressimminentperilstatecancellationtimeout) {
        this.offnetworkinprogressimminentperilstatecancellationtimeout = offnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public Duration getOnnetworkhangtimer() {
        return onnetworkhangtimer;
    }

    public void setOnnetworkhangtimer(Duration onnetworkhangtimer) {
        this.onnetworkhangtimer = onnetworkhangtimer;
    }

    public Duration getOnnetworkmaximumduration() {
        return onnetworkmaximumduration;
    }

    public void setOnnetworkmaximumduration(Duration onnetworkmaximumduration) {
        this.onnetworkmaximumduration = onnetworkmaximumduration;
    }

    public Duration getOffnetworkhangtimer() {
        return offnetworkhangtimer;
    }

    public void setOffnetworkhangtimer(Duration offnetworkhangtimer) {
        this.offnetworkhangtimer = offnetworkhangtimer;
    }

    public Duration getOffnetworkmaximumduration() {
        return offnetworkmaximumduration;
    }

    public void setOffnetworkmaximumduration(Duration offnetworkmaximumduration) {
        this.offnetworkmaximumduration = offnetworkmaximumduration;
    }

    public Integer getOnnetworkminimumnumbertostart() {
        return onnetworkminimumnumbertostart;
    }

    public void setOnnetworkminimumnumbertostart(Integer onnetworkminimumnumbertostart) {
        this.onnetworkminimumnumbertostart = onnetworkminimumnumbertostart;
    }

    public Duration getOnnetworktimeoutforacknowledgementofrequiredmembers() {
        return onnetworktimeoutforacknowledgementofrequiredmembers;
    }

    public void setOnnetworktimeoutforacknowledgementofrequiredmembers(Duration onnetworktimeoutforacknowledgementofrequiredmembers) {
        this.onnetworktimeoutforacknowledgementofrequiredmembers = onnetworktimeoutforacknowledgementofrequiredmembers;
    }

    public String getOnnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers() {
        return onnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers;
    }

    public void setOnnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers(String onnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers) {
        this.onnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers = onnetworkactionuponexpirationoftimeoutforacknowledgementofrequiredmembers;
    }

    public Boolean getProtectmedia() {
        return protectmedia;
    }

    public void setProtectmedia(Boolean protectmedia) {
        this.protectmedia = protectmedia;
    }

    public Boolean getProtectfloorcontrolsignalling() {
        return protectfloorcontrolsignalling;
    }

    public void setProtectfloorcontrolsignalling(Boolean protectfloorcontrolsignalling) {
        this.protectfloorcontrolsignalling = protectfloorcontrolsignalling;
    }

    public EmptyType getRequiremulticastfloorcontrolsignalling() {
        return requiremulticastfloorcontrolsignalling;
    }

    public void setRequiremulticastfloorcontrolsignalling(EmptyType requiremulticastfloorcontrolsignalling) {
        this.requiremulticastfloorcontrolsignalling = requiremulticastfloorcontrolsignalling;
    }

    public Boolean getOffnetworkqueueusage() {
        return offnetworkqueueusage;
    }

    public void setOffnetworkqueueusage(Boolean offnetworkqueueusage) {
        this.offnetworkqueueusage = offnetworkqueueusage;
    }

    public Boolean getMcpttonnetworkaudiocutin() {
        return mcpttonnetworkaudiocutin;
    }

    public void setMcpttonnetworkaudiocutin(Boolean mcpttonnetworkaudiocutin) {
        this.mcpttonnetworkaudiocutin = mcpttonnetworkaudiocutin;
    }

    public Boolean getMcvideoonnetworkinvitemembers() {
        return mcvideoonnetworkinvitemembers;
    }

    public void setMcvideoonnetworkinvitemembers(Boolean mcvideoonnetworkinvitemembers) {
        this.mcvideoonnetworkinvitemembers = mcvideoonnetworkinvitemembers;
    }

    public Duration getMcvideoonnetworkmaximumduration() {
        return mcvideoonnetworkmaximumduration;
    }

    public void setMcvideoonnetworkmaximumduration(Duration mcvideoonnetworkmaximumduration) {
        this.mcvideoonnetworkmaximumduration = mcvideoonnetworkmaximumduration;
    }

    public Boolean getMcvideoprotectmedia() {
        return mcvideoprotectmedia;
    }

    public void setMcvideoprotectmedia(Boolean mcvideoprotectmedia) {
        this.mcvideoprotectmedia = mcvideoprotectmedia;
    }

    public Boolean getMcvideoprotecttransmissioncontrol() {
        return mcvideoprotecttransmissioncontrol;
    }

    public void setMcvideoprotecttransmissioncontrol(Boolean mcvideoprotecttransmissioncontrol) {
        this.mcvideoprotecttransmissioncontrol = mcvideoprotecttransmissioncontrol;
    }

    public EncodingType getMcvideopreferredaudioencodings() {
        return mcvideopreferredaudioencodings;
    }

    public void setMcvideopreferredaudioencodings(EncodingType mcvideopreferredaudioencodings) {
        this.mcvideopreferredaudioencodings = mcvideopreferredaudioencodings;
    }

    public EncodingType getMcvideopreferredvideoencodings() {
        return mcvideopreferredvideoencodings;
    }

    public void setMcvideopreferredvideoencodings(EncodingType mcvideopreferredvideoencodings) {
        this.mcvideopreferredvideoencodings = mcvideopreferredvideoencodings;
    }

    public String getMcvideopreferredvideoresolutions() {
        return mcvideopreferredvideoresolutions;
    }

    public void setMcvideopreferredvideoresolutions(String mcvideopreferredvideoresolutions) {
        this.mcvideopreferredvideoresolutions = mcvideopreferredvideoresolutions;
    }

    public String getMcvideopreferredvideoframerate() {
        return mcvideopreferredvideoframerate;
    }

    public void setMcvideopreferredvideoframerate(String mcvideopreferredvideoframerate) {
        this.mcvideopreferredvideoframerate = mcvideopreferredvideoframerate;
    }

    public Boolean getMcvideourgentrealtimevideomode() {
        return mcvideourgentrealtimevideomode;
    }

    public void setMcvideourgentrealtimevideomode(Boolean mcvideourgentrealtimevideomode) {
        this.mcvideourgentrealtimevideomode = mcvideourgentrealtimevideomode;
    }

    public Boolean getMcvideononurgentrealtimevideomode() {
        return mcvideononurgentrealtimevideomode;
    }

    public void setMcvideononurgentrealtimevideomode(Boolean mcvideononurgentrealtimevideomode) {
        this.mcvideononurgentrealtimevideomode = mcvideononurgentrealtimevideomode;
    }

    public Boolean getMcvideononrealtimevideomode() {
        return mcvideononrealtimevideomode;
    }

    public void setMcvideononrealtimevideomode(Boolean mcvideononrealtimevideomode) {
        this.mcvideononrealtimevideomode = mcvideononrealtimevideomode;
    }

    public String getMcvideoactiverealtimevideomode() {
        return mcvideoactiverealtimevideomode;
    }

    public void setMcvideoactiverealtimevideomode(String mcvideoactiverealtimevideomode) {
        this.mcvideoactiverealtimevideomode = mcvideoactiverealtimevideomode;
    }

    public Integer getMcvideomaximumsimultaneousmcvideotransmittinggroupmembers() {
        return mcvideomaximumsimultaneousmcvideotransmittinggroupmembers;
    }

    public void setMcvideomaximumsimultaneousmcvideotransmittinggroupmembers(Integer mcvideomaximumsimultaneousmcvideotransmittinggroupmembers) {
        this.mcvideomaximumsimultaneousmcvideotransmittinggroupmembers = mcvideomaximumsimultaneousmcvideotransmittinggroupmembers;
    }

    public Integer getMcvideoonnetworkminimumnumbertostart() {
        return mcvideoonnetworkminimumnumbertostart;
    }

    public void setMcvideoonnetworkminimumnumbertostart(Integer mcvideoonnetworkminimumnumbertostart) {
        this.mcvideoonnetworkminimumnumbertostart = mcvideoonnetworkminimumnumbertostart;
    }

    public Integer getMcvideoonnetworkgrouppriority() {
        return mcvideoonnetworkgrouppriority;
    }

    public void setMcvideoonnetworkgrouppriority(Integer mcvideoonnetworkgrouppriority) {
        this.mcvideoonnetworkgrouppriority = mcvideoonnetworkgrouppriority;
    }

    public String getMcvideooffnetworkarbitrationapproach() {
        return mcvideooffnetworkarbitrationapproach;
    }

    public void setMcvideooffnetworkarbitrationapproach(String mcvideooffnetworkarbitrationapproach) {
        this.mcvideooffnetworkarbitrationapproach = mcvideooffnetworkarbitrationapproach;
    }

    public Integer getMcvideooffnetworkmaximumsimultaneoustransmissions() {
        return mcvideooffnetworkmaximumsimultaneoustransmissions;
    }

    public void setMcvideooffnetworkmaximumsimultaneoustransmissions(Integer mcvideooffnetworkmaximumsimultaneoustransmissions) {
        this.mcvideooffnetworkmaximumsimultaneoustransmissions = mcvideooffnetworkmaximumsimultaneoustransmissions;
    }

    public String getMcvideooffnetworkProSesignallingPPPP() {
        return mcvideooffnetworkProSesignallingPPPP;
    }

    public void setMcvideooffnetworkProSesignallingPPPP(String mcvideooffnetworkProSesignallingPPPP) {
        this.mcvideooffnetworkProSesignallingPPPP = mcvideooffnetworkProSesignallingPPPP;
    }

    public String getMcvideooffnetworkProSeemergencycallsignallingPPPP() {
        return mcvideooffnetworkProSeemergencycallsignallingPPPP;
    }

    public void setMcvideooffnetworkProSeemergencycallsignallingPPPP(String mcvideooffnetworkProSeemergencycallsignallingPPPP) {
        this.mcvideooffnetworkProSeemergencycallsignallingPPPP = mcvideooffnetworkProSeemergencycallsignallingPPPP;
    }

    public String getMcvideooffnetworkProSeimminentperilcallsignallingPPPP() {
        return mcvideooffnetworkProSeimminentperilcallsignallingPPPP;
    }

    public void setMcvideooffnetworkProSeimminentperilcallsignallingPPPP(String mcvideooffnetworkProSeimminentperilcallsignallingPPPP) {
        this.mcvideooffnetworkProSeimminentperilcallsignallingPPPP = mcvideooffnetworkProSeimminentperilcallsignallingPPPP;
    }

    public String getMcvideooffnetworkProSemediaPPPP() {
        return mcvideooffnetworkProSemediaPPPP;
    }

    public void setMcvideooffnetworkProSemediaPPPP(String mcvideooffnetworkProSemediaPPPP) {
        this.mcvideooffnetworkProSemediaPPPP = mcvideooffnetworkProSemediaPPPP;
    }

    public String getMcvideooffnetworkProSeemergencycallmediaPPPP() {
        return mcvideooffnetworkProSeemergencycallmediaPPPP;
    }

    public void setMcvideooffnetworkProSeemergencycallmediaPPPP(String mcvideooffnetworkProSeemergencycallmediaPPPP) {
        this.mcvideooffnetworkProSeemergencycallmediaPPPP = mcvideooffnetworkProSeemergencycallmediaPPPP;
    }

    public String getMcvideooffnetworkProSeimminentperilcallmediaPPPP() {
        return mcvideooffnetworkProSeimminentperilcallmediaPPPP;
    }

    public void setMcvideooffnetworkProSeimminentperilcallmediaPPPP(String mcvideooffnetworkProSeimminentperilcallmediaPPPP) {
        this.mcvideooffnetworkProSeimminentperilcallmediaPPPP = mcvideooffnetworkProSeimminentperilcallmediaPPPP;
    }

    public Duration getMcvideooffnetworkinprogressemergencystatecancellationtimeout() {
        return mcvideooffnetworkinprogressemergencystatecancellationtimeout;
    }

    public void setMcvideooffnetworkinprogressemergencystatecancellationtimeout(Duration mcvideooffnetworkinprogressemergencystatecancellationtimeout) {
        this.mcvideooffnetworkinprogressemergencystatecancellationtimeout = mcvideooffnetworkinprogressemergencystatecancellationtimeout;
    }

    public Duration getMcvideooffnetworkinprogressimminentperilstatecancellationtimeout() {
        return mcvideooffnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public void setMcvideooffnetworkinprogressimminentperilstatecancellationtimeout(Duration mcvideooffnetworkinprogressimminentperilstatecancellationtimeout) {
        this.mcvideooffnetworkinprogressimminentperilstatecancellationtimeout = mcvideooffnetworkinprogressimminentperilstatecancellationtimeout;
    }

    public Duration getMcvideooffnetworkmaximumduration() {
        return mcvideooffnetworkmaximumduration;
    }

    public void setMcvideooffnetworkmaximumduration(Duration mcvideooffnetworkmaximumduration) {
        this.mcvideooffnetworkmaximumduration = mcvideooffnetworkmaximumduration;
    }

    public Boolean getMcdataprotectmedia() {
        return mcdataprotectmedia;
    }

    public void setMcdataprotectmedia(Boolean mcdataprotectmedia) {
        this.mcdataprotectmedia = mcdataprotectmedia;
    }

    public Boolean getMcdataprotecttransmissioncontrol() {
        return mcdataprotecttransmissioncontrol;
    }

    public void setMcdataprotecttransmissioncontrol(Boolean mcdataprotecttransmissioncontrol) {
        this.mcdataprotecttransmissioncontrol = mcdataprotecttransmissioncontrol;
    }

    public Boolean getMcdataallowshortdataservice() {
        return mcdataallowshortdataservice;
    }

    public void setMcdataallowshortdataservice(Boolean mcdataallowshortdataservice) {
        this.mcdataallowshortdataservice = mcdataallowshortdataservice;
    }

    public Boolean getMcdataallowfiledistribution() {
        return mcdataallowfiledistribution;
    }

    public void setMcdataallowfiledistribution(Boolean mcdataallowfiledistribution) {
        this.mcdataallowfiledistribution = mcdataallowfiledistribution;
    }

    public Boolean getMcdataallowconversationmanagement() {
        return mcdataallowconversationmanagement;
    }

    public void setMcdataallowconversationmanagement(Boolean mcdataallowconversationmanagement) {
        this.mcdataallowconversationmanagement = mcdataallowconversationmanagement;
    }

    public Boolean getMcdataallowtxcontrol() {
        return mcdataallowtxcontrol;
    }

    public void setMcdataallowtxcontrol(Boolean mcdataallowtxcontrol) {
        this.mcdataallowtxcontrol = mcdataallowtxcontrol;
    }

    public Boolean getMcdataallowrxcontrol() {
        return mcdataallowrxcontrol;
    }

    public void setMcdataallowrxcontrol(Boolean mcdataallowrxcontrol) {
        this.mcdataallowrxcontrol = mcdataallowrxcontrol;
    }

    public Boolean getMcdataallowenhancedstatus() {
        return mcdataallowenhancedstatus;
    }

    public void setMcdataallowenhancedstatus(Boolean mcdataallowenhancedstatus) {
        this.mcdataallowenhancedstatus = mcdataallowenhancedstatus;
    }

    public String getMcdataenhancedstatusoperationalvalues() {
        return mcdataenhancedstatusoperationalvalues;
    }

    public void setMcdataenhancedstatusoperationalvalues(String mcdataenhancedstatusoperationalvalues) {
        this.mcdataenhancedstatusoperationalvalues = mcdataenhancedstatusoperationalvalues;
    }

    public Integer getMcdataonnetworkmaxdatasizeforSDS() {
        return mcdataonnetworkmaxdatasizeforSDS;
    }

    public void setMcdataonnetworkmaxdatasizeforSDS(Integer mcdataonnetworkmaxdatasizeforSDS) {
        this.mcdataonnetworkmaxdatasizeforSDS = mcdataonnetworkmaxdatasizeforSDS;
    }

    public Integer getMcdataonnetworkmaxdatasizeforFD() {
        return mcdataonnetworkmaxdatasizeforFD;
    }

    public void setMcdataonnetworkmaxdatasizeforFD(Integer mcdataonnetworkmaxdatasizeforFD) {
        this.mcdataonnetworkmaxdatasizeforFD = mcdataonnetworkmaxdatasizeforFD;
    }

    public Integer getMcdataonnetworkmaxdatasizeautorecv() {
        return mcdataonnetworkmaxdatasizeautorecv;
    }

    public void setMcdataonnetworkmaxdatasizeautorecv(Integer mcdataonnetworkmaxdatasizeautorecv) {
        this.mcdataonnetworkmaxdatasizeautorecv = mcdataonnetworkmaxdatasizeautorecv;
    }

    public Integer getMcdataonnetworkgrouppriority() {
        return mcdataonnetworkgrouppriority;
    }

    public void setMcdataonnetworkgrouppriority(Integer mcdataonnetworkgrouppriority) {
        this.mcdataonnetworkgrouppriority = mcdataonnetworkgrouppriority;
    }

    public String getMcdataoffnetworkProSesignallingPPPP() {
        return mcdataoffnetworkProSesignallingPPPP;
    }

    public void setMcdataoffnetworkProSesignallingPPPP(String mcdataoffnetworkProSesignallingPPPP) {
        this.mcdataoffnetworkProSesignallingPPPP = mcdataoffnetworkProSesignallingPPPP;
    }

    public String getMcdataoffnetworkProSemediaPPPP() {
        return mcdataoffnetworkProSemediaPPPP;
    }

    public void setMcdataoffnetworkProSemediaPPPP(String mcdataoffnetworkProSemediaPPPP) {
        this.mcdataoffnetworkProSemediaPPPP = mcdataoffnetworkProSemediaPPPP;
    }
}
