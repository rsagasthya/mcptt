/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source Doubango Framework
* Copyright (C) 2010-2011 Mamadou Diop.
* Copyright (C) 2012 Doubango Telecom <http://doubango.org>
*
* This file is part of Open Source Doubango Framework.
*
* DOUBANGO is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* DOUBANGO is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with DOUBANGO.
*
*/
#include "DRegisterCallback.h"

#include "SipStack.h"

#include "Common.h"




tsk_buffer_t* DRegisterCallback::auth_register_cb(const void* arg, const char* fmt, const char* testMessage)
{
	int ret = -1;
	tsk_buffer_t* dataReturn=tsk_null;
	int response=-1;
	if(!arg){
		return tsk_null;
	}

	const SipStack* stack = dyn_cast<const SipStack*>((const SipStack*)arg);

 	if(stack && stack->getRegisterCallback()){
		char* message = tsk_null;
		
		tsk_sprintf(&message,fmt,testMessage);
		
				TSK_DEBUG_INFO("Authentication command: %s size: %i",(char *)stack->getdataRegisterCallback(),stack->lengthDataRegisterCallback());
				if(message && message!=tsk_null)
				ret=(stack->getRegisterCallback()) ? stack->getRegisterCallback()->onAuthRegister(message) : -1;
				if(ret && ret>0 && 
					stack->getdataRegisterCallback() && 
					stack->lengthDataRegisterCallback() && 
					stack->lengthDataRegisterCallback()>0){

					void* data=tsk_null;
					int len_buff_char=0;
					len_buff_char=stack->lengthDataRegisterCallback();
					#if HAVE_CRT //Debug memory
					data=malloc((len_buff_char));
					#else
					data=tsk_malloc((len_buff_char));
					#endif //HAVE_CRT
					memset( data, '\0',(len_buff_char));
					memcpy(data,stack->getdataRegisterCallback(),(len_buff_char));					
					dataReturn=tsk_buffer_create(data,(len_buff_char));
				}else{
					TSK_DEBUG_WARN("The response from the authentication server was not expected, or communication between the service and the client is not correct");
					dataReturn=tsk_buffer_create("",0);
				}
				
				
				

				
		
		TSK_FREE(message);
	}

	return dataReturn;
}




