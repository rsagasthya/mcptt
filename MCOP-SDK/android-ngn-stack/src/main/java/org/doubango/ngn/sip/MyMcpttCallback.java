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
package org.doubango.ngn.sip;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnMcpttEventArgs;
import org.doubango.ngn.events.NgnMcpttEventTypes;
import org.doubango.tinyWRAP.McpttCallback;
import org.doubango.tinyWRAP.McpttEvent;
import org.doubango.tinyWRAP.McpttMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.tmcptt_event_type_t;
import org.doubango.utils.Utils;

import static org.doubango.ngn.events.NgnMcpttEventArgs.ACTION_MCPTT_EVENT;

/**
 * MyMcpttCallback extends McpttCallback class, and onEvent method is overwritten. This allows to
 * get all received MCPTT messages. Then, after token status update messages, a broadcast message
 * is sent so that it can be heard from any part of the program.
 */
public class MyMcpttCallback extends McpttCallback {
    private static final String TAG = Utils.getTAG(MyMcpttCallback.class.getCanonicalName());
    private NgnAVSession mSession;
    final Context mAppContext;
    private String mContentType;
    public final String ACTION_MCPTT;



    public MyMcpttCallback(NgnAVSession session){
        super();
        this.mSession=session;
        mAppContext = NgnApplication.getContext();
        Log.d(TAG,"Create Callback for MCPTT calls");
        ACTION_MCPTT=ACTION_MCPTT_EVENT+""+mSession.getId();

    }



    /**
     * Overwrite OnEvent method
     * @param e
     * @return
     */
    //
    public int OnEvent(McpttEvent e) {
        McpttMessage message;
        NgnMcpttEventArgs eargs;
        Intent intent;
        String user;

        tmcptt_event_type_t type = e.getType();
        SipSession session = e.getSipSession();
        if (session == null || session.getId() != this.mSession.getId())
            return -1;
        Log.d(TAG,"OnEvent");
        switch (type){
            case tmcptt_event_type_token_granted:
                Log.d(TAG,"tmcptt_event_type_token_granted");
                message = e.getMessage();
                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_GRANTED);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                user=message.getUser();
                if(user!=null) {
                    intent.putExtra(NgnMcpttEventArgs.EXTRA_USER, message.getUser());
                }
                intent.putExtra(NgnMcpttEventArgs.EXTRA_TIME, message.getTime());
                intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE, message.getRCode());
                intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE,message.getPhrase());

                /*
                The Time argument indicates the time that is given to us to keep the token.
                If more time is spent, the token is revoked.
                */

                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_event_type_token_taken:
                Log.e(TAG,"tmcptt_event_type_token_taken");

                message = e.getMessage();

                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_TAKEN);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                user=message.getUser();
                if(user!=null){
                    intent.putExtra(NgnMcpttEventArgs.EXTRA_USER, message.getUser());
                    Log.d(TAG,"User taking is "+message.getUser());
                }else{
                    Log.d(TAG,"User not taking "+message.getUser());
                }
                intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE, message.getRCode());
                intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE,message.getPhrase());
                intent.putExtra(NgnMcpttEventArgs.EXTRA_TIME,message.getTime());
                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_event_type_idle_channel:
                Log.d(TAG, "tmcptt_event_type_idle_channel");


                message = e.getMessage();
                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.IDLE_CHANNEL);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                mAppContext.sendBroadcast(intent);



                break;
            case tmcptt_event_type_request_sent:
                Log.d(TAG,"tmcptt_event_type_request_sent");
                message = e.getMessage();
                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_REQUESTED);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);

                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_event_type_release_sent:
                Log.d(TAG,"tmcptt_event_type_release_sent");

                message = e.getMessage();
                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_RELEASED);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_event_type_permission_revoked:
                Log.e(TAG,"tmcptt_event_type_permission_revoked");

                message = e.getMessage();
                eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_REVOKED);
                intent = new Intent(ACTION_MCPTT);
                intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                if(message!=null){
                    if(message.getRCode()>0){
                        intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE, message.getRCode());
                        if(BuildConfig.DEBUG)Log.d(TAG,"CODE token_revoked "+message.getRCode());
                    }
                    intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE,message.getPhrase());
                    if(BuildConfig.DEBUG)Log.d(TAG,"PHRASE token_revoked "+message.getPhrase());
                }else{

                }
                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_event_type_token_denied:
                Log.e(TAG,"tmcptt_event_type_token_denied");

                message = e.getMessage();

                    eargs = new NgnMcpttEventArgs(this.mSession.getId(),NgnMcpttEventTypes.TOKEN_DENIED);
                    intent = new Intent(ACTION_MCPTT);
                    intent.putExtra(NgnMcpttEventArgs.EXTRA_EMBEDDED,eargs);
                if(message!=null){
                    if(message.getRCode()>0){
                        intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE, message.getRCode());
                        if(BuildConfig.DEBUG)Log.d(TAG,"CODE token_denied "+message.getRCode());
                    }
                    intent.putExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE,message.getPhrase());
                    if(BuildConfig.DEBUG)Log.d(TAG,"PHRASE token_denied "+message.getPhrase());
                    mAppContext.sendBroadcast(intent);
                }else{

                }


                break;
            default:
                break;
        }
        return 0;

    }

}
