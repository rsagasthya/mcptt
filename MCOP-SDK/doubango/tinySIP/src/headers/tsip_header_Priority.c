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


#include "tinysip/headers/tsip_header_Priority.h"

#include "tsk_debug.h"
#include "tsk_memory.h"
#include "tsk_time.h"

#include <string.h>

tsip_header_Priority_t* tsip_header_Priority_create()
{
	return tsk_object_new(tsip_header_Priority_def_t);
}

int tsip_header_Priority_serialize(const tsip_header_t* header, tsk_buffer_t* output)
{
	if(header){
		const tsip_header_Priority_t *Priority = (const tsip_header_Priority_t *)header;
		if(Priority){
			return 0;
		}
	}
	return -1;
}

tsip_header_Priority_t *tsip_header_Priority_parse(const char *data, tsk_size_t size)
{
	tsip_header_Priority_t *sip_hdr = 0;
		
	return sip_hdr;
}







//========================================================
//	Priority header object definition
//

static tsk_object_t* tsip_header_Priority_ctor(tsk_object_t *self, va_list * app)
{
	tsip_header_Priority_t *Priority = self;
	if(Priority){
		TSIP_HEADER(Priority)->type = tsip_htype_Priority;
		TSIP_HEADER(Priority)->serialize = tsip_header_Priority_serialize;
	}
	else{
		TSK_DEBUG_ERROR("Failed to create new Priority header.");
	}
	return self;
}

static tsk_object_t* tsip_header_Priority_dtor(tsk_object_t *self)
{
	tsip_header_Priority_t *Priority = self;
	if(Priority){

		TSK_OBJECT_SAFE_FREE(TSIP_HEADER_PARAMS(Priority));
	}
	else{
		TSK_DEBUG_ERROR("Null Priority header.");
	}

	return self;
}

static const tsk_object_def_t tsip_header_Priority_def_s = 
{
	sizeof(tsip_header_Priority_t),
	tsip_header_Priority_ctor,
	tsip_header_Priority_dtor,
	tsk_null
};
const tsk_object_def_t *tsip_header_Priority_def_t = &tsip_header_Priority_def_s;