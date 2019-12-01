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

package org.doubango.ngn.services.impl.ms;


import android.content.Context;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.datatype.ms.cms.CMSData;
import org.doubango.ngn.datatype.ms.cms.CMSDatas;
import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.McpttUEInitialConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
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
import java.util.ArrayList;
import java.util.Calendar;


class CMSUtils {
    private final static String TAG = Utils.getTAG(CMSUtils.class.getCanonicalName());
    private final static String NAME_FILE_MCPTT_UE_INIT_CONFIG="mcpttueinitconf";



    //INIT utils xml




        //INIT service-configuration-info

    protected static ServiceConfigurationInfoType getMcpttServiceConfigurationInfoType(String string) throws Exception {
        return getMcpttServiceConfigurationInfoType(string.getBytes());
    }

    private static ServiceConfigurationInfoType getMcpttServiceConfigurationInfoType(byte[] bytes) throws Exception {
        return getMcpttServiceConfigurationInfoType(new ByteArrayInputStream(bytes));
    }
    private static ServiceConfigurationInfoType getMcpttServiceConfigurationInfoType(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(ServiceConfigurationInfoType.class,stream);
    }


    private static InputStream getOutputStreamOfMcpttServiceConfigurationInfoType(Context context, ServiceConfigurationInfoType serviceConfigurationInfoType) throws Exception {
        if(serviceConfigurationInfoType==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(serviceConfigurationInfoType,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMcpttServiceConfigurationInfoType(Context context,ServiceConfigurationInfoType serviceConfigurationInfoType) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttServiceConfigurationInfoType(context,serviceConfigurationInfoType);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public   static String  getStringOfMcpttServiceConfigurationInfoType(Context context,ServiceConfigurationInfoType serviceConfigurationInfoType) throws Exception {
        return new String(getBytesOfMcpttServiceConfigurationInfoType(context,serviceConfigurationInfoType)).trim();
    }


        //END service-configuration-info
        //INIT mcptt-UE-configuration

    protected static McpttUEConfiguration getMcpttUEConfiguration(String string) throws Exception {
        return getMcpttUEConfiguration(string.getBytes());
    }

    private static McpttUEConfiguration getMcpttUEConfiguration(byte[] bytes) throws Exception {
        return getMcpttUEConfiguration(new ByteArrayInputStream(bytes));
    }
    private static McpttUEConfiguration getMcpttUEConfiguration(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(McpttUEConfiguration.class,stream);
    }


    private static InputStream getOutputStreamOfMcpttUEConfiguration(Context context, McpttUEConfiguration mcpttUEConfiguration) throws Exception {
        if(mcpttUEConfiguration==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mcpttUEConfiguration,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMcpttUEConfiguration(Context context,McpttUEConfiguration mcpttUEConfiguration) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttUEConfiguration(context,mcpttUEConfiguration);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public  static String  getStringOfMcpttUEConfiguration(Context context,McpttUEConfiguration mcpttUEConfiguration) throws Exception {
        return new String(getBytesOfMcpttUEConfiguration(context,mcpttUEConfiguration)).trim();
    }
        //END mcptt-UE-configuration


    //INIT cms-data

    protected static CMSDatas getCMSDatas(String string) throws Exception {
        return getCMSDatas(string.trim().getBytes());
    }

    private static CMSDatas getCMSDatas(byte[] bytes) throws Exception {
        return getCMSDatas(new ByteArrayInputStream(bytes));
    }
    private static CMSDatas getCMSDatas(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(CMSDatas.class,stream);
    }


    protected static CMSData getCMSData(String string) throws Exception {
        return getCMSData(string.trim().getBytes());
    }

    private static CMSData getCMSData(byte[] bytes) throws Exception {
        return getCMSData(new ByteArrayInputStream(bytes));
    }
    private static CMSData getCMSData(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(CMSData.class,stream);
    }


    private static InputStream getOutputStreamOfCMSData(Context context, CMSDatas cmsData) throws Exception {
        if(cmsData==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(cmsData,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfCMSData(Context context,CMSDatas cmsData) throws Exception {
        InputStream inputStream=getOutputStreamOfCMSData(context,cmsData);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected   static String  getStringOfCMSData(Context context,CMSDatas cmsData) throws Exception {
        return new String(getBytesOfCMSData(context,cmsData)).trim();
    }

    private static InputStream getOutputStreamOfCMSData(Context context, CMSData cmsData) throws Exception {
        if(cmsData==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(cmsData,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfCMSData(Context context,CMSData cmsData) throws Exception {
        InputStream inputStream=getOutputStreamOfCMSData(context,cmsData);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected   static String  getStringOfCMSData(Context context,CMSData cmsData) throws Exception {
        return new String(getBytesOfCMSData(context,cmsData)).trim();
    }
    //END cms-data


    protected static Object getMcpttCMSData(String string,RestService.ContentTypeData contentTypeData){
        ArrayList<String> errorString = new ArrayList<>();
        if(string!=null && !string.isEmpty()){
            Object result=null;
            try{
                switch (contentTypeData) {
                    case CONTENT_TYPE_MCPTT_EU_INIT_CONFIG:
                        result= getMcpttUEInitialConfiguration(string.trim());
                    break;
                    case CONTENT_TYPE_MCPTT_EU_CONFIG:
                        result=getMcpttUEConfiguration(string.trim());
                        break;
                    case CONTENT_TYPE_MCPTT_USER_PROFILE:
                        result=getMcpttUserProfile(string.trim());
                        break;
                    case CONTENT_TYPE_MCPTT_SERVICE_CONFIG:
                        result=getMcpttServiceConfigurationInfoType(string.trim());
                        break;
                    case CONTENT_TYPE_MCPTT_GROUPS:
                    case CONTENT_TYPE_NONE:
                    default:
                        Log.e(TAG,"Error in the content-type received");
                        break;
                }
            }catch (Exception ex){
                Log.e(TAG,"Error translating data from CMS:"+ex.getMessage()+(BuildConfig.DEBUG?(" string:"+string):""));
            }
            return result;
        }else if(string!=null){
            Log.e(TAG,"Error parsing data from CMS. Data:"+string);
        }else{
            Log.e(TAG,"Error parsing data from CMS.");
        }
        for (String error:errorString){
            Log.e(TAG,"Error translating data from CMS: " + error);
        }

        return null;
    }

        //INIT mcptt-UE-initial-configuration

    protected static McpttUEInitialConfiguration getMcpttUEInitialConfiguration(Context context) throws Exception {
        if(context==null)return null;
        InputStream inputStream=getFileRaw(context,NAME_FILE_MCPTT_UE_INIT_CONFIG);
        if(inputStream==null)return null;
        return getMcpttUEInitialConfiguration(inputStream);

    }

    protected static McpttUEInitialConfiguration getMcpttUEInitialConfiguration(String string) throws Exception {
        return getMcpttUEInitialConfiguration(string.getBytes());
    }

    private static McpttUEInitialConfiguration getMcpttUEInitialConfiguration(byte[] bytes) throws Exception {
        return getMcpttUEInitialConfiguration(new ByteArrayInputStream(bytes));
    }
    private static McpttUEInitialConfiguration getMcpttUEInitialConfiguration(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(McpttUEInitialConfiguration.class,stream);
    }


    private static InputStream getOutputStreamOfMcpttUEInitialConfiguration(Context context, McpttUEInitialConfiguration mcpttUEInitialConfiguration) throws Exception {
        if(mcpttUEInitialConfiguration==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mcpttUEInitialConfiguration,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMcpttUEInitialConfiguration(Context context,McpttUEInitialConfiguration mcpttUEInitialConfiguration) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttUEInitialConfiguration(context,mcpttUEInitialConfiguration);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected   static String  getStringOfMcpttUEInitialConfiguration(Context context,McpttUEInitialConfiguration mcpttUEInitialConfiguration) throws Exception {
        return new String(getBytesOfMcpttUEInitialConfiguration(context,mcpttUEInitialConfiguration)).trim();
    }
        //END mcptt-UE-initial-configuration


    //INIT mcptt-user-profile

    protected static McpttUserProfile getMcpttUserProfile(String string) throws Exception {
        return getMcpttUserProfile(string.getBytes());
    }

    private static McpttUserProfile getMcpttUserProfile(byte[] bytes) throws Exception {
        return getMcpttUserProfile(new ByteArrayInputStream(bytes));
    }
    private static McpttUserProfile getMcpttUserProfile(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(McpttUserProfile.class,stream);
    }


    private static InputStream getOutputStreamOfMcpttUserProfile(Context context, McpttUserProfile mcpttUserProfile) throws Exception {
        if(mcpttUserProfile==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mcpttUserProfile,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfMcpttUserProfile(Context context, McpttUserProfile mcpttUserProfile) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttUserProfile(context,mcpttUserProfile);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public  static String  getStringOfMcpttUserProfile(Context context, McpttUserProfile mcpttUserProfile) throws Exception {
        return new String(getBytesOfMcpttUserProfile(context,mcpttUserProfile)).trim();
    }
    //END mcptt-user-profile





    private static byte[] readBytes(InputStream inputStream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();


        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];


        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }


        return byteBuffer.toByteArray();
    }
    //END utils xml

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
