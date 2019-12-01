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

public class NgnPublicationAffiliationEventArgs extends NgnEventArgs{
	private final static String TAG = Utils.getTAG(NgnPublicationAffiliationEventArgs.class.getCanonicalName());

	public static final String ACTION_PUBLICATION_AFFILIATION_EVENT = TAG + ".ACTION_PUBLICATION_EVENT";

	public static final String EXTRA_EMBEDDED = NgnEventArgs.EXTRA_EMBEDDED;

	private long mSessionId;
	private NgnPublicationAffiliationEventTypes mType;
	private short mSipCode;
	private String mPhrase;

	public NgnPublicationAffiliationEventArgs(long sessionId, NgnPublicationAffiliationEventTypes type, short sipCode, String phrase){
    	super();
    	mSessionId = sessionId;
    	mType = type;
    	mSipCode = sipCode;
    	mPhrase = phrase;
    }

    public NgnPublicationAffiliationEventArgs(Parcel in){
    	super(in);
    }

    public static final Creator<NgnPublicationAffiliationEventArgs> CREATOR = new Creator<NgnPublicationAffiliationEventArgs>() {
        public NgnPublicationAffiliationEventArgs createFromParcel(Parcel in) {
            return new NgnPublicationAffiliationEventArgs(in);
        }

        public NgnPublicationAffiliationEventArgs[] newArray(int size) {
            return new NgnPublicationAffiliationEventArgs[size];
        }
    };

    public long getSessionId(){
    	return mSessionId;
    }
    
    public NgnPublicationAffiliationEventTypes getEventType(){
        return mType;
    }

    public short getSipCode(){
        return mSipCode;
    }

    public String getPhrase(){
        return mPhrase;
    }

	@Override
	protected void readFromParcel(Parcel in) {
		mSessionId = in.readLong();
		mType = Enum.valueOf(NgnPublicationAffiliationEventTypes.class, in.readString());
		mSipCode = (short)in.readInt();
		mPhrase = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(mSessionId);
		dest.writeString(mType.toString());
		dest.writeInt(mSipCode);
		dest.writeString(mPhrase);
	}
}
