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

public class NgnMessagingLocationEventArgs extends NgnEventArgs{
	private final static String TAG = Utils.getTAG(NgnMessagingLocationEventArgs.class.getCanonicalName());

	private long mSessionId;
    private NgnMessagingLocationEventTypes mEventType;
    private String mPhrase;
    private byte[] mPayload;
    private String mContentType;

    public static final String ACTION_MESSAGING_LOCATION_EVENT = TAG + ".ACTION_MESSAGING_LOCATION_EVENT";

    public static final String EXTRA_EMBEDDED = NgnEventArgs.EXTRA_EMBEDDED; // NgnEventArgs
    public static final String EXTRA_SESSION = TAG + "session"; // NgnSession
    public static final String EXTRA_CODE = TAG + "code"; // Short
    public static final String EXTRA_REMOTE_PARTY = TAG + "from"; // String
    public static final String EXTRA_DATE = TAG + "date"; // Date
    public static final String EXTRA_T140_DATA_TYPE = TAG + "t140_data_type"; // tmedia_t140_data_type_t

    public NgnMessagingLocationEventArgs(long sessionId, NgnMessagingLocationEventTypes type, String phrase, byte[] payload, String contentType){
    	super();
        mSessionId = sessionId;
        mEventType = type;
        mPhrase = phrase;
        mPayload = payload;
        mContentType = contentType;
    }

    public NgnMessagingLocationEventArgs(Parcel in){
    	super(in);
    }

    public static final Creator<NgnMessagingLocationEventArgs> CREATOR = new Creator<NgnMessagingLocationEventArgs>() {
        public NgnMessagingLocationEventArgs createFromParcel(Parcel in) {
            return new NgnMessagingLocationEventArgs(in);
        }

        public NgnMessagingLocationEventArgs[] newArray(int size) {
            return new NgnMessagingLocationEventArgs[size];
        }
    };
    
    public long getSessionId(){
        return mSessionId;
    }

    public NgnMessagingLocationEventTypes getEventType(){
        return mEventType;
    }

    public String getPhrase(){
        return mPhrase;
    }

    public byte[] getPayload(){
        return mPayload;
    }
    
    public String getContentType() {
    	return mContentType;
    }

	@Override
	protected void readFromParcel(Parcel in) {
		mSessionId = in.readLong();
		mEventType = Enum.valueOf(NgnMessagingLocationEventTypes.class, in.readString());
		mPhrase = in.readString();
		mContentType = in.readString();
		mPayload = in.createByteArray();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mSessionId);
		dest.writeString(mEventType.toString());
		dest.writeString(mPhrase);
		dest.writeString(mContentType);
		dest.writeByteArray(mPayload);
	}
}
