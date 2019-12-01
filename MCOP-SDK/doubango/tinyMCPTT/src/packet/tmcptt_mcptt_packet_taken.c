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

#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_taken.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"
#include "tinyrtp/rtcp/trtp_rtcp_sdes_item.h"

#include "tnet_endianness.h"

#include "tsk_memory.h"
#include "tsk_debug.h"
#include "tsk_string.h"

static tsk_object_t* tmcptt_mcptt_packet_taken_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_taken_t *taken_pkt = (tmcptt_mcptt_packet_taken_t *)self;
	if(taken_pkt){
		taken_pkt->granted_party_id = tsk_null;
		taken_pkt->permission = tsk_null;
		taken_pkt->user_id = tsk_null;
		taken_pkt->message_seq_num = tsk_null;
		taken_pkt->track_info = tsk_null;
		taken_pkt->floor_indicator = tsk_null;
		taken_pkt->ssrc_taken_participant = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_taken_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_taken_t *taken_pkt = (tmcptt_mcptt_packet_taken_t *)self;
	if(taken_pkt){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_taken_def_s = 
{
	sizeof(tmcptt_mcptt_packet_taken_t),
	tmcptt_mcptt_packet_taken_ctor, 
	tmcptt_mcptt_packet_taken_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_taken_def_t = &tmcptt_mcptt_packet_taken_def_s;

tmcptt_mcptt_packet_taken_t* tmcptt_mcptt_packet_taken_create_null()
{
	tmcptt_mcptt_packet_taken_t* taken_pkt;
	taken_pkt = (tmcptt_mcptt_packet_taken_t*)tsk_object_new(tmcptt_mcptt_packet_taken_def_t);
	return taken_pkt;
}

tmcptt_mcptt_packet_taken_t* tmcptt_mcptt_packet_taken_deserialize(const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_taken_t* taken_pkt = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if (pdata == tsk_null) {
		TSK_DEBUG_ERROR("Incorrect packet data");
		goto bail;
	}

	if(!(taken_pkt = tmcptt_mcptt_packet_taken_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	while (pdata != tsk_null && size > 0) { 
		switch (pdata[0]) {
		case FID_GRANTED_PARTY_ID_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_GRANTED_PARTY_ID:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect buffer size");
				return tsk_null;
			}
			taken_pkt->granted_party_id = tmcptt_mcptt_packet_specific_txt_deserialize(pdata[0], pdata, size);
			if (taken_pkt->granted_party_id == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(taken_pkt->granted_party_id);
			break;

        case FID_SSRC:
			//TODO New change, Now the SSRC use the code 014.
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_32_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect buffer size");
				return tsk_null;
			}
				taken_pkt->ssrc_taken_participant = tmcptt_mcptt_packet_specific_ssrc_deserialize(pdata[0], pdata, size);
				if (taken_pkt->ssrc_taken_participant == tsk_null) {
					TSK_DEBUG_ERROR("Error deserializing field");
					return tsk_null;
				}
				field_size = tmcptt_mcptt_packet_specific_binary_32_get_size(taken_pkt->permission);
				break;
		case FID_FLOOR_REQ_PERMISSION_OLD:
				TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_FLOOR_REQ_PERMISSION:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect buffer size");
				return tsk_null;
			}
			taken_pkt->permission = tmcptt_mcptt_packet_specific_binary_16_deserialize(pdata[0], pdata, size);
			if (taken_pkt->permission == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(taken_pkt->permission);
			break;
		case FID_USER_ID_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_USER_ID:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			taken_pkt->user_id = tmcptt_mcptt_packet_specific_txt_deserialize(pdata[0], pdata, size);
			if (taken_pkt->user_id == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(taken_pkt->user_id);
			break;
		case FID_MSG_SEQ_NUMBER_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_MSG_SEQ_NUMBER:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			taken_pkt->message_seq_num = tmcptt_mcptt_packet_specific_binary_16_deserialize(pdata[0], pdata, size);
			if (taken_pkt->message_seq_num == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(taken_pkt->message_seq_num);
			break;
		case FID_TRACK_INFO_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_TRACK_INFO:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_TXT_REF_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			taken_pkt->track_info = tmcptt_mcptt_packet_specific_binary_txt_ref_deserialize(pdata[0], pdata, size);
			if (taken_pkt->track_info == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(taken_pkt->track_info);
			break;
		case FID_FLOOR_INDICATOR_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_FLOOR_INDICATOR:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			taken_pkt->floor_indicator = tmcptt_mcptt_packet_specific_binary_16_deserialize(pdata[0], pdata, size);
			if (taken_pkt->floor_indicator == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(taken_pkt->floor_indicator);
			break;
		default:
			TSK_DEBUG_ERROR("Field not supported");
			return tsk_null;
		}
		pdata += field_size;
		size -= field_size;
	}

bail:
	return taken_pkt;
}

int tmcptt_mcptt_packet_taken_serialize_to(const tmcptt_mcptt_packet_taken_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	uint32_t ssrc_net = 0;
	tsk_size_t field_size = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_taken_get_size(self)){
		return -1;
	}

	if (self->granted_party_id) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->granted_party_id, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->granted_party_id);
		pdata += field_size;
		size -= field_size;
	}
	
	if (self->permission) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->permission, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->permission);
		pdata += field_size;
		size -= field_size;
	}
	
	if (self->user_id) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->user_id, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->user_id);
		pdata += field_size;
		size -= field_size;
	}

	if (self->message_seq_num) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->message_seq_num, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->message_seq_num);
		pdata += field_size;
		size -= field_size;
	}

	if (self->track_info) {
		tmcptt_mcptt_packet_specific_binary_txt_ref_serialize_to(self->track_info, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);
		pdata += field_size;
		size -= field_size;
	}

	if (self->floor_indicator) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->floor_indicator, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->floor_indicator);
		pdata += field_size;
		size -= field_size;
	}

	if (self->ssrc_taken_participant) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->ssrc_taken_participant, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->ssrc_taken_participant);
		pdata += field_size;
		size -= field_size;
	}



	return ret;
}

tsk_size_t tmcptt_mcptt_packet_taken_get_size(const tmcptt_mcptt_packet_taken_t* self)
{
	tsk_size_t size = 0;

	if (!self) {
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	if (self->granted_party_id)
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->granted_party_id);

	if (self->permission)
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->permission);

	if (self->user_id)
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->user_id);

	if (self->message_seq_num)
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->message_seq_num);
	
	if (self->track_info)
		size += tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);

	if (self->floor_indicator)
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->floor_indicator);

	if (self->ssrc_taken_participant)
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->ssrc_taken_participant);


	return size;
}
