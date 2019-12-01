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
#ifndef TINYWRAP_SIP_DEBUG_H
#define TINYWRAP_SIP_DEBUG_H

class DDebugCallback
{
public:
	DDebugCallback() {  }
	virtual ~DDebugCallback() {}

	virtual int OnDebugTest(const char* message) { return -1; }
	virtual int OnDebugInfo(const char* message) { return -1; }
	virtual int OnDebugWarn(const char* message) { return -1; }
	virtual int OnDebugError(const char* message) { return -1; }
	virtual int OnDebugFatal(const char* message) { return -1; }

#if !defined(SWIG)
public:
	static int debug_test_cb(const void* arg, const char* fmt, ...);
	static int debug_info_cb(const void* arg, const char* fmt, ...);
	static int debug_warn_cb(const void* arg, const char* fmt, ...);
	static int debug_error_cb(const void* arg, const char* fmt, ...);
	static int debug_fatal_cb(const void* arg, const char* fmt, ...);
#endif

private:
	
};

#endif /* TINYWRAP_SIP_DEBUG_H */
