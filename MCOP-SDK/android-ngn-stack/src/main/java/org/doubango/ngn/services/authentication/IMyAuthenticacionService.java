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




package org.doubango.ngn.services.authentication;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.doubango.ngn.datatype.openId.CampsType;
import org.doubango.ngn.services.INgnBaseService;


public interface IMyAuthenticacionService extends INgnBaseService {



    CampsType getCampsTypeCurrent(@NonNull Context context);

    String getMCPTTIdNow(@NonNull Context context);

    String register(@NonNull Context context);

     boolean startServiceAuthenticationAfterToken(@NonNull Context context);

     Boolean isAllowAutomaticCommencement(@NonNull Context context);






     boolean initConfigure(Context context,Uri authEndpoint,Uri tokenEndPoint);

     boolean initConfigure(Context context);


     boolean initConfigure(Context  context,String client_id,Uri authEndpoint,Uri tokenEndPoint,Uri redirectUri);


     boolean initConfigure(Context  context,String client_id,Uri issuerUri,Uri redirectUri);





     boolean refreshToken();

     void deleteToken(Context context);


     interface OnAuthenticationListener{
         void onAuthentication(String dataURI,String redirectionURI);
        void onAuthenticationOk(String data);
        void onAuthenticationError(String error);
        void onAuthenticationRefresh(String refresh);

    }

     void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener);



    void getAuthenticationToken(Uri uri);



    //End Timer
}
