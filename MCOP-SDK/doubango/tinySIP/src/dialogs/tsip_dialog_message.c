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


/**@file tsip_dialog_message.c
 * @brief SIP dialog message (Client side).
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include "tinysip/dialogs/tsip_dialog_message.h"
#include "tinysip/parsers/tsip_parser_uri.h"

#include "tinysip/api/tsip_api_message.h"

#include "tinysip/headers/tsip_header_Dummy.h"
#include "tinysip/headers/tsip_header_Min_Expires.h"
#include "tinysdp/parsers/tsdp_parser_message.h"
#include "tinysip/transactions/tsip_transac_layer.h"
#include "tinymedia/content/tmedia_content_multipart.h"



#include "tsk_memory.h"
#include "tsk_debug.h"
#include "tsk_time.h"
#include "tsk_common.h"

#if HAVE_LIBXML2
#include <libxml/tree.h>
#include <libxml/parser.h>
#include <libxml/xpath.h>
#include <libxml/xpathInternals.h>
#include <tsip.h>
#include <tinysip.h>
#include <tinysip/tsip_ssession.h>

#endif

#define VALUE_CONTENT_TYPE_MESSAGE_MBMS "application/vnd.3gpp.mcptt-mbms-usage-info+xml"
#define VALUE_CONTENT_TYPE_MULTIPART "multipart/mixed"
#define DEBUG_STATE_MACHINE											1
#define TSIP_DIALOG_MESSAGE_SIGNAL(self, type, code, phrase, message)	\
	tsip_message_event_signal(type, TSIP_DIALOG(self)->ss, code, phrase, message)

/* ======================== internal functions ======================== */
static int send_MESSAGE(tsip_dialog_message_t *self);
static int receive_MESSAGE(tsip_dialog_message_t *self,const tsip_request_t *request);
static int tsip_dialog_message_OnTerminated(tsip_dialog_message_t *self);
static tsk_buffer_t* tsip_dialog_message_create_mcpttinfo(const tsip_dialog_message_t* self);



/*static*/ int tsip_dialog_message_msession_configure(tsip_dialog_message_t *self);
/* ======================== transitions ======================== */
static int tsip_dialog_message_Started_2_Sending_X_sendMESSAGE(va_list *app);
static int tsip_dialog_message_Started_2_Receiving_X_recvMESSAGE(va_list *app);
static int tsip_dialog_message_Sending_2_Sending_X_1xx(va_list *app);
static int tsip_dialog_message_Sending_2_Terminated_X_2xx(va_list *app);
static int tsip_dialog_message_Sending_2_Sending_X_401_407_421_494(va_list *app);
static int tsip_dialog_message_Sending_2_Terminated_X_300_to_699(va_list *app);
static int tsip_dialog_message_Sending_2_Terminated_X_cancel(va_list *app);
static int tsip_dialog_message_Receiving_2_Terminated_X_accept(va_list *app);
static int tsip_dialog_message_Receiving_2_Terminated_X_reject(va_list *app);
static int tsip_dialog_message_Any_2_Terminated_X_transportError(va_list *app);
static int tsip_dialog_message_Any_2_Terminated_X_Error(va_list *app);

/* ======================== conds ======================== */

/* ======================== actions ======================== */
typedef enum _fsm_action_e
{
	_fsm_action_sendMESSAGE = tsip_atype_message_send,
	_fsm_action_accept = tsip_atype_accept,
	_fsm_action_reject = tsip_atype_reject,
	_fsm_action_cancel = tsip_atype_cancel,
	_fsm_action_shutdown = tsip_atype_shutdown,
	_fsm_action_transporterror = tsip_atype_transport_error,

	_fsm_action_receiveMESSAGE = 0xFF,
	_fsm_action_1xx,
	_fsm_action_2xx,
	_fsm_action_401_407_421_494,
	_fsm_action_300_to_699,
	_fsm_action_error,
}
_fsm_action_t;

/* ======================== states ======================== */
typedef enum _fsm_state_e
{
	_fsm_state_Started,
	_fsm_state_Sending,
	_fsm_state_Receiving,
	_fsm_state_Terminated
}
_fsm_state_t;


static int tsip_dialog_message_event_callback(const tsip_dialog_message_t *self, tsip_dialog_event_type_t type, const tsip_message_t *msg)
{
	int ret = -1;

	switch(type)
	{
	case tsip_dialog_i_msg:
		{
			if(msg){
				if(TSIP_MESSAGE_IS_RESPONSE(msg)){
					const tsip_action_t* action = tsip_dialog_keep_action(TSIP_DIALOG(self), msg) ? TSIP_DIALOG(self)->curr_action : tsk_null;
					if(TSIP_RESPONSE_IS_1XX(msg)){
						ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_1xx, msg, action);
					}
					else if(TSIP_RESPONSE_IS_2XX(msg)){
						ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_2xx, msg, action);
					}
					else if(TSIP_RESPONSE_CODE(msg) == 401 || TSIP_RESPONSE_CODE(msg) == 407 || TSIP_RESPONSE_CODE(msg) == 421 || TSIP_RESPONSE_CODE(msg) == 494){
						ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_401_407_421_494, msg, action);
					}
					else if(TSIP_RESPONSE_IS_3456(msg)){
						ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_300_to_699, msg, action);
					}
					else{ /* Should never happen */
						ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_error, msg, action);
					}
				}
				else if (TSIP_REQUEST_IS_MESSAGE(msg)){ /* have been checked by dialog layer...but */
					// REQUEST ==> Incoming MESSAGE
					//
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_receiveMESSAGE, msg, tsk_null);
				}
			}
			break;
		}

	case tsip_dialog_canceled:
		{
			ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_cancel, msg, tsk_null);
			break;
		}

	case tsip_dialog_terminated:
	case tsip_dialog_timedout:
	case tsip_dialog_error:
	case tsip_dialog_transport_error:
		{
			ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_transporterror, msg, tsk_null);
			break;
		}
            
    default: break;
	}

	return ret;
}

tsip_dialog_message_t* tsip_dialog_message_create(const tsip_ssession_handle_t* ss)
{
	return tsk_object_new(tsip_dialog_message_def_t, ss);
}

int tsip_dialog_message_init(tsip_dialog_message_t *self)
{
	//const tsk_param_t* param;

	/* Initialize the state machine. */
	tsk_fsm_set(TSIP_DIALOG_GET_FSM(self),
			
			/*=======================
			* === Started === 
			*/
			// Started -> (send) -> Sending
			TSK_FSM_ADD_ALWAYS(_fsm_state_Started, _fsm_action_sendMESSAGE, _fsm_state_Sending, tsip_dialog_message_Started_2_Sending_X_sendMESSAGE, "tsip_dialog_message_Started_2_Sending_X_sendMESSAGE"),
			// Started -> (receive) -> Receiving
			TSK_FSM_ADD_ALWAYS(_fsm_state_Started, _fsm_action_receiveMESSAGE, _fsm_state_Receiving, tsip_dialog_message_Started_2_Receiving_X_recvMESSAGE, "tsip_dialog_message_Started_2_Receiving_X_recvMESSAGE"),
			// Started -> (Any) -> Started
			TSK_FSM_ADD_ALWAYS_NOTHING(_fsm_state_Started, "tsip_dialog_message_Started_2_Started_X_any"),
			

			/*=======================
			* === Sending === 
			*/
			// Sending -> (1xx) -> Sending
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_1xx, _fsm_state_Sending, tsip_dialog_message_Sending_2_Sending_X_1xx, "tsip_dialog_message_Sending_2_Sending_X_1xx"),
			// Sending -> (2xx) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_2xx, _fsm_state_Terminated, tsip_dialog_message_Sending_2_Terminated_X_2xx, "tsip_dialog_message_Sending_2_Terminated_X_2xx"),
			// Sending -> (401/407/421/494) -> Sending
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_401_407_421_494, _fsm_state_Sending, tsip_dialog_message_Sending_2_Sending_X_401_407_421_494, "tsip_dialog_message_Sending_2_Sending_X_401_407_421_494"),
			// Sending -> (300_to_699) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_300_to_699, _fsm_state_Terminated, tsip_dialog_message_Sending_2_Terminated_X_300_to_699, "tsip_dialog_message_Sending_2_Terminated_X_300_to_699"),
			// Sending -> (cancel) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_cancel, _fsm_state_Terminated, tsip_dialog_message_Sending_2_Terminated_X_cancel, "tsip_dialog_message_Sending_2_Terminated_X_cancel"),
			// Sending -> (shutdown) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Sending, _fsm_action_shutdown, _fsm_state_Terminated, tsk_null, "tsip_dialog_message_Sending_2_Terminated_X_shutdown"),
			// Sending -> (Any) -> Sending
			TSK_FSM_ADD_ALWAYS_NOTHING(_fsm_state_Sending, "tsip_dialog_message_Sending_2_Sending_X_any"),

			/*=======================
			* === Receiving === 
			*/
			// Receiving -> (accept) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Receiving, _fsm_action_accept, _fsm_state_Terminated, tsip_dialog_message_Receiving_2_Terminated_X_accept, "tsip_dialog_message_Receiving_2_Terminated_X_accept"),
			// Receiving -> (rejected) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Receiving, _fsm_action_reject, _fsm_state_Terminated, tsip_dialog_message_Receiving_2_Terminated_X_reject, "tsip_dialog_message_Receiving_2_Terminated_X_reject"),
			// Receiving -> (Any) -> Receiving
			TSK_FSM_ADD_ALWAYS_NOTHING(_fsm_state_Receiving, "tsip_dialog_message_Receiving_2_Receiving_X_any"),

			/*=======================
			* === Any === 
			*/
			// Any -> (transport error) -> Terminated
			TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_transporterror, _fsm_state_Terminated, tsip_dialog_message_Any_2_Terminated_X_transportError, "tsip_dialog_message_Any_2_Terminated_X_transportError"),
			// Any -> (transport error) -> Terminated
			TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_error, _fsm_state_Terminated, tsip_dialog_message_Any_2_Terminated_X_Error, "tsip_dialog_message_Any_2_Terminated_X_Error"),

			TSK_FSM_ADD_NULL());

	TSIP_DIALOG(self)->callback = TSIP_DIALOG_EVENT_CALLBACK_F(tsip_dialog_message_event_callback);

	return 0;
}


//--------------------------------------------------------
//				== STATE MACHINE BEGIN ==
//--------------------------------------------------------


/* Started -> (sendMESSAGE) -> Sending
*/
int tsip_dialog_message_Started_2_Sending_X_sendMESSAGE(va_list *app)
{
	tsip_dialog_message_t *self;

	self = va_arg(*app, tsip_dialog_message_t *);

	TSIP_DIALOG(self)->running = tsk_true;

	return send_MESSAGE(self);
}

/* Started -> (recvMESSAGE) -> Receiving
*/
int tsip_dialog_message_Started_2_Receiving_X_recvMESSAGE(va_list *app)
{
	int result=0;
	tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);
	const tsip_request_t *request = va_arg(*app, const tsip_request_t *);
    result=receive_MESSAGE(self,request);

	return result;
}

/*	Sending -> (1xx) -> Sending
*/
int tsip_dialog_message_Sending_2_Sending_X_1xx(va_list *app)
{
	/*tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);*/
	/*const tsip_response_t *response = va_arg(*app, const tsip_response_t *);*/

	return 0;
}

/*	Sending -> (2xx) -> Sending
*/
int tsip_dialog_message_Sending_2_Terminated_X_2xx(va_list *app)
{
	tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	/* Alert the user. */
	TSIP_DIALOG_MESSAGE_SIGNAL(self, tsip_ao_message, 
		TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);

	/* Reset curr action */
	tsip_dialog_set_curr_action(TSIP_DIALOG(self), tsk_null);

	return 0;
}

/*	Sending -> (401/407/421/494) -> Sending
*/
int tsip_dialog_message_Sending_2_Sending_X_401_407_421_494(va_list *app)
{
	tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);
	int ret;
	
	if((ret = tsip_dialog_update(TSIP_DIALOG(self), response))){
		// Alert the user
		TSIP_DIALOG_MESSAGE_SIGNAL(self, tsip_ao_message, 
								   TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);
		
		return ret;
	}
	
	return send_MESSAGE(self);
}

/*	Sending -> (300 to 699) -> Terminated
*/
int tsip_dialog_message_Sending_2_Terminated_X_300_to_699(va_list *app)
{
	tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	/* set last error (or info) */
	tsip_dialog_set_lasterror(TSIP_DIALOG(self), TSIP_RESPONSE_PHRASE(response), TSIP_RESPONSE_CODE(response));

	/* Alert the user. */
	TSIP_DIALOG_MESSAGE_SIGNAL(self, tsip_ao_message, 
		TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);

	return 0;
}

/*	Sending -> (cancel) -> Terminated
*/
int tsip_dialog_message_Sending_2_Terminated_X_cancel(va_list *app)
{
	tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);
	/* const tsip_message_t *message = va_arg(*app, const tsip_message_t *); */

	/* RFC 3261 - 9.1 Client Behavior
	   A CANCEL request SHOULD NOT be sent to cancel a request other than INVITE.
	*/

	/* Cancel all transactions associated to this dialog (will also be done when the dialog is destroyed (worth nothing)) */
	tsip_transac_layer_cancel_by_dialog(TSIP_DIALOG_GET_STACK(self)->layer_transac, TSIP_DIALOG(self));

	/* Alert the user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_request_cancelled, "MESSAGE cancelled");

	return 0;
}

/*	Receiving -> (accept) -> Terminated
*/
int tsip_dialog_message_Receiving_2_Terminated_X_accept(va_list *app)
{
	tsip_dialog_message_t *self;
	const tsip_action_t* action;

	self = va_arg(*app, tsip_dialog_message_t *);
	va_arg(*app, tsip_message_t *);
	action = va_arg(*app, const tsip_action_t *);
	
	if(!self->request){
		TSK_DEBUG_ERROR("There is non MESSAGE to accept()");
		/* Not an error ...but do not update current action */
	}
	else{
		tsip_response_t *response;
		int ret;

		/* curr_action is only used for outgoing requests */
		/* tsip_dialog_set_curr_action(TSIP_DIALOG(self), action); */

		/* send 200 OK */
		if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 200, "OK", self->request))){
			tsip_dialog_apply_action(response, action); /* apply action params to "this" response */
			if((ret = tsip_dialog_response_send(TSIP_DIALOG(self), response))){
				TSK_DEBUG_ERROR("Failed to send SIP response.");
				TSK_OBJECT_SAFE_FREE(response);
				return ret;
			}
			TSK_OBJECT_SAFE_FREE(response);
		}
		else{
			TSK_DEBUG_ERROR("Failed to create SIP response.");
			return -1;
		}
	}

	return 0;
}

/*	Receiving -> (reject) -> Terminated
*/
int tsip_dialog_message_Receiving_2_Terminated_X_reject(va_list *app)
{
	tsip_dialog_message_t *self;
	const tsip_action_t* action;

	self = va_arg(*app, tsip_dialog_message_t *);
	va_arg(*app, tsip_message_t *);
	action = va_arg(*app, const tsip_action_t *);
	
	if(!self->request){
		TSK_DEBUG_ERROR("There is non MESSAGE to reject()");
		/* Not an error ...but do not update current action */
	}
	else{
		tsip_response_t *response;
		int ret;

		/* curr_action is only used for outgoing requests */
		/* tsip_dialog_set_curr_action(TSIP_DIALOG(self), action); */

		/* send 486 Rejected */
		if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 486, "Rejected", self->request))){
			tsip_dialog_apply_action(response, action); /* apply action params to "this" response */
			if((ret = tsip_dialog_response_send(TSIP_DIALOG(self), response))){
				TSK_DEBUG_ERROR("Failed to send SIP response.");
				TSK_OBJECT_SAFE_FREE(response);
				return ret;
			}
			TSK_OBJECT_SAFE_FREE(response);
		}
		else{
			TSK_DEBUG_ERROR("Failed to create SIP response.");
			return -1;
		}
	}

	return 0;
}

/*	Any -> (transport error) -> Terminated
*/
int tsip_dialog_message_Any_2_Terminated_X_transportError(va_list *app)
{
	/*tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);*/
	/*const tsip_message_t *message = va_arg(*app, const tsip_message_t *);*/

	return 0;
}

/*	Any -> (error) -> Terminated
*/
int tsip_dialog_message_Any_2_Terminated_X_Error(va_list *app)
{
	/*tsip_dialog_message_t *self = va_arg(*app, tsip_dialog_message_t *);*/
	/*const tsip_message_t *message = va_arg(*app, const tsip_message_t *);*/

	return 0;
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				== STATE MACHINE END ==
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++

int send_MESSAGE(tsip_dialog_message_t *self)
{
	tsip_dialog_t* dialog_message=tsk_null;
	tsip_request_t* request = tsk_null;
	int ret = -1;
	char* type_Content_Type="Content-Type";
	char* value_Content_Type_Location="application/vnd.3gpp.mcptt-location-info+xml";
	char* value_Content_Type_Mbms="application/vnd.3gpp.mcptt-mbms-usage-info+xml";
	const tsip_header_t* hdr;
	tsk_buffer_t* mcptt_info;

	tmedia_multipart_body_t* body = tsk_null;
	tmedia_content_multipart_t* content = tsk_null;
	tmedia_content_multipart_t* mcptt_info_content = tsk_null;
	tsk_size_t i;
	char* content_type_hdr  = tsk_null;
	char* body_string  = tsk_null;
	tsk_buffer_t* body_buffer = tsk_buffer_create_null();
	tsk_bool_t control_accept_contact=tsk_false;
	if(!self){
		return -1;
	}

    if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt_group) != tmedia_mcptt_group){
		// MCPTT Private Call
        if(TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri==tsk_null){
            TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri=tsip_uri_tostring(TSIP_DIALOG_GET_SS(self)->to,tsk_false, tsk_false);
        }
    }else{
		// MCPTT Group Call
        if(TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_group_uri==tsk_null){
            TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_group_uri=tsip_uri_tostring(TSIP_DIALOG_GET_SS(self)->to,tsk_false, tsk_false);
        }

        if(TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri==tsk_null){
            TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri=tsip_uri_tostring(TSIP_DIALOG_GET_SS(self)->to,tsk_false, tsk_false);
        }
    }

	//MCPTT
	if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt) == tmedia_mcptt){
		//The request_uri is PSI in New Invite to MCPTT
		tsip_dialog_request_configure_mcptt(TSIP_DIALOG(self));
	}

	dialog_message=TSIP_DIALOG(self);
	if(!(request = tsip_dialog_request_new(dialog_message, "MESSAGE"))){
		return -2;
	}

	//Generate Header for mcptt_location message
	if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt) == tmedia_mcptt){
		TSIP_HEADER_ADD_PARAM(request->Contact, "+g.3gpp.icsi-ref", "\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mcptt\"");
			TSIP_HEADER_ADD_PARAM(request->Contact, "+g.3gpp.mcptt",tsk_null);
	}
	

	if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt_location) == tmedia_mcptt_location){
		//insert in head Content type
		//insert Context-Type
		//Create Body
		if(TSIP_DIALOG(self)->curr_action && TSIP_DIALOG(self)->curr_action->payload){
			tsip_message_add_content(request, value_Content_Type_Location, TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload), TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload));
		}
	}else 
		if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt_mbms) == tmedia_mcptt_mbms){

		//insert in head Content type
		//insert Context-Type
		//Create Body multipart/mixed

		for(i=0,control_accept_contact=tsk_false; (hdr = tsip_message_get_headerAt(request, tsip_htype_Dummy, i)); i++)
		{		
			const tsip_header_Dummy_t* dummy_hdr = (const tsip_header_Dummy_t*)hdr;

			if(tsk_strcmp(dummy_hdr->name, "Accept-Contact") == 0)
			{
				/*
				Accept-Contact: *;+g.3gpp.mcptt;require;explicit
				Accept-Contact: *;+g.3gpp.icsi-ref="urn%3Aurn-7%3A3gpp-service.ims.icsi.mcptt";require;explicit
				*/
				//Accept-Contact 
				TSIP_HEADER_ADD_PARAM(dummy_hdr,"+g.3gpp.mcptt;require;explicit", tsk_null);
				TSIP_HEADER_ADD_PARAM(dummy_hdr,"+g.3gpp.icsi-ref=\"urn:urn-7:3gpp-service.ims.icsi.mcptt\";require;explicit", tsk_null);
				control_accept_contact=tsk_true;
				break;
			}
		}
		if(control_accept_contact==tsk_false){
				tsip_message_add_headers(request,
				TSIP_HEADER_DUMMY_VA_ARGS("Accept-Contact", "*;+g.3gpp.mcptt;require;explicit+g.3gpp.icsi-ref=\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mcptt\";require;explicit"),
				tsk_null
				);
		}


		if(TSIP_DIALOG(self)->curr_action && TSIP_DIALOG(self)->curr_action->payload){

			mcptt_info = tsip_dialog_message_create_mcpttinfo(self);
			body = tmedia_content_multipart_body_create("multipart/mixed", tsk_null);
			if(body)
			{
				//mcpt-info
				mcptt_info_content = tmedia_content_multipart_create(TSK_BUFFER_DATA(mcptt_info), TSK_BUFFER_SIZE(mcptt_info), "application/vnd.3gpp.mcptt-info+xml",tsk_null);
				if(mcptt_info_content){
					tmedia_content_multipart_body_add_content(body, mcptt_info_content);
				}
				//Content-Type: application/vnd.3gpp.mcptt-mbms-usage-info+xml
				content = tmedia_content_multipart_create( TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload), TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload),value_Content_Type_Mbms,tsk_null);
				if(content){
					tmedia_content_multipart_body_add_content(body,content);
				}
				body_string = tmedia_content_multipart_body_tostring(body);
				content_type_hdr = tmedia_content_multipart_body_get_header(body);

			}
			tsip_message_add_content(request, content_type_hdr,body_string,tsk_strlen(body_string));
		}

	}else


	/* apply action params to the request */
	if(TSIP_DIALOG(self)->curr_action)
		tsip_dialog_apply_action(request, TSIP_DIALOG(self)->curr_action);

	

	ret = tsip_dialog_request_send(TSIP_DIALOG(self), request);
	TSK_OBJECT_SAFE_FREE(request);

	return ret;
}

//Use in MCPTT for MBMS
int receive_MESSAGE(tsip_dialog_message_t *self,const tsip_request_t *request)
{
	tsdp_message_t* sdp_ro = tsk_null;
	tsip_message_t* sipmessage=tsk_null;
	tmedia_multipart_body_t* mp_body = tsk_null;
	tmedia_content_multipart_t* mp_content = tsk_null;
	tmedia_content_multipart_t* mp_content_sdp = tsk_null;
	tmedia_ro_type_t ro_type = tmedia_ro_type_none;
	const tsdp_header_M_t* multicast_media_mcptt_audio = tsk_null;
	const tsdp_header_M_t* multicast_media_mcptt_mux = tsk_null;
	const tsdp_header_M_t* multicast_media_mcptt_mux_2 = tsk_null;
	const tsdp_header_M_t* multicast_media_mcptt = tsk_null;
//	const char* addrs;
	uint32_t port;
	tsip_ssession_t* session=tsk_null;
	int ret=0;
	uint32_t local_ssrc = 0;
	char* boundary = tsk_null;
//	tsk_list_item_t *item;
	//const tsdp_message_t* sdp_lo;
	char* sdp=tsk_null;
	sipmessage=(tsip_message_t*)request;
	if(sipmessage!=tsk_null && tsk_striequals(VALUE_CONTENT_TYPE_MULTIPART, TSIP_MESSAGE_CONTENT_TYPE(sipmessage))){
		//multipart/mixed
		boundary = tsip_header_get_param_value((tsip_header_t*)sipmessage->Content_Type, "boundary");
			if(boundary != tsk_null) 
			{
				mp_body = tmedia_content_multipart_body_parse(TSIP_MESSAGE_CONTENT_DATA(sipmessage), TSIP_MESSAGE_CONTENT_DATA_LENGTH(sipmessage), VALUE_CONTENT_TYPE_MULTIPART, boundary);
                if(mp_body != tsk_null) {
                    //mp_content is xml of MBMS
                    mp_content = tmedia_content_multipart_body_get_content(mp_body,
                                                                           VALUE_CONTENT_TYPE_MESSAGE_MBMS);
                    //mp_content_sdp is the data in char* of SDP
                    mp_content_sdp = tmedia_content_multipart_body_get_content(mp_body,
                                                                               "application/sdp");
                    if (mp_content != tsk_null && mp_content_sdp != tsk_null)//MBMS service
                    {

                        self->msession_mgr = tmedia_session_mgr_create(tmedia_audio,
                                                                       TSIP_DIALOG_GET_STACK(
                                                                               self)->network.local_ip[TSIP_TRANSPORT_IDX_UDP],
                                                                       TNET_SOCKET_TYPE_IS_IPV6(
                                                                               TSIP_DIALOG_GET_STACK(
                                                                                       self)->network.proxy_cscf_type[TSIP_TRANSPORT_IDX_UDP]),
                                                                       tsk_true);
                        //sdp_ro es the parse data in SDP. sdp_ro is a list

                        if (!(sdp_ro = tsdp_message_parse(mp_content_sdp->data,
                                                          mp_content_sdp->data_size))) {
                            TSK_DEBUG_ERROR("Failed to parse remote sdp message:\n [%.*s]",
                                            mp_content->data_size, (const char *) mp_content->data);
                            return -2;
                        } else {
                            sdp = tsdp_message_tostring(sdp_ro);
                        }

                        if (!(multicast_media_mcptt_audio = tsdp_message_find_media(sdp_ro,
                                                                                    "audio"))) {
                            return -1;
                        }
                        if (!(multicast_media_mcptt_mux = tsdp_message_find_media_at_index(sdp_ro,
                                                                                           "application",
                                                                                           0))) {
                            return -1;
                        }
                        multicast_media_mcptt = multicast_media_mcptt_mux;
                        if (!(multicast_media_mcptt_mux_2 = tsdp_message_find_media_at_index(sdp_ro,
                                                                                             "application",
                                                                                             1)) &&
                            multicast_media_mcptt_mux->port == 9) {
                            return -1;
                        } else {
                            multicast_media_mcptt = multicast_media_mcptt_mux_2;
                        }



                        //session=TSIP_DIALOG_GET_SS(self);
                        port = multicast_media_mcptt->port;
                        //session->stack->pttMCPTTMbms.port_manager=port;
#if HAVE_CRT //Debug memory
                        //addrs=(char*)malloc(tsk_strlen(multicast_media_mcptt->C->addr) + 1);

#else
                        //addrs=(char*)tsk_malloc(tsk_strlen(multicast_media_mcptt->C->addr) + 1);

#endif //HAVE_CRT
                        //strcpy(addrs,multicast_media_mcptt->C->addr);
                        //session->stack->pttMCPTTMbms.addr_multicast=addrs;

                        /*
                        tsk_list_lock(TSIP_DIALOG_GET_STACK(self)->ssessions);
                        tsk_list_foreach(item,TSIP_DIALOG_GET_STACK(self)->ssessions){
                            va_list ap;
                            if (!(audio_session2 = item->data) || !(audio_session2->type & tmedia_mcptt)){
                                va_end(ap);
                                continue;
                            }

                            va_end(ap);
                            break;
                        }
                        */
                        /*
                        if(sdp_ro){



                            //session->stack->pttMCPTTMbms.sdp_ro=tsdp_message_clone(sdp_ro);
                            if (tmedia_session_mgr_is_new_ro(self->msession_mgr, sdp_ro)) {
                                ret = tsip_dialog_message_msession_configure(self);
                            }

                            if((ret = tmedia_session_mgr_set_ro(self->msession_mgr, sdp_ro, ro_type))){
                                TSK_DEBUG_ERROR("Failed to set remote offer");
                                return -2;
                            }

                        }




                        local_ssrc = tmedia_session_mgr_audio_get_ssrc(self->msession_mgr);
                        audio_session = tmedia_session_mgr_find(self->msession_mgr, tmedia_audio);
                        tmedia_session_mgr_set(self->msession_mgr,
                            TMEDIA_SESSION_SET_INT32(tmedia_mcptt, "local_ssrc", local_ssrc),
                            TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "audio_session", audio_session),
                            TMEDIA_SESSION_SET_NULL());
                        audio_session = tmedia_session_mgr_find(self->msession_mgr,tmedia_mcptt);
                        audio_session->lo_held = tsk_true;
                        */
                        //TSK_OBJECT_SAFE_FREE(mp_content);
                        /*if(addrs != tsk_null){
                            TSK_OBJECT_SAFE_FREE(addrs);
                        }*/
                        if (sdp_ro != tsk_null) {
                            TSK_OBJECT_SAFE_FREE(sdp_ro);
                        }
                    } else if (mp_content != tsk_null) {
                        TSK_DEBUG_ERROR("The new message isn't valid.");
                    }
                }
			}
	}
	
	if(boundary!=tsk_null){
		TSK_OBJECT_SAFE_FREE(boundary);
	}
	if(mp_body!=tsk_null){
		TSK_OBJECT_SAFE_FREE(mp_body);
	}

	/* Alert the user. */
	if(sdp!=tsk_null){
		ret=TSIP_DIALOG_MESSAGE_SIGNAL(self, tsip_i_message, 
			tsip_event_code_dialog_request_incoming, sdp, request);
	}else {
        ret=TSIP_DIALOG_MESSAGE_SIGNAL(self, tsip_i_message,
            tsip_event_code_dialog_request_incoming, "incoming message", request);
	}

	/* Update last incoming MESSAGE */
	TSK_OBJECT_SAFE_FREE(self->request);
	self->request = tsk_object_ref((void*)request);

	return ret;
}

int tsip_dialog_message_msession_configure(tsip_dialog_message_t *self)
{
	/*
	tmedia_srtp_mode_t srtp_mode;
	tmedia_mode_t avpf_mode;
	tsk_bool_t is_rtcweb_enabled;
	tsk_bool_t is_webrtc2sip_mode_enabled;
	
	if(!self || !self->msession_mgr){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	is_webrtc2sip_mode_enabled = (TSIP_DIALOG_GET_STACK(self)->network.mode == tsip_stack_mode_webrtc2sip);
	is_rtcweb_enabled = (((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.profile == tmedia_profile_rtcweb);
	srtp_mode = is_rtcweb_enabled ? tmedia_srtp_mode_mandatory : ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.srtp_mode;
	avpf_mode = is_rtcweb_enabled ? tmedia_mode_mandatory : ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.avpf_mode;

	// set callback functions
	tmedia_session_mgr_set_onerror_cbfn(self->msession_mgr, self, tsip_dialog_message_msession_onerror_cb);
	tmedia_session_mgr_set_rfc5168_cbfn(self->msession_mgr, self, tsip_dialog_invite_msession_rfc5168_cb);

	// set params
	return tmedia_session_mgr_set(self->msession_mgr,
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "srtp-mode", srtp_mode),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "avpf-mode", avpf_mode),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "webrtc2sip-mode-enabled", is_webrtc2sip_mode_enabled), // hack the media stack
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "rtcp-enabled", self->use_rtcp),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "rtcpmux-enabled", self->use_rtcpmux),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "codecs-supported", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.codecs),
			
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "bypass-encoding", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.bypass_encoding),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "bypass-decoding", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.bypass_decoding),

			TMEDIA_SESSION_SET_INT32(tmedia_audio, "rtp-ssrc", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.rtp.ssrc.audio),
			TMEDIA_SESSION_SET_INT32(tmedia_video, "rtp-ssrc", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.rtp.ssrc.video),
			
			TMEDIA_SESSION_SET_STR(self->msession_mgr->type, "dtls-file-ca", TSIP_DIALOG_GET_STACK(self)->security.tls.ca),
			TMEDIA_SESSION_SET_STR(self->msession_mgr->type, "dtls-file-pbk", TSIP_DIALOG_GET_STACK(self)->security.tls.pbk),
			TMEDIA_SESSION_SET_STR(self->msession_mgr->type, "dtls-file-pvk", TSIP_DIALOG_GET_STACK(self)->security.tls.pvk),
			TMEDIA_SESSION_SET_INT32(self->msession_mgr->type, "dtls-cert-verify", TSIP_DIALOG_GET_STACK(self)->security.tls.verify),

			TMEDIA_SESSION_SET_INT32(tmedia_video, "fps", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.video_fps),
			TMEDIA_SESSION_SET_INT32(tmedia_video, "bandwidth-max-upload", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.video_bw_up),
			TMEDIA_SESSION_SET_INT32(tmedia_video, "bandwidth-max-download", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.video_bw_down),
			TMEDIA_SESSION_SET_INT32(tmedia_video, "pref-size", ((tsip_ssession_t*)TSIP_DIALOG(self)->ss)->media.video_pref_size),

			tsk_null);
			*/
	return 0;
}

int tsip_dialog_message_OnTerminated(tsip_dialog_message_t *self)
{
	TSK_DEBUG_INFO("=== MESSAGE Dialog terminated ===");

	/* Alert the user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_terminated, 
		TSIP_DIALOG(self)->last_error.phrase ? TSIP_DIALOG(self)->last_error.phrase : "Dialog terminated");

	/* Remove from the dialog layer. */
	return tsip_dialog_remove(TSIP_DIALOG(self));
}
//MCPTT mbms
static tsk_buffer_t* tsip_dialog_message_create_mcpttinfo(const tsip_dialog_message_t* self)
{
	tsip_stack_t* ptsip_stack_t;
	tsk_buffer_t* output = tsk_buffer_create_null();
/*
#if HAVE_LIBXML2
	ret = tsk_null;
#else*/
	tmedia_type_t type;
	const char* head  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						"<mcpttinfo xmlns=\"urn:3gpp:ns:mcpttInfo:1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
						"<mcptt-Params>\r\n";
	const char* headout  = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						"<mcpttinfo>\r\n"
						"<mcptt-Params>\r\n";
	const char* tabInitSessionType =  "<session-type>";
	const char* tabEndSessionType =  "</session-type>\r\n";
	const char* tabInitMCPTTID =  "<mcptt-calling-user-id>\r\n";
	const char* tabEndMCPTTID =  "</mcptt-calling-user-id>\r\n";
	const char* tabInitGroupID =  "<mcptt-calling-group-id>\r\n";
	const char* tabEndGroupID =  "</mcptt-calling-group-id>\r\n";
	const char* tabInitMcpttURI= "<mcpttURI>";
	const char* tabEndMcpttURI= "</mcpttURI>\r\n";
	const char* tabInitRequestUri =  "<mcptt-request-uri type=\"Normal\">";
	const char* tabInitRequestUri_old =  "<mcptt-request-uri>";
	const char* tabEndRequestUri =  "</mcptt-request-uri>\r\n";
	const char* tail =  "</mcptt-Params>\r\n"
						"</mcpttinfo>\r\n";
	ptsip_stack_t=TSIP_DIALOG_GET_STACK(self);
	if(ptsip_stack_t->pttMCPTT.mcptt_namespace!=tsk_false){
		tsk_buffer_append_2(output, "%s", head);
	}else{
		tsk_buffer_append_2(output, "%s", headout);
	}
	
	type=TSIP_DIALOG_GET_SS(self)->media.type;

	if((type & tmedia_mbms) == tmedia_mbms){
		//<session-type>
			tsk_buffer_append_2(output, "%s", tabInitRequestUri);
			tsk_buffer_append_2(output, "%s", tabInitMcpttURI);

		tsk_buffer_append_2(output, "%s", tsip_uri_tostring(TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_id,tsk_true, tsk_false));
			tsk_buffer_append_2(output, "%s", tabEndMcpttURI);
		
		
		tsk_buffer_append_2(output, "%s", tabEndRequestUri);
		//<mcptt-calling-user-id>
		/*
		tsk_buffer_append_2(output, "%s", tabInitMCPTTID);
		tsk_buffer_append_2(output, "%s\r\n", tsip_uri_tostring(TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_id,tsk_true, tsk_false));
		tsk_buffer_append_2(output, "%s", tabEndMCPTTID);
		*/
	}
	tsk_buffer_append_2(output, "%s", tail);
		
	

	
	return output;
}









//========================================================
//	SIP dialog MESSAGE object definition
//
static tsk_object_t* tsip_dialog_message_ctor(tsk_object_t * self, va_list * app)
{
	tsip_dialog_message_t *dialog = self;
	if(dialog){
		tsip_ssession_handle_t *ss = va_arg(*app, tsip_ssession_handle_t *);

		/* Initialize base class */
		tsip_dialog_init(TSIP_DIALOG(self), tsip_dialog_MESSAGE, tsk_null, ss, _fsm_state_Started, _fsm_state_Terminated);

		/* FSM */
		TSIP_DIALOG_GET_FSM(self)->debug = DEBUG_STATE_MACHINE;
		tsk_fsm_set_callback_terminated(TSIP_DIALOG_GET_FSM(self), TSK_FSM_ONTERMINATED_F(tsip_dialog_message_OnTerminated), (const void*)dialog);

		/* Initialize the class itself */
		tsip_dialog_message_init(self);
	}
	return self;
}

static tsk_object_t* tsip_dialog_message_dtor(tsk_object_t * self)
{ 
	tsip_dialog_message_t *dialog = self;
	if(dialog){
		/* DeInitialize base class (will cancel all transactions) */
		tsip_dialog_deinit(TSIP_DIALOG(self));

		/* DeInitialize self */
		TSK_OBJECT_SAFE_FREE(dialog->request);

		TSK_DEBUG_INFO("*** MESSAGE Dialog destroyed ***");
	}
	return self;
}

static int tsip_dialog_message_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return tsip_dialog_cmp(obj1, obj2);
}

static const tsk_object_def_t tsip_dialog_message_def_s = 
{
	sizeof(tsip_dialog_message_t),
	tsip_dialog_message_ctor, 
	tsip_dialog_message_dtor,
	tsip_dialog_message_cmp, 
};
const tsk_object_def_t *tsip_dialog_message_def_t = &tsip_dialog_message_def_s;