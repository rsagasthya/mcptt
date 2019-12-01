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
package org.mcopenplatform.muoapi.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import org.mcopenplatform.muoapi.BuildConfig;

public class PermissionRequestUtils {
    private static final String TAG = Utils.getTAG(PermissionRequestUtils.class.getCanonicalName());
    private static final String PERMISSION_REQUEST_LIST=TAG+"PERMISSION_REQUEST_LIST";
    private static final int PERMISSION_REQUEST_CODE=199;




    public static void requestPermissions(Context context,String[] permission) {
        Intent permIntent = new Intent(context, PermissionActivity.class);
        permIntent.putExtra(PERMISSION_REQUEST_LIST,permission);
        permIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(permIntent);
    }



    public static class PermissionActivity extends Activity {
        ResultReceiver resultReceiver;
        String[] permissions;
        int requestCode;
        private String[] permision;


        @Override
        protected void onStart() {
            super.onStart();
            Intent intent=this.getIntent();
            permision=intent.getStringArrayExtra(PERMISSION_REQUEST_LIST);
            ActivityCompat.requestPermissions(this, permision, PERMISSION_REQUEST_CODE);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            if(BuildConfig.DEBUG)Log.i(TAG,"onRequestPermissionsResult");
            super.onRequestPermissionsResult(requestCode, permissions,grantResults);
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:

                    break;
                default:
                    if(BuildConfig.DEBUG)Log.w(TAG,"receive onRequestPermissionsResult no valid");
                    break;
            }
            finish();
        }

    }
}