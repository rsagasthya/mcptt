#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
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


/**@file tsip_api_publish.c
 * @brief Public subscription (PUBLISH) functions.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include "tinysip/api/tsip_api_publish.h"

#include "tinysip/dialogs/tsip_dialog_layer.h"
#include "tinysip/dialogs/tsip_dialog_publish.h"

#include "tsip.h"

#include "tsk_runnable.h"
#include "tsk_debug.h"

#define TSIP_PUBLISH_EVENT_CREATE( type)		tsk_object_new(tsip_publish_event_def_t, type)

extern tsip_action_t* _tsip_action_create(tsip_action_type_t type, va_list* app);

int tsip_publish_event_signal(tsip_publish_event_type_t type, tsip_ssession_handle_t* ss, short status_code, const char *phrase, const tsip_message_t* sipmessage)
{
	tsip_publish_event_t* sipevent = TSIP_PUBLISH_EVENT_CREATE(type);

	if((((tsip_ssession_t*)ss)->media.type & tmedia_affiliation) == tmedia_affiliation){
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_publish_affiliation);
	}else{
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_publish);
	}
	

	TSK_RUNNABLE_ENQUEUE_OBJECT(TSK_RUNNABLE(TSIP_SSESSION(ss)->stack), sipevent);

	return 0;
}

int tsip_api_publish_send_publish(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_publish, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_PUBLISH, ss);
		}
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}




int tsip_api_publish_send_unpublish(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	/* Checks if the stack is running */
	if(!TSK_RUNNABLE(_ss->stack)->running){
		TSK_DEBUG_ERROR("Stack not running.");
		return -2;
	}
	
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_unpublish, &ap))){
		ret = tsip_ssession_handle(ss, action);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return 0;
}

//MCPTT Affiliation
int tsip_api_publish_send_publish_affiliation(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	//Session is Affiliation mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_affiliation;

	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_publish, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_PUBLISH, ss);
		}
		TSIP_DIALOG_PUBLISH(dialog)->unpublishingAffiliationAndAuthentication=tsk_false;
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}

//MCPTT Affiliation
int tsip_api_publish_send_unpublish_affiliation(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	//Session is Affiliation mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_affiliation;
	
	// Checks if the stack has been started 
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	
	va_start(ap, ss);

	if((action = _tsip_action_create(tsip_atype_publish, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_PUBLISH, ss);
		}
		TSIP_DIALOG_PUBLISH(dialog)->unpublishingAffiliationAndAuthentication=tsk_true;//Uso on publish affiliation
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}

//MCPTT Idms send token in publish
int tsip_api_publish_send_publish_authentication(const char* mcptt_info,const char* poc_settings,const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	//Session is authentication mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_authentication;

	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_publish, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_PUBLISH, ss);
		}
		TSIP_DIALOG_PUBLISH(dialog)->unpublishingAffiliationAndAuthentication=tsk_false;//Uso on publish authentication
		TSIP_DIALOG_PUBLISH(dialog)->mcptt_info_authentication=strdup(mcptt_info);
		TSIP_DIALOG_PUBLISH(dialog)->poc_settings_authentication=strdup(poc_settings);
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}
//MCPTT Idms send token in publish
int tsip_api_publish_send_unpublish_authentication(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}

	//Session is authentication mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_authentication;
	
	// Checks if the stack has been started 
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	
	va_start(ap, ss);

	if((action = _tsip_action_create(tsip_atype_publish, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_PUBLISH, ss);
		}
		TSIP_DIALOG_PUBLISH(dialog)->unpublishingAffiliationAndAuthentication=tsk_true;//Uso on publish authentication
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}








//========================================================
//	SIP PUBLISH event object definition
//
static tsk_object_t* tsip_publish_event_ctor(tsk_object_t * self, va_list * app)
	{
	tsip_publish_event_t *sipevent = self;
	if(sipevent){
		sipevent->type = va_arg(*app, tsip_publish_event_type_t);
	}
	return self;
}

static tsk_object_t* tsip_publish_event_dtor(tsk_object_t * self)
{ 
	tsip_publish_event_t *sipevent = self;
	if(sipevent){
		tsip_event_deinit(TSIP_EVENT(sipevent));
	}
	return self;
}

static int tsip_publish_event_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return -1;
}

static const tsk_object_def_t tsip_publish_event_def_s = 
{
	sizeof(tsip_publish_event_t),
	tsip_publish_event_ctor, 
	tsip_publish_event_dtor,
	tsip_publish_event_cmp, 
};
const tsk_object_def_t *tsip_publish_event_def_t = &tsip_publish_event_def_s;
