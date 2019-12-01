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
#include "SipStack.h"

#include "SipSession.h"
#include "SipEvent.h"

#include "DDebug.h"
#include "DRegisterCallback.h"
#include "tsk_register.h"
#include "Common.h"

bool SipStack::g_bInitialized = false;


/* === ANSI-C functions (local use) === */
static int stack_callback(const tsip_event_t *sipevent);
static int session_handle_event(const tsip_event_t *sipevent);

SipStack::SipStack(SipCallback* pCallback, const char* realm_uri, const char* impi_uri, const char* impu_uri)
:SafeObject()
{
	m_pDebugCallback = tsk_null;
	m_pCallback = pCallback;

	/* Initialize network and media layers */
	if(!SipStack::initialize()){
		return;// isValid() will be false
	}

	/* Creates stack handle */
	m_pHandle = tsip_stack_create(stack_callback, realm_uri, impi_uri, impu_uri,
			TSIP_STACK_SET_USERDATA(this), /* used as context (useful for server-initiated requests) */
			TSIP_STACK_SET_NULL());
}

SipStack::~SipStack()
{
	this->stop();

	/* Destroy stack handle */
	TSK_OBJECT_SAFE_FREE(m_pHandle);
}

bool SipStack::start()
{
	bool ret = (tsip_stack_start(m_pHandle) == 0);
	return ret;
}

bool SipStack::setDebugCallback(DDebugCallback* pCallback)
{
	if(this && pCallback){
		m_pDebugCallback = pCallback;
		tsk_debug_set_arg_data(this);
		tsk_debug_set_test_cb(DDebugCallback::debug_test_cb);
		tsk_debug_set_info_cb(DDebugCallback::debug_info_cb);
		tsk_debug_set_warn_cb(DDebugCallback::debug_warn_cb);
		tsk_debug_set_error_cb(DDebugCallback::debug_error_cb);
		tsk_debug_set_fatal_cb(DDebugCallback::debug_fatal_cb);
	}
	else if(this){
		m_pDebugCallback = tsk_null;
		tsk_debug_set_arg_data(tsk_null);
		tsk_debug_set_test_cb(tsk_null);
		tsk_debug_set_info_cb(tsk_null);
		tsk_debug_set_warn_cb(tsk_null);
		tsk_debug_set_error_cb(tsk_null);
		tsk_debug_set_fatal_cb(tsk_null);
	}

	return true;
}

//
bool SipStack::setRegisterCallback(DRegisterCallback* pCallback,void* dataResponseRegisterCallback,int size)
{
	if(this && pCallback){
		m_dataResponseRegisterCallback=dataResponseRegisterCallback;
		size_dataResponseRegisterCallback=size;
		m_pRegisterCallback = pCallback;
		tsk_register_set_arg_data(this);
		tsk_register_set_cb(DRegisterCallback::auth_register_cb);
	}
	return true;
}

bool SipStack::setDisplayName(const char* display_name)
{
	int ret = tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_DISPLAY_NAME(display_name),
		TSIP_STACK_SET_NULL());
	return (ret == 0);
}

bool SipStack::setRealm(const char* realm_uri)
{
	int ret = tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_REALM(realm_uri),
		TSIP_STACK_SET_NULL());
	return (ret == 0);
}

bool SipStack::setIMPI(const char* impi)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_IMPI(impi),
		TSIP_STACK_SET_NULL()) == 0);
}


bool SipStack::setIMPU(const char* impu_uri)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_IMPU(impu_uri),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setIMPUIP(const char* impuip_uri)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_IMPU(impuip_uri),
		TSIP_STACK_SET_NULL()) == 0);
}


bool SipStack::setPassword(const char* password)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_PASSWORD(password),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setAMF(const char* amf)
{
	uint16_t _amf = (uint16_t)tsk_atox(amf);
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_IMS_AKA_AMF(_amf),
			TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setOperatorId(const char* opid)
{
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_IMS_AKA_OPERATOR_ID(opid),
			TSIP_STACK_SET_NULL()) == 0); 
}

bool SipStack::setProxyCSCF(const char* fqdn, unsigned short port, const char* transport, const char* ipversion)
{
	unsigned _port = port;//promote
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_PROXY_CSCF(fqdn, _port, transport, ipversion),
			TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setLocalIP(const char* ip, const char* transport/*=tsk_null*/)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_LOCAL_IP_2(transport, ip),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setLocalPort(unsigned short port, const char* transport/*=tsk_null*/)
{
	unsigned _port = port;//promote
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_LOCAL_PORT_2(transport, _port),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setEarlyIMS(bool enabled){
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_EARLY_IMS(enabled? tsk_true : tsk_false),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::addHeader(const char* name, const char* value)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_HEADER(name, value),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::removeHeader(const char* name)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_UNSET_HEADER(name),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::addDnsServer(const char* ip)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_DNS_SERVER(ip),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setDnsDiscovery(bool enabled)
{
	tsk_bool_t _enabled = enabled;// 32bit/64bit workaround
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_DISCOVERY_NAPTR(_enabled),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setAoR(const char* ip, int port)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_AOR(ip, port),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMode(enum tsip_stack_mode_e mode)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MODE(mode),
		TSIP_STACK_SET_NULL()) == 0); 
}

bool SipStack::setSigCompParams(unsigned dms, unsigned sms, unsigned cpb, bool enablePresDict)
{
	tsk_bool_t _enablePresDict= enablePresDict;// 32bit/64bit workaround
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_SIGCOMP(dms, sms, cpb, _enablePresDict),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::addSigCompCompartment(const char* compId)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_SIGCOMP_NEW_COMPARTMENT(compId),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::removeSigCompCompartment(const char* compId)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_UNSET_SIGCOMP_COMPARTMENT(compId),
		TSIP_STACK_SET_NULL()) == 0);
}

 // @deprecated
bool SipStack::setSTUNEnabledForICE(bool enabled)
{
#if 0
	tsk_bool_t _enabled = enabled ? tsk_true : tsk_false;
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_ICE_STUN_ENABLED(_enabled),
		TSIP_STACK_SET_NULL()) == 0);
#else
	// set global value
	return (tmedia_defaults_set_icestun_enabled(enabled ? tsk_true : tsk_false) == 0);
	// to set the value per session, use "CallSession::setICEStun()"
#endif
}

 // @deprecated
bool SipStack::setSTUNServer(const char* hostname, unsigned short port)
{
#if 0
	unsigned _port = port;//promote
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_STUN_SERVER(hostname, _port),
		TSIP_STACK_SET_NULL()) == 0);
#else
	// set global value
	return (tmedia_defaults_set_stun_server(hostname, port) == 0);
	// to set the value per session, use "CallSession::setSTUNServer()"
#endif
}

 // @deprecated
bool SipStack::setSTUNCred(const char* login, const char* password)
{
#if 0
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_STUN_CRED(login, password),
		TSIP_STACK_SET_NULL()) == 0);
#else
	// set global value
	return (tmedia_defaults_set_stun_cred(login, password) == 0);
	// to set the value per session, use "CallSession::setSTUNCred()"
#endif
}

bool SipStack::setSTUNEnabled(bool enabled)
{
	tsk_bool_t _enabled = enabled ? tsk_true : tsk_false;
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_STUN_ENABLED(_enabled),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setTLSSecAgree(bool enabled)
{
	tsk_bool_t _enable = enabled ? tsk_true : tsk_false;
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_SECAGREE_TLS(_enable),
		TSIP_STACK_SET_NULL()) == 0);
}

/*@deprecated: typo  */
bool SipStack::setSSLCretificates(const char* privKey, const char* pubKey, const char* caKey, bool verify/* = false*/)
{
	return setSSLCertificates(privKey, pubKey, caKey, verify);
}

bool SipStack::setSSLCertificates(const char* privKey, const char* pubKey, const char* caKey, bool verify/* = false*/)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_TLS_CERTS_2(caKey, pubKey, privKey, (verify ? tsk_true : tsk_false)),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setIPSecSecAgree(bool enabled)
{
	tsk_bool_t _enable = enabled;
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_SECAGREE_IPSEC(_enable),
		TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setIPSecParameters(const char* algo, const char* ealgo, const char* mode, const char* proto)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_IPSEC_PARAMS(algo, ealgo, mode, proto),
		TSIP_STACK_SET_NULL()) == 0);
}

char* SipStack::dnsENUM(const char* service, const char* e164num, const char* domain)
{
	tnet_dns_ctx_t* dnsctx = tsip_stack_get_dnsctx(m_pHandle);
	char* uri = tsk_null;

	if(dnsctx){
		if(!(uri = tnet_dns_enum_2(dnsctx, service, e164num, domain))){
			TSK_DEBUG_ERROR("ENUM(%s) failed", e164num);
		}
		tsk_object_unref(dnsctx);
		return uri;
	}
	else{
		TSK_DEBUG_ERROR("No DNS Context could be found");
		return tsk_null;
	}
}

char* SipStack::dnsNaptrSrv(const char* domain, const char* service, unsigned short *OUTPUT)
{
	tnet_dns_ctx_t* dnsctx = tsip_stack_get_dnsctx(m_pHandle);
	char* ip = tsk_null;
	tnet_port_t port;
	*OUTPUT = 0;
	

	if(dnsctx){
		if(!tnet_dns_query_naptr_srv(dnsctx, domain, service, &ip, &port)){
			*OUTPUT = port;
		}
		tsk_object_unref(dnsctx);
		return ip;
	}
	else{
		TSK_DEBUG_ERROR("No DNS Context could be found");
		return tsk_null;
	}
}

char* SipStack::dnsSrv(const char* service, unsigned short* OUTPUT)
{
	tnet_dns_ctx_t* dnsctx = tsip_stack_get_dnsctx(m_pHandle);
	char* ip = tsk_null;
	tnet_port_t port = 0;
	*OUTPUT = 0;

	if(dnsctx){
		if(!tnet_dns_query_srv(dnsctx, service, &ip, &port)){
			*OUTPUT = port;
		}
		tsk_object_unref(dnsctx);
		return ip;
	}
	else{
		TSK_DEBUG_ERROR("No DNS Context could be found");
		return tsk_null;
	}
}

bool SipStack::setMaxFDs(unsigned max_fds)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MAX_FDS(max_fds),
		TSIP_STACK_SET_NULL()) == 0);
}

char* SipStack::getLocalIPnPort(const char* protocol, unsigned short* OUTPUT)
{
	tnet_ip_t ip;
	tnet_port_t port;
	int ret;

	if(!OUTPUT || !protocol){
		TSK_DEBUG_ERROR("invalid parameter");
		return tsk_null;
	}

	if((ret = tsip_stack_get_local_ip_n_port(m_pHandle, protocol, &port, &ip))){
		TSK_DEBUG_ERROR("Failed to get local ip and port with error code=%d", ret);
		return tsk_null;
	}

	*OUTPUT = port;
	return tsk_strdup(ip); // See Swig %newobject
}

char* SipStack::getPreferredIdentity()
{
	tsip_uri_t* ppid = tsip_stack_get_preferred_id(m_pHandle);
	char* str_ppid = tsk_null;
	if(ppid){
		str_ppid = tsip_uri_tostring(ppid, tsk_false, tsk_false);
		TSK_OBJECT_SAFE_FREE(ppid);
	}
	return str_ppid;
}

bool SipStack::isValid()
{
	return (m_pHandle != tsk_null);
}

bool SipStack::stop()
{
	int ret = tsip_stack_stop(m_pHandle);
	return (ret == 0);
}

bool SipStack::initialize()
{
	if (!g_bInitialized) {
		int ret;
		
		if((ret = tnet_startup())){
			TSK_DEBUG_ERROR("tnet_startup failed with error code=%d", ret);
			return false;
		}
		if((ret = tdav_init())){
			TSK_DEBUG_ERROR("tdav_init failed with error code=%d", ret);
			return false;
		}
		g_bInitialized = true;
	}
	return true;
}

bool SipStack::deInitialize()
{
	if (SipStack::g_bInitialized) {
		tdav_deinit();
		tnet_cleanup();
		SipStack::g_bInitialized = false;
	}
	return false;
}

void SipStack::setCodecs(tdav_codec_id_t codecs)
{
	tdav_set_codecs(codecs);
}

void SipStack::setCodecs_2(int64_t codecs) // For stupid languages
{
	SipStack::setCodecs((tdav_codec_id_t)codecs);
}

bool SipStack::setCodecPriority(tdav_codec_id_t codec_id, int priority)
{
	return tdav_codec_set_priority(codec_id, priority) == 0;
}

bool SipStack::setCodecPriority_2(int codec_id, int priority)// For stupid languages
{
	return SipStack::setCodecPriority((tdav_codec_id_t)codec_id, priority);
}

bool SipStack::isCodecSupported(tdav_codec_id_t codec_id)
{
	return tdav_codec_is_supported(codec_id) ? true : false;
}

bool SipStack::isIPSecSupported()
{
	return tdav_ipsec_is_supported() ? true : false;
}
bool SipStack::setMCPTTPSIPrivate(const char* mcptt_psi_private){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PSI_PRIVATE(mcptt_psi_private),
			TSIP_STACK_SET_NULL()) == 0);
}
bool SipStack::setMCPTTPSIGroup(const char* mcptt_psi_group){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PSI_GROUP(mcptt_psi_group),
			TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMCPTTPSIPreestablished(const char* mcptt_psi_preestablished){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PSI_PREESTABLISHED(mcptt_psi_preestablished),
			TSIP_STACK_SET_NULL()) == 0);
}


bool SipStack::setMCPTTPSICMS(const char* mcptt_psi_cms){
	return (tsip_stack_set(m_pHandle,
						   TSIP_STACK_SET_MCPTT_PSI_CMS(mcptt_psi_cms),
						   TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMCPTTPSIGMS(const char* mcptt_psi_gms){
	return (tsip_stack_set(m_pHandle,
						   TSIP_STACK_SET_MCPTT_PSI_GMS(mcptt_psi_gms),
						   TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMCPTTID(const char* mcptt_id){
	return (tsip_stack_set(m_pHandle,
						   TSIP_STACK_SET_MCPTT_ID(mcptt_id),
						   TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMCPTTClientID(const char* mcptt_client_id){
	return (tsip_stack_set(m_pHandle,
						   TSIP_STACK_SET_MCPTT_CLIENT_ID(mcptt_client_id),
						   TSIP_STACK_SET_NULL()) == 0);
}



bool SipStack::setMCPTTPriority(const int mcptt_priority){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PRIORITY(mcptt_priority),
			TSIP_STACK_SET_NULL()) == 0);
}

bool SipStack::setMCPTTImplicit(const bool mcptt_implicit){
	tsk_bool_t result;
	if(mcptt_implicit){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_IMPLICIT(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}


bool SipStack::setMCPTTGranted(const bool mcptt_granted){
	tsk_bool_t result;
	if(mcptt_granted){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_GRANTED(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}






bool SipStack::setMCPTTAnswerMode(const bool mcptt_answer_mode){
	tsk_bool_t result;
	if(mcptt_answer_mode){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_ANSWER_MODE(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}

bool SipStack::setMCPTTPrivAnswerMode(const bool mcptt_priv_answer_mode){
	tsk_bool_t result;
	if(mcptt_priv_answer_mode){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PRIV_ANSWER_MODE(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}

bool SipStack::setMCPTTNameSpace(const bool mcptt_namespace){
	tsk_bool_t result;
	if(mcptt_namespace){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_NAMESPACE(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}

bool SipStack::setMCPTTInsertXFramedIP(const bool mcptt_insert_x_framed_ip){
	tsk_bool_t result;
	if(mcptt_insert_x_framed_ip){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_INSERT_X_FRAMED_IP(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}




//Timers recived from CMS
bool SipStack::setMCPTTTimerT100(const int mcptt_timer_t100){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_TIMER_T100(mcptt_timer_t100),
			TSIP_STACK_SET_NULL()) == 0);
}
bool SipStack::setMCPTTTimerT101(const int mcptt_timer_t101){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_TIMER_T101(mcptt_timer_t101),
			TSIP_STACK_SET_NULL()) == 0);
}
bool SipStack::setMCPTTTimerT103(const int mcptt_timer_t103){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_TIMER_T103(mcptt_timer_t103),
			TSIP_STACK_SET_NULL()) == 0);
}
bool SipStack::setMCPTTTimerT104(const int mcptt_timer_t104){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_TIMER_T104(mcptt_timer_t104),
			TSIP_STACK_SET_NULL()) == 0);
}
bool SipStack::setMCPTTTimerT132(const int mcptt_timer_t132){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_TIMER_T132(mcptt_timer_t132),
			TSIP_STACK_SET_NULL()) == 0);
}






//Location
bool SipStack::setLocationPAssertedIdentityServer(const char* serverUriSIP)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_LOCATION_P_ASSERTED_IDENTITY(serverUriSIP),
		TSIP_SSESSION_SET_NULL()) == 0);
}
//MCPTT MBMS
bool SipStack::setMbmsPAssertedIdentityServer(const char* serverUriSIP)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MBMS_P_ASSERTED_IDENTITY(serverUriSIP),
		TSIP_SSESSION_SET_NULL()) == 0);
}

//MCPTT MBMS
bool SipStack::setMbmsPortManager(unsigned int portManager)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MBMS_PORT_MANAGER(portManager),
		TSIP_SSESSION_SET_NULL()) == 0);
}
//MCPTT MBMS
bool SipStack::setMbmsAddrManager(const char* addrManager)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MBMS_ADDR_MANAGER(addrManager),
		TSIP_SSESSION_SET_NULL()) == 0);
}
//MCPTT MBMS
bool SipStack::setMbmsIsRTCPMux(const bool isRTCPMux)
{
	return (tsip_stack_set(m_pHandle,
		TSIP_STACK_SET_MBMS_IS_RTCP_MUX(isRTCPMux),
		TSIP_SSESSION_SET_NULL()) == 0);
}



//MCPTT AFFILIATION
bool SipStack::setMCPTTPSIAffiliation(const char* mcptt_psi_affiliation){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PSI_AFFILIATION(mcptt_psi_affiliation),
			TSIP_STACK_SET_NULL()) == 0);
}



bool SipStack::setMCPTTAffiliationIsEnable(const bool mcptt_affiliation_is_enable){
	tsk_bool_t result;
	if(mcptt_affiliation_is_enable){
		result=tsk_true;
	}else{
		result=tsk_false;
	}
	
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_AFFILIATION_IS_ENABLE(result),
			TSIP_STACK_SET_NULL()) == 0);
	
}

bool SipStack::setMCPTTAffiliationGroupsDefualt(const char* mcptt_affiliation_groups_default){
	char caracte=';';
	tsip_uris_L_t* result=tsk_list_create();
	tsip_uri_t* uriTemp;
	char* pch;
	char* copyString=strdup(mcptt_affiliation_groups_default);
	pch = strtok (copyString,";");
	int con=0;
	for (con=0;pch != NULL;con++)
	{
		#if HAVE_CRT //Debug memory
		/*
		if(result==NULL){
			result=(tsip_uri_t**)malloc(sizeof(tsip_uri_t*)*(con+1));
		}else{
			result=(tsip_uri_t**)realloc(result,sizeof(tsip_uri_t*)*(con+1));
		}
		*/
		#else
		/*
		if(result==NULL){
			result=(tsip_uri_t**)tsk_malloc(sizeof(tsip_uri_t*)*(con+1));
		}else{
			result=(tsip_uri_t**)tsk_realloc(result,sizeof(tsip_uri_t*)*(con+1));
		}
		*/
		#endif //HAVE_CRT
		
		//printf ("%s\n",pch);
		#if HAVE_CRT //Debug memory
				//result[con]=(char*)malloc(sizeof(char)*(strlen(pch)));

		#else
				//result[con]=(char*)tsk_malloc(sizeof(char)*(strlen(pch)));

		#endif //HAVE_CRT
		//result[con]=strdup(pch);
		tsk_object_t* copy;
		if(!tsk_strnullORempty(pch) && (uriTemp = tsip_uri_parse(pch, tsk_strlen(pch)))){
			if(uriTemp->type == uri_unknown){ // scheme is missing or unsupported?
			tsk_strupdate(&uriTemp->scheme, "sip");
			uriTemp->type = uri_sip;
		  }
			//result[con]=uriTemp;
			copy = tsk_object_ref(uriTemp);
			tsk_list_push_back_data(result,(void**)&copy);
		}
		
		//strcpy(result[con], pch);
		pch = strtok(NULL, ";");
	}
	
	free(copyString);
	free(pch);

	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_AFFILIATION_GROUPS_DEFAULT(result),
			TSIP_STACK_SET_NULL()) == 0);
}
//MCPTT AUTHENTICATION
bool SipStack::setMCPTTPSIAuthentication(const char* mcptt_psi_authentication){
	return (tsip_stack_set(m_pHandle,
			TSIP_STACK_SET_MCPTT_PSI_AUTHENTICATION(mcptt_psi_authentication),
			TSIP_STACK_SET_NULL()) == 0);
}


static int stack_callback(const tsip_event_t *sipevent)
{
	int ret = 0;
	const SipStack* sipStack = tsk_null;
	SipEvent* e = tsk_null;

	if(!sipevent){ /* should never happen ...but who know? */
		TSK_DEBUG_WARN("Null SIP event.");
		return -1;
	}
	else {
		if(sipevent->type == tsip_event_stack && sipevent->userdata){
			/* sessionless event */
			sipStack = dyn_cast<const SipStack*>((const SipStack*)sipevent->userdata);
		}
		else {
			const void* userdata;
			/* gets the stack from the session */
			const tsip_stack_handle_t* stack_handle = tsip_ssession_get_stack(sipevent->ss);
			if(stack_handle && (userdata = tsip_stack_get_userdata(stack_handle))){
				sipStack = dyn_cast<const SipStack*>((const SipStack*)userdata);
			}
		}
	}

	if(!sipStack){
		TSK_DEBUG_WARN("Invalid SIP event (Stack is Null).");
		return -2;
	}

	sipStack->Lock();

	switch(sipevent->type){
		
		case tsip_event_register:
			{	/* REGISTER */
				if(sipStack->getCallback()){
					e = new RegistrationEvent(sipevent);
					sipStack->getCallback()->OnRegistrationEvent((const RegistrationEvent*)e);
				}
				break;
			}
		case tsip_event_register_authentication:
			{	/* REGISTER AUTHENTICATION*/
				if(sipStack->getCallback()){
					e = new RegistrationAuthenticationEvent(sipevent);
					sipStack->getCallback()->OnRegistrationAuthenticationEvent((const RegistrationAuthenticationEvent*)e);
				}
				break;
			}
		case tsip_event_invite:
			{	/* INVITE */
				if(sipStack->getCallback()){
					e = new InviteEvent(sipevent);
					sipStack->getCallback()->OnInviteEvent((const InviteEvent*)e);
				}
				break;
			}
		case tsip_event_message:
			{	/* MESSAGE */
				if(sipStack->getCallback()){
					e = new MessagingEvent(sipevent);
					sipStack->getCallback()->OnMessagingEvent((const MessagingEvent*)e);
				}
				break;
			}
		case tsip_event_message_location:
			{	/* MESSAGE LOCATION */
				//SEND EVENT TO USERS
				if(sipStack->getCallback()){
					e = new MessagingLocationEvent(sipevent);
					sipStack->getCallback()->OnMessagingLocationEvent((const MessagingLocationEvent*)e);
				}
				break;
			}
		case tsip_event_message_affiliation:
			{	/* MESSAGE AFFILIATION */
				//SEND EVENT TO USERS
				if(sipStack->getCallback()){
					e = new MessagingAffiliationEvent(sipevent);
					sipStack->getCallback()->OnMessagingAffiliationEvent((const MessagingAffiliationEvent*)e);
				}
				break;
			}
		case tsip_event_message_mbms:
			{	/* MESSAGE MBMS */
				//SEND EVENT TO USERS
				if(sipStack->getCallback()){
					e = new MessagingMbmsEvent(sipevent);
					sipStack->getCallback()->OnMessagingMbmsEvent((const MessagingMbmsEvent*)e);
				}
				break;
			}
		case tsip_event_info:
			{	/* INFO */
				if(sipStack->getCallback()){
					e = new InfoEvent(sipevent);
					sipStack->getCallback()->OnInfoEvent((const InfoEvent*)e);
				}
				break;
			}
		case tsip_event_options:
			{ /* OPTIONS */
				if(sipStack->getCallback()){
					e = new OptionsEvent(sipevent);
					sipStack->getCallback()->OnOptionsEvent((const OptionsEvent*)e);
				}
				break;
			}
		case tsip_event_publish:
			{ /* PUBLISH */
				if(sipStack->getCallback()){
					e = new PublicationEvent(sipevent);
					sipStack->getCallback()->OnPublicationEvent((const PublicationEvent*)e);
				}
				break;
			}
		case tsip_event_publish_affiliation:
			{ /* PUBLISH affiliation */
				if(sipStack->getCallback()){
					e = new PublicationAffiliationEvent(sipevent);
					sipStack->getCallback()->OnPublicationAffiliationEvent((const PublicationAffiliationEvent*)e);
				}
				break;
			}
		case tsip_event_subscribe:
			{	/* SUBSCRIBE */
				if(sipStack->getCallback()){
					e = new SubscriptionEvent(sipevent);
					sipStack->getCallback()->OnSubscriptionEvent((const SubscriptionEvent*)e);
				}
				break;
			}
		case tsip_event_subscribe_affiliation:
			{	/* SUBSCRIBE AFFILIATION */
				if(sipStack->getCallback()){
					e = new SubscriptionAffiliationEvent(sipevent);
					sipStack->getCallback()->OnSubscriptionAffiliationEvent((const SubscriptionAffiliationEvent*)e);
				}
				break;
			}
		case tsip_event_subscribe_cms:
		{	/* SUBSCRIBE CMS */
			if(sipStack->getCallback()){
				e = new SubscriptionCMSEvent(sipevent);
				sipStack->getCallback()->OnSubscriptionCMSEvent((const SubscriptionCMSEvent*)e);
			}
			break;
		}

		case tsip_event_subscribe_gms:
		{	/* SUBSCRIBE GMS */
			if(sipStack->getCallback()){
				e = new SubscriptionGMSEvent(sipevent);
				sipStack->getCallback()->OnSubscriptionGMSEvent((const SubscriptionGMSEvent*)e);
			}
			break;
		}

		case tsip_event_dialog:
			{	/* Common to all dialogs */
				if(sipStack->getCallback()){
					e = new DialogEvent(sipevent);
					sipStack->getCallback()->OnDialogEvent((const DialogEvent*)e);
				}
				break;
			}

		case tsip_event_stack:
			{	/* Stack event */
				if(sipStack->getCallback()){
					e = new StackEvent(sipevent);
					sipStack->getCallback()->OnStackEvent((const StackEvent*)e);
				}
				break;
			}

		default:
			{	/* Unsupported */
				TSK_DEBUG_WARN("%d not supported as SIP event.", sipevent->type);
				ret = -3;
				break;
			}
	}

	sipStack->UnLock();

	if(e){
		delete e;
	}

	return ret;
}

