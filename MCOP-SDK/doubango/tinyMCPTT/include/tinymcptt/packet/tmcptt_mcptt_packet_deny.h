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

#ifndef TINYMCPTT_MCPTT_PACKET_DENY_H
#define TINYMCPTT_MCPTT_PACKET_DENY_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#include "tsk_object.h"

#define TMCPTT_MCPTT_PACKET_DENY_REASON_MIN_SIZE 2

TMCPTT_BEGIN_DECLS

/* Deny reason codes */

/* MCPTT Floor Deny reason codes */
typedef enum {
  DENY_REASON_ANOTHER_USER            = 1,
  DENY_REASON_INTERNAL_ERROR          = 2,
  DENY_REASON_ONLY_ONE_PARTICIPANT    = 3,
  DENY_REASON_RETRY_TIMER_NOT_EXPIRED = 4,
  DENY_REASON_RECEIVE_ONLY            = 5,
  DENY_REASON_NO_RESOURCES            = 6,
  DENY_REASON_QUEUE_FULL              = 7,
  DENY_REASON_OTHER                   = 255,
} tmcptt_mcptt_packet_deny_reason_t;

/*
* MCPTT Floor DENY
*
 0                   1                   2                   3
0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|V=2|P| Subtype |   PT=APP=204  |            length             |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                SSRC of floor control server                   |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                          name=MCPT                            |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                      Reject Cause field                       |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       User ID field                           |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
|                       Track Info field                        |
+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

*/

typedef struct tmcptt_mcptt_packet_deny_s
{
	TSK_DECLARE_OBJECT;
	tmcptt_mcptt_packet_specific_binary_16_txt_t*	reject_cause;
	tmcptt_mcptt_packet_specific_txt_t*				user_id;                   //Only in off-network
	tmcptt_mcptt_packet_specific_binary_txt_ref_t*	track_info;                //Only when non-controlling is included
} tmcptt_mcptt_packet_deny_t;

TINYMCPTT_API tmcptt_mcptt_packet_deny_t* tmcptt_mcptt_packet_deny_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_deny_t* tmcptt_mcptt_packet_deny_deserialize(const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_deny_serialize_to(const tmcptt_mcptt_packet_deny_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_deny_get_size(const tmcptt_mcptt_packet_deny_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_DENY_H */