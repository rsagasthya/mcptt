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

import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.SubscriptionGMSSession;
import org.doubango.utils.Utils;

import java.nio.ByteBuffer;

public class MySubscriptionGMSSession extends NgnSipSession{
	private final static String TAG = Utils.getTAG(MySubscriptionGMSSession.class.getCanonicalName());
	private final SubscriptionGMSSession mSession;

	private final static NgnObservableHashMap<Long, MySubscriptionGMSSession> sSessions = new NgnObservableHashMap<Long, MySubscriptionGMSSession>(
			true);

	public static MySubscriptionGMSSession createOutgoingSession(NgnSipStack sipStack) {
		synchronized (sSessions) {
			final MySubscriptionGMSSession subSession = new MySubscriptionGMSSession(sipStack);
			sSessions.put(subSession.getId(), subSession);
			return subSession;
		}
	}
	public static void releaseSession(MySubscriptionGMSSession session) {
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
			MySubscriptionGMSSession session = MySubscriptionGMSSession.getSession(id);
			if (session != null) {
				session.decRef();
				sSessions.remove(id);
			}
		}
	}
	public static MySubscriptionGMSSession getSession(long id) {
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
	protected MySubscriptionGMSSession(NgnSipStack sipStack){
		super(sipStack);
		mSession = new SubscriptionGMSSession(sipStack);
		super.init();
	}
	@Override
	protected SipSession getSession() {
		return mSession;
	}

	public boolean subscribeGMS(String resourceList,String mcpttInfo){
		if(resourceList==null || resourceList.isEmpty()){
			if(BuildConfig.DEBUG)Log.e(TAG,"For Subcribe CMS is not correct parameters");
			return false;
		}
		byte[] bytes = resourceList.getBytes();
		ByteBuffer payload = ByteBuffer.allocateDirect(bytes.length);
		payload.put(bytes);
		ByteBuffer payload2=null;
		//With ACCESS TOKEN
		if(mcpttInfo!=null && !mcpttInfo.isEmpty()){
			byte[] bytes2 = mcpttInfo.getBytes();
			payload2 = ByteBuffer.allocateDirect(bytes2.length);
			payload2.put(bytes2);
		}
		return mSession.subscribeGMS(payload, payload.capacity(),payload2!=null?payload2:ByteBuffer.allocate(0),payload2!=null?payload2.capacity():-1);
	}

	public boolean unSubscribeGMS(){
		Log.d(TAG,"Unsuscribe GMS");
		return mSession.unSubscribeGMS();
	}

}
