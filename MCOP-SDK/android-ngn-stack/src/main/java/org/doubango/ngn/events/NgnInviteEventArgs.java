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
import android.os.Parcelable;

import org.doubango.ngn.media.NgnMediaType;
import org.doubango.utils.Utils;

/**
 * Event argument for SIP INVITE sessions
 */
public class NgnInviteEventArgs extends NgnEventArgs{
	private final static String TAG = Utils.getTAG(NgnInviteEventArgs.class.getCanonicalName());
	
	private long mSessionId;
    private NgnInviteEventTypes mEventType;
    private NgnMediaType mMediaType;
    private int mCode;
    private String mPhrase;
    
    public static final String ACTION_INVITE_EVENT = TAG + ".ACTION_INVITE_EVENT";
    
    public static final String EXTRA_EMBEDDED = NgnEventArgs.EXTRA_EMBEDDED; // @NgnInviteEventArgs
    public static final String EXTRA_SESSION = "session"; // @object
    public static final String EXTRA_SIPCODE = "sipCode"; // @short
    public static final String EXTRA_REFERTO_URI = "referto-uri"; //@String

    public NgnInviteEventArgs(long mSessionId, NgnInviteEventTypes mEventType, NgnMediaType mMediaType, String mPhrase, int code) {
        this.mSessionId = mSessionId;
        this.mEventType = mEventType;
        this.mMediaType = mMediaType;
        this.mCode = code;
        this.mPhrase = mPhrase;
    }

    public NgnInviteEventArgs(long sessionId, NgnInviteEventTypes eventType, NgnMediaType mediaType, String phrase){
    	super();
    	mSessionId = sessionId;
    	mEventType = eventType;
    	mMediaType = mediaType;
    	mPhrase = phrase;
    	mCode=-1;
    }

    public NgnInviteEventArgs(Parcel in){
    	super(in);
    }

    public static final Parcelable.Creator<NgnInviteEventArgs> CREATOR = new Parcelable.Creator<NgnInviteEventArgs>() {
        public NgnInviteEventArgs createFromParcel(Parcel in) {
            return new NgnInviteEventArgs(in);
        }

        public NgnInviteEventArgs[] newArray(int size) {
            return new NgnInviteEventArgs[size];
        }
    };
    
    public long getSessionId(){
        return mSessionId;
    }

    public NgnInviteEventTypes getEventType(){
        return mEventType;
    }
    
    public NgnMediaType getMediaType(){
        return mMediaType;
    }

    public String getPhrase(){
        return mPhrase;
    }

    @Override
	protected void readFromParcel(Parcel in) {
    	mSessionId = in.readLong();
		mEventType = Enum.valueOf(NgnInviteEventTypes.class, in.readString());
		mMediaType = Enum.valueOf(NgnMediaType.class, in.readString());
		mPhrase = in.readString();
        mCode=in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mSessionId);
		dest.writeString(mEventType.toString());
		dest.writeString(mMediaType.toString());
		dest.writeString(mPhrase);
		dest.writeInt(mCode);
	}

    public int getCode() {
        return mCode;
    }
}
