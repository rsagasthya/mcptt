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

package org.mcopenplatform.muoapi;

import org.doubango.ngn.datatype.ms.gms.ns.common_policy.ExtensibleType;
import org.doubango.ngn.datatype.ms.gms.ns.common_policy.OneType;
import org.doubango.ngn.datatype.ms.gms.ns.common_policy.RuleType;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.ListServiceType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

class ManagerClientUtils {



    protected enum TypeParticipant{
        URI,
        DISPLAY_NAME,
        TYPE
    }

    protected static List<String> getParticipantGroupsWithTypes(ListServiceType serviceType,TypeParticipant type){
        if(serviceType==null)return null;
        List<String> participants=new ArrayList<>();
        if(serviceType.getList()!=null && serviceType.getList().getEntry()!=null)
            for(EntryType entryType:serviceType.getList().getEntry()){
                if(entryType.getUri()!=null && !entryType.getUri().isEmpty()){
                    String participant=new String();
                    switch (type){
                        case URI:
                            participant=entryType.getUri();

                            break;
                        case TYPE:
                            if(entryType.getParticipanttype()!=null && !entryType.getParticipanttype().trim().isEmpty()){
                                participant=entryType.getParticipanttype();
                            }
                            break;
                        case DISPLAY_NAME:
                            if(entryType.getDisplayName()!=null && entryType.getDisplayName().getValue()!=null && !entryType.getDisplayName().getValue().trim().isEmpty())
                                participant=entryType.getDisplayName().getValue();
                            break;
                    }
                    participants.add(participant);
                }
            }

        return participants;

    }

    protected static List<String[]> getParticipantGroups(ListServiceType serviceType){
        if(serviceType==null)return null;
        List<String[]> participants=new ArrayList<>();
        if(serviceType.getList()!=null && serviceType.getList().getEntry()!=null)
            for(EntryType entryType:serviceType.getList().getEntry()){
                if(entryType.getUri()!=null && !entryType.getUri().isEmpty()){
                    String[] participant=new String[3];
                    participant[0]=entryType.getUri();
                    if(entryType.getParticipanttype()!=null && !entryType.getParticipanttype().trim().isEmpty())
                        participant[1]=entryType.getParticipanttype();
                    if(entryType.getDisplayName()!=null && entryType.getDisplayName().getValue()!=null && !entryType.getDisplayName().getValue().trim().isEmpty())
                        participant[2]=entryType.getDisplayName().getValue();
                    participants.add(participant);
                }
            }

        return participants;

    }

    protected static Set<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> getAllowsGroups(String userID, ListServiceType serviceType){
        if(serviceType==null)return null;
        List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowsType=new ArrayList<>();
        Boolean iniviteMember=serviceType.getInviteMembers();
        if(iniviteMember!=null && iniviteMember){
            allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.invite_members);
        }else{
            Boolean onnetworkIniviteMember=serviceType.getOnnetworkinvitemembers();
            if(onnetworkIniviteMember!=null && onnetworkIniviteMember)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.invite_members);
        }
        Boolean nonrealtimevideo=serviceType.getMcvideononrealtimevideomode();
        if(nonrealtimevideo!=null && nonrealtimevideo)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_real_time_video_mode);
        Boolean nonurgentrealtime=serviceType.getMcvideononurgentrealtimevideomode();
        if(nonurgentrealtime!=null && nonurgentrealtime)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_urgent_real_time_video_mode);
        Boolean urgentrealtimevide=serviceType.getMcvideourgentrealtimevideomode();
        if(urgentrealtimevide!=null && urgentrealtimevide)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.urgent_real_time_video_mode);
        Boolean allowshortdataservice=serviceType.getMcdataallowshortdataservice();
        if(allowshortdataservice!=null && allowshortdataservice)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.short_data_service);
        Boolean allowfiledistribution=serviceType.getMcdataallowfiledistribution();
        if(allowfiledistribution!=null && allowfiledistribution)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.file_distribution);
        Boolean allowconversationmanagement=serviceType.getMcdataallowconversationmanagement();
        if(allowconversationmanagement!=null && allowconversationmanagement)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.conversation_management);
        Boolean allowtxcontrol=serviceType.getMcdataallowtxcontrol();
        if(allowtxcontrol!=null && allowtxcontrol)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.tx_control);
        Boolean allowrxcontrol=serviceType.getMcdataallowrxcontrol();
        if(allowrxcontrol!=null && allowrxcontrol)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.rx_control);
        Boolean enhancedstatus=serviceType.getMcdataallowenhancedstatus();
        if(enhancedstatus!=null && enhancedstatus)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.enhanced_status);
        if(serviceType.getList()!=null && serviceType.getList().getEntry()!=null && userID!=null)
            for(EntryType entry:serviceType.getList().getEntry())
                if(entry.getUri()!=null &&
                        !entry.getUri().trim().isEmpty() &&
                        entry.getUri().trim().compareTo(userID)==0
                        ){
                    if(entry.getOnnetworkrecvonly()!=null)allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.recvonly);
                }
        if(serviceType.getRuleset()!=null && serviceType.getRuleset().getRule()!=null && userID!=null)
            for(RuleType rule:serviceType.getRuleset().getRule())
                if(rule.getActions()!=null &&
                        rule.getConditions()!=null
                        ){
                    org.doubango.ngn.datatype.ms.gms.ns.common_policy.IdentityType identityType=null;
                    if(rule.getConditions().getIdentity()!=null &&
                            !rule.getConditions().getIdentity().isEmpty() &&
                            (identityType=rule.getConditions().getIdentity().get(0))!=null){
                        if(identityType.getOne()!=null)
                            for(OneType oneType:identityType.getOne())
                                if(oneType.getId()!=null && !oneType.getId().trim().isEmpty() && oneType.getId().trim().compareTo(userID)==0){
                                    List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowsType2=getAllowsGroups(rule.getActions());
                                    allowsType.addAll(allowsType2);
                                }

                    }else if(rule.getConditions().getIslistmember()!=null){
                        List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowsType2=getAllowsGroups(rule.getActions());
                        allowsType.addAll(allowsType2);
                    }
                }
        //"LinkedHashSet" used to avoid the duplicate "allow"
        return new LinkedHashSet<>(allowsType);
    }
    private static List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> getAllowsGroups(ExtensibleType extensibleType){
        List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowsType=new ArrayList<>();
        if(extensibleType!=null){
            if(extensibleType.getAllowMCPTTemergencycall()!=null && extensibleType.getAllowMCPTTemergencycall())
                allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.emergency_call);
            if(extensibleType.getAllowMCPTTemergencycall()!=null && extensibleType.getAllowMCPTTemergencyalert())
                allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.emergency_alert_call);
            if(extensibleType.getAllowMCPTTemergencycall()!=null && extensibleType.getAllowimminentperilcall())
                allowsType.add(ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.imminent_peril_call);
        }
        return allowsType;

    }
}
