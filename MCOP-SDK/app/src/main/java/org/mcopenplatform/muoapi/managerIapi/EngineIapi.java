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

import android.content.Context;
import android.util.Log;

import org.mcopenplatform.muoapi.utils.Utils;
import org.mcopenplatform.muoapi.BuildConfig;


public class EngineIapi {
    private final static String TAG = Utils.getTAG(EngineIapi.class.getCanonicalName());
    private static EngineIapi sInstance;
    private boolean mStarted;
    private ManagerSimService mManagerSimService;
    private ManagerConfigurationService mManagerConfigurationService;
    private ManagerConnectivityService mManagerConnectivityService;
    private ManagerMBMSGroupCom mManagerMBMSGroupCom;

    public static EngineIapi getInstance(){
        if(sInstance == null){
            sInstance = new EngineIapi();
        }
        return sInstance;
    }

    public EngineIapi() {
        if(BuildConfig.DEBUG)Log.d(TAG,"Start IAPI Engine");
    }

    public synchronized boolean start(Context context
            ,String connectivityPluginPackageService
            ,String connectivityPluginPackageMain
            ,String simPluginPackageService
            ,String simPluginPackageMain
            ,String configurationPluginPackageService
            ,String configurationPluginPackageMain
            ,String mbmsPluginPackageService
            ,String mbmsPluginPackageMain
    ){
        if(mStarted){
            if(BuildConfig.DEBUG)Log.e(TAG,"Started");
            return true;
        }
        boolean success = true;
        //TODO: Error to the start the service ConfigurationService
        success &= getConfigurationService().start(context
                ,configurationPluginPackageService
                ,configurationPluginPackageMain
        );
        success &= getConnectivityService().start(context
                ,connectivityPluginPackageService
                ,connectivityPluginPackageMain
        );
        success &= getSimService().start(context
                ,simPluginPackageService
                ,simPluginPackageMain
        );
        success &= getMBMSGroupCom().start(context
                ,mbmsPluginPackageService
                ,mbmsPluginPackageMain
        );
        mStarted = true;
        return success;
    }

    public synchronized boolean isStarted(){
        return mStarted;
    }

    public synchronized boolean stop() {
        if(!mStarted){
            if(BuildConfig.DEBUG)Log.e(TAG,"Stopped");
            return true;
        }
        boolean success = true;
        success &= getConfigurationService().stop();
        success &= getConnectivityService().stop();
        success &= getSimService().stop();
        success &= getMBMSGroupCom().stop();

        if(!success){
            Log.e(TAG, "Failed to stop Services");
        }
        mStarted = false;
        return success;
    }

    public ManagerSimService getSimService(){
        if(mManagerSimService == null){
            mManagerSimService = new ManagerSimService();
        }
        return mManagerSimService;
    }

    public ManagerConfigurationService getConfigurationService(){
        if(mManagerConfigurationService == null){
            mManagerConfigurationService = new ManagerConfigurationService();
        }
        return mManagerConfigurationService;
    }

    public ManagerConnectivityService getConnectivityService(){
        if(mManagerConnectivityService == null){
            mManagerConnectivityService = new ManagerConnectivityService();
        }
        return mManagerConnectivityService;
    }

    public ManagerMBMSGroupCom getMBMSGroupCom(){
        if(mManagerMBMSGroupCom == null){
            mManagerMBMSGroupCom = new ManagerMBMSGroupCom();
        }
        return mManagerMBMSGroupCom;
    }

    public boolean isConnects() {
        if(!mStarted){
            return false;
        }
        boolean success = true;
        success &= getConfigurationService().isConnect();
        success &= getConnectivityService().isConnect();
        success &= getSimService().isConnect();
        success &= getMBMSGroupCom().isConnect();
        return success;
    }
}