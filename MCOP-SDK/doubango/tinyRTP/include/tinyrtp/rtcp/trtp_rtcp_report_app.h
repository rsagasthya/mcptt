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

#ifndef TINYRTP_RTCP_REPORT_APP_H
#define TINYRTP_RTCP_REPORT_APP_H

#include "tinyrtp_config.h"

#include "tinyrtp/rtcp/trtp_rtcp_packet.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_fb.h"



typedef struct trtp_rtcp_report_app_s
{
	TRTP_DECLARE_RTCP_PACKET;
	uint32_t subtype;
	char name[4];
	uint32_t ssrc;
	uint8_t* payload;
	uint16_t payload_size; 
}
trtp_rtcp_report_app_t;

TINYRTP_API trtp_rtcp_report_app_t* trtp_rtcp_report_app_create_null();
TINYRTP_API trtp_rtcp_report_app_t* trtp_rtcp_report_app_create(struct trtp_rtcp_header_s* header);
TINYRTP_API trtp_rtcp_report_app_t* trtp_rtcp_report_app_create_2(const char* app_name, uint8_t subtype, uint32_t ssrc, const char* data, tsk_size_t size);
TINYRTP_API trtp_rtcp_report_app_t* trtp_rtcp_report_app_deserialize(const void* data, tsk_size_t size);
TINYRTP_API tsk_size_t trtp_rtcp_report_app_get_size(const trtp_rtcp_report_app_t* self);
TINYRTP_API int trtp_rtcp_report_app_serialize_to(const trtp_rtcp_report_app_t* self, void* data, tsk_size_t size);

#endif /* TINYRTP_RTCP_REPORT_APP_H */
