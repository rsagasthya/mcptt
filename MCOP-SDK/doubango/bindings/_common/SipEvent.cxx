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
#include "SipEvent.h"
#include "SipSession.h"
#include "SipMessage.h"

#include "SipStack.h"


#define takeOwnership_Implement(cls, name, session) \
name##Session* cls##Event::take##session##Ownership() const \
{ \
	if(this->sipevent && this->sipevent->ss /*&& !tsip_ssession_have_ownership(this->sipevent->ss)*/){ \
		SipStack* stack = this->getStack(); \
		if(stack){ \
			/* The constructor will call take_ownerhip() */ \
			return new name##Session(stack, this->sipevent->ss); \
		} \
	} \
	return tsk_null; \
} \

/* ======================== SipEvent ========================*/
SipEvent::SipEvent(const tsip_event_t *_sipevent)
{
	this->sipevent = _sipevent;
	if(_sipevent){
		this->sipmessage = new SipMessage(_sipevent->sipmessage);
	}
	else{
		this->sipmessage = tsk_null;
	}
}

SipEvent::~SipEvent()
{
	if(this->sipmessage){
		delete this->sipmessage;
	}
}

short SipEvent::getCode() const
{
	return this->sipevent->code;
}

const char* SipEvent::getPhrase() const
{
	return this->sipevent->phrase;
}

const SipSession* SipEvent::getBaseSession() const
{
	const void* userdata = tsip_ssession_get_userdata(this->sipevent->ss);
	if(userdata){
		return dyn_cast<const SipSession*>((const SipSession*)userdata);
	}
	return tsk_null;
}

const SipMessage* SipEvent::getSipMessage() const
{
	return this->sipmessage;
}

SipStack* SipEvent::getStack()const
{
	const tsip_stack_handle_t* stack_handle = tsip_ssession_get_stack(sipevent->ss);
	const void* userdata;
	if(stack_handle && (userdata = tsip_stack_get_userdata(stack_handle))){
		return dyn_cast<SipStack*>((SipStack*)userdata);
	}
	return tsk_null;
}


/* ======================== DialogEvent ========================*/
DialogEvent::DialogEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent){ }

DialogEvent::~DialogEvent(){ }


/* ======================== DialogEvent ========================*/
StackEvent::StackEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent){ }

StackEvent::~StackEvent(){ }


/* ======================== InviteEvent ========================*/
InviteEvent::InviteEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

InviteEvent::~InviteEvent()
{
}

tsip_invite_event_type_t InviteEvent::getType() const
{
	return TSIP_INVITE_EVENT(this->sipevent)->type;
}

twrap_media_type_t InviteEvent::getMediaType() const
{
	// Ignore Mixed session (both audio/video and MSRP) as specified by GSMA RCS.
	if (this->sipevent && this->sipevent->ss) {
		tmedia_type_t type = tsip_ssession_get_mediatype(this->sipevent->ss);
		if ((type & tmedia_msrp) == tmedia_msrp) {
			return twrap_media_msrp;
		}
		else {
			return twrap_get_wrapped_media_type(type);
		}
	}
	return twrap_media_none;
}

const InviteSession* InviteEvent::getSession() const
{
	return dyn_cast<const InviteSession*>(this->getBaseSession());
}

takeOwnership_Implement(Invite, Call, CallSession);
takeOwnership_Implement(Invite, Msrp, MsrpSession);

/* ======================== MessagingEvent ========================*/
MessagingEvent::MessagingEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

MessagingEvent::~MessagingEvent()
{
}

tsip_message_event_type_t MessagingEvent::getType() const
{
	return TSIP_MESSAGE_EVENT(this->sipevent)->type;
}

const MessagingSession* MessagingEvent::getSession() const
{
	return dyn_cast<const MessagingSession*>(this->getBaseSession());
}

takeOwnership_Implement(Messaging, Messaging, Session);
/* ======================== MessagingLocationEvent ========================*/
MessagingLocationEvent::MessagingLocationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

MessagingLocationEvent::~MessagingLocationEvent()
{
}

tsip_message_event_type_t MessagingLocationEvent::getType() const
{
	return TSIP_MESSAGE_EVENT(this->sipevent)->type;
}

const MessagingLocationSession* MessagingLocationEvent::getSession() const
{
	return dyn_cast<const MessagingLocationSession*>(this->getBaseSession());
}

takeOwnership_Implement(MessagingLocation, MessagingLocation, Session);
//takeSessionOwnership


/* ======================== MessagingAffiliationEvent ========================*/
MessagingAffiliationEvent::MessagingAffiliationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

MessagingAffiliationEvent::~MessagingAffiliationEvent()
{
}

tsip_message_event_type_t MessagingAffiliationEvent::getType() const
{
	return TSIP_MESSAGE_EVENT(this->sipevent)->type;
}

const MessagingAffiliationSession* MessagingAffiliationEvent::getSession() const
{
	return dyn_cast<const MessagingAffiliationSession*>(this->getBaseSession());
}

takeOwnership_Implement(MessagingAffiliation, MessagingAffiliation, Session);


/* ======================== MessagingMbmsEvent ========================*/
MessagingMbmsEvent::MessagingMbmsEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

MessagingMbmsEvent::~MessagingMbmsEvent()
{
}

tsip_message_event_type_t MessagingMbmsEvent::getType() const
{
	return TSIP_MESSAGE_EVENT(this->sipevent)->type;
}

const MessagingMbmsSession* MessagingMbmsEvent::getSession() const
{
	return dyn_cast<const MessagingMbmsSession*>(this->getBaseSession());
}

takeOwnership_Implement(MessagingMbms, MessagingMbms, Session);



/* ======================== InfoEvent ========================*/
InfoEvent::InfoEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

InfoEvent::~InfoEvent()
{
}

tsip_info_event_type_t InfoEvent::getType() const
{
	return TSIP_INFO_EVENT(this->sipevent)->type;
}

const InfoSession* InfoEvent::getSession() const
{
	return dyn_cast<const InfoSession*>(this->getBaseSession());
}

takeOwnership_Implement(Info, Info, Session);



/* ======================== OptionsEvent ========================*/
OptionsEvent::OptionsEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

OptionsEvent::~OptionsEvent()
{
}

tsip_options_event_type_t OptionsEvent::getType() const
{
	return TSIP_OPTIONS_EVENT(this->sipevent)->type;
}

const OptionsSession* OptionsEvent::getSession() const
{
	return dyn_cast<const OptionsSession*>(this->getBaseSession());
}

takeOwnership_Implement(Options, Options, Session);


/* ======================== PublicationEvent ========================*/
PublicationEvent::PublicationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

PublicationEvent::~PublicationEvent()
{
}

tsip_publish_event_type_t PublicationEvent::getType() const
{
	return TSIP_PUBLISH_EVENT(this->sipevent)->type;
}

const PublicationSession* PublicationEvent::getSession() const
{
	return dyn_cast<const PublicationSession*>(this->getBaseSession());
}

takeOwnership_Implement(Publication, Publication, Session);

/* ======================== PublicationAffiliationEvent ========================*/
PublicationAffiliationEvent::PublicationAffiliationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

PublicationAffiliationEvent::~PublicationAffiliationEvent()
{
}

tsip_publish_event_type_t PublicationAffiliationEvent::getType() const
{
	return TSIP_PUBLISH_EVENT(this->sipevent)->type;
}

const PublicationAffiliationSession* PublicationAffiliationEvent::getSession() const
{
	return dyn_cast<const PublicationAffiliationSession*>(this->getBaseSession());
}

takeOwnership_Implement(PublicationAffiliation, PublicationAffiliation, Session);
/* ======================== PublicationAuthenticationEvent ========================*/
PublicationAuthenticationEvent::PublicationAuthenticationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

PublicationAuthenticationEvent::~PublicationAuthenticationEvent()
{
}

tsip_publish_event_type_t PublicationAuthenticationEvent::getType() const
{
	return TSIP_PUBLISH_EVENT(this->sipevent)->type;
}

const PublicationAuthenticationSession* PublicationAuthenticationEvent::getSession() const
{
	return dyn_cast<const PublicationAuthenticationSession*>(this->getBaseSession());
}

takeOwnership_Implement(PublicationAuthentication, PublicationAuthentication, Session);


/* ======================== RegistrationAuthenticationEvent ========================*/
RegistrationAuthenticationEvent::RegistrationAuthenticationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

RegistrationAuthenticationEvent::~RegistrationAuthenticationEvent()
{
}

tsip_register_authentication_event_type_t RegistrationAuthenticationEvent::getType() const
{
	return TSIP_REGISTER_AUTHENTICATION_EVENT(this->sipevent)->type;
}

//takeOwnership_Implement(RegistrationAuthentication, RegistrationAuthentication, Session);


/* ======================== RegistrationEvent ========================*/
RegistrationEvent::RegistrationEvent(const tsip_event_t *_sipevent)
:SipEvent(_sipevent)
{
}

RegistrationEvent::~RegistrationEvent()
{
}

tsip_register_event_type_t RegistrationEvent::getType() const
{
	return TSIP_REGISTER_EVENT(this->sipevent)->type;
}

const RegistrationSession* RegistrationEvent::getSession() const
{
	return dyn_cast<const RegistrationSession*>(this->getBaseSession());
}

takeOwnership_Implement(Registration, Registration, Session);


/* ======================== SubscriptionEvent ========================*/
SubscriptionEvent::SubscriptionEvent(const tsip_event_t *sipevent)
:SipEvent(sipevent)
{
}

SubscriptionEvent::~SubscriptionEvent()
{
}

tsip_subscribe_event_type_t SubscriptionEvent::getType() const
{
	return TSIP_SUBSCRIBE_EVENT(this->sipevent)->type;
}

const SubscriptionSession* SubscriptionEvent::getSession() const
{
	return dyn_cast<const SubscriptionSession*>(this->getBaseSession());
}

takeOwnership_Implement(Subscription, Subscription, Session);

/* ======================== SubscriptionAffiliationEvent ========================*/
SubscriptionAffiliationEvent::SubscriptionAffiliationEvent(const tsip_event_t *sipevent)
		:SipEvent(sipevent)
{
}

SubscriptionAffiliationEvent::~SubscriptionAffiliationEvent()
{
}

tsip_subscribe_event_type_t SubscriptionAffiliationEvent::getType() const
{
	return TSIP_SUBSCRIBE_EVENT(this->sipevent)->type;
}

const SubscriptionAffiliationSession* SubscriptionAffiliationEvent::getSession() const
{
	return dyn_cast<const SubscriptionAffiliationSession*>(this->getBaseSession());
}

takeOwnership_Implement(SubscriptionAffiliation, SubscriptionAffiliation, Session);

/* ======================== SubscriptionCMSEvent ========================*/
SubscriptionCMSEvent::SubscriptionCMSEvent(const tsip_event_t *sipevent)
		:SipEvent(sipevent)
{
}

SubscriptionCMSEvent::~SubscriptionCMSEvent()
{
}

tsip_subscribe_event_type_t SubscriptionCMSEvent::getType() const
{
	return TSIP_SUBSCRIBE_EVENT(this->sipevent)->type;
}

const SubscriptionCMSSession* SubscriptionCMSEvent::getSession() const
{
	return dyn_cast<const SubscriptionCMSSession*>(this->getBaseSession());
}

takeOwnership_Implement(SubscriptionCMS, SubscriptionCMS, Session);

/* ======================== SubscriptionGMSEvent ========================*/
SubscriptionGMSEvent::SubscriptionGMSEvent(const tsip_event_t *sipevent)
		:SipEvent(sipevent)
{
}

SubscriptionGMSEvent::~SubscriptionGMSEvent()
{
}

tsip_subscribe_event_type_t SubscriptionGMSEvent::getType() const
{
	return TSIP_SUBSCRIBE_EVENT(this->sipevent)->type;
}

const SubscriptionGMSSession* SubscriptionGMSEvent::getSession() const
{
	return dyn_cast<const SubscriptionGMSSession*>(this->getBaseSession());
}

takeOwnership_Implement(SubscriptionGMS, SubscriptionGMS, Session);
