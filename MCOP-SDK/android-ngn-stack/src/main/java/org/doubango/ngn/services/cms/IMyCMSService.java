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




package org.doubango.ngn.services.cms;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.McpttUEInitialConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.doubango.ngn.services.INgnBaseService;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;

import java.util.List;






public interface IMyCMSService extends INgnBaseService{


    final static String TAG = Utils.getTAG(IMyCMSService.class.getCanonicalName());


    public static final String CMS_ACTION_NOTIFY=TAG +".CMS_ACTION_NOTIFY";
    public static final String CMS_ACTION_SUBSCRIBE=TAG +".CMS_ACTION_SUBSCRIBE";
    public static final String CMS_ACTION_UNSUBSCRIBE=TAG +".CMS_ACTION_UNSUBSCRIBE";

    public static final String CMS_RESPONSE_SUBSCRIBE_ERROR=TAG +".CMS_RESPONSE_SUBSCRIBE_ERROR";
    public static final String CMS_RESPONSE_SUBSCRIBE_OK=TAG +".CMS_RESPONSE_SUBSCRIBE_OK";


    public static final String CMS_NEWCMS_NOTIFY=TAG +".CMS_NEWCMS_NOTIFY";


    //Init logic machine





     boolean setUserProfileForUse(String userProfile,Context context);


    //Step 1: Get UE init config and then get default user profile.
     boolean initConfiguration(Context context,NgnSipPrefrences ngnSipPrefrences);

    //Step 1: Get UE init config and then get default user profile.
     boolean initConfiguration(Context context);


    //End logic Machine


    void setOnCMSPrivateContactsListener(OnCMSPrivateContactsListener onCMSPrivateContactsListener);

    interface OnCMSPrivateContactsListener{
        void onCMSPrivateContactsError();
        void onCMSPrivateContacts(McpttUserProfile mcpttUserProfile);
    }


    //Init CMSData


    //End CMSData


     String register(@NonNull Context context);


    boolean startServiceAuthenticationAfterToken(@NonNull Context context);


    String getMCPTTUEID(Context context);
    String getMCPTTmcpttIdFile(Context context);
    String getMCPTTServiceConfigurationFile(Context context);







    //Init Listener CMS

    void setOnGetMcpttUEInitConfigurationListener(OnGetMcpttUEInitialConfigurationListener onGetMcpttUEInitialConfigurationListener);


    interface OnGetMcpttUEInitialConfigurationListener{
        void onGetmcpttUEInitialConfiguration(McpttUEInitialConfiguration mcpttUEInitialConfiguration);
        void onGetmcpttUEInitialConfigurationError(String error);
    }

     void setOnGetMcpttUEConfigurationListener(OnGetMcpttUEConfigurationListener onGetMcpttUEConfigurationListener);

     interface OnGetMcpttUEConfigurationListener{
        void onGetMcpttUEConfiguration(McpttUEConfiguration mcpttUEConfiguration);
        void onGetMcpttUEConfigurationError(String error);
    }




    interface OnStableListener{
        void onStable();
    }

    void setOnStableListener(OnStableListener onOnStableListener);


    interface OnGetMcpttServiceConfListener{
        void onGetMcpttServiceConf(ServiceConfigurationInfoType mcpttServiceConf);
        void onGetMcpttServiceConfError(String error);
    }

     void setOnGetMcpttServiceConfListener(OnGetMcpttServiceConfListener onGetMcpttServiceConfListener);

    interface OnGetMcpttUserProfile2Listener{
        void onGetMcpttUserProfile(McpttUserProfile mcpttUserProfile);
        void onGetMcpttUserProfileError(String error);
    }
    void setOnGetMcpttUserProfile2Listener(OnGetMcpttUserProfile2Listener onGetMcpttUserProfile2Listener);


    interface OnGetMcpttUserProfileListener{
         void onGetMcpttUserDefaultProfile(McpttUserProfile mcpttUserProfile);
         void onGetMcpttUserProfile(McpttUserProfile mcpttUserProfile);
        void onGetMcpttUserProfileError(String error);
        void onSelectMcpttUserProfile(List<String> mcpttUserProfiles);
    }

    void setOnGetMcpttUserProfileListener(OnGetMcpttUserProfileListener onGetMcpttUserProfileListener);






    void setOnGetServiceConfigurationInfoTypeListener(OnGetServiceConfigurationInfoTypeListener onGetServiceConfigurationInfoTypeListener);

     interface OnGetServiceConfigurationInfoTypeListener{
        void onGetServiceConfigurationInfoType(ServiceConfigurationInfoType serviceConfigurationInfoType);
        void onGetServiceConfigurationInfoTypeError(String error);

    }



     interface OnAuthenticationListener{
         void onAuthentication(String dataURI, String redirectionURI);
        void onAuthenticationOk(String data);
        void onAuthenticationError(String error);
        void onAuthenticationRefresh(String refresh);

    }

     void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener);

    //End Listener CMS

    //Init Configure UE

     boolean configureWithUEInitConfigNow(NgnSipPrefrences ngnSipPrefrences);

     boolean configureWithUEConfigNow(NgnSipPrefrences ngnSipPrefrences);












    //End COnfigure UE

     boolean configureAllProfile(Context context,NgnSipPrefrences ngnSipPrefrences);



     boolean deleteAllProfile(Context context);




     boolean isConfigureNowCMSProfile();

    void getAuthenticationToken(Uri uri);


}
