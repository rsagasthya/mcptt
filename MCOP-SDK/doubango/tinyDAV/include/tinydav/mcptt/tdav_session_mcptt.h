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

/**@file tdav_session_mcptt.h
 * @brief The Mission Critical Push-To-Talk (MCPTT) session.
 * Used for MCPTT floor control plane
 */

#ifndef TINYDAV_SESSION_MCPTT_H
#define TINYDAV_SESSION_MCPTT_H

#include "tinydav_config.h"
#include "tinymcptt/tmcptt_manager.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_taken.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_idle.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_granted.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_request.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_release.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_ack.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_deny.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_revoke.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_queue_position_info.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_queue_position_request.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_preestablished.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_ack_preestablished.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_connect.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_disconnect.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms_map.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms_unmap.h"

#if !defined(HAVE_TINYMCPTT) || HAVE_TINYMCPTT

//#include "tnet_transport.h"

#include "tinysip/tsip_uri.h"
#include "tinymedia/tmedia_session.h"
#include "tsk_timer.h"

TDAV_BEGIN_DECLS

typedef enum 
{
  mcptt_session_type_none,
  mcptt_session_type_private,
  mcptt_session_type_group_prearranged,
  mcptt_session_type_group_chat
} tdav_session_mcptt_type_t;

typedef enum tdav_mcptt_status_e
{
	mcptt_status_start_stop = 1,
	mcptt_status_no_permission = 2,
	mcptt_status_pending_request = 3,
	mcptt_status_permission = 4,
	mcptt_status_pending_release = 5,
	mcptt_status_releasing = 6,
	mcptt_status_queued = 7
}
tdav_mcptt_status_t;

typedef struct tdav_session_mcptt_s
{
	TMEDIA_DECLARE_SESSION_MCPTT;

	uint32_t priority_local;
	tsk_bool_t implicit_local;
	tsk_bool_t granted_local;
	tsk_bool_t with_floor_control;
	tmedia_type_t type_session;
	tsip_uri_t* mcptt_id_local;
	tsip_uri_t* mcptt_request_uri;
	tsip_uri_t* mcptt_calling_user_id;
	tsip_uri_t* mcptt_called_party_id;
	tsip_uri_t* mcptt_calling_group_id;

	uint32_t priority_remote;
	tsk_bool_t implicit_remote;
	tsk_bool_t granted_remote;
	tsk_bool_t origin_competitor;//If origin_competitor is tsk_true, this UA is a origin in the session. But, it is tsk_false, the UA, It is the recipient of the message.  

	char* remote_ip;
	uint16_t remote_port;

	char* local_ip;
	uint16_t local_port;
	char* mbms_iface;
	int mbms_iface_idx;
	char* remote_ip_mbms;
	uint16_t remote_port_mbms;
	uint16_t local_port_mbms;
	char* remote_ip_mbms_media;
	uint16_t remote_port_mbms_audio;
	uint16_t remote_port_mbms_floor;

	tsk_bool_t is_multimedia;
	char* media_label;
	uint16_t floorid;

	uint32_t local_ssrc;

	char* local_mcptt_id;

	tsk_bool_t is_broadcast;
	tsk_bool_t is_emergency;
	tsk_bool_t is_dual_floor;
	tsk_bool_t queueing_enabled;
	tsk_bool_t is_imminent_peril;
	tdav_session_mcptt_type_t session_type;

	tdav_mcptt_status_t mcptt_status;

	tsk_timer_manager_handle_t *h_timer;

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_tinit; //Time no standart for NAT

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_t100; //Floor release

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_t101; //Floor request

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_t103; //End of RTP

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_t104; //Queue position request

	struct{
		tsk_timer_id_t id;
		int64_t timeout;
	}timer_t132; //Queued granted user action

	struct {
		uint8_t curr_value;
		uint8_t max_value;
	} counter_c100;

	struct {
		uint8_t curr_value;
		uint8_t max_value;
	} counter_c101;
	
	struct {
		uint8_t curr_value;
		uint8_t max_value;
	} counter_c104;

	struct tmcptt_manager_s* mcptt_manager;
	tmedia_session_audio_t* audio_session;
	tmedia_session_audio_t* multicast_audio_session;
	//MCPTT
	//This boolean indicates if the device sends floor indicator in MCPTT floor controler packet
	tsk_bool_t has_floor_incator;
}
tdav_session_mcptt_t;

TINYDAV_GEXTERN const tmedia_session_plugin_def_t *tdav_session_mcptt_plugin_def_t;

int tdav_session_mcptt_request_token (tmedia_session_mcptt_t* self, va_list *app);
int tdav_session_mcptt_release_token (tmedia_session_mcptt_t* self, va_list *app);
int tdav_session_mcptt_request_queue_position (tmedia_session_mcptt_t* self, va_list *app);
int tdav_session_mcptt_mbms_start_manager(tmedia_session_mcptt_t* self,const char* remote_ip,const int remote_port, const char* local_iface, const int local_iface_idx, va_list *app);
int tdav_session_mcptt_send_request (tmedia_session_mcptt_t* self);
int tdav_session_mcptt_send_release (tmedia_session_mcptt_t* self);
int tdav_session_mcptt_send_ack (tmedia_session_mcptt_t* self, tmcptt_mcptt_packet_type_t type);
int tdav_session_mcptt_send_queue_position_request (tmedia_session_mcptt_t* self);

uint16_t tdav_session_mcptt_get_floor_indicator(tmedia_session_mcptt_t* self);

int tdav_session_mcptt_process_taken(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_taken_t* taken_msg);
int tdav_session_mcptt_process_idle(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_idle_t* idle_msg);
int tdav_session_mcptt_process_granted(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_granted_t* granted_msg);
int tdav_session_mcptt_process_deny(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_deny_t* deny_msg);
int tdav_session_mcptt_process_revoke(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_revoke_t* revoke_msg);
int tdav_session_mcptt_process_queue_position_info(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_queue_position_info_t* queue_position_info_msg);
int tdav_session_mcptt_process_mbms_map(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_mbms_map_t* map_msg);
int tdav_session_mcptt_process_mbms_unmap(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_mbms_unmap_t* unmap_msg);
static void start_time_t101(tdav_session_mcptt_t *mcptt);

TDAV_END_DECLS

#endif /* !defined(HAVE_TINYMCPTT) || HAVE_TINYMCPTT */

#endif /* TINYDAV_SESSION_MCPTT_H */
