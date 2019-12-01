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
package org.doubango.ngn.services.impl.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.doubango.ngn.services.preference.IPreferencesManager;
import org.doubango.utils.Utils;


public class PreferencesManager implements IPreferencesManager {
    protected static String TAG = Utils.getTAG(PreferencesManager.class.getCanonicalName());
    private  String PREFERENCE_ID;



    SharedPreferences sharedPref;
    public PreferencesManager(String preference_ID){
        PREFERENCE_ID=preference_ID;

    }

    private SharedPreferences createSharedPreferences(Context context){
        return createSharedPreferences(context, PREFERENCE_ID);
    }

    private SharedPreferences createSharedPreferences(Context context,String preference_ID){
        if(context==null){
            return null;
        }
        this.PREFERENCE_ID=preference_ID;
        sharedPref = context.getSharedPreferences(preference_ID, Context.MODE_PRIVATE);
        return sharedPref;
    }

    /*
    public static PreferencesManager getInstance(){
        if(mPreferencesManager==null){
            mPreferencesManager=new PreferencesManager();
        }
        return mPreferencesManager;
    }
    */

    public boolean putString(Context context,String key,String data){
        if(context==null || key==null){
            Log.e(TAG, "Some parameter is nullpoint in putString");
            return false;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        if(data==null){
            editor.remove(key);
            editor.apply();
            return true;
        }else{
            editor.putString(key, data);
            return editor.commit();
        }



    }

    public String getString(Context context,String key){
        return getString(context, key, STRING_DEFAULT);

    }

    public String getString(Context context,String key,String defaultString){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in getString");
            return null;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        return createSharedPreferences(context).getString(key, defaultString);

    }

    public boolean putInt(Context context,String key,Integer data){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in putString");
            return false;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        if(data==null){
            editor.remove(key);
            editor.apply();
            return true;
        }else{
            editor.putInt(key, data);
            return editor.commit();
        }

    }

    public int getInt(Context context,String key){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in getString");
            return -1;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        return createSharedPreferences(context).getInt(key, -1);

    }

    public boolean putLong(Context context,String key,Long data){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in putLong");
            return false;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        if(data==null){
            editor.remove(key);
            editor.apply();
            return true;
        }else {
            editor.putLong(key, data);
            return editor.commit();
        }
    }

    public long getLong(Context context,String key){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in getLong");
            return -1;
        }
        return createSharedPreferences(context).getLong(key, -1);

    }


    public boolean putFloat(Context context,String key,Float data){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in putFloat");
            return false;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        if(data==null){
            editor.remove(key);
            editor.apply();
            return true;
        }else {
            editor.putFloat(key, data);
            return editor.commit();
        }
    }

    public float getFloat(Context context,String key){
        if(context==null || key==null){
            Log.e(TAG,"Some parameter is nullpoint in getLong");
            return -1;
        }
        SharedPreferences.Editor editor = createSharedPreferences(context).edit();
        return createSharedPreferences(context).getFloat(key, -1);

    }

}
