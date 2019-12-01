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



package org.doubango.ngn.services.mbms;

import android.content.Context;

import org.doubango.ngn.datatype.mbms.MbmsData;
import org.doubango.ngn.services.INgnBaseService;
import org.doubango.utils.Utils;

import java.util.Iterator;
import java.util.List;


public interface IMyMbmsService extends INgnBaseService {
    final static String TAG = Utils.getTAG(IMyMbmsService.class.getCanonicalName());

    public static final String MBMS_ACTION = TAG + ".MBMS_ACTION";
    public static final String MBMS_SESSION_ID_ACTION = TAG + ".MBMS_SESSION_ID_ACTION";
    public static final String MBMS_MEDIA_ACTION = TAG + ".MBMS_MEDIA_ACTION";
    public static final String MBMS_NEWMESSAGE_MBMS = TAG + ".MBMS_NEWMESSAGE_MBMS";
    public static final String MBMS_SESSION_ID_MBMS = TAG + ".MBMS_SESSION_ID_MBMS";
    public static final String MBMS_MAP_MBMS = TAG + ".MBMS_MAP_MBMS";
    public static final String MBMS_GROUP_ID_MBMS = TAG + ".MBMS_GROUP_ID_MBMS";
    public static final String MBMS_PORT_MANAGER_MBMS = TAG + ".MBMS_PORT_MANAGER_MBMS";
    public static final String MBMS_LOCAL_IFACE = TAG + ".MBMS_LOCAL_IFACE";
    public static final String MBMS_LOCAL_IFACE_INDEX = TAG + ".MBMS_LOCAL_IFACE_INDEX";
    public static final String MBMS_IP_MANAGER_MBMS = TAG + ".MBMS_IP_MANAGER_MBMS";
    public static final String MBMS_P_ASSERTED_IDENTITY = TAG + ".MBMS_P_ASSERTED_IDENTITY";
    public static final String MBMS_PORT_MEDIA_MBMS = TAG + ".MBMS_PORT_MEDIA_MBMS";
    public static final String MBMS_PORT_CONTROL_MEDIA_MBMS = TAG + ".MBMS_PORT_CONTROL_MEDIA_MBMS";
    public static final String MBMS_IP_MEDIA_MBMS = TAG + ".MBMS_IP_MEDIA_MBMS";
    public static final String MBMS_TMGI_MBMS = TAG + ".MBMS_TMGI_MBMS";
    public static final String MBMS_SERVICE_AREA_CANCELL = "0";
    public static final int MBMS_DEFAULT_INT = -1;
    public static final String MBMS_CALL_ACTION_MANAGER_START = TAG + ".MBMS_CALL_ACTION_MANAGER_START";
    public static final String MBMS_CALL_ACTION_MANAGER_STOP = TAG + ".MBMS_CALL_ACTION_MANAGER_STOP";
    public static final String MBMS_CALL_ACTION_MEDIA_START = TAG + ".MBMS_CALL_ACTION_MEDIA_START";

    //INIT service location



    MbmsData getMbmsDataOfTmgi(long tmgi);


    /**
     * it receive new service area
     * @param currentServiceArea
     * @return
     */
     boolean onChangeServiceArea(int[] currentServiceArea,Context context);

    /**
     * New service area received
     * TODO: Temporal function
     * @param tmgi
     * @return
     */
     void onReceiveMCCP(String tmgi,String ipMedia,int portMedia,int portControlMedia,Context context);


     void hangUpCallMbms(long sessionID);

     void stopServiceMbms();


    //INIT EXTERNAL MBMS
    interface MbmsExternalServiceListener{
        void startedClient(boolean status);
        void startedServer(boolean status);
        void startMbmsMedia(long sessionID,long tmgi);
        void stopMbmsMedia(long sessionID,long tmgi);
        boolean mbmsListeningServiceAreaCurrent(long TMGI,  int[] sai,  int[] frequencies);
        void stopServiceMBMS();
    }
    void setMbmsExternalServiceListener(MbmsExternalServiceListener mbmsExternalServiceListener);
    //END EXTERNAL MBMS


     interface OnMbmsListener{
        void onNewServiceArea(long TMGI,  int[] sai,  int[] frequencies, long QCI);
    }

     void setOnMbmsListener(OnMbmsListener onMbmsListener);
     Iterator<Integer> getServiceAreas();

     void startMbmsManager(String  interfaceNet,long tmgi);

    void stopMbmsManager(long tmgi);


    List<Long> getTMGIs(int serviceAreaID);

     int[] getSais(long TMGI);

    int[] getFrequencies(long TMGI);

}
