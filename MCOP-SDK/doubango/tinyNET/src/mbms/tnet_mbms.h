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


#ifndef TNET_MBMS_H
#define TNET_MBMS_H

#include "tinynet_config.h"

#include "tnet_utils.h"

#include "tsk_safeobj.h"


TNET_BEGIN_DECLS

#define TNET_MBMS_TMGI_SIZE				6
#define TNET_MBMS_SERVICE_ID_SIZE		3
#define TNET_MBMS_MCC_SIZE				3

/**MBMS context.
*/
typedef struct tnet_mbms_ctx_s
{
	TSK_DECLARE_OBJECT;
	uint8_t* tmgi;
	tsk_size_t tmgi_size;
    uint8_t* mbms_service_id;
	tsk_size_t mbms_service_id_length;
	uint8_t* mcc;
	tsk_size_t mcc_length;
	uint8_t* mnc;
	tsk_size_t mnc_length;

	TSK_DECLARE_SAFEOBJ;
}
tnet_mbms_ctx_t;

TINYNET_API tsk_bool_t tnet_mbms_check_network_tmgi(tnet_mbms_ctx_t* ctx);
TINYNET_API tsk_bool_t tnet_mbms_parse_tmgi(tnet_mbms_ctx_t* ctx, uint8_t* tmgi, tsk_size_t tmgi_size);
TINYNET_API tnet_mbms_ctx_t* tnet_mbms_ctx_create();

TINYNET_GEXTERN const tsk_object_def_t *tnet_mbms_ctx_def_t;

TNET_END_DECLS

#endif /* TNET_MBMS_H */
