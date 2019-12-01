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

package org.mcopenplatform.muoapi.session;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnInviteEventTypes;
import org.doubango.ngn.sip.NgnAVSession;
import org.mcopenplatform.muoapi.utils.Utils;
import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.ConstantsMCOP;
import org.mcopenplatform.muoapi.datatype.error.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ManagerSessions implements
        Session.OnSessionListener
{
    private final static String TAG = Utils.getTAG(ManagerSessions.class.getCanonicalName());
    private Map<Long,Session> sessions;
    private static ManagerSessions mInstance;
    private Context context;
    private BroadcastReceiver mSipBroadcastRecvInvite;
    private OnManagerSessionListener onManagerSessionListener;

    public ManagerSessions(Context context) {
        this.context=context;
        sessions=new HashMap<>();
        startManagerSessions();
    }

    public static ManagerSessions getInstance(Context context){
        if(mInstance == null){
            mInstance = new ManagerSessions(context);

        }
        if(!mInstance.isStarted()){
            mInstance.startManagerSessions();
        }
        return mInstance;
    }

    public boolean isStarted(){
        return mSipBroadcastRecvInvite!=null?true:false;
    }

    public long newSession(NgnAVSession session){
        if(session==null || session.getId()<=0)return -1;
        if(BuildConfig.DEBUG)Log.d(TAG,"newSession");
        Session newSession=new Session(session,context);
        newSession.setOnSessionListener(this);
        sessions.put(session.getId(),newSession);
        return session.getId();
    }

    public Session getSession(long id){
        return sessions.get(id);
    }

    public Session getSession(String id){
        try{
            return getSession(Long.valueOf(id));
        }catch (Exception ex){
            return null;
        }
    }

    private void startManagerSessions(){
        if(mSipBroadcastRecvInvite==null){
            mSipBroadcastRecvInvite=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    //Registration Event
                    if (NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)) {
                        NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
                        NgnAVSession mSession;
                        Session session;
                        long sessionID=args.getSessionId();
                        if ((mSession = NgnAVSession.getSession(sessionID)) == null && (sessionID<=-1 || args.getEventType()!= NgnInviteEventTypes.TERMINATED)) {
                            Log.e(TAG, "Null Session (2) "+args.getEventType().toString());
                            return;
                        }else if((session=getSession(args.getSessionId()))==null){
                            //New Session
                            newSession(mSession);
                            session=getSession(args.getSessionId());
                        }
                        session.newInviteEvent(args);
                    }
                }
            };
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
            context.registerReceiver(mSipBroadcastRecvInvite, intentFilter);
        }

    }

    public void stopManagerSessions(){
        if(mSipBroadcastRecvInvite!=null && context!=null){
            context.unregisterReceiver(mSipBroadcastRecvInvite);
            mSipBroadcastRecvInvite=null;
            if(BuildConfig.DEBUG)Log.d(TAG,"stopManagerSessions");
        }
    }

    public boolean hangUpCall(String sessionID){
        Session session=null;
        if(sessionID==null || sessionID.trim().isEmpty() || sessions==null || (session=getSession(sessionID))==null){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDIV,sessionID);
            return false;
        }
        return session.hangUpCall();
    }

    public boolean acceptCall(String sessionID){
        Session session=null;
        if(sessionID==null || sessionID.trim().isEmpty() || sessions==null || (session=getSession(sessionID))==null){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDV,sessionID);
            return false;
        }
        return session.acceptCall();
    }

    public boolean floorControlOperation(
            String sessionID,
            org.mcopenplatform.muoapi.ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum operationType,
            String userID){
        Session session=null;
        if(sessionID==null || sessionID.trim().isEmpty() || sessions==null || (session=getSession(sessionID))==null){
            sendErrorFloorControlEvent(Constants.ConstantsErrorMCOP.FloorControlEventError.CI,sessionID);
            return false;
        }else if(operationType==null || operationType== ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.none){
            sendErrorFloorControlEvent(Constants.ConstantsErrorMCOP.FloorControlEventError.CII,sessionID);
            return false;
        }
        return session.floorControlOperation(operationType,userID);
    }



//START EVENT

    //START CALL EVENT
    private void sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError,String sessionID){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        if(BuildConfig.DEBUG)Log.e(TAG, "CallEvent Error "+ callEventError.getCode()+": "+ callEventError.getString());
        //eventType Error
        event.putExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.ERROR.getValue());
        //Error Code
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE,callEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING,callEventError.getString());
        //SessionID
        event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        sendEvent(event);
    }
    //END CALL EVENT

    //START FLOOR CONTROL EVENT
    private void sendErrorFloorControlEvent(Constants.ConstantsErrorMCOP.FloorControlEventError  floorControlEventError,String sessionID){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        if(BuildConfig.DEBUG)Log.e(TAG, "Floor Control Error "+ floorControlEventError.getCode()+": "+ floorControlEventError.getString());
        //Error Code
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_CODE,floorControlEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.ERROR_STRING,floorControlEventError.getString());
        //SessionID
        event.putExtra(ConstantsMCOP.FloorControlEventExtras.SESSION_ID,sessionID);
        sendEvent(event);
    }
    //END FLOOR CONTROL EVENT

    @Override
    public void onEvents(List<Intent> events) {
        if(onManagerSessionListener!=null)onManagerSessionListener.onEvents(events);
    }

    public interface OnManagerSessionListener{
        void onEvents(List<Intent> events);
    }

    public void setOnManagerSessionListener(OnManagerSessionListener onManagerSessionListener){
        this.onManagerSessionListener=onManagerSessionListener;
    }

    private void sendEvent(Intent event){
        if(event==null )return;
        List<Intent> events=new ArrayList<>();
        events.add(event);
        if(onManagerSessionListener!=null)onManagerSessionListener.onEvents(events);
    }

//END EVENT
}