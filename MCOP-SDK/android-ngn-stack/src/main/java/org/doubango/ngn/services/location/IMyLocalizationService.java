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




package org.doubango.ngn.services.location;

import android.content.Context;

import org.doubango.ngn.services.INgnBaseService;
import org.doubango.utils.Utils;


public interface IMyLocalizationService extends INgnBaseService {
    final static String TAG = Utils.getTAG(IMyLocalizationService.class.getCanonicalName());

    public static final String LOCATION_ACTION=TAG +".MCPTT_EVENT_FOR_LOCATION_ACTION";
    public static final String LOCATION_NEWLOCATION_INFO=TAG +".MCPTT_EVENT_FOR_LOCATION_NEWLOCATION_INFO";



    //INIT service location


     void startServiceLocation();


     void configureNewServiceLocation(Context context,byte[] locationInfo);


     void reloadServiceLocation(final Context context);



     void stopServiceLocation();

     String createReport(Context context);

     boolean sendLocationNow();

//END service location


     interface OnErrorLocationListener{
        void onErrorLocation(String error, int code);
    }

     void setOnErrorLocationListener(OnErrorLocationListener onErrorLocationListener);



}
