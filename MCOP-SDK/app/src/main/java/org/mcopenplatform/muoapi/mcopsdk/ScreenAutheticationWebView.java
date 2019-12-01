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



package org.mcopenplatform.muoapi.mcopsdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.doubango.ngn.R;
import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.utils.Utils;

import java.net.URI;
import java.net.URISyntaxException;

public class ScreenAutheticationWebView extends AppCompatActivity {

    private final static String TAG = Utils.getTAG(ScreenAutheticationWebView.class.getCanonicalName());
    private Context mContext;
    public final static int RETURN_ON_AUTHENTICATION_LISTENER_OK= 6547;
    public final static int RETURN_ON_AUTHENTICATION_LISTENER_FAILURE= 6548;
    public final static String RETURN_ON_AUTHENTICATION_CAMPS= "RETURN__ON_AUTHENTICATION_CAMPS."+TAG;
    public final static String RETURN_ON_AUTHENTICATION_ERROR= "RETURN__ON_AUTHENTICATION_ERROR."+TAG;
    public final static String RETURN_ON_AUTHENTICATION_RESPONSE= "RETURN__ON_AUTHENTICATION_RESPONSE."+TAG;

    public final static String DATA_URI_INTENT= "DATA_URI_INTENT."+TAG;
    public final static String DATA_REDIRECTION_URI= "DATA_REDIRECTION_URI."+TAG;
    public final static String DATA_USER= "DATA_USER."+TAG;
    public final static String DATA_PASS= "DATA_PASS."+TAG;
    private final static String QUERY_PATH="code";

    private final static int TIME_DELAY_MSEG=5000;

    private WebView screen_authentication_WebView_info;
    private String userAuthentication;
    private String passAuthentication;
    private String uriString;
    private Runnable mRunnableCheckUrl;
    private Handler handler;
    private URI redirectionDataUri;
    private WebViewClient mWebViewClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_authetication_web);

        mContext=this;
        Log.d(TAG,"Start Authentication Process");
        //Delete all Cache for webView
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        //End Delete all Cache for webView
        screen_authentication_WebView_info=(WebView)findViewById(R.id.screen_authentication_WebView_info);
        //Force links and redirects to open in the WebView instead of in a browser
        screen_authentication_WebView_info.setWebViewClient(new WebViewClient());
        screen_authentication_WebView_info.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int i, int i1, boolean b) {
                Log.d(TAG,"Response web:"+i+" "+i1);
            }
        });
        //Enable Javascript
        WebSettings webSettings = screen_authentication_WebView_info.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        Intent intent=getIntent();
        if (intent != null) {
            uriString=intent.getStringExtra(DATA_URI_INTENT);
            String redirectionDataString=intent.getStringExtra(DATA_REDIRECTION_URI);
            if(redirectionDataString==null || redirectionDataString.trim().isEmpty()){
                Log.e(TAG,"Invalid redirectionURI data");
                sendError("Invalid redirectionURI data");
            }else{
                try {
                    redirectionDataUri=new URI(redirectionDataString);
                } catch (URISyntaxException e) {
                    Log.e(TAG,"Error parsing redirectionURI. "+e.getMessage() );
                    sendError("Error parsing redirectionURI. "+e.getMessage());
                }
            }
            userAuthentication=intent.getStringExtra(DATA_USER);
            passAuthentication=intent.getStringExtra(DATA_PASS);
            if(uriString!=null){
                screen_authentication_WebView_info.loadUrl(uriString);
            }
        }
        mWebViewClient=new WebViewClient(){
            @Override
            public void onReceivedError(final WebView view, int errorCode, String description, final String failingUrl) {
                if(BuildConfig.DEBUG)Log.e(TAG,"onReceivedError: "+errorCode+" des: "+description);
                Uri uri=Uri.parse(screen_authentication_WebView_info.getUrl());
                if(((uri.getScheme().compareToIgnoreCase(redirectionDataUri.getScheme())!=0 ||
                        uri.getHost().compareToIgnoreCase(redirectionDataUri.getHost())!=0 ||
                        uri.getPort()!=redirectionDataUri.getPort() )&&
                        description.trim().compareTo("net::ERR_CONNECTION_REFUSED") ==0
                )){
                    Log.e(TAG,"Web Code Error: "+errorCode+" des: "+description);
                    finishError(getString(R.string.Error_in_authetication)+" code: "+errorCode+" des: "+description);
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(userAuthentication!=null && passAuthentication!=null){
                    screen_authentication_WebView_info.loadUrl("javascript:(function() { document.getElementById('j_username').value = '" + userAuthentication + "'; ;})()");
                    screen_authentication_WebView_info.loadUrl("javascript:(function() { document.getElementById('j_password').value = '" + passAuthentication + "'; ;})()");
                }
                checkUrl();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if(BuildConfig.DEBUG)Log.d(TAG,"onReceivedHttpError: "+errorResponse.toString());
            }
        };

        screen_authentication_WebView_info.setWebViewClient(mWebViewClient);
    }

    /*private String getUrlParameters(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null, // Ignore the query part of the input url
                uri.getFragment()).toString();
    }*/
    private String getUrlParameters(Uri uri) throws URISyntaxException {
        String uriString=new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null, // Ignore the query part of the input url
                uri.getFragment()).toString();
        return uriString;
    }

    private boolean isEquals(String stringUri1,String stringUri2){
        try {
            int index=stringUri1.lastIndexOf("/");
            if(stringUri1.lastIndexOf("/")==(stringUri1.length()-1)){
                stringUri1=stringUri1.substring(0,stringUri1.length()-1);
            }
            index=stringUri2.lastIndexOf("/");
            if(stringUri2.lastIndexOf("/")==(stringUri2.length()-1)){
                stringUri2=stringUri2.substring(0,stringUri2.length()-1);
            }
            return stringUri1.compareToIgnoreCase(stringUri2)==0?true:false;
        } catch (Exception e) {
            Log.e(TAG,"Error parse URL.");
           return false;
        }
    }

    private void checkUrl(){
        handler = new Handler();
        mRunnableCheckUrl=new Runnable() {
            @Override
            public void run() {
                try {
                    Uri uri=Uri.parse(screen_authentication_WebView_info.getUrl());
                    String scheme=uri.getScheme();
                    String host=uri.getHost();
                    String path=uri.getPath();
                    String uriWhitoutParameters=getUrlParameters(uri);
                    if(isEquals(uriWhitoutParameters,redirectionDataUri.toString())
                            ){
                            Intent intent = new Intent();
                        intent.putExtra(RETURN_ON_AUTHENTICATION_RESPONSE, uri.toString());
                            setResult(RETURN_ON_AUTHENTICATION_LISTENER_OK,intent);
                        finish();
                    }
                    else{
                        if(userAuthentication!=null &&
                            !userAuthentication.isEmpty() &&
                            passAuthentication!=null &&
                            !passAuthentication.isEmpty()) {
                            //Configure User and Pass and click Submit
                            screen_authentication_WebView_info.loadUrl("javascript:(function() { document.getElementsByName('submit')[0].click(); })()");
                            //Click Authorize
                            screen_authentication_WebView_info.loadUrl("javascript:(function() { document.getElementsByName('authorize')[0].click(); })()");
                        }else{
                            checkUrl();
                        }

                    }
                } catch (Exception e) {
                    Log.e(TAG,"Error checking URL authentication: "+e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(mRunnableCheckUrl,TIME_DELAY_MSEG);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    @Override
    public void finish(){
        if(handler!=null){
            handler.removeCallbacks(mRunnableCheckUrl);
        }
         super.finish();
    }

    public void finishError(String error){
        sendError(error);
        finish();
    }

    private void sendError(String error){
        Intent intent = new Intent();
        intent.putExtra(RETURN_ON_AUTHENTICATION_ERROR,error);
        setResult(RETURN_ON_AUTHENTICATION_LISTENER_FAILURE,intent);
    }
}
