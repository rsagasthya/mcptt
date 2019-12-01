#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
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
 * @file tmcptt_event.c
 * @brief MCPTT Event
 * 
 */
#include "tinymcptt/tmcptt_event.h"

#include "tsk_debug.h"

tmcptt_event_t* tmcptt_event_create(const void* callback_data, tmcptt_event_type_t type, tmcptt_message_t* message)
{
	tmcptt_event_t* _event;
	if((_event = (tmcptt_event_t*)tsk_object_new(tmcptt_event_def_t))){
		_event->callback_data = callback_data;
		_event->type = type;
		_event->message = (tmcptt_message_t*)tsk_object_ref(message);
	}
	else{
		TSK_DEBUG_ERROR("Failed to create new MCPTT event");
	}

	return _event;
}

//========================================================
//	MCPTT Event definition
//

/**@ingroup tmcptt_event_group
*/
static tsk_object_t* tmcptt_event_ctor(tsk_object_t *self, va_list * app)
{
	tmcptt_event_t *_event = (tmcptt_event_t *)self;
	if(_event){
	}
	else{
		TSK_DEBUG_ERROR("Failed to create new MCPTT Event");
	}
	return self;
}

/**@ingroup tmcptt_event_group
*/
static tsk_object_t* tmcptt_event_dtor(tsk_object_t *self)
{
	tmcptt_event_t *_event = (tmcptt_event_t *)self;
	if(_event){
		TSK_OBJECT_SAFE_FREE(_event->message);
	}
	else{
		TSK_DEBUG_ERROR("Null MCPTT Event");
	}

	return self;
}

static const tsk_object_def_t tmcptt_event_def_s = 
{
	sizeof(tmcptt_event_t),
	tmcptt_event_ctor,
	tmcptt_event_dtor,
	tsk_null
};
const tsk_object_def_t* tmcptt_event_def_t = &tmcptt_event_def_s;
