#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
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

/*
*@file tmcptt_manager.c
* @brief MCPTT manager.
*
*/
#include "tinyrtp/trtp_manager.h"
#include "tinymcptt/tmcptt_manager.h"

#include "tinyrtp/rtp/trtp_rtp_packet.h"
#include "tinyrtp/rtcp/trtp_rtcp_packet.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_app.h"

#include "tinyrtp/rtcp/trtp_rtcp_session.h"


#include "tsk_string.h"
#include "tsk_memory.h"
#include "tsk_base64.h"
#include "tsk_md5.h"
#include "tsk_debug.h"
#include "tsk_timer.h"

#include <limits.h> /* INT_MAX */


#if !defined(TMCPTT_TRANSPORT_NAME)
#	define TMCPTT_TRANSPORT_NAME "MCPTT Manager"
#endif



static tmcptt_manager_t* _tmcptt_manager_create(const char* local_ip)
{
	tmcptt_manager_t* manager;

	if((manager = (tmcptt_manager_t *)tsk_object_new(tmcptt_manager_def_t))){
		manager->rtp_manager = trtp_manager_create(tsk_true, local_ip, tsk_false, tmedia_srtp_type_none, tmedia_srtp_mode_none);
		manager->rtp_manager->use_rtcpmux = tsk_true;
		manager->rtp_manager_mbms = trtp_manager_create(tsk_true, local_ip, tsk_false, tmedia_srtp_type_none, tmedia_srtp_mode_none);
		manager->rtp_manager_mbms->use_rtcpmux = tsk_true;
		manager->rtp_manager_mbms_floor = trtp_manager_create(tsk_true, local_ip, tsk_false, tmedia_srtp_type_none, tmedia_srtp_mode_none);
		manager->rtp_manager_mbms_floor->use_rtcpmux = tsk_true;
	}
	return manager;
}
/*
static int tmcptt_manager_rtp_cb(const void* callback_data, const trtp_rtp_packet_t* packet)
{
	int ret = 0;
	tmcptt_manager_t* manager = (tmcptt_manager_t*)callback_data;
	trtp_rtcp_packet_t* rtcp_packet = tsk_null;
	tsk_size_t size;
	char* data;

	size = trtp_rtp_packet_guess_serialbuff_size(packet);
	#if HAVE_CRT //Debug memory
	data = (char*)tsk_malloc(size*sizeof(char));
		
	#else
	data = (char*)malloc(size*sizeof(char));
		
	#endif //HAVE_CRT
	size = trtp_rtp_packet_serialize_to(packet, data, size);

	if(size <= 0){
		TSK_FREE(data);
		return -1;
	}

	rtcp_packet = (trtp_rtcp_packet_t*)trtp_rtcp_packet_deserialize(data, size);

	if(manager->mcptt_callback.fun)
	  ret = manager->mcptt_callback.fun(manager->mcptt_callback.usrdata, rtcp_packet);
	else{
		TSK_DEBUG_ERROR("Undefined callback function");
		ret = -1;
	}
	
	TSK_FREE(data);
	return ret;
}

*/
/** Create MCPTT manager */
tmcptt_manager_t* tmcptt_manager_create(const char* local_ip)
{
	tmcptt_manager_t* manager;
	if((manager = _tmcptt_manager_create(local_ip))){
	}
	return manager;
}

/** Prepare MCPTT manager */
int tmcptt_manager_prepare(tmcptt_manager_t* self)
{
	/*
	const char *rtcp_local_ip = tsk_null;
	tnet_port_t rtcp_local_port = 0;
	tnet_socket_type_t socket_type;
	uint8_t retry_count;
	*/
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	if(self->rtp_manager->transport){
		TSK_DEBUG_ERROR("MCPTT manager already prepared");
		return -2;
	}
	/*
	#define __retry_count_max 5
	#define __retry_count_max_minus1 (__retry_count_max - 1)
	
	retry_count = __retry_count_max;
	socket_type = tnet_socket_type_udp_ipv4;

	/* Creates local rtcp socket */
	/*
	while(retry_count--){
		/* random number in the range 1024 to 65535 */
	/*
		static int counter = 0;

		// first check => try to use port from latest active session if exist
		tnet_port_t local_port = (retry_count == __retry_count_max_minus1 && (self->rtp_manager->port_range.start <= self->rtp_manager->rtcp.public_port && self->rtp_manager->rtcp.public_port <= self->rtp_manager->port_range.stop))
			? self->rtp_manager->rtcp.public_port
			: (((rand() ^ ++counter) % (self->rtp_manager->port_range.stop - self->rtp_manager->port_range.start)) + self->rtp_manager->port_range.start);

		local_port = (local_port & 0xFFFE); /* turn to even number */

		/* because failure will cause errors in the log, print a message to alert that there is
		* nothing to worry about */
	/*
		TSK_DEBUG_INFO("MCPTT manager[Begin]: Trying to bind to random ports");

		/* MCPTT */
	/*
		if(!(self->rtp_manager->transport = tnet_transport_create(self->rtp_manager->local_ip, local_port, socket_type, TMCPTT_TRANSPORT_NAME))){
			TSK_DEBUG_ERROR("Failed to create MCPTT Transport");
			return -3;
		}

		TSK_DEBUG_INFO("MCPTT manager[End]: Trying to bind to random ports");
		break;
	}// end-of-while(retry_count)

	rtcp_local_ip = self->rtp_manager->transport->master->ip;
	rtcp_local_port = self->rtp_manager->transport->master->port;

	tsk_strupdate(&self->rtp_manager->rtcp.public_ip, rtcp_local_ip);
	self->rtp_manager->rtcp.public_port = rtcp_local_port;

	*/

	trtp_manager_prepare(self->rtp_manager);
	self->rtp_manager->transport->description = tsk_strdup("MCPTT Manager");
	self->public_port = self->rtp_manager->rtp.public_port;

	/*
	if(self->rtp_manager->transport){
		/* set callback function *//*
		tnet_transport_set_callback(self->rtp_manager->transport, _tmcptt_transport_layer_cb, self);
	}
*/
	return 0;
}
/** Prepares the MBMS manager */
int tmcptt_mbms_manager_prepare(tmcptt_manager_t* self)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	if(self->rtp_manager_mbms->transport){
		TSK_DEBUG_ERROR("MCPTT manager already prepared");
		return -2;
	}
	
	//MCPTT MBMS
	trtp_manager_prepare(self->rtp_manager_mbms);
	if(self->rtp_manager_mbms){
		self->rtp_manager_mbms->transport->description = tsk_strdup("MCPTT MBMS Manager");
		self->public_port_mbms_manager = self->rtp_manager_mbms->rtp.public_port;
		self->rtp_manager_mbms->use_rtcp = tsk_true; //trtp_manager_prepare sets these values to "false" if multicast
		self->rtp_manager_mbms->use_rtcpmux = tsk_true;
	}
	
	return 0;
}

int tmcptt_mbms_floor_manager_prepare(tmcptt_manager_t* self)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	if(self->rtp_manager_mbms_floor->transport){
		TSK_DEBUG_ERROR("MCPTT MBMS floor manager already prepared");
		return -2;
	}
	
	//MCPTT MBMS floor ctrl
	trtp_manager_prepare(self->rtp_manager_mbms_floor);
	if(self->rtp_manager_mbms_floor){
		self->rtp_manager_mbms_floor->transport->description = tsk_strdup("MCPTT MBMS Floor Manager");
		self->rtp_manager_mbms_floor->use_rtcp = tsk_true; //trtp_manager_prepare sets these values to "false" if multicast
		self->rtp_manager_mbms_floor->use_rtcpmux = tsk_true;
	}
	
	return 0;
}


/** Indicates whether the manager is already ready or not */
tsk_bool_t tmcptt_manager_is_ready(tmcptt_manager_t* self)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return tsk_false;
	}
	return (self->rtp_manager->transport != tsk_null);
}


/** Sets remote parameters for mcptt session */
int tmcptt_manager_set_mcptt_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	tsk_strupdate(&self->rtp_manager->rtp.remote_ip, remote_ip);
	self->rtp_manager->rtp.remote_port = remote_port;
	return 0;
}
/** Sets remote parameters for mbms session */
int tmcptt_manager_set_mcptt_mbms_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	tsk_strupdate(&self->rtp_manager_mbms->rtp.remote_ip, remote_ip);
	self->rtp_manager_mbms->rtp.remote_port = remote_port;
	return 0;
}

/** Sets remote parameters for MBMS floor control session */
int tmcptt_manager_set_mcptt_mbms_floor_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	tsk_strupdate(&self->rtp_manager_mbms_floor->rtp.remote_ip, remote_ip);
	self->rtp_manager_mbms_floor->rtp.remote_port = remote_port;
	return 0;
}

/** Sets MCPTT callback */
int tmcptt_manager_set_mcptt_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata)
{
	if(!self || !self->rtp_manager){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

//	self->mcptt_callback.fun = fun;
//	self->mcptt_callback.usrdata = usrdata;

//	return trtp_manager_set_rtp_callback(self->rtp_manager, tmcptt_manager_rtp_cb, self);
//	return trtp_manager_set_rtp_callback(self->rtp_manager, fun, usrdata);

	return trtp_manager_set_rtcp_callback(self->rtp_manager, fun, usrdata);

}
/** Sets MBMS callback */
int tmcptt_manager_set_mcptt_mbms_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata)
{
	if(!self || !self->rtp_manager_mbms){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

//	self->mcptt_callback.fun = fun;
//	self->mcptt_callback.usrdata = usrdata;

//	return trtp_manager_set_rtp_callback(self->rtp_manager, tmcptt_manager_rtp_cb, self);
//	return trtp_manager_set_rtp_callback(self->rtp_manager, fun, usrdata);

	return trtp_manager_set_rtcp_callback(self->rtp_manager_mbms, fun, usrdata);

}

int tmcptt_manager_set_mcptt_mbms_floor_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata)
{
	if(!self || !self->rtp_manager_mbms_floor){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

//	self->mcptt_callback.fun = fun;
//	self->mcptt_callback.usrdata = usrdata;

//	return trtp_manager_set_rtp_callback(self->rtp_manager, tmcptt_manager_rtp_cb, self);
//	return trtp_manager_set_rtp_callback(self->rtp_manager, fun, usrdata);

	return trtp_manager_set_rtcp_callback(self->rtp_manager_mbms_floor, fun, usrdata);
}
int tmcptt_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	self->rtp_manager->port_range.start = start;
	self->rtp_manager->port_range.stop = stop;
	return 0;
}
int tmcptt_mbms_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	self->rtp_manager_mbms->port_range.start = start;
	self->rtp_manager_mbms->port_range.stop = stop;
	return 0;
}

int tmcptt_mbms_floor_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop)
{
	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	self->rtp_manager_mbms_floor->port_range.start = start;
	self->rtp_manager_mbms_floor->port_range.stop = stop;
	return 0;
}
/** Starts the MCPTT manager */
int tmcptt_manager_start(tmcptt_manager_t* self)
{
	int ret = 0;
//	int rcv_buf = (int)tmedia_defaults_get_rtpbuff_size();
//	int snd_buf = (int)tmedia_defaults_get_rtpbuff_size();
//
//	if(!self){
//		TSK_DEBUG_ERROR("Invalid parameter");
//		return -1;
//	}
//
//	tsk_safeobj_lock(self);
//
//	if (self->rtp_manager->is_started) {
//		goto bail;
//	}
//
//	if(!self->rtp_manager->transport && (ret = tmcptt_manager_prepare(self))){
//		TSK_DEBUG_ERROR("Failed to prepare MCPTT manager");
//		goto bail;
//	}
//
//	if(!self->rtp_manager->transport || !self->rtp_manager->transport->master){
//		TSK_DEBUG_ERROR("MCPTT manager not prepared");
//		ret = -2;
//		goto bail;
//	}
//
//	/* Flush buffers and re-enable sockets */
//	if(self->rtp_manager->transport->master && self->rtp_manager->is_socket_disabled){
//		static char buff[1024];
//		tsk_size_t guard_count = 0;
//
//		TSK_DEBUG_INFO("Start flushing MCPTT socket...");
//		// Buffer should be empty ...but who knows?
//		// rcv() should never block() as we are always using non-blocking sockets
//		while ((ret = recv(self->rtp_manager->transport->master->fd, buff, sizeof(buff), 0)) > 0 && ++guard_count < 0xF0){
//			TSK_DEBUG_INFO("Flushing MCPTT Buffer %d", ret);
//		}
//		TSK_DEBUG_INFO("End flushing MCPTT socket");
//	}
//
//	/* enlarge socket buffer */
//	TSK_DEBUG_INFO("SO_RCVBUF = %d, SO_SNDBUF = %d", rcv_buf, snd_buf);
//	if((ret = setsockopt(self->rtp_manager->transport->master->fd, SOL_SOCKET, SO_RCVBUF, (char*)&rcv_buf, sizeof(rcv_buf)))){
//		TNET_PRINT_LAST_ERROR("setsockopt(SOL_SOCKET, SO_RCVBUF, %d) has failed with error code %d", rcv_buf, ret);
//	}
//	if((ret = setsockopt(self->rtp_manager->transport->master->fd, SOL_SOCKET, SO_SNDBUF, (char*)&snd_buf, sizeof(snd_buf)))){
//		TNET_PRINT_LAST_ERROR("setsockopt(SOL_SOCKET, SO_SNDBUF, %d) has failed with error code %d", snd_buf, ret);
//	}
//   
//
//	// check remote IP address validity
//	if((tsk_striequals(self->rtp_manager->rtcp.remote_ip, "0.0.0.0") || tsk_striequals(self->rtp_manager->rtcp.remote_ip, "::"))) { // most likely loopback testing
//		tnet_ip_t source = {0};
//		tsk_bool_t updated = tsk_false;
//		if(self->rtp_manager->transport && self->rtp_manager->transport->master){
//			updated = (tnet_getbestsource(self->rtp_manager->transport->master->ip, self->rtp_manager->transport->master->port, self->rtp_manager->transport->master->type, &source) == 0);
//		}
//		// Not allowed to send data to "0.0.0.0"
//		TSK_DEBUG_INFO("MCPTT remote IP contains not allowed value ...changing to '%s'", updated ? source : "oops");
//		if(updated){
//			tsk_strupdate(&self->rtp_manager->rtcp.remote_ip, source);
//		}
//	}
//	if((ret = tnet_sockaddr_init(self->rtp_manager->rtcp.remote_ip, self->rtp_manager->rtcp.remote_port, self->rtp_manager->transport->master->type, &self->rtp_manager->rtcp.remote_addr))){
//		tnet_transport_shutdown(self->rtp_manager->transport);
//		TSK_OBJECT_SAFE_FREE(self->rtp_manager->transport);
//		TSK_DEBUG_ERROR("Invalid MCPTT host:port [%s:%u]", self->rtp_manager->rtcp.remote_ip, self->rtp_manager->rtcp.remote_port);
//		goto bail;
//	}
//	TSK_DEBUG_INFO("rtcp.remote_ip=%s, rtcp.remote_port=%d, rtcp.local_fd=%d", self->rtp_manager->rtcp.remote_ip, self->rtp_manager->rtcp.remote_port, self->rtp_manager->transport->master->fd);
//
//	/* add RTCP socket to the transport */
//	if(self->rtp_manager->rtcp.local_socket){
//		TSK_DEBUG_INFO("rtcp.local_ip=%s, rtcp.local_port=%d, rtcp.local_fd=%d", self->rtp_manager->rtcp.local_socket->ip, self->rtp_manager->rtcp.local_socket->port, self->rtp_manager->rtcp.local_socket->fd);
//		if(ret == 0 && (ret = tnet_transport_add_socket(self->rtp_manager->transport, self->rtp_manager->rtcp.local_socket->fd, self->rtp_manager->rtcp.local_socket->type, tsk_false/* do not take ownership */, tsk_true/* only Meaningful for tls*/, tsk_null))){
//			TSK_DEBUG_ERROR("Failed to add RTCP socket");
//			/* do not exit */
//		}
//	}
//	
//	/* create and start RTCP session */
//	if(!self->rtp_manager->rtcp.session && ret == 0){
//		self->rtp_manager->rtcp.session = trtp_rtcp_session_create_2(self->rtp_manager->ice_ctx, self->rtp_manager->rtp.ssrc.local, self->rtp_manager->rtcp.cname);
//	}
//
//	if (ret = tnet_transport_start(self->rtp_manager->transport)) {
//		TSK_DEBUG_ERROR("Failed to start the RTP/RTCP transport");
//		goto bail;
//	}
//
//	self->rtp_manager->is_started = tsk_true;
//
//bail:
//
//	tsk_safeobj_unlock(self);
	self->rtp_manager->is_MCPTT_session=tsk_true;//MCPTT
	ret = trtp_manager_start(self->rtp_manager);
	trtp_manager_disable_automated_rtcp_reporting(self->rtp_manager);

	return ret;
}
/** Starts the MBMS manager */
int tmcptt_mbms_manager_start(tmcptt_manager_t* self)
{
	int ret = 0;

	self->rtp_manager_mbms->is_MCPTT_session=tsk_true;//MCPTT
	ret = trtp_manager_start(self->rtp_manager_mbms);
	trtp_manager_disable_automated_rtcp_reporting(self->rtp_manager_mbms);

	return ret;
}

/** Stops the MBMS manager */
int tmcptt_mbms_manager_stop(tmcptt_manager_t* self)
{
	int ret = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	TSK_DEBUG_INFO("trtp_manager_stop()");
	tsk_safeobj_lock(self);
	if(self->rtp_manager_mbms){
		trtp_manager_stop(self->rtp_manager_mbms);
	}
	tsk_safeobj_unlock(self);

	return ret;
}

/** Starts the MBMS floor manager */
int tmcptt_mbms_floor_manager_start(tmcptt_manager_t* self)
{
	int ret = 0;

	self->rtp_manager_mbms_floor->is_MCPTT_session=tsk_true;//MCPTT
	ret = trtp_manager_start(self->rtp_manager_mbms_floor);
	trtp_manager_disable_automated_rtcp_reporting(self->rtp_manager_mbms_floor);

	return ret;
}

/** Stops the MBMS manager */
int tmcptt_mbms_floor_manager_stop(tmcptt_manager_t* self)
{
	int ret = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	TSK_DEBUG_INFO("trtp_manager_stop()");
	tsk_safeobj_lock(self);
	if(self->rtp_manager_mbms_floor){
		trtp_manager_stop(self->rtp_manager_mbms_floor);
	}
	tsk_safeobj_unlock(self);

	return ret;
}
/** Stops the MCPTT manager */
int tmcptt_manager_stop(tmcptt_manager_t* self)
{
	int ret = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	TSK_DEBUG_INFO("trtp_manager_stop()");

	tsk_safeobj_lock(self);
/*
	// callbacks
	if (self->rtp_manager->transport) {
		ret = tnet_transport_set_callback(self->rtp_manager->transport, tsk_null, tsk_null);
	}

	// Free transport to force next call to start() to create new one with new sockets
	if(self->rtp_manager->transport){
		tnet_transport_shutdown(self->rtp_manager->transport);

		TSK_OBJECT_SAFE_FREE(self->rtp_manager->transport);
	}
	
	self->rtp_manager->is_started = tsk_false;
	*/

	trtp_manager_stop(self->rtp_manager);
	//Stop MBMS
	if(self->rtp_manager_mbms){
		trtp_manager_stop(self->rtp_manager_mbms);
	}
	if (self->rtp_manager_mbms_floor) {
		trtp_manager_stop(self->rtp_manager_mbms_floor);
	}
	tsk_safeobj_unlock(self);

	return ret;
}

int tmcptt_manager_send_mcptt_packet(tmcptt_manager_t* self, trtp_rtcp_report_app_t* packet)
{
	char* payload = tsk_null;
	tsk_size_t payload_size = 0;
	int ret = -1;

	payload_size = trtp_rtcp_report_app_get_size(packet);
	#if HAVE_CRT //Debug memory
	payload = (char*)malloc(payload_size*sizeof(char));
		
	#else
	payload = (char*)tsk_malloc(payload_size*sizeof(char));
		
	#endif //HAVE_CRT
	
	if(!payload)
		return -1;

	if(trtp_rtcp_report_app_serialize_to(packet, payload, payload_size) != 0)
	{
		TSK_FREE(payload);
		return -1;
	}

	ret = trtp_manager_send_rtp_raw(self->rtp_manager, payload, payload_size);

	TSK_FREE(payload);
	return ret;
}

//=================================================================================================
//	RTP manager object definition
//
static tsk_object_t* tmcptt_manager_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_manager_t *manager = (tmcptt_manager_t*)self;
	if(manager){
		/*		
		manager->port_range.start = tmedia_defaults_get_rtp_port_range_start();
		manager->port_range.stop = tmedia_defaults_get_rtp_port_range_stop();

		manager->ssrc.local = rand()^rand()^(int)tsk_time_epoch();

		/* timer */
		/*manager->timer_mgr_global = tsk_timer_mgr_global_ref();
		*/
		tsk_safeobj_init(manager);
	}
	return self;
}

static tsk_object_t* tmcptt_manager_dtor(tsk_object_t * self)
{ 
	tmcptt_manager_t *manager = (tmcptt_manager_t *)self;
	if(manager){
		/* callbacks */
		/*
		if (manager->transport) {

		}

		/* stop */
		/*
		if (manager->is_started) {
			tmcptt_manager_stop(manager);
		}

		TSK_OBJECT_SAFE_FREE(manager->transport);

		TSK_FREE(manager->local_ip);
		
		/* rtcp */
		/*
		TSK_OBJECT_SAFE_FREE(manager->rtcp.session);
		TSK_FREE(manager->rtcp.remote_ip);
		TSK_FREE(manager->rtcp.public_ip);
	
		TSK_OBJECT_SAFE_FREE(manager->rtcp.local_socket);

		/* Timer manager */
		/*	
		if(manager->timer_mgr_global){
			tsk_timer_mgr_global_unref(&manager->timer_mgr_global);
		}
		*/
		tsk_safeobj_deinit(manager);

		TSK_DEBUG_INFO("*** MCPTT manager destroyed ***");
	}

	return self;
}

static const tsk_object_def_t tmcptt_manager_def_s = 
{
	sizeof(tmcptt_manager_t),
	tmcptt_manager_ctor, 
	tmcptt_manager_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_manager_def_t = &tmcptt_manager_def_s;
