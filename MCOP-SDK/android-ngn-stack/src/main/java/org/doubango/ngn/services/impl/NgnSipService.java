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
 */
package org.doubango.ngn.services.impl;

import android.content.Context;
import android.content.Intent;
import android.os.ConditionVariable;
import android.os.Looper;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnInviteEventTypes;
import org.doubango.ngn.events.NgnMessagingAffiliationEventArgs;
import org.doubango.ngn.events.NgnMessagingAffiliationEventTypes;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.events.NgnMessagingEventTypes;
import org.doubango.ngn.events.NgnPublicationAffiliationEventArgs;
import org.doubango.ngn.events.NgnPublicationEventArgs;
import org.doubango.ngn.events.NgnPublicationEventTypes;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventTypes;
import org.doubango.ngn.events.NgnSubscriptionAffiliationEventArgs;
import org.doubango.ngn.events.NgnSubscriptionAffiliationEventTypes;
import org.doubango.ngn.events.NgnSubscriptionCMSEventArgs;
import org.doubango.ngn.events.NgnSubscriptionEventArgs;
import org.doubango.ngn.events.NgnSubscriptionEventTypes;
import org.doubango.ngn.model.NgnDeviceInfo.Orientation;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnNetworkService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.services.affiliation.IMyAffiliationService;
import org.doubango.ngn.services.gms.IMyGMSService;
import org.doubango.ngn.services.profiles.IMyProfilesService;
import org.doubango.ngn.sip.MyDRegisterCallback;
import org.doubango.ngn.sip.MyMessagingAffiliationSession;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;
import org.doubango.ngn.sip.NgnMessagingSession;
import org.doubango.ngn.sip.NgnMsrpSession;
import org.doubango.ngn.sip.NgnPresenceStatus;
import org.doubango.ngn.sip.NgnPublicationSession;
import org.doubango.ngn.sip.NgnRegistrationSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.sip.NgnSipSession;
import org.doubango.ngn.sip.NgnSipSession.ConnectionState;
import org.doubango.ngn.sip.NgnSipStack;
import org.doubango.ngn.sip.NgnSipStack.STACK_STATE;
import org.doubango.ngn.sip.NgnSubscriptionSession;
import org.doubango.ngn.sip.NgnSubscriptionSession.EventPackageType;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnDateTimeUtils;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;
import org.doubango.tinyWRAP.CallSession;
import org.doubango.tinyWRAP.DDebugCallback;
import org.doubango.tinyWRAP.DialogEvent;
import org.doubango.tinyWRAP.InviteEvent;
import org.doubango.tinyWRAP.InviteSession;
import org.doubango.tinyWRAP.MessagingAffiliationEvent;
import org.doubango.tinyWRAP.MessagingAffiliationSession;
import org.doubango.tinyWRAP.MessagingEvent;
import org.doubango.tinyWRAP.MessagingSession;
import org.doubango.tinyWRAP.MsrpSession;
import org.doubango.tinyWRAP.OptionsEvent;
import org.doubango.tinyWRAP.OptionsSession;
import org.doubango.tinyWRAP.PublicationAffiliationEvent;
import org.doubango.tinyWRAP.PublicationAffiliationSession;
import org.doubango.tinyWRAP.RPMessage;
import org.doubango.tinyWRAP.SMSData;
import org.doubango.tinyWRAP.SMSEncoder;
import org.doubango.tinyWRAP.SdpMessage;
import org.doubango.tinyWRAP.SipCallback;
import org.doubango.tinyWRAP.SipMessage;
import org.doubango.tinyWRAP.SipSession;
import org.doubango.tinyWRAP.SipStack;
import org.doubango.tinyWRAP.StackEvent;
import org.doubango.tinyWRAP.SubscriptionAffiliationEvent;
import org.doubango.tinyWRAP.SubscriptionAffiliationSession;
import org.doubango.tinyWRAP.SubscriptionEvent;
import org.doubango.tinyWRAP.SubscriptionSession;
import org.doubango.tinyWRAP.tinyWRAPConstants;
import org.doubango.tinyWRAP.tsip_invite_event_type_t;
import org.doubango.tinyWRAP.tsip_message_event_type_t;
import org.doubango.tinyWRAP.tsip_options_event_type_t;
import org.doubango.tinyWRAP.tsip_publish_event_type_t;
import org.doubango.tinyWRAP.tsip_request_type_t;
import org.doubango.tinyWRAP.tsip_subscribe_event_type_t;
import org.doubango.tinyWRAP.twrap_media_type_t;
import org.doubango.tinyWRAP.twrap_sms_type_t;
import org.doubango.utils.Utils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


public class NgnSipService extends NgnBaseService implements
		INgnSipService,
		tinyWRAPConstants, IMyProfilesService.OnSetProfileListener,
		MyDRegisterCallback.OnRegisterCallBackListener
{
	private final static String TAG = Utils.getTAG(NgnSipService.class.getCanonicalName());
	private final org.doubango.ngn.services.location.IMyLocalizationService mLocalizationService;
	private final org.doubango.ngn.services.mbms.IMyMbmsService mMbmsService;
	private final IMyAffiliationService mAffiliationService;
	private final org.doubango.ngn.services.cms.IMyCMSService mCMSService;
	private final org.doubango.ngn.services.gms.IMyGMSService mGMSService;
	private final IMyProfilesService mProfilesService;


	private NgnRegistrationSession mRegSession;
	private NgnSipStack mSipStack;
	private final DDebugCallback mDebugCallback;
	private final MyDRegisterCallback mRegisterCallback;
	private final MySipCallback mSipCallback;
	private NgnSipPrefrences mPreferences;
	private boolean registerBefore=false;
	private final INgnConfigurationService mConfigurationService;
	private final INgnNetworkService mNetworkService;
	private String iPCurrentStack=null;



	private ConditionVariable mCondHackAoR;
	private OnAuthenticationListener onAuthenticationListener;

	public NgnSipService() {
		super();
		mDebugCallback = new DDebugCallback();
		mRegisterCallback = new MyDRegisterCallback(NgnApplication.getContext());
		mRegisterCallback.setOnRegisterCallBackListener(this);
		mLocalizationService=NgnEngine.getInstance().getLocationService();
		mMbmsService=NgnEngine.getInstance().getMbmsService();
		mAffiliationService=NgnEngine.getInstance().getAffiliationService();
		mProfilesService=NgnEngine.getInstance().getProfilesService();
		mCMSService=NgnEngine.getInstance().getCMSService();
		mGMSService=NgnEngine.getInstance().getGMSService();
		mConfigurationService = NgnEngine.getInstance()
				.getConfigurationService();
		mNetworkService = NgnEngine.getInstance().getNetworkService();
		mSipCallback = new MySipCallback(this,NgnApplication.getContext());
		mSipCallback.setLocalizationService(mLocalizationService);
		mSipCallback.setAffiliationService(mAffiliationService);
		mSipCallback.setCMSService(mCMSService);
		mSipCallback.setGMSService(mGMSService);
		mSipCallback.setMbmsService(mMbmsService);
		mSipCallback.setConfigurationService(mConfigurationService);
		mPreferences = new NgnSipPrefrences();
	}

	@Override
	public boolean start() {
		Log.d(TAG, "starting...");
		configureProfile(NgnApplication.getContext());
		NgnEngine.getInstance().getProfilesService().setOnSetProfileListener(this);
		return true;
	}

	@Override
	public boolean stop() {
		Log.d(TAG, "stopping...");
		if (mSipStack != null && mSipStack.getState() == STACK_STATE.STARTED) {
			return mSipStack.stop();
		}
		return true;
	}

	@Override
	public String getDefaultIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDefaultIdentity(String identity) {
		// TODO Auto-generated method stub
	}

	@Override
	public NgnSipStack getSipStack() {
		return mSipStack;
	}

	@Override
	public boolean isRegistered() {
		if(BuildConfig.DEBUG)Log.i(TAG,"isRegistered execute");
		if (mRegSession != null) {
			boolean response=mRegSession.isConnected();
			if(BuildConfig.DEBUG)Log.i(TAG,"isRegistered "+response);
			return response;
		}else{
			if(BuildConfig.DEBUG)Log.i(TAG,"isRegistered false");
		}
		return false;
	}



	@Override
	public ConnectionState getRegistrationState() {
		if (mRegSession != null) {
			return mRegSession.getConnectionState();
		}
		return ConnectionState.NONE;
	}

	@Override
	public boolean isXcapEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPublicationEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubscriptionEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubscriptionToRLSEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCodecs() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCodecs(int coddecs) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] getSubRLSContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getSubRegContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getSubMwiContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getSubWinfoContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean stopStack() {
		if (mSipStack != null) {
			mSipStack.stop();
		}
		return false;
	}
	@Override
	public boolean setLocalIp(String ip){
		if(ip!=null && !ip.trim().isEmpty() && Utils.checkFormatIp(ip.trim())){
			iPCurrentStack=ip;
			return true;
		}else{
			Log.e(TAG,"Error configure IP");
		}
		return false;
	}

	@Override
	public boolean configureProfile(Context context){
		return configureProfile(true,context);
	}

	@Override
	public boolean configureProfile(boolean invalidProfile,Context context){
		if(mProfilesService.getProfileNow(context)!=null){
			Log.d(TAG,"Use configuration of:"+mPreferences.getName());
			if(invalidProfile)mProfilesService.invalidProfile(context);
			mPreferences=mProfilesService.getProfileNow(context);
			if(mPreferences.getRealm()==null){
				mPreferences.setRealm(
						mConfigurationService.getString(
								NgnConfigurationEntry.NETWORK_REALM,
								NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
			}
			if(mPreferences.getPassword()==null){
				mPreferences.setPassword(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDENTITY_PASSWORD,
								NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD));
			}
			if(mPreferences.getIMPI()==null){
				mPreferences.setIMPI(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDENTITY_IMPI,
								NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI));
			}
			if(mPreferences.getIMPU()==null){
				mPreferences.setIMPU(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDENTITY_IMPU,
								NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU));
			}
			if(mPreferences.getMcpttPsiCallPrivate()==null){
				mPreferences.setMcpttPsiCallPrivate(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_CALL_PRIVATE,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_PRIVATE));
			}
			if(mPreferences.getMcpttPsiCallGroup()==null){
				mPreferences.setMcpttPsiCallGroup(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_CALL_GROUP,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_GROUP));
			}
			if(mPreferences.getMcpttPsiCallPreestablished()==null){
				mPreferences.setMcpttPsiCallPreestablished(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_CALL_PREESTABLISHED,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_PREESTABLISHED));
			}
			if(mPreferences.getMcpttPsiCMS()==null){
				mPreferences.setMcpttPsiCMS(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_CMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CMS));
			}
			if(mPreferences.isMcpttEnableSubcriptionCMS()==null){
				mPreferences.setMcpttEnableSubcriptionCMS(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_ENABLE_SUBSCRIPTION_CMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_CMS));
			}

			if(mPreferences.getCMSXCAPRootURI()==null){
				mPreferences.setCMSXCAPRootURI(
						mConfigurationService.getString(
								NgnConfigurationEntry.CMS_XCAP_ROOT_URI,
								NgnConfigurationEntry.DEFAULT_CMS_XCAP_ROOT_URI));
			}
			if(mPreferences.getMcpttPsiGMS()==null){
				mPreferences.setMcpttPsiGMS(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_GMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_GMS));
			}
			if(mPreferences.isMcpttEnableSubcriptionGMS()==null){
				mPreferences.setMcpttEnableSubcriptionGMS(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_ENABLE_SUBSCRIPTION_GMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_GMS));
			}
			if(mPreferences.getGMSXCAPRootURI()==null){
				mPreferences.setGMSXCAPRootURI(
						mConfigurationService.getString(
								NgnConfigurationEntry.GMS_XCAP_ROOT_URI,
								NgnConfigurationEntry.DEFAULT_GMS_XCAP_ROOT_URI));
			}
			if(mPreferences.isMcpttIsEnableAffiliation()==null){
				mPreferences.setMcpttIsEnableAffiliation(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_IS_AFFILIATION,
								NgnConfigurationEntry.DEFAULT_MCPTT_IS_AFFILIATION));
			}
			// Self affiliation
			if(mPreferences.isMcpttIsSelfAffiliation()==null) {
				mPreferences.setMcpttIsSelfAffiliation(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_IS_SELF_AFFILIATION,
								NgnConfigurationEntry.DEFAULT_MCPTT_IS_SELF_AFFILIATION));
			}
			if(mPreferences.getMcpttPsiAffiliation()==null){
				mPreferences.setMcpttPsiAffiliation(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_AFFILIATION,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_AFFILIATION));
			}
			if(mPreferences.getMcpttPsiAuthentication()==null){
				mPreferences.setMcpttPsiAuthentication(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_PSI_AUTHENTICATION,
								NgnConfigurationEntry.DEFAULT_MCPTT_PSI_AUTHENTICATION));
			}
			if(mPreferences.getMcpttId()==null){
				mPreferences.setMcpttId(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_ID,
								NgnConfigurationEntry.DEFAULT_MCPTT_ID));
			}
			if(mPreferences.getMcpttClientId()==null){
				String mcpttClientId=mConfigurationService.getString(
						NgnConfigurationEntry.MCPTT_CLIENT_ID,
						NgnConfigurationEntry.DEFAULT_MCPTT_CLIENT_ID);
				mPreferences.setMcpttClientId(mcpttClientId
				);
			}
			if(mPreferences.getMcpttPriority()<0){
				mPreferences.setMcpttPriority(
						mConfigurationService.getInt(
								NgnConfigurationEntry.MCPTT_PRIORITY,
								NgnConfigurationEntry.DEFAULT_MCPTT_PRIORITY));
			}
			if(mPreferences.isMcpttImplicit()==null){
				mPreferences.setMcpttImplicit(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_IMPLICIT,
								NgnConfigurationEntry.DEFAULT_MCPTT_IMPLICIT));
			}
			if(mPreferences.isMcpttGranted()==null){
				mPreferences.setMcpttGranted(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_GRANTED,
								NgnConfigurationEntry.DEFAULT_MCPTT_GRANTED));
			}


			if(mPreferences.isMcpttEnableMbms()==null){
				mPreferences.setMcpttEnableMbms(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_ENABLE_MBMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_MBMS));
			}
			if(mPreferences.isMcpttLocationInfoVersionOld()==null){
				mPreferences.setMcpttLocationInfoVersionOld(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_LOCATION_INFO_VERSION_OLD,
								NgnConfigurationEntry.DEFAULT_MCPTT_LOCATION_INFO_VERSION_OLD));
			}
			if(mPreferences.isMcpttPrivAnswerMode()==null){
				mPreferences.setMcpttPrivAnswerMode(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_PRIV_ANSWER_MODE,
								NgnConfigurationEntry.DEFAULT_MCPTT_PRIV_ANSWER_MODE));
			}
			if(mPreferences.isMcpttAnswerMode()==null){
				mPreferences.setMcpttAnswerMode(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_ANSWER_MODE,
								NgnConfigurationEntry.DEFAULT_MCPTT_ANSWER_MODE));
			}
			if(mPreferences.isMcpttNameSpace()==null){
				mPreferences.setMcpttNameSpace(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_NAMESPACE,
								NgnConfigurationEntry.DEFAULT_MCPTT_NAMESPACE));
				// Set Proxy-CSCF
			}
			if(mPreferences.getPcscfHost()==null){
				mPreferences.setPcscfHost(
						mConfigurationService.getString(
								NgnConfigurationEntry.NETWORK_PCSCF_HOST,
								NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_HOST)); // null will trigger DNS NAPTR+SRV
			}
			if(mPreferences.getPcscfPort()<0){
				mPreferences.setPcscfPort(
						mConfigurationService.getInt(
								NgnConfigurationEntry.NETWORK_PCSCF_PORT,
								NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
			}
			if(mPreferences.getTransport()==null){
				mPreferences.setTransport(
						mConfigurationService.getString(
								NgnConfigurationEntry.NETWORK_TRANSPORT,
								NgnConfigurationEntry.DEFAULT_NETWORK_TRANSPORT));
			}
			if(mPreferences.getIPVersion()==null){
				mPreferences.setIPVersion(
						mConfigurationService.getString(
								NgnConfigurationEntry.NETWORK_IP_VERSION,
								NgnConfigurationEntry.DEFAULT_NETWORK_IP_VERSION));
				// Preference values
			}
			if(mPreferences.isXcapEnabled()==null){
				mPreferences.setXcapEnabled(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.XCAP_ENABLED,
								NgnConfigurationEntry.DEFAULT_XCAP_ENABLED));
			}
			if(mPreferences.isPresenceEnabled()==null){
				mPreferences.setPresenceEnabled(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.RCS_USE_PRESENCE,
								NgnConfigurationEntry.DEFAULT_RCS_USE_PRESENCE));
			}
			if(mPreferences.isMWI()==null){
				mPreferences.setMWI(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.RCS_USE_MWI,
								NgnConfigurationEntry.DEFAULT_RCS_USE_MWI));
		/* Before registering, check if AoR hacking id enabled */
			}
			if(mPreferences.isHackAoR()==null) {
				mPreferences.setHackAoR(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.NATT_HACK_AOR,
								NgnConfigurationEntry.DEFAULT_NATT_HACK_AOR));
			}


			if(mPreferences.isMcpttPlayerSound()==null) {
				mPreferences.setMcpttPlayerSound(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_PLAY_SOUND_MCPTT_CALL,
								NgnConfigurationEntry.DEFAULT_MCPTT_PLAY_SOUND_MCPTT_CALL));
			}

			//IdMs Authentication
			if(mPreferences.isMcpttIsSelfAuthentication()==null) {
				mPreferences.setMcpttIsSelfAuthentication(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.SELF_CONFIGURE,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE));
			}
			if(mPreferences.getMcpttSelfAuthenticationClientId()==null) {
				mPreferences.setMcpttSelfAuthenticationClientId(
						mConfigurationService.getString(
								NgnConfigurationEntry.SELF_CONFIGURE_CLIENT_ID,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_CLIENT_ID));
			}
			if(mPreferences.getMcpttSelfAuthenticationIssuerUri()==null) {
				mPreferences.setMcpttSelfAuthenticationIssuerUri(
						mConfigurationService.getString(
								NgnConfigurationEntry.SELF_CONFIGURE_ISSUER_URI,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_ISSUER_URI));
			}
			if(mPreferences.getMcpttSelfAuthenticationRedirectUri()==null) {
				mPreferences.setMcpttSelfAuthenticationRedirectUri(
						mConfigurationService.getString(
								NgnConfigurationEntry.SELF_CONFIGURE_REDIRECT_URI,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_REDIRECT_URI));
			}


			if(mPreferences.getDisplayName()==null) {
				mPreferences.setDisplayName(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDENTITY_DISPLAY_NAME,
								NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
			}

			if(mPreferences.isMcpttEnableCMS()==null) {
				mPreferences.setMcpttEnableCMS(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.ENABLE_CMS,
								NgnConfigurationEntry.DEFAULT_ENABLE_CMS));
			}


			if(mPreferences.isMcpttUseIssuerUriIdms()==null) {
				mPreferences.setMcpttUseIssuerUriIdms(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.MCPTT_USE_ISSUER_URI_IDMS,
								NgnConfigurationEntry.DEFAULT_MCPTT_USE_ISSUER_URI_IDMS));
			}
			if(mPreferences.getMcpttUEId()==null) {
				mPreferences.setMcpttUEId(
						mConfigurationService.getString(
								NgnConfigurationEntry.MCPTT_UE_ID,
								NgnConfigurationEntry.DEFAULT_MCPTT_UE_ID));
			}

			if(mPreferences.getIdmsTokenEndPoint()==null) {
				mPreferences.setIdmsTokenEndPoint(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDMS_TOKEN_END_POINT,
								NgnConfigurationEntry.DEFAULT_IDMS_TOKEN_END_POINT));
			}
			if(mPreferences.getIdmsAuthEndpoint()==null) {
				mPreferences.setIdmsAuthEndpoint(
						mConfigurationService.getString(
								NgnConfigurationEntry.IDMS_AUTH_END_POINT,
								NgnConfigurationEntry.DEFAULT_IDMS_AUTH_END_POINT));
			}
			if(mPreferences.isMcpttSelfAuthenticationSendTokenRegister()==null) {
				mPreferences.setMcpttSelfAuthenticationSendTokenRegister(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.SELF_CONFIGURE_SEND_TOKEN_REGISTER,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_REGISTER));
			}
			if(mPreferences.isMcpttSelfAuthenticationSendTokenFail()==null) {
				mPreferences.setMcpttSelfAuthenticationSendTokenFail(
						mConfigurationService.getBoolean(
								NgnConfigurationEntry.SELF_CONFIGURE_SEND_TOKEN_FAIL,
								NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_FAIL));
			}

			return true;
		}else{
			Log.d(TAG,"Use configuration default");
			mPreferences.setRealm(mConfigurationService.getString(
					NgnConfigurationEntry.NETWORK_REALM,
					NgnConfigurationEntry.DEFAULT_NETWORK_REALM));
			mPreferences.setIMPI(mConfigurationService.getString(
					NgnConfigurationEntry.IDENTITY_IMPI,
					NgnConfigurationEntry.DEFAULT_IDENTITY_IMPI));
			mPreferences.setIMPU(mConfigurationService.getString(
					NgnConfigurationEntry.IDENTITY_IMPU,
					NgnConfigurationEntry.DEFAULT_IDENTITY_IMPU));
			mPreferences.setMcpttLocationInfoVersionOld(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.MCPTT_LOCATION_INFO_VERSION_OLD,
							NgnConfigurationEntry.DEFAULT_MCPTT_LOCATION_INFO_VERSION_OLD));
			mPreferences.setMcpttPsiCallPrivate(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_CALL_PRIVATE,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_PRIVATE));
			mPreferences.setMcpttPsiCallGroup(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_CALL_GROUP,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_GROUP));
			mPreferences.setMcpttPsiCallPreestablished(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_CALL_PREESTABLISHED,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CALL_PREESTABLISHED));

			mPreferences.setMcpttPsiCMS(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_CMS,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_CMS));
			mPreferences.setMcpttEnableSubcriptionCMS(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_ENABLE_SUBSCRIPTION_CMS,
					NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_CMS));

			mPreferences.setMcpttPsiGMS(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_GMS,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_GMS));
			mPreferences.setMcpttEnableSubcriptionGMS(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_ENABLE_SUBSCRIPTION_GMS,
					NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_GMS));

			mPreferences.setMcpttIsEnableAffiliation(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_IS_AFFILIATION,
					NgnConfigurationEntry.DEFAULT_MCPTT_IS_AFFILIATION));
			mPreferences.setMcpttIsSelfAffiliation(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_IS_SELF_AFFILIATION,
					NgnConfigurationEntry.DEFAULT_MCPTT_IS_SELF_AFFILIATION));
			mPreferences.setMcpttPsiAffiliation(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_PSI_AFFILIATION,
					NgnConfigurationEntry.DEFAULT_MCPTT_PSI_AFFILIATION));
			mPreferences.setMcpttId(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_ID,
					NgnConfigurationEntry.DEFAULT_MCPTT_ID));
			mPreferences.setMcpttClientId(mConfigurationService.getString(
					NgnConfigurationEntry.MCPTT_CLIENT_ID,
					NgnConfigurationEntry.DEFAULT_MCPTT_CLIENT_ID));
			mPreferences.setMcpttPriority(mConfigurationService.getInt(
					NgnConfigurationEntry.MCPTT_PRIORITY,
					NgnConfigurationEntry.DEFAULT_MCPTT_PRIORITY));
			mPreferences.setMcpttImplicit(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_IMPLICIT,
					NgnConfigurationEntry.DEFAULT_MCPTT_IMPLICIT));
			mPreferences.setMcpttGranted(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_GRANTED,
					NgnConfigurationEntry.DEFAULT_MCPTT_GRANTED));


			mPreferences.setMcpttEnableMbms(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_ENABLE_MBMS,
					NgnConfigurationEntry.DEFAULT_MCPTT_ENABLE_MBMS));
			mPreferences.setMcpttPrivAnswerMode(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_PRIV_ANSWER_MODE,
					NgnConfigurationEntry.DEFAULT_MCPTT_PRIV_ANSWER_MODE));
			mPreferences.setMcpttAnswerMode(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_ANSWER_MODE,
					NgnConfigurationEntry.DEFAULT_MCPTT_ANSWER_MODE));
			mPreferences.setMcpttNameSpace(mConfigurationService.getBoolean(
					NgnConfigurationEntry.MCPTT_NAMESPACE,
					NgnConfigurationEntry.DEFAULT_MCPTT_NAMESPACE));
			// Set Proxy-CSCF
			mPreferences.setPcscfHost(mConfigurationService.getString(
					NgnConfigurationEntry.NETWORK_PCSCF_HOST, null)); // null will trigger DNS NAPTR+SRV
			mPreferences.setPcscfPort(mConfigurationService.getInt(
					NgnConfigurationEntry.NETWORK_PCSCF_PORT,
					NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_PORT));
			mPreferences.setTransport(mConfigurationService.getString(
					NgnConfigurationEntry.NETWORK_TRANSPORT,
					NgnConfigurationEntry.DEFAULT_NETWORK_TRANSPORT));
			mPreferences.setIPVersion(mConfigurationService.getString(
					NgnConfigurationEntry.NETWORK_IP_VERSION,
					NgnConfigurationEntry.DEFAULT_NETWORK_IP_VERSION));
			mPreferences.setPassword(mConfigurationService.getString(
					NgnConfigurationEntry.IDENTITY_PASSWORD,
					NgnConfigurationEntry.DEFAULT_IDENTITY_PASSWORD));
			// Preference values
			mPreferences.setXcapEnabled(mConfigurationService.getBoolean(
					NgnConfigurationEntry.XCAP_ENABLED,
					NgnConfigurationEntry.DEFAULT_XCAP_ENABLED));
			mPreferences.setPresenceEnabled(mConfigurationService.getBoolean(
					NgnConfigurationEntry.RCS_USE_PRESENCE,
					NgnConfigurationEntry.DEFAULT_RCS_USE_PRESENCE));
			mPreferences.setMWI(mConfigurationService.getBoolean(
					NgnConfigurationEntry.RCS_USE_MWI,
					NgnConfigurationEntry.DEFAULT_RCS_USE_MWI));
		/* Before registering, check if AoR hacking id enabled */
			mPreferences.setHackAoR(mConfigurationService.getBoolean(
					NgnConfigurationEntry.NATT_HACK_AOR,
					NgnConfigurationEntry.DEFAULT_NATT_HACK_AOR));
			mPreferences.setMcpttPlayerSound(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.MCPTT_PLAY_SOUND_MCPTT_CALL,
							NgnConfigurationEntry.DEFAULT_MCPTT_PLAY_SOUND_MCPTT_CALL));
			//Idms authentication
			mPreferences.setMcpttIsSelfAuthentication(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.SELF_CONFIGURE,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE));
			mPreferences.setMcpttSelfAuthenticationClientId(
					mConfigurationService.getString(
							NgnConfigurationEntry.SELF_CONFIGURE_CLIENT_ID,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_CLIENT_ID));
			mPreferences.setMcpttSelfAuthenticationIssuerUri(
					mConfigurationService.getString(
							NgnConfigurationEntry.SELF_CONFIGURE_ISSUER_URI,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_ISSUER_URI));
			mPreferences.setMcpttUEId(
					mConfigurationService.getString(
							NgnConfigurationEntry.MCPTT_UE_ID,
							NgnConfigurationEntry.DEFAULT_MCPTT_UE_ID));
			mPreferences.setMcpttSelfAuthenticationRedirectUri(
					mConfigurationService.getString(
							NgnConfigurationEntry.SELF_CONFIGURE_REDIRECT_URI,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_REDIRECT_URI));


			mPreferences.setDisplayName(
					mConfigurationService.getString(
							NgnConfigurationEntry.IDENTITY_DISPLAY_NAME,
							NgnConfigurationEntry.DEFAULT_IDENTITY_DISPLAY_NAME));
			mPreferences.setMcpttEnableCMS(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.ENABLE_CMS,
							NgnConfigurationEntry.DEFAULT_ENABLE_CMS));


			mPreferences.setMcpttUseIssuerUriIdms(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.MCPTT_USE_ISSUER_URI_IDMS,
							NgnConfigurationEntry.DEFAULT_MCPTT_USE_ISSUER_URI_IDMS));
			mPreferences.setIdmsTokenEndPoint(
					mConfigurationService.getString(
							NgnConfigurationEntry.IDMS_TOKEN_END_POINT,
							NgnConfigurationEntry.DEFAULT_IDMS_TOKEN_END_POINT));
			mPreferences.setIdmsAuthEndpoint(
					mConfigurationService.getString(
							NgnConfigurationEntry.IDMS_AUTH_END_POINT,
							NgnConfigurationEntry.DEFAULT_IDMS_AUTH_END_POINT));
			mPreferences.setMcpttSelfAuthenticationSendTokenRegister(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.SELF_CONFIGURE_SEND_TOKEN_REGISTER,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_REGISTER));


			mPreferences.setMcpttSelfAuthenticationSendTokenFail(
					mConfigurationService.getBoolean(
							NgnConfigurationEntry.SELF_CONFIGURE_SEND_TOKEN_FAIL,
							NgnConfigurationEntry.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_FAIL));
			mProfilesService.setProfileNow(mPreferences);
		}
		return false;
	}


	public boolean register(Context context) {
		return register(true,context);
	}

	@Override
	public boolean register(boolean invalidProfile,Context context) {
		Log.d(TAG, "register()");
		configureProfile(invalidProfile,context);
		Log.d(TAG, String.format("realm='%s', impu='%s', impi='%s'",
				mPreferences.getRealm(), mPreferences.getIMPU(),
				mPreferences.getIMPI()));

		if (mSipStack == null) {
			mSipStack = new NgnSipStack(mSipCallback, mPreferences.getRealm(),
					mPreferences.getIMPI(), mPreferences.getIMPU()
			);
			mSipStack.setDebugCallback(mDebugCallback);
			// for USE authentication SIM
			if(BuildConfig.AUTHENTICATION_SIM)
				mSipStack.setRegisterCallback(mRegisterCallback,((MyDRegisterCallback)mRegisterCallback).dataResponseRegisterCallback,MyDRegisterCallback.SIZE_BUFFER_DATA);
			int codec=mConfigurationService.getInt(
					NgnConfigurationEntry.MEDIA_CODECS,
					NgnConfigurationEntry.DEFAULT_MEDIA_CODECS);
			SipStack.setCodecs_2(codec);
			Log.d(TAG,"Codecs support:"+codec);
		} else {
			if (!mSipStack.setRealm(mPreferences.getRealm())) {
				Log.e(TAG, "Failed to set realm");
				return false;
			}
			if (!mSipStack.setIMPI(mPreferences.getIMPI())) {
				Log.e(TAG, "Failed to set IMPI");
				return false;
			}
			if (!mSipStack.setIMPUIP(mPreferences.getIMPU())) {
				Log.e(TAG, "Failed to set IMPU");
				return false;
			}


		}


		//Use custom IP
		if(iPCurrentStack!=null){
			Log.d(TAG,"Configure ip: "+iPCurrentStack);
			mSipStack.setLocalIP(iPCurrentStack.trim());
		}


		//SET parameter for MCPTT
		if(!mSipStack.setMCPTTPSIPrivate(mPreferences.getMcpttPsiCallPrivate())){
			Log.e(TAG, "Failed to set PSI private MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTPSIGroup(mPreferences.getMcpttPsiCallGroup())){
			Log.e(TAG, "Failed to set PSI Group MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTPSIPreestablished(mPreferences.getMcpttPsiCallPreestablished())){
			Log.e(TAG, "Failed to set PSI Preestablished MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTPSICMS(mPreferences.getMcpttPsiCMS())){
			Log.e(TAG, "Failed to set PSI CMS");
			return false;
		}


		if(!mSipStack.setMCPTTPSIGMS(mPreferences.getMcpttPsiGMS())){
			Log.e(TAG, "Failed to set PSI GMS");
			return false;
		}

		if(!mSipStack.setMCPTTAffiliationIsEnable(mPreferences.isMcpttIsEnableAffiliation())){
			Log.e(TAG, "Failed to set is enable Affiliation MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTPSIAffiliation(mPreferences.getMcpttPsiAffiliation().trim())){
			Log.e(TAG, "Failed to set PSI Affiliation MCPTT");
			return false;
		}else{
			Log.d(TAG,"Configure affiliation PSI: "+mPreferences.getMcpttPsiAffiliation());
		}


		if(!mSipStack.setMCPTTPSIAuthentication(mPreferences.getMcpttPsiAuthentication().trim())){
			Log.e(TAG, "Failed to set PSI Authentication MCPTT");
			return false;
		}else{
			Log.d(TAG,"Configure Authentication PSI: "+mPreferences.getMcpttPsiAuthentication());
		}


		//Configure data from CMS
		if(mPreferences!=null){
			//T100 in seconds
			if(mPreferences.getT100()>=0 &&
                    !mSipStack.setMCPTTTimerT100(mPreferences.getT100())){
				Log.e(TAG, "Failed to set T100");
				return false;
			}
			//T101 in seconds
            if(mPreferences.getT101()<0 ||
                    !mSipStack.setMCPTTTimerT101(mPreferences.getT101())){
				Log.e(TAG, "Failed to set T101");
				return false;
			}
			//T103 in seconds
            if(mPreferences.getT103()<0 ||
                    !mSipStack.setMCPTTTimerT103(mPreferences.getT103())){
				Log.e(TAG, "Failed to set T103");
				return false;
			}
			//T104 in seconds
            if(mPreferences.getT104()<0 ||
                    !mSipStack.setMCPTTTimerT104(mPreferences.getT104())){
				Log.e(TAG, "Failed to set T104");
				return false;
			}
			//T132 in seconds
            if(mPreferences.getT132()<0 ||
                    !mSipStack.setMCPTTTimerT132(mPreferences.getT132())){
				Log.e(TAG, "Failed to set T132");
				return false;
			}
		}




		if(!mSipStack.setMCPTTID(mPreferences.getMcpttId())){
			Log.e(TAG, "Failed to set MCPTT ID");
			return false;
		}

		if(!mSipStack.setMCPTTClientID(mPreferences.getMcpttClientId())){
			Log.e(TAG, "Failed to set MCPTT client ID");
			return false;
		}



		if(!mSipStack.setMCPTTPriority(mPreferences.getMcpttPriority())){
			Log.e(TAG, "Failed to set Priority MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTImplicit(mPreferences.isMcpttImplicit())){
			Log.e(TAG, "Failed to set implicit MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTGranted(mPreferences.isMcpttGranted())){
			Log.e(TAG, "Failed to set Granted MCPTT");
			return false;
		}

		if(!mSipStack.setMCPTTPrivAnswerMode(mPreferences.isMcpttPrivAnswerMode())){
			Log.e(TAG, "Failed to set priv_answer_mode MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTAnswerMode(mPreferences.isMcpttAnswerMode())){
			Log.e(TAG, "Failed to set answer_mode MCPTT");
			return false;
		}
		if(!mSipStack.setMCPTTNameSpace(mPreferences.isMcpttNameSpace())){
			Log.e(TAG, "Failed to set NameSpace in mcpttinfo MCPTT");
			return false;
		}


		// set the Password
		mSipStack.setPassword(mPreferences.getPassword());
		// Set AMF
		mSipStack.setAMF(mConfigurationService.getString(
				NgnConfigurationEntry.SECURITY_IMSAKA_AMF,
				NgnConfigurationEntry.DEFAULT_SECURITY_IMSAKA_AMF));
		// Set Operator Id
		mSipStack.setOperatorId(mConfigurationService.getString(
				NgnConfigurationEntry.SECURITY_IMSAKA_OPID,
				NgnConfigurationEntry.DEFAULT_SECURITY_IMSAKA_OPID));

		// Check stack
		if (!mSipStack.isValid()) {
			Log.e(TAG, "Trying to use invalid stack");
			return false;
		}

		// Set STUN information
		mSipStack.setSTUNEnabled(mConfigurationService.getBoolean(
				NgnConfigurationEntry.NATT_USE_STUN_FOR_SIP,
				NgnConfigurationEntry.DEFAULT_NATT_USE_STUN_FOR_SIP));
		if (mConfigurationService.getBoolean(
				NgnConfigurationEntry.NATT_STUN_DISCO,
				NgnConfigurationEntry.DEFAULT_NATT_STUN_DISCO)) {
			final String realm = mPreferences.getRealm();
			String domain = realm.substring(realm.indexOf(':') + 1);
			int[] port = new int[1];
			String server = mSipStack.dnsSrv(
					String.format("_stun._udp.%s", domain), port);
			if (server == null) {
				Log.e(TAG, "STUN discovery has failed");
			}
			Log.d(TAG, String.format("STUN1 - server=%s and port=%d",
					server, port[0]));
			mSipStack.setSTUNServer(server, port[0]);// Needed event if null
		} else {
			String server = mConfigurationService.getString(
					NgnConfigurationEntry.NATT_STUN_SERVER,
					NgnConfigurationEntry.DEFAULT_NATT_STUN_SERVER);
			int port = mConfigurationService.getInt(
					NgnConfigurationEntry.NATT_STUN_PORT,
					NgnConfigurationEntry.DEFAULT_NATT_STUN_PORT);
			Log.d(NgnSipService.TAG, String.format(
					"STUN2 - server=%s and port=%d", server, port));
			mSipStack.setSTUNServer(server, port);
		}



		Log.d(TAG,
				String.format(
						"pcscf-host='%s', pcscf-port='%d', transport='%s', ipversion='%s'",
						mPreferences.getPcscfHost(),
						mPreferences.getPcscfPort(),
						mPreferences.getTransport(),
						mPreferences.getIPVersion()));

		if (!mSipStack.setProxyCSCF(mPreferences.getPcscfHost(),
				mPreferences.getPcscfPort(), mPreferences.getTransport(),
				mPreferences.getIPVersion())) {
			Log.e(NgnSipService.TAG, "Failed to set Proxy-CSCF parameters");
			return false;
		}

		// Set local IP (If you're reusing this code on non-Android platforms
		// (iOS, Symbian, WinPhone, ...),
		// let Doubango retrieve the best IP address
		boolean ipv6 = NgnStringUtils.equals(mPreferences.getIPVersion(),
				"ipv6", true);
		String ip=mNetworkService.getLocalIP(ipv6);
		if(iPCurrentStack!=null){
			ip=iPCurrentStack;
		}
		mPreferences.setLocalIP(ip);
		if (mPreferences.getLocalIP() == null) {
			// if(fromNetworkService){
			// this.preferences.localIP = ipv6 ? "::" : "10.0.2.15"; /* Probably
			// on the emulator */
			// }
			// else{
			// Log.e(TAG, "IP address is Null. Trying to start network");
			// this.networkService.setNetworkEnabledAndRegister();
			// return false;
			// }
		}
		if (!mSipStack.setLocalIP(mPreferences.getLocalIP())) {
			Log.e(TAG, "Failed to set the local IP");
			return false;
		}
		Log.d(TAG, String.format("Local IP='%s'", mPreferences.getLocalIP()));

		// Whether to use DNS NAPTR+SRV for the Proxy-CSCF discovery (even if
		// the DNS requests are sent only when the stack starts,
		// should be done after setProxyCSCF())
		String discoverType = mConfigurationService.getString(
				NgnConfigurationEntry.NETWORK_PCSCF_DISCOVERY,
				NgnConfigurationEntry.DEFAULT_NETWORK_PCSCF_DISCOVERY);
		mSipStack.setDnsDiscovery(NgnStringUtils.equals(discoverType,
				NgnConfigurationEntry.PCSCF_DISCOVERY_DNS_SRV, true));

		// enable/disable 3GPP early IMS
		mSipStack.setEarlyIMS(mConfigurationService.getBoolean(
				NgnConfigurationEntry.NETWORK_USE_EARLY_IMS,
				NgnConfigurationEntry.DEFAULT_NETWORK_USE_EARLY_IMS));

		// SigComp (only update compartment Id if changed)
		if (mConfigurationService.getBoolean(
				NgnConfigurationEntry.NETWORK_USE_SIGCOMP,
				NgnConfigurationEntry.DEFAULT_NETWORK_USE_SIGCOMP)) {
			String compId = String.format("urn:uuid:%s", UUID.randomUUID()
					.toString());
			mSipStack.setSigCompId(compId);
		} else {
			mSipStack.setSigCompId(null);
		}

		// TLS
		final String pvFilePath = mConfigurationService.getString(
				NgnConfigurationEntry.SECURITY_TLS_PRIVKEY_FILE_PATH,
				NgnConfigurationEntry.DEFAULT_SECURITY_TLS_PRIVKEY_FILE_PATH);
		final String pbFilePath = mConfigurationService.getString(
				NgnConfigurationEntry.SECURITY_TLS_PUBKEY_FILE_PATH,
				NgnConfigurationEntry.DEFAULT_SECURITY_TLS_PUBKEY_FILE_PATH);
		final String caFilePath = mConfigurationService.getString(
				NgnConfigurationEntry.SECURITY_TLS_CA_FILE_PATH,
				NgnConfigurationEntry.DEFAULT_SECURITY_TLS_CA_FILE_PATH);
		final boolean verifyCerts = mConfigurationService.getBoolean(
				NgnConfigurationEntry.SECURITY_TLS_VERIFY_CERTS,
				NgnConfigurationEntry.DEFAULT_SECURITY_TLS_VERIFY_CERTS);
		Log.d(TAG, String.format("TLS - pvk='%s' pbk='%s' ca='%s' verify=%s",
				pvFilePath, pbFilePath, caFilePath, verifyCerts));
		if (!mSipStack.setSSLCertificates(pvFilePath, pbFilePath, caFilePath,
				verifyCerts)) {
			Log.e(TAG, "Failed to set TLS certificates");
			return false;
		}

		// Start the Stack
		if (!mSipStack.start()) {
			if (context != null
					&& Thread.currentThread() == Looper.getMainLooper()
							.getThread()) {
				//Toast.makeText(context, "Failed to start the SIP stack",Toast.LENGTH_LONG).show();
			}
			Log.e(TAG, "Failed to start the SIP stack");
			return false;
		}



		// Create registration session
		if (mRegSession == null) {
			mRegSession = new NgnRegistrationSession(mSipStack);
		} else {
			mRegSession.setSigCompId(mSipStack.getSigCompId());
		}

		// Set/update from URI. For Registration: ToUri should be equal to realm
		// (done by the stack)
		mRegSession.setFromUri(mPreferences.getIMPU());


		if (mPreferences.isHackAoR()) {
			if (mCondHackAoR == null) {
				mCondHackAoR = new ConditionVariable();
			}
			final OptionsSession optSession = new OptionsSession(mSipStack);
			// optSession.setToUri(String.format("sip:%s@%s", "hacking_the_aor",
			// this.preferences.realm));
			optSession.send();
			try {
				synchronized (mCondHackAoR) {
					mCondHackAoR
							.wait(mConfigurationService
									.getInt(NgnConfigurationEntry.NATT_HACK_AOR_TIMEOUT,
											NgnConfigurationEntry.DEFAULT_NATT_HACK_AOR_TIMEOUT));
				}
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
			mCondHackAoR = null;
			optSession.delete();
		}
		String mcpttInfoRegister=null;
		mcpttInfoRegister=mCMSService.register(context);
		if (!mRegSession.register(mcpttInfoRegister)) {
			Log.e(TAG, "Failed to send REGISTER request");
			return false;
		}else{
			registerBefore=true;
		}





		return true;
	}

	@Override
	public boolean unRegister() {
		if (isRegistered()) {
			Thread thread=new Thread(new Runnable() {
				@Override
				public void run() {
					if(BuildConfig.DEBUG)Log.d(TAG,"Init unRegister");
					mSipStack.stop();
					if(BuildConfig.DEBUG)Log.d(TAG,"End unRegister");
				}
			});
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();
		}
		return true;
	}

	@Override
	public boolean PresencePublish() {
		return false;
	}

	@Override
	public boolean PresencePublish(NgnPresenceStatus status) {
		// TODO Auto-generated method stub
		return false;
	}

	private void broadcastRegistrationEvent(NgnRegistrationEventArgs args) {
		final Intent intent = new Intent(
				NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		intent.putExtra(NgnRegistrationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastInviteEvent(NgnInviteEventArgs args, short sipCode) {
		Log.d(TAG,"broadcastInviteEvent 1");
		final Intent intent = new Intent(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		intent.putExtra(NgnInviteEventArgs.EXTRA_EMBEDDED, args);
		intent.putExtra(NgnInviteEventArgs.EXTRA_SIPCODE, sipCode);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	
	private void broadcastTransferRequestEvent(NgnInviteEventArgs args, String referToUri) {
		final Intent intent = new Intent(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		intent.putExtra(NgnInviteEventArgs.EXTRA_EMBEDDED, args);
		intent.putExtra(NgnInviteEventArgs.EXTRA_REFERTO_URI, referToUri);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastInviteEvent(NgnInviteEventArgs args) {
		Log.d(TAG,"broadcastInviteEvent 2");
		final Intent intent = new Intent(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		intent.putExtra(NgnInviteEventArgs.EXTRA_EMBEDDED, args);
		intent.putExtra(NgnInviteEventArgs.EXTRA_SIPCODE, 0);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	private void broadcastMessagingEvent(NgnMessagingEventArgs args,
			String remoteParty, String date) {
		final Intent intent = new Intent(
				NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
		intent.putExtra(NgnMessagingEventArgs.EXTRA_REMOTE_PARTY, remoteParty);
		intent.putExtra(NgnMessagingEventArgs.EXTRA_DATE, date);
		intent.putExtra(NgnMessagingEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	private void broadcastMessagingLocationEvent(org.doubango.ngn.events.NgnMessagingLocationEventArgs args,
										 String remoteParty, String date) {
		final Intent intent = new Intent(
				org.doubango.ngn.events.NgnMessagingLocationEventArgs.ACTION_MESSAGING_LOCATION_EVENT);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingLocationEventArgs.EXTRA_REMOTE_PARTY, remoteParty);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingLocationEventArgs.EXTRA_DATE, date);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingLocationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	//MCPTT MBMS
	private void broadcastMessagingMbmsEvent(org.doubango.ngn.events.NgnMessagingMbmsEventArgs args,
												 String remoteParty, String date) {
		final Intent intent = new Intent(
				org.doubango.ngn.events.NgnMessagingMbmsEventArgs.ACTION_MESSAGING_MBMS_EVENT);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingMbmsEventArgs.EXTRA_REMOTE_PARTY, remoteParty);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingMbmsEventArgs.EXTRA_DATE, date);
		intent.putExtra(org.doubango.ngn.events.NgnMessagingMbmsEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastMessagingAffiliationEvent(NgnMessagingAffiliationEventArgs args,
												 String remoteParty, String date) {
		final Intent intent = new Intent(
				NgnMessagingAffiliationEventArgs.ACTION_MESSAGING_AFFILIATION_EVENT);
		intent.putExtra(NgnMessagingAffiliationEventArgs.EXTRA_REMOTE_PARTY, remoteParty);
		intent.putExtra(NgnMessagingAffiliationEventArgs.EXTRA_DATE, date);
		intent.putExtra(NgnMessagingAffiliationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}


	private void broadcastPublicationEvent(NgnPublicationEventArgs args) {
		final Intent intent = new Intent(
				NgnPublicationEventArgs.ACTION_PUBLICATION_EVENT);
		intent.putExtra(NgnPublicationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastPublicationAffiliationEvent(NgnPublicationAffiliationEventArgs args) {
		final Intent intent = new Intent(
				NgnPublicationAffiliationEventArgs.ACTION_PUBLICATION_AFFILIATION_EVENT);
		intent.putExtra(NgnPublicationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastSubscriptionEvent(NgnSubscriptionEventArgs args) {
		final Intent intent = new Intent(
				NgnSubscriptionEventArgs.ACTION_SUBSCRIBTION_EVENT);
		intent.putExtra(NgnSubscriptionEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	private void broadcastSubscriptionAffiliationEvent(NgnSubscriptionAffiliationEventArgs args) {
		final Intent intent = new Intent(
				NgnSubscriptionAffiliationEventArgs.ACTION_SUBSCRIBTION_AFFILIATION_EVENT);
		intent.putExtra(NgnSubscriptionAffiliationEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	private void broadcastSubscriptionCMSEvent(org.doubango.ngn.events.NgnSubscriptionCMSEventArgs args) {
		final Intent intent = new Intent(
				org.doubango.ngn.events.NgnSubscriptionCMSEventArgs.ACTION_SUBSCRIBTION_CMS_EVENT);
		intent.putExtra(org.doubango.ngn.events.NgnSubscriptionCMSEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}
	private void broadcastSubscriptionGMSEvent(org.doubango.ngn.events.NgnSubscriptionGMSEventArgs args) {
		final Intent intent = new Intent(
				org.doubango.ngn.events.NgnSubscriptionGMSEventArgs.ACTION_SUBSCRIBTION_GMS_EVENT);
		intent.putExtra(org.doubango.ngn.events.NgnSubscriptionGMSEventArgs.EXTRA_EMBEDDED, args);
		NgnApplication.getContext().sendBroadcast(intent);
	}

	@Override
	public void onSetProfile() {
		configureProfile(NgnApplication.getContext());
	}


	static class MySipCallback extends SipCallback{
		private  final NgnSipService mSipService;
		private org.doubango.ngn.services.location.IMyLocalizationService mLocalizationService=null;
		private IMyAffiliationService mAffiliationService;
		private org.doubango.ngn.services.cms.IMyCMSService mCMSService;
		private org.doubango.ngn.services.gms.IMyGMSService mGMSService;
		private INgnConfigurationService mConfigurationService;
		private org.doubango.ngn.services.mbms.IMyMbmsService mMbmsService;
		private Context context;

		private MySipCallback(NgnSipService sipService,Context context) {
			super();
			this.mSipService=sipService;
			this.context=context;
		}


		public void setLocalizationService(org.doubango.ngn.services.location.IMyLocalizationService mLocalizationService) {
			this.mLocalizationService = mLocalizationService;
		}

		public void setAffiliationService(IMyAffiliationService mAffiliationService) {
			this.mAffiliationService = mAffiliationService;
		}
		public void setCMSService(org.doubango.ngn.services.cms.IMyCMSService mCMSService) {
			this.mCMSService = mCMSService;
		}

		public void setGMSService(org.doubango.ngn.services.gms.IMyGMSService mGMSService) {
			this.mGMSService = mGMSService;
		}
		public void setConfigurationService(INgnConfigurationService mConfigurationService) {
			this.mConfigurationService = mConfigurationService;
		}
		public void setMbmsService(org.doubango.ngn.services.mbms.IMyMbmsService mMbmsService) {
			this.mMbmsService = mMbmsService;
		}


		@Override
		public int OnDialogEvent(DialogEvent e) {
			final String phrase = e.getPhrase();
			final short eventCode = e.getCode();
			final short sipCode;
			final SipSession session = e.getBaseSession();

			if (session == null) {
				return 0;
			}

			final long sessionId = new Long(session.getId());
			final SipMessage message = e.getSipMessage();
			NgnSipSession mySession = null;

			sipCode = (message != null && message.isResponse()) ? message
					.getResponseCode() : eventCode;

			Log.d(TAG,
					String.format("OnDialogEvent (%s,%d)", phrase, sessionId));

			switch (eventCode) {
			// == Connecting ==
			case tinyWRAPConstants.tsip_event_code_dialog_connecting: {
				// Registration
				if (mSipService.mRegSession != null
						&& mSipService.mRegSession.getId() == sessionId) {
					mSipService.mRegSession
							.setConnectionState(ConnectionState.CONNECTING);
					mSipService
							.broadcastRegistrationEvent(new NgnRegistrationEventArgs(
									sessionId,
									NgnRegistrationEventTypes.REGISTRATION_INPROGRESS,
									eventCode, phrase));
				}
				// Audio/Video/MSRP(Chat, FileTransfer)
				else if (((mySession = NgnAVSession.getSession(sessionId)) != null)
						|| ((mySession = NgnMsrpSession.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTING);
					((NgnInviteSession) mySession)
							.setState(InviteState.INPROGRESS);
					mSipService.broadcastInviteEvent(new NgnInviteEventArgs(
							sessionId, NgnInviteEventTypes.INPROGRESS,
							((NgnInviteSession) mySession).getMediaType(),
							phrase), sipCode);

				}
				// Publication
				else if (((mySession = NgnPublicationSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTING);
					mSipService
							.broadcastPublicationEvent(new NgnPublicationEventArgs(
									sessionId,
									NgnPublicationEventTypes.PUBLICATION_INPROGRESS,
									eventCode, phrase));
				}
				// Subscription
				else if (((mySession = NgnSubscriptionSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTING);
					mSipService
							.broadcastSubscriptionEvent(new NgnSubscriptionEventArgs(
									sessionId,
									NgnSubscriptionEventTypes.SUBSCRIPTION_INPROGRESS,
									eventCode, phrase, null, null,
									((NgnSubscriptionSession) mySession)
											.getEventPackage()));
				}

				break;
			}

			// == Connected == //
			case tinyWRAPConstants.tsip_event_code_dialog_connected: {
				// Registration
				if (mSipService.mRegSession != null
						&& mSipService.mRegSession.getId() == sessionId) {
					mSipService.mRegSession
							.setConnectionState(ConnectionState.CONNECTED);
					// Update default identity (vs barred)
					String _defaultIdentity = mSipService.mSipStack
							.getPreferredIdentity();
					if (!NgnStringUtils.isNullOrEmpty(_defaultIdentity)) {
						mSipService.setDefaultIdentity(_defaultIdentity);
					}
					if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_OK");
					mSipService
							.broadcastRegistrationEvent(new NgnRegistrationEventArgs(
									sessionId,
									NgnRegistrationEventTypes.REGISTRATION_OK,
									sipCode, phrase));
					//Register in coreIMS. Subscribe in service affiliation
					//On register to authentication service
					mCMSService.startServiceAuthenticationAfterToken(NgnApplication.getContext());

					//Start the service affiliation;
					mAffiliationService.startServiceAffiliation();
				}
				// Audio/Video/MSRP(Chat, FileTransfer)
				else if (((mySession = NgnAVSession.getSession(sessionId)) != null)
						|| ((mySession = NgnMsrpSession.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTED);
					((NgnInviteSession) mySession).setState(InviteState.INCALL);
					mSipService.broadcastInviteEvent(new NgnInviteEventArgs(
							sessionId, NgnInviteEventTypes.CONNECTED,
							((NgnInviteSession) mySession).getMediaType(),
							phrase), sipCode);
				}
				// Publication
				else if (((mySession = NgnPublicationSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTED);
					mSipService
							.broadcastPublicationEvent(new NgnPublicationEventArgs(
									sessionId,
									NgnPublicationEventTypes.PUBLICATION_OK,
									sipCode, phrase));
				}
				// Subscription
				else if (((mySession = NgnSubscriptionSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.CONNECTED);
					mSipService
							.broadcastSubscriptionEvent(new NgnSubscriptionEventArgs(
									sessionId,
									NgnSubscriptionEventTypes.SUBSCRIPTION_OK,
									sipCode, phrase, null, null,
									((NgnSubscriptionSession) mySession)
											.getEventPackage()));
				}

				break;
			}

			// == Terminating == //
			case tinyWRAPConstants.tsip_event_code_dialog_terminating: {
				// Registration
				if (mSipService.mRegSession != null
						&& mSipService.mRegSession.getId() == sessionId) {
					mSipService.mRegSession
							.setConnectionState(ConnectionState.TERMINATING);
					mSipService
							.broadcastRegistrationEvent(new NgnRegistrationEventArgs(
									sessionId,
									NgnRegistrationEventTypes.UNREGISTRATION_INPROGRESS,
									eventCode, phrase));

				}
				// Audio/Video/MSRP(Chat, FileTransfer)
				else if (((mySession = NgnAVSession.getSession(sessionId)) != null)
						|| ((mySession = NgnMsrpSession.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.TERMINATING);
					((NgnInviteSession) mySession)
							.setState(InviteState.TERMINATING);
					mSipService.broadcastInviteEvent(new NgnInviteEventArgs(
							sessionId, NgnInviteEventTypes.TERMWAIT,
							((NgnInviteSession) mySession).getMediaType(),
							phrase), sipCode);
				}
				// Publication
				else if (((mySession = NgnPublicationSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.TERMINATING);
					mSipService
							.broadcastPublicationEvent(new NgnPublicationEventArgs(
									sessionId,
									NgnPublicationEventTypes.UNPUBLICATION_INPROGRESS,
									eventCode, phrase));
				}
				// Subscription
				else if (((mySession = NgnSubscriptionSession
						.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.TERMINATING);
					mSipService
							.broadcastSubscriptionEvent(new NgnSubscriptionEventArgs(
									sessionId,
									NgnSubscriptionEventTypes.UNSUBSCRIPTION_INPROGRESS,
									eventCode, phrase, null, null,
									((NgnSubscriptionSession) mySession)
											.getEventPackage()));
				}

				break;
			}

			// == Terminated == //
			case tinyWRAPConstants.tsip_event_code_dialog_terminated: {
				// Registration
				if (mSipService.mRegSession != null
						&& mSipService.mRegSession.getId() == sessionId) {
					mSipService.mRegSession
							.setConnectionState(ConnectionState.TERMINATED);
					mSipService
							.broadcastRegistrationEvent(new NgnRegistrationEventArgs(
									sessionId,
									NgnRegistrationEventTypes.UNREGISTRATION_OK,
									sipCode, phrase));
					//Stop service Location;



					//All services are cleaned to avoid problems in the event of change of profile.
					NgnEngine.getInstance().clearServices();

					/*
					 * Stop the stack (as we are already in the stack-thread,
					 * then do it in a new thread)
					 */
					new Thread(new Runnable() {
						public void run() {
							if (mSipService.mSipStack.getState() == STACK_STATE.STARTING
									|| mSipService.mSipStack.getState() == STACK_STATE.STARTED) {
								mSipService.mSipStack.stop();
							}
						}
					}).start();

				}
				// PagerMode IM
				else if (NgnMessagingSession.hasSession(sessionId)) {
					NgnMessagingSession.releaseSession(sessionId);
				}
				// Audio/Video/MSRP(Chat, FileTransfer)
				else if (((mySession = NgnAVSession.getSession(sessionId)) != null)
						|| ((mySession = NgnMsrpSession.getSession(sessionId)) != null)) {
					mySession.setConnectionState(ConnectionState.TERMINATED);
					((NgnInviteSession) mySession)
							.setState(InviteState.TERMINATED);
					mSipService.broadcastInviteEvent(new NgnInviteEventArgs(
							sessionId, NgnInviteEventTypes.TERMINATED,
							((NgnInviteSession) mySession).getMediaType(),
							phrase), sipCode);
					if (mySession instanceof NgnAVSession) {
						NgnAVSession.releaseSession((NgnAVSession) mySession);
					} else if (mySession instanceof NgnMsrpSession) {
						NgnMsrpSession
								.releaseSession((NgnMsrpSession) mySession);
					}
				}
				// Publication
				else if (((mySession = NgnPublicationSession
						.getSession(sessionId)) != null)) {
					ConnectionState previousConnState = mySession
							.getConnectionState();
					mySession.setConnectionState(ConnectionState.TERMINATED);
					mSipService
							.broadcastPublicationEvent(new NgnPublicationEventArgs(
									sessionId,
									(previousConnState == ConnectionState.TERMINATING) ? NgnPublicationEventTypes.UNPUBLICATION_OK
											: NgnPublicationEventTypes.PUBLICATION_NOK,
									sipCode, phrase));
				}
				// Subscription
				else if (((mySession = NgnSubscriptionSession
						.getSession(sessionId)) != null)) {
					ConnectionState previousConnState = mySession
							.getConnectionState();

					mySession.setConnectionState(ConnectionState.TERMINATED);
					mSipService
							.broadcastSubscriptionEvent(new NgnSubscriptionEventArgs(
									sessionId,
									(previousConnState == ConnectionState.TERMINATING) ? NgnSubscriptionEventTypes.UNSUBSCRIPTION_OK
											: NgnSubscriptionEventTypes.SUBSCRIPTION_NOK,
									sipCode, phrase, null, null,
									((NgnSubscriptionSession) mySession)
											.getEventPackage()));
				}
				break;
			}
			}

			return 0;
		}

		private int newCallProcess(final short code,InviteSession session,SipMessage message,InviteEvent e,final tsip_invite_event_type_t type,final String phrase){
			NgnInviteEventTypes typeCode=NgnInviteEventTypes.INCOMING;
			 if(BuildConfig.DEBUG) Log.i(TAG, "OnINVITEevent");
			if (session != null) /* As we are not owners, the session MUST be null */{
				Log.e(TAG, "Invalid incoming session");
				session.hangup(); // To avoid another callback event
				return -1;
			}


			if (message == null){
				Log.e(TAG,"Invalid message");
				return -1;
			}
			if(tsip_event_code_dialog_connected==code){
				typeCode=NgnInviteEventTypes.CONNECTED;
			}

			final twrap_media_type_t sessionType = e.getMediaType();
			if (sessionType == twrap_media_type_t.twrap_media_msrp) {
				if ((session = e.takeMsrpSessionOwnership()) == null){
					Log.e(TAG,"Failed to take MSRP session ownership");
					return -1;
				}

				NgnMsrpSession msrpSession = NgnMsrpSession.takeIncomingSession(mSipService.getSipStack(),
						(MsrpSession)session, message);
				if (msrpSession == null){
					Log.e(TAG,"Failed to create new session");
					session.hangup();
					session.delete();
					return 0;
				}
				mSipService.broadcastInviteEvent(new NgnInviteEventArgs(msrpSession.getId(), typeCode, msrpSession.getMediaType(), phrase));
			}
			else if ((sessionType == twrap_media_type_t.twrap_media_audio) ||
					(sessionType == twrap_media_type_t.twrap_media_audio_video) ||
					(sessionType == twrap_media_type_t.twrap_media_audiovideo) ||
					(sessionType == twrap_media_type_t.twrap_media_video) ||
					(sessionType.swigValue() == (twrap_media_type_t.twrap_media_audio.swigValue() | twrap_media_type_t.twrap_media_t140.swigValue())) ||
					(sessionType.swigValue() == (twrap_media_type_t.twrap_media_audio.swigValue() | twrap_media_type_t.twrap_media_video.swigValue() | twrap_media_type_t.twrap_media_t140.swigValue())) ||
					(sessionType == twrap_media_type_t.twrap_media_t140)) {
				if ((session = e.takeCallSessionOwnership()) == null) {
					Log.e(TAG,"Failed to take audio/video session ownership");
					return -1;
				}
				final NgnInviteEventTypes eType = type == tsip_invite_event_type_t.tsip_i_newcall ? typeCode : NgnInviteEventTypes.REMOTE_TRANSFER_INPROGESS;
				final NgnAVSession avSession = NgnAVSession.takeIncomingSession(mSipService.getSipStack(), (CallSession)session, sessionType, message);
				mSipService.broadcastInviteEvent(new NgnInviteEventArgs(avSession.getId(), eType, avSession.getMediaType(), phrase));
			}


			else if((sessionType.swigValue() & twrap_media_type_t.twrap_media_audio_ptt_mcptt.swigValue())  == twrap_media_type_t.twrap_media_audio_ptt_mcptt.swigValue()){
				Log.d(TAG,"New call, type MCPTT");
				if(message!=null){
					sendContactToMbms(message.getSipHeaderValue("Contact"));
					String pUserAgentServer = message.getSipHeaderValue("User-Agent");
				}

				if ((session = e.takeCallSessionOwnership()) == null) {
					Log.e(TAG,"Failed to take audio/video session ownership");
					return -1;
				}

				final NgnInviteEventTypes eType = type == tsip_invite_event_type_t.tsip_i_newcall ? typeCode : NgnInviteEventTypes.REMOTE_TRANSFER_INPROGESS;
				final NgnAVSession avSession = NgnAVSession.takeIncomingSession(mSipService.getSipStack(), (CallSession)session, sessionType, message);
				if(typeCode==NgnInviteEventTypes.CONNECTED){
					avSession.setConnectionState(ConnectionState.CONNECTED);
					((NgnInviteSession) avSession).setState(InviteState.INCALL);
					avSession.registerCallBacksMCPTT(context);
				}
				mSipService.broadcastInviteEvent(new NgnInviteEventArgs(avSession.getId(), eType, avSession.getMediaType(), phrase));
				return 0;
			}else {
				Log.e(TAG,"Invalid media type");
				return 0;
			}
			return -2;
		}

		@Override
		public int OnInviteEvent(InviteEvent e) {
			 final tsip_invite_event_type_t type = e.getType();
			 final short code = e.getCode();
			 final String phrase = e.getPhrase();
			 InviteSession session = e.getSession();
			 NgnSipSession mySession = null;
			 SipMessage message = e.getSipMessage();
			 if(BuildConfig.DEBUG)Log.d(TAG,"Oninvite event: code="+code+ "   body="+phrase+"  type="+type.name());
			switch (type){
                case tsip_i_newcall:
                case tsip_i_ect_newcall:
					return newCallProcess(code,session,message, e,type,phrase);

                case tsip_ao_request:
					Log.d(TAG,"tsip_ao_request");

                	// For backward compatibility keep both "RINGING" and "SIP_RESPONSE"
                    if(code==180){
						Log.d(TAG,"packet RINGING");
					}else if(code/100==2){
						if(message!=null){
							String pUserAgentServer = message.getSipHeaderValue("User-Agent");
							sendContactToMbms(message.getSipHeaderValue("Contact"));
						}
					}
					if (code == 180 && session != null){
						Log.d(TAG,"rcv RINGING");
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
							Log.d(TAG,"send broadcast RINGING");
							mSipService.broadcastInviteEvent(new NgnInviteEventArgs(mySession.getId(), NgnInviteEventTypes.RINGING, ((NgnInviteSession)mySession).getMediaType(), phrase), code);
                    	}
                    }
                    int typeCode=-1;
                    if(((typeCode=(int)(code/100))==4 || typeCode==5 || typeCode==6) && session != null){
						Log.e(TAG,"Error in invite event" +typeCode);
						switch (typeCode){
							case 4:
								Log.e(TAG,"Error: Client Failure Responses");
								break;
							case 5:
								Log.e(TAG,"Error: Server Failure Responses");
								break;
							case 6:
								Log.e(TAG,"Error: Global Failure Responses");
								break;
							default:
								break;
						}
						if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
							Log.d(TAG,"send broadcast ERROR in INVITE");
							mSipService.broadcastInviteEvent(new NgnInviteEventArgs(mySession.getId(), NgnInviteEventTypes.ERROR_INVITE, ((NgnInviteSession)mySession).getMediaType(), phrase,code), code);
						}
					}

                    if(session != null){
						Log.e(TAG,"RINGING, session is null");
						Log.d(TAG,"Code response:"+code);
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(mySession.getId(), NgnInviteEventTypes.SIP_RESPONSE, ((NgnInviteSession)mySession).getMediaType(), phrase), code);
                    	}//In this point detected the
                    }
                    break;

                case tsip_i_request:
                    {
						Log.d(TAG,"tsip_i_request");
                    	final SipMessage sipMessage = e.getSipMessage();
                    	if(sipMessage != null && session != null && ((mySession = NgnAVSession.getSession(session.getId())) != null)){
							switch (sipMessage.getRequestType()){
								case tsip_INFO:
									final String contentType = sipMessage.getSipHeaderValue("c");
									if(NgnStringUtils.equals(contentType, NgnContentType.DOUBANGO_DEVICE_INFO, true)){
										final byte content[] = sipMessage.getSipContent();
										if(content != null){
											final String values[] = new String(content).split("\r\n");
											for(String value : values){
												if(value == null) continue;
												final String kvp[] = value.split(":");
												if(kvp.length == 2){
													if(NgnStringUtils.equals(kvp[0], "orientation", true)){
														if(NgnStringUtils.equals(kvp[1], "landscape", true)){
															((NgnInviteSession)mySession).getRemoteDeviceInfo().setOrientation(Orientation.LANDSCAPE);
														}
														else if(NgnStringUtils.equals(kvp[1], "portrait", true)){
															((NgnInviteSession)mySession).getRemoteDeviceInfo().setOrientation(Orientation.PORTRAIT);
														}
													}
													else if(NgnStringUtils.equals(kvp[0], "lang", true)){
														((NgnInviteSession)mySession).getRemoteDeviceInfo().setLang(kvp[1]);
													}
												}
											}
											mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_DEVICE_INFO_CHANGED, ((NgnInviteSession)mySession).getMediaType(), phrase));
										}else{
											Log.e(TAG, "Error processing Invite request 4 ");
										}
									}else{
										Log.e(TAG, "Error processing Invite request 3");
									}
									break;
								case tsip_BYE:
									Log.d(TAG,"the message SIP receive is BYE");
								default:
									if(BuildConfig.DEBUG)Log.e(TAG, "Error processing Invite request 2 type:"+sipMessage.getRequestType().name());
									break;
							}
                    	}else{
							Log.i(TAG, "Error processing Invite request");
						}
                        break;
                    }
                case tsip_o_ect_trying:
	                {
						Log.d(TAG,"tsip_o_ect_trying");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_TRANSFER_TRYING, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                	break;
	                }
	            case tsip_o_ect_accepted:
	                {
						Log.d(TAG,"tsip_o_ect_accepted");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_TRANSFER_ACCEPTED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                    break;
	                }
	            case tsip_o_ect_completed:
	                {
						Log.d(TAG,"tsip_o_ect_completed");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_TRANSFER_COMPLETED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                    break;
	                }
	            case tsip_o_ect_failed:
	                {
						Log.d(TAG,"tsip_o_ect_failed");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_TRANSFER_FAILED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                    break;
	                }
	            case tsip_o_ect_notify:
	            case tsip_i_ect_notify:
	                {
						Log.d(TAG,"tsip_o_ect_notify or tsip_i_ect_notify");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		NgnInviteEventTypes eType = (type == tsip_invite_event_type_t.tsip_o_ect_notify ? NgnInviteEventTypes.LOCAL_TRANSFER_NOTIFY : NgnInviteEventTypes.REMOTE_TRANSFER_NOTIFY);
	                		NgnInviteEventArgs args = new NgnInviteEventArgs(session.getId(), eType, ((NgnInviteSession)mySession).getMediaType(), phrase);
	                		mSipService.broadcastInviteEvent(args, code);
	                	}
	                	break;
	                }
	            case tsip_i_ect_requested:
	                {
						Log.d(TAG,"tsip_i_ect_requested");
						if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
		                	final SipMessage sipMessage = e.getSipMessage();
		                    if (sipMessage != null)
		                    {
		                        if (sipMessage.getRequestType() == tsip_request_type_t.tsip_REFER)
		                        {
		                            String referToUri = sipMessage.getSipHeaderValue("refer-to");
		                            if (!NgnStringUtils.isNullOrEmpty(referToUri))
		                            {
		                            	NgnInviteEventArgs args = new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_TRANSFER_REQUESTED, ((NgnInviteSession)mySession).getMediaType(), phrase);
		                            	mSipService.broadcastTransferRequestEvent(args, referToUri);
		                            }
		                        }
		                    }
	                	}
	                    
	                    break;
	                }
	            case tsip_i_ect_failed:
	                {
						Log.d(TAG,"tsip_i_ect_failed");
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_TRANSFER_FAILED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                	break;
	                }
	            case tsip_i_ect_completed:
	                {
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_TRANSFER_COMPLETED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                	break;
	                }
                case tsip_m_early_media:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		((NgnInviteSession)mySession).setState(InviteState.EARLY_MEDIA);
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.EARLY_MEDIA, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                case tsip_m_local_hold_ok:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		((NgnInviteSession)mySession).setLocalHold(true);
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_HOLD_OK, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;

                    }
                case tsip_m_updating:
	                {
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.MEDIA_UPDATING, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
	                	break;
	                }
                case tsip_m_updated:
	                {
	                	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
	                		if(mySession instanceof NgnAVSession){
	                			NgnAVSession.handleMediaUpdate(mySession.getId(), e.getMediaType());
	                		}
	                		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.MEDIA_UPDATED, ((NgnInviteSession)mySession).getMediaType(), phrase));
	                	}
	                	break;
	                }
                case tsip_m_local_hold_nok:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_HOLD_NOK, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                case tsip_m_local_resume_ok:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		((NgnInviteSession)mySession).setLocalHold(false);
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_RESUME_OK, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                case tsip_m_local_resume_nok:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.LOCAL_RESUME_NOK, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                case tsip_m_remote_hold:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		((NgnInviteSession)mySession).setRemoteHold(true);
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_HOLD, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                case tsip_m_remote_resume:
                    {
                    	if (((mySession = NgnAVSession.getSession(session.getId())) != null) || ((mySession = NgnMsrpSession.getSession(session.getId())) != null)){
                    		((NgnInviteSession)mySession).setRemoteHold(false);
                    		mSipService.broadcastInviteEvent(new NgnInviteEventArgs(session.getId(), NgnInviteEventTypes.REMOTE_RESUME, ((NgnInviteSession)mySession).getMediaType(), phrase));
                    	}
                        break;
                    }
                default:
                    {
                    	break;
                    }
            }
			
			return 0;
		}
		private void sendContactToMbms(String contact){
			if(contact==null || contact.isEmpty()){
				Log.e(TAG, "Contact in this call isn´t valid");
				return;
			}
			Intent intent=new Intent();
			Log.d(TAG,"New call MBMS and Contact is: "+contact);
			intent.putExtra(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_SESSION_ID_MBMS,contact);
			intent.setAction(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_SESSION_ID_ACTION);
			NgnApplication.getContext().sendBroadcast(intent);
		}


		@Override
		public int OnMessagingEvent(MessagingEvent e) {
			final tsip_message_event_type_t type = e.getType();
			MessagingSession _session;
			final SipMessage message;

			switch (type) {
			case tsip_ao_message:
				_session = e.getSession();
				message = e.getSipMessage();
				short code = e.getCode();
				if (_session != null && code >= 200 && message != null) {
					mSipService
							.broadcastMessagingEvent(
									new NgnMessagingEventArgs(
											_session.getId(),
											(code >= 200 && code <= 299) ? NgnMessagingEventTypes.SUCCESS
													: NgnMessagingEventTypes.FAILURE,
											e.getPhrase(), new byte[0], null),
									message.getSipHeaderValue("f"),
									NgnDateTimeUtils.now());
				}
				break;
			case tsip_i_message:
				message = e.getSipMessage();
				_session = e.getSession();
				NgnMessagingSession imSession;
				if (_session == null) {
					/*
					 * "Server-side-session" e.g. Initial MESSAGE sent by the
					 * remote party
					 */
					_session = e.takeSessionOwnership();
				}

				if (_session == null) {
					Log.e(NgnSipService.TAG, "Failed to take session ownership");
					return -1;
				}
				imSession = NgnMessagingSession.takeIncomingSession(
						mSipService.mSipStack, _session, message);
				if (message == null) {
					imSession.reject();
					imSession.decRef();
					return 0;
				}

				String from = message.getSipHeaderValue("f");
				final String contentType = message.getSipHeaderValue("c");
				final byte[] bytes = message.getSipContent();
				byte[] content = null;

				if (bytes == null || bytes.length == 0) {
					Log.e(NgnSipService.TAG, "Invalid MESSAGE");
					imSession.reject();
					imSession.decRef();
					return 0;
				}

				imSession.accept();

				if (NgnStringUtils.equals(contentType, NgnContentType.SMS_3GPP,
						true)) {
					/* ==== 3GPP SMSIP === */
					ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
					buffer.put(bytes);
					SMSData smsData = SMSEncoder.decode(buffer,
							buffer.capacity(), false);
					if (smsData != null) {
						twrap_sms_type_t smsType = smsData.getType();
						if (smsType == twrap_sms_type_t.twrap_sms_type_rpdata) {
							/* === We have received a RP-DATA message === */
							long payLength = smsData.getPayloadLength();
							String SMSC = message
									.getSipHeaderValue("P-Asserted-Identity");
							String SMSCPhoneNumber;
							String origPhoneNumber = smsData.getOA();

							/* Destination address */
							if (origPhoneNumber != null) {
								from = NgnUriUtils
										.makeValidSipUri(origPhoneNumber,NgnApplication.getContext());
							} else if ((origPhoneNumber = NgnUriUtils
									.getValidPhoneNumber(from)) == null) {
								Log.e(NgnSipService.TAG,
										"Invalid destination address");
								return 0;
							}

							/*
							 * SMS Center 3GPP TS 24.341 - 5.3.2.4 Sending a
							 * delivery report The address of the IP-SM-GW is
							 * received in the P-Asserted-Identity header in the
							 * SIP MESSAGE request including the delivered short
							 * message.
							 */
							if ((SMSCPhoneNumber = NgnUriUtils
									.getValidPhoneNumber(SMSC)) == null) {
								SMSC = NgnEngine
										.getInstance()
										.getConfigurationService()
										.getString(
												NgnConfigurationEntry.RCS_SMSC,
												NgnConfigurationEntry.DEFAULT_RCS_SMSC);
								if ((SMSCPhoneNumber = NgnUriUtils
										.getValidPhoneNumber(SMSC)) == null) {
									Log.e(NgnSipService.TAG,
											"Invalid IP-SM-GW address");
									return 0;
								}
							}

							if (payLength > 0) {
								/* Send RP-ACK */
								RPMessage rpACK = SMSEncoder.encodeACK(
										smsData.getMR(), SMSCPhoneNumber,
										origPhoneNumber, false);
								if (rpACK != null) {
									long ack_len = rpACK.getPayloadLength();
									if (ack_len > 0) {
										buffer = ByteBuffer
												.allocateDirect((int) ack_len);
										long len = rpACK.getPayload(buffer,
												buffer.capacity());
										MessagingSession m = new MessagingSession(
												mSipService.getSipStack());
										m.setToUri(SMSC);
										m.addHeader("Content-Type",
												NgnContentType.SMS_3GPP);
										m.addHeader(
												"Content-Transfer-Encoding",
												"binary");
										m.addCaps("+g.3gpp.smsip");
										m.send(buffer, len);
										m.delete();
									}
									rpACK.delete();
								}

								/* Get ascii content */
								buffer = ByteBuffer
										.allocateDirect((int) payLength);
								content = new byte[(int) payLength];
								smsData.getPayload(buffer, buffer.capacity());
								buffer.get(content);
							} else {
								/* Send RP-ERROR */
								RPMessage rpError = SMSEncoder.encodeError(
										smsData.getMR(), SMSCPhoneNumber,
										origPhoneNumber, false);
								if (rpError != null) {
									long err_len = rpError.getPayloadLength();
									if (err_len > 0) {
										buffer = ByteBuffer
												.allocateDirect((int) err_len);
										long len = rpError.getPayload(buffer,
												buffer.capacity());

										MessagingSession m = new MessagingSession(
												mSipService.getSipStack());
										m.setToUri(SMSC);
										m.addHeader("Content-Type",
												NgnContentType.SMS_3GPP);
										m.addHeader("Transfer-Encoding",
												"binary");
										m.addCaps("+g.3gpp.smsip");
										m.send(buffer, len);
										m.delete();
									}
									rpError.delete();
								}
							}
						} else {
							/* === We have received any non-RP-DATA message === */
							if (smsType == twrap_sms_type_t.twrap_sms_type_ack) {
								/*
								 * Find message from the history (by MR) and
								 * update it's status
								 */
								Log.d(NgnSipService.TAG, "RP-ACK");
							} else if (smsType == twrap_sms_type_t.twrap_sms_type_error) {
								/*
								 * Find message from the history (by MR) and
								 * update it's status
								 */
								Log.d(NgnSipService.TAG, "RP-ERROR");
							}
						}
					}
				} else {
					/* ==== text/plain or any other === */
					content = bytes;
				}

				/* Alert the user and add message to history */
				if (content != null) {
					mSipService
							.broadcastMessagingEvent(
									new NgnMessagingEventArgs(_session.getId(),
											NgnMessagingEventTypes.INCOMING, e
													.getPhrase(), content,
											contentType), from,
									NgnDateTimeUtils.now());
				}

				break;
			}

			return 0;
		}
		@Override
		public int OnMessagingLocationEvent(org.doubango.tinyWRAP.MessagingLocationEvent e) {
			final tsip_message_event_type_t type = e.getType();
			org.doubango.tinyWRAP.MessagingLocationSession _session;
			final SipMessage message;
			Log.d(TAG,"Receive message Location");
			switch (type) {
				case tsip_ao_message:
					_session = e.getSession();
					message = e.getSipMessage();
					short code = e.getCode();
					if (_session != null && code >= 200 && message != null) {
						mSipService
								.broadcastMessagingLocationEvent(
										new org.doubango.ngn.events.NgnMessagingLocationEventArgs(
												_session.getId(),
												(code >= 200 && code <= 299) ? org.doubango.ngn.events.NgnMessagingLocationEventTypes.SUCCESS
														: org.doubango.ngn.events.NgnMessagingLocationEventTypes.FAILURE,
												e.getPhrase(), new byte[0], null),
										message.getSipHeaderValue("f"),
										NgnDateTimeUtils.now());
					}
					break;
				case tsip_i_message:
					message = e.getSipMessage();
					_session = e.getSession();

					org.doubango.ngn.sip.MyMessagingLocationSession imSession;
					if (_session == null) {
					/*
					 * "Server-side-session" e.g. Initial MESSAGE sent by the
					 * remote party
					 */
						_session = e.takeSessionOwnership();
					}

					if (_session == null) {
						Log.e(NgnSipService.TAG, "Failed to take session ownership");
						return -1;
					}
					imSession = org.doubango.ngn.sip.MyMessagingLocationSession.takeIncomingSession(
							mSipService.mSipStack, _session, message);
					if (message == null) {
						imSession.reject();
						imSession.decRef();
						return 0;
					}

					String from = message.getSipHeaderValue("f");
					final String contentType = message.getSipHeaderValue("c");
					final byte[] bytes = message.getSipContent();
					byte[] content = null;

					if (bytes == null || bytes.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid MESSAGE");
						imSession.reject();
						imSession.decRef();
						return 0;
					}

					//Configure P-Preferred-Identity
					String pPreferredIdentity = message.getSipHeaderValue("P-Asserted-Identity");
					if(pPreferredIdentity==null || pPreferredIdentity.isEmpty()){
						Log.e(TAG, "Invalid MESSAGE, message is not valid P-Asserted-Identity:"+pPreferredIdentity);
						imSession.reject();
						imSession.decRef();
						return 0;
					}
					mSipService.getSipStack().setServerLocation(pPreferredIdentity);
					imSession.accept();
					Intent intent=new Intent();
					intent.setAction(org.doubango.ngn.services.location.IMyLocalizationService.LOCATION_ACTION);
					intent.putExtra(org.doubango.ngn.services.location.IMyLocalizationService.LOCATION_NEWLOCATION_INFO,bytes);
					NgnApplication.getContext().sendBroadcast(intent);

					break;
			}

			return 0;
		}


		@Override
		public int OnMessagingMbmsEvent(org.doubango.tinyWRAP.MessagingMbmsEvent e) {
			final tsip_message_event_type_t type = e.getType();
			org.doubango.tinyWRAP.MessagingMbmsSession _session;
			final SipMessage message;
			Log.d(TAG,"Receive message MBMS");
			switch (type) {
				case tsip_ao_message:
					_session = e.getSession();
					message = e.getSipMessage();

					short code = e.getCode();
					if (_session != null && code >= 200 && message != null) {
						mSipService
								.broadcastMessagingMbmsEvent(
										new org.doubango.ngn.events.NgnMessagingMbmsEventArgs(
												_session.getId(),
												(code >= 200 && code <= 299) ? org.doubango.ngn.events.NgnMessagingMbmsEventTypes.SUCCESS
														: org.doubango.ngn.events.NgnMessagingMbmsEventTypes.FAILURE,
												e.getPhrase(), new byte[0], null),
										message.getSipHeaderValue("f"),
										NgnDateTimeUtils.now());
					}
					break;
				case tsip_i_message:
					message = e.getSipMessage();
					_session = e.getSession();
					org.doubango.ngn.sip.MyMessagingMbmsSession imSession;
					if (_session == null) {
					/*
					 * "Server-side-session" e.g. Initial MESSAGE sent by the
					 * remote party
					 */
						_session = e.takeSessionOwnership();
					}





					if (_session == null) {
						Log.e(NgnSipService.TAG, "Failed to take session ownership");
						return -1;
					}
					imSession = org.doubango.ngn.sip.MyMessagingMbmsSession.takeIncomingSession(
							mSipService.mSipStack, _session, message);
					if (message == null) {
						imSession.reject();
						imSession.decRef();
						return 0;
					}

					String from = message.getSipHeaderValue("f");
					final String contentType = message.getSipHeaderValue("c");
					final byte[] bytes=message.getSipContentMbms();
					byte[] content = null;

					if (bytes == null || bytes.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid MESSAGE");
						imSession.reject();
						imSession.decRef();
						return 0;
					}

					//Configure P-Preferred-Identity
					String pPreferredIdentity = message.getSipHeaderValue("P-Asserted-Identity");
					if(pPreferredIdentity==null || pPreferredIdentity.isEmpty()){
						Log.e(TAG, "Invalid MESSAGE, message is not valid P-Asserted-Identity:"+pPreferredIdentity);
						imSession.reject();
						imSession.decRef();
						return 0;
					}
					SdpMessage sdpMessage = message.getSdpMessage();
					int portType1 = sdpMessage.getSdpHeaderMPort("application", 0);
					String dataSdpAddr = sdpMessage.getSdpHeaderCAddr("application", 0);
					String dataSdpAddrType = sdpMessage.getSdpHeaderCAddrType("application", 0);

					int portType2 = sdpMessage.getSdpHeaderMPort("application", 1);
					String dataSdpAddr2 = sdpMessage.getSdpHeaderCAddr("application", 1);
					String dataSdpAddrType2 = sdpMessage.getSdpHeaderCAddrType("application", 1);
					int portMBMSManager=-1;
					String ipMBMSManager=null;
					if (portType1 != 9)
					{
						portMBMSManager=portType1;
						ipMBMSManager=dataSdpAddr;
					}
					else if(portType2 !=9)
					{
						portMBMSManager=portType2;
						ipMBMSManager=dataSdpAddr2;
					}else{
						Log.e(TAG,"Error in SDP MESSAGE MBMS");
					}
					if(ipMBMSManager!=null && pPreferredIdentity!=null && !pPreferredIdentity.trim().isEmpty()){
						mSipService.getSipStack().setServerMbms(pPreferredIdentity);
						imSession.accept();
						Intent intent=new Intent();
						Log.d(TAG,"New Message MBMS, address manager: "+ipMBMSManager+":"+portMBMSManager);
						intent.putExtra(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_NEWMESSAGE_MBMS,bytes);
						intent.putExtra(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_PORT_MANAGER_MBMS,portMBMSManager);
						intent.putExtra(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_IP_MANAGER_MBMS,ipMBMSManager);
						intent.putExtra(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_P_ASSERTED_IDENTITY,pPreferredIdentity);

						intent.setAction(org.doubango.ngn.services.mbms.IMyMbmsService.MBMS_ACTION);
						NgnApplication.getContext().sendBroadcast(intent);
					}


					break;
			}

			return 0;
		}
		@Override
		public int OnMessagingAffiliationEvent(MessagingAffiliationEvent e) {
			final tsip_message_event_type_t type = e.getType();
			MessagingAffiliationSession _session;
			final SipMessage message;
			Log.d(TAG,"Receive message Affiliation");
			switch (type) {
				case tsip_ao_message:
					_session = e.getSession();
					message = e.getSipMessage();
					short code = e.getCode();
					if (_session != null && code >= 200 && message != null) {
						mSipService
								.broadcastMessagingAffiliationEvent(
										new NgnMessagingAffiliationEventArgs(
												_session.getId(),
												(code >= 200 && code <= 299) ? NgnMessagingAffiliationEventTypes.SUCCESS
														: NgnMessagingAffiliationEventTypes.FAILURE,
												e.getPhrase(), new byte[0], null),
										message.getSipHeaderValue("f"),
										NgnDateTimeUtils.now());
					}
					break;
				case tsip_i_message:
					message = e.getSipMessage();
					_session = e.getSession();
					MyMessagingAffiliationSession imSession;
					if (_session == null) {
					/*
					 * "Server-side-session" e.g. Initial MESSAGE sent by the
					 * remote party
					 */
						_session = e.takeSessionOwnership();
					}





					if (_session == null) {
						Log.e(NgnSipService.TAG, "Failed to take session ownership");
						return -1;
					}
					imSession = MyMessagingAffiliationSession.takeIncomingSession(
							mSipService.mSipStack, _session, message);
					if (message == null) {
						imSession.reject();
						imSession.decRef();
						return 0;
					}

					String from = message.getSipHeaderValue("f");
					final String contentType = message.getSipHeaderValue("c");
					final byte[] bytes = message.getSipContent();
					byte[] content = null;

					if (bytes == null || bytes.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid MESSAGE");
						imSession.reject();
						imSession.decRef();
						return 0;
					}
					imSession.accept();

					Intent intent=new Intent();
					intent.setAction(IMyAffiliationService.AFFILIATION_ACTION_MESSAGE);
					intent.putExtra(IMyAffiliationService.AFFILIATION_NEWAFFILIATION_MESSAGE,bytes);
					NgnApplication.getContext().sendBroadcast(intent);

					break;
			}

			return 0;
		}




		@Override
		public int OnStackEvent(StackEvent e) {
			// final String phrase = e.getPhrase();
			final short code = e.getCode();
			switch (code) {
			case tinyWRAPConstants.tsip_event_code_stack_started:
				mSipService.mSipStack.setState(STACK_STATE.STARTED);
				Log.d(NgnSipService.TAG, "Stack started");
				break;
			case tinyWRAPConstants.tsip_event_code_stack_failed_to_start:
				final String phrase = e.getPhrase();
				Log.e(TAG, String.format(
						"Failed to start the stack. \nAdditional info:\n%s",
						phrase));
				break;
			case tinyWRAPConstants.tsip_event_code_stack_failed_to_stop:
				Log.e(TAG, "Failed to stop the stack");
				break;
			case tinyWRAPConstants.tsip_event_code_stack_stopped:
				mSipService.mSipStack.setState(STACK_STATE.STOPPED);
				Log.d(TAG, "Stack stopped");
				break;
			case tinyWRAPConstants.tsip_event_code_stack_disconnected:
				mSipService.mSipStack.setState(STACK_STATE.DISCONNECTED);
				Log.d(TAG, "Stack disconnected");
				break;
			}
			return 0;
		}

		@Override
		public int OnSubscriptionEvent(SubscriptionEvent e) {
			final tsip_subscribe_event_type_t type = e.getType();
			SubscriptionSession _session = e.getSession();

			switch (type) {
			case tsip_i_notify: {
				final short code = e.getCode();
				final String phrase = e.getPhrase();
				final SipMessage message = e.getSipMessage();
				if (message == null || _session == null) {
					return 0;
				}
				final String contentType = message.getSipHeaderValue("c");
				final byte[] content = message.getSipContent();



				if (NgnStringUtils.equals(contentType, NgnContentType.REG_INFO,
						true)) {
					// mReginfo = content;
				} else if (NgnStringUtils.equals(contentType,
						NgnContentType.WATCHER_INFO, true)) {
					// mWInfo = content;
				}

				NgnSubscriptionSession ngnSession = NgnSubscriptionSession
						.getSession(_session.getId());
				NgnSubscriptionEventArgs eargs = new NgnSubscriptionEventArgs(
						_session.getId(),
						NgnSubscriptionEventTypes.INCOMING_NOTIFY, code,
						phrase, content, contentType,
						ngnSession == null ? EventPackageType.None : ngnSession
								.getEventPackage());
				mSipService.broadcastSubscriptionEvent(eargs);

				break;
			}

			case tsip_ao_notify:
			case tsip_i_subscribe:
			case tsip_ao_subscribe:
			case tsip_i_unsubscribe:
			case tsip_ao_unsubscribe:
			default: {
				break;
			}
			}

			return 0;
		}

		@Override
		public int OnSubscriptionGMSEvent(org.doubango.tinyWRAP.SubscriptionGMSEvent e) {
			final tsip_subscribe_event_type_t type = e.getType();
			org.doubango.tinyWRAP.SubscriptionGMSSession _session = e.getSession();
			short code ;
			String phrase ;
			SipMessage message;
			switch (type) {
				case tsip_i_notify: {
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					final String contentType = message.getSipHeaderValue("c");
					final byte[] content = message.getSipContent();

					/*if (content == null || content.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid Notify");
						return 0;
					}*/

					Intent intent=new Intent();
					intent.setAction(org.doubango.ngn.services.gms.IMyGMSService.GMS_ACTION_NOTIFY);
					intent.putExtra(org.doubango.ngn.services.gms.IMyGMSService.GMS_NEWGMS_NOTIFY,content);
					NgnApplication.getContext().sendBroadcast(intent);

					org.doubango.ngn.events.NgnSubscriptionGMSEventArgs eargs = new org.doubango.ngn.events.NgnSubscriptionGMSEventArgs(
							_session.getId(),
							org.doubango.ngn.events.NgnSubscriptionGMSEventTypes.INCOMING_NOTIFY, code,
							phrase, content, contentType,
							EventPackageType.None);
					mSipService.broadcastSubscriptionGMSEvent(eargs);

					break;
				}

				case tsip_ao_notify:
				case tsip_i_subscribe:
					break;
				case tsip_ao_subscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(org.doubango.ngn.services.gms.IMyGMSService.GMS_ACTION_SUBSCRIBE);
						if(code/100==2){
							intent.putExtra(org.doubango.ngn.services.gms.IMyGMSService.GMS_RESPONSE_SUBSCRIBE_OK,phrase);
						}else{
							intent.putExtra(org.doubango.ngn.services.gms.IMyGMSService.GMS_RESPONSE_SUBSCRIBE_ERROR,phrase);
						}
						NgnApplication.getContext().sendBroadcast(intent);


					}
					break;

				case tsip_i_unsubscribe:
					break;
				case tsip_ao_unsubscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(IMyGMSService.GMS_ACTION_UNSUBSCRIBE);
						NgnApplication.getContext().sendBroadcast(intent);
					}
					break;
				default: {
					break;
				}
			}

			return 0;
		}

		@Override
		public int OnSubscriptionCMSEvent(org.doubango.tinyWRAP.SubscriptionCMSEvent e) {
			final tsip_subscribe_event_type_t type = e.getType();
			org.doubango.tinyWRAP.SubscriptionCMSSession _session = e.getSession();
			short code ;
			String phrase ;
			SipMessage message;
			switch (type) {
				case tsip_i_notify: {
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					final String contentType = message.getSipHeaderValue("c");
					final byte[] content = message.getSipContent();

					/*if (content == null || content.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid Notify");
						return 0;
					}*/

					Intent intent=new Intent();
					intent.setAction(org.doubango.ngn.services.cms.IMyCMSService.CMS_ACTION_NOTIFY);
					intent.putExtra(org.doubango.ngn.services.cms.IMyCMSService.CMS_NEWCMS_NOTIFY,content);
					NgnApplication.getContext().sendBroadcast(intent);

					NgnSubscriptionCMSEventArgs eargs = new org.doubango.ngn.events.NgnSubscriptionCMSEventArgs(
							_session.getId(),
							org.doubango.ngn.events.NgnSubscriptionCMSEventTypes.INCOMING_NOTIFY, code,
							phrase, content, contentType,
							EventPackageType.None);
					mSipService.broadcastSubscriptionCMSEvent(eargs);

					break;
				}

				case tsip_ao_notify:
				case tsip_i_subscribe:
					break;
				case tsip_ao_subscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(org.doubango.ngn.services.cms.IMyCMSService.CMS_ACTION_SUBSCRIBE);
						if(code/100==2){
							intent.putExtra(org.doubango.ngn.services.cms.IMyCMSService.CMS_RESPONSE_SUBSCRIBE_OK,phrase);
						}else{
							intent.putExtra(org.doubango.ngn.services.cms.IMyCMSService.CMS_RESPONSE_SUBSCRIBE_ERROR,phrase);
						}
						NgnApplication.getContext().sendBroadcast(intent);


					}
					break;

				case tsip_i_unsubscribe:
					break;
				case tsip_ao_unsubscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(org.doubango.ngn.services.cms.IMyCMSService.CMS_ACTION_UNSUBSCRIBE);
						NgnApplication.getContext().sendBroadcast(intent);
					}
					break;
				default: {
					break;
				}
			}

			return 0;
		}

		@Override
		public int OnSubscriptionAffiliationEvent(SubscriptionAffiliationEvent e) {
			final tsip_subscribe_event_type_t type = e.getType();
			SubscriptionAffiliationSession _session = e.getSession();
			 short code ;
			 String phrase ;
			 SipMessage message;
			switch (type) {
				case tsip_i_notify: {
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					final String contentType = message.getSipHeaderValue("c");
					final byte[] content = message.getSipContent();

					/*if (content == null || content.length == 0) {
						Log.e(NgnSipService.TAG, "Invalid Notify");
						return 0;
					}*/

					Intent intent=new Intent();
					intent.setAction(IMyAffiliationService.AFFILIATION_ACTION_NOTIFY);
					intent.putExtra(IMyAffiliationService.AFFILIATION_NEWAFFILIATION_NOTIFY,content);
					NgnApplication.getContext().sendBroadcast(intent);

					NgnSubscriptionAffiliationEventArgs eargs = new NgnSubscriptionAffiliationEventArgs(
							_session.getId(),
							NgnSubscriptionAffiliationEventTypes.INCOMING_NOTIFY, code,
							phrase, content, contentType,
							 EventPackageType.None);
					mSipService.broadcastSubscriptionAffiliationEvent(eargs);

					break;
				}

				case tsip_ao_notify:
				case tsip_i_subscribe:
					break;
				case tsip_ao_subscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(IMyAffiliationService.AFFILIATION_ACTION_SUBSCRIBE);
						if(code/100==2){
							intent.putExtra(IMyAffiliationService.AFFILIATION_RESPONSE_SUBSCRIBE_OK,phrase);
						}else{
							intent.putExtra(IMyAffiliationService.AFFILIATION_RESPONSE_SUBSCRIBE_ERROR,phrase);
						}
						NgnApplication.getContext().sendBroadcast(intent);


					}
					break;

				case tsip_i_unsubscribe:
					break;
				case tsip_ao_unsubscribe:
					code = e.getCode();
					phrase = e.getPhrase();
					message = e.getSipMessage();
					if (message == null || _session == null) {
						return 0;
					}
					if (_session != null && code >= 200 && message != null) {
						Intent intent=new Intent();
						intent.setAction(IMyAffiliationService.AFFILIATION_ACTION_UNSUBSCRIBE);
						NgnApplication.getContext().sendBroadcast(intent);
					}
					break;
				default: {
					break;
				}
			}

			return 0;
		}



		@Override
		public int OnPublicationAffiliationEvent(PublicationAffiliationEvent e) {
			final tsip_publish_event_type_t type = e.getType();
			PublicationAffiliationSession _session = e.getSession();


			/*
			  tsip_i_publish,
			  tsip_ao_publish,
			  tsip_i_unpublish,
			  tsip_ao_unpublish;
			*/
			switch (type) {
				case tsip_i_publish:
					Log.d(TAG,"Publish received");
					break;
				case tsip_ao_publish:
					Log.d(TAG,"200OK publish received");
					break;
				case tsip_i_unpublish:
					Log.d(TAG,"Unpublish received");
					break;
				case tsip_ao_unpublish:
					Log.d(TAG,"200ok unpublish received");
					break;
				default: {
					break;
				}
			}

			return 0;
		}

		@Override
		public int OnOptionsEvent(OptionsEvent e) {
			final tsip_options_event_type_t type = e.getType();
			OptionsSession ptSession = e.getSession();

			switch (type) {
			case tsip_i_options:
				if (ptSession == null) { // New session
					if ((ptSession = e.takeSessionOwnership()) != null) {
						ptSession.accept();
						ptSession.delete();
					}
				}
				break;
			default:
				break;
			}
			return 0;
		}

	}
	@Override
	public boolean isRegisterBefore() {
		return registerBefore;
	}
	@Override
	public void setRegisterBefore(boolean registerBefore) {
		this.registerBefore = registerBefore;
	}
	@Override
	public boolean clearService(){
		return true;
	}

	//INIT AUTH
	@Override
	public String onAuthRegister(String nonce) {
		return onAuthenticationListener==null?null:onAuthenticationListener.onAuthRegister(nonce);
	}

	public  void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener){
		this.onAuthenticationListener=onAuthenticationListener;
	}

	//END AUTH
}
