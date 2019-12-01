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


/**@file tsip_api_subscribe.c
 * @brief Public subscription (SUBSCRIBE) functions.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include "tinysip/api/tsip_api_subscribe.h"

#include "tinysip/dialogs/tsip_dialog_layer.h"
#include "tinysip/dialogs/tsip_dialog_subscribe.h"

#include "tinysip/tsip_action.h"
#include "tsip.h"

#include "tsk_runnable.h"
#include "tsk_debug.h"

#define TSIP_SUBSCRIBE_EVENT_CREATE( type)		tsk_object_new(tsip_subscribe_event_def_t, type)

extern tsip_action_t* _tsip_action_create(tsip_action_type_t type, va_list* app);

int tsip_subscribe_event_signal(tsip_subscribe_event_type_t type, tsip_ssession_t* ss, short status_code, const char *phrase, const tsip_message_t* sipmessage)
{
	tsip_subscribe_event_t* sipevent = TSIP_SUBSCRIBE_EVENT_CREATE(type);
	
	if((((tsip_ssession_t*)ss)->media.type & tmedia_affiliation) == tmedia_affiliation ){
		TSK_DEBUG_INFO("Send notify to user of affiliation.");
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_subscribe_affiliation);
	}else if((((tsip_ssession_t*)ss)->media.type & tmedia_cms) == tmedia_cms ){
		TSK_DEBUG_INFO("Send notify to user of CMS.");
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_subscribe_cms);
	}else if((((tsip_ssession_t*)ss)->media.type & tmedia_gms) == tmedia_gms ){
		TSK_DEBUG_INFO("Send notify to user of GMS.");
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_subscribe_gms);
	}else{
	tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_subscribe);
	}
	
	

	TSK_RUNNABLE_ENQUEUE_OBJECT(TSK_RUNNABLE(TSIP_SSESSION(ss)->stack), sipevent);

	return 0;
}

int tsip_api_subscribe_send_subscribe(const tsip_ssession_handle_t *ss, ...)
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
	if((action = _tsip_action_create(tsip_atype_subscribe, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_SUBSCRIBE, ss);
		}
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}

int tsip_api_subscribe_send_unsubscribe(const tsip_ssession_handle_t *ss, ...)
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
	if((action = _tsip_action_create(tsip_atype_unsubscribe, &ap))){
		ret = tsip_ssession_handle(ss, action);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return 0;
}

//MCPTT AFFILIATION
int tsip_api_subscribe_affiliation_send_subscribe(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;
	//Session is Affiliation mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_affiliation;
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
	if((action = _tsip_action_create(tsip_atype_subscribe, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_SUBSCRIBE, ss);
		}
		//send subscribe.
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}



//MCPTT AFFILIATION
int tsip_api_subscribe_affiliation_unsubscribe(const tsip_ssession_handle_t *ss, ...)
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

	if((action = _tsip_action_create(tsip_atype_unsubscribe, &ap))){
		
		ret = tsip_ssession_handle(ss, action);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return 0;
}

//MCPTT CMS
int tsip_api_subscribe_cms_send_subscribe(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;
	//Session is Affiliation mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_cms;
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
	if((action = _tsip_action_create(tsip_atype_subscribe, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_SUBSCRIBE, ss);
		}
		//send subscribe.
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);

		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}



//MCPTT AFFILIATION
int tsip_api_subscribe_cms_unsubscribe(const tsip_ssession_handle_t *ss, ...)
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

	if((action = _tsip_action_create(tsip_atype_unsubscribe, &ap))){

		ret = tsip_ssession_handle(ss, action);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return 0;
}


//MCPTT CMS
int tsip_api_subscribe_gms_send_subscribe(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;
	//Session is Affiliation mcptt
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_gms;
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
	if((action = _tsip_action_create(tsip_atype_subscribe, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_SUBSCRIBE, ss);
		}
		//send subscribe.
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);

		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}



//MCPTT AFFILIATION
int tsip_api_subscribe_gms_unsubscribe(const tsip_ssession_handle_t *ss, ...)
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

	if((action = _tsip_action_create(tsip_atype_unsubscribe, &ap))){

		ret = tsip_ssession_handle(ss, action);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return 0;
}


//========================================================
//	SIP SUBSCRIBE event object definition
//
static tsk_object_t* tsip_subscribe_event_ctor(tsk_object_t * self, va_list * app)
{
	tsip_subscribe_event_t *sipevent = self;
	if(sipevent){
		sipevent->type = va_arg(*app, tsip_subscribe_event_type_t);
	}
	return self;
}

static tsk_object_t* tsip_subscribe_event_dtor(tsk_object_t * self)
{ 
	tsip_subscribe_event_t *sipevent = self;
	if(sipevent){
		tsip_event_deinit(TSIP_EVENT(sipevent));
	}
	return self;
}

static int tsip_subscribe_event_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return -1;
}

static const tsk_object_def_t tsip_subscribe_event_def_s = 
{
	sizeof(tsip_subscribe_event_t),
	tsip_subscribe_event_ctor, 
	tsip_subscribe_event_dtor,
	tsip_subscribe_event_cmp, 
};
const tsk_object_def_t *tsip_subscribe_event_def_t = &tsip_subscribe_event_def_s;
