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

#ifndef TINYMCPTT_MCPTT_PACKET_MBMS_H
#define TINYMCPTT_MCPTT_PACKET_MBMS_H

#include "tinymcptt_config.h"

#include "tsk_object.h"

#define MCPTT_MBMS_PROTO_NAME		"MCMC"
#define MCPTT_MBMS_PROTO_NAME_OLD	"MCCP"

#define IPv6_ADDR_LENGTH 4

#define TMCPTT_MCPTT_PACKET_MBMS_FIELD_SUBCHANNEL_MIN_SIZE 12

TMCPTT_BEGIN_DECLS

/* MCPTT MBMS session message type header field */
typedef enum {
	MAP_GROUP_TO_BEARER			= 0,  //0b00000,
	UNMAP_GROUP_TO_BEARER		= 1  //0b00001
} tmcptt_mcptt_packet_mbms_type_t;

/* MCPTT specific field ids */
typedef enum {
  FID_MBMS_SUBCHANNEL            = 000,
  FID_MBMS_TMGI                  = 001,
  FID_MBMS_MCPTT_GROUP_ID_v13_3  = 002,
  FID_MBMS_MCPTT_GROUP_ID        = 003
} tmcptt_mcptt_packet_mbms_fid_t;

/* Some constant values */
typedef enum {
  IPv4			= 0, 
  IPv6			= 1
} tmcptt_mcptt_packet_mbms_ip_version_t;

typedef struct tmcptt_mcptt_packet_mbms_field_subchannel_s {
	
	TSK_DECLARE_OBJECT;

	uint8_t  f_id;
	uint8_t  f_length;
	uint8_t  audio_m_line;
	uint8_t  floor_m_line;
	uint8_t  ip_version;
	uint32_t floor_port;
	uint32_t media_port;
	uint32_t ipv4_address;
	uint32_t ipv6_address[IPv6_ADDR_LENGTH];

} tmcptt_mcptt_packet_mbms_field_subchannel_t;


TINYMCPTT_API tmcptt_mcptt_packet_mbms_field_subchannel_t* tmcptt_mcptt_packet_mbms_field_subchannel_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_mbms_field_subchannel_t* tmcptt_mcptt_packet_mbms_field_subchannel_deserialize(const uint8_t fid, const void* data, tsk_size_t _size);
TINYMCPTT_API int tmcptt_mcptt_packet_mbms_field_subchannel_serialize_to(const tmcptt_mcptt_packet_mbms_field_subchannel_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_mbms_field_subchannel_get_size(const tmcptt_mcptt_packet_mbms_field_subchannel_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_MBMS_PACKET_H */
