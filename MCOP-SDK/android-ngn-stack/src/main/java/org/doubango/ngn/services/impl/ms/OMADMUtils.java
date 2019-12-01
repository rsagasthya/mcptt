
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



package org.doubango.ngn.services.impl.ms;


import android.content.Context;
import android.util.Log;

import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.BroadcastGroupType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.DefaultProsePerPacketPriorityType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.PrivateCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationParamsType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.TransmitTimeType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.CommonType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.NameType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.OnNetworkType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.RelayMCPTTGroupIDType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.RelayedMCPTTGroupType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.AliasEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.DisplayNameElementType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EmergencyAlertType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EmergencyCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryInfoTypeList;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.GroupServerInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ImminentPerilCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ListEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.MCPTTGroupCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.MCPTTGroupInitiationEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.MCPTTPrivateCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.MCPTTPrivateRecipientEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.PrivateCallListEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ProSeUserEntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.UserAliasType;
import org.doubango.ngn.datatype.ms.gms.ns.common_policy.ExtensibleType;
import org.doubango.ngn.datatype.ms.gms.ns.common_policy.RuleType;
import org.doubango.ngn.datatype.ms.gms.ns.common_policy.Ruleset;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.ListServiceType;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.ListType;
import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EncodingType;
import org.doubango.ngn.datatype.ms.gms.ns.mcpttgroup.EncodingsType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.DisplayNameType;
import org.doubango.ngn.datatype.mo.MgmtTree;
import org.doubango.ngn.datatype.mo.Node;
import org.doubango.ngn.datatype.mo.Value;
import org.doubango.utils.Utils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;


class OMADMUtils {
    private final static String TAG = Utils.getTAG(OMADMUtils.class.getCanonicalName());
    private final static String NAME_FILE_MCPTT_UE_INIT_CONFIG="mcpttueinitconf";
    private static DatatypeFactory datatypeFactory;




    //INIT utils MO to xml



        private static List<NameType> valuesToNameTypeUEConfigure(List<Value> values){
            if(values==null)return null;
            ArrayList<NameType> nameTypes=new ArrayList<>();
            for(Value value:values){
                NameType nameType=new NameType();
                nameType.setValue(value.getvalue());
                nameTypes.add(nameType);
            }
            return nameTypes;
        }

        private static List<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.NameType> valuesToNameTypeMcpttUserProfile(List<Value> values){
            if(values==null)return null;
            ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.NameType> nameTypes=new ArrayList<>();
            for(Value value:values){
                org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.NameType nameType=new org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.NameType();
                nameType.setValue(value.getvalue());
                nameTypes.add(nameType);
            }
            return nameTypes;
        }

        private static List<byte[]> valuesIntToHexBytes(List<Value> values){
            if(values==null)return null;
            ArrayList<byte[]> bytes=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        int integer=Integer.valueOf(value.getvalue());
                        String hex = Integer.toHexString(integer);
                        bytes.add(hexStringToByteArray(hex));
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to integer for Byte");
            }
            return bytes;
        }

        private static byte[] valuesIntToHexByte(List<Value> values){
            List<byte[]> bytes=valuesIntToHexBytes(values);
            if(bytes!=null && bytes.size()>0)
                return bytes.get(0);
            return null;
        }

        private static byte[] hexStringToByteArray(String s) {
            int len = s.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i+1), 16));
            }
            return data;
        }



        private static List<EncodingType> valuesToEncodingTypes(List<Value> values){
            if(values==null)return null;
            ArrayList<EncodingType> encodingTypes=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        EncodingType encodingType=new EncodingType();
                        encodingType.setName(value.getvalue());
                        encodingTypes.add(encodingType);
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to Big Integer");
            }
            return encodingTypes;
        }

        private static EncodingType valuesToEncodingType(List<Value> values){
            List<EncodingType> encodingTypes=valuesToEncodingTypes(values);
            if(encodingTypes!=null && encodingTypes.size()>0)
                return encodingTypes.get(0);
            return null;
        }

        private static List<BigInteger> valuesToBigIntegers(List<Value> values){
            if(values==null)return null;
            ArrayList<BigInteger> bigIntegers=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        BigInteger bigInteger=BigInteger.valueOf(Long.valueOf(value.getvalue()));
                        bigIntegers.add(bigInteger);
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to Big Integer");
            }
            return bigIntegers;
        }

        private static BigInteger valuesToBigInteger(List<Value> values){
            List<BigInteger> bigIntegers=valuesToBigIntegers(values);
            if(bigIntegers!=null && bigIntegers.size()>0)
                return bigIntegers.get(0);
            return null;
        }


        private static List<Integer> valuesToIntegers(List<Value> values){
            if(values==null)return null;
            ArrayList<Integer> integers=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        Integer integer=Integer.valueOf(value.getvalue());
                        integers.add(integer);
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to  Integer: "+e.getMessage());
            }
            return integers;
        }

        private static Integer valuesToInteger(List<Value> values){
            List<Integer> integers=valuesToIntegers(values);
            if(integers!=null && integers.size()>0)
                return integers.get(0);
            return null;
        }
        public static Duration longToDuration(final long duration) throws DatatypeConfigurationException {
            if(datatypeFactory==null)
            datatypeFactory=DatatypeFactory.newInstance();
            return datatypeFactory.newDuration(duration);
        }

        private static List<Duration> valuesIntToDurations(List<Value> values){
                if(values==null)return null;
                ArrayList<Duration> durations=new ArrayList<>();
                try {
                    for(Value value:values){
                        if(value.getvalue()!=null){
                            durations.add(longToDuration(Integer.valueOf(value.getvalue())));
                        }
                    }
                }catch (Exception e){
                    Log.e(TAG,"Error parse data to Duration");
                }
                return durations;
        }

        private static Duration valueIntToDuration(List<Value> values){
            List<Duration> durations=valuesIntToDurations(values);
            if(durations!=null && durations.size()>0)
                return durations.get(0);
            return null;
        }

        private static List<String> valuesToStrings(List<Value> values){
            if(values==null)return null;
            ArrayList<String> strings=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        strings.add(value.getvalue());
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to String");
            }
            return strings;
        }

        private static String valuesToString(List<Value> values){
            List<String> string=valuesToStrings(values);
            if(string!=null && string.size()>0)
                return string.get(0);
            return null;
        }

        private static List<Boolean> valuesToBools(List<Value> values){
            if(values==null)return null;
            ArrayList<Boolean> booleans=new ArrayList<>();
            try {
                for(Value value:values){
                    if(value.getvalue()!=null){
                        booleans.add(Boolean.valueOf(value.getvalue()));
                    }
                }
            }catch (Exception e){
                Log.e(TAG,"Error parse data to Boolean");
            }
            return booleans;
        }

        private static Boolean valuesToBool(List<Value> values){
            List<Boolean> booleans=valuesToBools(values);
            if(booleans!=null && booleans.size()>0)
                return booleans.get(0);
            return null;
        }

        public static McpttUEConfiguration generateMcpttUEConfiguration(MgmtTree mgmtTree){
            if(mgmtTree== null || mgmtTree.getNode()==null)return null;
            McpttUEConfiguration mcpttUEConfiguration=new McpttUEConfiguration();
            for(Node node:mgmtTree.getNode()){
                for(Node node1:node.getNode()) {
                    switch (node1.getNodeName()) {
                        case "Name":
                            if(node.getValue()!=null && node.getValue().get(0)!=null){
                                mcpttUEConfiguration.setName(valuesToNameTypeUEConfigure(node.getValue()));
                            }
                            break;
                        case "Ext":
                            break;
                        case "Common":
                            CommonType commonType=new CommonType();
                            mcpttUEConfiguration.setCommon(commonType);
                            for(Node node2:node1.getNode()) {
                                switch (node2.getNodeName()) {
                                    case "PrivateCall":
                                        CommonType.PrivateCall privateCall=new CommonType.PrivateCall();
                                        commonType.setPrivateCall(privateCall);
                                        for(Node node3:node2.getNode()) {
                                            switch (node3.getNodeName()) {
                                                case "MaxCallN10":
                                                    privateCall.setMaxSimulCallN10(valuesToBigInteger(node3.getValue()));
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        break;
                                    case "MCPTTGroupCall":
                                        CommonType.MCPTTGroupCall mcpttGroupCall=new CommonType.MCPTTGroupCall();
                                        commonType.setMCPTTGroupCall(mcpttGroupCall);
                                        for(Node node3:node2.getNode()) {
                                            switch (node3.getNodeName()) {
                                                case "MaxCallN4":
                                                    mcpttGroupCall.setMaxSimulCallN4(valuesToBigInteger(node3.getValue()));
                                                    break;
                                                case "MaxTransmissionN5":
                                                    mcpttGroupCall.setMaxSimulTransN5(valuesToBigInteger(node3.getValue()));
                                                    break;
                                                case "PrioritizedMCPTTGroup":
                                                    if(mcpttGroupCall.getPrioritizedMCPTTGroup()==null){
                                                        CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup prioritizedMCPTTGroup=new CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup();
                                                        mcpttGroupCall.setPrioritizedMCPTTGroup(prioritizedMCPTTGroup);
                                                        List<CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority> mcpttGroupPriorities=new ArrayList<>();
                                                        mcpttGroupCall.getPrioritizedMCPTTGroup().setMcpttGroupPriority(mcpttGroupPriorities);
                                                    }
                                                    for(Node node4:node3.getNode()) {
                                                        if(node4!=null){
                                                            CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority mcpttGroupPriority=new CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority();
                                                            mcpttGroupCall.getPrioritizedMCPTTGroup().getMCPTTGroupPriority().add(mcpttGroupPriority);
                                                            for(Node node5:node4.getNode()) {
                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                    switch (node5.getNodeName()) {
                                                                        case "MCPTTGroupID":
                                                                            String data1=valuesToString(node5.getValue());
                                                                            if(data1!=null)
                                                                            mcpttGroupPriority.setMCPTTGroupID(data1);
                                                                            break;
                                                                        case "MCPTTGroupPriorityHierarchy":
                                                                            BigInteger data2=valuesToBigInteger(node5.getValue());
                                                                            if(data2!=null)
                                                                            mcpttGroupPriority.setGroupPriorityHierarchy(data2);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "OnNetwork":
                            OnNetworkType onNetworkType=new OnNetworkType();
                            mcpttUEConfiguration.setOnNetwork(onNetworkType);
                            for(Node node2:node1.getNode()) {
                                Boolean response=null;
                                switch (node2.getNodeName()) {
                                    case "RelayService":
                                        response=valuesToBool(node2.getValue());
                                        if(response!=null)onNetworkType.setRelayService(response);
                                        break;
                                    case "IPv6Preferred":
                                        response=valuesToBool(node2.getValue());
                                        if(response!=null)onNetworkType.setIPv6Preferred(response);
                                        break;
                                    case "RelayedMCPTTGroup":
                                        if(onNetworkType.getRelayedMCPTTGroup()==null){
                                            RelayedMCPTTGroupType relayedMCPTTGroupType=new RelayedMCPTTGroupType();
                                            onNetworkType.setRelayedMCPTTGroup(relayedMCPTTGroupType);
                                            ArrayList<RelayMCPTTGroupIDType> relayMCPTTGroupIDTypes=new ArrayList<>();
                                            relayedMCPTTGroupType.setRelayMCPTTGroupID(relayMCPTTGroupIDTypes);
                                        }
                                        for(Node node3:node2.getNode()) {
                                            if(node3!=null){
                                                RelayMCPTTGroupIDType relayMCPTTGroupIDType=new RelayMCPTTGroupIDType();
                                                onNetworkType.getRelayedMCPTTGroup().getRelayMCPTTGroupID().add(relayMCPTTGroupIDType);
                                                for(Node node4:node3.getNode()) {
                                                    if (node4 != null && node4.getNodeName() != null)
                                                        switch (node4.getNodeName()) {
                                                            case "MCPTTGroupID":
                                                                relayMCPTTGroupIDType.setMCPTTGroupID(valuesToString(node4.getValue()));
                                                                break;
                                                            case "RelayServiceCode":
                                                                relayMCPTTGroupIDType.setRelayServiceCode(valuesToString(node4.getValue()));
                                                                break;
                                                        }
                                                }
                                            }
                                        }
                                        break;
                                    default:

                                        break;

                                }
                            }
                            break;
                        case "OffNetwork":
                            System.out.println("test1 OffNetwork");
                            break;
                        default:
                            System.out.println("test1 default: "+node1.getNodeName());
                            break;
                    }
                }
            }
            return mcpttUEConfiguration;
        }

        public static McpttUserProfile generateMcpttUserProfile(MgmtTree mgmtTree){
            if(mgmtTree== null || mgmtTree.getNode()==null)return null;
            McpttUserProfile mcpttUserProfile=new McpttUserProfile();
            for(Node node:mgmtTree.getNode()){
                //if(node!=null && node.getNodeName()!=null)
                        for(Node node1:node.getNode()) {
                            if(node1!=null && node1.getNodeName()!=null)
                            switch (node1.getNodeName()) {
                                case "Name":
                                    if(node.getValue()!=null && node.getValue().get(0)!=null){
                                        mcpttUserProfile.setName(valuesToNameTypeMcpttUserProfile(node.getValue()));
                                    }
                                    break;
                                case "Ext":
                                    break;
                                case "Common":
                                    if(mcpttUserProfile.getCommon()==null){
                                        ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.CommonType> commonTypes=new ArrayList<>();
                                        mcpttUserProfile.setCommon(commonTypes);
                                    }
                                    org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.CommonType commonType=new org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.CommonType();
                                    mcpttUserProfile.getCommon().add(commonType);
                                    for(Node node2:node1.getNode()) {
                                        if(commonType.getmCPTTUserID()==null){
                                            ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryType> entryTypes=new ArrayList<>();
                                            commonType.setmCPTTUserID(entryTypes);
                                            org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryType entryType=new EntryType();
                                            commonType.getmCPTTUserID().add(entryType);
                                        }
                                        switch (node2.getNodeName()) {
                                            case "MCPTTUserID":
                                                commonType.getmCPTTUserID().get(0).setUriEntry(valuesToString(node2.getValue()));
                                                break;
                                            case "MCPTTUserProfileIndex":
                                                commonType.getmCPTTUserID().get(0).setIndex(valuesToString(node2.getValue()));
                                                break;
                                            case "PreSelectedIndication":
                                                Log.w(TAG,"Now, it don´t support PreSelectedIndication in User profile");
                                                break;
                                            case "UserAliases":
                                                if(commonType.getUserAlias()==null){
                                                    ArrayList<UserAliasType> userAliasTypes=new ArrayList<UserAliasType>();
                                                    commonType.setUserAlias(userAliasTypes);
                                                }
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null){
                                                        UserAliasType userAliasType=new UserAliasType();
                                                        commonType.getUserAlias().add(userAliasType);
                                                        for(Node node4:node3.getNode()) {
                                                            if(node4!=null && node4.getNodeName()!=null)
                                                                switch (node4.getNodeName()) {
                                                                    case "UserAlias":
                                                                        ArrayList<AliasEntryType> aliasEntryTypes= new ArrayList<AliasEntryType>();
                                                                        userAliasType.setAliasEntry(aliasEntryTypes);
                                                                        AliasEntryType aliasEntryType=new AliasEntryType();
                                                                        aliasEntryTypes.add(aliasEntryType);
                                                                        aliasEntryType.setValue(valuesToString(node4.getValue()));
                                                                        break;
                                                                    default:
                                                                        break;
                                                                }
                                                        }
                                                    }
                                                }
                                                break;
                                            case "AuthorisedAlias":
                                                //When set to "true" the MCPTT user is authorised to create and delete aliases of other MCPTT users and their associated MCPTT user profiles.
                                                //When set to "false" the MCPTT user is not authorised to create and delete aliases of other MCPTT user and their associated MCPTT user profiles. This is the default if this leaf node is not present.
                                                Log.w(TAG,"Now, it don´t support AuthorisedAlias in User profile");
                                                break;
                                            case "ParticipantType":
                                                //The ParticipantType means the functional category of the participant (e.g., first responder, second responder, dispatch, dispatch supervisor), typically defined by the MCPTT administrators
                                                commonType.setParticipantType(valuesToStrings(node2.getValue()));
                                                break;
                                            case "Organization":
                                                //This leaf node indicates the organization an MCPTT user belongs to.
                                                commonType.setMissionCriticalOrganization(valuesToStrings(node2.getValue()));
                                                break;
                                            case "PrivateCall":
                                                //This interior node is a placeholder for the MCPTT private call configuration.break;
                                                if(commonType.getPrivateCall()==null){
                                                    ArrayList<MCPTTPrivateCallType> mcpttPrivateCallTypes=new ArrayList<>();
                                                    commonType.setPrivateCall(mcpttPrivateCallTypes);
                                                }
                                                MCPTTPrivateCallType mcpttPrivateCallType=new MCPTTPrivateCallType();
                                                commonType.getPrivateCall().add(mcpttPrivateCallType);
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "Authorised":
                                                                //This leaf node indicates the authorisation to make a MCPTT private call.
                                                                Log.w(TAG,"Now, it don´t support Authorised in User profile");

                                                                break;
                                                            case "AuthorisedAny":
                                                                //	This leaf node indicates the authorisation to make a MCPTT private call to any MCPTT user.
                                                                //When set to "true" any MCPTT user is authorised to make an MCPTT private call to any MCPTT user.
                                                                // When set to "false" any MCPTT user is not authorised to make an MCPTT private call to any MCPTT user.
                                                                Log.w(TAG,"Now, it don´t support AuthorisedAny in User profile");
                                                                break;
                                                            case "UserList":
                                                                //This interior node is a placeholder for a list of MCPTT user(s) who can be called in a MCPTT private call.
                                                                PrivateCallListEntryType privateCallListEntryType=new PrivateCallListEntryType();
                                                                mcpttPrivateCallType.setPrivateCallList(privateCallListEntryType);
                                                                for(Node node4:node3.getNode()) {
                                                                    //This interior node is a placeholder for one or more list of MCPTT users who can be called in a MCPTT private call.
                                                                    if(privateCallListEntryType.getPrivateCallProSeUser()==null){
                                                                        ArrayList<ProSeUserEntryType> proSeUserEntryTypes= new ArrayList<>();
                                                                        privateCallListEntryType.setPrivateCallProSeUser(proSeUserEntryTypes);
                                                                    }
                                                                    if(privateCallListEntryType.getPrivateCallURI()==null){
                                                                        ArrayList<EntryType> privateCallEntryTypes= new ArrayList<>();
                                                                        privateCallListEntryType.setPrivateCallURI(privateCallEntryTypes);
                                                                    }
                                                                    if( privateCallListEntryType.getPrivateCallKMSURIS()==null){
                                                                        ArrayList<EntryType> privateCallKMSURIS=new ArrayList<EntryType>();
                                                                        privateCallListEntryType.setPrivateCallKMSURIS(privateCallKMSURIS);
                                                                    }
                                                                    if(node4!=null){


                                                                        for(Node node5:node4.getNode()) {
                                                                            EntryType privateCallEntryType=null;
                                                                            ProSeUserEntryType proSeUserEntryType=null;
                                                                            EntryType privateCallKMSURIEntryType=null;
                                                                            if(node5!=null && node5.getNodeName()!=null)
                                                                                switch (node5.getNodeName()) {
                                                                                    case "Entry":
                                                                                        //This interior node is a placeholder for one or more MCPTT users who can be called in a private call.
                                                                                        byte[] bytes=null;
                                                                                        for(Node node6:node5.getNode()) {
                                                                                            if(node6!=null && node6.getNodeName()!=null)
                                                                                                switch (node6.getNodeName()) {
                                                                                                    case "MCPTTID":
                                                                                                        if(privateCallEntryType==null)privateCallEntryType=new EntryType();
                                                                                                        //This leaf node indicates an MCPTT user identity (MCPTT ID) which is a globally unique identifier within the MCPTT service that represents the MCPTT user.
                                                                                                        //The value is a "uri" attribute specified in OMA OMA-TS-XDM_Group-V1_1 [4].
                                                                                                        privateCallEntryType.setUriEntry(valuesToString(node6.getValue()));
                                                                                                        break;
                                                                                                    case "DiscoveryGroupID":
                                                                                                        if(proSeUserEntryType==null)proSeUserEntryType=new ProSeUserEntryType();
                                                                                                        //This leaf node indicates a discovery group ID as specified in 3GPP TS 23.303 [6].
                                                                                                        //The value is used as the discovery group ID in the ProSe discovery procedures as specified in 3GPP TS 23.303 [6].
                                                                                                        bytes=valuesIntToHexByte(node6.getValue());
                                                                                                        if(bytes!=null && bytes.length>0)
                                                                                                            proSeUserEntryType.setDiscoveryGroupID(new String(bytes));
                                                                                                        break;
                                                                                                    case "UserInfoID":
                                                                                                        if(proSeUserEntryType==null)proSeUserEntryType=new ProSeUserEntryType();
                                                                                                        //This leaf node indicates a ProSe user info ID as specified in 3GPP TS 23.303 [6].
                                                                                                        bytes=valuesIntToHexByte(node6.getValue());
                                                                                                        if(bytes!=null && bytes.length>0)
                                                                                                            proSeUserEntryType.setUserInfoID(new String(bytes));
                                                                                                        break;
                                                                                                    case "DisplayName":
                                                                                                        if(privateCallEntryType==null)privateCallEntryType=new EntryType();
                                                                                                        //This leaf node contains a  human readable name.
                                                                                                        DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                        displayNameElementType.setValue(valuesToString(node6.getValue()));
                                                                                                        privateCallEntryType.setDisplayName(displayNameElementType);
                                                                                                        break;
                                                                                                    case "PrivateCallKMSURI":
                                                                                                        //This leaf node indicates the identity (URI) of the KMS associated with the MCPTTID. If the value is empty, the KMS leaf node present in the MCS UE initial configuration MO is used
                                                                                                        if(privateCallKMSURIEntryType==null)privateCallKMSURIEntryType=new EntryType();
                                                                                                        privateCallEntryType.setUriEntry(valuesToString(node6.getValue()));
                                                                                                        break;
                                                                                                    case "ManualCommence":
                                                                                                        //	This leaf node indicates the authorisation to make a MCPTT private call with manual commencement.
                                                                                                        // When set to "true" the MCPTT user is authorised to make a MCPTT private call in manual commencement mode.
                                                                                                        // When set to "false" the MCPTT user is not authorised to make a MCPTT private call in manual commencement mode.
                                                                                                        Log.w(TAG,"Now, it don´t support ManualCommence in User profile");
                                                                                                        break;
                                                                                                    case "AutoCommence":
                                                                                                        //	This leaf node indicates the authorisation to make a MCPTT private call with automatic commencement.
                                                                                                        // When set to "true" the MCPTT user is authorised to make a MCPTT private call in automatic commencement mode.
                                                                                                        // When set to "false" the MCPTT user is not authorised to make a MCPTT private call in manual commencement mode.
                                                                                                        Log.w(TAG,"Now, it don´t support AutoCommence in User profile");
                                                                                                        break;
                                                                                                    default:
                                                                                                        break;
                                                                                                }
                                                                                        }
                                                                                        if(privateCallEntryType!=null)
                                                                                            privateCallListEntryType.getPrivateCallURI().add(privateCallEntryType);
                                                                                        if(proSeUserEntryType!=null)
                                                                                            privateCallListEntryType.getPrivateCallProSeUser().add(proSeUserEntryType);
                                                                                        if(privateCallKMSURIEntryType!=null)
                                                                                            privateCallListEntryType.getPrivateCallKMSURIS().add(privateCallKMSURIEntryType);
                                                                                        break;

                                                                                    default:
                                                                                        break;
                                                                                }
                                                                        }
                                                                    }
                                                                }
                                                                break;
                                                            case "AutoAnswer":
                                                                //This leaf node indicates the authorisation of MCPTT user to force automatic answer for a MCPTT private call.
                                                                //When set to "true" the MCPTT user is authorised to force automatic answer for a MCPTT private call.
                                                                //When set to "false" the MCPTT user is not authorised to force automatic answer for a MCPTT private call.
                                                                Log.w(TAG,"Now, it don´t support AutoAnswer in User profile");
                                                                break;
                                                            case "FailRestrict":
                                                                //This leaf node indicates the authorisation to restrict the provision of a notification of call failure reason for a MCPTT private call.
                                                                //When set to "true" the MCPTT user is authorised to restrict notification of call failure reason for MCPTT private call.
                                                                // When set to "false" the MCPTT user is not authorised to restrict notification of call failure reason for MCPTT private call.
                                                                Log.w(TAG,"Now, it don´t support FailRestrict in User profile");
                                                                break;
                                                            case "AllowedMediaProtection":
                                                                //This leaf node indicates authorisation to protect confidentiality and integrity of media for MCPTT private calls.
                                                                // When set to "true" the MCPTT user is authorised to protect confidentiality and integrity of media for MCPTT private calls.
                                                                // When set to "false" the MCPTT user is not authorised to protect confidentiality and integrity of media for MCPTT private calls.
                                                                // The default value is set to "true".
                                                                Log.w(TAG,"Now, it don´t support AllowedMediaProtection in User profile");
                                                                break;
                                                            case "AllowedFloorControlProtection":
                                                                //This leaf node indicates authorisation to protect confidentiality and integrity of floor control signalling for MCPTT private calls.
                                                                //When set to "true" the MCPTT user is authorised to protect confidentiality and integrity of floor control signalling for MCPTT private calls.
                                                                // When set to "false" the MCPTT user is not authorised to protect confidentiality and integrity of floor control signalling for MCPTT private calls.
                                                                // The default value is set to "true".
                                                                Log.w(TAG,"Now, it don´t support AllowedFloorControlProtection in User profile");
                                                                break;
                                                            case "EmergencyCall":
                                                                //This interior node is a placeholder for the MCPTT emergency call policy..
                                                                EmergencyCallType emergencyCallType=new EmergencyCallType();
                                                                mcpttPrivateCallType.setEmergencyCall(emergencyCallType);
                                                                for(Node node4:node3.getNode()) {
                                                                    if(node4!=null && node4.getNodeName()!=null)
                                                                        switch (node4.getNodeName()) {
                                                                            case "Authorised":
                                                                                //This leaf node indicates the authorisation to make an MCPTT emergency private call.
                                                                                // When set to "true" the MCPTT user is authorised to make an MCPTT emergency private call.
                                                                                // When set to "false" the MCPTT user is not authorised to make an MCPTT emergency private call.
                                                                                    Log.w(TAG,"Now, it don´t support Authorised in User profile");
                                                                                break;
                                                                            case "CancelPriority":
                                                                                //	This leaf node indicates the authorisation to cancel emergency priority in an MCPTT emergency private call by an authorised MCPTT user.
                                                                                // When set to "true" the MCPTT user is authorised to cancel an emergency priority in an MCPTT private call.
                                                                                // When set to "false" the MCPTT user is not authorised to cancel an emergency priority in an MPCTT private call.
                                                                                Log.w(TAG,"Now, it don´t support Authorised in User profile");
                                                                                break;
                                                                            case "MCPTTPrivateRecipient":
                                                                                //	This interior node is a placeholder for the details of the MCPTT private recipient for an MCPTT emergency private call.
                                                                                MCPTTPrivateRecipientEntryType mcpttPrivateRecipientEntryType=new MCPTTPrivateRecipientEntryType();
                                                                                emergencyCallType.setMCPTTPrivateRecipient(mcpttPrivateRecipientEntryType);
                                                                                for(Node node5:node4.getNode()) {
                                                                                    if(node5!=null && node5.getNodeName()!=null)
                                                                                        switch (node5.getNodeName()) {
                                                                                            case "Entry":
                                                                                                EntryType mcpttPrivateRecipients=null;
                                                                                                ProSeUserEntryType proSeUserEntryType=null;
                                                                                                //This interior node is a placeholder for the details of the MCPTT private recipient for an MCPTT emergency private call.
                                                                                                for(Node node6:node5.getNode()) {
                                                                                                    if(node6!=null && node6.getNodeName()!=null && node6.getValue()!=null)
                                                                                                        switch (node6.getNodeName()) {
                                                                                                            case "ID":
                                                                                                                //This leaf node indicates the MCPTT private recipient used upon certain criteria on initiation of an MCPTT emergency private call.
                                                                                                                if(proSeUserEntryType==null)proSeUserEntryType=new ProSeUserEntryType();
                                                                                                                proSeUserEntryType.setIndex(valuesToString(node6.getValue()));
                                                                                                                break;
                                                                                                            case "DiscoveryGroupID":
                                                                                                                //This leaf node indicates the discovery group ID as specified in 3GPP TS 23.303 [6].
                                                                                                                if(proSeUserEntryType==null)proSeUserEntryType=new ProSeUserEntryType();
                                                                                                                proSeUserEntryType.setDiscoveryGroupID(new String(valuesIntToHexByte(node6.getValue())));
                                                                                                                break;
                                                                                                            case "UserInfoID":
                                                                                                                //This leaf node indicates a ProSe user info ID as specified in 3GPP TS 23.303 [6].
                                                                                                                if(proSeUserEntryType==null)proSeUserEntryType=new ProSeUserEntryType();
                                                                                                                proSeUserEntryType.setUserInfoID(new String(valuesIntToHexByte(node6.getValue())));
                                                                                                                break;
                                                                                                            case "DisplayName":
                                                                                                                //This leaf node contains a human readable name that corresponds to the MCPTT private recipient ID.
                                                                                                                if(mcpttPrivateRecipients==null)mcpttPrivateRecipients=new EntryType();
                                                                                                                DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                                displayNameElementType.setValue(valuesToString(node6.getValue()));
                                                                                                                mcpttPrivateRecipients.setDisplayName(displayNameElementType);
                                                                                                                break;
                                                                                                            case "Usage":
                                                                                                                //This leaf node indicates the criteria to determine when initiation of an MCPTT emergency private call uses the MCPTT private recipient ID..
                                                                                                                /*
                                                                                                                    When set to 'LocallyDetermined' then if the MCPTT user selects an MCPTT ID then use that MCPTT ID for the MCPTT emergency private call, if the MCPTT user does not select a MCPTT ID then use the MCPTT ID identified by the MCPTT private recipient ID in subclause 5.2.29B for an on-network MCPTT emergency private call.
                                                                                                                    When set to 'UsePreConfigured' then use the MCPTT ID identified by the MCPTT private recipient ID in subclause 5.2.29B for an on-network MCPTT emergency private call.
                                                                                                                    When set to 'LocallyDetermined' then if the MCPTT user selects an MCPTT user then use the UserInfoID that corresponds to that MCPTT user for the MCPTT emergency private call, if the MCPTT user does not select a MCPTT user then use the User Info ID identified by the UserInfoID in subclause 5.2.29D for an off-network MCPTT emergency private call.
                                                                                                                    When set to 'UsePreConfigured' then use the User Info ID identified by the UserInfoID in subclause 5.2.29D for an off-network MCPTT emergency private call.
                                                                                                                */
                                                                                                                try{
                                                                                                                    if(mcpttPrivateRecipients==null)mcpttPrivateRecipients=new EntryType();
                                                                                                                    mcpttPrivateRecipients.setEntryInfo(EntryInfoTypeList.fromValue(valuesToString(node6.getValue())));
                                                                                                                }catch (Exception e){
                                                                                                                    Log.e(TAG, "Error in User profile MO: "+e.getMessage());
                                                                                                                }

                                                                                                                break;
                                                                                                            default:
                                                                                                                break;
                                                                                                        }

                                                                                                }
                                                                                                if(mcpttPrivateRecipients!=null)mcpttPrivateRecipientEntryType.setEntry(mcpttPrivateRecipients);
                                                                                                if(proSeUserEntryType!=null)mcpttPrivateRecipientEntryType.setProSeUserIDEntry(proSeUserEntryType);
                                                                                                break;
                                                                                            default:
                                                                                                break;
                                                                                        }
                                                                                }
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                }
                                                                break;

                                                            default:
                                                                break;
                                                        }
                                                }
                                            case "MCPTTGroupCall":
                                                //This interior node is a placeholder for the MCPTT group call configuration.
                                                if(commonType.getMCPTTGroupCall()==null){
                                                    ArrayList<MCPTTGroupCallType> mcpttGroupCallTypes=new ArrayList<>();
                                                    commonType.setMCPTTGroupCall(mcpttGroupCallTypes);
                                                }
                                                MCPTTGroupCallType mcpttGroupCallType=new MCPTTGroupCallType();
                                                commonType.getMCPTTGroupCall().add(mcpttGroupCallType);
                                                for(Node node3:node2.getNode()) {

                                                    if(node3!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "MaxSimultaneousCallsN6":
                                                                //This leaf node indicates the maximum number of simultaneously received MCPTT group calls (N6).
                                                                mcpttGroupCallType.setMaxSimultaneousCallsN6(valuesToBigIntegers(node3.getValue()));
                                                                break;
                                                            case "EmergencyCall":
                                                                //This interior node is a placeholder for the MCPTT emergency call policy.
                                                                if(mcpttGroupCallType.getEmergencyCall()==null){
                                                                    ArrayList<EmergencyCallType> emergencyCallTypes=new ArrayList<EmergencyCallType>();
                                                                    mcpttGroupCallType.setEmergencyCall(emergencyCallTypes);
                                                                }
                                                                EmergencyCallType emergencyCallType=new EmergencyCallType();
                                                                mcpttGroupCallType.getEmergencyCall().add(emergencyCallType);
                                                                for(Node node4:node3.getNode()) {
                                                                    if(node4!=null && node4.getNodeName()!=null)
                                                                        switch (node4.getNodeName()) {
                                                                            case "Enabled":
                                                                                //This leaf node indicates the authorisation to make an MCPTT emergency group call functionality enabled for MCPTT user.
                                                                                // When set to "true" the MCPTT user is authorised to make an MCPTT emergency group call functionality enabled.
                                                                                // When set to "false" the MCPTT user is not authorised to make an MCPTT emergency group call functionality enabled.
                                                                                Log.w(TAG,"Now, it don´t support Enabled in User profile");
                                                                                break;
                                                                            case "CancelMCPTTGroup":
                                                                                //	This leaf node indicates the authorisation to cancel an in progress MCPTT emergency group call associated with a group.
                                                                                // When set to "true" the MCPTT user is authorised to cancel a MCPTT emergency group call.
                                                                                // When set to "false" the MCPTT user is not authorised to cancel a MCTT emergency group call.
                                                                                Log.w(TAG,"Now, it don´t support CancelMCPTTGroup in User profile");
                                                                                break;
                                                                            case "MCPTTGroupInitiation":
                                                                                //This interior node is a placeholder for  the group used on initiation of an MCPTT emergency group call.
                                                                                MCPTTGroupInitiationEntryType mcpttGroupInitiationEntryType=new MCPTTGroupInitiationEntryType();
                                                                                emergencyCallType.setMCPTTGroupInitiation(mcpttGroupInitiationEntryType);
                                                                                for(Node node5:node4.getNode()) {
                                                                                    if(node5!=null && node5.getNodeName()!=null)
                                                                                        switch (node5.getNodeName()) {
                                                                                            case "Entry":
                                                                                                //This interior node is a placeholder for the details of the group used on initiation of an MCPTT emergency group call.
                                                                                                EntryType entryType=null;
                                                                                                //This interior node is a placeholder for the details of the MCPTT private recipient for an MCPTT emergency private call.
                                                                                                for(Node node6:node5.getNode()) {
                                                                                                    if(node6!=null && node6.getNodeName()!=null)
                                                                                                        switch (node6.getNodeName()) {
                                                                                                            case "GroupID":
                                                                                                                //This leaf node indicates the group used upon certain criteria on initiation of an MCPTT emergency group call.
                                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                                entryType.setUriEntry(valuesToString(node6.getValue()));
                                                                                                                break;
                                                                                                            case "DisplayName":
                                                                                                                //This leaf node contains a human readable name that corresponds to the Group ID.
                                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                                DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                                displayNameElementType.setValue(valuesToString(node6.getValue()));
                                                                                                                entryType.setDisplayName(displayNameElementType);

                                                                                                                break;
                                                                                                            case "Usage":
                                                                                                                //This leaf node indicates the criteria to determine when initiation of an MCPTT emergency group call uses the GroupID.
                                                                                                                // The valid values are 'UseCurrentlySelectedGroup' and 'DedicatedGroup'.
                                                                                                                // When set to 'UseCurrentlySelectedGroup' then if the MCPTT user has currently selected an MCPTT group then use that MCPTT group for an on-network MCPTT emergency group call, if the MCPTT user does not have a currently selected MCPTT group then use the MCPTT group identified by the GroupID in subclause 5.2.34B for an MCPTT emergency group call.
                                                                                                                // When set to 'DedicatedGroup' then use the MCPTT group identified by the GroupID in subclause 5.2.34B for an MCPTT emergency group call.
                                                                                                                try{
                                                                                                                    if(entryType==null)entryType=new EntryType();
                                                                                                                    entryType.setEntryInfo(EntryInfoTypeList.fromValue(valuesToString(node6.getValue())));
                                                                                                                }catch (Exception e){
                                                                                                                    Log.e(TAG, "Error in User profile MO: "+e.getMessage());
                                                                                                                }

                                                                                                                break;
                                                                                                            default:
                                                                                                                break;
                                                                                                        }

                                                                                                }
                                                                                                if(entryType!=null)mcpttGroupInitiationEntryType.setEntry(entryType);
                                                                                                break;
                                                                                            default:
                                                                                                break;
                                                                                        }
                                                                                }
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                }
                                                                break;
                                                            case "ImminentPerilCall":
                                                                //This interior node is a placeholder for the MCPTT emergency call policy.
                                                                if(mcpttGroupCallType.getImminentPerilCall()==null){
                                                                    ArrayList<ImminentPerilCallType> imminentPerilCallTypes=new ArrayList<ImminentPerilCallType>();
                                                                    mcpttGroupCallType.setImminentPerilCall(imminentPerilCallTypes);
                                                                }
                                                                ImminentPerilCallType imminentPerilCallType=new ImminentPerilCallType();
                                                                mcpttGroupCallType.getImminentPerilCall().add(imminentPerilCallType);
                                                                for(Node node4:node3.getNode()) {
                                                                    if(node4!=null && node4.getNodeName()!=null)
                                                                        switch (node4.getNodeName()) {
                                                                            case "Authorised":
                                                                                //This leaf node indicates the authorisation to make an Imminent Peril group call.
                                                                                // When set to "true" the MCPTT user is authorised to create an MCPTT imminent peril group call.
                                                                                // When set to "false" the MCPTT user is not authorised to create an MCPTT imminent peril group call.

                                                                                Log.w(TAG,"Now, it don´t support Authorised in User profile");
                                                                                break;
                                                                            case "Cancel":
                                                                                // This leaf node indicates the authorisation to make an Imminent Peril group call.
                                                                                // When set to "true" the MCPTT user is authorised to create an MCPTT imminent peril group call.
                                                                                // When set to "false" the MCPTT user is not authorised to create an MCPTT imminent peril group call.

                                                                                Log.w(TAG,"Now, it don´t support Cancel in User profile");
                                                                                break;
                                                                            case "MCPTTGroupInitiation":
                                                                                //This interior node is a placeholder for the group used on initiation of an MCPTT imminent peril group call.
                                                                                MCPTTGroupInitiationEntryType mcpttGroupInitiationEntryType=new MCPTTGroupInitiationEntryType();
                                                                                imminentPerilCallType.setMCPTTGroupInitiation(mcpttGroupInitiationEntryType);
                                                                                for(Node node5:node4.getNode()) {
                                                                                    if(node5!=null && node5.getNodeName()!=null)
                                                                                        switch (node5.getNodeName()) {
                                                                                            case "Entry":
                                                                                                //This interior node is a placeholder for the details of the group used on initiation of an imminent peril call.
                                                                                                EntryType entryType=null;
                                                                                                for(Node node6:node5.getNode()) {
                                                                                                    if(node6!=null && node6.getNodeName()!=null)
                                                                                                        switch (node6.getNodeName()) {
                                                                                                            case "GroupID":
                                                                                                                //This leaf node indicates the group used upon certain criteria on initiation of an MCPTT emergency group call.
                                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                                entryType.setUriEntry(valuesToString(node6.getValue()));
                                                                                                                break;
                                                                                                            case "DisplayName":
                                                                                                                //This leaf node contains a human readable name that corresponds to the Group ID.
                                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                                DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                                displayNameElementType.setValue(valuesToString(node6.getValue()));
                                                                                                                entryType.setDisplayName(displayNameElementType);

                                                                                                                break;
                                                                                                            case "Usage":
                                                                                                                //The valid values are 'UseCurrentlySelectedGroup' and 'DedicatedGroup'.
                                                                                                                // When set to 'UseCurrentlySelectedGroup' then if the MCPTT user has currently selected an MCPTT group then use that MCPTT group for an on-network MCPTT imminent peril group call, if the MCPTT user does not have a currently selected MCPTT group then use the MCPTT group identified by the GroupID in subclause 5.2.39B for an MCPTT imminent peril group call.
                                                                                                                // When set to 'DedicatedGroup' then use the MCPTT group identified by the GroupID in subclause 5.2.39B for an MCPTT imminent peril group call.
                                                                                                                try{
                                                                                                                    if(entryType==null)entryType=new EntryType();
                                                                                                                    entryType.setEntryInfo(EntryInfoTypeList.fromValue(valuesToString(node6.getValue())));
                                                                                                                }catch (Exception e){
                                                                                                                    Log.e(TAG, "Error in User profile MO: "+e.getMessage());
                                                                                                                }

                                                                                                                break;
                                                                                                            default:
                                                                                                                break;
                                                                                                        }

                                                                                                }
                                                                                                if(entryType!=null)mcpttGroupInitiationEntryType.setEntry(entryType);
                                                                                                break;
                                                                                            default:
                                                                                                break;
                                                                                        }
                                                                                }
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                }
                                                                break;
                                                            case "EmergencyAlert":
                                                                //This interior node is a placeholder for the MCPTT emergency alert policy.
                                                                if(mcpttGroupCallType.getEmergencyAlert()==null){
                                                                    ArrayList<EmergencyAlertType> emergencyAlertTypes=new ArrayList<EmergencyAlertType>();
                                                                    mcpttGroupCallType.setEmergencyAlert(emergencyAlertTypes);
                                                                }
                                                                EmergencyAlertType emergencyAlertType=new EmergencyAlertType();
                                                                mcpttGroupCallType.getEmergencyAlert().add(emergencyAlertType);
                                                                for(Node node4:node3.getNode()) {
                                                                    if(node4!=null && node4.getNodeName()!=null)
                                                                        switch (node4.getNodeName()) {
                                                                            case "Authorised":
                                                                                //This leaf node indicates the authorisation to make an Imminent Peril group call.
                                                                                // When set to "true" the MCPTT user is authorised to create an MCPTT imminent peril group call.
                                                                                // When set to "false" the MCPTT user is not authorised to create an MCPTT imminent peril group call.

                                                                                Log.w(TAG,"Now, it don´t support Authorised in User profile");
                                                                                break;
                                                                            case "Cancel":
                                                                                // This leaf node indicates the authorisation to make an Imminent Peril group call.
                                                                                // When set to "true" the MCPTT user is authorised to create an MCPTT imminent peril group call.
                                                                                // When set to "false" the MCPTT user is not authorised to create an MCPTT imminent peril group call.

                                                                                Log.w(TAG,"Now, it don´t support Cancel in User profile");
                                                                                break;
                                                                            case "Entry":

                                                                                //This interior node is a placeholder for the details of the group used on initiation of an imminent peril call.
                                                                                EntryType entryType=null;
                                                                                for(Node node5:node4.getNode()) {
                                                                                    if(node5!=null && node5.getNodeName()!=null)
                                                                                        switch (node5.getNodeName()) {
                                                                                            case "ID":
                                                                                                //This leaf node indicates the MCPTT group used upon certain criteria on initiation of an MCPTT emergency alert
                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                break;
                                                                                            case "DisplayName":
                                                                                                //This leaf node contains a human readable name that corresponds to the ID.
                                                                                                if(entryType==null)entryType=new EntryType();
                                                                                                DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                                entryType.setDisplayName(displayNameElementType);

                                                                                                break;
                                                                                            case "Usage":
                                                                                                //	This leaf node indicates the criteria to determine when initiation of an MCPTT emergency alert uses the ID.
                                                                                                // The valid values are 'LocallyDetermined', 'UseCurrentlySelectedGroup', 'UsePreConfigured'and 'DedicatedGroup'.
                                                                                                // When set to 'LocallyDetermined' then if the MCPTT user selects an MCPTT ID then use that MCPTT ID for an on-network MCPTT emergency alert, if the MCPTT user does not select a MCPTT ID then use the MCPTT ID identified by the ID in subclause 5.2.43B for an on-network MCPTT emergency alert.
                                                                                                // When set to 'UseCurrentlySelectedGroup' then if the MCPTT user has currently selected an MCPTT group then use that MCPTT group for an on-network MCPTT emergency alert, if the MCPTT user does not have a currently selected MCPTT group then use the MCPTT group identified by the ID in subclause 5.2.43B for an MCPTT emergency alert.
                                                                                                //When set to 'UsePreConfigured' then use the ID identified by the ID in subclause 5.2.43B for an on-network MCPTT emergency alert.
                                                                                                // When set to 'DedicatedGroup' then use the MCPTT group identified by the ID in subclause 5.2.43B for an MCPTT emergency alert.
                                                                                                try{
                                                                                                    if(entryType==null)entryType=new EntryType();
                                                                                                    entryType.setEntryInfo(EntryInfoTypeList.fromValue(valuesToString(node5.getValue())));
                                                                                                }catch (Exception e){
                                                                                                    Log.e(TAG, "Error in User profile MO: "+e.getMessage());
                                                                                                }

                                                                                                break;
                                                                                            default:
                                                                                                break;
                                                                                        }

                                                                                }
                                                                                if(entryType!=null)emergencyAlertType.setEntry(entryType);
                                                                                break;
                                                                            default:
                                                                                break;
                                                                        }
                                                                }
                                                                break;
                                                            case "Priority":
                                                                //	This leaf node indicates the priority of the MCPTT group calls.
                                                                // -	Values: 0-255
                                                                // The MCPTT group call with the lowest Priority value shall be considered as the MCPTT group call having the lowest level among the MCPTT group calls.
                                                                mcpttGroupCallType.setPriority(valuesToIntegers(node3.getValue()));
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "MCPTTGroupBroadcast":
                                                //This interior node is a placeholder for the group-broadcast group policy.
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "Authorised":
                                                                //	This leaf node indicates the authorisation to create a group-broadcast group.
                                                                // When set to "true" the MCPTT user is authorised to create a group-broadcast group.
                                                                // When set to "false" the MCPTT user is not authorised to create a group-broadcast group.
                                                                Log.w(TAG,"Now, it don´t support MCPTTGroupBroadcast->Authorised in User profile");
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "UserBroadcast":
                                                //This interior node is a placeholder for the user-broadcast group policy.
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "Authorised":
                                                                //		This leaf node indicates the authorisation to create a user-broadcast group.
                                                                // When set to "true" the MCPTT user is authorised to create a user-broadcast group.
                                                                // When set to "false" the MCPTT user is not authorised to create a user-broadcast group.
                                                                Log.w(TAG,"Now, it don´t support UserBroadcast->Authorised in User profile");
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                    break;
                                case "OnNetwork":
                                    if(mcpttUserProfile.getOnNetwork()==null){
                                        ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OnNetworkType> onNetworkTypes=new ArrayList<>();
                                        mcpttUserProfile.setOnNetwork(onNetworkTypes);
                                    }
                                    org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OnNetworkType onNetworkType=new org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OnNetworkType();
                                    mcpttUserProfile.getOnNetwork().add(onNetworkType);
                                    for(Node node2:node1.getNode()) {
                                        if(node2!=null && node2.getNodeName()!=null)
                                            switch (node2.getNodeName()) {
                                                case "MCPTTGroupList":
                                                    //This interior node is a placeholder for the list of on-network MCPTT groups that the MCPTT user is allowed to affiliate to.
                                                    if(onNetworkType.getmCPTTGroupInfo()==null){
                                                        ArrayList<ListEntryType> onNetworkTypes=new ArrayList<ListEntryType>();
                                                        onNetworkType.setmCPTTGroupInfo(onNetworkTypes);
                                                    }
                                                    ListEntryType listEntryType=new ListEntryType();
                                                    onNetworkType.getmCPTTGroupInfo().add(listEntryType);
                                                    //This interior node is a placeholder for the list of on-network MCPTT groups that the MCPTT user is allowed to affiliate to.
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null){
                                                            for(Node node4:node3.getNode()) {
                                                                //This interior node is a placeholder for one or more list of on-network MCPTT groups that the MCPTT user is allowed to affiliate to.
                                                                if(node4!=null && node4.getNodeName()!=null)
                                                                    switch (node4.getNodeName()) {
                                                                        case "Entry":
                                                                            //This interior node is a placeholder for the details of the on-network MCPTT groups that the MCPTT user is allowed to affiliate to.
                                                                            if(listEntryType.getEntry()==null){
                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                listEntryType.setEntry(entryTypes);
                                                                            }
                                                                            EntryType entryType=null;
                                                                            for(Node node5:node4.getNode()) {
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "MCPTTGroupID":
                                                                                            //This leaf node indicates the MCPTT group ID for the on-network MCPTT group that the MCPTT user is allowed to affiliate to.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                            break;
                                                                                        case "DisplayName":
                                                                                            //This leaf node contains a human readable name that corresponds to the MCPTT Group ID.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                            displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                            entryType.setDisplayName(displayNameElementType);

                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }

                                                                            }
                                                                            if(entryType!=null)listEntryType.getEntry().add(entryType);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "ImplicitAffiliations":
                                                    //This interior node is a placeholder for the implicit affiliation configuration.
                                                    if(onNetworkType.getImplicitAffiliations()==null){
                                                        ArrayList<ListEntryType> listEntryTypes=new ArrayList<ListEntryType>();
                                                        onNetworkType.setImplicitAffiliations(listEntryTypes);
                                                    }
                                                    ListEntryType listEntryType1=new ListEntryType();
                                                    onNetworkType.getImplicitAffiliations().add(listEntryType1);
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null){
                                                            for(Node node4:node3.getNode()) {
                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                if(node4!=null && node4.getNodeName()!=null)
                                                                    switch (node4.getNodeName()) {
                                                                        case "Entry":
                                                                            //This interior node is a placeholder for the details of the on-network MCPTT groups that the MCPTT user is implictly affiliated to.
                                                                            if(listEntryType1.getEntry()==null){
                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                listEntryType1.setEntry(entryTypes);
                                                                            }
                                                                            EntryType entryType=null;
                                                                            for(Node node5:node4.getNode()) {
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "MCPTTGroupID":
                                                                                            //	This leaf node indicates a MCPTT group ID to which the MCPTT user is implicitly affiliated to.
                                                                                            // The value is a "uri" attribute specified in OMA OMA-TS-XDM_Group-V1_1 [4].
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                            break;
                                                                                        case "DisplayName":
                                                                                            //This leaf node contains a human readable name that corresponds to the MCPTT Group ID.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                            displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                            entryType.setDisplayName(displayNameElementType);

                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }

                                                                            }
                                                                            if(entryType!=null)listEntryType1.getEntry().add(entryType);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "AllowedRegroup":
                                                    //	This leaf node indicates whether the MCPTT user is authorised to perform dynamic regrouping operations.
                                                    // When set to "true" the MCPTT user is authorised to perform dynamic regrouping operations.
                                                    // When set to "false" the MCPTT user is not authorised to perform dynamic regrouping operations.
                                                    Log.w(TAG,"Now, it don´t support AllowedRegroup in User profile");

                                                    break;
                                                case "AllowedPresenceStatus":
                                                    //	This leaf node indicates the presence status on the network of this MCPTT user is available.
                                                    // When set to "true" the presence status on the network of this MCPTT user is available.
                                                    // When set to "false" the presence status on the network of this MCPTT user is not available. This is the default if this leaf node is not present.
                                                    Log.w(TAG,"Now, it don´t support AllowedRegroup in User profile");
                                                    break;
                                                case "AllowedPresence":
                                                    //This leaf node indicates whether the MCPTT user is authorised to obtain whether a particular MCPTT User is present on the network.
                                                    //When set to "true" the MCPTT user is authorised to obtain whether a particular MCPTT User is present on the network.
                                                    // When set to "false" the MCPTT user is not authorised to obtain whether a particular MCPTT User is present on the network.
                                                    Log.w(TAG,"Now, it don´t support AllowedPresence in User profile");
                                                    break;
                                                case "EnabledParticipation":
                                                    //	This leaf node indicates whether the MCPTT user is allowed to participate in MCPTT private calls that they are invited to.
                                                    // When set to "true" the MCPTT user is allowed to participate in MCPTT private calls that they are invited to.
                                                    // When set to "false" the MCPTT user is not allowed to participate in MCPTT private calls that they are invited to.
                                                    Log.w(TAG,"Now, it don´t support EnabledParticipation in User profile");
                                                    break;
                                                case "AllowedTransmission":
                                                    //	This leaf node indicates whether the MCPTT user is authorised to override transmission in a MCPTT private call.
                                                    // When set to "true" the MCPTT user is authorised to override transmission in a MCPTT private call.
                                                    // When set to "false" the MCPTT user is not authorised to override transmission in a MCPTT private call.
                                                    Log.w(TAG,"Now, it don´t support AllowedTransmission in User profile");
                                                    break;
                                                case "AllowedManualSwitch":
                                                    //	This leaf node indicates whether the MCPTT user is authorised to manually switch to off-network operation while in on-network operation.
                                                    // When set to "true" the MCPTT user is authorised to manually switch to off-network operation while in on-network operation.
                                                    // When set to "false" the MCPTT user is not authorised to manually switch to off-network operation while in on-network operation.
                                                    Log.w(TAG,"Now, it don´t support AllowedManualSwitch in User profile");
                                                    break;
                                                case "PrivateCall":
                                                    //This interior node is a placeholder for the MCPTT private call configuration.
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null)
                                                            switch (node3.getNodeName()) {
                                                                case "EmergencyAlert":
                                                                    //This interior node is a placeholder for the MCPTT private emergency alert policy.
                                                                    if(onNetworkType.getPrivateEmergencyAlert()==null){
                                                                        ArrayList<EmergencyAlertType> emergencyAlertTypes=new ArrayList<>();
                                                                        onNetworkType.setPrivateEmergencyAlert(emergencyAlertTypes);
                                                                    }
                                                                    EmergencyAlertType emergencyAlertType=new EmergencyAlertType();
                                                                    onNetworkType.getPrivateEmergencyAlert().add(emergencyAlertType);
                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null && node4.getNodeName()!=null)
                                                                            switch (node4.getNodeName()) {
                                                                                case "Entry":
                                                                                    //This leaf node indicates the MCPTT user ID used upon certain criteria on initiation of an MCPTT private emergency alert for on-network.
                                                                                    EntryType entryType=null;
                                                                                    for(Node node5:node4.getNode()) {
                                                                                        if(node5!=null && node5.getNodeName()!=null)
                                                                                            switch (node5.getNodeName()) {
                                                                                                case "ID":
                                                                                                    //This leaf node indicates the MCPTT user ID used upon certain criteria on initiation of an MCPTT private emergency alert for on-network.
                                                                                                    if(entryType==null)entryType=new EntryType();
                                                                                                    entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                    break;
                                                                                                case "DisplayName":
                                                                                                    //This leaf node contains a human readable name that corresponds to the ID.
                                                                                                    if(entryType==null)entryType=new EntryType();
                                                                                                    DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                                    displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                                    entryType.setDisplayName(displayNameElementType);

                                                                                                    break;
                                                                                                case "Usage":
                                                                                                    //	This leaf node indicates the criteria to determine when initiation of an MCPTT private emergency alert uses the ID.
                                                                                                    // The valid values are 'LocallyDetermined' and 'UsePreConfigured'.
                                                                                                    // When set to 'LocallyDetermined' then if the MCPTT user selects an MCPTT ID then use that MCPTT ID for an on-network MCPTT private emergency alert, if the MCPTT user does not select a MCPTT ID then use the MCPTT ID identified by the ID in subclause 5.2.48M for an on-network MCPTT private emergency alert.
                                                                                                    // When set to 'UsePreConfigured' then use the ID identified by the ID in subclause 5.2.48M for an on-network MCPTT private emergency alert.

                                                                                                    try{
                                                                                                        if(entryType==null)entryType=new EntryType();
                                                                                                        entryType.setEntryInfo(EntryInfoTypeList.fromValue(valuesToString(node5.getValue())));
                                                                                                    }catch (Exception e){
                                                                                                        Log.e(TAG, "Error in User profile MO: "+e.getMessage());
                                                                                                    }

                                                                                                    break;
                                                                                                default:
                                                                                                    break;
                                                                                            }

                                                                                    }
                                                                                    if(entryType!=null)emergencyAlertType.setEntry(entryType);
                                                                                    break;
                                                                                default:
                                                                                    break;
                                                                            }
                                                                    }


                                                                    break;
                                                                case "AllowedCallBackRequest":
                                                                    //	This leaf node indicates whether the MCPTT user is allowed to request a private call call-back.
                                                                    // When set to "true" the MCPTT user is allowed to request a private call call-back.
                                                                    // When set to "false" the MCPTT user is not allowed to request a private call call-back.
                                                                    Log.w(TAG,"Now, it don´t support AllowedCallBackRequest in User profile");
                                                                    break;
                                                                case "AllowedCallBackCancelRequest":
                                                                    //This leaf node indicates whether the MCPTT user is allowed to cancel an outstanding private call call-back request.
                                                                    // When set to "true" the MCPTT user is allowed to cancel an outstanding private call call-back request.
                                                                    // When set to "false" the the MCPTT user is not allowed to cancel an outstanding private call call-back request.
                                                                    Log.w(TAG,"Now, it don´t support AllowedCallBackCancelRequest in User profile");
                                                                    break;
                                                                case "AllowedRemoteInitiatedAmbientListening":
                                                                    //	This leaf node indicates whether the MCPTT user is allowed to request a remote initiated ambient listening call.
                                                                    // When set to "true" the MCPTT user is allowed to request a remote initiated ambient listening call.
                                                                    // When set to "false" the MCPTT user is not allowed to request a remote initiated ambient listening call.

                                                                    Log.w(TAG,"Now, it don´t support AllowedRemoteInitiatedAmbientListening in User profile");
                                                                    break;
                                                                case "AllowedLocallyInitiatedAmbientListening":
                                                                    //	This leaf node indicates whether the MCPTT user is allowed to request a locally initiated ambient listening call.
                                                                    // When set to "true" the MCPTT user is allowed to request a locally initiated ambient listening call.
                                                                    // When set to "false" the MCPTT user is not allowed to request a locally initiated ambient listening call.

                                                                    Log.w(TAG,"Now, it don´t support AllowedLocallyInitiatedAmbientListening in User profile");
                                                                    break;
                                                                case "AllowedRequestFirstToAnswerCall":
                                                                    //	This leaf node indicates whether the MCPTT user is allowed to request a first to answer call.
                                                                    // When set to "true" the MCPTT user is allowed to request a first to answer call.
                                                                    // When set to "false" the MCPTT user is not allowed to request a first to answer call

                                                                    Log.w(TAG,"Now, it don´t support AllowedRequestFirstToAnswerCall in User profile");
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                    }
                                                    break;
                                                case "RemoteGroupSelection":
                                                    //This interior node is a placeholder for one or more remote group selection configuration elements
                                                    if(onNetworkType.getRemoteGroupSelectionURIList()==null){
                                                        ArrayList<ListEntryType> listEntryTypes=new ArrayList<ListEntryType>();
                                                        onNetworkType.setRemoteGroupSelectionURIList(listEntryTypes);
                                                    }
                                                    ListEntryType listEntryType2=new ListEntryType();
                                                    onNetworkType.getRemoteGroupSelectionURIList().add(listEntryType2);
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null){
                                                            for(Node node4:node3.getNode()) {
                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                if(node4!=null && node4.getNodeName()!=null)
                                                                    switch (node4.getNodeName()) {
                                                                        case "Entry":
                                                                            //This interior node is a placeholder for the details of the on-network MCPTT users whose selected MCPTT group is allowed to be remotely changed by the MCPTT user.
                                                                            if(listEntryType2.getEntry()==null){
                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                listEntryType2.setEntry(entryTypes);
                                                                            }
                                                                            EntryType entryType=null;
                                                                            for(Node node5:node4.getNode()) {
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "MCPTTID":
                                                                                            //This leaf node indicates a MCPTT ID of an MCPTT user whose selected MCPTT group is allowed to be remotely changed by the MCPTT user.
                                                                                            // The value is a "uri" attribute specified in OMA OMA-TS-XDM_Group-V1_1 [4].
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                            break;
                                                                                        case "DisplayName":
                                                                                            //This leaf node contains a human readable name that corresponds to the MCPTT ID of the MCPTT user.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                            displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                            entryType.setDisplayName(displayNameElementType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }

                                                                            }
                                                                            if(entryType!=null)listEntryType2.getEntry().add(entryType);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "GroupServerInfo":
                                                    //This interior node is a placeholder for the configured identity management an group management servers for the groups contained in the MCPTTGroupList
                                                    if(onNetworkType.getGroupServerInfo()==null){
                                                        ArrayList<GroupServerInfoType> groupServerInfoTypes=new ArrayList<GroupServerInfoType>();
                                                        onNetworkType.setGroupServerInfo(groupServerInfoTypes);
                                                    }
                                                    GroupServerInfoType groupServerInfoType=new GroupServerInfoType();
                                                    onNetworkType.getGroupServerInfo().add(groupServerInfoType);
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null)
                                                            switch (node3.getNodeName()) {
                                                                case "GMSServList":
                                                                    //	This interior node is a placeholder for the list of MCPTT group management server for the groups contained in the MCPTTGroupList.
                                                                    ListEntryType listEntryType3=new ListEntryType();
                                                                    groupServerInfoType.setGMSServId(listEntryType3);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for the details of the on-network MCPTT users whose selected MCPTT group is allowed to be remotely changed by the MCPTT user.
                                                                                            if(listEntryType3.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType3.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "GMSServID":
                                                                                                            //This leaf node indicates the identity (URI) of the GMS owning a specific group contained in the MCPTTGroupList.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType3.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }

                                                                    break;
                                                                case "IDMSTokenEndpointList":
                                                                    //This interior node is a placeholder for the list of MCPTT IDMS token endpoints for the groups contained in the MCPTTGroupList.
                                                                    ListEntryType listEntryType4=new ListEntryType();
                                                                    groupServerInfoType.setIDMSTokenEndpoint(listEntryType4);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for the IDMS token endpoint for a specific groups contained in the MCPTTGroupList.
                                                                                            if(listEntryType4.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType4.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "IDMSTokenID":
                                                                                                            //This interior node is a placeholder for the IDMS token endpoint for a specific groups contained in the MCPTTGroupList.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType4.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
                                                                    break;
                                                                case "KMSURIList":
                                                                    //This interior node is a placeholder for the list of KMS identities (URIs) for the groups contained in the MCPTTGroupList.
                                                                    ListEntryType listEntryType5=new ListEntryType();
                                                                    groupServerInfoType.setKMSURI(listEntryType5);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for the KMS identity (URI) for a specific group contained in the MCPTTGroupList.
                                                                                            if(listEntryType5.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType5.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "KMSURI":
                                                                                                            //This leaf node indicates the identity (URI) of the KMS identity (URI) for a specific group contained in the MCPTTGroupList. If the value is empty, the KMS identity (URI) (kms) present in the MCS UE initial configuration MO is used.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType5.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
                                                                    break;

                                                                default:
                                                                    break;
                                                            }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                    }
                                    break;
                                case "OffNetwork":
                                    if(mcpttUserProfile.getOffNetwork()==null){
                                        ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OffNetworkType> offNetworkTypes=new ArrayList<>();
                                        mcpttUserProfile.setOffNetwork(offNetworkTypes);
                                    }
                                    org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OffNetworkType offNetworkType=new org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OffNetworkType();
                                    mcpttUserProfile.getOffNetwork().add(offNetworkType);
                                    for(Node node2:node1.getNode()) {
                                        if(node2!=null && node2.getNodeName()!=null)
                                            switch (node2.getNodeName()) {
                                                case "Authorised":
                                                    //	This leaf node indicates the authorisation for off-network services.
                                                    // When set to "true" the MCPTT user is authorised for off-network.
                                                    // When set to "false" the MCPTT user is not authorised for off-network operation.
                                                    Log.w(TAG,"Now, it don´t support Authorised in User profile");
                                                    break;
                                                case "MCPTTGroupInfo":
                                                    //	This interior node is a placeholder for one or more group information.
                                                    if(offNetworkType.getMcpttGroupInfo()==null){
                                                        ArrayList<ListEntryType> listEntryTypes=new ArrayList<ListEntryType>();
                                                        offNetworkType.setMcpttGroupInfo(listEntryTypes);
                                                    }
                                                    ListEntryType listEntryType2=new ListEntryType();
                                                    offNetworkType.getMcpttGroupInfo().add(listEntryType2);
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null){
                                                            for(Node node4:node3.getNode()) {
                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                if(node4!=null && node4.getNodeName()!=null)
                                                                    switch (node4.getNodeName()) {
                                                                        case "Entry":
                                                                            //This interior node is a placeholder for one or more off-network MCPTT groups for use by an MCPTT user.
                                                                            if(listEntryType2.getEntry()==null){
                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                listEntryType2.setEntry(entryTypes);
                                                                            }
                                                                            EntryType entryType=null;
                                                                            for(Node node5:node4.getNode()) {
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "MCPTTGroupID":
                                                                                            //This leaf node indicates an off-network MCPTT group for use by an MCPTT user.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                            break;
                                                                                        case "DisplayName":
                                                                                            //This leaf node contains a human readable name that corresponds to the MCPTT group represented by the MCPTT group ID.
                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                            DisplayNameElementType displayNameElementType=new DisplayNameElementType();
                                                                                            displayNameElementType.setValue(valuesToString(node5.getValue()));
                                                                                            entryType.setDisplayName(displayNameElementType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }

                                                                            }
                                                                            if(entryType!=null)listEntryType2.getEntry().add(entryType);
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case "AllowedListen":
//                                                    This leaf node indicates whether the MCPTT user is allowed to listen both overriding and overriden.
//                                                    When set to "true" the MCPTT user is allowed to listen both overriding and overriden.
//                                                        When set to "false" the MCPTT user is not allowed to listen both overriding and overriden.
                                                    Log.w(TAG,"Now, it don´t support AllowedListen in User profile");
                                                    break;
                                                case "AllowedTransmission":
//                                                    //		This leaf node indicates whether the MCPTT user is allowed to transmit in case of override (overriding and/or overridden).
//                                                    When set to "true" the MCPTT user is allowed to transmit in case of override (overriding and/or overridden).
//                                                        When set to "false" the MCPTT user is not allowed to transmit in case of override (overriding and/or overridden).

                                                        Log.w(TAG,"Now, it don´t support AllowedTransmission in User profile");
                                                    break;
                                                case "EmergencyCallChange":
//                                                    This leaf node indicates the authorization for a participant to change an off-network group call in-progress to an off-network MCPTT emergency group call.
//                                                    When set to "true" the MCPTT user is authorised to change an MCPTT emergency group call.
//                                                    When set to "false" the MCPTT user is not authorised to change an MCPTT emergency group call.

                                                        Log.w(TAG,"Now, it don´t support EmergencyCallChange in User profile");
                                                    break;
                                                case "ImminentPerilCallChange":
//                                                    This leaf node indicates the authorization for a participant to change an off-network group call in-progress to an off-network MCPTT imminent peril group call.
//                                                        When set to "true" the MCPTT user is authorised to change an MCPTT imminent peril group call.
//                                                        When set to "false" the MCPTT user is not authorised to change an MCPTT imminent peril group call.

                                                    Log.w(TAG,"Now, it don´t support ImminentPerilCallChange in User profile");
                                                    break;
                                                case "UserInfoID":
                                                    //	This leaf node indicates the ProSe user info ID as specified in 3GPP TS 23.303 [6].
                                                    Log.w(TAG,"Now, it don´t support UserInfoID in User profile");
                                                    break;
                                                case "GroupServerInfo":
                                                    //This interior node is a placeholder for the configured identity management an group management servers for the groups contained in the off-network MCPTTGroupList
                                                    if(offNetworkType.getGroupServerInfo()==null){
                                                        ArrayList<GroupServerInfoType> groupServerInfoTypes=new ArrayList<GroupServerInfoType>();
                                                        offNetworkType.setGroupServerInfo(groupServerInfoTypes);
                                                    }
                                                    GroupServerInfoType groupServerInfoType=new GroupServerInfoType();
                                                    offNetworkType.getGroupServerInfo().add(groupServerInfoType);
                                                    for(Node node3:node2.getNode()) {
                                                        if(node3!=null)
                                                            switch (node3.getNodeName()) {
                                                                case "GMSServList":
                                                                    //	This interior node is a placeholder for the list of MCPTT group management server for the groups contained in the off-network MCPTTGroupList.
                                                                    ListEntryType listEntryType3=new ListEntryType();
                                                                    groupServerInfoType.setGMSServId(listEntryType3);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for one or more implicit affiliation configuration.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for identity of the GMS owning a specific group contained in the off-network MCPTTGroupList.
                                                                                            if(listEntryType3.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType3.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "GMSServID":
                                                                                                            //This leaf node indicates the identity (URI) of the GMS owning a specific group contained in the off-network MCPTTGroupList.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType3.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }

                                                                    break;
                                                                case "IDMSTokenEndpointList":
                                                                    //This interior node is a placeholder for the list of MCPTT IDMS token endpoints for the groups contained in the off-network MCPTTGroupList.
                                                                    ListEntryType listEntryType4=new ListEntryType();
                                                                    groupServerInfoType.setIDMSTokenEndpoint(listEntryType4);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for the IDMS token endpoint for a specific group contained in the off-network MCPTTGroupList.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for the IDMS token endpoint for a specific group contained in the off-network MCPTTGroupList.
                                                                                            if(listEntryType4.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType4.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "IDMSTokenID":
                                                                                                            //This leaf node indicates the identity (URI) of the IDMS token endpoint for a specific group contained in the off-network MCPTTGroupList. If the value is empty, the IDMS identities (IDMSAuthEndpoint and IDMSTokenEndpoint) present in the MCS UE initial configuration MO are used.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType4.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
                                                                    break;
                                                                case "KMSURIList":
                                                                    //This interior node is a placeholder for the list of KMS identities (URIs) for the groups contained in the off-network MCPTTGroupList.
                                                                    ListEntryType listEntryType5=new ListEntryType();
                                                                    groupServerInfoType.setKMSURI(listEntryType5);


                                                                    for(Node node4:node3.getNode()) {
                                                                        if(node4!=null){
                                                                            for(Node node5:node4.getNode()) {
                                                                                //This interior node is a placeholder for the KMS identity (URI) for a specific group contained in the off-network MCPTTGroupList.
                                                                                if(node5!=null && node5.getNodeName()!=null)
                                                                                    switch (node5.getNodeName()) {
                                                                                        case "Entry":
                                                                                            //This interior node is a placeholder for the KMS identity (URI) for a specific group contained in the off-network MCPTTGroupList.
                                                                                            if(listEntryType5.getEntry()==null){
                                                                                                ArrayList<EntryType> entryTypes=new ArrayList<EntryType>();
                                                                                                listEntryType5.setEntry(entryTypes);
                                                                                            }
                                                                                            EntryType entryType=null;
                                                                                            for(Node node6:node5.getNode()) {
                                                                                                if(node6!=null && node6.getNodeName()!=null)
                                                                                                    switch (node6.getNodeName()) {
                                                                                                        case "KMSURI":
                                                                                                            //This leaf node indicates the identity (URI) of the KMS identity (URI) for a specific group contained in the off-network MCPTTGroupList. If the value is empty, the KMS identity (URI) (kms) present in the MCS UE initial configuration MO is used.
                                                                                                            if(entryType==null)entryType=new EntryType();
                                                                                                            entryType.setUriEntry(valuesToString(node5.getValue()));
                                                                                                            break;
                                                                                                        default:
                                                                                                            break;
                                                                                                    }

                                                                                            }
                                                                                            if(entryType!=null)listEntryType5.getEntry().add(entryType);
                                                                                            break;
                                                                                        default:
                                                                                            break;
                                                                                    }
                                                                            }
                                                                        }
                                                                    }
                                                                    break;

                                                                default:
                                                                    break;
                                                            }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                    }
                                    break;
                                case "Status":
//                                    This leaf node indicates whether this MCPTT user profile is enabled or disabled.
//                                        When set to "true" this MCPTT user profile is enabled.
//                                        When set to "false" this MCPTT user profile is disabled.
                                    Boolean resultB=null;
                                    if((resultB=valuesToBool(node1.getValue()))!=null)
                                        mcpttUserProfile.setStatus(resultB);
                                    break;
                                default:
                                    System.out.println("No logic: "+node1.getNodeName());
                                    break;
                            }
                        }


            }
            return mcpttUserProfile;
        }

        public static ServiceConfigurationInfoType generateServiceConfigurationInfoType(MgmtTree mgmtTree){
            if(mgmtTree== null || mgmtTree.getNode()==null)return null;
            ServiceConfigurationInfoType serviceConfigurationInfoType=new ServiceConfigurationInfoType();
            for(Node node:mgmtTree.getNode()){
                //if(node!=null && node.getNodeName()!=null)
                for(Node node1:node.getNode()) {
                    ServiceConfigurationParamsType serviceConfigurationParamsType=null;
                    if((serviceConfigurationParamsType=serviceConfigurationInfoType.getServiceConfigurationParams())==null){
                        serviceConfigurationParamsType = new ServiceConfigurationParamsType();
                        serviceConfigurationInfoType.setServiceConfigurationParams(serviceConfigurationParamsType);
                    }
                    if (node1 != null && node1.getNodeName() != null)
                        switch (node1.getNodeName()) {
                            case "Name":
                                if (node.getValue() != null && node.getValue().get(0) != null) {
                                    serviceConfigurationParamsType.setDomain(valuesToString(node.getValue()));
                                }
                                break;
                            case "Ext":
                                break;
                            case "Common":
                                //This interior node represents a container for the common network operation which means both on-network operation and off-network operation.
                                if(serviceConfigurationParamsType.getCommon()==null){
                                    ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.CommonType> commonTypes = new ArrayList<>();
                                    serviceConfigurationParamsType.setCommon(commonTypes);
                                }

                                org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.CommonType commonType = new org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.CommonType();
                                serviceConfigurationParamsType.getCommon().add(commonType);
                                for (Node node2 : node1.getNode()) {
                                    if (node2 != null)
                                        switch (node2.getNodeName()) {
                                            case "BroadcastMCPTTGroupCall":
                                                //This interior node is a placeholder for Broadcast MCPTT Group Call configuration.
                                                BroadcastGroupType broadcastGroupType = new BroadcastGroupType();
                                                commonType.setBroadcastGroup(broadcastGroupType);
                                                for (Node node3 : node2.getNode()) {
                                                    if (node3 != null)
                                                        switch (node3.getNodeName()) {
                                                            case "NumLevelGroupHierarchy":
//                                                                This leaf node indicates the number of levels of group hierarchy for group-broadcast groups.
//                                                                    -	Values: 0-255
//                                                                The group-broadcast group with the lowest NumLevelGroupHierarchy value shall be considered as the group-broadcast group having the lowest level among the groups.
                                                                broadcastGroupType.setNumLevelsGroupHierarchy(valuesToInteger(node3.getValue()));
                                                                break;
                                                            case "NumLevelUserHierarchy":
//                                                                This leaf node indicates the number of levels of user hierarchy for user-broadcast groups.
//                                                                    -	Values: 0-255
//                                                                The user-broadcast group with the lowest NumLevelUserHierarchy value shall be considered as the user-broadcast group the lowest level among the groups
                                                                broadcastGroupType.setNumLevelsUserHierarchy(valuesToInteger(node3.getValue()));
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "MinLengthAliasID":
                                                //This interior node represents a container for off-network operation.
                                                commonType.setMinLengthAlias(valuesToInteger(node2.getValue()));
                                                break;
                                            default:
                                                break;
                                        }
                                }
                                break;
                            case "OffNetwork":
                                //This interior node represents a container for off-network operation.
                                if (serviceConfigurationParamsType.getOffNetwork() == null) {
                                    ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.OffNetworkType> offNetworkTypes = new ArrayList<org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.OffNetworkType>();
                                    serviceConfigurationParamsType.setOffNetwork(offNetworkTypes);
                                }
                                org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.OffNetworkType offNetworkType = new org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.OffNetworkType();
                                serviceConfigurationParamsType.getOffNetwork().add(offNetworkType);
                                for(Node node2:node1.getNode()) {
                                    if(node2!=null && node2.getNodeName()!=null)
                                        switch (node2.getNodeName()) {
                                            case "PrivateCall":
                                                //This interior node is a placeholder for private call configuration.
                                                PrivateCallType privateCallType= new PrivateCallType();
                                                offNetworkType.setPrivateCall(privateCallType);
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null && node3.getNodeName()!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "MaxDuration":
//                                                                This leaf node indicates max private call (with floor control) duration.
//                                                                    -	Values: 0-65535
//                                                                The MaxDuration time is in seconds.
                                                                privateCallType.setMaxDurationWithFloorControl(valuesToString(node3.getValue()));
                                                                break;
                                                            case "HangTime":
//                                                                This leaf node indicates hang timer for private calls (with floor control).
//                                                                    -	Values: 0-65535
//                                                                The HangTime is in seconds.
                                                                privateCallType.setMaxDurationWithFloorControl(valuesToString(node3.getValue()));
                                                                break;
                                                            case "CancelTimeout":
//                                                                This leaf node indicates timeout value for the cancellation of an in progress emergency for an MCPTT private call.
//                                                                -	Values: 0-65535
//                                                                The CancelTimeout is in seconds.
                                                                Log.w(TAG,"Now, it don´t support CancelTimeout in Service Configure");
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "EmergencyCall":
                                                //This interior node indicates a placeholder for the MCPTT emergency call policy.
                                                org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.EmergencyCallType emergencyCallType=new org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.EmergencyCallType();
                                                offNetworkType.setEmergencyCall(emergencyCallType);
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null && node3.getNodeName()!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "MCPTTGroupTimeout":
//                                                                This leaf node indicates time limit for an in progress MCPTT emergency call related to an MCPTT group.
//                                                                    -	Values: 0-65535
//                                                                The GroupTimeout is in seconds.
                                                                    emergencyCallType.setGroupTimeLimit(valuesToString(node3.getValue()));
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "NumLevelHierarchy":
//                                                This leaf node indicates the number of levels of hierarchy for floor control override in off-network.
//                                                    -	Values: 4-256
//                                                The request with the lowest NumLevelHierarchy value shall be considered as the request having the lowest priority level given to override an active transmission among the requests.
                                                offNetworkType.setNumLevelsPriorityHierarchy(valuesToInteger(node2.getValue()));
                                                break;
                                            case "TransmitTimeout":
//                                                This leaf node indicates transmit time limit from a single request to transmit in a group or private call.
//                                                -	Values: 0-65535
//                                                The TransmitTimeout is in seconds.
                                                if(offNetworkType.getTransmitTime()==null){
                                                        TransmitTimeType transmitTimeType=new TransmitTimeType();
                                                    offNetworkType.setTransmitTime(transmitTimeType);
                                                }
                                                offNetworkType.getTransmitTime().setTimeLimit(valuesToString(node2.getValue()));

                                                break;
                                            case "TransmissionWarning":
//                                               	This leaf node indicates configuration of warning time before time limit of transmission is reached (off-network).
//                                                -	Values: 0-255
//                                                The TransmissionWarning time is in seconds.

                                                if(offNetworkType.getTransmitTime()==null){
                                                    TransmitTimeType transmitTimeType=new TransmitTimeType();
                                                    offNetworkType.setTransmitTime(transmitTimeType);
                                                }
                                                offNetworkType.getTransmitTime().setTimeWarning(valuesToString(node2.getValue()));
                                                break;

                                            case "HangTimeWarning":
//                                                This leaf node indicates configuration of warning time before hang time is reached (off-network).
//                                                    -	Values: 0-255
//                                                The HangTimeWarning time is in seconds.

                                                offNetworkType.setHangTimeWarning(valuesToString(node2.getValue()));
                                                break;

                                            case "DefaultPPPP":
                                                //This interior node is a placeholder for the default ProSe Per-Packet Priority (PPPP) configuration.
                                                DefaultProsePerPacketPriorityType defaultProsePerPacketPriorityType=new DefaultProsePerPacketPriorityType();
                                                offNetworkType.setDefaultProsePerPacketPriority(defaultProsePerPacketPriorityType);
                                                for(Node node3:node2.getNode()) {
                                                    if(node3!=null && node3.getNodeName()!=null)
                                                        switch (node3.getNodeName()) {
                                                            case "MCPTTPrivateCallSignalling":
//                                                                This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT private call signalling.
//                                                                -	Values: 1-8
//                                                                The MCPTT user data with the lowest ProSe Per-Packet Priority value shall be considered as the MCPTT user data having the highest priority among the MCPTT user data.
                                                                defaultProsePerPacketPriorityType.setMcpttPrivateCallSignalling(valuesToInteger(node3.getValue()));
                                                                break;
                                                            case "MCPTTPrivateCallMedia":
//                                                                This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT private call media.
//                                                                -	Values: 1-8
//                                                                The MCPTT user data with the lowest ProSe Per-Packet Priority value shall be considered as the MCPTT user data having the highest priority among the MCPTT user data.
                                                                defaultProsePerPacketPriorityType.setMcpttPrivateCallMedia(valuesToInteger(node3.getValue()));
                                                                break;
                                                            case "MCPTTEmergencyPrivateCallSignalling":
//                                                                This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT emerency private call signalling.
//                                                                -	Values: 1-8
//                                                                The MCPTT user data with the lowest ProSe Per-Packet Priority value shall be considered as the MCPTT user data having the highest priority among the MCPTT user data.

                                                                defaultProsePerPacketPriorityType.setMcpttEmergencyPrivateCallSignalling(valuesToInteger(node3.getValue()));
                                                                break;
                                                            case "MCPTTEmergencyPrivateCallMedia":
//                                                                This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT emerency private call media.
//                                                                -	Values: 1-8
//                                                                The MCPTT user data with the lowest ProSe Per-Packet Priority
                                                                defaultProsePerPacketPriorityType.setMcpttEmergencyPrivateCallMedia(valuesToInteger(node3.getValue()));
                                                                break;
                                                            default:
                                                                break;
                                                        }
                                                }
                                                break;
                                            case "LogMetadata":
//                                                This leaf node indicates whether logging of metadata for MCPTT group calls, MCPTT private calls and non-call activities is permitted.
//                                                    When set to "true" logging of metadata for MCPTT group calls, MCPTT private calls and non-call activities, is enabled.
//                                                    When set to "false" logging of metadata for MCPTT group calls, MCPTT private calls and non-call activities, is not enabled.
                                                offNetworkType.setAllowLogMetadata(valuesToBool(node2.getValue()));
                                                break;

                                            default:
                                                break;
                                        }
                                }
                                break;
                            default:
                                break;
                            }
                    }

            }
            return serviceConfigurationInfoType;
        }

        public static Group generateGroupConfiguration(MgmtTree mgmtTree){
        if(mgmtTree== null || mgmtTree.getNode()==null)return null;
        Group group=new Group();
        for(Node node:mgmtTree.getNode()){
            for(Node node1:node.getNode()) {
                if (node1 != null && node1.getNodeName() != null){
                    switch (node1.getNodeName()) {
                        case "Name":
                            //The Name leaf is a name for the MCPTT group configuration settings.
                            break;
                        case "Ext":
                            break;
                        default:

                            break;
                    }
                }else{
                    if(group.getListService()==null){
                        ArrayList<ListServiceType> listServiceTypes=new ArrayList<ListServiceType>();
                        group.setListService(listServiceTypes);
                    }
                    for(Node node2:node1.getNode()) {
                        if(node2!=null && node2.getNodeName()!=null){
                            ListServiceType listServiceType=new ListServiceType();
                            group.getListService().add(listServiceType);
                                    switch (node2.getNodeName()) {
                                        case "Common":
                                            //This interior node represents a container for the common network operation which means both on-network operation and off-network operation.
                                            if(listServiceType.getRuleset()==null){
                                                listServiceType.setRuleset(new Ruleset());
                                            }
                                            if(listServiceType.getRuleset().getRule()==null){
                                                listServiceType.getRuleset().setRule(new ArrayList<RuleType>());
                                            }
                                            ExtensibleType extensibleType=null;
                                            if(listServiceType.getRuleset().getRule().isEmpty() ||
                                                    listServiceType.getRuleset().getRule().get(0)==null){
                                                RuleType ruleType=new RuleType();
                                                extensibleType=new ExtensibleType();
                                                ruleType.setActions(extensibleType);
                                                listServiceType.getRuleset().getRule().add(ruleType);
                                            }else{
                                                extensibleType=listServiceType.getRuleset().getRule().get(0).getActions();
                                            }
                                            for(Node node3:node2.getNode()) {
                                                if(node3!=null && node3.getNodeName()!=null)
                                                    switch (node3.getNodeName()) {
                                                        case "MCPTTGroupID":
                                                            //This leaf node indicates the MCPTT group ID.
                                                            listServiceType.setUri(valuesToString(node3.getValue()));
                                                            break;
                                                        case "MCPTTGroupAlias":
                                                            //This leaf node indicates the group alias.
                                                            DisplayNameType displayNameType=new DisplayNameType();
                                                            displayNameType.setValue(valuesToString(node3.getValue()));
                                                            listServiceType.setDisplayName(displayNameType);
                                                            break;
                                                        case "MCPTTGroupMemberList":
                                                            //This interior node is a placeholder for a list of group members (group membership information).
                                                            if(listServiceType.getList()==null){
                                                                listServiceType.setList(new ListType());
                                                            }
                                                            if(listServiceType.getList().getEntry()==null){
                                                                ArrayList<org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType> entryTypes=new ArrayList<org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType>();
                                                                listServiceType.getList().setEntry(entryTypes);
                                                            }
                                                            for(Node node4:node3.getNode()) {
                                                                if(node4!=null){
                                                                    org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType entryType=new org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType();
                                                                    listServiceType.getList().getEntry().add(entryType);
                                                                    for(Node node5:node4.getNode()) {
                                                                        if(node5!=null && node5.getNodeName()!=null)
                                                                            switch (node5.getNodeName()) {
                                                                                case "MCPTTID":
                                                                                    //This leaf node indicates an MCPTT user identity (MCPTT ID) which is a globally unique identifier within the MCPTT service that represents the MCPTT user.
                                                                                    entryType.setUri(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                case "UserPriority":
                                                                                    //This leaf node indicates the user priority for the group.
                                                                                    Integer value;
                                                                                    if((value=valuesToInteger(node5.getValue()))!=null)
                                                                                        entryType.setUserpriority(value);
                                                                                    break;
                                                                                case "ParticpantType":
                                                                                    //This leaf node indicates the participant type for the group.
                                                                                    entryType.setParticipanttype(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                default:
                                                                                    break;
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        case "MCPTTGroupOwner":
                                                            //This leaf node indicates the group's owner (Mission Critical Organisation).
                                                            listServiceType.setOwner(valuesToString(node3.getValue()));
                                                            break;
                                                        case "PreferredVoiceCodec":
                                                            //This leaf node indicates the preferred voice codec for an MCPTT group.
                                                            EncodingsType encodingsType=new EncodingsType();
                                                            encodingsType.setEncoding(valuesToEncodingTypes(node3.getValue()));
                                                            listServiceType.setPreferredvoiceencodings(encodingsType);
                                                            break;
                                                        case "MCPTTGroupLevel":
                                                            //This leaf node indicates the level within a group hierarchy (only applicable for group-broadcast group).
                                                            listServiceType.setLevelwithingrouphierarchy(valuesToInteger(node3.getValue()));
                                                            break;
                                                        case "UserLevel":
                                                            //This leaf node indicates the level within user hierarchy (only applicable for user-broadcast group).
                                                            listServiceType.setLevelwithinuserhierarchy(valuesToInteger(node3.getValue()));
                                                            break;
                                                        case "AllowedEmergencyCall":
                                                            //This leaf node indicates whether an MCPTT emergency group call is permitted on the MCPTT group.
                                                            extensibleType.setAllowMCPTTemergencycall(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "AllowedImminentPerilCall":
                                                            //This leaf node indicates whether an MCPTT imminent peril group call is permitted on the MCPTT group.
                                                            extensibleType.setAllowimminentperilcall(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "AllowedEmergencyAlert":
                                                            //This leaf node indicates whether an MCPTT emergency alert is possible on the MCPTT group.
                                                            extensibleType.setAllowMCPTTemergencyalert(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "AllowedMediaProtection":
                                                            //This leaf node indicates whether confidentiality and integrity of media is permitted on the MCPTT group.
                                                            listServiceType.setProtectmedia(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "AllowedFloorControlProtection":
                                                            //This interior node indicates whether confidentiality and integrity of floor control signalling is permitted on the MCPTT group.
                                                            listServiceType.setProtectfloorcontrolsignalling(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "MediaProtectionSecurityMaterial":
                                                            //This leaf node indicates security material (as specified in 3GPP TS 33.179 [14]) for media protection in the MCPTT group.
                                                            Log.w(TAG,"Now, it don´t support MediaProtectionSecurityMaterial in Group configure");
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                            }
                                            break;
                                        case "OffNetwork":
                                            //This interior node represents a container for off-network operation.
                                            for(Node node3:node2.getNode()) {
                                                if(node3!=null && node3.getNodeName()!=null)
                                                    switch (node3.getNodeName()) {
                                                        case "MCPTTGroupParameter":
                                                            //This interior node is a placeholder for the group parameters.

                                                            for(Node node4:node3.getNode()) {
                                                                //This interior node is a placeholder for one or more group parameters.
                                                                if(node4!=null){
                                                                    for(Node node5:node4.getNode()) {
                                                                        if(node5!=null && node5.getNodeName()!=null)
                                                                            switch (node5.getNodeName()) {
                                                                                case "ProSeLayer2GroupID":
                                                                                    //This leaf node indicates the Prose layer-2 group ID as specified in 3GPP TS 23.303 [6].
                                                                                    listServiceType.setOffnetworkProSelayer2groupid(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                case "IPMulticastAddress":
                                                                                    //This leaf node indicates the ProSe group IP multicast address as specified in 3GPP TS 23.303 [6].
                                                                                    listServiceType.setOffnetworkIPmulticastaddress(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                case "RelayServiceCode":
                                                                                    //This leaf node indicates the connectivity service that the ProSe UE-to-network relay provides to public safety applications as specified in 3GPP TS 23.303 [6].
                                                                                    listServiceType.setOffnetworkPDNtype(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                case "IPVersions":
                                                                                    //This leaf node indicates whether IPv4 or IPv6 is used for the MCPTT group as specified in 3GPP TS 23.303 [6].
                                                                                    listServiceType.setOffnetworkProSerelayservicecode(valuesToString(node5.getValue()));
                                                                                    break;
                                                                                default:
                                                                                    break;
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                            break;
                                                        case "EmergencyCallCancel":
                                                            //This leaf node indicates the timeout value for the cancellation of an in progress emergency for an MCPTT group call.
                                                            listServiceType.setOffnetworkinprogressemergencystatecancellationtimeout(valueIntToDuration(node3.getValue()));
                                                            break;
                                                        case "ImminentPerilCallCancel":
                                                            //This leaf node indicates the timeout value for the cancellation of an in progress MCPTT imminent peril group call.
                                                            listServiceType.setOffnetworkinprogressimminentperilstatecancellationtimeout(valueIntToDuration(node3.getValue()));
                                                            break;
                                                        case "HangTime":
                                                            //This leaf node indicates the group call hang timer.
                                                            listServiceType.setOffnetworkhangtimer(valueIntToDuration(node3.getValue()));
                                                            break;
                                                        case "MaxDuration":
                                                            //This leaf node indicates the max duration of group calls.
                                                            listServiceType.setOffnetworkmaximumduration(valueIntToDuration(node3.getValue()));
                                                            break;
                                                        case "QueueUsage":
                                                            //This leaf node indicates if queuing is enabled or not.
                                                            listServiceType.setOffnetworkqueueusage(valuesToBool(node3.getValue()));
                                                            break;
                                                        case "DefaultPPPP":
                                                            //This interior node is a placeholder for the default ProSe Per-Packet Priority (PPPP) configuration.
                                                            for(Node node4:node3.getNode()) {
                                                                if(node4!=null && node4.getNodeName()!=null)
                                                                    switch (node4.getNodeName()) {
                                                                        case "MCPTTGroupCallSignalling":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT group call signalling.
                                                                            listServiceType.setOffnetworkProSesignallingPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        case "MCPTTGroupCallMedia":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT group call media.
                                                                            listServiceType.setOffnetworkProSemediaPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        case "MCPTTEmergencyGroupCallSignalling":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT emerency group call signalling.
                                                                            listServiceType.setOffnetworkProSeemergencycallsignallingPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        case "MCPTTEmergencyGroupCallMedia":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT emerency group call media.
                                                                            listServiceType.setOffnetworkProSeemergencycallmediaPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        case "MCPTTImminentPerilGroupCallSignalling":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT imminent peril group call signalling.
                                                                            listServiceType.setOffnetworkProSeimminentperilcallsignallingPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        case "MCPTTImminentPerilGroupCallMedia":
                                                                            //This leaf node indicates the default ProSe Per-Packet Priority (PPPP) value (as specified in 3GPP TS 23.303 [6]) for the MCPTT imminent peril group call media.
                                                                            listServiceType.setOffnetworkProSeimminentperilcallmediaPPPP(valuesToString(node4.getValue()));
                                                                            break;
                                                                        default:
                                                                            break;
                                                                    }
                                                            }
                                                            break;
                                                        default:
                                                            break;
                                                    }
                                            }
                                            break;
                                        default:
                                            break;

                            }
                        }
                    }
                }

            }

        }
        return group;
    }


    //END utils MO to xml


    //INIT utils xml
        //INIT MgmtTree

    public static MgmtTree getMgmtTree(String string) throws Exception {
        return getMgmtTree(string.getBytes());
    }

    private static MgmtTree getMgmtTree(byte[] bytes) throws Exception {
        return getMgmtTree(new ByteArrayInputStream(bytes));
    }
    private static MgmtTree getMgmtTree(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(MgmtTree.class,stream);
    }


    private static InputStream getOutputStreamOfMgmtTree(Context context, MgmtTree mgmtTree) throws Exception {
        if(mgmtTree==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mgmtTree,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMgmtTree(Context context,MgmtTree mgmtTree) throws Exception {
        InputStream inputStream=getOutputStreamOfMgmtTree(context,mgmtTree);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public   static String  getStringOfMgmtTree(Context context,MgmtTree mgmtTree) throws Exception {
        return new String(getBytesOfMgmtTree(context,mgmtTree)).trim();
    }


        //END MgmtTree

    //END utils xml



    private static byte[] readBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();


        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];


        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }


        return byteBuffer.toByteArray();
    }

    //INIT files RAW
    private static InputStream getFileRaw(Context context,String nameFileWithoutExtension){
        if(context==null || nameFileWithoutExtension==null || nameFileWithoutExtension.isEmpty())return null;
        InputStream ins = context.getResources().openRawResource(
                context.getResources().getIdentifier(nameFileWithoutExtension,
                        "raw", context.getPackageName()));
        return ins;
    }
    //END files RAW

}
