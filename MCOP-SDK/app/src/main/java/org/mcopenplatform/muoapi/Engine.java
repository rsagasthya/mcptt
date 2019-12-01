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

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.mcopenplatform.muoapi.utils.Utils;

import java.util.ArrayList;


public class Engine {

    private final static String TAG = Utils.getTAG(Engine.class.getCanonicalName());
    private static Engine sInstance;
    private static ArrayList<ManagerClient> mClients;
    private static int NUM_MAX_CLIENT=1;

    public static Engine getInstance(){
        if(sInstance == null){
            sInstance = new Engine();
        }
        return sInstance;
    }

    public Engine() {
        mClients=new ArrayList<>();
        Log.d(TAG,"Start engine SDK MCOP");
    }

    protected IBinder newClient(Intent intent,Context context,boolean isRebind){
        IBinder result=null;
        ManagerClient managerClient=null;
        if(mClients.size()==NUM_MAX_CLIENT){
            managerClient=mClients.get(0);
        }if(mClients.size()<NUM_MAX_CLIENT){
            Log.d(TAG,"Create new client");

            String connectivityPluginPackageMain  = intent.getStringExtra(ConstantsMCOP.CONNECTIVITY_PLUGIN_PACKAGE_ID);
            String connectivityPluginPackageService = intent.getStringExtra(ConstantsMCOP.CONNECTIVITY_PLUGIN_SERVICE_ID);
            String simPluginPackageMain  = intent.getStringExtra(ConstantsMCOP.SIM_PLUGIN_PACKAGE_ID);
            String simPluginPackageService = intent.getStringExtra(ConstantsMCOP.SIM_PLUGIN_SERVICE_ID);
            String configurationPluginPackageMain  = intent.getStringExtra(ConstantsMCOP.CONFIGURATION_PLUGIN_PACKAGE_ID);
            String configurationPluginPackageService = intent.getStringExtra(ConstantsMCOP.CONFIGURATION_PLUGIN_SERVICE_ID);
            String mbmsPluginPackageMain = intent.getStringExtra(ConstantsMCOP.MBMS_PLUGIN_PACKAGE_ID);
            String mbmsPluginPackageService = intent.getStringExtra(ConstantsMCOP.MBMS_PLUGIN_SERVICE_ID);
            managerClient=new ManagerClient(context
            ,connectivityPluginPackageService
            ,connectivityPluginPackageMain
            ,simPluginPackageService
            ,simPluginPackageMain
            ,configurationPluginPackageService
            ,configurationPluginPackageMain
            ,mbmsPluginPackageService
            ,mbmsPluginPackageMain
            );
            mClients.add(managerClient);

        }else{
            Log.e(TAG,"The number of users of the server to exceeded the allowed maximum number");
        }

        //Test
        //Only in demo
        String userSelect=null;
        if(managerClient!=null && (userSelect=intent.getStringExtra("PROFILE_SELECT"))!=null && !userSelect.trim().isEmpty()){
            Log.d(TAG,"Select profile:"+userSelect);
            managerClient.selectProfileMCOP2(userSelect);
        }
        if(managerClient!=null)
        result=managerClient.startManagerClient(isRebind);
        return result;
    }

    public boolean stopClients(){
        boolean success=false;
        if(mClients!=null){
            success=true;
            for(ManagerClient managerClient:mClients){
                success &=managerClient.stopManagerClient();
                //mClients.remove(managerClient);
            }
        }
        return success;
    }

    public boolean onDestroyClients(){
        boolean success=false;
        if(mClients!=null){
            success=true;
            for(ManagerClient managerClient:mClients){
                success &=managerClient.onDestroyClient();
                //mClients.remove(managerClient);
            }
        }
        return success;
    }




}
