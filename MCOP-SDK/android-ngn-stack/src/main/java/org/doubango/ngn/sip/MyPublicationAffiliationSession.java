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
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.tinyWRAP.ActionConfig;
import org.doubango.tinyWRAP.PublicationAffiliationSession;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.utils.Utils;

public class MyPublicationAffiliationSession extends NgnSipSession {
	private final static String TAG = Utils.getTAG(MyPublicationAffiliationSession.class.getCanonicalName());

	private final PublicationAffiliationSession mSession;

	private final static NgnObservableHashMap<Long, MyPublicationAffiliationSession> sSessions = new NgnObservableHashMap<Long, MyPublicationAffiliationSession>(
			true);

	public static MyPublicationAffiliationSession createOutgoingSession(
			NgnSipStack sipStack, String toUri) {
		synchronized (sSessions) {
			final MyPublicationAffiliationSession pubSession = new MyPublicationAffiliationSession(
					sipStack, toUri);
			sSessions.put(pubSession.getId(), pubSession);
			return pubSession;
		}
	}

	public static void releaseSession(MyPublicationAffiliationSession session) {
		synchronized (sSessions) {
			if (session != null && sSessions.containsKey(session.getId())) {
				long id = session.getId();
				session.decRef();
				sSessions.remove(id);
			}
		}
	}

	public static void releaseSession(long id) {
		synchronized (sSessions) {
			MyPublicationAffiliationSession session = MyPublicationAffiliationSession
					.getSession(id);
			if (session != null) {
				session.decRef();
				sSessions.remove(id);
			}
		}
	}

	public static MyPublicationAffiliationSession getSession(long id) {
		synchronized (sSessions) {
			if (sSessions.containsKey(id))
				return sSessions.get(id);
			else
				return null;
		}
	}

	public static int getSize() {
		synchronized (sSessions) {
			return sSessions.size();
		}
	}

	public static boolean hasSession(long id) {
		synchronized (sSessions) {
			return sSessions.containsKey(id);
		}
	}

	protected MyPublicationAffiliationSession(NgnSipStack sipStack, String toUri) {
		super(sipStack);
		mSession = new PublicationAffiliationSession(sipStack);

		super.init();
		super.setSigCompId(sipStack.getSigCompId());
		super.setToUri(toUri);
		super.setFromUri(toUri);

		// default
		//mSession.addHeader("Event", "presence");
	}

	@Override
	protected SipSession getSession() {
		return mSession;
	}

	public boolean setEvent(String event) {
		return mSession.addHeader("Event", event);
	}

	public boolean setContentType(String contentType) {
		return mSession.addHeader("Content-Type", contentType);
	}

	public boolean publish(byte[] bytes, String event, String contentType, Context context) {
		if (bytes != null) {
			NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
			if(profileNow!=null && !mSipStack.setMCPTTPSIAffiliation(profileNow.getMcpttPsiAffiliation())){
				Log.e(TAG,"Error configuration PSI for affiliation");
			}

			if(BuildConfig.DEBUG && bytes!=null){
				String stringDataSendPublish=new String(bytes);
				Log.w(TAG,"PUBLISH: event->"+event+" contentType->"+contentType);
				String lines[] = Utils.stringToArray(stringDataSendPublish);
				for(String line:lines)Log.w(TAG,line);

			}

			Log.d(TAG,"Start publish of Affiliation");
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(bytes.length);
			byteBuffer.put(bytes);
			ActionConfig config = new ActionConfig();
			final boolean ret = mSession.publish(byteBuffer, byteBuffer.capacity(), config);
			config.delete();
			if(ret){
				Log.d(TAG,"Publish OK.");
			}else{
				Log.d(TAG,"Publish Failed.");
			}
			return ret;
		}
		else{
			Log.e(TAG, "Null content");
		}
		return false;
	}

	public boolean unPublish(byte[] bytes, String event, String contentType, Context context) {
		boolean ret;
		if (bytes != null) {
			NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
			if(profileNow!=null && !mSipStack.setMCPTTPSIAffiliation(profileNow.getMcpttPsiAffiliation())){
				Log.e(TAG,"Error configuration PSI for affiliation.");
			}
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(bytes.length);
			byteBuffer.put(bytes);
			ActionConfig config = new ActionConfig();
			ret = mSession.unPublish(byteBuffer, byteBuffer.capacity(), config);
			config.delete();
			return ret;
		}
		else{
			Log.d(TAG, "Null content");
			ActionConfig config = new ActionConfig();
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(0);

			ret = mSession.unPublish(byteBuffer, byteBuffer.capacity(), config);
			config.delete();
		}
		return ret;
	}
	
	public boolean publish(byte[] bytes, Context context) {
		return publish(bytes, null, null,context);
	}
	public boolean unPublish(byte[] bytes, Context context) {
		Log.d(TAG,"Send unpublish.");
		return unPublish(bytes, null, null, context);
	}

}
