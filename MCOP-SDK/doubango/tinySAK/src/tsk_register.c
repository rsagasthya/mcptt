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

/**@file tsk_debug.c
 * @brief Utility functions for debugging purpose.
 */
#include "tsk_register.h"

/**@defgroup tsk_debug_group Utility functions for debugging purpose.
*/


static const void* tsk_register_arg_data = tsk_null;
static tsk_register_f tsk_register_cb = tsk_null;


void tsk_register_set_arg_data(const void* arg_data){
	tsk_register_arg_data = arg_data;
}

const void* tsk_register_get_arg_data(){
	return tsk_register_arg_data;
}


void tsk_register_set_cb(tsk_register_f cb){
	tsk_register_cb = cb;
}

tsk_register_f tsk_register_get_cb(){
	return tsk_register_cb;
}
