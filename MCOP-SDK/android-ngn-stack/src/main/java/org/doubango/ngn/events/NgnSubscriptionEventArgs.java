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

import org.doubango.ngn.sip.NgnSubscriptionSession.EventPackageType;
import org.doubango.utils.Utils;

public class NgnSubscriptionEventArgs extends NgnEventArgs{
	private final static String TAG = Utils.getTAG(NgnSubscriptionEventArgs.class.getCanonicalName());
	
	public static final String ACTION_SUBSCRIBTION_EVENT = TAG + ".ACTION_SUBSCRIBTION_EVENT";
	
	private long mSessionId;
	private NgnSubscriptionEventTypes mType;
    private short mSipCode;
    private String mPhrase;
    private byte[] mContent;
    private String mContentType;
    private EventPackageType mEventPackage;

    public static final String EXTRA_EMBEDDED = NgnEventArgs.EXTRA_EMBEDDED;
    public final String EXTRA_CONTENTYPE_TYPE = "ContentTypeType";
    public final String EXTRA_CONTENTYPE_START = "ContentTypeStart";
    public final String EXTRA_CONTENTYPE_BOUNDARY = "ContentTypeBoundary";
	
	public NgnSubscriptionEventArgs(long sessionId, NgnSubscriptionEventTypes type, short sipCode, String phrase, 
			byte[] content, String contentType, EventPackageType eventPackage){
		super();
		mSessionId = sessionId;
		mType = type;
		mSipCode = sipCode;
		mPhrase = phrase;
		mContent = content;
		mContentType = contentType;
		mEventPackage = eventPackage;
	}

	public NgnSubscriptionEventArgs(Parcel in){
    	super(in);
    }

    public static final Parcelable.Creator<NgnSubscriptionEventArgs> CREATOR = new Parcelable.Creator<NgnSubscriptionEventArgs>() {
        public NgnSubscriptionEventArgs createFromParcel(Parcel in) {
            return new NgnSubscriptionEventArgs(in);
        }

        public NgnSubscriptionEventArgs[] newArray(int size) {
            return new NgnSubscriptionEventArgs[size];
        }
    };
    
    public long getSessionId(){
        return mSessionId;
    }

    public NgnSubscriptionEventTypes getEventType(){
        return mType;
    }

    public String getPhrase(){
        return mPhrase;
    }

    public byte[] getContent(){
        return mContent;
    }
    
    public String getContentType(){
        return mContentType;
    }
    
    public EventPackageType getEventPackage(){
        return mEventPackage;
    }
    
	@Override
	protected void readFromParcel(Parcel in) {
		mSessionId = in.readLong();
		mType = Enum.valueOf(NgnSubscriptionEventTypes.class, in.readString());
		mSipCode = (short)in.readInt();
		mPhrase = in.readString();
		mContent = in.createByteArray();
		mContentType = in.readString();
		mEventPackage = Enum.valueOf(EventPackageType.class, in.readString());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mSessionId);
		dest.writeString(mType.toString());
		dest.writeInt(mSipCode);
		dest.writeString(mPhrase);
		dest.writeByteArray(mContent);
		dest.writeString(mContentType);
		dest.writeString(mEventPackage.toString());
	}
}
