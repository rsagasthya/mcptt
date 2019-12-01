/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*  Copyright (C) 2011, Philippe Verney <verney(dot)philippe(AT)gmail(dot)com>
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
package org.doubango.ngn.utils;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.R;
import org.doubango.ngn.datatype.mo.Bool;
import org.doubango.ngn.sip.NgnPresenceStatus;
import org.doubango.tinyWRAP.tdav_codec_id_t;
import org.doubango.tinyWRAP.tmedia_bandwidth_level_t;
import org.doubango.tinyWRAP.tmedia_pref_video_size_t;
import org.doubango.tinyWRAP.tmedia_profile_t;
import org.doubango.tinyWRAP.tmedia_qos_strength_t;
import org.doubango.tinyWRAP.tmedia_qos_stype_t;
import org.doubango.tinyWRAP.tmedia_srtp_mode_t;
import org.doubango.tinyWRAP.tmedia_srtp_type_t;
import org.doubango.utils.Utils;


public class NgnConfigurationEntry {
	private static final String TAG = Utils.getTAG(NgnConfigurationEntry.class.getCanonicalName());
	
	public final static String  SHARED_PREF_NAME = TAG;

	public static final String PCSCF_DISCOVERY_DNS_SRV=getDefaultString(R.string.PCSCF_DISCOVERY_DNS_SRV);
	
	// General
	public static final String GENERAL_AUTOSTART=getDefaultString(R.string.GENERAL_AUTOSTART);
	public static final String GENERAL_AUTOSTART_VIDEO=getDefaultString(R.string.GENERAL_AUTOSTART_VIDEO);
	public static final String GENERAL_SHOW_WELCOME_SCREEN=getDefaultString(R.string.GENERAL_SHOW_WELCOME_SCREEN);
	public static final String GENERAL_FULL_SCREEN_VIDEO=getDefaultString(R.string.GENERAL_FULL_SCREEN_VIDEO);
	public static final String GENERAL_USE_FFC=getDefaultString(R.string.GENERAL_USE_FFC);
	public static final String GENERAL_INTERCEPT_OUTGOING_CALLS=getDefaultString(R.string.GENERAL_INTERCEPT_OUTGOING_CALLS);
	public static final String GENERAL_AUDIO_PLAY_LEVEL=getDefaultString(R.string.GENERAL_AUDIO_PLAY_LEVEL);
	public static final String GENERAL_ENUM_DOMAIN=getDefaultString(R.string.GENERAL_ENUM_DOMAIN);
	public static final String GENERAL_AEC=getDefaultString(R.string.GENERAL_AEC);
	public static final String GENERAL_VAD=getDefaultString(R.string.GENERAL_VAD);
	public static final String GENERAL_NR=getDefaultString(R.string.GENERAL_NR);
	public static final String GENERAL_ECHO_TAIL=getDefaultString(R.string.GENERAL_ECHO_TAIL);
	public static final String GENERAL_USE_ECHO_TAIL_ADAPTIVE=getDefaultString(R.string.GENERAL_USE_ECHO_TAIL_ADAPTIVE);
	public static final String GENERAL_SEND_DEVICE_INFO=getDefaultString(R.string.GENERAL_SEND_DEVICE_INFO);
	
	// Identity
	public static final String IDENTITY_DISPLAY_NAME=getDefaultString(R.string.IDENTITY_DISPLAY_NAME);
	public static final String IDENTITY_IMPU=getDefaultString(R.string.IDENTITY_IMPU);
	public static final String IDENTITY_IMPI=getDefaultString(R.string.IDENTITY_IMPI);
	public static final String IDENTITY_PASSWORD=getDefaultString(R.string.IDENTITY_PASSWORD);
	
	// Network
	public static final String NETWORK_REGISTRATION_TIMEOUT=getDefaultString(R.string.NETWORK_REGISTRATION_TIMEOUT);
	public static final String NETWORK_REALM=getDefaultString(R.string.NETWORK_REALM);
	public static final String NETWORK_USE_WIFI=getDefaultString(R.string.NETWORK_USE_WIFI);
	public static final String NETWORK_USE_3G=getDefaultString(R.string.NETWORK_USE_3G);
	public static final String NETWORK_USE_EARLY_IMS=getDefaultString(R.string.NETWORK_USE_EARLY_IMS);
	public static final String NETWORK_IP_VERSION=getDefaultString(R.string.NETWORK_IP_VERSION);
	public static final String NETWORK_PCSCF_DISCOVERY=getDefaultString(R.string.NETWORK_PCSCF_DISCOVERY);
	public static final String NETWORK_PCSCF_HOST=getDefaultString(R.string.NETWORK_PCSCF_HOST);
	public static final String NETWORK_PCSCF_PORT=getDefaultString(R.string.NETWORK_PCSCF_PORT);
	public static final String NETWORK_USE_SIGCOMP=getDefaultString(R.string.NETWORK_USE_SIGCOMP);
	public static final String NETWORK_TRANSPORT=getDefaultString(R.string.NETWORK_TRANSPORT);



	//MCPTT
	public static final String MCPTT_PSI_CALL_PRIVATE=getDefaultString(R.string.MCPTT_PSI_CALL_PRIVATE);
	public static final String MCPTT_PSI_CALL_GROUP=getDefaultString(R.string.MCPTT_PSI_CALL_PRIVATE);
	public static final String MCPTT_PSI_CALL_PREESTABLISHED=getDefaultString(R.string.MCPTT_PSI_CALL_PRIVATE);
	public static final String MCPTT_PSI_AFFILIATION=getDefaultString(R.string.MCPTT_PSI_CALL_PRIVATE);
	public static final String MCPTT_PSI_AUTHENTICATION=getDefaultString(R.string.MCPTT_PSI_CALL_PRIVATE);

	public static final String MCPTT_PSI_CMS=getDefaultString(R.string.MCPTT_PSI_CMS);
	public static final String MCPTT_ENABLE_SUBSCRIPTION_CMS=getDefaultString(R.string.MCPTT_ENABLE_SUBSCRIPTION_CMS);
	public static final String CMS_XCAP_ROOT_URI=getDefaultString(R.string.CMS_XCAP_ROOT_URI);

	public static final String MCPTT_PSI_GMS=getDefaultString(R.string.MCPTT_PSI_GMS);
	public static final String MCPTT_ENABLE_SUBSCRIPTION_GMS=getDefaultString(R.string.MCPTT_ENABLE_SUBSCRIPTION_GMS);
	public static final String GMS_XCAP_ROOT_URI=getDefaultString(R.string.GMS_XCAP_ROOT_URI);

	public static final String MCPTT_ID=getDefaultString(R.string.MCPTT_ID);
	public static final String MCPTT_CLIENT_ID=getDefaultString(R.string.MCPTT_CLIENT_ID);
	public static final String MCPTT_PRIORITY=getDefaultString(R.string.MCPTT_PRIORITY);
	public static final String MCPTT_IMPLICIT=getDefaultString(R.string.MCPTT_IMPLICIT);
	public static final String MCPTT_GRANTED=getDefaultString(R.string.MCPTT_GRANTED);
	public static final String MCPTT_ENABLE_MBMS=getDefaultString(R.string.MCPTT_ENABLE_MBMS);
	public static final String MCPTT_INSERT_SDP_FMTP=getDefaultString(R.string.MCPTT_INSERT_SDP_FMTP);
	public static final String MCPTT_LOCATION_INFO_VERSION_OLD=getDefaultString(R.string.MCPTT_LOCATION_INFO_VERSION_OLD);




	public static final String MCPTT_PRIV_ANSWER_MODE=getDefaultString(R.string.MCPTT_PRIV_ANSWER_MODE);
	public static final String MCPTT_ANSWER_MODE=getDefaultString(R.string.MCPTT_ANSWER_MODE);
	public static final String MCPTT_NAMESPACE=getDefaultString(R.string.MCPTT_NAMESPACE);
	public static final String MCPTT_LOCATION=getDefaultString(R.string.MCPTT_ANSWER_MODE);;
	public static final String MCPTT_IS_AFFILIATION=getDefaultString(R.string.MCPTT_IS_AFFILIATION);;
	public static final String MCPTT_IS_SELF_AFFILIATION=getDefaultString(R.string.MCPTT_IS_SELF_AFFILIATION);;

	//GUI
	public static final String MCPTT_PLAY_SOUND_MCPTT_CALL=getDefaultString(R.string.MCPTT_PLAY_SOUND_MCPTT_CALL);;
	//SELF CONFIGURE
	public static final String SELF_CONFIGURE=getDefaultString(R.string.SELF_CONFIGURE);;
	public static final String SELF_CONFIGURE_CLIENT_ID=getDefaultString(R.string.SELF_CONFIGURE_CLIENT_ID);;
	public static final String SELF_CONFIGURE_ISSUER_URI=getDefaultString(R.string.SELF_CONFIGURE_ISSUER_URI);;
	public static final String SELF_CONFIGURE_REDIRECT_URI=getDefaultString(R.string.SELF_CONFIGURE_REDIRECT_URI);;
	public static final String MCPTT_USE_ISSUER_URI_IDMS=getDefaultString(R.string.MCPTT_USE_ISSUER_URI_IDMS);;
	public static final String MCPTT_UE_ID=getDefaultString(R.string.MCPTT_UE_ID);;
	public static final String IDMS_TOKEN_END_POINT=getDefaultString(R.string.IDMS_TOKEN_END_POINT);;
	public static final String IDMS_AUTH_END_POINT=getDefaultString(R.string.IDMS_AUTH_END_POINT);;





	public static final String RTCP_MUX=getDefaultString(R.string.RTCP_MUX);


	//CMS
	public static final String ENABLE_CMS=getDefaultString(R.string.ENABLE_CMS);

	public static final String SELF_CONFIGURE_SEND_TOKEN_REGISTER=getDefaultString(R.string.SELF_CONFIGURE_SEND_TOKEN_REGISTER);;
	public static final String SELF_CONFIGURE_SEND_TOKEN_FAIL=getDefaultString(R.string.SELF_CONFIGURE_SEND_TOKEN_FAIL);;

	public static final String MCPTT_INSERT_X_FRAMER_IP=getDefaultString(R.string.MCPTT_INSERT_X_FRAMER_IP);;
	//SSH
	public static final String SSH_HOST=getDefaultString(R.string.SSH_HOST);
	public static final String SSH_USER=getDefaultString(R.string.SSH_USER);
	public static final String SSH_PASS=getDefaultString(R.string.SSH_PASS);
	public static final String SSH_PORT=getDefaultString(R.string.SSH_PORT);


	// NAT Traversal
	public static final String NATT_HACK_AOR=getDefaultString(R.string.NATT_HACK_AOR);
	public static final String NATT_HACK_AOR_TIMEOUT=getDefaultString(R.string.NATT_HACK_AOR_TIMEOUT);
	/**@deprecated use {@link NATT_USE_STUN_FOR_SIP} instead.*/
	public static final String NATT_USE_STUN=getDefaultString(R.string.NATT_USE_STUN);
	public static final String NATT_USE_STUN_FOR_SIP=getDefaultString(R.string.NATT_USE_STUN); // same name as "NATT_USE_STUN" for backward compatibility
	public static final String NATT_USE_ICE=getDefaultString(R.string.NATT_USE_ICE);
	public static final String NATT_USE_STUN_FOR_ICE=getDefaultString(R.string.NATT_USE_STUN_FOR_ICE);
	public static final String NATT_USE_TURN_FOR_ICE=getDefaultString(R.string.NATT_USE_TURN_FOR_ICE);
	public static final String NATT_STUN_DISCO=getDefaultString(R.string.NATT_STUN_DISCO);
	public static final String NATT_STUN_SERVER=getDefaultString(R.string.NATT_STUN_SERVER);
	public static final String NATT_STUN_PORT=getDefaultString(R.string.NATT_STUN_PORT);
	public static final String NATT_STUN_USERNAME=getDefaultString(R.string.NATT_STUN_USERNAME);
	public static final String NATT_STUN_PASSWORD=getDefaultString(R.string.NATT_STUN_PASSWORD);
	
	// QoS
	public static final String QOS_PRECOND_BANDWIDTH_LEVEL=getDefaultString(R.string.QOS_PRECOND_BANDWIDTH_LEVEL);
	public static final String QOS_PRECOND_STRENGTH=getDefaultString(R.string.QOS_PRECOND_STRENGTH);
    public static final String QOS_PRECOND_TYPE=getDefaultString(R.string.QOS_PRECOND_TYPE);
    public static final String QOS_REFRESHER=getDefaultString(R.string.QOS_REFRESHER);
    public static final String QOS_SIP_CALLS_TIMEOUT=getDefaultString(R.string.QOS_SIP_CALLS_TIMEOUT);
    public static final String QOS_SIP_SESSIONS_TIMEOUT=getDefaultString(R.string.QOS_SIP_SESSIONS_TIMEOUT);
    public static final String QOS_USE_SESSION_TIMERS=getDefaultString(R.string.QOS_USE_SESSION_TIMERS);
    public static final String QOS_PREF_VIDEO_SIZE=getDefaultString(R.string.QOS_PREF_VIDEO_SIZE);
    public static final String QOS_USE_ZERO_VIDEO_ARTIFACTS=getDefaultString(R.string.QOS_USE_ZERO_VIDEO_ARTIFACTS);

	
	// Media
	public static final String MEDIA_CODECS=getDefaultString(R.string.MEDIA_CODECS);
	public static final String MEDIA_AUDIO_RESAMPLER_QUALITY=getDefaultString(R.string.MEDIA_AUDIO_RESAMPLER_QUALITY);
	public static final String MEDIA_AUDIO_CONSUMER_GAIN=getDefaultString(R.string.MEDIA_AUDIO_CONSUMER_GAIN);
	public static final String MEDIA_AUDIO_PRODUCER_GAIN=getDefaultString(R.string.MEDIA_AUDIO_PRODUCER_GAIN);
	public static final String MEDIA_AUDIO_CONSUMER_ATTENUATION=getDefaultString(R.string.MEDIA_AUDIO_CONSUMER_ATTENUATION);
	public static final String MEDIA_AUDIO_PRODUCER_ATTENUATION=getDefaultString(R.string.MEDIA_AUDIO_PRODUCER_ATTENUATION);
	public static final String MEDIA_PROFILE=getDefaultString(R.string.MEDIA_PROFILE);
	
	// Security
	public static final String SECURITY_SRTP_MODE=getDefaultString(R.string.SECURITY_SRTP_MODE);
	public static final String SECURITY_SRTP_TYPE=getDefaultString(R.string.SECURITY_SRTP_TYPE);
	public static final String SECURITY_IMSAKA_AMF=getDefaultString(R.string.SECURITY_IMSAKA_AMF);
	public static final String SECURITY_IMSAKA_OPID=getDefaultString(R.string.SECURITY_IMSAKA_OPID);
	public static final String SECURITY_TLS_PRIVKEY_FILE_PATH=getDefaultString(R.string.SECURITY_TLS_PRIVKEY_FILE_PATH);
	public static final String SECURITY_TLS_PUBKEY_FILE_PATH=getDefaultString(R.string.SECURITY_TLS_PUBKEY_FILE_PATH);
	public static final String SECURITY_TLS_CA_FILE_PATH=getDefaultString(R.string.SECURITY_TLS_CA_FILE_PATH);
	public static final String SECURITY_TLS_VERIFY_CERTS=getDefaultString(R.string.SECURITY_TLS_VERIFY_CERTS);
	
	// XCAP
	public static final String XCAP_PASSWORD=getDefaultString(R.string.XCAP_PASSWORD);
	public static final String XCAP_USERNAME=getDefaultString(R.string.XCAP_USERNAME);
	public static final String XCAP_ENABLED=getDefaultString(R.string.XCAP_ENABLED);
	public static final String XCAP_XCAP_ROOT=getDefaultString(R.string.XCAP_XCAP_ROOT);
	
	// RCS (Rich Communication Suite)
	public static final String RCS_AVATAR_PATH=getDefaultString(R.string.RCS_AVATAR_PATH);
	public static final String RCS_USE_BINARY_SMS=getDefaultString(R.string.RCS_USE_BINARY_SMS);
	public static final String RCS_CONF_FACT=getDefaultString(R.string.RCS_CONF_FACT);
	public static final String RCS_FREE_TEXT=getDefaultString(R.string.RCS_FREE_TEXT);
	public static final String RCS_HACK_SMS=getDefaultString(R.string.RCS_HACK_SMS);
	public static final String RCS_USE_MSRP_FAILURE=getDefaultString(R.string.RCS_USE_MSRP_FAILURE);
	public static final String RCS_USE_MSRP_SUCCESS=getDefaultString(R.string.RCS_USE_MSRP_SUCCESS);
	public static final String RCS_USE_MWI=getDefaultString(R.string.RCS_USE_MWI);
	public static final String RCS_USE_OMAFDR=getDefaultString(R.string.RCS_USE_OMAFDR);
	public static final String RCS_USE_PARTIAL_PUB=getDefaultString(R.string.RCS_USE_PARTIAL_PUB);
	public static final String RCS_USE_PRESENCE=getDefaultString(R.string.RCS_USE_PRESENCE);
	public static final String RCS_USE_RLS=getDefaultString(R.string.RCS_USE_RLS);
	public static final String RCS_SMSC=getDefaultString(R.string.RCS_SMSC);
	public static final String RCS_STATUS =getDefaultString(R.string.RCS_STATUS);

	//Error parameters
	public static final String ERROR_PARAMETERS=getDefaultString(R.string.ERROR_PARAMETERS);


	//profiles save
	public static final String PROFILE_USE =getDefaultString(R.string.PROFILE_USE);


	//

	//
	//	Default values
	//
	
	// General
	public static final boolean DEFAULT_GENERAL_SHOW_WELCOME_SCREEN = true;
	public static final boolean DEFAULT_GENERAL_FULL_SCREEN_VIDEO = true;
	public static final boolean DEFAULT_GENERAL_INTERCEPT_OUTGOING_CALLS = true;
	public static final boolean DEFAULT_GENERAL_USE_FFC = true;
	public static final boolean DEFAULT_GENERAL_AUTOSTART = true;
	public static final boolean DEFAULT_GENERAL_AUTOSTART_VIDEO = true;
	public static final float DEFAULT_GENERAL_AUDIO_PLAY_LEVEL = 1.0f;
	public static final String DEFAULT_GENERAL_ENUM_DOMAIN = "e164.org";
	public static final boolean DEFAULT_GENERAL_AEC = true;
	public static final boolean DEFAULT_GENERAL_USE_ECHO_TAIL_ADAPTIVE = false;
	public static final boolean DEFAULT_GENERAL_VAD = false; // speex-dsp doesn't support VAD for fixed-point implementation
	public static final boolean DEFAULT_GENERAL_NR = true;
	public static final int DEFAULT_GENERAL_ECHO_TAIL = 100;
	public static final boolean DEFAULT_GENERAL_SEND_DEVICE_INFO = false;
	
	//	Identity

	public static final String DEFAULT_IDENTITY_DISPLAY_NAME=getDefaultString(R.string.DEFAULT_IDENTITY_DISPLAY_NAME);//= "mcptt-test-A@organization.org";
	public static final String DEFAULT_IDENTITY_IMPU=getDefaultString(R.string.DEFAULT_IDENTITY_IMPU);// = "sip:mcptt-test-A@organization.org";
	public static final String DEFAULT_IDENTITY_IMPI=getDefaultString(R.string.DEFAULT_IDENTITY_IMPI);// = "mcptt-test-A@organization.org";
	public static final String DEFAULT_IDENTITY_PASSWORD=getDefaultString(R.string.DEFAULT_IDENTITY_PASSWORD);// = "";

	// Network



	public static final int DEFAULT_NETWORK_REGISTRATION_TIMEOUT=getDefaultInt(R.integer.DEFAULT_NETWORK_REGISTRATION_TIMEOUT);// = 1700;
	public static final String DEFAULT_NETWORK_REALM=getDefaultString(R.string.DEFAULT_NETWORK_REALM);// = "organization.org";
	public static final boolean DEFAULT_NETWORK_USE_WIFI=getDefaultBoolean(R.bool.DEFAULT_NETWORK_USE_WIFI);// = true;
	public static final boolean DEFAULT_NETWORK_USE_3G=getDefaultBoolean(R.bool.DEFAULT_NETWORK_USE_3G);// = true;
	public static final String DEFAULT_NETWORK_PCSCF_DISCOVERY=getDefaultString(R.string.DEFAULT_NETWORK_PCSCF_DISCOVERY);// = "None";
	public static final String DEFAULT_NETWORK_PCSCF_HOST=getDefaultString(R.string.DEFAULT_NETWORK_PCSCF_HOST);// = "pcscf.organization.org";
	public static final int DEFAULT_NETWORK_PCSCF_PORT=getDefaultInt(R.integer.DEFAULT_NETWORK_PCSCF_PORT);// = 5060;//5060;
	public static final boolean DEFAULT_NETWORK_USE_SIGCOMP=getDefaultBoolean(R.bool.DEFAULT_NETWORK_USE_SIGCOMP);// = false;
	public static final String DEFAULT_NETWORK_TRANSPORT=getDefaultString(R.string.DEFAULT_NETWORK_TRANSPORT);// = "udp";
	public static final String DEFAULT_NETWORK_IP_VERSION=getDefaultString(R.string.DEFAULT_NETWORK_IP_VERSION);// = "ipv4";
	public static final boolean DEFAULT_NETWORK_USE_EARLY_IMS=getDefaultBoolean(R.bool.DEFAULT_NETWORK_USE_EARLY_IMS);// = false;


	//MCPTT

	public static final String DEFAULT_MCPTT_PSI_CALL_PRIVATE=getDefaultString(R.string.DEFAULT_MCPTT_PSI_CALL_PRIVATE);// = "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final String DEFAULT_MCPTT_PSI_CALL_GROUP=getDefaultString(R.string.DEFAULT_MCPTT_PSI_CALL_GROUP);// = "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final String DEFAULT_MCPTT_PSI_CALL_PREESTABLISHED=getDefaultString(R.string.DEFAULT_MCPTT_PSI_CALL_PREESTABLISHED);//= "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final String DEFAULT_MCPTT_PSI_AFFILIATION=getDefaultString(R.string.DEFAULT_MCPTT_PSI_AFFILIATION);//= "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final String DEFAULT_MCPTT_PSI_AUTHENTICATION=getDefaultString(R.string.DEFAULT_MCPTT_PSI_AUTHENTICATION);//= "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final String DEFAULT_MCPTT_PSI_CMS=getDefaultString(R.string.DEFAULT_MCPTT_PSI_CMS);// = "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final Boolean DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_CMS=getDefaultBoolean(R.bool.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_CMS);
	public static final String DEFAULT_CMS_XCAP_ROOT_URI=getDefaultString(R.string.DEFAULT_CMS_XCAP_ROOT_URI);



	public static final String DEFAULT_MCPTT_PSI_GMS=getDefaultString(R.string.DEFAULT_MCPTT_PSI_GMS);// = "sip:mcptt-server-orig-part@organization.org";//"sip:mcptt-server@organization.org";
	public static final Boolean DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_GMS=getDefaultBoolean(R.bool.DEFAULT_MCPTT_ENABLE_SUBSCRIPTION_GMS);
	public static final String DEFAULT_GMS_XCAP_ROOT_URI=getDefaultString(R.string.DEFAULT_GMS_XCAP_ROOT_URI);


	public static final String DEFAULT_MCPTT_ID=getDefaultString(R.string.DEFAULT_MCPTT_ID);// = "sip:mcptt_id_test_A@organization.org";
	public static final String DEFAULT_MCPTT_CLIENT_ID=getDefaultString(R.string.DEFAULT_MCPTT_CLIENT_ID);// = "sip:mcptt_id_test_A@organization.org";
	public static final int DEFAULT_MCPTT_PRIORITY=getDefaultInt(R.integer.DEFAULT_MCPTT_PRIORITY);// = 7;
	public static final Boolean DEFAULT_MCPTT_IMPLICIT=getDefaultBoolean(R.bool.DEFAULT_MCPTT_IMPLICIT);// = true;
	public static final Boolean DEFAULT_MCPTT_GRANTED=getDefaultBoolean(R.bool.DEFAULT_MCPTT_GRANTED);// = true;
	public static final Boolean DEFAULT_MCPTT_ENABLE_MBMS=getDefaultBoolean(R.bool.DEFAULT_MCPTT_ENABLE_MBMS);// = false;


	public static final Boolean DEFAULT_MCPTT_INSERT_SDP_FMTP=getDefaultBoolean(R.bool.DEFAULT_MCPTT_INSERT_SDP_FMTP);// = true;
	public static final Boolean DEFAULT_MCPTT_PRIV_ANSWER_MODE=getDefaultBoolean(R.bool.DEFAULT_MCPTT_PRIV_ANSWER_MODE);// = false;
	public static final Boolean DEFAULT_MCPTT_ANSWER_MODE=getDefaultBoolean(R.bool.DEFAULT_MCPTT_ANSWER_MODE);// = true;
	public static final Boolean DEFAULT_MCPTT_NAMESPACE=getDefaultBoolean(R.bool.DEFAULT_MCPTT_NAMESPACE);// = true;
	public static final Boolean DEFAULT_MCPTT_LOCATION=getDefaultBoolean(R.bool.DEFAULT_MCPTT_LOCATION);// = true;
	public static final Boolean DEFAULT_MCPTT_IS_AFFILIATION=getDefaultBoolean(R.bool.DEFAULT_MCPTT_IS_AFFILIATION);// = true;
	public static final Boolean DEFAULT_MCPTT_IS_SELF_AFFILIATION=getDefaultBoolean(R.bool.DEFAULT_MCPTT_IS_SELF_AFFILIATION);// = false;
	public static final Boolean DEFAULT_MCPTT_LOCATION_INFO_VERSION_OLD=getDefaultBoolean(R.bool.DEFAULT_MCPTT_LOCATION_INFO_VERSION_OLD);// = false;

	//GUI

	public static final Boolean DEFAULT_MCPTT_PLAY_SOUND_MCPTT_CALL=getDefaultBoolean(R.bool.DEFAULT_MCPTT_PLAY_SOUND_MCPTT_CALL);// = false;

	public static final Boolean DEFAULT_MCPTT_INSERT_X_FRAMER_IP=getDefaultBoolean(R.bool.DEFAULT_MCPTT_INSERT_X_FRAMER_IP);// = false;



	//SELF CONFIGURE

	public static final Boolean DEFAULT_SELF_CONFIGURE=getDefaultBoolean(R.bool.DEFAULT_SELF_CONFIGURE);// = false;
	public static final Boolean DEFAULT_SELF_CONFIGURE_SEND_TOKEN_REGISTER=getDefaultBoolean(R.bool.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_REGISTER);// = false;
	public static final Boolean DEFAULT_SELF_CONFIGURE_SEND_TOKEN_FAIL=getDefaultBoolean(R.bool.DEFAULT_SELF_CONFIGURE_SEND_TOKEN_FAIL);// = false;
	public static final String DEFAULT_SELF_CONFIGURE_CLIENT_ID=getDefaultString(R.string.DEFAULT_SELF_CONFIGURE_CLIENT_ID);// = "mcptt_client";
	public static final String DEFAULT_SELF_CONFIGURE_ISSUER_URI=getDefaultString(R.string.DEFAULT_SELF_CONFIGURE_ISSUER_URI);// = "http://idms.organization.com/openid-connect-server-webapp/.well-known/openid-configuration";
	public static final String DEFAULT_SELF_CONFIGURE_REDIRECT_URI=getDefaultString(R.string.DEFAULT_SELF_CONFIGURE_REDIRECT_URI);// = "mcptt://organization_mcptt/cb";//"http://httpbin.org/get";
	public static final Boolean DEFAULT_MCPTT_USE_ISSUER_URI_IDMS=getDefaultBoolean(R.bool.DEFAULT_MCPTT_USE_ISSUER_URI_IDMS);// = false;
	public static final String DEFAULT_IDMS_TOKEN_END_POINT=getDefaultString(R.string.DEFAULT_IDMS_TOKEN_END_POINT);// = "";
	public static final String DEFAULT_MCPTT_UE_ID=getDefaultString(R.string.DEFAULT_MCPTT_UE_ID);// = "mcptt_UE_id";
	public static final String DEFAULT_IDMS_AUTH_END_POINT=getDefaultString(R.string.DEFAULT_IDMS_AUTH_END_POINT);// = "";



	//CMS
	public static final String DEFAULT_CMS_URI=getDefaultString(R.string.DEFAULT_CMS_URI);// = "http://cms.organizatio.com";
	public static final Boolean DEFAULT_ENABLE_CMS=getDefaultBoolean(R.bool.DEFAULT_ENABLE_CMS);// = false;



	public static final Boolean DEFAULT_RTCP_MUX=getDefaultBoolean(R.bool.DEFAULT_RTCP_MUX);// = false;


	//SSH

	public static final String DEFAULT_SSH_HOST=getDefaultString(R.string.DEFAULT_SSH_HOST);//= "organization.org";
	public static final String DEFAULT_SSH_USER=getDefaultString(R.string.DEFAULT_SSH_USER);//= "";
	public static final String DEFAULT_SSH_PASS=getDefaultString(R.string.DEFAULT_SSH_PASS);//= "";
	public static final int DEFAULT_SSH_PORT=getDefaultInt(R.integer.DEFAULT_SSH_PORT);//= 22;


	// NAT Traversal
	public static final int DEFAULT_NATT_HACK_AOR_TIMEOUT=getDefaultInt(R.integer.DEFAULT_NATT_HACK_AOR_TIMEOUT);// = 2000;
	public static final boolean DEFAULT_NATT_HACK_AOR=getDefaultBoolean(R.bool.DEFAULT_NATT_HACK_AOR);// = false;
	/**@deprecated use {@link DEFAULT_NATT_USE_STUN_FOR_SIP} instead.*/
	public static final boolean DEFAULT_NATT_USE_STUN=getDefaultBoolean(R.bool.DEFAULT_NATT_USE_STUN);// = false;
	public static final boolean DEFAULT_NATT_USE_STUN_FOR_SIP=getDefaultBoolean(R.bool.DEFAULT_NATT_USE_STUN_FOR_SIP);// = false;
	public static final boolean DEFAULT_NATT_USE_ICE=getDefaultBoolean(R.bool.DEFAULT_NATT_USE_ICE);// = false;
	public static final boolean DEFAULT_NATT_USE_STUN_FOR_ICE=getDefaultBoolean(R.bool.DEFAULT_NATT_USE_STUN_FOR_ICE);// = false;
	public static final boolean DEFAULT_NATT_USE_TURN_FOR_ICE=getDefaultBoolean(R.bool.DEFAULT_NATT_USE_TURN_FOR_ICE);// = false;
	public static final boolean DEFAULT_NATT_STUN_DISCO=getDefaultBoolean(R.bool.DEFAULT_NATT_STUN_DISCO);// = false;
	public static final String DEFAULT_NATT_STUN_SERVER=getDefaultString(R.string.DEFAULT_NATT_STUN_SERVER);// = "numb.viagenie.ca";
	public static final int DEFAULT_NATT_STUN_PORT=getDefaultInt(R.integer.DEFAULT_NATT_STUN_PORT);// = 3478;
	public static final String DEFAULT_NATT_STUN_USERNAME=getDefaultString(R.string.DEFAULT_NATT_STUN_USERNAME);// = "";
	public static final String DEFAULT_NATT_STUN_PASSWORD=getDefaultString(R.string.DEFAULT_NATT_STUN_PASSWORD);// = "";
	
	// QoS

    public static final int DEFAULT_QOS_PRECOND_BANDWIDTH_LEVEL = tmedia_bandwidth_level_t.tmedia_bl_unrestricted.swigValue(); // should be String but do not change for backward compatibility
    public static final String DEFAULT_QOS_PRECOND_STRENGTH = tmedia_qos_strength_t.tmedia_qos_strength_none.toString();
    public static final String DEFAULT_QOS_PRECOND_TYPE = tmedia_qos_stype_t.tmedia_qos_stype_none.toString();
    public static final String DEFAULT_QOS_REFRESHER= "none";
    public static final int DEFAULT_QOS_SIP_SESSIONS_TIMEOUT = 600000;
    public static final int DEFAULT_QOS_SIP_CALLS_TIMEOUT = 3600;
    public static final boolean DEFAULT_QOS_USE_SESSION_TIMERS = false;
    public static final boolean DEFAULT_QOS_USE_ZERO_VIDEO_ARTIFACTS= false;
    public static final String DEFAULT_QOS_PREF_VIDEO_SIZE = tmedia_pref_video_size_t.tmedia_pref_video_size_cif.toString();
	
	// Media
    public static final String DEFAULT_MEDIA_PROFILE = tmedia_profile_t.tmedia_profile_default.toString();
	public static final int DEFAULT_MEDIA_CODECS =
		//tdav_codec_id_t.tdav_codec_id_pcma.swigValue() |
		tdav_codec_id_t.tdav_codec_id_amr_wb_oa.swigValue() |
		tdav_codec_id_t.tdav_codec_id_amr_wb_be.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_amr_nb_be.swigValue() |
		//tdav_codec_id_t.tdav_codec_id_amr_nb_oa.swigValue() |
		tdav_codec_id_t.tdav_codec_id_pcmu.swigValue() |//Only Codec Audio
		tdav_codec_id_t.tdav_codec_id_h264_bp.swigValue() |//All Codec Video
		//tdav_codec_id_t.tdav_codec_id_h264_hp.swigValue() |
		/*
		tdav_codec_id_t.tdav_codec_id_vp8.swigValue() |
		tdav_codec_id_t.tdav_codec_id_mp4ves_es.swigValue()|
		tdav_codec_id_t.tdav_codec_id_theora.swigValue() |
		tdav_codec_id_t.tdav_codec_id_h263.swigValue() |
		tdav_codec_id_t.tdav_codec_id_h263p.swigValue() |
		tdav_codec_id_t.tdav_codec_id_h263pp.swigValue() // |
		//tdav_codec_id_t.tdav_codec_id_h263p.swigValue() |
		/*tdav_codec_id_t.tdav_codec_id_h263.swigValue()*/
		tdav_codec_id_t.tdav_codec_id_h264_mp.swigValue() ;
	public static final int DEFAULT_MEDIA_AUDIO_RESAMPLER_QUALITY = 0;
	public static final int DEFAULT_MEDIA_AUDIO_CONSUMER_GAIN = 0; // disabled
	public static final int DEFAULT_MEDIA_AUDIO_PRODUCER_GAIN = 0; // disabled
	public static final float DEFAULT_MEDIA_AUDIO_CONSUMER_ATTENUATION = 1f; // disabled
	public static final float DEFAULT_MEDIA_AUDIO_PRODUCER_ATTENUATION = 1f; // disabled



	// Security
	public static final String DEFAULT_SECURITY_IMSAKA_AMF = "0x8000";//PRE-TEST "0x0000";
	public static final String DEFAULT_SECURITY_IMSAKA_OPID = "0xcbe847409e5e8ecd2f4155599b0d5fbc";//"0x00000000000000000000000000000000";
	public static final String DEFAULT_SECURITY_SRTP_MODE = tmedia_srtp_mode_t.tmedia_srtp_mode_none.toString();
	public static final String DEFAULT_SECURITY_SRTP_TYPE = tmedia_srtp_type_t.tmedia_srtp_type_sdes.toString();
	public static final String DEFAULT_SECURITY_TLS_PRIVKEY_FILE_PATH = null;
	public static final String DEFAULT_SECURITY_TLS_PUBKEY_FILE_PATH = null;
	public static final String DEFAULT_SECURITY_TLS_CA_FILE_PATH = null;
	public static final boolean DEFAULT_SECURITY_TLS_VERIFY_CERTS = false;


	// XCAP
	public static final boolean DEFAULT_XCAP_ENABLED=getDefaultBoolean(R.bool.DEFAULT_XCAP_ENABLED);// = false;
	public static final String DEFAULT_XCAP_ROOT=getDefaultString(R.string.DEFAULT_XCAP_ROOT);// = "http://example.org:8080/services";
	public static final String DEFAULT_XCAP_USERNAME=getDefaultString(R.string.DEFAULT_XCAP_USERNAME);// = "sip:johndoe@example.org";
	public static final String DEFAULT_XCAP_PASSWORD=getDefaultString(R.string.DEFAULT_XCAP_PASSWORD);// = null;
	
	// RCS (Rich Communication Suite)
	public static final String DEFAULT_RCS_AVATAR_PATH = "";
	public static final boolean DEFAULT_RCS_USE_BINARY_SM = false; 
	public static final String DEFAULT_RCS_CONF_FACT = "sip:Conference-Factory@example.org";
	public static final String DEFAULT_RCS_FREE_TEXT = "Hello world";
	public static final boolean DEFAULT_RCS_HACK_SMS = false;
	public static final boolean DEFAULT_RCS_USE_MSRP_FAILURE = true;
	public static final boolean DEFAULT_RCS_USE_MSRP_SUCCESS = false;
	public static final boolean DEFAULT_RCS_USE_BINARY_SMS = false;
	public static final boolean DEFAULT_RCS_USE_MWI = false;
	public static final boolean DEFAULT_RCS_USE_OMAFDR = false;
	public static final boolean DEFAULT_RCS_USE_PARTIAL_PUB = false;
	public static final boolean DEFAULT_RCS_USE_PRESENCE = false;
	public static final boolean DEFAULT_RCS_USE_RLS = false;
	public static final String DEFAULT_RCS_SMSC = "sip:+331000000000@example.org";
	public static final NgnPresenceStatus DEFAULT_RCS_STATUS = NgnPresenceStatus.Online;

	//Error parameters
	public static final boolean DEFAULT_ERROR_PARAMETERS=getDefaultBoolean(R.bool.DEFAULT_ERROR_PARAMETERS);// = false;

	//profiles save
	public static final String DEFAULT_PROFILE_USE=getDefaultString(R.string.DEFAULT_PROFILE_USE);//  = "";

	private static String getDefaultString(int index){
		return NgnApplication.getContext().getResources().getString(index);
	}
	private static int getDefaultInt(int index){
		return NgnApplication.getContext().getResources().getInteger(index);
	}
	private static boolean getDefaultBoolean(int index){
		return NgnApplication.getContext().getResources().getBoolean(index);
	}
	private static float getDefaultFloat(int index){
		return Float.valueOf(NgnApplication.getContext().getResources().getString(index));
	}


}
