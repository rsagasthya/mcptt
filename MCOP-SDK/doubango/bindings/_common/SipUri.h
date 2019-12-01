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
#ifndef TINYWRAP_SIPURI_H
#define TINYWRAP_SIPURI_H

#include "tinyWRAP_config.h"

#include "tinysip.h"

class TINYWRAP_API SipUri
{
public:
	SipUri(const char* uriString, const char* displayName=tsk_null);
	~SipUri();

public:
	static bool isValid(const char*);

	bool isValid();
	const char* getScheme();
	const char* getHost();
	unsigned short getPort();
	const char* getUserName();
	const char* getPassword();
	const char* getDisplayName();
	const char* getParamValue(const char* pname);
	void setDisplayName(const char* displayName);
#if !defined(SWIG)
	inline const tsip_uri_t* getWrappedUri()const{
		return m_pUri;
	}
#endif

private:
	tsip_uri_t* m_pUri;
};

#endif /* TINYWRAP_SIPURI_H */
