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
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms_unmap.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tnet_endianness.h"

#include "tsk_memory.h"
#include "tsk_debug.h"

static tsk_object_t* tmcptt_mcptt_packet_mbms_unmap_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_mbms_unmap_t *mbms_unmap_pkt = (tmcptt_mcptt_packet_mbms_unmap_t *)self;
	if(mbms_unmap_pkt){
		mbms_unmap_pkt->mcptt_group_identity = tsk_null;	
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_mbms_unmap_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_mbms_unmap_t *mbms_unmap_pkt = (tmcptt_mcptt_packet_mbms_unmap_t *)self;
	if(mbms_unmap_pkt){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_mbms_unmap_def_s = 
{
	sizeof(tmcptt_mcptt_packet_mbms_unmap_t),
	tmcptt_mcptt_packet_mbms_unmap_ctor, 
	tmcptt_mcptt_packet_mbms_unmap_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_mbms_unmap_def_t = &tmcptt_mcptt_packet_mbms_unmap_def_s;

tmcptt_mcptt_packet_mbms_unmap_t* tmcptt_mcptt_packet_mbms_unmap_create_null()
{
	tmcptt_mcptt_packet_mbms_unmap_t* mbms_unmap_pkt;
	mbms_unmap_pkt = (tmcptt_mcptt_packet_mbms_unmap_t*)tsk_object_new(tmcptt_mcptt_packet_mbms_unmap_def_t);
	return mbms_unmap_pkt;
}

tmcptt_mcptt_packet_mbms_unmap_t* tmcptt_mcptt_packet_mbms_unmap_deserialize(const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_mbms_unmap_t* mbms_unmap_pkt = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;
	uint16_t participants = 0;
	int i = 0;

	if (pdata == tsk_null) {
		TSK_DEBUG_ERROR("Incorrect packet data");
		goto bail;
	}

	if(!(mbms_unmap_pkt = tmcptt_mcptt_packet_mbms_unmap_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	while (pdata != tsk_null && size > 0) { 
		switch (pdata[0]) {
		case FID_MBMS_MCPTT_GROUP_ID:
		case FID_MBMS_MCPTT_GROUP_ID_v13_3:
			if (size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE) {
				TSK_DEBUG_ERROR("Incorrect buffer size");
				return tsk_null;
			}
			mbms_unmap_pkt->mcptt_group_identity = tmcptt_mcptt_packet_specific_txt_deserialize(pdata[0], pdata, size);
			if (mbms_unmap_pkt->mcptt_group_identity == tsk_null) {
				TSK_DEBUG_ERROR("Error deserializing field");
				return tsk_null;
			}
			field_size = tmcptt_mcptt_packet_specific_txt_get_size(mbms_unmap_pkt->mcptt_group_identity);
			break;
		default:
			TSK_DEBUG_ERROR("Field not supported");
			return tsk_null;
		}
		pdata += field_size;
		size -= field_size;
	}
bail:
	return mbms_unmap_pkt;
}

int tmcptt_mcptt_packet_mbms_unmap_serialize_to(const tmcptt_mcptt_packet_mbms_unmap_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	uint32_t net_l_value = 0;
	int i = 0;

	if (!self || !data || size < tmcptt_mcptt_packet_mbms_unmap_get_size(self)){
		return -1;
	}

	if (self->mcptt_group_identity) {
		tmcptt_mcptt_packet_specific_txt_serialize_to(self->mcptt_group_identity, pdata, size);
		field_size = tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_group_identity);
		pdata += field_size;
		size -= field_size;
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_mbms_unmap_get_size(const tmcptt_mcptt_packet_mbms_unmap_t* self)
{
	tsk_size_t size = 0;
	int i = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	if(self->mcptt_group_identity)
		size += tmcptt_mcptt_packet_specific_txt_get_size(self->mcptt_group_identity);
	
	return size;
}
