/*
 *
 *  Copyright (C) 2018, University of the Basque Country (UPV/EHU)
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

package org.mcopenplatform.muoapi.utils;

import android.content.Context;
import android.util.Log;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.affiliation.pidf.AffiliationType;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.datatype.affiliation.pidf.Tuple;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.ConstantsMCOP;
import org.mcopenplatform.muoapi.datatype.error.Constants;
import org.mcopenplatform.muoapi.datatype.group.GroupAffiliation;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(Utils.class.getCanonicalName());

    public static Constants.CallEvent.CallTypeValidEnum validationCallType(int type){
        if(type<=0)return null;
        for(Constants.CallEvent.CallTypeValidEnum data:Constants.CallEvent.CallTypeValidEnum.values()){
            if(data.getValue()==type) {
                return data;
            }
        }
        return null;
    }

    public static String getTAG(String tag){
        if(BuildConfig.LOG_SHOW){
            return tag;
        }else{
            return BuildConfig.APPLICATION_ID;
        }
    }

    //START Affiliation Utils
    public static boolean checkGroupIsExist(NgnSipPrefrences profileNow, String groupID,Context context){
        boolean result=true;
        Map<String,NgnSipPrefrences.EntryType> stringEntryTypeMap=null;
        if(groupID==null ||
                groupID.trim().isEmpty() ||
                context==null ||
                profileNow==null ||
                (stringEntryTypeMap=profileNow.getMCPTTGroupInfo())==null){
            result=false;
        }

        if(stringEntryTypeMap!=null && stringEntryTypeMap.get(groupID)==null)
            for(String groupDisplay:stringEntryTypeMap.keySet()){
                if(stringEntryTypeMap.get(groupDisplay).getUriEntry().compareTo(groupID)==0)result=true;
            }
        return result;
    }

    public static boolean isAffiliatedGroup(Presence presenceNow, String groupID,Context context){
        return isGroupGroupAffiliationStateEnum(presenceNow,groupID,context,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.affiliated);
    }

    public static boolean isDeaffiliatedGroup(Presence presenceNow, String groupID,Context context){
        return isGroupGroupAffiliationStateEnum(presenceNow,groupID,context,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.notaffiliated);
    }

    public static boolean isDeaffiliatingGroup(Presence presenceNow, String groupID,Context context){
        return isGroupGroupAffiliationStateEnum(presenceNow,groupID,context,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.deaffiliating);
    }

    public static boolean isAffiliatingGroup(Presence presenceNow, String groupID,Context context){
        return isGroupGroupAffiliationStateEnum(presenceNow,groupID,context,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.affiliating);
    }

    private static boolean isGroupGroupAffiliationStateEnum(Presence presenceNow, String groupID,Context context,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum groupAffiliationStateEnum){
        ArrayList<GroupAffiliation> groupAffiliations=checkPresence(presenceNow,context);
        if(groupAffiliations!=null)
            for(GroupAffiliation groupAffiliation:groupAffiliations){
                if(groupAffiliation.getGroupID().compareTo(groupID)==0 &&
                        groupAffiliation.getStateAffiliation()==groupAffiliationStateEnum)
                    return true;
            }
        return false;
    }

    public static ArrayList<GroupAffiliation> checkPresence(Presence presence, Context context){
        if(presence==null)return new ArrayList<>();
        ArrayList<GroupAffiliation> groupAffiliations=new ArrayList<>();
        String mcpttClientID=null;
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null)mcpttClientID=profile.getMcpttClientId();
        if(presence.getTuple()!=null && mcpttClientID!=null){
            for(Tuple tuple:presence.getTuple()){
                if(tuple!=null && presence.getTuple().get(0).getStatus()!=null && tuple.getStatus().getAffiliations()!=null && tuple.getId().trim().equals(mcpttClientID)){
                    for(AffiliationType affiliation:presence.getTuple().get(0).getStatus().getAffiliations()){
                        GroupAffiliation groupAffiliation= null;
                        if(affiliation.getGroup()!=null){
                            groupAffiliation = new GroupAffiliation(affiliation.getGroup(),affiliation.getStatus());
                            groupAffiliations.add(groupAffiliation);
                        }

                    }
                }
            }
        }
        return groupAffiliations;
    }

    public static Map<String,Integer> groupAffiliationToMap(List<GroupAffiliation> groupAffiliations){
        if(BuildConfig.DEBUG && groupAffiliations!=null) Log.d(TAG,"groupAffiliationToMap size: "+groupAffiliations.size());
        Map<String,Integer> groups=new HashMap<>();
        if(groupAffiliations!=null)
            for(GroupAffiliation group:groupAffiliations){
                groups.put(group.getGroupID(),group.getStateAffiliation().getValue());
            }
        return groups;
    }
    //END Affiliation Utils


}