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

import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.SubscriptionAffiliationSession;
import org.doubango.utils.Utils;

public class MySubscriptionAffiliationSession extends NgnSipSession{
	private final static String TAG = Utils.getTAG(MySubscriptionAffiliationSession.class.getCanonicalName());
	private final SubscriptionAffiliationSession mSession;

	private final static NgnObservableHashMap<Long, MySubscriptionAffiliationSession> sSessions = new NgnObservableHashMap<Long, MySubscriptionAffiliationSession>(
			true);

	public static MySubscriptionAffiliationSession createOutgoingSession(NgnSipStack sipStack) {
		synchronized (sSessions) {
			final MySubscriptionAffiliationSession subSession = new MySubscriptionAffiliationSession(sipStack);
			sSessions.put(subSession.getId(), subSession);
			return subSession;
		}
	}
	public static void releaseSession(MySubscriptionAffiliationSession session) {
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
			MySubscriptionAffiliationSession session = MySubscriptionAffiliationSession.getSession(id);
			if (session != null) {
				session.decRef();
				sSessions.remove(id);
			}
		}
	}
	public static MySubscriptionAffiliationSession getSession(long id) {
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
	protected MySubscriptionAffiliationSession(NgnSipStack sipStack){
		super(sipStack);
		mSession = new SubscriptionAffiliationSession(sipStack);
		super.init();
	}
	@Override
	protected SipSession getSession() {
		return mSession;
	}
	
	public boolean subscribeAffiliation(){
		return mSession.subscribeAffiliation();
	}
	public boolean unSubscribeAffiliation(){
		Log.d(TAG,"Unsuscribe affiliation");
		return mSession.unSubscribeAffiliation();
	}

}
