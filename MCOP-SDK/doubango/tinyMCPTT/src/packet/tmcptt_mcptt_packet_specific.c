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
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tnet_endianness.h"

#include "tsk_memory.h"
#include "tsk_debug.h"
#include "tsk_string.h"

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_binary_t *specific = (tmcptt_mcptt_packet_specific_binary_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_h_value = 0;
		specific->f_l_value = 0;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_binary_t *specific = (tmcptt_mcptt_packet_specific_binary_t *)self;
	if(specific){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_specific_binary_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_binary_t),
	tmcptt_mcptt_packet_specific_binary_ctor, 
	tmcptt_mcptt_packet_specific_binary_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_binary_def_t = &tmcptt_mcptt_packet_specific_binary_def_s;

tmcptt_mcptt_packet_specific_binary_t* tmcptt_mcptt_packet_specific_binary_create_null()
{
	tmcptt_mcptt_packet_specific_binary_t* specific;
	specific = (tmcptt_mcptt_packet_specific_binary_t*)tsk_object_new(tmcptt_mcptt_packet_specific_binary_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_binary_t* tmcptt_mcptt_packet_specific_binary_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_binary_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(pdata[1] != 2){
		TSK_DEBUG_INFO("Incorrect size id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_binary_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	specific->f_h_value = pdata[0];
	specific->f_l_value = pdata[1];
	
bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_binary_serialize_to(const tmcptt_mcptt_packet_specific_binary_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	uint16_t value_net = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_specific_binary_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_h_value, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_l_value, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_get_size(const tmcptt_mcptt_packet_specific_binary_t* self)
{
	tsk_size_t size;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2*sizeof(uint8_t) + 2*sizeof(uint8_t);

	return size;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_16_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_binary_16_t *specific = (tmcptt_mcptt_packet_specific_binary_16_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_value = 0;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_16_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_binary_16_t *specific = (tmcptt_mcptt_packet_specific_binary_16_t *)self;
	if(specific){
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_specific_binary_16_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_binary_16_t),
	tmcptt_mcptt_packet_specific_binary_16_ctor, 
	tmcptt_mcptt_packet_specific_binary_16_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_binary_16_def_t = &tmcptt_mcptt_packet_specific_binary_16_def_s;

tmcptt_mcptt_packet_specific_binary_16_t* tmcptt_mcptt_packet_specific_binary_16_create_null()
{
	tmcptt_mcptt_packet_specific_binary_16_t* specific;
	specific = (tmcptt_mcptt_packet_specific_binary_16_t*)tsk_object_new(tmcptt_mcptt_packet_specific_binary_16_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_binary_16_t* tmcptt_mcptt_packet_specific_binary_16_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_binary_16_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(pdata[1] != 2){
		TSK_DEBUG_INFO("Incorrect size id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_binary_16_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	specific->f_value = tnet_ntohs_2(pdata);
	pdata += sizeof(uint16_t);
	size -= sizeof(uint16_t);

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_binary_16_serialize_to(const tmcptt_mcptt_packet_specific_binary_16_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	uint16_t value_net = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_specific_binary_16_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	value_net = tnet_htons_2(&self->f_value);
	memcpy(pdata, &value_net, sizeof(uint16_t));
	pdata += sizeof(uint16_t);
	size -= sizeof(uint16_t);
	

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_32_get_size(const tmcptt_mcptt_packet_specific_ssrc_t* self)
{
	tsk_size_t size;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2*sizeof(uint8_t) + sizeof(uint32_t)+ 2*sizeof(uint8_t);

	return size;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_16_get_size(const tmcptt_mcptt_packet_specific_binary_16_t* self)
{
	tsk_size_t size;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2*sizeof(uint8_t) + sizeof(uint16_t);

	return size;
}


static tsk_object_t* tmcptt_mcptt_packet_specific_txt_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_txt_t *specific = (tmcptt_mcptt_packet_specific_txt_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_value = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_txt_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_txt_t *specific = (tmcptt_mcptt_packet_specific_txt_t *)self;
	if(specific){
		if(specific->f_value)
			TSK_FREE(specific->f_value);
	}

	return self;
}

//INIT new SSRC
static tsk_object_t* tmcptt_mcptt_packet_specific_ssrc_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_ssrc_t *specific = (tmcptt_mcptt_packet_specific_ssrc_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_value = 0;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_ssrc_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_ssrc_t *specific = (tmcptt_mcptt_packet_specific_ssrc_t *)self;
	if(specific){
		if(specific->f_value)
			TSK_FREE(specific->f_value);
	}

	return self;
}
//END new SSRC

static const tsk_object_def_t tmcptt_mcptt_packet_specific_txt_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_txt_t),
	tmcptt_mcptt_packet_specific_txt_ctor, 
	tmcptt_mcptt_packet_specific_txt_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_txt_def_t = &tmcptt_mcptt_packet_specific_txt_def_s;


static const tsk_object_def_t tmcptt_mcptt_packet_specific_ssrc_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_ssrc_t),
	tmcptt_mcptt_packet_specific_ssrc_ctor, 
	tmcptt_mcptt_packet_specific_ssrc_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_ssrc_def_t = &tmcptt_mcptt_packet_specific_ssrc_def_s;

tmcptt_mcptt_packet_specific_txt_t* tmcptt_mcptt_packet_specific_txt_create_null()
{
	tmcptt_mcptt_packet_specific_txt_t* specific;
	specific = (tmcptt_mcptt_packet_specific_txt_t*)tsk_object_new(tmcptt_mcptt_packet_specific_txt_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_txt_t* tmcptt_mcptt_packet_specific_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_txt_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_txt_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	if(specific->f_length > 0){
		#if HAVE_CRT //Debug memory
		specific->f_value = (char *)malloc(specific->f_length * sizeof(char));
		#else
		specific->f_value = (char *)tsk_malloc(specific->f_length * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(specific->f_value, pdata, specific->f_length * sizeof(char));
		pdata += specific->f_length;
		size -= specific->f_length;
	}

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_txt_serialize_to(const tmcptt_mcptt_packet_specific_txt_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	int padding_bytes = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_specific_txt_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	if(self->f_length > 0){
		memcpy(pdata, self->f_value, self->f_length * sizeof(char));
		pdata += self->f_length;
		size -= self->f_length;
	}

	/* Padding? */
	if ((self->f_length + 2 * sizeof(uint8_t)) % 4 != 0) {
		padding_bytes = 4 - ((self->f_length + 2 * sizeof(uint8_t)) % 4);
		if (padding_bytes != 0) 
			memset(pdata, 0, padding_bytes);
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_txt_get_size(const tmcptt_mcptt_packet_specific_txt_t* self)
{
	tsk_size_t size;
	int padding_bytes = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2*sizeof(uint8_t) + self->f_length;
	if ((self->f_length + 2 * sizeof(uint8_t)) % 4 != 0) { //There is no 16 bit "value" field
    padding_bytes = 4 - ((self->f_length + 2 * sizeof(uint8_t)) % 4);
    size += padding_bytes;
  }
	return size;
}

//INIT new SSRC
tmcptt_mcptt_packet_specific_ssrc_t* tmcptt_mcptt_packet_specific_ssrc_create_null()
{
	tmcptt_mcptt_packet_specific_ssrc_t* specific;
	specific = (tmcptt_mcptt_packet_specific_ssrc_t*)tsk_object_new(tmcptt_mcptt_packet_specific_ssrc_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_ssrc_t* tmcptt_mcptt_packet_specific_ssrc_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_ssrc_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_ssrc_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];

	if(specific->f_length!=6){
		TSK_DEBUG_ERROR("Error in SSRC format. incorrect length");
		return tsk_null;
	}

	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	if(specific->f_length > 0){
		uint32_t ssrc_net;
		memcpy(&ssrc_net, pdata, (specific->f_length-2) * sizeof(uint8_t));
		specific->f_value=tnet_ntohl(ssrc_net);
		pdata += specific->f_length-2;
		size -= specific->f_length-2;
	}


	if(pdata[0]!=0x000 || pdata[1]!=0x000){//second and thiers byte is =0b00000000, value is 0;
		TSK_DEBUG_ERROR("Error in SSRC format. It does not have 2 bytes with value 0");
		return tsk_null;
	}

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_ssrc_serialize_to(const tmcptt_mcptt_packet_specific_ssrc_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	int padding_bytes = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_specific_ssrc_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	if(self->f_length > 0){
		memcpy(pdata, &self->f_value, self->f_length * sizeof(uint8_t));
		pdata += self->f_length;
		size -= self->f_length;
	}

	/* Padding? */
	if ((self->f_length + 2 * sizeof(uint8_t)) % 4 != 0) {
		padding_bytes = 4 - ((self->f_length + 2 * sizeof(uint8_t)) % 4);
		if (padding_bytes != 0) 
			memset(pdata, 0, padding_bytes);
	}

	return ret;
}



tsk_size_t tmcptt_mcptt_packet_specific_ssrc_get_size(const tmcptt_mcptt_packet_specific_ssrc_t* self)
{
	tsk_size_t size;
	int padding_bytes = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2*sizeof(uint8_t) + self->f_length;
	if ((self->f_length + 2 * sizeof(uint8_t)) % 4 != 0) { //There is no 16 bit "value" field
    padding_bytes = 4 - ((self->f_length + 2 * sizeof(uint8_t)) % 4);
    size += padding_bytes;
  }
	return size;
}
//END new SSRC


static tsk_object_t* tmcptt_mcptt_packet_specific_binary_8_txt_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_binary_8_txt_t *specific = (tmcptt_mcptt_packet_specific_binary_8_txt_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_bin_value = 0;
		specific->f_txt_value = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_8_txt_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_binary_8_txt_t *specific = (tmcptt_mcptt_packet_specific_binary_8_txt_t *)self;
	if(specific){
		if(specific->f_txt_value)
			TSK_FREE(specific->f_txt_value);
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_specific_binary_8_txt_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_binary_8_txt_t),
	tmcptt_mcptt_packet_specific_binary_8_txt_ctor, 
	tmcptt_mcptt_packet_specific_binary_8_txt_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_binary_8_txt_def_t = &tmcptt_mcptt_packet_specific_binary_8_txt_def_s;

tmcptt_mcptt_packet_specific_binary_8_txt_t* tmcptt_mcptt_packet_specific_binary_8_txt_create_null()
{
	tmcptt_mcptt_packet_specific_binary_8_txt_t* specific;
	specific = (tmcptt_mcptt_packet_specific_binary_8_txt_t*)tsk_object_new(tmcptt_mcptt_packet_specific_binary_8_txt_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_binary_8_txt_t* tmcptt_mcptt_packet_specific_binary_8_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_binary_8_txt_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;
	tsk_size_t field_txt_size = 0;
	
	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_8_TXT_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_binary_8_txt_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];
	specific->f_bin_value = pdata[2];
	
	pdata += 3*sizeof(uint8_t);
	size -= 3*sizeof(uint8_t);

	if (size > 0 && specific->f_length > sizeof(uint8_t)) {
		field_txt_size = specific->f_length - sizeof(uint8_t);
		if (field_txt_size > 0) {
			#if HAVE_CRT //Debug memory
		specific->f_txt_value = (char *)malloc(field_txt_size * sizeof(char));
			#else
		specific->f_txt_value = (char *)tsk_malloc(field_txt_size * sizeof(char));
			#endif //HAVE_CRT
			
			memcpy(specific->f_txt_value, pdata, field_txt_size);
			pdata += field_txt_size;
			size -= field_txt_size;	
		}
	}

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_binary_8_txt_serialize_to(const tmcptt_mcptt_packet_specific_binary_8_txt_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	tsk_size_t field_txt_size = 0;
	int padding_bytes = 0;
    int i = 0;
	
	if(!self || !data || size < tmcptt_mcptt_packet_specific_binary_8_txt_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_bin_value, sizeof(uint8_t));
    pdata += sizeof(uint8_t);
    size -= sizeof(uint8_t);

	if (self->f_txt_value != tsk_null && self->f_length > sizeof(uint8_t)) {    
		field_txt_size = self->f_length - sizeof(uint8_t);
		if (field_txt_size > 0) {
			memcpy(pdata, self->f_txt_value, field_txt_size);
			pdata += field_txt_size;
			size -= field_txt_size;

			/* Padding? */
			if ((2 * sizeof(uint8_t) + self->f_length) % 4 != 0) {
				padding_bytes = 4 - ((2 * sizeof(uint8_t) + self->f_length) % 4);
				if (padding_bytes != 0)
					memset(data, 0, padding_bytes);
				pdata += padding_bytes;
				size -= padding_bytes;
			}
		}
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_8_txt_get_size(const tmcptt_mcptt_packet_specific_binary_8_txt_t* self)
{
	tsk_size_t size;
	int padding_bytes = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2 * sizeof(uint8_t) + self->f_length;
	if ((self->f_length + 2 * sizeof(uint8_t)) % 4 != 0) { 
		padding_bytes = 4 - ((self->f_length + 2 * sizeof(uint8_t)) % 4);
		size += padding_bytes;
	}

	return size;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_16_txt_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_binary_16_txt_t *specific = (tmcptt_mcptt_packet_specific_binary_16_txt_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_bin_value = 0;
		specific->f_txt_value = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_16_txt_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_binary_16_txt_t *specific = (tmcptt_mcptt_packet_specific_binary_16_txt_t *)self;
	if(specific){
		if(specific->f_txt_value)
			TSK_FREE(specific->f_txt_value);
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_specific_binary_16_txt_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_binary_16_txt_t),
	tmcptt_mcptt_packet_specific_binary_16_txt_ctor, 
	tmcptt_mcptt_packet_specific_binary_16_txt_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_binary_16_txt_def_t = &tmcptt_mcptt_packet_specific_binary_16_txt_def_s;

tmcptt_mcptt_packet_specific_binary_16_txt_t* tmcptt_mcptt_packet_specific_binary_16_txt_create_null()
{
	tmcptt_mcptt_packet_specific_binary_16_txt_t* specific;
	specific = (tmcptt_mcptt_packet_specific_binary_16_txt_t*)tsk_object_new(tmcptt_mcptt_packet_specific_binary_16_txt_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_binary_16_txt_t* tmcptt_mcptt_packet_specific_binary_16_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_binary_16_txt_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;
	tsk_size_t field_txt_size = 0;
	uint16_t net_value_s = 0;
	
	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_TXT_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_binary_16_txt_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];
	
	pdata += 2*sizeof(uint8_t);
	size -= 2*sizeof(uint8_t);

	specific->f_bin_value = pdata[2];

	memcpy(&net_value_s, pdata, sizeof(uint16_t));
	specific->f_bin_value = tnet_ntohs(net_value_s);

    pdata += sizeof(uint16_t);
	if (specific->f_length > sizeof(uint16_t)) {
		field_txt_size = specific->f_length - sizeof(uint16_t);
		#if HAVE_CRT //Debug memory
		specific->f_txt_value = (char*)malloc(field_txt_size * sizeof(char));
		#else
		specific->f_txt_value = (char*)tsk_malloc(field_txt_size * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(specific->f_txt_value, pdata, field_txt_size);
	}

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_binary_16_txt_serialize_to(const tmcptt_mcptt_packet_specific_binary_16_txt_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	tsk_size_t field_txt_size = 0;
	int padding_bytes = 0;
    int i = 0;
	
	if(!self || !data || size < tmcptt_mcptt_packet_specific_binary_16_txt_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_bin_value, sizeof(uint16_t));
    pdata += sizeof(uint16_t);
    size -= sizeof(uint16_t);

	if (self->f_txt_value != tsk_null && self->f_length > sizeof(uint16_t)) {    
		field_txt_size = self->f_length - sizeof(uint16_t);
		if (field_txt_size > 0) {
			memcpy(pdata, self->f_txt_value, field_txt_size);
			pdata += field_txt_size;
			size -= field_txt_size;

			/* Padding? */
			if (field_txt_size % 4 != 0) {
				padding_bytes = 4 - (field_txt_size % 4);
				if (padding_bytes != 0)
					memset(data, 0, padding_bytes);
				pdata += padding_bytes;
				size -= padding_bytes;
			}
		}
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_16_txt_get_size(const tmcptt_mcptt_packet_specific_binary_16_txt_t* self)
{
	tsk_size_t size;
	int padding_bytes = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 2 * sizeof(uint8_t) + self->f_length;
	if ((self->f_length - sizeof(uint16_t)) % 4 != 0) { 
		padding_bytes = 4 - ((self->f_length - sizeof(uint16_t)) % 4);
		size += padding_bytes;
	}

	return size;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_txt_ref_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mcptt_packet_specific_binary_txt_ref_t *specific = (tmcptt_mcptt_packet_specific_binary_txt_ref_t *)self;
	if(specific){
		specific->f_id = 0;
		specific->f_length = 0;
		specific->f_bin_h_value = 0;
		specific->f_bin_l_value = 0;
		specific->f_txt_value = tsk_null;
		specific->num_refs = 0;
		specific->f_refs = tsk_null;
	}
	return self;
}

static tsk_object_t* tmcptt_mcptt_packet_specific_binary_txt_ref_dtor(tsk_object_t * self)
{ 
	tmcptt_mcptt_packet_specific_binary_txt_ref_t *specific = (tmcptt_mcptt_packet_specific_binary_txt_ref_t *)self;
	if(specific){
		if(specific->f_txt_value)
			TSK_FREE(specific->f_txt_value);
		if(specific->f_refs)
			TSK_FREE(specific->f_refs);
	}

	return self;
}

static const tsk_object_def_t tmcptt_mcptt_packet_specific_binary_txt_ref_def_s = 
{
	sizeof(tmcptt_mcptt_packet_specific_binary_txt_ref_t),
	tmcptt_mcptt_packet_specific_binary_txt_ref_ctor, 
	tmcptt_mcptt_packet_specific_binary_txt_ref_dtor,
	tsk_null, 
};
const tsk_object_def_t *tmcptt_mcptt_packet_specific_binary_txt_ref_def_t = &tmcptt_mcptt_packet_specific_binary_txt_ref_def_s;

tmcptt_mcptt_packet_specific_binary_txt_ref_t* tmcptt_mcptt_packet_specific_binary_txt_ref_create_null()
{
	tmcptt_mcptt_packet_specific_binary_txt_ref_t* specific;
	specific = (tmcptt_mcptt_packet_specific_binary_txt_ref_t*)tsk_object_new(tmcptt_mcptt_packet_specific_binary_txt_ref_def_t);
	return specific;
}

tmcptt_mcptt_packet_specific_binary_txt_ref_t* tmcptt_mcptt_packet_specific_binary_txt_ref_deserialize(const uint8_t fid, const void* data, tsk_size_t _size)
{
	tmcptt_mcptt_packet_specific_binary_txt_ref_t* specific = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;
	tsk_size_t field_size = 0;
	uint32_t net_l_value = 0;
	int padding_bytes = 0;
	int i = 0;

	if(!data || size < TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_TXT_REF_MIN_SIZE){
		TSK_DEBUG_ERROR("Incorrect parameters");
		goto bail;
	}

	if(pdata[0] != fid){
		TSK_DEBUG_INFO("Incorrect field id");
		specific = tsk_null;
		goto bail;
	}

	if(!(specific = tmcptt_mcptt_packet_specific_binary_txt_ref_create_null())){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	specific->f_id = pdata[0];
	specific->f_length = pdata[1];
	specific->f_bin_h_value = pdata[2];
	specific->f_bin_l_value = pdata[3];

	pdata += 4*sizeof(uint8_t);
	size -= 4*sizeof(uint8_t);

	if (size > 0 && specific->f_bin_l_value > 0) {
		#if HAVE_CRT //Debug memory
		specific->f_txt_value = (char *)malloc(specific->f_bin_l_value * sizeof(char));
		#else
		specific->f_txt_value = (char *)tsk_malloc(specific->f_bin_l_value * sizeof(char));
		#endif //HAVE_CRT
		
		memcpy(specific->f_txt_value, pdata, specific->f_bin_l_value * sizeof(char));
		pdata += specific->f_bin_l_value;
		size -= specific->f_bin_l_value;

		/* Padding? */
		if (specific->f_bin_l_value % 4 != 0) {
		  padding_bytes = 4 - (specific->f_bin_l_value % 4);
		  pdata += padding_bytes;
		  size -= padding_bytes;
		}
	}

	//Length == Size(f_bin_h_value) + n*Size(ref)
	specific->num_refs = (specific->f_length - sizeof(uint8_t)) / sizeof(uint32_t);

	if (specific->num_refs > 0) {
		#if HAVE_CRT //Debug memory
		specific->f_refs = (uint32_t*)malloc(specific->num_refs * sizeof(uint32_t));
		
		#else
		specific->f_refs = (uint32_t*)tsk_malloc(specific->num_refs * sizeof(uint32_t));
		
		#endif //HAVE_CRT
		for (i = 0; i < specific->num_refs && size > 0; i++) {
			memcpy(&net_l_value, pdata, sizeof(uint32_t));
			specific->f_refs[i] = tnet_ntohl(net_l_value);
			pdata += sizeof(uint32_t);
			size -= sizeof(uint32_t);
		}
	}

bail:
	return specific;
}

int tmcptt_mcptt_packet_specific_binary_txt_ref_serialize_to(const tmcptt_mcptt_packet_specific_binary_txt_ref_t* self, void* data, tsk_size_t size)
{
	int ret = 0;
	uint8_t* pdata = (uint8_t*)data;
	tsk_size_t field_size = 0;
	int padding_bytes = 0;
    int i = 0;
	uint32_t net_l_value = 0;

	if(!self || !data || size < tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(self)){
		return -1;
	}

	memcpy(pdata, &self->f_id, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_length, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_bin_h_value, sizeof(uint8_t));
    pdata += sizeof(uint8_t);
    size -= sizeof(uint8_t);

	memcpy(pdata, &self->f_bin_l_value, sizeof(uint8_t));
	pdata += sizeof(uint8_t);
	size -= sizeof(uint8_t);

	if (self->f_txt_value != tsk_null) {    
		memcpy(pdata, self->f_txt_value, self->f_bin_l_value);
		pdata += self->f_bin_l_value;
		size -= self->f_bin_l_value;

		/* Padding? */
		if (self->f_bin_l_value % 4 != 0) {
		  padding_bytes = 4 - (self->f_bin_l_value % 4);
		  if (padding_bytes != 0)
			memset(pdata, 0, padding_bytes);
		  pdata += padding_bytes;
		  size -= padding_bytes;
		}
	}

	for (i = 0; i < self->num_refs; i++) {
		net_l_value = tnet_htonl(self->f_refs[i]);
		memcpy(data, &net_l_value, sizeof(uint32_t));
		pdata += sizeof(uint32_t);
		size -= sizeof(uint32_t);
	}

	return ret;
}

tsk_size_t tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(const tmcptt_mcptt_packet_specific_binary_txt_ref_t* self)
{
	tsk_size_t size;
	int padding_bytes = 0;

	if(!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = 4*sizeof(uint8_t) + self->f_bin_l_value + self->num_refs * sizeof(uint32_t);
	if (self->f_bin_l_value % 4 != 0) {
		padding_bytes = 4 - (self->f_bin_l_value % 4);
		size += padding_bytes;
	}

	return size;
}

