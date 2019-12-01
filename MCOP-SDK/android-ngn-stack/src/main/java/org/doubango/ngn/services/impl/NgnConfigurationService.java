/*
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source IMSDROID
*  Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of Open Source Doubango Framework.
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
package org.doubango.ngn.services.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.utils.Utils;


public class NgnConfigurationService extends NgnBaseService implements INgnConfigurationService {
	private final static String TAG = Utils.getTAG(NgnConfigurationService.class.getCanonicalName());
	
	private SharedPreferences mSettings;
	private SharedPreferences.Editor mSettingsEditor;
	
	public NgnConfigurationService(){
		final Context applicationContext = NgnApplication.getContext();
		if(applicationContext != null){
			mSettings= PreferenceManager.getDefaultSharedPreferences(NgnApplication.getContext());
			//This is for the older version
			//mSettings = NgnApplication.getContext().getSharedPreferences(NgnConfigurationEntry.SHARED_PREF_NAME, 0);
			mSettingsEditor = mSettings.edit();
		}
	}
	
	@Override
	public boolean start() {
		Log.d(TAG, "Starting...");
		return true;
	}

	@Override
	public boolean stop() {
		Log.d(TAG, "Stopping...");
		return true;
	}

	@Override
	public boolean putString(final String entry, String value, boolean commit) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return false;
		}
		mSettingsEditor.putString(entry.toString(), value);
		if(commit){
			return mSettingsEditor.commit();
		}
		return true;
	}

	@Override
	public boolean putString(final String entry, String value) {
		return putString(entry, value, false);
	}

	@Override
	public boolean putInt(final String entry, int value, boolean commit) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return false;
		}
		mSettingsEditor.putInt(entry.toString(), value);
		if(commit){
			return mSettingsEditor.commit();
		}
		return true;
	}

	@Override
	public boolean putInt(final String entry, int value) {
		return putInt(entry, value, false);
	}

	@Override
	public boolean putFloat(final String entry, float value, boolean commit) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return false;
		}
		mSettingsEditor.putFloat(entry.toString(), value);
		if(commit){
			return mSettingsEditor.commit();
		}
		return true;
	}

	@Override
	public boolean putFloat(final String entry, float value) {
		return putFloat(entry, value, false);
	}

	@Override
	public boolean putBoolean(final String entry, boolean value, boolean commit) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return false;
		}
		mSettingsEditor.putBoolean(entry.toString(), value);
		if(commit){
			return mSettingsEditor.commit();
		}
		return true;
	}

	@Override
	public boolean putBoolean(final String entry, boolean value) {
		return putBoolean(entry, value, false);
	}

	@Override
	public String getString(final String entry, String defaultValue) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return defaultValue;
		}
		try{
			return mSettings.getString(entry.toString(), defaultValue);
		}
		catch(Exception e){
			e.printStackTrace();
			return defaultValue;
		}
	}

	@Override
	public int getInt(final String entry, int defaultValue) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return defaultValue;
		}
		try{
			return mSettings.getInt(entry.toString(), defaultValue);
		}
		catch(Exception e){

			//String to Integer
			try{
				String data=getString(entry.toString(),String.valueOf(defaultValue));
				if(data!=null && !data.trim().isEmpty()){
					return Integer.valueOf(data);
				}
			}catch(Exception e1){
				e.printStackTrace();
				e1.printStackTrace();
				return defaultValue;
			}
			return defaultValue;
		}
	}

	@Override
	public float getFloat(final String entry, float defaultValue) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return defaultValue;
		}
		try{
			return mSettings.getFloat(entry.toString(), defaultValue);
		}
		catch(Exception e){
			e.printStackTrace();
			return defaultValue;
		}
	}

	@Override
	public boolean getBoolean(final String entry, boolean defaultValue) {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return defaultValue;
		}
		try{
			return mSettings.getBoolean(entry.toString(), defaultValue);
		}
		catch(Exception e){
			e.printStackTrace();
			return defaultValue;
		}
	}
	
	@Override
	public boolean commit() {
		if(mSettingsEditor == null){
			Log.e(TAG,"Settings are null.");
			return false;
		}
		return mSettingsEditor.commit();
	}
	@Override
	public boolean clearService(){
		return true;
	}
}
