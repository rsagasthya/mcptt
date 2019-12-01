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
/**@file tnet_mbms.c
 * @brief 3GPP MBMS
 */

#include "tnet_mbms.h"

#include "../tnet_endianness.h"

#include "tsk_string.h"
#include "tsk_memory.h"
#include "tsk_debug.h"

#include <string.h>

tnet_mbms_ctx_t* tnet_mbms_ctx_create()
{
	return tsk_object_new(tnet_mbms_ctx_def_t);
}


tsk_bool_t tnet_mbms_check_network_tmgi(tnet_mbms_ctx_t* ctx)
{
	if(ctx == tsk_null)
		return tsk_false;

	if (ctx->mbms_service_id == tsk_null || 
		ctx->mcc == tsk_null || 
		ctx->mnc == tsk_null ||
	    ctx->mbms_service_id_length != TNET_MBMS_SERVICE_ID_SIZE || 
		ctx->mcc_length != TNET_MBMS_MCC_SIZE || 
		ctx->mnc_length < 2 ||
		ctx->mnc_length > 3)
	{
		return tsk_false;
	}

	//TODO: Need to check MBMS Service ID and MCC/MNC values in MBSFNAreaConfiguration and SystemInformationBlockType1 messages (LTE)
	return tsk_true;
}

tsk_bool_t tnet_mbms_parse_tmgi(tnet_mbms_ctx_t* ctx, uint8_t* tmgi, tsk_size_t tmgi_size)
{
	tsk_bool_t ret = tsk_false;
	uint8_t* p;

	if(ctx == tsk_null)
		return tsk_false;

	if(tmgi_size != TNET_MBMS_TMGI_SIZE)
		return tsk_false;

	if(tmgi == tsk_null)
		return tsk_false;

	#if HAVE_CRT //Debug memory
	ctx->tmgi = (uint8_t*)calloc(TNET_MBMS_TMGI_SIZE, sizeof(uint8_t));
		
	#else
		
	ctx->tmgi = (uint8_t*)tsk_calloc(TNET_MBMS_TMGI_SIZE, sizeof(uint8_t));
	#endif //HAVE_CRT
	if(ctx->tmgi == tsk_null)
		goto bail;
	memcpy(ctx->tmgi, tmgi, tmgi_size);
	ctx->tmgi_size = TNET_MBMS_TMGI_SIZE;
	p = ctx->tmgi;
	#if HAVE_CRT //Debug memory
	ctx->mbms_service_id = (uint8_t*)calloc(TNET_MBMS_SERVICE_ID_SIZE, sizeof(uint8_t));
		
	#else
	ctx->mbms_service_id = (uint8_t*)tsk_calloc(TNET_MBMS_SERVICE_ID_SIZE, sizeof(uint8_t));
		
	#endif //HAVE_CRT
	if(ctx->mbms_service_id == tsk_null)
	{
		TSK_FREE(ctx->tmgi);
		ctx->tmgi_size = 0;
		goto bail;
	}
	memcpy(ctx->mbms_service_id, p, TNET_MBMS_SERVICE_ID_SIZE);
	ctx->mbms_service_id_length = TNET_MBMS_SERVICE_ID_SIZE;
	p += TNET_MBMS_SERVICE_ID_SIZE;
	#if HAVE_CRT //Debug memory
	ctx->mcc = (uint8_t*)calloc(TNET_MBMS_MCC_SIZE, sizeof(uint8_t));
		
	#else
	ctx->mcc = (uint8_t*)tsk_calloc(TNET_MBMS_MCC_SIZE, sizeof(uint8_t));
		
	#endif //HAVE_CRT
	if(ctx->mcc == tsk_null)
	{
		TSK_FREE(ctx->tmgi);
		ctx->tmgi_size = 0;
		TSK_FREE(ctx->mbms_service_id);
		ctx->mbms_service_id_length = 0;
		goto bail;
	}
	ctx->mcc[0] = p[0] & 0x0F;
	ctx->mcc[1] = (p[0] & 0xF0) >> 4;
	ctx->mcc[2] = p[1] & 0x0F; 
	ctx->mcc_length = TNET_MBMS_MCC_SIZE;
	
	if(((p[1] & 0xF0) >> 4) == 0x0F)
	{
		ctx->mnc_length = 2;
		#if HAVE_CRT //Debug memory
		ctx->mnc = (uint8_t*)calloc(ctx->mnc_length, sizeof(uint8_t));
		
		#else
		ctx->mnc = (uint8_t*)tsk_calloc(ctx->mnc_length, sizeof(uint8_t));
		
		#endif //HAVE_CRT
		if(ctx->mnc == tsk_null)
		{
			TSK_FREE(ctx->tmgi);
			ctx->tmgi_size = 0;
			TSK_FREE(ctx->mbms_service_id);
			ctx->mbms_service_id_length = 0;
			TSK_FREE(ctx->mcc);
			ctx->mcc_length = 0;
			ctx->mnc_length = 0;
			goto bail;
		}
		ctx->mnc[0] = p[2] & 0x0F;
		ctx->mnc[1] = (p[2] & 0xF0) >> 4;
	}
	else
	{
		ctx->mnc_length = 3;
		#if HAVE_CRT //Debug memory
		ctx->mnc = (uint8_t*)calloc(ctx->mnc_length, sizeof(uint8_t));
		
	#else
		ctx->mnc = (uint8_t*)tsk_calloc(ctx->mnc_length, sizeof(uint8_t));
		
	#endif //HAVE_CRT
		if(ctx->mnc == tsk_null)
			goto bail;
		ctx->mnc[0] = p[2] & 0x0F;
		ctx->mnc[1] = (p[2] & 0xF0) >> 4;
		ctx->mnc[2] = (p[1] & 0xF0) >> 4;
	}

	ret = tsk_true;

bail:
	return ret;
}


//=================================================================================================
//	[[MBMS CONTEXT]] object definition
//
static tsk_object_t* tnet_mbms_ctx_ctor(tsk_object_t * self, va_list * app)
{
	tnet_mbms_ctx_t *ctx = self;
	if (ctx){

		ctx->tmgi = tsk_null;
		ctx->tmgi_size = 0;
		ctx->mbms_service_id = tsk_null;
		ctx->mbms_service_id_length = 0;
		ctx->mcc = tsk_null;
		ctx->mcc_length = 0;
		ctx->mnc = tsk_null;
		ctx->mnc_length = 0;

		tsk_safeobj_init(ctx);
	}
	return self;
}

static tsk_object_t* tnet_mbms_ctx_dtor(tsk_object_t * self)
{
	tnet_mbms_ctx_t *ctx = self;
	if (ctx){
		tsk_safeobj_deinit(ctx);

		TSK_FREE(ctx->tmgi);
		TSK_FREE(ctx->mbms_service_id);
		TSK_FREE(ctx->mcc);
		TSK_FREE(ctx->mnc);

	}
	return self;
}

static const tsk_object_def_t tnet_mbms_ctx_def_s =
{
	sizeof(tnet_mbms_ctx_t),
	tnet_mbms_ctx_ctor,
	tnet_mbms_ctx_dtor,
	tsk_null,
};
const tsk_object_def_t *tnet_mbms_ctx_def_t = &tnet_mbms_ctx_def_s;
