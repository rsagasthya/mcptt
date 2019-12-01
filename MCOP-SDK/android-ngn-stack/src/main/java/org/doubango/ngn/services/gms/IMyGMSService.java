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






package org.doubango.ngn.services.gms;

import android.content.Context;

import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;
import org.doubango.ngn.services.INgnBaseService;
import org.doubango.utils.Utils;


public interface IMyGMSService extends INgnBaseService{
    final static String TAG = Utils.getTAG(IMyGMSService.class.getCanonicalName());


    public static final String GMS_ACTION_NOTIFY=TAG +".GMS_ACTION_NOTIFY";
    public static final String GMS_ACTION_SUBSCRIBE=TAG +".GMS_ACTION_SUBSCRIBE";
    public static final String GMS_ACTION_UNSUBSCRIBE=TAG +".GMS_ACTION_UNSUBSCRIBE";

    public static final String GMS_RESPONSE_SUBSCRIBE_ERROR=TAG +".GMS_RESPONSE_SUBSCRIBE_ERROR";
    public static final String GMS_RESPONSE_SUBSCRIBE_OK=TAG +".GMS_RESPONSE_SUBSCRIBE_OK";

    public static final String GMS_NEWGMS_NOTIFY=TAG +".GMS_NEWGMS_NOTIFY";






    void setOnGMSListener(IMyGMSService.OnGMSListener onGMSListener);

    //Init GMS action

    Group getGroupInfo(String group);


    //end GMS action

    interface OnGMSListener{
        void onGMSErrorGroup(String error);
        void onGMSGroup(Group group);
    }

    /**
     * Executed when the service starts
     */
    void startServiceGMS(Context context);



}
