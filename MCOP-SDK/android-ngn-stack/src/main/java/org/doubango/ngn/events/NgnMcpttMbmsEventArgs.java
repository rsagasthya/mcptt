/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source IMSDROID
*  Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of Open Source Doubango Framework.
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
package org.doubango.ngn.events;

import android.os.Parcel;

import org.doubango.utils.Utils;

public class NgnMcpttMbmsEventArgs extends NgnEventArgs{
	private final static String TAG = Utils.getTAG(NgnMcpttMbmsEventArgs.class.getCanonicalName());

	private long mSessionId;
    private NgnMcpttMbmsEventTypes mEventType;

    public static final String ACTION_MCPTT_MBMS_EVENT = TAG + ".ACTION_MCPTT_MBMS_EVENT";

    private static long sessionId;
    private NgnMcpttMbmsEventTypes type;

    public static String EXTRA_SESSION = NgnInviteEventArgs.EXTRA_SESSION;
    public static String EXTRA_GROUP = "group";
    public static String EXTRA_TMGI = "tmgi";
    public static String EXTRA_MEDIA_IP = "media_ip";
    public static String EXTRA_MEDIA_PORT = "media_port";
    public static String EXTRA_MEDIA_CTRL_PORT = "media_ctrl_port";

    public NgnMcpttMbmsEventArgs(long sessionId, NgnMcpttMbmsEventTypes type){
    	super();
    	mSessionId = sessionId;
    	mEventType = type;
    }

    public NgnMcpttMbmsEventArgs(Parcel in){
    	super(in);
    }

    public static final Creator<NgnMcpttMbmsEventArgs> CREATOR = new Creator<NgnMcpttMbmsEventArgs>() {
        public NgnMcpttMbmsEventArgs createFromParcel(Parcel in) {
            return new NgnMcpttMbmsEventArgs(in);
        }

        public NgnMcpttMbmsEventArgs[] newArray(int size) {
            return new NgnMcpttMbmsEventArgs[size];
        }
    };
    
    public long getSessionId(){
        return mSessionId;
    }

    public NgnMcpttMbmsEventTypes getEventType(){
        return mEventType;
    }

	@Override
	protected void readFromParcel(Parcel in) {
		mSessionId = in.readLong();
		mEventType = Enum.valueOf(NgnMcpttMbmsEventTypes.class, in.readString());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mSessionId);
		dest.writeString(mEventType.toString());
	}
}
