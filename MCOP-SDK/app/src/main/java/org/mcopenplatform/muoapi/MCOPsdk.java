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

package org.mcopenplatform.muoapi;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.mcopenplatform.muoapi.utils.PermissionRequestUtils;
import org.mcopenplatform.muoapi.utils.Utils;

import java.util.ArrayList;

import static android.support.v4.app.NotificationCompat.PRIORITY_MIN;


/**
 * MCOP SDK Service
 * @version 0.1
 */
public class MCOPsdk extends Service implements ActivityCompat.OnRequestPermissionsResultCallback {
    private final static String TAG = Utils.getTAG(MCOPsdk.class.getCanonicalName());
    private final static int ONGOING_NOTIFICATION_ID=101;
    private Engine engine;
    private Intent startIntent;

    public MCOPsdk() {
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.d(TAG,"MCOPsdk");
        engine=Engine.getInstance();

    }
    @Override
    public void onCreate(){
        super.onCreate();
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.d(TAG,"onCreate "+getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "org.mcopenplatform.muoapi.MCOPsdk";
            String CHANNEL_NAME = "MCOPsdk service";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,NotificationManager.IMPORTANCE_NONE);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationCompat.Builder  notificationBuild=new NotificationCompat.Builder(this,CHANNEL_ID);
            Notification  notification = notificationBuild
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(org.mcopenplatform.muoapi.R.mipmap.ic_launcher)
                    .setPriority(PRIORITY_MIN)
                    .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }else{
            startForeground(1, new Notification());
        }

    }

    /**
     * Link the Service with a client. MCOP Service is started at this point.
     * @param intent data received from the client.
     * @return returns the Binder to be used by the binded client to comunicate with the server using the defined AIDL.
     */
    @Override
    public IBinder onBind(Intent intent) {
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.d(TAG,"onBind packet client: "+getApplicationContext().getPackageName()+" 6");
        Signature[] sigs = new Signature[0];

        try {
            sigs = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES).signatures;
        } catch (PackageManager.NameNotFoundException e) {
            if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.e(TAG,"Error in get sign "+e.getMessage());
        }
        for (Signature sig : sigs)
        {
            if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.i(TAG, "Signature hashcode : "+ sig.hashCode());
        }
        String[] paramets;
        if((paramets=checkPermission(this))!=null && paramets.length>0){
            PermissionRequestUtils.requestPermissions(this,paramets);
        }else{
            if(org.mcopenplatform.muoapi.BuildConfig.DEBUG){
                Log.d(TAG,"The SDK has all the permissions"); }
        }
        if(startIntent != null){
            intent=startIntent;
        }
        return engine.newClient(intent,this,false);
    }

    private static String[] checkPermission(final Context context) {
        ArrayList<String> permissionList=new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
                permissionList.add(Manifest.permission.FOREGROUND_SERVICE);

        String[] permission=new String[permissionList.size()];
        return permissionList.toArray(permission);
    }
    /**
     * Link the Service with a client. MCOP Service is started at this point.
     * @param intent data received from the client.
     * @return returns the Binder to be used by the binded client to comunicate with the server using the defined AIDL
     */
    @Override
    public void onRebind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.d(TAG,"onRebind");
        if(startIntent != null){
            intent=startIntent;
        }
        engine.newClient(intent,this,true);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean result;
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG)Log.d(TAG,"onUnbind");
        if(engine!=null) engine.stopClients();
         return true;
    }

    @Override
    public void onDestroy() {
        if(org.mcopenplatform.muoapi.BuildConfig.DEBUG) Log.d(TAG,"onDestroy");
        engine.onDestroyClients();
       // super.onDestroy();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        startIntent = intent;
        return START_STICKY;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
}
