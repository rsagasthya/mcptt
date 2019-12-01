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
 * @file tmcptt_manager.h
 * @brief MCPTT manager.
 */

#ifndef TINYMCPTT_MANAGER_H
#define TINYMCPTT_MANAGER_H

#include "tinymcptt_config.h"

#include "tinyrtp/rtcp/trtp_rtcp_session.h"
#include "tinyrtp/rtcp/trtp_rtcp_packet.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_app.h"

#include "tinymedia/tmedia_defaults.h"

#include "tinynet.h"

TMCPTT_BEGIN_DECLS

/** MCPTT manager */
typedef struct tmcptt_manager_s
{
	TSK_DECLARE_OBJECT;
	/*
	char* local_ip;
	tsk_bool_t is_started;
	tsk_bool_t is_socket_disabled;

	tnet_transport_t* transport;

	tsk_timer_manager_handle_t* timer_mgr_global;

	struct{
		uint32_t local;
		uint32_t remote;
	} ssrc;

	struct{
		uint16_t start;
		uint16_t stop;
	} port_range;

	struct{
		char* remote_ip;
		tnet_port_t remote_port;
		struct sockaddr_storage remote_addr;
		tnet_socket_t* local_socket;

		char* public_ip;
		tnet_port_t public_port;

		struct{
			const void* usrdata;
			trtp_rtcp_cb_f fun;
		} cb;

		struct trtp_rtcp_session_s* session;
	} rtcp;
	*/
	
	tnet_port_t public_port;

	/*
	struct{
			const void* usrdata;
			trtp_rtcp_cb_f fun;
	} mcptt_callback;
	*/
	struct trtp_manager_s* rtp_manager;
	tnet_port_t public_port_mbms_manager;
	struct trtp_manager_s* rtp_manager_mbms;
	struct trtp_manager_s* rtp_manager_mbms_floor;

	TSK_DECLARE_SAFEOBJ;

}
tmcptt_manager_t;

TINYMCPTT_API tmcptt_manager_t* tmcptt_manager_create(const char* local_ip);
TINYMCPTT_API int tmcptt_mbms_manager_prepare(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_mbms_floor_manager_prepare(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_manager_prepare(tmcptt_manager_t* self);
TINYMCPTT_API tsk_bool_t tmcptt_manager_is_ready(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_manager_set_mcptt_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata);
TINYMCPTT_API int tmcptt_manager_set_mcptt_mbms_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata);
TINYMCPTT_API int tmcptt_manager_set_mcptt_mbms_floor_callback(tmcptt_manager_t* self, trtp_rtcp_cb_f fun, const void* usrdata);
TINYMCPTT_API int tmcptt_manager_set_mcptt_mbms_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port);
TINYMCPTT_API int tmcptt_manager_set_mcptt_mbms_floor_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port);
TINYMCPTT_API int tmcptt_manager_set_mcptt_remote(tmcptt_manager_t* self, const char* remote_ip, tnet_port_t remote_port);
TINYMCPTT_API int tmcptt_mbms_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop);
TINYMCPTT_API int tmcptt_mbms_floor_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop);
TINYMCPTT_API int tmcptt_manager_set_port_range(tmcptt_manager_t* self, uint16_t start, uint16_t stop);
TINYMCPTT_API int tmcptt_mbms_manager_start(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_mbms_floor_manager_start(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_manager_start(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_mbms_manager_stop(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_mbms_floor_manager_stop(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_manager_stop(tmcptt_manager_t* self);
TINYMCPTT_API int tmcptt_manager_send_mcptt_packet(tmcptt_manager_t* self, trtp_rtcp_report_app_t* packet);

TINYMCPTT_GEXTERN const tsk_object_def_t *tmcptt_manager_def_t;

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MANAGER_H */
