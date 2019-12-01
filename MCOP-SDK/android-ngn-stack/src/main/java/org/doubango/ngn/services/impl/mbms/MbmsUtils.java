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

package org.doubango.ngn.services.impl.mbms;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.doubango.ngn.datatype.mbms.McpttMbmsUsageInfoType;
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


class MbmsUtils {
    private final static String TAG = Utils.getTAG(MbmsUtils.class.getCanonicalName());



    //INIT utils xml

    protected static McpttMbmsUsageInfoType getMcpttMbmsUsageInfoType(@NonNull String string){
        return getMcpttMbmsUsageInfoType(string.getBytes());
    }

    protected static McpttMbmsUsageInfoType getMcpttMbmsUsageInfoType(byte[] bytes) {
        return getMcpttMbmsUsageInfoType(new ByteArrayInputStream(bytes));
    }
    private static McpttMbmsUsageInfoType getMcpttMbmsUsageInfoType(InputStream stream)  {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        McpttMbmsUsageInfoType mcpttMbmsUsageInfoType=null;
        try{
            mcpttMbmsUsageInfoType=serializer.read(McpttMbmsUsageInfoType.class,stream);
        }catch (Exception e){
            //System.out.println("Error in: "+e.toString());
            Log.e(TAG,"Error in :"+e.toString());
        }

        return mcpttMbmsUsageInfoType;
    }

    private static InputStream getOutputStreamOfMcpttMbmsUsageInfoType(@NonNull Context context, McpttMbmsUsageInfoType mcpttMbmsUsageInfoType) throws Exception {
        if(mcpttMbmsUsageInfoType==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(mcpttMbmsUsageInfoType,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);

        return inputStream;
    }

    private static byte[] getBytesOfMcpttMbmsUsageInfoType(@NonNull Context context,McpttMbmsUsageInfoType mcpttMbmsUsageInfoType) throws Exception {
        InputStream inputStream=getOutputStreamOfMcpttMbmsUsageInfoType(context,mcpttMbmsUsageInfoType);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected static String  getStringOfMcpttMbmsUsageInfoType(@NonNull Context context, McpttMbmsUsageInfoType mcpttMbmsUsageInfoType) throws Exception {
        return new String(getBytesOfMcpttMbmsUsageInfoType(context,mcpttMbmsUsageInfoType)).trim();
    }

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

}
