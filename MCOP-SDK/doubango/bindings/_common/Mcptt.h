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
#ifndef TINYWRAP_MCPTT_H
#define TINYWRAP_MCPTT_H

#include "tinyWRAP_config.h"

#include "tinymcptt.h"

class CallSession;
class McpttSession;

class TINYWRAP_API McpttMessage
{
public:
	McpttMessage();
#if !defined(SWIG)
	McpttMessage(tmcptt_message_t *message);
#endif
	virtual ~McpttMessage();

  short getRCode();
  const char* getPhrase();
  const char* getUser();
  short getParticipants();
  short getTime();

#if !defined(SWIG)
	const tmcptt_message_t* getWrappedMcpttMessage() { return m_pMessage; }
#endif

private:
	tmcptt_message_t *m_pMessage;
};

class TINYWRAP_API McpttEvent
{
public:
#if !defined(SWIG)
	McpttEvent(const tmcptt_event_t *_event);
#endif
	virtual ~McpttEvent();

	tmcptt_event_type_t getType();
	const CallSession* getSipSession();
	const McpttMessage* getMessage() const;

protected:
	const tmcptt_event_t *_event;
	McpttMessage* m_pMessage;
};

class TINYWRAP_API McpttCallback
{
public:
	McpttCallback() {  }
	virtual ~McpttCallback() {}
	virtual int OnEvent(const McpttEvent* e) { return -1; }
};
class TINYWRAP_API McpttMbmsMessage
{
public:
	McpttMbmsMessage();
#if !defined(SWIG)
	McpttMbmsMessage(tmcptt_mbms_message_t *message);
#endif
	virtual ~McpttMbmsMessage();

  const char* getGroupId();
  const char* getTMGI();
  const char* getMediaIP();
  short getMediaPort();
  short getMediaControlPort();

#if !defined(SWIG)
	const tmcptt_mbms_message_t* getWrappedMcpttMbmsMessage() { return m_pMessage; }
#endif

private:
	tmcptt_mbms_message_t *m_pMessage;
};

class TINYWRAP_API McpttMbmsEvent
{
public:
#if !defined(SWIG)
	McpttMbmsEvent(const tmcptt_mbms_event_t *_event);
#endif
	virtual ~McpttMbmsEvent();

	tmcptt_mbms_event_type_t getType();
	const CallSession* getSipSession();
	const McpttMbmsMessage* getMessage() const;

protected:
	const tmcptt_mbms_event_t *_event;
	McpttMbmsMessage* m_pMessage;
};

class TINYWRAP_API McpttMbmsCallback
{
public:
	McpttMbmsCallback() {  }
	virtual ~McpttMbmsCallback() {}
	virtual int OnEvent(const McpttMbmsEvent* e) { return -1; }
};
#if !defined(SWIG)
int twrap_mcptt_cb(const tmcptt_event_t* _event);
#endif
#if !defined(SWIG)
int twrap_mcptt_mbms_cb(const tmcptt_mbms_event_t* _event);
#endif
#endif /* TINYWRAP_MSRP_H */
