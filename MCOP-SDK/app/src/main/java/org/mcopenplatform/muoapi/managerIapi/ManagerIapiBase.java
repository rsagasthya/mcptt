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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.mcopenplatform.iapi.McopMessenger;
import org.mcopenplatform.muoapi.BuildConfig;

import static android.content.Context.BIND_AUTO_CREATE;


public abstract class ManagerIapiBase {
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerIapiBase.class.getCanonicalName());
    protected ServiceConnection mConnection;
    protected Object mService;
    protected McopMessenger mcopMessenger;
    protected boolean isConnect;
    protected String PACKET_SERVICE="org.mcopenplatform.iapi.test";
    protected Context mContext;
    protected Handler handler;
    protected OnIapiListener onIapiListener;
    protected boolean changedPacket=false;
    public ManagerIapiBase() {
        mContext=null;
        isConnect=false;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                if(BuildConfig.DEBUG)Log.d(TAG,"Received Message from: "+getPACKET_SERVICE());
                //TODO: receive Message and process data from other Service
                receiveEvent(message);
            }
        };
        mcopMessenger=new McopMessenger(handler);
    }

    public boolean start(Context context
            ,String packetService
            ,String packetMainService
        ) {
        if(packetService!=null){
            setPACKET_SERVICE(packetService);
        }
        if(packetMainService !=null){
            setPACKET_MAIN_SERVICE(packetMainService);
        }
        if(BuildConfig.DEBUG)Log.d(TAG, "Starting..."+getPACKET_SERVICE());
        mContext=context;

        if(mConnection==null)
            mConnection = new ServiceConnection() {

                @Override
                public void onServiceConnected(ComponentName className, IBinder service) {
                    Log.d(TAG,"Service binded! "+getPACKET_SERVICE());
                    mService=registerInterface(service);
                    isConnect=true;
                    isServiceConnected();
                }

                @Override
                public void onServiceDisconnected(ComponentName className) {
                    mService = null;
                    //This method is only invoked when the service quits from the other end or gets killed
                    //Invoking exit() from the AIDL interface makes the Service kill itself, thus invoking this.
                    Log.e(TAG,"Service disconnected "+getPACKET_SERVICE());
                    isConnect=false;
                }

                @Override
                public void onBindingDied(ComponentName name) {
                    Log.e(TAG,"Service died."+getPACKET_SERVICE());
                }
            };

        Intent serviceIntent = new Intent()
                .setComponent(new ComponentName(
                        getPACKET_MAIN_SERVICE(),
                        getPACKET_SERVICE()));
        serviceIntent.setAction(getPACKET_SERVICE());

        try{
            ComponentName componentName=null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                componentName=mContext.startForegroundService(serviceIntent);
            }
            else
                componentName=mContext.startService(serviceIntent);
            if(componentName!=null){
                Log.d(TAG,"Start Service: "+componentName.getPackageName());
            }else{
                if(BuildConfig.DEBUG)Log.e(TAG,"Service Error: "+getPACKET_SERVICE() +" "+getPACKET_MAIN_SERVICE());
            }
        }catch (Exception e){
            if(BuildConfig.DEBUG)Log.w(TAG,"Error in start service: "+e.getMessage());
        }
        Log.d(TAG,"Bind Service "+getPACKET_SERVICE()+":"+context.bindService(serviceIntent, mConnection, BIND_AUTO_CREATE));
        startInternal();
        return true;
    }

    public boolean stop() {
        if(BuildConfig.DEBUG)Log.d(TAG, "Stopping..."+getPACKET_SERVICE());
        if(mContext!=null)
        mContext.unbindService(mConnection);

        isConnect=false;
        stopInternal();
        return true;
    }

    abstract protected void isServiceConnected();

    abstract protected void startInternal();

    abstract protected void stopInternal();

    abstract protected Object registerInterface(IBinder service);

    abstract protected boolean receiveEvent(Message message);


    /**
     * It allows to distinguish between the different PACKET_SERVICE for each of the extended class of ManagerIapiBase
     * @return PACKET_SERVICE constant
     */
    abstract protected String getPACKET_SERVICE();

    abstract protected String getPACKET_MAIN_SERVICE();


    abstract protected void setPACKET_SERVICE(String packetService);

    abstract protected void setPACKET_MAIN_SERVICE(String packetMainService);


    abstract protected boolean checkChangedPacket();





    public boolean isConnect() {
        return isConnect;
    }

    interface OnIapiListener{
        void onIapiError(int codeError,String error);
    }

    public void setOnIapiListener(OnIapiListener onIapiListener){
        this.onIapiListener=onIapiListener;
    }
}