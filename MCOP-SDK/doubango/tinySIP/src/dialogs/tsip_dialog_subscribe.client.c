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


/**@file tsip_dialog_subscribe.client.c
 * @brief SIP dialog SUBSCRIBE (Client side) as per RFC 3265.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include <tinysip.h>
#include "tinysip/dialogs/tsip_dialog_subscribe.h"

#include "tinysip/headers/tsip_header_Dummy.h"
#include "tinysip/headers/tsip_header_Event.h"
#include "tinysip/headers/tsip_header_Min_Expires.h"
#include "tinysip/headers/tsip_header_Subscription_State.h"

#include "tinysip/transactions/tsip_transac_layer.h"

#include "tinysip/api/tsip_api_subscribe.h"

#include "tsk_debug.h"
#include "tsk_time.h"



#define DEBUG_STATE_MACHINE											0
#define TSIP_DIALOG_SUBSCRIBE_TIMER_SCHEDULE(TX)						TSIP_DIALOG_TIMER_SCHEDULE(subscribe, TX)
#define TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, type, code, phrase, message)	\
	tsip_subscribe_event_signal(type, TSIP_DIALOG(self)->ss, code, phrase, message)

/* ======================== internal functions ======================== */
static char* tsip_dialog_subscribe_create_mcpttinfo(const tsip_dialog_subscribe_t* self);
static int process_i_notify(tsip_dialog_subscribe_t *self, const tsip_request_t* notify);
static int send_SUBSCRIBE(tsip_dialog_subscribe_t *self);
static int send_200NOTIFY(tsip_dialog_subscribe_t *self, const tsip_request_t* request);
static int tsip_dialog_subscribe_OnTerminated(tsip_dialog_subscribe_t *self);

/* ======================== transitions ======================== */
static int tsip_dialog_subscribe_Started_2_Trying_X_subscribe(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Trying_X_1xx(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Terminated_X_2xx(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Connected_X_2xx(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Trying_X_401_407_421_494(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Trying_X_423(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Terminated_X_300_to_699(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Terminated_X_cancel(va_list *app);
static int tsip_dialog_subscribe_Trying_2_Trying_X_NOTIFY(va_list *app);
static int tsip_dialog_subscribe_Connected_2_Trying_X_unsubscribe(va_list *app);
static int tsip_dialog_subscribe_Connected_2_Trying_X_subscribe(va_list *app);
static int tsip_dialog_subscribe_Connected_2_Connected_X_NOTIFY(va_list *app);
static int tsip_dialog_subscribe_Connected_2_Terminated_X_NOTIFY(va_list *app);
static int tsip_dialog_subscribe_Any_2_Trying_X_hangup(va_list *app);
static int tsip_dialog_subscribe_Any_2_Trying_X_shutdown(va_list *app);
static int tsip_dialog_subscribe_Any_2_Terminated_X_transportError(va_list *app);
static int tsip_dialog_subscribe_Any_2_Terminated_X_Error(va_list *app);


/* ======================== conds ======================== */
static tsk_bool_t _fsm_cond_unsubscribing(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	return dialog->unsubscribing;
}
static tsk_bool_t _fsm_cond_subscribing(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	return !_fsm_cond_unsubscribing(dialog, message);
}

static tsk_bool_t _fsm_cond_notify_terminated(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	const tsip_header_Subscription_State_t *hdr_state;
	if((hdr_state = (const tsip_header_Subscription_State_t*)tsip_message_get_header(message, tsip_htype_Subscription_State)))
	{
		return tsk_striequals(hdr_state->state, "terminated") &&
			(hdr_state->expires < 0 || tsk_striequals(hdr_state->reason, "rejected") || tsk_striequals(hdr_state->reason, "noresource"));
	}
	return tsk_false;
}
static tsk_bool_t _fsm_cond_notify_not_terminated(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	return !_fsm_cond_notify_terminated(dialog, message);
}

static tsk_bool_t _fsm_cond_silent_hangup(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	return TSIP_DIALOG(dialog)->ss->silent_hangup;
}
static tsk_bool_t _fsm_cond_not_silent_hangup(tsip_dialog_subscribe_t* dialog, tsip_message_t* message)
{
	return !TSIP_DIALOG(dialog)->ss->silent_hangup;
}
#define _fsm_cond_silent_shutdown _fsm_cond_silent_hangup
#define _fsm_cond_not_silent_shutdown _fsm_cond_not_silent_hangup

/* ======================== actions ======================== */
typedef enum _fsm_action_e
{
	_fsm_action_subscribe = tsip_atype_subscribe,
	_fsm_action_hangup = tsip_atype_hangup,
	_fsm_action_cancel = tsip_atype_cancel,
	_fsm_action_shutdown = tsip_atype_shutdown,
	_fsm_action_transporterror = tsip_atype_transport_error,

	_fsm_action_1xx = 0xFF,
	_fsm_action_2xx,
	_fsm_action_401_407_421_494,
	_fsm_action_423,
	_fsm_action_300_to_699,
	_fsm_action_shutdown_timedout, /* Any -> Terminated */
	_fsm_action_notify,
	_fsm_action_error,
}
_fsm_action_t;

/* ======================== states ======================== */
typedef enum _fsm_state_e
{
	_fsm_state_Started,
	_fsm_state_Trying,
	_fsm_state_Connected,
	_fsm_state_Terminated
}
_fsm_state_t;

/**
 * Callback function called to alert the dialog for new events from the transaction/transport layers.
 *
 * @param [in,out]	self	A reference to the dialog.
 * @param	type		The event type. 
 * @param [in,out]	msg	The incoming SIP/IMS message. 
 *
 * @return	Zero if succeed and non-zero error code otherwise. 
**/
int tsip_dialog_subscribe_event_callback(const tsip_dialog_subscribe_t *self, tsip_dialog_event_type_t type, const tsip_message_t *msg)
{
	int ret = -1;

	switch(type)
	{
	case tsip_dialog_i_msg:
		{
			if(msg && TSIP_MESSAGE_IS_RESPONSE(msg)){
				//
				//	RESPONSE
				//
				const tsip_action_t* action = tsip_dialog_keep_action(TSIP_DIALOG(self), msg) ? TSIP_DIALOG(self)->curr_action : tsk_null;
				if(TSIP_RESPONSE_IS_1XX(msg)){
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_1xx, msg, action);
				}
				else if(TSIP_RESPONSE_IS_2XX(msg)){
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_2xx, msg, action);
				}
				else if(TSIP_RESPONSE_IS(msg,401) || TSIP_RESPONSE_IS(msg,407) || TSIP_RESPONSE_IS(msg,421) || TSIP_RESPONSE_IS(msg,494)){
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_401_407_421_494, msg, action);
				}
				else if(TSIP_RESPONSE_IS(msg,423)){
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_423, msg, action);
				}
				else{
					// Alert User
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_error, msg, action);
					/* TSK_DEBUG_WARN("Not supported status code: %d", TSIP_RESPONSE_CODE(msg)); */
				}
			}
			else{
				//
				//	REQUEST
				//
				if(TSIP_REQUEST_IS_NOTIFY(msg)){
					ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_notify, msg, tsk_null);
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

/** Timer manager callback.
 *
 * @param [in,out]	self	The owner of the signaled timer. 
 * @param	timer_id		The identifier of the signaled timer.
 *
 * @return	Zero if succeed and non-zero error code otherwise.  
**/
int tsip_dialog_subscribe_timer_callback(const tsip_dialog_subscribe_t* self, tsk_timer_id_t timer_id)
{
	int ret = -1;

	if(self)
	{
		if(timer_id == self->timerrefresh.id){
			tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_subscribe, tsk_null, tsk_null);
			ret = 0;
		}
		else if(timer_id == self->timershutdown.id){
			ret = tsip_dialog_fsm_act(TSIP_DIALOG(self), _fsm_action_shutdown_timedout, tsk_null, tsk_null);
		}
	}
	return ret;
}

tsip_dialog_subscribe_t* tsip_dialog_subscribe_create(const tsip_ssession_handle_t* ss)
{
	return tsk_object_new(tsip_dialog_subscribe_def_t, ss);
}

/**	Initializes the dialog.
 *
 * @param [in,out]	self	The dialog to initialize. 
**/
int tsip_dialog_subscribe_init(tsip_dialog_subscribe_t *self)
{	
	/* Initialize the State Machine. */
	tsk_fsm_set(TSIP_DIALOG_GET_FSM(self),
			
			/*=======================
			* === Started === 
			*/
			// Started -> (Send) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Started, _fsm_action_subscribe, _fsm_state_Trying, tsip_dialog_subscribe_Started_2_Trying_X_subscribe, "tsip_dialog_subscribe_Started_2_Trying_X_subscribe"),
			// Started -> (Any) -> Started
			TSK_FSM_ADD_ALWAYS_NOTHING(_fsm_state_Started, "tsip_dialog_subscribe_Started_2_Started_X_any"),
			

			/*=======================
			* === Trying === 
			*/
			// Trying -> (1xx) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_1xx, _fsm_state_Trying, tsip_dialog_subscribe_Trying_2_Trying_X_1xx, "tsip_dialog_subscribe_Trying_2_Trying_X_1xx"),
			// Trying -> (2xx) -> Terminated
			TSK_FSM_ADD(_fsm_state_Trying, _fsm_action_2xx, _fsm_cond_unsubscribing, _fsm_state_Terminated, tsip_dialog_subscribe_Trying_2_Terminated_X_2xx, "tsip_dialog_subscribe_Trying_2_Terminated_X_2xx"),
			// Trying -> (2xx) -> Connected
			TSK_FSM_ADD(_fsm_state_Trying, _fsm_action_2xx, _fsm_cond_subscribing, _fsm_state_Connected, tsip_dialog_subscribe_Trying_2_Connected_X_2xx, "tsip_dialog_subscribe_Trying_2_Connected_X_2xx"),
			// Trying -> (401/407/421/494) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_401_407_421_494, _fsm_state_Trying, tsip_dialog_subscribe_Trying_2_Trying_X_401_407_421_494, "tsip_dialog_subscribe_Trying_2_Trying_X_401_407_421_494"),
			// Trying -> (423) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_423, _fsm_state_Trying, tsip_dialog_subscribe_Trying_2_Trying_X_423, "tsip_dialog_subscribe_Trying_2_Trying_X_423"),
			// Trying -> (300_to_699) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_300_to_699, _fsm_state_Terminated, tsip_dialog_subscribe_Trying_2_Terminated_X_300_to_699, "tsip_dialog_subscribe_Trying_2_Terminated_X_300_to_699"),
			// Trying -> (cancel) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_cancel, _fsm_state_Terminated, tsip_dialog_subscribe_Trying_2_Terminated_X_cancel, "tsip_dialog_subscribe_Trying_2_Terminated_X_cancel"),
			// Trying -> (Notify) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_notify, _fsm_state_Trying, tsip_dialog_subscribe_Trying_2_Trying_X_NOTIFY, "tsip_dialog_subscribe_Trying_2_Trying_X_NOTIFY"),
			// Trying -> (hangup) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_hangup, _fsm_state_Terminated, tsk_null, "tsip_dialog_subscribe_Trying_2_Terminated_X_hangup"),
			// Trying -> (shutdown) -> Terminated
			TSK_FSM_ADD_ALWAYS(_fsm_state_Trying, _fsm_action_shutdown, _fsm_state_Terminated, tsk_null, "tsip_dialog_subscribe_Trying_2_Terminated_X_shutdown"),
			// Trying -> (Any) -> Trying
			//TSK_FSM_ADD_ALWAYS_NOTHING(_fsm_state_Trying, "tsip_dialog_subscribe_Trying_2_Trying_X_any"),


			/*=======================
			* === Connected === 
			*/
			// Connected -> (SUBSCRIBE) -> Trying
			TSK_FSM_ADD_ALWAYS(_fsm_state_Connected, _fsm_action_subscribe, _fsm_state_Trying, tsip_dialog_subscribe_Connected_2_Trying_X_subscribe, "tsip_dialog_subscribe_Connected_2_Trying_X_subscribe"),
			// Connected -> (NOTIFY) -> Connected
			TSK_FSM_ADD(_fsm_state_Connected, _fsm_action_notify, _fsm_cond_notify_not_terminated, _fsm_state_Connected, tsip_dialog_subscribe_Connected_2_Connected_X_NOTIFY, "tsip_dialog_subscribe_Connected_2_Connected_X_NOTIFY"),
			// Connected -> (NOTIFY) -> Terminated
			TSK_FSM_ADD(_fsm_state_Connected, _fsm_action_notify, _fsm_cond_notify_terminated, _fsm_state_Terminated, tsip_dialog_subscribe_Connected_2_Terminated_X_NOTIFY, "tsip_dialog_subscribe_Connected_2_Terminated_X_NOTIFY"),

			/*=======================
			* === Any === 
			*/
			// Any -> (hangup) -> Trying
			TSK_FSM_ADD(tsk_fsm_state_any, _fsm_action_hangup, _fsm_cond_not_silent_hangup, _fsm_state_Trying, tsip_dialog_subscribe_Any_2_Trying_X_hangup, "tsip_dialog_subscribe_Any_2_Trying_X_hangup"),
			// Any -> (silenthangup) -> Terminated
			TSK_FSM_ADD(tsk_fsm_state_any, _fsm_action_hangup, _fsm_cond_silent_hangup, _fsm_state_Terminated, tsk_null, "tsip_dialog_subscribe_Any_2_Trying_X_silenthangup"),
			// Any -> (shutdown) -> Trying
			TSK_FSM_ADD(tsk_fsm_state_any, _fsm_action_shutdown, _fsm_cond_not_silent_shutdown, _fsm_state_Trying, tsip_dialog_subscribe_Any_2_Trying_X_shutdown, "tsip_dialog_subscribe_Any_2_Trying_X_shutdown"),
			// Any -> (silentshutdown) -> Terminated
			TSK_FSM_ADD(tsk_fsm_state_any, _fsm_action_shutdown, _fsm_cond_silent_shutdown, _fsm_state_Terminated, tsk_null, "tsip_dialog_subscribe_Any_2_Trying_X_silentshutdown"),
			// Any -> (shutdown timedout) -> Terminated
			TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_shutdown_timedout, _fsm_state_Terminated, tsk_null, "tsip_dialog_subscribe_shutdown_timedout"),			
			// Any -> (transport error) -> Terminated
			TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_transporterror, _fsm_state_Terminated, tsip_dialog_subscribe_Any_2_Terminated_X_transportError, "tsip_dialog_subscribe_Any_2_Terminated_X_transportError"),
			// Any -> (error) -> Terminated
			TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_error, _fsm_state_Terminated, tsip_dialog_subscribe_Any_2_Terminated_X_Error, "tsip_dialog_subscribe_Any_2_Terminated_X_Error"),

			TSK_FSM_ADD_NULL());

	/* Sets callback function */
	TSIP_DIALOG(self)->callback = TSIP_DIALOG_EVENT_CALLBACK_F(tsip_dialog_subscribe_event_callback);
	
	/* Timers */
	self->timerrefresh.id = TSK_INVALID_TIMER_ID;
	self->timerrefresh.timeout = TSIP_DIALOG(self)->expires;
	self->timershutdown.id = TSK_INVALID_TIMER_ID;
	self->timershutdown.timeout = TSIP_DIALOG_SHUTDOWN_TIMEOUT;

	return 0;
}


//--------------------------------------------------------
//				== STATE MACHINE BEGIN ==
//--------------------------------------------------------

/* Started -> (SUBSCRIBE) -> Trying
*/
int tsip_dialog_subscribe_Started_2_Trying_X_subscribe(va_list *app)
{
	//Init subscribe
	tsip_dialog_subscribe_t *self;

	self = va_arg(*app, tsip_dialog_subscribe_t *);

	TSIP_DIALOG(self)->running = tsk_true;

	/* alert the user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_connecting, "Dialog connecting");

	return send_SUBSCRIBE(self);
}

/* Trying -> (1xx) -> Trying
*/
int tsip_dialog_subscribe_Trying_2_Trying_X_1xx(va_list *app)
{
	/*tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);*/
	/*const tsip_response_t *response = va_arg(*app, const tsip_response_t *);*/

	return 0;
}

/* Trying -> (2xx) -> Terminated
*/
int tsip_dialog_subscribe_Trying_2_Terminated_X_2xx(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	/* Alert the user. */
	TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, self->unsubscribing ? tsip_ao_unsubscribe : tsip_ao_subscribe, 
		TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);

	return 0;
}

/* Trying -> (2xx) -> Connected
*/
int tsip_dialog_subscribe_Trying_2_Connected_X_2xx(va_list *app)
{
	int ret;
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	tsk_bool_t first_time_to_connect = (TSIP_DIALOG(self)->state == tsip_initial);

	/* Update the dialog state. */
	if((ret = tsip_dialog_update(TSIP_DIALOG(self), response))){
		return ret;
	}

	/* Alert the user(session) */
	TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, self->unsubscribing ? tsip_ao_unsubscribe : tsip_ao_subscribe, 
		TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);
	/* Alert the user(dialog) */
	if(first_time_to_connect){
		TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_connected, "Dialog connected");
	}

	/* Reset current action */
	tsip_dialog_set_curr_action(TSIP_DIALOG(self), tsk_null);

	/* Request timeout for dialog refresh (re-subscribtion). */
	self->timerrefresh.timeout = tsip_dialog_get_newdelay(TSIP_DIALOG(self), response);
	TSIP_DIALOG_SUBSCRIBE_TIMER_SCHEDULE(refresh);

	return 0;
}

/* Trying -> (401/407/421/494) -> Trying
*/
int tsip_dialog_subscribe_Trying_2_Trying_X_401_407_421_494(va_list *app)
{
	int ret;
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	if((ret = tsip_dialog_update(TSIP_DIALOG(self), response))){
		/* Alert the user. */
		TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, self->unsubscribing ? tsip_ao_unsubscribe : tsip_ao_subscribe, 
			TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);
		
		return ret;
	}
	
	return send_SUBSCRIBE(self);
}

/* Trying -> (423) -> Trying
*/
int tsip_dialog_subscribe_Trying_2_Trying_X_423(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	tsip_header_Min_Expires_t *hdr;

	/*
	RFC 3261 - 10.2.8 Error Responses

	If a UA receives a 423 (Interval Too Brief) response, it MAY retry
	the registration after making the expiration interval of all contact
	addresses in the SUBSCRIBE request equal to or greater than the
	expiration interval within the Min-Expires header field of the 423
	(Interval Too Brief) response.
	*/
	hdr = (tsip_header_Min_Expires_t*)tsip_message_get_header(response, tsip_htype_Min_Expires);
	if(hdr){
		TSIP_DIALOG(self)->expires = TSK_TIME_S_2_MS(hdr->value);
		send_SUBSCRIBE(self);
	}
	else{
		TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_message_error, "Received invalid SIP response.");

		return -1;
	}

	return 0;
}

/* Trying -> (300-699) -> Terminated
*/
int tsip_dialog_subscribe_Trying_2_Terminated_X_300_to_699(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	/* save last error */
	tsip_dialog_set_lasterror_2(TSIP_DIALOG(self), TSIP_RESPONSE_PHRASE(response), TSIP_RESPONSE_CODE(response), response);

	/* alert the user */
	TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, self->unsubscribing ? tsip_ao_unsubscribe : tsip_ao_subscribe,  
		TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);

	return 0;
}

/* Trying -> (cancel) -> Terminated
*/
int tsip_dialog_subscribe_Trying_2_Terminated_X_cancel(va_list *app)
{
	int ret;
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	/* const tsip_response_t *response = va_arg(*app, const tsip_response_t *); */

	/* Cancel all transactions associated to this dialog (will also be done when the dialog is destroyed (worth nothing)) */
	ret = tsip_transac_layer_cancel_by_dialog(TSIP_DIALOG_GET_STACK(self)->layer_transac, TSIP_DIALOG(self));

	/*	RFC 3261 - 9.1 Client Behavior
		A CANCEL request SHOULD NOT be sent to cancel a request other than INVITE.
   */

	/* alert the user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_request_cancelled, "Subscription cancelled");

	return ret;
}

/* Trying -> (NOTIFY) -> Trying
*/
int tsip_dialog_subscribe_Trying_2_Trying_X_NOTIFY(va_list *app)
{
	int ret;
	
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_request_t *request = va_arg(*app, const tsip_request_t *);

	ret = send_200NOTIFY(self, request);
	ret = process_i_notify(self, request);
	
	return ret;
}

/* Connected -> (SUBSCRIBE) -> Trying
*/
int tsip_dialog_subscribe_Connected_2_Trying_X_subscribe(va_list *app)
{
	tsip_dialog_subscribe_t *self;

	self = va_arg(*app, tsip_dialog_subscribe_t *);

	return send_SUBSCRIBE(self);
}

/* Connected -> (NOTIFY) -> Connected
*/
int tsip_dialog_subscribe_Connected_2_Connected_X_NOTIFY(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_request_t *request = va_arg(*app, const tsip_request_t *);
	int ret;

	ret = send_200NOTIFY(self, request);
	ret = process_i_notify(self, request);

	return ret;
}

/* Connected -> (NOTIFY) -> Terminated
*/
int tsip_dialog_subscribe_Connected_2_Terminated_X_NOTIFY(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_request_t *request = va_arg(*app, const tsip_request_t *);

	/* Alert the user */
	TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, tsip_i_notify, 
		tsip_event_code_dialog_request_incoming, "Incoming NOTIFY.", request);

	return send_200NOTIFY(self, request);
}

/* Any -> (hangup) -> Trying
*/
int tsip_dialog_subscribe_Any_2_Trying_X_hangup(va_list *app)
{
	tsip_dialog_subscribe_t *self;

	self = va_arg(*app, tsip_dialog_subscribe_t *);

	/* Alert the user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_terminating, "Terminating dialog");

	self->unsubscribing = tsk_true;
	return send_SUBSCRIBE(self);
}

/* Any -> (shutdown) -> Trying
*/
int tsip_dialog_subscribe_Any_2_Trying_X_shutdown(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);

	/* schedule shutdow timeout */
	TSIP_DIALOG_SUBSCRIBE_TIMER_SCHEDULE(shutdown);

	/* alert user */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_terminating, "Terminating dialog");

	self->unsubscribing = tsk_true;
	return send_SUBSCRIBE(self);
}

/* Any -> (transport error) -> Terminated
*/
int tsip_dialog_subscribe_Any_2_Terminated_X_transportError(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	/* const tsip_response_t *response = va_arg(*app, const tsip_response_t *); */

	/* Alert the user. */
	TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_transport_error, "Transport error.");

	return 0;
}

/* Any -> (error) -> Terminated
*/
int tsip_dialog_subscribe_Any_2_Terminated_X_Error(va_list *app)
{
	tsip_dialog_subscribe_t *self = va_arg(*app, tsip_dialog_subscribe_t *);
	const tsip_response_t *response = va_arg(*app, const tsip_response_t *);

	// save last error
	tsip_dialog_set_lasterror_2(TSIP_DIALOG(self), TSIP_RESPONSE_PHRASE(response), TSIP_RESPONSE_CODE(response), response);

	/* Alert user */
	if(response){
		TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, self->unsubscribing ? tsip_ao_unsubscribe : tsip_ao_subscribe, 
				TSIP_RESPONSE_CODE(response), TSIP_RESPONSE_PHRASE(response), response);
	}
	else{
		TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_global_error, "Global error.");
	}

	return 0;
}


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				== STATE MACHINE END ==
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++

/**
 * Sends a SUBSCRIBE request. 
 *
 * @param [in,out]	self	The caller.
 *
 * @return	Zero if succeed and non-zero error code otherwise. 
**/
int send_SUBSCRIBE(tsip_dialog_subscribe_t *self)
{
	char* body_string  = tsk_null;
	const int64_t NUM_MAX_EXPIRES=4294967295000;/*milisecons*/
	const char* contentType="application/vnd.3gpp.mcptt-info+xml";
	const char* contentTypeResourceList="application/resource-lists+xml";
	tsip_dialog_t *dialog;
	tsip_stack_t *stack;
	tsip_ssession_t *tsip_ssession_t;
	tsip_request_t *request;
	int ret = -1;
	tsip_uri_t* psi_affiliation;
	
	dialog=TSIP_DIALOG(self);
	stack=TSIP_DIALOG_GET_STACK(self);
	tsip_ssession_t=TSIP_DIALOG_GET_SS(self);
	if(self->unsubscribing){
		dialog->expires = 0;
	}

	//MCPTT AFFILIATION
	if(((tsip_ssession_t->media.type & tmedia_affiliation) == tmedia_affiliation
         )
       && !self->unsubscribing){
		if(self->unsubscribing){
			dialog->expires = 0;//No Receive notify. 
		}else{
			dialog->expires = NUM_MAX_EXPIRES;//Receive notify.
		}
		//select URI_remote

		psi_affiliation=stack->pttMCPTTAffiliation.psi_affiliation;
		if(psi_affiliation!=tsk_null){
			dialog->uri_remote_target=tsip_uri_clone(stack->pttMCPTTAffiliation.psi_affiliation,tsk_true, tsk_false);
			dialog->uri_remote=tsip_uri_clone(stack->pttMCPTTAffiliation.psi_affiliation,tsk_true, tsk_false);
		}

	}
	//MCPTT
	if(((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt) == tmedia_mcptt) && self->unsubscribing==tsk_false){
		//The request_uri is PSI in New Invite to MCPTT
		tsip_dialog_request_configure_mcptt(TSIP_DIALOG(self));
	}


	if((request = tsip_dialog_request_new(dialog, "SUBSCRIBE"))){

		
		//MCPTT Insert parameter in contact
		if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_mcptt) == tmedia_mcptt){
			TSIP_HEADER_ADD_PARAM(request->Contact, "+g.3gpp.icsi-ref", "\"urn%3Aurn-7%3A3gpp-service.ims.icsi.mcptt\"");
			TSIP_HEADER_ADD_PARAM(request->Contact, "+g.3gpp.mcptt",tsk_null);
		}

		if((tsip_ssession_t->media.type & tmedia_affiliation) == tmedia_affiliation){


			//TSIP_HEADER_ADD_PARAM(request->Contact, "urn:urn-7:3gpp-service.ims.icsi.mcptt", tsk_null);
			//new param insert in headers P-Preferred-Service
			tsip_message_add_headers(request,
									 TSIP_HEADER_DUMMY_VA_ARGS("P-Preferred-Service", "urn:urn-7:3gpp-service.ims.icsi.mcptt"),
									 tsk_null);
			tsip_message_add_headers(request,
									 TSIP_HEADER_DUMMY_VA_ARGS("Accept", "application/pidf+xml"),
									 tsk_null);
			//it isn�t necessary
			/*tsip_message_add_headers(request,
				TSIP_HEADER_DUMMY_VA_ARGS("Content-Type",contentType),
				tsk_null);*/
			tsip_message_add_headers(request,
									 TSIP_HEADER_DUMMY_VA_ARGS("Event","presence"),
									 tsk_null);


			//add body

			//mcpt-info
			body_string = tsip_dialog_subscribe_create_mcpttinfo(self);
			if(body_string){
				tsip_message_add_content(request,contentType, body_string, tsk_strlen(body_string));
			}


		}
		else if((tsip_ssession_t->media.type & tmedia_cms) == tmedia_cms ||
                (tsip_ssession_t->media.type & tmedia_gms) == tmedia_gms){
			tmedia_multipart_body_t* body = tsk_null;
			tmedia_content_multipart_t* content = tsk_null;
			tmedia_content_multipart_t* mcptt_info_content = tsk_null;
			char* content_type_hdr  = tsk_null;



			//new param insert in headers P-Asserted-Service
            tsip_message_add_headers(request,
                                     TSIP_HEADER_DUMMY_VA_ARGS("P-Preferred-Service", "urn:urn-7:3gpp-service.ims.icsi.mcptt"),
                                     tsk_null);
			tsip_message_add_headers(request,
									 TSIP_HEADER_DUMMY_VA_ARGS("Accept", "application/xcap-diff+xml"),
									 tsk_null);

			tsip_message_add_headers(request,
									 TSIP_HEADER_DUMMY_VA_ARGS("Event","xcap-diff"),
									 tsk_null);


			//add body





            if(self->unsubscribing!=tsk_true && TSIP_DIALOG(self)->curr_action)
            if(TSIP_DIALOG(self)->curr_action->payload2 && TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload2)>0){
                //multipart with token
                body = tmedia_content_multipart_body_create("multipart/mixed", tsk_null);
                if(body)
                {
                    //mcptt-info with access token.
                    mcptt_info_content = tmedia_content_multipart_create(TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload2),  TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload2), "application/vnd.3gpp.mcptt-info+xml",tsk_null);
                    if(mcptt_info_content){
                        tmedia_content_multipart_body_add_content(body, mcptt_info_content);
                    }

                    content = tmedia_content_multipart_create( TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload), TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload),contentTypeResourceList,tsk_null);
                    if(content){
                        tmedia_content_multipart_body_add_content(body,content);
                    }
                    body_string = tmedia_content_multipart_body_tostring(body);
                    content_type_hdr = tmedia_content_multipart_body_get_header(body);
                }
                if(body_string){
                    tsip_message_add_content(request,content_type_hdr, body_string, tsk_strlen(body_string));
                }
            }
			else
            if( TSIP_DIALOG(self)->curr_action->payload &&
                    TSIP_DIALOG(self)->curr_action->payload!=tsk_null &&
                     TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload) &&
                     TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload)>0){
                //resource_list
                tsip_message_add_content(request,contentTypeResourceList, TSK_BUFFER_DATA(TSIP_DIALOG(self)->curr_action->payload), TSK_BUFFER_SIZE(TSIP_DIALOG(self)->curr_action->payload));
            }else{
                TSK_DEBUG_ERROR("it can not subscribe to the CMS, missing data for this purpose.");

            }





		}
		else {
            /* apply action params to the request */
            if(dialog->curr_action){
                tsip_dialog_apply_action(request,dialog->curr_action);
            }
            /* send the request */
		}



		ret = tsip_dialog_request_send(dialog, request);
		TSK_OBJECT_SAFE_FREE(request);
	}

	return ret;
}

static char* tsip_dialog_subscribe_create_mcpttinfo(const tsip_dialog_subscribe_t* self)
{
	tsip_stack_t* ptsip_stack_t;
	tsk_buffer_t* output = tsk_buffer_create_null();
	char* ret = tsk_null;
	tmedia_type_t type;
/*
#if HAVE_LIBXML2
	ret = tsk_null;
#else*/
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
	const char* tabEndMcpttURI= "</mcpttURI>";
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
	
	
	if((type & tmedia_affiliation) == tmedia_affiliation){
		//<mcptt-request-uri>
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
		
	ret = tsk_strndup(TSK_BUFFER_DATA(output), TSK_BUFFER_SIZE(output));
	TSK_OBJECT_SAFE_FREE(output);

	
	return ret;
}

int send_200NOTIFY(tsip_dialog_subscribe_t *self, const tsip_request_t* request)
{
	tmedia_type_t type;
	tsip_response_t *response;
	int ret = -1;
	tsip_dialog_t* dialog;
	
	dialog=TSIP_DIALOG(self);
	type=TSIP_DIALOG_GET_SS(self)->media.type;
	if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 200, "OK", request))){
		
		if((type & tmedia_affiliation) == tmedia_affiliation){
			//is notify affiliation
		}else
		if((type & tmedia_cms) == tmedia_cms){
			//is notify cms
		}else
		if((type & tmedia_gms) == tmedia_gms){
			//is notify gms
		}else
		{

		}
		ret = tsip_dialog_response_send(TSIP_DIALOG(self), response);
		TSK_OBJECT_SAFE_FREE(response);
	}
	return ret;
}

// process incoming notify: refresh delay and alert the user
int process_i_notify(tsip_dialog_subscribe_t *self, const tsip_request_t* notify)
{
	int control=0;
	//MCPTT AFFILIATION
	tmedia_type_t type=(TSIP_DIALOG_GET_SS(self))->media.type;

	if((type & tmedia_affiliation) == tmedia_affiliation ){
		//recieve Notify of affiliation
		TSK_DEBUG_INFO("Recieve Notify of affiliation");
		control++;
	}else
	if((type & tmedia_cms) == tmedia_cms ){
		//recieve Notify of cms
		TSK_DEBUG_INFO("Recieve Notify of CMS");
		control++;
	}else
	if((type & tmedia_gms) == tmedia_gms ){
		//recieve Notify of gms
		TSK_DEBUG_INFO("Recieve Notify of GMS");
		control++;
	}else
	{
		TSK_DEBUG_INFO("Recieve Notify");
	}

	if(!self || !notify){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}
	
	/* Request timeout for dialog refresh (re-registration). */
	self->timerrefresh.timeout = tsip_dialog_get_newdelay(TSIP_DIALOG(self), notify);
	TSIP_DIALOG_SUBSCRIBE_TIMER_SCHEDULE(refresh);
	
	/* Alert the user */
	TSIP_DIALOG_SUBSCRIBE_SIGNAL(self, tsip_i_notify, 
								 tsip_event_code_dialog_request_incoming, "Incoming NOTIFY.", notify);
	
	return 0;
}

/**
 * Callback function called by the state machine manager to signal that the final state has been reached.
 *
 * @param [in,out]	self	The state machine owner.
**/
int tsip_dialog_subscribe_OnTerminated(tsip_dialog_subscribe_t *self)
{
	TSK_DEBUG_INFO("=== SUBSCRIBE Dialog terminated ===");

	/* Alert the user */
	TSIP_DIALOG_SIGNAL_2(self, tsip_event_code_dialog_terminated,
			TSIP_DIALOG(self)->last_error.phrase ? TSIP_DIALOG(self)->last_error.phrase : "Dialog terminated",
			TSIP_DIALOG(self)->last_error.message);


	/* Remove from the dialog layer. */
	/*
    if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_affiliation) == tmedia_affiliation ||
            (TSIP_DIALOG_GET_SS(self)->media.type & tmedia_cms) == tmedia_cms ||
            (TSIP_DIALOG_GET_SS(self)->media.type & tmedia_gms) == tmedia_gms){
		TSIP_DIALOG(self)->expires = 0;
	}
	 */
	return tsip_dialog_remove(TSIP_DIALOG(self));
}














//========================================================
//	SIP dialog SUBSCRIBE object definition
//
static tsk_object_t* tsip_dialog_subscribe_ctor(tsk_object_t * self, va_list * app)
{
	tsip_dialog_subscribe_t *dialog = self;
	if(dialog){
		tsip_ssession_handle_t *ss = va_arg(*app, tsip_ssession_handle_t *);

		/* Initialize base class */
		tsip_dialog_init(TSIP_DIALOG(self), tsip_dialog_SUBSCRIBE, tsk_null, ss, _fsm_state_Started, _fsm_state_Terminated);

		/* FSM */
		TSIP_DIALOG_GET_FSM(self)->debug = DEBUG_STATE_MACHINE;
		tsk_fsm_set_callback_terminated(TSIP_DIALOG_GET_FSM(self), TSK_FSM_ONTERMINATED_F(tsip_dialog_subscribe_OnTerminated), (const void*)dialog);

		/* Initialize the class itself */
		tsip_dialog_subscribe_init(self);
	}
	return self;
}

static tsk_object_t* tsip_dialog_subscribe_dtor(tsk_object_t * _self)
{ 
	tsip_dialog_subscribe_t *self = _self;
	if(self){
		/* Cancel all timers */
		TSIP_DIALOG_TIMER_CANCEL(refresh);
		TSIP_DIALOG_TIMER_CANCEL(shutdown);

		/* DeInitialize base class (will cancel all transactions) */
		tsip_dialog_deinit(TSIP_DIALOG(self));

		TSK_DEBUG_INFO("*** SUBSCRIBE Dialog destroyed ***");
	}

	return self;
}

static int tsip_dialog_subscribe_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return tsip_dialog_cmp(obj1, obj2);
}

static const tsk_object_def_t tsip_dialog_subscribe_def_s = 
{
	sizeof(tsip_dialog_subscribe_t),
	tsip_dialog_subscribe_ctor, 
	tsip_dialog_subscribe_dtor,
	tsip_dialog_subscribe_cmp, 
};
const tsk_object_def_t *tsip_dialog_subscribe_def_t = &tsip_dialog_subscribe_def_s;
