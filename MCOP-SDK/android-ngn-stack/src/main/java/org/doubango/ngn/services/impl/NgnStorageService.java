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
import android.util.Log;

import org.doubango.ngn.services.INgnStorageService;
import org.doubango.utils.Utils;

import java.io.File;

/**@page NgnStorageService_page Storage Service
 * This service is used to manage storage functions.
 */

public class NgnStorageService  extends NgnBaseService implements INgnStorageService{
	private final static String TAG = Utils.getTAG(NgnStorageService.class.getCanonicalName());
	
	private final File mCurrentDir;
	private final String mContentShareDir;
	
	public NgnStorageService(Context context){
		mCurrentDir = context.getFilesDir();
		mContentShareDir = "/sdcard/wiPhone";
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
	public File getCurrentDir(){
		return this.mCurrentDir;
	}
	
	@Override
	public String getContentShareDir(){
		return this.mContentShareDir;
	}
	@Override
	public boolean clearService(){
		return true;
	}
}
