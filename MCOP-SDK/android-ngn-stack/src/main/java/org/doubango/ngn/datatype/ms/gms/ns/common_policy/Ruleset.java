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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
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
 *       &lt;sequence>
 *         &lt;element name="rule" type="{urn:ietf:params:xml:ns:common-policy}ruleType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@Root(strict=false, name = "ruleset")
public class Ruleset {
    @ElementList(required=false,inline=true,entry = "rule")
    protected List<RuleType> rule;



    @Element(required = false , name ="allow-presence-status" )
    protected boolean allowpresencestatus;
    @Element(required = false , name ="allow-request-presence" )
    protected boolean allowrequestpresence;
    @Element(required = false , name ="allow-query-availability-for-private-calls" )
    protected boolean allowqueryavailabilityforprivatecalls;
    @Element(required = false , name ="allow-enable-disable-user" )
    protected boolean allowenabledisableuser;
    @Element(required = false , name ="allow-enable-disable-UE" )
    protected boolean allowenabledisableUE;
    @Element(required = false , name ="allow-create-delete-user-alias" )
    protected boolean allowcreatedeleteuseralias;
    @Element(required = false , name ="allow-private-call" )
    protected boolean allowprivatecall;
    @Element(required = false , name ="allow-manual-commencement" )
    protected boolean allowmanualcommencement;
    @Element(required = false , name ="allow-automatic-commencement" )
    protected boolean allowautomaticcommencement;
    @Element(required = false , name ="allow-force-auto-answer" )
    protected boolean allowforceautoanswer;
    @Element(required = false , name ="allow-failure-restriction" )
    protected boolean allowfailurerestriction;
    @Element(required = false , name ="allow-emergency-group-call" )
    protected boolean allowemergencygroupcall;
    @Element(required = false , name ="allow-emergency-private-call" )
    protected boolean allowemergencyprivatecall;
    @Element(required = false , name ="allow-cancel-group-emergency" )
    protected boolean allowcancelgroupemergency;
    @Element(required = false , name ="allow-cancel-private-emergency-call" )
    protected boolean allowcancelprivateemergencycall;
    @Element(required = false , name ="allow-imminent-peril-call" )
    protected boolean allowimminentperilcall;
    @Element(required = false , name ="allow-cancel-imminent-peril" )
    protected boolean allowcancelimminentperil;
    @Element(required = false , name ="allow-activate-emergency-alert" )
    protected boolean allowactivateemergencyalert;
    @Element(required = false , name ="allow-cancel-emergency-alert" )
    protected boolean allowcancelemergencyalert;
    @Element(required = false , name ="allow-offnetwork" )
    protected boolean allowoffnetwork;
    @Element(required = false , name ="allow-imminent-peril-change" )
    protected boolean allowimminentperilchange;
    @Element(required = false , name ="allow-private-call-media-protection" )
    protected boolean allowprivatecallmediaprotection;
    @Element(required = false , name ="allow-private-call-floor-control-protection" )
    protected boolean allowprivatecallfloorcontrolprotection;
    @Element(required = false , name ="allow-request-affiliated-groups" )
    protected boolean allowrequestaffiliatedgroups;
    @Element(required = false , name ="allow-request-to-affiliate-other-users" )
    protected boolean allowrequesttoaffiliateotherusers;
    @Element(required = false , name ="allow-recommend-to-affiliate-other-users" )
    protected boolean allowrecommendtoaffiliateotherusers;
    @Element(required = false , name ="allow-private-call-to-any-user" )
    protected boolean allowprivatecalltoanyuser;
    @Element(required = false , name ="allow-regroup" )
    protected boolean allowregroup;
    @Element(required = false , name ="allow-private-call-participation" )
    protected boolean allowprivatecallparticipation;
    @Element(required = false , name ="allow-override-of-transmission" )
    protected boolean allowoverrideoftransmission;
    @Element(required = false , name ="allow-manual-off-network-switch" )
    protected boolean allowmanualoffnetworkswitch;
    @Element(required = false , name ="allow-listen-both-overriding-and-overridden" )
    protected boolean allowlistenbothoverridingandoverridden;
    @Element(required = false , name ="allow-transmit-during-override" )
    protected boolean allowtransmitduringoverride;
    @Element(required = false , name ="allow-off-network-group-call-change-to-emergency" )
    protected boolean allowoffnetworkgroupcallchangetoemergency;
    @Element(required = false , name ="allow-revoke-transmit" )
    protected boolean allowrevoketransmit;
    @Element(required = false , name ="allow-create-group-broadcast-group" )
    protected boolean allowcreategroupbroadcastgroup;
    @Element(required = false , name ="allow-create-user-broadcast-group" )
    protected boolean allowcreateuserbroadcastgroup;
    @Element(required = false , name ="allow-request-private-call-call-back" )
    protected boolean allowrequestprivatecallcallback;
    @Element(required = false , name ="allow-cancel-private-call-call-back" )
    protected boolean allowcancelprivatecallcallback;
    @Element(required = false , name ="allow-request-remote-initiated-ambient-listening" )
    protected boolean allowrequestremoteinitiatedambientlistening;
    @Element(required = false , name ="allow-request-locally-initiated-ambient-listening" )
    protected boolean allowrequestlocallyinitiatedambientlistening;
    @Element(required = false , name ="allow-request-first-to-answer-call" )
    protected boolean allowrequestfirsttoanswercall;


    /**
     * Gets the value of the rule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RuleType }
     * 
     * 
     */
    public List<RuleType> getRule() {
        return this.rule;
    }

    public void setRule(List<RuleType> rule) {
        this.rule = rule;
    }

    public boolean isAllowpresencestatus() {
        return allowpresencestatus;
    }

    public void setAllowpresencestatus(boolean allowpresencestatus) {
        this.allowpresencestatus = allowpresencestatus;
    }

    public boolean isAllowrequestpresence() {
        return allowrequestpresence;
    }

    public void setAllowrequestpresence(boolean allowrequestpresence) {
        this.allowrequestpresence = allowrequestpresence;
    }

    public boolean isAllowqueryavailabilityforprivatecalls() {
        return allowqueryavailabilityforprivatecalls;
    }

    public void setAllowqueryavailabilityforprivatecalls(boolean allowqueryavailabilityforprivatecalls) {
        this.allowqueryavailabilityforprivatecalls = allowqueryavailabilityforprivatecalls;
    }

    public boolean isAllowenabledisableuser() {
        return allowenabledisableuser;
    }

    public void setAllowenabledisableuser(boolean allowenabledisableuser) {
        this.allowenabledisableuser = allowenabledisableuser;
    }

    public boolean isAllowenabledisableUE() {
        return allowenabledisableUE;
    }

    public void setAllowenabledisableUE(boolean allowenabledisableUE) {
        this.allowenabledisableUE = allowenabledisableUE;
    }

    public boolean isAllowcreatedeleteuseralias() {
        return allowcreatedeleteuseralias;
    }

    public void setAllowcreatedeleteuseralias(boolean allowcreatedeleteuseralias) {
        this.allowcreatedeleteuseralias = allowcreatedeleteuseralias;
    }

    public boolean isAllowprivatecall() {
        return allowprivatecall;
    }

    public void setAllowprivatecall(boolean allowprivatecall) {
        this.allowprivatecall = allowprivatecall;
    }

    public boolean isAllowmanualcommencement() {
        return allowmanualcommencement;
    }

    public void setAllowmanualcommencement(boolean allowmanualcommencement) {
        this.allowmanualcommencement = allowmanualcommencement;
    }

    public boolean isAllowautomaticcommencement() {
        return allowautomaticcommencement;
    }

    public void setAllowautomaticcommencement(boolean allowautomaticcommencement) {
        this.allowautomaticcommencement = allowautomaticcommencement;
    }

    public boolean isAllowforceautoanswer() {
        return allowforceautoanswer;
    }

    public void setAllowforceautoanswer(boolean allowforceautoanswer) {
        this.allowforceautoanswer = allowforceautoanswer;
    }

    public boolean isAllowfailurerestriction() {
        return allowfailurerestriction;
    }

    public void setAllowfailurerestriction(boolean allowfailurerestriction) {
        this.allowfailurerestriction = allowfailurerestriction;
    }

    public boolean isAllowemergencygroupcall() {
        return allowemergencygroupcall;
    }

    public void setAllowemergencygroupcall(boolean allowemergencygroupcall) {
        this.allowemergencygroupcall = allowemergencygroupcall;
    }

    public boolean isAllowemergencyprivatecall() {
        return allowemergencyprivatecall;
    }

    public void setAllowemergencyprivatecall(boolean allowemergencyprivatecall) {
        this.allowemergencyprivatecall = allowemergencyprivatecall;
    }

    public boolean isAllowcancelgroupemergency() {
        return allowcancelgroupemergency;
    }

    public void setAllowcancelgroupemergency(boolean allowcancelgroupemergency) {
        this.allowcancelgroupemergency = allowcancelgroupemergency;
    }

    public boolean isAllowcancelprivateemergencycall() {
        return allowcancelprivateemergencycall;
    }

    public void setAllowcancelprivateemergencycall(boolean allowcancelprivateemergencycall) {
        this.allowcancelprivateemergencycall = allowcancelprivateemergencycall;
    }

    public boolean isAllowimminentperilcall() {
        return allowimminentperilcall;
    }

    public void setAllowimminentperilcall(boolean allowimminentperilcall) {
        this.allowimminentperilcall = allowimminentperilcall;
    }

    public boolean isAllowcancelimminentperil() {
        return allowcancelimminentperil;
    }

    public void setAllowcancelimminentperil(boolean allowcancelimminentperil) {
        this.allowcancelimminentperil = allowcancelimminentperil;
    }

    public boolean isAllowactivateemergencyalert() {
        return allowactivateemergencyalert;
    }

    public void setAllowactivateemergencyalert(boolean allowactivateemergencyalert) {
        this.allowactivateemergencyalert = allowactivateemergencyalert;
    }

    public boolean isAllowcancelemergencyalert() {
        return allowcancelemergencyalert;
    }

    public void setAllowcancelemergencyalert(boolean allowcancelemergencyalert) {
        this.allowcancelemergencyalert = allowcancelemergencyalert;
    }

    public boolean isAllowoffnetwork() {
        return allowoffnetwork;
    }

    public void setAllowoffnetwork(boolean allowoffnetwork) {
        this.allowoffnetwork = allowoffnetwork;
    }

    public boolean isAllowimminentperilchange() {
        return allowimminentperilchange;
    }

    public void setAllowimminentperilchange(boolean allowimminentperilchange) {
        this.allowimminentperilchange = allowimminentperilchange;
    }

    public boolean isAllowprivatecallmediaprotection() {
        return allowprivatecallmediaprotection;
    }

    public void setAllowprivatecallmediaprotection(boolean allowprivatecallmediaprotection) {
        this.allowprivatecallmediaprotection = allowprivatecallmediaprotection;
    }

    public boolean isAllowprivatecallfloorcontrolprotection() {
        return allowprivatecallfloorcontrolprotection;
    }

    public void setAllowprivatecallfloorcontrolprotection(boolean allowprivatecallfloorcontrolprotection) {
        this.allowprivatecallfloorcontrolprotection = allowprivatecallfloorcontrolprotection;
    }

    public boolean isAllowrequestaffiliatedgroups() {
        return allowrequestaffiliatedgroups;
    }

    public void setAllowrequestaffiliatedgroups(boolean allowrequestaffiliatedgroups) {
        this.allowrequestaffiliatedgroups = allowrequestaffiliatedgroups;
    }

    public boolean isAllowrequesttoaffiliateotherusers() {
        return allowrequesttoaffiliateotherusers;
    }

    public void setAllowrequesttoaffiliateotherusers(boolean allowrequesttoaffiliateotherusers) {
        this.allowrequesttoaffiliateotherusers = allowrequesttoaffiliateotherusers;
    }

    public boolean isAllowrecommendtoaffiliateotherusers() {
        return allowrecommendtoaffiliateotherusers;
    }

    public void setAllowrecommendtoaffiliateotherusers(boolean allowrecommendtoaffiliateotherusers) {
        this.allowrecommendtoaffiliateotherusers = allowrecommendtoaffiliateotherusers;
    }

    public boolean isAllowprivatecalltoanyuser() {
        return allowprivatecalltoanyuser;
    }

    public void setAllowprivatecalltoanyuser(boolean allowprivatecalltoanyuser) {
        this.allowprivatecalltoanyuser = allowprivatecalltoanyuser;
    }

    public boolean isAllowregroup() {
        return allowregroup;
    }

    public void setAllowregroup(boolean allowregroup) {
        this.allowregroup = allowregroup;
    }

    public boolean isAllowprivatecallparticipation() {
        return allowprivatecallparticipation;
    }

    public void setAllowprivatecallparticipation(boolean allowprivatecallparticipation) {
        this.allowprivatecallparticipation = allowprivatecallparticipation;
    }

    public boolean isAllowoverrideoftransmission() {
        return allowoverrideoftransmission;
    }

    public void setAllowoverrideoftransmission(boolean allowoverrideoftransmission) {
        this.allowoverrideoftransmission = allowoverrideoftransmission;
    }

    public boolean isAllowmanualoffnetworkswitch() {
        return allowmanualoffnetworkswitch;
    }

    public void setAllowmanualoffnetworkswitch(boolean allowmanualoffnetworkswitch) {
        this.allowmanualoffnetworkswitch = allowmanualoffnetworkswitch;
    }

    public boolean isAllowlistenbothoverridingandoverridden() {
        return allowlistenbothoverridingandoverridden;
    }

    public void setAllowlistenbothoverridingandoverridden(boolean allowlistenbothoverridingandoverridden) {
        this.allowlistenbothoverridingandoverridden = allowlistenbothoverridingandoverridden;
    }

    public boolean isAllowtransmitduringoverride() {
        return allowtransmitduringoverride;
    }

    public void setAllowtransmitduringoverride(boolean allowtransmitduringoverride) {
        this.allowtransmitduringoverride = allowtransmitduringoverride;
    }

    public boolean isAllowoffnetworkgroupcallchangetoemergency() {
        return allowoffnetworkgroupcallchangetoemergency;
    }

    public void setAllowoffnetworkgroupcallchangetoemergency(boolean allowoffnetworkgroupcallchangetoemergency) {
        this.allowoffnetworkgroupcallchangetoemergency = allowoffnetworkgroupcallchangetoemergency;
    }

    public boolean isAllowrevoketransmit() {
        return allowrevoketransmit;
    }

    public void setAllowrevoketransmit(boolean allowrevoketransmit) {
        this.allowrevoketransmit = allowrevoketransmit;
    }

    public boolean isAllowcreategroupbroadcastgroup() {
        return allowcreategroupbroadcastgroup;
    }

    public void setAllowcreategroupbroadcastgroup(boolean allowcreategroupbroadcastgroup) {
        this.allowcreategroupbroadcastgroup = allowcreategroupbroadcastgroup;
    }

    public boolean isAllowcreateuserbroadcastgroup() {
        return allowcreateuserbroadcastgroup;
    }

    public void setAllowcreateuserbroadcastgroup(boolean allowcreateuserbroadcastgroup) {
        this.allowcreateuserbroadcastgroup = allowcreateuserbroadcastgroup;
    }

    public boolean isAllowrequestprivatecallcallback() {
        return allowrequestprivatecallcallback;
    }

    public void setAllowrequestprivatecallcallback(boolean allowrequestprivatecallcallback) {
        this.allowrequestprivatecallcallback = allowrequestprivatecallcallback;
    }

    public boolean isAllowcancelprivatecallcallback() {
        return allowcancelprivatecallcallback;
    }

    public void setAllowcancelprivatecallcallback(boolean allowcancelprivatecallcallback) {
        this.allowcancelprivatecallcallback = allowcancelprivatecallcallback;
    }

    public boolean isAllowrequestremoteinitiatedambientlistening() {
        return allowrequestremoteinitiatedambientlistening;
    }

    public void setAllowrequestremoteinitiatedambientlistening(boolean allowrequestremoteinitiatedambientlistening) {
        this.allowrequestremoteinitiatedambientlistening = allowrequestremoteinitiatedambientlistening;
    }

    public boolean isAllowrequestlocallyinitiatedambientlistening() {
        return allowrequestlocallyinitiatedambientlistening;
    }

    public void setAllowrequestlocallyinitiatedambientlistening(boolean allowrequestlocallyinitiatedambientlistening) {
        this.allowrequestlocallyinitiatedambientlistening = allowrequestlocallyinitiatedambientlistening;
    }

    public boolean isAllowrequestfirsttoanswercall() {
        return allowrequestfirsttoanswercall;
    }

    public void setAllowrequestfirsttoanswercall(boolean allowrequestfirsttoanswercall) {
        this.allowrequestfirsttoanswercall = allowrequestfirsttoanswercall;
    }
}
