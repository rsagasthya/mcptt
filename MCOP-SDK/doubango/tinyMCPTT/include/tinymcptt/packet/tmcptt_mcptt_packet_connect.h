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

#ifndef TINYMCPTT_MCPTT_PACKET_CONNECT_H
#define TINYMCPTT_MCPTT_PACKET_CONNECT_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_preestablished.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tsk_object.h"

TMCPTT_BEGIN_DECLS

typedef struct tmcptt_mcptt_packet_connect_s
{
	TSK_DECLARE_OBJECT;
	
	tmcptt_mcptt_packet_specific_binary_8_txt_t*	mcptt_session_identity;	
	tmcptt_mcptt_packet_specific_txt_t*				mcptt_group_identity;	
	tmcptt_mcptt_packet_specific_binary_t*			media_streams;
	tmcptt_mcptt_packet_specific_txt_t*				warning_text;
	tmcptt_mcptt_packet_specific_binary_16_t*		answer_state;
	tmcptt_mcptt_packet_specific_txt_t*				mcptt_id_inviting_user;

} tmcptt_mcptt_packet_connect_t;

TINYMCPTT_API tmcptt_mcptt_packet_connect_t* tmcptt_mcptt_packet_connect_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_connect_t* tmcptt_mcptt_packet_connect_deserialize(const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_connect_serialize_to(const tmcptt_mcptt_packet_connect_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_connect_get_size(const tmcptt_mcptt_packet_connect_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_CONNECT_H */