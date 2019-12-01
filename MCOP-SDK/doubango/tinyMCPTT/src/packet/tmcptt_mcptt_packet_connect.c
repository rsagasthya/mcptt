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

#include "tinymcptt/packet/tmcptt_mcptt_packet_preestablished.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_connect.h"

#include "tsk_memory.h"
#include "tsk_debug.h"

static tsk_object_t* tmcptt_mcptt_packet_connect_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_connect_t *connect_pkt = (tmcptt_mcptt_packet_connect_t *)self;
	if(connect_pkt){
		connect_pkt->mcptt_session_identity = tsk_null;
		connect_pkt->mcptt_group_identity = tsk_null;
		connect_pkt->media_streams = tsk_null;
		connect_pkt->warning_text = tsk_null;
		connect_pkt->answer_state = tsk_null;
		connect_pkt->mcptt_id_inviting_user = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_connect_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_connect_t *connect_pkt = (tmcptt_mcptt_packet_connect_t *)self;
	if(connect_pkt){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_connect_def_s = 
{
	sizeof(tmcptt_mcptt_packet_connect_t),
	tmcptt_mcptt_packet_connect_ctor, 
	tmcptt_mcptt_packet_connect_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_connect_def_t = &tmcptt_mcptt_packet_connect_def_s;

tmcptt_mcptt_packet_connect_t* tmcptt_mcptt_packet_connect_create_null()
{
	tmcptt_mcptt_packet_connect_t* connect_pkt;
	connect_pkt = (tmcptt_mcptt_packet_connect_t*)tsk_object_new(tmcptt_mcptt_packet_connect_def_t);
	return connect_pkt;
}

tmcptt_mcptt_packet_connect_t* tmcptt_mcptt_packet_connect_deserialize(const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_connect_t* connect_pkt = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if (pdata == tsk_null) {
		TSK_DEBUG_ERROR("Incorrect packet data");
		goto bail;
	}

	if(!(connect_pkt = tmcptt_mcptt_packet_connect_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	while (pdata != tsk_null && size > 0) { 
		switch (pdata[0]) {
		case FID_MCPTT_SESSION_ID:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_8_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->mcptt_session_identity = tmcptt_mcptt_packet_specific_binary_8_txt_deserialize(FID_MCPTT_SESSION_ID, pdata, size);
			if (connect_pkt->mcptt_session_identity == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_8_txt_get_size(connect_pkt->mcptt_session_identity);
			break;
		case FID_MCPTT_GROUP_ID:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->mcptt_group_identity = tmcptt_mcptt_packet_specific_txt_deserialize(FID_MCPTT_GROUP_ID, pdata, size);
			if (connect_pkt->mcptt_group_identity == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(connect_pkt->mcptt_group_identity);
			break;
		case FID_MEDIA_STREAMS:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->media_streams = tmcptt_mcptt_packet_specific_binary_deserialize(FID_MEDIA_STREAMS, pdata, size);
			if (connect_pkt->media_streams == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_get_size(connect_pkt->media_streams);
			break;
		case FID_WARNING_TEXT:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->warning_text = tmcptt_mcptt_packet_specific_txt_deserialize(FID_WARNING_TEXT, pdata, size);
			if (connect_pkt->warning_text == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(connect_pkt->warning_text);
			break;
		case FID_ANSWER_STATE:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->answer_state = tmcptt_mcptt_packet_specific_binary_16_deserialize(FID_ANSWER_STATE, pdata, size);
			if (connect_pkt->answer_state == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(connect_pkt->answer_state);
			break;
		case FID_INVITING_USER_ID:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			connect_pkt->mcptt_id_inviting_user = tmcptt_mcptt_packet_specific_txt_deserialize(FID_INVITING_USER_ID, pdata, size);
			if (connect_pkt->mcptt_id_inviting_user == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(connect_pkt->mcptt_id_inviting_user);
			break;
		default:
			TSK_DEBUG_ERROR("Field not supported");
			return tsk_null;
		}
		pdata += field_size;
		size -= field_size;
	}

bail:
	return connect_pkt;
}

int tmcptt_mcptt_packet_connect_serialize_to(const tmcptt_mcptt_packet_connect_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_connect_get_size(self)){
		return -1;
	}

	if (self->mcptt_session_identity) {
		tmcptt_mcptt_packet_specific_binary_8_txt_serialize_to(self->mcptt_session_identity, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_8_txt_get_size(self->mcptt_session_identity);
		pdata += field_size;
		size -= field_size;
	}

	if (self->mcptt_group_identity) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->mcptt_group_identity, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_group_identity);
		pdata += field_size;
		size -= field_size;
	}

	if (self->media_streams) {
		tmcptt_mcptt_packet_specific_binary_serialize_to(self->media_streams, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_get_size(self->media_streams);
		pdata += field_size;
		size -= field_size;
	}

	if (self->warning_text) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->warning_text, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->warning_text);
		pdata += field_size;
		size -= field_size;
	}

	if (self->answer_state) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->answer_state, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->answer_state);
		pdata += field_size;
		size -= field_size;
	}

	if (self->mcptt_id_inviting_user) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->mcptt_id_inviting_user, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_id_inviting_user);
		pdata += field_size;
		size -= field_size;
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_connect_get_size(const tmcptt_mcptt_packet_connect_t* self)
{
	tsk_size_t size = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	if (self->mcptt_session_identity) {
		size += tmcptt_mcptt_packet_specific_binary_8_txt_get_size(self->mcptt_session_identity);
	}

	if (self->mcptt_group_identity) {
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_group_identity);
	}

	if (self->media_streams) {
		size += tmcptt_mcptt_packet_specific_binary_get_size(self->media_streams);
	}

	if (self->warning_text) {
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->warning_text);
	}

	if (self->answer_state) {
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->answer_state);
	}

	if (self->mcptt_id_inviting_user) {
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_id_inviting_user);
	}

	return size;
}
