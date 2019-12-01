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

import org.doubango.ngn.datatype.ms.gms.ns.xcap_diff.XcapDiff;
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


class MSUtils {
    private final static String TAG = Utils.getTAG(MSUtils.class.getCanonicalName());



    //INIT utils xml



        //INIT xcap-diff

    protected static XcapDiff getXcapDiff(String string) throws Exception {
        return getXcapDiff(string.getBytes());
    }

    protected static XcapDiff getXcapDiff(byte[] bytes) throws Exception {
        return getXcapDiff(new ByteArrayInputStream(bytes));
    }
    private static XcapDiff getXcapDiff(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(XcapDiff.class,stream);
    }


    private static InputStream getOutputStreamOfXcapDiff(Context context, XcapDiff xcapDiff) throws Exception {
        if(xcapDiff==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(xcapDiff,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfXcapDiff(Context context,XcapDiff xcapDiff) throws Exception {
        InputStream inputStream=getOutputStreamOfXcapDiff(context,xcapDiff);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public   static String  getStringOfXcapDiff(Context context,XcapDiff xcapDiff) throws Exception {
        return new String(getBytesOfXcapDiff(context,xcapDiff)).trim();
    }


        //END xcap-diff


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
