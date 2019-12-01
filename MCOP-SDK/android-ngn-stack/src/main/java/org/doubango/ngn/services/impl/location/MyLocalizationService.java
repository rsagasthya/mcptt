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


package org.doubango.ngn.services.impl.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.location.TypeMcpttSignallingEvent;
import org.doubango.ngn.services.location.IMyLocalizationService;
import org.doubango.ngn.services.profiles.IMyProfilesService;
import org.doubango.ngn.sip.MyMessagingLocationSession;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.utils.Utils;

import java.util.ArrayList;


public class MyLocalizationService   implements IMyLocalizationService, LocationServer.OnReportListener {
    private final static String TAG = Utils.getTAG(MyLocalizationService.class.getCanonicalName());

    private static LocationServer mLocationServer;
    private final BroadcastReceiver broadcastReceiverLocationInfo;
    private BroadcastReceiver broadcastReceiverMcpttEvent;
    private static byte[] currentLocationInfo;
    private static boolean isStart;
    private static IMyProfilesService myProfilesService;
    private static org.doubango.ngn.services.emergency.IMyEmergencyService myEmergencyService;
    private OnErrorLocationListener onErrorLocationListener;


    @Override
    public boolean start() {
        myProfilesService=NgnEngine.getInstance().getProfilesService();
        myEmergencyService=NgnEngine.getInstance().getEmergencyService();
        Log.d(TAG,"Start "+"LocalizationService.");
        isStart=false;
        return true;
    }

    @Override
    public boolean stop() {
        myProfilesService=null;
        Log.d(TAG,"Stop "+"LocalizationService.");
        stopServiceLocation();
        unRegister();
        isStart=false;
        return true;
    }

    public MyLocalizationService() {
        broadcastReceiverLocationInfo=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG,"New message received.");
                if (intent.getAction().equals(LOCATION_ACTION)) {
                    byte[] locationInfo=intent.getByteArrayExtra(LOCATION_NEWLOCATION_INFO);
                    if(locationInfo==null || locationInfo.length==0){
                        Log.e(TAG,"Invalid location info.");
                    }else{
                        if(mLocationServer==null || !mLocationServer.sendRequestNow(context,locationInfo))configureNewServiceLocation(context,locationInfo);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LOCATION_ACTION);
        NgnApplication.getContext().registerReceiver(broadcastReceiverLocationInfo,intentFilter);
    }



    //INIT service location


    public void startServiceLocation(){
        if(mLocationServer!=null){
            mLocationServer.onInitialLogOn();
            isStart=true;
        }

    }


    public void configureNewServiceLocation(Context context,byte[] locationInfo){
        currentLocationInfo=locationInfo;
        reloadServiceLocation(context);
        mLocationServer.onLocationConfigurationReceived();
        if(!isStart)startServiceLocation();
    }


    public void reloadServiceLocation(final Context context){
        if(currentLocationInfo==null){
            Log.e(TAG,"No configuration for location service.");
        }
        Intent msgIntent = new Intent(context,LocationServer.class);
        msgIntent.putExtra(LocationServer.ACTION_CONFIGURE,currentLocationInfo);
        boolean oldVersion=false;
        if(myProfilesService!=null &&
                myProfilesService.getProfileNow(context)!=null &&
                myProfilesService.getProfileNow(context).isMcpttLocationInfoVersionOld()!=null &&
                myProfilesService.getProfileNow(context).isMcpttLocationInfoVersionOld()){
            oldVersion=true;
        }

        if(mLocationServer!=null && mLocationServer.isStart())mLocationServer.onDestroy();
        mLocationServer=LocationServer.getInstance(context,msgIntent,oldVersion);
        //Start trigger of inicial Log on
        // mLocationServer.onLocationConfigurationReceived();
        registerEventMcptt();
        setOnClickItemAddListener(this);
    }

    @Override
    public void onReport(String xmlReport) {
        final MyMessagingLocationSession imSession = MyMessagingLocationSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(),"");
        if(!(imSession.sendTextMessage(xmlReport))){
            Log.e(TAG,"Send report error.");
        }else{
            Log.d(TAG,"Send OK.");
            //reloadServiceLocation(NgnApplication.getContext());
        }
        MyMessagingLocationSession.releaseSession(imSession);
    }

    @Override
    public void onConfiguration(Boolean isConfiguration) {
        if(!isConfiguration)Log.e(TAG,"Configuration error.");
    }

    @Override
    public void errorLocation(String error, int code) {
        Log.e(TAG,"Location error: "+error);
        if(onErrorLocationListener!=null)onErrorLocationListener.onErrorLocation(error,code);
    }

    @Override
    public boolean isEmergency() {
        return
         myEmergencyService!=null?myEmergencyService.isStateEmergency():
        false;

    }

    public String createReport(Context context) {
        return mLocationServer!=null?mLocationServer.createReport(context):null;
    }


    public void stopServiceLocation(){
        if(mLocationServer!=null)mLocationServer.onDestroy();
    }

    private void registerEventMcptt(){
        broadcastReceiverMcpttEvent=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NgnAVSession.MCPTT_EVENT_FOR_LOCATION_ACTION)) {
                    TypeMcpttSignallingEvent typeMcpttSignallingEvent=(TypeMcpttSignallingEvent)intent.getSerializableExtra(NgnAVSession.MCPTT_EVENT_FOR_LOCATION_TYPE);
                    if(typeMcpttSignallingEvent==null){
                        Log.e(TAG,"Invalid mcptt event.");
                    }else if(mLocationServer!=null){
                        mLocationServer.onEventMcptt(typeMcpttSignallingEvent);
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnAVSession.MCPTT_EVENT_FOR_LOCATION_ACTION);
        NgnApplication.getContext().registerReceiver(broadcastReceiverMcpttEvent,intentFilter);
    }

    private void unRegister(){

        try {
            if(broadcastReceiverMcpttEvent!=null){

                NgnApplication.getContext().unregisterReceiver(broadcastReceiverMcpttEvent);
            }
            if(broadcastReceiverLocationInfo!=null){
                NgnApplication.getContext().unregisterReceiver(broadcastReceiverLocationInfo);
            }
        }catch (Exception e){
            Log.e(TAG,"Error: "+e.getMessage());
        }

    }

    public boolean sendLocationNow(){
        if(mLocationServer!=null && mLocationServer.isStart()){
            ArrayList<String> testData=new ArrayList<String>();
            testData.add("test");
            return mLocationServer.sendReportNow(testData);
        }else{
            Log.e(TAG,"Error sending location test.");
        }
        return false;
    }

//END service location

    private void setOnClickItemAddListener(LocationServer.OnReportListener onReportListener){
        if(mLocationServer!=null)mLocationServer.setOnClickItemAddListener(onReportListener);
    }



    public void setOnErrorLocationListener(OnErrorLocationListener onErrorLocationListener){
        this.onErrorLocationListener=onErrorLocationListener;
    }

    @Override
    public boolean clearService(){
        Log.d(TAG,"Clear:  "+"LocalizationService");
        stopServiceLocation();
        return true;
    }

}
