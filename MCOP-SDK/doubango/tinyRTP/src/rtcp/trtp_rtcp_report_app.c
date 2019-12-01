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
#include "tinyrtp/rtcp/trtp_rtcp_report_app.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_fb.h"
#include "tinyrtp/rtcp/trtp_rtcp_header.h"

#include "tnet_endianness.h"

#include "tsk_memory.h"
#include "tsk_debug.h"
#include "tsk_string.h"

#define TRTP_RTCP_PACKET_APP_MIN_SIZE (TRTP_RTCP_HEADER_SIZE + 0/* could have no data */)

static tsk_object_t* trtp_rtcp_report_app_ctor(tsk_object_t * self, va_list * app)
{
	trtp_rtcp_report_app_t *rtcp_app = self;
	if(rtcp_app){
		rtcp_app->payload_size = 0;
		rtcp_app->payload = tsk_null;
	}
	return self;
}

static tsk_object_t* trtp_rtcp_report_app_dtor(tsk_object_t * self)
{ 
	trtp_rtcp_report_app_t *rtcp_app = self;
	if(rtcp_app){
		if(rtcp_app->payload)
		{
		  TSK_FREE(rtcp_app->payload);
	 	  rtcp_app->payload = tsk_null;
		}
		rtcp_app->payload_size = 0;
		// deinit base
		trtp_rtcp_packet_deinit(TRTP_RTCP_PACKET(rtcp_app));
	}

	return self;
}

static const tsk_object_def_t trtp_rtcp_report_app_def_s = 
{
	sizeof(trtp_rtcp_report_app_t),
	trtp_rtcp_report_app_ctor, 
	trtp_rtcp_report_app_dtor,
	tsk_null, 
};
const tsk_object_def_t *trtp_rtcp_report_app_def_t = &trtp_rtcp_report_app_def_s;

trtp_rtcp_report_app_t* trtp_rtcp_report_app_create_null()
{
	trtp_rtcp_report_app_t* rtcp_app;
	if((rtcp_app = (trtp_rtcp_report_app_t*)tsk_object_new(trtp_rtcp_report_app_def_t))){
		trtp_rtcp_packet_init(TRTP_RTCP_PACKET(rtcp_app), TRTP_RTCP_HEADER_VERSION_DEFAULT, 0, 0, trtp_rtcp_packet_type_app, TRTP_RTCP_HEADER_SIZE);
	}
	return rtcp_app;
}

trtp_rtcp_report_app_t* trtp_rtcp_report_app_create(struct trtp_rtcp_header_s* header)
{
	trtp_rtcp_report_app_t* rtcp_app;
	if((rtcp_app = (trtp_rtcp_report_app_t*)tsk_object_new(trtp_rtcp_report_app_def_t))){
		TRTP_RTCP_PACKET(rtcp_app)->header = tsk_object_ref(header);
	}
	return rtcp_app;
}

trtp_rtcp_report_app_t* trtp_rtcp_report_app_create_2(const char* app_name, uint8_t subtype, uint32_t ssrc, const char* data, tsk_size_t size)
{
	trtp_rtcp_report_app_t* rtcp_app = tsk_null;
	uint16_t total_length = 0;
	if(data){
		if((rtcp_app = (trtp_rtcp_report_app_t*)tsk_object_new(trtp_rtcp_report_app_def_t))){
			total_length = TRTP_RTCP_HEADER_SIZE + 4*sizeof(char) + sizeof(uint32_t) + (uint16_t)size;
			total_length = ((total_length % 4) == 0)?total_length:(total_length + (4-(total_length % 4)));
			trtp_rtcp_packet_init(TRTP_RTCP_PACKET(rtcp_app), TRTP_RTCP_HEADER_VERSION_DEFAULT, 0, subtype, trtp_rtcp_packet_type_app, total_length);
			memcpy(rtcp_app->name, app_name, 4*sizeof(char));
			rtcp_app->ssrc = ssrc;
			#if HAVE_CRT //Debug memory
		rtcp_app->payload = (uint8_t*)malloc(size*sizeof(uint8_t));
	#else
		rtcp_app->payload = (uint8_t*)tsk_malloc(size*sizeof(uint8_t));
	#endif //HAVE_CRT
			
			if(rtcp_app->payload){
				memcpy(rtcp_app->payload, data, size);
				rtcp_app->payload_size = (uint16_t)size;
			}
			rtcp_app->subtype = subtype;
		}
		
		return rtcp_app;
	}
	return tsk_null;
}

trtp_rtcp_report_app_t* trtp_rtcp_report_app_deserialize(const void* data, tsk_size_t _size)
{
	trtp_rtcp_report_app_t* rtcp_app = tsk_null;
	trtp_rtcp_header_t* header = tsk_null;
	const uint8_t* pdata = (const uint8_t*)data;
	int32_t size = (int32_t)_size;

	if(!data || size < TRTP_RTCP_PACKET_APP_MIN_SIZE){

		TSK_DEBUG_ERROR("Invalid parameter");
		return tsk_null;
	}
	
	if(!(header = trtp_rtcp_header_deserialize(pdata, size))){
		TSK_DEBUG_ERROR("Failed to deserialize the header");
		goto bail;
	}
	if(header->length_in_bytes < TRTP_RTCP_PACKET_APP_MIN_SIZE){
		TSK_DEBUG_ERROR("Too short");
		goto bail;
	}
	
	if(!(rtcp_app = trtp_rtcp_report_app_create(header))){
		TSK_DEBUG_ERROR("Failed to create object");
		goto bail;
	}

	rtcp_app->subtype = header->rc;

	pdata += (TRTP_RTCP_HEADER_SIZE);
	size -= (TRTP_RTCP_HEADER_SIZE);
	
	rtcp_app->ssrc = tnet_ntohl_2(pdata);
	
	pdata += sizeof(uint32_t);
	size -= sizeof(uint32_t);

	memcpy(rtcp_app->name, pdata, 4*sizeof(char));

	pdata += 4*sizeof(char);
	size -= 4*sizeof(char);
	#if HAVE_CRT //Debug memory
		rtcp_app->payload = (uint8_t*)malloc(size*sizeof(uint8_t));
	#else
		rtcp_app->payload = (uint8_t*)tsk_malloc(size*sizeof(uint8_t));
	#endif //HAVE_CRT
	
	if(rtcp_app->payload){
		memcpy(rtcp_app->payload, pdata, size);
		rtcp_app->payload_size = size;
	}

bail:
	TSK_OBJECT_SAFE_FREE(header);
	return rtcp_app;
}

int trtp_rtcp_report_app_serialize_to(const trtp_rtcp_report_app_t* self, void* data, tsk_size_t size)
{
	int ret;
	uint8_t* pdata = (uint8_t*)data;
	uint32_t ssrc_net = 0;

	if(!self || !data || size < trtp_rtcp_report_app_get_size(self)){
		return -1;
	}

	if((ret = trtp_rtcp_header_serialize_to(TRTP_RTCP_PACKET(self)->header, pdata, size))){
		TSK_DEBUG_ERROR("Failed to serialize the header");
		return ret;
	}

	pdata += (TRTP_RTCP_HEADER_SIZE);
	size -= (TRTP_RTCP_HEADER_SIZE);

	ssrc_net = tnet_htonl_2(&self->ssrc);
	memcpy(pdata, &ssrc_net, sizeof(uint32_t));

	pdata += sizeof(uint32_t);
	size -= sizeof(uint32_t);

	memcpy(pdata, self->name, 4*sizeof(char));

	pdata += 4*sizeof(char);
	size -= 4*sizeof(char);

	memcpy(pdata, self->payload, self->payload_size);

	pdata += self->payload_size;
	size -= self->payload_size;

	if(size > 0)
		memset(pdata, 0, size);

	return ret;
}

tsk_size_t trtp_rtcp_report_app_get_size(const trtp_rtcp_report_app_t* self)
{
	tsk_size_t size;

	if(!self || !TRTP_RTCP_PACKET(self)->header){
		TSK_DEBUG_ERROR("Invalid parameter");
		return 0;
	}

	size = TRTP_RTCP_PACKET(self)->header->length_in_bytes;

	return size;
}
