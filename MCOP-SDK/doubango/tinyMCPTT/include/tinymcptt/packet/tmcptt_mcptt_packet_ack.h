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

#ifndef TINYMCPTT_MCPTT_PACKET_ACK_H
#define TINYMCPTT_MCPTT_PACKET_ACK_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tsk_object.h"

#define TMCPTT_MCPTT_PACKET_ACK_MIN_SIZE 4

TMCPTT_BEGIN_DECLS

typedef struct tmcptt_mcptt_packet_ack_s
{
	TSK_DECLARE_OBJECT;
	
	tmcptt_mcptt_packet_specific_binary_16_t*		source;
	tmcptt_mcptt_packet_specific_binary_t*			message_type;
	tmcptt_mcptt_packet_specific_binary_txt_ref_t*	track_info;           //Only when non-controlling is included

} tmcptt_mcptt_packet_ack_t;

TINYMCPTT_API tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_create(uint16_t source, uint8_t message_type);
TINYMCPTT_API tmcptt_mcptt_packet_ack_t* tmcptt_mcptt_packet_ack_deserialize(const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_ack_serialize_to(const tmcptt_mcptt_packet_ack_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_ack_get_size(const tmcptt_mcptt_packet_ack_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_IDLE_H */