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




#include "tinysak_config.h"
#include <stdio.h>
#include "tsk_buffer.h"




	typedef tsk_buffer_t* (*tsk_register_f)(const void* arg, const char* fmt,const char* testMessage);

	/* INFO */
#define TSK_REGISTER(FMT, testMessage)		\
		if(tsk_register_get_cb()) \
			tsk_register_get_cb()(tsk_debug_get_arg_data(), "*INFO: " FMT "\n", testMessage); \
		else \
			fprintf(stderr, "*INFO: " FMT "\n", testMessage); 
	



TINYSAK_API void tsk_register_set_arg_data(const void*);
TINYSAK_API const void* tsk_register_get_arg_data();
TINYSAK_API void tsk_register_set_cb(tsk_register_f );
TINYSAK_API tsk_register_f tsk_register_get_cb();



