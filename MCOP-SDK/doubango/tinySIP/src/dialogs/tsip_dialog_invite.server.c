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

/**@file tsip_dialog_invite.client.c
 * @brief SIP dialog INVITE as per RFC 3261.
 * The SOA machine is designed as per RFC 3264 and draft-ietf-sipping-sip-offeranswer-12.
 * MMTel services implementation follow 3GPP TS 24.173.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango[dot]org>
 *

 */
#include "tinysip/dialogs/tsip_dialog_invite.h"

#include "tinysip/dialogs/tsip_dialog_invite.common.h"

#include "tinysip/transports/tsip_transport_layer.h"

#include "tinysip/headers/tsip_header_Resource_Priority.h"
#include "tinysip/headers/tsip_header_Dummy.h"
#include "tinysip/headers/tsip_header_Min_SE.h"
#include "tinysip/headers/tsip_header_RAck.h"
#include "tinysip/headers/tsip_header_Require.h"
#include "tinysip/headers/tsip_header_Session_Expires.h"
#include "tinysip/headers/tsip_header_Priority.h"
#include "tinysip/headers/tsip_header_P_Asserted_Identity.h"
#include "tinysip/headers/tsip_header_Referred_By.h"
#include "tinysip/parsers/tsip_parser_uri.h"

#include "tsk_debug.h"

#include "tinyxcap/txcap_action.h"
#include "tinyxcap/txcap_node.h"

#include "tinymedia/content/tmedia_content_multipart.h"


#if HAVE_LIBXML2
#include <libxml/tree.h>
#include <libxml/parser.h>
#include <libxml/xpath.h>
#include <libxml/xpathInternals.h>
#include <tsip.h>
#include <tinysip/tsip_ssession.h>


static int register_xml_namespaces(xmlXPathContextPtr xpathCtx, const xmlChar* nsList);
static tsip_uri_t* string_to_uri(const char* stringData);

#endif

#define PRECONF_GROUP_USERS_NS_LIST_NO_DEFAULT	"xmlns=\"urn:oma:xml:pcps:list-service\" rl=\"urn:ietf:params:xml:ns:resource-lists\" cr=\"urn:ietf:params:xml:ns:common-policy\" ocr=\"urn:oma:xml:xdm:common-policy\" oxe=\"urn:oma:xml:xdm:extensions\" osgxe=\"urn:oma:xml:pcps:poc2.1-shared-group-ext\""
#define PRECONF_GROUP_USERS_NS_LIST				"xmlns=\"urn:oma:xml:pcps:list-service\" xmlns:rl=\"urn:ietf:params:xml:ns:resource-lists\" xmlns:cr=\"urn:ietf:params:xml:ns:common-policy\" xmlns:ocr=\"urn:oma:xml:xdm:common-policy\" xmlns:oxe=\"urn:oma:xml:xdm:extensions\" xmlns:osgxe=\"urn:oma:xml:pcps:poc2.1-shared-group-ext\""
#define RESOURCE_LIST_USERS_NS_LIST_NO_DEFAULT	"xmlns=\"urn:ietf:params:xml:ns:resource-lists\" xsi=\"http://www.w3.org/2001/XMLSchema-instance\" cc=\"urn:ietf:params:xml:ns:copycontrol\""
#define RESOURCE_LIST_USERS_NS_LIST				"xmlns=\"urn:ietf:params:xml:ns:resource-lists\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:cc=\"urn:ietf:params:xml:ns:copycontrol\""
#define MCPTT_INFO_PARAMS	"xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"urn:3gpp:ns:mcpttInfo:1.0\" xmlns:mcpttinfo=\"urn:3gpp:ns:mcpttInfo:1.0\""

#define DUMMY_XML_ROOT							"group"

static const char* supported_options[] = { "100rel", "precondition", "timer" };

/* ======================== external functions ======================== */
extern int tsip_dialog_invite_msession_start(tsip_dialog_invite_t *self);
extern int send_RESPONSE(tsip_dialog_invite_t *self, const tsip_request_t* request, short code, const char* phrase, tsk_bool_t force_sdp);
extern int tsip_dialog_invite_process_ro(tsip_dialog_invite_t *self, const tsip_message_t* message);
extern int tsip_dialog_invite_stimers_schedule(tsip_dialog_invite_t* self, uint64_t timeout);
extern int send_ERROR(tsip_dialog_invite_t* self, const tsip_request_t* request, short code, const char* phrase, const char* reason);

extern int tsip_dialog_invite_timer_callback(const tsip_dialog_invite_t* self, tsk_timer_id_t timer_id);
extern tsk_bool_t tsip_dialog_invite_ice_is_enabled(const tsip_dialog_invite_t * self);
extern tsk_bool_t tsip_dialog_invite_ice_is_connected(const tsip_dialog_invite_t * self);

/* ======================== internal functions ======================== */
static int send_UNSUPPORTED(tsip_dialog_invite_t* self, const tsip_request_t* request, const char* option);

/* ======================== transitions ======================== */
static int s0000_Started_2_Terminated_X_iINVITE(va_list *app); // Failure
static int s0000_Started_2_Started_X_iINVITE(va_list *app); // Session Interval Too Small
static int s0000_Started_2_InProgress_X_iINVITE(va_list *app); // 100rel supported
static int s0000_Started_2_Ringing_X_iINVITE(va_list *app); // Neither 100rel nor QoS
static int s0000_InProgress_2_InProgress_X_iPRACK(va_list *app); // PRACK for our 18x response (with QoS)
static int s0000_InProgress_2_Ringing_X_iPRACK(va_list *app); // PRACK for our 18x response (without QoS)
static int s0000_InProgress_2_InProgress_X_iUPDATE(va_list *app); // QoS cannot resume
static int s0000_InProgress_2_Ringing_X_iUPDATE(va_list *app); // QoS can resume (do not alert user, wait for PRACK)
static int s0000_Inprogress_2_Terminated_X_iCANCEL(va_list *app);
static int s0000_Ringing_2_Ringing_X_iPRACK(va_list *app); // Alert user
static int s0000_Ringing_2_Connected_X_Accept(va_list *app);
static int s0000_Started_2_Connected_X_iINVITE(va_list *app);


static int s0000_Ringing_2_Terminated_X_Reject(va_list *app);
static int s0000_Ringing_2_Terminated_X_iCANCEL(va_list *app);
static int s0000_Any_2_Any_X_timer100rel(va_list *app);

static tsk_bool_t _fsm_cond_bad_auto_call(tsip_dialog_invite_t* self, tsip_message_t* message);

static tsk_buffer_t* processing_body_invite(tsip_dialog_invite_t* self, tsip_message_t* message);


/* ======================== conds ======================== */
static tsk_bool_t _fsm_cond_bad_extension(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    const tsip_header_Require_t* requireHdr;
    const tsk_list_item_t* item;
    tsk_size_t i, j;

    /* Check if we support all extensions */
    for(i = 0; (requireHdr = (const tsip_header_Require_t*)tsip_message_get_headerAt(message, tsip_htype_Require, i)); i++){
        tsk_bool_t bad_extension = tsk_false;
        const tsk_string_t* option = tsk_null;
        tsk_list_foreach(item, requireHdr->options){
            option = item->data;
            bad_extension = tsk_true;
            for(j = 0; option && j<sizeof(supported_options)/sizeof(const char*); j++){
                if(tsk_striequals(option->value, supported_options[j])){
                    bad_extension = tsk_false;
                    break;
                }
            }
            if(bad_extension){
                break;
            }
        }
        if(bad_extension && option){
            send_UNSUPPORTED(self, message, option->value);
            return tsk_true;
        }
    }


    return tsk_false;
}

static tsk_bool_t _fsm_cond_bad_content(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    int ret;
    const tsdp_message_t* sdp_lo;
    const tsip_header_t* hdr;
    tsk_size_t i;
	tsk_string_t *boolean_emergency_string=tsk_null;

    tsk_bool_t bodiless_INVITE = (TSIP_DIALOG(self)->state == tsip_initial && !TSIP_MESSAGE_HAS_CONTENT(message)); // Initial Bodiless INVITE

    /* Check remote offer */
    /*Processing SDP Remote*/
    if((ret = tsip_dialog_invite_process_ro(self, message))){
        ret = send_ERROR(self, message, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
        return tsk_true;
    }
    //Send data configure in client
    //MCPTT

    tmedia_session_mgr_set(self->msession_mgr,
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"implicit",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_implicit),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"priority",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_priority),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"granted",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_granted),
                           TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt,"mcptt_id_local",(TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_id)),
            //Insert Timers that received from CMS MCPTT UE init Config
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"t100",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.timer_s.timer_t100),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"t101",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.timer_s.timer_t101),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"t103",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.timer_s.timer_t103),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"t104",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.timer_s.timer_t104),
                           TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"t132",TSIP_DIALOG_GET_STACK(self)->pttMCPTT.timer_s.timer_t132),
                           TMEDIA_SESSION_SET_NULL());
    /* generate local offer and check it's validity */
    if(self->msession_mgr && (sdp_lo = tmedia_session_mgr_get_lo(self->msession_mgr))){
        /* check that we have at least one valid session (Only if no bodiless initial INVITE) */
        if(!bodiless_INVITE && !tmedia_session_mgr_has_active_session(self->msession_mgr)){
            ret = send_ERROR(self, message, 488, "Not Acceptable", "SIP; cause=488; text=\"No common codecs\"");
            return tsk_true;
        }

			if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_audio_ptt_mcptt) == tmedia_audio_ptt_mcptt)
			//MCPTT //Added
		{
			uint32_t local_ssrc = tmedia_session_mgr_audio_get_ssrc(self->msession_mgr);							
			tmedia_session_t* audio_session = tmedia_session_mgr_find(self->msession_mgr, tmedia_audio);

			//MCPTT Emergence call
			for(i=0;(hdr = tsip_message_get_headerAt(message, tsip_htype_Dummy, i)); i++)
			{		

				const tsip_header_Dummy_t* reosurce_priority_hdr = (const tsip_header_Dummy_t*)hdr;

				if(tsk_strcmp(reosurce_priority_hdr->name, "Resource-Priority") == 0){
					char* header_priority_string=tsk_null;
					int	header_priority_int=-1;
					//reosurce_priority_hdr->name, tsip_header_get_name(tsip_htype_Priority);
					#if HAVE_CRT //Debug memory
						header_priority_string=calloc(strlen(reosurce_priority_hdr->value)+1,sizeof(char));
					#else
						header_priority_string=tsk_calloc(strlen(reosurce_priority_hdr->value)+1,sizeof(char));
					#endif //HAVE_CRT
					sscanf(reosurce_priority_hdr->value, "%[^.].%d",header_priority_string,&header_priority_int);
					#if HAVE_CRT //Debug memory
						TSIP_DIALOG_GET_SS(self)->pttMCPTT.emergency.resource_priority_string=calloc(strlen(header_priority_string)+1,sizeof(char));
					#else
						TSIP_DIALOG_GET_SS(self)->pttMCPTT.emergency.resource_priority_string=tsk_calloc(strlen(header_priority_string)+1,sizeof(char));
					#endif //HAVE_CRT
					strcpy(TSIP_DIALOG_GET_SS(self)->pttMCPTT.emergency.resource_priority_string,header_priority_string);
					TSIP_DIALOG_GET_SS(self)->pttMCPTT.emergency.resource_priority_int=header_priority_int;
					TSK_FREE(header_priority_string);
				}
			}

            tmedia_session_mgr_set(self->msession_mgr,
                                   TMEDIA_SESSION_SET_INT32(tmedia_mcptt, "local_ssrc", local_ssrc),
                                   TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "audio_session",
                                                              audio_session),
                                   TMEDIA_SESSION_SET_NULL());
            audio_session->lo_held = tsk_true;
        }
        processing_body_invite(self,message);


        // media type could change if there are zombies (medias with port equal to zero)
        //TSIP_DIALOG_GET_SS(self)->media.type = self->msession_mgr->type;
    }
    else{
        ret = send_ERROR(self, message, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
        return tsk_true;
    }

    return tsk_false;
}

static tsk_bool_t _fsm_cond_bad_auto_call(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    /*
     * If the answer is TRUE, it means that the call is an automatic response. But on the other hand, if it is FALSE, it means that it is a normal call and must be treated as such.
     * */
    //test
    tsk_size_t i;

    const tsip_header_t* priv_ans_mode=tsk_null;
    const tsip_header_t* hdr=tsk_null;
    const tsip_header_t* ans_mode=tsk_null;
    for(i=0;(hdr = tsip_message_get_headerAt(message, tsip_htype_Dummy, i)) && (priv_ans_mode==tsk_null || ans_mode==tsk_null); i++)
    {
        const tsip_header_Dummy_t* dummy_hdr = (const tsip_header_Dummy_t*)hdr;
        if(tsk_strcmp(dummy_hdr->name, "Priv-Answer-Mode") == 0)
        {
            priv_ans_mode=hdr;
        }else if(tsk_strcmp(dummy_hdr->name, "Answer-Mode") == 0){
            ans_mode=hdr;
        }
    }
    //TODO: The operation of accepting the call automatically has not yet been verified correctly, and if it is necessary to distinguish the sending configuration of "answer mode" with the reception of the same.
    if(priv_ans_mode != tsk_null && tsk_strcmp("Auto",((tsip_header_Referred_By_t*)priv_ans_mode)->uri )==0){
        //It is mandatory to accept the call automatically
        return tsk_true;
    }else if(ans_mode != tsk_null && tsk_strcmp("Auto",((tsip_header_Referred_By_t*)ans_mode)->uri )==0
		/*&& self && TSIP_DIALOG_GET_STACK(self) && TSIP_DIALOG_GET_STACK(self)->pttMCPTT.mcptt_answer_mode==tsk_true*/){
        //It must be verified that automatic response is allowed or not in the user configuration.
        return tsk_true;
    }


    return tsk_false;

}


static tsk_buffer_t* processing_body_invite(tsip_dialog_invite_t* self, tsip_message_t* message){


        if(tsk_striequals("multipart/mixed", TSIP_MESSAGE_CONTENT_TYPE(message)))
    {
        tsk_string_t *boolean_emergency_string=tsk_null;
        uint32_t floorControlNO = 0;
        uint32_t floorControlYES = 1;
        uint32_t withFloorControl=floorControlNO;
        tmedia_multipart_body_t* mp_body = tsk_null;
        tmedia_content_multipart_t* mp_content = tsk_null;
        char* boundary = tsk_null;

        boundary = tsip_header_get_param_value((tsip_header_t*)message->Content_Type, "boundary");
        if(boundary != tsk_null)
        {
            mp_body = tmedia_content_multipart_body_parse(TSIP_MESSAGE_CONTENT_DATA(message), TSIP_MESSAGE_CONTENT_DATA_LENGTH(message), "multipart/mixed", boundary);

            if(mp_body != tsk_null)
            {
                mp_content = tmedia_content_multipart_body_get_content(mp_body, "application/vnd.3gpp.mcptt-info+xml");
                if(mp_content != tsk_null)
                {
                    xmlDoc *pDoc;
                    xmlNode *pRootElement;
                    xmlXPathContext *pPathCtx;
                    xmlXPathObject *pPathObj;
                    tsip_uri_t* mcptt_request_uri;
                    tsip_uri_t* mcptt_calling_user_id;//mcptt-calling-user-id
                    tsip_uri_t* mcptt_called_party_id;
                    tsip_uri_t* mcptt_calling_group_id;


                    static const xmlChar* __xpath_expr_session_type = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='session-type']";//<session-type>
                    static const xmlChar* __xpath_expr_mcptt_request_uri_old = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-request-uri']";//<mcptt-request-uri>
                    static const xmlChar* __xpath_expr_mcptt_calling_user_id_old = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-calling-user-id']";//<mcptt-calling-user-id>
                    static const xmlChar* __xpath_expr_mcptt_called_party_id_old = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-called-party-id']";//<mcptt-called-party-id>
                    static const xmlChar* __xpath_expr_mcptt_calling_group_id_old = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-calling-group-id']";//mcptt-calling-group-id
                    static const xmlChar* __xpath_expr_mcptt_request_uri = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-request-uri']/*[local-name()='mcpttURI']";//<mcptt-request-uri><mcpttURI>
                    static const xmlChar* __xpath_expr_mcptt_calling_user_id = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-calling-user-id']/*[local-name()='mcpttURI']";//<mcptt-calling-user-id><mcpttURI>
                    static const xmlChar* __xpath_expr_mcptt_called_party_id = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-called-party-id']/*[local-name()='mcpttURI']";//<mcptt-called-party-id><mcpttURI>
                    static const xmlChar* __xpath_expr_mcptt_calling_group_id = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='mcptt-calling-group-id']/*[local-name()='mcpttURI']";//<mcptt-calling-group-id><mcpttURI>
                    //MCPTT emergency
                    static const xmlChar* __xpath_expr_mcptt_emergency_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='emergency-ind']/*[local-name()='mcpttBoolean']";//<emergency-ind><mcpttBoolean>
                    static const xmlChar* __xpath_expr_mcptt_alert_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='alert-ind']/*[local-name()='mcpttBoolean']";//<alert-ind><mcpttBoolean>
                    static const xmlChar* __xpath_expr_imminentperil_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='imminentperil-ind']/*[local-name()='mcpttBoolean']";//<imminentperil-ind><mcpttBoolean>


                    static const xmlChar* __xml_namespace_mcptt_info = (const xmlChar*)MCPTT_INFO_PARAMS;
                    char* xmlContent;

                    mcptt_request_uri=tsk_null;
                    mcptt_calling_user_id=tsk_null;
                    mcptt_called_party_id=tsk_null;
                    mcptt_calling_group_id=tsk_null;

#if HAVE_CRT //Debug memory
                    xmlContent = (char*)calloc((mp_content->data_size + 1), sizeof(char));

#else
                    xmlContent = (char*)tsk_calloc((mp_content->data_size + 1), sizeof(char));

#endif //HAVE_CRT

                    memcpy(xmlContent, mp_content->data, mp_content->data_size);
                    xmlContent[mp_content->data_size] = '\0';

                    if (!(pDoc = xmlParseDoc(xmlContent))) {
                        TSK_DEBUG_ERROR("Failed to parse XML content ");
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }
                    if (!(pRootElement = xmlDocGetRootElement(pDoc))) {
                        TSK_DEBUG_ERROR("Failed to get root element from XML content");
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }
                    if (!(pPathCtx = xmlXPathNewContext(pDoc))) {
                        TSK_DEBUG_ERROR("Failed to create path context from XML content");
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }

                    if(register_xml_namespaces(pPathCtx, __xml_namespace_mcptt_info) < 0)
                    {
                        TSK_DEBUG_ERROR("Error: unable to register namespaces:mcptt-info");
                        xmlXPathFreeContext(pPathCtx);
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }


                    //mcptt_request_uri
                    if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_request_uri, pPathCtx))) {
                        TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt_request_uri");
                        xmlXPathFreeContext(pPathCtx);
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }

                    if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                    {
                        tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                        mcptt_request_uri=string_to_uri(uri->value);

                    }else{
                        if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_request_uri_old, pPathCtx))) {
                            TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: request_uri");
                            xmlXPathFreeContext(pPathCtx);
                            xmlFreeDoc(pDoc);
                            tsk_free(&xmlContent);
                            return tsk_false;
                        }
                        if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                        {
                            tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                            mcptt_request_uri=string_to_uri(uri->value);

                        }else{
                            TSK_DEBUG_ERROR("Error: the mcptt_request_uri isn?t exist");
                            xmlXPathFreeContext(pPathCtx);
                            xmlFreeDoc(pDoc);
                            tsk_free(&xmlContent);
                            return tsk_false;
                        }
                    }

                    //mcptt_calling_user_id
                    if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_user_id, pPathCtx))) {
                        TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: calling-user-id");
                        xmlXPathFreeContext(pPathCtx);
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }

                    if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                    {
                        tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                        mcptt_calling_user_id=string_to_uri(uri->value);
                    }else{
                        if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_user_id_old, pPathCtx))) {
                            TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: user-id-old");
                            xmlXPathFreeContext(pPathCtx);
                            xmlFreeDoc(pDoc);
                            tsk_free(&xmlContent);
                            return tsk_false;
                        }
                        if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                        {
                            tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                            mcptt_calling_user_id=string_to_uri(uri->value);
                        }else{
                            TSK_DEBUG_ERROR("Error: the mcptt_calling_user_id isn?t exist:");
                            xmlXPathFreeContext(pPathCtx);
                            xmlFreeDoc(pDoc);
                            tsk_free(&xmlContent);
                            return tsk_false;
                        }

                    }



                    //<session-type>
                    if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_session_type, pPathCtx))) {
                        TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: session-type");
                        xmlXPathFreeContext(pPathCtx);
                        xmlFreeDoc(pDoc);
                        tsk_free(&xmlContent);
                        return tsk_false;
                    }



                    if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                    {
                        tsk_string_t* session_type = tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                        if(tsk_striequals(session_type->value,"private")==tsk_true){
                            //Session MCPTT is private
                            if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_audio_ptt_mcptt) == tmedia_audio_ptt_mcptt){
                                TSIP_DIALOG_GET_SS(self)->media.type=tmedia_audio_ptt_mcptt_with_floor_control;
                            }else{
                                TSIP_DIALOG_GET_SS(self)->media.type=tmedia_audio_ptt_mcptt;// for with out floot control
                            }


                            //__xpath_expr_mcptt_called_party_id
                            if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_called_party_id, pPathCtx))) {
                                TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt-called-party-id");
                                xmlXPathFreeContext(pPathCtx);
                                xmlFreeDoc(pDoc);
                                tsk_free(&xmlContent);
                                return tsk_false;
                            }
                            //VERSION OLD MCPTTINFO
                            if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                            {
                                tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                                mcptt_called_party_id=string_to_uri(uri->value);


                            }else{

                                if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_called_party_id_old, pPathCtx))) {
                                    TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt-called-parted-id");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }
                                if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                                {
                                    tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                                    mcptt_called_party_id=string_to_uri(uri->value);


                                }else{
                                    TSK_DEBUG_ERROR("Error: the mcptt_called_party_id isn?t exist");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }

                            }


                        }
                        else if(tsk_striequals(session_type->value,"prearranged")==tsk_true){
                            //Session MCPTT is group
							tsip_ssession_t* tsip_ssession=TSIP_DIALOG_GET_SS(self);
							tsip_ssession->media.type=tmedia_audio_ptt_group_mcptt_with_floor_control;
                            //__xpath_expr_mcptt_calling_group_id
                            if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_group_id, pPathCtx))) {
                                TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt_calling_group_id");
                                xmlXPathFreeContext(pPathCtx);
                                xmlFreeDoc(pDoc);
                                tsk_free(&xmlContent);
                                return tsk_false;
                            }

                            if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                            {
                                tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);

                                mcptt_calling_group_id=string_to_uri(uri->value);

								 if(mcptt_calling_group_id && TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_group_uri == tsk_null){
                                    /*
								     #if HAVE_CRT //Debug memory
                                     tsip_ssession->pttMCPTT.ptt_group_uri = malloc(strlen(uri->value) + 1);
                                    #else
                                     */
                                    /*
                                     tsip_ssession->pttMCPTT.ptt_group_uri = tsk_malloc(strlen(uri->value) + 1);
                                     strcpy(tsip_ssession->pttMCPTT.ptt_group_uri,uri->value);
                                       */
                                     tsip_ssession->pttMCPTT.ptt_group_uri=tsip_uri_tostring(mcptt_calling_group_id,tsk_false,tsk_false);
								 }

                            }else{
                                //VERSION OLD MCPTTINFO
                                if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_group_id_old, pPathCtx))) {
                                    TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt_calling_group_id");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }
                                if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                                {
                                    tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                                    mcptt_calling_group_id=string_to_uri(uri->value);

                                }else{
                                    TSK_DEBUG_ERROR("Error: the mcptt_calling_group_id isn?t exist");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }

                            }

                        }
                        else if(tsk_striequals(session_type->value,"chat")==tsk_true){
                            //Session MCPTT is Chat group call.
                            TSIP_DIALOG_GET_SS(self)->media.type=tmedia_audio_ptt_chat_group_mcptt_with_floor_control;

                            //__xpath_expr_mcptt_calling_group_id
                            if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_group_id, pPathCtx))) {
                                TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt_calling_group_id");
                                xmlXPathFreeContext(pPathCtx);
                                xmlFreeDoc(pDoc);
                                tsk_free(&xmlContent);
                                return tsk_false;
                            }

                            if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                            {
                                tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                                mcptt_calling_group_id=string_to_uri(uri->value);

                            }else{
                                //VERSION OLD MCPTTINFO
                                if (!(pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_calling_group_id_old, pPathCtx))) {
                                    TSK_DEBUG_ERROR("Error: unable to evaluate xpath expression: mcptt_calling_group_id");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }
                                if (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0 )
                                {
                                    tsk_string_t *uri=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content);
                                    mcptt_calling_group_id=string_to_uri(uri->value);

                                }else{
                                    TSK_DEBUG_ERROR("Error: the mcptt_calling_group_id isn?t exist");
                                    xmlXPathFreeContext(pPathCtx);
                                    xmlFreeDoc(pDoc);
                                    tsk_free(&xmlContent);
                                    return tsk_false;
                                }

                            }

                        }

                        else{
                            TSK_DEBUG_ERROR("Error: type session isn?t valid: ");
                            xmlXPathFreeContext(pPathCtx);
                            xmlFreeDoc(pDoc);
                            tsk_free(&xmlContent);
                            return tsk_false;
                        }
                        /*
                        static const xmlChar* __xpath_expr_mcptt_emergency_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='emergency-ind']/*[local-name()='mcpttBoolean']";//<emergency-ind><mcpttBoolean>
                    static const xmlChar* __xpath_expr_mcptt_alert_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='alert-ind']/*[local-name()='mcpttBoolean']";//<alert-ind><mcpttBoolean>
                    static const xmlChar* __xpath_expr_imminentperil_ind = (const xmlChar*)"/*[local-name()='mcpttinfo']/*[local-name()='mcptt-Params']/*[local-name()='imminentperil-ind']/*[local-name()='mcpttBoolean']";//<imminentperil-ind><mcpttBoolean>

                    if(tsk_striequals(session_type->value,"private")==tsk_true){

                        */
                        //MCPTT emergency
                        //Now check if the call is type emergency and which type is.
                        //__xpath_expr_mcptt_calling_group_id
                        if ((pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_emergency_ind, pPathCtx)) &&
                            (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0) &&
                            (boolean_emergency_string=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content)) &&
                            tsk_striequals(boolean_emergency_string->value,"True")==tsk_true) {
                            //Session MCPTT is EMERGENCY
                            TSIP_DIALOG_GET_SS(self)->media.type=TSIP_DIALOG_GET_SS(self)->media.type | tmedia_emergency | tmedia_floor_control;
                            TSK_DEBUG_INFO("This call is type emergency");
                        }else if((pPathObj = xmlXPathEvalExpression(__xpath_expr_mcptt_alert_ind, pPathCtx)) &&
                                 (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0) &&
                                 (boolean_emergency_string=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content)) &&
                                 tsk_striequals(boolean_emergency_string->value,"True")==tsk_true){
                            //Session MCPTT is ALERT
                            TSIP_DIALOG_GET_SS(self)->media.type=TSIP_DIALOG_GET_SS(self)->media.type | tmedia_alert | tmedia_floor_control;
                            TSK_DEBUG_INFO("This call is type alert");
                        }else if((pPathObj = xmlXPathEvalExpression(__xpath_expr_imminentperil_ind, pPathCtx)) &&
                                 (pPathObj->type == XPATH_NODESET && pPathObj->nodesetval != tsk_null && pPathObj->nodesetval->nodeNr >0) &&
                                 (boolean_emergency_string=tsk_string_create(pPathObj->nodesetval->nodeTab[0]->children->content)) &&
                                 tsk_striequals(boolean_emergency_string->value,"True")==tsk_true){
                            //Session MCPTT is IMMINENTPERIL
                            TSIP_DIALOG_GET_SS(self)->media.type=TSIP_DIALOG_GET_SS(self)->media.type | tmedia_imminentperil | tmedia_floor_control;
                            TSK_DEBUG_INFO("This call is type imminentperil");
                        }else{
                            TSK_DEBUG_INFO("This call isn?t type emergency");
                        }
                        if(TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri == tsk_null)
                            TSIP_DIALOG_GET_SS(self)->pttMCPTT.ptt_caller_uri=tsip_uri_tostring(mcptt_calling_user_id, tsk_true, tsk_true);
                        if((TSIP_DIALOG_GET_SS(self)->media.type & tmedia_floor_control) == tmedia_floor_control) {
                            withFloorControl=floorControlYES;
                        }

                        tmedia_session_mgr_set(self->msession_mgr,
                                               TMEDIA_SESSION_SET_INT32(tmedia_mcptt,"type_session",(TSIP_DIALOG_GET_SS(self)->media.type)),
                                               TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "mcptt_request_uri", mcptt_request_uri),
											   TMEDIA_SESSION_SET_INT32(tmedia_mcptt, "with_floor_control",withFloorControl),//for fulduplex call
                                               TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "mcptt_calling_user_id", mcptt_calling_user_id),
                                               TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "mcptt_called_party_id",mcptt_called_party_id),
                                               TMEDIA_SESSION_SET_POBJECT(tmedia_mcptt, "mcptt_calling_group_id", mcptt_calling_group_id),
                                               TMEDIA_SESSION_SET_NULL());

                    }






                    xmlXPathFreeObject(pPathObj);

                    xmlXPathFreeContext(pPathCtx);
                    xmlFreeDoc(pDoc);

                    tsk_free(&xmlContent);
                }
            }
        }
    }
}

static tsk_bool_t _fsm_cond_toosmall(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    if(TSIP_DIALOG_GET_SS(self)->media.timers.timeout && (tsip_message_supported(message, "timer") || tsip_message_required(message, "timer"))){
        const tsip_header_Session_Expires_t* Session_Expires;
        if((Session_Expires = (const tsip_header_Session_Expires_t*)tsip_message_get_header(message, tsip_htype_Session_Expires))){
            if(Session_Expires->delta_seconds < TSIP_SESSION_EXPIRES_MIN_VALUE){
                self->stimers.minse = TSIP_SESSION_EXPIRES_MIN_VALUE;
                send_RESPONSE(self, message, 422, "Session Interval Too Small", tsk_false);
                return tsk_true;
            }
            else{
                const tsip_header_Min_SE_t* Min_SE;
                self->stimers.timer.timeout = Session_Expires->delta_seconds;
                tsk_strupdate(&self->stimers.refresher, Session_Expires->refresher_uas ? "uas" : "uac");
                self->stimers.is_refresher = tsk_striequals(self->stimers.refresher, "uas");
                if((Min_SE = (const tsip_header_Min_SE_t*)tsip_message_get_header(message, tsip_htype_Min_SE))){
                    self->stimers.minse = Min_SE->delta_seconds;
                }
            }
        }
    }
    return tsk_false;
}

// 100rel && (QoS or ICE)
static tsk_bool_t _fsm_cond_use_early_media(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    if((tsip_message_supported(message, "100rel") && self->supported._100rel) || tsip_message_required(message, "100rel")){
        if((tsip_message_supported(message, "precondition") && self->supported.precondition) || tsip_message_required(message, "precondition")){
            return tsk_true;
        }
    }
#if 0
    if(tsip_dialog_invite_ice_is_enabled(self)){
		return tsk_true;
	}
#endif
    return tsk_false;
}


static tsk_bool_t _fsm_cond_prack_match(tsip_dialog_invite_t* self, tsip_message_t* message)
{
    const tsip_header_RAck_t* RAck;

    if(!self->last_o1xxrel){
        return tsk_false;
    }

    if((RAck = (const tsip_header_RAck_t*)tsip_message_get_header(message, tsip_htype_RAck))){
        if((RAck->seq == self->rseq) &&
           (tsk_striequals(RAck->method, self->last_o1xxrel->CSeq->method)) &&
           (RAck->cseq == self->last_o1xxrel->CSeq->seq)){
            self->rseq++;
            return tsk_true;
        }
        else{
            TSK_DEBUG_WARN("Failed to match PRACK request");
        }
    }

    return tsk_false;
}
static tsk_bool_t _fsm_cond_negociates_preconditions(tsip_dialog_invite_t* self, tsip_message_t* rPRACK)
{
    //tsip_message_supported(self->last_iInvite, "precondition") || tsip_message_required(self->last_iInvite, "precondition")
    if(tsip_message_required(self->last_iInvite, "precondition") || (self->msession_mgr && self->msession_mgr->qos.strength == tmedia_qos_strength_mandatory)){
        return tsk_true;
    }
    return tsk_false;
}
static tsk_bool_t _fsm_cond_cannotresume(tsip_dialog_invite_t* self, tsip_message_t* rUPDATE)
{
    if(!tsip_dialog_invite_process_ro(self, rUPDATE)){
        return !tmedia_session_mgr_canresume(self->msession_mgr);
    }
    else{
        return tsk_false;
    }
}

static tsk_bool_t _fsm_cond_initial_iack_pending(tsip_dialog_invite_t* self, tsip_message_t* rACK)
{
    return self->is_initial_iack_pending;
}



/* Init FSM */
int tsip_dialog_invite_server_init(tsip_dialog_invite_t *self)
{
    return tsk_fsm_set(TSIP_DIALOG_GET_FSM(self),

            /*=======================
            * === Started ===
            */
            // Started -> (Bad Extendion) -> Terminated
                       TSK_FSM_ADD(_fsm_state_Started, _fsm_action_iINVITE, _fsm_cond_bad_extension, _fsm_state_Terminated, s0000_Started_2_Terminated_X_iINVITE, "s0000_Started_2_Terminated_X_iINVITE"),
            // Started -> (Bad content) -> Terminated
                       TSK_FSM_ADD(_fsm_state_Started, _fsm_action_iINVITE, _fsm_cond_bad_content, _fsm_state_Terminated, s0000_Started_2_Terminated_X_iINVITE, "s0000_Started_2_Terminated_X_iINVITE"),
            // Started -> (Session Interval Too Small) -> Started
                       TSK_FSM_ADD(_fsm_state_Started, _fsm_action_iINVITE, _fsm_cond_toosmall, _fsm_state_Started, s0000_Started_2_Started_X_iINVITE, "s0000_Started_2_Started_X_iINVITE"),
            // Started -> (100rel && (QoS or ICE)) -> InProgress
                       TSK_FSM_ADD(_fsm_state_Started, _fsm_action_iINVITE, _fsm_cond_use_early_media, _fsm_state_InProgress, s0000_Started_2_InProgress_X_iINVITE, "s0000_Started_2_InProgress_X_iINVITE"),
            // Started -> (non-100rel and non-QoS, referred to as "basic") -> Accept to connect
                       TSK_FSM_ADD(_fsm_state_Started, _fsm_action_iINVITE,_fsm_cond_bad_auto_call, _fsm_state_Connected, s0000_Started_2_Connected_X_iINVITE, "s0000_Started_2_Connected_X_iINVITE"),
            // Started -> (non-100rel and non-QoS, referred to as "basic") -> Ringing
                       TSK_FSM_ADD_ALWAYS(_fsm_state_Started, _fsm_action_iINVITE, _fsm_state_Ringing, s0000_Started_2_Ringing_X_iINVITE, "s0000_Started_2_Ringing_X_iINVITE"),


            /*=======================
            * === InProgress ===
            */
            // InProgress ->(iPRACK with QoS) -> InProgress
                       TSK_FSM_ADD(_fsm_state_InProgress, _fsm_action_iPRACK, _fsm_cond_negociates_preconditions, _fsm_state_InProgress, s0000_InProgress_2_InProgress_X_iPRACK, "s0000_InProgress_2_InProgress_X_iPRACK"),
            // InProgress ->(iPRACK without QoS) -> Ringing
                       TSK_FSM_ADD(_fsm_state_InProgress, _fsm_action_iPRACK, _fsm_cond_prack_match, _fsm_state_Ringing, s0000_InProgress_2_Ringing_X_iPRACK, "s0000_InProgress_2_Ringing_X_iPRACK"),
            // InProgress ->(iUPDATE but cannot resume) -> InProgress
                       TSK_FSM_ADD(_fsm_state_InProgress, _fsm_action_iUPDATE, _fsm_cond_cannotresume, _fsm_state_InProgress, s0000_InProgress_2_InProgress_X_iUPDATE, "s0000_InProgress_2_InProgress_X_iUPDATE"),
            // InProgress ->(iUPDATE can resume) -> Ringing
                       TSK_FSM_ADD_ALWAYS(_fsm_state_InProgress, _fsm_action_iUPDATE, _fsm_state_Ringing, s0000_InProgress_2_Ringing_X_iUPDATE, "s0000_InProgress_2_Ringing_X_iUPDATE"),
            // InProgress ->(iCANCEL) -> Terminated
                       TSK_FSM_ADD_ALWAYS(_fsm_state_InProgress, _fsm_action_iCANCEL, _fsm_state_Terminated, s0000_Inprogress_2_Terminated_X_iCANCEL, "s0000_Inprogress_2_Terminated_X_iCANCEL"),


            /*=======================
            * === Ringing ===
            */
            // Ringing -> (iPRACK) -> Ringing
                       TSK_FSM_ADD(_fsm_state_Ringing, _fsm_action_iPRACK, _fsm_cond_prack_match, _fsm_state_Ringing, s0000_Ringing_2_Ringing_X_iPRACK, "s0000_Ringing_2_Ringing_X_iPRACK"),
            // Ringing -> (oAccept) -> Connected
                       TSK_FSM_ADD_ALWAYS(_fsm_state_Ringing, _fsm_action_accept, _fsm_state_Connected, s0000_Ringing_2_Connected_X_Accept, "s0000_Ringing_2_Connected_X_Accept"),
            // Ringing -> (oReject) -> Terminated
                       TSK_FSM_ADD_ALWAYS(_fsm_state_Ringing, _fsm_action_reject, _fsm_state_Terminated, s0000_Ringing_2_Terminated_X_Reject, "s0000_Ringing_2_Terminated_X_Reject"),
            // Ringing ->(iCANCEL) -> Terminated
                       TSK_FSM_ADD_ALWAYS(_fsm_state_Ringing, _fsm_action_iCANCEL, _fsm_state_Terminated, s0000_Ringing_2_Terminated_X_iCANCEL, "s0000_Ringing_2_Terminated_X_iCANCEL"),

            /*=======================
            * === FRESH CONNECTED ===
            */
            // Fresh Connected [ACK is pending] ->(iCANCEL) -> Terminated
                       TSK_FSM_ADD(_fsm_state_Connected, _fsm_action_iCANCEL, _fsm_cond_initial_iack_pending, _fsm_state_Terminated, s0000_Ringing_2_Terminated_X_iCANCEL, "s0000_FreshConnected_2_Terminated_X_iCANCEL"),

            /*=======================
            * === ANY ===
            */
            // Any ->(timer100rel) -> Any
                       TSK_FSM_ADD_ALWAYS(tsk_fsm_state_any, _fsm_action_timer100rel, tsk_fsm_state_any, s0000_Any_2_Any_X_timer100rel, "s0000_Any_2_Any_X_timer100rel"),


                       TSK_FSM_ADD_NULL());
}

//--------------------------------------------------------
//				== STATE MACHINE BEGIN ==
//--------------------------------------------------------


/* Started -> (Failure) -> Terminated */
int s0000_Started_2_Terminated_X_iINVITE(va_list *app)
{
    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    /* tsip_request_t *request = va_arg(*app, tsip_request_t *); */

    /* We are not the client */
    self->is_client = tsk_false;

    return 0;
}

/* Started -> (Too Small) -> Started */
int s0000_Started_2_Started_X_iINVITE(va_list *app)
{
    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);

    /* We are not the client */
    self->is_client = tsk_false;

    return 0;
}

/* Started -> (non-100rel and non-QoS, referred to as "basic") -> Ringing */
int s0000_Started_2_Ringing_X_iINVITE(va_list *app)
{
    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);
    const tsip_header_Session_Expires_t* hdr_SessionExpires;
    /* we are not the client */
    self->is_client = tsk_false;

    /* update last INVITE */
    TSK_OBJECT_SAFE_FREE(self->last_iInvite);
    self->last_iInvite = tsk_object_ref(request);

    // add "require:100rel" tag if the incoming INVITE contains "100rel" tag in "supported" header
    if(self->last_iInvite && (tsip_message_supported(self->last_iInvite, "100rel") || tsip_message_required(self->last_iInvite, "100rel")) && self->supported._100rel){
        self->required._100rel = tsk_true;
    }

    // add "require:timer" tag if incoming INVITE contains "timer" tag in "supported" header and session timers is enabled
    if(TSIP_DIALOG_GET_SS(self)->media.timers.timeout){
        if((hdr_SessionExpires = (const tsip_header_Session_Expires_t*)tsip_message_get_header(request, tsip_htype_Session_Expires))){
            // "hdr_SessionExpires->delta_seconds" smallnest already checked
            self->stimers.timer.timeout = hdr_SessionExpires->delta_seconds;
            tsk_strupdate(&self->stimers.refresher, hdr_SessionExpires->refresher_uas ? "uas" : "uac");
            self->stimers.is_refresher = tsk_striequals(self->stimers.refresher, "uas");
            self->required.timer = tsk_true;
        }
    }

    /* update state */
    tsip_dialog_update_2(TSIP_DIALOG(self), request);

    /* send Ringing */
    /*if(TSIP_DIALOG_GET_STACK(self)->network.mode != tsip_stack_mode_webrtc2sip)*/{
        send_RESPONSE(self, request, 180, "Ringing", tsk_false);
    }



    /* alert the user (session) */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_newcall,
                              tsip_event_code_dialog_request_incoming, "Incoming Call", request);

    return 0;
}

/* Started -> (QoS (preconditions)) -> InProgress */
int s0000_Started_2_InProgress_X_iINVITE(va_list *app)
{

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    /* We are not the client */
    self->is_client = tsk_false;

    /* update last INVITE */
    TSK_OBJECT_SAFE_FREE(self->last_iInvite);
    self->last_iInvite = tsk_object_ref(request);

    /* Update state */
    tsip_dialog_update_2(TSIP_DIALOG(self), request);

    /* Send In Progress
        RFC 3262 - 3 UAS Behavior

        The provisional response to be sent reliably is constructed by the
        UAS core according to the procedures of Section 8.2.6 of RFC 3261.
        In addition, it MUST contain a Require header field containing the
        option tag 100rel, and MUST include an RSeq header field.  The value
        of the header field for the first reliable provisional response in a
        transaction MUST be between 1 and 2**31 - 1.
    */
    self->rseq = (rand() ^ rand()) % (0x00000001 << 31);
    self->required._100rel = tsk_true;
    self->required.precondition = (tsip_message_supported(self->last_iInvite, "precondition") || tsip_message_required(self->last_iInvite, "precondition"));
    send_RESPONSE(self, request, 183, "Session in Progress", tsk_true);

    return 0;
}

/* InProgress ->(iPRACK with QoS) -> InProgress */
int s0000_InProgress_2_InProgress_X_iPRACK(va_list *app)
{
    int ret;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* In all cases: Send 2xx PRACK */
    if(!(ret = send_RESPONSE(self, request, 200, "OK", tsk_false))){
        ++self->rseq;
    }

    /*
        1. Alice sends an initial INVITE without offer
        2. Bob's answer is sent in the first reliable provisional response, in this case it's a 1xx INVITE response
        3. Alice's answer is sent in the PRACK response
    */
    if(!self->msession_mgr->sdp.ro){
        if(TSIP_MESSAGE_HAS_CONTENT(request)){
            if((ret = tsip_dialog_invite_process_ro(self, request))){
                /* Send Error and break the FSM */
                ret = send_ERROR(self, self->last_iInvite, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
                return -4;
            }
        }
        else{
            /* 488 INVITE */
            ret = send_ERROR(self, self->last_iInvite, 488, "Not Acceptable", "SIP; cause=488; text=\"Offer expected in the PRACK\"");
            return -3;
        }
    }

    return ret;
}

/* InProgress ->(iPRACK without QoS) -> Ringing */
int s0000_InProgress_2_Ringing_X_iPRACK(va_list *app)
{
    int ret;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* In all cases: Send 2xx PRACK */
    if(!(ret = send_RESPONSE(self, request, 200, "OK", tsk_false))){
        ++self->rseq;
    }

    /*
        1. Alice sends an initial INVITE without offer
        2. Bob's answer is sent in the first reliable provisional response, in this case it's a 1xx INVITE response
        3. Alice's answer is sent in the PRACK response
    */
    if(self->msession_mgr && !self->msession_mgr->sdp.ro){
        if(TSIP_MESSAGE_HAS_CONTENT(request)){
            if((ret = tsip_dialog_invite_process_ro(self, request))){
                /* Send Error and break the FSM */
                ret = send_ERROR(self, self->last_iInvite, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
                return -4;
            }
        }
        else{
            /* 488 INVITE */
            ret = send_ERROR(self, self->last_iInvite, 488, "Not Acceptable", "SIP; cause=488; text=\"Offer expected in the PRACK\"");
            return -3;
        }
    }

    /* Send Ringing */
    /*if(TSIP_DIALOG_GET_STACK(self)->network.mode != tsip_stack_mode_webrtc2sip)*/{
        ret = send_RESPONSE(self, self->last_iInvite, 180, "Ringing", tsk_false);
    }

    /* Alert the user (session) */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_newcall,
                              tsip_event_code_dialog_request_incoming, "Incoming Call", request);

    return ret;
}

/* InProgress ->(iUPDATE but cannot resume) -> InProgress */
int s0000_InProgress_2_InProgress_X_iUPDATE(va_list *app)
{
    int ret;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    if((ret = tsip_dialog_invite_process_ro(self, request))){
        /* Send Error and break the FSM */
        ret = send_ERROR(self, request, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
        return -4;
    }
    else{
        // force SDP in 200 OK even if the request has the same SDP version
        tsk_bool_t force_sdp = TSIP_MESSAGE_HAS_CONTENT(request);
        ret = send_RESPONSE(self, request, 200, "OK",
                            (self->msession_mgr && (force_sdp || self->msession_mgr->ro_changed || self->msession_mgr->state_changed)));
    }

    return ret;
}

/* InProgress ->(iUPDATE can resume) -> Ringing */
int s0000_InProgress_2_Ringing_X_iUPDATE(va_list *app)
{
    int ret;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);
    tsk_bool_t force_sdp;

    if((ret = tsip_dialog_invite_process_ro(self, request))){
        /* Send Error and break the FSM */
        ret = send_ERROR(self, request, 488, "Not Acceptable", "SIP; cause=488; text=\"Bad content\"");
        return -4;
    }

    /* Send 200 UPDATE */
    // force SDP in 200 OK even if the request has the same SDP version
    force_sdp = TSIP_MESSAGE_HAS_CONTENT(request);
    ret = send_RESPONSE(self, request, 200, "OK",
                        (self->msession_mgr && (force_sdp || self->msession_mgr->ro_changed || self->msession_mgr->state_changed)));

    /* Send Ringing */
    /*if(TSIP_DIALOG_GET_STACK(self)->network.mode != tsip_stack_mode_webrtc2sip)*/{
        ret = send_RESPONSE(self, self->last_iInvite, 180, "Ringing", tsk_false);
    }

    /* alert the user */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_newcall,
                              tsip_event_code_dialog_request_incoming, "Incoming Call", request);

    return ret;
}

/* InProgress ->(iCANCEL) -> Terminated */
int s0000_Inprogress_2_Terminated_X_iCANCEL(va_list *app)
{
    tsip_response_t* response;
    int ret = -1;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    /* Send 2xx for the CANCEL (Direct to Transport layer beacause CANCEL is a special case) */
    if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 200, "OK", request))){
        ret = tsip_transport_layer_send(TSIP_DIALOG_GET_STACK(self)->layer_transport, tsk_null, response);
        TSK_OBJECT_SAFE_FREE(response);
    }

    /* Send Request Cancelled */
    ret = send_ERROR(self, self->last_iInvite, 487, "Request Cancelled", "SIP; cause=487; text=\"Request Cancelled\"");

    /* set last error (or info) */
    tsip_dialog_set_lasterror(TSIP_DIALOG(self), "Call Cancelled", tsip_event_code_dialog_terminated);

    /* alert the user */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_request,
                              tsip_event_code_dialog_request_incoming, "Incoming Request.", request);

    return ret;
}

/* Ringing -> (iPRACK) -> Ringing */
int s0000_Ringing_2_Ringing_X_iPRACK(va_list *app)
{
    int ret;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    if(!self->last_iInvite){
        /* silently ignore */
        return 0;
    }

    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* Send 2xx PRACK */
    ret = send_RESPONSE(self, request, 200, "OK", tsk_false);

    /* alert the user */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_request,
                              tsip_event_code_dialog_request_incoming, "Incoming Request.", request);

    return ret;
}


/* Started -> (iINVITE) -> Connected */
int s0000_Started_2_Connected_X_iINVITE(va_list *app)
{

    int ret;

    tsip_dialog_invite_t *self;
    //const tsip_action_t* action;
    tsk_bool_t mediaType_changed;

    self = va_arg(*app, tsip_dialog_invite_t *);
    //va_arg(*app, const tsip_message_t *);
    //action = va_arg(*app, const tsip_action_t *);

    //tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    self->is_client = tsk_false;

    TSK_DEBUG_INFO("s0000_Started_2_Connected_X_iINVITE");


    /* Determine whether the remote party support UPDATE */
    self->support_update = tsip_message_allowed(self->last_iInvite, "UPDATE");


    /* Get Media type from the action */
    /*
    mediaType_changed = (TSIP_DIALOG_GET_SS(self)->media.type != action->media.type && action->media.type != tmedia_none);
    if(self->msession_mgr && mediaType_changed){
        ret = tmedia_session_mgr_set_media_type(self->msession_mgr, action->media.type);
    }
*/
    /* Appy media params received from the user */
  /*
    if(!TSK_LIST_IS_EMPTY(action->media.params)){
        ret = tmedia_session_mgr_set_3(self->msession_mgr, action->media.params);
    }
*/
    /* set MSRP callback */
    /*
    if((self->msession_mgr->type & tmedia_msrp) == tmedia_msrp){
        ret = tmedia_session_mgr_set_msrp_cb(self->msession_mgr, TSIP_DIALOG_GET_SS(self)->userdata, TSIP_DIALOG_GET_SS(self)->media.msrp.callback);
    }
    */
    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* send 2xx OK */
    ret = send_RESPONSE(self,request, 200, "OK", tsk_true);

    /* say we're waiting for the incoming ACK */
    self->is_initial_iack_pending = tsk_true;

    /* do not start the session until we get the ACK message
    * http://code.google.com/p/doubango/issues/detail?id=157
    */
    /* do not start the session until we get at least one remote SDP
     * https://code.google.com/p/doubango/issues/detail?id=438
     */
    // FIXME: (chrome) <-RTCWeb Breaker-> (chrome) do not work if media session is not started on i200
    // http://code.google.com/p/webrtc2sip/issues/detail?id=45
    if(/*TSIP_DIALOG_GET_STACK(self)->network.mode == tsip_stack_mode_webrtc2sip*/ TSIP_MESSAGE_HAS_CONTENT(self->last_iInvite)){
        ret = tsip_dialog_invite_msession_start(self);
    }

    /* Session Timers */
    if(self->stimers.timer.timeout){
        if(self->stimers.is_refresher){
            /* RFC 4028 - 9. UAS Behavior
                It is RECOMMENDED that this refresh be sent oncehalf the session interval has elapsed.
                Additional procedures for this refresh are described in Section 10.
            */
            tsip_dialog_invite_stimers_schedule(self, (self->stimers.timer.timeout*1000)/2);
        }
        else{
            tsip_dialog_invite_stimers_schedule(self, (self->stimers.timer.timeout*1000));
        }
    }
    /* update state */
    tsip_dialog_update_2(TSIP_DIALOG(self), request);

    /* alert the user */

    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_newcall,
                              tsip_event_code_dialog_connected, "connected request.", request);


    return ret;
}

/* Ringing -> (oAccept) -> Connected */
int s0000_Ringing_2_Connected_X_Accept(va_list *app)
{

    int ret;

    tsip_dialog_invite_t *self;
    const tsip_action_t* action;
    tsk_bool_t mediaType_changed;

    self = va_arg(*app, tsip_dialog_invite_t *);
    va_arg(*app, const tsip_message_t *);
    action = va_arg(*app, const tsip_action_t *);

    TSK_DEBUG_INFO("s0000_Ringing_2_Connected_X_Accept");


    /* Determine whether the remote party support UPDATE */
    self->support_update = tsip_message_allowed(self->last_iInvite, "UPDATE");

    /* Get Media type from the action */
    mediaType_changed = (TSIP_DIALOG_GET_SS(self)->media.type != action->media.type && action->media.type != tmedia_none);
    if(self->msession_mgr && mediaType_changed){
        ret = tmedia_session_mgr_set_media_type(self->msession_mgr, action->media.type);
    }

    /* Appy media params received from the user */
    if(!TSK_LIST_IS_EMPTY(action->media.params)){
        ret = tmedia_session_mgr_set_3(self->msession_mgr, action->media.params);
    }

    /* set MSRP callback */
    if((self->msession_mgr->type & tmedia_msrp) == tmedia_msrp){
        ret = tmedia_session_mgr_set_msrp_cb(self->msession_mgr, TSIP_DIALOG_GET_SS(self)->userdata, TSIP_DIALOG_GET_SS(self)->media.msrp.callback);
    }

    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* send 2xx OK */
    ret = send_RESPONSE(self, self->last_iInvite, 200, "OK", tsk_true);

    /* say we're waiting for the incoming ACK */
    self->is_initial_iack_pending = tsk_true;

    /* do not start the session until we get the ACK message
    * http://code.google.com/p/doubango/issues/detail?id=157
    */
    /* do not start the session until we get at least one remote SDP
     * https://code.google.com/p/doubango/issues/detail?id=438
     */
    // FIXME: (chrome) <-RTCWeb Breaker-> (chrome) do not work if media session is not started on i200
    // http://code.google.com/p/webrtc2sip/issues/detail?id=45
    if(/*TSIP_DIALOG_GET_STACK(self)->network.mode == tsip_stack_mode_webrtc2sip*/ TSIP_MESSAGE_HAS_CONTENT(self->last_iInvite)){
        ret = tsip_dialog_invite_msession_start(self);
    }

    /* Session Timers */
    if(self->stimers.timer.timeout){
        if(self->stimers.is_refresher){
            /* RFC 4028 - 9. UAS Behavior
                It is RECOMMENDED that this refresh be sent oncehalf the session interval has elapsed.
                Additional procedures for this refresh are described in Section 10.
            */
            tsip_dialog_invite_stimers_schedule(self, (self->stimers.timer.timeout*1000)/2);
        }
        else{
            tsip_dialog_invite_stimers_schedule(self, (self->stimers.timer.timeout*1000));
        }
    }



    /* alert the user (dialog) */
    TSIP_DIALOG_SIGNAL(self, tsip_event_code_dialog_connected, "Dialog connected");

    return ret;
}

/* Ringing -> (oReject) -> Terminated */
int s0000_Ringing_2_Terminated_X_Reject(va_list *app)
{
    int ret;
    short code;
    const char* phrase;
    char* reason = tsk_null;

    tsip_dialog_invite_t *self;
    const tsip_action_t* action;

    self = va_arg(*app, tsip_dialog_invite_t *);
    va_arg(*app, const tsip_message_t *);
    action = va_arg(*app, const tsip_action_t *);

    /* Cancel 100rel timer */
    TSIP_DIALOG_TIMER_CANCEL(100rel);

    /* Send Reject */
    code = action->line_resp.code>=300 ? action->line_resp.code : 603;
    phrase = action->line_resp.phrase ? action->line_resp.phrase : "Decline";
    tsk_sprintf(&reason, "SIP; cause=%hi; text=\"%s\"", code, phrase);
    ret = send_ERROR(self, self->last_iInvite, code, phrase, reason);
    TSK_FREE(reason);

    /* set last error (or info) */
    tsip_dialog_set_lasterror(TSIP_DIALOG(self), "Call Terminated", tsip_event_code_dialog_terminated);

    return ret;
}

/* Ringing ->(iCANCEL) -> Terminated */
int s0000_Ringing_2_Terminated_X_iCANCEL(va_list *app)
{
    int ret;
    tsip_response_t* response;

    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);
    tsip_request_t *request = va_arg(*app, tsip_request_t *);

    if(!self->last_iInvite){
        /* silently ignore */
        return 0;
    }

    /* Send 2xx for the CANCEL (Direct to Transport layer beacause CANCEL is a special case) */
    if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 200, "OK", request))){
        ret = tsip_transport_layer_send(TSIP_DIALOG_GET_STACK(self)->layer_transport, tsk_null, response);
        TSK_OBJECT_SAFE_FREE(response);
    }

    /* Send Request Cancelled */
    ret = send_ERROR(self, self->last_iInvite, 487, "Request Cancelled", "SIP; cause=487; text=\"Request Cancelled\"");

    /* set last error (or info) */
    tsip_dialog_set_lasterror(TSIP_DIALOG(self), "Call Cancelled", tsip_event_code_dialog_terminated);

    /* alert the user */
    TSIP_DIALOG_INVITE_SIGNAL(self, tsip_i_request,
                              tsip_event_code_dialog_request_incoming, "Incoming Request.", request);

    return ret;
}

/* Any ->(timer 100rel) -> Any */
int s0000_Any_2_Any_X_timer100rel(va_list *app)
{
    tsip_dialog_invite_t *self = va_arg(*app, tsip_dialog_invite_t *);

    int ret;

    if(!self->last_o1xxrel){
        /* silently ignore */
        return 0;
    }

    /* resync timer */
    if((self->timer100rel.timeout *= 2) >= (64 * tsip_timers_getA())){
        TSK_DEBUG_ERROR("Sending reliable 1xx failed");
        return -2;
    }

    /* resend reliable 1xx */
    if((ret = tsip_dialog_response_send(TSIP_DIALOG(self), self->last_o1xxrel))){
        return ret;
    }
    else{
        /* schedule timer */
        TSIP_DIALOG_INVITE_TIMER_SCHEDULE(100rel);
    }

    return ret;
}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//				== STATE MACHINE END ==
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++

int send_UNSUPPORTED(tsip_dialog_invite_t* self, const tsip_request_t* request, const char* option)
{
    tsip_response_t *response;

    if(!self || !option){
        TSK_DEBUG_ERROR("Invalid parameter");
        return -1;
    }

    if((response = tsip_dialog_response_new(TSIP_DIALOG(self), 420, "Bad Extension", request))){
        // Add UnSupported header
        tsip_message_add_headers(response,
                                 TSIP_HEADER_DUMMY_VA_ARGS("Unsupported", option),
                                 TSIP_HEADER_DUMMY_VA_ARGS("Reason", "SIP; cause=420; text=\"Bad Extension\""),
                                 tsk_null
        );

        tsip_dialog_response_send(TSIP_DIALOG(self), response);
        TSK_OBJECT_SAFE_FREE(response);
    }
    return 0;
}


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


static tsip_uri_t *string_to_uri(const char* stringData){
    tsip_uri_t *uri = tsip_uri_create(uri_unknown);
    if(!tsk_strnullORempty(stringData) && (uri = tsip_uri_parse(stringData, tsk_strlen(stringData)))!=tsk_null ){
        if(uri->type == uri_unknown){ /* scheme is missing or unsupported? */
            tsk_strupdate(&uri->scheme, "sip");
            uri->type = uri_sip;
        }
        return uri;
    }
    else
    TSK_DEBUG_ERROR("is not uri");
    return uri;
}

