/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source Doubango Framework
* Copyright (C) 2010-2011 Mamadou Diop.
* Copyright (C) 2012 Doubango Telecom <http://doubango.org>
*
* This file is part of Open Source Doubango Framework.
*
* DOUBANGO is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* DOUBANGO is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with DOUBANGO.
*
*/
#ifndef TINYWRAP_SIPSESSION_H
#define TINYWRAP_SIPSESSION_H

#include "tinyWRAP_config.h"

#include "tinysip.h"
#include "tinymedia/tmedia_qos.h"
#include "ActionConfig.h"



class SipUri;
class SipStack;
class MsrpCallback;
class MediaSessionMgr;
//MCPTT
class McpttCallback;
class McpttMbmsCallback;
class XcapCallback;



/* ======================== T140Callback ========================*/
class TINYWRAP_API T140CallbackData{
	public:
#if !defined(SWIG)
	T140CallbackData(enum tmedia_t140_data_type_e data_type, const void* data_ptr, unsigned data_size){
		m_eType = data_type;
		m_pPtr = data_ptr;
		m_nSize = data_size;
	}
#endif
	virtual ~T140CallbackData(){}

	inline enum tmedia_t140_data_type_e getType()const{ return m_eType; }
	inline unsigned getSize()const{ return m_nSize; }
	inline unsigned getData(void* pOutput, unsigned nMaxsize)const{
		unsigned nRetsize = 0;
		if(pOutput && nMaxsize && m_pPtr){
			nRetsize = (m_nSize > nMaxsize) ? nMaxsize : m_nSize;
			memcpy(pOutput, m_pPtr, nRetsize);
		}
		return nRetsize;
	}

	private:
		enum tmedia_t140_data_type_e m_eType;
		const void* m_pPtr;
		unsigned m_nSize;
};

class TINYWRAP_API T140Callback
{
public:
	T140Callback() {}
	virtual ~T140Callback(){}
	virtual int ondata(const T140CallbackData* pData){ return 0; }
};

#if !defined(SWIG)
class RtcpCallbackData{
	public:
	RtcpCallbackData(enum tmedia_rtcp_event_type_e event_type, uint32_t ssrc_media){
		m_eType = event_type;
		m_nSSRC = ssrc_media;
	}
	virtual ~RtcpCallbackData(){}
	inline enum tmedia_rtcp_event_type_e getType()const{ return m_eType; }
	inline uint32_t getSSRC()const{ return m_nSSRC; }
	private:
		enum tmedia_rtcp_event_type_e m_eType;
		uint32_t m_nSSRC;
};

class TINYWRAP_API RtcpCallback
{
public:
	RtcpCallback() {}
	virtual ~RtcpCallback(){}
	virtual int onevent(const RtcpCallbackData* e){ return 0; }
};
#endif /* #if !defined(SWIG) */



/* ======================== SipSession ========================*/
class TINYWRAP_API SipSession
{
public:
	SipSession(SipStack* stack);
#if !defined(SWIG)
	SipSession(SipStack* stack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~SipSession();

public:
	bool haveOwnership();
	bool startMBMSManager(bool isStart);
	bool addHeader(const char* name, const char* value);
	bool removeHeader(const char* name);
	bool addCaps(const char* name, const char* value);
	bool addCaps(const char* name);
	bool removeCaps(const char* name);

	bool setExpires(unsigned expires);
	bool setFromUri(const char* fromUriString);
	bool setFromUri(const SipUri* fromUri);
	bool setToUri(const char* toUriString);
	bool setToUri(const SipUri* toUri);
	bool setSilentHangup(bool silent);
	bool addSigCompCompartment(const char* compId);
	bool removeSigCompCompartment();
#if !defined(SWIG)
	bool setAuth(const char* authHa1, const char* authIMPI);
#endif
	unsigned getId()const;

#if !defined(SWIG)
	bool setWebSocketSrc(const char* host, int32_t port, const char* proto);
	const SipStack* getStack() const;
	const tsip_ssession_handle_t* getWrappedSession() { return m_pHandle; }
#endif
	
private:
	void init(SipStack* stack, tsip_ssession_handle_t* pHandle=tsk_null);

protected:
	tsip_ssession_handle_t* m_pHandle;
	const SipStack* m_pStack;
};

/* ======================== InviteSession ========================*/
class TINYWRAP_API InviteSession : public SipSession
{
public: /* ctor() and dtor() */
	InviteSession(SipStack* Stack);
#if !defined(SWIG)
	InviteSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~InviteSession();

public: /* Public functions */
	bool accept(ActionConfig* config=tsk_null);
	bool hangup(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
	bool sendInfo(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	const MediaSessionMgr* getMediaMgr();

private:
	MediaSessionMgr* m_pMediaMgr;
};


/* ======================== CallSession ========================*/
class TINYWRAP_API CallSession : public InviteSession
{
public: /* ctor() and dtor() */
	CallSession(SipStack* pStack);
#if !defined(SWIG)
	CallSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~CallSession();

public: /* Public functions */
	bool callAudio(const char* remoteUriString, ActionConfig* config=tsk_null); /* @deprecated */
	bool callAudio(const SipUri* remoteUri, ActionConfig* config=tsk_null); /* @deprecated */
	bool callAudioVideo(const char* remoteUriString, ActionConfig* config=tsk_null); /* @deprecated */
	bool callAudioVideo(const SipUri* remoteUri, ActionConfig* config=tsk_null); /* @deprecated */
	bool callVideo(const char* remoteUriString, ActionConfig* config=tsk_null); /* @deprecated */
	bool callVideo(const SipUri* remoteUri, ActionConfig* config=tsk_null); /* @deprecated */

	bool call(const char* remoteUriString, twrap_media_type_t media
			,const bool answer_mode_auto
			, ActionConfig* config=tsk_null);
	bool call(const SipUri* remoteUri, twrap_media_type_t media
			,const bool answer_mode_auto
			, ActionConfig* config=tsk_null);
	bool callEmergency(const char* remoteUriString, twrap_media_type_t media
			,const bool answerMode
			,const char* emergencyType/*=tsk_null*/,const int levelEmergency, ActionConfig* config=tsk_null);
	bool callEmergency(const SipUri* remoteUri, twrap_media_type_t media
			,const bool answerMode
			,const char* emergencyType/*=tsk_null*/,const int levelEmergency, ActionConfig* config=tsk_null);
	bool callGroup(char** user_list, int user_count, twrap_media_type_t media, ActionConfig* config);
	


#if !defined(SWIG)
	bool setSupportedCodecs(int32_t codecs);
	int32_t getNegotiatedCodecs();
	bool setMediaSSRC(twrap_media_type_t media, uint32_t ssrc);
#endif
	bool setSessionTimer(unsigned timeout, const char* refresher);
	bool set100rel(bool enabled);
	bool setRtcp(bool enabled);
	bool setRtcpMux(bool enabled);
	bool setSRtpMode(enum tmedia_srtp_mode_e mode);
	bool setAvpfMode(enum tmedia_mode_e mode);
	bool setICE(bool enabled);
	bool setICEStun(bool enabled);
	bool setICETurn(bool enabled);
	bool setSTUNServer(const char* hostname, uint16_t port);
	bool setSTUNCred(const char* username, const char* password);
	bool setVideoFps(int32_t fps);
	bool setVideoBandwidthUploadMax(int32_t max);
	bool setVideoBandwidthDownloadMax(int32_t max);
	bool setVideoPrefSize(tmedia_pref_video_size_t pref_video_size);
	bool setQoS(tmedia_qos_stype_t type, tmedia_qos_strength_t strength);
	bool setPoCQoE(tmedia_poc_qoe_profile_t profile, tmedia_poc_qoe_profile_strength_t strength);
	bool hold(ActionConfig* config=tsk_null);
	bool resume(ActionConfig* config=tsk_null);
	bool transfer(const char* referToUriString, ActionConfig* config=tsk_null);
	bool acceptTransfer(ActionConfig* config=tsk_null);
	bool rejectTransfer(ActionConfig* config=tsk_null);
	bool sendDTMF(int number);
	unsigned getSessionTransferId();
	bool sendT140Data(enum tmedia_t140_data_type_e data_type, const void* data_ptr = NULL, unsigned data_size = 0);
	bool setT140Callback(const T140Callback* pT140Callback);
	//MCPTT
	bool setMcpttCallback(const McpttCallback* pMcpttCallback);
	bool setMcpttMbmsCallback(const McpttMbmsCallback* pMcpttMbmsCallback);
	//MCPTT
	bool requestMcpttToken(ActionConfig* config=tsk_null);
	bool releaseMcpttToken(ActionConfig* config=tsk_null);
	//MCPTT MBMS
	bool startMbmsManager(ActionConfig* config/*=tsk_null*/,const char* remoteIP,int remotePort, const char* localIface, int localIfaceIdx);
	bool stopMbmsManager(ActionConfig* config/*=tsk_null*/ );
	bool startMbmsMedia(ActionConfig* config/*=tsk_null*/, const char* mediaIP, int mediaPort, int mediaCtrlPort);
	const char* getPTTMcpttGroupIdentity();
	int getPTTMcpttGroupMembers();
	const char* getPTTMcpttGroupMemberAtPosition(int pos);
	const char* getPttMcpttEmergencyResourcePriorityString();
	const int getPttMcpttEmergencyResourcePriority();
	//
	char* getSipPartyUri();


#if !defined(SWIG)
	bool sendRtcpEvent(enum tmedia_rtcp_event_type_e event_type, twrap_media_type_t media_type, uint32_t ssrc_media = 0);
	bool setRtcpCallback(const RtcpCallback* pRtcpCallback, twrap_media_type_t media_type);
	const T140Callback* getT140Callback() const;
	static int t140OnDataCallback(const void* context, enum tmedia_t140_data_type_e data_type, const void* data_ptr, unsigned data_size);
	const RtcpCallback* getRtcpCallback() const;
	static int rtcpOnCallback(const void* context, enum tmedia_rtcp_event_type_e event_type, uint32_t ssrc_media);
	//MCPTT
	const McpttCallback* getMcpttCallback() const;
	const McpttMbmsCallback* getMcpttMbmsCallback() const;
	const XcapCallback* getXcapCallback() const;
	
#endif /* #if !defined(SWIG) */

private:
	const T140Callback* m_pT140Callback;
	const RtcpCallback* m_pRtcpCallback;
	//MCPTT
	const McpttCallback* m_pMcpttCallback;
	const McpttMbmsCallback* m_pMcpttMbmsCallback;
	const XcapCallback* m_pPTTXcapCallback;
};

/* ======================== MsrpSession ========================*/
class TINYWRAP_API MsrpSession : public InviteSession
{
public: /* ctor() and dtor() */
	MsrpSession(SipStack* pStack, MsrpCallback* pCallback);
#if !defined(SWIG)
	MsrpSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~MsrpSession();

public: /* Public functions */
	bool setCallback(MsrpCallback* pCallback);
	bool callMsrp(const char* remoteUriString, ActionConfig* config=tsk_null);
	bool callMsrp(const SipUri* remoteUri, ActionConfig* config=tsk_null);
	bool sendMessage(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool sendFile(ActionConfig* config=tsk_null);

	public: /* Public helper function */
#if !defined(SWIG)
		inline MsrpCallback* getCallback()const{
			return m_pCallback;
		}
#endif

private:
	MsrpCallback* m_pCallback;
};

/* ======================== McpttSession ========================*/
class TINYWRAP_API McpttSession : public InviteSession
{
public: /* ctor() and dtor() */
	McpttSession(SipStack* pStack, McpttCallback* pCallback);
#if !defined(SWIG)
	McpttSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~McpttSession();

public: /* Public functions */
	bool setCallback(McpttCallback* pCallback);
	bool requestToken(ActionConfig* config=tsk_null);

	public: /* Public helper function */
#if !defined(SWIG)
		inline McpttCallback* getCallback()const{
			return m_pCallback;
		}
#endif

private:
	McpttCallback* m_pCallback;
};


/* ======================== MessagingSession ========================*/
class TINYWRAP_API MessagingSession : public SipSession
{
public: /* ctor() and dtor() */
	MessagingSession(SipStack* pStack);
#if !defined(SWIG)
	MessagingSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~MessagingSession();

public: /* Public functions */
	bool send(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};


//
/* ======================== MessagingLocationSession ========================*/
class TINYWRAP_API MessagingLocationSession : public SipSession
{
public: /* ctor() and dtor() */
	MessagingLocationSession(SipStack* pStack);
#if !defined(SWIG)
	MessagingLocationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~MessagingLocationSession();

public: /* Public functions */
	bool send(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};
/* ======================== MessagingAffiliationSession ========================*/
class TINYWRAP_API MessagingAffiliationSession : public SipSession
{
public: /* ctor() and dtor() */
	MessagingAffiliationSession(SipStack* pStack);
#if !defined(SWIG)
	MessagingAffiliationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~MessagingAffiliationSession();

public: /* Public functions */
	bool send(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};
/* ======================== MessagingMbmsSession ========================*/
class TINYWRAP_API MessagingMbmsSession : public SipSession
{
public: /* ctor() and dtor() */
	MessagingMbmsSession(SipStack* pStack);
#if !defined(SWIG)
	MessagingMbmsSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~MessagingMbmsSession();

public: /* Public functions */
	bool send(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};

/* ======================== InfoSession ========================*/
class TINYWRAP_API InfoSession : public SipSession
{
public: /* ctor() and dtor() */
	InfoSession(SipStack* pStack);
#if !defined(SWIG)
	InfoSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~InfoSession();

public: /* Public functions */
	bool send(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};

/* ======================== OptionsSession ========================*/
class TINYWRAP_API OptionsSession : public SipSession
{
public: /* ctor() and dtor() */
	OptionsSession(SipStack* pStack);
#if !defined(SWIG)
	OptionsSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~OptionsSession();

public: /* Public functions */
	bool send(ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);
};



/* ======================== PublicationSession ========================*/
class TINYWRAP_API PublicationSession : public SipSession
{
public: /* ctor() and dtor() */
	PublicationSession(SipStack* pStack);
#if !defined(SWIG)
	PublicationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~PublicationSession();

public: /* Public functions */
	bool publish(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool unPublish(ActionConfig* config=tsk_null);
};

/* ======================== PublicationAffiliationSession ========================*/
class TINYWRAP_API PublicationAffiliationSession : public SipSession
{
public: /* ctor() and dtor() */
	PublicationAffiliationSession(SipStack* pStack);
#if !defined(SWIG)
	PublicationAffiliationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~PublicationAffiliationSession();

public: /* Public functions */
	bool publish(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool unPublish(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	/*bool unPublish(ActionConfig* config=tsk_null);*/
	
};
/* ======================== PublicationAuthenticationSession ========================*/
class TINYWRAP_API PublicationAuthenticationSession : public SipSession
{
public: /* ctor() and dtor() */
	PublicationAuthenticationSession(SipStack* pStack);
#if !defined(SWIG)
	PublicationAuthenticationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~PublicationAuthenticationSession();

public: /* Public functions */
	bool publish(const char* mcptt_info,const char* poc_settings,const void* payload, unsigned len, ActionConfig* config=tsk_null);
	bool unPublish(const void* payload, unsigned len, ActionConfig* config=tsk_null);
	/*bool unPublish(ActionConfig* config=tsk_null);*/
	
};

/* ======================== RegistrationSession ========================*/
class TINYWRAP_API RegistrationSession : public SipSession
{
public: /* ctor() and dtor() */
	RegistrationSession(SipStack* pStack);
#if !defined(SWIG)
	RegistrationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~RegistrationSession();
    
public: /* Public functions */
	//bool register_(ActionConfig* config=tsk_null);

	bool register_(ActionConfig* config=tsk_null);
	bool registerWithMcpttInfo(const void* payload, unsigned len,ActionConfig* config=tsk_null);
	bool unRegister(ActionConfig* config=tsk_null);
	bool accept(ActionConfig* config=tsk_null);
	bool reject(ActionConfig* config=tsk_null);



};


/* ======================== SubscriptionSession ========================*/
class TINYWRAP_API SubscriptionSession : public SipSession
{
public: /* ctor() and dtor() */
	SubscriptionSession(SipStack* pStack);
#if !defined(SWIG)
	SubscriptionSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~SubscriptionSession();

public: /* Public functions */
	bool subscribe();
	bool unSubscribe();
};

/* ======================== SubscriptionAffiliationSession ========================*/
class TINYWRAP_API SubscriptionAffiliationSession : public SipSession
{
public: /* ctor() and dtor() */
	SubscriptionAffiliationSession(SipStack* pStack);
#if !defined(SWIG)
	SubscriptionAffiliationSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~SubscriptionAffiliationSession();

public: /* Public functions */
	bool subscribeAffiliation();
	bool unSubscribeAffiliation();
};

/* ======================== SubscriptionCMSSession ========================*/
class TINYWRAP_API SubscriptionCMSSession : public SipSession
{
public: /* ctor() and dtor() */
	SubscriptionCMSSession(SipStack* pStack);
#if !defined(SWIG)
	SubscriptionCMSSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~SubscriptionCMSSession();

public: /* Public functions */
	bool subscribeCMS(const void* payload, unsigned len,const void* payload2, unsigned len2,ActionConfig* config=tsk_null);
	bool unSubscribeCMS();
};

/* ======================== SubscriptionGMDSession ========================*/
class TINYWRAP_API SubscriptionGMSSession : public SipSession
{
public: /* ctor() and dtor() */
	SubscriptionGMSSession(SipStack* pStack);
#if !defined(SWIG)
	SubscriptionGMSSession(SipStack* pStack, tsip_ssession_handle_t* pHandle);
#endif
	virtual ~SubscriptionGMSSession();

public: /* Public functions */
	bool subscribeGMS(const void* payload, unsigned len, const void* payload2, unsigned len2,ActionConfig* config=tsk_null);
	bool unSubscribeGMS();
};


#endif /* TINYWRAP_SIPSESSION_H */
