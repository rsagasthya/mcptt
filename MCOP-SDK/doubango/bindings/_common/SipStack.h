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
#ifndef TINYWRAP_SIPSTACK_H
#define TINYWRAP_SIPSTACK_H

#include "tinyWRAP_config.h"

#include "SipCallback.h"
#include "SafeObject.h"

#include "tinydav/tdav.h"
#include "tinysip.h"

class DDebugCallback;
//
class DRegisterCallback;



class TINYWRAP_API SipStack: public SafeObject
{
public: /* ctor() and dtor() */
	SipStack(SipCallback* pCallback, const char* realm_uri, const char* impi_uri, const char* impu_uri);
    ~SipStack();

public: /* API functions */
	bool start();
	bool setDebugCallback(DDebugCallback* pCallback);

	bool setRegisterCallback(DRegisterCallback* pCallback,void* dataResponseRegisterCallback,int size);
	bool setDisplayName(const char* display_name);
	bool setRealm(const char* realm_uri);
	bool setIMPI(const char* impi);
	bool setIMPU(const char* impu_uri);
	bool setIMPUIP(const char* impuip_uri);
	bool setPassword(const char* password);
	bool setAMF(const char* amf);
	bool setOperatorId(const char* opid);
	bool setProxyCSCF(const char* fqdn, unsigned short port, const char* transport, const char* ipversion);
	bool setLocalIP(const char* ip, const char* transport=tsk_null);
	bool setLocalPort(unsigned short port, const char* transport=tsk_null);
	bool setEarlyIMS(bool enabled);
	bool addHeader(const char* name, const char* value);
	bool removeHeader(const char* name);
	bool addDnsServer(const char* ip);
	bool setDnsDiscovery(bool enabled);
	bool setAoR(const char* ip, int port);
#if !defined(SWIG)
	bool setMode(enum tsip_stack_mode_e mode);
#endif

	bool setSigCompParams(unsigned dms, unsigned sms, unsigned cpb, bool enablePresDict);
	bool addSigCompCompartment(const char* compId);
	bool removeSigCompCompartment(const char* compId);
	
	bool setSTUNEnabledForICE(bool enabled);  // @deprecated
	bool setSTUNServer(const char* hostname, unsigned short port);  // @deprecated
	bool setSTUNCred(const char* login, const char* password);  // @deprecated
	bool setSTUNEnabled(bool enabled);

	bool setTLSSecAgree(bool enabled);
	bool setSSLCertificates(const char* privKey, const char* pubKey, const char* caKey, bool verify = false);
	bool setSSLCretificates(const char* privKey, const char* pubKey, const char* caKey, bool verify = false); /*@deprecated: typo */
	bool setIPSecSecAgree(bool enabled);
	bool setIPSecParameters(const char* algo, const char* ealgo, const char* mode, const char* proto);
	//MCPTT
	bool setMCPTTPSIPrivate(const char* mcptt_psi_private);
	bool setMCPTTPSIGroup(const char* mcptt_psi_group);
	bool setMCPTTPSIPreestablished(const char* mcptt_psi_preestablished);

	bool setMCPTTPSICMS(const char* mcptt_psi_cms);

	bool setMCPTTPSIGMS(const char* mcptt_psi_gms);

	bool setMCPTTID(const char* mcptt_id);
	bool setMCPTTClientID(const char* mcptt_client_id);


	bool setMCPTTPriority(const int mcptt_priority);
	bool setMCPTTImplicit(const bool mcptt_implicit);
	bool setMCPTTGranted(const bool mcptt_granted);
	bool setMCPTTAnswerMode(const bool mcptt_answer_mode);
	bool setMCPTTPrivAnswerMode(const bool mcptt_priv_answer_mode);
	bool setMCPTTNameSpace(const bool mcptt_namespace);
	bool setMCPTTInsertXFramedIP(const bool mcptt_insert_x_framed_ip);
	//Timers recived from CMS
	bool setMCPTTTimerT100(const int mcptt_timer_t100);
	bool setMCPTTTimerT101(const int mcptt_timer_t101);
	bool setMCPTTTimerT103(const int mcptt_timer_t103);
	bool setMCPTTTimerT104(const int mcptt_timer_t104);
	bool setMCPTTTimerT132(const int mcptt_timer_t132);
	//MCPTT LOCATION
	bool setLocationPAssertedIdentityServer(const char* serverUriSIP);
	//MCPTT MBMS
	bool setMbmsPAssertedIdentityServer(const char* serverUriSIP);
	bool setMbmsPortManager(unsigned int portManager);
	bool setMbmsAddrManager(const char* addrManager);
	bool setMbmsIsRTCPMux(const bool isRTCPMux);




	//MCPTT AFFILIATION
	bool setMCPTTPSIAffiliation(const char* mcptt_psi_affiliation);
	bool setMCPTTAffiliationIsEnable(const bool mcptt_affiliation_is_enable);
	bool setMCPTTAffiliationGroupsDefualt(const char* mcptt_affiliation_groups_default);
	//MCPTT AUTHENTICATION
	bool setMCPTTPSIAuthentication(const char* mcptt_psi_authentication);
	char* dnsENUM(const char* service, const char* e164num, const char* domain);
	char* dnsNaptrSrv(const char* domain, const char* service, unsigned short *OUTPUT);
	char* dnsSrv(const char* service, unsigned short* OUTPUT);

	bool setMaxFDs(unsigned max_fds);

	char* getLocalIPnPort(const char* protocol, unsigned short* OUTPUT);

	char* getPreferredIdentity();

	bool isValid();
	bool stop();
	
	static bool initialize();
	static bool deInitialize();
	static void setCodecs(tdav_codec_id_t codecs);
	static void setCodecs_2(int64_t codecs); // For stupid languages
	static bool setCodecPriority(tdav_codec_id_t codec_id, int priority);
	static bool setCodecPriority_2(int codec, int priority);// For stupid languages
	static bool isCodecSupported(tdav_codec_id_t codec_id);
	static bool isIPSecSupported();

public: /* Public helper function */
#if !defined(SWIG)
	inline tsip_stack_handle_t* getHandle()const{
		return m_pHandle;
	}
	inline SipCallback* getCallback()const{
		return m_pCallback;
	}
	inline DDebugCallback* getDebugCallback() const{
		return m_pDebugCallback;
	}
	//
	//This CallBack is from Register session.
	inline DRegisterCallback* getRegisterCallback() const{
		return m_pRegisterCallback;
	}

	inline void* getdataRegisterCallback() const{
		return m_dataResponseRegisterCallback;
	}
	inline int lengthDataRegisterCallback() const{
		return size_dataResponseRegisterCallback;
	}
#endif



private:
	SipCallback* m_pCallback;
	DDebugCallback* m_pDebugCallback;
	//
	DRegisterCallback* m_pRegisterCallback;
	int size_dataResponseRegisterCallback;
	void* m_dataResponseRegisterCallback;
	tsip_stack_handle_t* m_pHandle;

	static bool g_bInitialized;
};

#endif /* TINYWRAP_SIPSTACK_H */
