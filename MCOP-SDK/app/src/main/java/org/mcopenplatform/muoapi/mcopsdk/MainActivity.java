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

package org.mcopenplatform.muoapi.mcopsdk;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.ConstantsMCOP;
import org.mcopenplatform.muoapi.IMCOPCallback;
import org.mcopenplatform.muoapi.IMCOPsdk;
import org.mcopenplatform.muoapi.R;
import org.mcopenplatform.muoapi.mcopsdk.datatype.Session;
import org.mcopenplatform.muoapi.mcopsdk.datatype.UserData;
import org.mcopenplatform.muoapi.mcopsdk.preference.PreferencesManager;
import org.mcopenplatform.muoapi.mcopsdk.preference.PreferencesManagerDefault;
import org.mcopenplatform.muoapi.utils.Utils;

import java.net.InterfaceAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getCanonicalName();
    private ServiceConnection mConnection;
    private IMCOPsdk mService;
    private IMCOPCallback mMCOPCallback;
    private boolean isConnect = false;
    private static final int ERROR_CODE_DEFAULT = -1;
    private static final int AUTHETICATION_RESULT = 101;
    private static final int GET_PERMISSION = 102;
    private static final boolean VALUE_BOOLEAN_DEFAULT = false;
    private static UserData userData;
    private static final String PARAMETER_PROFILE = "parameters";
    private static final String PARAMETER_SAVE_PROFILE = TAG+".PARAMETER_SAVE_PROFILE";
    private String[] currentProfile;
    private PreferencesManager preferencesManager;

    private Button mainActivity_Button_Register;
    private Button mainActivity_Button_deRegister;
    private TextView mainActivity_TextView_info;
    private TextView mainActivity_TextView_error;
    private TextView mainActivity_TextView_affiliation;
    private Button mainActivity_Button_affiliation;
    private Button mainActivity_Button_unaffiliation;
    private EditText mainActivity_EditText_affiliation;
    private Button mainActivity_Button_make_call;
    private Button mainActivity_Button_Hang_up_call;
    private DialogMenu mDialogIds;
    private Button mainActivity_Button_accept_call;
    private Button mainActivity_Button_Release_token;
    private Button mainActivity_Button_Request_token;
    private DialogMenu mDialogMenu;
    private Map<String, String[]> clients;
    private boolean isSpeakerphoneOn;
    private Button mainActivity_Button_Speaker;
    private Intent serviceIntent;
    private List<InterfaceAddress> interfaceAddresses;
    private DialogMenu mDialogMenuIPs;
    private Button mainActivity_Button_Advanced_Functions;
    private DialogMenu mDialogShowAdvanceFunction;


    private Map<String,String[]> getProfilesParameters(List<String> parameters){
        Map<String,String[]> parametersMap=new HashMap<>();
        if(parameters!=null && !parameters.isEmpty()){
            Log.i(TAG,"External Parameters");
        }else{
            Log.i(TAG,"No External Parameters");
            parameters=loadParameters();
        }
        if(parameters!=null && !parameters.isEmpty())
        for (String parameter:parameters){
            Log.i(TAG,"Parameter: "+parameter);
            String[] parametersSplit=parameter.split(":");
            if(parametersSplit!=null)
            if(parametersSplit.length==2){
                parametersMap.put(parametersSplit[0],new String[]{parametersSplit[1]});
            }else if(parametersSplit.length==4){
                parametersMap.put(parametersSplit[0],new String[]{parametersSplit[1],parametersSplit[2],parametersSplit[3]});
            }else{
                Log.e(TAG,"Error in parse parameter");
            }
        }
        if(parametersMap!=null && !parametersMap.isEmpty()){
            saveParameters(parameters);
        }
        return parametersMap;
    }

    private boolean saveParameters(List<String> parameters){
        if(preferencesManager!=null){
            return preferencesManager.putStringSet(this,PARAMETER_SAVE_PROFILE,new HashSet<String>(parameters));
        }
        return false;
    }

    private ArrayList<String> loadParameters(){
        if(preferencesManager!=null){
            Set<String> stringSet=preferencesManager.getStringSet(this,PARAMETER_SAVE_PROFILE);
            if(stringSet!=null){
                return (new ArrayList<String>(stringSet));
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(BuildConfig.DEBUG)Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setPermissions();
        preferencesManager=new PreferencesManagerDefault();
        isSpeakerphoneOn=false;

        clients = new TreeMap<>();

        if(clients==null || clients.isEmpty()){
            clients.put("TESTA", new String[]{"TESTA","TESTA","TESTA"});
            clients.put("TESTB", new String[]{"TESTB","TESTB","TESTB"});
            clients.put("TESTC", new String[]{"TESTC","TESTC","TESTC"});
            clients.put("TESTD", new String[]{"TESTD","TESTD","TESTD"});
            clients.put("TESTE", new String[]{"TESTE","TESTE","TESTE"});
        }

        ArrayList<String> strings=getIntent().getStringArrayListExtra(PARAMETER_PROFILE);
        Map<String, String[]> parameterClients= getProfilesParameters(strings);
        if(parameterClients!=null && !parameterClients.isEmpty())
            clients=parameterClients;



        mainActivity_Button_Register=(Button)findViewById(R.id.mainActivity_Button_Register);

        mainActivity_Button_deRegister=(Button)findViewById(R.id.mainActivity_Button_deRegister);
        mainActivity_TextView_info=(TextView)findViewById(R.id.mainActivity_TextView_info);
        mainActivity_TextView_error=(TextView)findViewById(R.id.mainActivity_TextView_error);
        mainActivity_TextView_affiliation=(TextView)findViewById(R.id.mainActivity_TextView_affiliation);
        mainActivity_Button_affiliation=(Button)findViewById(R.id.mainActivity_Button_affiliation);
        mainActivity_Button_unaffiliation=(Button)findViewById(R.id.mainActivity_Button_unaffiliation);
        mainActivity_EditText_affiliation=(EditText)findViewById(R.id.mainActivity_EditText_affiliation);
        mainActivity_Button_make_call=(Button)findViewById(R.id.mainActivity_Button_make_call);
        mainActivity_Button_Hang_up_call=(Button)findViewById(R.id.mainActivity_Button_Hang_up_call);
        mainActivity_Button_accept_call=(Button)findViewById(R.id.mainActivity_Button_accept_call);
        mainActivity_Button_Release_token=(Button)findViewById(R.id.mainActivity_Button_Release_token);
        mainActivity_Button_Request_token=(Button)findViewById(R.id.mainActivity_Button_Request_token);
        mainActivity_Button_Speaker=(Button)findViewById(R.id.mainActivity_Button_Speaker);
        mainActivity_Button_Advanced_Functions=(Button)findViewById(R.id.mainActivity_Button_Advanced_Functions);
        if(userData==null);
        userData=new UserData();

        mMCOPCallback=new IMCOPCallback.Stub() {
            @Override
            public void handleOnEvent(final List<Intent> actionList) throws RemoteException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(BuildConfig.DEBUG)Log.i(TAG,"Receive event");
                        for(Intent action:actionList){
                            int codeError=-1;
                            int eventTypeInt=-1;
                            String stringError=null;
                            String sessionID=null;
                            if(action!=null && action.getAction()!=null && !action.getAction().trim().isEmpty())
                                try {
                                    switch (ConstantsMCOP.ActionsCallBack.fromString(action.getAction())){
                                        case none:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"none");
                                            break;
                                        case authorizationRequestEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"authorizationRequestEvent");
                                            codeError=-1;
                                            if((codeError=action.getIntExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                //Error in authorizationRequestEvent
                                                stringError=action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_STRING);
                                                showLastError("authorizationRequestEvent",codeError,stringError);
                                            }else  {
                                                //No error
                                                String requestUri=null;
                                                String redirect=null;
                                                if((requestUri=action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.REQUEST_URI))!=null &&
                                                        (redirect=action.getStringExtra(ConstantsMCOP.AuthorizationRequestExtras.REDIRECT_URI))!=null
                                                        ){
                                                    if(BuildConfig.DEBUG)Log.d(TAG,"onAuthentication URI: "+requestUri+ " redirectionURI: "+redirect);
                                                    Intent intent2 = new Intent(getApplicationContext(), ScreenAutheticationWebView.class);
                                                    intent2.putExtra(ScreenAutheticationWebView.DATA_URI_INTENT,requestUri.trim());
                                                    intent2.putExtra(ScreenAutheticationWebView.DATA_REDIRECTION_URI,redirect.trim());
                                                    //Test
                                                    //For testing purposes only
                                                    if(currentProfile!=null && currentProfile.length>=3){
                                                        intent2.putExtra(ScreenAutheticationWebView.DATA_USER,currentProfile[1]);
                                                        intent2.putExtra(ScreenAutheticationWebView.DATA_PASS,currentProfile[2]);
                                                    }

                                                    startActivityForResult(intent2,AUTHETICATION_RESULT);
                                                }
                                            }
                                            break;
                                        case loginEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"loginEvent");
                                            codeError=-1;
                                            if((codeError=action.getIntExtra(ConstantsMCOP.LoginEventExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                //LoginEvent Error
                                                stringError=action.getStringExtra(ConstantsMCOP.LoginEventExtras.ERROR_STRING);
                                                showLastError("LoginEvent",codeError,stringError);
                                            }else  {
                                                //No Error
                                                boolean success=false;
                                                String mcptt_id=null;
                                                String displayName=null;
                                                if((success=action.getBooleanExtra(ConstantsMCOP.LoginEventExtras.SUCCESS,VALUE_BOOLEAN_DEFAULT))==true &&
                                                        (mcptt_id=action.getStringExtra(ConstantsMCOP.LoginEventExtras.MCPTT_ID))!=null){
                                                    if(BuildConfig.DEBUG)Log.d(TAG,"Successful Login: "+success+ " mcptt_id: "+mcptt_id);
                                                    displayName=action.getStringExtra(ConstantsMCOP.LoginEventExtras.DISPLAY_NAME);

                                                    isRegisted(success,mcptt_id,displayName);
                                                }else{
                                                    Log.e(TAG,"Registration Error");
                                                }
                                            }
                                            break;
                                        case unLoginEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"unLoginEvent");
                                            codeError=-1;
                                            if((codeError=action.getIntExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                //unLoginEvent Error
                                                stringError=action.getStringExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING);
                                                showLastError("unLoginEvent",codeError,stringError);
                                            }else  {
                                                //No Error
                                                boolean success=false;
                                                if((success=action.getBooleanExtra(ConstantsMCOP.UnLoginEventExtras.SUCCESS,VALUE_BOOLEAN_DEFAULT))==true){
                                                    unRegisted(success);
                                                }else{
                                                    Log.e(TAG,"Unregistration Error");
                                                }
                                            }
                                            break;
                                        case configurationUpdateEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"configurationUpdateEvent");
                                            break;
                                        case callEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"callEvent");
                                            codeError=-1;
                                            eventTypeInt=action.getIntExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE,ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventTypeCall=null;

                                            if(eventTypeInt!=ERROR_CODE_DEFAULT &&
                                                    (eventTypeCall=ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.fromInt(eventTypeInt))!=null ){
                                                String callerID;
                                                String groupCallerID;
                                                int callType;
                                                switch (eventTypeCall) {
                                                    case NONE:
                                                        break;
                                                    case INCOMING:
                                                        stringError=action.getStringExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING);
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        callerID=action.getStringExtra(ConstantsMCOP.CallEventExtras.CALLER_USERID);
                                                        groupCallerID=action.getStringExtra(ConstantsMCOP.CallEventExtras.CALLER_GROUPID);
                                                        callType=action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE,ERROR_CODE_DEFAULT);
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        showData("callEvent ("+sessionID+")","INCOMING"+" -> "+callerID+" "+(groupCallerID!=null?groupCallerID:null)+" callType:"+callType);
                                                        break;
                                                    case RINGING:
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        showData("callEvent ("+sessionID+")","RINGING");
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        break;
                                                    case INPROGRESS:
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        showData("callEvent ("+sessionID+")","INPROGRESS");
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        break;
                                                    case CONNECTED:
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        callerID=action.getStringExtra(ConstantsMCOP.CallEventExtras.CALLER_USERID);
                                                        groupCallerID=action.getStringExtra(ConstantsMCOP.CallEventExtras.CALLER_GROUPID);
                                                        callType=action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE,ERROR_CODE_DEFAULT);
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        showData("callEvent ("+sessionID+")","CONNECTED"+" -> "+callerID+" "+(groupCallerID!=null?groupCallerID:null)+" callType:"+callType);
                                                        break;
                                                    case TERMINATED:
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        showData("callEvent ("+sessionID+")","TERMINATED");
                                                        if(sessionID!=null)userData.removeSessionID(sessionID);

                                                        break;
                                                    case ERROR:
                                                        if((codeError=action.getIntExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                            //callEvent Error
                                                            stringError=action.getStringExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING);
                                                            sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                            showLastError("callEvent ("+sessionID+")",codeError,stringError);
                                                        }
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        break;
                                                    case UPDATE:
                                                        sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                        int updateCallType=action.getIntExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE,ERROR_CODE_DEFAULT);
                                                        showData("callEvent ("+sessionID+")","UPDATE"+" -> CallType: "+updateCallType);
                                                        if(sessionID!=null)userData.addSessionID(sessionID);
                                                        break;
                                                    default:
                                                        showLastError("callEvent: ",999,"INVALID RECEIVED EVENT");
                                                        break;
                                                }
                                            }else{
                                                showLastError("callEvent: ",999,"INVALID RECEIVED EVENT");
                                            }
                                            break;
                                        case floorControlEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"floorControlEvent");
                                            codeError=-1;
                                            if((codeError=action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                //Error in unLoginEvent
                                                sessionID=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.SESSION_ID);
                                                stringError=action.getStringExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING);
                                                showLastError("floorControlEvent("+sessionID+")",codeError,stringError);
                                            }else  {
                                                //No Error
                                                boolean success=false;
                                                String eventFloorControl=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.FLOOR_CONTROL_EVENT);
                                                String causeString=null;
                                                int causeInt=-1;
                                                try{
                                                    sessionID=action.getStringExtra(ConstantsMCOP.CallEventExtras.SESSION_ID);
                                                    switch (ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.fromString(eventFloorControl)) {
                                                        case none:
                                                            break;
                                                        case granted:
                                                            int durationGranted=action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.DURATION_TOKEN,ERROR_CODE_DEFAULT);
                                                            Log.d(TAG,"floorControl ("+sessionID+") granted");
                                                            showData("floorControl ("+sessionID+")","granted -> Duration: "+durationGranted);
                                                            break;
                                                        case idle:
                                                            Log.d(TAG,"floorControl ("+sessionID+") idle");
                                                            showData("floorControl ("+sessionID+")","idle");
                                                            break;
                                                        case taken:
                                                            String userIDTaken=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.USER_ID);
                                                            String displayNameTaken=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.DISPLAY_NAME);
                                                            boolean allow_request=action.getBooleanExtra(ConstantsMCOP.FloorControlEventExtras.ALLOW_REQUEST,VALUE_BOOLEAN_DEFAULT);
                                                            Log.d(TAG,"floorControl ("+sessionID+") taken");
                                                            showData("floorControl ("+sessionID+")","taken -> userIDTaken(allowRequest="+allow_request+"):("+userIDTaken+":"+displayNameTaken+")");
                                                            break;
                                                        case denied:
                                                            causeString=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_STRING);
                                                            causeInt=action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_CODE,ERROR_CODE_DEFAULT);
                                                            Log.d(TAG,"floorControl ("+sessionID+") denied");
                                                            showData("floorControl ("+sessionID+")","denied -> cause("+causeInt+":"+causeString+")");
                                                            break;
                                                        case revoked:
                                                            causeString=action.getStringExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_STRING);
                                                            causeInt=action.getIntExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_CODE,ERROR_CODE_DEFAULT);
                                                            Log.d(TAG,"floorControl ("+sessionID+") revoked");
                                                            showData("floorControl ("+sessionID+")","revoked ->cause("+causeInt+":"+causeString+")");
                                                            break;
                                                        case request_sent:
                                                            break;
                                                        case release_sent:
                                                            break;
                                                        case queued:
                                                            break;
                                                        case queued_timeout:
                                                            break;
                                                        //TODO: for REL14
                                                        case transmission_granted:
                                                            break;
                                                        case reception_granted:
                                                            break;
                                                        case transmission_rejection:
                                                            break;
                                                        case reception_rejection:
                                                            break;
                                                        case transmission_revoke:
                                                            break;
                                                        case reception_revoke:
                                                            break;
                                                        case transmission_notification:
                                                            break;
                                                        case transmission_end_notification:
                                                            break;
                                                        case transmission_end_response:
                                                            break;
                                                        case reception_end_response:
                                                            break;
                                                    }
                                                }catch (Exception e){

                                                }
                                            }
                                            break;
                                        case groupInfoEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"groupInfoEvent");
                                            String groupID2=action.getStringExtra(ConstantsMCOP.GroupInfoEventExtras.GROUP_ID);
                                            String displayName=action.getStringExtra(ConstantsMCOP.GroupInfoEventExtras.DISPLAY_NAME);
                                            int maxDataSizeForSDS=action.getIntExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_FOR_SDS,ERROR_CODE_DEFAULT);
                                            int maxDataSizeAutoRecv=action.getIntExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_AUTO_RECV,ERROR_CODE_DEFAULT);
                                            String activeRealTimeVideoMode=action.getStringExtra(ConstantsMCOP.GroupInfoEventExtras.ACTIVE_REAL_TIME_VIDEO_MODE);
                                            ArrayList<String> participantsList=null;
                                            ArrayList<String> participantsListDisplayName=null;
                                            ArrayList<String> participantsListType=null;
                                            try{
                                                participantsList= action.getStringArrayListExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST);
                                                Log.d(TAG,"ParticipantsList: "+participantsList.size());
                                            }catch (Exception e){
                                                Log.e(TAG,"Error in participants info");
                                            }
                                            try{
                                                participantsListDisplayName= action.getStringArrayListExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST_DISPLAY_NAME);
                                                Log.d(TAG,"ParticipantsList display name: "+participantsListDisplayName.size());
                                            }catch (Exception e){
                                                Log.e(TAG,"Error in participants info");
                                            }
                                            try{
                                                participantsListType= action.getStringArrayListExtra(ConstantsMCOP.GroupInfoEventExtras.PARTICIPANTS_LIST_TYPE);
                                                Log.d(TAG,"ParticipantsList type: "+participantsListType.size());
                                            }catch (Exception e){
                                                Log.e(TAG,"Error in participants info");
                                            }
                                            Log.d(TAG,"INFO Group ("+groupID2+" "+displayName+")");
                                            Log.d(TAG,"maxDataSizeForSDS:"+maxDataSizeForSDS+"");
                                            Log.d(TAG,"maxDataSizeAutoRecv:"+maxDataSizeAutoRecv+"");
                                            Log.d(TAG,"activeRealTimeVideoMode:"+activeRealTimeVideoMode+"");
                                            Log.d(TAG,"Users:");
                                            if(participantsList.size()==participantsListDisplayName.size() && participantsList.size()==participantsListType.size())
                                            for(int con=0;con<participantsList.size();con++){
                                                Log.d(TAG,"Participant: "+participantsList.get(con));
                                                Log.d(TAG,"DisplayName: "+participantsListDisplayName.get(con));
                                                Log.d(TAG,"Type: "+participantsListType.get(con));
                                            }

                                            break;
                                        case groupAffiliationEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"groupAffiliationEvent");
                                            codeError=-1;
                                            eventTypeInt=action.getIntExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE,ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum eventTypeAffiliation=null;
                                            if(eventTypeInt!=ERROR_CODE_DEFAULT &&
                                                    (eventTypeAffiliation=ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.fromInt(eventTypeInt))!=null ){
                                                if(BuildConfig.DEBUG)Log.d(TAG,"receive event ");
                                                switch (eventTypeAffiliation) {
                                                    case GROUP_AFFILIATION_UPDATE:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"GROUP_AFFILIATION_UPDATE");
                                                        Map<String, Integer> groups=(HashMap<String, Integer>)action.getSerializableExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUPS_LIST);
                                                        if(groups!=null)
                                                            showGroups(groups);
                                                        break;
                                                    case GROUP_AFFILIATION_ERROR:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"GROUP_AFFILIATION_ERROR");
                                                        if((codeError=action.getIntExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_CODE,ERROR_CODE_DEFAULT))!=ERROR_CODE_DEFAULT){
                                                            //Error in unLoginEvent
                                                            stringError=action.getStringExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_STRING);
                                                            String groupID=action.getStringExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUP_ID);
                                                            showLastError("groupAffiliationEvent ("+groupID+")",codeError,stringError);
                                                        }
                                                        break;
                                                    case REMOTE_AFFILIATION:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"REMOTE_AFFILIATION");
                                                        //TODO: Receive Remote Affiliation
                                                        break;
                                                    default:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"groupAffiliationEvent type default");
                                                        showLastError("groupAffiliationEvent: ",999,"INVALID RECEIVED EVENT");
                                                        break;
                                                }
                                            }else{
                                                showLastError("groupAffiliationEvent: ",999,"INVALID RECEIVED EVENT");
                                            }

                                            break;
                                        case selectedContactChangeEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"selectedContactChangeEvent");
                                            break;
                                        case eMBMSNotificationEvent:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent");
                                            codeError=-1;
                                            eventTypeInt=action.getIntExtra(ConstantsMCOP.EMBMSNotificationEventExtras.EVENT_TYPE,ERROR_CODE_DEFAULT);
                                            ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum eventType=null;
                                            if(eventTypeInt!=ERROR_CODE_DEFAULT &&
                                                    (eventType=ConstantsMCOP.EMBMSNotificationEventExtras.EMBMSNotificationEventEventTypeEnum.fromInt(eventTypeInt))!=null ){
                                                if(BuildConfig.DEBUG)Log.d(TAG,"receive event ");
                                                switch (eventType) {
                                                    case none:
                                                        break;
                                                    case eMBMSAvailable:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent eMBMSAvailable");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent eMBMSAvailable");
                                                        break;
                                                    case UndereMBMSCoverage:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent UndereMBMSCoverage");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent UndereMBMSCoverage");
                                                        break;
                                                    case eMBMSBearerInUse:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent eMBMSBearerInUse");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent eMBMSBearerInUse");
                                                        break;
                                                    case eMBMSBearerNotInUse:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent eMBMSBearerNotInUse");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent eMBMSBearerNotInUse");
                                                        break;
                                                    case NoeMBMSCoverage:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent NoeMBMSCoverage");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent NoeMBMSCoverage");
                                                        break;
                                                    case eMBMSNotAvailable:
                                                        if(BuildConfig.DEBUG)Log.d(TAG,"eMBMSNotificationEvent eMBMSNotAvailable");
                                                        mainActivity_TextView_error.setText("eMBMSNotificationEvent eMBMSNotAvailable");
                                                        break;
                                                }
                                            }else{
                                                showLastError("eMBMSNotificationEvent: ",999,"INVALID RECEIVED EVENT");
                                            }
                                            break;
                                        default:
                                            if(BuildConfig.DEBUG)Log.d(TAG,"Event type is not valid. ");
                                            break;
                                    }
                                }catch (Exception ex){
                                    Log.e(TAG,"Event Action Error: "+action.getAction()+" error:"+ex.getMessage());
                                }
                        }
                    }
                });
            }


        };

        mainActivity_Button_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTypeRegister(getApplicationContext());
            }
        });
        mainActivity_Button_deRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(mService!=null)
                        mService.unLoginMCOP();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mainActivity_Button_affiliation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(mService!=null)
                        mService.groupAffiliationOperation(
                                mainActivity_EditText_affiliation.getText().toString().trim(),
                                ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum.Affiliate.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mainActivity_Button_unaffiliation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(mService!=null)
                        mService.groupAffiliationOperation(
                                mainActivity_EditText_affiliation.getText().toString().trim(),
                                ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum.Deaffiliate.getValue());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        mainActivity_Button_make_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMakeCallTypes(getApplicationContext());

            }
        });

        mainActivity_Button_Hang_up_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIds(getApplicationContext());
            }
        });
        mainActivity_Button_accept_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIdsAcceptCall(getApplicationContext());
            }
        });

        mainActivity_Button_Release_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIdsOperationFloorControl(getApplicationContext(),false);
            }
        });

        mainActivity_Button_Request_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showIdsOperationFloorControl(getApplicationContext(),true);
            }
        });

        mainActivity_Button_Speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AudioManager mAudioManager;
                mAudioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if(isSpeakerphoneOn){
                    isSpeakerphoneOn=false;
                    Log.d(TAG, "Speaker false");
                    mainActivity_Button_Speaker.setText("Speaker false");
                }else{
                    isSpeakerphoneOn=true;
                    Log.d(TAG, "Speaker true");
                    mainActivity_Button_Speaker.setText("Speaker true");
                }
                mAudioManager.setSpeakerphoneOn(isSpeakerphoneOn);

            }
        });

        mainActivity_Button_Advanced_Functions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdvanceFeatures();
        }
        });

        if(mConnection==null)
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                Log.e(TAG,"Service Binded!\n");
                mService = IMCOPsdk.Stub.asInterface(service);
                try {
                    mService.registerCallback(mMCOPCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                isConnect=true;
            }

            @Override
            public void onServiceDisconnected(ComponentName className) {
                mService = null;
                // This method is only invoked when the service quits from the other end or gets killed
                // Invoking exit() from the AIDL interface makes the Service kill itself, thus invoking this.
                Log.e(TAG,"Service Disconnected.\n");
                isConnect=false;
            }
        };
        /*
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if(tm!=null) {
                String imei = tm.getDeviceId();
                Log.d(TAG,"imei current:"+imei);
                if(clients!=null && imei!=null){
                    String[] client=clients.get(imei);
                    if(client!=null){
                        this.currentProfile=client;
                        Log.i(TAG,"currentProfile: " +currentProfile);
                        connectService(currentProfile[0]);
                    }else{
                        showOptionsProfiles(clients, this);
                    }
                }else{
                    showOptionsProfiles(clients, this);
                }

            }
        }else{
            showOptionsProfiles(clients, this);
        }
        //showOptionsProfiles(clients, this);
        */

        connectService(null);

    }
    private void showAdvanceFeatures(){

        final String[] strings={
        };
        mDialogShowAdvanceFunction = DialogMenu.newInstance(strings,null);
        mDialogShowAdvanceFunction.setOnClickItemListener(new DialogMenu.OnClickListener() {
            @Override
            public void onClickItem(int item) {
                if(item>=0 && strings.length>item){

                    if(false){

                    }else
                    {

                    }
                }
            }
        });
        mDialogShowAdvanceFunction.show(getSupportFragmentManager(), "SimpleDialog");
    }


    private void connectService(String client){
        if(!isConnect){
            serviceIntent = new Intent()
                    .setComponent(new ComponentName(
                            "org.mcopenplatform.muoapi",
                            "org.mcopenplatform.muoapi.MCOPsdk"));

            if(client==null){
                Log.i(TAG,"Current Profile: "+client);
                serviceIntent.putExtra("PROFILE_SELECT",currentProfile!=null?currentProfile[0]:client);
            }


            try{
                ComponentName componentName=this.startService(serviceIntent);
                if(componentName==null){
                    Log.e(TAG,"Starting Error: "+componentName.getPackageName());
                }else if(serviceIntent==null){
                    Log.e(TAG,"serviceIntent Error: "+componentName.getPackageName());
                }else if(mConnection==null){
                    Log.e(TAG,"mConnection Error: "+componentName.getPackageName());
                }else{

                }
            }catch (Exception e){
                if(BuildConfig.DEBUG)Log.w(TAG,"Error in start service: "+e.getMessage());
            }

            Log.i(TAG,"Bind Service: "+bindService(serviceIntent, mConnection, BIND_AUTO_CREATE));
        }
    }
/*
    private void showOptionsProfiles(Map<String,String[]> stringsList,final Context context){
        if(stringsList==null)return;
        final ArrayList<String> strings=new ArrayList<>();
        for(String[] value:stringsList.values()){
            if(value!=null && value.length>=1 && value[0]!=null){
                strings.add(value[0]);
            }else{
                Log.e(TAG,"Error in process value");
            }
        }
        if(strings==null || strings.isEmpty())return;
        mDialogMenu = DialogMenu.newInstance( strings.toArray(new String[strings.size()]),null);
        mDialogMenu.setOnClickItemListener(new DialogMenu.OnClickListener() {
            @Override
            public void onClickItem(int item) {
                if(item>=0 && strings.size()>item){
                    Log.d(TAG,"Select Profile "+strings.get(item));
                    //TODO:
                    connectService(strings.get(item));
                }
            }
        });
        mDialogMenu.show(getSupportFragmentManager(), "SimpleDialog");
    }
*/
    private void showTypeRegister(final Context context){
        final String[] strings={"With External authentication","No authentication"};
        if(strings==null || strings.length==0)return;
        mDialogMenu=null;
        mDialogMenu = DialogMenu.newInstance(strings,"Registration types");
        mDialogMenu.setOnClickItemListener(new DialogMenu.OnClickListener() {
            @Override
            public void onClickItem(int item) {
                if(item>=0 && strings.length>item){
                    Log.d(TAG,"Select type call "+strings[item]);
                    try {
                        int typeCalls=-1;
                        switch (item){
                            case 0:
                                    if(mService!=null)
                                        mService.loginMCOP();
                                break;
                            case 1:
                                //Test
                                    if(mService!=null)
                                        mService.authorizeUser(null);
                                break;

                            default:
                                break;
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mDialogMenu.show(getSupportFragmentManager(), "SimpleDialog");
        if(mDialogMenu==null){
            if(mService!=null) {
                try {
                    mService.authorizeUser(null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showMakeCallTypes(final Context context){
        final String[] strings={"Private call"
                ,"Private call(whitout floor control)"
                ,"Group call"
                ,"Emergency Group call"
                ,"Emergency Private call"
                ,"Chat Group call"

        };
        if(strings==null || strings.length==0)return;
        mDialogMenu = DialogMenu.newInstance(strings,"Call types");
        mDialogMenu.setOnClickItemListener(new DialogMenu.OnClickListener() {
            @Override
            public void onClickItem(int item) {
                if(item>=0 && strings.length>item){
                    Log.d(TAG,"Select type call "+strings[item]);
                    try {
                        int typeCalls=-1;
                            String typeCall=null;
                            if((typeCall=strings[item])!=null){
                                if(typeCall.compareTo("Private call")==0){
                                    typeCalls=ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue()|
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.Private.getValue();
                                }
                                else if(typeCall.compareTo("Private call(whitout floor control)")==0){
                                    typeCalls=ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithoutFloorCtrl.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.Private.getValue();
                                }
                                else if(typeCall.compareTo("Group call")==0) {
                                    typeCalls = ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.PrearrangedGroup.getValue();
                                }
                                else if(typeCall.compareTo("Emergency Group call")==0) {
                                    typeCalls = ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.PrearrangedGroup.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue();
                                }
                                else if(typeCall.compareTo("Emergency Private call")==0) {
                                    typeCalls = ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.Private.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.Emergency.getValue();
                                }
                                else if(typeCall.compareTo("Chat Group call")==0) {
                                    typeCalls = ConstantsMCOP.CallEventExtras.CallTypeEnum.Audio.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.WithFloorCtrl.getValue() |
                                            ConstantsMCOP.CallEventExtras.CallTypeEnum.ChatGroup.getValue();
                                }

                            }



                        if(typeCalls>0 && mService!=null)
                            mService.makeCall(
                                    mainActivity_EditText_affiliation.getText().toString().trim(),
                                    typeCalls
                            );
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mDialogMenu.show(getSupportFragmentManager(), "SimpleDialog");
    }

    private void showIds(final Context context){
        if(userData.getSessionIDs()==null)return;
        final String[] strings=userData.getSessionIDs().toArray(new String[userData.getSessionIDs().size()]);

        if(strings==null || strings.length==0)return;
        if(strings.length==1) {
            try {
                if(mService!=null)
                mService.hangUpCall(strings[0]);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            mDialogIds = DialogMenu.newInstance(strings,null);
            mDialogIds.setOnClickItemListener(new DialogMenu.OnClickListener() {
                @Override
                public void onClickItem(int item) {
                    if(item>=0 && strings.length>item){
                        try {
                            if(mService!=null)
                                mService.hangUpCall(strings[item]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mDialogIds.show(getSupportFragmentManager(), "SimpleDialog");
        }
    }

    private void showIdsOperationFloorControl(final Context context, final boolean request){
        if(userData.getSessionIDs()==null)return;
        final String[] strings=userData.getSessionIDs().toArray(new String[userData.getSessionIDs().size()]);
        if(strings==null || strings.length==0)return;
        if(strings.length==1) {
            try {
                if(mService!=null)
                    mService.floorControlOperation(
                            strings[0],
                            request?ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Request.getValue():ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Release.getValue(),
                            null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            mDialogIds = DialogMenu.newInstance(strings,null);
            mDialogIds.setOnClickItemListener(new DialogMenu.OnClickListener() {
                @Override
                public void onClickItem(int item) {
                    if(item>=0 && strings.length>item){
                        try {
                            if(mService!=null)
                                mService.floorControlOperation(
                                        strings[item],
                                        request?ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Request.getValue():ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.MCPTT_Release.getValue(),
                                        null);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            mDialogIds.show(getSupportFragmentManager(), "SimpleDialog");
        }
    }

    private void showIdsAcceptCall(final Context context){
        if(userData.getSessionIDs()==null)return;
        final String[] strings=userData.getSessionIDs().toArray(new String[userData.getSessionIDs().size()]);
        if(strings==null || strings.length==0)return;
        if(strings.length==1) {
            try {
                if(mService!=null)
                    mService.acceptCall(strings[0]);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else{
            mDialogIds = DialogMenu.newInstance(strings,null);
            mDialogIds.setOnClickItemListener(new DialogMenu.OnClickListener() {
                @Override
                public void onClickItem(int item) {
                    if(item>=0 && strings.length>item){
                        try {
                            if(mService!=null)
                                mService.acceptCall(strings[item]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mDialogIds.show(getSupportFragmentManager(), "SimpleDialog");
        }
    }

    @Override
    protected void onDestroy(){
        if(BuildConfig.DEBUG)Log.i(TAG,"onDestroy");
        super.onDestroy();

        if(mConnection!=null && isConnect){
            try{
                if(BuildConfig.DEBUG)Log.i(TAG,"unbindService");
                unbindService(mConnection);
                isConnect=false;
            }catch (Exception e){
                Log.e(TAG,"Error in unbind Service");
            }
        }else{
            Log.e(TAG,"Error 2 in unbind Service");
        }

        if(serviceIntent!=null){
            try{
                stopService(serviceIntent);
            }catch (Exception e){
                Log.e(TAG,"Error in stop Service");
            }
        }else{
            Log.e(TAG,"Error 2 in stop Service");
        }

        mConnection=null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(BuildConfig.DEBUG)Log.i(TAG,"onActivityResult");
        switch (requestCode){
            case AUTHETICATION_RESULT:
                if ( resultCode == ScreenAutheticationWebView.RETURN_ON_AUTHENTICATION_LISTENER_FAILURE) {
                    String dataError;
                    if (data != null &&
                            (dataError= data.getStringExtra(org.mcopenplatform.muoapi.mcopsdk.ScreenAutheticationWebView.RETURN_ON_AUTHENTICATION_ERROR))!=null &&
                            dataError instanceof String) {
                        Log.e(TAG,"Authentication Error: "+dataError);
                    }else{
                        Log.e(TAG,"Error Processing Authentication.");
                    }
                }else if ( resultCode == ScreenAutheticationWebView.RETURN_ON_AUTHENTICATION_LISTENER_OK) {
                    String dataUri;
                    if (data != null &&
                            (dataUri= data.getStringExtra(org.mcopenplatform.muoapi.mcopsdk.ScreenAutheticationWebView.RETURN_ON_AUTHENTICATION_RESPONSE))!=null &&
                            dataUri instanceof String) {
                        URI uri = null;
                        try {
                            uri = new URI(dataUri);
                            Log.i(TAG, "Uri: " + uri.toString());
                            try {
                                if(mService!=null)
                                mService.authorizeUser(dataUri);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } catch (URISyntaxException e) {
                            Log.e(TAG,"Authentication Error: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }else{
                        Log.e(TAG,"Error processing file to import Profiles.");
                    }
                }
                break;
        }
    }

    //START GUI
    private void unRegisted(boolean success){
        userData.setRegisted(false);
        userData.setDisplayName(null);
        userData.setMcpttID(null);
        mainActivity_TextView_info.setText("UNREGISTERED");
    }

    private void isRegisted( boolean success, String  mcpttID, String displayName){
        userData.setRegisted(success);
        if(mcpttID!=null)
            userData.setMcpttID(mcpttID);
        if(displayName!=null){
            userData.setDisplayName(displayName);
        }
        Log.d(TAG,"REGISTERED. MCPTT ID: "+mcpttID+" DISPLAY NAME: "+displayName);
        mainActivity_TextView_info.setText("REGISTERED. MCPTT ID: "+mcpttID+" DISPLAY NAME: "+displayName);
    }

    private void showData(String eventType,String data){
        Log.d(TAG,eventType+": "+data);
        mainActivity_TextView_info.setText(eventType+": "+data);
    }

    private void showLastError(String from,int code,String errorString){
        Log.e(TAG,"ERROR "+from+": "+code+" "+errorString);
        mainActivity_TextView_error.setText("ERROR "+from+": "+code+" "+errorString);
    }

    private void showGroups(Map<String, Integer> groups){
        if(BuildConfig.DEBUG && groups!=null)Log.d(TAG,"showGroups size: "+groups.size());
        String result="";
        if(groups!=null)
            for (String groupID:groups.keySet()){
                String type="";
                switch (ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.fromInt(groups.get(groupID))){
                    case notaffiliated:
                        type="notaffiliated";
                        break;
                    case affiliating:
                        type="affiliating";
                        break;
                    case affiliated:
                        type="affiliated";
                        break;
                    case deaffiliating:
                        type="deaffiliating";
                        break;
                }
                result=result+"groupID:"+groupID+":"+type+"\n";
            }
        Calendar calendar = Calendar.getInstance();

        mainActivity_TextView_affiliation.setText("Lists Group Affiliations:(Time:"+String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", calendar)+")\n"+result);
    }




    /**
     * Set permissions for Android 6.0 or above
     */
    protected void setPermissions(){
        //Set Permissions
        //READ_PHONE_STATE
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                //ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
                ) {
            //Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                //Show an explanation to the user *asynchronously* -- don't block
                //this thread waiting for the user's response! After the user
                //sees the explanation, request the permission again.
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_PHONE_STATE,
                               // Manifest.permission.FOREGROUND_SERVICE
                        },
                        GET_PERMISSION);
            } else {

                //No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE,Manifest.permission.FOREGROUND_SERVICE},
                        GET_PERMISSION);

                //MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                //app-defined int constant. The callback method gets the
                //result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(BuildConfig.DEBUG)Log.i(TAG,"onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch (requestCode) {
            case GET_PERMISSION: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // API>22
                    setPermissionsWriteSetting();
                } else {
                    setPermissions();
                    // Permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            default:
                break;
            //other 'case' lines to check for other
            //permissions this app might request
        }
    }

    /**
     * API>22
     */
    @TargetApi(Build.VERSION_CODES.M)
    protected void setPermissionsWriteSetting(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Settings.System.canWrite(this) ){
                //Do stuff here
            }
            else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }
    //END GUI
}
