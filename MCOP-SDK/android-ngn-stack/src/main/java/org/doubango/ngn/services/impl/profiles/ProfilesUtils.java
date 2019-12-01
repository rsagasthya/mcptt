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
package org.doubango.ngn.services.impl.profiles;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.datatype.profiles.Profiles;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.utils.Utils;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;


class ProfilesUtils {
    private final static String TAG = Utils.getTAG(ProfilesUtils.class.getCanonicalName());
    //INIT utils xml
    private final static String NAME_FILE_PROFILES_RAW="profiles";


    protected static Profiles getProfiles(Context context) throws Exception {
        if(context==null)return null;
        if(BuildConfig.DEBUG)Log.d(TAG,"getProfiles from raw");
        InputStream inputStream=getFileRaw(context,NAME_FILE_PROFILES_RAW);
        if(inputStream==null)return null;
        return getProfiles(inputStream);

    }
    protected static Profiles getProfiles(String string) throws Exception {
        if(BuildConfig.DEBUG)Log.d(TAG,"getProfiles: "+string);
        return getProfiles(string.getBytes());
    }

    protected static Profiles getProfiles(byte[] bytes) throws Exception {
        //InputStream stream = new ByteArrayInputStream(profilesDefault.getBytes());
        return getProfiles(new ByteArrayInputStream(bytes));
        //return getProfiles(stream);
    }
    protected static Profiles getProfiles(InputStream stream) throws Exception {
        if(stream==null )return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        Profiles preferences=serializer.read(Profiles.class,stream);
        return preferences;
    }


    protected static InputStream getOutputStreamOfProfiles(Context context, Profiles profiles) throws Exception {
        if(profiles==null)return null;
        if(context==null){
            Log.e(TAG,"Parameter cannot be null.");
            return null;
        }
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(profiles,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);
        return inputStream;
    }

    protected static byte[] getBytesOfProfiles(Context context, Profiles profiles) throws Exception {
        InputStream inputStream=getOutputStreamOfProfiles(context,profiles);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected  static String  getStringOfProfiles(Context context,Profiles profiles) throws Exception {
        byte[] bytes=getBytesOfProfiles(context,profiles);
        if(bytes==null)return null;
        return new String(bytes).trim();
    }

    protected static NgnSipPrefrences getNgnSipPrefrences(String string) throws Exception {
        return getNgnSipPrefrences(string.getBytes());
    }

    protected static NgnSipPrefrences getNgnSipPrefrences(byte[] bytes) throws Exception {
        return getNgnSipPrefrences(new ByteArrayInputStream(bytes));
    }
    
    private static NgnSipPrefrences getNgnSipPrefrences(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        NgnSipPrefrences preference=serializer.read(NgnSipPrefrences.class,stream);
        return preference;
    }


    private static InputStream getOutputStreamOfNgnSipPrefrences(Context context, NgnSipPrefrences preference) throws Exception {
        if(preference==null)return null;
        if(context==null){
            Log.e(TAG,"Parameter cannot be null.");
            return null;
        }
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(preference,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);
        return inputStream;
    }

    protected static byte[] getBytesNgnSipPrefrences(Context context, NgnSipPrefrences preference) throws Exception {
        InputStream inputStream=getOutputStreamOfNgnSipPrefrences(context,preference);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected  static String  getStringOfNgnSipPrefrences(Context context,NgnSipPrefrences preference) throws Exception {
        byte[] bytes=getBytesNgnSipPrefrences(context,preference);
        if(bytes==null)return null;
        return new String(bytes).trim();
    }
    //END utils xml

    //INIT Utils
    private static byte[] readBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // storage overwritten on each iteration in bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        //
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

    /* Checks if external storage is available to read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    //END Utils

    //INIT files RAW
    private static InputStream getFileRaw(Context context,String nameFileWithoutExtension){
        if(context==null || nameFileWithoutExtension==null || nameFileWithoutExtension.isEmpty())return null;
        InputStream ins = context.getResources().openRawResource(
                context.getResources().getIdentifier(nameFileWithoutExtension,
                        "raw", context.getPackageName()));
        return ins;
    }
    //END files RAW

}
