#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
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


/**@file tmedia_content_multipart.c
 * @brief Base multipart content object.
 */
#include "tinymedia/content/tmedia_content_multipart.h"

#include "tsk_memory.h"
#include "tsk_string.h"
#include "tsk_debug.h"
#include "tsk_uuid.h"

tmedia_content_multipart_t* tmedia_content_multipart_create(const char* data, tsk_size_t data_size, const char* content_type, const char* content_disposition)
{
	tmedia_content_multipart_t* content = tsk_null;
	if(!(content = tsk_object_new(tmedia_content_multipart_def_t))){
		return tsk_null;
	}

	if(data == tsk_null || data_size == 0)
	{
		TSK_OBJECT_SAFE_FREE(content);
		return tsk_null;
	}
	#if HAVE_CRT //Debug memory
	content->data = (char*)calloc(data_size, sizeof(char));
		
	#else
	content->data = (char*)tsk_calloc(data_size, sizeof(char));
		
	#endif //HAVE_CRT
	memcpy(content->data, data, data_size);
	content->data_size = data_size;

	if(content_type != tsk_null)
		content->content_type = tsk_strdup(content_type);
	else
		content->content_type = tsk_null;

	if(content_disposition != tsk_null)
		content->content_disposition = tsk_strdup(content_disposition);
	else
		content->content_disposition = tsk_null;

	return content;
}

tmedia_content_multipart_t* tmedia_content_multipart_parse(const void* data, tsk_size_t size)
{
	//TO-DO
	tmedia_content_multipart_t* mp_content = tsk_null;
	const char* content_type_hdr = "Content-Type: ";
	char* content_type_value = tsk_null;
	const char* content_disp_hdr = "Content-Disposition: ";
	const char* CRLF = "\r\n";
	const char* DBL_CRLF = "\r\n\r\n";
	char* content_disp_value = tsk_null;
	int start_index = 0, stop_index = 0, value_size = 0;
	char* content_buffer = (char*)data;
	
	start_index = tsk_strindexOf(content_buffer, size, content_type_hdr);
	if(start_index >= 0)
	{
		start_index += strlen(content_type_hdr);
		value_size = tsk_strindexOf(&content_buffer[start_index], size - start_index, CRLF);
		if(value_size > 0)
		{
			#if HAVE_CRT //Debug memory
				content_type_value = (char*)calloc((value_size + 1), sizeof(char));
	
	#else
				content_type_value = (char*)tsk_calloc((value_size + 1), sizeof(char));
	
	#endif //HAVE_CRT
			memcpy(content_type_value, &content_buffer[start_index], value_size);
			content_type_value[value_size] = '\0';
		}
	}

	start_index = tsk_strindexOf(content_buffer, size, content_disp_hdr);
	if(start_index >= 0)
	{
		start_index += strlen(content_disp_hdr);
		value_size = tsk_strindexOf(&content_buffer[start_index], size - start_index, CRLF);
		if(value_size > 0)
		{
			#if HAVE_CRT //Debug memory
					content_disp_value = (char*)calloc((value_size + 1), sizeof(char));

	#else
					content_disp_value = (char*)tsk_calloc((value_size + 1), sizeof(char));

	#endif //HAVE_CRT
			memcpy(content_disp_value, &content_buffer[start_index], value_size);
			content_disp_value[value_size] = '\0';
		}
	}

	start_index = tsk_strindexOf(content_buffer, size, DBL_CRLF);

	if(start_index < 0)
	{
		TSK_FREE(content_type_value);
	    TSK_FREE(content_disp_value);
		return tsk_null;
	}
	
	start_index += strlen(DBL_CRLF);

	//If there is a final double CRLF, delete one
	stop_index = tsk_strindexOf(&content_buffer[start_index], size - start_index, DBL_CRLF);
	if(stop_index > 0)
		stop_index += start_index + 2;
	else
		stop_index = size;

	mp_content = tmedia_content_multipart_create(&content_buffer[start_index], stop_index - start_index, content_type_value, content_disp_value);
	
	TSK_FREE(content_type_value);
	TSK_FREE(content_disp_value);
	return mp_content;
}

char* tmedia_content_multipart_tostring(tmedia_content_multipart_t* self)
{
	char* string = tsk_null;

	if(!self) {
		return tsk_null;
	}

	if(self->content_type && !tsk_strempty(self->content_type))
		tsk_sprintf(&string, "Content-Type: %s", self->content_type);

	if(self->content_disposition && !tsk_strempty(self->content_disposition))
		tsk_strcat_2(&string, "\r\nContent-Disposition: %s", self->content_disposition);

	tsk_strcat_2(&string, "\r\n\r\n%.*s", self->data_size, self->data);

	return string;
}


int tmedia_content_multipart_init(tmedia_content_multipart_t* self)
{
	if (!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	return 0;
}

int tmedia_content_multipart_deinit(tmedia_content_multipart_t* self)
{
	if (!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	TSK_FREE(self->data);
	self->data_size = 0;
	TSK_FREE(self->content_type);
	TSK_FREE(self->content_disposition);

	return 0;
}

/* constructor */
static tsk_object_t* tmedia_content_multipart_ctor(tsk_object_t * self, va_list * app)
{
	tmedia_content_multipart_t *content = self;
	if (content){
	}
	return self;
}
/* destructor */
static tsk_object_t* tmedia_content_multipart_dtor(tsk_object_t * self)
{
	tmedia_content_multipart_t *content = self;
	if (content){
		tmedia_content_multipart_deinit(content);
	}

	return self;
}

static const tsk_object_def_t tmedia_content_multipart_def_s =
{
	sizeof(tmedia_content_multipart_t),
	tmedia_content_multipart_ctor,
	tmedia_content_multipart_dtor,
	tsk_null,
};
const tsk_object_def_t *tmedia_content_multipart_def_t = &tmedia_content_multipart_def_s;


tmedia_multipart_body_t* tmedia_content_multipart_body_create(const char* multipart_type, const char* boundary)
{
	tmedia_multipart_body_t* content = tsk_null;
	if(!(content = tsk_object_new(tmedia_multipart_body_def_t))){
		return tsk_null;
	}

	content->multipart_type = tsk_strdup(multipart_type);
	if(boundary)
		content->boundary = tsk_strdup(boundary);
	else
	{
		tsk_uuidstring_t uuid;
		tsk_uuidgenerate(&uuid);
		tsk_strupdate(&(content->boundary), uuid);
	}
	content->contents = tsk_list_create();

	return content;
}

tmedia_multipart_body_t* tmedia_content_multipart_body_parse(const void* data, tsk_size_t sizeconst, char* multipart_type, const char* boundary)
{
	//TO-DO
	tmedia_multipart_body_t* mp_body = tsk_null;
	tmedia_content_multipart_t* mp_content = tsk_null;
	const char* body_buffer = (char*)data;
	char* content_buffer = tsk_null;
	int start_index = 0, stop_index = 0, read_index = 0, final_index = 0;
	tsk_size_t boundary_size = 0, delimited_boundary_size = 0, delimited_final_boundary_size = 0, content_buffer_size = 0;
	char* delimited_boundary = tsk_null;
	char* delimited_final_boundary = tsk_null;

	if(data == tsk_null || sizeconst == 0 || multipart_type == tsk_null || boundary == tsk_null)
		return tsk_null;
	
	boundary_size = tsk_strlen(boundary);
	delimited_boundary_size = boundary_size + 3;
	#if HAVE_CRT //Debug memory
	delimited_boundary = (char*)calloc(delimited_boundary_size, sizeof(char));
		
	#else
	delimited_boundary = (char*)tsk_calloc(delimited_boundary_size, sizeof(char));
		
	#endif //HAVE_CRT
	tsk_sprintf(&delimited_boundary, "--%s", boundary);
	delimited_boundary[delimited_boundary_size - 1] = '\0';
	delimited_final_boundary_size = boundary_size + 5;
	#if HAVE_CRT //Debug memory
		delimited_final_boundary = (char*)calloc(delimited_final_boundary_size, sizeof(char));
	
	#else
		delimited_final_boundary = (char*)tsk_calloc(delimited_final_boundary_size, sizeof(char));
	
	#endif //HAVE_CRT
	tsk_sprintf(&delimited_final_boundary, "--%s--", boundary);
	delimited_final_boundary[delimited_final_boundary_size - 1] = '\0';

	final_index = tsk_strLastIndexOf(body_buffer, sizeconst, delimited_final_boundary);
	if(final_index <= 0)
	{
		TSK_DEBUG_ERROR("Error trying to get the end of the message");
		TSK_FREE(delimited_boundary);
		TSK_FREE(delimited_final_boundary);
		return tsk_null;
	}

	while(read_index < final_index)
	{
		start_index = tsk_strindexOf(&body_buffer[read_index], sizeconst - read_index, delimited_boundary);
		if(start_index < 0)
			break;
		start_index += read_index + (delimited_boundary_size - 1) + 2; //CRLF
		stop_index = tsk_strindexOf(&body_buffer[start_index], sizeconst - start_index, delimited_boundary);
		if(stop_index < 0) 
			break;
		stop_index += start_index;
		if(stop_index > final_index)
			break;
		content_buffer_size = stop_index - start_index;
		#if HAVE_CRT //Debug memory
		content_buffer = (char*)calloc(content_buffer_size, sizeof(char));
		
	#else
		content_buffer = (char*)tsk_calloc(content_buffer_size, sizeof(char));
		
	#endif //HAVE_CRT
		memcpy(content_buffer, &body_buffer[start_index], content_buffer_size);
		mp_content = tmedia_content_multipart_parse(content_buffer, content_buffer_size);
		if(mp_content != tsk_null)
		{
			if(mp_body == tsk_null)
				mp_body = tmedia_content_multipart_body_create(multipart_type, boundary);

			tsk_list_push_back_data(mp_body->contents, (void**)&mp_content);
		}
		read_index = stop_index;
		TSK_FREE(content_buffer);
		content_buffer = tsk_null;
		content_buffer_size = 0;
	}
	
	TSK_FREE(delimited_boundary);
	TSK_FREE(delimited_final_boundary);
	return mp_body;
}

char* tmedia_content_multipart_body_tostring(tmedia_multipart_body_t* self)
{
	char* string = tsk_null;
	tmedia_content_multipart_t *content;
	const tsk_list_item_t* item;

	if(!self || !self->boundary) {
		return tsk_null;
	}

	tsk_list_foreach(item, self->contents){
		char* content_string = tsk_null;
		content = (tmedia_content_multipart_t*)item->data;
		content_string = tmedia_content_multipart_tostring(content);
		if(content_string)
			tsk_strcat_2(&string, "--%s\r\n%s\r\n", self->boundary, content_string);
	}

	tsk_strcat_2(&string, "--%s--\r\n", self->boundary);

	return string;
}


int tmedia_content_multipart_body_init(tmedia_multipart_body_t* self)
{
	if (!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	return 0;
}

char* tmedia_content_multipart_body_get_header(tmedia_multipart_body_t* self)
{
	char* content_type_hdr = tsk_null;

	if(!self){
		return tsk_null;
	}

	tsk_sprintf(&content_type_hdr, "%s;boundary=%s", self->multipart_type, self->boundary);//change  "%s;boundary=\"%s\"" to "%s;boundary=%s"

    return content_type_hdr;
}

int tmedia_content_multipart_body_add_content(tmedia_multipart_body_t* self, tmedia_content_multipart_t* content)
{
	if(!self || !content)
		return -1;

	if(!self->contents)
		self->contents = tsk_list_create();

	tsk_list_push_back_data(self->contents, (void**)&content);

	return 0;
}

tmedia_content_multipart_t* tmedia_content_multipart_body_get_content(tmedia_multipart_body_t* self, const char* content_type)
{
	tmedia_content_multipart_t *content = tsk_null;
	tmedia_content_multipart_t *ret = tsk_null;
	const tsk_list_item_t* item;

	if(!self || !self->contents) {
		return tsk_null;
	}

	tsk_list_foreach(item, self->contents)
	{
        content = (tmedia_content_multipart_t*)item->data;
        if(memcmp(content->content_type, content_type, strlen(content->content_type)) == 0)
		{
			ret = content;
			break;
		}
	}

	return ret;
}

int tmedia_content_multipart_body_deinit(tmedia_multipart_body_t* self)
{
	if (!self){
		TSK_DEBUG_ERROR("Invalid parameter");
		return -1;
	}

	TSK_FREE(self->multipart_type);
	TSK_FREE(self->boundary);
	TSK_FREE(self->contents);

	return 0;
}

/* constructor */
static tsk_object_t* tmedia_content_multipart_body_ctor(tsk_object_t * self, va_list * app)
{
	tmedia_multipart_body_t *content = self;
	if (content){
	}
	return self;
}
/* destructor */
static tsk_object_t* tmedia_content_multipart_body_dtor(tsk_object_t * self)
{
	tmedia_multipart_body_t *content = self;
	if (content){
		tmedia_content_multipart_body_deinit(content);
	}

	return self;
}

static const tsk_object_def_t tmedia_multipart_body_def_s =
{
	sizeof(tmedia_multipart_body_t),
	tmedia_content_multipart_body_ctor,
	tmedia_content_multipart_body_dtor,
	tsk_null,
};
const tsk_object_def_t *tmedia_multipart_body_def_t = &tmedia_multipart_body_def_s;