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
 * @file tmcptt_mbms_message.c
 * @brief MCPTT message.
 *
 */
#include "tinymcptt/tmcptt_mbms_message.h"

#include "tsk_string.h"
#include "tsk_memory.h"
#include "tsk_debug.h"


tmcptt_mbms_message_t* tmcptt_mbms_message_create_null()
{
	tmcptt_mbms_message_t* message;
	if((message = (tmcptt_mbms_message_t*)tsk_object_new(tmcptt_mbms_message_def_t))){
		message->group_id = tsk_null;
		message->tmgi = tsk_null;
		message->media_ip = tsk_null;
		message->media_port = 0;
		message->media_control_port = 0;
	}
	return message;
}


//=================================================================================================
//	MCPTT object definition
//
static void* tmcptt_mbms_message_ctor(tsk_object_t * self, va_list * app)
{
	tmcptt_mbms_message_t *message = (tmcptt_mbms_message_t *)self;
	if(message){
	}
	return self;
}

static void* tmcptt_mbms_message_dtor(tsk_object_t * self)
{ 
	tmcptt_mbms_message_t *message = (tmcptt_mbms_message_t *)self;
	if(message){
		if (message->group_id != tsk_null)
			TSK_FREE(message->group_id);
		if (message->tmgi != tsk_null)
			TSK_FREE(message->tmgi);
		if (message->media_ip != tsk_null)
			TSK_FREE(message->media_ip);
	}
	return self;
}

static int tmcptt_mbms_message_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return -1;
}

static const tsk_object_def_t tmcptt_mbms_message_def_s = 
{
	sizeof(tmcptt_mbms_message_t),
	tmcptt_mbms_message_ctor,
	tmcptt_mbms_message_dtor,
	tmcptt_mbms_message_cmp, 
};
const tsk_object_def_t *tmcptt_mbms_message_def_t = &tmcptt_mbms_message_def_s;
