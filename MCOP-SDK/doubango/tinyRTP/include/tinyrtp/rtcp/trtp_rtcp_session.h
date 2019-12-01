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
/**@file trtp_rtcp_session.h
 * @brief RTCP session.
 *
 * @author Mamadou Diop <diopmamadou(at)doubango.org>
 *

 */
#ifndef TINYMEDIA_RTCP_SESSION_H
#define TINYMEDIA_RTCP_SESSION_H

#include "tinyrtp_config.h"
#include "tinyrtp/trtp_srtp.h"
#include "tinyrtp/rtcp/trtp_rtcp_report_sdes.h"
#include "tnet_types.h"
#include "tsk_timer.h"
#include "tsk_common.h"
#include "tsk_safeobj.h"

TRTP_BEGIN_DECLS

typedef enum event_
{
	EVENT_BYE,
	EVENT_REPORT,
	EVENT_RTP
}
event_;

typedef enum PacketType_
{
	PACKET_RTCP_REPORT,
	PACKET_BYE,
	PACKET_RTP,
}
PacketType_;

struct trtp_rtcp_packet_s;
struct trtp_rtp_packet_s;
struct tnet_ice_ctx_s;



typedef int (*trtp_rtcp_cb_f)(const void* callback_data, const struct trtp_rtcp_packet_s* packet);
typedef double time_tp;
typedef void* packet_;
typedef time_tp (*tc_f)();
typedef tsk_list_t trtp_rtcp_sources_L_t; /**< List of @ref trtp_rtcp_header_t elements */

typedef struct trtp_rtcp_source_s
{
	TSK_DECLARE_OBJECT;

	uint32_t ssrc;			 /* source's ssrc */
	uint16_t max_seq;        /* highest seq. number seen */
	uint32_t cycles;         /* shifted count of seq. number cycles */
	uint32_t base_seq;       /* base seq number */
	uint32_t bad_seq;        /* last 'bad' seq number + 1 */
	uint32_t probation;      /* sequ. packets till source is valid */
	uint32_t received;       /* packets received */
	uint32_t expected_prior; /* packet expected at last interval */
	uint32_t received_prior; /* packet received at last interval */
	uint32_t transit;        /* relative trans time for prev pkt */
	double jitter;         /* estimated jitter */
	
	uint32_t base_ts;	/* base timestamp */
	uint32_t max_ts;	/* highest timestamp number seen */
	uint32_t rate;		/* codec sampling rate */

	uint32_t ntp_msw;  /* last received NTP timestamp from RTCP sender */
	uint32_t ntp_lsw;  /* last received NTP timestamp from RTCP sender */
	uint64_t dlsr;    /* delay since last SR */
}
trtp_rtcp_source_t;

typedef struct trtp_rtcp_session_s
{
	TSK_DECLARE_OBJECT;
	
	tsk_bool_t automated_reporting;
	tsk_bool_t is_MCPTT_session; //Added MCPTT
	tsk_bool_t is_started;
	tnet_fd_t local_fd;
	const struct sockaddr * remote_addr;
	struct tnet_ice_ctx_s* ice_ctx;
	tsk_bool_t is_ice_turn_active;

	const void* callback_data;
	trtp_rtcp_cb_f callback;

	int32_t app_bw_max_upload; // application specific (kbps)
	int32_t app_bw_max_download; // application specific (kbps)

	struct{
		tsk_timer_manager_handle_t* handle_global;
		tsk_timer_id_t id_report;
		tsk_timer_id_t id_bye;
	} timer;

	trtp_rtcp_source_t* source_local; /**< local source */
	trtp_rtcp_report_sdes_t* sdes;
	uint64_t time_start; /**< Start time in millis (NOT in NTP unit yet) */
	
	// <RTCP-FB>
	uint8_t fir_seqnr;
	// </RTCP-FB>

	// <sender>
	char* cname;
	uint32_t packets_count;
	uint32_t octets_count;
	// </sender>



	// <others>
	time_tp tp; /**< the last time an RTCP packet was transmitted; */
	tc_f tc; /**< the current time */
	time_tp tn; /**< the next scheduled transmission time of an RTCP packet */
	int32_t pmembers; /**< the estimated number of session members at the time tn was last recomputed */
	int32_t members; /**< the most current estimate for the number of session members */
	int32_t senders; /**< the most current estimate for the number of senders in the session */
	double rtcp_bw; /**< The target RTCP bandwidth, i.e., the total bandwidth
      that will be used for RTCP packets by all members of this session,
      in octets per second.  This will be a specified fraction of the
      "session bandwidth" parameter supplied to the application at
      startup*/
	tsk_bool_t we_sent; /**< Flag that is true if the application has sent data since the 2nd previous RTCP report was transmitted */
	double avg_rtcp_size; /**< The average compound RTCP packet size, in octets,
      over all RTCP packets sent and received by this participant.  The
      size includes lower-layer transport and network protocol headers
      (e.g., UDP and IP) as explained in Section 6.2*/
	tsk_bool_t initial; /**< Flag that is true if the application has not yet sent an RTCP packet */
	// </others>

	trtp_rtcp_sources_L_t *sources;

	TSK_DECLARE_SAFEOBJ;

#if HAVE_SRTP
	struct{
		const srtp_t* session;
	} srtp;
#endif
}
trtp_rtcp_session_t;

struct trtp_rtcp_session_s* trtp_rtcp_session_create(uint32_t ssrc, const char* cname);
struct trtp_rtcp_session_s* trtp_rtcp_session_create_2(struct tnet_ice_ctx_s* ice_ctx, uint32_t ssrc, const char* cname);
int trtp_rtcp_session_set_callback(struct trtp_rtcp_session_s* self, trtp_rtcp_cb_f callback, const void* callback_data);
#if HAVE_SRTP
int trtp_rtcp_session_set_srtp_sess(struct trtp_rtcp_session_s* self, const srtp_t* session);
#endif
int trtp_rtcp_session_set_app_bandwidth_max(struct trtp_rtcp_session_s* self, int32_t bw_upload_kbps, int32_t bw_download_kbps);
int trtp_rtcp_session_start(struct trtp_rtcp_session_s* self, tnet_fd_t local_fd, const struct sockaddr* remote_addr);
int trtp_rtcp_session_stop(struct trtp_rtcp_session_s* self);
int trtp_rtcp_session_process_rtp_out(struct trtp_rtcp_session_s* self, const struct trtp_rtp_packet_s* packet_rtp, tsk_size_t size);
int trtp_rtcp_session_process_rtp_in(struct trtp_rtcp_session_s* self, const struct trtp_rtp_packet_s* packet_rtp, tsk_size_t size);
int trtp_rtcp_session_process_rtcp_in(struct trtp_rtcp_session_s* self, const void* buffer, tsk_size_t size);
int trtp_rtcp_session_signal_pkt_loss(struct trtp_rtcp_session_s* self, uint32_t ssrc_media, const uint16_t* seq_nums, tsk_size_t count);
int trtp_rtcp_session_signal_frame_corrupted(struct trtp_rtcp_session_s* self, uint32_t ssrc_media);
int trtp_rtcp_session_signal_jb_error(struct trtp_rtcp_session_s* self, uint32_t ssrc_media);
int trtp_rtcp_session_disable_automated_reporting(struct trtp_rtcp_session_s* self);
tsk_size_t trtp_rtcp_send_report(trtp_rtcp_session_t* session);
TRTP_END_DECLS

#endif /* TINYMEDIA_RTCP_SESSION_H */
