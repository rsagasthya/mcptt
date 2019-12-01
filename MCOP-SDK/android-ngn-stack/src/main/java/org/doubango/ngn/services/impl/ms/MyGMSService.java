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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.doubango.ngn.datatype.ms.gms.ns.GMSData;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ListType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ResourceLists;
import org.doubango.ngn.datatype.ms.gms.ns.xcap_diff.DocumentType;
import org.doubango.ngn.datatype.ms.gms.ns.xcap_diff.XcapDiff;
import org.doubango.ngn.datatype.openId.CampsType;
import org.doubango.ngn.services.authentication.IMyAuthenticacionService;
import org.doubango.ngn.services.cms.IMyCMSService;
import org.doubango.ngn.services.gms.IMyGMSService;
import org.doubango.ngn.sip.MySubscriptionGMSSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyGMSService implements IMyGMSService, RestService.OnRestServiceListener, IMyCMSService.OnGetMcpttUserProfile2Listener {

    private final static String TAG = Utils.getTAG(MyGMSService.class.getCanonicalName());
    private boolean isSubscribed=false;
    private boolean reSubscribed=false;
    private static boolean isStart;
    private static Map<String,GMSData> currentGroups;
    private final IMyAuthenticacionService mAuthenticationService;
    private final BroadcastReceiver broadcastReceiverGMSMessage;
    private static OnGMSListener onGMSListener;
    private MySubscriptionGMSSession mGMSService;
    private RestService restService;
    private XcapDiff currentXcapDiff;

    public MyGMSService() {
        restService=RestService.getInstance();

        broadcastReceiverGMSMessage=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(GMS_ACTION_NOTIFY)) {
                    Log.d(TAG,"New notify received.");
                    boolean sendAccound=false;
                    byte[] messageGMS=intent.getByteArrayExtra(GMS_NEWGMS_NOTIFY);
                    Log.d(TAG,"New notify GMS received.");

                    //try {
                    XcapDiff xcapDiff=null;
                    if(messageGMS==null || messageGMS.length==0){
                        Log.w(TAG,"GMS notify not valid or empty.");
                        xcapDiff=new XcapDiff();
                    }else{
                        Log.d(TAG,"Valid GMS notify.");
                        try {
                            if(BuildConfig.DEBUG)Log.d(TAG,"new notify: "+new String(messageGMS));
                            xcapDiff=MSUtils.getXcapDiff(messageGMS);
                        } catch (Exception e) {
                            Log.e(TAG,"Error proccess new GMS:"+e.getMessage());
                        }
                    }
                    requestGroups(xcapDiff,context);

                }else if (intent.getAction().equals(GMS_ACTION_SUBSCRIBE)) {
                    Log.d(TAG,"Receive response subscribe");
                    String error=intent.getStringExtra(GMS_RESPONSE_SUBSCRIBE_ERROR);
                    String responseOk=intent.getStringExtra(GMS_RESPONSE_SUBSCRIBE_OK);
                    if(error!=null){
                        //Error
                        if(BuildConfig.DEBUG)Log.e(TAG,"Error in subscribe for GMS "+error);
                        isSubscribed=false;
                    }else if(responseOk!=null){
                        //Ok
                        Log.d(TAG,"Correct subscribe for GMS");
                        isSubscribed=true;
                        /*
                        if(GMSGroupDelay!=null){
                            Log.d(TAG,"GMS now to groups");
                            GMSGroups(context,GMSGroupDelay);
                            GMSGroupDelay=null;
                        }*/

                    }else
                        Log.w(TAG,"This situation isn´t logic");
                }else if (intent.getAction().equals(GMS_ACTION_UNSUBSCRIBE)) {
                    Log.d(TAG,"UnSubscribe");
                    isSubscribed=false;
                }
            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GMS_ACTION_NOTIFY);
        intentFilter.addAction(GMS_ACTION_SUBSCRIBE);
        intentFilter.addAction(GMS_ACTION_UNSUBSCRIBE);
        NgnApplication.getContext().registerReceiver(broadcastReceiverGMSMessage,intentFilter);

        currentGroups=null;
        isStart=false;
        mAuthenticationService=NgnEngine.getInstance().getAuthenticationService();

        NgnEngine.getInstance().getCMSService().setOnGetMcpttUserProfile2Listener(this);

    }

    private boolean requestGroups(XcapDiff xcapDiff,Context context){
        if(xcapDiff==null){
            Log.e(TAG,"Error in xcapdiff");
            return false;
        }
        this.currentXcapDiff=xcapDiff;
        String gmsUri=getGMSUri(context);
        restService.setOnRestServiceListener(this);
        if(currentXcapDiff!=null && currentXcapDiff.getDocument()!=null)
        for(DocumentType documentType:currentXcapDiff.getDocument()){
            if(gmsUri!=null && !gmsUri.isEmpty()){
                restService.DownloaderMCPTTGmsGroups(gmsUri,documentType.getSel(),null,getAccessToken(context));
            }
        }

        return true;
    }

    private String getGMSUri(Context context){
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        String gmsUri=null;
        if(ngnSipPrefrences!=null)
        gmsUri=NgnEngine.getInstance().getProfilesService().getProfileNow(context).getGMSXCAPRootURI();
        return gmsUri;
    }


    @Override
    public boolean start() {
        currentGroups=null;
        isStart=true;
        isSubscribed=false;
        return true;
    }

    @Override
    public boolean stop() {
        isStart=false;
        currentGroups=null;
        isSubscribed=false;
        return true;
    }

    @Override
    public boolean clearService() {
        pauseServiceGMS();
        return false;
    }


    @Override
    public void setOnGMSListener(OnGMSListener onGMSListener) {
        this.onGMSListener=onGMSListener;
    }

    @Override
    public org.doubango.ngn.datatype.ms.gms.ns.list_service.Group getGroupInfo(String group) {
        if(currentGroups!=null){
            GMSData gmsData=currentGroups.get(group.trim());
            if(gmsData.getGroup()!=null){
                return gmsData.getGroup();
            }
        }
        return null;
    }
    @Override
    public void startServiceGMS(Context context) {
        //Init Subscribe GMS
        if(BuildConfig.DEBUG)Log.d(TAG,"start service GMS");
        gmsChange(true,context);
    }

    private void pauseServiceGMS() {
        //Pause Subscribe GMS
        if(BuildConfig.DEBUG)Log.d(TAG,"pause service GMS");
        gmsChange(false,null);
    }

    private void gmsChange(boolean isRegister,Context context){
        NgnSipPrefrences currentProfile;
        if((currentProfile=NgnEngine.getInstance().getProfilesService().getProfileNow(context))!=null &&
                currentProfile.isMcpttEnableSubcriptionGMS()!=null &&
                currentProfile.isMcpttEnableSubcriptionGMS()){
            if(!isRegister){
                if(mGMSService!=null){
                    Log.d(TAG,"Unsubscribe GMS");
                    mGMSService.unSubscribeGMS();
                    mGMSService=null;
                }
            }else{
                if(!isSubscribed){
                    mGMSService= MySubscriptionGMSSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack());
                    if(mGMSService.subscribeGMS(getResoultList(context),getMCPTTInfoAccessToken(context))){
                        Log.d(TAG,"Subscribe sent GMS.");
                    }
                }else{
                    if(mGMSService!=null){
                        if(mGMSService.unSubscribeGMS()){
                            Log.d(TAG,"Unsubscribe sent GMS");
                        }
                        mGMSService=null;
                    }
                }

            }
        }

    }
    private String getResoultList(Context context){
        ResourceLists resourceLists=new ResourceLists();
        List<ListType> list=new ArrayList<>();
        ListType listType=new ListType();
        List<org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType> listEntryTypes=new ArrayList<>();
        NgnSipPrefrences currentProfiles;
        Map<String,NgnSipPrefrences.EntryType> groups=null;
        if((currentProfiles=NgnEngine.getInstance().getProfilesService().getProfileNow(context))!=null &&
                (groups=currentProfiles.getMCPTTGroupInfo())!=null && currentProfiles.getGMSXCAPRootURI()!=null){

            for(String displayNameGroup:groups.keySet()){
                NgnSipPrefrences.EntryType entryType=groups.get(displayNameGroup);
                if(entryType.getUriEntry()!=null){
                    org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType entryType1=new EntryType();
                    entryType1.setUri("org.openmobilealliance.groups/global/byGroup/"+entryType.getUriEntry());
                    listEntryTypes.add(entryType1);
                }
            }
        }
        listType.setEntry(listEntryTypes);
        list.add(listType);
        resourceLists.setList(list);
        try {
            return GMSUtils.getStringOfResourceLists(context,resourceLists);
        } catch (Exception e) {
            Log.e(TAG,"GMS processing error: "+e.getMessage());
        }
        return null;
    }

    private String  getMCPTTInfoAccessToken(Context context){
        CampsType campsType=mAuthenticationService.getCampsTypeCurrent(context);
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(campsType!=null && ngnSipPrefrences!=null && ngnSipPrefrences.getMcpttClientId()!=null)
            return AuthenticacionUtils.generateMcpttinfoType(
                    context,
                    campsType,
                    ngnSipPrefrences.getMcpttId(),
                    ngnSipPrefrences.getMcpttClientId(),
                    ngnSipPrefrences.isMcpttSelfAuthenticationSendTokenFail()!=null?ngnSipPrefrences.isMcpttSelfAuthenticationSendTokenFail():false);
        return null;
    }

    private String getAccessToken(Context context){
        CampsType campsType=null;
        if(mAuthenticationService==null ||
                (campsType=mAuthenticationService.getCampsTypeCurrent(context))==null ||
                        campsType.getAccessToken()==null)return null;
        return campsType.getAccessToken();
    }


    @Override
    public void onDownloaderXML(String results, String etag,String path, int codeRespone, RestService.ContentTypeData contentTypeData) {
        Log.d(TAG,"XML GMS downloaded.");
        Group gmsData=null;
        switch (codeRespone){
            case HttpURLConnection.HTTP_OK:
                try {
                    gmsData=GMSUtils.getGroupConfiguration(results.trim());
                    if(gmsData!=null){
                       if(BuildConfig.DEBUG) Log.d(TAG,"Downloaded GMS data with eTAG:"+etag+" and path:"+path);
                    }else{
                        Log.d(TAG,"Error processing downloaded GMS data.");
                    }
                } catch (Exception e) {
                    Log.e(TAG,"Translation error:"+e.getMessage());
                }
                break;
            case HttpURLConnection.HTTP_NOT_MODIFIED:
                Log.d(TAG,"Data not modified.");
                break;
            default:
                Log.e(TAG,"Error in code response:"+codeRespone);
                break;
        }

        switch (contentTypeData){
            case CONTENT_TYPE_MCPTT_GROUPS:
                if(gmsData==null || gmsData.getListService()==null || gmsData.getListService().get(0)==null || gmsData.getListService().get(0).getUri()==null){
                    Log.e(TAG,"Error in GMS proccess.");

                }else {
                    if(currentGroups==null)currentGroups=new HashMap<>();
                    currentGroups.put(gmsData.getListService().get(0).getUri(),new GMSData(gmsData,etag));

                    if(onGMSListener!=null)onGMSListener.onGMSGroup(gmsData);
                }
                break;
            case CONTENT_TYPE_NONE:
            default:
                Log.e(TAG,"Invalid content-type. in GMS");
                break;
        }
        return;
    }


    @Override
    public void errorOnDownloaderXML(String error, RestService.ContentTypeData contentTypeData) {
        if(BuildConfig.DEBUG)Log.d(TAG,"Result GMS"+ error+" "+contentTypeData.getText());
    }

    @Override
    public void onGetMcpttUserProfile(McpttUserProfile mcpttUserProfile) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttUserProfile");
        startServiceGMS(NgnApplication.getContext());
    }

    @Override
    public void onGetMcpttUserProfileError(String error) {
        if(BuildConfig.DEBUG)Log.e(TAG,"onGetMcpttUserProfileError: "+error);
    }
}
