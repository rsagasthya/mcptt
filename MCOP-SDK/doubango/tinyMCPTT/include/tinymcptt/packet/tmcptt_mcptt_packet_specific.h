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

#ifndef TINYMCPTT_MCPTT_PACKET_SPECIFIC_H
#define TINYMCPTT_MCPTT_PACKET_SPECIFIC_H

#include "tinymcptt_config.h"
#include "tinymcptt/packet/tmcptt_mcptt_packet.h"

#include <stdint.h>

#include "tsk_object.h"
#include "tsk_memory.h"
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_32_MIN_SIZE			8
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_MIN_SIZE			4
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_MIN_SIZE			4
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_8_TXT_MIN_SIZE		3
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_16_TXT_MIN_SIZE		4
#define TMCPTT_MCPTT_PACKET_SPECIFIC_TXT_MIN_SIZE				2
#define TMCPTT_MCPTT_PACKET_SPECIFIC_SRCC_MIN_SIZE				2
#define TMCPTT_MCPTT_PACKET_SPECIFIC_BINARY_TXT_REF_MIN_SIZE	4

TMCPTT_BEGIN_DECLS
	
/* MCPTT Specific fields */
typedef struct tmcptt_mcptt_packet_specific_binary_s {
	TSK_DECLARE_OBJECT;

	uint8_t  f_id;
	uint8_t  f_length;
	uint8_t  f_h_value;
	uint8_t  f_l_value;
} tmcptt_mcptt_packet_specific_binary_t;

typedef struct tmcptt_mcptt_packet_specific_binary_16_s {
	TSK_DECLARE_OBJECT;

	uint8_t f_id;
	uint8_t f_length;
	uint16_t f_value;
} tmcptt_mcptt_packet_specific_binary_16_t;





typedef struct tmcptt_mcptt_packet_specific_txt_s {
	TSK_DECLARE_OBJECT;

	uint8_t f_id;
	uint8_t f_length;
	char* f_value;
} tmcptt_mcptt_packet_specific_txt_t;

typedef struct tmcptt_mcptt_packet_specific_ssrc_s {
	TSK_DECLARE_OBJECT;

	uint8_t f_id;
	uint8_t f_length;
	uint32_t f_value;
} tmcptt_mcptt_packet_specific_ssrc_t;

typedef struct tmcptt_mcptt_packet_specific_binary_8_txt_s {
	TSK_DECLARE_OBJECT;

	uint8_t  f_id;
	uint8_t  f_length;
	uint8_t  f_bin_value;
	char*    f_txt_value;
} tmcptt_mcptt_packet_specific_binary_8_txt_t;

typedef struct tmcptt_mcptt_packet_specific_binary_16_txt_s {
	TSK_DECLARE_OBJECT;

	uint8_t  f_id;
	uint8_t  f_length;
	uint16_t f_bin_value;
	char*    f_txt_value;
} tmcptt_mcptt_packet_specific_binary_16_txt_t;

typedef struct tmcptt_mcptt_packet_specific_binary_txt_ref_s {
	TSK_DECLARE_OBJECT;
	
	uint8_t   f_id;
	uint8_t   f_length;
	uint8_t   f_bin_h_value;
	uint8_t   f_bin_l_value;
	char*     f_txt_value;
	uint8_t   num_refs;
	uint32_t* f_refs;
} tmcptt_mcptt_packet_specific_binary_txt_ref_t;

TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_t* tmcptt_mcptt_packet_specific_binary_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_t* tmcptt_mcptt_packet_specific_binary_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_binary_serialize_to(const tmcptt_mcptt_packet_specific_binary_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_get_size(const tmcptt_mcptt_packet_specific_binary_t* self);

TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_16_t* tmcptt_mcptt_packet_specific_binary_16_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_16_t* tmcptt_mcptt_packet_specific_binary_16_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_binary_16_serialize_to(const tmcptt_mcptt_packet_specific_binary_16_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_16_get_size(const tmcptt_mcptt_packet_specific_binary_16_t* self);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_32_get_size(const tmcptt_mcptt_packet_specific_ssrc_t* self);


TINYMCPTT_API tmcptt_mcptt_packet_specific_txt_t* tmcptt_mcptt_packet_specific_txt_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_txt_t* tmcptt_mcptt_packet_specific_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_txt_serialize_to(const tmcptt_mcptt_packet_specific_txt_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_txt_get_size(const tmcptt_mcptt_packet_specific_txt_t* self);

//INIT new SRCC
TINYMCPTT_API tmcptt_mcptt_packet_specific_ssrc_t* tmcptt_mcptt_packet_specific_ssrc_deserialize(const uint8_t fid, const void* data, tsk_size_t _size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_ssrc_serialize_to(const tmcptt_mcptt_packet_specific_ssrc_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tmcptt_mcptt_packet_specific_ssrc_t* tmcptt_mcptt_packet_specific_ssrc_create_null();
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_ssrc_get_size(const tmcptt_mcptt_packet_specific_ssrc_t* self);
//END new SRCC

TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_8_txt_t* tmcptt_mcptt_packet_specific_binary_8_txt_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_8_txt_t* tmcptt_mcptt_packet_specific_binary_8_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_binary_8_txt_serialize_to(const tmcptt_mcptt_packet_specific_binary_8_txt_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_8_txt_get_size(const tmcptt_mcptt_packet_specific_binary_8_txt_t* self);

TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_16_txt_t* tmcptt_mcptt_packet_specific_binary_16_txt_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_16_txt_t* tmcptt_mcptt_packet_specific_binary_16_txt_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_binary_16_txt_serialize_to(const tmcptt_mcptt_packet_specific_binary_16_txt_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_16_txt_get_size(const tmcptt_mcptt_packet_specific_binary_16_txt_t* self);

TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_txt_ref_t* tmcptt_mcptt_packet_specific_binary_txt_ref_create_null();
TINYMCPTT_API tmcptt_mcptt_packet_specific_binary_txt_ref_t* tmcptt_mcptt_packet_specific_binary_txt_ref_deserialize(const uint8_t fid, const void* data, tsk_size_t size);
TINYMCPTT_API int tmcptt_mcptt_packet_specific_binary_txt_ref_serialize_to(const tmcptt_mcptt_packet_specific_binary_txt_ref_t* self, void* data, tsk_size_t size);
TINYMCPTT_API tsk_size_t tmcptt_mcptt_packet_specific_binary_txt_ref_get_size(const tmcptt_mcptt_packet_specific_binary_txt_ref_t* self);

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MCPTT_PACKET_SPECIFIC_H */