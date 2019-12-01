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
package org.doubango.ngn.services.impl.affiliation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.affiliation.affiliationcommand.CommandList;
import org.doubango.ngn.datatype.affiliation.pidf.AffiliationType;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.datatype.affiliation.pidf.Status;
import org.doubango.ngn.datatype.affiliation.pidf.StatusType;
import org.doubango.ngn.datatype.affiliation.pidf.Tuple;
import org.doubango.ngn.services.affiliation.IMyAffiliationService;
import org.doubango.ngn.sip.MyPublicationAffiliationSession;
import org.doubango.ngn.sip.MySubscriptionAffiliationSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MyAffiliationService implements IMyAffiliationService {
    private final static String TAG = Utils.getTAG(MyAffiliationService.class.getCanonicalName());

    private static boolean isStart;
    private BroadcastReceiver broadcastReceiverAffiliationMessage;
    private MySubscriptionAffiliationSession mSessionSuscription=null;

   private static final boolean USE_VERSION_OLD_AFFILIATION= false;


    public static final String AFFILIATION_REGISTER=TAG +".AFFILIATION_REGISTER";
    private Presence presenceNow=null;
    private Map<String,Presence> stringPresenceMap;
    private Map<Long,String> longStringMap;
    private OnAffiliationServiceListener onAffiliationServiceListener;
    private MyPublicationAffiliationSession mSessionPublication;
    private Map<String, Long> mGroupsMap;
    private TreeMap<Long, Map<String,String>> mExpiresMap;
    private Handler handlerService;
    private static final boolean checkExpiredActive=false;
    private Presence presenceNowDelay;
    private String pidNowDelay;
    private Map<String, String> expiresNowDelay;
    private CommandList commandListNowDelay;
    private Handler handlerStartAffiliation;
    private Runnable runnableStartAffiliation;
    private ArrayList affiliationGroupDelay;
    private boolean isSubscribed=false;


    @Override
    public boolean start() {
        Log.d(TAG,"Start "+"AffiliationService");
        stringPresenceMap=new HashMap<>();
        isStart=true;
        isSubscribed=false;
        broadcastReceiverAffiliationMessage=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(AFFILIATION_ACTION_MESSAGE)) {
                    Log.d(TAG,"New message received");
                    byte[] messageAffiliation=intent.getByteArrayExtra(AFFILIATION_NEWAFFILIATION_MESSAGE);
                    if(messageAffiliation==null || messageAffiliation.length==0){
                        Log.e(TAG,"Affiliation message not valid.");
                    }else{
                        Log.d(TAG,"Received new affiliation message.");
                        try {
                            CommandList commandList=AffiliationUtils.getCommandList(messageAffiliation);
                            receiveNewSelfAffiliation(commandList);
                        } catch (Exception e) {
                            Log.e(TAG,"Error parsing new message: "+e.toString()+" "+e.getMessage());
                        }

                    }
                }else if (intent.getAction().equals(AFFILIATION_ACTION_NOTIFY)) {
                    Log.d(TAG,"New notify received.");
                    boolean sendAccound=false;
                    byte[] messageAffiliation=intent.getByteArrayExtra(AFFILIATION_NEWAFFILIATION_NOTIFY);
                    Log.d(TAG,"New notify affiliation received.");
                    //try {
                    Presence presence=null;
                    if(messageAffiliation==null || messageAffiliation.length==0){
                        Log.w(TAG,"Affiliation notify not valid or empty.");
                        presence=new Presence();
                    }else{
                        Log.d(TAG,"Valid affiliation notify.");
                        try {
                            if(BuildConfig.DEBUG)Log.d(TAG,"new notify: "+new String(messageAffiliation));
                            presence=AffiliationUtils.getPresence(messageAffiliation);
                        } catch (Exception e) {
                            Log.e(TAG,"Error proccess new affiliation:"+e.getMessage());
                        }
                    }
                    String mcpttID=null;
                    NgnSipPrefrences profile=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
                    if(profile!=null)mcpttID=profile.getMcpttId();
                    if((presence!=null) && (mcpttID!=null) &&
                            (presence.getEntity()!=null) &&
                            (presence.getEntity().trim().compareTo(mcpttID.trim())==0)){
                        setPresenceNow(presence);
                        if(presence.getTuple()!=null && profile!=null){
                            if(!sendAccound){
                                if(presence.getPId()!=null &&
                                        !presence.getPId().isEmpty() &&
                                        stringPresenceMap!=null &&
                                        stringPresenceMap.get(presence.getPId())!=null){

                                    receiveNewPresenceResponse(presence,presence.getPId());
                                }else{
                                    receiveNewPresence(presence);
                                }
                            }else{
                                Log.d(TAG,"No listeners.");
                            }
                        }else{
                            Log.e(TAG,"Error processing affiliation data.");
                        }



                    }else if(presence!=null && mcpttID!=null &&
                            presence.getEntity()==null){
                        setPresenceNow(presence);
                        receiveNewPresence(presence);
                    }else{
                        Log.e(TAG,"Invalid new notify.");
                    }
                    /*
                    } catch (Exception e) {
                        Log.e(TAG,"it isn´t possible to parse the info on sip notify "+e.toString());
                    }
                    */

                }else if (intent.getAction().equals(AFFILIATION_ACTION_SUBSCRIBE)) {
                    Log.d(TAG,"Receive response subscribe");
                    String error=intent.getStringExtra(AFFILIATION_RESPONSE_SUBSCRIBE_ERROR);
                    String responseOk=intent.getStringExtra(AFFILIATION_RESPONSE_SUBSCRIBE_OK);
                    if(error!=null){
                        //Error
                        Log.e(TAG,"Error in subscribe for affiliation "+error);
                        isSubscribed=false;
                    }else if(responseOk!=null){
                        //Ok
                        Log.d(TAG,"Correct subscribe for affiliation");
                        isSubscribed=true;
                        if(affiliationGroupDelay!=null){
                            Log.d(TAG,"Affiliation now to groups");
                            affiliationGroups(context,affiliationGroupDelay);
                            affiliationGroupDelay=null;
                        }

                    }else
                        Log.w(TAG,"This situation isn´t logic");
                }else if (intent.getAction().equals(AFFILIATION_ACTION_UNSUBSCRIBE)) {
                    Log.d(TAG,"UnSubscribe");
                    isSubscribed=false;
                }
            }

        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AFFILIATION_ACTION_MESSAGE);
        intentFilter.addAction(AFFILIATION_ACTION_NOTIFY);
        intentFilter.addAction(AFFILIATION_ACTION_SUBSCRIBE);
        intentFilter.addAction(AFFILIATION_ACTION_UNSUBSCRIBE);
        NgnApplication.getContext().registerReceiver(broadcastReceiverAffiliationMessage,intentFilter);
        return true;
    }





    @Override
    public boolean stop() {
        Log.d(TAG,"Stop "+"AffiliationService");
        stringPresenceMap=null;
        isSubscribed=false;
        unRegister();
        try{
            if(handlerService!=null && runnableStartAffiliation!=null){
                handlerService.removeCallbacks(runnableStartAffiliation);
                runnableStartAffiliation=null;
                handlerService=null;
            }
        }catch (Exception e){
            Log.e(TAG,"Error in stop affiliation");
            e.printStackTrace();
        }

        isStart=false;
        return true;
    }

    public MyAffiliationService() {

    }

    private void affilitionChange(boolean isRegister){
        if(!isRegister){
            if(mSessionSuscription!=null){
                Log.d(TAG,"Unsubscribe affiliation");
                mSessionSuscription.unSubscribeAffiliation();
                mSessionSuscription=null;
            }
        }else{
            mSessionSuscription= MySubscriptionAffiliationSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack());
            if(mSessionSuscription.subscribeAffiliation()){
                Log.d(TAG,"Subscribe sent.");
            }
        }
    }

    //INIT Publication


    public String unAffiliationGroup(Context context,String groupSusbcribe){
        if(context==null || groupSusbcribe==null)return null;
        ArrayList<String> groups=new ArrayList<>();
        groups.add(groupSusbcribe);
        Log.d(TAG,"Group unaffiliation initialized: "+groupSusbcribe);
        return unAffiliationGroups( context,groups);
    }

    public String unAffiliationGroups(Context context,List<String> groupsSusbcribe){
        if(groupsSusbcribe==null || groupsSusbcribe.isEmpty()){
            Log.e(TAG,"Invalid configuration.");
            return null;
        }
        byte[] bytes=null;
        Presence presence=null;
        if(!USE_VERSION_OLD_AFFILIATION){
            presence=generatePif(groupsSusbcribe,context,getPresenceNow(),false);
        }else{
            presence=generatePif(groupsSusbcribe,context);
        }
        if(presence!=null)
        try {
            bytes=AffiliationUtils.getBytesOfPresenceForAffiliation(context,presence);
        } catch (Exception e) {
            Log.e(TAG,"Invalid pidf+xml.");
            return null;
        }
        String impu=null;
        NgnSipPrefrences profileNow=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profileNow!=null)impu=profileNow.getIMPU();
        if(mSessionPublication==null && impu!=null){
            mSessionPublication= MyPublicationAffiliationSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(),impu);
        }else{
            Log.d(TAG,"Publication is different than NULL.");
        }
        boolean result=false;
        if(!USE_VERSION_OLD_AFFILIATION){
            //use new version.

            if(bytes==null){
                result=mSessionPublication.unPublish(bytes,context);
            }else{
                result=mSessionPublication.publish(bytes,context);
            }
        }else{
            result=mSessionPublication.unPublish(bytes,context);

        }
        if(result && stringPresenceMap!=null){
            if(presence!=null){
                stringPresenceMap.put(presence.getPId(),presence);
                return presence.getPId();
            }else{
                return "";
            }
        }else{
            Log.e(TAG,"Error sending publication.");
        }
        return null;
    }


    public String affiliationGroup(Context context,String groupSusbcribe){
        if(context==null || groupSusbcribe==null)return null;
        Log.d(TAG,"Init Affiliation->"+groupSusbcribe);
        ArrayList<String> groups=new ArrayList<>();
        groups.add(groupSusbcribe.trim());
        return affiliationGroups( context,groups);
    }

    public String affiliationGroups(Context context,List<String> groupsSusbcribe){
        if(groupsSusbcribe==null || groupsSusbcribe.isEmpty())return null;

        if(!isSubscribed){
            Log.d(TAG,"Now,it isn´t subcribe");
            if(affiliationGroupDelay==null)affiliationGroupDelay=new ArrayList();
            affiliationGroupDelay.addAll(groupsSusbcribe);
            return null;
        }

        byte[] bytes=null;
        Presence presence=generatePif(groupsSusbcribe,context,getPresenceNow(),true);
        try {
            bytes=AffiliationUtils.getBytesOfPresenceForAffiliation(context,presence);
        } catch (Exception e) {
            Log.e(TAG,"Invalid pidf+xml.");
            return null;
        }

        String impu=null;
        NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profileNow!=null)impu=profileNow.getIMPU();
        if(bytes==null || bytes.length==0)return null;
        if(mSessionPublication==null && impu!=null)
            mSessionPublication= MyPublicationAffiliationSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(),impu);
        boolean result=mSessionPublication.publish(bytes,context);
        if(result){
            stringPresenceMap.put(presence.getPId(),presence);
            return presence.getPId();
        }
        return null;
    }

    /**
     * @param groupsSusbcribe
     * @param context
     * @return
     */
    private Presence generatePif(List<String> groupsSusbcribe,Context context){
       return generatePif(groupsSusbcribe,context,null,true);
    }

    /**
     * Function to create the PIF (generate new pif or use an old pif+xml).
     * @param groupsSusbcribe
     * @param context
     * @param presence
     * @return
     */
    private  Presence generatePif(List<String> groupsSusbcribe,Context context,Presence presence,boolean affiliate){
        if(groupsSusbcribe==null || groupsSusbcribe.isEmpty())return null;
        ArrayList<AffiliationType> affiliationTypes;
        Tuple tuple;
        Status status;

        if(presence!=null && presence.getTuple()!=null &&
                presence.getTuple().size()>0 &&
                (tuple=presence.getTuple().get(0))!=null &&
                (status=tuple.getStatus())!=null &&
                (status.getAffiliations()!=null) && (new ArrayList<>(status.getAffiliations()))!=null
                ){
            Log.d(TAG,"Using the old presence.");
        }else{
            if(presence==null){
                Log.d(TAG,"Generating new presence for affiliation process.");
            }else{
                Log.e(TAG,"Invalid current presence. New one generated.");
            }

        }

        String mcpttID=null;
        Presence presence2=new Presence();
        NgnSipPrefrences profileNow= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profileNow!=null)mcpttID=profileNow.getMcpttId();
        presence2.setEntity(mcpttID);
        tuple=new Tuple();
        String mcpttClientID=null;
        if(profileNow!=null)mcpttClientID=profileNow.getMcpttClientId();
        tuple.setId(mcpttClientID);
        status=new Status();
        affiliationTypes=new ArrayList<>();
        status.setAffiliations(affiliationTypes);
        tuple.setStatus(status);
        ArrayList<Tuple> tuples=new ArrayList<>();
        tuples.add(tuple);
        String pid=mcpttID+Calendar.getInstance().getTimeInMillis();
        presence2.setPId(pid);
        presence2.setTuple(tuples);
        Tuple tupleRemote=null;
        if(affiliate){
            if(presence!=null && presence.getTuple()!=null &&
                    presence.getTuple().size()>0)
            for(Tuple tupleRemote2:presence.getTuple()){
                if(profileNow.getMcpttClientId()!=null && tupleRemote2.getId()!=null && tupleRemote2.getId().compareTo(profileNow.getMcpttClientId())==0){
                    tupleRemote=tupleRemote2;
                }
            }
            if((tupleRemote!=null &&
                    (status=tupleRemote.getStatus())!=null
                    && status.getAffiliations()!=null
                    )){
                affiliationTypes=new ArrayList<>(status.getAffiliations());

            }else{
                affiliationTypes=new ArrayList<>();
            }
            for(String group:groupsSusbcribe){
                if(/*AffiliationUtils.isValidURISIP(group) &&*/ checkGroupStatus(affiliationTypes,group)<0){
                    AffiliationType affiliationType=new AffiliationType();
                    affiliationType.setGroup(group);
                    affiliationTypes.add(affiliationType);
                }
            }
            presence2.getTuple().get(0).getStatus().setAffiliations(createListAffiliationType(affiliationTypes));
        }else{
            //unaffiliate

            if(presence!=null){
                affiliationTypes=new ArrayList<>(presence.getTuple().get(0).getStatus().getAffiliations());
            }else{
                affiliationTypes=new ArrayList<>();
            }
            for(String group:groupsSusbcribe){
                int index=-1;
                if((index=checkGroupStatus(affiliationTypes,group))>=0 && affiliationTypes.size()>index){
                    Log.d(TAG,"unaffiliate");
                    affiliationTypes.remove(index);
                }else{
                    Log.e(TAG,"Error unaffiliating "+index);
                }
            }
            if(affiliationTypes.size()==0){
                Log.d(TAG,"Device does not have any affiliation group.");
                return null;
            }
            presence2.getTuple().get(0).getStatus().setAffiliations(createListAffiliationType(affiliationTypes));
            if(affiliationTypes.size()==0){
                presence2=null;
            }
        }


        return presence2;
    }

    private int checkGroupStatus(ArrayList<AffiliationType> affiliationTypes,String group){
        if(group==null && affiliationTypes==null){
            Log.e(TAG, "Error checking group affiliations parameters.");
            return -1;
        }
        if(affiliationTypes!=null){
            for(int con=0;con<affiliationTypes.size();con++){
                if(affiliationTypes.get(con).getGroup().compareTo(group)==0){
                    return con;
                }
            }
        }

        return -1;

    }

    private  List<AffiliationType> createListAffiliationType( List<AffiliationType> affiliationTypes){
        ArrayList<AffiliationType> result=new ArrayList<>();
        for(AffiliationType affiliationType:affiliationTypes){
            result.add(new AffiliationType(affiliationType.getGroup()));
        }
        return result;
    }

    //END Publication

    //INIT service affiliation


    /**
     * Executed when the service starts
     */
    public void startServiceAffiliation(Context context){
        //Start Service

        if(isAffiliation(context)){
            affilitionChange(true);
        }
    }

    /**
     * Executed when the service starts
     */
    public void startServiceAffiliation(){
        //Start Service
        Log.d(TAG,"Start affiliation service.");
        handlerStartAffiliation = new Handler(Looper.getMainLooper());
        runnableStartAffiliation=new Runnable() {
            @Override
            public void run() {
                if(isAffiliation(NgnApplication.getContext())){
                    affilitionChange(true);
                }
            }
        };
        handlerStartAffiliation.postDelayed(runnableStartAffiliation, DELAY_ACTION_AFFILIATION_MSEC);
    }

    /**
     * Executed when the service stops
     */
    public void stopServiceAffiliation(){
        //Stop Serveice
        //affilitionChange(false);
    }



    private void unRegister(){
        try {
            if(broadcastReceiverAffiliationMessage!=null){
                NgnApplication.getContext().unregisterReceiver(broadcastReceiverAffiliationMessage);
                broadcastReceiverAffiliationMessage=null;
            }
            if(BuildConfig.DEBUG)Log.d(TAG,"Unregisted: broadcastReceiverAffiliationMessage");
        }catch (Exception e){
            Log.e(TAG,"Error1:"+e.getMessage());
        }

    }

    public Presence getPresenceNow() {
        return presenceNow;
    }

    public void setPresenceNow(Presence presenceNow) {
        this.presenceNow = presenceNow;
    }

    public void setOnAffiliationServiceListener(OnAffiliationServiceListener onAffiliationServiceListener){
        this.onAffiliationServiceListener=onAffiliationServiceListener;

    }

    //Init affiliation action
    private void receiveNewPresence(Presence presence) {
        if(BuildConfig.DEBUG)Log.d(TAG,"receiveNewPresence");
        if(onAffiliationServiceListener!=null){
            onAffiliationServiceListener.receiveNewPresence( presence);
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"No define interface affiliation");
        }
    }

    private void receiveNewPresenceResponse(Presence presence, String pid) {
        if(BuildConfig.DEBUG)Log.d(TAG,"receiveNewPresence:"+pid);
        presenceNowDelay=presence;
        pidNowDelay=pid;
        if(onAffiliationServiceListener!=null){
            onAffiliationServiceListener.receiveNewPresenceResponse( presenceNowDelay, pidNowDelay);
        }else{
            if(BuildConfig.DEBUG)Log.d(TAG,"No define interface affiliation");
        }
    }

    private void expireAffiliations(Map<String, String> expires) {
        expiresNowDelay=expires;
        if(onAffiliationServiceListener!=null)onAffiliationServiceListener.expireAffiliations(expiresNowDelay);
    }

    private void receiveNewSelfAffiliation(CommandList commandList) {
        commandListNowDelay=commandList;
        if(onAffiliationServiceListener!=null)onAffiliationServiceListener.receiveNewSelfAffiliation( commandListNowDelay);
    }

    private void startNewService() {
        if(onAffiliationServiceListener!=null)onAffiliationServiceListener.startNewServiceAffiliation();

    }
    //end affiliation action




    //END service affiliation

    //INIT affiliation automatically

    public void processingCommandList(Context context,CommandList commandList){
        if(commandList==null || !AffiliationUtils.isSelfAffiliation(context)){
            return;
        }
        //Send to GUI from the client.
        //Affiliation
        if(commandList.getAffiliate()!=null && commandList.getAffiliate().getGroup()!=null){
            List<String> groups=AffiliationUtils.isValidURIsSIP(new ArrayList<String>(commandList.getAffiliate().getGroup()));
            if(groups!=null)affiliationGroups(context,groups);
        }
        //Deaffiliation
        if(commandList.getDeAffiliate()!=null && commandList.getDeAffiliate().getGroup()!=null){
            List<String> groups=AffiliationUtils.isValidURIsSIP(new ArrayList<String>(commandList.getDeAffiliate().getGroup()));
            if(groups!=null)unAffiliationGroups(context,groups);
        }
    }

    //END affiliation automatically

    //INIT checkExpire
    private void checkExpire(){
        if(isCheckExpiredActive()) {
            handlerService = new Handler();
            handlerService.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isStart && mExpiresMap.size() > 0) {
                        checkExpire();
                    }
                }
            }, DEFAULT_SECOS_BETWEEN_CHECK_EXPIRES * 1000);
            checkExpire(Calendar.getInstance().getTime());
        }
    }
    private void checkExpire(Date date){
        Map<String,String> expires=new HashMap<>();
        expires=checkExpire(date,expires);
        if(expires!=null && expires.size()>0){
           expireAffiliations(expires);
        }
    }

    private Map<String,String> checkExpire(Date date,Map<String,String> expires){
        if(mExpiresMap==null)return null;
        Map.Entry<Long, Map<String,String>> hashMapEntry= mExpiresMap.firstEntry();
        if(hashMapEntry==null)return expires;
        Long time=hashMapEntry.getKey();
        if(date.getTime()>=time){
            Map<String,String> arrayList=hashMapEntry.getValue();
            if(arrayList!=null)arrayList.putAll(expires);
            mExpiresMap.remove(time);
            return checkExpire(date,arrayList);
        }else{
            return expires;
        }
    }
    public void addNewOrChangeTimeExpire(List<AffiliationType> affiliationTypes){
        mExpiresMap=new TreeMap<>();
        if(affiliationTypes==null)return;
        for(AffiliationType  affiliationType:affiliationTypes){
            if(affiliationType!=null && affiliationType.getStatus()== StatusType.affiliated){
                String group=affiliationType.getGroup();
                Date date=affiliationType.getExpiresDate();
                if(group!=null && date!=null){
                    Map<String,String> strings=mExpiresMap.get(date.getTime());
                    if(strings==null){
                        strings=new HashMap<>();
                        mExpiresMap.put(date.getTime(),strings);
                    }
                    strings.put(group,group);
                }

            }
        }
        checkExpire();
    }
    //END checkExpire


    private boolean isCheckExpiredActive() {
        return checkExpiredActive;
    }

    public boolean isAffiliation(Context context){
        NgnSipPrefrences profileNow=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profileNow==null || profileNow.isMcpttIsEnableAffiliation()==null)return false;
        return profileNow.isMcpttIsEnableAffiliation();
    }

    @Override
    public boolean clearService(){
        Log.d(TAG,"Clear:  "+"LocalizationService");
        if(presenceNow!=null)presenceNow=null;
        return true;
    }
}
