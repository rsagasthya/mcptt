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

import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.tinyWRAP.MessagingAffiliationSession;
import org.doubango.tinyWRAP.SipMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.utils.Utils;

import java.nio.ByteBuffer;

/**
 * Messaging session used to send Pager Mode IM (SIP MESSAGE)
 */
public class MyMessagingAffiliationSession extends NgnSipSession {
	private static String TAG = Utils.getTAG(MyMessagingAffiliationSession.class.getCanonicalName());

	private final MessagingAffiliationSession mSession;
	private static int SMS_MR = 0;

	private final static NgnObservableHashMap<Long, MyMessagingAffiliationSession> sSessions = new NgnObservableHashMap<Long, MyMessagingAffiliationSession>(true);

	public static MyMessagingAffiliationSession takeIncomingSession(NgnSipStack sipStack, MessagingAffiliationSession session, SipMessage sipMessage){
		final String toUri = sipMessage==null ? null: sipMessage.getSipHeaderValue("f");
		MyMessagingAffiliationSession imSession = new MyMessagingAffiliationSession(sipStack, session, toUri);
		sSessions.put(imSession.getId(), imSession);
        return imSession;
    }

    public static MyMessagingAffiliationSession createOutgoingSession(NgnSipStack sipStack, String toUri){
        synchronized (sSessions){
            final MyMessagingAffiliationSession imSession = new MyMessagingAffiliationSession(sipStack, null, toUri);
            sSessions.put(imSession.getId(), imSession);
            return imSession;
        }
    }

    public static void releaseSession(MyMessagingAffiliationSession session){
		synchronized (sSessions){
            if (session != null && sSessions.containsKey(session.getId())){
                long id = session.getId();
                session.decRef();
                sSessions.remove(id);
            }
        }
    }

    public static void releaseSession(long id){
		synchronized (sSessions){
			MyMessagingAffiliationSession session = MyMessagingAffiliationSession.getSession(id);
            if (session != null){
                session.decRef();
                sSessions.remove(id);
            }
        }
    }

	public static MyMessagingAffiliationSession getSession(long id) {
		synchronized (sSessions) {
			if (sSessions.containsKey(id))
				return sSessions.get(id);
			else
				return null;
		}
	}

	public static int getSize(){
        synchronized (sSessions){
            return sSessions.size();
        }
    }

    public static boolean hasSession(long id){
        synchronized (sSessions){
            return sSessions.containsKey(id);
        }
    }

	protected MyMessagingAffiliationSession(NgnSipStack sipStack, MessagingAffiliationSession session, String toUri) {
		super(sipStack);
        mSession = session == null ? new MessagingAffiliationSession(sipStack) : session;

        super.init();
        super.setSigCompId(sipStack.getSigCompId());
        super.setToUri(toUri);
	}

	@Override
	protected SipSession getSession() {
		return mSession;
	}
	


	/**
	 * Send plain text message using SIP MESSAGE request
	 * @param text
	 * @return true if succeed and false otherwise
	 * @sa @ref SendBinaryMessage()
	 */
    public boolean sendTextMessage(String text, String contentType){

        byte[] bytes = text.getBytes();
        ByteBuffer payload = ByteBuffer.allocateDirect(bytes.length);
        payload.put(bytes);
        return mSession.send(payload, payload.capacity());
    }

    public boolean sendTextMessage(String text){
    	return sendTextMessage(text, null);    	
    }

    /**
     * Accepts the message (sends 200 OK).
     * @return true if succeed and false otherwise
     */
    public boolean accept() {
        return mSession.accept();
      }

    /**
     * Reject the message (sends 603 Decline)
     * @return true if succeed and false otherwise
     */
      public boolean reject() {
        return mSession.reject();
      }
}
