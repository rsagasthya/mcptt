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
#include "tinymcptt/packet/tmcptt_mcptt_packet_ack.h"

#include "tsk_memory.h"
#include "tsk_debug.h"

#include "tnet_endianness.h"

static tsk_object_t* tmcptt_mcptt_packet_ack_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_ack_t *ack_pkt = (tmcptt_mcptt_packet_ack_t *)self;
	if(ack_pkt){
		ack_pkt->source = tsk_null;
		ack_pkt->message_type = tsk_null;
		ack_pkt->track_info = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_ack_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_ack_t *ack_pkt = (tmcptt_mcptt_packet_ack_t *)self;
	if(ack_pkt){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_ack_def_s = 
{
	sizeof(tmcptt_mcptt_packet_ack_t),
	tmcptt_mcptt_packet_ack_ctor, 
	tmcptt_mcptt_packet_ack_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_ack_def_t = &tmcptt_mcptt_packet_ack_def_s;

tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_create_null()
{
	tmcptt_mcptt_packet_ack_t* ack_pkt;
	ack_pkt = (tmcptt_mcptt_packet_ack_t*)tsk_object_new(tmcptt_mcptt_packet_ack_def_t);
	return ack_pkt;
}

tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_create(uint16_t source, uint8_t message_type)
{
	tmcptt_mcptt_packet_ack_t* ack_pkt = tsk_null;
	if (source != FLR_SOURCE_PARTICIPANT &&
		source != FLR_SOURCE_PARTICIPATING &&
		source != FLR_SOURCE_CONTROLLING &&
		source != FLR_SOURCE_NON_CONTROLLING)
	{
		TSK_DEBUG_INFO("Invalid source parameter");
		return tsk_null;
	}
	ack_pkt = (tmcptt_mcptt_packet_ack_t*)tsk_object_new(tmcptt_mcptt_packet_ack_def_t);
	if(ack_pkt)
	{
		ack_pkt->source = tmcptt_mcptt_packet_specific_binary_16_create_null();
		ack_pkt->source->f_id = FID_SOURCE;
	    ack_pkt->source->f_length = 2;
	    ack_pkt->source->f_value = source;

		ack_pkt->message_type = tmcptt_mcptt_packet_specific_binary_create_null();
		ack_pkt->message_type->f_id = FID_MESSAGE_TYPE;
		ack_pkt->message_type->f_length = 2;
		ack_pkt->message_type->f_h_value = message_type;
		ack_pkt->message_type->f_l_value = 0;

		ack_pkt->track_info = tsk_null;

	}
	return ack_pkt;
}

tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_deserialize(const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_ack_t* ack_pkt = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	const uint8_t* field_data = tsk_null;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if (pdata == tsk_null) {
		TSK_DEBUG_ERROR("Incorrect packet data");
		goto bail;
	}

	if(!(ack_pkt = tmcptt_mcptt_packet_ack_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	while (pdata != tsk_null && size > 0) { 
		switch (pdata[0]) {
		case FID_SOURCE_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_SOURCE:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			ack_pkt->source = tmcptt_mcptt_packet_specific_binary_16_deserialize(pdata[0], pdata, size);
			if (ack_pkt->source == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(ack_pkt->source);
			break;
		case FID_MESSAGE_TYPE_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_MESSAGE_TYPE:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			ack_pkt->message_type = tmcptt_mcptt_packet_specific_binary_deserialize(pdata[0], pdata, size);
			if (ack_pkt->message_type == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_get_size(ack_pkt->message_type);
			break;
		case FID_TRACK_INFO_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_TRACK_INFO:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_TXT_REF_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			ack_pkt->track_info = tmcptt_mcptt_packet_specific_binary_txt_ref_deserialize(pdata[0], pdata, size);
			if (ack_pkt->track_info == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(ack_pkt->track_info);
			break;
		default:
			TSK_DEBUG_ERROR("Field not supported");
			return tsk_null;
		}
		pdata += field_size;
		size -= field_size;
	}
bail:
	return ack_pkt;
}

int tmcptt_mcptt_packet_ack_serialize_to(const tmcptt_mcptt_packet_ack_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_ack_get_size(self)){
		return -1;
	}

	if (self->source != tsk_null) {
		tmcptt_mcptt_packet_specific_binary_16_serialize_to(self->source, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_get_size(self->source);
		pdata += field_size;
		size -= field_size;
	}

	if (self->message_type != tsk_null) {
		tmcptt_mcptt_packet_specific_binary_serialize_to(self->message_type, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_get_size(self->message_type);
		pdata += field_size;
		size -= field_size;
	}

	if (self->track_info != tsk_null) {
		tmcptt_mcptt_packet_specific_binary_txt_ref_serialize_to(self->track_info, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);
		pdata += field_size;
		size -= field_size;
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_ack_get_size(const tmcptt_mcptt_packet_ack_t* self)
{
	tsk_size_t size = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	if (self->source != tsk_null)
		size += tmcptt_mcptt_packet_specific_binary_16_get_size(self->source);

	if (self->message_type != tsk_null)
		size += tmcptt_mcptt_packet_specific_binary_get_size(self->message_type);

	if (self->track_info != tsk_null)
		size += tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);

	return size;
}
