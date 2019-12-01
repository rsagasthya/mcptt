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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.utils.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


class RestClient {
    private static String TAG =  Utils.getTAG(RestClient.class.getCanonicalName());

    private final Map<String,String> properties=new HashMap<>();
    public static final String PARAMETER_REQUEST="PARAMETER_REQUEST."+TAG;



    private final static String AUTH="Authorization";


    private String pathServer="http://test.com/cms";

    private String pathApplication;
    private OnGetHTTPListener onGetHTTPListener;


    protected RestClient(String pathServer,String pathApplication){
        setPathServer(pathServer);
        this.pathApplication=pathApplication;
    }
    protected RestClient(String pathApplication){
        this.pathApplication=pathApplication;
    }
    protected RestClient(){
    }

    private HttpURLConnection getConnection(String path) throws IOException {
        if(getPathServer()==null || path==null)return null;
        URL url=new URL(String.format("%s/%s",getPathServer(),path));
        HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        for(Map.Entry<String,String> property:properties.entrySet()){
            conn.setRequestProperty(property.getKey(),property.getValue());
        }
        conn.setUseCaches(false);
        return conn;
    }

    protected int setHttpBasicAuth(String user, String passwd){
        if(user == null || passwd==null || user.trim().equals(new String("")) )return -1;
        String basicAuth= Base64.encodeToString(String.format("%s:%s",user.trim(),passwd.trim()).getBytes(),Base64.DEFAULT);
        properties.put(AUTH, String.format("Basic %s", basicAuth));
        return 1;
    }

    protected int setHttpTokenAuth(String token){
        if(token == null || token.trim().isEmpty())return -1;
        properties.put(AUTH, String.format("Bearer %s", token));
        return 1;
    }

    protected static String getAUTH() {
        return AUTH;
    }

    protected String getAuthorization(){
        return properties.get(AUTH);
    }
    protected int setAuthorization(String auth){
        if(auth==null)return -1;
        properties.put(AUTH,auth);
        return 1;
    }

    private PathData getString(String path,Map<String,String> parameters) throws Exception {
        PathData resultPathData=null;
        if(path==null || path.equals("")){
            throw new RestException("Exception: no parameters.");
        }
        if(BuildConfig.DEBUG)Log.d(TAG,"Get HTTP to "+path);
        HttpURLConnection conn=null;
        InputStream inputStream=null;
        try{

            if(parameters!=null && !parameters.isEmpty()){
                //Insert parameter head
                properties.putAll(parameters);
                conn=getConnection(path);

            }

            if(conn==null)throw new RestException("Exception: null HttpURLConnection.");
            inputStream=conn.getInputStream();
            if(inputStream==null)throw new RestException("Exception: null InputStream.");

            InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
            if(inputStreamReader==null) throw new RestException("Exception: null InputStreamReader.");

            BufferedReader br=new BufferedReader(inputStreamReader);
            if(br==null)throw new RestException("Exception: null BufferedReader.");
                String line=null;
                String result=null;
                while((line=br.readLine())!=null){
                    if(result==null)result=new String();
                    result+="\n";
                    result+=line;
                }
            resultPathData= new PathData(conn.getResponseCode(),conn.getHeaderFields(),result);
            resultPathData.setParameters(parameters);

        }catch (Exception e) {
            Log.e(TAG,"CMS error: "+e.getMessage());
            resultPathData= new PathData(-1,conn.getHeaderFields(),null);
            resultPathData.setParameters(parameters);
        }finally{
            if(conn!=null){
                conn.disconnect();
                if(inputStream!=null)
                    inputStream.close();
            }
        }
        return resultPathData;
    }

    protected boolean getHTTP(PathData[] paths){
        if(paths==null || paths.length<=0)return false;
        GetHttpAsyncTask getHttpAsyncTask=new GetHttpAsyncTask();
        getHttpAsyncTask.execute(paths);
        return true;
    }

    protected int postJson(final String json,String path) throws IOException {
        if (path == null || json == null || path.equals("")) {
            throw new RestException("Exception: no parameters.");
        }
        Log.d(TAG, json);
        HttpURLConnection conn = null;
        try {
            conn = getConnection(path);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            PrintWriter pw = new PrintWriter(conn.getOutputStream());
            pw.print(json);
            pw.close();
            return conn.getResponseCode();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }


    protected int postFile(String path,InputStream is,String fileName) throws IOException {
        String boundary=Long.toString(System.currentTimeMillis());
        String newLine="\r\n";
        String prefix="--";
        HttpURLConnection conn=null;
        try{
            conn=getConnection(path);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            DataOutputStream out=new DataOutputStream(conn.getOutputStream());
            out.writeBytes(prefix+boundary+newLine);
            out.writeBytes("Content-Disposition: form-data; name=\"uploadedFile\";filename=\""+fileName+"\""+newLine);
            out.writeBytes(newLine);
            byte[] data=new byte[1024*1024];
            int len;
            while ((len=is.read(data))>0){
                out.write(data,0,len);
            }

            out.writeBytes(newLine);
            out.writeBytes(prefix+boundary+prefix+newLine);
            out.close();
            return conn.getResponseCode();
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
    }
    protected String getPathServer() {
        return pathServer;
    }

    protected void setPathServer(String pathServer) {
        try {
            Uri pathServerUri = Uri.parse(pathServer);
            pathServer = pathServerUri.toString();
            if(BuildConfig.DEBUG)Log.d(TAG,"Set Path Server: "+pathServer);
            this.pathServer=((pathServer.lastIndexOf("/")==pathServer.length()-1)?pathServer.substring(0,pathServer.lastIndexOf("/")):pathServer);
        }catch (Exception ex){
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in configure PathService:"+ex.getMessage());
        }

    }

    protected class RestException extends IOException {
        protected RestException() { super(); }
        protected RestException(String message) { super(message); }
        protected RestException(String message, Throwable cause) { super(message, cause); }
        protected RestException(Throwable cause) { super(cause); }
    }

    protected static boolean isOnline(Context context) {
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private class GetHttpAsyncTask extends AsyncTask<PathData, Void, Map<String,PathData>> {

        @Override
        protected  Map<String,PathData> doInBackground(PathData... params) {
            int count = params.length;
            long totalSize = 0;
            Map<String,PathData> results=new HashMap<>();
            if(count>0){
                for(int con=0;con<count;con++){

                    PathData path=params[con];
                    if(path!=null && path.getPath()!=null && !path.getPath().isEmpty()){
                        try {
                            PathData result=getString(path.getPath(),path.getParameters());
                            if(result!=null){
                                results.put(path.getPath(),result);
                                if (BuildConfig.DEBUG) {
                                    Log.w(TAG, "Rest client get path:"+path.getPath()+" code: "+result.getCodeResult()+"\n"+"\n");
                                    if( result.getResponseBody()!=null){
                                        String lines[] = Utils.stringToArray(result.getResponseBody());
                                        for(String line:lines)Log.w(TAG,line);
                                    }
                                }
                            }else{
                                Log.e(TAG, "Response data error.");
                            }
                        } catch (Exception e) {
                            Log.e(TAG,"Error: Get HTTP "+path.getPath()+" : "+e.getMessage());
                        }
                    }else{
                        Log.e(TAG,"Invalid Address parameter.");
                    }

                }
            }
            return results;
        }

        @Override
        protected void onProgressUpdate(Void... values) {



        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Map<String,PathData> results) {
            if(onGetHTTPListener!=null)onGetHTTPListener.onGetHTTPResponse(results);
        }
        @Override
        protected void onCancelled() {
            if(onGetHTTPListener!=null)onGetHTTPListener.errorGetHTTPResponse("Error in downloaded string.");
        }
    }

    protected void setOnGetHTTPListener(OnGetHTTPListener onGetHTTPListener){
        this.onGetHTTPListener=onGetHTTPListener;

    }

    protected interface OnGetHTTPListener{
       void onGetHTTPResponse(Map<String,PathData> results);
        void errorGetHTTPResponse(String error);
    }


}
