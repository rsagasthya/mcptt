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

#ifndef TINYMCPTT_MCPTT_PACKET_IDLE_H
#define TINYMCPTT_MCPTT_PACKET_IDLE_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tsk_object.h"

TMCPTT_BEGIN_DECLS

/*
* MCPTT Floor IDLE
*
 0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|V=2|P| Subtype |   PT=APP=204  |          length=2             |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                      SSRC of floor control server             |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                          name=MCPT                            |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                 Message Sequence Number field                 |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       Track Info field                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                    Floor Indicator field                      |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++
*/

typedef struct tmcptt_mcptt_packet_idle_s {
	TSK_DECLARE_OBJECT;

	tmcptt_mcptt_packet_specific_binary_16_t*		message_seq_num;
	tmcptt_mcptt_packet_specific_binary_txt_ref_t*	track_info;			//Only when non-controlling is included
	tmcptt_mcptt_packet_specific_binary_16_t*		floor_indicator;  
} tmcptt_mcptt_packet_idle_t;

TINYMCPTT_API tmcptt_mcptt_packet_idle_t* tmcptt_mcptt_packet_idle_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_idle_t* tmcptt_mcptt_packet_idle_deserialize(const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_idle_serialize_to(const tmcptt_mcptt_packet_idle_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_idle_get_size(const tmcptt_mcptt_packet_idle_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_IDLE_H */