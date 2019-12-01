/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
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
* 
* @contributors: See $(DOUBANGO_HOME)\contributors.txt
*/
package org.doubango.ngn.sip;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.location.TypeMcpttSignallingEvent;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnMcpttEventArgs;
import org.doubango.ngn.events.NgnMcpttEventTypes;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.events.NgnMessagingEventTypes;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.media.NgnProxyAudioConsumer;
import org.doubango.ngn.media.NgnProxyAudioProducer;
import org.doubango.ngn.media.NgnProxyPlugin;
import org.doubango.ngn.media.NgnProxyPluginMgr;
import org.doubango.ngn.media.NgnProxyVideoConsumer;
import org.doubango.ngn.media.NgnProxyVideoProducer;
import org.doubango.ngn.model.NgnHistoryAVCallEvent;
import org.doubango.ngn.model.NgnHistoryEvent;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.impl.mbms.MyMbmsService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnDateTimeUtils;
import org.doubango.ngn.utils.NgnListUtils;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.ngn.utils.NgnPredicate;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;
import org.doubango.tinyWRAP.ActionConfig;
import org.doubango.tinyWRAP.CallSession;
import org.doubango.tinyWRAP.Codec;
import org.doubango.tinyWRAP.MediaSessionMgr;
import org.doubango.tinyWRAP.ProxyPlugin;
import org.doubango.tinyWRAP.SipMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.T140Callback;
import org.doubango.tinyWRAP.T140CallbackData;
import org.doubango.tinyWRAP.tmedia_bandwidth_level_t;
import org.doubango.tinyWRAP.tmedia_qos_strength_t;
import org.doubango.tinyWRAP.tmedia_qos_stype_t;
import org.doubango.tinyWRAP.tmedia_t140_data_type_t;
import org.doubango.tinyWRAP.twrap_media_type_t;
import org.doubango.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Audio/Video call session
 */
public class NgnAVSession extends NgnInviteSession{
	private static final String TAG = Utils.getTAG(NgnAVSession.class.getCanonicalName());

	private NgnT140Callback mT140Callback;
	private CallSession mSession;
	private boolean mConsumersAndProducersInitialzed;
	private NgnProxyVideoConsumer mVideoConsumer;
	private NgnProxyAudioConsumer mAudioConsumer;
	private NgnProxyVideoProducer mVideoProducer;
	private NgnProxyAudioProducer mAudioProducer;
	private Context mContext;
	private boolean isInitSendRTCPreportInMCPTT=false;
	private static final int MCPTT_TIME_MS_SEND_RTCP_REPORT_PERIOD=5000;
	private static final int MCPTT_TIME_NUM_RTCP_REPORT_INIT=3;
	private int numPeriodSendRTCPReport=0;

	private static final short VALUE_SHORT_DEFAULT=-2;

	private final NgnHistoryAVCallEvent mHistoryEvent;
	private final INgnConfigurationService mConfigurationService;
	public static final String MCPTT_EVENT_FOR_LOCATION_ACTION=TAG +".MCPTT_EVENT_FOR_LOCATION_ACTION";
	public static final String MCPTT_EVENT_FOR_LOCATION_TYPE=TAG +".MCPTT_EVENT_FOR_LOCATION_TYPE";


	public enum EmergencyCallType {
		MCPTT_Q("mcpttq"),
		MCPTT_P("mcpttp"),
		NONE("none")
		;

		private final String text;

		/**
		 * @param text
		 */
		private EmergencyCallType(final String text) {
			this.text = text;
		}

		/* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
		@Override
		public String toString() {
			return text;
		}
	}

	private boolean mSendingVideo;
	private boolean mMuteOn;
	private boolean mSpeakerOn;
	
    private final static NgnObservableHashMap<Long, NgnAVSession> sSessions = new NgnObservableHashMap<Long, NgnAVSession>(true);



	protected PTTState mPTTState;
	protected String mPTTTalkingUser;
	protected short mPTTParticipants;
	protected Date mPTTStopTime;
	private MyMcpttCallback mMcpttCallback;
	private MyMcpttMbmsCallback mMcpttMbmsCallback;
	//MCPTT parameters
	private OnEventMCPTTListener mOnEventMCPTTListener;
	private BroadcastReceiver mSipBroadCastRecvMCPTT;
	private BroadcastReceiver mBroadCastRecvMCPTTMbms;
	private String takingUserMCPTT;
	private short grantedTimeSecMCPTT=-1;
	private Handler handlerSendRTCPreportPeriody;
	private Runnable sendRTCPDelayRunnable;
	private short lastCodeDenied;
	private String lastPhraseDenied;
	private short lastCodeRevoke;
	private String lastPhraseRevoke;


	public PTTState getmPTTState() {
		return mPTTState;
	}
	public void setmPTTState(PTTState mPTTState) {
		this.mPTTState = mPTTState;
	}

	public enum PTTState
	{
		CALLING,
		TALKING,
		RELEASING,
		DENIED,
		IDLE,
		WAITING,
		REQUESTING,
		REVOKED,
		GRANTED
	}





	public void registerCallMCPTT(Context context){
		if(mContext==null)mContext=context;
		mSipBroadCastRecvMCPTT = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				// Registration Event
				if((NgnMcpttEventArgs.ACTION_MCPTT_EVENT+""+getId()).equals(action)){
					Log.d(TAG,"MCPTT EVENT");
					NgnMcpttEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
					final NgnMcpttEventTypes type;
					if(args == null){
						Log.e(TAG, "Invalid event args");
						return;
					}else{
						Log.d(TAG, "MCPTT event args");
					}
					String phrase=null;
					short code=VALUE_SHORT_DEFAULT;
					switch((type = args.getEventType())){
						case TOKEN_TAKEN:

							Log.d(TAG, "TOKEN_TAKEN");
							takingUserMCPTT=intent.getStringExtra(NgnMcpttEventArgs.EXTRA_USER);
							Log.d(TAG, takingUserMCPTT+" is speaking");
							setmPTTState(NgnAVSession.PTTState.TALKING);
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());

							break;
						case IDLE_CHANNEL:
							Log.d(TAG, "IDLE_CHANNEL");
							setmPTTState(PTTState.IDLE);
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());
							break;
						case TOKEN_DENIED:
							Log.d(TAG, "TOKEN_DENIED");
							setmPTTState(PTTState.DENIED);
							phrase=intent.getStringExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE);
							code=intent.getShortExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE,VALUE_SHORT_DEFAULT);
							if(code!=VALUE_SHORT_DEFAULT){
								setCodeDenied(code);
								setPhraseDenied(phrase);
							}
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());
							break;
						case TOKEN_GRANTED:
							Log.d(TAG, "TOKEN_GRANTED");

							setmPTTState(PTTState.GRANTED);
							grantedTimeSecMCPTT=intent.getShortExtra(NgnMcpttEventArgs.EXTRA_TIME,new Short("-1"));
							Log.d(TAG,"Time Granted:"+grantedTimeSecMCPTT);
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());
							break;
						case TOKEN_RELEASED:
							Log.d(TAG, "TOKEN_RELEASED");
							setmPTTState(PTTState.RELEASING);
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());

							break;
						case TOKEN_REQUESTED:
							Log.d(TAG, "TOKEN_REQUESTED");
							setmPTTState(PTTState.REQUESTING);
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());
							break;
						case TOKEN_REVOKED:
							Log.d(TAG, "TOKEN_REVOKED");
							setmPTTState(PTTState.REVOKED);
							phrase=intent.getStringExtra(NgnMcpttEventArgs.EXTRA_REASON_PHRASE);
							code=intent.getShortExtra(NgnMcpttEventArgs.EXTRA_REASON_CODE,VALUE_SHORT_DEFAULT);
							if(code!=VALUE_SHORT_DEFAULT){
								setCodeRevoke(code);
								setPhraseRevoke(phrase);
							}
							if(mOnEventMCPTTListener!=null)mOnEventMCPTTListener.onEventMCPTT(getmPTTState());
							break;
						default:
							Log.d(TAG, "Invalid event");
							break;
					}


				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnMcpttEventArgs.ACTION_MCPTT_EVENT+""+getId());
		if(mContext!=null){
			Log.d(TAG,"Register MCPTT event");
			mContext.registerReceiver(mSipBroadCastRecvMCPTT, intentFilter);
		}else{
			Log.e(TAG,"Context error");
		}

		//MBMS actions received from MBMS service
		mBroadCastRecvMCPTTMbms=new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				if(action.compareTo(MyMbmsService.MBMS_CALL_ACTION_MANAGER_START) == 0){
					Log.d(TAG,"Starting MBMS manager...");
					String managerIP = intent.getStringExtra(MyMbmsService.MBMS_IP_MANAGER_MBMS);
					int managerPort = intent.getIntExtra(MyMbmsService.MBMS_PORT_MANAGER_MBMS, 0);
					String localIface = intent.getStringExtra(MyMbmsService.MBMS_LOCAL_IFACE);
					int localIfaceIndex = intent.getIntExtra(MyMbmsService.MBMS_LOCAL_IFACE_INDEX, -1);
					Log.d(TAG, "MBMS data: IP=" + managerIP + " Port=" + managerPort + " Interface=" + localIface + " Index=" + localIfaceIndex);
					if(!startMbmsManager(managerIP, managerPort, localIface, localIfaceIndex)){
						Log.e(TAG,"Error starting MBMS manager");
					}
				}else if(action.compareTo(MyMbmsService.MBMS_CALL_ACTION_MANAGER_STOP) == 0){
					Log.d(TAG,"Stopping MBMS manager...");

					if(!stopMbmsManager()){
						Log.e(TAG,"Error stopping MBMS manager");
					}
				}else if(action.compareTo(MyMbmsService.MBMS_CALL_ACTION_MEDIA_START)==0){
					Log.d(TAG,"Starting MBMS media...");
					String mediaIP = intent.getStringExtra(MyMbmsService.MBMS_IP_MEDIA_MBMS);
					int mediaPort = intent.getIntExtra(MyMbmsService.MBMS_PORT_MEDIA_MBMS, 0);
					int mediaControlPort = intent.getIntExtra(MyMbmsService.MBMS_PORT_CONTROL_MEDIA_MBMS, 0);
					if(!startMbmsMedia(mediaIP, mediaPort, mediaControlPort)){
						Log.e(TAG,"Error starting MBMS media");
					}
				}
			}
		};
		final IntentFilter intentFilter3 = new IntentFilter();
		intentFilter3.addAction(MyMbmsService.MBMS_CALL_ACTION_MANAGER_START);
		intentFilter3.addAction(MyMbmsService.MBMS_CALL_ACTION_MANAGER_STOP);
		intentFilter3.addAction(MyMbmsService.MBMS_CALL_ACTION_MEDIA_START);
		if(mContext!=null){
			Log.d(TAG,"Register MCPTT MBMS event");
			mContext.registerReceiver(mBroadCastRecvMCPTTMbms, intentFilter3);
		}else{
			Log.e(TAG,"Context error");
		}
	}

	public void unconfigureCallMCPTT(){
		// release the listeners
		if (mSipBroadCastRecvMCPTT != null) {
			Log.d(TAG, "unregisterReceiver:MCPTT");
			if(mContext!=null)
			mContext.unregisterReceiver(mSipBroadCastRecvMCPTT);
			mSipBroadCastRecvMCPTT = null;
		}
		if (mBroadCastRecvMCPTTMbms != null) {
			Log.d(TAG, "unregisterReceiver: MBMS MCPTT");
			if(mContext!=null)
				mContext.unregisterReceiver(mBroadCastRecvMCPTTMbms);
			mBroadCastRecvMCPTTMbms = null;
		}
	}


	public interface  OnEventMCPTTListener{
		public void onEventMCPTT(PTTState mPTTState);
	}

	public void setOnEventMCPTTListener(OnEventMCPTTListener mOnEventMCPTTListener){
		this.mOnEventMCPTTListener=mOnEventMCPTTListener;
	}



	public String getTakingUserMCPTT() {
		return takingUserMCPTT;
	}

	private void setTakingUserMCPTT(String takingUserMCPTT) {
		this.takingUserMCPTT = takingUserMCPTT;
	}


	public short getCodeDenied() {
		return lastCodeDenied;
	}

	private void setCodeDenied(short lastCodeDenied) {
		this.lastCodeDenied = lastCodeDenied;
	}

	public String getPhraseDenied() {
		return lastPhraseDenied;
	}

	private void setPhraseDenied(String lastPhraseDenied) {
		this.lastPhraseDenied = lastPhraseDenied;
	}

	public short getCodeRevoke() {
		return lastCodeRevoke;
	}

	private void setCodeRevoke(short lastCodeRevoke) {
		this.lastCodeRevoke = lastCodeRevoke;
	}

	public String getPhraseRevoke() {
		return lastPhraseRevoke;
	}

	private void setPhraseRevoke(String lastPhraseRevoke) {
		this.lastPhraseRevoke = lastPhraseRevoke;
	}

	public short getGrantedTimeSecMCPTT() {
		return grantedTimeSecMCPTT;
	}


	private void sendMCPTTEventForLocation(TypeMcpttSignallingEvent typeMcpttSignallingEvent){
		if(typeMcpttSignallingEvent==null)return;
		Intent intent=new Intent();
		intent.setAction(MCPTT_EVENT_FOR_LOCATION_ACTION);
		intent.putExtra(MCPTT_EVENT_FOR_LOCATION_TYPE,typeMcpttSignallingEvent);
		getContext().sendBroadcast(intent);
	}
	//MCPTT End


	public boolean requestMCPTTToken()
	{
		if(getMediaType()==null){
			Log.e(TAG,"Unable to perform the token request: one of the values is null");
		}else if ((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) != NgnMediaType.SessionMCPTT.getValue()) {
			Log.e(TAG,"Unable to perform the token request: call is not MCPTT type");
			return false;
		}else{
			Log.d(TAG,"Call is PoC type and token request process starts");
		}
		ActionConfig config=new ActionConfig();
		//
		mPTTState = PTTState.REQUESTING;

		Log.d(TAG, "Call status changed to order, and token request processed");
		boolean ret = mSession.requestMcpttToken(config);
		if(ret){

			Log.d(TAG,"(MCPTT) token request process made. Proper activation");
		}else{
			Log.e(TAG, "Error sending (MCPTT) token request");
		}
		return ret;
	}

	public boolean releaseMCPTTToken()
	{
		if(getMediaType()==null){
			Log.e(TAG,"Unable to perform the token Release: one of the values is null");
		}

		else if ((getMediaType().getValue() & NgnMediaType.WithFloorControl.getValue()) != NgnMediaType.WithFloorControl.getValue()) {
			if(BuildConfig.DEBUG)Log.w(TAG,"Unable to perform the token Release: This Call does not have floor control");
			return false;
		}

		else if ((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) != NgnMediaType.SessionMCPTT.getValue()) {
			if(BuildConfig.DEBUG)Log.w(TAG,"Unable to perform the token Release: call is not a MCPTT type");
			return false;
		}else{
			Log.d(TAG,"The call is PoC type and token release process starts");
		}
		ActionConfig config =
				new ActionConfig();
		mPTTState = PTTState.RELEASING;

		boolean ret = mSession.releaseMcpttToken(config);
		if(ret){
			Log.d(TAG, "Token release process made. Proper activation");
		}else{
			Log.e(TAG, "Error sending token release");
		}
		return ret;
	}

	public boolean startMbmsManager(String managerIP, int managerPort, String localIface, int localIfaceIndex) {
		ActionConfig config = new ActionConfig();
		boolean ret=false;
		if(mSession!=null){
			ret= mSession.startMbmsManager(config, managerIP, managerPort, localIface, localIfaceIndex);
			if(ret){
				Log.d(TAG, "MBMS manager properly activated");
			}else{
				Log.e(TAG, "Error activating MBMS manager");
			}
		}else{
			Log.e(TAG,"It is not possible to initiate MBMS already that the session is null");
		}
		return ret;
	}

	public boolean stopMbmsManager() {
		ActionConfig config = new ActionConfig();
		boolean ret = mSession.stopMbmsManager(config);
		if(ret){
			Log.d(TAG, "MBMS manager properly deactivated");
		}else{
			Log.e(TAG, "Error deactivating MBMS manager");
		}
		return ret;
	}

	public boolean startMbmsMedia(String mediaIP, int mediaPort, int mediaControlPort) {
		ActionConfig config = new ActionConfig();
		boolean ret = mSession.startMbmsMedia(config, mediaIP, mediaPort, mediaControlPort);
		if(ret){
			Log.d(TAG, "MBMS media properly started");
		}else{
			Log.e(TAG, "Error starting MBMS media");
		}
		return ret;
	}
	@Override
	public String getRemotePartyUri(){
		String result=null;
		if(((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) == NgnMediaType.SessionMCPTT.getValue())
				){
			if(mSession!=null)
			result= mSession.getSipPartyUri();
		}else{
			result=super.getRemotePartyUri();
		}
		return (result==null)?result:result.replace("<","").replace(">","");
	}

	public String getPTTMcpttGroupIdentity(){
		String result=null;
		if(((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) == NgnMediaType.SessionMCPTT.getValue()) ){
			if(mSession!=null)
				result= mSession.getPTTMcpttGroupIdentity();
		}else{
			result=null;
		}
		return (result==null)?result:result.replace("<","").replace(">","");
	}

	@Override
	public String getRemotePartyDisplayName(){
		if((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) == NgnMediaType.SessionMCPTT.getValue() ){
			return mSession.getSipPartyUri();
		}

		return super.getRemotePartyDisplayName();
	}

    public static NgnAVSession takeIncomingSession(NgnSipStack sipStack, CallSession session, twrap_media_type_t mediaType, SipMessage sipMessage){
        NgnMediaType media = NgnMediaType.ConvertFromNative(mediaType);
        if(media == NgnMediaType.None){
        	Log.e(TAG, "Invalid media type");
        	return null;
        }
        synchronized (sSessions){
            NgnAVSession avSession = new NgnAVSession(sipStack, session, media, InviteState.INCOMING);
            if (sipMessage != null){
                avSession.setRemotePartyUri(sipMessage.getSipHeaderValue("f"));
            }
            sSessions.put(avSession.getId(), avSession);
            return avSession;
        }
    }
    
    public static boolean handleMediaUpdate(long id, twrap_media_type_t newMediaType){
    	NgnAVSession avSession = NgnAVSession.getSession(id);
        if (avSession != null){
        	NgnMediaType _newMediaType = NgnMediaType.ConvertFromNative(newMediaType);
        	if(_newMediaType != NgnMediaType.None){
        		avSession.setMediaType(_newMediaType); // mediaType must be updated here because it's used by initializeConsumersAndProducers();
        	}
        	avSession.mConsumersAndProducersInitialzed = false;
        	return avSession.initializeConsumersAndProducers();
        }
        
        return false;
    }

    /**
     * Creates an outgoing audio/video call session.
     * @param sipStack the IMS/SIP stack to use to make the call
     * @param mediaType the media type. 
     * @return an audio/video session
     * @sa @ref makeAudioCall() @ref makeAudioVideoCall()
     */
    public static NgnAVSession createOutgoingSession(NgnSipStack sipStack, NgnMediaType mediaType){
    	if(BuildConfig.DEBUG)Log.d(TAG,"createOutgoingSession type:"+mediaType.getValue());
        synchronized (sSessions){
            final NgnAVSession avSession = new NgnAVSession(sipStack, null, mediaType, InviteState.INPROGRESS);
            sSessions.put(avSession.getId(), avSession);
            return avSession;
        }
    }
	
	public static void releaseSession(NgnAVSession session){
		synchronized (sSessions){
            if (session != null && sSessions.containsKey(session.getId())){
                long id = session.getId();
                session.decRef();
                sSessions.remove(id);
            }
        }
    }

	public static NgnObservableHashMap<Long, NgnAVSession> getSessions(){
		return sSessions;
	}

	/**
	 * Retrieves an audio/video session by id.
	 * @param id the id of the audio/video session to retrieve
	 * @return an audio/video session with the specified id if exist and null otherwise
	 */
	public static NgnAVSession getSession(long id) {
		synchronized (sSessions) {
			if (sSessions.containsKey(id))
				return sSessions.get(id);
			else
				return null;
		}
	}
	
	public static NgnAVSession getSession(NgnPredicate<NgnAVSession> predicate) {
		synchronized (sSessions) {
			return NgnListUtils.getFirstOrDefault(sSessions.values(), predicate);
		}
	}

	/**
	 * Gets the number of pending audio/video sessions. These sessions could be active or not.
	 * @return the number of pending audio/video sessions.
	 * @sa @ref hasActiveSession()
	 */
	public static int getSize(){
        synchronized (sSessions){
            return sSessions.size();
        }
    }
	
	public static int getSize(NgnPredicate<NgnAVSession> predicate) {
		synchronized (sSessions) {
			return NgnListUtils.filter(sSessions.values(), predicate).size();
		}
	}
	
	/**
	 * Checks whether we already have an audio/video session with the specified id.
	 * @param id the id of the session to look for
	 * @return true if exist and false otherwise
	 */
    public static boolean hasSession(long id){
        synchronized (sSessions){
            return sSessions.containsKey(id);
        }
    }
    
    /**
     * Check whether we have at least one active audio/video session.
     * @return true if exist and false otherwise
     */
    public static boolean hasActiveSession(){
    	synchronized (sSessions){
    		final Collection<NgnAVSession> mysessions = sSessions.values();
	    	for(NgnAVSession session : mysessions){
	    		if(session.isActive()){
	    			return true;
	    		}
	    	}
    	}
    	return false;
    }
    
    /**
     * Gets the first active audio/video session with an id different than the one specified
     * as parameter
     * @param id the id of the session to exclude from the search
     * @return an audio/video session matching the criteria or null if no one exist
     */
    public static NgnAVSession getFirstActiveCallAndNot(long id){
		NgnAVSession session;
		for(Map.Entry<Long, NgnAVSession> entry : sSessions.entrySet()) {
			session = entry.getValue();
			if(session.getId() != id && session.isActive() && !session.isLocalHeld() && !session.isRemoteHeld()){
				return session;
			}
		}
		return null;
	}






    /**
     * Starts video sharing session
     * @param remoteUri  the remote party uri. Could be a SIP/TEL uri, nomadic number, MSISDN number, ...
     * example: sip:test@doubango.org, tel:+33600000000, 78888667, ...
	 * @return true if the call succeed and false otherwise
     */
    public boolean makeVideoSharingCall(String remoteUri){
        boolean ret;

        super.mOutgoing = true;

        ActionConfig config = new ActionConfig();
        ret = mSession.callVideo(remoteUri, config);
        config.delete();

        return ret;
    }
    
    protected NgnAVSession(NgnSipStack sipStack, CallSession session, NgnMediaType mediaType, InviteState callState){
		super(sipStack);
		if(BuildConfig.DEBUG)Log.d(TAG,"NgnAVSession");

		mSession = (session == null) ? new CallSession(sipStack) : session;
		if(BuildConfig.DEBUG)Log.d(TAG,"Created new session: "+(mSession!=null?mSession.getId():-1));

		setMediaType(mediaType);

	    mConfigurationService = NgnEngine.getInstance().getConfigurationService();
	    
	    mSendingVideo = mConfigurationService.getBoolean(
	    		NgnConfigurationEntry.GENERAL_AUTOSTART_VIDEO,
				NgnConfigurationEntry.DEFAULT_GENERAL_AUTOSTART_VIDEO);

	    super.init();
	    // SigComp
	    super.setSigCompId(sipStack.getSigCompId());
	    // 100rel
	    // mSession.set100rel(true); // will add "Supported: 100rel"   => Use defaults     
        // Session timers
        if(mConfigurationService.getBoolean(NgnConfigurationEntry.QOS_USE_SESSION_TIMERS, NgnConfigurationEntry.DEFAULT_QOS_USE_SESSION_TIMERS)){
			mSession.setSessionTimer((long) mConfigurationService.getInt(
					NgnConfigurationEntry.QOS_SIP_CALLS_TIMEOUT,
					NgnConfigurationEntry.DEFAULT_QOS_SIP_CALLS_TIMEOUT),
					mConfigurationService.getString(NgnConfigurationEntry.QOS_REFRESHER,
							NgnConfigurationEntry.DEFAULT_QOS_REFRESHER));
        }
        // Precondition
		mSession.setQoS(tmedia_qos_stype_t.valueOf(mConfigurationService
				.getString(NgnConfigurationEntry.QOS_PRECOND_TYPE,
						NgnConfigurationEntry.DEFAULT_QOS_PRECOND_TYPE)),
				tmedia_qos_strength_t.valueOf(mConfigurationService.getString(NgnConfigurationEntry.QOS_PRECOND_STRENGTH,
						NgnConfigurationEntry.DEFAULT_QOS_PRECOND_STRENGTH)));

		// T.140 callback
		if(NgnMediaType.isT140Type(getMediaType())){
			mT140Callback = new NgnT140Callback(this);
		}
		
	    /* 3GPP TS 24.173
	        *
	        * 5.1 IMS communication service identifier
	        * URN used to define the ICSI for the IMS Multimedia Telephony Communication Service: urn:urn-7:3gpp-service.ims.icsi.mmtel. 
	        * The URN is registered at http://www.3gpp.com/Uniform-Resource-Name-URN-list.html.
	        * Summary of the URN: This URN indicates that the device supports the IMS Multimedia Telephony Communication Service.
	        *
	        * Contact: <sip:impu@doubango.org;gr=urn:uuid:xxx;comp=sigcomp>;+g.3gpp.icsi-ref="urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel"
	        * Accept-Contact: *;+g.3gpp.icsi-ref="urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel"
	        * P-Preferred-Service: urn:urn-7:3gpp-service.ims.icsi.mmtel
	        */
	    //super.addCaps("+g.3gpp.icsi-ref", "\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mmtel\"");
	    //super.addHeader("Accept-Contact", "");
	    //super.addHeader("P-Preferred-Service", "urn:urn-7:3gpp-service.ims.icsi.mmtel");
	    
	    mHistoryEvent = new NgnHistoryAVCallEvent((mediaType == NgnMediaType.AudioVideo || mediaType == NgnMediaType.Video), null);
	    this.setState(callState);


	}
    
	@Override
	protected SipSession getSession() {
		return mSession;
	}
	
	@Override
	protected  NgnHistoryEvent getHistoryEvent(){
		 return mHistoryEvent;
	}
	
	private boolean initializeConsumersAndProducers(){
		Log.d(TAG, "initializeConsumersAndProducers()");
		if(mConsumersAndProducersInitialzed){
			return true;
		}
		
		final MediaSessionMgr mediaMgr;
		if((mediaMgr = super.getMediaSessionMgr()) != null){
			ProxyPlugin plugin;
			NgnProxyPlugin myProxyPlugin;
			// Video
			if(NgnMediaType.isVideoType(super.getMediaType())){
				if(BuildConfig.DEBUG)Log.w(TAG,"Now it is using video");
				if((plugin = mediaMgr.findProxyPluginConsumer(twrap_media_type_t.twrap_media_video)) != null){
					if((myProxyPlugin = NgnProxyPluginMgr.findPlugin(plugin.getId())) != null){
						mVideoConsumer = (NgnProxyVideoConsumer)myProxyPlugin;
						mVideoConsumer.setContext(mContext);
						mVideoConsumer.setSipSessionId(super.getId());
					}else{
						if(BuildConfig.DEBUG)Log.w(TAG,"no Consumer Find myProxyPlugin");
					}
				}else{
					if(BuildConfig.DEBUG)Log.w(TAG,"NO Consumer Find twrap_media_video");
				}
				if((plugin = mediaMgr.findProxyPluginProducer(twrap_media_type_t.twrap_media_video)) != null){
					if((myProxyPlugin = NgnProxyPluginMgr.findPlugin(plugin.getId())) != null){
						mVideoProducer = (NgnProxyVideoProducer)myProxyPlugin;
						mVideoProducer.setContext(mContext);
						mVideoProducer.setSipSessionId(super.getId());
					}else{
						if(BuildConfig.DEBUG)Log.w(TAG,"no Producer Find myProxyPlugin");
					}
				}else{
					if(BuildConfig.DEBUG)Log.w(TAG,"NO Producer Find twrap_media_video");
				}
			}else{
				if(BuildConfig.DEBUG)Log.w(TAG,"Now it do not use video");
			}
			// Audio
			if(NgnMediaType.isAudioType(getMediaType())){
				if(BuildConfig.DEBUG)Log.w(TAG,"Now it is using audio");

				if((plugin = mediaMgr.findProxyPluginConsumer(twrap_media_type_t.twrap_media_audio)) != null){
					if((myProxyPlugin = NgnProxyPluginMgr.findPlugin(plugin.getId())) != null){
						mAudioConsumer = (NgnProxyAudioConsumer)myProxyPlugin;
						mAudioConsumer.setSipSessionId(super.getId());
					}
				}
				if((plugin = mediaMgr.findProxyPluginProducer(twrap_media_type_t.twrap_media_audio)) != null){
					if((myProxyPlugin = NgnProxyPluginMgr.findPlugin(plugin.getId())) != null){
						mAudioProducer = (NgnProxyAudioProducer)myProxyPlugin;
						mAudioProducer.setSipSessionId(super.getId());
					}
				}
			}else{
				if(BuildConfig.DEBUG)Log.w(TAG,"Now it do not use audio");
			}
			
			mConsumersAndProducersInitialzed = true;
			return true;
		}else{
			if(BuildConfig.DEBUG)Log.e(TAG,"Error in getMediaSessionMgr");
		}
		
		return false;	
	}
	
	private void deInitializeMediaSession(){
		//If MBMS is active, this service will be stopped;
		NgnEngine.getInstance().getMbmsService().stopServiceMbms();
		if(super.mMediaSessionMgr != null){
			super.mMediaSessionMgr.delete();
			super.mMediaSessionMgr = null;
		}
	}
	
	private void updateEchoTail(){
		if(mConfigurationService.getBoolean(NgnConfigurationEntry.GENERAL_USE_ECHO_TAIL_ADAPTIVE,
				NgnConfigurationEntry.DEFAULT_GENERAL_USE_ECHO_TAIL_ADAPTIVE)){
			Log.d(TAG, "Setting new echo tail");
			final MediaSessionMgr mediaMgr;
			if((mediaMgr = super.getMediaSessionMgr()) != null){
				final Codec codec = mediaMgr.producerGetCodec(twrap_media_type_t.twrap_media_audio);
				if(codec == null){
					Log.e(TAG, "Failed to get producer codec");
					return;
				}
				final int samplingRate = codec.getAudioSamplingRate();
				final int channels = codec.getAudioChannels();
				if(samplingRate <= 0){
					Log.e(TAG, samplingRate + " not valid as audio sampling rate");
					return;
				}
				if(channels != 1 && channels != 2){
					Log.e(TAG, channels + " not valid as audio channels value");
					return;
				}
				final int minBufferSize = AudioRecord.getMinBufferSize(samplingRate, channels == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
				Log.d(TAG, "getMinBufferSize("+samplingRate+ ","+channels+")="+minBufferSize);
				final int echoTail1 = (((1000 * minBufferSize) / samplingRate) << 1);
				final int echoTail2 = echoTail1 < 200 ? 200 : echoTail1; // make it more reasonable
				Log.d(TAG, "Echo tail ("+echoTail1+"->"+echoTail2+")");
				mediaMgr.sessionSetInt32(twrap_media_type_t.twrap_media_audio, "echo-tail", echoTail2);
			}
		}
	}
	
	/**
	 * Gets the context associated to this session. Only used for video session to track the SurfaceView
	 * lifecycle
	 * @return the context
	 */
	public Context getContext(){
		return mContext;
	}
	
	/**
	 * Sets a context to associated to this session
	 * @param context the context
	 */
	public void setContext(Context context){
		mContext = context;
	}
	
	/**
	 * Starts the video consumer. A video consumer view used to display the video stream
	 * sent from the remote party. It's up to you to embed this view into a layout (LinearLayout, RelativeLayou,
	 * FrameLayout, ...) in order to display it.
	 * @return the view where the remote video stream will be displayed
	 */
	public final View startVideoConsumerPreview(){
		if(BuildConfig.DEBUG)Log.d(TAG,"startVideoConsumerPreview");
		if(mVideoConsumer != null){
			return mVideoConsumer.startPreview(mContext);
		}else{
			Log.e(TAG,"Error in Video Consumer ");
		}
		return null;
	}
	
	/**
	 * Starts the video producer. A video producer is any device capable to generate video frames.
	 * It's likely a video camera (front facing or rear). The view associated to the producer is used as a feedback to
	 * show the local video stream sent to the remote party.
	 * It's up to you to embed this view into a layout (LinearLayout, RelativeLayou,
	 * FrameLayout, ...) in order to display it.
	 * @return the view where the local video stream will be displayed
	 */
	public final View startVideoProducerPreview(Context context){
		mContext = context == null ? mContext : context;
		if(BuildConfig.DEBUG)Log.w(TAG,"startVideoProducerPreview");
		if(mVideoProducer != null){
			return mVideoProducer.startPreview(mContext);
		}else{
			if(BuildConfig.DEBUG)Log.w(TAG,"it is not possible startPreview Video produce");
			initializeConsumersAndProducers();
			return mVideoProducer.startPreview(mContext);
		}
		//return null;
	}


	
	/**
	 * Checks whether we are sending video or not
	 * @return true if we are already sending video and false otherwise
	 */
	public boolean isSendingVideo(){
		return mSendingVideo;
	}
	
	public void setSendingVideo(boolean sendingVideo){
		mSendingVideo = sendingVideo;
	}
	
	public void pushBlankPacket(){
		if(mVideoProducer != null){
			mVideoProducer.pushBlankPacket();
		}
	}
	
	/**
	 * Switch from rear to front-facing camera or vice-versa
	 */
	public void toggleCamera(){
		if(mVideoProducer != null){
			mVideoProducer.toggleCamera();
		}
	}
	
	public boolean isFrontFacingCameraEnabled() {
		if(mVideoProducer != null){
			return mVideoProducer.isFrontFacingCameraEnabled();
		}
		return false ;
	}
	
	public int compensCamRotation(boolean preview){
		if(mVideoProducer != null){
			return mVideoProducer.compensCamRotation(preview);
		}
		return 0;
	}
	
	public int camRotation(boolean preview){
		if(mVideoProducer != null){
			return mVideoProducer.getNativeCameraHardRotation(preview);
		}
		return 0;
	}
	
	/**
	 * Sets the local video rotation angle
	 * @param rot rotation angle in degrees
	 */
	public void setRotation(int rot){
		if (mVideoProducer != null) {
			mVideoProducer.setRotation(rot);
		}
	}
	
	/**
	 * Sets whether to mirror the outgoing video
	 * @param mirror
	 */
	public void setMirror(boolean mirror){
		if(mVideoProducer != null){
			mVideoProducer.setMirror(mirror);
		}
	}
	
	public boolean setProducerFlipped(boolean flipped){
		final MediaSessionMgr mediaMgr;
		if((mediaMgr = super.getMediaSessionMgr()) != null){
			return mediaMgr.producerSetInt32(twrap_media_type_t.twrap_media_video, "flip", flipped ? 1 : 0);
		}
		return false;
	}
	
	public boolean setConsumerFlipped(boolean flipped){
		final MediaSessionMgr mediaMgr;
		if((mediaMgr = super.getMediaSessionMgr()) != null){
			return mediaMgr.consumerSetInt32(twrap_media_type_t.twrap_media_video, "flip", flipped ? 1 : 0);
		}
		return false;
	}
	
	public boolean isSecure(){
		final MediaSessionMgr mediaMgr;
		if((mediaMgr = super.getMediaSessionMgr()) != null){
			return (mediaMgr.sessionGetInt32(twrap_media_type_t.twrap_media_audiovideo, "srtp-enabled") != 0);
		}
		return false;
	}
	
	// Doubango AEC for THIS session. Default value (for all sessions) is the one in "NgnConfigurationEntry.GENERAL_AEC" configuration entry.
	public boolean setAECEnabled(boolean enabled){
		final MediaSessionMgr mediaMgr;
		if((mediaMgr = super.getMediaSessionMgr()) != null){
			return mediaMgr.sessionSetInt32(twrap_media_type_t.twrap_media_audio, "echo-supp", enabled ? 1 : 0);
		}
		return false;
	}
	
	public boolean setVideoFps(int fps) {
		if (mSession != null) {
			return mSession.setVideoFps(fps);
		}
		return false;
	}
	
	public boolean setVideoBandwidthUploadMax(int bw_max_kbps) {
		if (mSession != null) {
			return mSession.setVideoBandwidthUploadMax(bw_max_kbps);
		}
		return false;
	}
	
	public boolean setVideoBandwidthDownloadMax(int bw_max_kbps) {
		if (mSession != null) {
			return mSession.setVideoBandwidthDownloadMax(bw_max_kbps);
		}
		return false;
	}

	/**
	 * Enables or disables the speakerphone
	 * @param speakerOn true to enable the speakerphone and false to disable it
	 */
	public void setSpeakerphoneOn(boolean speakerOn){
		if(NgnApplication.isSLEs2KnownToWork()){
			final MediaSessionMgr mediaMgr;
			if((mediaMgr = super.getMediaSessionMgr()) != null){
				if(mediaMgr.consumerSetInt32(twrap_media_type_t.twrap_media_audio, "speaker-on", speakerOn ? 1 : 0)){
					mSpeakerOn = speakerOn;
				}
			}
		}
		else{
			if(mAudioProducer != null){
				mAudioProducer.setSpeakerphoneOn(speakerOn);
			}
			if(mAudioConsumer != null){
				mAudioConsumer.setSpeakerphoneOn(speakerOn);
			}
			mSpeakerOn = speakerOn;
		}
	}
	
	/**
	 * Toggles the speakerphone. Enable if disabled and vice-versa
	 */
	public void toggleSpeakerphone(){
		setSpeakerphoneOn(!mSpeakerOn);
	}
	
	public boolean isSpeakerOn(){
		if(!NgnApplication.isSLEs2KnownToWork()){
			if(mAudioProducer != null){
				return mAudioProducer.isSpeakerOn();
			}
		}
		return mSpeakerOn;
	}
	
	public boolean isMicrophoneMute() {
		if(NgnApplication.isSLEs2KnownToWork()){
			return mMuteOn;
		}
		else{
			if(mAudioProducer != null){
				return mAudioProducer.isOnMute();
			}
			return mMuteOn;
		}
	}
	
	public void setMicrophoneMute(boolean mute) {
		if(BuildConfig.DEBUG)Log.d(TAG,"Execute setMicrophoneMute:"+mute);
		if(NgnApplication.isSLEs2KnownToWork()){
			final MediaSessionMgr mediaMgr;
			if((mediaMgr = super.getMediaSessionMgr()) != null){
				if(mediaMgr.producerSetInt32(twrap_media_type_t.twrap_media_audio, "mute", mute ? 1 : 0)){
					mMuteOn =  mute;
				}
			}
		}
		else{
		    if(mAudioProducer != null){
		        mAudioProducer.setOnMute(mute);
		        mMuteOn = mAudioProducer.isOnMute();
		    }
		}
	}



	
	public boolean onVolumeChanged(boolean bDown){
		if(!NgnApplication.isSLEs2KnownToWork()){
			if(mAudioProducer == null || !mAudioProducer.onVolumeChanged(bDown)){
				return false;
			}
			if(mAudioConsumer == null || !mAudioConsumer.onVolumeChanged(bDown)){
				return false;
			}
		}
		return false;
	}
	
	public void setMode(InviteState state){
		if(NgnApplication.isSetModeAllowed()){
			final AudioManager audiomanager = NgnApplication.getAudioManager();
			if(audiomanager != null){
				Log.d(TAG, "setMode("+state+")");
				switch(state){
					case INCOMING:
					case INPROGRESS:
					case REMOTE_RINGING:
						audiomanager.setMode(AudioManager.MODE_RINGTONE);
						break;
					case INCALL:
					case EARLY_MEDIA:
						audiomanager.setMode(NgnApplication.getSDKVersion() >= 11 ? AudioManager.MODE_IN_COMMUNICATION : AudioManager.MODE_IN_CALL);
						break;
					case TERMINATED:
					case TERMINATING:
						audiomanager.setMode(AudioManager.MODE_NORMAL);
						break;
					default: break;
				}
			}
		}
	}
	
	@Override
	public void setState(InviteState state){
		if(super.mState == state){
			return;
		}
		Log.d(TAG, "setState("+state+")");
		super.setState(state);
		setMode(state);
		
		switch(state){
			case INCOMING:
				initializeConsumersAndProducers();
				break;
				
			case INPROGRESS:
				initializeConsumersAndProducers();
				break;
				
			case INCALL:
			case EARLY_MEDIA:
				initializeConsumersAndProducers();
				updateEchoTail();
				mSession.setT140Callback(mT140Callback);
				break;
			
			case TERMINATED:
			case TERMINATING:
				deInitializeMediaSession();
				mSession.setT140Callback(null);
				break;
			default:
				break;
		}
		
		super.setChangedAndNotifyObservers(this);
    }
	
	public long getStartTime(){
		return mHistoryEvent.getStartTime();
	}
	
	public int getVideoWidthNegotiated() {
		return (mVideoConsumer != null) ? mVideoConsumer.getVideoWidthNegotiated() : 0;
	}
	
	public int getVideoHeightNegotiated() {
		return (mVideoConsumer != null) ? mVideoConsumer.getVideoHeightNegotiated() : 0;
	}
	
	public int getVideoWidthReceived() {
		return (mVideoConsumer != null) ? mVideoConsumer.getVideoWidthReceived() : 0;
	}
	
	public int getVideoHeightReceived() {
		return (mVideoConsumer != null) ? mVideoConsumer.getVideoHeightReceived() : 0;
	}
	
	/**
	 * Accepts an incoming audio/video call
	 * @return true is succeed and false otherwise
	 * @sa @ref hangUpCall()
	 */
	public boolean acceptCall(){

        return super.isActive() ? mSession.accept() : false;

    }


    public void registerCallBacksMCPTT(Context context){
		if((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) == NgnMediaType.SessionMCPTT.getValue())
		{
			registerCallMCPTT(context);
			Log.d(TAG,"MCPTT call accepted");
			if (mMcpttCallback == null)
				mMcpttCallback = new MyMcpttCallback(this);
			mSession.setMcpttCallback(mMcpttCallback);
			//MBMS control messages callback
			if (mMcpttMbmsCallback == null)
				mMcpttMbmsCallback = new MyMcpttMbmsCallback(this);
			mSession.setMcpttMbmsCallback(mMcpttMbmsCallback);
			//MCPTT event for Location
			if((getMediaType().getValue() & NgnMediaType.SessionAudioGroupMCPTT.getValue())==NgnMediaType.SessionAudioGroupMCPTT.getValue()){
				if(BuildConfig.DEBUG)Log.d(TAG,"SessionAudioGroupMCPTT");
				sendMCPTTEventForLocation(TypeMcpttSignallingEvent.GROUP_CALL_NON_EMERGENCY);
			}else 	if((getMediaType().getValue() & NgnMediaType.SessionAudioMCPTT.getValue())==NgnMediaType.SessionAudioMCPTT.getValue()){
				if(BuildConfig.DEBUG)Log.d(TAG,"SessionAudioMCPTT");
				sendMCPTTEventForLocation(TypeMcpttSignallingEvent.PRIVATE_CALL_NON_EMERGENCY);
			}
		}else{
			Log.d(TAG,"Not MCPTT session");
		}
	}

    private void registerCallBacks(Context context){
			if ((getMediaType().getValue() & NgnMediaType.SessionMCPTT.getValue()) == NgnMediaType.SessionMCPTT.getValue())
		{
			registerCallMCPTT(context);
			Log.d(TAG,"MCPTT call accepted");
			if (mMcpttCallback == null)
				mMcpttCallback = new MyMcpttCallback(this);
			mSession.setMcpttCallback(mMcpttCallback);
		}
	}

	public boolean acceptCallMCPTT(Context context) {
		Log.d(TAG,"MCPTT call accepted");
		registerCallBacksMCPTT(context);
		return mSession.accept(null);
	}





	//MCPTT private
	public boolean makeCallMCPTT(String remoteUri
			,Context context
			,boolean answerMode
								 ){
		return makeCallMCPTT(remoteUri,context
				,answerMode
				,(String)null,-1);
	}
	public boolean makeCallMCPTT(String remoteUri,Context context
								,boolean answerMode
								 ,EmergencyCallType emergencyCallType, int levelEmergency

			){
		return makeCallMCPTT( remoteUri, context
				,answerMode
				,emergencyCallType.toString(),  levelEmergency);
	}
	public boolean makeCallMCPTT(String remoteUri,Context context
			,boolean answerMode
			,String emergencyType, int levelEmergency
	)
	{
		Log.d(TAG,"makeCallMCPTT");
		super.mOutgoing = true;
		super.setToUri(remoteUri);
		boolean ret=false;
		ActionConfig config =new ActionConfig();
		int level = mConfigurationService.getInt(NgnConfigurationEntry.QOS_PRECOND_BANDWIDTH_LEVEL,
				NgnConfigurationEntry.DEFAULT_QOS_PRECOND_BANDWIDTH_LEVEL);
		tmedia_bandwidth_level_t bl = tmedia_bandwidth_level_t.swigToEnum(level);
		config.setMediaInt(twrap_media_type_t.twrap_media_audiovideo, "bandwidth-level", bl.swigValue());
		if(NgnMediaType.ConvertToNative(getMediaType())==null){
		}else if(((getMediaType().getValue() & NgnMediaType.SessionAudioMCPTT.getValue())==NgnMediaType.SessionAudioMCPTT.getValue())
				){
			registerCallMCPTT(context);
			if((getMediaType().getValue() & NgnMediaType.SessionEmergency.getValue()) == NgnMediaType.SessionEmergency.getValue() ||
					(getMediaType().getValue() & NgnMediaType.SessionAlert.getValue()) == NgnMediaType.SessionAlert.getValue() ||
					(getMediaType().getValue() & NgnMediaType.SessionImminentperil.getValue()) == NgnMediaType.SessionImminentperil.getValue()){
				if(BuildConfig.DEBUG)Log.d(TAG,"Type call is emergency and the level is "+levelEmergency);
				ret = mSession.callEmergency(remoteUri, NgnMediaType.ConvertToNative(super.getMediaType())
						,answerMode
						,emergencyType,levelEmergency,config);

			}else
				ret = mSession.call(remoteUri, NgnMediaType.ConvertToNative(super.getMediaType())
						,answerMode
						,config);

			if(BuildConfig.DEBUG)Log.d(TAG,"init call type: "+super.getMediaType().name());



			if(!ret){
				Log.e(TAG,"Mistranslation of the SIP address 2");
			}else{
				if ((getMediaType().getValue() & NgnMediaType.SessionAudioMCPTT.getValue()) == NgnMediaType.SessionAudioMCPTT.getValue()){
					Log.d(TAG,"MCPTT Audio type call");
					if (mMcpttCallback == null){
						mMcpttCallback = new MyMcpttCallback(this);
						Log.d(TAG,"Null PTT callback");
					}
					mSession.setMcpttCallback(mMcpttCallback);
					//MBMS callback
					if (mMcpttMbmsCallback == null) {
						mMcpttMbmsCallback = new MyMcpttMbmsCallback(this);
					}
					mSession.setMcpttMbmsCallback(mMcpttMbmsCallback);
				}config.delete();
			}
			//MCPTT event for Location
			if(getMediaType()==NgnMediaType.SessionAudioGroupMCPTT){
				sendMCPTTEventForLocation(TypeMcpttSignallingEvent.GROUP_CALL_NON_EMERGENCY);
			}else if(getMediaType()==NgnMediaType.SessionAudioMCPTT){
				sendMCPTTEventForLocation(TypeMcpttSignallingEvent.PRIVATE_CALL_NON_EMERGENCY);
			}
		}else{
			Log.e(TAG,"Mistranslation of the SIP address: "+getMediaType().getValue()+" "+NgnMediaType.ConvertToNative(getMediaType()).swigValue());
		}

		return ret;
	}

	//MCPTT group
	public boolean makeCallGroupMCPTT(String remoteUri,Context context
			,boolean answerMode
	){
			return makeCallGroupMCPTT(remoteUri,context
					,answerMode
					,(String)null,-1);
	}
	public boolean makeCallGroupMCPTT(String remoteUri,Context context
			,boolean answerMode
			,EmergencyCallType emergencyCallType, int levelEmergency){
		return makeCallGroupMCPTT( remoteUri, context
				,answerMode
				,emergencyCallType.toString(),  levelEmergency);
	}
	public boolean makeCallGroupMCPTT(String remoteUri,Context context
			,boolean answerMode
			,String emergencyType, int levelEmergency
	)
	{
		super.mOutgoing = true;
		super.setToUri(remoteUri);
		boolean ret=false;
		ActionConfig config =new ActionConfig();
		int level = mConfigurationService.getInt(NgnConfigurationEntry.QOS_PRECOND_BANDWIDTH_LEVEL,
				NgnConfigurationEntry.DEFAULT_QOS_PRECOND_BANDWIDTH_LEVEL);
		tmedia_bandwidth_level_t bl = tmedia_bandwidth_level_t.swigToEnum(level);
		config.setMediaInt(twrap_media_type_t.twrap_media_audiovideo, "bandwidth-level", bl.swigValue());
		if(NgnMediaType.ConvertToNative(getMediaType())==null){
		}else if((getMediaType().getValue() & NgnMediaType.SessionAudioGroupMCPTT.getValue())==NgnMediaType.SessionAudioGroupMCPTT.getValue()){
			registerCallMCPTT(context);
			if((getMediaType().getValue() & NgnMediaType.SessionEmergency.getValue()) == NgnMediaType.SessionEmergency.getValue() ||
					(getMediaType().getValue() & NgnMediaType.SessionAlert.getValue()) == NgnMediaType.SessionAlert.getValue() ||
					(getMediaType().getValue() & NgnMediaType.SessionImminentperil.getValue()) == NgnMediaType.SessionImminentperil.getValue()){
				ret = mSession.callEmergency(remoteUri, NgnMediaType.ConvertToNative(super.getMediaType())
						,answerMode
						,emergencyType,levelEmergency,config);
			}else
				ret = mSession.call(remoteUri, NgnMediaType.ConvertToNative(super.getMediaType())
						,answerMode
						,config);

			if(!ret){
				Log.e(TAG,"Mistranslation of the SIP address 3");
			}else{
				if ((getMediaType().getValue() & NgnMediaType.SessionGroup.getValue()) == NgnMediaType.SessionGroup.getValue()){
					sendMCPTTEventForLocation(TypeMcpttSignallingEvent.GROUP_CALL_NON_EMERGENCY);
					Log.d(TAG,"MCPTT Group type call");
					if (mMcpttCallback == null){
						mMcpttCallback = new MyMcpttCallback(this);
						Log.d(TAG,"Null PTT callback for MCPTT group");
					}
					mSession.setMcpttCallback(mMcpttCallback);
					if (mMcpttMbmsCallback == null){
						mMcpttMbmsCallback = new MyMcpttMbmsCallback(this);
					}
					mSession.setMcpttMbmsCallback(mMcpttMbmsCallback);
				}config.delete();
			}
		}else{
			Log.e(TAG,"Mistranslation of the SIP address: "+getMediaType().getValue()+" "+NgnMediaType.ConvertToNative(getMediaType()).swigValue());
		}




		return ret;
	}

	public String getMcpttEmergencyResourcePriorityString(){
		if(mSession!=null){
			return mSession.getPttMcpttEmergencyResourcePriorityString();
		}
		return null;
	}

	public int getMcpttEmergencyResourcePriority(){
		if(mSession!=null){
			return mSession.getPttMcpttEmergencyResourcePriority();
		}
		return -1;
	}

	/**
	 * Ends an audio/video call. The call could be in any state: incoming, outgoing, incall, ...
	 * @return true if succeed and false otherwise
	 */
    public boolean hangUpCall(){
    	if (super.isActive()) {

    		if(BuildConfig.DEBUG)Log.d(TAG,"Hang up Call");
			NgnEngine.getInstance().getMbmsService().hangUpCallMbms(getId());
    		return isConnected() ? mSession.hangup() : mSession.reject();
    	}else{
			Log.e(TAG,"Inactive call");
		}
    	return false;
    }

    /**
     * Puts the call on hold. At any time you can check if the call is held or not by using @ref isLocalHeld()
     * @return true if succeed and false otherwise
     * @sa @ref resumeCall() @ref isLocalHeld() @ref isRemoteHeld() @ref resumeCall()
     */
    public boolean holdCall(){
		return super.isActive() ? mSession.hold() : false;
	}
    
    /**
     * Resumes a call. The call should be previously held using @ref holdCall()
     * @return true is succeed and false otherwise
     * @sa @ref holdCall() @ref isLocalHeld() @ref isRemoteHeld()
     */
	public boolean resumeCall(){		
		return super.isActive() ? mSession.resume() : false;
	}
	
	/**
	 * Transfers the current call.
	 * @param transferUri The destination Uri.
	 * @return true if succeed and false otherwise.
	 */
	public boolean transferCall(String transferUri){
        if (NgnStringUtils.isNullOrEmpty(transferUri) || !NgnUriUtils.isValidSipUri(transferUri)){
            return false;
        }
        return super.isActive() ? mSession.transfer(transferUri) : false;
    }

	/**
	 * Accepts the incoming call transfer request.
	 * @return true if succeed and false otherwise.
	 */
    public boolean acceptCallTransfer(){
        return super.isActive() ? mSession.acceptTransfer() : false;
    }

    /**
     * Rejects the incoming call transfer request.
     * @return true if succeed and false otherwise.
     */
    public boolean rejectCallTransfer(){
        return super.isActive() ? mSession.rejectTransfer() : false;
    }
	
	/**
	 * Checks whether the call is locally held held or not. You should use @ref resumeCall() to resume
	 * the call.
	 * @return true if locally held and false otherwise
	 * @sa @ref isRemoteHeld()
	 */
	@Override
	public boolean isLocalHeld(){
		return super.isLocalHeld();
	}
	
	@Override
	public void setLocalHold(boolean localHold){
		final boolean changed = mLocalHold!= localHold;
		super.setLocalHold(localHold);
		
		if(mVideoProducer != null){
			mVideoProducer.setOnPause(mLocalHold);
		}
		if(mAudioProducer != null){
			mAudioProducer.setOnPause(mLocalHold);
		}
		
		if(changed){
			super.setChangedAndNotifyObservers(this);
		}
	}
	
	/**
	 * Checks whether the call is remotely held or not
	 * @return true if the call is remotely held and false otherwise
	 * @sa @ref isLocalHeld()
	 */
	@Override
	public boolean isRemoteHeld(){
		return super.isRemoteHeld();
	}
	
	@Override
	public void setRemoteHold(boolean remoteHold){
		final boolean changed = mRemoteHold != remoteHold;
		super.setRemoteHold(remoteHold);
		
		if(changed){
			super.setChangedAndNotifyObservers(this);
		}
	}

	public boolean isOnMute(){
		if(mAudioProducer != null){
			return mAudioProducer.isOnMute();
		}
		return false;
	}
	
	public void setOnMute(boolean bOnMute){
		if(mAudioProducer != null){
			mAudioProducer.setOnMute(bOnMute);
		}
	}
	
    /**
     * Sends DTMF digit. The session must be active (incoming, outgoing, incall, ...) in order to try
     * to send DTMF digits.
     * @param digit the digit to send
     * @return true if succeed and false otherwise
     */
    public boolean sendDTMF(int digit){
        return mSession.sendDTMF(digit);
    }
    
    private boolean sendT140Data(tmedia_t140_data_type_t dataType, String dataStr) {
    	if(!isConnected()){
    		Log.e(TAG, "Cannot send T.140 data. The session must be connected first.");
    		return false;
    	}
    	if(!NgnMediaType.isT140Type(getMediaType())){
    		Log.e(TAG, "Cannot send T.140 data. Not supported by this session.");
    		return false;
    	}
        if (!NgnStringUtils.isNullOrEmpty(dataStr)){
			try {
				byte[] bytes = dataStr.getBytes("UTF-8");
				ByteBuffer dataPtr = ByteBuffer.allocateDirect(bytes.length);
	            dataPtr.put(bytes);      
	            return mSession.sendT140Data(dataType, dataPtr, dataPtr.capacity());
			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, e.toString());
				return false;
			}
        }
        else{
            return mSession.sendT140Data(dataType);
        }
    }

    public boolean sendT140Data(String dataStr){
        return sendT140Data(tmedia_t140_data_type_t.tmedia_t140_data_type_utf8, dataStr);
    }

    public boolean sendT140Data(tmedia_t140_data_type_t dataType){
        return sendT140Data(dataType, null);
    }
    
    
    /**
     * NgnT140Callback
     */
    static class NgnT140Callback extends T140Callback {
    	final NgnAVSession mAVSession;
    	
    	NgnT140Callback(NgnAVSession avSession){
    		mAVSession = avSession;
    	}

		@Override
		public int ondata(T140CallbackData pData) {
			tmedia_t140_data_type_t dataType = pData.getType();
			final Intent intent = new Intent(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
			final byte[] bytes;
			final String contentType;
			switch(dataType){
				case tmedia_t140_data_type_utf8:
					{
						bytes = pData.getData();
						contentType = NgnContentType.TEXT_PLAIN;
						break;
					}
				default:
					{
						bytes = null;
						contentType = NgnContentType.T140COMMAND;
						break;
					}
			}
			
			final NgnMessagingEventArgs args = new NgnMessagingEventArgs(mAVSession.getId(), NgnMessagingEventTypes.INCOMING, 
					"T.140", bytes, contentType);
			intent.putExtra(NgnMessagingEventArgs.EXTRA_REMOTE_PARTY, mAVSession.getRemotePartyUri());
			intent.putExtra(NgnMessagingEventArgs.EXTRA_DATE, NgnDateTimeUtils.now());
			intent.putExtra(NgnMessagingEventArgs.EXTRA_EMBEDDED, args);
			intent.putExtra(NgnMessagingEventArgs.EXTRA_T140_DATA_TYPE, dataType);
			NgnApplication.getContext().sendBroadcast(intent);
			
			return 0;
		}
    }
}