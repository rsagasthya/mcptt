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

#ifndef TINYMCPTT_CONFIG_H
#define TINYMCPTT_CONFIG_H

#ifdef __SYMBIAN32__
#undef _WIN32 /* Because of WINSCW */
#endif

// Windows (XP/Vista/7/CE and Windows Mobile) macro definition.
#if defined(WIN32)|| defined(_WIN32) || defined(_WIN32_WCE)
#	define TMCPTT_UNDER_WINDOWS	1
#	if defined(_WIN32_WCE) || defined(UNDER_CE)
#		define TMCPTT_UNDER_WINDOWS_CE	1
#	endif
#	if defined(WINAPI_FAMILY) && (WINAPI_FAMILY == WINAPI_FAMILY_PHONE_APP || WINAPI_FAMILY == WINAPI_FAMILY_APP)
#		define TMCPTT_UNDER_WINDOWS_RT		1
#	endif
#endif

#if (TMCPTT_UNDER_WINDOWS || defined(__SYMBIAN32__)) && defined(TINYMCPTT_EXPORTS)
# 	define TINYMCPTT_API		__declspec(dllexport)
# 	define TINYMCPTT_GEXTERN __declspec(dllexport)
#elif (TMCPTT_UNDER_WINDOWS || defined(__SYMBIAN32__)) && !defined(TINYMCPTT_IMPORTS_IGNORE)
# 	define TINYMCPTT_API __declspec(dllimport)
# 	define TINYMCPTT_GEXTERN __declspec(dllimport)
#else
#	define TINYMCPTT_API
#	define TINYMCPTT_GEXTERN	extern
#endif

/* Guards against C++ name mangling 
*/
#ifdef __cplusplus
#	define TMCPTT_BEGIN_DECLS extern "C" {
#	define TMCPTT_END_DECLS }
#else
#	define TMCPTT_BEGIN_DECLS 
#	define TMCPTT_END_DECLS
#endif

/* Disable some well-known warnings
*/
#ifdef _MSC_VER
#	define _CRT_SECURE_NO_WARNINGS
#endif

/* Detecting C99 compilers
 */
#if (__STDC_VERSION__ == 199901L) && !defined(__C99__)
#	define __C99__
#endif

#include <stdint.h>
#ifdef __SYMBIAN32__
#   include <stdlib.h>
#endif

#if defined(__APPLE__)
#   include <TargetConditionals.h>
#endif

// http://code.google.com/p/idoubs/issues/detail?id=111
#if TARGET_IPHONE_SIMULATOR

#endif

#if HAVE_CONFIG_H
	#include <config.h>
#endif

#endif // TINYMCPTT_CONFIG_H
