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

package org.mcopenplatform.muoapi.managerIapi;

import android.net.Uri;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.mcopenplatform.iapi.Constants;
import org.mcopenplatform.iapi.ISimService;
import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.R;


public class ManagerSimService extends ManagerIapiBase{
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerSimService.class.getCanonicalName());

    //TODO: slotSIM must be defined by capabilities
    protected static int slotSIMCurrent= Constants.Sim.SimSlot.SLOT_ID_2;
    //TODO: SimApp must be defined by capabilities
    //IMP: Tested cards use USIM
    protected static int simAppCurrent= Constants.Sim.SimApp.USIM;
    //TODO: SimAuth must be defined by capabilities
    protected static int simAuthCurrent= Constants.Sim.SimAuth.AKA;

    public static final int ERROR_CONFIGURATION=104;

    protected String PACKET_SERVICE="org.mcopenplatform.iapi.SimService";
    protected String PACKET_MAIN_SERVICE="org.mcopenplatform.iapi";
    private OnSimServiceListener onSimServiceListener;

    public ManagerSimService() {
        super();
    }

    @Override
    protected void isServiceConnected() {
        startConfigurate();
    }

    @Override
    protected void startInternal() {

    }

    @Override
    protected void stopInternal() {

    }

    @Override
    protected Object registerInterface(IBinder service) {
        ISimService serviceInterface = ISimService.Stub.asInterface(service);
        Log.d(TAG,"Register notification in "+getPACKET_SERVICE());
        try {
            (serviceInterface).registerNotificationReceiver(mcopMessenger);
        } catch (RemoteException e) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in registerInterface in ManagerSimService:"+e.getMessage());
        }catch (Exception e) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in registerInterface in ManagerSimService:"+e.getMessage());
        }
        return serviceInterface;
    }

    @Override
    protected boolean receiveEvent(Message message) {
        if(BuildConfig.DEBUG)
            Log.d(TAG,"Execute receiveEvent in "+getPACKET_SERVICE()+": what: "+message.what);
        try {
            int error=((ISimService)mService).getErrorCode();
            String errorString=((ISimService)mService).getErrorStr();
            Log.e(TAG,"Error "+getPACKET_SERVICE()+": "+error+" \""+errorString+"\"");
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in ISimService"+" code:"+error+" string:"+errorString);
        }catch (RemoteException e) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in getErrorStr in ManagerSimService:"+e.getMessage());
        }catch (Exception e) {
            if(BuildConfig.DEBUG)Log.e(TAG,"Error in getErrorStr in ManagerSimService:"+e.getMessage());
        }
        return false;
    }

    /**
     * It allows to distinguish between the different PACKET_SERVICEs for each of the extended class of ManagerIapiBase
     * @return PACKET_SERVICE constant
     */
    @Override
    protected String getPACKET_SERVICE() {
        return PACKET_SERVICE;
    }

    @Override
    protected String getPACKET_MAIN_SERVICE() {
        return PACKET_MAIN_SERVICE;
    }


    @Override
    protected void setPACKET_SERVICE(String packetService) {
        changedPacket=true;
        PACKET_SERVICE = packetService;
    }

    @Override
    protected void setPACKET_MAIN_SERVICE(String packetMainService) {
        changedPacket=true;
        PACKET_MAIN_SERVICE = packetMainService;
    }
    @Override
    protected boolean checkChangedPacket(){
        return changedPacket;
    }



    protected boolean startConfigurate(){
        if(mService!=null){
            try {
                //TODO: Not sure if pcscf and impu should be used at all the times
                String[] pcscfSlot=getPcscf();
                String[] impu=getImpu();
                String impi=getImpi();
                String domain=getDomain();
                String imei=getImei();
                String imsi=getImsi();
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"Configuration client:");
                    if(pcscfSlot!=null && pcscfSlot.length>0) Log.d(TAG,"pcscf: "+pcscfSlot[0]);
                    if(impu!=null && impu.length>0) Log.d(TAG,"impu: "+impu[0]);
                    if(impi!=null) Log.d(TAG,"impi: "+impi);
                    if(domain!=null) Log.d(TAG,"domain: "+domain);
                    if(imei!=null) Log.d(TAG,"imei: "+imei);
                    if(imsi!=null) Log.d(TAG,"imsi: "+imsi);
                }
                int length=pcscfSlot!=null?pcscfSlot.length:0;
                String[] pcscfHosts=new String[length];
                int[] pcscfPorts=new int[length];

                for(int con=0;con<(length);con++){
                    Uri pcscfUri;
                    if(pcscfSlot[con]!=null && (pcscfUri=Uri.parse((pcscfSlot[con].compareToIgnoreCase("sip://")!=0)?("sip://"+pcscfSlot[con]):pcscfSlot[con]))!=null){
                        pcscfHosts[con]=pcscfUri.getHost();
                        pcscfPorts[con]=pcscfUri.getPort();
                    }else{
                        if(BuildConfig.DEBUG)Log.e(TAG,pcscfSlot[con]+ "does not have the correct format");
                    }
                }

                //TODO: process pcscf to port
                if(onSimServiceListener!=null)onSimServiceListener.onConfiguration(impu,impi,domain,pcscfHosts,pcscfPorts,imsi,imei);
            }catch (RemoteException e) {
                if(BuildConfig.DEBUG)Log.e(TAG,"Error in startConfigurate in ManagerSimService:"+e.getMessage());
                if(onIapiListener!=null)onIapiListener.onIapiError(ERROR_CONFIGURATION,mContext.getString(R.string.Not_be_connected_with_the_service)+getPACKET_SERVICE());
            }catch (Exception e) {
                if(BuildConfig.DEBUG)Log.e(TAG,"Error in startConfigurate in ManagerSimService:"+e.getMessage());
                if(onIapiListener!=null)onIapiListener.onIapiError(ERROR_CONFIGURATION,mContext.getString(R.string.Not_be_connected_with_the_service)+getPACKET_SERVICE());

            }

        }else{
            if(onIapiListener!=null)onIapiListener.onIapiError(ERROR_CONFIGURATION,mContext.getString(R.string.Error_when_accessing_service)+getPACKET_SERVICE());
            return false;
        }
        return true;
    }

    //START METHOD
    private String[] getPcscf() throws RemoteException {
        String[] pcscfs=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getPcscf Error");
            return null;
        }else{
            pcscfs=((ISimService)mService).getPcscf(slotSIMCurrent);
            String pcscfString=new String();
            if(pcscfs!=null && pcscfs.length>0)
                for(String pscrf:pcscfs){
                    pcscfString+="\n"+pscrf;
                }
            if(BuildConfig.DEBUG)Log.i(TAG,"response getPcscf(): "+pcscfString);
        }
        return pcscfs;
    }

    private String[] getImpu() throws RemoteException {
        String[] impus=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImpu Error");
            return null;
        }else{
            impus=((ISimService)mService).getImpu(slotSIMCurrent);
            String impusString=new String();
            if(impus!=null && impus.length>0)
            for(String impu:impus){
                impusString+="\n"+impu;
            }
            if(BuildConfig.DEBUG)Log.i(TAG,"response getImpu(): "+impusString);
        }

        return ((ISimService)mService).getImpu(slotSIMCurrent);
    }

    private String getImsi() throws RemoteException {
        String imsi=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImsi Error");
            return null;
        }else{
            imsi=((ISimService)mService).getSubscriberIdentity(slotSIMCurrent);
            if(BuildConfig.DEBUG)Log.i(TAG,"response getSubscriberIdentity():"+imsi);
        }
        return imsi;
    }

    private String getImei() throws RemoteException {
        String imei=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImei Error");
            return null;
        }else{
            imei=((ISimService)mService).getDeviceIdentity(slotSIMCurrent);
            if(BuildConfig.DEBUG)Log.i(TAG,"response  getDeviceIdentity(): "+imei);
        }
        return imei;
    }

    private String getImpi() throws RemoteException {
        String impi=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImpi Error");
            return null;
        }
        else{
            impi=((ISimService)mService).getImpi(slotSIMCurrent);
            if(BuildConfig.DEBUG)Log.i(TAG,"response  getImpi(): "+impi);
        }
        return impi;
    }

    private String getDomain() throws RemoteException {
        String domain=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getDomain Error");
            return null;
        }else{
            domain=((ISimService)mService).getDomain(slotSIMCurrent);
            if(BuildConfig.DEBUG)Log.i(TAG,"response  getDomain(): "+domain);
        }
        return domain;
    }

    public String getAuthentication(String data) throws RemoteException {
        String response=null;
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getAuthentication Error");
            return null;
        }else{
            if(BuildConfig.DEBUG)Log.i(TAG,"Executing getAuthentication: "+data);
        }
        response=((ISimService)mService).getAuthentication(slotSIMCurrent,simAppCurrent,simAuthCurrent,data);
        if(BuildConfig.DEBUG)Log.i(TAG,"response getAuthentication(): "+response);
        return response;
    }
    //END METHOD

    public interface OnSimServiceListener{
        void onConfiguration(
                final String[] impu,
                final String impi,
                final String domain,
                final String pcscf[],
                final int[] pcscfPort,
                final String imsi,
                final String imei
        );
    }

    public void setOnSimServiceListener(OnSimServiceListener onSimServiceListener){
        this.onSimServiceListener=onSimServiceListener;
    }
}