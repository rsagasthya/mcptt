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

package org.mcopenplatform.muoapi.session;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.sip.NgnAVSession;
import org.mcopenplatform.muoapi.ConstantsMCOP;
import org.mcopenplatform.muoapi.datatype.error.Constants;
import org.mcopenplatform.muoapi.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlChatGroup;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrearrangedGroup;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrearrangedGroupEmergency;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrearrangedGroupImminentPeril;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrivate;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithFloorCtrlPrivateEmergency;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.AudioWithoutFloorCtrlPrivate;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.NONE;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.VideoAudioWithFloorCtrlPrearrangedGroup;
import static org.mcopenplatform.muoapi.datatype.error.Constants.CallEvent.CallTypeValidEnum.VideoAudioWithFloorCtrlPrivate;

public class Session implements NgnAVSession.OnEventMCPTTListener {
    private final static String TAG = Utils.getTAG(Session.class.getCanonicalName());
    private boolean isPrepare=false;

    private NgnAVSession session;
    private OnSessionListener onSessionListener;
    private Context context;
    public Session(NgnAVSession session,Context context) {
        this.isPrepare=false;
        this.context=context;
        this.session = session;
        if(session!=null){
            session.setOnEventMCPTTListener(this);
        }
    }

    protected boolean hangUpCall(){
        //TODO
        return session.hangUpCall();
    }

    protected boolean acceptCall(){
        //TODO
        return session.acceptCallMCPTT(context);
    }

    public boolean floorControlOperation(
            org.mcopenplatform.muoapi.ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum operationType,
            String userID){
        switch (operationType) {
            case none:
                break;
                //TODO: Token Request control logic missing
            case MCPTT_Request:
                session.requestMCPTTToken();
                break;
            case MCPTT_Release:
                session.releaseMCPTTToken();
                break;

                //TODO: Define the rest of floor control actions for MCVideo
            case TRANSMISION_Request:
                break;
            case TRANSMISION_End_Request:
                break;
            case RECEPTION_Request:
                break;
            case RECEPTION_End_Request:
                break;
        }
        return false;
    }

    private Constants.CallEvent.CallTypeValidEnum eventSIPToMCOPType(NgnMediaType mediaType){
        Constants.CallEvent.CallTypeValidEnum result=NONE;
        if(mediaType!=null){
            switch (mediaType) {
                case SessionMCPTT:
                    break;
                case SessionGroup:
                    break;
                case SessionAudioMCPTT:
                    result=AudioWithoutFloorCtrlPrivate;
                    break;
                case SessionAudioMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlPrivate;
                    break;
                case SessionAudioGroupMCPTT:
                    break;
                case SessionAudioGroupMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlPrearrangedGroup;
                    break;

                case SessionEmergencyAudioMCPTT:
                    break;
                case SessionEmergencyAudioMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlPrivateEmergency;
                    break;
                case SessionEmergencyAudioGroupMCPTT:
                    break;
                case SessionEmergencyAudioGroupMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlPrearrangedGroupEmergency;
                    break;
                case SessionAlertAudioMCPTT:
                    break;
                case SessionAlertAudioMCPTTWithFloorControl:
                    break;
                case SessionAlertAudioGroupMCPTT:
                    break;
                case SessionAlertAudioGroupMCPTTWithFloorControl:
                    break;
                case SessionImminentperilAudioMCPTT:
                    break;
                case SessionImminentperilAudioMCPTTWithFloorControl:
                    break;
                case SessionImminentperilAudioGroupMCPTT:
                    break;
                case SessionImminentperilAudioGroupMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlPrearrangedGroupImminentPeril;
                    break;
                case SessionAudioChatMCPTT:
                    break;
                case SessionAudioChatGroupMCPTT:
                    break;
                case SessionAudioChatGroupMCPTTWithFloorControl:
                    result=AudioWithFloorCtrlChatGroup;
                    break;


                case All:
                    break;
                default:
                    if(BuildConfig.DEBUG)Log.e(TAG,"Event Type isn´t logic :"+mediaType.name()+" "+mediaType.getValue());
                    break;
            }
        }
        return result;
    }

    private void prepareSession(){
        if(!isPrepare){
            if(BuildConfig.DEBUG)Log.d(TAG,"prepareSession()");
        }
        isPrepare=true;
    }

    //START CALL EVENT
    protected void newInviteEvent(NgnInviteEventArgs args){
        String userID=null;
        String groupID=null;
        Constants.CallEvent.CallTypeValidEnum typeCall=null;
        switch (args.getEventType()) {
            case INCOMING:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session INCOMING: "+args.getCode());
                userID=session.getRemotePartyUri();
                groupID=session.getPTTMcpttGroupIdentity();
                typeCall =eventSIPToMCOPType(session.getMediaType());
                sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.INCOMING,typeCall,userID,groupID);
                prepareSession();
                break;
            case INPROGRESS:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session INPROGRESS: "+args.getCode());
                sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.INPROGRESS);
                //prepareSession();
                break;
            case RINGING:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session RINGING: "+args.getCode());
                sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.RINGING);
                break;
            case EARLY_MEDIA:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session EARLY_MEDIA: "+args.getCode());
                break;
            case CONNECTED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session CONNECTED: "+args.getCode());
                userID=session.getRemotePartyUri();
                groupID=session.getPTTMcpttGroupIdentity();
                typeCall =eventSIPToMCOPType(session.getMediaType());
                sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.CONNECTED,typeCall,userID,groupID);
                prepareSession();
                break;
            case TERMWAIT:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session TERMWAIT: "+args.getCode());
                break;
            case TERMINATED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session TERMINATED: "+args.getCode());
                sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.TERMINATED);
                break;
            case LOCAL_HOLD_OK:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_HOLD_OK: "+args.getCode());
                break;
            case LOCAL_HOLD_NOK:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_HOLD_NOK: "+args.getCode());
                break;
            case LOCAL_RESUME_OK:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_RESUME_OK: "+args.getCode());
                break;
            case LOCAL_RESUME_NOK:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_RESUME_NOK: "+args.getCode());
                break;
            case REMOTE_HOLD:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_HOLD: "+args.getCode());
                break;
            case REMOTE_RESUME:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_RESUME: "+args.getCode());
                break;
            case MEDIA_UPDATING:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session MEDIA_UPDATING: "+args.getCode());
                break;
            case MEDIA_UPDATED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session MEDIA_UPDATED: "+args.getCode());
                break;
            case SIP_RESPONSE:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session SIP_RESPONSE: "+args.getCode());
                break;
            case ERROR_INVITE:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session ERROR_INVITE: "+args.getCode());
                switch (args.getCode()){
                    case 480:
                        sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVIII);
                    case 486:
                        sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVX);
                        break;
                    default:
                        sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDIX);
                    break;

                }
                //TODO: Must control when a TERMINATED is received
            case REMOTE_DEVICE_INFO_CHANGED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_DEVICE_INFO_CHANGED: "+args.getCode());
                break;
            case LOCAL_TRANSFER_TRYING:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_TRANSFER_TRYING: "+args.getCode());
                break;
            case LOCAL_TRANSFER_ACCEPTED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_TRANSFER_ACCEPTED: "+args.getCode());
                break;
            case LOCAL_TRANSFER_COMPLETED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_TRANSFER_COMPLETED: "+args.getCode());
                break;
            case LOCAL_TRANSFER_FAILED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_TRANSFER_FAILED: "+args.getCode());
                break;
            case LOCAL_TRANSFER_NOTIFY:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session LOCAL_TRANSFER_NOTIFY: "+args.getCode());
                break;
            case REMOTE_TRANSFER_REQUESTED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_TRANSFER_REQUESTED: "+args.getCode());
                break;
            case REMOTE_TRANSFER_NOTIFY:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_TRANSFER_NOTIFY: "+args.getCode());
                break;
            case REMOTE_TRANSFER_INPROGESS:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_TRANSFER_INPROGESS: "+args.getCode());
                break;
            case REMOTE_TRANSFER_FAILED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_TRANSFER_FAILED: "+args.getCode());
                break;
            case REMOTE_TRANSFER_COMPLETED:
                if(BuildConfig.DEBUG) Log.d(TAG,"Session REMOTE_TRANSFER_COMPLETED: "+args.getCode());
                break;
        }
    }

    @Override
    public void onEventMCPTT(NgnAVSession.PTTState mPTTState) {
        String stringPhrase=null;
        short shortCode=-1;
        switch (mPTTState) {
            case CALLING:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: CALLING");
                break;
            case TALKING:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: TALKING");
                String accountTalking=session.getTakingUserMCPTT();
                //TODO: ALLOW_REQUEST needed. Now it´s "true"
                sendFloorControlEvent(ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.taken,accountTalking,accountTalking,true);
                break;
            case RELEASING:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: RELEASING");
                break;
            case DENIED:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: DENIED");
                stringPhrase=session.getPhraseDenied();
                shortCode=session.getCodeDenied();
                sendFloorControlEvent(ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.denied,shortCode,stringPhrase);
                break;
            case IDLE:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: IDLE");
                sendFloorControlEvent(ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.idle);
                break;
            case WAITING:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: WAITING");
                break;
            case REQUESTING:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: REQUESTING");
                break;
            case REVOKED:
                stringPhrase=session.getPhraseRevoke();
                shortCode=session.getCodeRevoke();
                sendFloorControlEvent(ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.granted,shortCode,stringPhrase);
                break;
            case GRANTED:
                if(BuildConfig.DEBUG) Log.d(TAG,"onEventMCPTT: GRANTED");
                sendFloorControlEvent(ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum.granted,(int)session.getGrantedTimeSecMCPTT());
                break;
        }
    }

    //START FLOOR CONTROL EVENT
    private void sendFloorControlEvent(@NonNull ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum eventType){
        sendFloorControlEvent(eventType,null,null,null,null,null,null);
    }

    private void sendFloorControlEvent(@NonNull ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum eventType,
                                       Short numCause,
                                       String stringCause){
        sendFloorControlEvent(eventType,null,null,null,null,numCause,stringCause);
    }

    private void sendFloorControlEvent(@NonNull ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum eventType,
                                       String displayName,
                                       String userID,
                                       Boolean allowRequest){
        //TODO: Missing displayName of each participant from the GMS
        sendFloorControlEvent(eventType,null,displayName,userID,allowRequest,null,null);
    }

    private void sendFloorControlEvent(@NonNull ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum eventType,
                                       Integer durationToken){
        sendFloorControlEvent(eventType,durationToken,null,null,null,null,null);
    }

    private void sendFloorControlEvent(@NonNull ConstantsMCOP.FloorControlEventExtras.FloorControlEventTypeEnum eventType,
                                       Integer durationToken,
                                       String displayName,
                                       String userID,
                                       Boolean allowRequest,
                                       Short numCause,
                                       String stringCause){
        if(eventType==null)return;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.floorControlEvent.toString());
        //eventType
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.FLOOR_CONTROL_EVENT, eventType.toString());
        //DURATION_TOKEN
        if(durationToken!=null)
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.DURATION_TOKEN, durationToken);
        //TODO: For the time being, "display name" is equal to "userID"
        //Display Name
        if(displayName!=null)
            event.putExtra(ConstantsMCOP.FloorControlEventExtras.DISPLAY_NAME, displayName);
        //UserID
        if(userID!=null)
            event.putExtra(ConstantsMCOP.FloorControlEventExtras.USER_ID, userID);
        //Allow Request
        if(allowRequest!=null)
            event.putExtra(ConstantsMCOP.FloorControlEventExtras.ALLOW_REQUEST, allowRequest);
        //Code
        if(numCause!=null && numCause>0)
            event.putExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_CODE, (int)numCause);
        //Cause
        if(stringCause!=null)
            event.putExtra(ConstantsMCOP.FloorControlEventExtras.CAUSE_STRING, stringCause);
        try{
            String sessionID=String.valueOf(session.getId());
            event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        }catch (Exception ex){

        }
        sendEvent(event);
    }

    private void sendErrorFloorControlEvent(Constants.ConstantsErrorMCOP.FloorControlEventError  floorControlEventError,String sessionID){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.floorControlEvent.toString());
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.e(TAG, "Floor Control Error "+ floorControlEventError.getCode()+": "+ floorControlEventError.getString());
        //Error Code
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_CODE,floorControlEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_STRING,floorControlEventError.getString());
        //SessionID
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.SESSION_ID,sessionID);
        sendEvent(event);
    }
    //END FLOOR CONTROL EVENT

    //END CALL EVENT
    private void sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventType){
        sendCallEvent(eventType,(List<ConstantsMCOP.CallEventExtras.CallTypeEnum>)null,null,null);
    }

    private void sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventType,List<ConstantsMCOP.CallEventExtras.CallTypeEnum> callTypeEnums){
        sendCallEvent(eventType,callTypeEnums,null,null);
    }


    private void sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventType,Constants.CallEvent.CallTypeValidEnum callTypeEnum,String userID,String groupID){

        sendCallEvent(eventType,
                (callTypeEnum!=null && callTypeEnum!=NONE)?ConstantsMCOP.CallEventExtras.CallTypeEnum.getListCallType(callTypeEnum.getValue()):null,
                userID,groupID);
    }

    private void sendCallEvent(ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum eventType, List<ConstantsMCOP.CallEventExtras.CallTypeEnum> callTypeEnums, String userID,String groupID){
        if(eventType==null)return;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        //eventType
        event.putExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, eventType.getValue());
        //CallTypeEnum
        if(callTypeEnums!=null && !callTypeEnums.isEmpty())
        event.putExtra(ConstantsMCOP.CallEventExtras.CALL_TYPE, ConstantsMCOP.CallEventExtras.CallTypeEnum.getValue(callTypeEnums));
        //UserID String
        if(userID!=null && !userID.trim().isEmpty()){
            event.putExtra(ConstantsMCOP.CallEventExtras.CALLER_USERID,userID);
        }
        //GroupID String
        if(groupID!=null && !groupID.trim().isEmpty()){
            event.putExtra(ConstantsMCOP.CallEventExtras.CALLER_GROUPID,groupID);
        }
        try{
            String sessionID=String.valueOf(session.getId());
            event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        }catch (Exception ex){

        }
        sendEvent(event);
    }

    private void sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.e(TAG, "CallEvent Error "+ callEventError.getCode()+": "+ callEventError.getString());
        //eventType Error
        event.putExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.ERROR.getValue());
        //Error Code
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE,callEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING,callEventError.getString());
        //SessionID
        try{
            String sessionID=String.valueOf(session.getId());
            event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        }catch (Exception ex){

        }
        sendEvent(event);
    }

    private void sendEvent(Intent event){
        if(event==null )return;
        List<Intent> events=new ArrayList<>();
        events.add(event);
        if(onSessionListener!=null)onSessionListener.onEvents(events);
    }

    public interface OnSessionListener{
        void onEvents(List<Intent> events);
    }

    public void setOnSessionListener(OnSessionListener onSessionListener){
        this.onSessionListener=onSessionListener;
    }



}
