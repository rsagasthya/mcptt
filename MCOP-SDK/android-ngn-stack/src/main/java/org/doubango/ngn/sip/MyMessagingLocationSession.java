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

import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.ngn.utils.NgnUriUtils;
import org.doubango.tinyWRAP.MessagingLocationSession;
import org.doubango.tinyWRAP.RPMessage;
import org.doubango.tinyWRAP.SMSEncoder;
import org.doubango.tinyWRAP.SipMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.utils.Utils;

import java.nio.ByteBuffer;

/**
 * Messaging session used to send Pager Mode IM (SIP MESSAGE)
 */
public class MyMessagingLocationSession extends NgnSipSession {
	private static String TAG = Utils.getTAG(MyMessagingLocationSession.class.getCanonicalName());

	private final MessagingLocationSession mSession;
	private static int SMS_MR = 0;

	private final static NgnObservableHashMap<Long, MyMessagingLocationSession> sSessions = new NgnObservableHashMap<Long, MyMessagingLocationSession>(true);

	public static MyMessagingLocationSession takeIncomingSession(NgnSipStack sipStack, MessagingLocationSession session, SipMessage sipMessage){
		final String toUri = sipMessage==null ? null: sipMessage.getSipHeaderValue("f");
		MyMessagingLocationSession imSession = new MyMessagingLocationSession(sipStack, session, toUri);
		sSessions.put(imSession.getId(), imSession);
        return imSession;
    }

    public static MyMessagingLocationSession createOutgoingSession(NgnSipStack sipStack, String toUri){
        synchronized (sSessions){
            final MyMessagingLocationSession imSession = new MyMessagingLocationSession(sipStack, null, toUri);
            sSessions.put(imSession.getId(), imSession);
            return imSession;
        }
    }

    public static void releaseSession(MyMessagingLocationSession session){
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
			MyMessagingLocationSession session = MyMessagingLocationSession.getSession(id);
            if (session != null){
                session.decRef();
                sSessions.remove(id);
            }
        }
    }

	public static MyMessagingLocationSession getSession(long id) {
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

	protected MyMessagingLocationSession(NgnSipStack sipStack, MessagingLocationSession session, String toUri) {
		super(sipStack);
        mSession = session == null ? new MessagingLocationSession(sipStack) : session;

        super.init();
        super.setSigCompId(sipStack.getSigCompId());
        super.setToUri(toUri);
	}

	@Override
	protected SipSession getSession() {
		return mSession;
	}
	
	/**
	 * Sends binary SMS (3gpp) using SIP MESSAGE request
	 * @param text the text (utf-8) to send.
	 * @param SMSC the address (PSI) of the SMS center
	 * @return true if succeed and false otherwise
	 * @sa @ref sendTextMessage()
	 */
	public boolean SendBinaryMessage(String text, String SMSC){
        String SMSCPhoneNumber;
        String dstPhoneNumber;
        String dstSipUri = super.getToUri();

        if ((SMSCPhoneNumber = NgnUriUtils.getValidPhoneNumber(SMSC)) != null && (dstPhoneNumber = NgnUriUtils.getValidPhoneNumber(dstSipUri)) != null){
            super.setToUri(SMSC);
            super.addHeader("Content-Type", NgnContentType.SMS_3GPP);
            super.addHeader("Content-Transfer-Encoding", "binary");
            super.addCaps("+g.3gpp.smsip");       
                    
            RPMessage rpMessage;
            //if(ServiceManager.getConfigurationService().getBoolean(CONFIGURATION_SECTION.RCS, CONFIGURATION_ENTRY.HACK_SMS, false)){
                //    rpMessage = SMSEncoder.encodeDeliver(++ScreenSMSCompose.SMS_MR, SMSCPhoneNumber, dstPhoneNumber, new String(content));
                //    session.addHeader("P-Asserted-Identity", SMSC);
            //}
            //else{
                    rpMessage = SMSEncoder.encodeSubmit(++MyMessagingLocationSession.SMS_MR, SMSCPhoneNumber, dstPhoneNumber, text);
            //}
        
            long rpMessageLen = rpMessage.getPayloadLength();
            ByteBuffer payload = ByteBuffer.allocateDirect((int)rpMessageLen);
            long payloadLength = rpMessage.getPayload(payload, (long)payload.capacity());
            boolean ret = mSession.send(payload, payloadLength);
            rpMessage.delete();
            if(MyMessagingLocationSession.SMS_MR >= 255){
                 MyMessagingLocationSession.SMS_MR = 0;
            }

            return ret;
        }
        else{
            Log.e(TAG, String.format("SMSC=%s or RemoteUri=%s is invalid", SMSC, dstSipUri));
            return sendTextMessage(text);
        }
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
