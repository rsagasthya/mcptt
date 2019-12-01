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

#include "tinymcptt/packet/tmcptt_mcptt_packet_mbms.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tnet_endianness.h"

#include "tsk_memory.h"
#include "tsk_debug.h"
#include "tsk_string.h"

static tsk_object_t* tmcptt_mcptt_packet_mbms_field_subchannel_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_mbms_field_subchannel_t *subch_field = (tmcptt_mcptt_packet_mbms_field_subchannel_t *)self;
	int i = 0;
	if(subch_field){
		subch_field->f_id = 0;
		subch_field->f_length = 0;
		subch_field->audio_m_line = 0;
		subch_field->floor_m_line = 0;
		subch_field->ip_version = IPv4;
		subch_field->floor_port = 0;
		subch_field->media_port = 0;
		subch_field->ipv4_address = 0;
		for (i = 0; i < IPv6_ADDR_LENGTH; i++)
			subch_field->ipv6_address[i] = 0;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_mbms_field_subchannel_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_mbms_field_subchannel_t *subch_field = (tmcptt_mcptt_packet_mbms_field_subchannel_t *)self;
	if(subch_field){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_mbms_field_subchannel_def_s = 
{
	sizeof(tmcptt_mcptt_packet_mbms_field_subchannel_t),
	tmcptt_mcptt_packet_mbms_field_subchannel_ctor, 
	tmcptt_mcptt_packet_mbms_field_subchannel_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_mbms_field_subchannel_def_t = &tmcptt_mcptt_packet_mbms_field_subchannel_def_s;

tmcptt_mcptt_packet_mbms_field_subchannel_t* tmcptt_mcptt_packet_mbms_field_subchannel_create_null()
{
	tmcptt_mcptt_packet_mbms_field_subchannel_t* subch_field;
	subch_field = (tmcptt_mcptt_packet_mbms_field_subchannel_t*)tsk_object_new(tmcptt_mcptt_packet_mbms_field_subchannel_def_t);
	return subch_field;
}

tmcptt_mcptt_packet_mbms_field_subchannel_t* tmcptt_mcptt_packet_mbms_field_subchannel_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_mbms_field_subchannel_t* subch_field = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;
	uint32_t value_net = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_MBMS_FIELD_SUBCHANNEL_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		subch_field = tsk_null;
		goto bail;
	}

	if(!(subch_field = tmcptt_mcptt_packet_mbms_field_subchannel_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	subch_field->f_id = pdata[0];
	subch_field->f_length = pdata[1];

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	subch_field->audio_m_line = (pdata[0] & 0xF0) >> 4;
	subch_field->floor_m_line = (pdata[0] & 0x0F);
	subch_field->ip_version = (pdata[1] & 0xF0) >> 4;

	if (subch_field->ip_version != IPv4 && subch_field->ip_version != IPv6) {
		TSK_DEBUG_ERROR("Incorrect IP version");
		goto bail;
	}

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	if (subch_field->floor_m_line != 0) {
		memcpy(&value_net, pdata, sizeof(uint32_t));
		subch_field->floor_port = tnet_ntohl(value_net);
		pdata += sizeof(uint32_t);
		size -= sizeof(uint32_t);
	}

	memcpy(&value_net, pdata, sizeof(uint32_t));
	subch_field->media_port = tnet_ntohl(value_net);
	pdata += sizeof(uint32_t);
	size -= sizeof(uint32_t);
	
	if (subch_field->ip_version == IPv4) {
		memcpy(&(subch_field->ipv4_address), pdata, sizeof(uint32_t));
		pdata += sizeof(uint32_t);
		size -= sizeof(uint32_t);
	} else if (subch_field->ip_version == IPv6) {
		memcpy(subch_field->ipv6_address, pdata, IPv6_ADDR_LENGTH * sizeof(uint32_t));
		pdata += IPv6_ADDR_LENGTH * sizeof(uint32_t);
		size -= IPv6_ADDR_LENGTH * sizeof(uint32_t);
	}
	
bail:
	return subch_field;
}

int tmcptt_mcptt_packet_mbms_field_subchannel_serialize_to(const tmcptt_mcptt_packet_mbms_field_subchannel_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	uint8_t  value = 0;
	uint32_t value_net = 0;
	int i = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_mbms_field_subchannel_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	value = (((self->audio_m_line << 4) & 0xF0) | ((self->floor_m_line) & 0x0F));
	memcpy(pdata, &value, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);
	
	value = ((self->ip_version << 4) & 0xF0);
	memcpy(pdata, &value, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);
	
	if (self->floor_m_line != 0) {
		value_net = tnet_htonl(self->floor_port);
		memcpy(pdata, &value_net, sizeof(uint32_t));
		pdata += sizeof(uint32_t);
		size -= sizeof(uint32_t);
	}
	
	value_net = tnet_htonl(self->media_port);
	memcpy(pdata, &value_net, sizeof(uint32_t));
	pdata += sizeof(uint32_t);
	size -= sizeof(uint32_t);
	
	if (self->ip_version == IPv4) {
		//TODO: not sure... net_l_value
		memcpy(data, &self->ipv4_address, sizeof(uint32_t));
		pdata += sizeof(uint32_t);
		size -= sizeof(uint32_t);
	} else if (self->ip_version == IPv6) {
		for (i = 0; i < IPv6_ADDR_LENGTH; i++) {
			//TODO: Not sure... net_l_value = htonl(mcptt_field->ipv6_address[i]);
			memcpy(data, &self->ipv6_address[i], sizeof(uint32_t));
			pdata += sizeof(uint32_t);
			size -= sizeof(uint32_t);
		}
	}



	return ret;
}

tsk_size_t tmcptt_mcptt_packet_mbms_field_subchannel_get_size(const tmcptt_mcptt_packet_mbms_field_subchannel_t* self)
{
	tsk_size_t size;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 4*sizeof(uint8_t);

	if (self->floor_m_line != 0)
		size += sizeof(uint32_t); //Floor ctrl port
	
	size += sizeof(uint32_t); //Media port
	
	if (self->ip_version == IPv4)
		size += sizeof(uint32_t);
	else if (self->ip_version == IPv6)
		size += IPv6_ADDR_LENGTH * sizeof(uint32_t);

	return size;
}
