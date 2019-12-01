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

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.events.NgnMcpttMbmsEventArgs;
import org.doubango.ngn.events.NgnMcpttMbmsEventTypes;
import org.doubango.tinyWRAP.McpttMbmsCallback;
import org.doubango.tinyWRAP.McpttMbmsEvent;
import org.doubango.tinyWRAP.McpttMbmsMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.tmcptt_mbms_event_type_t;
import org.doubango.utils.Utils;

import java.math.BigInteger;

/**
 * MyMcpttMbmsCallback extends McpttMbmsCallback class, and onEvent method is overwritten. This allows to
 * get all received MCPTT messages. Then, after token status update messages, a broadcast message
 * is sent so that it can be heard from any part of the program.
 */
public class MyMcpttMbmsCallback extends McpttMbmsCallback {
    private static final String TAG = Utils.getTAG(MyMcpttMbmsCallback.class.getCanonicalName());
    private NgnAVSession mSession;
    final Context mAppContext;
    private String mContentType;



    public MyMcpttMbmsCallback(NgnAVSession session){
        super();
        this.mSession=session;
        mAppContext = NgnApplication.getContext();
        Log.d(TAG,"Create Callback for MCPTT MBMS calls");
    }



    /**
     * Overwrite OnEvent method
     * @param e
     * @return
     */
    public int OnEvent(McpttMbmsEvent e) {
        McpttMbmsMessage message;
        NgnMcpttMbmsEventArgs eargs;
        Intent intent;
        String group;
        String tmgi;
        String mediaIP;

        tmcptt_mbms_event_type_t type = e.getType();
        SipSession session = e.getSipSession();
        if (session == null || session.getId() != this.mSession.getId())
            return -1;
        Log.d(TAG,"OnEvent");
        switch (type){
            case tmcptt_mbms_event_type_map_group:
                Log.d(TAG,"tmcptt_mbms_event_type_map_group");

                message = e.getMessage();
                eargs = new NgnMcpttMbmsEventArgs(this.mSession.getId(),NgnMcpttMbmsEventTypes.MAP_GROUP);
                intent = new Intent(NgnMcpttMbmsEventArgs.ACTION_MCPTT_MBMS_EVENT);
                intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_EMBEDDED,eargs);
                group=message.getGroupId();
                if(group!=null) {
                    intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_GROUP, message.getGroupId());
                    Log.d(TAG,"Mapping MBMS bearer to group " + message.getGroupId());
                }
                tmgi=message.getTMGI();
                if(tmgi!=null) {
                    BigInteger bigInt = new BigInteger(tmgi, 16);
                    long dataTMGI=-1;
                    if(bigInt!=null){
                        dataTMGI= bigInt.longValue();
                    }
                    intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_TMGI, dataTMGI);

                }
                mediaIP = message.getMediaIP();
                if (mediaIP != null) {
                    intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_IP, message.getMediaIP());
                }
                intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_PORT, message.getMediaPort());
                intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_CTRL_PORT, message.getMediaControlPort());

                mAppContext.sendBroadcast(intent);

                break;
            case tmcptt_mbms_event_type_unmap_group:
                Log.e(TAG,"tmcptt_mbms_event_type_unmap_group");

                message = e.getMessage();

                eargs = new NgnMcpttMbmsEventArgs(this.mSession.getId(),NgnMcpttMbmsEventTypes.UNMAP_GROUP);
                intent = new Intent(NgnMcpttMbmsEventArgs.ACTION_MCPTT_MBMS_EVENT);
                intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_EMBEDDED,eargs);
                group=message.getGroupId();
                if(group!=null){
                    intent.putExtra(NgnMcpttMbmsEventArgs.EXTRA_GROUP, message.getGroupId());
                    Log.d(TAG,"Unmapping MBMS bearer from group " + message.getGroupId());
                }

                mAppContext.sendBroadcast(intent);

                break;
            default:
                break;
        }
        return 0;

    }

}
