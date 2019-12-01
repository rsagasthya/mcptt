/*
 *
 *   Copyright (C) 2018, University of the Basque Country (UPV/EHU)
 *
 *  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
 *
 *  This file is part of MCOP MCPTT Client
 *
 *  This is free software: you can redistribute it and/or modify it under the terms of
 *  the GNU General Public License as published by the Free Software Foundation, either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.mcopenplatform.muoapi.datatype.error;

import org.mcopenplatform.muoapi.ConstantsMCOP.CallEventExtras.CallTypeEnum;


public class Constants {

    public static class CallEvent{
        public enum CallTypeValidEnum {
            NONE(0),
            AudioWithoutFloorCtrlPrivate(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithoutFloorCtrl.getValue()| CallTypeEnum.Private.getValue()),
            AudioWithFloorCtrlPrivate(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Private.getValue()),
            AudioWithFloorCtrlPrivateEmergency (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Private.getValue()| CallTypeEnum.Emergency.getValue()),
            AudioWithFloorCtrlPrearrangedGroupEmergency (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()| CallTypeEnum.Emergency.getValue()),
            AudioWithFloorCtrlPrearrangedGroupImminentPeril (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            AudioWithFloorCtrlPrearrangedGroup(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()),
            AudioWithFloorCtrlChatGroupEmergency (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()| CallTypeEnum.Emergency.getValue()),
            AudioWithFloorCtrlChatGroupImminentPeril (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            AudioWithFloorCtrlChatGroup(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()),
            AudioWithFloorCtrlBroadcastpEmergency (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()| CallTypeEnum.Emergency.getValue()),
            AudioWithFloorCtrlBroadcastImminentPeril (CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            AudioWithFloorCtrlBroadcast(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()),
            AudioWithFloorCtrlFirstToAnswer(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.FirstToAnswer.getValue()),
            AudioWithFloorCtrlPrivateCallCallback(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrivateCallCallback.getValue()),
            AudioWithFloorCtrlRemoteAmbientListening(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.RemoteAmbientListening.getValue()),
            AudioWithFloorCtrlLocalAmbientListening(CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.LocalAmbientListening.getValue()),
            VideoAudioWithFloorCtrlPrivate(CallTypeEnum.Video.getValue() | CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Private.getValue()),
            VideoAudioWithFloorCtrlPrivateEmergency (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Private.getValue()| CallTypeEnum.Emergency.getValue()),
            VideoAudioWithFloorCtrlPrearrangedGroupEmergency (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()| CallTypeEnum.Emergency.getValue()),
            VideoAudioWithFloorCtrlPrearrangedGroupImminentPeril (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            VideoAudioWithFloorCtrlPrearrangedGroup(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrearrangedGroup.getValue()),
            VideoAudioWithFloorCtrlChatGroupEmergency (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()| CallTypeEnum.Emergency.getValue()),
            VideoAudioWithFloorCtrlChatGroupImminentPeril (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            VideoAudioWithFloorCtrlChatGroup(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.ChatGroup.getValue()),
            VideoAudioWithFloorCtrlBroadcastpEmergency (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()| CallTypeEnum.Emergency.getValue()),
            VideoAudioWithFloorCtrlBroadcastImminentPeril (CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()| CallTypeEnum.ImminentPeril.getValue()),
            VideoAudioWithFloorCtrlBroadcast(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.Broadcast.getValue()),
            VideoAudioWithFloorCtrlFirstToAnswer(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.FirstToAnswer.getValue()),
            VideoAudioWithFloorCtrlPrivateCallCallback(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.PrivateCallCallback.getValue()),
            VideoAudioWithFloorCtrlRemoteAmbientListening(CallTypeEnum.Video.getValue() |CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.RemoteAmbientListening.getValue()),
            VideoAudioWithFloorCtrlLocalAmbientListening(CallTypeEnum.Video.getValue() | CallTypeEnum.Audio.getValue() | CallTypeEnum.WithFloorCtrl.getValue()| CallTypeEnum.LocalAmbientListening.getValue());

            private int code;
            CallTypeValidEnum(int code) {
                this.code = code;
            }
            public int getValue() {
                return code;
            }
        }
    }

    public static class ConstantsErrorMCOP {

        public enum CallEventError{
            CIII(103,"maximum simultaneous MCPTT group calls reached\tThe number of maximum simultaneous MCPTT group calls supported for the MCPTT user has been exceeded."),
            CIV(104,"isfocus not assigned	A controlling MCPTT function has not been assigned to the MCPTT session"),
            CV(105,"subscription not allowed in a broadcast group call	Subscription to the conference event package rejected during a group call initiated as a broadcast group call"),
            CVI(106,"user not authorised to join chat group	The MCPTT user is not authorised to join this chat group"),
            CVII(107,"user not authorised to make private calls	The MCPTT user is not authorised to make private calls"),
            CVIII(108,"user not authorised to make chat group calls	The MCPTT user is not authorised to make chat group calls"),
            CIX(109,"user not authorised to make prearranged group calls	The MCPTT user is not authorised to make group calls to a prearranged group"),
            CX(110,"user declined the call invitation	The MCPTT user declined to accept the call"),
            CXI(111,"group call proceeded without all required group members	The required members of the group did not respond within the acknowledged call time, but the call still went ahead"),
            CXII(112,"group call abandoned due to required group members not part of the group session	The group call was abandoned, as the required members of the group did not respond within the acknowledged call time"),
            CXVI(116,"user is not part of the MCPTT group	The group exists on the group management server but the requesting user is not part of this group"),
            CXVII(117,"the group identity indicated in the request is a prearranged group	The group id that is indicated in the request is for a prearranged group, but did not match the request from the MCPTT user"),
            CXVIII(118,"the group identity indicated in the request is a chat group	The group id that is indicated in the request is for a chat group, but did not match the request from the MCPTT user"),
            CXIX(119,"user is not authorised to initiate the group call	The MCPTT user identified by the MCPTT ID is not authorised to initiate the group call"),
            CXX(120,"user is not affiliated to this group	The MCPTT user is not affiliated to the group"),
            CXXI(121,"user is not authorised to join the group call	The MCPTT user identified by the MCPTT ID is not authorised to join the group call"),
            CXXII(122,"too many participants	The group call has reached its maximum number of participants"),
            CXXIII(123,"MCPTT session already exists	Inform the MCPTT user that the group call is currently ongoing"),
            CXXIV(124,"maximum number of private calls reached	The maximum number of private calls allowed at the MCPTT server for the MCPTT user has been reached"),
            CXXVII(127,"user not authorised to be called in private call	The called MCPTT user is not allowed to be part of a private call"),
            CXLIV(144,"user not authorised to call this particular user	The calling user is not authorised to call this particular called user"),
            CXLV(145,"unable to determine called party	The participating function was unable to determine the called party from the information received in the SIP request"),
            CXLVII(147,"user is authorized to initiate a temporary group call	The non-controlling MCPTT function has authorized a request from the controlling MCPTT function to authorize a user to initiate an temporary group session"),
            CXLVIII(148,"MCPTT group is regrouped	The MCPTT group hosted by a non-controlling MCPTT function is part of a temporary group session as the result of the group regroup function"),
            CLI(151,"user not authorised to make a private call call-back request	The MCPTT user is not authorised to make a private call call-back request"),
            CLII(152,"user not authorised to make a private call call-back cancel request	The MCPTT user is not authorised to make a private call call-back cancel request"),
            CLIII(153,"user not authorised to call any of the users requested in the first-to-answer call	All users that were invited in the first-to-answer call cannot be involved in a private call with the inviting user"),
            CLIV(154,"user not authorised to make ambient listening call	The MCPTT user is not authorised to make an ambient listening call"),
            CLVI(156,"user not authorised to originate a first-to-answer call	The MCPTT user is not authorised to make a first-to-answer call"),
            CDI(401,"Invalid call type"),
            CDII(402,"Invalid action"),
            CDIIII(403,"Invalid the UserID. the UserID is empty, or don´t have URI format"),
            CDIV(404,"The call can not be hung because is not made"),
            CDV(405,"The call cannot be accepted because it has not received any call for that sessionID"),
            CDVI(406,"The sessionID could not be created"),
            CDVII(407,"The call cannot be made because this unregistered"),
            CDVIII(408,"User destination is not available at the moment"),
            CDIX(409,"Undefined signal error"),
            CDVX(410,"The destination is busy");



            int code;
            String string;
            CallEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum UnLoginEventError{
            CCV(205,"Impossible to unregister because the customer is not registered right now"),
            CCVII(207,"There was an error in the unregistration process");

            int code;
            String string;

            UnLoginEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum FloorControlEventCauseCode{

            CI(101,"Cause #1 - Another MCPTT client has permission The <Reject cause> value set to '1' indicates that another MCPTT user has permission to send a media."),
            CII(102,"Cause #2 - Internal floor control server error The <Reject cause> value set to '2' indicates that the floor control server cannot grant the floor request due to an internal error."),
            CIII(103,"Cause #3 - Only one participant The <Reject cause> value set to '3' indicates that the floor control server cannot grant the floor request, because the requesting party is the only participant in the MCPTT session."),
            CIV(104,"Cause #4 - Retry-after timer has not expired The <Reject cause> value set to '4' indicates that the floor control server cannot grant the floor request, because timer T9 (Retry-after) has not expired after permission to send media has been revoked."),
            CV(105,"Cause #5 - Receive only The <Reject cause> value set to '5' indicates that the floor control server cannot grant the floor request, because the requesting party only has receive privilege."),
            CVI(106,"Cause #6 - No resources available The <Reject cause> value set to '6' indicates that the floor control server cannot grant the floor request due to congestion."),
            CVII(107,"Cause #7 – Queue full The <Reject cause> value set to 7 indicates that the floor control server cannot queue the floor request, because the queue is full."),
            CVIII(108,"Cause #255 - Other reason The <Reject cause> value set to '255' indicates that the floor control server does not grant the floor request due to the floor control server local policy."),
            CCI(201,"Cause #1 – Only one MCPTT client The <Reject Cause> value set to '1' indicates that the MCPTT client is the only MCPTT client in the MCPTT session or the only participant connected to a floor control server. No additional information included."),
            CCII(202,"Cause #2 – Media burst too long The <Reject Cause> value set to '2' indicates that the MCPTT User has talked too long (e.g., the stop-talking timer has expired). No additional information included."),
            CCIII(203,"Cause #3 - No permission to send a Media Burst The <Reject Cause> value set to '3' indicates that the MCPTT client does not have permission to send media. No additional information is included."),
            CCIV(204,"Cause #4 - Media Burst pre-empted The <Reject Cause> value set to '4' indicates that the MCPTT client 's permission to send a media is being pre-empted. No additional information is included."),
            CCV(205,"Cause #6 - No resources available The <Reject Cause> value set to '6' indicates that the floor control server can no longer grant MCPTT client to send media due to congestion. No additional information is included."),
            CCVI(206,"Cause #255 – Other reason The <Reject Cause> value set to '255' indicates that the floor control server can no longer grant MCPTT client to send media due to the floor control server local policy. No additional information is included."),
            CCCI(301,"Cause #1 - Transmission limit reached The <Reject cause> value set to '1' indicates that the number of transmitters have reached maximum."),
            CCCII(302,"Cause #2 - Internal transmission control server error The <Reject cause> value set to '2' indicates that the transmission control server cannot grant the transmission request due to an internal error."),
            CCCIII(303,"Cause #3 - Only one participant The <Reject cause> value set to '3' indicates that the transmission control server cannot grant the transmission request, because the requesting party is the only participant in the MCVideo session."),
            CCCIV(304,"Cause #4 - Retry-after timer has not expired The <Reject cause> value set to '4' indicates that the transmission control server cannot grant the transmission request, because timer T9 (Retry-after) has not expired after permission to send media has been revoked."),
            CCCV(305,"Cause #5 - Receive only The <Reject cause> value set to '5' indicates that the transmission control server cannot grant the transmission request, because the requesting party only has receive privilege."),
            CCCVI(306,"Cause #6 - No resources available The <Reject cause> value set to '6' indicates that the transmission control server cannot grant the transmission request due to congestion."),
            CCCVII(307,"Cause #255 - Other reason The <Reject cause> value set to '255' indicates that the transmission control server does not grant the transmission request due to the transmission control server local policy."),
            CDI(401,"Cause #1 – Only one MCVideo client The <Reject Cause> value set to '1' indicates that the MCVideo client is the only MCVideo client in the MCVideo session or the only participant connected to a transmission control server. No additional information included."),
            CDII(402,"Cause #2 – Media burst too long The <Reject Cause> value set to '2' indicates that the MCVideo User has transmitted too long (e.g., the stop-transmission timer has expired). No additional information included."),
            CDIII(403,"Cause #3 - No permission to send a Media Burst The <Reject Cause> value set to '3' indicates that the MCVideo client does not have permission to send media. No additional information is included."),
            CDIV(404,"Cause #4 - Media Burst pre-empted The <Reject Cause> value set to '4' indicates that the MCVideo client's permission to send a media is being pre-empted. No additional information is included."),
            CDV(405,"Cause #6 - No resources available The <Reject Cause> value set to '6' indicates that the transmission control server can no longer grant MCVideo client to send media due to congestion. No additional information is included."),
            CDVI(406,"Cause #255 – Other reason The <Reject Cause> value set to '255' indicates that the transmission control server can no longer grant MCVideo client to send media due to the transmission control server local policy. No additional information is included."),
            DI(501,"Cause #2 - Internal transmission control server error The <Reject cause> value set to '2' indicates that the transmission control server cannot grant the receive media request due to an internal error."),
            DII(502,"Cause #4 - Retry-after timer has not expired The <Reject cause> value set to '4' indicates that the transmission control server cannot grant the receive media request, because timer T9 (Retry-after) has not expired after permission to send media has been revoked."),
            DIII(503,"Cause #5 - Send only The <Reject cause> value set to '5' indicates that the transmission control server cannot grant the receive media request, because the requesting party only has send privilege."),
            DIV(504,"Cause #6 - No resources available The <Reject cause> value set to '6' indicates that the transmission control server cannot grant the receive media request due to congestion."),
            DV(505,"Cause #255 - Other reason The <Reject cause> value set to '255' indicates that the transmission control server does not grant the receive media request due to the transmission control server local policy.");

            int code;
            String string;

            FloorControlEventCauseCode(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum FloorControlEventError{
            CI(101,"The session cannot be accepted because it has not received any session for that sessionID"),
            CII(102,"Floor control invalid operation");

            int code;
            String string;

            FloorControlEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum GroupAffiliationEventError{
            CI(101,"Non-existent group"),
            CII(102,"Action not allowed"),
            CIII(103,"The user is not a member of the group"),
            CIV(104,"The group is not currently affiliate"),
            CV(105,"The group already is affiliated"),
            CVI(106,"The group already is not affiliated"),
            CVII(107,"The group does not belong to the list of existing groups"),
            CVIII(108,"We don't have information of affiliation"),
            CIX(109,"The group already is affiliating or deaffiliating");

            int code;
            String string;
            GroupAffiliationEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum GroupInfoEventError{
            CI(101,"Received data groups not valid");

            int code;
            String string;

            GroupInfoEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum MbmsInfoEventError{
            CI(101,"Received data mbms not valid");

            int code;
            String string;

            MbmsInfoEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum LoginEventError{
            CI(101,"Unknown GMS address"),
            CII(102,"Unknown CMS address"),
            CIII(103,"Unknown KMS address"),
            CIV(104,"Incorrect answer from GMS"),
            CV(105,"Incorrect answer from CMS"),
            CVI(106,"Incorrect answer from KMS"),
            CVII(107,"There was error with CMS"),
            CCI(201,"IMS registration error"),
            CCII(202,"IMS authentication error"),
            CCIII(203,"Synchronization error between SIM and IMS"),
            CCIV(204,"SIM access error"),
            CCV(205,"It is not possible to register because the customer is registered right now"),
            CCVI(206,"the URL for the authentication is not valid"),
            CCVII(207,"There was an error in the registration process"),
            CCVIII(208,"Error in the authentication process");

            int code;
            String string;
            LoginEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }

        public enum AuthorizationRequestEventError{
            CI(101,"Impossible to obtain authentication data");

            int code;
            String string;

            AuthorizationRequestEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }
        public enum ConfigurationUpdateEventError{
            CI(101,"It has been impossible to obtain authentication data");

            int code;
            String string;

            ConfigurationUpdateEventError(int code, String string) {
                this.code = code;
                this.string = string;
            }

            public  int getCode() {
                return code;
            }

            public  String getString() {
                return string;
            }
        }
        //#end if HAVE_CMS_USER_INFO
    }
}