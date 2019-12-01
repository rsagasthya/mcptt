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

#ifndef TINYMCPTT_MCPTT_PACKET_H
#define TINYMCPTT_MCPTT_PACKET_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet_specific.h"

#define MCPTT_PROTO_NAME "MCPT"

TMCPTT_BEGIN_DECLS

/* MCPTT message type header field */

typedef enum {
	MCPTT_REQUEST				= 0,  //0b00000,
	MCPTT_GRANTED				= 1,  //0b00001,
	MCPTT_GRANTED_ACK			= 17, //0b10001,
	MCPTT_DENY					= 3,  //0b00011,
	MCPTT_DENY_ACK				= 19, //0b10011,
	MCPTT_RELEASE				= 4,  //0b00100,
	MCPTT_RELEASE_ACK			= 20, //0b10100,
	MCPTT_IDLE					= 5,  //0b00101,
	MCPTT_IDLE_ACK				= 21, //0b10101,
	MCPTT_TAKEN					= 2,  //0b00010,
	MCPTT_TAKEN_ACK				= 18, //0b10010,
	MCPTT_REVOKE				= 6,  //0b00110,
	MCPTT_QUEUE_POS_REQ			= 8,  //0b01000,
	MCPTT_QUEUE_POS_INFO		= 9,  //0b01001,
	MCPTT_QUEUE_POS_INFO_ACK	= 25, //0b11001,
	MCPTT_ACK					= 10, //0b01010
	MCPTT_ACK_ACK				= 26, //0b11010
} tmcptt_mcptt_packet_type_t;

/* MCPTT specific field ids */
typedef enum {
  FID_FLOOR_PRIORITY       = 0x000, // =0b00000000,
  FID_DURATION             = 0x001, // =0b00000001,
  FID_REJECT_CAUSE         = 0x002, // =0b00000010,
  FID_QUEUE_INFO           = 0x003, // =0b00000011,
  FID_GRANTED_PARTY_ID     = 0x004, // =0b00000100,
  FID_FLOOR_REQ_PERMISSION = 0x005, // =0b00000101,
  FID_USER_ID              = 0x006, // =0b00000110,
  FID_QUEUE_SIZE           = 0x007, // =0b00000111,
  FID_MSG_SEQ_NUMBER       = 0x008, // =0b00001000,
  FID_QUEUED_USER_ID       = 0x009, // =0b00001001,
  FID_SOURCE               = 0x00a, // =0b00001010,
  FID_TRACK_INFO           = 0x00b, // =0b00001011,
  FID_MESSAGE_TYPE         = 0x00c, // =0b00001100,
  FID_FLOOR_INDICATOR      = 0x00d, // =0b00001101,
  FID_SSRC     = 0x00e, // =0b00001110,

  FID_FLOOR_PRIORITY_OLD   =0x66,//= 102, // =0b01100110,
  FID_DURATION_OLD         =0x67,//= 103, // =0b01100111,
  FID_REJECT_CAUSE_OLD     =0x68,//= 104, // =0b01101000,
  FID_QUEUE_INFO_OLD       =0x69,// = 105, // =0b01101001,
  FID_GRANTED_PARTY_ID_OLD =0x6a,//= 106, // =0b01101010,
  FID_FLOOR_REQ_PERMISSION_OLD =0x6c,//= 108, // =0b01101100,
  FID_USER_ID_OLD             =0x6d,//= 109, // =0b01101101,
  FID_QUEUE_SIZE_OLD           =0x6e,//= 110, // =0b01101110,
  FID_MSG_SEQ_NUMBER_OLD       =0x6f,//= 111, // =0b01101111,
  FID_QUEUED_USER_ID_OLD   =0x70, //= 112, // =0b01110000,
  FID_SOURCE_OLD         =0x71,// = 113, // =0b01110001,
  FID_TRACK_INFO_OLD     =0x72,//  = 114, // =0b01110010,
  FID_MESSAGE_TYPE_OLD   =0x73,//  = 115, // =0b01110011
  FID_FLOOR_INDICATOR_OLD =0x74// = 116 // =0b01110100
} tmcptt_mcptt_packet_fid_t;


//Old version SSRC
//typedef tmcptt_mcptt_packet_specific_ssrc_t tmcptt_mcptt_packet_ssrc_t;

/* Some constant values */
typedef enum {
  FLR_SOURCE_PARTICIPANT     = 0,
  FLR_SOURCE_PARTICIPATING   = 1,
  FLR_SOURCE_CONTROLLING     = 2,
  FLR_SOURCE_NON_CONTROLLING = 3
} tmcptt_mcptt_packet_source_value_t;

typedef enum {
  PERMISSION_TO_REQ_NOT_PERMITTED = 0,
  PERMISSION_TO_REQ_PERMITTED     = 1
} tmcptt_mcptt_packet_permission_to_request_t;

typedef enum {
  QUEUEING_NOT_SUPPORTED = 0,
  QUEUEING_SUPPORTED     = 1
} tmcptt_mcptt_packet_queueing_capability_t;

typedef enum {
  FLR_IND_NORMAL_CALL         = 0x8000, //0b1000000000000000,
  FLR_IND_BROADCAST_GRP_CALL  = 0x4000, //0b0100000000000000,
  FLR_IND_SYSTEM_CALL         = 0x2000, //0b0010000000000000,
  FLR_IND_EMERGENCY_CALL      = 0x1000, //0b0001000000000000,
  FLR_IND_IMMINENT_PERIL_CALL = 0x0800, //0b0000100000000000,
  FLR_IND_QUEUEING_SUPPORTED  = 0x0400, //0b0000010000000000
  FLR_IND_DUAL_FLOOR          = 0x0200  //0b0000001000000000
} tmcptt_mcptt_packet_floor_indicator_t;

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_H */