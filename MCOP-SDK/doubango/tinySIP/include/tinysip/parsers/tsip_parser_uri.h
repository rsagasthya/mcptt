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

/**@file tsip_parser_uri.h
 * @brief SIP/SIPS/TEL URI parser.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#ifndef TINYSIP_PARSER_URI_H
#define TINYSIP_PARSER_URI_H

#include "tinysip_config.h"
#include "tinysip/tsip_uri.h"

#include "tsk_ragel_state.h"

TSIP_BEGIN_DECLS

TINYSIP_API tsip_uri_t *tsip_uri_parse(const char *data, tsk_size_t size);

TSIP_END_DECLS

#endif /* TINYSIP_PARSER_URI_H */

