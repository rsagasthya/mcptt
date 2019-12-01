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

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.tinyWRAP.ActionConfig;
import org.doubango.tinyWRAP.PublicationAuthenticationSession;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.utils.Utils;

public class MyPublicationAuthenticationSession extends NgnSipSession {
	private final static String TAG = Utils.getTAG(MyPublicationAuthenticationSession.class.getCanonicalName());

	private final PublicationAuthenticationSession mSession;

	private final static NgnObservableHashMap<Long, MyPublicationAuthenticationSession> sSessions = new NgnObservableHashMap<Long, MyPublicationAuthenticationSession>(
			true);

	public static MyPublicationAuthenticationSession createOutgoingSession(
			NgnSipStack sipStack, String toUri) {
		synchronized (sSessions) {
			final MyPublicationAuthenticationSession pubSession = new MyPublicationAuthenticationSession(
					sipStack, toUri);
			sSessions.put(pubSession.getId(), pubSession);
			return pubSession;
		}
	}

	public static void releaseSession(MyPublicationAuthenticationSession session) {
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
			MyPublicationAuthenticationSession session = MyPublicationAuthenticationSession
					.getSession(id);
			if (session != null) {
				session.decRef();
				sSessions.remove(id);
			}
		}
	}

	public static MyPublicationAuthenticationSession getSession(long id) {
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

	protected MyPublicationAuthenticationSession(NgnSipStack sipStack, String toUri) {
		super(sipStack);
		mSession = new PublicationAuthenticationSession(sipStack);

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

	public boolean publish(String mcpttInfo, String pocSettings, Context context) {

		if (mcpttInfo != null && pocSettings!=null) {
			NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
			if(profileNow!=null && !mSipStack.setMCPTTPSIAuthentication(profileNow.getMcpttPsiAuthentication())){
				Log.e(TAG,"Error on PSI configuration for Authentication");
			}
			byte[] bytes={34,43};
			Log.d(TAG,"Start publish of Authentication.");
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(bytes.length);
			byteBuffer.put(bytes);
			ActionConfig config = new ActionConfig();
			final boolean ret = mSession.publish(mcpttInfo,pocSettings,byteBuffer, byteBuffer.capacity(), config);
			config.delete();
			if(ret){
				Log.d(TAG,"Publish OK");
			}else{
				Log.d(TAG,"Publish Failed");
			}
			return ret;
		}
		else{
			Log.e(TAG, "Null content");
		}
		return false;
	}

	public boolean unPublish(byte[] bytes, String event, String contentType, Context context) {
		if (bytes != null) {
			NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
			if(profileNow!=null && !mSipStack.setMCPTTPSIAuthentication(profileNow.getMcpttPsiAuthentication())){
				Log.e(TAG,"Error on PSI configuration for Authentication");
			}
			final java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(bytes.length);
			byteBuffer.put(bytes);
			ActionConfig config = new ActionConfig();
			final boolean ret = mSession.unPublish(byteBuffer, byteBuffer.capacity(), config);
			config.delete();
			return ret;
		}
		else{
			Log.e(TAG, "Null content");
		}
		return false;
	}

	public boolean unPublish(byte[] bytes, Context context) {
		Log.d(TAG,"Send unpublish");
		return unPublish(bytes, null, null, context);
	}

	public boolean unPublish(Context context) {
		Log.d(TAG,"Send unpublish");
		byte[] bytes={34,43};
		return unPublish(bytes, null, null, context);
	}

}
