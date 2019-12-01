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




package org.doubango.ngn.datatype.ms.cms.mcpttUserProfile;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "extensibleType")
public class ExtensibleType {

    @Element(required = false , name ="allow-presence-status" )
    protected Boolean allowpresencestatus;
    @Element(required = false , name ="allow-request-presence" )
    protected Boolean allowrequestpresence;
    @Element(required = false , name ="allow-query-availability-for-private-calls" )
    protected Boolean allowqueryavailabilityforprivatecalls;
    @Element(required = false , name ="allow-enable-disable-user" )
    protected Boolean allowenabledisableuser;
    @Element(required = false , name ="allow-enable-disable-UE" )
    protected Boolean allowenabledisableUE;
    @Element(required = false , name ="allow-create-delete-user-alias" )
    protected Boolean allowcreatedeleteuseralias;
    @Element(required = false , name ="allow-private-call" )
    protected Boolean allowprivatecall;
    @Element(required = false , name ="allow-manual-commencement" )
    protected Boolean allowmanualcommencement;
    @Element(required = false , name ="allow-automatic-commencement" )
    protected Boolean allowautomaticcommencement;
    @Element(required = false , name ="allow-force-auto-answer" )
    protected Boolean allowforceautoanswer;
    @Element(required = false , name ="allow-failure-restriction" )
    protected Boolean allowfailurerestriction;
    @Element(required = false , name ="allow-emergency-group-call" )
    protected Boolean allowemergencygroupcall;
    @Element(required = false , name ="allow-emergency-private-call" )
    protected Boolean allowemergencyprivatecall;
    @Element(required = false , name ="allow-cancel-group-emergency" )
    protected Boolean allowcancelgroupemergency;
    @Element(required = false , name ="allow-cancel-private-emergency-call" )
    protected Boolean allowcancelprivateemergencycall;
    @Element(required = false , name ="allow-imminent-peril-call" )
    protected Boolean allowimminentperilcall;
    @Element(required = false , name ="allow-cancel-imminent-peril" )
    protected Boolean allowcancelimminentperil;
    @Element(required = false , name ="allow-activate-emergency-alert" )
    protected Boolean allowactivateemergencyalert;
    @Element(required = false , name ="allow-cancel-emergency-alert" )
    protected Boolean allowcancelemergencyalert;
    @Element(required = false , name ="allow-offnetwork" )
    protected Boolean allowoffnetwork;
    @Element(required = false , name ="allow-imminent-peril-change" )
    protected Boolean allowimminentperilchange;
    @Element(required = false , name ="allow-private-call-media-protection" )
    protected Boolean allowprivatecallmediaprotection;
    @Element(required = false , name ="allow-private-call-floor-control-protection" )
    protected Boolean allowprivatecallfloorcontrolprotection;
    @Element(required = false , name ="allow-request-affiliated-groups" )
    protected Boolean allowrequestaffiliatedgroups;
    @Element(required = false , name ="allow-request-to-affiliate-other-users" )
    protected Boolean allowrequesttoaffiliateotherusers;
    @Element(required = false , name ="allow-recommend-to-affiliate-other-users" )
    protected Boolean allowrecommendtoaffiliateotherusers;
    @Element(required = false , name ="allow-private-call-to-any-user" )
    protected Boolean allowprivatecalltoanyuser;
    @Element(required = false , name ="allow-regroup" )
    protected Boolean allowregroup;
    @Element(required = false , name ="allow-private-call-participation" )
    protected Boolean allowprivatecallparticipation;
    @Element(required = false , name ="allow-override-of-transmission" )
    protected Boolean allowoverrideoftransmission;
    @Element(required = false , name ="allow-manual-off-network-switch" )
    protected Boolean allowmanualoffnetworkswitch;
    @Element(required = false , name ="allow-listen-both-overriding-and-overridden" )
    protected Boolean allowlistenbothoverridingandoverridden;
    @Element(required = false , name ="allow-transmit-during-override" )
    protected Boolean allowtransmitduringoverride;
    @Element(required = false , name ="allow-off-network-group-call-change-to-emergency" )
    protected Boolean allowoffnetworkgroupcallchangetoemergency;
    @Element(required = false , name ="allow-revoke-transmit" )
    protected Boolean allowrevoketransmit;
    @Element(required = false , name ="allow-create-group-broadcast-group" )
    protected Boolean allowcreategroupbroadcastgroup;
    @Element(required = false , name ="allow-create-user-broadcast-group" )
    protected Boolean allowcreateuserbroadcastgroup;
    @Element(required = false , name ="allow-request-private-call-call-back" )
    protected Boolean allowrequestprivatecallcallback;
    @Element(required = false , name ="allow-cancel-private-call-call-back" )
    protected Boolean allowcancelprivatecallcallback;
    @Element(required = false , name ="allow-request-remote-initiated-ambient-listening" )
    protected Boolean allowrequestremoteinitiatedambientlistening;
    @Element(required = false , name ="allow-request-locally-initiated-ambient-listening" )
    protected Boolean allowrequestlocallyinitiatedambientlistening;
    @Element(required = false , name ="allow-request-first-to-answer-call" )
    protected Boolean allowrequestfirsttoanswercall;


    public Boolean isAllowpresencestatus() {
        return allowpresencestatus;
    }

    public void setAllowpresencestatus(Boolean allowpresencestatus) {
        this.allowpresencestatus = allowpresencestatus;
    }

    public Boolean isAllowrequestpresence() {
        return allowrequestpresence;
    }

    public void setAllowrequestpresence(Boolean allowrequestpresence) {
        this.allowrequestpresence = allowrequestpresence;
    }

    public Boolean isAllowqueryavailabilityforprivatecalls() {
        return allowqueryavailabilityforprivatecalls;
    }

    public void setAllowqueryavailabilityforprivatecalls(Boolean allowqueryavailabilityforprivatecalls) {
        this.allowqueryavailabilityforprivatecalls = allowqueryavailabilityforprivatecalls;
    }

    public Boolean isAllowenabledisableuser() {
        return allowenabledisableuser;
    }

    public void setAllowenabledisableuser(Boolean allowenabledisableuser) {
        this.allowenabledisableuser = allowenabledisableuser;
    }

    public Boolean isAllowenabledisableUE() {
        return allowenabledisableUE;
    }

    public void setAllowenabledisableUE(Boolean allowenabledisableUE) {
        this.allowenabledisableUE = allowenabledisableUE;
    }

    public Boolean isAllowcreatedeleteuseralias() {
        return allowcreatedeleteuseralias;
    }

    public void setAllowcreatedeleteuseralias(Boolean allowcreatedeleteuseralias) {
        this.allowcreatedeleteuseralias = allowcreatedeleteuseralias;
    }

    public Boolean isAllowprivatecall() {
        return allowprivatecall;
    }

    public void setAllowprivatecall(Boolean allowprivatecall) {
        this.allowprivatecall = allowprivatecall;
    }

    public Boolean isAllowmanualcommencement() {
        return allowmanualcommencement;
    }

    public void setAllowmanualcommencement(Boolean allowmanualcommencement) {
        this.allowmanualcommencement = allowmanualcommencement;
    }

    public Boolean isAllowautomaticcommencement() {
        return allowautomaticcommencement;
    }

    public void setAllowautomaticcommencement(Boolean allowautomaticcommencement) {
        this.allowautomaticcommencement = allowautomaticcommencement;
    }

    public Boolean isAllowforceautoanswer() {
        return allowforceautoanswer;
    }

    public void setAllowforceautoanswer(Boolean allowforceautoanswer) {
        this.allowforceautoanswer = allowforceautoanswer;
    }

    public Boolean isAllowfailurerestriction() {
        return allowfailurerestriction;
    }

    public void setAllowfailurerestriction(Boolean allowfailurerestriction) {
        this.allowfailurerestriction = allowfailurerestriction;
    }

    public Boolean isAllowemergencygroupcall() {
        return allowemergencygroupcall;
    }

    public void setAllowemergencygroupcall(Boolean allowemergencygroupcall) {
        this.allowemergencygroupcall = allowemergencygroupcall;
    }

    public Boolean isAllowemergencyprivatecall() {
        return allowemergencyprivatecall;
    }

    public void setAllowemergencyprivatecall(Boolean allowemergencyprivatecall) {
        this.allowemergencyprivatecall = allowemergencyprivatecall;
    }

    public Boolean isAllowcancelgroupemergency() {
        return allowcancelgroupemergency;
    }

    public void setAllowcancelgroupemergency(Boolean allowcancelgroupemergency) {
        this.allowcancelgroupemergency = allowcancelgroupemergency;
    }

    public Boolean isAllowcancelprivateemergencycall() {
        return allowcancelprivateemergencycall;
    }

    public void setAllowcancelprivateemergencycall(Boolean allowcancelprivateemergencycall) {
        this.allowcancelprivateemergencycall = allowcancelprivateemergencycall;
    }

    public Boolean isAllowimminentperilcall() {
        return allowimminentperilcall;
    }

    public void setAllowimminentperilcall(Boolean allowimminentperilcall) {
        this.allowimminentperilcall = allowimminentperilcall;
    }

    public Boolean isAllowcancelimminentperil() {
        return allowcancelimminentperil;
    }

    public void setAllowcancelimminentperil(Boolean allowcancelimminentperil) {
        this.allowcancelimminentperil = allowcancelimminentperil;
    }

    public Boolean isAllowactivateemergencyalert() {
        return allowactivateemergencyalert;
    }

    public void setAllowactivateemergencyalert(Boolean allowactivateemergencyalert) {
        this.allowactivateemergencyalert = allowactivateemergencyalert;
    }

    public Boolean isAllowcancelemergencyalert() {
        return allowcancelemergencyalert;
    }

    public void setAllowcancelemergencyalert(Boolean allowcancelemergencyalert) {
        this.allowcancelemergencyalert = allowcancelemergencyalert;
    }

    public Boolean isAllowoffnetwork() {
        return allowoffnetwork;
    }

    public void setAllowoffnetwork(Boolean allowoffnetwork) {
        this.allowoffnetwork = allowoffnetwork;
    }

    public Boolean isAllowimminentperilchange() {
        return allowimminentperilchange;
    }

    public void setAllowimminentperilchange(Boolean allowimminentperilchange) {
        this.allowimminentperilchange = allowimminentperilchange;
    }

    public Boolean isAllowprivatecallmediaprotection() {
        return allowprivatecallmediaprotection;
    }

    public void setAllowprivatecallmediaprotection(Boolean allowprivatecallmediaprotection) {
        this.allowprivatecallmediaprotection = allowprivatecallmediaprotection;
    }

    public Boolean isAllowprivatecallfloorcontrolprotection() {
        return allowprivatecallfloorcontrolprotection;
    }

    public void setAllowprivatecallfloorcontrolprotection(Boolean allowprivatecallfloorcontrolprotection) {
        this.allowprivatecallfloorcontrolprotection = allowprivatecallfloorcontrolprotection;
    }

    public Boolean isAllowrequestaffiliatedgroups() {
        return allowrequestaffiliatedgroups;
    }

    public void setAllowrequestaffiliatedgroups(Boolean allowrequestaffiliatedgroups) {
        this.allowrequestaffiliatedgroups = allowrequestaffiliatedgroups;
    }

    public Boolean isAllowrequesttoaffiliateotherusers() {
        return allowrequesttoaffiliateotherusers;
    }

    public void setAllowrequesttoaffiliateotherusers(Boolean allowrequesttoaffiliateotherusers) {
        this.allowrequesttoaffiliateotherusers = allowrequesttoaffiliateotherusers;
    }

    public Boolean isAllowrecommendtoaffiliateotherusers() {
        return allowrecommendtoaffiliateotherusers;
    }

    public void setAllowrecommendtoaffiliateotherusers(Boolean allowrecommendtoaffiliateotherusers) {
        this.allowrecommendtoaffiliateotherusers = allowrecommendtoaffiliateotherusers;
    }

    public Boolean isAllowprivatecalltoanyuser() {
        return allowprivatecalltoanyuser;
    }

    public void setAllowprivatecalltoanyuser(Boolean allowprivatecalltoanyuser) {
        this.allowprivatecalltoanyuser = allowprivatecalltoanyuser;
    }

    public Boolean isAllowregroup() {
        return allowregroup;
    }

    public void setAllowregroup(Boolean allowregroup) {
        this.allowregroup = allowregroup;
    }

    public Boolean isAllowprivatecallparticipation() {
        return allowprivatecallparticipation;
    }

    public void setAllowprivatecallparticipation(Boolean allowprivatecallparticipation) {
        this.allowprivatecallparticipation = allowprivatecallparticipation;
    }

    public Boolean isAllowoverrideoftransmission() {
        return allowoverrideoftransmission;
    }

    public void setAllowoverrideoftransmission(Boolean allowoverrideoftransmission) {
        this.allowoverrideoftransmission = allowoverrideoftransmission;
    }

    public Boolean isAllowmanualoffnetworkswitch() {
        return allowmanualoffnetworkswitch;
    }

    public void setAllowmanualoffnetworkswitch(Boolean allowmanualoffnetworkswitch) {
        this.allowmanualoffnetworkswitch = allowmanualoffnetworkswitch;
    }

    public Boolean isAllowlistenbothoverridingandoverridden() {
        return allowlistenbothoverridingandoverridden;
    }

    public void setAllowlistenbothoverridingandoverridden(Boolean allowlistenbothoverridingandoverridden) {
        this.allowlistenbothoverridingandoverridden = allowlistenbothoverridingandoverridden;
    }

    public Boolean isAllowtransmitduringoverride() {
        return allowtransmitduringoverride;
    }

    public void setAllowtransmitduringoverride(Boolean allowtransmitduringoverride) {
        this.allowtransmitduringoverride = allowtransmitduringoverride;
    }

    public Boolean isAllowoffnetworkgroupcallchangetoemergency() {
        return allowoffnetworkgroupcallchangetoemergency;
    }

    public void setAllowoffnetworkgroupcallchangetoemergency(Boolean allowoffnetworkgroupcallchangetoemergency) {
        this.allowoffnetworkgroupcallchangetoemergency = allowoffnetworkgroupcallchangetoemergency;
    }

    public Boolean isAllowrevoketransmit() {
        return allowrevoketransmit;
    }

    public void setAllowrevoketransmit(Boolean allowrevoketransmit) {
        this.allowrevoketransmit = allowrevoketransmit;
    }

    public Boolean isAllowcreategroupbroadcastgroup() {
        return allowcreategroupbroadcastgroup;
    }

    public void setAllowcreategroupbroadcastgroup(Boolean allowcreategroupbroadcastgroup) {
        this.allowcreategroupbroadcastgroup = allowcreategroupbroadcastgroup;
    }

    public Boolean isAllowcreateuserbroadcastgroup() {
        return allowcreateuserbroadcastgroup;
    }

    public void setAllowcreateuserbroadcastgroup(Boolean allowcreateuserbroadcastgroup) {
        this.allowcreateuserbroadcastgroup = allowcreateuserbroadcastgroup;
    }

    public Boolean isAllowrequestprivatecallcallback() {
        return allowrequestprivatecallcallback;
    }

    public void setAllowrequestprivatecallcallback(Boolean allowrequestprivatecallcallback) {
        this.allowrequestprivatecallcallback = allowrequestprivatecallcallback;
    }

    public Boolean isAllowcancelprivatecallcallback() {
        return allowcancelprivatecallcallback;
    }

    public void setAllowcancelprivatecallcallback(Boolean allowcancelprivatecallcallback) {
        this.allowcancelprivatecallcallback = allowcancelprivatecallcallback;
    }

    public Boolean isAllowrequestremoteinitiatedambientlistening() {
        return allowrequestremoteinitiatedambientlistening;
    }

    public void setAllowrequestremoteinitiatedambientlistening(Boolean allowrequestremoteinitiatedambientlistening) {
        this.allowrequestremoteinitiatedambientlistening = allowrequestremoteinitiatedambientlistening;
    }

    public Boolean isAllowrequestlocallyinitiatedambientlistening() {
        return allowrequestlocallyinitiatedambientlistening;
    }

    public void setAllowrequestlocallyinitiatedambientlistening(Boolean allowrequestlocallyinitiatedambientlistening) {
        this.allowrequestlocallyinitiatedambientlistening = allowrequestlocallyinitiatedambientlistening;
    }

    public Boolean isAllowrequestfirsttoanswercall() {
        return allowrequestfirsttoanswercall;
    }

    public void setAllowrequestfirsttoanswercall(Boolean allowrequestfirsttoanswercall) {
        this.allowrequestfirsttoanswercall = allowrequestfirsttoanswercall;
    }

}
