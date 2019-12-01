#if HAVE_CRT
#define _CRTDBG_MAP_ALLOC 
#include <stdlib.h> 
#include <crtdbg.h>
#endif //HAVE_CRT
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


/**@file tsip_api_message.c
 * @brief Public short messaging (MESSAGE) functions.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include <tinysip/tsip_ssession.h>
#include "tinysip/api/tsip_api_message.h"

#include "tinysip/dialogs/tsip_dialog_layer.h"
#include "tinysip/dialogs/tsip_dialog_message.h"
#include "tinymedia/content/tmedia_content_multipart.h"
#include "tinysip/headers/tsip_header_Dummy.h"
#include "tinysip/tsip_message.h"
#include "tsip.h"
#include "tsk_memory.h"

#include "tsk_runnable.h"
#include "tsk_debug.h"

#define TSIP_MESSAGE_EVENT_CREATE( type)		(tsip_message_event_t*)tsk_object_new(tsip_message_event_def_t, type)
#define VALUE_CONTENT_TYPE_MESSAGE_LOCATION "application/vnd.3gpp.mcptt-location-info+xml"

#define VALUE_CONTENT_TYPE_MESSAGE_AFFILIATION "application/vnd.3gpp.mcptt-affiliation-command+xml"
#define VALUE_CONTENT_TYPE_MESSAGE_MBMS "application/vnd.3gpp.mcptt-mbms-usage-info+xml"
#define VALUE_P_ASSERTED_SERVICE_MESSAGE_AFFILIATION "urn:urn-7:3gpp-service.ims.icsi.mcptt"
#define VALUE_P_ASSERTED_SERVICE_MESSAGE_MBMS "urn:urn-7:3gpp-service.ims.icsi.mcptt"
#define VALUE_CONTENT_TYPE_MULTIPART "multipart/mixed"

extern tsip_action_t* _tsip_action_create(tsip_action_type_t type, va_list* app);


#if HAVE_LIBXML2
static int register_xml_namespaces(xmlXPathContextPtr xpathCtx, const xmlChar* nsList)
{
    xmlChar* nsListDup;
    xmlChar* prefix;
    xmlChar* href;
    xmlChar* next;

    nsListDup = xmlStrdup(nsList);

    if(nsListDup == NULL)
    {
        return(-1);
    }
    next = nsListDup;

    while(next != NULL)
    {
        /* skip spaces */
        while((*next) == ' ') next++;
        if((*next) == '\0') break;

        /* find prefix */
        prefix = next;
        next = (xmlChar*)xmlStrchr(next, '=');

        if(next == NULL)
        {
            xmlFree(nsListDup);
            return(-1);
        }

        *(next++) = '\0';

        /* find href */
        href = next;
        next = (xmlChar*)xmlStrchr(next, ' ');
        if(next != NULL)
        {
            *(next++) = '\0';
        }

        if(href[0] == '\"' || href[0] == '\'')
        {
            href = href + 1;
            if(href[strlen(href) - 1]  == '\"' || href[strlen(href) - 1] == '\'')
                href[strlen(href) - 1] = '\0';
        }

        /* do register namespace */
        if(xmlXPathRegisterNs(xpathCtx, prefix, href) != 0)
        {
            xmlFree(nsListDup);
            return(-1);
        }
    }
    xmlFree(nsListDup);
    return(0);
}
#endif

int tsip_message_event_signal(tsip_message_event_type_t type, tsip_ssession_handle_t* ss, short status_code, const char *phrase, const tsip_message_t* sipmessage)
{
	
	
	tsip_message_event_t* sipevent = TSIP_MESSAGE_EVENT_CREATE(type);
	
	tmedia_content_multipart_t* mp_content = tsk_null;
	tmedia_content_multipart_t* mp_content_sdp = tsk_null;
	tmedia_multipart_body_t* mp_body = tsk_null;
	char* boundary = tsk_null;
	const tsip_header_t* header =tsk_null;
	char* hdr_value=tsk_null;
	uint32_t local_ssrc = 0;
	char* psi_mbms_value=tsk_null;
	//tmedia_session_t* audio_session;
	//tsdp_message_t* sdp_ro = tsk_null;
	int con=0;
	char* sdp=tsk_null;
	tsip_message_t* sipmessage2=tsk_null;

	//if the messager is send
	if((((tsip_ssession_t*)ss)->media.type & tmedia_mcptt_location) == tmedia_mcptt_location){
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_location);
	
	}else 
	
		if(tsk_strcmp(TSIP_MESSAGE_CONTENT_TYPE(sipmessage),VALUE_CONTENT_TYPE_MESSAGE_LOCATION)==0){//it receive message
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_location);
	
	}else 
		if(
		tsk_strcmp(TSIP_MESSAGE_CONTENT_TYPE(sipmessage),VALUE_CONTENT_TYPE_MESSAGE_AFFILIATION)==0){//it receive message
		for(con=0; (header = tsip_message_get_headerAt(sipmessage, tsip_htype_Dummy, con)); con++)
		{		
			 const tsip_header_Dummy_t* dummy_hdr = (const tsip_header_Dummy_t*)header;

			if(tsk_strcmp(dummy_hdr->name, "P-Asserted-Service") == 0)
			{
				#if HAVE_CRT //Debug memory
					hdr_value = (char *)malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));
	
				#else
					hdr_value = (char *)tsk_malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));
	
				#endif //HAVE_CRT
				hdr_value=strdup(dummy_hdr->value);
				break;
			}
	/*				else if(tsk_strcmp(dummy_hdr->name, "User-Agent") == 0)
			{
				len = tsk_strlen(dummy_hdr->value) + 19;
				hdr_value = (char *)malloc(len * sizeof(char));
				tsk_sprintf(&hdr_value, "PoC-client/OMA_PCPS_1.0 %s", dummy_hdr->value);
				tsk_object_delete(dummy_hdr->value);
	
			}*/
		}
		if((hdr_value)!=tsk_null &&
		tsk_strcmp(hdr_value,VALUE_P_ASSERTED_SERVICE_MESSAGE_AFFILIATION)==0 ){
			tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_affiliation);
		}
		TSK_FREE(hdr_value);
		
	}else if(tsk_striequals(VALUE_CONTENT_TYPE_MULTIPART, TSIP_MESSAGE_CONTENT_TYPE(sipmessage))){
		//multipart/mixed
		boundary = tsip_header_get_param_value((tsip_header_t*)sipmessage->Content_Type, "boundary");
			if(boundary != tsk_null) 
			{			
				mp_body = tmedia_content_multipart_body_parse(TSIP_MESSAGE_CONTENT_DATA(sipmessage), TSIP_MESSAGE_CONTENT_DATA_LENGTH(sipmessage), VALUE_CONTENT_TYPE_MULTIPART, boundary);
				if(mp_body != tsk_null)
				{
					mp_content = tmedia_content_multipart_body_get_content(mp_body,VALUE_CONTENT_TYPE_MESSAGE_LOCATION);
					if(mp_content != tsk_null)//Location
					{	
						//change de content for format location
                        TSK_FREE(sipmessage->Content->data);
						if ((sipmessage->Content->data = (char*)tsk_calloc((mp_content->data_size) + 1, sizeof(uint8_t)))) {
						    memcpy(sipmessage->Content->data, mp_content->data, mp_content->data_size);
                            ((char *)sipmessage->Content->data)[mp_content->data_size]='\0';
						}
						sipmessage->Content->size=mp_content->data_size;
						tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_location);
					}else
					if(1){

						mp_content = tmedia_content_multipart_body_get_content(mp_body,VALUE_CONTENT_TYPE_MESSAGE_AFFILIATION);
						
						if(mp_content != tsk_null )//Affiliation
						{	
							for(con=0; (header = tsip_message_get_headerAt(sipmessage, tsip_htype_Dummy, con)); con++)
							{		
								const tsip_header_Dummy_t* dummy_hdr = (const tsip_header_Dummy_t*)header;
								if(tsk_strcmp(dummy_hdr->name, "P-Asserted-Service") == 0)
								{
									#if HAVE_CRT //Debug memory
									hdr_value = (char *)malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));
		
									#else
									hdr_value = (char *)tsk_malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));
		
									#endif //HAVE_CRT
									hdr_value=strdup(dummy_hdr->value);
									break;
								}
				
							}
							if((hdr_value)!=tsk_null &&
							tsk_strcmp(hdr_value,VALUE_P_ASSERTED_SERVICE_MESSAGE_AFFILIATION)==0 ){
								//change de content for format affiliation
								//tsk_realloc(sipmessage->Content->data,mp_content->data_size);
								TSK_FREE(sipmessage->Content->data)
								sipmessage->Content->data=tsk_strndup(mp_content->data,mp_content->data_size);
								sipmessage->Content->size=mp_content->data_size;
								tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_affiliation);
							}
							
						}
						else{
							
							tmedia_multipart_body_t* body = tsk_null;
							char* content_type_hdr  = tsk_null;
							char* body_string = tsk_null;
							
							//mp_content is xml of MBMS
							mp_content = tmedia_content_multipart_body_get_content(mp_body,VALUE_CONTENT_TYPE_MESSAGE_MBMS);
							//mp_content_sdp is the data in char* of SDP
							mp_content_sdp = tmedia_content_multipart_body_get_content(mp_body, "application/sdp");
			
							if(mp_content != tsk_null && mp_content_sdp!=tsk_null)//MBMS service
							{	
								for(con=0; (header = tsip_message_get_headerAt(sipmessage, tsip_htype_Dummy, con)); con++)
								{		
									const tsip_header_Dummy_t* dummy_hdr = (const tsip_header_Dummy_t*)header;
									if(tsk_strcmp(dummy_hdr->name, "P-Asserted-Service") == 0)
									{
										#if HAVE_CRT //Debug memory
												hdr_value = (char *)malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));

										#else
												hdr_value = (char *)tsk_malloc(tsk_strlen(dummy_hdr->value) * sizeof(char));

										#endif //HAVE_CRT
										hdr_value=strdup(dummy_hdr->value);
										break;
									}
				
								}
								
								//SDP of MBMS message
								if(mp_content_sdp == tsk_null){
									TSK_DEBUG_ERROR("content-type is not supportted");
									return -3;
								}


									

									if((hdr_value)!=tsk_null &&
									tsk_strcmp(hdr_value,VALUE_P_ASSERTED_SERVICE_MESSAGE_MBMS)==0 ){
										body = tmedia_content_multipart_body_create("multipart/mixed", tsk_null);
										if(body)
										{
										  tmedia_content_multipart_body_add_content(body, mp_content_sdp);
										  tmedia_content_multipart_body_add_content(body, mp_content);
										  body_string =tmedia_content_multipart_body_tostring(body);
										  content_type_hdr = tmedia_content_multipart_body_get_header(body);
										  sipmessage2=tsip_message_create();
										  tsip_message_add_content(sipmessage2, content_type_hdr, body_string, tsk_strlen(body_string));
										}
										
										/*
										//change de content for format MBMS
										tsk_realloc(sipmessage->Content->data,mp_content->data_size);
										strcpy(sipmessage->Content->data, mp_content->data);
										sipmessage->Content->size=mp_content->data_size;
										*/
										tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message_mbms);
									}
							}else{
								TSK_DEBUG_ERROR("The new message isn't valid.");
								tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message);
							}
							
						}
						}
				}
			}else{
				tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message);
			}
	}else
		tsip_event_init(TSIP_EVENT(sipevent), ss, status_code, phrase, sipmessage, tsip_event_message);

	if(hdr_value)
		TSK_FREE(hdr_value);

	TSK_RUNNABLE_ENQUEUE_OBJECT(TSK_RUNNABLE(TSIP_SSESSION(ss)->stack), sipevent);

	return 0;
}

int tsip_api_message_send_message(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;

	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}
	
	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}

	/* action */
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_message_send, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_MESSAGE, ss);
		}
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}
//Location
int tsip_api_message_send_message_location(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;



	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}
	
	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}

	//This session is type MCPTT location
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_location;

	/* action */
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_message_send, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_MESSAGE, ss);
		}
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}
//MBMS
int tsip_api_message_send_message_mbms(const tsip_ssession_handle_t *ss, ...)
{
	const tsip_ssession_t* _ss;
	va_list ap;
	tsip_action_t* action;
	tsip_dialog_t* dialog;
	int ret = -1;



	if(!(_ss = ss) || !_ss->stack){
		TSK_DEBUG_ERROR("Invalid parameter.");
		return ret;
	}
	
	/* Checks if the stack has been started */
	if(!TSK_RUNNABLE(_ss->stack)->started){
		TSK_DEBUG_ERROR("Stack not started.");
		return -2;
	}
	//This session is type MCPTT MBMS
	((tsip_ssession_t*)ss)->media.type=tmedia_mcptt_mbms;
	
	/* action */
	va_start(ap, ss);
	if((action = _tsip_action_create(tsip_atype_message_send, &ap))){
		if(!(dialog = tsip_dialog_layer_find_by_ss(_ss->stack->layer_dialog, ss))){
			dialog = tsip_dialog_layer_new(_ss->stack->layer_dialog, tsip_dialog_MESSAGE, ss);
		}
		ret = tsip_dialog_fsm_act(dialog, action->type, tsk_null, action);
		
		tsk_object_unref(dialog);
		TSK_OBJECT_SAFE_FREE(action);
	}
	va_end(ap);

	return ret;
}








//========================================================
//	SIP MESSAGE event object definition
//
static tsk_object_t* tsip_message_event_ctor(tsk_object_t * self, va_list * app)
{
	tsip_message_event_t *sipevent = self;
	if(sipevent){
		sipevent->type = va_arg(*app, tsip_message_event_type_t);
	}
	return self;
}

static tsk_object_t* tsip_message_event_dtor(tsk_object_t * self)
{ 
	tsip_message_event_t *sipevent = self;
	if(sipevent){
		tsip_event_deinit(TSIP_EVENT(sipevent));
	}
	return self;
}

static int tsip_message_event_cmp(const tsk_object_t *obj1, const tsk_object_t *obj2)
{
	return -1;
}

static const tsk_object_def_t tsip_message_event_def_s = 
{
	sizeof(tsip_message_event_t),
	tsip_message_event_ctor, 
	tsip_message_event_dtor,
	tsip_message_event_cmp, 
};
const tsk_object_def_t *tsip_message_event_def_t = &tsip_message_event_def_s;
