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
#include "Mcptt.h"

#include "SipSession.h"
//#include "Common.h"

/* ======================== McpttMessage ========================*/
McpttMessage::McpttMessage()
:m_pMessage(tsk_null)
{
}

McpttMessage::McpttMessage(tmcptt_message_t *_message)
{
	m_pMessage = (tmcptt_message_t *)tsk_object_ref(_message);
}

McpttMessage::~McpttMessage()
{
	TSK_OBJECT_SAFE_FREE(m_pMessage);
}


short McpttMessage::getRCode()
{
	return m_pMessage->reason_code;
}

const char* McpttMessage::getPhrase()
{
	return m_pMessage->reason_phrase;
}

const char* McpttMessage::getUser()
{
	return m_pMessage->user;
}

short McpttMessage::getParticipants()
{
	return m_pMessage->participants;
}

short McpttMessage::getTime()
{
	return m_pMessage->time;
}

/* ======================== McpttEvent ========================*/
McpttEvent::McpttEvent(const tmcptt_event_t *_mcpttevent)
{
	this->_event = _mcpttevent;
	if(this->_event && this->_event->message){	
		m_pMessage = new McpttMessage((tmcptt_message_t *)this->_event->message);
	}
	else{
		m_pMessage = tsk_null;
	}
}

McpttEvent::~McpttEvent()
{
	if(m_pMessage){
		delete m_pMessage;
	}
}

tmcptt_event_type_t McpttEvent::getType()
{
	if(this->_event){
		return this->_event->type;
	}
	return tmcptt_event_type_none;
}

const CallSession* McpttEvent::getSipSession()
{
	if(this->_event && this->_event->callback_data){
		return dyn_cast<const CallSession*>((const CallSession*)this->_event->callback_data);
	}
	return tsk_null;
}

const McpttMessage* McpttEvent::getMessage() const
{
	return m_pMessage;
}
/* ======================== McpttMbmsMessage ========================*/
McpttMbmsMessage::McpttMbmsMessage()
:m_pMessage(tsk_null)
{
}

McpttMbmsMessage::McpttMbmsMessage(tmcptt_mbms_message_t *_message)
{
	m_pMessage = (tmcptt_mbms_message_t *)tsk_object_ref(_message);
}

McpttMbmsMessage::~McpttMbmsMessage()
{
	TSK_OBJECT_SAFE_FREE(m_pMessage);
}

const char* McpttMbmsMessage::getGroupId()
{
	return m_pMessage->group_id;
}

const char* McpttMbmsMessage::getTMGI()
{
	return m_pMessage->tmgi;
}

const char* McpttMbmsMessage::getMediaIP()
{
	return m_pMessage->media_ip;
}

short McpttMbmsMessage::getMediaPort()
{
	return m_pMessage->media_port;
}
//getMediaControlPort test1
short McpttMbmsMessage::getMediaControlPort()
{
	return m_pMessage->media_control_port;
}

/* ======================== McpttMbmsEvent ========================*/
McpttMbmsEvent::McpttMbmsEvent(const tmcptt_mbms_event_t *_mcpttmbmsevent)
{
	this->_event = _mcpttmbmsevent;
	if(this->_event && this->_event->message){	
		m_pMessage = new McpttMbmsMessage((tmcptt_mbms_message_t *)this->_event->message);
	}
	else{
		m_pMessage = tsk_null;
	}
}

McpttMbmsEvent::~McpttMbmsEvent()
{
	if(m_pMessage){
		delete m_pMessage;
	}
}

tmcptt_mbms_event_type_t McpttMbmsEvent::getType()
{
	if(this->_event){
		return this->_event->type;
	}
	return tmcptt_mbms_event_type_none;
}

const CallSession* McpttMbmsEvent::getSipSession()
{
	if(this->_event && this->_event->callback_data){
		return dyn_cast<const CallSession*>((const CallSession*)this->_event->callback_data);
	}
	return tsk_null;
}

const McpttMbmsMessage* McpttMbmsEvent::getMessage() const
{
	return m_pMessage;
}
/**
CALLBACK
*/
int twrap_mcptt_cb(const tmcptt_event_t* _event) 
{
	const CallSession* session = dyn_cast<const CallSession*>((const CallSession*)_event->callback_data);
	if(session && session->getMcpttCallback()){
		const McpttEvent* e = new McpttEvent(_event);
		int ret = const_cast<McpttCallback*>(session->getMcpttCallback())->OnEvent(e);
		return ret;
	}
	return 0;
}
int twrap_mcptt_mbms_cb(const tmcptt_mbms_event_t* _event) 
{
	const CallSession* session = dyn_cast<const CallSession*>((const CallSession*)_event->callback_data);
	if(session && session->getMcpttMbmsCallback()){
		const McpttMbmsEvent* e = new McpttMbmsEvent(_event);
		int ret = const_cast<McpttMbmsCallback*>(session->getMcpttMbmsCallback())->OnEvent(e);
		return ret;
	}
	return 0;
}
