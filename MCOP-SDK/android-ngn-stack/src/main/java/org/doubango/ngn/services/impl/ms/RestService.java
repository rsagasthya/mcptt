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

import android.support.annotation.Nullable;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


class RestService {

    private final static String TAG = Utils.getTAG(RestService.class.getCanonicalName());

    private static final String DEFAULT_ADDRESS_CMS="http://test.servicecms.org/xcap-root";
    protected static final String PATH_MCPTT_EU_INIT_CONFIG="org.3gpp.mcptt.ue-init-config";
    protected static final String PATH_MCPTT_EU_CONFIG="org.3gpp.mcptt.ue-config";
    protected static final String PATH_MCPTT_USER_PROFILE="org.3gpp.mcptt.user-profile";
    protected static final String PATH_MCPTT_SERVICE_CONFIG="org.3gpp.mcptt.service-config";



    private static final String PATH_USERS="users";
    private static final String PATH_GLOBAL="global";
    private static final String CONTENT_TYPE="Contact-Type";
    private static final String PREFIX_MCPTT_UE_ID="urn:uuid:";
    private static final String PARAMETER_ETAG="Etag";
    private static final String PARAMETER_IF_NONE_MATCH="If-None-Match";

    private static String addressCMS=DEFAULT_ADDRESS_CMS;
    private static RestService mRestService;
    private OnRestServiceListener onRestServiceListener;

    protected enum ContentTypeData{
        CONTENT_TYPE_NONE("none"),
        CONTENT_TYPE_MCPTT_EU_INIT_CONFIG("application/vnd.3gpp.mcptt-ue-init-config+xml"),
        CONTENT_TYPE_MCPTT_EU_CONFIG("application/vnd.3gpp.mcptt-ue-config+xml"),
        CONTENT_TYPE_MCPTT_USER_PROFILE("application/vnd.3gpp.mcptt-user-profile+xml"),
        CONTENT_TYPE_MCPTT_SERVICE_CONFIG("application/vnd.3gpp.mcptt-service-config+xml"),
        CONTENT_TYPE_MCPTT_GROUPS("application/vnd.oma.poc.groups+xml");


        private String text;
        ContentTypeData(String text) {
            this.text = text;
        }
        protected String getText() {
            return this.text;
        }
        protected static ContentTypeData fromString(String text) {
            for (ContentTypeData b : ContentTypeData.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    protected boolean DownloaderLastVersionXML(final String pathServer,final String path,String contactType,String etag){
        return DownloaderLastVersionXML(pathServer,path,contactType,etag,null);
    }


    protected boolean DownloaderLastVersionXML(final String pathServer,final String path,String contactType,String etag,String token){
        if(path==null || path.isEmpty() || contactType==null || contactType.isEmpty()){
            Log.e(TAG,"Error in HTTP parameter.");
            return false;
        }
        //Log.d(TAG,"Path server:"+pathServer+"   PATH for rest http: "+path);
        RestClient restClient=new RestClient();
        if(token!=null && !token.isEmpty());
            restClient.setHttpTokenAuth(token);
        restClient.setPathServer(pathServer);
        Map<String,String> parameters=new HashMap<>();
        if(etag!=null && !etag.isEmpty()) parameters.put(PARAMETER_IF_NONE_MATCH,etag);
        parameters.put(CONTENT_TYPE,contactType);
        PathData pathData=new PathData(path,parameters);
        PathData[] pathDatas=new PathData[1];
        pathDatas[0]=pathData;
        restClient.setOnGetHTTPListener(new RestClient.OnGetHTTPListener() {
            @Override
            public void onGetHTTPResponse(Map<String, PathData> results) {
                ContentTypeData contentTypeData=ContentTypeData.CONTENT_TYPE_NONE;
                String etag=null, resultString;

                if(results.get(path)!=null && results.get(path).getCodeResult()!=-1 && ((resultString=results.get(path).getResponseBody())!=null || results.get(path).getCodeResult()==HttpsURLConnection.HTTP_NOT_MODIFIED) ){
                    for(Map.Entry<String,List<String>> property:results.get(path).getResponseParameters().entrySet()) {
                        if (property.getKey() != null && property.getKey().compareToIgnoreCase(PARAMETER_ETAG) == 0) {
                            String resultparameteretag = null;
                            for (String value : property.getValue()) {
                                if (value != null) resultparameteretag = value;
                            }
                            etag = resultparameteretag;
                        }
                    }
                    contentTypeData=getContentTypeData(results.get(path));
                    if(onRestServiceListener!=null)onRestServiceListener.onDownloaderXML(resultString,etag,path,results.get(path).getCodeResult(),contentTypeData);
                }else{
                    if(results!=null && results.get(path)!=null)contentTypeData=getContentTypeData(results.get(path));
                    if(onRestServiceListener!=null)onRestServiceListener.errorOnDownloaderXML("it can´t downloader now",contentTypeData);
                }
            }

            @Override
            public void errorGetHTTPResponse(String error) {
                if(onRestServiceListener!=null)onRestServiceListener.errorOnDownloaderXML(error,ContentTypeData.CONTENT_TYPE_NONE);
            }
        });
        if(BuildConfig.DEBUG)Log.d(TAG,"Path for Rest -> "+pathServer);
        final boolean result=restClient.getHTTP(pathDatas);
        return result;
    }

    private ContentTypeData getContentTypeData(PathData pathData){
        ContentTypeData contentTypeData=ContentTypeData.CONTENT_TYPE_NONE;
        String contentType=null;
        if(pathData!=null && (pathData.getParameters()!=null) &&
                (contentType=pathData.getParameters().get(CONTENT_TYPE))!=null &&
                !contentType.isEmpty()){
            try {
                contentTypeData=ContentTypeData.fromString(contentType);
            }catch (Exception e){
                            Log.e(TAG,"Content-type error.");
            }
        }
        return contentTypeData;
    }



    protected static String getMCPTTUEConfigSub(String uuidUE){
        return PATH_MCPTT_EU_CONFIG+"/"+"users"+"/"+uuidUE+"/";
    }

    protected static String getMCPTTUEConfig(String uuidUE){
        return PATH_MCPTT_EU_CONFIG+"/"+"users"+"/"+uuidUE+"/"+uuidUE;
    }

    protected static String getMCPTTUEInitConfigSub(String uuidUE){
        return PATH_MCPTT_EU_INIT_CONFIG+"/"+"users"+"/"+uuidUE+"/";
    }


    protected static String getMCPTTUEInitConfig(String uuidUE){
        return PATH_MCPTT_EU_INIT_CONFIG+"/"+"users"+"/"+uuidUE+"/"+uuidUE;
    }

    protected static String getMCPTTUserProfiles(String mcpttIdFile){
        return PATH_MCPTT_USER_PROFILE+"/"+"users"+"/"+mcpttIdFile;
    }

    protected static String getMCPTTServiceConfig(String serviceConfFile){
        return PATH_MCPTT_SERVICE_CONFIG+"/"+"global"+"/"+serviceConfFile;
    }

    protected static String getMCPTTGmsGroups(String serviceConfFile){
        return PATH_MCPTT_SERVICE_CONFIG+"/"+"global"+"/"+serviceConfFile;
    }

    protected boolean DownloaderMCPTTUEInitConfig(final String pathServer,final String uuidUE,String etag){
        if(BuildConfig.DEBUG)Log.d(TAG ,"DownloaderMCPTTUEInitConfig");
        return DownloaderLastVersionXML(pathServer,getMCPTTUEInitConfig(uuidUE),ContentTypeData.CONTENT_TYPE_MCPTT_EU_INIT_CONFIG.getText(),etag);
    }

    protected boolean DownloaderMCPTTUserProfiles(final String pathServer,@Nullable final String allSel,final String mcpttIdFile,String etag,String token){
        if(BuildConfig.DEBUG)Log.d(TAG ,"DownloaderMCPTTUserProfiles");
        return DownloaderLastVersionXML(pathServer,(allSel!=null && !allSel.isEmpty())?allSel:getMCPTTUserProfiles(mcpttIdFile),ContentTypeData.CONTENT_TYPE_MCPTT_USER_PROFILE.getText(),etag,token);
    }
    protected boolean DownloaderMCPTTUEConfig(final String pathServer,@Nullable final String allSel, @Nullable final String uuidUE, String etag, String token){
        if(BuildConfig.DEBUG)Log.d(TAG ,"DownloaderMCPTTUEConfig");
        return DownloaderLastVersionXML(pathServer,(allSel!=null && !allSel.isEmpty())?allSel:getMCPTTUEConfig(uuidUE),ContentTypeData.CONTENT_TYPE_MCPTT_EU_CONFIG.getText(),etag,token);
    }

    protected boolean DownloaderMCPTTServiceConfig(final String pathServer,@Nullable final String allSel,final String serviceConfFile,String etag,String token){
        if(BuildConfig.DEBUG)Log.d(TAG ,"DownloaderMCPTTServiceConfig");
        return DownloaderLastVersionXML(pathServer,(allSel!=null && !allSel.isEmpty())?allSel:getMCPTTServiceConfig(serviceConfFile),ContentTypeData.CONTENT_TYPE_MCPTT_SERVICE_CONFIG.getText(),etag,token);
    }

    protected boolean DownloaderMCPTTGmsGroups(final String pathServer,final String groupsGMS,String etag,String token){
        if(BuildConfig.DEBUG)Log.d(TAG ,"DownloaderMCPTTGmsGroups");
        return DownloaderLastVersionXML(pathServer,groupsGMS,ContentTypeData.CONTENT_TYPE_MCPTT_GROUPS.getText(),etag,token);
    }


    public static RestService getInstance(){
            mRestService = new RestService();
        return mRestService;
    }


    protected void setOnRestServiceListener(OnRestServiceListener onRestServiceListener){
        this.onRestServiceListener=onRestServiceListener;

    }

    protected interface OnRestServiceListener{
        void onDownloaderXML(String results,String etag,String path,int codeRespone,ContentTypeData contentTypeData);
        void errorOnDownloaderXML(String error,ContentTypeData contentTypeData);
    }

}
