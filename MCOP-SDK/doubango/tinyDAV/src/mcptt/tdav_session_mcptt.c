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


/**@file tdav_session_mcptt.h
 * @brief The Media Burst Control Protocol (MCPTT) session.
 * Used for OMA PoC control plane
 */


#if !defined(HAVE_TINYMCPTT) || HAVE_TINYMCPTT

#include <math.h>
#include "tinydav/mcptt/tdav_session_mcptt.h"
#include "tinydav/audio/tdav_session_audio.h"
#include "tinydav/tdav_session_av.h"

#include "tinymcptt/tmcptt_manager.h"
#include "tinyrtp/rtp/trtp_rtp_packet.h"
#include "tinyrtp/trtp_manager.h"
#include "tinyrtp/rtcp/trtp_rtcp_packet.h"
#include "tinyrtp/rtcp/trtp_rtcp_header.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_app.h"
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
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms_map.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms_unmap.h"
#include "tinymcptt/tmcptt_mbms_event.h"
#include "tinymcptt/tmcptt_manager.h"
#include "tinymcptt/tmcptt_event.h"
#include "tinymcptt/tmcptt_timers.h"
#include "tinymcptt/tmcptt_counters.h"


#include "tsk_memory.h" /* TSK_FREE */
#include "tsk_timer.h"

#include "tinysdp/headers/tsdp_header.h"

/**
This function is in all services  tinyDAV
*/
int tdav_mcptt_event_proxy_cb(tmcptt_event_t* _event/*!Not the owner of the object*/)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;	

	if(!_event || !_event->callback_data){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	mcptt = tsk_object_ref((void*)_event->callback_data);
	if(TMEDIA_SESSION_MCPTT(mcptt)->callback.func){
		_event->callback_data = TMEDIA_SESSION_MCPTT(mcptt)->callback.data; // steal callback data
		ret = TMEDIA_SESSION_MCPTT(mcptt)->callback.func(_event); // call callback function()
	}
	tsk_object_unref(mcptt);

	return ret;
}
int tdav_mcptt_mbms_event_proxy_cb(tmcptt_mbms_event_t* _event/*!Not the owner of the object*/)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;	

	if(!_event || !_event->callback_data){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	mcptt = tsk_object_ref((void*)_event->callback_data);
	if(TMEDIA_SESSION_MCPTT(mcptt)->callback_mbms.func){
		_event->callback_data = TMEDIA_SESSION_MCPTT(mcptt)->callback_mbms.data; // steal callback data
		ret = TMEDIA_SESSION_MCPTT(mcptt)->callback_mbms.func(_event); // call callback function()
	}
	tsk_object_unref(mcptt);

	return ret;
}
static int tdav_session_mcptt_alert_user(tdav_session_mcptt_t* self, tmcptt_event_type_t type, tmcptt_message_t* message)
{
	int ret;
	tdav_session_mcptt_t *session = (tdav_session_mcptt_t*)tsk_object_ref((void*)self);
	tmcptt_event_t* _event = tmcptt_event_create(session, type, message);
	ret = tdav_mcptt_event_proxy_cb(_event);
	TSK_OBJECT_SAFE_FREE(_event); 
	tsk_object_unref(session); 
	return ret;
}
static int tdav_session_mcptt_mbms_alert_user(tdav_session_mcptt_t* self, tmcptt_mbms_event_type_t type, tmcptt_mbms_message_t* message)
{
	int ret;
	tdav_session_mcptt_t *session = (tdav_session_mcptt_t*)tsk_object_ref((void*)self);
	tmcptt_mbms_event_t* _event = tmcptt_mbms_event_create(session, type, message);
	ret = tdav_mcptt_mbms_event_proxy_cb(_event);
	TSK_OBJECT_SAFE_FREE(_event); 
	tsk_object_unref(session); 
	return ret;
}

static int tdav_session_mcptt_timer_t100_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//T100 (Floor release)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_t100.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_pending_release:
		{
			if (mcptt->counter_c100.curr_value < mcptt->counter_c100.max_value)
			{
				tdav_session_mcptt_send_release(TMEDIA_SESSION_MCPTT(mcptt));

				mcptt->timer_t100.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t100.timeout, tdav_session_mcptt_timer_t100_expired_handler, mcptt);
				mcptt->counter_c100.curr_value++;
			}
			else
			{
				TSK_DEBUG_INFO("Alert user IDLE 4");
				tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, tsk_null);
				mcptt->mcptt_status = mcptt_status_no_permission;
			}
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in t100 expired ");
		}
	}

	return 0;
}

static int tdav_session_mcptt_timer_t101_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//T101 (Floor request)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_t101.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_pending_request:
		{
			if (mcptt->counter_c101.curr_value < mcptt->counter_c101.max_value)
			{
				tdav_session_mcptt_send_request(TMEDIA_SESSION_MCPTT(mcptt));

				mcptt->timer_t101.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t101.timeout, tdav_session_mcptt_timer_t101_expired_handler, mcptt);
				mcptt->counter_c101.curr_value++;
			}
			else
			{
				TSK_DEBUG_INFO("Alert user DENIED 5");
				tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_denied, tsk_null);
				mcptt->mcptt_status = mcptt_status_no_permission;
			}
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in tinit expired");
		}
	}

	return 0;
}

static int tdav_session_mcptt_timer_tinit_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//Tinit (no standart for communications)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_tinit.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_pending_request:
	case mcptt_status_start_stop:
	case mcptt_status_no_permission:
	case mcptt_status_pending_release:
	case mcptt_status_releasing:
	case mcptt_status_queued:
		{
			if(mcptt->audio_session && TMEDIA_SESSION(mcptt->audio_session)->lo_held == tsk_false) //Stop RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
			TSK_DEBUG_ERROR("it stop transmission RTP on init");
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in t103 expired");
		}
	}

	return 0;
}

static int tdav_session_mcptt_timer_t103_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//T103 (End of RTP media)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_t103.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_no_permission:
		{
			TSK_DEBUG_INFO("Alert user IDLE 5");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, tsk_null);
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in t104 expired");
		}
	}

	return 0;
}

static int tdav_session_mcptt_timer_t104_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//T104 (Floor Queue Position Request)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_t104.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_queued:
		{
			if (mcptt->counter_c104.curr_value < mcptt->counter_c104.max_value)
			{
				tdav_session_mcptt_send_queue_position_request(TMEDIA_SESSION_MCPTT(mcptt));

				mcptt->timer_t104.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t104.timeout, tdav_session_mcptt_timer_t104_expired_handler, mcptt);
				mcptt->counter_c104.curr_value++;
			}
			else
			{
				TSK_DEBUG_INFO("Alert user QUEUED TIMEOUT 1");
				tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_queued_timeout, tsk_null);

				tdav_session_mcptt_send_release(TMEDIA_SESSION_MCPTT(mcptt));

				mcptt->mcptt_status = mcptt_status_pending_release;
			}
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in t104 expired");
		}
	}

	return 0;
}

static int tdav_session_mcptt_timer_t132_expired_handler(const void* arg, tsk_timer_id_t timer_id)
{
	//T132 (Queued granted user action)
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)arg;

	if (mcptt == tsk_null) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->timer_t132.id != timer_id) {
		TSK_DEBUG_ERROR("Incorrect timer identity");
		return -1;
	}

	switch (mcptt->mcptt_status)
	{
	case mcptt_status_queued:
		{
			tdav_session_mcptt_send_release(TMEDIA_SESSION_MCPTT(mcptt));

			mcptt->mcptt_status = mcptt_status_no_permission;
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in t104 expired");
		}
	}

	return 0;
}


/**
In this function is state machine of mcptt
*/
static int tdav_session_mcptt_cb(const void* callback_data, const trtp_rtcp_packet_t* packet, ...)
{
	
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)callback_data;
	const trtp_rtcp_report_app_t* rtcp_app = tsk_null;
	
	TSK_DEBUG_INFO("tdav_session_mcptt_cb");

	//#define TMCPTT_ALERT_USER(type) \
	//{ \
	//	tdav_session_mcptt_t *session = (tdav_session_mcptt_t*)tsk_object_ref((void*)mcptt); \
	//	tmcptt_event_t* _event = tmcptt_event_create(session, tsk_false, type); \
	//	tdav_mcptt_event_proxy_cb(_event); \
	//	TSK_OBJECT_SAFE_FREE(_event); \
	//	tsk_object_unref(session); \
	//}
	
	if (!packet || !callback_data) {
		TSK_DEBUG_ERROR("Invalid MCPTT packet");
		return -1;
	}
	
	if(packet->header->type != trtp_rtcp_packet_type_app){
		TSK_DEBUG_ERROR("Invalid RTCP packet.");
		return -1;
	}
	
	rtcp_app = (const trtp_rtcp_report_app_t*)packet;

	if(tsk_strnicmp(rtcp_app->name, MCPTT_PROTO_NAME, 4) != 0){
		TSK_DEBUG_ERROR("Incorrect application" );
		return -1;
	} 

	TSK_DEBUG_INFO("MCPTT message received from SSRC: %u", rtcp_app->ssrc);

	switch(rtcp_app->subtype)
	{
	  case MCPTT_TAKEN_ACK:
		  {
			tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_TAKEN_ACK);
		  }
	  case MCPTT_TAKEN:
		  {
			  tmcptt_mcptt_packet_taken_t* taken_msg = tmcptt_mcptt_packet_taken_create_null();
			  TSK_DEBUG_INFO("MCPTT TAKEN received");
			  taken_msg = tmcptt_mcptt_packet_taken_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(taken_msg)
			  {
				  if (tdav_session_mcptt_process_taken(mcptt, taken_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing TAKEN message");
					  return -1;
				  }
			  }
			  break;
		  }
	  case MCPTT_IDLE_ACK:
		  {
			tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_IDLE_ACK);
		  }
	  case MCPTT_IDLE:
		  {
			  tmcptt_mcptt_packet_idle_t* idle_msg = tmcptt_mcptt_packet_idle_create_null();
			  TSK_DEBUG_INFO("MCPTT IDLE received");
			  idle_msg = tmcptt_mcptt_packet_idle_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(idle_msg)
			  {
				  if (tdav_session_mcptt_process_idle(mcptt, idle_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing IDLE message");
					  return -1;
				  }
			  }
			  break;
		  }
	  case MCPTT_GRANTED_ACK:
		  {
			tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_GRANTED_ACK);
		  }
	  case MCPTT_GRANTED:
		  {
			  tmcptt_mcptt_packet_granted_t* granted_msg = tmcptt_mcptt_packet_granted_create_null();
			  TSK_DEBUG_INFO("MCPTT GRANTED received");
			  granted_msg = tmcptt_mcptt_packet_granted_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(granted_msg)
			  {
				  if (tdav_session_mcptt_process_granted(mcptt, granted_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing GRANTED message");
					  return -1;
				  }
			  }

			  break;
		  }
	  case MCPTT_DENY_ACK:
		  {
			tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_DENY_ACK);
		  }
	  case MCPTT_DENY:
		  {
			  tmcptt_mcptt_packet_deny_t* deny_msg = tmcptt_mcptt_packet_deny_create_null();
			  TSK_DEBUG_INFO("MCPTT DENY received");
			  deny_msg = tmcptt_mcptt_packet_deny_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(deny_msg)
			  {
				  if (tdav_session_mcptt_process_deny(mcptt, deny_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing DENY message");
					  return -1;
				  }
			  }
		  }
	  case MCPTT_QUEUE_POS_INFO_ACK:
		  {
			tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_QUEUE_POS_INFO_ACK);
		  }
	  case MCPTT_QUEUE_POS_INFO:
		  {
			  tmcptt_mcptt_packet_queue_position_info_t* queue_pos_info_msg = tmcptt_mcptt_packet_queue_position_info_create_null();
			  TSK_DEBUG_INFO("MCPTT QUEUE POSITION INFO received");
			  queue_pos_info_msg = tmcptt_mcptt_packet_queue_position_info_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(queue_pos_info_msg)
			  {
				  if (tdav_session_mcptt_process_queue_position_info(mcptt, queue_pos_info_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing QUEUE POSITION INFO message");
					  return -1;
				  }
			  }
		  }
	  case MCPTT_REVOKE:
		  {
			  tmcptt_mcptt_packet_revoke_t* revoke_msg = tmcptt_mcptt_packet_revoke_create_null();
			  TSK_DEBUG_INFO("MCPTT REVOKE received");
			  revoke_msg = tmcptt_mcptt_packet_revoke_deserialize(rtcp_app->payload, rtcp_app->payload_size);
			  if(revoke_msg)
			  {
				  if (tdav_session_mcptt_process_revoke(mcptt, revoke_msg) != 0) {
					  TSK_DEBUG_ERROR("Error processing REVOKE message");
					  return -1;
				  }
			  }
		  }
    }
	
	return 0;
}
//TODO: define logic for message RTCP MBMS
static int tdav_session_mcptt_mbms_cb(const void* callback_data, const trtp_rtcp_packet_t* packet, ...)
{
	
	tdav_session_mcptt_t* mcptt = (tdav_session_mcptt_t*)callback_data;
	const trtp_rtcp_report_app_t* rtcp_app = tsk_null;
	
	TSK_DEBUG_INFO("tdav_session_mcptt_mbms_cb");

	//#define TMCPTT_ALERT_USER(type) \
	//{ \
	//	tdav_session_mcptt_t *session = (tdav_session_mcptt_t*)tsk_object_ref((void*)mcptt); \
	//	tmcptt_event_t* _event = tmcptt_event_create(session, tsk_false, type); \
	//	tdav_mcptt_event_proxy_cb(_event); \
	//	TSK_OBJECT_SAFE_FREE(_event); \
	//	tsk_object_unref(session); \
	//}
	
	if (!packet || !callback_data) {
		TSK_DEBUG_ERROR("Invalid MCPTT packet");
		return -1;
	}
	
	if(packet->header->type != trtp_rtcp_packet_type_app){
		TSK_DEBUG_ERROR("Invalid RTCP packet [type=%u]", packet->header->type);
		return -1;
	}
	
	rtcp_app = (const trtp_rtcp_report_app_t*)packet;

	if(tsk_strnicmp(rtcp_app->name, MCPTT_PROTO_NAME, 4) == 0)
	{
		TSK_DEBUG_INFO("MCPTT message received from SSRC: %u", rtcp_app->ssrc);

		switch(rtcp_app->subtype)
		{
		  case MCPTT_TAKEN_ACK:
			  {
				tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_TAKEN_ACK);
			  }
		  case MCPTT_TAKEN:
			  {
				  tmcptt_mcptt_packet_taken_t* taken_msg = tmcptt_mcptt_packet_taken_create_null();
				  TSK_DEBUG_INFO("MCPTT TAKEN received");
				  taken_msg = tmcptt_mcptt_packet_taken_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(taken_msg)
				  {
					  if (tdav_session_mcptt_process_taken(mcptt, taken_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing TAKEN message");
						  return -1;
					  }
				  }
				  break;
			  }
		  case MCPTT_IDLE_ACK:
			  {
				tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_IDLE_ACK);
			  }
		  case MCPTT_IDLE:
			  {
				  tmcptt_mcptt_packet_idle_t* idle_msg = tmcptt_mcptt_packet_idle_create_null();
				  TSK_DEBUG_INFO("MCPTT IDLE received");
				  idle_msg = tmcptt_mcptt_packet_idle_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(idle_msg)
				  {
					  if (tdav_session_mcptt_process_idle(mcptt, idle_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing IDLE message");
						  return -1;
					  }
				  }
				  break;
			  }
		  case MCPTT_GRANTED_ACK:
			  {
				tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_GRANTED_ACK);
			  }
		  case MCPTT_GRANTED:
			  {
				  tmcptt_mcptt_packet_granted_t* granted_msg = tmcptt_mcptt_packet_granted_create_null();
				  TSK_DEBUG_INFO("MCPTT GRANTED received");
				  granted_msg = tmcptt_mcptt_packet_granted_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(granted_msg)
				  {
					  if (tdav_session_mcptt_process_granted(mcptt, granted_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing GRANTED message");
						  return -1;
					  }
				  }

				  break;
			  }
		  case MCPTT_DENY_ACK:
			  {
				tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_DENY_ACK);
			  }
		  case MCPTT_DENY:
			  {
				  tmcptt_mcptt_packet_deny_t* deny_msg = tmcptt_mcptt_packet_deny_create_null();
				  TSK_DEBUG_INFO("MCPTT DENY received");
				  deny_msg = tmcptt_mcptt_packet_deny_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(deny_msg)
				  {
					  if (tdav_session_mcptt_process_deny(mcptt, deny_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing DENY message");
						  return -1;
					  }
				  }
				  break;
			  }
		  case MCPTT_QUEUE_POS_INFO_ACK:
			  {
				tdav_session_mcptt_send_ack(TMEDIA_SESSION_MCPTT(mcptt), MCPTT_QUEUE_POS_INFO);
			  }
		  case MCPTT_QUEUE_POS_INFO:
			  {
				  tmcptt_mcptt_packet_queue_position_info_t* queue_pos_info_msg = tmcptt_mcptt_packet_queue_position_info_create_null();
				  TSK_DEBUG_INFO("MCPTT QUEUE POSITION INFO received");
				  queue_pos_info_msg = tmcptt_mcptt_packet_queue_position_info_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(queue_pos_info_msg)
				  {
					  if (tdav_session_mcptt_process_queue_position_info(mcptt, queue_pos_info_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing QUEUE POSITION INFO message");
						  return -1;
					  }
				  }
				  break;
			  }
		  case MCPTT_REVOKE:
			  {
				  tmcptt_mcptt_packet_revoke_t* revoke_msg = tmcptt_mcptt_packet_revoke_create_null();
				  TSK_DEBUG_INFO("MCPTT REVOKE received");
				  revoke_msg = tmcptt_mcptt_packet_revoke_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				  if(revoke_msg)
				  {
					  if (tdav_session_mcptt_process_revoke(mcptt, revoke_msg) != 0) {
						  TSK_DEBUG_ERROR("Error processing REVOKE message");
						  return -1;
					  }
				  }
				  break;
			  }
		}
	} else if (tsk_strnicmp(rtcp_app->name, MCPTT_MBMS_PROTO_NAME, 4) == 0 || tsk_strnicmp(rtcp_app->name, MCPTT_MBMS_PROTO_NAME_OLD, 4) == 0 ) {
		TSK_DEBUG_INFO("MCPTT MBMS message received from SSRC: %u", rtcp_app->ssrc);
		switch(rtcp_app->subtype)
		{
			case MAP_GROUP_TO_BEARER:
			{
				tmcptt_mcptt_packet_mbms_map_t* map_msg = tmcptt_mcptt_packet_mbms_map_create_null();
				TSK_DEBUG_INFO("MCPTT MBMS MAP GROUP TO BEARER received");
				map_msg = tmcptt_mcptt_packet_mbms_map_deserialize(rtcp_app->payload, rtcp_app->payload_size);

				if (map_msg)
				{
					if (tdav_session_mcptt_process_mbms_map(mcptt, map_msg) != 0) {
						TSK_DEBUG_ERROR("Error processing MAP GROUP TO BEARER message");
						return -1;
					}
				}
				break;
			}
			case UNMAP_GROUP_TO_BEARER:
			{
				tmcptt_mcptt_packet_mbms_unmap_t* unmap_msg = tmcptt_mcptt_packet_mbms_unmap_create_null();
				TSK_DEBUG_INFO("MCPTT MBMS UNMAP GROUP TO BEARER received");
				unmap_msg = tmcptt_mcptt_packet_mbms_unmap_deserialize(rtcp_app->payload, rtcp_app->payload_size);
				if (unmap_msg) {
					if (tdav_session_mcptt_process_mbms_unmap(mcptt, unmap_msg) != 0) {
						TSK_DEBUG_ERROR("Error processing UNMAP GROUP TO BEARER message");
						return -1;
					}
				}
				break;
			}
		}
	}
	
	return 0;
}
/* ============ Plugin interface ================= */

int tdav_session_mcptt_set(tmedia_session_t* self, const tmedia_param_t* param)
{
	int ret = 0;
	tdav_session_mcptt_t* mcptt;
	int64_t timeoutseg=-1;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	TSK_DEBUG_INFO("tdav_session_mcptt_set");

	mcptt = (tdav_session_mcptt_t*)self;

	if(param->value_type == tmedia_pvt_int32)
	{
		if(tsk_striequals(param->key, "local_ssrc")){
			mcptt->local_ssrc = TSK_TO_UINT32((uint8_t*)param->value);
		}else if (tsk_striequals(param->key, "priority")){
			mcptt->priority_local=TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "implicit")){
			mcptt->implicit_local=TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "granted")){
			mcptt->granted_local=TSK_TO_UINT32(((uint8_t*)param->value));
		}
		else if (tsk_striequals(param->key, "with_floor_control")){
			mcptt->with_floor_control=TSK_TO_UINT32(((uint8_t*)param->value));
		}
		else if (tsk_striequals(param->key, "type_session")){
			mcptt->type_session=TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "t100")){
			timeoutseg = TSK_TO_UINT32(((uint8_t*)param->value));
			if(timeoutseg>0 && timeoutseg)mcptt->timer_t100.timeout = timeoutseg*1000;
		}else if (tsk_striequals(param->key, "tinit")){
			mcptt->timer_tinit.timeout = TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "t101")){
			timeoutseg = TSK_TO_UINT32(((uint8_t*)param->value));
			if(timeoutseg>0)mcptt->timer_t101.timeout = timeoutseg*1000;
		}else if (tsk_striequals(param->key, "t103")){
			timeoutseg = TSK_TO_UINT32(((uint8_t*)param->value));
			if((timeoutseg*1000)>MCPTT_TIMER_T132_MAX_VALUE){
				mcptt->timer_t103.timeout = MCPTT_TIMER_T132_MAX_VALUE;
			}else if(timeoutseg>0){
				mcptt->timer_t103.timeout = timeoutseg*1000;
			}
		}else if (tsk_striequals(param->key, "t104")){
			timeoutseg = TSK_TO_UINT32(((uint8_t*)param->value));
			if(timeoutseg>0)mcptt->timer_t104.timeout = timeoutseg*1000;
		}else if (tsk_striequals(param->key, "t132")){
			timeoutseg = TSK_TO_UINT32(((uint8_t*)param->value));
			if(timeoutseg>0)mcptt->timer_t132.timeout = timeoutseg*1000;
		}else if (tsk_striequals(param->key, "c100")){
			mcptt->counter_c100.max_value = TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "c101")){
			mcptt->counter_c101.max_value = TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "c104")){
			mcptt->counter_c104.max_value = TSK_TO_UINT32(((uint8_t*)param->value));
		}else if (tsk_striequals(param->key, "queueing_enabled")){
			if(TSK_TO_UINT32(((uint8_t*)param->value)) > 0)
				mcptt->queueing_enabled = tsk_true;
			else
				mcptt->queueing_enabled = tsk_false;
		}
	}
	else if(param->value_type == tmedia_pvt_pobject) 
	{
		if (tsk_striequals(param->key, "audio_session")) {
			TSK_OBJECT_SAFE_FREE(mcptt->audio_session);
			mcptt->audio_session = (tmedia_session_audio_t*)tsk_object_ref(param->value);
		} 
		else if(tsk_striequals(param->key, "multicast_audio_session")) {
			TSK_OBJECT_SAFE_FREE(mcptt->multicast_audio_session);
			mcptt->multicast_audio_session = (tmedia_session_audio_t*)tsk_object_ref(param->value);
		}else if(tsk_striequals(param->key, "mcptt_id_local")) {
			TSK_OBJECT_SAFE_FREE(mcptt->mcptt_id_local);
			mcptt->mcptt_id_local = (tsip_uri_t*)tsk_object_ref(param->value);
		}else if(tsk_striequals(param->key, "mcptt_calling_user_id")) {
			TSK_OBJECT_SAFE_FREE(mcptt->mcptt_calling_user_id);
			mcptt->mcptt_calling_user_id = (tsip_uri_t*)tsk_object_ref(param->value);
		}else if(tsk_striequals(param->key, "mcptt_called_party_id")) {
			TSK_OBJECT_SAFE_FREE(mcptt->mcptt_called_party_id);
			mcptt->mcptt_called_party_id = (tsip_uri_t*)tsk_object_ref(param->value);
		}else if(tsk_striequals(param->key, "mcptt_calling_group_id")) {
			TSK_OBJECT_SAFE_FREE(mcptt->mcptt_calling_group_id);
			mcptt->mcptt_calling_group_id = (tsip_uri_t*)tsk_object_ref(param->value);
		}
	}
	else if(param->value_type == tmedia_pvt_pchar){
		if(tsk_striequals(param->key, "remote-ip")){
			// only if no ip associated to the "m=" line
			if(param->value && !mcptt->remote_ip){
				mcptt->remote_ip = tsk_strdup((const char*)param->value);
			}
		}
		else if(tsk_striequals(param->key, "local-ip")){
			tsk_strupdate(&mcptt->local_ip, (const char*)param->value);
		}
	}
	return ret;
}

int tdav_session_mcptt_get(tmedia_session_t* self, tmedia_param_t* param)
{
	return -1;
}
int tdav_session_mbms_prepare(tmedia_session_mcptt_t* self,const char* remote_ip,const int remote_port, const char* local_iface, const int local_iface_idx)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	TSK_DEBUG_INFO("tdav_session_mbms_prepare");

	mcptt = (tdav_session_mcptt_t*)self;
	
	mcptt->is_multimedia = tsk_true;
	mcptt->floorid = 0; //TO-DO
	mcptt->media_label = "AAA"; //TO-DO

    if(mcptt->mcptt_manager)
	{
		if((ret = tmcptt_mbms_manager_set_port_range(mcptt->mcptt_manager, remote_port,remote_port))){
			return ret;
		}

		if((ret = tmcptt_manager_set_mcptt_mbms_callback(mcptt->mcptt_manager, tdav_session_mcptt_mbms_cb, mcptt))){
			return ret;
		}
		mcptt->mcptt_manager->rtp_manager_mbms->is_multicast=tsk_true;
		mcptt->mcptt_manager->rtp_manager_mbms->multicast.multicast_port=remote_port;
		if (remote_ip)
			mcptt->mcptt_manager->rtp_manager_mbms->multicast.multicast_ip=tsk_strdup(remote_ip);
		if (local_iface)
			mcptt->mcptt_manager->rtp_manager_mbms->multicast.multicast_iface = tsk_strdup(local_iface);
		mcptt->mcptt_manager->rtp_manager_mbms->multicast.multicast_iface_idx = local_iface_idx;
		if((ret = tmcptt_mbms_manager_prepare(mcptt->mcptt_manager))){
			return ret;
		}
	}

	if(mcptt->local_port == 0)
		mcptt->local_port = mcptt->mcptt_manager->public_port;

	

	return ret;
}
int tdav_session_mcptt_prepare(tmedia_session_t* self)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	TSK_DEBUG_INFO("tdav_session_mcptt_prepare");

	mcptt = (tdav_session_mcptt_t*)self;
	
	mcptt->is_multimedia = tsk_true;
	mcptt->floorid = 0; //TO-DO
	mcptt->media_label = "AAA"; //TO-DO

    if(!mcptt->mcptt_manager)
	{
		mcptt->mcptt_manager = tmcptt_manager_create(mcptt->local_ip);
		if(mcptt->mcptt_manager)
		{
			if((ret = tmcptt_manager_set_port_range(mcptt->mcptt_manager, tmedia_defaults_get_rtp_port_range_start(), tmedia_defaults_get_rtp_port_range_stop()))){
				return ret;
			}

		    if((ret = tmcptt_manager_set_mcptt_callback(mcptt->mcptt_manager, tdav_session_mcptt_cb, mcptt))){
				return ret;
			}

			if((ret = tmcptt_manager_prepare(mcptt->mcptt_manager))){
				return ret;
			}
		}
	}

	if(mcptt->local_port == 0)
		mcptt->local_port = mcptt->mcptt_manager->public_port;

	
	
	return ret;
}



int tdav_session_mcptt_start(tmedia_session_t* self) {
	tdav_session_mcptt_t *mcptt;
	tmcptt_message_t *msg;

	int ret = 0;

	TSK_DEBUG_INFO("tdav_session_mcptt_start");

	if (!self) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	mcptt = (tdav_session_mcptt_t *) self;

	if ((ret = tmcptt_manager_set_mcptt_remote(mcptt->mcptt_manager, mcptt->remote_ip,
											   mcptt->remote_port))) {
		TSK_DEBUG_ERROR("Error setting remote MCPTT parameters");
		return -1;
	}

	//MCPTT
	if ((ret = tmcptt_manager_start(mcptt->mcptt_manager))) {
		TSK_DEBUG_ERROR("Error starting MCPTT manager");
		return -1;
	}


	//MCPTT
	if (mcptt) {
		//On Init session MCPTT the device send floor incator in all MCPTT floor control packets, 
		//But if the device receive MCPTT packet without floor indicator, the next MCPTT packet will be sent without floor indicator.
		mcptt->has_floor_incator = tsk_true;
	}

	//MCPTT
	if (mcptt) {
		//On Init session MCPTT the device send floor incator in all MCPTT floor control packets, 
		//But if the device receive MCPTT packet without floor indicator, the next MCPTT packet will be sent without floor indicator.		mcptt->has_floor_incator=tsk_true;
	}

	if ((ret = tsk_timer_manager_start(mcptt->h_timer)) != 0) {
		TSK_DEBUG_ERROR("Failed to start the timer");
		return ret;
	}
	if(mcptt->with_floor_control && mcptt->with_floor_control==tsk_false){
		mcptt->mcptt_status = mcptt_status_permission; //for fullduplex call
	}else
	if(mcptt->origin_competitor==tsk_true){
		if (mcptt->implicit_local && mcptt->implicit_remote)
		{
			if (mcptt->granted_local && mcptt->granted_remote) {
				msg = tmcptt_message_create_null();
				TSK_DEBUG_INFO("Alert user GRANTED 6");
				tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_granted, msg);
				mcptt->mcptt_status = mcptt_status_permission; //No need to send REQUEST nor GRANTED
			}
			else 
				mcptt->mcptt_status = mcptt_status_pending_request; //Continue waiting for GRANTED request
		} else { //Need to send REQUEST message
			//Now we don�t send REQUEST because if we haven�t sent the granted, now isn�t necessary. 
			/*
			tdav_session_mcptt_send_request(TMEDIA_SESSION_MCPTT(mcptt));

			
			
			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t101.id)) {
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t101.id);
				mcptt->timer_t101.id = TSK_INVALID_TIMER_ID;
			}	

			// Start timer T101
			mcptt->timer_t101.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t101.timeout, tdav_session_mcptt_timer_t101_expired_handler, mcptt);
			mcptt->counter_c101.curr_value = 1;

			mcptt->mcptt_status = mcptt_status_pending_request; //Wait for GRANTED
			*/
			//Because we haven�t sent Request, the user will receive a idle token.
			msg = tmcptt_message_create_null();
			TSK_DEBUG_INFO("Alert user IDLE 6");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, msg);
			mcptt->mcptt_status = mcptt_status_no_permission;
		}
	}else{
		msg = tmcptt_message_create_null();
		TSK_DEBUG_INFO("Alert user IDLE 7");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, msg);
		mcptt->mcptt_status = mcptt_status_no_permission;
	}
	

	
	if(mcptt->audio_session && mcptt->mcptt_status != mcptt_status_permission)
		TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
	else if(mcptt->audio_session && mcptt->mcptt_status == mcptt_status_permission)
		TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_false;
	
	
	
	if(mcptt->multicast_audio_session)
		TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
//	/* start the transport */
//	if((ret = tnet_transport_start(msrp->transport))){
//		goto bail;
//	}
//
//	switch(msrp->setup){
//		case msrp_setup_active:
//		case msrp_setup_actpass:
//			{
//				//
//				//	ACTIVE
//				//
//				TSK_DEBUG_INFO("connectto(%s:%d)", msrp->remote_ip, msrp->remote_port);
//				if((msrp->connectedFD = tnet_transport_connectto_2(msrp->transport, msrp->remote_ip, msrp->remote_port)) == TNET_INVALID_FD){
//					TSK_DEBUG_ERROR("Failed to connect to the remote party");
//					ret = -2;
//					goto bail;
//				}
//				else{
//					//TSK_DEBUG_INFO("Msrp connected FD=%d", msrp->connectedFD);
//					//if((ret = tnet_sockfd_waitUntilWritable(msrp->connectedFD, TDAV_MSRP_CONNECT_TIMEOUT)) && msrp->offerer){
//					//	TSK_DEBUG_ERROR("%d milliseconds elapsed and the socket is still not connected to (%s:%d).", TDAV_MSRP_CONNECT_TIMEOUT, msrp->remote_ip, msrp->remote_port);
//					//	goto bail;
//					//}
//					/*	draft-denis-simple-msrp-comedia-02 - 4.2.3. Setting up the connection
//						Once the TCP session is established, and if the answerer was the
//						active connection endpoint, it MUST send an MSRP request.  In
//						particular, if it has no pending data to send, it MUST send an empty
//						MSRP SEND request.  That is necessary for the other endpoint to
//						authenticate this TCP session.
//
//						...RFC 4975 - 7.1
//					*/
//					msrp->send_bodiless = tsk_true;
//				}
//				break;
//			}
//		default:
//			{
//				//
//				//	PASSIVE
//				//
//				break;
//			}
//	}
//	
//	// create and start the receiver
//	if(!msrp->receiver){
//		if((msrp->receiver = tmsrp_receiver_create(msrp->config, msrp->connectedFD))){
//			tnet_transport_set_callback(msrp->transport, TNET_TRANSPORT_CB_F(tdav_transport_layer_stream_cb), msrp);
//			if((ret = tmsrp_receiver_start(msrp->receiver, msrp, tdav_msrp_event_proxy_cb))){
//				TSK_DEBUG_ERROR("Failed to start the MSRP receiver");
//				goto bail;
//			}
//		}
//	}
//
//	// create and start the sender
//	if(!msrp->sender){
//		if((msrp->sender = tmsrp_sender_create(msrp->config, msrp->connectedFD))){
//			msrp->sender->chunck_duration = msrp->chunck_duration;
//			if((ret = tmsrp_sender_start(msrp->sender))){
//				TSK_DEBUG_ERROR("Failed to start the MSRP sender");
//				goto bail;
//			}
//		}
//	}
//
//bail:
	return ret;
}
int tdav_session_mcptt_mbms_start(tmedia_session_mcptt_t* self,const char* remote_ip,const int remote_port, const char* local_iface, const int local_iface_idx)
{
	tdav_session_mcptt_t* mcptt;

	int ret = 0;

	TSK_DEBUG_INFO("tdav_session_mcptt_mbms_start");

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	mcptt = (tdav_session_mcptt_t*)self;
	if(remote_ip && remote_port>0){
		mcptt->remote_ip_mbms=tsk_strdup(remote_ip);
		mcptt->remote_port_mbms=remote_port;
		mcptt->mbms_iface = tsk_strdup(local_iface);
		mcptt->mbms_iface_idx = local_iface_idx;
	}else{
		TSK_DEBUG_ERROR("Error setting remote MBMS parameters: Parameter isn�t valid.");
		return -1;
	}
	if((ret = tmcptt_manager_set_mcptt_mbms_remote(mcptt->mcptt_manager, mcptt->remote_ip_mbms, mcptt->remote_port_mbms))){
		TSK_DEBUG_ERROR("Error setting remote MBMS parameters");
		return -1;
	}
	if((ret = tmcptt_mbms_manager_start(mcptt->mcptt_manager))){
		TSK_DEBUG_ERROR("Error starting MBMS manager");
		return -1;
	}

	
	return ret;
}
//MBMS
int tdav_session_mcptt_mbms_stop(tmedia_session_mcptt_t* self)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	mcptt = (tdav_session_mcptt_t*)self;
	if((ret = tmcptt_mbms_manager_stop(mcptt->mcptt_manager))){
		TSK_DEBUG_ERROR("Error stopping MBMS manager");
		return -1;
	}

	return 0;
}

int tdav_session_mcptt_pause(tmedia_session_t* self)
{
	TSK_DEBUG_ERROR("Not Implemented");
	return -1;
}
//BYE
int tdav_session_mcptt_stop(tmedia_session_t* self)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	mcptt = (tdav_session_mcptt_t*)self;

	tsk_object_unref(mcptt->audio_session);

	tsk_object_unref(mcptt->multicast_audio_session);
//	
//	if(msrp->sender){
//		if((ret = tmsrp_sender_stop(msrp->sender))){
//			TSK_DEBUG_ERROR("Failed to stop the MSRP sender");
//		}
//	}
//	if(msrp->receiver){
//		if((ret = tmsrp_receiver_stop(msrp->receiver))){
//			TSK_DEBUG_ERROR("Failed to stop the MSRP receiver");
//		}
//	}
//
//	if(msrp->transport){
//		if((ret = tnet_transport_shutdown(msrp->transport))){
//			TSK_DEBUG_ERROR("Failed to stop the MSRP transport");
//		}
//	}
	
	if((ret = tmcptt_manager_stop(mcptt->mcptt_manager))){
		TSK_DEBUG_ERROR("Error stopping MCPTT manager");
		return -1;
	}

	return 0;
}

const tsdp_header_M_t* tdav_session_mcptt_get_lo(tmedia_session_t* self)
{
	tmedia_session_t* base = TMEDIA_SESSION(self);
	tdav_session_mcptt_t* mcptt;
	tsk_bool_t changed = tsk_false;

	const char* proto_transport = "udp";
	const char* proto = "MCPTT";// BFCP
	char* proto_attr;
	size_t proto_attr_len = 0;
	const char* multi = "multimedia";
	const char* mstrm = "mstrm";
	const char* priority = "mc_priority";
	const char* implicit = "mc_implicit_request";
	const char* granted = "mc_granted";
	char* fmtp_attr;
	char* fmtp_attr_int;
	size_t fmtp_attr_len = 0;
	size_t priority_len = 0;
	tsk_istr_t floorid_str;

	TSK_DEBUG_INFO("tdav_session_mcptt_get_lo");

	if(!self || !self->plugin){
		TSK_DEBUG_ERROR("Invalid parameter");
		return tsk_null;
	}
	
	mcptt = (tdav_session_mcptt_t*)self;
	
	if(!base->M.lo
	   && mcptt->with_floor_control && mcptt->with_floor_control==tsk_true
			)
	{
		//Create INVITE SRC and recive INVITE
		if((base->M.lo = tsdp_header_M_create(base->plugin->media, mcptt->local_port, proto_transport)))
		{
			
			
			fmtp_attr_int=(char*)tsk_calloc(fmtp_attr_len, 1);
			tsdp_header_M_add_headers(base->M.lo,
					TSDP_FMT_VA_ARGS(proto),
					tsk_null);
			proto_attr_len = tsk_strlen(proto) + 1;
				if(mcptt->is_multimedia)
					proto_attr_len += tsk_strlen(multi) + 2;
				#if HAVE_CRT //Debug memory
						proto_attr = (char*)calloc(proto_attr_len, sizeof(char));

	#else
				proto_attr = (char*)tsk_calloc(proto_attr_len, sizeof(char));
		
	#endif //HAVE_CRT
				if(mcptt->is_multimedia)
					tsk_sprintf(&proto_attr, "%s %s=1", proto, multi); 
				else
					tsk_sprintf(&proto_attr, "%s", proto);

				tsk_itoa(mcptt->floorid, &floorid_str);

			if(base->M.ro){
				//recibe INVITE

				mcptt->origin_competitor=tsk_false;

				fmtp_attr_len=tsk_strlen(proto)+tsk_strlen(priority)+3;
				#if HAVE_CRT //Debug memory
					fmtp_attr = (char*)calloc(fmtp_attr_len, sizeof(char));
	
	#else
						fmtp_attr = (char*)tsk_calloc(fmtp_attr_len, sizeof(char));

	#endif //HAVE_CRT
				if(mcptt->priority_remote>mcptt->priority_local && mcptt->priority_local>0 && mcptt->priority_local<10){
					tsk_sprintf(&fmtp_attr, "%s %s=%d", proto,priority,mcptt->priority_local);
				}else if(mcptt->priority_remote<=mcptt->priority_local && mcptt->priority_remote>0 && mcptt->priority_remote<10){
					tsk_sprintf(&fmtp_attr, "%s %s=%d", proto,priority,mcptt->priority_remote);
				}else {
					tsk_sprintf(&fmtp_attr, "%s %s=%d", proto,priority,0);
				}
				/*
				if(mcptt->granted_remote==tsk_true && mcptt->granted_local==tsk_true){
					fmtp_attr_len+=1+tsk_strlen(granted);
					#if HAVE_CRT //Debug memory
					fmtp_attr_int=(char*)calloc(fmtp_attr_len, sizeof(char));
		
	#else
					fmtp_attr_int=(char*)tsk_calloc(fmtp_attr_len, sizeof(char));
		
	#endif //HAVE_CRT
					tsk_sprintf(&fmtp_attr_int, "%s;%s",fmtp_attr, granted);
					fmtp_attr=tsk_realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
					strcpy(fmtp_attr, fmtp_attr_int);
				}

				
			
				if(mcptt->implicit_remote==tsk_true && mcptt->implicit_local==tsk_true){
					fmtp_attr_len+=1+tsk_strlen(implicit);
					#if HAVE_CRT //Debug memory
						fmtp_attr_int=(char*)calloc(fmtp_attr_len, sizeof(char));
	
	#else
						fmtp_attr_int=(char*)tsk_calloc(fmtp_attr_len, sizeof(char));
	
	#endif //HAVE_CRT
					tsk_sprintf(&fmtp_attr_int, "%s;%s", fmtp_attr, implicit);
					fmtp_attr=tsk_realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
					strcpy(fmtp_attr, fmtp_attr_int);
				}
				*/
				
				mcptt->granted_remote=tsk_false;
				mcptt->implicit_remote=tsk_false;
				
			}else{
				//Send invite
				mcptt->origin_competitor=tsk_true;
				start_time_t101(mcptt);//init t101

				if (mcptt->priority_local != 0) { //priority_local could be up to 255
					priority_len = (size_t)(floor(log10(abs(mcptt->priority_local))) + 1);
				}
				fmtp_attr_len=tsk_strlen(proto)+tsk_strlen(" ")+tsk_strlen(priority)
						+tsk_strlen("=")+priority_len+1;
				#if HAVE_CRT //Debug memory
						fmtp_attr = (char*)calloc(fmtp_attr_len, sizeof(char));
	            #else
						fmtp_attr = (char*)tsk_calloc(fmtp_attr_len, sizeof(char));
	            #endif //HAVE_CRT
				if(mcptt->priority_local>0 && mcptt->priority_local<10){
					tsk_sprintf(&fmtp_attr, "%s %s=%d", proto,priority,mcptt->priority_local);
				}else{
					tsk_sprintf(&fmtp_attr, "%s %s=%d", proto,priority,0);
				}
			
				if(mcptt->granted_local==tsk_true){
					fmtp_attr_len+=1+tsk_strlen(granted);
					#if HAVE_CRT //Debug memory
						fmtp_attr_int=(char*)calloc(fmtp_attr_len, sizeof(char));
	                #else
						fmtp_attr_int=(char*)tsk_calloc(fmtp_attr_len, sizeof(char));
	                #endif //HAVE_CRT
					tsk_sprintf(&fmtp_attr_int, "%s;%s",fmtp_attr, granted);

					#if HAVE_CRT //Debug memory
						fmtp_attr=realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
	                #else
		            	fmtp_attr=tsk_realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
	                #endif //HAVE_CRT
					strcpy(fmtp_attr, fmtp_attr_int);
				}
			
				if(mcptt->implicit_local==tsk_true){
					fmtp_attr_len+=1+tsk_strlen(implicit);
					#if HAVE_CRT //Debug memory
						fmtp_attr_int=(char*)calloc(fmtp_attr_len, sizeof(char));
	                #else
						fmtp_attr_int=(char*)tsk_calloc(fmtp_attr_len, sizeof(char));
	                #endif //HAVE_CRT
					tsk_sprintf(&fmtp_attr_int, "%s;%s", fmtp_attr, implicit);

					#if HAVE_CRT //Debug memory
						fmtp_attr=realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
	                #else
		            	fmtp_attr=tsk_realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
	                #endif //HAVE_CRT
					strcpy(fmtp_attr, fmtp_attr_int);
				}
			}
			//strcpy(array2, array1);
 
			//mc_priority=(0-7)
				tsdp_header_M_add_headers(base->M.lo,
				    TSDP_HEADER_A_VA_ARGS("fmtp",fmtp_attr),
					tsk_null);
					
			tsk_free((void**)&fmtp_attr_int);
			tsk_free((void**)&proto_attr);
			fmtp_attr=tsk_realloc(fmtp_attr, fmtp_attr_len*sizeof(char));
			tsk_free((void**)&fmtp_attr);
		}
	}else{//PROCESS INVITE DSC
		
	}

	

	return self->M.lo;
}

int tdav_session_mcptt_set_ro(tmedia_session_t* self, const tsdp_header_M_t* m)
{
	tdav_session_mcptt_t* mcptt;
	const tsdp_header_A_t* A_fmtp;

	//tsk_bool_t answer;
    if(!self || !m){
        TSK_DEBUG_ERROR("Invalid parameter");
        return -1;
    }

	TSK_DEBUG_INFO("tdav_session_mcptt_set_ro");


    self->M.ro = tsk_object_ref((void*)m);

    mcptt = (tdav_session_mcptt_t*)self;
    mcptt->remote_port = m->port;
    /* get connection associated to this media line
 If the connnection is global, then the manager will call tdav_session_mcptt_set_ro() */
    if (m->C && m->C!=tsk_null && m->C->addr && m->C->addr!=tsk_null){
        mcptt->remote_ip=tsk_strdup(m->C->addr);
    }


    if((A_fmtp = tsdp_header_M_findA(m, "fmtp"))){
        int index;
        char num_char;
        int num;
        const char* priority = "mc_priority";
        const char* implicit = "mc_implicit_request";
        const char* granted = "mc_granted";

        if((index=tsk_strindexOf(A_fmtp->value,tsk_strlen(A_fmtp->value),granted))>=0){
            mcptt->granted_remote=tsk_true;
        }

        if((index=tsk_strindexOf(A_fmtp->value,tsk_strlen(A_fmtp->value),implicit))>=0){
            mcptt->implicit_remote=tsk_true;
        }

        if((index=tsk_strindexOf(A_fmtp->value,tsk_strlen(A_fmtp->value),priority))>=0){
            num_char=(A_fmtp->value)[1+index+tsk_strlen(priority)];
            num=num_char-'0';
            if(num>=0 && num<10){
                mcptt->priority_remote=(uint32_t)num;
            }else{
                mcptt->priority_remote=(uint32_t)0;
            }
        }
    }


	

	return 0;
}



/* ============ Public functions ================= */
//MCPTT MBMS
// Start MBMS session manager
//TODO: generate logic to start mbms
int tdav_session_mcptt_mbms_start_manager(tmedia_session_mcptt_t* self,const char* remote_ip,const int remote_port, const char* local_iface, const int local_iface_idx, va_list *app)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	if(tdav_session_mbms_prepare(self,remote_ip,remote_port, local_iface, local_iface_idx) < 0){
		TSK_DEBUG_ERROR("Error reparing MBMS for MCPTT");
		return -1;
	}
	//Configure parameter
	if (tdav_session_mcptt_mbms_start(self,remote_ip,remote_port, local_iface, local_iface_idx) < 0) {
		TSK_DEBUG_ERROR("Error sending REQUEST");
		return -1;
	}

	return ret;
}
//MCPTT MBMS
// Stop MBMS manager session 
int tdav_session_mcptt_mbms_stop_manager(tmedia_session_mcptt_t* self, va_list *app)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;
	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	//Configure parameter
	if (tdav_session_mcptt_mbms_stop(self) < 0) {
		TSK_DEBUG_ERROR("Error sending REQUEST");
		return -1;
	}
	return ret;
}
int tdav_session_mcptt_mbms_start_media(tmedia_session_mcptt_t* self, const char* media_ip, const int media_port, const int media_ctrl_port, va_list *app)// tmedia_session_mgr_t* session_mgr, va_list *app)
{
	tsdp_header_M_t* sdp_ro = tsk_null;
	const tsdp_header_M_t* sdp_lo;
	struct addrinfo ip_addr;
	struct addrinfo* ip_addr_out;
	//const tmedia_codec_t* best_codec;
	tdav_session_mcptt_t* mcptt = tsk_null;
	int ret = 0;
	tsk_boolean_t updated = tsk_false;
	tmedia_session_t* base = tsk_null;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	
	//TODO: Use stored media IPs & ports or use the function params??
	if (mcptt->remote_ip_mbms_media == tsk_null) {
		TSK_DEBUG_ERROR("Incorrect MBMS media IP address");
		return -1;
	}

	if (mcptt->remote_port_mbms_audio == 0) {
		TSK_DEBUG_ERROR("Incorrect MBMS audio port");
		return -1;
	}

	if (mcptt->audio_session != tsk_null)
		TDAV_SESSION_AV(mcptt->multicast_audio_session)->local_ip = tsk_strdup(TDAV_SESSION_AV(mcptt->audio_session)->local_ip);


	base = TMEDIA_SESSION(mcptt->multicast_audio_session);

	if (base->plugin && (base->plugin->prepare(base) < 0)) {
		TSK_DEBUG_ERROR("Error preparing MBMS audio session");
		return -1;
	}
	
	//Set multicast interface (if exists)
	if (mcptt->mbms_iface)
		TDAV_SESSION_AV(mcptt->multicast_audio_session)->rtp_manager->multicast.multicast_iface = tsk_strdup(mcptt->mbms_iface);
	
	TDAV_SESSION_AV(mcptt->multicast_audio_session)->rtp_manager->multicast.multicast_iface_idx = mcptt->mbms_iface_idx;

	base->prepared = tsk_true;
	
	memset(&ip_addr, 0, sizeof(ip_addr));

	ip_addr.ai_family = PF_UNSPEC;
    ip_addr.ai_flags = AI_NUMERICHOST;

    ret = tnet_getaddrinfo(mcptt->remote_ip_mbms_media, NULL, &ip_addr, &ip_addr_out);
    if (ret) {
        TSK_DEBUG_ERROR("Invalid MBMS multicast address");
		return -1;
    }

    sdp_ro = tsdp_header_M_create("audio", mcptt->remote_port_mbms_audio, "RTP/AVP");
	if (ip_addr_out->ai_family == AF_INET) 
        sdp_ro->C = tsdp_header_c_create("IN", "IP4", mcptt->remote_ip_mbms_media);
    else if (ip_addr_out->ai_family == AF_INET6)
        sdp_ro->C = tsdp_header_c_create("IN", "IP6", mcptt->remote_ip_mbms_media);
		
	if (tdav_session_av_set_ro(TDAV_SESSION_AV(mcptt->multicast_audio_session), sdp_ro, &updated) < 0) {
		TSK_DEBUG_ERROR("Error setting MBMS audio session parameters");
		return -1;
	}

	if ((sdp_lo = tdav_session_av_get_lo(TDAV_SESSION_AV(mcptt->multicast_audio_session), &updated)) == tsk_null) {
		TSK_DEBUG_ERROR("Error obtaining local MBMS audio session parameters");
		return -1;
	}

	
	/*if(!(best_codec = tdav_session_av_get_best_neg_codec(TDAV_SESSION_AV(mcptt->audio_session)))){
		TSK_DEBUG_ERROR("Error selecting MBMS audio session codec");
		return -1;
	}
	
	if (tdav_session_av_start(TDAV_SESSION_AV(mcptt->multicast_audio_session), best_codec) < 0) {
		TSK_DEBUG_ERROR("Error starting MBMS audio session");
		return -1;
	}*/

	//TODO: Codecs from unicast audio session???
	base->neg_codecs = tsk_list_clone(TMEDIA_SESSION(mcptt->audio_session)->neg_codecs);

	if (base->plugin && (base->plugin->start(base) < 0)) {
		TSK_DEBUG_ERROR("Error starting MBMS audio session");
		return -1;
	}

	TDAV_SESSION_AUDIO(mcptt->multicast_audio_session)->is_started = tsk_true;
	
	if (ip_addr_out)
		tnet_freeaddrinfo(ip_addr_out);

	if (sdp_ro) {
		tsdp_header_M_remove(sdp_ro, tsdp_htype_C);
		TSK_OBJECT_SAFE_FREE(sdp_ro);
	}

	//Floor control session
	if (mcptt->remote_port_mbms_floor != 0) {
		if ((ret = tmcptt_mbms_floor_manager_set_port_range(mcptt->mcptt_manager, mcptt->remote_port_mbms_floor, mcptt->remote_port_mbms_floor))) {
			TSK_DEBUG_ERROR("Error setting MBMS floor control port range");
			return -1;
		}
			
		if ((ret = tmcptt_manager_set_mcptt_mbms_floor_callback(mcptt->mcptt_manager, tdav_session_mcptt_cb, mcptt))){
			TSK_DEBUG_ERROR("Error setting MBMS floor control callback");
			return -1;
		}

		mcptt->mcptt_manager->rtp_manager_mbms_floor->is_multicast=tsk_true;
		mcptt->mcptt_manager->rtp_manager_mbms_floor->multicast.multicast_port = mcptt->remote_port_mbms_floor;
		mcptt->mcptt_manager->rtp_manager_mbms_floor->multicast.multicast_ip = tsk_strdup(mcptt->remote_ip_mbms_media);
		mcptt->mcptt_manager->rtp_manager_mbms_floor->multicast.multicast_iface = tsk_strdup(mcptt->mbms_iface);
		mcptt->mcptt_manager->rtp_manager_mbms_floor->multicast.multicast_iface_idx = mcptt->mbms_iface_idx;
		if ((ret = tmcptt_mbms_floor_manager_prepare(mcptt->mcptt_manager))){
			TSK_DEBUG_ERROR("Error preparing MBMS floor control session");
			return -1;
		}

		if ((ret = tmcptt_manager_set_mcptt_mbms_floor_remote(mcptt->mcptt_manager, mcptt->remote_ip_mbms_media, mcptt->remote_port_mbms_floor))){
			TSK_DEBUG_ERROR("Error setting remote MBMS floor session parameters");
			return -1;
		}

		if ((ret = tmcptt_mbms_floor_manager_start(mcptt->mcptt_manager))) {
			TSK_DEBUG_ERROR("Error starting MBMS floor control session");
			return -1;
		}
	}

	return ret;
}
int tdav_session_mcptt_request_token (tmedia_session_mcptt_t* self, va_list *app)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	else if(mcptt->with_floor_control && mcptt->with_floor_control==tsk_false){
        TSK_DEBUG_ERROR("In this type of call, you can not release or request token");
        return -1;
    }

	if (mcptt->mcptt_status != mcptt_status_no_permission) {
		TSK_DEBUG_ERROR("Incorrect status");
		return -1;
	}

	if (tdav_session_mcptt_send_request(self) < 0) {
		TSK_DEBUG_ERROR("Error sending REQUEST");
		return -1;
	}
	
	/* Start timer T101 */
	mcptt->timer_t101.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t101.timeout, tdav_session_mcptt_timer_t101_expired_handler, mcptt);
	mcptt->counter_c101.curr_value = 1;

	mcptt->mcptt_status = mcptt_status_pending_request;
	TSK_DEBUG_INFO("Alert user REQUEST");
	tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_request_sent, tsk_null);

	return ret;
}



int tdav_session_mcptt_release_token (tmedia_session_mcptt_t* self, va_list *app)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	else if(mcptt->with_floor_control && mcptt->with_floor_control==tsk_false){
        TSK_DEBUG_ERROR("In this type of call, you can not release or request token");
        return -1;
    }

	if(mcptt->audio_session) //Stop RTP
		TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;

	if(mcptt->multicast_audio_session) //Multicast channel ON. In case someone transmits
		TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;

	if (tdav_session_mcptt_send_release(self) < 0) {
		TSK_DEBUG_ERROR("Error sending RELEASE");
		return -1;
	}

	mcptt->timer_t100.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t100.timeout, tdav_session_mcptt_timer_t100_expired_handler, mcptt);
	mcptt->counter_c100.curr_value = 1;

	if (mcptt->mcptt_status == mcptt_status_pending_request) {
		if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t101.id))
		{
			tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t101.id);
			mcptt->timer_t101.id = TSK_INVALID_TIMER_ID;
		}
	}

	if (mcptt->mcptt_status == mcptt_status_queued) {
		if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t104.id))
		{
			tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t104.id);
			mcptt->timer_t104.id = TSK_INVALID_TIMER_ID;		
		}
	}

	mcptt->mcptt_status = mcptt_status_pending_release;
	TSK_DEBUG_INFO("Alert user RELEASE");
	tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_release_sent, tsk_null);

	return ret;
}

int tdav_session_mcptt_request_queue_position (tmedia_session_mcptt_t* self, va_list *app)
{
	tdav_session_mcptt_t* mcptt;
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if(mcptt->audio_session) //Stop RTP
		TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;

	if(mcptt->multicast_audio_session) //Multicast channel ON. In case someone transmits
		TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;

	if (tdav_session_mcptt_send_queue_position_request(self) < 0) {
		TSK_DEBUG_ERROR("Error sending QUEUE POSITION REQUEST");
		return -1;
	}

	mcptt->timer_t104.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t100.timeout, tdav_session_mcptt_timer_t100_expired_handler, mcptt);
	mcptt->counter_c104.curr_value = 1;

	mcptt->mcptt_status = mcptt_status_queued;
	TSK_DEBUG_INFO("Alert user REQUEST 2");
	tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_queue_pos_request_sent, tsk_null);

	return ret;
}

int tdav_session_mcptt_send_request (tmedia_session_mcptt_t* self)
{
	tdav_session_mcptt_t* mcptt;
	tmcptt_mcptt_packet_request_t* request_pkt;
	tsk_size_t rtcp_payload_size = 0;
	char* rtcp_payload = tsk_null;
	trtp_rtcp_report_app_t* rtcp_pkt;
	int ret = 0;
	
	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	request_pkt = tmcptt_mcptt_packet_request_create_null();
	request_pkt->floor_priority = tmcptt_mcptt_packet_specific_binary_create_null();
	request_pkt->floor_priority->f_id = FID_FLOOR_PRIORITY;
	request_pkt->floor_priority->f_length = 2;
	request_pkt->floor_priority->f_h_value = (uint8_t)mcptt->priority_remote;
	request_pkt->floor_priority->f_l_value = 0;

	request_pkt->floor_indicator = tmcptt_mcptt_packet_specific_binary_16_create_null();
	request_pkt->floor_indicator->f_id = FID_FLOOR_INDICATOR;
	request_pkt->floor_indicator->f_length = 2;
	request_pkt->floor_indicator->f_value = tdav_session_mcptt_get_floor_indicator(self);

	rtcp_payload_size = tmcptt_mcptt_packet_request_get_size(request_pkt);
	#if HAVE_CRT //Debug memory
		rtcp_payload = (char*)malloc(rtcp_payload_size*sizeof(char));
	#else
		rtcp_payload = (char*)tsk_malloc(rtcp_payload_size*sizeof(char));
	#endif //HAVE_CRT
	
	
	if(!rtcp_payload)
		return -1;

	tmcptt_mcptt_packet_request_serialize_to(request_pkt, rtcp_payload, rtcp_payload_size);
		
	rtcp_pkt = trtp_rtcp_report_app_create_2(MCPTT_PROTO_NAME, MCPTT_REQUEST, mcptt->local_ssrc, rtcp_payload, rtcp_payload_size);
	ret = tmcptt_manager_send_mcptt_packet(mcptt->mcptt_manager, rtcp_pkt);

	TSK_FREE(rtcp_payload);
	
	return ret;
}

int tdav_session_mcptt_send_release (tmedia_session_mcptt_t* self)
{
	tdav_session_mcptt_t* mcptt;
	tmcptt_mcptt_packet_release_t* release_pkt;
	tsk_size_t rtcp_payload_size = 0;
	char* rtcp_payload = tsk_null;
	trtp_rtcp_report_app_t* rtcp_pkt;
	uint32_t ssrc = 0; 
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if(mcptt->audio_session) //Stop RTP
		TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;

	if(mcptt->multicast_audio_session) //Multicast channel ON. In case someone transmits
		TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;

	release_pkt = tmcptt_mcptt_packet_release_create_null();
	release_pkt->floor_indicator = tmcptt_mcptt_packet_specific_binary_16_create_null();
	release_pkt->floor_indicator->f_id = FID_FLOOR_INDICATOR;
	release_pkt->floor_indicator->f_length = 2;
	release_pkt->floor_indicator->f_value = tdav_session_mcptt_get_floor_indicator(self);
		
	rtcp_payload_size = tmcptt_mcptt_packet_release_get_size(release_pkt);
	#if HAVE_CRT //Debug memory
		rtcp_payload = (char*)malloc(rtcp_payload_size*sizeof(char));
	#else
		rtcp_payload = (char*)tsk_malloc(rtcp_payload_size*sizeof(char));
	#endif //HAVE_CRT
	
	
	if(!rtcp_payload)
		return -1;

	tmcptt_mcptt_packet_release_serialize_to(release_pkt, rtcp_payload, rtcp_payload_size);
		
	rtcp_pkt = trtp_rtcp_report_app_create_2(MCPTT_PROTO_NAME, MCPTT_RELEASE, mcptt->local_ssrc, rtcp_payload, rtcp_payload_size);
	ret = tmcptt_manager_send_mcptt_packet(mcptt->mcptt_manager, rtcp_pkt);

	TSK_FREE(rtcp_payload);

	return ret;
}

int tdav_session_mcptt_send_ack (tmedia_session_mcptt_t* self, tmcptt_mcptt_packet_type_t type)
{
	tdav_session_mcptt_t* mcptt;
	tmcptt_mcptt_packet_ack_t* ack_pkt;
	tsk_size_t rtcp_payload_size = 0;
	char* rtcp_payload = tsk_null;
	trtp_rtcp_report_app_t* rtcp_pkt;
	uint32_t ssrc = 0; 
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	ack_pkt = tmcptt_mcptt_packet_ack_create(FLR_SOURCE_PARTICIPANT, type);

	rtcp_payload_size = tmcptt_mcptt_packet_ack_get_size(ack_pkt);
	#if HAVE_CRT //Debug memory
		rtcp_payload = (char*)malloc(rtcp_payload_size*sizeof(char));
	#else
		rtcp_payload = (char*)tsk_malloc(rtcp_payload_size*sizeof(char));
	#endif //HAVE_CRT

	
	if(!rtcp_payload)
		return -1;

	tmcptt_mcptt_packet_ack_serialize_to(ack_pkt, rtcp_payload, rtcp_payload_size);
		
	rtcp_pkt = trtp_rtcp_report_app_create_2(MCPTT_PROTO_NAME, MCPTT_ACK, mcptt->local_ssrc, rtcp_payload, rtcp_payload_size);
	ret = tmcptt_manager_send_mcptt_packet(mcptt->mcptt_manager, rtcp_pkt);

	TSK_FREE(rtcp_payload);

	return ret;
}

int tdav_session_mcptt_send_queue_position_request (tmedia_session_mcptt_t* self)
{
	tdav_session_mcptt_t* mcptt;
	tmcptt_mcptt_packet_queue_position_request_t* queue_pos_req_pkt;
	tsk_size_t rtcp_payload_size = 0;
	char* rtcp_payload = tsk_null;
	trtp_rtcp_report_app_t* rtcp_pkt;
	uint32_t ssrc = 0; 
	int ret = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	if (mcptt->mcptt_status != mcptt_status_queued)
	{
		TSK_DEBUG_ERROR("Invalid MCPTT status");
		return -1;
	}

	queue_pos_req_pkt = tmcptt_mcptt_packet_queue_position_request_create_null();

	rtcp_payload_size = tmcptt_mcptt_packet_queue_position_request_get_size(queue_pos_req_pkt);
	#if HAVE_CRT //Debug memory
		rtcp_payload = (char*)malloc(rtcp_payload_size*sizeof(char));
	#else
		rtcp_payload = (char*)tsk_malloc(rtcp_payload_size*sizeof(char));
	#endif //HAVE_CRT
	
	
	if(!rtcp_payload)
		return -1;

	tmcptt_mcptt_packet_queue_position_request_serialize_to(queue_pos_req_pkt, rtcp_payload, rtcp_payload_size);
		
	rtcp_pkt = trtp_rtcp_report_app_create_2(MCPTT_PROTO_NAME, MCPTT_QUEUE_POS_REQ, mcptt->local_ssrc, rtcp_payload, rtcp_payload_size);
	ret = tmcptt_manager_send_mcptt_packet(mcptt->mcptt_manager, rtcp_pkt);

	TSK_FREE(rtcp_payload);

	return ret;
}


uint16_t tdav_session_mcptt_get_floor_indicator(tmedia_session_mcptt_t* self)
{
	tdav_session_mcptt_t* mcptt;
	uint16_t floor_indicator = 0;

	if(!(mcptt = (tdav_session_mcptt_t*)self)){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

   /* if (mcptt->session_type == mcptt_session_type_private ||
		mcptt->session_type == mcptt_session_type_group_prearranged ||
		mcptt->session_type == mcptt_session_type_group_chat) */
	if ((mcptt->type_session == tmedia_audio_ptt_mcptt ||
		mcptt->type_session == tmedia_audio_ptt_group_mcptt) && mcptt->has_floor_incator==tsk_true)
		//In this situation isn�t necesari if the foor. 
		floor_indicator = FLR_IND_NORMAL_CALL;

	if (mcptt->is_broadcast == tsk_true)
		floor_indicator = FLR_IND_BROADCAST_GRP_CALL;
	if (mcptt->is_emergency == tsk_true || ((mcptt->type_session | tmedia_emergency)==tmedia_emergency))
		floor_indicator |= FLR_IND_EMERGENCY_CALL;
	
	if (mcptt->is_imminent_peril == tsk_true || ((mcptt->type_session | tmedia_imminentperil)==tmedia_imminentperil))
		floor_indicator |= FLR_IND_IMMINENT_PERIL_CALL;
	if (mcptt->queueing_enabled == tsk_true)
		floor_indicator |= FLR_IND_QUEUEING_SUPPORTED;

	if (mcptt->is_dual_floor == tsk_true)
		floor_indicator |= FLR_IND_DUAL_FLOOR;
	
	return floor_indicator;
}

int tdav_session_mcptt_process_taken(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_taken_t* taken_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();

	TSK_DEBUG_INFO("MCPTT TAKEN TOKEN");

	if (mcptt == tsk_null)
		return -1;

	if (taken_msg == tsk_null) 
		return -1;

	


	if (taken_msg->granted_party_id)
	{
		TSK_DEBUG_INFO("MCPTT TAKEN client name: %.*s", taken_msg->granted_party_id->f_length, taken_msg->granted_party_id->f_value);
		#if HAVE_CRT //Debug memory
		msg->user = (char*)malloc((taken_msg->granted_party_id->f_length + 1) * sizeof(char));
		#else
		msg->user = (char*)tsk_malloc((taken_msg->granted_party_id->f_length + 1) * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(msg->user, taken_msg->granted_party_id->f_value, taken_msg->granted_party_id->f_length * sizeof(char));
		msg->user[taken_msg->granted_party_id->f_length] = '\0';
	}
	if (taken_msg->floor_indicator && (taken_msg->floor_indicator->f_value & FLR_IND_BROADCAST_GRP_CALL) == FLR_IND_BROADCAST_GRP_CALL){
		msg->is_broadcast_call = tsk_true;
	}

	if(taken_msg->floor_indicator){
		mcptt->has_floor_incator=tsk_true;
	}else{
		mcptt->has_floor_incator=tsk_false;
	}

	if (taken_msg->granted_party_id==tsk_null || mcptt->mcptt_id_local==tsk_null || (tsk_strcmp(taken_msg->granted_party_id->f_value,tsip_uri_tostring(mcptt->mcptt_id_local,tsk_false,tsk_false)) != 0) ) {
        if (msg->user && msg->user != tsk_null && mcptt && mcptt->mcptt_id_local) {
            if (strcmp(msg->user, tsip_uri_tostring(mcptt->mcptt_id_local, tsk_false, tsk_false)) ==
                0) {
                TSK_DEBUG_ERROR("A taken with local client data has been received %s = %s",
                                msg->user,
                                tsip_uri_tostring(mcptt->mcptt_id_local, tsk_false, tsk_false));
                return 0;
            } else {
                TSK_DEBUG_INFO("Received correct Taken");
            }
        }


        switch (mcptt->mcptt_status) {
            case mcptt_status_no_permission:
            case mcptt_status_start_stop:
            {
                //if (msg->is_broadcast_call)//it is not valid
                TSK_DEBUG_INFO("Alert user TAKEN 1");
                tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_taken, msg);

                /* Cannot start timer T103 (End of RTP media).
                   Cannot access RTP packet reception function */
                // mcptt->timer_t103.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t103.timeout, tdav_session_mcptt_timer_t103_expired_handler, mcptt);
                break;
            }
            case mcptt_status_pending_request: {
                if (tsk_strcmp(taken_msg->granted_party_id->f_value, mcptt->local_mcptt_id) != 0) {
                    TSK_DEBUG_INFO("Alert user TAKEN 2");
                    tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_taken, msg);
                    if (mcptt->audio_session) //Stop RTP transmission
                        TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
                    if (mcptt->multicast_audio_session) //Multicast channel ON
                    {
                        TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;
                        TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
                    }

                    if (mcptt->queueing_enabled == tsk_false) {
                        if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t101.id)) {
                            tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t101.id);
                            mcptt->timer_t101.id = TSK_INVALID_TIMER_ID;
                        }

                        /* Cannot start timer T103 (End of RTP media) */
                        //mcptt->timer_t103.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t103.timeout, tdav_session_mcptt_timer_t103_expired_handler, mcptt);

                        mcptt->mcptt_status = mcptt_status_no_permission;
                    }
                }

                break;
            }
            case mcptt_status_pending_release: {
                TSK_DEBUG_INFO("Alert user TAKEN 3");
                tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_taken, msg);

                //Start timer T103??

                if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t100.id)) {
                    tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t100.id);
                    mcptt->timer_t100.id = TSK_INVALID_TIMER_ID;
                }

                mcptt->mcptt_status = mcptt_status_no_permission;

                break;
            }
            case mcptt_status_queued: {
                TSK_DEBUG_INFO("Alert user TAKEN 4");
                tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_taken, msg);
                break;
            }
            default: {
                TSK_DEBUG_WARN("State illogical in process idle");
                TSK_DEBUG_INFO("The current status is %d", mcptt->mcptt_status);
            }
        }
    }


	return 0;
}


int tdav_session_mcptt_process_idle(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_idle_t* idle_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();

	TSK_DEBUG_INFO("MCPTT IDLE TOKEN");

	if (mcptt == tsk_null)
		return -1;

	if (idle_msg == tsk_null) 
		return -1;

	if(idle_msg->message_seq_num)
		TSK_DEBUG_INFO("MCPTT IDLE seq num: %u", idle_msg->message_seq_num->f_value);
	
	if (idle_msg->floor_indicator && (idle_msg->floor_indicator->f_value & FLR_IND_BROADCAST_GRP_CALL) == FLR_IND_BROADCAST_GRP_CALL){
		msg->is_broadcast_call = tsk_true;
	}

	if(idle_msg->floor_indicator){
		mcptt->has_floor_incator=tsk_true;
	}else{
		mcptt->has_floor_incator=tsk_false;
	}

	switch (mcptt->mcptt_status) 
	{

		/*
		This part of the code is reviewed because it came in conflict 
		when is received in the data desorde of idle and granted, 
		therefore always will await the REVOKE message to make case to the idle
		*/
	/*case mcptt_status_permission:
		{
			TSK_DEBUG_INFO("Alert user REVOKED 1");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_permission_revoked, msg);
			TSK_DEBUG_INFO("Alert user IDLE 1");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, msg);

			mcptt->mcptt_status = mcptt_status_no_permission;

			if(mcptt->audio_session) //Stop RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
			if(mcptt->multicast_audio_session) //Multicast channel ON
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
			}

			break;
		}*/
	case mcptt_status_no_permission:
		{
			TSK_DEBUG_INFO("Alert user IDLE 2");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, msg);

			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t103.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t103.id);
				mcptt->timer_t103.id = TSK_INVALID_TIMER_ID;
			}	  
			break;
		}
	case mcptt_status_pending_release:
		{
			TSK_DEBUG_INFO("Alert user IDLE 3");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_idle_channel, msg);

			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t100.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t100.id);
				mcptt->timer_t100.id = TSK_INVALID_TIMER_ID;
			}
			
			if (!msg->is_broadcast_call ||
				(idle_msg->floor_indicator && idle_msg->floor_indicator->f_value & FLR_IND_NORMAL_CALL) == FLR_IND_NORMAL_CALL)
				mcptt->mcptt_status = mcptt_status_no_permission;
			

			if (msg->is_broadcast_call)
				mcptt->mcptt_status = mcptt_status_releasing;

			break;
		}
	case mcptt_status_queued:
		{
			mcptt->mcptt_status = mcptt_status_no_permission;
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in process Granted");
		}
	}
	
	return 0;
}

int tdav_session_mcptt_process_granted(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_granted_t* granted_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();

	TSK_DEBUG_INFO("MCPTT GRANTED TOKEN");

	if (mcptt == tsk_null)
		return -1;

	if (granted_msg == tsk_null) 
		return -1;

	if (granted_msg->duration) {
		TSK_DEBUG_INFO("MCPTT GRANTED duration: %u", granted_msg->duration->f_value);
		msg->time = granted_msg->duration->f_value;
	}
	
	if (granted_msg->floor_priority)
		TSK_DEBUG_INFO("MCPTT GRANTED priority: %u", granted_msg->floor_priority->f_h_value);

	if (granted_msg->floor_indicator && (granted_msg->floor_indicator->f_value & FLR_IND_BROADCAST_GRP_CALL) == FLR_IND_BROADCAST_GRP_CALL){
		msg->is_broadcast_call = tsk_true;
	}

	if (granted_msg->floor_indicator && (granted_msg->floor_indicator->f_value & FLR_IND_DUAL_FLOOR) == FLR_IND_DUAL_FLOOR){
		mcptt->is_dual_floor = tsk_true;
		
	}

	if(granted_msg->floor_indicator){
		mcptt->has_floor_incator=tsk_true;
	}else{
		mcptt->has_floor_incator=tsk_false;
	}

	switch (mcptt->mcptt_status) 
	{
	case mcptt_status_pending_request:
		{
			TSK_DEBUG_INFO("Alert user GRANTED 1");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_granted, msg);

			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t103.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t103.id);
				mcptt->timer_t103.id = TSK_INVALID_TIMER_ID;
			}
			
			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t101.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t101.id);
				mcptt->timer_t101.id = TSK_INVALID_TIMER_ID;
			}

			mcptt->mcptt_status = mcptt_status_permission;

			if(mcptt->audio_session) //Start RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_false;
			if(mcptt->multicast_audio_session) //Multicast channel OFF. We should not hear ourselves
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_true;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true; //Just unicast uplink stream
			}

			break;
		}
	case mcptt_status_queued:
		{
			TSK_DEBUG_INFO("Alert user GRANTED 2");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_granted, msg);

			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t104.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t104.id);
				mcptt->timer_t104.id = TSK_INVALID_TIMER_ID;
			}
			
			mcptt->timer_t132.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t132.timeout, tdav_session_mcptt_timer_t132_expired_handler, mcptt);
			
			mcptt->mcptt_status = mcptt_status_permission;

			if(mcptt->audio_session) //Start RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_false;
			if(mcptt->multicast_audio_session) //Multicast channel OFF. We should not hear ourselves
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_true;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true; //Just unicast uplink stream
			}

			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in process DENY status");
		}
	}
	
	return 0;
}

int tdav_session_mcptt_process_deny(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_deny_t* deny_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();
	tsk_size_t size = 0;

	TSK_DEBUG_INFO("MCPTT DENY TOKEN");

	if (mcptt == tsk_null)
		return -1;

	if (deny_msg == tsk_null) 
		return -1;

	if (deny_msg->reject_cause)
		TSK_DEBUG_INFO("MCPTT DENY reject cause: %u (%s)", deny_msg->reject_cause->f_bin_value, deny_msg->reject_cause->f_txt_value);

	msg->reason_code = deny_msg->reject_cause->f_bin_value;
	size = deny_msg->reject_cause->f_length - sizeof(uint16_t);
	if (size > 0)
	{
		#if HAVE_CRT //Debug memory
		msg->reason_phrase = (char*)malloc((size + 1) * sizeof(char));
		#else
		msg->reason_phrase = (char*)tsk_malloc((size + 1) * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(msg->reason_phrase, deny_msg->reject_cause->f_txt_value, size);
		msg->reason_phrase[size] = '\0';
	}

	switch (mcptt->mcptt_status) 
	{
	case mcptt_status_pending_request:
		{
			TSK_DEBUG_INFO("Alert user DENIED 1");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_denied, msg);

			/* Stop timer T101 (Floor request) */
			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t101.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t101.id);
				mcptt->timer_t101.id = TSK_INVALID_TIMER_ID;
			}

			mcptt->mcptt_status = mcptt_status_no_permission;

			if(mcptt->audio_session) //Stop RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
			if(mcptt->multicast_audio_session) //Multicast channel ON
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
			}

			break;
		}
	case mcptt_status_queued:
		{
			TSK_DEBUG_INFO("Alert user DENIED 2");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_token_denied, msg);

			/* Stop timer T104 (Floor queue position request) */
			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t104.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t104.id);
				mcptt->timer_t104.id = TSK_INVALID_TIMER_ID;
			}
			
			mcptt->mcptt_status = mcptt_status_no_permission;

			if(mcptt->audio_session) //Stop RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
			if(mcptt->multicast_audio_session) //Multicast channel ON
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
			}

			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in process Revoke");
		}
	}
	
	return 0;
}


int tdav_session_mcptt_process_revoke(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_revoke_t* revoke_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();
	tsk_size_t size = 0;

	TSK_DEBUG_INFO("MCPTT REVOKE TOKEN");

	if (mcptt == tsk_null)
		return -1;

	if (revoke_msg == tsk_null) 
		return -1;

	if (revoke_msg->reject_cause)
		TSK_DEBUG_INFO("MCPTT REVOKE reject cause: %u (%s)", revoke_msg->reject_cause->f_bin_value, revoke_msg->reject_cause->f_txt_value);

	msg->reason_code = revoke_msg->reject_cause->f_bin_value;
	size = revoke_msg->reject_cause->f_length - sizeof(uint16_t);
	if (size > 0)
	{
		#if HAVE_CRT //Debug memory
		msg->reason_phrase = (char*)malloc((size + 1) * sizeof(char));
		#else
		msg->reason_phrase = (char*)tsk_malloc((size + 1) * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(msg->reason_phrase, revoke_msg->reject_cause->f_txt_value, size);
		msg->reason_phrase[size] = '\0';
	}

	switch (mcptt->mcptt_status) 
	{
	case mcptt_status_permission:
		{
			TSK_DEBUG_INFO("Alert user REVOKE 3");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_permission_revoked, msg);

			tdav_session_mcptt_send_release(TMEDIA_SESSION_MCPTT(mcptt));

			mcptt->timer_t100.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t100.timeout, tdav_session_mcptt_timer_t100_expired_handler, mcptt);
			mcptt->counter_c100.curr_value = 1;

			mcptt->mcptt_status = mcptt_status_pending_release;

			if(mcptt->audio_session) //Stop RTP transmission
				TMEDIA_SESSION(mcptt->audio_session)->lo_held = tsk_true;
			if(mcptt->multicast_audio_session) //Multicast channel ON
			{
				TMEDIA_SESSION(mcptt->multicast_audio_session)->ro_held = tsk_false;
				TMEDIA_SESSION(mcptt->multicast_audio_session)->lo_held = tsk_true;
			}

			break;
		}
	case mcptt_status_pending_release:
		{
			TSK_DEBUG_INFO("Alert user REVOKE 5");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_permission_revoked, msg);
			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in process QUEUE position info");
		}
	}
	
	return 0;
}

int tdav_session_mcptt_process_queue_position_info(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_queue_position_info_t* queue_position_info_msg)
{
	tmcptt_message_t* msg = tmcptt_message_create_null();
	tsk_size_t size = 0;

	if (mcptt == tsk_null)
		return -1;

	if (queue_position_info_msg == tsk_null) 
		return -1;

	if (queue_position_info_msg->queue_info)
		TSK_DEBUG_INFO("MCPTT QUEUE POSITION INFO : position: %u, priority: %u", queue_position_info_msg->queue_info->f_h_value, queue_position_info_msg->queue_info->f_l_value);

	msg->queue_position = queue_position_info_msg->queue_info->f_h_value;
	msg->queue_priority = queue_position_info_msg->queue_info->f_l_value;
	
	switch (mcptt->mcptt_status) 
	{
	case mcptt_status_pending_request:
		{
			TSK_DEBUG_INFO("Alert user QUEUED 1");
			tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_queued, msg);

			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t104.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t104.id);
				mcptt->timer_t104.id = TSK_INVALID_TIMER_ID;
			}

			mcptt->mcptt_status = mcptt_status_queued;

			break;
		}
	case mcptt_status_queued:
		{
			/* If queue position info == 65534, MCPTT client is not queued
			 * If queue position info == 65535, MCPTT server unable to determine position */
			if (!(msg->queue_position == 255 && msg->queue_priority == 254)) //If client is queued
				TSK_DEBUG_INFO("Alert user QUEUED 2");
				tdav_session_mcptt_alert_user(mcptt, tmcptt_event_type_queued, msg);
			
			if (TSK_TIMER_ID_IS_VALID(mcptt->timer_t104.id))
			{
				tsk_timer_manager_cancel(mcptt->h_timer, mcptt->timer_t104.id);
				mcptt->timer_t104.id = TSK_INVALID_TIMER_ID;
			}

			break;
		}
	default:
		{
			TSK_DEBUG_WARN("State illogical in process MBMS map");
		}
	}
	
	return 0;
}
int tdav_session_mcptt_process_mbms_map(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_mbms_map_t* map_msg)
{
	tmcptt_mbms_message_t* msg = tmcptt_mbms_message_create_null();
	uint8_t size = 0;

	if (mcptt == tsk_null)
		return -1;

	if (map_msg == tsk_null) 
		return -1;
	
	if (map_msg->mcptt_group_identity == tsk_null) 
		return -1;
	if (map_msg->mbms_subchannel == tsk_null) 
		return -1;
	if (map_msg->tmgi == tsk_null)
		return -1;
	
	if (mcptt->multicast_audio_session != tsk_null) //Multicast session already created. Ignore MAP message.
		return 0;

	if (map_msg->mcptt_group_identity) {
		TSK_DEBUG_INFO("MBMS MCPTT Group Identity: %.*s", map_msg->mcptt_group_identity->f_length, map_msg->mcptt_group_identity->f_value);
		#if HAVE_CRT //Debug memory
		msg->group_id = (char*)malloc((map_msg->mcptt_group_identity->f_length + 1) * sizeof(char));
		#else
		msg->group_id = (char*)tsk_malloc((map_msg->mcptt_group_identity->f_length + 1) * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(msg->group_id, map_msg->mcptt_group_identity->f_value, map_msg->mcptt_group_identity->f_length * sizeof(char));
		msg->group_id[map_msg->mcptt_group_identity->f_length] = '\0';
	}

	if (map_msg->tmgi) {
		size = map_msg->tmgi->f_length * 2 + 1;
		#if HAVE_CRT //Debug memory
		msg->tmgi = (char*)malloc(size * sizeof(char));
		#else
		msg->tmgi = (char*)tsk_malloc(size * sizeof(char));
		#endif //HAVE_CRT
		
		tsk_str_from_hex((const uint8_t*)map_msg->tmgi->f_value, map_msg->tmgi->f_length, msg->tmgi);
		msg->tmgi[size - 1] = '\0';
	}
	if (map_msg->mbms_subchannel) {
		
		mcptt->remote_port_mbms_audio = map_msg->mbms_subchannel->media_port;
		msg->media_port = map_msg->mbms_subchannel->media_port;
		if (map_msg->mbms_subchannel->floor_m_line != 0) {
			mcptt->remote_port_mbms_floor = map_msg->mbms_subchannel->floor_port;
			msg->media_control_port = map_msg->mbms_subchannel->floor_port;
		} else {
			mcptt->remote_port_mbms_floor = 0;
			msg->media_control_port = 0;
		}
		
		TSK_DEBUG_INFO("MBMS MCPTT audio port: %u", mcptt->remote_port_mbms_audio);
		TSK_DEBUG_INFO("MBMS MCPTT floor control port: %u", mcptt->remote_port_mbms_floor);

		if (map_msg->mbms_subchannel->ip_version == IPv4) {
			#if HAVE_CRT //Debug memory
		mcptt->remote_ip_mbms_media = (char*)malloc(INET_ADDRSTRLEN * sizeof(char));
			#else
		mcptt->remote_ip_mbms_media = (char*)tsk_malloc(INET_ADDRSTRLEN * sizeof(char));
			#endif //HAVE_CRT
			
			if (tnet_inet_ntop(AF_INET, &map_msg->mbms_subchannel->ipv4_address, mcptt->remote_ip_mbms_media, INET_ADDRSTRLEN) == tsk_null) {
				TSK_DEBUG_ERROR("Could not obtain media IP address. tnet_inet_ntop() failed");
				return -1;
			}

			TSK_DEBUG_INFO("MBMS MCPTT media multicast IP: %s", mcptt->remote_ip_mbms_media);
		} else if (map_msg->mbms_subchannel->ip_version == IPv6) {
			#if HAVE_CRT //Debug memory
		mcptt->remote_ip_mbms_media = (char*)malloc(INET6_ADDRSTRLEN * sizeof(char));
			#else
		mcptt->remote_ip_mbms_media = (char*)tsk_malloc(INET6_ADDRSTRLEN * sizeof(char));
			#endif //HAVE_CRT
			
			if (tnet_inet_ntop(AF_INET6, &map_msg->mbms_subchannel->ipv6_address, mcptt->remote_ip_mbms_media, INET6_ADDRSTRLEN) == tsk_null) {
				TSK_DEBUG_ERROR("Could not obtain media IP address. tnet_inet_ntop() failed");
				return -1;
			}
			TSK_DEBUG_INFO("MBMS MCPTT media multicast IP: %s", mcptt->remote_ip_mbms_media);
		}

		if (mcptt->remote_ip_mbms_media) {
			size = (uint8_t)(strlen(mcptt->remote_ip_mbms_media) + 1);
			#if HAVE_CRT //Debug memory
		msg->media_ip = (char*)malloc(size * sizeof(char));
			#else
		msg->media_ip = (char*)tsk_malloc(size * sizeof(char));
			#endif //HAVE_CRT
			
			strncpy(msg->media_ip, mcptt->remote_ip_mbms_media, size - 1);
			msg->media_ip[size - 1] = '\0';
		}
    }
	tdav_session_mcptt_mbms_alert_user(mcptt, tmcptt_mbms_event_type_map_group, msg);

	return 0;
}
int tdav_session_mcptt_process_mbms_unmap(tdav_session_mcptt_t* mcptt, tmcptt_mcptt_packet_mbms_unmap_t* unmap_msg)
{
	tmcptt_mbms_message_t* msg = tmcptt_mbms_message_create_null();
	uint8_t size = 0;

	if (mcptt == tsk_null)
		return -1;

	if (unmap_msg == tsk_null) 
		return -1;
	
	if (unmap_msg->mcptt_group_identity == tsk_null) 
		return -1;

	if (unmap_msg->mcptt_group_identity) {
		TSK_DEBUG_INFO("MBMS MCPTT Group Identity: %.*s", unmap_msg->mcptt_group_identity->f_length, unmap_msg->mcptt_group_identity->f_value);
		#if HAVE_CRT //Debug memory
		msg->group_id = (char*)malloc((unmap_msg->mcptt_group_identity->f_length + 1) * sizeof(char));
	#else
		msg->group_id = (char*)tsk_malloc((unmap_msg->mcptt_group_identity->f_length + 1) * sizeof(char));
	#endif //HAVE_CRT
		
		memcpy(msg->group_id, unmap_msg->mcptt_group_identity->f_value, unmap_msg->mcptt_group_identity->f_length * sizeof(char));
		msg->group_id[unmap_msg->mcptt_group_identity->f_length] = '\0';
	}

	
	tdav_session_mcptt_mbms_alert_user(mcptt, tmcptt_mbms_event_type_unmap_group, msg);

	return 0;
}
/*Constructor*/
static tsk_object_t* tdav_session_mcptt_ctor(tsk_object_t * self, va_list * app)
{
	tdav_session_mcptt_t *session = self;
	if(session){

		TMEDIA_SESSION_MCPTT(session)->request_token = tdav_session_mcptt_request_token;
		TMEDIA_SESSION_MCPTT(session)->release_token = tdav_session_mcptt_release_token;
		TMEDIA_SESSION_MCPTT(session)->start_mbms_manage = tdav_session_mcptt_mbms_start_manager;
		TMEDIA_SESSION_MCPTT(session)->stop_mbms_manage = tdav_session_mcptt_mbms_stop_manager;
		TMEDIA_SESSION_MCPTT(session)->start_mbms_media = tdav_session_mcptt_mbms_start_media;

		session->floorid = 0;
		session->is_multimedia = tsk_true;
		session->with_floor_control = tsk_true;
		session->local_ip = tsk_null;
		session->local_port = 0;
		session->remote_ip = tsk_null;
		session->remote_port = 0;
		session->local_ssrc = 0;
		session->mcptt_status = mcptt_status_start_stop;
		session->is_broadcast = tsk_false;
		session->is_emergency = tsk_false;
		session->is_imminent_peril = tsk_false;
		session->is_dual_floor = tsk_false;
		session->queueing_enabled = tsk_false;
		session->remote_ip_mbms = tsk_null;
		session->remote_port_mbms = 0;
		session->remote_port_mbms_audio = 0;
		session->remote_port_mbms_floor = 0;
		session->remote_ip_mbms_media = tsk_null;
		session->mbms_iface = tsk_null;
		session->mbms_iface_idx = -1;
		if(!(session->h_timer = tsk_timer_manager_create())){
			TSK_DEBUG_ERROR("Failed to create timer manager");
			return tsk_null;
		}
		session->timer_tinit.id = TSK_INVALID_TIMER_ID;
		session->timer_tinit.timeout = MCPTT_TIMER_TINIT_DEFAULT_VALUE;
		session->timer_t100.id = TSK_INVALID_TIMER_ID;
		session->timer_t100.timeout = MCPTT_TIMER_T100_DEFAULT_VALUE;
		session->timer_t101.id = TSK_INVALID_TIMER_ID;
		session->timer_t101.timeout = MCPTT_TIMER_T101_DEFAULT_VALUE;
		session->timer_t103.id = TSK_INVALID_TIMER_ID;
		session->timer_t103.timeout = MCPTT_TIMER_T103_DEFAULT_VALUE;
		session->timer_t104.id = TSK_INVALID_TIMER_ID;
		session->timer_t104.timeout = MCPTT_TIMER_T103_DEFAULT_VALUE;
		session->timer_t132.id = TSK_INVALID_TIMER_ID;
		session->timer_t132.timeout = MCPTT_TIMER_T132_DEFAULT_VALUE;
		session->counter_c100.curr_value = 1;
		session->counter_c100.max_value = MCPTT_COUNTER_C100_DEFAULT_VALUE;
		session->counter_c101.curr_value = 1;
		session->counter_c101.max_value = MCPTT_COUNTER_C101_DEFAULT_VALUE;
		session->counter_c104.curr_value = 1;
		session->counter_c104.max_value = MCPTT_COUNTER_C104_DEFAULT_VALUE;

	}
	return self;
}
/* destructor */
static tsk_object_t* tdav_session_mcptt_dtor(tsk_object_t * self)
{ 
	tdav_session_mcptt_t *session = self;
	if(session){
		if(session->remote_ip)
			TSK_FREE(session->remote_ip);
	
		/* deinit base */
		tmedia_session_deinit(self);
	}

	return self;
}



/* object definition */
static const tsk_object_def_t tdav_session_mcptt_def_s = 
{
	sizeof(tdav_session_mcptt_t),
	tdav_session_mcptt_ctor, 
	tdav_session_mcptt_dtor,
	tmedia_session_cmp, 
};
/* plugin definition*/
static const tmedia_session_plugin_def_t tdav_session_mcptt_plugin_def_s = 
{
	&tdav_session_mcptt_def_s,
	
	tmedia_mcptt,
	"application",
	
	tdav_session_mcptt_set,
	tdav_session_mcptt_get,
	tdav_session_mcptt_prepare,
	tdav_session_mcptt_start,
	tdav_session_mcptt_pause,
	tdav_session_mcptt_stop,

	/* Audio part */
	{ tsk_null },

	tdav_session_mcptt_get_lo,
	tdav_session_mcptt_set_ro
};
const tmedia_session_plugin_def_t *tdav_session_mcptt_plugin_def_t = &tdav_session_mcptt_plugin_def_s;

static void start_time_t101(tdav_session_mcptt_t *mcptt){
	if (mcptt->implicit_local) {
		mcptt->mcptt_status = mcptt_status_pending_request;

		/* Start timer T101 */
		mcptt->timer_t101.id = tsk_timer_manager_schedule(mcptt->h_timer, mcptt->timer_t101.timeout, tdav_session_mcptt_timer_t101_expired_handler, mcptt);
		mcptt->counter_c101.curr_value = 1;
	}
}



#endif /* !defined(HAVE_TINYMSRP) || HAVE_TINYMSRP */