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

#ifndef TINYMEDIA_CONTENT_MULTIPART_H
#define TINYMEDIA_CONTENT_MULTIPART_H

#include "tinymedia_config.h"

#include "tsk_buffer.h"
#include "tsk_list.h"

TMEDIA_BEGIN_DECLS

typedef struct tmedia_content_multipart_s
{
	TSK_DECLARE_OBJECT;

	char* data;
	tsk_size_t data_size;
	const char* content_type;
	const char* content_disposition;
}
tmedia_content_multipart_t;

typedef tsk_list_t tmedia_content_multipart_L_t;

TINYMEDIA_API tmedia_content_multipart_t* tmedia_content_multipart_create(const char* data, tsk_size_t data_size, const char* content_type, const char* content_disposition);
TINYMEDIA_API tmedia_content_multipart_t* tmedia_content_multipart_parse(const void* data, tsk_size_t size);

TINYMEDIA_API int tmedia_content_multipart_init(tmedia_content_multipart_t* self);
TINYMEDIA_API int tmedia_content_multipart_deinit(tmedia_content_multipart_t* self);
TINYMEDIA_API char* tmedia_content_multipart_tostring(tmedia_content_multipart_t* self);

TINYMEDIA_GEXTERN const tsk_object_def_t* tmedia_content_multipart_def_t;


typedef struct tmedia_multipart_body_s
{
	TSK_DECLARE_OBJECT;

	tmedia_content_multipart_L_t* contents;
	char* boundary;
	const char* multipart_type;
}
tmedia_multipart_body_t;

TINYMEDIA_API tmedia_multipart_body_t* tmedia_content_multipart_body_create(const char* multipart_type, const char* boundary);
TINYMEDIA_API tmedia_multipart_body_t* tmedia_content_multipart_body_parse(const void* data, tsk_size_t sizeconst, char* multipart_type, const char* boundary);

TINYMEDIA_API int tmedia_content_multipart_body_init(tmedia_multipart_body_t* self);
TINYMEDIA_API int tmedia_content_multipart_body_deinit(tmedia_multipart_body_t* self);
TINYMEDIA_API int tmedia_content_multipart_body_add_content(tmedia_multipart_body_t* self, tmedia_content_multipart_t* content);
TINYMEDIA_API tmedia_content_multipart_t* tmedia_content_multipart_body_get_content(tmedia_multipart_body_t* self, const char* content_type);
TINYMEDIA_API char* tmedia_content_multipart_body_tostring(tmedia_multipart_body_t* self);
TINYMEDIA_API char* tmedia_content_multipart_body_get_header(tmedia_multipart_body_t* self);


TINYMEDIA_GEXTERN const tsk_object_def_t* tmedia_multipart_body_def_t;

TMEDIA_END_DECLS

#endif /* TINYMEDIA_CONTENT_MULTIPART_H */