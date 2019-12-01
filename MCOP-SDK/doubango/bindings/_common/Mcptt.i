%{
#include "Mcptt.h"
%}

/* Callbacks */
%feature("director") McpttCallback;
%feature("director") McpttMbmsCallback;

%nodefaultctor;
%include "Mcptt.h"
%clearnodefaultctor;

/* From tinyMCPTT/tmcptt_event.h */
typedef enum tmcptt_event_type_e
{
        tmcptt_event_type_none,
		tmcptt_event_type_token_granted,
		tmcptt_event_type_idle_channel,
		tmcptt_event_type_token_taken,
		tmcptt_event_type_request_sent,
		tmcptt_event_type_release_sent,
		tmcptt_event_type_token_denied,
		tmcptt_event_type_permission_revoked,
		tmcptt_event_type_queued,
		tmcptt_event_type_queued_timeout,
		tmcptt_event_type_queue_pos_request_sent
}
tmcptt_event_type_t;
typedef enum tmcptt_mbms_event_type_e
{
        tmcptt_mbms_event_type_none,
		tmcptt_mbms_event_type_map_group,
		tmcptt_mbms_event_type_unmap_group
}
tmcptt_mbms_event_type_t;
