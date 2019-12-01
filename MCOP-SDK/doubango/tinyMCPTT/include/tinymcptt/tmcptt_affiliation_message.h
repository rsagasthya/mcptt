

/*
 * @file tmcptt_affiliation_message.h
 * @brief MCPTT message.
 *
 */
#ifndef TINYMCPTT_MESSAGE_H
#define TINYMCPTT_MESSAGE_H

#include "tinymcptt_config.h"
#include "tsk_object.h"

TMCPTT_BEGIN_DECLS

#define TMCPTT_MESSAGE_AFFILATION(self)				((tmcptt_affiliation_message_t*)(self))

typedef struct tmcptt_affiliation_message_s
{
	TSK_DECLARE_OBJECT;
	
	uint16_t reason_code;
	char* reason_phrase;
	char* user;
	tsk_bool_t is_broadcast_call;
	uint16_t time;
	uint16_t participants;
	uint8_t queue_position;
	uint8_t queue_priority;
}
tmcptt_affiliation_message_t;


TINYMCPTT_API tmcptt_affiliation_message_t* tmcptt_affiliation_message_create(uint16_t rcode, const char* rphrase, const char* user, uint16_t parts, uint16_t t);
TINYMCPTT_API tmcptt_affiliation_message_t* tmcptt_affiliation_message_create_null();

TINYMCPTT_GEXTERN const tsk_object_def_t *tmcptt_affiliation_message_def_t;

TMCPTT_END_DECLS

#endif /* TINYMCPTT_MESSAGE_H */
