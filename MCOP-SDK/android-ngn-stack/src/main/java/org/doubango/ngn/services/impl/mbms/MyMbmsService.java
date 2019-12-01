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

package org.doubango.ngn.services.impl.mbms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.datatype.mbms.MbmsData;
import org.doubango.ngn.datatype.mbms.McpttMbmsUsageInfoType;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnMcpttMbmsEventArgs;
import org.doubango.ngn.events.NgnMcpttMbmsEventTypes;
import org.doubango.ngn.services.mbms.IMyMbmsService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnObservableHashMap;
import org.doubango.utils.Utils;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


//TODO:At the moment only MBMS service works with a single session of MCPTT group. But in the future is nesitaria that would work with N sessions simultaneous.
public class MyMbmsService implements
        IMyMbmsService,
        MyMbmsExtensionService.MyMbmsExtenxionServiceListener
        {
    private final static String TAG = Utils.getTAG(MyMbmsService.class.getCanonicalName());

    private final BroadcastReceiver broadcastReceiverMbmsInfo;
    private final BroadcastReceiver broadcastReceiverMbmsControl;
    private final MyMbmsExtensionService mbmsExtensionService;
    private int[] currentServiceArea;
    private IBinder mService;
    private boolean isBound;
    private OnMbmsListener onMbmsListener;

    protected static final String[] DEFAULT_SERVICE_CLASS_TYPES = {};
    private MbmsExternalServiceListener mbmsExternalServiceListener;

            @Override
    public boolean start() {
        Log.d(TAG, "Start " + "MbmsService");
        //Start services mbms
        try {
            startServer(NgnApplication.getContext());
        }catch (Exception e){
            if(BuildConfig.DEBUG)Log.w(TAG,"MBMS error:"+e.getMessage());
        }
        //Start client eMBMS
        try {
            startClient(NgnApplication.getContext());
        } catch (Exception e) {
            Log.e(TAG,"Error start client mbms "+e.getMessage());
        }
        return true;
    }

    @Override
    public boolean stop() {
        Log.d(TAG, "Stopped " + "MbmsService");
        //Stop client eMBMS
        try {
            stopClient(NgnApplication.getContext());
        } catch (Exception e) {
            Log.e(TAG,"Error stop client mbms "+e.getMessage());
        }
        //Stop services mbms
        try {
            stopServer(NgnApplication.getContext());
            stopServiceMbms();
        }catch (Exception e){
            Log.e(TAG,"Error in MBMS servide"+e.getMessage());
        }
        return true;
    }

    public MyMbmsService() {
        mbmsExtensionService=new MyMbmsExtensionService();
        mbmsExtensionService.setMyMbmsExtenxionServiceListener(this);
        broadcastReceiverMbmsInfo = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "New message received.");
                if (intent.getAction().equals(MBMS_ACTION)) {
                    byte[] mbmsListeningStatusType = intent.getByteArrayExtra(MBMS_NEWMESSAGE_MBMS);
                    int portMBMSManager = intent.getIntExtra(MBMS_PORT_MANAGER_MBMS, MBMS_DEFAULT_INT);
                    String ipMBMSManager = intent.getStringExtra(MBMS_IP_MANAGER_MBMS);
                    String pAssertedIdentity = intent.getStringExtra(MBMS_P_ASSERTED_IDENTITY);
                    if (mbmsListeningStatusType == null ||
                            mbmsListeningStatusType.length == 0 ||
                            portMBMSManager == MBMS_DEFAULT_INT ||
                            ipMBMSManager == null ||
                            ipMBMSManager.isEmpty()||
                            pAssertedIdentity==null||
                            pAssertedIdentity.isEmpty()) {
                        Log.e(TAG, "Invalid MBMS info.");
                    } else {
                        /*
                            Configurated with new Message
                         */
                        configureNewServiceMbms(mbmsListeningStatusType, portMBMSManager, ipMBMSManager,pAssertedIdentity);
                    }
                }else if(intent.getAction().equals(MBMS_SESSION_ID_ACTION)){
                    String sessionID=intent.getStringExtra(MBMS_SESSION_ID_MBMS);
                    if(sessionID!=null && !sessionID.isEmpty()){
                        Log.d(TAG,"Set call ID Session.");
                        if(mbmsExtensionService!=null)
                        mbmsExtensionService.setSessionIdCurrent(sessionID.replace("<","").replace(">","").replace("gr","").replace(";",""));
                    }
                }else if(intent.getAction().equals(MBMS_MEDIA_ACTION)){{
                    Log.d(TAG,"MBMS media parameters received.");
                    if(intent.getBooleanExtra(MBMS_MAP_MBMS,true)){
                        String groupID=intent.getStringExtra(MBMS_GROUP_ID_MBMS);
                        String ipMedia=intent.getStringExtra(MBMS_IP_MEDIA_MBMS);
                        long tmgi=intent.getLongExtra(MBMS_TMGI_MBMS,MBMS_DEFAULT_INT);
                        int portMedia=intent.getIntExtra(MBMS_PORT_MEDIA_MBMS,MBMS_DEFAULT_INT);
                        int portControlMedia=intent.getIntExtra(MBMS_PORT_CONTROL_MEDIA_MBMS,MBMS_DEFAULT_INT);
                        if(ipMedia!=null &&
                                !ipMedia.isEmpty()&&
                                tmgi!=MBMS_DEFAULT_INT &&
                                portControlMedia!=MBMS_DEFAULT_INT &&
                                portMedia!=MBMS_DEFAULT_INT){
                            Log.d(TAG,"MBMS IP and Port configured.");
                            MbmsData mbmsData=null;
                            if((mbmsData=getMbmsDataOfTmgi(tmgi))!=null){
                                mbmsData.setIpMulticastMedia(ipMedia);
                                mbmsData.setPortMulticastMedia(portMedia);
                                mbmsData.setPortControlMulticastMedia(portControlMedia);
                                mbmsData.setGroupID(groupID);
                                checkTMGItoListening(tmgi,context);
                            }
                        }
                    }else{
                        //TODO: for UNMAP
                    }

                }}
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MBMS_ACTION);
        intentFilter.addAction(MBMS_SESSION_ID_ACTION);
        intentFilter.addAction(MBMS_MEDIA_ACTION);
        NgnApplication.getContext().registerReceiver(broadcastReceiverMbmsInfo, intentFilter);

        broadcastReceiverMbmsControl = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                long tmgi;
                String ipMedia;
                String groupID;
                int portMedia;
                int portControlMedia;
                Intent intentMediaAct=new Intent();
                // Registration Event
                if(NgnMcpttMbmsEventArgs.ACTION_MCPTT_MBMS_EVENT.equals(action)){
                    Log.d(TAG,"MBMS Control EVENT.");
                    NgnMcpttMbmsEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    final NgnMcpttMbmsEventTypes type;
                    if(args == null){
                        Log.e(TAG, "Invalid event args.");
                        return;
                    }else{
                        Log.d(TAG, "MCPTT MBMS event args.");
                    }
                    switch((type = args.getEventType())){
                        case MAP_GROUP:

                            Log.d(TAG, "MBMS GROUP MAPPED");
                            groupID=intent.getStringExtra(NgnMcpttMbmsEventArgs.EXTRA_GROUP);
                            tmgi = intent.getLongExtra(NgnMcpttMbmsEventArgs.EXTRA_TMGI,-1);
                            ipMedia = intent.getStringExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_IP);
                            portMedia = intent.getShortExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_PORT, new Short("-1"));
                            portControlMedia = intent.getShortExtra(NgnMcpttMbmsEventArgs.EXTRA_MEDIA_CTRL_PORT, new Short("-1"));


                            intentMediaAct.putExtra(MBMS_GROUP_ID_MBMS,groupID);
                            intentMediaAct.putExtra(MBMS_MAP_MBMS,true);
                            intentMediaAct.putExtra(MBMS_IP_MEDIA_MBMS,ipMedia);
                            intentMediaAct.putExtra(MBMS_PORT_CONTROL_MEDIA_MBMS,portControlMedia);
                            intentMediaAct.putExtra(MBMS_PORT_MEDIA_MBMS,portMedia);
                            intentMediaAct.putExtra(MBMS_TMGI_MBMS,tmgi);
                            intentMediaAct.setAction(MBMS_MEDIA_ACTION);
                            NgnApplication.getContext().sendBroadcast(intentMediaAct);

                            break;
                        case UNMAP_GROUP:
                            Log.d(TAG, "MBMS GROUP UNMAPPED");
                            groupID=intent.getStringExtra(NgnMcpttMbmsEventArgs.EXTRA_GROUP);
                            intentMediaAct.putExtra(MBMS_MAP_MBMS,false);
                            intentMediaAct.putExtra(MBMS_GROUP_ID_MBMS,groupID);
                            intentMediaAct.setAction(MBMS_MEDIA_ACTION);

                            NgnApplication.getContext().sendBroadcast(intentMediaAct);

                            break;
                       default:
                            Log.d(TAG, "Invalid event.");
                            break;
                    }


                }
            }
        };
        final IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(NgnMcpttMbmsEventArgs.ACTION_MCPTT_MBMS_EVENT);
        NgnApplication.getContext().registerReceiver(broadcastReceiverMbmsControl, intentFilter2);

    }

    //INIT service location



    /**
     * Executed when new service configuration received
     * TODO: This method is where you must create logic to listen to from the network the TMGIs and the related SAIs
     * @param mcpttMbmsUsageInfoTypeBytes
     */
    private boolean configureNewServiceMbms(byte[] mcpttMbmsUsageInfoTypeBytes, int portMBMSManager, String ipMBMSManager,String pAssertedIdentity) {
        if(BuildConfig.DEBUG)Log.i(TAG,"configureNewServiceMbms portMBMSManager:"+portMBMSManager+ " ipMBMSManager:"+ipMBMSManager);
        McpttMbmsUsageInfoType mcpttMbmsUsageInfoType = MbmsUtils.getMcpttMbmsUsageInfoType(mcpttMbmsUsageInfoTypeBytes);
        if (mcpttMbmsUsageInfoType != null) {
            //check if mbmsListeningStatusType is listening or not
            mcpttMbmsUsageInfoType.getAnnouncement().getMbmsServiceAreas();
            MbmsData mbmsData=null;
            if (mbmsExtensionService != null &&
                    mcpttMbmsUsageInfoType.getAnnouncement() != null &&
                    mcpttMbmsUsageInfoType.getAnnouncement().getMbmsServiceAreas() != null &&
                    !mcpttMbmsUsageInfoType.getAnnouncement().getMbmsServiceAreas().trim().isEmpty()) {
                if (mcpttMbmsUsageInfoType.getAnnouncement().getMbmsServiceAreas().trim().equals(MBMS_SERVICE_AREA_CANCELL)) {
                    Log.d(TAG, "Delete all service area MBMS.");
                    stopMbmsManagerListening();
                    mbmsExtensionService.deleteActiveMcpttMbmsUsageInfoType();
                    mbmsExtensionService.deleteActiveMcpttMbmsUsageInfoTypeforTMGI();
                } else{
                    Log.d(TAG, "Add new service area MBMS.");
                    mbmsData = new MbmsData(mcpttMbmsUsageInfoType, portMBMSManager, ipMBMSManager,pAssertedIdentity);
                    putMbmsData(mbmsData);
                    //This will be done when the stream is opened
                    // startMbmsManagerListening(mbmsData);
                }
            }

            return true;
        } else {
            Log.e(TAG, "Unable to configure MBMS server.");
            return false;
        }
    }
    @Override
    public void startMbmsManagerListening(MbmsData mbmsData){
        //TODO: start Listening MBMS manager
        Log.d(TAG,"MyMbmsService. Starting MBMS manager. IP=" + mbmsData.getIpMBMSManager() + " Port=" + mbmsData.getPortMBMSManager() + " Interface=" + mbmsData.getLocalInterface() + " Index=" + mbmsData.getLocalInterfaceIndex());
        /*
        Intent intent=new Intent();
        intent.putExtra(MyMbmsService.MBMS_IP_MANAGER_MBMS, mbmsData.getIpMBMSManager());
        intent.putExtra(MyMbmsService.MBMS_PORT_MANAGER_MBMS, mbmsData.getPortMBMSManager());
        intent.putExtra(MyMbmsService.MBMS_LOCAL_IFACE, mbmsData.getLocalInterface());
        intent.putExtra(MyMbmsService.MBMS_LOCAL_IFACE_INDEX, mbmsData.getLocalInterfaceIndex());
        intent.setAction(MyMbmsService.MBMS_CALL_ACTION_MANAGER_START);
        NgnApplication.getContext().sendBroadcast(intent);
        */
        //TODO: it is necessary send for all sessions:
        NgnObservableHashMap<Long, NgnAVSession> sessions = NgnAVSession.getSessions();
        if(sessions!=null && !sessions.isEmpty())
        for(NgnAVSession session:sessions.values()){
            if(session.isActive() && session.isConnected() && session.startMbmsManager(mbmsData.getIpMBMSManager(),mbmsData.getPortMBMSManager(), mbmsData.getLocalInterface(),mbmsData.getLocalInterfaceIndex())){
                if(BuildConfig.DEBUG)Log.i(TAG,"execute ok startMbmsManager in session id:"+session.getId());

            }else{
                if(BuildConfig.DEBUG)Log.e(TAG,"No correct execute startMbmsManager in session id:"+session.getId());
            }
        }
    }

    @Override
    public void stopMbmsManagerListening(MbmsData mbmsData) {
//TODO: start Listening MBMS manager
        Log.d(TAG,"MyMbmsService. Stopping MBMS manager. IP=" + mbmsData.getIpMBMSManager() + " Port=" + mbmsData.getPortMBMSManager() + " Interface=" + mbmsData.getLocalInterface() + " Index=" + mbmsData.getLocalInterfaceIndex());
        //TODO: it is necessary send for all sessions:
        NgnObservableHashMap<Long, NgnAVSession> sessions = NgnAVSession.getSessions();
        if(sessions!=null && !sessions.isEmpty())
            for(NgnAVSession session:sessions.values()){
                if(session.stopMbmsManager()){
                    if(BuildConfig.DEBUG)Log.i(TAG,"execute ok stopMbmsManager in session id:"+session.getId());
                }else{
                    if(BuildConfig.DEBUG)Log.e(TAG,"No correct execute stopMbmsManager in session id:"+session.getId());
                }
            }
    }

    @Override
    public void startMbmsManager(String  interfaceNet,long tmgi){
        if(mbmsExtensionService!=null)mbmsExtensionService.startMbmsManager(interfaceNet,tmgi);
    }

    @Override
    public void stopMbmsManager(long tmgi){
        if(mbmsExtensionService!=null)mbmsExtensionService.stopMbmsManager(tmgi);
    }


    @Override
    public List<Long> getTMGIs(int serviceAreaID) {
        return mbmsExtensionService.getTMGIs(serviceAreaID);
    }

    @Override
    public int[] getSais(long TMGI) {
        return mbmsExtensionService.getSais(TMGI);
    }

    @Override
    public int[] getFrequencies(long TMGI) {
        return mbmsExtensionService.getFrequencies(TMGI);
    }

            @Override
    public void startedClient(boolean status) {
        if(mbmsExternalServiceListener!=null)mbmsExternalServiceListener.startedClient(status);
    }

    @Override
    public void startedServer(boolean status) {
        if(mbmsExternalServiceListener!=null)mbmsExternalServiceListener.startedServer(status);
    }

    @Override
    public boolean mbmsListeningServiceAreaCurrent(long TMGI,  int[] sai,  int[] frequencies) {
        if(mbmsExternalServiceListener!=null)return mbmsExternalServiceListener.mbmsListeningServiceAreaCurrent( TMGI,   sai,   frequencies);
        return false;
    }



    private void putMbmsData(MbmsData mbmsData){
        if(mbmsExtensionService!=null)
        mbmsExtensionService.putMbmsData(mbmsData);

        if(onMbmsListener!=null &&
                mbmsData.getMcpttMbmsUsageInfoType()!=null &&
                mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement()!=null){
            onMbmsListener.onNewServiceArea(
                    mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getTMGIBigInteger()!=null?mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getTMGIBigInteger().longValue():-1,
                    mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getMbmsServiceAreasArrayInteger(),
                    mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getFrequencyArrayInteger(),
                    mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getQCI()!=null? mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getQCI().longValue():-1);
        }
    }



    private void stopMbmsManagerListening(){
        //TODO: stop Listening MBMS manager
        Log.d(TAG, "Stopping MBMS manager");
        Intent intent=new Intent();
        intent.setAction(MyMbmsService.MBMS_CALL_ACTION_MANAGER_STOP);
        NgnApplication.getContext().sendBroadcast(intent);
    }

    /**
     * If the device is in service area, it will return true
     *
     * @return
     */
    private boolean checkServiceArea(Context context) {
        if (mbmsExtensionService == null || mbmsExtensionService.getActiveMcpttMbmsUsageInfoType().isEmpty() || currentServiceArea == null) {
            Log.e(TAG,"Parameters MBMS is not valid.");
            return false;
        }
        ArrayList<MbmsData> mbmsDatas = mbmsExtensionService.getMbmsDataOfServiceArea(currentServiceArea);
        for(MbmsData mbmsData:mbmsDatas){
            if (mbmsData != null || mbmsData.getMcpttMbmsUsageInfoType() != null ) {
                Log.d(TAG, "Device has current service area MBMS.");
                if(mbmsExtensionService!=null)
                    mbmsExtensionService.sendMbmsListeningServiceAreaCurrent(mbmsData,true,context);
                return true;
            }
            Log.d(TAG, "Device does not have current service area MBMS.");
        }

        return false;
    }



    /**
     * it receive new service area, change in SAI
     * @param currentServiceArea
     * @return
     */
    @Override
    public boolean onChangeServiceArea(int[] currentServiceArea,Context context){
        if(currentServiceArea==null || currentServiceArea.length==0){
            Log.e(TAG,"The new Service Area MBMS isn´t valid");
            return false;
        }else{
            this.currentServiceArea=currentServiceArea;
            boolean control=checkServiceArea(context);
            if(control){
                Log.d(TAG,"Checked service area.");
            }else {
                Log.e(TAG,"Error checking service area.");
            }
            return control;
        }

    }

    /**
     * New service area received
     * TODO: Temporal function
     * @param tmgi
     * @return
     */
    @Override
    public void onReceiveMCCP(String tmgi,String ipMedia,int portMedia,int portControlMedia,Context context){
        if(tmgi==null || tmgi.trim().isEmpty()){
            Log.e(TAG,"New tmgi invalid.");
            return ;
        }else{
            Log.d(TAG,"Receive");
            Intent intent=new Intent();
            intent.putExtra(MyMbmsService.MBMS_IP_MEDIA_MBMS,ipMedia);
            intent.putExtra(MyMbmsService.MBMS_PORT_CONTROL_MEDIA_MBMS,portControlMedia);
            intent.putExtra(MyMbmsService.MBMS_PORT_MEDIA_MBMS,portMedia);
            intent.putExtra(MyMbmsService.MBMS_TMGI_MBMS,tmgi);
            intent.setAction(MyMbmsService.MBMS_MEDIA_ACTION);
            context.sendBroadcast(intent);



            return;
        }
    }
    @Override
    public MbmsData getMbmsDataOfTmgi(long tmgi){
        if(mbmsExtensionService!=null)
        return mbmsExtensionService.getMbmsDataOfTmgi(tmgi);
        return null;
    }

    private MbmsData getMbmsDataOfTmgi(String groupID){
        if(mbmsExtensionService!=null)
            return mbmsExtensionService.getMbmsDataOfTmgi(groupID);
        return null;
    }


    /**
     * If the device is in tmgi, it will return true
     *
     * @return
     */
    private boolean checkTMGItoListening(long tmgi,Context context) {
        if (mbmsExtensionService == null || mbmsExtensionService.getActiveMcpttMbmsUsageInfoType().isEmpty()) {
            Log.e(TAG,"MBMS parameters invalid.");
            return false;
        }
        MbmsData mbmsData = getMbmsDataOfTmgi(tmgi);
        if (mbmsData != null || mbmsData.getMcpttMbmsUsageInfoType() != null) {
            Log.e(TAG, "Message as TMGI.");
            if(listeningMulticast(tmgi,mbmsData,true) && mbmsExtensionService!=null){
                mbmsExtensionService.sendMbmsListeningServiceAreaCurrent(mbmsData,true,context);
                return true;
            }

        }
        return false;
    }

    private boolean listeningMulticast(long tmgi,MbmsData mbmsData,boolean start){
        //TODO: init listening media for MBMS audio in IP address Multicast
        boolean ret = true;
        if (start) {
            Log.d(TAG, "Start listening MBMS media.");
            /*
            Intent intent=new Intent();
            intent.putExtra(MyMbmsService.MBMS_IP_MEDIA_MBMS, mbmsData.getIpMulticastMedia());
            intent.putExtra(MyMbmsService.MBMS_PORT_MEDIA_MBMS, mbmsData.getPortMulticastMedia());
            intent.putExtra(MyMbmsService.MBMS_PORT_CONTROL_MEDIA_MBMS, mbmsData.getPortControlMulticastMedia());
            intent.setAction(MyMbmsService.MBMS_CALL_ACTION_MEDIA_START);
            NgnApplication.getContext().sendBroadcast(intent);
            */
            //TODO: it is necessary send for only any session:
            NgnObservableHashMap<Long, NgnAVSession> sessions = NgnAVSession.getSessions();
            if(sessions!=null && !sessions.isEmpty())
                if(BuildConfig.DEBUG && mbmsData.getGroupID()!=null)
                    Log.d(TAG,"Start multicast Group ID: "+mbmsData.getGroupID());

                for(NgnAVSession session:sessions.values()) {
                    String mcpttGroupIdentity=session.getPTTMcpttGroupIdentity();
                    if (session.isActive() &&
                            session.isConnected() &&
                            mcpttGroupIdentity!=null &&
                            mcpttGroupIdentity.trim().compareTo(mbmsData.getGroupID().trim())==0 &&
                            session.startMbmsMedia( mbmsData.getIpMulticastMedia(), mbmsData.getPortMulticastMedia(), mbmsData.getPortControlMulticastMedia())) {
                        if(mbmsExternalServiceListener!=null)mbmsExternalServiceListener.startMbmsMedia(session.getId(),tmgi);
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "execute ok mbms listeningMulticast in session id:" + session.getId());
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "No correct execute mbms listeningMulticast in session id:" + session.getId());
                    }
                }

        }else{
            //TODO: Is no Tried;
            NgnObservableHashMap<Long, NgnAVSession> sessions = NgnAVSession.getSessions();
            if(sessions!=null && !sessions.isEmpty())
                for(NgnAVSession session:sessions.values()) {
                    if (session.isActive() && session.isConnected() && session.stopMbmsManager()) {
                        if(mbmsExternalServiceListener!=null)mbmsExternalServiceListener.stopMbmsMedia(session.getId(),tmgi);
                        if (BuildConfig.DEBUG)
                            Log.i(TAG, "execute ok stopMbmsManager in session id:" + session.getId());
                    } else {
                        if (BuildConfig.DEBUG)
                            Log.e(TAG, "No correct execute stopMbmsManager in session id:" + session.getId());
                    }
                }
        }
        return ret;
    }


    public void hangUpCallMbms(long sessionID){

        NgnObservableHashMap<Long, NgnAVSession> sessions = NgnAVSession.getSessions();
        NgnAVSession session=NgnAVSession.getSession(sessionID);
        long tmgi=-1;
        MbmsData mbmsData=null;
        if(mbmsData!=null){
            String groupID=session.getPTTMcpttGroupIdentity();
            mbmsData=getMbmsDataOfTmgi(groupID);
            if(mbmsData==null || (tmgi=mbmsData.getMcpttMbmsUsageInfoType().getAnnouncement().getTMGIBigInteger().longValue())<0){
                if(BuildConfig.DEBUG){
                    Log.w(TAG,"TMGI not found for session "+sessionID);
                }
            }
        }

        Log.d(TAG,"hangUpCallMbms MBMS");
        if(mbmsExternalServiceListener!=null)
            mbmsExternalServiceListener.stopMbmsMedia(sessionID,tmgi);
    }



    public void stopServiceMbms(){
        Log.d(TAG,"Stop MBMS server.");
        mbmsExtensionService.setSessionIdCurrent(null);
        mbmsExtensionService.deleteActiveMcpttMbmsUsageInfoTypeforTMGI();
        mbmsExtensionService.deleteActiveMcpttMbmsUsageInfoType();
        if(mbmsExternalServiceListener!=null)
            mbmsExternalServiceListener.stopServiceMBMS();
    }


    public void setOnMbmsListener(OnMbmsListener onMbmsListener){
        this.onMbmsListener=onMbmsListener;
    }
    public Iterator<Integer> getServiceAreas(){
        Iterator<Integer> serviceAreas=null;
        if(mbmsExtensionService!=null && !mbmsExtensionService.getActiveMcpttMbmsUsageInfoType().isEmpty()){
            Set<Integer> stringSet=mbmsExtensionService.getActiveMcpttMbmsUsageInfoType().keySet();
            serviceAreas=stringSet.iterator();
        }
        return serviceAreas;
    }




            private void startClient(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {
        if(BuildConfig.DEBUG)Log.d(TAG,"Starting CLIENT MBMS.");
        mbmsExtensionService.startClient(context);
    }

    private void stopClient(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {
        if(BuildConfig.DEBUG)Log.d(TAG,"Stoping CLIENT MBMS.");
        mbmsExtensionService.stopClient(context);
    }

    private void stopServer(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {
        Log.d(TAG,"Stoping SERVER MBMS.");
        mbmsExtensionService.stopServer(context);
    }


    private void startServer(Context context) throws PackageManager.NameNotFoundException, MalformedURLException {
        Log.d(TAG,"Starting SERVER MBMS.");
        mbmsExtensionService.startServer(context);
    }


    @Override
    public boolean clearService(){
        return true;
    }


    //INIT EXTERNAL MBMS

    public void setMbmsExternalServiceListener(MbmsExternalServiceListener mbmsExternalServiceListener){
        this.mbmsExternalServiceListener=mbmsExternalServiceListener;
    }
    //END EXTERNAL MBMS

}
