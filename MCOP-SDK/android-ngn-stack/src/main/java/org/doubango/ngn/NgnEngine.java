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
package org.doubango.ngn;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import org.doubango.utils.AndroidUtils;
import org.doubango.utils.Utils;

import java.io.File;



/**
 * Next Generation Network Engine.
 * Main entry point to access all services (SIP, XCAP, MSRP, History, ...).
 * An instance of the engine can be had anywhere by calling the @ref getInstance() function.
 */
public class NgnEngine {
	private final static String TAG = Utils.getTAG(NgnEngine.class.getCanonicalName());
	
	protected static NgnEngine sInstance;
	private static boolean sInitialized;
	private static final String DATA_FOLDER = String.format("/data/data/%s", NgnApplication.getContext().getPackageName());
	private static final String LIBS_FOLDER = String.format("%s/lib", NgnEngine.DATA_FOLDER);
	
	protected boolean mStarted;
	protected Activity mMainActivity;
	
	protected final NotificationManager mNotifManager;
	protected final Vibrator mVibrator;
	
	protected org.doubango.ngn.services.INgnConfigurationService mConfigurationService;
	protected org.doubango.ngn.services.INgnStorageService mStorageService;
	protected org.doubango.ngn.services.INgnNetworkService mNetworkService;
	protected org.doubango.ngn.services.INgnHttpClientService mHttpClientService;
	protected org.doubango.ngn.services.INgnContactService mContactService;
	protected org.doubango.ngn.services.INgnHistoryService mHistoryService;
	protected org.doubango.ngn.services.INgnSipService mSipService;
	protected org.doubango.ngn.services.INgnSoundService mSoundService;

	private org.doubango.ngn.services.impl.ms.MyGMSService mGMSService;
	protected org.doubango.ngn.services.emergency.IMyEmergencyService mEmergencyService;
	protected org.doubango.ngn.services.location.IMyLocalizationService mLocalizationServer;
	protected org.doubango.ngn.services.affiliation.IMyAffiliationService mAffiliationServer;
	protected org.doubango.ngn.services.mbms.IMyMbmsService mMbmsServer;

	protected org.doubango.ngn.services.authentication.IMyAuthenticacionService mAuthenticationServer;
	private org.doubango.ngn.services.impl.ms.MyCMSService mCMSService;
	private org.doubango.ngn.services.profiles.IMyProfilesService mProfilesService;




	static{
		NgnEngine.initialize2();
	}




	// This function will be renamed as "initialize()" when "initialize()" gets removed
	private static void initialize2(){
		// do not add try/catch to let the app die if libraries are missing or incompatible
		if(!sInitialized){
			// See 'http://code.google.com/p/imsdroid/issues/detail?id=197' for more information
			// Load Android utils library (required to detect CPU features)
			boolean haveLibUtils = new File(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libutils_armv5te.so")).exists();
			if (haveLibUtils) { // only "armeabi-v7a" comes with "libutils.so"

				try {

					System.loadLibrary("utils_armv5te");
					Log.d(TAG,"Native code library load. utils_armv5te\n");
				} catch (UnsatisfiedLinkError e) {
					Log.e(TAG,"Native code library failed to load.\n" + e);
					System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libutils_armv5te.so"));
				}
				Log.d(TAG,"CPU_Feature="+AndroidUtils.getCpuFeatures());
				Log.d(TAG,"CPU_Family="+AndroidUtils.getCpuFamily());

				if(NgnApplication.isCpuARMv7()){
					Log.d(TAG,"isCpuARMv7()=YES");
				}else{
					Log.d(TAG,"isCpuARMv7()=NO");
				}
				if(NgnApplication.isCpuNeon()){
					Log.d(TAG,"isCpuNeon()=YES");
					if(new File(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libtinyWRAP_neon.so")).exists()){

						try {

							System.loadLibrary("tinyWRAP_neon");
							Log.d(TAG,"Native code library load. tinyWRAP_neon\n");
						} catch (UnsatisfiedLinkError e) {
							Log.e(TAG,"Native code library failed to load.\n" + e);
							try{
								System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libtinyWRAP_neon.so"));
							}catch (Exception e1){
								Log.e(TAG,"Error:"+e1.getMessage());
								e1.printStackTrace();
							}
						}
						/*
						if(new File(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "gdbserver.so")).exists()){
							try {

								System.loadLibrary("gdbserver");
								Log.d(TAG,"Native code library load GDB.\n");
							} catch (UnsatisfiedLinkError e) {
								Log.e(TAG,"Native code library failed to load GDB.\n" + e);
								try{
									System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "gdbserver.so"));
								}catch (Exception e1){
									e.printStackTrace();
								}
							}
						}
						*/

					}else{
						Log.d(TAG,"No exit file of WRAP neon");
					}
				}
				else{
					Log.d(TAG,"isCpuNeon()=NO");
					try {

						System.loadLibrary("tinyWRAP");
						Log.d(TAG,"Native code library load. tinyWRAP\n");
					} catch (UnsatisfiedLinkError e) {
						Log.e(TAG,"Native code library failed to load.\n" + e);
						try{
							System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libtinyWRAP.so"));
						}catch (UnsatisfiedLinkError e2){
							Log.e(TAG, "loadLibrary" + Log.getStackTraceString(e2));
							e2.printStackTrace();
						}
					}
					//System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libtinyWRAP.so"));
				}
			}else {
				// "armeabi", "mips", "x86"...
				try {
					System.loadLibrary("tinyWRAP");
					if(BuildConfig.DEBUG)Log.d(TAG,"Native code library load. libtinyWRAP\n");
				} catch (UnsatisfiedLinkError e) {
					if(BuildConfig.DEBUG)Log.e(TAG,"Native code library failed to load.\n" + e);
					try{
						System.load(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libtinyWRAP.so"));
					}catch (UnsatisfiedLinkError e2){
						if(BuildConfig.DEBUG)Log.e(TAG, "loadLibrary" + Log.getStackTraceString(e2));
					}
				}
			}
			// If OpenSL ES is supported and known to work on current device then use it
			if(NgnApplication.isSLEs2KnownToWork()) {
				if(new File(String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libplugin_audio_opensles.so")).exists()){
					final String pluginPath = String.format("%s/%s", NgnEngine.LIBS_FOLDER, "libplugin_audio_opensles.so");

					// returned value is the number of registered add-ons (2 = 1 consumer + 1 producer)
					if (org.doubango.tinyWRAP.MediaSessionMgr.registerAudioPluginFromFile(pluginPath) < 2) {
						// die if cannot load add-ons
						throw new RuntimeException("Failed to register audio plugin with path=" + pluginPath);
					}
				}else{
					// returned value is the number of registered add-ons (2 = 1 consumer + 1 producer)
					if (org.doubango.tinyWRAP.MediaSessionMgr.registerAudioPluginOpenSLES() < 2) {
						// die if cannot load add-ons
						throw new RuntimeException("Failed to register audio plugin opensles");
					}
				}

				if(BuildConfig.DEBUG)Log.d(TAG, "Using OpenSL ES audio driver");

			}
			// otherwise, use AudioTrack/Record
			else{
				org.doubango.tinyWRAP.ProxyAudioProducer.registerPlugin();
				org.doubango.tinyWRAP.ProxyAudioConsumer.registerPlugin();
			}
			
			org.doubango.tinyWRAP.ProxyVideoProducer.registerPlugin();
			org.doubango.tinyWRAP.ProxyVideoConsumer.registerPlugin();
			
			org.doubango.tinyWRAP.SipStack.initialize();
			
			org.doubango.ngn.media.NgnProxyPluginMgr.Initialize();
			
			sInitialized = true;
		}
	}
	
	// This function is deprecated and there's no longer need to call it. Also, do not load the native libs in your app
	// Will be removed in next releases
	@Deprecated
	public static void initialize(){
		initialize2();
	}
	
	/**
	 * Gets an instance of the NGN engine. This function can be called as many times as needed, and it will always return the
	 * same instance.
	 * @return An instance of the NGN engine.
	 */
	public static NgnEngine getInstance(){
		if(sInstance == null){
			sInstance = new NgnEngine();
		}
		return sInstance;
	}


	
	/**
	 * Default constructor for the NGN engine. You should never call this function from your code. Instead you should
	 * use @ref getInstance().
	 * @sa @ref getInstance()
	 */
	protected NgnEngine(){
		final Context applicationContext = NgnApplication.getContext();
		final org.doubango.ngn.services.INgnConfigurationService configurationService = getConfigurationService();
		if(applicationContext != null){
			mNotifManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
		}
		else{ 
			mNotifManager = null;
		}
		mVibrator = null;
		
		// Initialize SIP stack
		org.doubango.tinyWRAP.SipStack.initialize();
		// Set codec priorities
		int prio = 0;
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_amr_nb_oa, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_amr_nb_be, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_amr_wb_be, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_amr_wb_oa, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_g722, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_speex_wb, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_speex_uwb, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_speex_nb, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_pcma, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_pcmu, prio++);
		org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_ilbc, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_gsm, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_g729ab, prio++);
		//org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_h264_hp, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_h264_bp, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_h264_mp, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_vp8, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_mp4ves_es, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_theora, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_h263, prio++);
        org.doubango.tinyWRAP.SipStack.setCodecPriority(org.doubango.tinyWRAP.tdav_codec_id_t.tdav_codec_id_h261, prio++);
        
        // Profile
        org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetProfile(org.doubango.tinyWRAP.tmedia_profile_t.valueOf(configurationService.getString(
				org.doubango.ngn.utils.NgnConfigurationEntry.MEDIA_PROFILE,
				org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_MEDIA_PROFILE)));
        // Set default mediaType to use when receiving bodiless INVITE
        org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetMediaType(org.doubango.tinyWRAP.twrap_media_type_t.twrap_media_audiovideo);
		// Preferred video size
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetPrefVideoSize(org.doubango.tinyWRAP.tmedia_pref_video_size_t.valueOf(configurationService.getString(
				org.doubango.ngn.utils.NgnConfigurationEntry.QOS_PREF_VIDEO_SIZE,
				org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_QOS_PREF_VIDEO_SIZE)));
		// Zero Video Artifacts
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetVideoZeroArtifactsEnabled(configurationService.getBoolean(
				org.doubango.ngn.utils.NgnConfigurationEntry.QOS_USE_ZERO_VIDEO_ARTIFACTS,
				org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_QOS_USE_ZERO_VIDEO_ARTIFACTS));
		// SRTP mode
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetSRtpMode(org.doubango.tinyWRAP.tmedia_srtp_mode_t.valueOf(configurationService.getString(
				org.doubango.ngn.utils.NgnConfigurationEntry.SECURITY_SRTP_MODE,
				org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_SECURITY_SRTP_MODE)));
		// SRTP type
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetSRtpType(org.doubango.tinyWRAP.tmedia_srtp_type_t.valueOf(configurationService.getString(
				org.doubango.ngn.utils.NgnConfigurationEntry.SECURITY_SRTP_TYPE,
				org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_SECURITY_SRTP_TYPE)));
		// NAT Traversal (ICE, STUN and TURN)
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetIceEnabled(configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_USE_ICE, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_USE_ICE));
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetStunEnabled(configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_USE_STUN_FOR_SIP, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_USE_STUN_FOR_SIP)); // Public IP/port in SIP Contact/Via headers and SDP connection info.
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetIceStunEnabled(configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_USE_STUN_FOR_ICE, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_USE_STUN_FOR_ICE)); // ICE reflexive candidates?
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetIceTurnEnabled(configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_USE_TURN_FOR_ICE, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_USE_TURN_FOR_ICE)); // ICE reflexive candidates?
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetStunServer(
				configurationService.getString(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_STUN_SERVER, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_STUN_SERVER), 
				configurationService.getInt(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_STUN_PORT, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_STUN_PORT));
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetStunCred(
				configurationService.getString(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_STUN_USERNAME, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_STUN_USERNAME),
				configurationService.getString(org.doubango.ngn.utils.NgnConfigurationEntry.NATT_STUN_PASSWORD, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_NATT_STUN_PASSWORD));
		
		// codecs, AEC, NoiseSuppression, Echo cancellation, ....
		final boolean aec = configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.GENERAL_AEC, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_GENERAL_AEC) ;
		final boolean echo_tail_adaptive = configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.GENERAL_USE_ECHO_TAIL_ADAPTIVE, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_GENERAL_USE_ECHO_TAIL_ADAPTIVE);
		final boolean vad = configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.GENERAL_VAD, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_GENERAL_VAD) ;
		final boolean nr = configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.GENERAL_NR, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_GENERAL_NR) ;
		final int echo_tail = configurationService.getInt(org.doubango.ngn.utils.NgnConfigurationEntry.GENERAL_ECHO_TAIL, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_GENERAL_ECHO_TAIL);
		
		Log.d(TAG, "Configure AEC["+aec+"/"+echo_tail+"] AEC_TAIL_ADAPT["+echo_tail_adaptive+"] NoiseSuppression["+nr+"], Voice activity detection["+vad+"]");
		
		if (aec){
			org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetEchoSuppEnabled(true);
			// Very Important: EchoTail in milliseconds
			// When using WebRTC AEC, the maximum value is 500ms
			// When using Speex-DSP, any number is valid but you should choose a multiple of 20ms
			// In all cases this value will be updated per session if adaptive echo tail option is enabled
			org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetEchoTail(echo_tail);
			org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetEchoSkew(0);
		}
		else{
			org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetEchoSuppEnabled(false);
			org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetEchoTail(0); 
		}
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetAgcEnabled(true);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetVadEnabled(vad);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetNoiseSuppEnabled(nr);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetJbMargin(100);
		// /!\IMPORTANT: setting the Jitter buffer max late to (0) causes "SIGFPE" error in SpeexDSP function "jitter_buffer_ctl(JITTER_BUFFER_SET_MAX_LATE_RATE)"
		// This only happens when the audio engine is dynamically loaded from shared library (at least on Galaxy Nexus)
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetJbMaxLateRate(1);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetRtcpEnabled(true);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetRtcpMuxEnabled(configurationService.getBoolean(org.doubango.ngn.utils.NgnConfigurationEntry.RTCP_MUX, org.doubango.ngn.utils.NgnConfigurationEntry.DEFAULT_RTCP_MUX));
		// supported opus mw_rates: 8000,12000,16000,24000,48000
		// opensl-es playback_rates: 8000, 11025, 16000, 22050, 24000, 32000, 44100, 64000, 88200, 96000, 192000
		// webrtc aec record_rates: 8000, 16000, 32000
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetOpusMaxCaptureRate(16000);// /!\IMPORTANT: only 8k and 16k will work with WebRTC AEC
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetOpusMaxPlaybackRate(16000);
		
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetCongestionCtrlEnabled(false);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetBandwidthVideoDownloadMax(-1);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetBandwidthVideoUploadMax(-1);
		
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetAudioChannels(1, 1); // (mono, mono)
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetAudioPtime(20);
		
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetAvpfMode(org.doubango.tinyWRAP.tmedia_mode_t.tmedia_mode_optional);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetAvpfTail(30, 160);
		org.doubango.tinyWRAP.MediaSessionMgr.defaultsSetVideoFps(15);

	}
	
	/**
	 * Starts the engine. This function will start all underlying services (SIP, XCAP, MSRP, History, ...).
	 * You must call this function before trying to use any of the underlying services.
	 * @return true if all services have been successfully started and false otherwise
	 */
	public synchronized boolean start() {
		if(mStarted){
			return true;
		}
		
		boolean success = true;
		
		success &= getConfigurationService().start();
		success &= getStorageService().start();
		success &= getNetworkService().start();
		success &= getHttpClientService().start();
		success &= getHistoryService().start();
		success &= getContactService().start();

		success &= getSoundService().start();
		//new service for location
		success &= getLocationService().start();

		success &= getEmergencyService().start();
		//New service for affiliation
		success &= getAffiliationService().start();
		//New Service for CMD
		success &= getCMSService().start();
		//New Service for OpenId
		success &= getAuthenticationService().start();

		//New Service for GMD
		success &= getGMSService().start();

		//New Service for Profiles
		success &= getProfilesService().start();

		//new service for MBMS
		try{
			success &= getMbmsService().start();
		}catch (Exception ex){
			if(BuildConfig.DEBUG)Log.w(TAG,"MBMS error");
		}

		success &= getSipService().start();
		if(success){
			success &= getHistoryService().load();
			/* success &=*/ getContactService().load();
			
			NgnApplication.getContext().startService(
					new Intent(NgnApplication.getContext(), getNativeServiceClass()));
		}
		else{
			Log.e(TAG, "Failed to start services");
		}
		
		mStarted = true;
		return success;
	}
	
	/**
	 * Stops the engine. This function will stop all underlying services (SIP, XCAP, MSRP, History, ...).
	 * @return true if all services have been successfully stopped and false otherwise
	 */
	public synchronized boolean stop() {
		if(!mStarted){
			return true;
		}
		
		boolean success = true;
		
		success &= getConfigurationService().stop();
		success &= getHttpClientService().stop();
		success &= getHistoryService().stop();
		success &= getStorageService().stop();
		success &= getContactService().stop();
		success &= getSipService().stop();
		success &= getSoundService().stop();
		success &= getNetworkService().stop();
		//New service for location
		success &= getLocationService().stop();
		success &= getEmergencyService().stop();
		//New service for affiliation
		success &= getAffiliationService().stop();
		//New Service for CMS
		success &= getCMSService().stop();
		//New Service for OpenId
		success &= getAuthenticationService().stop();

		//New Service for GMD
		success &= getGMSService().stop();

		//New Service for Profiles
		success &= getProfilesService().stop();
		//New Service for MBMS
		success &= getMbmsService().stop();
		if(!success){
			Log.e(TAG, "Failed to stop services");
		}
		
		NgnApplication.getContext().stopService(
				new Intent(NgnApplication.getContext(), getNativeServiceClass()));
		
		// Cancel the persistent notifications.
		if(mNotifManager != null){
			mNotifManager.cancelAll();
		}
		
		mStarted = false;
		return success;
	}

	public synchronized boolean clearServices() {
		if(!mStarted){
			return true;
		}
		boolean success = true;
		success &= getConfigurationService().clearService();
		success &= getHttpClientService().clearService();
		success &= getHistoryService().clearService();
		success &= getStorageService().clearService();
		success &= getContactService().clearService();
		success &= getSipService().clearService();
		success &= getSoundService().clearService();
		success &= getNetworkService().clearService();
		//New service for location
		success &= getLocationService().clearService();
		success &= getEmergencyService().clearService();
		//New service for affiliation
		success &= getAffiliationService().clearService();
		//New Service for CMS
		success &= getCMSService().clearService();
		//New Service for OpenId
		success &= getAuthenticationService().clearService();

		//New Service for Profiles
		success &= getProfilesService().clearService();
		//New Service for MBMS
		success &= getMbmsService().clearService();
		if(!success){
			Log.e(TAG, "Failed to stop services");
		}
		return success;

	}
	
	/**
	 * Checks whether the engine is started.
	 * @return true is the engine is running and false otherwise.
	 * @sa @ref start() @ref stop()
	 */
	public synchronized boolean isStarted(){
		return mStarted;
	}
	
	/**
	 * Sets the main activity to use as context in order to query some native resources.
	 * It's up to you to call this function in order to retrieve the contacts for the ContactService.
	 * @param mainActivity The activity
	 * @sa @ref getMainActivity()
	 */
	public void setMainActivity(Activity mainActivity){
		mMainActivity = mainActivity;
	}
	
	/**
	 * Gets the main activity.
	 * @return the main activity
	 * @sa @ref setMainActivity()
	 */
	public Activity getMainActivity(){
		return mMainActivity;
	}
	
	/**
	 * Gets the configuration service.
	 * @return the configuration service.
	 */
	public org.doubango.ngn.services.INgnConfigurationService getConfigurationService(){
		if(mConfigurationService == null){
			mConfigurationService = new org.doubango.ngn.services.impl.NgnConfigurationService();
		}
		return mConfigurationService;
	}
	
	/**
	 * Gets the storage service.
	 * @return the storage service.
	 */
	public org.doubango.ngn.services.INgnStorageService getStorageService(){
		if(mStorageService == null){
			mStorageService = new org.doubango.ngn.services.impl.NgnStorageService(NgnApplication.getContext());
		}
		return mStorageService;
	}
	
	/**
	 * Gets the network service
	 * @return the network service
	 */
	public org.doubango.ngn.services.INgnNetworkService getNetworkService(){
		if(mNetworkService == null){
			mNetworkService = new org.doubango.ngn.services.impl.NgnNetworkService();
		}
		return mNetworkService;
	}
	
	/**
	 * Gets the HTTP service
	 * @return the HTTP service
	 */
	public org.doubango.ngn.services.INgnHttpClientService getHttpClientService(){
		if(mHttpClientService == null){
			mHttpClientService = new org.doubango.ngn.services.impl.NgnHttpClientService();
		}
		return mHttpClientService;
	}
	
	/**
	 * Gets the contact service
	 * @return the contact service
	 */
	public org.doubango.ngn.services.INgnContactService getContactService(){
		if(mContactService == null){
			mContactService = new org.doubango.ngn.services.impl.NgnContactService();
		}
		return mContactService;
	}
	
	/**
	 * Gets the history service
	 * @return the history service
	 */
	public org.doubango.ngn.services.INgnHistoryService getHistoryService(){
		if(mHistoryService == null){
			mHistoryService = new org.doubango.ngn.services.impl.NgnHistoryService();
		}
		return mHistoryService;
	}

	/**
	 * Gets the GMS service
	 * @return the gms service
	 */
	public org.doubango.ngn.services.gms.IMyGMSService getGMSService(){
		if(mGMSService == null){
			mGMSService = new org.doubango.ngn.services.impl.ms.MyGMSService();
		}
		return mGMSService;
	}

	/**
	 * Gets the SIP service
	 * @return the sip service
	 */
	public org.doubango.ngn.services.INgnSipService getSipService(){
		if(mSipService == null){
			mSipService = new org.doubango.ngn.services.impl.NgnSipService();
		}
		return mSipService;
	}
	
	/**
	 * Gets the sound service
	 * @return the sound service
	 */
	public org.doubango.ngn.services.INgnSoundService getSoundService(){
		if(mSoundService == null){
			mSoundService = new org.doubango.ngn.services.impl.NgnSoundService();
		}
		return mSoundService;
	}
	/**
	 * Gets the Location service
	 * @return the Location service
	 */
	public org.doubango.ngn.services.location.IMyLocalizationService getLocationService(){
		if(mLocalizationServer == null){
			mLocalizationServer = new org.doubango.ngn.services.impl.location.MyLocalizationService();
		}
		return mLocalizationServer;
	}


	/**
	 * Gets the Emergency service
	 * @return the Emergency service
	 */
	public org.doubango.ngn.services.emergency.IMyEmergencyService getEmergencyService(){
		if(mEmergencyService == null){
			mEmergencyService = new org.doubango.ngn.services.impl.emergency.MyEmergencyService();
		}
		return mEmergencyService;
	}


	/**
	 * Gets the MBMS service
	 * @return the MBMS service
	 */
	public org.doubango.ngn.services.mbms.IMyMbmsService getMbmsService(){
		if(mMbmsServer == null){
			mMbmsServer = new org.doubango.ngn.services.impl.mbms.MyMbmsService();
		}
		return mMbmsServer;
	}
	/**
	 * Gets the Affiliation service
	 * @return the Affiliation service
	 */
	public org.doubango.ngn.services.affiliation.IMyAffiliationService getAffiliationService(){
		if(mAffiliationServer == null){
			mAffiliationServer = new org.doubango.ngn.services.impl.affiliation.MyAffiliationService();
		}
		return mAffiliationServer;
	}

	/**
	 * Gets the CMS service
	 * @return the CMS service
	 */
	public org.doubango.ngn.services.cms.IMyCMSService getCMSService(){
		if(mCMSService == null){
			mCMSService = new org.doubango.ngn.services.impl.ms.MyCMSService();
		}
		return mCMSService;
	}

	/**
	 * Gets the OpenId service
	 * @return the OpenId service
	 */
	public org.doubango.ngn.services.authentication.IMyAuthenticacionService getAuthenticationService(){
		if(mAuthenticationServer == null){
			mAuthenticationServer = new org.doubango.ngn.services.impl.ms.MyAuthenticacionService();
		}
		return mAuthenticationServer;
	}



	/**
	 * Gets the Profiles service
	 * @return the Profiles service
	 */
	public org.doubango.ngn.services.profiles.IMyProfilesService getProfilesService(){
		if(mProfilesService == null){
			mProfilesService = new org.doubango.ngn.services.impl.profiles.MyProfilesService();
		}
		return mProfilesService;
	}


	/**
	 * Gets the native service class
	 * @return the native service class
	 */
	public Class<? extends NgnNativeService> getNativeServiceClass(){
		return NgnNativeService.class;
	}


}
