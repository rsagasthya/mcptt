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
#ifndef TINYWRAP_SIPCALLBACK_H
#define TINYWRAP_SIPCALLBACK_H

#include "SipEvent.h"

class DialogEvent;
class StackEvent;

class InviteEvent;
class MessagingEvent;
class MessagingLocationEvent;
//MCPTT affiliation
class MessagingAffiliationEvent;
//MCPTT MBMS
class MessagingMbmsEvent;
class InfoEvent;
class OptionsEvent;
class PublicationEvent;
//MCPTT affiliation
class PublicationAffiliationEvent;
class RegistrationAuthenticationEvent;
class RegistrationEvent;
class SubscriptionEvent;
//MCPTT affiliation
class SubscriptionAffiliationEvent;
//MCPTT authentication
class PublicationAuthenticationEvent;
class SipCallback
{
public:
	SipCallback() {  }
	virtual ~SipCallback() {}
	virtual int OnDialogEvent(const DialogEvent* e) { return -1; }
	virtual int OnStackEvent(const StackEvent* e) { return -1; }
	virtual int OnInviteEvent(const InviteEvent* e) { return -1; }
	virtual int OnMessagingEvent(const MessagingEvent* e) { return -1; }
	//MCPTT affiliation
	virtual int OnMessagingAffiliationEvent(const MessagingAffiliationEvent* e) { return -1; }
	//MCPTT MBMS
	virtual int OnMessagingMbmsEvent(const MessagingMbmsEvent* e) { return -1; }
	virtual int OnMessagingLocationEvent(const MessagingLocationEvent* e) { return -1; }

	virtual int OnInfoEvent(const InfoEvent* e) { return -1; }
	virtual int OnOptionsEvent(const OptionsEvent* e) { return -1; }
	virtual int OnPublicationEvent(const PublicationEvent* e) { return -1; }
	//MCPTT affiliation
	virtual int OnPublicationAffiliationEvent(const PublicationAffiliationEvent* e) { return -1; }
	//MCPTT authentication
	virtual int OnPublicationAuthenticationEvent(const PublicationAuthenticationEvent* e) { return -1; }
	virtual int OnRegistrationAuthenticationEvent(const RegistrationAuthenticationEvent* e) { return -1; }
	virtual int OnRegistrationEvent(const RegistrationEvent* e) { return -1; }
	virtual int OnSubscriptionEvent(const SubscriptionEvent* e) { return -1; }
	//MCPTT affiliation
	virtual int OnSubscriptionAffiliationEvent(const SubscriptionAffiliationEvent* e) { return -1; }
	//MCPTT CMS
	virtual int OnSubscriptionCMSEvent(const SubscriptionCMSEvent* e) { return -1; }
	//MCPTT GMS
	virtual int OnSubscriptionGMSEvent(const SubscriptionGMSEvent* e) { return -1; }

	

private:
	
};

#endif /* TINYWRAP_SIPCALLBACK_H */
