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

/*
 * @file tmcptt_event.h
 * @brief MCPTT Event.
 *
 */

#ifndef TINYMCPTT_EVENT_H
#define TINYMCPTT_EVENT_H

#include "tinymcptt_config.h"

#include "tinymcptt/tmcptt_message.h"

#include "tsk_params.h"
#include "tsk_buffer.h"

TMCPTT_BEGIN_DECLS

typedef enum tmcptt_event_type_e
{
	tmcptt_event_type_none,
	tmcptt_event_type_token_granted,
	tmcptt_event_type_idle_channel,
	tmcptt_event_type_token_taken,
	tmcptt_event_type_request_sent,
	tmcptt_event_type_release_sent,
	tmcptt_event_type_token_denied,
	tmcptt_event_type_permission_revoked,
	tmcptt_event_type_queued,
	tmcptt_event_type_queued_timeout,
	tmcptt_event_type_queue_pos_request_sent
}
tmcptt_event_type_t;
//mcptt
typedef struct tmcptt_event_s
{
	TSK_DECLARE_OBJECT;

	const void* callback_data;

	tmcptt_event_type_t type;
	tmcptt_message_t* message;
}
tmcptt_event_t;



typedef int (*tmcptt_event_cb_f)(tmcptt_event_t* _event);

TINYMCPTT_API tmcptt_event_t* tmcptt_event_create(const void* callback_data, tmcptt_event_type_t type, tmcptt_message_t* message);

TINYMCPTT_GEXTERN const tsk_object_def_t *tmcptt_event_def_t;

TMCPTT_END_DECLS

#endif /* TINYMCPTT_EVENT_H */
