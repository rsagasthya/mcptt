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


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.browser.BrowserWhitelist;
import net.openid.appauth.browser.VersionedBrowserMatcher;
import net.openid.appauth.connectivity.ConnectionBuilder;

import org.doubango.ngn.datatype.mcpttinfo.ContentType;
import org.doubango.ngn.datatype.mcpttinfo.McpttParamsType;
import org.doubango.ngn.datatype.mcpttinfo.McpttinfoType;
import org.doubango.ngn.datatype.mcpttinfo.ProtectionType;
import org.doubango.ngn.datatype.openId.CampsType;
import org.doubango.ngn.datatype.pocsettings.AmSettingType;
import org.doubango.ngn.datatype.pocsettings.EntityType;
import org.doubango.ngn.datatype.pocsettings.PocSettingsType;
import org.doubango.ngn.datatype.pocsettings.SelectedUserProfileIndex;
import org.doubango.utils.Utils;
import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.JSONValue;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.lang.JoseException;
import org.json.JSONException;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class AuthenticacionUtils {
    private final static String TAG = Utils.getTAG(AuthenticacionUtils.class.getCanonicalName());
    private final static String AUTHENTICATION_PREFERENCE="AUTHENTICATION_PREFERENCE";
    private final static String AUTHENTICATION_PREFERENCE_AUTH_STATE="AUTHENTICATION_PREFERENCE_AUTH_STATE";
    private final static String AUTHENTICATION_PREFERENCE_AUTH_CAMPS="AUTHENTICATION_PREFERENCE_AUTH_CAMPS";
    private final static String SCOPES_DEFUALT_OLD="openid 3gpp:mcptt:ptt_server 3gpp:mcptt:key_management_server 3gpp:mcptt:config_management_server 3gpp:mcptt:group_management_server";
    private final static String SCOPES_DEFUALT="3gpp:mc:data_group_management_service 3gpp:mc:video_service 3gpp:mc:data_config_management_service openid 3gpp:mc:ptt_service 3gpp:mc:video_key_management_service 3gpp:mc:data_service 3gpp:mc:video_config_management_service 3gpp:mc:ptt_group_management_service 3gpp:mc:video_group_management_service 3gpp:mc:ptt_key_management_service 3gpp:mc:data_key_management_service 3gpp:mc:ptt_config_management_service";
    private static AuthState authStateNow;
    private static CampsType campsTypesNow;



    //INIT SAVE DATA AUTHENTICATION
    @NonNull
    public static AuthState readAuthState(Context context) throws JSONException {
        if(authStateNow==null){
            SharedPreferences authPrefs = context.getSharedPreferences(AUTHENTICATION_PREFERENCE,context.MODE_PRIVATE);
            String stateJson = authPrefs.getString(AUTHENTICATION_PREFERENCE_AUTH_STATE,null);
            if (stateJson != null){
                return AuthState.jsonDeserialize(stateJson);
            }
            else {
                return new AuthState();
            }
        }
        return authStateNow;

    }

    public static void writeAuthState( AuthState state,@NonNull Context context) {

        if(context!=null){
            String dataWrite=null;
            if(state!=null){
                authStateNow=state;
                dataWrite=authStateNow.jsonSerializeString();
            }else{
                authStateNow=state;
            }
            SharedPreferences authPrefs = context.getSharedPreferences(AUTHENTICATION_PREFERENCE, context.MODE_PRIVATE);
            authPrefs.edit()
                    .putString(AUTHENTICATION_PREFERENCE_AUTH_STATE,dataWrite )
                    .apply();
        }


    }


    protected static CampsType readAuthCamps(Context context) throws JSONException {
        if(campsTypesNow!=null){
            return campsTypesNow;
        }
        SharedPreferences authPrefs = context.getSharedPreferences(AUTHENTICATION_PREFERENCE,context.MODE_PRIVATE);
        String campsString = authPrefs.getString(AUTHENTICATION_PREFERENCE_AUTH_CAMPS,null);
        if (campsString != null){
            try {
                return CampsType.jsonDeserialize(campsString);
            } catch (IOException e) {
                return new CampsType();
            }
        }
        else {
            return new CampsType();
        }
    }

    protected static boolean writeAuthCamps(@NonNull CampsType campsType, Context context) {
        if(context!=null){
            campsTypesNow=campsType;
            SharedPreferences authPrefs = context.getSharedPreferences(AUTHENTICATION_PREFERENCE, context.MODE_PRIVATE);
            SharedPreferences.Editor editor = authPrefs.edit();
            try {


                if(campsType==null){
                    editor.remove(AUTHENTICATION_PREFERENCE_AUTH_CAMPS);
                    editor.apply();
                    return true;
                }else {
                    String data=null;
                    data=campsType.jsonSerializeString();
                    editor.putString(AUTHENTICATION_PREFERENCE_AUTH_CAMPS, data).apply();
                    return editor.commit();
                }
            } catch (IOException e) {
                Log.e(TAG,"Error in write Camps datas:"+e.getMessage());
                return false;
            } catch (JSONException e) {
                Log.e(TAG,"Error in write Camps datas 2");
                return false;
            }

        }
        return false;
    }


    //END SAVE DATA AUTHENTICATION
    public static JsonWebKey getProviderRSAJWK(String is, String keyID) throws java.text.ParseException {
        // Read all data from stream

        //StringBuilder sb = new StringBuilder();
        //try (Scanner scanner = new Scanner(is);) {
        //    while (scanner.hasNext()) {
        //        sb.append(scanner.next());
        //    }
        //}


        // Parse data as json
        //String jsonString = sb.toString();
        JSONObject json = null;
        json = (JSONObject) JSONValue.parse(is);

        // Find the RSA signing key
        JSONArray keyList = null;
        if(json==null){
            Log.e(TAG,"Error processing JSONObject");
            return null;
        }

        keyList = (JSONArray) json.get("keys");

        for (Object key : keyList) {
            JSONObject k = (JSONObject) key;

            if (k.get("kid").equals(keyID) && k.get("kty").equals("RSA")) {
                try {
                    JsonWebKey jwk = JsonWebKey.Factory.newJwk(k);
                    return jwk;
                } catch (JoseException e) {
                    e.printStackTrace();
                }

            }

        }


        return null;
    }


     //Generates network configuration in authentication
     //@return network configuration

    public static AppAuthConfiguration generateConfigureNet(){
        AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()
                .setBrowserMatcher(new BrowserWhitelist(
                        VersionedBrowserMatcher.CHROME_CUSTOM_TAB,
                        VersionedBrowserMatcher.FIREFOX_BROWSER,
                        VersionedBrowserMatcher.SAMSUNG_BROWSER,
                        VersionedBrowserMatcher.SAMSUNG_CUSTOM_TAB))
                .setConnectionBuilder(new ConnectionBuilder() {
                    @NonNull
                    @Override
                    public HttpURLConnection openConnection(@NonNull Uri uri) throws IOException {
                        URL url = new URL(uri.toString());
                        HttpURLConnection connection =(HttpURLConnection) url.openConnection();


                        //if (connection instanceof HttpsURLConnection) {
                        //    HttpsURLConnection connection = (HttpsURLConnection) connection;
                        //    connection.setSSLSocketFactory(MySocketFactory.getInstance());
                        //}
                        return connection;
                    }
                })
                .build();

        return appAuthConfig;
    }
    //INIT utils xml
        //INIT MCPTT-INFO

    protected static McpttinfoType getMcpttinfoType(String string) throws Exception {
        return getMcpttinfoType(string.getBytes());
    }

    private static McpttinfoType getMcpttinfoType(byte[] bytes) throws Exception {
        return getMcpttinfoType(new ByteArrayInputStream(bytes));
    }
    private static McpttinfoType getMcpttinfoType(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(McpttinfoType.class,stream);
    }


    private static InputStream getOutputStreamOfMcpttinfoType(Context context, McpttinfoType mcpttinfoType) throws Exception {
        if(mcpttinfoType==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mcpttinfoType,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMcpttinfoType(Context context,McpttinfoType mcpttinfoType) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttinfoType(context,mcpttinfoType);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    private  static String  getStringOfMcpttinfoType(Context context,McpttinfoType mcpttinfoType) throws Exception {
        return new String(getBytesOfMcpttinfoType(context,mcpttinfoType)).trim();
    }

    private static McpttinfoType generateMcpttinfoType(CampsType campsTypeCurrent,String mcpttId,String mcpttClientIdString){
        McpttinfoType mcpttinfoType=new McpttinfoType();
        McpttParamsType mcpttParamsType=new McpttParamsType();
        mcpttinfoType.setMcpttParams(mcpttParamsType);
        if(mcpttClientIdString!=null &&
                !mcpttClientIdString.trim().isEmpty()
                ){
            ContentType mcpttClientId=new ContentType();
            mcpttClientId.setType(ProtectionType.Normal);
            mcpttParamsType.setMcpttClientId(mcpttClientId);
            mcpttClientId.setMcpttString(mcpttClientIdString.trim());
        }

        if(campsTypeCurrent!=null &&
                campsTypeCurrent.getAccessToken()!=null &&
                !campsTypeCurrent.getAccessToken().trim().isEmpty()){
            ContentType accessToken=new ContentType();
            accessToken.setType(ProtectionType.Normal);
            mcpttParamsType.setMcpttAccessToken(accessToken);
            accessToken.setMcpttString(campsTypeCurrent.getAccessToken().trim());
        }
        if(mcpttId!=null &&
                !mcpttId.trim().isEmpty()
                ){
            ContentType mcpttIdContentType=new ContentType();
            mcpttParamsType.setMcpttRequestUri(mcpttIdContentType);
            mcpttIdContentType.setType(ProtectionType.Normal);
            mcpttIdContentType.setMcpttURI(mcpttId);
        }

        return  mcpttinfoType;

    }

    protected static String generateMcpttinfoType(Context context,CampsType campsTypeCurrent,String mcpttIdString,String mcpttClientIdString){
        McpttinfoType mcpttinfoType=generateMcpttinfoType(campsTypeCurrent,mcpttIdString,mcpttClientIdString);
        if(mcpttinfoType!=null){
            try {
                return getStringOfMcpttinfoType(context,mcpttinfoType);
            } catch (Exception e) {
                Log.e(TAG,"Error generating Mcptt-info.");
            }
        }
        return null;
    }




    /**
     * In this function, if the campsTypeCurrentToGenerate is null, the device will generate mcpttinfo with token fail.
     * @param context
     * @param campsTypeCurrentToGenerate
     * @param mcpttClientIdString
     * @return
     */
    protected static String generateMcpttinfoType(@NonNull Context context,CampsType campsTypeCurrentToGenerate,@NonNull String mcpttId,@NonNull String mcpttClientIdString, boolean sendTokenFail){
        if((campsTypeCurrentToGenerate==null ||
                campsTypeCurrentToGenerate.getAccessToken()==null ||
                campsTypeCurrentToGenerate.getAccessToken().isEmpty())
                && sendTokenFail){
            if(mcpttId!=null){
                String data = String.format("{\"mcptt_id\":\"%s\",\"sub\":\"mcptt-client-A\",\"azp\":\"mcptt_client\",\"scope\":[\"3gpp:mcptt:ptt_server\",\"openid\",\"3gpp:mcptt:config_management_server\",\"3gpp:mcptt:key_management_server\",\"3gpp:mcptt:group_management_server\"],\"iss\":\"http:\\/\\/idms.organization.org:8080\\/openid-connect-server-webapp\\/\",\"exp\":1492767428,\"iat\":1492763828,\"jti\":\"b099481f-199b-41f9-8396-48eed364b288\",\"client_id\":\"mcptt_client\"}",
                        mcpttId);
                byte[]   bytesEncoded = Base64.encodeBase64(data .getBytes());
                String stringEncoded = new String(bytesEncoded);
                if(!stringEncoded.isEmpty()){
                    CampsType campsType=new CampsType();
                    String accessTokenFail=String.format("eyJraWQiOiJyc2ExIiwiYWxnIjoiUlMyNTYifQ.%s.Q3RO7otkthtACrL9tya9-CRn9rtQBoH2XC4lZJCoTau4SPQ2gTllT2qJRSg0ciNgNgj1zq_cmnZZ1mM7E3HME_4gM0ATyHPZg5hv0gIquKvZUDs6sDZDDcmHwZTg6koZYv-XaQxtQCZwmyZ8OJXuQELaYAJ2rBaB0EnubrTmZdHKnvnWzpIjz1skI8AOnfBM8ixisBKpUaTi3TLETLmGJDY_k6YdPo5z18kZ_2SppJXPLOcxr7Z4r2VmsD3ZYSq_cnZwPcC_IhoLObfrW_N-Mki-lqRd5nw4TyURMLbuZRaRVnC7aUQBempb31OztjEm6y_UPtyxb_qQ9p8cTO_E6A",
                            stringEncoded);
                    if(accessTokenFail!=null && !accessTokenFail.isEmpty()){
                        Log.d(TAG,"Send access token Fail");
                        campsType.setAccessToken(accessTokenFail);
                        return  generateMcpttinfoType(context,campsType,mcpttId,mcpttClientIdString);
                    }
                }
            }

        }

        return generateMcpttinfoType(context,campsTypeCurrentToGenerate,mcpttId,mcpttClientIdString);
    }
        //END MCPTT-INFO

        //INIT POC-SETTINGS
    private static PocSettingsType generatePocSettingsType(boolean isManual,int indexUserProfile){
        PocSettingsType pocSettingsType=new PocSettingsType();
        ArrayList<EntityType> entityTypes=new ArrayList<>();
        pocSettingsType.setEntity(entityTypes);
        EntityType entity=new EntityType();
        entityTypes.add(entity);
        AmSettingType answerMode=new AmSettingType();
        entity.setAmSettings(answerMode);
        entity.setId(randomStringUUID());
        if(isManual){
            answerMode.setAnswerMode("manual");
        }else{
            answerMode.setAnswerMode("automatic");
        }
        if(indexUserProfile>=0){
            SelectedUserProfileIndex selectedUserProfileIndex=new SelectedUserProfileIndex();
            selectedUserProfileIndex.setUserProfileIndex(indexUserProfile);
            entity.setSelectedUserProfileIndex(new SelectedUserProfileIndex());
        }
        return pocSettingsType;
    }

    protected static String generatePocSettingsType(@NonNull Context context,boolean isManual,int indexUserProfile){
        PocSettingsType pocSettingsType=generatePocSettingsType(isManual,indexUserProfile);
        if(pocSettingsType!=null){
            try {
                return getStringOfPocSettingsType(context,pocSettingsType);
            } catch (Exception e) {
                Log.e(TAG,"Invalid parameters to generate poc-settings: "+e.getMessage());
            }
        }
        return null;
    }

    protected static String generatePocSettingsType(@NonNull Context context,boolean isManual){
        PocSettingsType pocSettingsType=generatePocSettingsType(isManual,-1);
        if(pocSettingsType!=null){
            try {
                return getStringOfPocSettingsType(context,pocSettingsType);
            } catch (Exception e) {
                Log.e(TAG,"Invalid parameters to generate poc-settings: "+e.getMessage());
            }
        }
        return null;
    }

    protected static PocSettingsType getPocSettingsType(String string) throws Exception {
        return getPocSettingsType(string.getBytes());
    }

    protected static PocSettingsType getPocSettingsType(byte[] bytes) throws Exception {
        return getPocSettingsType(new ByteArrayInputStream(bytes));
    }
    protected static PocSettingsType getPocSettingsType(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(PocSettingsType.class,stream);
    }


    protected static InputStream getOutputStreamOfPocSettingsType(Context context, PocSettingsType pocSettingsType) throws Exception {
        if(pocSettingsType==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(pocSettingsType,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);
        return inputStream;
    }

    protected static byte[] getBytesOfPocSettingsType(Context context,PocSettingsType pocSettingsType) throws Exception {
        InputStream inputStream=getOutputStreamOfPocSettingsType(context,pocSettingsType);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected  static String  getStringOfPocSettingsType(Context context,PocSettingsType pocSettingsType) throws Exception {
        return new String(getBytesOfPocSettingsType(context,pocSettingsType)).trim();
    }
        //END POC-SETTINGS

    private static byte[] readBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();


        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];


        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }


        return byteBuffer.toByteArray();
    }
    //END utils xml

    private static String randomStringUUID() {
        //
        // Creating a random UUID (Universally unique identifier).
        //
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }


    protected static String getDatas(Context context,Calendar calendar){
        if(context==null)return "";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date=format1.format(calendar.getTime());
        return date;
    }
    protected static AuthorizationRequest getAuthorizationRequest(@NonNull AuthorizationServiceConfiguration serviceConfiguration, @NonNull AuthorizationServiceConfiguration authorizationServiceConfiguration, @NonNull String client_id, @NonNull Uri redirectUri){
        return  getAuthorizationRequest(  serviceConfiguration,   authorizationServiceConfiguration,   client_id,   redirectUri, false);

        }


        protected static AuthorizationRequest getAuthorizationRequest(@NonNull AuthorizationServiceConfiguration serviceConfiguration, @NonNull AuthorizationServiceConfiguration authorizationServiceConfiguration, @NonNull String client_id, @NonNull Uri redirectUri,boolean useClientSecret){

        Map<String, String> additionalParameters=new HashMap<>();
        if(useClientSecret){
            additionalParameters.put("acr_values","3gpp:acr:password");
        }

        AuthorizationRequest req = new AuthorizationRequest.Builder(
                authorizationServiceConfiguration,
                client_id,
                ResponseTypeValues.CODE,
                redirectUri).setScope(SCOPES_DEFUALT)
                //.setAdditionalParameters(additionalParameters)
                .build();
        return req;
    }


}
