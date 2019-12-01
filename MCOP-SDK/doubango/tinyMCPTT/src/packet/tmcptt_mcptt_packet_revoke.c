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
#include "tinymcptt/packet/tmcptt_mcptt_packet_revoke.h"

#include "tsk_memory.h"
#include "tsk_debug.h"

static tsk_object_t* tmcptt_mcptt_packet_revoke_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_revoke_t *revoke_pkt = (tmcptt_mcptt_packet_revoke_t *)self;
	if(revoke_pkt){
		revoke_pkt->reject_cause = tsk_null;
		revoke_pkt->track_info = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_revoke_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_revoke_t *revoke_pkt = (tmcptt_mcptt_packet_revoke_t *)self;
	if(revoke_pkt){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_revoke_def_s = 
{
	sizeof(tmcptt_mcptt_packet_revoke_t),
	tmcptt_mcptt_packet_revoke_ctor, 
	tmcptt_mcptt_packet_revoke_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_revoke_def_t = &tmcptt_mcptt_packet_revoke_def_s;

tmcptt_mcptt_packet_revoke_t* tmcptt_mcptt_packet_revoke_create_null()
{
	tmcptt_mcptt_packet_revoke_t* revoke_pkt;
	revoke_pkt = (tmcptt_mcptt_packet_revoke_t*)tsk_object_new(tmcptt_mcptt_packet_revoke_def_t);
	return revoke_pkt;
}

tmcptt_mcptt_packet_revoke_t* tmcptt_mcptt_packet_revoke_deserialize(const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_revoke_t* revoke_pkt = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if(size < TMCPTT_MCPTT_PACKET_REVOKE_MIN_SIZE)
		goto bail;

	if(!(revoke_pkt = tmcptt_mcptt_packet_revoke_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	while (pdata != tsk_null && size > 0) { 
		switch (pdata[0]) {
		case FID_REJECT_CAUSE_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_REJECT_CAUSE:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			revoke_pkt->reject_cause = tmcptt_mcptt_packet_specific_binary_16_txt_deserialize(pdata[0], pdata, size);
			if (revoke_pkt->reject_cause == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_16_txt_get_size(revoke_pkt->reject_cause);
			break;
		case FID_TRACK_INFO_OLD:
			TSK_DEBUG_WARN("Use version 13.0 of Floor control specific fields");
		case FID_TRACK_INFO:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_TXT_REF_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect field size");
				return tsk_null;
			}
			revoke_pkt->track_info = tmcptt_mcptt_packet_specific_binary_txt_ref_deserialize(pdata[0], pdata, size);
			if (revoke_pkt->track_info == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(revoke_pkt->track_info);
			break;
		default:
			TSK_DEBUG_ERROR("Field not supported");
			return tsk_null;
		}
		pdata += field_size;
		size -= field_size;
	}

bail:
	return revoke_pkt;
}

int tmcptt_mcptt_packet_revoke_serialize_to(const tmcptt_mcptt_packet_revoke_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_revoke_get_size(self)){
		return -1;
	}

	if (self->reject_cause) {
		tmcptt_mcptt_packet_specific_binary_16_txt_serialize_to(self->reject_cause, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_16_txt_get_size(self->reject_cause);
		pdata += field_size;
		size -= field_size;
	}

	if (self->track_info) {
		tmcptt_mcptt_packet_specific_binary_txt_ref_serialize_to(self->track_info, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);
		pdata += field_size;
		size -= field_size;
	}


	return ret;
}

tsk_size_t tmcptt_mcptt_packet_revoke_get_size(const tmcptt_mcptt_packet_revoke_t* self)
{
	tsk_size_t size = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	if (self->reject_cause)
		size += tmcptt_mcptt_packet_specific_binary_16_txt_get_size(self->reject_cause);

	if (self->track_info)
		size += tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self->track_info);


	return size;
}
