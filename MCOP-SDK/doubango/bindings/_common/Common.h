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
#ifndef TINYWRAP_COMMON_H
#define TINYWRAP_COMMON_H

#include "tinyWRAP_config.h"

#if ANDROID
#	define dyn_cast	static_cast
#	define __JNIENV JNIEnv
#else
#	define dyn_cast	dynamic_cast
#	define __JNIENV void
#endif

typedef enum twrap_media_type_e
{
	// because of Java don't use OR
	twrap_media_none = 0x00,

	twrap_media_audio = 0x01, // (0x01 << 0)
	twrap_media_video = 0x02, // (0x01 << 1)
	twrap_media_msrp = 0x04, // (0x01 << 2)
	twrap_media_t140 = 0x08, // (0x01 << 3)
	twrap_media_bfcp = 0x10,  // (0x01 << 4)
	twrap_media_bfcp_audio = 0x30, // (0x01 << 5) | twrap_media_bfcp;
	twrap_media_bfcp_video = 0x50, // (0x01 << 6) | twrap_media_bfcp;

	twrap_media_audiovideo = 0x03, /* @deprecated */
	twrap_media_audio_video = twrap_media_audiovideo,
	twrap_media_floor_control = (0x01 << 27),
	//MCPTT Zarate
	twrap_media_mcptt = (0x01 << 13),
	twrap_media_group = (0x01 << 14),
	twrap_media_audio_ptt_mcptt =  twrap_media_mcptt | twrap_media_audio,
	twrap_media_audio_ptt_mcptt_with_floor_control =  twrap_media_mcptt | twrap_media_audio | twrap_media_floor_control,

	twrap_media_audio_ptt_group_mcptt = twrap_media_audio_ptt_mcptt | twrap_media_group,
	twrap_media_audio_ptt_group_mcptt_with_floor_control = twrap_media_audio_ptt_mcptt | twrap_media_group |twrap_media_floor_control,

	twrap_media_location= (0x01 << 15),
	twrap_media_mcptt_location=twrap_media_location | twrap_media_mcptt,
	//MCPTT AFFILATION
	twrap_media_affiliation= (0x01 << 16),
	twrap_media_mcptt_affiliation=twrap_media_affiliation | twrap_media_mcptt,
	//MCPTT MBMS
	twrap_media_mbms= (0x01 << 17),
	twrap_media_mcptt_mbms=twrap_media_mbms | twrap_media_mcptt,
	//MCPTT AUTHENTICATION
	twrap_media_authentication= (0x01 << 18),
	twrap_media_mcptt_authentication=twrap_media_authentication | twrap_media_mcptt,

	//CALL EMERGENCY
	twrap_media_emergency= (0x01 << 19),
	twrap_media_alert= (0x01 << 20),
	twrap_media_imminentperil= (0x01 << 21),

	twrap_media_mcptt_emergence= twrap_media_emergency | twrap_media_audio_ptt_mcptt,
	twrap_media_mcptt_emergence_with_floor_control= twrap_media_emergency | twrap_media_audio_ptt_mcptt | twrap_media_floor_control,

	twrap_media_mcptt_group_emergence= twrap_media_mcptt_emergence | twrap_media_group,
	twrap_media_mcptt_group_emergence_with_floor_control= twrap_media_mcptt_emergence | twrap_media_group | twrap_media_floor_control,

	twrap_media_mcptt_alert= twrap_media_alert | twrap_media_audio_ptt_mcptt,
	twrap_media_mcptt_alert_with_floor_control= twrap_media_alert | twrap_media_audio_ptt_mcptt | twrap_media_floor_control,

	twrap_media_mcptt_group_alert= twrap_media_mcptt_alert | twrap_media_group,
	twrap_media_mcptt_group_alert_with_floor_control= twrap_media_mcptt_alert | twrap_media_group | twrap_media_floor_control,

	twrap_media_mcptt_imminentperil= twrap_media_imminentperil | twrap_media_audio_ptt_mcptt,
	twrap_media_mcptt_imminentperil_with_floor_control= twrap_media_imminentperil | twrap_media_audio_ptt_mcptt | twrap_media_floor_control,

	twrap_media_mcptt_group_imminentperil= twrap_media_mcptt_imminentperil | twrap_media_group,
	twrap_media_mcptt_group_imminentperil_with_floor_control= twrap_media_mcptt_imminentperil | twrap_media_group | twrap_media_floor_control,


	twrap_media_cms= (0x01 << 22),
	twrap_media_mcptt_cms=twrap_media_mcptt | twrap_media_cms,
	twrap_media_gms= (0x01 << 23),
	twrap_media_mcptt_gms=twrap_media_mcptt | twrap_media_gms,
	twrap_media_mcptt_chat = (0x01 << 24),
	twrap_media_audio_ptt_chat_mcptt = twrap_media_audio_ptt_mcptt | twrap_media_mcptt_chat,
	twrap_media_audio_ptt_chat_group_mcptt = twrap_media_audio_ptt_mcptt | twrap_media_mcptt_chat | twrap_media_group,
	twrap_media_audio_ptt_chat_group_mcptt_with_floor_control = twrap_media_audio_ptt_mcptt | twrap_media_mcptt_chat | twrap_media_group | twrap_media_floor_control,



	
}
twrap_media_type_t;

#if !defined(SWIG)
#include "tinymedia/tmedia_common.h"

struct media_type_bind_s
{
	twrap_media_type_t twrap;
	tmedia_type_t tnative;
};
static const struct media_type_bind_s __media_type_binds[] =
{
	{ twrap_media_msrp, tmedia_msrp },
	{ twrap_media_audio , tmedia_audio },
	{ twrap_media_video, tmedia_video },
	{ twrap_media_audio_video, (tmedia_type_t)(tmedia_audio | tmedia_video) },
	{ twrap_media_t140, tmedia_t140 },
	{ twrap_media_bfcp, tmedia_bfcp },
	{ twrap_media_bfcp_audio, tmedia_bfcp_audio },
	{ twrap_media_bfcp_video, tmedia_bfcp_video },
	{ twrap_media_audio_ptt_mcptt, tmedia_audio_ptt_mcptt},
	{ twrap_media_audio_ptt_mcptt_with_floor_control, tmedia_audio_ptt_mcptt_with_floor_control},

	{ twrap_media_audio_ptt_group_mcptt,tmedia_audio_ptt_group_mcptt},
	{ twrap_media_audio_ptt_group_mcptt_with_floor_control,tmedia_audio_ptt_group_mcptt_with_floor_control},

//MCPTT LOCATION
	{ twrap_media_mcptt_location,tmedia_mcptt_location},
	//MCPTT AFFILIATION
	{ twrap_media_mcptt_affiliation,tmedia_mcptt_affiliation},
	//MCPTT MBMS
	{ twrap_media_mcptt_mbms,tmedia_mcptt_mbms},
	//MCPTT AUTHENTICATION
	{ twrap_media_mcptt_authentication,tmedia_mcptt_authentication},
	//MCPTT CALL EMERGENCY
	{ twrap_media_mcptt_emergence,tmedia_mcptt_emergence},
	{ twrap_media_mcptt_emergence_with_floor_control,tmedia_mcptt_emergence_with_floor_control},

	{ twrap_media_mcptt_group_emergence,tmedia_mcptt_group_emergence},
	{ twrap_media_mcptt_alert,tmedia_mcptt_alert},
	{ twrap_media_mcptt_alert_with_floor_control,tmedia_mcptt_alert_with_floor_control},

	{ twrap_media_mcptt_group_alert,tmedia_mcptt_group_alert},
	{ twrap_media_mcptt_group_alert_with_floor_control,tmedia_mcptt_group_alert_with_floor_control},

	{ twrap_media_mcptt_imminentperil,tmedia_mcptt_imminentperil},
	{ twrap_media_mcptt_imminentperil_with_floor_control,tmedia_mcptt_imminentperil_with_floor_control},
	{ twrap_media_mcptt_group_imminentperil,tmedia_mcptt_group_imminentperil},
	{ twrap_media_mcptt_group_imminentperil_with_floor_control,tmedia_mcptt_group_imminentperil_with_floor_control},

	{ twrap_media_cms,tmedia_cms},
	{ twrap_media_mcptt_cms,tmedia_mcptt_cms},
	{ twrap_media_gms,tmedia_gms},
	{ twrap_media_mcptt_gms,tmedia_mcptt_gms},
	{ twrap_media_mcptt_chat,tmedia_mcptt_chat},
	{ twrap_media_audio_ptt_chat_mcptt,tmedia_audio_ptt_chat_mcptt},
	{ twrap_media_audio_ptt_chat_group_mcptt,tmedia_audio_ptt_chat_group_mcptt},
	{ twrap_media_audio_ptt_chat_group_mcptt_with_floor_control,tmedia_audio_ptt_chat_group_mcptt_with_floor_control},




};
static const tsk_size_t __media_type_binds_count = sizeof(__media_type_binds)/sizeof(__media_type_binds[0]);
static tmedia_type_t twrap_get_native_media_type(twrap_media_type_t type)
{
	tsk_size_t u;
	tmedia_type_t t = tmedia_none;
	for (u = 0; u < __media_type_binds_count; ++u) {
		if ((__media_type_binds[u].twrap & type) == __media_type_binds[u].twrap) {
			t = (tmedia_type_t)(t | __media_type_binds[u].tnative);
		}
	}
	return t;
}
static twrap_media_type_t twrap_get_wrapped_media_type(tmedia_type_t type)
{
	twrap_media_type_t t = twrap_media_none;
	tsk_size_t u;
	for (u = 0; u < __media_type_binds_count; ++u) {
		if ((__media_type_binds[u].tnative & type) == __media_type_binds[u].tnative) {
			t = (twrap_media_type_t)(t | __media_type_binds[u].twrap);
		}
	}
	return t;
}
#endif

#endif /* TINYWRAP_COMMON_H */

