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
package org.doubango.ngn.services.affiliation;

import android.content.Context;

import org.doubango.ngn.datatype.affiliation.affiliationcommand.CommandList;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.services.INgnBaseService;
import org.doubango.utils.Utils;

import java.util.List;
import java.util.Map;


public interface IMyAffiliationService extends INgnBaseService {
    final static String TAG = Utils.getTAG(IMyAffiliationService.class.getCanonicalName());

    public static final String AFFILIATION_ACTION_MESSAGE=TAG +".AFFILIATION_ACTION_MESSAGE";
    public static final String AFFILIATION_ACTION_NOTIFY=TAG +".AFFILIATION_ACTION_NOTIFY";
    public static final String AFFILIATION_ACTION_SUBSCRIBE=TAG +".AFFILIATION_ACTION_SUBSCRIBE";
    public static final String AFFILIATION_ACTION_UNSUBSCRIBE=TAG +".AFFILIATION_ACTION_UNSUBSCRIBE";

    public static final String AFFILIATION_RESPONSE_SUBSCRIBE_ERROR=TAG +".AFFILIATION_RESPONSE_SUBSCRIBE_ERROR";
    public static final String AFFILIATION_RESPONSE_SUBSCRIBE_OK=TAG +".AFFILIATION_RESPONSE_SUBSCRIBE_OK";




    public static final String AFFILIATION_NEWAFFILIATION_NOTIFY=TAG +".AFFILIATION_NEWAFFILIATION_NOTIFY";
    public static final String AFFILIATION_NEWAFFILIATION_MESSAGE=TAG +".AFFILIATION_NEWAFFILIATION_MESSAGE";
    public static final int DEFAULT_SECOS_BETWEEN_CHECK_EXPIRES=50;
    public static final int DELAY_ACTION_AFFILIATION_MSEC=5000;
    public static final String AFFILIATION_NEWAFFILIATION_INFO=TAG +".MCPTT_EVENT_FOR_AFFILIATION_NEWAFFILIATION_INFO";


    String unAffiliationGroup(Context context,String groupSusbcribe);

    String unAffiliationGroups(Context context,List<String> groupsSusbcribe);


    String affiliationGroup(Context context,String groupSusbcribe);



    String affiliationGroups(Context context,List<String> groupsSusbcribe);

    void startServiceAffiliation(Context context);

    /**
     * Executed when the service starts
     */
    void startServiceAffiliation();

    /**
     * Executed when the service stops
     */
    void stopServiceAffiliation();




     Presence getPresenceNow();

     void setPresenceNow(Presence presenceNow);

     void setOnAffiliationServiceListener(OnAffiliationServiceListener onAffiliationServiceListener);

    //Init affiliation action




    //end affiliation action

    interface OnAffiliationServiceListener{
        void receiveNewPresence(Presence presence);
        void receiveNewPresenceResponse(Presence presence, String pid);
        void expireAffiliations(Map<String, String> expires);
        void receiveNewSelfAffiliation(CommandList commandList);
        void startNewServiceAffiliation();
    }




    //INIT affiliation automatically

    void processingCommandList(Context context,CommandList commandList);

    //END affiliation automatically


    boolean isAffiliation(Context context);

    @Override
    boolean clearService();
}
