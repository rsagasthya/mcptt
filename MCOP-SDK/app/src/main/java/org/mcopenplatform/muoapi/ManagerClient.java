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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.affiliation.affiliationcommand.CommandList;
import org.doubango.ngn.datatype.affiliation.pidf.AffiliationType;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.datatype.affiliation.pidf.StatusType;
import org.doubango.ngn.datatype.affiliation.pidf.Tuple;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.doubango.ngn.datatype.ms.gms.ns.list_service.ListServiceType;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.services.affiliation.IMyAffiliationService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnUriUtils;
import org.mcopenplatform.muoapi.datatype.Client;
import org.mcopenplatform.muoapi.datatype.ClientSIM;
import org.mcopenplatform.muoapi.datatype.error.Constants;
import org.mcopenplatform.muoapi.datatype.group.GroupAffiliation;
import org.mcopenplatform.muoapi.datatype.group.GroupInfo;
import org.mcopenplatform.muoapi.managerIapi.EngineIapi;
import org.mcopenplatform.muoapi.managerIapi.ManagerConfigurationService;
import org.mcopenplatform.muoapi.managerIapi.ManagerMBMSGroupCom;
import org.mcopenplatform.muoapi.managerIapi.ManagerSimService;
import org.mcopenplatform.muoapi.session.ManagerSessions;
import org.mcopenplatform.muoapi.utils.Utils;

import java.io.Serializable;
import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mcopenplatform.muoapi.ManagerClientUtils.TypeParticipant.DISPLAY_NAME;
import static org.mcopenplatform.muoapi.ManagerClientUtils.TypeParticipant.TYPE;
import static org.mcopenplatform.muoapi.ManagerClientUtils.TypeParticipant.URI;
import static org.mcopenplatform.muoapi.utils.Utils.checkGroupIsExist;
import static org.mcopenplatform.muoapi.utils.Utils.isAffiliatedGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isAffiliatingGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isDeaffiliatedGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isDeaffiliatingGroup;
import static org.mcopenplatform.muoapi.utils.Utils.validationCallType;

public class ManagerClient implements
        IMyAffiliationService.OnAffiliationServiceListener
        , ManagerSessions.OnManagerSessionListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnAuthenticationListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnGetMcpttUserProfileListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnGetMcpttServiceConfListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnGetMcpttUEConfigurationListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnGetMcpttUEInitialConfigurationListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnStableListener
        , org.doubango.ngn.services.gms.IMyGMSService.OnGMSListener
        , org.doubango.ngn.services.cms.IMyCMSService.OnCMSPrivateContactsListener
        , INgnSipService.OnAuthenticationListener
{
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerClient.class.getCanonicalName());
    private ArrayList<Long> sessionsID;
    private Client client;
    private IBinder iBinder;
    private IMCOPCallback mMCOPCallback;
    private NgnEngine ngnEngine;
    private INgnSipService ngnSipService;
    private IMyAffiliationService myAffiliationService;
    private org.doubango.ngn.services.cms.IMyCMSService myCMSService;
    private org.doubango.ngn.services.gms.IMyGMSService myGMSService;
    private ManagerSessions managerSessions;
    private Context context;
    private ManagerClient thisInstance;
    private BroadcastReceiver mSipBroadcastRecvRegister;
    private ServiceConnection mConnectionSimService;
    private EngineIapi engineIapi;
    private String connectivityPluginPackageService;
    private String connectivityPluginPackageMain;
    private String simPluginPackageService;
    private String simPluginPackageMain;
    private String configurationPluginPackageService;
    private String configurationPluginPackageMain;
    private String mbmsPluginPackageService;
    private String mbmsPluginPackageMain;

    public ManagerClient(Context context
    ,String connectivityPluginPackageService
    ,String connectivityPluginPackageMain
    ,String simPluginPackageService
    ,String simPluginPackageMain
    ,String configurationPluginPackageService
    ,String configurationPluginPackageMain
    ,String mbmsPluginPackageService
    ,String mbmsPluginPackageMain
    ) {
        this.context=context;
        this.connectivityPluginPackageService = connectivityPluginPackageService;
        this.connectivityPluginPackageMain = connectivityPluginPackageMain;
        this.simPluginPackageService = simPluginPackageService;
        this.simPluginPackageMain = simPluginPackageMain;
        this.configurationPluginPackageService = configurationPluginPackageService;
        this.configurationPluginPackageMain = configurationPluginPackageMain;
        this.mbmsPluginPackageService = mbmsPluginPackageService;
        this.mbmsPluginPackageMain = mbmsPluginPackageMain;
        engineIapi=EngineIapi.getInstance();
        ngnEngine=NgnEngine.getInstance();
        thisInstance=this;
    }
    private ManagerClient getThisInstance(){
        return this;

    }

    private void selectActiveProfile(){
           engineIapi.getConfigurationService().setOnConfigurationServiceListener(new ManagerConfigurationService.OnConfigurationServiceListener() {
               @Override
               public void onConfigurationProfile(String profile) {
                   if(profile!=null && !profile.isEmpty()){
                       if(getThisInstance().importProfileMCOP2(profile)){
                           if(BuildConfig.DEBUG)Log.d(TAG,"Correct selectActiveProfile");
                       }else{
                           if(BuildConfig.DEBUG)Log.w(TAG,"No correct selectActiveProfile");
                       }
                   }
               }

               @Override
               public void onErroConfigurationProfile(String error) {
                if(BuildConfig.DEBUG)Log.e(TAG,"Error in Configuration Profile: "+error);
               }
           });

    }
    private void initEngineService(){

        Log.d(TAG,"Initialize MCOP Service");


        selectActiveProfile();
        initMBMSEvent();
        configureGetParameterSIM();
        if(ngnEngine.start()){
            Log.d(TAG,"MCOP Service: Started");
            ngnSipService=ngnEngine.getSipService();
            ngnSipService.setOnAuthenticationListener(this);

            myAffiliationService=ngnEngine.getAffiliationService();
            myCMSService=ngnEngine.getCMSService();
            if(myCMSService!=null){
                myCMSService.setOnCMSPrivateContactsListener(this);
            }
            myGMSService=ngnEngine.getGMSService();
            if(myGMSService!=null){
                myGMSService.setOnGMSListener(thisInstance);
            }
            //Start Manager session
            managerSessions=ManagerSessions.getInstance(context);
            managerSessions.setOnManagerSessionListener(this);
            sessionsID=new ArrayList<>();
            //Affiliation
            ngnEngine.getAffiliationService().setOnAffiliationServiceListener(thisInstance);
        }else{
            Log.e(TAG,"MCOP Service: Error");
        }
        if(mSipBroadcastRecvRegister==null){
            if(BuildConfig.DEBUG)Log.d(TAG,"SipBroadcastRecvRegister execute");
            mSipBroadcastRecvRegister = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();

                    // Registration Event
                    if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
                        NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                        if(args == null){
                            Log.e(TAG, "Invalid event arguments");
                            return;
                        }
                        switch(args.getEventType()){
                            case REGISTRATION_NOK:
                                if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_NOK");
                                sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVII);
                                sendLoginEvent(false);
                                break;
                            case UNREGISTRATION_OK:
                                if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_OK");
                                sendUnLoginEvent(true);
                                break;
                            case REGISTRATION_OK:
                                if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_OK");
                                //Send affiliation from groups implicit
                                affiliationImplicitGroups();
                                sendLoginEvent(true);
                                break;
                            case REGISTRATION_INPROGRESS:
                                if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_INPROGRESS");
                                //TODO:Logical for register
                                break;
                            case UNREGISTRATION_INPROGRESS:
                                if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_INPROGRESS");
                                //TODO:Logical for register
                                break;
                            case UNREGISTRATION_NOK:
                                if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_NOK");
                                sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError.CCVII);
                                sendUnLoginEvent(false);
                                break;
                        }
                    }
                }
            };
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
            context.registerReceiver(mSipBroadcastRecvRegister, intentFilter);
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"SipBroadcastRecvRegister isn´t null");
        }
    }

    private void showInfoPackageName(){
        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if(BuildConfig.DEBUG)Log.d(TAG,"callingApp: "+callingApp+"\nuid: "+Binder.getCallingUid() );
    }


    protected IBinder startManagerClient(boolean reBind){
        if(BuildConfig.DEBUG)Log.d(TAG,"startManagerClient");
        //Start and Binder to services
        if(!engineIapi.isStarted()){
            engineIapi.start(context
            ,connectivityPluginPackageService
            ,connectivityPluginPackageMain
            ,simPluginPackageService
            ,simPluginPackageMain
            ,configurationPluginPackageService
            ,configurationPluginPackageMain
            ,mbmsPluginPackageService
            ,mbmsPluginPackageMain
            );
        }else{
            if(BuildConfig.DEBUG)Log.w(TAG,"EngineIapi started");
        }
        //Start NgnEngine
        if(!ngnEngine.isStarted()){
            if(BuildConfig.DEBUG)Log.d(TAG,"Engine is no started");
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"Engine started");
            ngnEngine.stop();
        }
        initEngineService();



        if(!reBind)
        iBinder=new IMCOPsdk.Stub() {
            @Override
            public String getMCOPCapabilities() throws RemoteException {
                showInfoPackageName();
                return null;
            }



            @Override
            public boolean loginMCOP() throws RemoteException {
                showInfoPackageName();
                boolean result=false;
                if(ngnEngine.isStarted() && ngnSipService!=null && ngnSipService.isRegistered()){
                    sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCV);
                    result=false;
                }else{
                    if(myCMSService!=null){
                        myCMSService.setOnAuthenticationListener(thisInstance);
                        myCMSService.setOnGetMcpttUserProfileListener(thisInstance);
                        myCMSService.setOnGetMcpttServiceConfListener(thisInstance);
                        myCMSService.setOnStableListener(thisInstance);
                        myCMSService.setOnGetMcpttUEConfigurationListener(thisInstance);
                        myCMSService.setOnGetMcpttUEInitConfigurationListener(thisInstance);
                        result=myCMSService.initConfiguration(context);
                    }
                }

                return result;
            }

            @Override
            public boolean unLoginMCOP() throws RemoteException {
                showInfoPackageName();
                if(BuildConfig.DEBUG)Log.d(TAG,"Initialize unLogin Process");
                if(ngnSipService!=null && ngnSipService.isRegistered()){
                    ngnSipService.unRegister();
                    return true;
                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"Device unregistered");
                    sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError.CCV);
                }
                return false;
            }

            @Override
            public boolean authorizeUser(String url) throws RemoteException {
                showInfoPackageName();
                boolean result=false;
                Uri uri=null;
                try {
                    if(url==null || url.trim().isEmpty() || (uri=Uri.parse(url))==null){
                        result=false;
                        // For testing purposes only
                        // To avoid using IDMS/CMS
                        registerNow();
                        if(url!=null){
                            if(BuildConfig.DEBUG)Log.e(TAG,Constants.ConstantsErrorMCOP.LoginEventError.CCVI.getString());
                            sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVI);
                        }
                    }else{
                        if(BuildConfig.DEBUG && uri!=null && !url.trim().isEmpty())Log.d(TAG,"Authentication Token uri: "+url);
                        myCMSService.getAuthenticationToken(uri);
                        result=true;
                    }
                }catch (Exception e){
                    if(BuildConfig.DEBUG)Log.e(TAG,Constants.ConstantsErrorMCOP.LoginEventError.CCVI.getString());
                    sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVI);
                }


                return result;
            }

            @Override
            public boolean makeCall(String userID, int callType) throws RemoteException {
                showInfoPackageName();
                return makeCallMCOP(userID,callType,context);
            }

            @Override
            public boolean hangUpCall(String sessionID) throws RemoteException {
                showInfoPackageName();
                return hangUpCallMCOP( sessionID);
            }

            @Override
            public boolean acceptCall(String sessionID) throws RemoteException {
                showInfoPackageName();
                return acceptCallMCOP(sessionID);
            }

            @Override
            public boolean updateEmergencyState(String sessionID, int callType) throws RemoteException {
                showInfoPackageName();
                //TODO: NOW NO IMPLEMENT
                return false;
            }

            @Override
            public boolean floorControlOperation(String sessionID, int requestType, String UserID) throws RemoteException {
                showInfoPackageName();
                return floorControlOperationMCOP(sessionID,requestType,UserID);
            }

            @Override
            public boolean updateGroupsInfo() throws RemoteException {
                showInfoPackageName();
                //TODO: NOW NO IMPLEMENT
                return false;
            }

            @Override
            public boolean updateGroupsAffiliation() throws RemoteException {
                showInfoPackageName();
                if(BuildConfig.DEBUG)Log.d(TAG,"Execute updateGroupsAffiliation");
                return newPresence();
            }

            @Override
            public boolean groupAffiliationOperation(String groupMcpttId, int affiliationOperation) throws RemoteException {
                showInfoPackageName();
                return newOperationAffiliation(groupMcpttId,ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum.fromInt(affiliationOperation));
            }

            @Override
            public boolean changeSelectedContact(String groupID) throws RemoteException {
                showInfoPackageName();
                return false;
            }

            @Override
            public boolean registerCallback(IMCOPCallback mcopCallBack) throws RemoteException {
                showInfoPackageName();
                boolean result=false;
                if(mcopCallBack!=null){
                    mMCOPCallback=mcopCallBack;
                    result=true;
                }else{
                    Log.e(TAG,"CallBack Error");
                }
                if(BuildConfig.DEBUG)Log.d(TAG,"Execute registerCallback "+result);
                return result;
            }



        };



        return iBinder;
    }

    private void affiliationImplicitGroups(){
        //Affiliating to group from CMS
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null &&
                profile.isMcpttEnableCMS()!=null &&
                profile.isMcpttEnableCMS() &&
                profile.getMCPTTGroupInfo()!=null){
            List<String> stringsAffiliationGroup=org.doubango.utils.Utils.parseAccountToEntry(profile.getImplicitCheckedAffiliations());
            if(stringsAffiliationGroup!=null && !stringsAffiliationGroup.isEmpty()){
                Log.w(TAG,"Available groups for affiliation. Size: "+stringsAffiliationGroup.size());
                newOperationAffiliation(stringsAffiliationGroup, ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum.Affiliate);
            }else{
                Log.w(TAG,"No groups available for affiliation.");
            }
        }
    }


    public boolean stopManagerClient(){
        if(BuildConfig.DEBUG)Log.d(TAG, "stopManagerClient");
        System.exit(0);
        engineIapi.stop();
        if(managerSessions!=null)
        managerSessions.stopManagerSessions();
        if (mSipBroadcastRecvRegister != null) {
            if(BuildConfig.DEBUG)Log.d(TAG, "unregisterReceiver: Register");
            context.unregisterReceiver(mSipBroadcastRecvRegister);
            mSipBroadcastRecvRegister=null;
        }

        if(ngnSipService!=null && ngnSipService.isRegistered()){

            ngnSipService.unRegister();
            if(ngnEngine.isStarted()){
                ngnEngine.stop();
            }
        }

        return true;
    }



    public boolean onDestroyClient(){
        if(BuildConfig.DEBUG)Log.d(TAG, "onDestroyClient");
        if (mSipBroadcastRecvRegister != null) {
            if(BuildConfig.DEBUG)Log.d(TAG, "unregisterReceiver: Register");
            context.unregisterReceiver(mSipBroadcastRecvRegister);
            mSipBroadcastRecvRegister=null;
        }
        if(managerSessions!=null)
            managerSessions.stopManagerSessions();
        ngnEngine.stop();
        engineIapi.isStarted();
        engineIapi.stop();
        return false;
    }



    //Only for testing purposes
    public boolean selectProfileMCOP2(String data){
        if(ngnEngine.getProfilesService()==null || ngnEngine.getProfilesService().getProfilesNames(context)==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"Error in load profiles from MCOP SDK");
            return false;
        }
        for(String names:ngnEngine.getProfilesService().getProfilesNames(context)){
            if(BuildConfig.DEBUG) Log.d(TAG,"User Profile \""+names+"\"");
        }

        boolean result=ngnEngine.getProfilesService().setProfileNow(context,data);
        if(BuildConfig.DEBUG)Log.d(TAG,"Configure Profile \""+data+"\""+" result: "+result);
        return result;
    }

    //Only for testing purposes
    public boolean importProfileMCOP2(String profile){
        boolean result=false;
        if(ngnEngine!=null)
            result=ngnEngine.getProfilesService().importProfiles(profile,context);
        return result;
    }


    //START AUTHENTICATION EVENT
    private boolean sendAuthorizationRequestDataEvent(String requestUri, String redirectUri){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.authorizationRequestEvent.toString());
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.REQUEST_URI,requestUri);
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.REDIRECT_URI,redirectUri);
        return sendEvent(event);
    }

    private boolean sendErrorAuthorizationEvent(Constants.ConstantsErrorMCOP.AuthorizationRequestEventError authorizationRequestEventError){
        if(authorizationRequestEventError==null || mMCOPCallback==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or AuthorizationEvent is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.authorizationRequestEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ authorizationRequestEventError.getCode()+": "+ authorizationRequestEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_CODE,authorizationRequestEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_STRING,authorizationRequestEventError.getString());
        return sendEvent(event);
    }
    //END AUTHENTICATION EVENT

    //START LOGIN EVENT
    private boolean sendLoginEvent(boolean isRegisted){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.loginEvent.toString());
        event.putExtra(ConstantsMCOP.LoginEventExtras.SUCCESS,isRegisted);
        if(isRegisted && ngnEngine.getProfilesService().getProfileNow(context)!=null){
            String mcpttID=ngnEngine.getProfilesService().getProfileNow(context).getMcpttId();
            event.putExtra(ConstantsMCOP.LoginEventExtras.MCPTT_ID,mcpttID);
            String displayName=mcpttID;
            displayName=ngnEngine.getProfilesService().getProfileNow(context).getDisplayName();
            event.putExtra(ConstantsMCOP.LoginEventExtras.DISPLAY_NAME,displayName);
        }
        return sendEvent(event);
    }

    private boolean sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError loginEventError){
        if(loginEventError==null || mMCOPCallback==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or LoginEvent is null");
            return false;
        };
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.loginEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ loginEventError.getCode()+": "+ loginEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.LoginEventExtras.ERROR_CODE,loginEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.LoginEventExtras.ERROR_STRING,loginEventError.getString());
        return sendEvent(event);
    }
    //END LOGIN EVENT

    //START LOGOUT EVENT
    private boolean sendUnLoginEvent(boolean isUnRegisted){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.unLoginEvent.toString());
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.SUCCESS,isUnRegisted);
        return sendEvent(event);
    }

    private boolean sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError unLoginEventError){
        if(unLoginEventError==null || mMCOPCallback==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or unLoginEventError is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.unLoginEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ unLoginEventError.getCode()+": "+ unLoginEventError.getString());
        //Error Code
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_CODE,unLoginEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING,unLoginEventError.getString());
        return sendEvent(event);
    }
    //END LOGOUT EVENT

    //START CALL EVENT
    protected boolean makeCallMCOP(String userID, int type, Context context

                                   ){
        //TODO: Create priority logic for emergency calls
        if(!ngnEngine.isStarted() || !ngnSipService.isRegistered()){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVII);
            return false;
        }

        boolean answerMode=false;
        Boolean aBoolean=ngnEngine.getAuthenticationService().isAllowAutomaticCommencement(context);
        answerMode=((aBoolean==null || !aBoolean)?false:true);

        int priority=-1;
        Constants.CallEvent.CallTypeValidEnum typeCall=null;
        if((typeCall= validationCallType(type))!=null &&
                ((typeCall.getValue() & ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue())== ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue() ||
                (typeCall.getValue() & ConstantsMCOP.CallEventExtras.CallTypeEnum.ImminentPeril.getValue())== ConstantsMCOP.CallEventExtras.CallTypeEnum.ImminentPeril.getValue())
            )//TODO: now the SDK doesn´t have priority default.
            priority=9;
        return  makeCallMCOPStart( userID,  type
                , answerMode
                ,priority
                ,  context);
    }

    protected boolean makeCallMCOPStart(String userID, int type
            ,boolean answerMode
            , int priority
            , Context context){
        //TODO: Verify that the client has permission to perform the particular call type
        //With type select if valid call type
        boolean result=false;
        if(BuildConfig.DEBUG)Log.d(TAG,"makeCallMCOPStart");
        Constants.CallEvent.CallTypeValidEnum typeCall=null;
        if(userID==null || userID.trim().isEmpty() || !NgnUriUtils.isValidSipUri(userID)){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDIIII);
        }else if(type<=0 || (typeCall= validationCallType(type))==null){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDI);
        }else{
            NgnAVSession session=null;
            long newID=-1;
            NgnMediaType typeCallNgn=NgnMediaType.None;
            switch (typeCall) {
                case AudioWithoutFloorCtrlPrivate:
                    typeCallNgn=NgnMediaType.SessionAudioMCPTT;
                    break;
                case AudioWithFloorCtrlPrivate:
                    typeCallNgn=NgnMediaType.SessionAudioMCPTTWithFloorControl;
                    break;
                case AudioWithFloorCtrlPrivateEmergency:
                    typeCallNgn=NgnMediaType.SessionEmergencyAudioMCPTTWithFloorControl;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlPrivateEmergency  " + typeCallNgn.getValue());

                    break;
                case AudioWithFloorCtrlPrearrangedGroupEmergency:
                    typeCallNgn=NgnMediaType.SessionEmergencyAudioGroupMCPTTWithFloorControl;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlPrearrangedGroupEmergency  " + typeCallNgn.getValue());

                    break;
                case AudioWithFloorCtrlPrearrangedGroupImminentPeril:

                    typeCallNgn=NgnMediaType.SessionImminentperilAudioGroupMCPTTWithFloorControl;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlPrearrangedGroupImminentPeril  " + typeCallNgn.getValue());
                    break;
                case AudioWithFloorCtrlPrearrangedGroup:
                    typeCallNgn=NgnMediaType.SessionAudioGroupMCPTTWithFloorControl;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlPrearrangedGroup   " + typeCallNgn.getValue());

                    break;
                case AudioWithFloorCtrlChatGroupEmergency:
                    typeCallNgn=NgnMediaType.SessionEmergencyAudioWithFloorCtrlChatGroup;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlChatGroupEmergency   " + typeCallNgn.getValue());
                    break;
                case AudioWithFloorCtrlChatGroupImminentPeril:
                    break;
                case AudioWithFloorCtrlChatGroup:
                    typeCallNgn=NgnMediaType.SessionAudioChatGroupMCPTTWithFloorControl;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"SessionAudioChatGroupMCPTTWithFloorControl   " + typeCallNgn.getValue());
                    break;
                case AudioWithFloorCtrlBroadcastpEmergency:
                    break;
                case AudioWithFloorCtrlBroadcastImminentPeril:
                    break;
                case AudioWithFloorCtrlBroadcast:
                    break;
                case AudioWithFloorCtrlFirstToAnswer:
                    break;
                case AudioWithFloorCtrlPrivateCallCallback:
                    break;
                case AudioWithFloorCtrlRemoteAmbientListening:
                    break;
                case AudioWithFloorCtrlLocalAmbientListening:
                    break;
                case VideoAudioWithFloorCtrlPrivate:
                    break;
                case VideoAudioWithFloorCtrlPrivateEmergency:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroupEmergency:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroupImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroup:
                    break;
                case VideoAudioWithFloorCtrlChatGroupEmergency:
                    break;
                case VideoAudioWithFloorCtrlChatGroupImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlChatGroup:
                    break;
                case VideoAudioWithFloorCtrlBroadcastpEmergency:
                    break;
                case VideoAudioWithFloorCtrlBroadcastImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlBroadcast:
                    break;
                case VideoAudioWithFloorCtrlFirstToAnswer:
                    break;
                case VideoAudioWithFloorCtrlPrivateCallCallback:
                    break;
                case VideoAudioWithFloorCtrlRemoteAmbientListening:
                    break;
                case VideoAudioWithFloorCtrlLocalAmbientListening:
                    break;
                default:
                    Log.e(TAG,"The call type is not properly known");
                    break;
            }
            if(typeCallNgn==null || typeCallNgn==NgnMediaType.None){
                Log.e(TAG,"Session create error");
                sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDI);
            }else{
                session = NgnAVSession.createOutgoingSession(
                        NgnEngine.getInstance().getSipService().getSipStack(),
                        typeCallNgn
                );
                newID=managerSessions.newSession(session);
                if(BuildConfig.DEBUG)Log.d(TAG,"Create new session ID: "+newID);

                if(newID<=0){
                    Log.e(TAG,"Session create error");
                    sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVI);
                }else{
                    Log.d(TAG,"Init session");
                    result=session.makeCallMCPTT(
                            NgnUriUtils.makeValidSipUri(userID,context)
                            ,context
                            , answerMode
                            ,NgnAVSession.EmergencyCallType.MCPTT_P
                            ,priority
                    );
                }
            }

        }
        return result;
    }

    private boolean hangUpCallMCOP(String sessionID){
        if(managerSessions==null){
            return false;
        }
        return managerSessions.hangUpCall(sessionID);
    }

    private boolean acceptCallMCOP(String sessionID){
        if(managerSessions==null){
            return false;
        }
        return managerSessions.acceptCall(sessionID);
    }

    public boolean floorControlOperationMCOP(String sessionID, int requestType, String userID){
        try{
            ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum floorControlOperationTypeEnum=ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.fromInt(requestType);
            if(managerSessions==null){
                return false;
            }
            return managerSessions.floorControlOperation(sessionID,floorControlOperationTypeEnum,userID);
        }catch (Exception ex){

        }
        return false;
    }

    private boolean sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError){
        return sendErrorCallEvent(callEventError,null);
    }

    private boolean sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError,String sessionID){
        if(callEventError==null || mMCOPCallback==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or CallEvent is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        Log.e(TAG, "CallEvent Error "+ callEventError.getCode()+": "+ callEventError.getString());
        if(sessionID!=null && !sessionID.trim().isEmpty())
        event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        //put eventType ERROR
        event.putExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.ERROR.getValue());
        //put Code Error
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE,callEventError.getCode());
        //put String Error
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING,callEventError.getString());
        return sendEvent(event);
    }




    @Override
    public void onEvents(List<Intent> events) {
        sendEvents(events);
    }

    private synchronized boolean sendEvents(final List<Intent> events){
        if(events==null || events.isEmpty() ){
            if(BuildConfig.DEBUG) Log.e(TAG,"Event is null");
            return false;
        }else if(mMCOPCallback==null){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback is null");

        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                try {
                    if(BuildConfig.DEBUG)Log.i(TAG,"Send event");
                    mMCOPCallback.handleOnEvent(events);
                } catch (RemoteException e) {
                    Log.e(TAG,"Error sending event to client: "+e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG,"Error sending events: "+e.getMessage());
                }
            }
        });
        return true;
    }

    private boolean sendEvent(final Intent event){
        if(event==null ){
            if(BuildConfig.DEBUG)Log.e(TAG,"the event could not be sent");
            return false;
        }
        List<Intent> events=new ArrayList<>();
        events.add(event);
        return sendEvents(events);
    }


    private void registerNow(){
        Log.d(TAG,"Starting Registration Process");
        if(ngnSipService==null || ngnSipService.isRegistered() || !ngnSipService.register(false,context)){
            sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVII);
            if(BuildConfig.DEBUG)Log.e(TAG,"No Start register correct");
        }else{
            if(BuildConfig.DEBUG)Log.i(TAG,"Start register correct");
        }
    }


    //START Authentication Event
    @Override
    public void onAuthentication(String dataURI, String redirectionURI) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onAuthentication dataURI:"+dataURI+" redirectionURI:"+redirectionURI);
        if(dataURI!=null && redirectionURI!=null){
            sendAuthorizationRequestDataEvent(dataURI,redirectionURI);
        }else{
            sendErrorAuthorizationEvent(Constants.ConstantsErrorMCOP.AuthorizationRequestEventError.CI);
        }
    }

    @Override
    public void onAuthenticationOk(String data) {
        //If you use CMS and IDMS or IDMS only, it is necessary to register after being authenticated
        registerNow();
        //TODO: Define send info onAuthenticationOk
    }

    @Override
    public void onAuthenticationError(String error) {
        //TODO: Define send info onAuthenticationError
        sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVIII);
    }

    @Override
    public void onAuthenticationRefresh(String refresh) {
        //TODO: Define send info onAuthenticationRefresh
        if(BuildConfig.DEBUG)Log.d(TAG,"onAuthenticationRefresh");
    }

    @Override
    public void onGetmcpttUEInitialConfiguration(org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.McpttUEInitialConfiguration mcpttUEInitialConfiguration) {
        //TODO: Define send info onGetmcpttUEInitialConfiguration
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetmcpttUEInitialConfiguration");
    }

    @Override
    public void onGetmcpttUEInitialConfigurationError(String error) {
        //TODO: Define send info onGetmcpttUEInitialConfigurationError
        sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CV);
    }

    @Override
    public void onGetMcpttUEConfiguration(org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration mcpttUEConfiguration) {
        //TODO: Define send info onGetMcpttUEConfiguration
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttUEConfiguration");
    }

    @Override
    public void onGetMcpttUEConfigurationError(String error) {
        //TODO: Define send info onGetMcpttUEConfigurationError
        sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CV);
    }

    @Override
    public void onGetMcpttServiceConf(org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType mcpttServiceConf) {
        //TODO: the Process CMS is FINISH
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttServiceConf");
    }

    @Override
    public void onStable() {
        registerNow();
    }

    @Override
    public void onGetMcpttServiceConfError(String error) {
        sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CVII);
    }

    @Override
    public void onGetMcpttUserDefaultProfile(McpttUserProfile mcpttUserProfile) {
        //TODO: Process CMS UserProfile default
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttUserDefaultProfile");

    }

    @Override
    public void onGetMcpttUserProfile(org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile mcpttUserProfile) {
        //TODO: Process CMS UserProfile
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttUserProfile");
    }

    @Override
    public void onGetMcpttUserProfileError(String error) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onGetMcpttUserProfileError");

    }

    @Override
    public void onSelectMcpttUserProfile(List<String> mcpttUserProfiles) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onSelectMcpttUserProfile");

    }
    //END Authentication Event

    //START Affiliation Event
    @Override
    public void receiveNewPresence(Presence presence) {
        newPresence(presence);
    }

    @Override
    public void receiveNewPresenceResponse(Presence presence, String pid) {
        newPresence(presence);
    }

    private boolean newPresence(){
        Presence presence=myAffiliationService.getPresenceNow();
        return newPresence(presence);
    }

    private boolean newPresence(Presence presence){
        Log.d(TAG,"New affiliation data received");
        if(presence==null ){
            Log.e(TAG,"Erroneous affiliation data received.");
            return false;
        }

        List<GroupAffiliation> groupAffiliations= Utils.checkPresence(presence,context);
        if(BuildConfig.DEBUG && groupAffiliations!=null)Log.d(TAG,"The groups received in Affiliation:"+groupAffiliations.size());
        return sendGroupAffiliationEvent(groupAffiliations);
    }

    @Override
    public void expireAffiliations(Map<String, String> expires) {
        Presence presence=ngnEngine.getAffiliationService().getPresenceNow();
        String mcpttClientID=null;
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null)mcpttClientID=profile.getMcpttClientId();
        if(presence.getTuple()!=null && mcpttClientID!=null){
            for(Tuple tuple:presence.getTuple()){
                if(tuple!=null && presence.getTuple().get(0).getStatus()!=null && tuple.getStatus().getAffiliations()!=null && tuple.getId().trim().equals(mcpttClientID)){
                    for(AffiliationType affiliation:presence.getTuple().get(0).getStatus().getAffiliations()){
                        if(expires.get(affiliation.getGroup())!=null){
                            affiliation.setStatus(StatusType.deaffiliating);
                        }
                    }
                }
            }
        }
        newPresence(presence);
    }

    @Override
    public void receiveNewSelfAffiliation(CommandList commandList) {
        //TODO: Failed to develop the situation where another user asks for affiliation
    }

    @Override
    public void startNewServiceAffiliation() {
        //TODO: Possibly no longer needed, but it should be checked
    }

    private boolean newOperationAffiliation(List<String> groupIDs, ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum operationTypeEnum){
        boolean result=false;
        ngnEngine.getProfilesService().getProfileNow(context);
        ArrayList<String> groupsAffiliation=new ArrayList<>();
        ArrayList<String> groupsUnAffiliation=new ArrayList<>();
        if(groupIDs!=null)
        for(String groupID:groupIDs){
            if(groupID!=null && !groupID.trim().isEmpty() && operationTypeEnum!=null){
                Presence presence=null;
                switch (operationTypeEnum) {
                    case Affiliate:
                        //Verify that the group you want to AFFILIATE to is obtained from the CMS
                        presence=myAffiliationService.getPresenceNow();
                        if(presence==null
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVIII,groupID);
                            result=false;
                        }else if(!checkGroupIsExist(ngnEngine.getProfilesService().getProfileNow(context),groupID,context)
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVII,groupID);
                            result=false;

                        }else if(isAffiliatedGroup(presence,groupID,context)){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CV,groupID);
                            result=false;
                        }else if(isDeaffiliatedGroup(presence,groupID,context) || (!isDeaffiliatingGroup(presence,groupID,context) && !isAffiliatingGroup(presence,groupID,context))){
                            //The group exists
                            myAffiliationService.affiliationGroup(context,groupID);
                            groupsAffiliation.add(groupID);
                            result=true;
                        }else{
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CIX,groupID);
                        }
                        break;
                    case Deaffiliate:
                        //Verify that the group to DEAFFILIATE from was obtained from the CMS
                        presence=myAffiliationService.getPresenceNow();
                        if(presence==null
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVIII,groupID);
                            result=false;
                        }else if(!checkGroupIsExist(ngnEngine.getProfilesService().getProfileNow(context),groupID,context)
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVII,groupID);
                            result=false;

                        }else if(isDeaffiliatedGroup(presence,groupID,context)){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVI,groupID);
                            result=false;
                        }else if(isAffiliatedGroup(presence,groupID,context) || (!isDeaffiliatingGroup(presence,groupID,context) && !isAffiliatingGroup(presence,groupID,context))){
                            //The group exists
                            groupsUnAffiliation.add(groupID);
                            result=true;
                        }else {
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CIX,groupID);
                        }
                        break;
                }
            }else{
                sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CII,groupID);
                result=false;
            }
        }
        if(groupsAffiliation!=null && !groupsAffiliation.isEmpty())
            myAffiliationService.affiliationGroups(context,groupsAffiliation);
        if(groupsUnAffiliation!=null && !groupsUnAffiliation.isEmpty())
            myAffiliationService.unAffiliationGroups(context,groupsUnAffiliation);

        return result;
    }

    private boolean newOperationAffiliation(String groupID, ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum operationTypeEnum){
        List<String> groupIDs=new ArrayList<>();
        if(groupID!=null);
        groupIDs.add(groupID);
        return newOperationAffiliation(groupIDs,operationTypeEnum);
    }
    private boolean processNewGroup(org.doubango.ngn.datatype.ms.gms.ns.list_service.Group group){
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(group==null || profile==null || profile.getMcpttId()==null || profile.getMcpttId().isEmpty()){
            sendErrorGroupInfoEvent(Constants.ConstantsErrorMCOP.GroupInfoEventError.CI);
            return false;
        }
        ArrayList<GroupInfo> groupsInfo=new ArrayList<>();
        if(group.getListService()!=null)
        for(ListServiceType serviceType:group.getListService()){
            if(serviceType.getUri()!=null && !serviceType.getUri().trim().isEmpty()){

                Set<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowTypes= ManagerClientUtils.getAllowsGroups(profile.getMcpttId(),serviceType);
                Iterator<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowTypeIterator= allowTypes.iterator();
                ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo=null;
                while(allowTypeIterator.hasNext()){
                    ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum allowType=allowTypeIterator.next();
                    if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.non_real_time;
                    }else if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_urgent_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.non_urgent_real_time;
                    }else if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.urgent_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.urgent_real_time;
                    }
                }

                GroupInfo groupInfo=new GroupInfo(
                        serviceType.getUri(),
                        serviceType.getDisplayName().getValue(),
                        allowTypes,
                        actionRealTimeVideo,//TODO: Maybe not correct
                        serviceType.getMcdataonnetworkmaxdatasizeforSDS(),
                        serviceType.getMcdataonnetworkmaxdatasizeforFD(),
                        serviceType.getMcdataonnetworkmaxdatasizeautorecv(),
                        ManagerClientUtils.getParticipantGroupsWithTypes(serviceType,URI),
                        ManagerClientUtils.getParticipantGroupsWithTypes(serviceType,DISPLAY_NAME),
                        ManagerClientUtils.getParticipantGroupsWithTypes(serviceType,TYPE)
                );
                groupsInfo.add(groupInfo);
            }

        }
        sendGroupInfoEvent(groupsInfo);
        return true;
    }
    private boolean sendGroupAffiliationEvent(List<GroupAffiliation> groupAffiliations){
        if(BuildConfig.DEBUG)Log.d(TAG,"sendGroupAffiliationEvent");
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupAffiliationEvent.toString());
        HashMap<String, Integer> stringIntegerHashMap=(HashMap<String, Integer>)Utils.groupAffiliationToMap(groupAffiliations);
        if(BuildConfig.DEBUG)Log.d(TAG,"stringIntegerHashMap size: "+stringIntegerHashMap.size());
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE, ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.GROUP_AFFILIATION_UPDATE.getValue());
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUPS_LIST,stringIntegerHashMap);
        return sendEvent(event);
    }

    private boolean sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError  groupAffiliationEventError ,String groupID){
        if(groupAffiliationEventError==null || mMCOPCallback==null ){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or GroupAffiliationEvent is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupAffiliationEvent.toString());
        Log.e(TAG, "GroupAffiliationEvent Error "+ groupAffiliationEventError.getCode()+": "+ groupAffiliationEventError.getString());
        //Error Group ID
        if(groupID!=null && !groupID.trim().isEmpty())
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUP_ID,groupID);
        //Event Type
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.GROUP_AFFILIATION_ERROR.getValue());
        //Error Code
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_CODE, groupAffiliationEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_STRING, groupAffiliationEventError.getString());
        return sendEvent(event);
    }
    //END Affiliation Event

    //START Group Info Event
    private boolean sendGroupInfoEvent(List<GroupInfo> groupsInfo){
        if(BuildConfig.DEBUG)Log.d(TAG,"sendGroupInfoEvent");
        boolean result=true;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupInfoEvent.toString());
        for(GroupInfo groupInfo:groupsInfo)
            if(groupInfo!=null){
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.GROUP_ID,
                        groupInfo.getGroupID());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.DISPLAY_NAME,
                        groupInfo.getDisplayName());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ALLOWS_GROUP,
                        ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.getValue(groupInfo.getAllowList()));
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_FOR_SDS,
                        groupInfo.getMaxDataSizeForSDS());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_FOR_FD,
                        groupInfo.getMaxDataSizeForFD());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_AUTO_RECV,
                        groupInfo.getMaxDataSizeAutoRecv());
                if(groupInfo.getActionRealTimeVideo()!=null)
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ACTIVE_REAL_TIME_VIDEO_MODE,
                        groupInfo.getActionRealTimeVideo().toString());
                if(groupInfo.getActionRealTimeVideo()!=null)
                    event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ACTIVE_REAL_TIME_VIDEO_MODE,
                            groupInfo.getActionRealTimeVideo().toString());
                if(groupInfo.getParticipantsList()!=null){
                    event.putExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST,
                            new ArrayList<String>(groupInfo.getParticipantsList()));
                }
                if(groupInfo.getParticipantsList()!=null){
                    event.putStringArrayListExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST_DISPLAY_NAME,
                            new ArrayList<String>(groupInfo.getParticipantsListDisplay()));
                }
                if(groupInfo.getParticipantsList()!=null){
                    event.putStringArrayListExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST_TYPE,
                            new ArrayList<String>(groupInfo.getParticipantsListType()));
                }
                result&=sendEvent(event);


            }
        return result;
    }

    private boolean sendErrorGroupInfoEvent(Constants.ConstantsErrorMCOP.GroupInfoEventError  groupInfoEventError){
        if(groupInfoEventError==null || mMCOPCallback==null ){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or GroupInfoEvent is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupInfoEvent.toString());
        Log.e(TAG, "GroupInfoEvent Error "+ groupInfoEventError.getCode()+": "+ groupInfoEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ERROR_CODE, groupInfoEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ERROR_STRING, groupInfoEventError.getString());
        return sendEvent(event);
    }
    //END org.doubango.ngn.datatype.gms.pocListService.ns.list_service.Group Info Event

    //START SIM AUTH
    @Override
    public String onAuthRegister(String nonce) {
        try {
            //TODO: Should be decided as and where it decides to use a slot of SIM or an algorithm specific CHECK
            String response = engineIapi.getSimService().getAuthentication( nonce.trim());
            return response;
        } catch (RemoteException e) {
            Log.e(TAG,"SIM Authorization Error: "+e.getMessage());
        }
        return null;
    }


    private void configureGetParameterSIM(){
        if(engineIapi!=null)
            engineIapi.getSimService().setOnSimServiceListener(new ManagerSimService.OnSimServiceListener() {
                @Override
                public void onConfiguration(final String[] impu, String impi, String domain, String[] pcscf, int pcscfPort[], String imsi, String imei) {
                    if(org.mcopenplatform.muoapi.BuildConfig.DEBUG){
                        if(BuildConfig.DEBUG)Log.d(TAG,"Configuration client:");
                        if(pcscf!=null && pcscf.length>0){
                            if(BuildConfig.DEBUG)Log.d(TAG,"pcscf: "+pcscf[0]);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"pcscf is null");
                        if(pcscfPort!=null && pcscfPort.length>0){
                            if(BuildConfig.DEBUG)Log.d(TAG,"pcscf port: "+pcscfPort[0]);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"pcscf is null");
                        if(impu!=null && impu.length>0){
                            if(BuildConfig.DEBUG)Log.d(TAG,"impu: "+impu[0]);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"impu is null");
                        if(impi!=null){
                            if(BuildConfig.DEBUG) Log.d(TAG,"impi: "+impi);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"impi is null");
                        if(domain!=null){
                            if(BuildConfig.DEBUG)Log.d(TAG,"domain: "+domain);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"domain is null");
                        if(imei!=null) {
                            if(BuildConfig.DEBUG)Log.d(TAG,"imei: "+imei);
                        }else
                            if(BuildConfig.DEBUG)Log.w(TAG,"imei is null");
                        if(imsi!=null) Log.d(TAG,"imsi: "+imsi);
                    }
                    if(client==null)
                    client=new Client();
                    ClientSIM clientSIM=new ClientSIM(impu,impi,domain,pcscf,pcscfPort,imsi,imei);
                    client.setClientSIM(clientSIM);
                    configureParametersSIM();
                }
            });
    }

    private void configureParametersSIM(){
        ClientSIM clientSIM=null;
        if(client==null ||(clientSIM=client.getClientSIM())==null){
            Log.e(TAG,"it is not possible to configure the SIM parameters");
        }
        if(ngnEngine!=null && context!=null){
            NgnSipPrefrences ngnSipPrefrences=ngnEngine.getProfilesService().getProfileNow(context);
            if(BuildConfig.DEBUG)Log.d(TAG,"configureParametersSIM execute");
            //TODO: select the best impu and pcscf
            String impu=null;
            if(clientSIM.getImpu()!=null && clientSIM.getImpu().length!=0 && (impu=clientSIM.getImpu()[0])!=null){
                ngnSipPrefrences.setIMPU(impu);
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure IMPU");
            }

            if(clientSIM.getImpi()!=null && !clientSIM.getImpi().trim().isEmpty()){
                ngnSipPrefrences.setIMPI(clientSIM.getImpi().trim());
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure IMPI");
            }

            if(clientSIM.getDomain()!=null && !clientSIM.getDomain().trim().isEmpty()){
                ngnSipPrefrences.setRealm(clientSIM.getDomain().trim());
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure Domain");
            }


            if(clientSIM.getImsi()!=null && !clientSIM.getImsi().trim().isEmpty()){
                ngnSipPrefrences.setImsi(clientSIM.getImsi().trim());
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure IMSI");
            }

            if(clientSIM.getImei()!=null && !clientSIM.getImei().trim().isEmpty()){
                ngnSipPrefrences.setImei(clientSIM.getImei().trim());
            }
            else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure IMEI");
            }

            String pcscf=null;
            if(clientSIM.getPcscf()!=null && clientSIM.getPcscf().length!=0 && (pcscf=clientSIM.getPcscf()[0])!=null){
                ngnSipPrefrences.setPcscfHost(pcscf);
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure PCSCR");
            }

            if(clientSIM.getPcscfPort()!=null && clientSIM.getPcscfPort().length!=0 && clientSIM.getPcscfPort()[0]>0){
                ngnSipPrefrences.setPcscfPort(clientSIM.getPcscfPort()[0]);
            }else{
                if(BuildConfig.DEBUG)Log.w(TAG,"No configure PCSCR port");
            }
        }
    }


    //INIT GMS
    @Override
    public void onGMSErrorGroup(String error) {

    }

    @Override
    public void onGMSGroup(org.doubango.ngn.datatype.ms.gms.ns.list_service.Group group) {
        processNewGroup(group);
    }
    //END GMS


    //END SIM AUTH




    //INIT MBMS event
    private void initMBMSEvent(){
        if(engineIapi!=null)
        engineIapi.getMBMSGroupCom().setOnManagerMBMSGroupComListener(new ManagerMBMSGroupCom.OnManagerMBMSGroupComListener() {
            @Override
            public void startMbmsMedia(String sessionID, String tmgi) {
                if(BuildConfig.DEBUG)Log.d(TAG,"startMbmsMedia tmgi:"+tmgi+" sessionID:"+sessionID);
                sendMbmsInfoEvent(sessionID,tmgi,null, ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum.eMBMSBearerInUse);
            }

            @Override
            public void stopMbmsMedia(String sessionID, String tmgi) {
                if(BuildConfig.DEBUG)Log.d(TAG,"stopMbmsMedia tmgi:"+tmgi+" sessionID:"+sessionID);
                sendMbmsInfoEvent(sessionID,tmgi,null, ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum.eMBMSBearerNotInUse);
            }
        });

    }

    private boolean sendMbmsInfoEvent(@Nullable String sessionID, @Nullable  String tmgi, @Nullable String[] areaList,@NonNull ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum embmsNotificationEventEventTypeEnum){
        if(BuildConfig.DEBUG)Log.d(TAG,"sendMbmsInfoEvent");
        boolean result=true;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.eMBMSNotificationEvent.toString());
        event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.EVENT_TYPE,embmsNotificationEventEventTypeEnum.getValue());
        switch (embmsNotificationEventEventTypeEnum){
            case none:

                break;
            case NoeMBMSCoverage:
            case UndereMBMSCoverage:
                if(BuildConfig.DEBUG)Log.d(TAG,"NoeMBMSCoverage or UndereMBMSCoverage");

                //TODO
                break;
            case eMBMSBearerInUse:
            case eMBMSBearerNotInUse:
                if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSBearerInUse or eMBMSBearerNotInUse");
                if(sessionID!=null)
                event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.SESSION_ID,sessionID);
                if(tmgi!=null)
                event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.TMGI,tmgi);
                break;
            case eMBMSAvailable:
            case eMBMSNotAvailable:
                if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSAvailable or eMBMSNotAvailable");

                //TODO
                break;
            default:
                if(BuildConfig.DEBUG)Log.d(TAG,"event mbms default");
                break;
        }
                result&=sendEvent(event);

        return result;
    }

    private boolean sendErrorMbmsInfoEvent(Constants.ConstantsErrorMCOP.MbmsInfoEventError  mbmsInfoEventError){
        if(mbmsInfoEventError==null || mMCOPCallback==null ){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or mbmsInfoEventError is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupInfoEvent.toString());
        Log.e(TAG, "MbmsInfoEventError Error "+ mbmsInfoEventError.getCode()+": "+ mbmsInfoEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.ERROR_CODE, mbmsInfoEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.ERROR_STRING, mbmsInfoEventError.getString());
        return sendEvent(event);
    }
    //END MBMS event



    @Override
    public void onCMSPrivateContactsError() {
        sendConfigurationInfoError(Constants.ConstantsErrorMCOP.ConfigurationUpdateEventError.CI);
    }

    @Override
    public void onCMSPrivateContacts(McpttUserProfile mcpttUserProfile) {
        boolean success = false;
        if(mcpttUserProfile ==null || mMCOPCallback==null ){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or mcpttUserConfiguration info is null");
            //return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.configurationUpdateEvent.toString());

        if (mcpttUserProfile!=null && mcpttUserProfile.getCommon() != null && mcpttUserProfile.getCommon().size() > 0 && mcpttUserProfile.getCommon().get(0) != null) {

            org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.CommonType commonType = mcpttUserProfile.getCommon().get(0);
            if (commonType.getMissionCriticalOrganization() != null && commonType.getMissionCriticalOrganization().size() > 0 && commonType.getMissionCriticalOrganization().get(0) != null) {
                event.putExtra(ConstantsMCOP.ConfigurationUpdateEventExtras.ORGANIZATION, commonType.getMissionCriticalOrganization().get(0));
            }
            //DEFAULT_EMERGENCY_CONTACT
            if (commonType.getPrivateCall() != null && commonType.getPrivateCall().size() > 0 && commonType.getPrivateCall().get(0) != null &&
                    commonType.getPrivateCall().get(0).getEmergencyCall() != null && commonType.getPrivateCall().get(0).getEmergencyCall().getMCPTTGroupInitiation() != null
                    && commonType.getPrivateCall().get(0).getEmergencyCall().getMCPTTGroupInitiation().getEntry() != null
                    && commonType.getPrivateCall().get(0).getEmergencyCall().getMCPTTGroupInitiation().getEntry().getUriEntry() != null) {
                event.putExtra(ConstantsMCOP.ConfigurationUpdateEventExtras.DEFAULT_EMERGERCY_CONTACT, commonType.getPrivateCall().get(0).getEmergencyCall().getMCPTTGroupInitiation().getEntry().getUriEntry());
            }

            //PRIVATE CONTACTS
            if (commonType.getPrivateCall() != null && commonType.getPrivateCall().size() > 0 && commonType.getPrivateCall().get(0) != null &&
                    commonType.getPrivateCall().get(0).getPrivateCallList() != null && commonType.getPrivateCall().get(0).getPrivateCallList().getPrivateCallURI() != null) {
                List<String> privateContactsSIPUri = new ArrayList<>();
                List<String> privateContactsDisplayName = new ArrayList<>();
                for (org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.EntryType entryType : commonType.getPrivateCall().get(0).getPrivateCallList().getPrivateCallURI()) {
                    if (entryType != null) {
                        if (entryType.getUriEntry() != null) {
                            privateContactsSIPUri.add(entryType.getUriEntry());
                        }
                        if (entryType.getDisplayName() != null && entryType.getDisplayName().getValue() != null) {
                            privateContactsDisplayName.add(entryType.getDisplayName().getValue());
                        }
                    }
                }
                event.putExtra(ConstantsMCOP.ConfigurationUpdateEventExtras.PRIVATE_CONTACT_LIST,(Serializable) privateContactsSIPUri);
                event.putExtra(ConstantsMCOP.ConfigurationUpdateEventExtras.PRIVATE_CONTACT_DISPLAY_NAME_LIST, (Serializable) privateContactsDisplayName);

            }
        }
            //USER PERMISSIONS
            //They are considered granted in case of missing (defined in the constructor)
            List<ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum> allowTypeEnumList = new ArrayList<>();
            if (mcpttUserProfile.getRuleset() != null && mcpttUserProfile.getRuleset().getRule() != null
                    && mcpttUserProfile.getRuleset().getRule().size() > 0 && mcpttUserProfile.getRuleset().getRule().get(0) != null
                    && mcpttUserProfile.getRuleset().getRule().get(0).getActions() != null) {

                if (mcpttUserProfile.getRuleset().getRule().get(0).getActions().isAllowprivatecall()) {
                    allowTypeEnumList.add(ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.private_call);
                }
                if (mcpttUserProfile.getRuleset().getRule().get(0).getActions().isAllowemergencyprivatecall()) {
                    allowTypeEnumList.add(ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.emergency_private_call);
                }
                if (mcpttUserProfile.getRuleset().getRule().get(0).getActions().isAllowemergencygroupcall()) {
                    allowTypeEnumList.add(ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.emergency_group_call);
                }
                if (mcpttUserProfile.getRuleset().getRule().get(0).getActions().isAllowimminentperilcall()) {
                    allowTypeEnumList.add(ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.imminent_peril_call);
                }
                if (mcpttUserProfile.getRuleset().getRule().get(0).getActions().isAllowactivateemergencyalert()) {
                    allowTypeEnumList.add(ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.activate_emergency_alert);
                }
                event.putExtra(ConstantsMCOP.ConfigurationUpdateEventExtras.ALLOWS_LIST ,ConstantsMCOP.ConfigurationUpdateEventExtras.AllowTypeEnum.getValue(allowTypeEnumList));
            }

        success = sendEvent(event);
    }

    private boolean sendConfigurationInfoError(Constants.ConstantsErrorMCOP.ConfigurationUpdateEventError configurationUpdateError){
        if(configurationUpdateError==null || mMCOPCallback==null ){
            if(BuildConfig.DEBUG) Log.e(TAG,"mMCOPCallback or configurationUpdateError is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.configurationUpdateEvent.toString());
        Log.e(TAG, "MbmsInfoEventError Error "+ configurationUpdateError.getCode()+": "+ configurationUpdateError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.ERROR_CODE, configurationUpdateError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.EMBMSNotificationEventExtras.ERROR_STRING, configurationUpdateError.getString());
        return sendEvent(event);
    }



}