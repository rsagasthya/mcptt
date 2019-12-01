/*
 *

 *   Copyright (C) 2017, University of the Basque Country (UPV/EHU)
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
package org.doubango.ngn.services.impl.emergency;

import android.content.Context;
import android.util.Log;

import org.doubango.ngn.datatype.emergency.StateEmergencyType;
import org.doubango.ngn.services.emergency.IMyEmergencyService;
import org.doubango.ngn.services.impl.location.MyLocalizationService;
import org.doubango.utils.Utils;






public class MyEmergencyService implements IMyEmergencyService {
    private final static String TAG = Utils.getTAG(MyEmergencyService.class.getCanonicalName());
    private StateEmergencyType currentState;

    private org.doubango.ngn.services.impl.location.MyLocalizationService myLocalizationService;

    /*
    MCPTT emergency group state values
    State-entering events
    Comments
    MEG 1: no-emergency
    initial state prior to any call activity

    Emergency group call cancel request received on behalf of another user from the MCPTT server

    Emergency group call cancel response (success) in response to initiator's request

    MEG 2: in-progress
    Emergency group call response received (confirm) to initiator's emergency group call request

    Emergency group call request received (on behalf of another user)
    In this state, all participants in calls on this group will receive emergency level priority whether or not they are in the MCPTT emergency state themselves.
    MEG 3: cancel-pending
    Emergency group call cancel request sent by initiator
    The controlling MCPTT server may not grant the cancel request for various reasons, e.g., other users in an MCPTT emergency state remain in the call.
    MEG 4: confirm-pending
    Emergency group call request sent by initiator

    The controlling MCPTT server may not grant the call request for various reasons, e.g., the MCPTT group is not configured as being emergency-capable so it can't be assumed that the group will enter the in-progress state.

     */
    private enum MCPTTEmergencyGroupStatus{
        NONE,//NONE STATUS
        MEG1,//no-emergency
        MEG2,//in-progress
        MEG3,//cancel-pending
        MEG4,//confirm-pending
    }

    /*
    MCPTT emergency alert state values
    State-entering events
    Comments
    MEA 1: no-alert
    initial state
    emergency alert cancelled
    emergency alert request denied
    emergency alerts can be cancelled in several ways:
    MCPTT emergency alert cancel request with <alert-ind> set to "false" (by initiator)
    MCPTT emergency alert cancel request with <alert-ind> set to "false" (by authorised user)
    MCPTT emergency group call cancel request with <alert-ind> set to "false"
    MCPTT emergency state: may be set or clear, depending on MCPTT emergency call status
    MEA 2:
    emergency alert request sent
    emergency alerts can be requested in several ways:
    MCPTT emergency alert request with <alert-ind> set to "true"
    MCPTT emergency group call request with <alert-ind> set to "true"
    MCPTT emergency state: is set
    MEA 3: emergency-alert -initiated
    emergency alert response (success) received
    MCPTT emergency state: is set
    MEA 4: emergency-alert-cancel-pending
    emergency alert cancellation request sent by alert originator
    MCPTT emergency state: is clear
 */
    private enum MCPTTEmergencyAlertStatus{
        NONE,//NONE STATUS
        MEA1,//no-alert
        MEA2,//emergency-alert-confirm-pending
        MEA3,//emergency-alert -initiated
        MEA4,//emergency-alert-cancel-pending
    }


    /*
     MCPTT emergency group call state values
     Semantics
     Comments
     MEGC 1: emergency-gc-capable
     MCPTT client emergency-capable client is not currently in an MCPTT emergency group call that it has originated, nor is it in the process of initiating one.
     MCPTT emergency state:
     may or may not be set in this state, depending upon the MCPTT client's MEA state
     MEGC 2: emergency-call-requested
     MCPTT client has initiated an MCPTT emergency group call request.
     MCPTT emergency state: is set
     MEGC 3: emergency-call-granted
     MCPTT client has received an MCPTT emergency group call grant.
     If the MCPTT user initiates a call while the MCPTT emergency state is still set, that call will be an MCPTT emergency group call, assuming that group is authorised for the client to initiate emergency group calls on.
     MCPTT emergency state: is set
     */
    private enum MCPTTEmergencyGroupCallState{
        NONE,//NONE STATUS
        MEGC1,//emergency-gc-capable
        MEGC2,//emergency-call-requested
        MEGC3,//emergency-call-granted
    }


    @Override
    public boolean start() {
        Log.d(TAG,"Start "+"EmergencyService");
        currentState=StateEmergencyType.NO_EMERGENCY;
        myLocalizationService=new MyLocalizationService();
        return true;
    }

    @Override
    public boolean stop() {
        Log.d(TAG,"Start "+"EmergencyService");
        currentState=StateEmergencyType.NO_EMERGENCY;
        return true;
    }

    public boolean isStateEmergency(){
        if(currentState!=null){
            switch (currentState){
                case EMERGENCY:
                    return true;
                case NO_EMERGENCY:
                case NONE:
                default:
                    return false;
            }
        }
        return false;
    }


    public StateEmergencyType changeStateEmergency(Context context){
        if(currentState!=null){
            switch (currentState){
                case EMERGENCY:
                    setStateEmergency(StateEmergencyType.NO_EMERGENCY);
                    break;
                case NO_EMERGENCY:
                    setStateEmergency(StateEmergencyType.EMERGENCY);
                    break;
                case NONE:
                default:
                    Log.e(TAG,"This state for service emergency isn´t valid.");
                    break;
            }

        }
        return currentState;
    }

    public void setStateEmergency(StateEmergencyType emergency){
        if(emergency==null)Log.e(TAG,"This state for service emergency isn´t valid. 2");
        this.currentState=emergency;
    }
    @Override
    public boolean clearService(){
        currentState=StateEmergencyType.NO_EMERGENCY;
        return true;
    }

}
