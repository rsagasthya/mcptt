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



package org.doubango.ngn.services.impl.mbms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.mbms.MbmsData;
import org.doubango.ngn.datatype.mbms.MbmsListeningStatusType;
import org.doubango.ngn.datatype.mbms.McpttMbmsUsageInfoType;
import org.doubango.ngn.sip.MyMessagingMbmsSession;
import org.doubango.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class MyMbmsExtensionService
{
    private final static String TAG = Utils.getTAG(MyMbmsExtensionService.class.getCanonicalName());

    private Map<Integer, ArrayList<MbmsData>> activeMcpttMbmsUsageInfoType;
    private Map<Long, MbmsData> activeMcpttMbmsUsageInfoTypeforTMGI;
    private String currentServiceArea;
    private IBinder mService;
    private boolean isBound;
    private String sessionIdCurrent=null;

    protected static final String[] DEFAULT_SERVICE_CLASS_TYPES = {};
    private MyMbmsExtenxionServiceListener myMbmsExtenxionServiceListener;


    MyMbmsExtensionService() {


    }

    protected String getSessionIdCurrent() {
        return sessionIdCurrent;
    }

    protected void setSessionIdCurrent(String sessionIdCurrent) {
        this.sessionIdCurrent = sessionIdCurrent;
    }

    protected MbmsData getMbmsDataOfTmgi(long tmgi){
        return getActiveMcpttMbmsUsageInfoTypeforTMGI().get(tmgi);
    }

    protected MbmsData getMbmsDataOfTmgi(String groupID){
        for(MbmsData mbmsData:getActiveMcpttMbmsUsageInfoTypeforTMGI().values())
            if(mbmsData.getGroupID().trim().compareTo(groupID.trim())==0)return mbmsData;
        return null;
    }



    protected ArrayList<MbmsData> getMbmsDataOfServiceArea(int serviceArea){
        return getActiveMcpttMbmsUsageInfoType().get(serviceArea);
    }

    protected ArrayList<MbmsData> getMbmsDataOfServiceArea(int[] serviceAreas){
        ArrayList<MbmsData> mbmsData=null;
        for(int serviceArea:serviceAreas)
            if((mbmsData=getActiveMcpttMbmsUsageInfoType().get(serviceArea))!=null)
            return mbmsData;
        return null;
    }



    protected Map<Integer, ArrayList<MbmsData>> getActiveMcpttMbmsUsageInfoType() {
        if (activeMcpttMbmsUsageInfoType == null) {
            activeMcpttMbmsUsageInfoType = new HashMap<>();
        }
        return activeMcpttMbmsUsageInfoType;
    }

    protected Map<Long, MbmsData> getActiveMcpttMbmsUsageInfoTypeforTMGI() {
        if (activeMcpttMbmsUsageInfoTypeforTMGI == null) {
            activeMcpttMbmsUsageInfoTypeforTMGI = new HashMap<>();
        }
        return activeMcpttMbmsUsageInfoTypeforTMGI;
    }

    protected boolean deleteActiveMcpttMbmsUsageInfoType() {
        activeMcpttMbmsUsageInfoType=null;
        return true;
    }

    protected boolean deleteActiveMcpttMbmsUsageInfoTypeforTMGI() {
        activeMcpttMbmsUsageInfoTypeforTMGI=null;
        return true;
    }

    protected void putMbmsData(MbmsData mbmsData){
        //TODO:If a Service AREA ID can have more than one TMGI this not better, since each one of the UPS for a single TMGI differs
        for(int sai:mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getMbmsServiceAreasArrayInteger()){
            ArrayList<MbmsData> mbmsDatas=null;
            if((mbmsDatas=getActiveMcpttMbmsUsageInfoType().get(sai))==null){
                mbmsDatas=new ArrayList<>();
            }
            mbmsDatas.add(mbmsData);
            getActiveMcpttMbmsUsageInfoType().put(sai, mbmsDatas);
        }
        getActiveMcpttMbmsUsageInfoTypeforTMGI().put(mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getTMGIBigInteger().longValue(),mbmsData);
    }



    /**
     *
     * @param mbmsData
     * @return
     */
    protected boolean sendMbmsListeningServiceAreaCurrent(MbmsData mbmsData,Boolean isListening,Context context){
        if(mbmsData==null)return false;
        Boolean purpose=true;
        String listening=null;
        String sessionId=null;
        if(mbmsData.getIpMulticastMedia()!=null &&
                !mbmsData.getIpMulticastMedia().isEmpty() &&
                mbmsData.getPortMulticastMedia()>0 &&
                mbmsData.getPortControlMulticastMedia()>0 &&
                mbmsData.getGroupID()!=null &&
                sessionIdCurrent!=null &&
                !sessionIdCurrent.isEmpty()
                ){
            Log.d(TAG,"All MBMS parameters filled.");
            purpose=null;
            listening="listening";
            sessionId=sessionIdCurrent;
        }else{
            if(isListening){
                purpose=true;
                listening="listening";
            }else{
                purpose=false;
                listening="not-listening";
            }
        }
        //Create MESSAGE for send McpttMbmsUsageInfoType
        McpttMbmsUsageInfoType mbmsUsageInfoType = mbmsData.getMcpttMbmsUsageInfoType();
        McpttMbmsUsageInfoType mbmsUsageInfoTypeSend=new McpttMbmsUsageInfoType();
        mbmsUsageInfoTypeSend.setGPMS(mbmsUsageInfoType.getGPMS());
        MbmsListeningStatusType mbmsListeningStatusType=new MbmsListeningStatusType();
        mbmsUsageInfoTypeSend.setMbmsListeningStatus(mbmsListeningStatusType);
        mbmsUsageInfoTypeSend.setVersion(BigInteger.valueOf(1));
        mbmsListeningStatusType.setMbmsListeningStatus(listening);
        mbmsListeningStatusType.setSessionId(sessionId);
        mbmsListeningStatusType.setGeneralPurpose(purpose);
        ArrayList<String> tmgis=new ArrayList<>();
        tmgis.add(mbmsUsageInfoType.getAnnouncement().getTMGI());
        mbmsListeningStatusType.setTmgi(tmgis);
        //send to service the new tmgi
            //TODO:
            if(myMbmsExtenxionServiceListener!=null)
                myMbmsExtenxionServiceListener.mbmsListeningServiceAreaCurrent( mbmsUsageInfoType.getAnnouncement().getTMGIBigInteger().longValue(),
                        mbmsUsageInfoType.getAnnouncement().getMbmsServiceAreasArrayInteger(),
                        mbmsUsageInfoType.getAnnouncement().getFrequencyArrayInteger());


        //Send message for indicarte to AS.
        try {
            //Send MESSAGE for MBMS with mbmsUsageInfoType
            String xmlMessage=MbmsUtils.getStringOfMcpttMbmsUsageInfoType(context,mbmsUsageInfoTypeSend);
            final MyMessagingMbmsSession imSession = MyMessagingMbmsSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(),"");
            if(!(imSession.sendTextMessage(xmlMessage))){
                Log.e(TAG,"Error sending MBMS message.");
            }else{
                Log.d(TAG,"Send MBMS message.");
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG,"Error generating MBMS message: "+e.getMessage());
        }
        return false;

    }


















     protected void startClient(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {
         Log.d(TAG,"Starting CLIENT MBMS.");
            //TODO: Send info for the external MBMS is connected.
             if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.startedClient(true);

     }

    protected void stopClient(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {

        Log.d(TAG,"Stop CLIENT MBMS.");
            //TODO: Send info for the external MBMS is disconnected.
            if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.startedClient(false);



    }

    protected void stopServer(Context context) {
        Log.d(TAG,"Stoping SERVER MBMS.");

            if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.startedServer(false);

    }


    protected void startServer(Context context) {
        Log.d(TAG,"Starting SERVER MBMS.");
            //TODO:
            if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.startedServer(true);
    }


    private Intent getExplicitIntent(String action,Context context) throws IOException
    {
        Log.v(TAG, "-->getExplicitIntent()");

        PackageManager pm  = context.getPackageManager();
        Intent            implicitIntent = new Intent(action);
        List<ResolveInfo> resolveInfos   = pm.queryIntentServices(implicitIntent, 0);

        int count = resolveInfos.size();


        if (resolveInfos == null || count != 1)
        {
            Log.e(TAG, ((count<1) ? "No service found!"+ count : "Multiple services found! [count: "+ count + "]"));
            throw new IOException("Impossible to query Intent Services to start MSP server!");
        }

        ResolveInfo   serviceInfo = resolveInfos.get(0);
        String        packageName = serviceInfo.serviceInfo.packageName;
        String        className   = serviceInfo.serviceInfo.name;

        Log.v(TAG, "[Service PackageName: " + packageName + "]");
        Log.v(TAG, "[Service Name: " + className + "]");

        ComponentName component   = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent();
        explicitIntent.setComponent(component);

        Log.v(TAG, "<--getExplicitIntent()");

        return explicitIntent;
    }


    /*
            * Service binding routines
            * */
    private ServiceConnection service_binding = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(BuildConfig.DEBUG)
                Log.d(TAG,"The client is Connected of Service MBMS: "+name.getPackageName());
            if(service!=null){
                mService=service;
            }
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(BuildConfig.DEBUG)
                Log.d(TAG,"The client is Disconnected of Service MBMS "+name.getPackageName());
            mService=null;
            isBound=false;
        }
    };




    protected void startMbmsManager(String interfaceNet,long tmgi){
        Log.i(TAG, "MCR will use " + interfaceNet + " to create Multicast socket.");

        //TODO: API Problem int target_ni_idx = serviceStreamingOpenedEvent.getNetworkInterface().getIndex();
        int target_ni_idx = -1;

        //TODO We should store the target_ni!!!

        MbmsData mbmsData = null ;

        if((mbmsData = getMbmsDataOfTmgi(tmgi))!=null){
            mbmsData.setLocalInterface(interfaceNet);
            mbmsData.setLocalInterfaceIndex(target_ni_idx);
            if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.startMbmsManagerListening(mbmsData);
        }else{
            Log.d(TAG,"the device don´t has datas for tmgi "+tmgi);
        }
    }


    protected void stopMbmsManager(long tmgi){
        Log.i(TAG, "MCR do not use tmgi(" + tmgi + ") to close Multicast socket.");

        //TODO: API Problem int target_ni_idx = serviceStreamingOpenedEvent.getNetworkInterface().getIndex();
        int target_ni_idx = -1;

        //TODO We should store the target_ni!!!

        MbmsData mbmsData = null ;
        if((mbmsData = getMbmsDataOfTmgi(tmgi))!=null){
            if(myMbmsExtenxionServiceListener!=null)myMbmsExtenxionServiceListener.stopMbmsManagerListening(mbmsData);
        }else{
            Log.d(TAG,"the device don´t has datas for tmgi "+tmgi);
        }
    }


    protected interface MyMbmsExtenxionServiceListener{
        void startMbmsManagerListening(MbmsData mbmsData);
        void stopMbmsManagerListening(MbmsData mbmsData);
        void startedClient(boolean status);
        void startedServer(boolean status);
        boolean mbmsListeningServiceAreaCurrent(long TMGI,  int[] sai,  int[] frequencies);
    }

    protected void setMyMbmsExtenxionServiceListener(MyMbmsExtenxionServiceListener myMbmsExtenxionServiceListener){
        this.myMbmsExtenxionServiceListener=myMbmsExtenxionServiceListener;
    }


    public List<Long> getTMGIs(int serviceAreaID) {
        ArrayList<MbmsData> mbmsDatas=getActiveMcpttMbmsUsageInfoType().get(serviceAreaID);
        ArrayList<Long> tmgis=new ArrayList<>();
        if(mbmsDatas!=null)
            for(MbmsData mbmsData:mbmsDatas)
                tmgis.add(mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getTMGIBigInteger().longValue());
        return tmgis;
    }



    public int[] getSais(long TMGI) {
        try {
            return getActiveMcpttMbmsUsageInfoTypeforTMGI().get(TMGI).getMcpttMbmsUsageInfoType().getAnnouncement().getMbmsServiceAreasArrayInteger();
        }catch (Exception e){
            Log.e(TAG,"Error in get SAI with "+TMGI);

        }
        return null;

    }

    public int[] getFrequencies(long TMGI) {
        try {
            return getActiveMcpttMbmsUsageInfoTypeforTMGI().get(TMGI).getMcpttMbmsUsageInfoType().getAnnouncement().getFrequencyArrayInteger();
        }catch (Exception e){
            Log.e(TAG,"Error in get frequencies with "+TMGI);
        }
        return null;
    }




}
