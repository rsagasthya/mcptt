/*

*  Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* This file is part of MCOP MCPTT Client
*
* This is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package org.doubango.ngn.services.impl.ms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.R;
import org.doubango.ngn.datatype.openId.CampsType;
import org.doubango.ngn.services.authentication.IMyAuthenticacionService;
import org.doubango.ngn.sip.MyPublicationAuthenticationSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.jose4j.jwx.JsonWebStructure;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MyAuthenticacionService implements IMyAuthenticacionService {

    private final static String TAG = Utils.getTAG(MyAuthenticacionService.class.getCanonicalName());

    //Send token
    private MyPublicationAuthenticationSession mSessionPublication;


    private static boolean isStart;
    private String client_id;
    private Uri issuerUri;
    private Uri redirectUri;
    private Context mContext;
    private final static  Uri REDIRECT_URI_DEFAULT=Uri.parse("http://test.redirect.org");
    private final static  Uri ISSUER_URI_DEFAULT=Uri.parse("http://test.issuer.org/openid-connect-server-webapp/.well-known/openid-configuration");
    private final static  String CLIENT_ID_DEFAULT="mcptt_client";
    private AuthorizationServiceConfiguration mAuthorizationServiceConfiguration;
    private BroadcastReceiver broadcastReceiverAuthentication;
    private OnAuthenticationListener onAuthenticationListener;
    private AuthorizationService service;
    private Handler handler;
    private CampsType campsTypeCurrent=null;
    private boolean isSendToken=false;
    private boolean isSendPublish=false;
    private Runnable runnableTimerRefresh;
    private Uri authEndpoint;
    private Uri tokenEndPoint;
    //For response
    private AuthState nowAuthState;
    private AuthorizationRequest mAuthorizationRequest;

    public CampsType getCampsTypeCurrent(@NonNull Context context) {
        if(campsTypeCurrent==null || campsTypeCurrent.isEmpty()){
            try {
                campsTypeCurrent= AuthenticacionUtils.readAuthCamps(context);
                Log.d(TAG,"get current MCPTT ID"+campsTypeCurrent.getMcptt_id());
            } catch (JSONException e) {
                Log.e(TAG,"Error reading authentication data.");
                return null;
            }
        }else{
            Log.w(TAG,"Authentication data not empty.");
        }
        return campsTypeCurrent;
    }

    public String getMCPTTIdNow(@NonNull Context context){
        CampsType campsType=getCampsTypeCurrent(context);
        if(campsType==null || campsType.getMcptt_id()==null){
            Log.e(TAG,"Invalid or not configured MCPTT id.");
            return null;
        }
        return campsType.getMcptt_id();
    }

    public String register(@NonNull Context context){
        String mcpttInfoTypeString=null;
        CampsType campsType;
        String mcpttClientId=NgnEngine.getInstance().getProfilesService().getProfileNow(context).getMcpttClientId();
        Boolean sendTokenInRegister=NgnEngine.getInstance().getProfilesService().getProfileNow(context).isMcpttSelfAuthenticationSendTokenRegister();
        if(sendTokenInRegister!=null && sendTokenInRegister && (campsType=getCampsTypeCurrent(context))!=null && (mcpttInfoTypeString=generateMcpttinfoType(context,campsType,getMcpttID(context),mcpttClientId))!=null){
            Log.d(TAG,"User authenticated: \n"+mcpttInfoTypeString);
            isSendToken=true;
        }else{

        }
        return mcpttInfoTypeString;
    }

    @Override
    public Boolean isAllowAutomaticCommencement(@NonNull Context context) {
        Boolean answerMode=false;
        NgnSipPrefrences profile=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null && profile.isMcpttPrivAnswerMode() || profile.isMcpttAnswerMode()){
            answerMode=true;
        }
        if(profile!=null &&
                profile.getAllowsUserProfile()!=null){
            if ((profile.getAllowsUserProfile().isAllowautomaticcommencement()!=null && profile.getAllowsUserProfile().isAllowautomaticcommencement())){
                answerMode=true;
            }else if((profile.getAllowsUserProfile().isAllowmanualcommencement()==null || !profile.getAllowsUserProfile().isAllowmanualcommencement())){
                answerMode=false;
            }

        }

        return answerMode;
    }

    private short getIndexUserProfile(@NonNull Context context) {
        short index=-1;
        NgnSipPrefrences profile=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null &&
                profile.getIndexUserProfile()!=null &&
                profile.getIndexUserProfile()>=0
                )
            index=profile.getIndexUserProfile();
        return index;
    }

    private String getMcpttID(Context context){
        CampsType campsType=null;
        campsType=getCampsTypeCurrent(context);
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        String mcpttId=null;
        if(ngnSipPrefrences!=null &&
                ngnSipPrefrences.getMcpttId()!=null &&
                campsType!=null &&
                campsType.getMcptt_id()!=null){
            mcpttId=campsType.getMcptt_id();
        }else{
            mcpttId=ngnSipPrefrences.getMcpttId();
        }
        return mcpttId;
    }

    @Override
    public boolean startServiceAuthenticationAfterToken(@NonNull Context context){
        CampsType campsType=null;
        if(isSendPublish || context==null || (campsType=getCampsTypeCurrent(context))==null)return false;
        NgnSipPrefrences profileNow=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        String impu=null;
        String mcpttInfo=null;
        String mcpttClientId=NgnEngine.getInstance().getProfilesService().getProfileNow(context).getMcpttClientId();
        Boolean answerMode=isAllowAutomaticCommencement(context);
        String pocSettings=AuthenticacionUtils.generatePocSettingsType(context,(answerMode!=null?!answerMode:true),getIndexUserProfile(context));
        Boolean sendTokenFail=NgnEngine.getInstance().getProfilesService().getProfileNow(context).isMcpttSelfAuthenticationSendTokenFail();
        //Send
        NgnSipPrefrences ngnSipPrefrences=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        String mcpttId=getMcpttID(context);
        if(profileNow!=null)
        if(isSendToken || (campsType==null || campsTypeCurrent.getAccessToken()==null || campsTypeCurrent.getAccessToken().isEmpty())
        ){
            mcpttInfo=generateMcpttinfoType(context,null,mcpttId,mcpttClientId);
        }else if(campsType!=null || (sendTokenFail!=null && sendTokenFail) ){
            //No send
            mcpttInfo=generateMcpttinfoType(context,campsType,mcpttId,mcpttClientId);
        }else{
            Log.e(TAG,"Error in send token");
        }
        if(profileNow!=null)impu=profileNow.getIMPU();
        if(mSessionPublication==null && impu!=null)
            mSessionPublication= MyPublicationAuthenticationSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(),impu);
        return mSessionPublication.publish(mcpttInfo,pocSettings,context);
    }

    /**
     * In this function, if the campsTypeCurrentToGenerate is null, the device will generate mcpttinfo with token fail.
     * @param context
     * @param campsTypeCurrentToGenerate
     * @param mcpttClientIdString
     * @return
     */
    protected static String generateMcpttinfoType(Context context,CampsType campsTypeCurrentToGenerate,String mcpttId,String mcpttClientIdString){
        if(NgnEngine.getInstance().getProfilesService().getProfileNow(context)==null){
            Log.e(TAG,"Error generate mcptt info");
            return null;
        }
        Boolean sendTokenFail=NgnEngine.getInstance().getProfilesService().getProfileNow(context).isMcpttSelfAuthenticationSendTokenFail();
        return AuthenticacionUtils.generateMcpttinfoType(context,campsTypeCurrentToGenerate,mcpttId,mcpttClientIdString,sendTokenFail!=null?sendTokenFail:false);
    }


    @Override
    public boolean start() {

        Log.d(TAG,"Start "+"OpenIdService");
        isStart=false;





        return true;

    }



    @Override
    public boolean stop() {

        Log.d(TAG,"Stop "+"OpenIdService");
        isStart=false;
        if(broadcastReceiverAuthentication!=null){
            NgnApplication.getContext().unregisterReceiver(broadcastReceiverAuthentication);
            broadcastReceiverAuthentication=null;
        }

        return true;
    }

    public MyAuthenticacionService() {
        this.client_id=CLIENT_ID_DEFAULT;
        this.issuerUri=ISSUER_URI_DEFAULT;
        this.redirectUri=REDIRECT_URI_DEFAULT;
    }


    public boolean initConfigure(Context context,Uri authEndpoint,Uri tokenEndPoint){
        NgnSipPrefrences profile = NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile==null){
            Log.e(TAG,"Self configuration can't start.");
            return false;
        }

        else if(profile.isMcpttIsSelfAuthentication()!=null && profile.isMcpttIsSelfAuthentication()){
            try{
                String clientId=profile.getMcpttSelfAuthenticationClientId();
                String redirectUri=profile.getMcpttSelfAuthenticationRedirectUri();
                return initConfigure(
                        context,
                        clientId,
                        authEndpoint,
                        tokenEndPoint,
                        Uri.parse(redirectUri)
                );
            }catch (Exception ex){
                Log.e(TAG,"Self configuration error: "+ex.getMessage());
                sendError("Self configuration error: "+ex.getMessage());
            }


        }
        return true;
    }

    public boolean initConfigure(Context context){
        NgnSipPrefrences profile = NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile==null){
            Log.e(TAG,"Self configuration can't start.");
            return false;
        }else if(profile.isMcpttIsSelfAuthentication()!=null && profile.isMcpttIsSelfAuthentication()){
            try{
                return initConfigure(
                        context,
                        profile.getMcpttSelfAuthenticationClientId(),
                        Uri.parse(profile.getMcpttSelfAuthenticationIssuerUri()),
                        Uri.parse(profile.getMcpttSelfAuthenticationRedirectUri())
                );
            }catch (Exception ex){
                Log.e(TAG,"Self configuration error: "+ex.getMessage());
            }


        }
        return true;
    }


    public boolean initConfigure(Context  context,String client_id,Uri authEndpoint,Uri tokenEndPoint,Uri redirectUri){
        if(context!=null){
            this.mContext=context;
        }
        if(client_id!=null && !client_id.isEmpty()){
            this.client_id=client_id;
        }
        if(authEndpoint!=null){
            this.authEndpoint=authEndpoint;
        }
        if(tokenEndPoint!=null){
            this.tokenEndPoint=tokenEndPoint;
        }
        if(redirectUri!=null){
            this.redirectUri=redirectUri;
        }
        return initConfigure();
    }


    public boolean initConfigure(Context  context,String client_id,Uri issuerUri,Uri redirectUri){
        if(context!=null){
            this.mContext=context;
        }
        if(client_id!=null && !client_id.isEmpty()){
            this.client_id=client_id;
        }
        if(issuerUri!=null){
            this.issuerUri=issuerUri;
        }
        if(redirectUri!=null){
            this.redirectUri=redirectUri;
        }
        return initConfigure();
    }




    private AuthorizationRequest getAuthorizationRequest(@NonNull AuthorizationServiceConfiguration serviceConfiguration){
        return AuthenticacionUtils.getAuthorizationRequest(serviceConfiguration,mAuthorizationServiceConfiguration,client_id,redirectUri);
    }

    private void startActivityAuthentication(@NonNull AuthorizationServiceConfiguration serviceConfiguration){
        if(BuildConfig.DEBUG)Log.d(TAG,"startActivityAuthentication");
        mAuthorizationServiceConfiguration=serviceConfiguration;

        mAuthorizationRequest=getAuthorizationRequest(serviceConfiguration);

        if(onAuthenticationListener!=null){
            onAuthenticationListener.onAuthentication(mAuthorizationRequest.toUri().toString(),mAuthorizationRequest.redirectUri.toString());
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"Now, Callback Authentication not registed");
        }



    }


    private boolean initConfigure(){
        Log.d(TAG,"Init self configuration.");

            if(authEndpoint!=null && tokenEndPoint!=null){
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"tokenEndPoint: "+tokenEndPoint.toString());
                    Log.d(TAG,"authEndpoint: "+authEndpoint.toString());
                }
                mAuthorizationServiceConfiguration=new AuthorizationServiceConfiguration(authEndpoint,tokenEndPoint,Uri.parse("http://192.168.16.181:8080/idms/register"));
                startActivityAuthentication(mAuthorizationServiceConfiguration);
                return true;
            }else{
                AppAuthConfiguration appAuthConfiguration= AuthenticacionUtils.generateConfigureNet();
                AuthorizationServiceConfiguration.fetchFromUrl(
                        issuerUri,
                        new AuthorizationServiceConfiguration.RetrieveConfigurationCallback() {
                            @Override public void onFetchConfigurationCompleted(
                                    @Nullable AuthorizationServiceConfiguration serviceConfiguration,
                                    @Nullable AuthorizationException ex) {
                                if (ex != null) {
                                    Log.w(TAG, "Failed to retrieve configuration for " + issuerUri, ex);
                                    sendError(mContext.getString(R.string.Failed_to_retrieve_configuration_for)+" "+issuerUri.toString());
                                    return;
                                } else {
                                    try {
                                        startActivityAuthentication(serviceConfiguration);
                                        //AuthorizationService service = new AuthorizationService(mContext);
                                        //Intent postAuthIntent = new Intent(mContext, ScreenAuthetication.class);
                                        //service.performAuthorizationRequest(
                                        //        req,
                                        //        PendingIntent.getActivity(mContext, req.hashCode(), postAuthIntent, 0));//

                                        //
                                    }catch (Exception e){
                                        Log.e(TAG,"Error processing data from configuration:"+e.toString());
                                        return;
                                    }

                                }
                            }
                        },appAuthConfiguration.getConnectionBuilder());
                return true;
            }


    }


    public boolean refreshToken(){
        Log.d(TAG,"Refresh Token init.");
        if(mContext==null){
            return false;
        }
         try {
            final AuthState state= AuthenticacionUtils.readAuthState(mContext);
             return refreshToken(mContext,state);
        } catch (JSONException e) {
             Log.e(TAG,"Error refreshing Token: "+e.getMessage());
            return false;
        }


    }

    public void deleteToken(Context context){
        saveAuthCamp(null);
        campsTypeCurrent=null;
    }

    private boolean refreshToken(final Context context,final AuthState state){
        if(context==null){
            return false;
        }
        if(state==null){
            return false;
        }AppAuthConfiguration appAuthConfiguration= AuthenticacionUtils.generateConfigureNet();
        if(appAuthConfiguration==null){
            return false;
        }
        service = new AuthorizationService(mContext,appAuthConfiguration);
        TokenRequest request = state.createTokenRefreshRequest();

        service = new AuthorizationService(mContext,appAuthConfiguration);
        service.performTokenRequest(request, new AuthorizationService.TokenResponseCallback() {
            @Override
            public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                if (exception != null) {
                    Log.w(TAG, "Token Exchange failed.", exception);
                    sendError(mContext.getString(R.string.Error_in_receive_new_token)+":"+exception.error);
                    return;
                } else {
                    if (tokenResponse != null) {
                        state.update(tokenResponse, exception);
                        //Save now state;
                        AuthenticacionUtils.writeAuthState(state, mContext);
                        Log.i(TAG, "Refreshing Token.");
                        if (state!=null &&
                                state.getAuthorizationServiceConfiguration()!=null &&
                                state.getAuthorizationServiceConfiguration().discoveryDoc!=null &&
                                state.getAuthorizationServiceConfiguration().discoveryDoc.getJwksUri() != null)
                            new DownloadFwkUriTaskRefresh().execute(state.getAuthorizationServiceConfiguration().discoveryDoc.getJwksUri());
                    } else {
                        Log.e(TAG, "Token response not processed.");
                        sendError(mContext.getString(R.string.Error_in_receive_new_token));
                    }
                }
            }
        });


        return true;
    }




    public void setOnAuthenticationListener(OnAuthenticationListener onAuthenticationListener){
        this.onAuthenticationListener=onAuthenticationListener;
    }

    private class DownloadFwkUriTaskRefresh extends AsyncTask<Uri, Integer, String> {

        @Override
        protected String doInBackground(Uri... urls) {
            URL url = null;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(urls[0].toString());
                if(url==null)return null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result=convertStreamToString(in);
                return result;
            }catch (IOException e) {
                Log.e(TAG,e.toString());
            }finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {

        }
        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                validationToken(result);
                Log.d(TAG,"data:"+result);
            }else{
                Log.e(TAG,"Error getting JSON.");
                sendError(mContext.getString(R.string.Error_in_downloader_the_keys_publics_for_validation));
            }
        }
        private String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    private void validationToken(String valid){
        Log.d(TAG,"validating: "+valid);
        try {
            final AuthState state= AuthenticacionUtils.readAuthState(mContext);
            if(state!=null && valid!=null && !valid.isEmpty()){
                JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                        .setSkipAllValidators()
                        .setDisableRequireSignature()
                        .setSkipSignatureVerification()
                        .build();

                try {
                    JwtContext jwtContext=jwtConsumer.process(state.getAccessToken());
                    List<JsonWebStructure> joseObjects = jwtContext.getJoseObjects();
                    if(joseObjects!=null && !joseObjects.isEmpty()){
                        JsonWebKey providerRSAJWK = AuthenticacionUtils.getProviderRSAJWK(valid,joseObjects.get(0).getKeyIdHeaderValue());
                        if(providerRSAJWK==null){
                            Log.e(TAG,"Token validation error.");
                            sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token));
                            return;
                        }else{
                            Log.d(TAG,"Public key is:"+providerRSAJWK.getKey());
                        }

                        JwtConsumer jwtConsumerOrigin = new JwtConsumerBuilder()
                                .setVerificationKey(providerRSAJWK.getKey())
                                .setRelaxVerificationKeyValidation().build();
                        JwtClaims claims =jwtConsumerOrigin.processToClaims(state.getAccessToken());
                        Map<String,Object> stringObjectMap= claims.getClaimsMap();
                        Object data=null;
                        CampsType dataNow=new CampsType();
                        data=stringObjectMap.get(CampsType.MCPTT_ID);
                        if(data instanceof String){
                            dataNow.setMcptt_id((String)data);
                        }
                        data=stringObjectMap.get(CampsType.AZP);
                        if(data instanceof String){
                            dataNow.setAzp((String)data);
                        }
                        data=stringObjectMap.get(CampsType.CLIENT_ID);
                        if(data instanceof String){
                            dataNow.setClient_id((String)data);
                        }
                        data=stringObjectMap.get(CampsType.EXP);
                        if(data instanceof String){
                            dataNow.setExp((String)data);
                        }
                        data=stringObjectMap.get(CampsType.IAT);
                        if(data instanceof String){
                            dataNow.setIat((String)data);
                        }
                        data=stringObjectMap.get(CampsType.ISS);
                        if(data instanceof String){
                            dataNow.setIss((String)data);
                        }
                        data=stringObjectMap.get(CampsType.JTI);
                        if(data instanceof String){
                            dataNow.setJti((String)data);
                        }
                        data=stringObjectMap.get(CampsType.SUB);
                        if(data instanceof String){
                            dataNow.setSub((String)data);
                        }
                        dataToken(dataNow);
                    }else{
                        Log.e(TAG,"Problems validating the new token");
                        sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token));

                    }
                } catch (InvalidJwtException e) {
                    Log.e(TAG,"Error validating the token:"+e.toString());
                    sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());

                }catch (ParseException e) {
                    Log.e(TAG,"Error parsing the token:"+e.toString());
                    sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());

                } catch (java.text.ParseException e) {
                    Log.e(TAG,"Error parsing the token:"+e.toString());
                    sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());

                }
            }
        } catch (JSONException e) {
            sendError(mContext.getString(R.string.There_are_problems_validating_the_new_token)+":"+e.getMessage());

            return ;
        }

        return;

    }

    private void dataToken(CampsType dataNow){
        if(dataNow!=null && mContext!=null){
            if(saveAuthCamp(dataNow) && onAuthenticationListener!=null){
                initTimerRefresh(mContext);
                onAuthenticationListener.onAuthenticationRefresh("All refresh Ok");
            }
        }
    }

    //Init Timers
    private void initTimerRefresh(Context context){
        try {
            AuthState authState= AuthenticacionUtils.readAuthState(context);
            if(authState!=null && authState.getLastTokenResponse()!=null && authState.getLastTokenResponse().accessTokenExpirationTime>0){
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(authState.getLastTokenResponse().accessTokenExpirationTime);
                long dif=authState.getLastTokenResponse().accessTokenExpirationTime-Calendar.getInstance().getTimeInMillis();
                if(dif>0){
                    Log.d(TAG,"Access token expiration time is: "+ AuthenticacionUtils.getDatas(context,calendar)+" Dif: "+(dif/1000));
                    initTimerRefresh(dif);
                }else{
                    Log.e(TAG,"Error in token access expiration time.");
                }

            }
        } catch (JSONException e) {
            return;
        }
    }

    private void initTimerRefresh(long mseg){
        if(handler==null){
            handler=new Handler();
        }else if(runnableTimerRefresh!=null){
            handler.removeCallbacks(runnableTimerRefresh);
        }
        runnableTimerRefresh=new Runnable() {
            @Override
            public void run() {
                refreshToken();
            }
        };
        handler.postDelayed(runnableTimerRefresh, mseg);

    }

    //End Timer
    @Override
    public boolean clearService(){
        return true;
    }


    //Init response

    public void getAuthenticationToken(Uri uri){
        AuthorizationRequest request=getAuthorizationRequest(mAuthorizationServiceConfiguration);
        try {
            request = AuthorizationRequest.jsonDeserialize(mAuthorizationRequest.jsonSerializeString());
            DownloadTokenTask downloadTokenTask=new DownloadTokenTask();
            DataDownloaderToken dataDownloaderToken=new DataDownloaderToken(uri,request);
            downloadTokenTask.execute(dataDownloaderToken);
        } catch (Exception e) {
            Log.e(TAG,getString(R.string.Error_in_authetication)+": "+e.getMessage());
        }
    }

    private void checkIntent(@Nullable final AuthorizationResponse response) {
        try {
            if (response != null) {
                final AuthState authState = new AuthState(response, null);
                Log.i(TAG, String.format("Handled Authorization Response %s ", authState.toString()));

                        AppAuthConfiguration appAuthConfiguration= AuthenticacionUtils.generateConfigureNet();
                        if(appAuthConfiguration!=null){
                            service = new AuthorizationService(mContext,appAuthConfiguration);
                            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {


                                @Override
                                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                                    if (exception != null) {
                                        Log.w(TAG, "Token Exchange failed", exception);
                                        Log.e(TAG,getString(R.string.Error_in_receive_new_token)+":"+exception.errorDescription +" URI: "+exception.errorUri);
                                        service=null;
                                        return;
                                    } else {
                                        if (tokenResponse != null && authState!=null) {
                                            authState.update(tokenResponse, exception);
                                            //Save now state;
                                            nowAuthState = authState;
                                            AuthenticacionUtils.writeAuthState(authState, mContext);
                                            Log.i(TAG, "Refresh Token:" +tokenResponse.accessToken);
                                            if (authState.getAuthorizationServiceConfiguration()!=null &&
                                                    authState.getAuthorizationServiceConfiguration().discoveryDoc!=null &&
                                                    authState.getAuthorizationServiceConfiguration().discoveryDoc.getJwksUri() != null){
                                                Log.d(TAG,"Init check token");
                                                new DownloadFwkUriTask().execute(authState.getAuthorizationServiceConfiguration().discoveryDoc.getJwksUri());
                                            }else {
                                                Log.i(TAG,"Validity of the token cannot be verified.");
                                                //Send date from token without verifying it
                                                try {
                                                    JwtConsumer jwtConsumer = new JwtConsumerBuilder ()
                                                            .setSkipAllValidators()
                                                            .setDisableRequireSignature()
                                                            .setSkipSignatureVerification()
                                                            .build();

                                                    JwtClaims claims =jwtConsumer.processToClaims(nowAuthState.getAccessToken());
                                                    Log.d(TAG,"The access token is "+nowAuthState.getIdToken()+" ");
                                                    CampsType campsType = getDataToken(claims);
                                                    processResponse(campsType);
                                                } catch (InvalidJwtException e) {
                                                    Log.e(TAG,"Error validating token: "+e.toString());
                                                    Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
                                                } catch (Exception e) {
                                                    Log.e(TAG,"Error validating token: "+e.toString());
                                                    Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
                                                }
                                            }
                                        } else {
                                            Log.e(TAG, "Token response not processed.");
                                            Log.e(TAG,getString(R.string.Error_in_receive_new_token));
                                        }
                                    }
                                }
                            });
                        }else{
                            Log.e(TAG,getString(R.string.Error_in_receive_new_token));
                        }






            }
        }catch (Exception e){
            Log.e(TAG,"Error: "+e.toString());
            Log.e(TAG,getString(R.string.Error_in_receive_new_token)+": "+e.getMessage());
        }



    }

    private CampsType getDataToken(JwtClaims claims) throws Exception{
        Map<String,Object> stringObjectMap= claims.getClaimsMap();
        Object data=null;
        CampsType dataNow=new CampsType();
        if(nowAuthState==null || nowAuthState.getAccessToken()==null || nowAuthState.getIdToken()==null || nowAuthState.getRefreshToken()==null ){
            Log.e(TAG,"Error in Authentication state.");
            if(nowAuthState!=null){
                if(nowAuthState.getAccessToken()!=null){
                    Log.w(TAG,"The access token is: "+nowAuthState.getAccessToken());
                }
                if(nowAuthState.getIdToken()!=null){
                    Log.w(TAG,"The token id is: "+nowAuthState.getIdToken());
                }
                if(nowAuthState.getRefreshToken()!=null){
                    Log.w(TAG,"The token refresh is: "+nowAuthState.getRefreshToken());
                }
            }

        }
        dataNow.setAccessToken(nowAuthState.getAccessToken());
        dataNow.setIdToken(nowAuthState.getIdToken());
        dataNow.setRefreshToken(nowAuthState.getRefreshToken());
        data=stringObjectMap.get(CampsType.MCPTT_ID);
        if(data instanceof String){
            Log.d(TAG,"MCPTT_ID: "+data);
            dataNow.setMcptt_id((String)data);
        }else{
            Log.e(TAG,"MCPTT ID error");
        }
        data=stringObjectMap.get(CampsType.AZP);
        if(data instanceof String){
            dataNow.setAzp((String)data);
        }else{
            Log.e(TAG,"MCPTT AZP");
        }
        data=stringObjectMap.get(CampsType.CLIENT_ID);

        if(data instanceof String){
            Log.d(TAG,"CLIENT_ID: "+data);
            dataNow.setClient_id((String)data);
        }else{
            Log.e(TAG,"CLIENT ID error");
        }
        data=stringObjectMap.get(CampsType.EXP);
        if(data instanceof String){
            dataNow.setExp((String)data);
        }else{
            Log.e(TAG,"EXP error");
        }
        data=stringObjectMap.get(CampsType.IAT);
        if(data instanceof String){
            dataNow.setIat((String)data);
        }else{
            Log.e(TAG,"IAT error");
        }
        data=stringObjectMap.get(CampsType.ISS);
        if(data instanceof String){
            dataNow.setIss((String)data);
        }else{
            Log.e(TAG,"ISS error");
        }
        data=stringObjectMap.get(CampsType.JTI);
        if(data instanceof String){
            dataNow.setJti((String)data);
        }else{
            Log.e(TAG,"JTI error");
        }
        data=stringObjectMap.get(CampsType.SUB);
        if(data instanceof String){
            dataNow.setSub((String)data);
        }else{
            Log.e(TAG,"SUB error");
        }

        return dataNow;
    }

    private boolean saveAuthCamp(CampsType campsType){
        NgnSipPrefrences profileNow=null;
        if(campsType!=null && campsType.getMcptt_id()!=null &&
                mContext!=null &&
                (profileNow=NgnEngine.getInstance().getProfilesService().getProfileNow(mContext))!=null){
            if(BuildConfig.DEBUG)Log.d(TAG,"saveAuthCamp with: "+campsType.getMcptt_id());
            profileNow.setMcpttId(campsType.getMcptt_id());
        }
        return AuthenticacionUtils.writeAuthCamps(campsType,mContext);
    }



    private void processResponse(CampsType campsType){
        //Save data Camps:
        if(campsType!=null && mContext!=null){
            if(saveAuthCamp(campsType) && onAuthenticationListener!=null){
                Log.d(TAG,"Current MCPTT ID is "+campsType.getMcptt_id());
                if(campsType.getMcptt_id()==null){
                    Log.e(TAG,mContext.getString(R.string.Error_Authentication_token));
                    sendError(mContext.getString(R.string.Error_Authentication_token));
                    return;
                }
                campsTypeCurrent=campsType;
                initTimerRefresh(mContext);
                if(onAuthenticationListener!=null)
                onAuthenticationListener.onAuthenticationOk("Everything OK.");
            }
        }
    }

    private boolean sendError(String errorString){
        if(errorString==null || errorString.trim().isEmpty())return false;
        if(onAuthenticationListener!=null){
            onAuthenticationListener.onAuthenticationError(errorString);
            return true;
        }
        return false;
    }

    private class DataDownloaderToken implements Serializable{
        private Uri requestUri;
        private AuthorizationRequest authorizationRequest;

        public DataDownloaderToken(Uri requestUri, AuthorizationRequest authorizationRequest) {
            this.requestUri = requestUri;
            this.authorizationRequest = authorizationRequest;
        }

        public Uri getRequestUri() {
            return requestUri;
        }

        public void setRequestUri(Uri requestUri) {
            this.requestUri = requestUri;
        }

        public AuthorizationRequest getAuthorizationRequest() {
            return authorizationRequest;
        }

        public void setAuthorizationRequest(AuthorizationRequest authorizationRequest) {
            this.authorizationRequest = authorizationRequest;
        }
    }

    private class DownloadTokenTask extends AsyncTask<DataDownloaderToken, Integer, AuthorizationResponse>  {

        @Override
        protected AuthorizationResponse doInBackground(DataDownloaderToken... data) {
            try{
                if(data.length>0 && data[0]!=null){
                    AuthorizationResponse response =new AuthorizationResponse.Builder(data[0].getAuthorizationRequest()).setAuthorizationCode(data[0].getRequestUri().getQueryParameters("code").get(0)).setState(data[0].getRequestUri().getQueryParameters("state").get(0)).build();
                    return response;
                }
            }catch (Exception e){
                Log.e(TAG,"Error in get Token: "+e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(AuthorizationResponse result) {
            checkIntent(result);
        }
    }



    private class DownloadFwkUriTask extends AsyncTask<Uri, Integer, String> {

        @Override
        protected String doInBackground(Uri... urls) {

            URL url = null;
            HttpURLConnection urlConnection=null;
            try {
                url = new URL(urls[0].toString());
                Log.d(TAG,"Init Downloader FWR: "+url.toString());
                if(url==null)return null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result=convertStreamToString(in);
                return result;
            }catch (IOException e) {
                Log.e(TAG,"Error in downloader FWR: "+e.toString());
            }finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {

        }
        @Override
        protected void onPostExecute(String result) {
            if(result!=null){
                //JsonWebKey jsonWebKey = OpenIdUtils.getProviderRSAJWK(result,);
                validationToken2(result);
                Log.d(TAG,"datas:"+result);
            }else{
                Log.e(TAG,"Error getting JSON.");
                Log.e(TAG,getString(R.string.Error_in_downloader_the_keys_publics_for_validation));
            }
        }
        private String convertStreamToString(InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }


    }


    private void validationToken2(String valid){
        if(nowAuthState!=null && nowAuthState.getAccessToken()!=null && valid!=null && !valid.isEmpty()){
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                    .setSkipAllValidators()
                    .setDisableRequireSignature()
                    .setSkipSignatureVerification()
                    .build();
            try {
                JwtContext jwtContext=jwtConsumer.process(nowAuthState.getAccessToken());
                List<JsonWebStructure> joseObjects = jwtContext.getJoseObjects();
                if(joseObjects!=null && !joseObjects.isEmpty()){
                    JsonWebKey providerRSAJWK = AuthenticacionUtils.getProviderRSAJWK(valid,joseObjects.get(0).getKeyIdHeaderValue());
                    if(providerRSAJWK==null){
                        Log.e(TAG,"Token validation problem.");
                        Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token));
                        return;
                    }else{
                        Log.d(TAG,"The public key is:"+providerRSAJWK.getKey());
                    }

                    JwtConsumer jwtConsumerOrigin = new JwtConsumerBuilder()
                            .setVerificationKey(providerRSAJWK.getKey())
                            .setRelaxVerificationKeyValidation().build();
                    JwtClaims claims =jwtConsumerOrigin.processToClaims(nowAuthState.getAccessToken());
                    Log.d(TAG,"Access token is "+nowAuthState.getAccessToken());
                    getDataToken(claims);
                }else{
                    Log.e(TAG,"There are problems validating the new token.");
                    Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token));
                }





            } catch (InvalidJwtException e) {
                Log.e(TAG,"Error validating the token:"+e.toString());
                Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
            }catch (ParseException e) {
                Log.e(TAG,"Error parsing the token:"+e.toString());
                Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
            } catch (java.text.ParseException e) {
                Log.e(TAG,"Error parsing the token:"+e.toString());
                Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
            } catch (Exception e) {
                Log.e(TAG,"Error parsing the token:"+e.toString());
                Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token)+": "+e.getMessage());
            }
        }else{
            Log.e(TAG,getString(R.string.There_are_problems_validating_the_new_token));
        }
        return;

    }
    //End Response

    private String getString(int id){
        if(mContext!=null)return mContext.getString(id);
        return null;
    }
}
