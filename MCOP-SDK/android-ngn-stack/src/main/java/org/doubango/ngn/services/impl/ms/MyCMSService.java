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

package org.doubango.ngn.services.impl.ms;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.ms.cms.CMSData;
import org.doubango.ngn.datatype.ms.cms.CMSDatas;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.EmergencyCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.FcTimersCountersType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.FloorControlQueueType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.PrivateCallType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationParamsType;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.SignallingProtectionType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.CommonType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.RelayedMCPTTGroupType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.McpttUEInitialConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.OnNetworkType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryType;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ListType;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ResourceLists;
import org.doubango.ngn.datatype.ms.gms.ns.xcap_diff.DocumentType;
import org.doubango.ngn.datatype.ms.gms.ns.xcap_diff.XcapDiff;
import org.doubango.ngn.datatype.openId.CampsType;
import org.doubango.ngn.services.authentication.IMyAuthenticacionService;
import org.doubango.ngn.services.cms.IMyCMSService;
import org.doubango.ngn.services.impl.preference.PreferencesManager;
import org.doubango.ngn.services.profiles.IMyProfilesService;
import org.doubango.ngn.sip.MySubscriptionCMSSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;

import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.AUTHENTICATION;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.CMS_UPDATING;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.DOWNLOAD_USER_PROFILE;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.INIT;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.NONE;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.PRE_AUTHENTICATION;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.RECEIVED_NOTIFICATIONS;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.REGISTERED;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.STABLE;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.SUBSCRIBED;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.USER_SELECT_PROFILE;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.WITH_SERVICE_CONFIG;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.WITH_UE_CONFIG;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.WITH_UE_INIT_CONFIG;
import static org.doubango.ngn.services.impl.ms.MyCMSService.StateCMS.WITH_USER_PROFILE;


public class MyCMSService  implements IMyCMSService, RestService.OnRestServiceListener, MyAuthenticacionService.OnAuthenticationListener{

    private final static String TAG = Utils.getTAG(MyCMSService.class.getCanonicalName());

    private static boolean isStart;
    private final BroadcastReceiver broadcastReceiverCMSMessage;
    private PreferencesManager preferencesManager;
    private boolean isSubscribed=false;
    private final RestService restService;
    private XcapDiff currentXcapDiff;
    private static OnCMSPrivateContactsListener onCMSPrivateContactsListener;



    private static final String PREFERENCE_DATA_CMS="PREFERENCE_DATA_CMS."+TAG;
    private static final String PREFERENCE_DATA_CMS_MCPTT_UE_INIT_CONFIGURATION="PREFERENCE_DATA_CMS_MCPTT_UE_INIT_CONFIGURATION."+TAG;
    private static final String PREFERENCE_DATA_CMS_MCPTT_UE_CONFIGURATION="PREFERENCE_DATA_CMS_MCPTT_UE_CONFIGURATION."+TAG;
    private static final String PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE="PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE."+TAG;
    private static final String PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE_DEFAULT="PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE_DEFAULT."+TAG;
    private static final String PREFERENCE_DATA_CMS_MCPTT_SERVICE_CONFIGURATION="PREFERENCE_DATA_CMS_MCPTT_SERVICE_CONFIGURATION."+TAG;




    private static final String MCPTT_USER_PROFILE_PATH_DEFAULT="mcptt-user-profile";

    private static final String MCPTT_USER_PROFILE_PATH_SUFFIX_DEFAULT=".xml";

    private static final char MCPTT_USER_PROFILE_PATH_SEPARATOR_DEFAULT='-';
    private static final String MCPTT_SERVICE_CONFIGURE_PATH_DEFAULT="service-config.xml";


    private Integer currentIndexProfileServiceSelect=null;



    private IMyProfilesService mProfilesService;
    private IMyAuthenticacionService mAuthenticationService;
    private MySubscriptionCMSSession mCMSService;

    private static String mcpttUEIdNow;
    private CMSData mcpttUeInitConfigNow;
    private CMSData mcpttUeConfigNow;
    private CMSDatas mcpttUsersProfileNow;
    private CMSDatas mcpttUsersProfileNew;
    private CMSData mcpttUserProfileDefault;
    private CMSData mcpttUserProfileNow;
    private CMSData mcpttServiceConfigurationNow;






    private static OnGetMcpttUEInitialConfigurationListener onGetMcpttUEInitialConfigurationListener;
    private static OnGetMcpttUEConfigurationListener onGetMcpttUEConfigurationListener;
    private OnGetServiceConfigurationInfoTypeListener onGetServiceConfigurationInfoTypeListener;
    private OnGetMcpttUserProfileListener onGetMcpttUserProfileListener;
    private static StateCMS stateCMSNow=NONE;
    private OnAuthenticationListener onAuthenticationListener;
    private String mcpttUserprofilePathNow;
    private String MCPTTIdNow;
    private boolean configureNowCMSProfile;
    private OnGetMcpttServiceConfListener onGetMcpttServiceConfListener;
    private OnGetMcpttUserProfile2Listener onGetMcpttUserProfile2Listener;
    private OnStableListener onStableListener;





    protected enum StateCMS{
        NONE("none"),
        INIT("init"),
        WITH_UE_INIT_CONFIG("with ue init config"),
        PRE_AUTHENTICATION("preauthentication"),
        AUTHENTICATION("authentication"),
        WITH_UE_CONFIG("with ue config"),
        DOWNLOAD_USER_PROFILE("Download all users profile"),
        USER_SELECT_PROFILE("The user has selected the \"UserProfile\""),
        REGISTERED("registered"),
        SUBSCRIBED("subscribed"),
        RECEIVED_NOTIFICATIONS("received notifications"),
        WITH_USER_PROFILE("with user profile"),
        WITH_SERVICE_CONFIG("with service config"),
        STABLE("stable"),
        CMS_UPDATING("cms updating");



        private String text;
        StateCMS(String text) {
            this.text = text;
        }
        protected String getText() {
            return this.text;
        }
        protected static StateCMS fromString(String text) {
            for (StateCMS b : StateCMS.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @Override
    public boolean start() {


        Log.d(TAG,"Start "+"CMS service");
        isStart=false;
        configureNowCMSProfile=false;
        preferencesManager=new PreferencesManager(PREFERENCE_DATA_CMS);
        mProfilesService=NgnEngine.getInstance().getProfilesService();
        mAuthenticationService=NgnEngine.getInstance().getAuthenticationService();
        mAuthenticationService.setOnAuthenticationListener(this);
        //Load in memory
        loadAllConfigureCMS(NgnApplication.getContext());
        isSubscribed=false;
        mcpttUsersProfileNew=new CMSDatas();
        return true;
    }

    @Override
    public boolean stop() {
        preferencesManager=null;
        isSubscribed=false;
        Log.d(TAG,"Stop "+"CMS service");
        return true;
    }

    public MyCMSService() {
        restService=RestService.getInstance();
        broadcastReceiverCMSMessage=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

              if (intent.getAction().equals(CMS_ACTION_NOTIFY)) {
                    Log.d(TAG,"New notify received.");
                    boolean sendAccound=false;
                    byte[] messageCMS=intent.getByteArrayExtra(CMS_NEWCMS_NOTIFY);
                    Log.d(TAG,"New notify CMS received.");
                    incomingNotify(messageCMS);

                }else if (intent.getAction().equals(CMS_ACTION_SUBSCRIBE)) {
                    Log.d(TAG,"Receive response subscribe");
                    String error=intent.getStringExtra(CMS_RESPONSE_SUBSCRIBE_ERROR);
                    String responseOk=intent.getStringExtra(CMS_RESPONSE_SUBSCRIBE_OK);
                    if(error!=null){
                        //Error
                        Log.e(TAG,"Error in subscribe for CMS "+error);
                        changeSubscribed(false);
                    }else if(responseOk!=null){
                        //Ok
                        changeSubscribed(true);

                    }else
                        Log.w(TAG,"This situation isn´t logic");
                }else if (intent.getAction().equals(CMS_ACTION_UNSUBSCRIBE)) {
                    Log.d(TAG,"UnSubscribe");
                }
            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CMS_ACTION_NOTIFY);
        intentFilter.addAction(CMS_ACTION_SUBSCRIBE);
        intentFilter.addAction(CMS_ACTION_UNSUBSCRIBE);
        NgnApplication.getContext().registerReceiver(broadcastReceiverCMSMessage,intentFilter);
    }

    private void changeSubscribed(boolean isSubscribed){
        if(isSubscribed){
            Log.d(TAG,"Correct subscribe for CMS");
            this.isSubscribed=true;
                        /*
                        if(CMSGroupDelay!=null){
                            Log.d(TAG,"CMS now to groups");
                            CMSGroups(context,CMSGroupDelay);
                            CMSGroupDelay=null;
                        }
                        */
            boolean result=false;
            switch (stateCMSNow){
                //It´s registed
                case REGISTERED:
                    stateCMSNow=SUBSCRIBED;
                    result=executeLogicMachine(NgnApplication.getContext());
                    break;
                case WITH_SERVICE_CONFIG:
                case STABLE:
                    if(BuildConfig.DEBUG)Log.d(TAG,"re-subscribe");
                    break;

                default:
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error: Invalid state for CMS service initialization 1. Current state:"+stateCMSNow.getText());
                    result=false;
                    break;
            }
        }else{
            this.isSubscribed=false;

        }

    }


    private boolean incomingNotify(byte[] messageCMS){
        boolean result=false;
            if(messageCMS!=null && messageCMS.length>0){
                XcapDiff xcapDiff=null;
                if(messageCMS==null || messageCMS.length==0){
                    Log.w(TAG,"CMS notify not valid or empty.");
                    xcapDiff=new XcapDiff();
                }else{
                    Log.d(TAG,"Valid GMS notify.");
                    try {
                        if(BuildConfig.DEBUG)Log.d(TAG,"new notify: "+new String(messageCMS));
                        xcapDiff=MSUtils.getXcapDiff(messageCMS);
                    } catch (Exception e) {
                        Log.e(TAG,"Error proccess new CMS:"+e.getMessage());
                    }
                    currentXcapDiff=xcapDiff;
                }
                switch (stateCMSNow) {
                    case SUBSCRIBED:
                    case REGISTERED:
                        stateCMSNow = RECEIVED_NOTIFICATIONS;
                        result = executeLogicMachine(NgnApplication.getContext());
                        break;
                    case WITH_SERVICE_CONFIG:
                        break;
                    case STABLE:
                        if(BuildConfig.DEBUG)Log.d(TAG,"Received Notify, and it is neccessary update CMS");
                        stateCMSNow = CMS_UPDATING;
                        result = executeLogicMachine(NgnApplication.getContext());
                        break;

                    default:
                        Log.e(TAG, "Error: Invalid state for CMS. Current state:" + stateCMSNow.getText());
                        result = false;
                        break;
                }
            }else if(!isSubscribed){
                if(BuildConfig.DEBUG)Log.e(TAG,"it is not subscribed");
            }else{
                if(BuildConfig.DEBUG)Log.e(TAG,"The notify recived is not valid or logic");
            }
        return result;
    }




    private String getAccessToken(Context context){
        CampsType campsType=null;
        if(mAuthenticationService==null ||
                (campsType=mAuthenticationService.getCampsTypeCurrent(context))==null ||
                campsType.getAccessToken()==null)return null;
        return campsType.getAccessToken();
    }

    //Init logic machine

    @Override
    public void onAuthentication(String dataURI, String redirectionURI) {
        if(onAuthenticationListener!=null)onAuthenticationListener.onAuthentication( dataURI,redirectionURI);
    }

    @Override
    public void onAuthenticationOk(String data) {
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(NgnApplication.getContext());
        if(ngnSipPrefrences!=null && ngnSipPrefrences.isMcpttEnableCMS()!=null && ngnSipPrefrences.isMcpttEnableCMS()){
            //CampsType campsType=mAuthenticationService.getCampsTypeCurrent(NgnApplication.getContext());
            //Get MCPTT_ID from Token Authentication for Downloader User profile
            changeStateToPreRegiste();
        }
        if(onAuthenticationListener!=null)onAuthenticationListener.onAuthenticationOk(data);
    }

    @Override
    public void onAuthenticationError(String error) {
        if(onAuthenticationListener!=null)onAuthenticationListener.onAuthenticationError(error);

    }

    @Override
    public void onAuthenticationRefresh(String refresh) {
        if(onAuthenticationListener!=null)onAuthenticationListener.onAuthenticationRefresh(refresh);

    }

    /**
     * This funcion returns the name from the profiles Name for this MCPTT ID.
     * @param context
     * @return
     */
    private List<String> getUserProfilesNames(Context context){
        CampsType campsType;

        return null;
    }

    public boolean setUserProfileForUse(String userProfile,Context context){
        if(BuildConfig.DEBUG)Log.d(TAG,"setUserProfileForUse");
        if(userProfile!=null){
            switch (stateCMSNow) {
                case WITH_UE_CONFIG:
                    stateCMSNow=DOWNLOAD_USER_PROFILE;
                    executeLogicMachine(context);
                    break;
            }
            return true;
        }else{
            Log.e(TAG,"Invalid user profile.");
        }
        return false;
    }

    private boolean downloaderUserProfile(){
        boolean result=false;
        switch (stateCMSNow){
            //Step 2: Get User profile default config and authenticate on Idms.
            case INIT:
                stateCMSNow=WITH_UE_INIT_CONFIG;
                result=executeLogicMachine(NgnApplication.getContext());
                break;
            case RECEIVED_NOTIFICATIONS:
                stateCMSNow=WITH_UE_CONFIG;
                result=executeLogicMachine(NgnApplication.getContext());
                break;


            default:
                Log.e(TAG,"Error: Invalid state for CMS service initialization 2. Current state:"+stateCMSNow.getText());
                result=false;
                break;
        }
        return result;
    }



    private boolean authenticationInitIdmsOrWithUserProfile(){

        boolean result=false;
        switch (stateCMSNow){
            //Step 3: Authenticate on Idms and download UE config for this specific UE.
            case WITH_UE_INIT_CONFIG:
                stateCMSNow=PRE_AUTHENTICATION;
                result=executeLogicMachine(NgnApplication.getContext());
                Log.d(TAG,"Change 1: pre authentication");
                break;
            //Step 6:
            case DOWNLOAD_USER_PROFILE:
                //TODO:Lack control if it runs more than once because is this downloading more than one mcptt UserProfile and how to manage this situation
                List<DocumentType> allUserProfilesXCAPDIFF=null;

                if((allUserProfilesXCAPDIFF=getMcpttUserProfileXCAPDIFF())!=null &&
                        mcpttUsersProfileNew.lengthCMSData()==(allUserProfilesXCAPDIFF.size())){
                    stateCMSNow=USER_SELECT_PROFILE;
                    if(BuildConfig.DEBUG)Log.w(TAG,"Change : user select profile");
                    result=executeLogicMachine(NgnApplication.getContext());
                }else{
                    if(BuildConfig.DEBUG)Log.w(TAG,"It is neccesary wait for next user profile. size: "+((mcpttUsersProfileNew!=null)?mcpttUsersProfileNew.lengthCMSData():0));
                }
                break;
            default:
                Log.e(TAG,"Error 3: Invalid state for CMS service initialization 3. Current state:"+stateCMSNow.getText());
                result=false;
                break;
        }
        return result;
    }

    private boolean checkUserProfileNowAndXCAPDiff(List<DocumentType> xcapdiff,CMSDatas usersProfile){
       for(DocumentType documentType:xcapdiff){
           if(usersProfile.isExistTAG(documentType.getNewEtag())<0){
               if(BuildConfig.DEBUG)Log.d(TAG,"checkUserProfileNowAndXCAPDiff isNotExistTAG");
               return false;
           }else{

           }
       }
       return true;
    }

    private boolean receivedServiceConfig(){

        boolean result=false;
        switch (stateCMSNow){
            //Step 7: in this momneto we have the service config for use in system
            case WITH_USER_PROFILE:
                stateCMSNow=WITH_SERVICE_CONFIG;
                Log.d(TAG,"Change 3: service configuration");
                result=executeLogicMachine(NgnApplication.getContext());
                break;
            default:
                Log.e(TAG,"Error 4: Invalid state for CMS service initialization 4: Current state:"+stateCMSNow.getText());
                result=false;
                break;
        }
        return result;
    }

    private boolean changeStateToPreRegiste(){
        boolean result=false;
        switch (stateCMSNow){
            //Step 4: The user is authenticated and has the MCPTT id, now download UE config
            case PRE_AUTHENTICATION:
                stateCMSNow=AUTHENTICATION;
                result=executeLogicMachine(NgnApplication.getContext());
                break;
            default:
                Log.e(TAG,"Error 1: Invalid state for CMS service initialization 5: Current state:"+stateCMSNow.getText());
                result=false;
                break;
        }
        return result;
    }

    //Step 1: Get UE init config and then get default user profile.
    public boolean initConfiguration(Context context,NgnSipPrefrences ngnSipPrefrences){
        if(BuildConfig.DEBUG)Log.d(TAG,"initConfiguration for CMS");
        if(ngnSipPrefrences!=null && ngnSipPrefrences.isMcpttEnableCMS()!=null && ngnSipPrefrences.isMcpttEnableCMS()){
            boolean result=false;
            switch (stateCMSNow){
                case NONE:
                    Log.d(TAG,"Init self configuration with CMS.");
                    executeLogicMachine(context);
                    break;
                default:
                    Log.w(TAG,"The configuration is in other state that Init.");
                    executeLogicMachine(context);
                    break;
            }
            return result;
        }
        else if(ngnSipPrefrences!=null && ngnSipPrefrences.isMcpttUseIssuerUriIdms()!=null && ngnSipPrefrences.isMcpttUseIssuerUriIdms()){
            Log.d(TAG,"IDMS only with IssuerUri.");
            //the CMS isn´t enabled for this profile, use idms only
            return mAuthenticationService.initConfigure(context);
        }
        else{
            Log.d(TAG,"IDMS only without IssuerUri.");
            //the CMS isn´t enabled for this profile, use idms only
            String IdmsAuthEndpointString=ngnSipPrefrences.getIdmsAuthEndpoint();
            String IdmsTokenEndPointString=ngnSipPrefrences.getIdmsTokenEndPoint();
            Uri IdmsAuthEndpointUri=Uri.parse(IdmsAuthEndpointString);
            Uri IdmsTokenEndPointUri=Uri.parse(IdmsTokenEndPointString);
            if(IdmsAuthEndpointUri!=null && IdmsTokenEndPointUri!=null){
                return mAuthenticationService.initConfigure(context,
                        IdmsAuthEndpointUri,
                        IdmsTokenEndPointUri);
            }else{
                return false;
            }

        }
    }

    //Step 1: Get UE init config and then get default user profile.
    public boolean initConfiguration(Context context){
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        return initConfiguration( context,ngnSipPrefrences);
    }

    private boolean executeLogicMachine(Context context){
        boolean result=false;
        String mcpttIdNow;
        HashMap<String,String> userProfiles;
        switch (stateCMSNow) {
            case NONE:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: NONE");
                stateCMSNow=INIT;
                result= executeLogicMachine(NgnApplication.getContext());
                break;
            case INIT:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: INIT");
                Log.i(TAG,"Start CMS service.");
                result= getMcpttUEInitConfig(context);
                break;
            case WITH_UE_INIT_CONFIG:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: WITH_UE_INIT_CONFIG");
                Log.i(TAG,"Receive UE init config.");
                if(mcpttUeInitConfigNow!=null &&
                        mcpttUeInitConfigNow.getMcpttUEInitialConfiguration()!=null &&
                        mcpttUeInitConfigNow.getMcpttUEInitialConfiguration() instanceof McpttUEInitialConfiguration){
                        McpttUEInitialConfiguration mcpttUEInitialConfiguration=mcpttUeInitConfigNow.getMcpttUEInitialConfiguration();
                        if(mcpttUEInitialConfiguration.getDefaultUserProfile()!=null && !mcpttUEInitialConfiguration.getDefaultUserProfile().isEmpty()){
                            String pathUserProfileDefault=mcpttUEInitialConfiguration.getDefaultUserProfile().get(0).getUserID();
                            short indexUserProfileDefault=-1;
                            indexUserProfileDefault=mcpttUEInitialConfiguration.getDefaultUserProfile().get(0).getUserProfileIndex();
                            if(pathUserProfileDefault!=null && !pathUserProfileDefault.isEmpty()){
                                result=getMcpttUserProfileConfig(context,pathUserProfileDefault.trim()+"/"+MCPTT_USER_PROFILE_PATH_DEFAULT+(((indexUserProfileDefault>0))?(MCPTT_USER_PROFILE_PATH_SEPARATOR_DEFAULT+""+indexUserProfileDefault):"")+MCPTT_USER_PROFILE_PATH_SUFFIX_DEFAULT,true);
                            }else{
                                if(BuildConfig.DEBUG)Log.d(TAG,"The mcptt Ue-init-config does not have DefaultUserProfile tag. And It is not possible to download the user-profile default");
                                //Pass to next step.
                                authenticationInitIdmsOrWithUserProfile();
                            }
                        }else{
                            Log.e(TAG,"Invalid mcptt UE init config parameter.");
                        }
                        //Configure data from UEinitConfig in the UE
                        if(!configureWithUEInitConfigNow(NgnEngine.getInstance().getProfilesService().getProfileNow(context)))
                            Log.e(TAG,"It´s possible to configure the data with mcptt UE Init config.");

                }else{
                    Log.e(TAG,"Invalid mcptt UE init config parameter.");
                }
                break;
            case PRE_AUTHENTICATION:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: PRE_AUTHENTICATION");
                Log.i(TAG,"Received default user profile.");
                Log.d(TAG,"Authentication necessary.");

                Log.d(TAG,"Select self authentication.");
                NgnSipPrefrences ngnSipPrefrences;
                if(mcpttUeInitConfigNow!=null &&
                        (ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context))!=null &&
                        ngnSipPrefrences.getIdmsAuthEndpoint()!=null &&
                        ngnSipPrefrences.getIdmsTokenEndPoint()!=null &&
                        !ngnSipPrefrences.getIdmsAuthEndpoint().isEmpty() &&
                        !ngnSipPrefrences.getIdmsTokenEndPoint().isEmpty()){
                    try{
                        Uri authEndPoint=Uri.parse(ngnSipPrefrences.getIdmsAuthEndpoint());
                        Uri tokenEndPoint=Uri.parse(ngnSipPrefrences.getIdmsTokenEndPoint());
                        mAuthenticationService.initConfigure(NgnApplication.getContext(),authEndPoint,tokenEndPoint);
                    }catch (Exception e){
                        Log.e(TAG,"Error processing IDMS Authentication or Token Endpoint. "+e.getMessage());
                    }
                }else{
                    mAuthenticationService.initConfigure(NgnApplication.getContext());
                }

                break;
            case AUTHENTICATION:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: AUTHENTICATION");
                Log.d(TAG,"User authenticated.");
                mcpttIdNow=mAuthenticationService.getMCPTTIdNow(context);
                if( mcpttIdNow!=null &&
                        (mcpttUserprofilePathNow=mcpttIdNow)!=null){
                    if(BuildConfig.DEBUG)Log.i(TAG,"AUTHENTICATION is correct and it define the mcptt UserProfile path:"+mcpttUserprofilePathNow);
                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error in authentication for mcptt UserProfile path default.");
                }
                //TODO: SEND ORDER TO SIP FOR REGISTED. AND WAIT
                break;
            case REGISTERED:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: REGISTERED");

                break;
                //TODO: INIT SUBSCRIBED TO CMS PSI
            case SUBSCRIBED:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: SUBSCRIBED");
                //TODO: WAIT TO RECEIVE NOTIFY
                break;
            case RECEIVED_NOTIFICATIONS:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: RECEIVED_NOTIFICATIONS");
                //TODO: process XCAPDIFF and get FILEs
                result=getMcpttUEConfig(context);
                break;
            case WITH_UE_CONFIG:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: WITH_UE_CONFIG");
                Log.d(TAG,"UE configuration downloaded.");
                if(mcpttUeConfigNow!=null){
                    //Configure data from UEinitConfig in the UE
                    if(!configureWithUEConfigNow(NgnEngine.getInstance().getProfilesService().getProfileNow(context)))
                        Log.e(TAG,"It´s possible to configure the data with UE config.");
                }


                mcpttIdNow=mcpttUserprofilePathNow;
                if(mcpttIdNow==null || mcpttIdNow.isEmpty()){
                 Log.e(TAG,"Error getting user profile, mcptt ID received not valid.");
                }else if(!setUserProfileForUse(mcpttIdNow,context))
                    Log.e(TAG,"Error: invalid user profile with mcptt id: " +mcpttIdNow);

                break;
            case DOWNLOAD_USER_PROFILE:

                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: DOWNLOAD_USER_PROFILE");
                Log.d(TAG,"User profile selected to download.");
                    if(mcpttUserprofilePathNow!=null){
                        getMcpttUserProfileConfig(context,mcpttUserprofilePathNow.trim()+"/"+MCPTT_USER_PROFILE_PATH_DEFAULT,false);
                    }else{
                        if(BuildConfig.DEBUG)Log.w(TAG,"mcptt User profile not selected.");
                    }
                break;
            case USER_SELECT_PROFILE:

                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: USER_SELECT_PROFILE");
                //TODO
                //check if the userProfile define the default
                boolean hasPreSelected=false;
                for(CMSData cmsData:this.mcpttUsersProfileNow.getCmsDataList()){
                    McpttUserProfile mcpttUserProfile;
                    if((mcpttUserProfile=cmsData.getMcpttUserProfile())!=null){
                        if(mcpttUserProfile.getPreSelectedIndication()!=null &&
                                mcpttUserProfile.getPreSelectedIndication().size()>=1){
                            mcpttUserProfileNow=cmsData;
                            if(BuildConfig.DEBUG)Log.d(TAG,"Select userProfile because "+((mcpttUserProfileNow.getMcpttUserProfile().getName().size()>=1)?mcpttUserProfileNow.getMcpttUserProfile().getName().get(0).getValue():"it")+" has <Pre-selected-indication/>");
                            hasPreSelected=true;

                        }
                    }

                }
                if(!hasPreSelected){
                    if(BuildConfig.DEBUG)Log.d(TAG,"no user profile has pre-select with what the user should be the one who chooses.");
                    //TODO: the user chooses the userprofile
                    List<String> profiles=getUserProfilesNames(context);
                    if(onGetMcpttUserProfileListener!=null && profiles!=null){
                        onGetMcpttUserProfileListener.onSelectMcpttUserProfile(profiles);
                    }else{
                        Log.e(TAG,"User profile not listened.");
                    }
                    if(this.mcpttUsersProfileNow.getCmsDataList().get(0)!=null){
                        this.mcpttUserProfileNow=this.mcpttUsersProfileNow.getCmsDataList().get(0);
                        if(BuildConfig.DEBUG)Log.d(TAG,"Selected User profile "+((mcpttUserProfileNow.getMcpttUserProfile().getName().size()>=1)?mcpttUserProfileNow.getMcpttUserProfile().getName().get(0):"it"));
                    }
                }

                if(this.mcpttUserProfileNow!=null){
                    if(BuildConfig.DEBUG)Log.d(TAG,"Change state to "+"WITH_USER_PROFILE");
                    stateCMSNow=WITH_USER_PROFILE;
                    executeLogicMachine(context);
                }

                break;

            case WITH_USER_PROFILE:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: WITH_USER_PROFILE");
                if(BuildConfig.DEBUG) Log.d(TAG,"Has user profile. Need to configure the terminal with this data.");
                //TODO:

                List<String> organizations;
                if(mcpttUserProfileNow!=null &&
                        mcpttUserProfileNow.getDataMCPTTCMS() instanceof McpttUserProfile &&
                        mcpttUserProfileNow.getMcpttUserProfile().getCommon()!=null &&
                        !mcpttUserProfileNow.getMcpttUserProfile().getCommon().isEmpty() &&
                        mcpttUserProfileNow.getMcpttUserProfile().getCommon().get(0)!=null &&
                        (organizations=mcpttUserProfileNow.getMcpttUserProfile().getCommon().get(0).getMissionCriticalOrganization())!=null &&
                        !organizations.isEmpty() &&
                        !organizations.get(0).isEmpty()){
                    //Configure parameters from user profile

                    configureWithUserProfile(NgnEngine.getInstance().getProfilesService().getProfileNow(context));

                    getMcpttServiceConfig(context,MCPTT_SERVICE_CONFIGURE_PATH_DEFAULT);
                    mcpttUserProfileNow.getMcpttUserProfile();
                }else{
                    Log.e(TAG,"Error downloading service configuration. User profile does not have necessary data.");
                }
                break;
            case WITH_SERVICE_CONFIG:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: WITH_SERVICE_CONFIG");
                ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
                configureWithServiceConfig(ngnSipPrefrences);
                stateCMSNow=STABLE;
                executeLogicMachine(context);
                 break;
            case STABLE:
                if(BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: STABLE");
                if(onStableListener!=null){
                    onStableListener.onStable();
                }else{
                    if(BuildConfig.DEBUG)Log.w(TAG,"No listener in STABLE.");
                }
                ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
                configureAllProfile(context,ngnSipPrefrences);

                //TODO:
                break;
            case CMS_UPDATING:

                if (BuildConfig.DEBUG)Log.d(TAG,"executeLogicMachine status: CMS_UPDATING");
                checkNotifyCMS(context);
                break;
        }
        return result;
    }
    //End logic Machine

    private void onGetMcpttUserProfile(McpttUserProfile mcpttUserProfile){
        if(onGetMcpttUserProfileListener!=null && mcpttUserProfile!=null){
            onGetMcpttUserProfileListener.onGetMcpttUserProfile(mcpttUserProfile);
        }else {
            if (BuildConfig.DEBUG) Log.e(TAG, "No listener in user profile.");
        }
        if(onGetMcpttUserProfile2Listener!=null && mcpttUserProfileNow!=null){
            onGetMcpttUserProfile2Listener.onGetMcpttUserProfile(mcpttUserProfile);
        }else {
            if (BuildConfig.DEBUG) Log.e(TAG, "No listener in user profile.");
        }
    }


    private void startServiceAuthenticationAfterTokenInUserProfile(@NonNull Context context){
        if(startServiceAuthenticationAfterToken(context)){
            if (BuildConfig.DEBUG) Log.d(TAG, "Sent publish for POCs");
        }else
        {
            if (BuildConfig.DEBUG) Log.e(TAG, "Error sent publish for POCs");
        }
    }



    //Init CMSData

    private CMSData getCMSData(Context context,RestService.ContentTypeData type){
        switch (type){
            case CONTENT_TYPE_MCPTT_EU_CONFIG:
                if(mcpttUeConfigNow==null)mcpttUeConfigNow=getCMSDataMcpttUEConfigMemory(context);
                return mcpttUeConfigNow;
            case CONTENT_TYPE_MCPTT_USER_PROFILE:
                if(mcpttUsersProfileNow==null)mcpttUsersProfileNow=getCMSDataMcpttUserProfileMemory(context);
                if(mcpttUserProfileDefault==null)mcpttUserProfileDefault=getCMSDataMcpttUserProfileDefaultMemory(context);
                return mcpttUserProfileNow;
            case CONTENT_TYPE_MCPTT_EU_INIT_CONFIG:
                if(mcpttUeInitConfigNow==null)mcpttUeInitConfigNow=getCMSDataMcpttUEInitConfigMemory(context);
                return mcpttUeInitConfigNow;
            case CONTENT_TYPE_MCPTT_SERVICE_CONFIG:
                if(mcpttServiceConfigurationNow==null)mcpttServiceConfigurationNow=getCMSDataMcpttServiceConfigMemory(context);
                return mcpttServiceConfigurationNow;



            case CONTENT_TYPE_NONE:
            default:
                Log.e(TAG,"Error in get CMS data");
                break;
        }
        return null;
    }

    private CMSDatas getCMSDatasMemory(Context context, String type){
        preferencesManager=new PreferencesManager(PREFERENCE_DATA_CMS);
        if(preferencesManager!=null && context!=null && type!=null && !type.isEmpty()){
            String data=preferencesManager.getString(context,type.trim());
            if(data.compareTo(PreferencesManager.STRING_DEFAULT)!=0){
                try {
                    CMSDatas cmsDatas=CMSUtils.getCMSDatas(data);
                    return cmsDatas;
                } catch (Exception e) {
                    Log.e(TAG, "Error getting CMS datas: "+e.getMessage());
                }
            }else{
                if(BuildConfig.DEBUG) Log.w(TAG,"Empty CMS data.");
            }
        }else if(context==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"Problem with data 1.");
        }else if(type==null || !type.isEmpty()){
            if(BuildConfig.DEBUG) Log.e(TAG,"Problem with data 2.");
        }else if(preferencesManager==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"Problem with data 3.");
        }
        Log.w(TAG,"Configuration not saved in memory for this UE or client.");
        return null;
    }
    private CMSData getCMSDataMcpttUEInitConfigMemory(Context context){
        CMSDatas datasNow= getCMSDatasMemory(context,PREFERENCE_DATA_CMS_MCPTT_UE_INIT_CONFIGURATION);
        if(datasNow==null || datasNow.getCmsDataList()==null || datasNow.getCmsDataList().isEmpty()){
            if(BuildConfig.DEBUG) Log.w(TAG,"No mcptt UE INIT CONF in memory.");
        }else{
            if(BuildConfig.DEBUG) Log.d(TAG,"mcptt UE INIT CONF: OK.");
        }
        return (datasNow!=null && datasNow.getCmsDataList()!=null && !datasNow.getCmsDataList().isEmpty())?datasNow.getCmsDataList().get(0):null;
    }
    private CMSData getCMSDataMcpttUEConfigMemory(Context context){
        CMSDatas datasNow= getCMSDatasMemory(context,PREFERENCE_DATA_CMS_MCPTT_UE_CONFIGURATION);
        if(datasNow==null || datasNow.getCmsDataList()==null || datasNow.getCmsDataList().isEmpty()){
            if(BuildConfig.DEBUG)Log.w(TAG,"No UE CONFIGURE in memory.");
        }else{
            if(BuildConfig.DEBUG) Log.d(TAG,"UE CONFIGURE OK.");
        }
        return (datasNow!=null && datasNow.getCmsDataList()!=null && !datasNow.getCmsDataList().isEmpty())?datasNow.getCmsDataList().get(0):null;
    }
    private CMSDatas getCMSDataMcpttUserProfileMemory(Context context){
        CMSDatas datasNow= getCMSDatasMemory(context,PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE);
        if(datasNow==null || datasNow.getCmsDataList()==null || datasNow.getCmsDataList().isEmpty()){
            if(BuildConfig.DEBUG)Log.w(TAG,"No USER PROFILE in memory.");
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"Get USER PROFILE: OK.");
        }
        return datasNow==null?new CMSDatas():datasNow;
    }

    private CMSData getCMSDataMcpttUserProfileDefaultMemory(Context context){
        CMSDatas datasNow= getCMSDatasMemory(context,PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE_DEFAULT);
        if(datasNow==null || datasNow.getCmsDataList()==null || datasNow.getCmsDataList().isEmpty()){
            if(BuildConfig.DEBUG)Log.w(TAG,"No USER PROFILE default in memory.");
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"Get USER PROFILE default: OK.");
        }
        return (datasNow!=null && datasNow.getCmsDataList()!=null && !datasNow.getCmsDataList().isEmpty())?datasNow.getCmsDataList().get(0):null;
    }

    private CMSData getCMSDataMcpttServiceConfigMemory(Context context){
        CMSDatas datasNow= getCMSDatasMemory(context,PREFERENCE_DATA_CMS_MCPTT_SERVICE_CONFIGURATION);
        if(datasNow==null || datasNow.getCmsDataList()==null || datasNow.getCmsDataList().isEmpty()){
            Log.w(TAG,"No SERVICE CONFIG in memory.");
        }else{
            Log.d(TAG,"Get SERVICE CONF: OK.");
        }
        return (datasNow!=null && datasNow.getCmsDataList()!=null && !datasNow.getCmsDataList().isEmpty())?datasNow.getCmsDataList().get(0):null;
    }






    private boolean setCMSDataMemory(Context context,String type,CMSData mcpttCMSData){
        if(preferencesManager!=null && context!=null && type!=null && !type.isEmpty()){
            String saveString=null;
            if(mcpttCMSData!=null && mcpttCMSData.getDataMCPTTCMS()!=null){
                try {
                    saveString=CMSUtils.getStringOfCMSData(context,mcpttCMSData);
                } catch (Exception e) {
                    Log.e(TAG, "Error saving CMS data: "+e.getMessage());
                    return false;
                }

            }else{
                Log.e(TAG,"Error saving CMS data.");
            }
            return preferencesManager.putString(context,type.trim(),saveString);


        }else{
            Log.e(TAG,"Unable to store CMS data.");
        }
        return false;
    }

    private boolean setCMSDataMemory(Context context,String type,CMSDatas mcpttCMSData){
        if(preferencesManager!=null && context!=null && type!=null && !type.isEmpty()){
            String saveString=null;
            if(mcpttCMSData!=null && mcpttCMSData.getCmsDataList()!=null && mcpttCMSData.getCmsDataList().size()>0){
                try {
                    saveString=CMSUtils.getStringOfCMSData(context,mcpttCMSData);
                } catch (Exception e) {
                    Log.e(TAG, "Error saving CMS data: "+e.getMessage());
                    return false;
                }

            }else{
                Log.e(TAG,"Error saving CMS data.");
            }
            return preferencesManager.putString(context,type.trim(),saveString);


        }else{
            Log.e(TAG,"Unable to store CMS data.");
        }
        return false;
    }

    private boolean setCMSDataMemory(Context context,CMSData mcpttCMSData){
        return setCMSDataMemory(context,mcpttCMSData,false);
    }

    private boolean setCMSDataMemory(Context context,CMSData mcpttCMSData, boolean isDefault){
        if(context!=null && mcpttCMSData!=null && mcpttCMSData.getDataMCPTTCMS()!=null){
            if(mcpttCMSData.getDataMCPTTCMS()instanceof McpttUEInitialConfiguration){
                return setCMSDataMcpttUEInitConfigMemory(context,mcpttCMSData);
            }else if(mcpttCMSData.getDataMCPTTCMS()instanceof McpttUEConfiguration){
                return setCMSDataMcpttUEConfigMemory(context,mcpttCMSData);
            }else if(mcpttCMSData.getDataMCPTTCMS()instanceof McpttUserProfile){
                if(isDefault){
                    return setCMSDataMcpttUserProfileDefaultMemory(context,mcpttCMSData);
                }else{
                    return setCMSDataMcpttUserProfileMemory(context,mcpttCMSData);
                }
            }else if(mcpttCMSData.getDataMCPTTCMS()instanceof ServiceConfigurationInfoType){
                return setCMSDataMcpttServiceConfiguration(context,mcpttCMSData);
            }else
            {
                Log.e(TAG,"Error saving CMS data 2.");
            }
        }else{
            Log.e(TAG,"Error saving CMS data.");
        }
        return false;
    }

    private boolean setCMSDataMcpttUEInitConfigMemory(Context context,CMSData mcpttCMSData){
        return setCMSDataMemory(context,PREFERENCE_DATA_CMS_MCPTT_UE_INIT_CONFIGURATION,new CMSDatas(mcpttCMSData));
    }
    private boolean setCMSDataMcpttUEConfigMemory(Context context,CMSData mcpttCMSData){
        return setCMSDataMemory(context,PREFERENCE_DATA_CMS_MCPTT_UE_CONFIGURATION,new CMSDatas(mcpttCMSData));
    }
    private boolean setCMSDataMcpttUserProfileMemory(Context context,CMSData mcpttCMSData){
        CMSDatas cmsDatas=getCMSDataMcpttUserProfileMemory(context);
        if(cmsDatas!=null){
            cmsDatas.addNewCMSData(mcpttCMSData);
        }else
            cmsDatas=new CMSDatas(mcpttCMSData);
        return setCMSDataMemory(context,PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE,cmsDatas);
    }
    private boolean setCMSDataMcpttUserProfileDefaultMemory(Context context,CMSData mcpttCMSData){
        return setCMSDataMemory(context,PREFERENCE_DATA_CMS_MCPTT_USER_PROFILE_DEFAULT,new CMSDatas(mcpttCMSData));
    }

    private boolean setCMSDataMcpttServiceConfiguration(Context context,CMSData mcpttCMSData){
        return setCMSDataMemory(context,PREFERENCE_DATA_CMS_MCPTT_SERVICE_CONFIGURATION,new CMSDatas(mcpttCMSData));
    }



    private boolean getMcpttUEInitConfig(final Context context){
        if(BuildConfig.DEBUG)Log.d(TAG,"getMcpttUEInitConfig for CMS");
        final CMSData mcpttUeInitConfig=getCMSDataMcpttUEInitConfigMemory(context);
        String etag=null;
        if(mcpttUeInitConfig!=null){
            if(mcpttUeInitConfig.getDataMCPTTCMS()!=null && mcpttUeInitConfig.getDataMCPTTCMS() instanceof McpttUEInitialConfiguration){
                mcpttUeInitConfigNow=mcpttUeInitConfig;
                etag=mcpttUeInitConfig.getEtag();
                }
            //Send check ETAG;
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"mcpttUEInitConfig no exist now");
        }
        String cmsUri=getCMSUri(context);
        if(cmsUri!=null && !cmsUri.isEmpty()){
            restService.setOnRestServiceListener(this);
            return restService.DownloaderMCPTTUEInitConfig(cmsUri,getMCPTTUEID(context),etag);
        }else{
            if(BuildConfig.DEBUG)Log.w(TAG,"cmsUri in getMcpttUEInitConfig is not correct.");
        }

        return false;
    }

    private void checkNotifyCMS(final Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "checkNotifyCMS for CMS");
        boolean result=false;
        //UEConfig
        DocumentType documentTypeUEConfig=getMcpttUEConfigXCAPDIFF();
        if(documentTypeUEConfig!= null){
                if(
                mcpttUeConfigNow==null ||
                mcpttUeConfigNow.getMcpttUEConfiguration()==null ||
                        !updateFileCMS(mcpttUeConfigNow,documentTypeUEConfig)){
                    if(BuildConfig.DEBUG)Log.d(TAG,"UE Configuration is not updated");
                    getMcpttUEConfig(context);
                }
        }
        //UsersProfile
        List<DocumentType> documentTypesUsersProfile=getMcpttUserProfileXCAPDIFF();
        if(documentTypeUEConfig!= null){
            boolean updateUserProfile=false;
            if(mcpttUsersProfileNow!=null && mcpttUserProfileNow!=null && mcpttUserProfileNow.getMcpttUserProfile()!=null){
                for(DocumentType documentType:documentTypesUsersProfile){
                    if(documentType!=null &&
                            mcpttUserProfileNow.getPath().compareTo(documentType.getSel())==0 &&
                            !updateFileCMS(mcpttUserProfileNow,documentType)){
                        updateUserProfile=true;
                    }
                }
            }else{
                updateUserProfile=true;;
            }
            if(updateUserProfile){
                if(BuildConfig.DEBUG)Log.d(TAG,"User Profile is not updated");
            }else{
                //TODO:
                //getMcpttUserProfileConfig()
            }
        }
        //ServiceConfig
        DocumentType documentTypeMcpttServiceConfigXCAPDIFF=getMcpttServiceConfigXCAPDIFF();
        if(documentTypeMcpttServiceConfigXCAPDIFF!= null){
            if(
                    mcpttServiceConfigurationNow==null ||
                            mcpttServiceConfigurationNow.getServiceConfigurationInfoType()==null ||
                            !updateFileCMS(mcpttServiceConfigurationNow,documentTypeMcpttServiceConfigXCAPDIFF)){
                if(BuildConfig.DEBUG)Log.d(TAG,"ServiceConfigurationInfo is not updated");
                getMcpttServiceConfig(context,MCPTT_SERVICE_CONFIGURE_PATH_DEFAULT);
            }
        }


    }

    private boolean updateFileCMS(CMSData oldCMSData,DocumentType documentTypeReceived){
        if(documentTypeReceived!= null){
            if(
                    oldCMSData!=null && oldCMSData.getEtag().compareTo(documentTypeReceived.getNewEtag())==0) {
                if(BuildConfig.DEBUG)Log.d(TAG,"File is updated");
                return true;
            }else{
                if(BuildConfig.DEBUG)Log.d(TAG,"File is not updated");
                return false;
            }
        }
        return false;
    }


    private boolean getMcpttUEConfig(final Context context){
        if(BuildConfig.DEBUG)Log.d(TAG,"getMcpttUEConfig for CMS");
        final CMSData mcpttUeConfig=getCMSDataMcpttUEConfigMemory(context);
        DocumentType documentTypeUEConfig=getMcpttUEConfigXCAPDIFF();
        String etag=null;
        String allSel=null;
        if(documentTypeUEConfig!=null)allSel=documentTypeUEConfig.getSel();
        if(mcpttUeConfig!=null){
            if(mcpttUeConfig.getDataMCPTTCMS()!=null && mcpttUeConfig.getDataMCPTTCMS() instanceof McpttUEConfiguration){
                mcpttUeConfigNow=mcpttUeConfig;
                etag=mcpttUeConfig.getEtag();
                if(documentTypeUEConfig!=null && documentTypeUEConfig.getNewEtag().compareTo(etag)==0){
                    if(BuildConfig.DEBUG)Log.d(TAG,"the"+" mcptt eu config "+"file has the etag equal to the most recent");
                    //TODO: It´s neccessary proccess new mcptt euconfig
                    onDownloaderXML(mcpttUeConfigNow,RestService.ContentTypeData.CONTENT_TYPE_MCPTT_EU_CONFIG);
                    return true;
                }
            }
            //Send check ETAG;
        }
        if(BuildConfig.DEBUG)Log.d(TAG,"mcptt UE downloader configuration.");
        if(BuildConfig.DEBUG && allSel!=null)Log.i(TAG,"Get mcptt UE config with parameter: "+allSel);
        String cmsUri=getCMSUri(context);
        if(cmsUri!=null && !cmsUri.isEmpty()){
            restService.setOnRestServiceListener(this);
            return restService.DownloaderMCPTTUEConfig(cmsUri,allSel,getMCPTTUEID(context),etag,getAccessToken(context));
        }
        return false;
    }

    private boolean getMcpttUserProfileConfig(final Context context,String pathFile,boolean isDefault){

        List<DocumentType> documentTypeProfileConfig=getMcpttUserProfileXCAPDIFF();
        final CMSDatas mcpttUsersProfile=(isDefault?null:getCMSDataMcpttUserProfileMemory(context));

        int con=mcpttUsersProfile!=null?mcpttUsersProfile.isExist(pathFile.trim()):-1;
        CMSData mcpttUserProfile=null;
        if(isDefault)mcpttUserProfile=getCMSDataMcpttUserProfileDefaultMemory(context);
        if(con>=0)mcpttUserProfile=mcpttUsersProfile.getCmsDataList().get(con);
        if(documentTypeProfileConfig!=null && !documentTypeProfileConfig.isEmpty() && !isDefault){
            boolean result=true;
            int con1=0;
            for(DocumentType documentType:documentTypeProfileConfig){
                //Downloader All UserProfile.
                if(BuildConfig.DEBUG){
                    con++;
                    Log.d(TAG,"Init downloader user profile num:"+con);
                }
                result&=getMcpttUserProfileConfig(context,documentType,mcpttUserProfile,pathFile,isDefault);
            }
            return result;
        }else{
            return getMcpttUserProfileConfig(context,mcpttUserProfile,pathFile,isDefault);
        }
    }

    private boolean getMcpttUserProfileConfig(final Context contextfinal, CMSData userProfile,String pathFile,boolean isDefault){
        return getMcpttUserProfileConfig(contextfinal,null,userProfile,pathFile,isDefault);
    }

    private boolean getMcpttUserProfileConfig(final Context context,final DocumentType documentType,final CMSData mcpttUserProfile,String pathFile,boolean isDefault){
        if(BuildConfig.DEBUG)Log.d(TAG,"getMcpttUserProfileConfig for CMS");
        Log.d(TAG,"User profile download initialization.");
        String etag=null;
        String allSel=null;
        if(documentType!=null && documentType.getSel()!=null)allSel=documentType.getSel();
        if(mcpttUserProfile!=null){
            if(mcpttUserProfile.getDataMCPTTCMS()!=null && mcpttUserProfile.getDataMCPTTCMS() instanceof McpttUserProfile){
                mcpttUserProfileNow=mcpttUserProfile;
                etag=mcpttUserProfile.getEtag();
                Log.d(TAG,"CMS mcptt User profile functions etag: "+etag);
                if(documentType!=null && documentType.getNewEtag().compareTo(etag)==0){
                    if(BuildConfig.DEBUG)Log.d(TAG,"the"+" mcptt User Profile "+"file has the etag equal to the most recent");
                    onDownloaderXML(mcpttUserProfileNow,RestService.ContentTypeData.CONTENT_TYPE_MCPTT_USER_PROFILE);
                    return true;
                }
            }
            //Send check ETAG;
        }
        String cmsUri=getCMSUri(context);
        if(cmsUri!=null && !cmsUri.isEmpty()){
            restService.setOnRestServiceListener(this);
            String accessToken=isDefault?null:getAccessToken(context);
            if(pathFile==null || pathFile.isEmpty()){
                return restService.DownloaderMCPTTUserProfiles(cmsUri,allSel,getMCPTTmcpttIdFile(context),etag,accessToken);
            }else{
                return restService.DownloaderMCPTTUserProfiles(cmsUri,allSel,pathFile,etag,accessToken);
            }

        }

        return false;
    }


    private boolean getMcpttServiceConfig(final Context context,String pathFile){
        if(BuildConfig.DEBUG)Log.d(TAG,"getMcpttServiceConfig for CMS");
        final CMSData mcpttServiceConfig=getCMSDataMcpttServiceConfigMemory(context);
        DocumentType documentTypeServiceConfig=getMcpttServiceConfigXCAPDIFF();
        String etag=null;
        String allSel=null;
        if(documentTypeServiceConfig!=null)allSel=documentTypeServiceConfig.getSel();
        if(mcpttServiceConfig!=null){
            if(mcpttServiceConfig.getDataMCPTTCMS()!=null && mcpttServiceConfig.getDataMCPTTCMS() instanceof ServiceConfigurationInfoType){
                mcpttServiceConfigurationNow=mcpttServiceConfig;
                etag=mcpttServiceConfig.getEtag();
                Log.d(TAG,"CMS mcptt Service Config functions etag: "+etag);
                if(documentTypeServiceConfig!=null && documentTypeServiceConfig.getNewEtag().compareTo(etag)==0){
                    if(BuildConfig.DEBUG)Log.d(TAG,"the"+" mcptt Service Profile "+"file has the etag equal to the most recent");
                    onDownloaderXML(mcpttServiceConfigurationNow,RestService.ContentTypeData.CONTENT_TYPE_MCPTT_SERVICE_CONFIG);
                    return true;
                }
            }
            //Send check ETAG;
        }
        String cmsUri=getCMSUri(context);
        if(cmsUri!=null && !cmsUri.isEmpty()){
            restService.setOnRestServiceListener(this);
            String accessToken=getAccessToken(context);
            if(pathFile==null || pathFile.isEmpty()){
                return restService.DownloaderMCPTTServiceConfig(cmsUri,allSel,getMCPTTServiceConfigurationFile(context),etag,accessToken);
            }else{
                return restService.DownloaderMCPTTServiceConfig(cmsUri,allSel,pathFile,etag,accessToken);
            }

        }

        return false;
    }







    private String getCMSUri(Context context){
        String cmsUri=mProfilesService.getProfileNow(context).getCMSXCAPRootURI();
        if(BuildConfig.DEBUG)if(cmsUri==null || cmsUri.isEmpty())Log.w(TAG,"CMSXCAPRootURI is null");
        return cmsUri;
    }


    //End CMSData

    //INIT PROCESS XCAPDIFF
    private List<DocumentType> getDocumentTypeXCAPDIFF(String typeDocument){
        ArrayList<DocumentType> result=new ArrayList<>();
        if(currentXcapDiff!=null && currentXcapDiff.getDocument()!=null){
            for(DocumentType documentType:currentXcapDiff.getDocument()){
                if(documentType.getSel().contains(typeDocument.trim()))
                    result.add(documentType);
            }
        }else{
            if(BuildConfig.DEBUG)Log.i(TAG,"NOw, the device doesn´t have a XcapDiff from CMS");
        }

        return result;
    }

    private DocumentType getMcpttUEConfigXCAPDIFF(){
        DocumentType documentType=null;
        List<DocumentType> result=getDocumentTypeXCAPDIFF(RestService.PATH_MCPTT_EU_CONFIG);
        if(result==null && result.isEmpty())return null;
        try {
            documentType=result.get(0);
        }catch (Exception e){
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in getUEConfigXCAPDIFF: "+e.getMessage());
        }
        return documentType;
    }

    private DocumentType getMcpttServiceConfigXCAPDIFF(){
        DocumentType documentType=null;
        List<DocumentType> result=getDocumentTypeXCAPDIFF(RestService.PATH_MCPTT_SERVICE_CONFIG);
        if(result==null && result.isEmpty())return null;
        try {
            documentType=result.get(0);
        }catch (Exception e){
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in getServiceConfigXCAPDIFF: "+e.getMessage());
        }
        return documentType;
    }


    private List<DocumentType> getMcpttUserProfileXCAPDIFF(){
        DocumentType documentType=null;
        List<DocumentType> result=getDocumentTypeXCAPDIFF(RestService.PATH_MCPTT_USER_PROFILE);
        if(result==null && result.isEmpty())return null;
        return result;
    }



    //END PROCESS XCAPDIFF

    @Override
    public String register(@NonNull Context context){
        Log.d(TAG,"Device registered.");
        if(mAuthenticationService==null) mAuthenticationService=NgnEngine.getInstance().getAuthenticationService();
        if((context!=null) && (mAuthenticationService!=null)){
            return mAuthenticationService.register(context);

        }else{
            Log.e(TAG,"Error Registering: null.");
        }
        return null;
    }
    @Override
    public boolean startServiceAuthenticationAfterToken(@NonNull Context context){
        //Init Subscribe CMS
        if(BuildConfig.DEBUG)Log.i(TAG,"start service CMS");

        cmsChange(true,context);
        Log.d(TAG,"Device registered and authenticated after token");
        return mAuthenticationService.startServiceAuthenticationAfterToken(context);
    }


    private boolean pauseServiceUnregistration(){
        //Init Subscribe CMS
        if(BuildConfig.DEBUG)Log.i(TAG,"stop service CMS");
        cmsChange(false,null);
        return true;
    }



    @Override
    public String getMCPTTUEID(Context context){
        NgnSipPrefrences ngnSipPrefrences;

        if(mcpttUEIdNow!=null){
            return mcpttUEIdNow;
        }else if((ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context))!=null &&
                ngnSipPrefrences.getMcpttUEId()!=null &&
                !ngnSipPrefrences.getMcpttUEId().isEmpty()){
            mcpttUEIdNow=ngnSipPrefrences.getMcpttUEId();
            return mcpttUEIdNow;
        }else{
            String uid=deviceUUID(context);
            if(uid!=null){
                mcpttUEIdNow=uid;
                return mcpttUEIdNow;
            }
        }
        return null;
    }
    @Override
    public  String getMCPTTmcpttIdFile(Context context){
        //TODO: generate path for file mcptt id. This date is in eu-init-config and ue-config
        return null;
    }
    @Override
    public String getMCPTTServiceConfigurationFile(Context context){
        //TODO: search the path to File ServiceConfiguration
        return null;
    }


    private static String deviceUUID(Context ctx) {
        //API>22
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            Log.e(TAG,"App has no permission to get UUID.");
            return null;
        }
        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" +android.provider.Settings.Secure.getString(ctx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        Log.d(TAG,"Device Id:"+ deviceId);
        return deviceId;
    }




    //Init Listener CMS
    @Override
    public void setOnGetMcpttUEInitConfigurationListener(OnGetMcpttUEInitialConfigurationListener onGetMcpttUEInitialConfigurationListener){
        this.onGetMcpttUEInitialConfigurationListener=onGetMcpttUEInitialConfigurationListener;

    }



    @Override
    public void setOnGetMcpttUEConfigurationListener(OnGetMcpttUEConfigurationListener onGetMcpttUEConfigurationListener){
        this.onGetMcpttUEConfigurationListener=onGetMcpttUEConfigurationListener;

    }


    @Override
    public void setOnGetMcpttUserProfileListener(OnGetMcpttUserProfileListener onGetMcpttUserProfileListener){
        this.onGetMcpttUserProfileListener=onGetMcpttUserProfileListener;

    }

    @Override
    public void setOnGetMcpttUserProfile2Listener(OnGetMcpttUserProfile2Listener onGetMcpttUserProfile2Listener){
        this.onGetMcpttUserProfile2Listener=onGetMcpttUserProfile2Listener;

    }




    @Override
    public void setOnGetMcpttServiceConfListener(OnGetMcpttServiceConfListener onGetMcpttServiceConfListener) {
        this.onGetMcpttServiceConfListener = onGetMcpttServiceConfListener;
    }

    @Override
    public void setOnStableListener(OnStableListener onStableListener){
        this.onStableListener = onStableListener;
    }




    @Override
    public void setOnGetServiceConfigurationInfoTypeListener(OnGetServiceConfigurationInfoTypeListener onGetServiceConfigurationInfoTypeListener){
        this.onGetServiceConfigurationInfoTypeListener=onGetServiceConfigurationInfoTypeListener;

    }




    private void onDownloaderXML(CMSData cmsData,RestService.ContentTypeData contentTypeData){
        switch (contentTypeData){
            case CONTENT_TYPE_MCPTT_EU_INIT_CONFIG:
                if(cmsData!=null &&
                        cmsData.getDataMCPTTCMS()!=null &&
                        cmsData.getDataMCPTTCMS() instanceof McpttUEInitialConfiguration){

                    if(BuildConfig.DEBUG)Log.d(TAG,"Select UE init config.");
                    mcpttUeInitConfigNow=cmsData;

                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error processing the EU-init-config");
                }
                if(mcpttUeInitConfigNow!=null){
                    downloaderUserProfile();
                }



                break;
            case CONTENT_TYPE_MCPTT_EU_CONFIG:
                if(cmsData!=null &&
                        cmsData.getDataMCPTTCMS()!=null &&
                        cmsData.getDataMCPTTCMS() instanceof McpttUEConfiguration){

                    Log.d(TAG,"Select UE config.");
                    mcpttUeConfigNow=cmsData;

                }else{
                    Log.e(TAG,"Invalid CMS data received.");
                }
                if(mcpttUeConfigNow!=null){
                    downloaderUserProfile();
                }


                break;
            case CONTENT_TYPE_MCPTT_USER_PROFILE:
                //TODO: You can receive multiple USER PROFILES and you must wait to receive all to be forward. IMPORTANT
                if(cmsData!=null &&
                        cmsData.getDataMCPTTCMS()!=null &&
                        cmsData.getDataMCPTTCMS() instanceof McpttUserProfile){

                    Log.d(TAG,"Download User profile.");

                    switch (stateCMSNow) {
                        //Step 3: Authenticate on Idms and download UE config for this specific UE.
                        case WITH_UE_INIT_CONFIG:
                            addNewUserProfileDefault(cmsData);

                            break;
                            //Step 6:
                        case DOWNLOAD_USER_PROFILE:
                            addNewUserProfile(cmsData);
                            //Sent user info to Client Manager
                            if(cmsData!=null && cmsData.getMcpttUserProfile()!=null){
                                setUserConfiguration(cmsData.getMcpttUserProfile());
                            }else{
                                setUserConfigurationError();
                            }
                            break;
                        default:
                            Log.e(TAG, "Error 3: Invalid state for CMS service initialization 10. Current state:" + stateCMSNow.getText());
                            break;
                    }

                }else{
                    Log.e(TAG,"Error processing the USER-Profile 1");
                }
                if(mcpttUsersProfileNow!=null){
                    authenticationInitIdmsOrWithUserProfile();
                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"Error in CMS: error in current user profile");
                }

                break;
            case CONTENT_TYPE_MCPTT_SERVICE_CONFIG:
                if(cmsData!=null &&
                        cmsData.getDataMCPTTCMS()!=null &&
                        cmsData.getDataMCPTTCMS() instanceof ServiceConfigurationInfoType){

                    Log.d(TAG,"Select service config.");
                    mcpttServiceConfigurationNow=cmsData;

                }else if(cmsData==null){
                    Log.e(TAG,"Invalid received CMS data. 1");
                }else{
                    Log.e(TAG,"Invalid received CMS data.");
                }

                if(mcpttServiceConfigurationNow!=null){
                    receivedServiceConfig();
                }


                break;
            case CONTENT_TYPE_MCPTT_GROUPS:
                if(cmsData!=null &&
                        cmsData.getDataMCPTTCMS()!=null &&
                        cmsData.getDataMCPTTCMS() instanceof Group){

                    Log.d(TAG,"Select service config.");
                    mcpttServiceConfigurationNow=cmsData;

                }else if(cmsData==null){
                    Log.e(TAG,"Invalid received CMS data. 1");
                }else{
                    Log.e(TAG,"Invalid received CMS data.");
                }

                if(mcpttServiceConfigurationNow!=null){
                    receivedServiceConfig();
                }


                break;




            case CONTENT_TYPE_NONE:
            default:
                Log.e(TAG,"Invalid content-type.");
                break;
        }
        return;
    }

    

    private void addNewUserProfileDefault(CMSData cmsData){
        if(BuildConfig.DEBUG)Log.d(TAG,"addNewUserProfileDefault");
        mcpttUserProfileDefault=cmsData;
    }
    private void addNewUserProfile(CMSData cmsData){
        if(BuildConfig.DEBUG)Log.d(TAG,"addNewUserProfile");
        mcpttUsersProfileNow.addNewCMSData(cmsData);
        if(mcpttUsersProfileNew==null)mcpttUsersProfileNew=new CMSDatas();
        mcpttUsersProfileNew.addNewCMSData(cmsData);
    }


    @Override
    public void onDownloaderXML(String results, String etag,String path, int codeRespone, RestService.ContentTypeData contentTypeData) {
        Log.d(TAG,"XML CMS downloaded.");
        CMSData cmsData=null;
        switch (codeRespone){
            case HttpURLConnection.HTTP_OK:
                try {
                    Object cmsDataObject=CMSUtils.getMcpttCMSData(results.trim(),contentTypeData);
                    if(cmsDataObject!=null){
                        if(BuildConfig.DEBUG) Log.d(TAG,"Downloaded CMS data with eTAG:"+etag+" and path:"+path);
                        cmsData=new CMSData(etag,path,cmsDataObject);


                        switch (stateCMSNow){
                            case WITH_UE_INIT_CONFIG:
                                if(!setCMSDataMemory(NgnApplication.getContext(),cmsData,true)){
                                    Log.e(TAG,"Error saving CMS data in memory.");
                                }
                                break;
                            default:
                                if(!setCMSDataMemory(NgnApplication.getContext(),cmsData,false)){
                                    Log.e(TAG,"Error saving CMS data in memory.");
                                }
                                break;
                        }
                        Log.d(TAG,"Process OK.");
                    }else{
                        Log.d(TAG,"Error processing downloaded CMS data.");
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
        onDownloaderXML(cmsData,contentTypeData);
    }

    @Override
    public void errorOnDownloaderXML(String error,RestService.ContentTypeData contentTypeData) {
        if(contentTypeData!=null)
        Log.e(TAG,"Error in get data CMS: "+error+" from: "+contentTypeData.getText());
        switch (contentTypeData){
            case CONTENT_TYPE_MCPTT_EU_INIT_CONFIG:
                if(onGetMcpttUEInitialConfigurationListener!=null){
                    onGetMcpttUEInitialConfigurationListener.onGetmcpttUEInitialConfigurationError(error);
                }else{
                    Log.d(TAG,"Not define CONTENT_TYPE_MCPTT_EU_INIT_CONFIG");
                }
                break;
            case CONTENT_TYPE_MCPTT_EU_CONFIG:
                if(onGetMcpttUEConfigurationListener!=null){
                    onGetMcpttUEConfigurationListener.onGetMcpttUEConfigurationError(error);
                }else{
                    Log.d(TAG,"Not define CONTENT_TYPE_MCPTT_EU_CONFIG");
                }
                break;
            case CONTENT_TYPE_MCPTT_USER_PROFILE:
                if(onGetMcpttUserProfileListener!=null){
                    onGetMcpttUserProfileListener.onGetMcpttUserProfileError(error);
                }else{
                    Log.d(TAG,"Not define CONTENT_TYPE_MCPTT_USER_PROFILE");
                }
                break;
            case CONTENT_TYPE_MCPTT_SERVICE_CONFIG:
                if(onGetServiceConfigurationInfoTypeListener!=null){
                    onGetServiceConfigurationInfoTypeListener.onGetServiceConfigurationInfoTypeError(error);
                }else{
                    Log.d(TAG,"Not define CONTENT_TYPE_MCPTT_SERVICE_CONFIG");
                }
                break;
            case CONTENT_TYPE_MCPTT_GROUPS:

                break;
            case CONTENT_TYPE_NONE:
            default:
                Log.e(TAG,"The content-type isn´t correct: "+contentTypeData.getText());
                break;
        }
    }



    public void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener){
        this.onAuthenticationListener=onAuthenticationListener;
    }

    //End Listener CMS

    //Init Configure UE

    public boolean configureWithUEInitConfigNow(NgnSipPrefrences ngnSipPrefrences){
        if(mcpttUeInitConfigNow==null || ngnSipPrefrences==null){
            Log.d(TAG,"Invalid UE init config data.");
            return false;
        };
        //1º step configure the timers
        McpttUEInitialConfiguration mcpttUEInitialConfigurationNow;
        if((mcpttUEInitialConfigurationNow=mcpttUeInitConfigNow.getMcpttUEInitialConfiguration())!=null &&
                mcpttUEInitialConfigurationNow.getOnNetwork()!=null &&
                !mcpttUEInitialConfigurationNow.getOnNetwork().isEmpty()){
            OnNetworkType.Timers timers;
            if((timers=mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getTimers())!=null){

                if(ngnSipPrefrences==null){
                    Log.e(TAG,"Profile error.");
                    return false;
                }

                if(timers.getT100()>=0)
                    ngnSipPrefrences.setT100(timers.getT101());
                if(timers.getT101()>=0)
                    ngnSipPrefrences.setT101(timers.getT101());
                if(timers.getT103()>=0)
                    ngnSipPrefrences.setT103(timers.getT103());
                if(timers.getT104()>=0)
                    ngnSipPrefrences.setT104(timers.getT104());
                if(timers.getT132()>=0)
                    ngnSipPrefrences.setT132(timers.getT132());


            }else{
                Log.e(TAG,"UE timer configuration not possible.");
                return false;
            }
            //2º App-Server-Info: cms, gms, kms and idms
            OnNetworkType.AppServerInfo appServerInfo;
            if((appServerInfo=mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getAppServerInfo())!=null){

                if(ngnSipPrefrences==null){
                    Log.e(TAG,"Profile error.");
                    setConfigureNowCMSProfile(false);
                    return false;
                }

                if(appServerInfo.getIdmsAuthEndpoint()!=null &&
                        !appServerInfo.getIdmsAuthEndpoint().isEmpty())
                    ngnSipPrefrences.setIdmsAuthEndpoint(appServerInfo.getIdmsAuthEndpoint());
                if(appServerInfo.getIdmsTokenEndpoint()!=null &&
                        !appServerInfo.getIdmsTokenEndpoint().isEmpty())
                    ngnSipPrefrences.setIdmsTokenEndPoint(appServerInfo.getIdmsTokenEndpoint());
                if(appServerInfo.getCms()!=null &&
                        !appServerInfo.getCms().isEmpty())
                    ngnSipPrefrences.setMcpttPsiCMS(appServerInfo.getCms());
                if(appServerInfo.getGms()!=null &&
                        !appServerInfo.getGms().isEmpty())
                    ngnSipPrefrences.setMcpttPsiGMS(appServerInfo.getGms());
                if(appServerInfo.getKms()!=null &&
                        !appServerInfo.getKms().isEmpty())
                    ngnSipPrefrences.setKms(appServerInfo.getKms());



            }else{
                Log.e(TAG,"UE Data configuration not possible.");
                return false;
            }

            //3º configure XCAP from CMS and GMS
            if(mcpttUEInitialConfigurationNow.getOnNetwork().get(0)!=null){
                if(ngnSipPrefrences==null){
                    Log.e(TAG,"Profile error.");
                    setConfigureNowCMSProfile(false);
                    return false;
                }
                if(mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getCMSXCAPRootURI()!=null &&
                        !mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getCMSXCAPRootURI().trim().isEmpty())
                    ngnSipPrefrences.setCMSXCAPRootURI(mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getCMSXCAPRootURI());
                if(mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getGMSXCAPRootURI()!=null &&
                        !mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getGMSXCAPRootURI().trim().isEmpty())
                    ngnSipPrefrences.setGMSXCAPRootURI(mcpttUEInitialConfigurationNow.getOnNetwork().get(0).getGMSXCAPRootURI());
            }else{
                Log.e(TAG,"UE Data configuration not possible.");
                return false;
            }


        }

        if(onGetMcpttUEInitialConfigurationListener!=null && mcpttUeInitConfigNow!=null){
            onGetMcpttUEInitialConfigurationListener.onGetmcpttUEInitialConfiguration(mcpttUeInitConfigNow.getMcpttUEInitialConfiguration());
        }else{
            if(BuildConfig.DEBUG)Log.w(TAG,"No listener in UE init Config.");
        }



        return true;
    }

    public boolean configureWithUEConfigNow(NgnSipPrefrences ngnSipPrefrences){
        if(mcpttUeConfigNow==null || ngnSipPrefrences==null){
            Log.d(TAG,"Invalid UE config data.");
            return false;
        }
        McpttUEConfiguration mcpttUEConfigurationNow;
        if(ngnSipPrefrences!=null){
            if((mcpttUEConfigurationNow=mcpttUeConfigNow.getMcpttUEConfiguration())!=null
                    ){
                CommonType commonType;
                //1º step: configure common for this ue
                if((commonType=mcpttUEConfigurationNow.getCommon())!=null){
                    //element contains an integer indicating the maximum number of simultaneous calls (N10) allowed for an on-network or off-network private call with floor control
                    if(commonType.getPrivateCall()!=null){
                        BigInteger maxSimulCallN10=commonType.getPrivateCall().getMaxSimulCallN10();
                        ngnSipPrefrences.setMaxSimulCallN10(maxSimulCallN10);
                    }


                    if(commonType.getMCPTTGroupCall()!=null){
                        BigInteger maxSimulCallN4=commonType.getMCPTTGroupCall().getMaxSimulCallN4();
                        ngnSipPrefrences.setMaxSimulCallN4(maxSimulCallN4);
                        BigInteger maxSimulTransN5=commonType.getMCPTTGroupCall().getMaxSimulTransN5();
                        ngnSipPrefrences.setMaxSimulTransN5(maxSimulTransN5);
                        CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup prioritizedMCPTTGroup=commonType.getMCPTTGroupCall().getPrioritizedMCPTTGroup();
                        if(prioritizedMCPTTGroup!=null){
                            List<CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority> mcpttGroupPriorities= prioritizedMCPTTGroup.getMCPTTGroupPriority();
                            ArrayList<NgnSipPrefrences.MCPTTGroupPriority> mcpttGroupPrioritiesPreference=new ArrayList<>();
                            for(CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority mcpttGroupPriority:mcpttGroupPriorities){
                                if(mcpttGroupPriority!=null &&
                                        mcpttGroupPriority.getGroupPriorityHierarchy()!=null &&
                                        mcpttGroupPriority.getMCPTTGroupID()!=null &&
                                        !mcpttGroupPriority.getMCPTTGroupID().isEmpty()){
                                    mcpttGroupPrioritiesPreference.add(new NgnSipPrefrences.MCPTTGroupPriority(mcpttGroupPriority.getMCPTTGroupID(),mcpttGroupPriority.getGroupPriorityHierarchy()));
                                    ngnSipPrefrences.setMcpttGroupPriority(mcpttGroupPrioritiesPreference);
                                }
                            }
                        }
                    }



                }
                org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.OnNetworkType onNetworkType;
                //2º step the date on network
                if((onNetworkType=mcpttUEConfigurationNow.getOnNetwork())!=null){
                    Boolean iPv6Preferred=onNetworkType.isIPv6Preferred();
                    ngnSipPrefrences.setiPv6Preferred(iPv6Preferred);
                    Boolean relayService=onNetworkType.isRelayService();
                    ngnSipPrefrences.setRelayService(relayService);
                    RelayedMCPTTGroupType relayedMCPTTGroup;
                    if((relayedMCPTTGroup=onNetworkType.getRelayedMCPTTGroup())!=null){
                        if(relayedMCPTTGroup.getRelayMCPTTGroupID()!=null &&
                                relayedMCPTTGroup.getRelayMCPTTGroupID().size()>0 &&
                                relayedMCPTTGroup.getRelayMCPTTGroupID().get(0)!=null) {
                            if (relayedMCPTTGroup.getRelayMCPTTGroupID().get(0).getMCPTTGroupID() != null)
                                ngnSipPrefrences.getRelayedMCPTTGroup().setMcpttGroupID(relayedMCPTTGroup.getRelayMCPTTGroupID().get(0).getMCPTTGroupID());
                            if (relayedMCPTTGroup.getRelayMCPTTGroupID().get(0).getRelayServiceCode() != null)
                                ngnSipPrefrences.getRelayedMCPTTGroup().setRelayServiceCode(relayedMCPTTGroup.getRelayMCPTTGroupID().get(0).getRelayServiceCode());
                            }
                        }

                }

            }
        }


        if(onGetMcpttUEConfigurationListener!=null && onGetMcpttUEConfigurationListener!=null){
            onGetMcpttUEConfigurationListener.onGetMcpttUEConfiguration(mcpttUeConfigNow.getMcpttUEConfiguration());
        }else{
            Log.w(TAG,"No listener in UE Config.");
        }



        return true;
    }



    //WITH_USER_PROFILE
    private boolean configureWithUserProfile(NgnSipPrefrences ngnSipPrefrences){
        if(mcpttUserProfileNow==null || ngnSipPrefrences==null){
            Log.d(TAG,"Invalid User data profile.");
            return false;
        }
        McpttUserProfile mcpttUserProfile;
        org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.CommonType commonType;
        if(ngnSipPrefrences!=null) {
            if ((mcpttUserProfile = mcpttUserProfileNow.getMcpttUserProfile()) != null
                    ) {
                //1º step: configure common for this ue
                if (mcpttUserProfile.getCommon() != null &&
                        !mcpttUserProfile.getCommon().isEmpty() &&
                        (commonType = mcpttUserProfile.getCommon().get(0)) != null) {
                    EntryType entryType;
                    if (commonType.getmCPTTUserID() != null &&
                            !commonType.getmCPTTUserID().isEmpty() &&
                            (entryType = commonType.getmCPTTUserID().get(0)) != null) {
                        if (entryType.getUriEntry() != null) {
                            //check the MCPTT ID
                            if (ngnSipPrefrences.getMcpttId() == null || ngnSipPrefrences.getMcpttId().compareTo(entryType.getUriEntry().trim()) != 0) {
                                Log.e(TAG, "Data received in user profile does not correspong to our MCPTT ID:"+ngnSipPrefrences.getMcpttId());
                            } else {
                                if (entryType.getDisplayName() != null && entryType.getDisplayName().getValue() != null) {
                                    ngnSipPrefrences.setDisplayName(entryType.getDisplayName().getValue());
                                }
                            }
                        }


                    }

                    List<EntryType> privateCallList;
                    if (commonType.getPrivateCall() != null &&
                            !commonType.getPrivateCall().isEmpty() &&
                            commonType.getPrivateCall().get(0) != null &&
                            commonType.getPrivateCall().get(0).getPrivateCallList() != null &&
                            (privateCallList = commonType.getPrivateCall().get(0).getPrivateCallList().getPrivateCallURI()) != null) {
                        ngnSipPrefrences.setPrivateCallList(parseEntryType(ngnSipPrefrences,privateCallList));
                    }
                    if(commonType.getMCPTTGroupCall()!=null &&
                            !commonType.getMCPTTGroupCall().isEmpty() &&
                            commonType.getMCPTTGroupCall().get(0)!=null){
                        if(commonType.getMCPTTGroupCall().get(0).getMaxSimultaneousCallsN6()!=null &&
                                !commonType.getMCPTTGroupCall().get(0).getMaxSimultaneousCallsN6().isEmpty() &&
                                commonType.getMCPTTGroupCall().get(0).getMaxSimultaneousCallsN6().get(0)!=null){
                            ngnSipPrefrences.setMaxSimultaneousCallsN6(commonType.getMCPTTGroupCall().get(0).getMaxSimultaneousCallsN6().get(0));
                        }

                    }
                }

                //2º step: configure onNetwork for this ue
                org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.OnNetworkType onNetworkType;
                if (mcpttUserProfile.getOnNetwork() != null &&
                        !mcpttUserProfile.getOnNetwork().isEmpty() &&
                        (onNetworkType = mcpttUserProfile.getOnNetwork().get(0)) != null) {
                    if(onNetworkType.getMaxAffiliationsN2()!=null &&
                            !onNetworkType.getMaxAffiliationsN2().isEmpty() &&
                            onNetworkType.getMaxAffiliationsN2().get(0)!=null){
                        ngnSipPrefrences.setMaxAffiliationsN2(onNetworkType.getMaxAffiliationsN2().get(0));
                    }

                    if(onNetworkType.getmCPTTGroupInfo()!=null &&
                            !onNetworkType.getmCPTTGroupInfo().isEmpty() &&
                            onNetworkType.getmCPTTGroupInfo().get(0)!=null &&
                            onNetworkType.getmCPTTGroupInfo().get(0).getEntry()!=null &&
                            !onNetworkType.getmCPTTGroupInfo().get(0).getEntry().isEmpty()){
                        ngnSipPrefrences.setMCPTTGroupInfo(parseEntryType(ngnSipPrefrences,onNetworkType.getmCPTTGroupInfo().get(0).getEntry()));
                    }

                    if(onNetworkType.getImplicitAffiliations()!=null &&
                            !onNetworkType.getImplicitAffiliations().isEmpty() &&
                            onNetworkType.getImplicitAffiliations().get(0)!=null &&
                            onNetworkType.getImplicitAffiliations().get(0).getEntry()!=null &&
                            !onNetworkType.getImplicitAffiliations().get(0).getEntry().isEmpty()){
                        ngnSipPrefrences.setImplicitAffiliations(parseEntryType(ngnSipPrefrences,onNetworkType.getImplicitAffiliations().get(0).getEntry()));
                    }

                }

                org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.RuleType ruleType;
                org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.ExtensibleType extensibleType;

                if (mcpttUserProfile.getRuleset() != null &&
                        mcpttUserProfile.getRuleset().getRule()!=null &&
                        !mcpttUserProfile.getRuleset().getRule().isEmpty() &&
                        (ruleType = mcpttUserProfile.getRuleset().getRule().get(0)) != null &&
                        (extensibleType=ruleType.getActions())!=null) {
                    //Set all allows
                    ngnSipPrefrences.setAllowsUserProfile(extensibleType);
                }

                if (mcpttUserProfile.getUserProfileIndex()!=null && mcpttUserProfile.getUserProfileIndex()>0) {
                    //Set IndexUserProfile
                    ngnSipPrefrences.setIndexUserProfile(mcpttUserProfile.getUserProfileIndex());
                }
                //3º step: configure allows for this ue

            }
        }




        onGetMcpttUserProfile(mcpttUserProfileNow.getMcpttUserProfile());

        return true;
    }

    //WITH_SERVICE_CONFIG
    private boolean configureWithServiceConfig(NgnSipPrefrences ngnSipPrefrences){
        if(mcpttServiceConfigurationNow==null || ngnSipPrefrences==null){
            Log.d(TAG,"Invalid Data Service config.");
            return false;
        }
        ServiceConfigurationParamsType serviceConfigurationParamsType;
        org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.CommonType commonType;
        if(ngnSipPrefrences!=null && mcpttUserProfileNow!=null) {
            if (mcpttUserProfileNow.getServiceConfigurationInfoType()!=null &&
            (serviceConfigurationParamsType = mcpttUserProfileNow.getServiceConfigurationInfoType().getServiceConfigurationParams()) != null
                    ) {
                //1º step: configure common for this service config
                if (serviceConfigurationParamsType.getCommon() != null &&
                        !serviceConfigurationParamsType.getCommon().isEmpty() &&
                        (commonType = serviceConfigurationParamsType.getCommon().get(0)) != null) {
                    //TODO: in this momento we don´t process the item "common" because we don´t know this items
                }

                //2º step: configure onNetwork for this ue
                org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.OnNetworkType onNetworkType;
                if (serviceConfigurationParamsType.getOnNetwork() != null &&
                        !serviceConfigurationParamsType.getOnNetwork().isEmpty() &&
                        (onNetworkType = serviceConfigurationParamsType.getOnNetwork().get(0)) != null) {
                    //On-network

                    //Emergence call
                    if(onNetworkType.getEmergencyCall()!=null){

                        EmergencyCallType emergencyCallType=onNetworkType.getEmergencyCall();
                        Long data;
                        NgnSipPrefrences.EmergencyCallType emergencyCallPrefrences=ngnSipPrefrences.getEmergencyCall();
                        try {
                            emergencyCallPrefrences.setPrivateCancelTimeout(parseDurationToMiliSec(emergencyCallType.getPrivateCancelTimeout()));
                            emergencyCallPrefrences.setGroupTimeLimit(parseDurationToMiliSec(emergencyCallType.getGroupTimeLimit()));
                        } catch (DatatypeConfigurationException e) {
                            Log.e(TAG, "Error parsing data: Private timeout cancel "+e.toString());
                        }
                    }
                    //Private call
                    PrivateCallType privateCallType;
                    if((privateCallType=onNetworkType.getPrivateCall())!=null){

                        try {
                            ngnSipPrefrences.getPrivateCall().setHangTime(parseDurationToMiliSec(privateCallType.getHangTime()));
                            ngnSipPrefrences.getPrivateCall().setMaxDurationWithFloorControl(parseDurationToMiliSec(privateCallType.getMaxDurationWithFloorControl()));
                            ngnSipPrefrences.getPrivateCall().setMaxDurationWithoutFloorControl(parseDurationToMiliSec(privateCallType.getMaxDurationWithoutFloorControl()));

                        } catch (DatatypeConfigurationException e) {
                            Log.e(TAG, "Error parsing data: Private timeout cancel "+e.toString());
                        }

                    }

                    //FloorControlQueueType
                    FloorControlQueueType floorControlQueue;
                    if((floorControlQueue=onNetworkType.getFloorControlQueue())!=null){

                        try {
                            ngnSipPrefrences.getFloorControlQueue().setDepth(floorControlQueue.getDepth());
                            ngnSipPrefrences.getFloorControlQueue().setMaxUserRequestTime(parseDurationToMiliSec(floorControlQueue.getMaxUserRequestTime()));
                        } catch (DatatypeConfigurationException e) {
                            Log.e(TAG, "Error parsing data: Private timeout cancel "+e.toString());
                        }

                    }

                    //FcTimersCountersType
                    FcTimersCountersType fcTimersCountersType;
                    if((fcTimersCountersType=onNetworkType.getFcTimersCounters())!=null){

                        try {
                            ngnSipPrefrences.getFcTimersCounters().setT1EndOfRtpMedia(parseDurationToMiliSec(fcTimersCountersType.getT1EndOfRtpMedia()));
                            ngnSipPrefrences.getFcTimersCounters().setT3StopTalkingGrace(parseDurationToMiliSec(fcTimersCountersType.getT3StopTalkingGrace()));
                            ngnSipPrefrences.getFcTimersCounters().setT7FloorIdle(parseDurationToMiliSec(fcTimersCountersType.getT7FloorIdle()));
                            ngnSipPrefrences.getFcTimersCounters().setT8FloorRevoke(parseDurationToMiliSec(fcTimersCountersType.getT8FloorRevoke()));
                            ngnSipPrefrences.getFcTimersCounters().setT11EndOfRTPDual(parseDurationToMiliSec(fcTimersCountersType.getT11EndOfRTPDual()));
                            ngnSipPrefrences.getFcTimersCounters().setT12StopTalkingDual(parseDurationToMiliSec(fcTimersCountersType.getT12StopTalkingDual()));
                            ngnSipPrefrences.getFcTimersCounters().setT15Conversation(parseDurationToMiliSec(fcTimersCountersType.getT15Conversation()));
                            ngnSipPrefrences.getFcTimersCounters().setT16MapGroupToBearer(parseDurationToMiliSec(fcTimersCountersType.getT16MapGroupToBearer()));
                            ngnSipPrefrences.getFcTimersCounters().setT17UnmapGroupToBearer(parseDurationToMiliSec(fcTimersCountersType.getT17UnmapGroupToBearer()));
                            ngnSipPrefrences.getFcTimersCounters().setT20FloorGranted(parseDurationToMiliSec(fcTimersCountersType.getT20FloorGranted()));
                            ngnSipPrefrences.getFcTimersCounters().setT55Connect(parseDurationToMiliSec(fcTimersCountersType.getT55Connect()));
                            ngnSipPrefrences.getFcTimersCounters().setC7FloorIdle(fcTimersCountersType.getC7FloorIdle());
                            ngnSipPrefrences.getFcTimersCounters().setC17UnmapGroupToBearer(fcTimersCountersType.getC17UnmapGroupToBearer());
                            ngnSipPrefrences.getFcTimersCounters().setC20FloorGranted(fcTimersCountersType.getC20FloorGranted());
                            ngnSipPrefrences.getFcTimersCounters().setC55Connect(fcTimersCountersType.getC55Connect());
                            ngnSipPrefrences.getFcTimersCounters().setC56Disconnect(fcTimersCountersType.getC56Disconnect());
                        } catch (DatatypeConfigurationException e) {
                            Log.e(TAG, "Error parsing data: Private timeout cancel "+e.toString());
                        }

                    }

                    //SignallingProtection
                    SignallingProtectionType signallingProtectionType;
                    if((signallingProtectionType=onNetworkType.getSignallingProtection())!=null){
                            ngnSipPrefrences.getSignallingProtection().setConfidentialityProtection(signallingProtectionType.isConfidentialityProtection());
                            ngnSipPrefrences.getSignallingProtection().setIntegrityProtection(signallingProtectionType.isIntegrityProtection());
                    }

                }
            }
        }



        if(onGetMcpttServiceConfListener!=null && mcpttServiceConfigurationNow!=null){
            onGetMcpttServiceConfListener.onGetMcpttServiceConf(mcpttServiceConfigurationNow.getServiceConfigurationInfoType());
        }else{
            Log.w(TAG,"No listener in user profile.");
        }


        return true;
    }

    private List<NgnSipPrefrences.EntryType> parseEntryType(final NgnSipPrefrences ngnSipPrefrences,List<EntryType> entryTypesIn){
        if(entryTypesIn==null || ngnSipPrefrences==null){
            Log.e(TAG,"Invalid data.");
            return null;
        }
        ArrayList<NgnSipPrefrences.EntryType> entryTypesOut=new ArrayList<>();
        for (EntryType entryType : entryTypesIn) {
            NgnSipPrefrences.EntryType entryTypePrefrences = ngnSipPrefrences.new EntryType();
            if (entryType.getUriEntry() != null) {
                entryTypePrefrences.setUriEntry(entryType.getUriEntry());
                if (entryType.getDisplayName() != null &&
                        entryType.getDisplayName().getValue() != null)
                    entryTypePrefrences.setDisplayName(entryType.getDisplayName().getValue());
                entryTypesOut.add(entryTypePrefrences);
            }
        }
        return entryTypesOut;
    }
    private Duration parseStringToDuration(String durationString) throws DatatypeConfigurationException {
        if(durationString==null || durationString.isEmpty())return null;
        Duration duration= DatatypeFactory.newInstance().newDuration(durationString);
        return duration;
    }

    private Long parseDurationToMiliSec(Duration duration) throws DatatypeConfigurationException {
        if(duration==null)return null;
        return duration.getTimeInMillis(Calendar.getInstance());
    }

    private Long parseDurationToMiliSec(String durationString) throws DatatypeConfigurationException {
        return parseDurationToMiliSec(parseStringToDuration(durationString));
    }

    //End COnfigure UE

    public boolean configureAllProfile(Context context,NgnSipPrefrences ngnSipPrefrences){
        if(ngnSipPrefrences==null || context==null){
            Log.e(TAG,"Invalid data profile.");
            return false;
        }
        boolean success = true;
        if(!(loadAllConfigureCMS(context))){
            Log.d(TAG,"Error loading CMS configuration.");
            success &=false;
        }
        //UE init conf
        if(!(configureWithUEInitConfigNow(ngnSipPrefrences))){
            Log.d(TAG,"Error processing UEInitConfig.");
            success &=false;
        }
        //UE conf
        if(!(configureWithUEConfigNow(ngnSipPrefrences))){
            Log.d(TAG,"Error processing UEConfig.");
            success &=false;
        }
        //User profile
        if(!(configureWithUserProfile(ngnSipPrefrences))){
            Log.d(TAG,"Error processing UserProfile.");
            success &=false;
        }
        //Service config
        if(!(configureWithServiceConfig(ngnSipPrefrences))){
            Log.d(TAG,"Error processing ServiceConf.");
            success &=false;
        }
        setConfigureNowCMSProfile(success);
        return success;
    }

    private boolean loadAllConfigureCMS(Context context){
        Log.d(TAG,"CMS data loaded in memory.");
        if((mcpttUeInitConfigNow!=null || (mcpttUeInitConfigNow=getCMSDataMcpttUEInitConfigMemory(context))!=null))
            Log.d(TAG,"loaded mcptt UEInitCong etag:"+mcpttUeInitConfigNow.getEtag());
        if((mcpttUeConfigNow!=null ||(mcpttUeConfigNow=getCMSDataMcpttUEConfigMemory(context))!=null))
            Log.d(TAG,"loaded mcptt InitConf etag:"+mcpttUeConfigNow.getEtag());
        if((mcpttUsersProfileNow!=null ||(mcpttUsersProfileNow=getCMSDataMcpttUserProfileMemory(context))!=null))
            Log.d(TAG,"loaded mcptt UserProfile etag");
        if((mcpttUserProfileDefault!=null ||(mcpttUserProfileDefault=getCMSDataMcpttUserProfileDefaultMemory(context))!=null))
            Log.d(TAG,"loaded UserProfile DEFAULT etag");
        if((mcpttServiceConfigurationNow!=null ||(mcpttServiceConfigurationNow=getCMSDataMcpttServiceConfigMemory(context))!=null))
            Log.d(TAG,"loaded mcptt ServiceConf etag:"+mcpttServiceConfigurationNow.getEtag());



        return true;
    }


    public boolean deleteAllProfile(Context context){
        if( context==null){
            Log.e(TAG,"Data in profile isn´t valid");
            return false;
        }
        boolean success = false;
        if(!(deleteAllConfigureCMS(context))){
            Log.d(TAG,"Error in load all configure cms");
            success &=true;
        }
        //UE init conf
        if(!setCMSDataMcpttUEInitConfigMemory(context,null)){
            Log.d(TAG,"Error in delete UEInitConfig");
            success &=true;
        }
        //UE conf
        if(!setCMSDataMcpttUEConfigMemory(context,null)){
            Log.d(TAG,"Error in delete UEConfig");
            success &=true;
        }
        //User profile
        if(!(setCMSDataMcpttUserProfileMemory(context,null))){
            Log.d(TAG,"Error in delete UserProfile");
            success &=true;
        }
        //Service config
        if(!(setCMSDataMcpttServiceConfiguration(context,null))){
            Log.d(TAG,"Error in delete ServiceConf");
            success &=true;
        }
        //Delete parameter IDMS too
        mAuthenticationService.deleteToken(context);
        setConfigureNowCMSProfile(success);
        if(!success){
            Log.d(TAG,"Deleted all data from CMS");
            stateCMSNow=NONE;
        }
        return !success;
    }

    private boolean deleteAllConfigureCMS(Context context){
        Log.d(TAG,"delete data cms in memory");
        mcpttUeInitConfigNow=null;
        mcpttUeConfigNow=null;
        mcpttUserProfileNow=null;
        mcpttServiceConfigurationNow=null;



        return true;
    }



    public boolean isConfigureNowCMSProfile() {
        if(configureNowCMSProfile){
            Log.d(TAG,"Device configured with CMS data.");
        }else {
            Log.d(TAG,"Device not configured with CMS data.");
        }
        return configureNowCMSProfile;
    }

    private void setConfigureNowCMSProfile(boolean configureNowCMSProfile) {
        Log.d(TAG,"Set configure now CMS: "+configureNowCMSProfile);
        this.configureNowCMSProfile = configureNowCMSProfile;
    }
    @Override
    public boolean clearService(){
        pauseServiceUnregistration();
        return true;
    }

    @Override
    public void getAuthenticationToken(Uri uri){
        if(mAuthenticationService!=null){
            mAuthenticationService.getAuthenticationToken(uri);
        }else{
            Log.e(TAG,"Error ini service authentication");
        }
    }

    private void cmsChange(boolean isRegisted,Context context){
        NgnSipPrefrences currentProfile;
        if((currentProfile=NgnEngine.getInstance().getProfilesService().getProfileNow(context))!=null &&
                currentProfile.isMcpttEnableSubcriptionCMS()!=null &&
                currentProfile.isMcpttEnableSubcriptionCMS()){
            if(isRegisted){
                boolean result=false;
                switch (stateCMSNow){
                    //It´s registed
                    case AUTHENTICATION:
                        stateCMSNow=REGISTERED;
                        result=executeLogicMachine(NgnApplication.getContext());
                        break;
                    case WITH_SERVICE_CONFIG:
                    case STABLE:
                        if(BuildConfig.DEBUG)Log.d(TAG,"it is STABLE");
                        //TODO: It is necessary check file.
                        stateCMSNow=STABLE;
                        executeLogicMachine(context);
                        break;
                    default:
                        Log.e(TAG,"Error: Invalid state for CMS service initialization 6. Current state:"+stateCMSNow.getText());
                        result=false;
                        break;
                }
            }else{
                boolean result=false;
                switch (stateCMSNow){
                    case WITH_SERVICE_CONFIG:
                    case STABLE:
                    case CMS_UPDATING:
                        if(BuildConfig.DEBUG)Log.d(TAG,"it is STABLE");
                        //TODO: It is necessary check file.
                        stateCMSNow=STABLE;
                        break;
                    default:
                        Log.e(TAG,"Error: Invalid state for CMS service initialization 7. Current state:"+stateCMSNow.getText());
                        result=false;
                        break;
                }
            }
            if(!isRegisted){
                if(mCMSService!=null){
                    Log.d(TAG,"Unsubscribe CMS");
                    mCMSService.unSubscribeCMS();
                    mCMSService=null;
                }
            }else{
                mCMSService= MySubscriptionCMSSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack());
                if(mCMSService.subscribeCMS(getResoultList(context),getMCPTTInfoAccessToken(context))){
                    Log.d(TAG,"Subscribe sent CMS.");
                }
            }


        }

    }





    private String getResoultList(Context context){
        ResourceLists resourceLists=new ResourceLists();
        List<ListType> list=new ArrayList<>();
        ListType listType=new ListType();
        List<org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType> listEntryTypes=new ArrayList<>();
        org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType ueConf=new org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType();
        ueConf.setUri(RestService.getMCPTTUEConfigSub(getMCPTTUEID(context)));
        listEntryTypes.add(ueConf);
        org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType userProfile=new org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType();
        if(mcpttUserprofilePathNow!=null){
            userProfile.setUri(RestService.getMCPTTUserProfiles(mcpttUserprofilePathNow.trim()+"/"));
            listEntryTypes.add(userProfile);
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"User profile not selected.");
        }

        org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType serviceConfig=new org.doubango.ngn.datatype.ms.gms.ns.resource_lists.EntryType();

        List<String> organizations;
        if(mcpttUserProfileNow!=null &&
                mcpttUserProfileNow.getDataMCPTTCMS() instanceof McpttUserProfile &&
                mcpttUserProfileNow.getMcpttUserProfile().getCommon()!=null &&
                !mcpttUserProfileNow.getMcpttUserProfile().getCommon().isEmpty() &&
                mcpttUserProfileNow.getMcpttUserProfile().getCommon().get(0)!=null &&
                (organizations=mcpttUserProfileNow.getMcpttUserProfile().getCommon().get(0).getMissionCriticalOrganization())!=null &&
                !organizations.isEmpty() &&
                !organizations.get(0).isEmpty()){
            serviceConfig.setUri(RestService.getMCPTTServiceConfig(MCPTT_SERVICE_CONFIGURE_PATH_DEFAULT));
            listEntryTypes.add(serviceConfig);
        }
        listType.setEntry(listEntryTypes);
        list.add(listType);
        resourceLists.setList(list);
        try {
            return GMSUtils.getStringOfResourceLists(context,resourceLists);
        } catch (Exception e) {
            Log.e(TAG,"CMS processing error: "+e.getMessage());
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


    @Override
    public void setOnCMSPrivateContactsListener(OnCMSPrivateContactsListener onCMSPrivateContactsListener) {
        this.onCMSPrivateContactsListener = onCMSPrivateContactsListener;
    }

    private void setUserConfiguration(McpttUserProfile mcpttUserProfile) {
        if (onCMSPrivateContactsListener != null) {
                onCMSPrivateContactsListener.onCMSPrivateContacts(mcpttUserProfile);
        }else {
            if (BuildConfig.DEBUG) Log.e(TAG, "CMSPrivateContactsListener not defined.");
        }
    }

    private void setUserConfigurationError(){
        if(onCMSPrivateContactsListener!=null){
            onCMSPrivateContactsListener.onCMSPrivateContactsError();
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"CMSPrivateContactsListener not defined.");
        }
    }


}


