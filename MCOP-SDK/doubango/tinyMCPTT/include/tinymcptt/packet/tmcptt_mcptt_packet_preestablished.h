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

#ifndef TINYMCPTT_MCPTT_PACKET_PREESTABLISHED_H
#define TINYMCPTT_MCPTT_PACKET_PREESTABLISHED_H

#include "tinymcptt_config.h"

#define MCPTT_PREESTABLISHED_PROTO_NAME "MCPC"

TMCPTT_BEGIN_DECLS

/* MCPTT pre-established session message type header field */

typedef enum {
	MCPTT_CONNECT				= 0,  //0b00000,
	MCPTT_CONNECT_ACK			= 16, //0b10000,
	MCPTT_DISCONNECT			= 1,  //0b00001,
	MCPTT_DISCONNECT_ACK		= 17, //0b10001,
	MCPTT_PREESTABLISHED_ACK	= 2   //0b00010
} tmcptt_mcptt_packet_preestablished_type_t;

/* MCPTT specific field ids */
typedef enum {
  FID_MEDIA_STREAMS			= 000,
  FID_MCPTT_SESSION_ID		= 001,
  FID_WARNING_TEXT			= 002,
  FID_MCPTT_GROUP_ID		= 003,
  FID_ANSWER_STATE			= 004,
  FID_INVITING_USER_ID		= 005,
  FID_REASON_CODE			= 006
} tmcptt_mcptt_packet_preestablished_fid_t;

/* Some constant values */
typedef enum {
  SESSION_TYPE_NONE			= 0, //0b00000000,
  SESSION_TYPE_PRIVATE		= 1, //0b00000001,
  SESSION_TYPE_PREARRANGED	= 3, //0b00000011,
  SESSION_TYPE_CHAT			= 4  //0b00000100
} tmcptt_mcptt_packet_preestablished_session_type_t;

typedef enum {
  ANSWER_STATE_UNCONFIRMED = 0,
  ANSWER_STATE_CONFIRMED   = 1
} tmcptt_mcptt_packet_preestablished_answer_state_t;

typedef enum {
  REASON_CODE_ACCEPTED     = 0,
  REASON_CODE_BUSY         = 1,
  REASON_CODE_NOT_ACCEPTED = 2
} tmcptt_mcptt_packet_preestablished_reason_code_t;

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PREESTABLISHED_PACKET_H */