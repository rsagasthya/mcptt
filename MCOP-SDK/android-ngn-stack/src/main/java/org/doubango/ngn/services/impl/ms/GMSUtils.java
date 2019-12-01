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



package org.doubango.ngn.services.impl.ms;


import android.content.Context;

import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;
import org.doubango.ngn.datatype.ms.gms.ns.resource_lists.ResourceLists;
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

class GMSUtils {

    //INIT group

    protected static Group getGroupConfiguration(String string) throws Exception {
        return getGroupConfiguration(string.getBytes());
    }

    private static Group getGroupConfiguration(byte[] bytes) throws Exception {
        return getGroupConfiguration(new ByteArrayInputStream(bytes));
    }
    private static Group getGroupConfiguration(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(Group.class,stream);
    }


    private static InputStream getOutputStreamOfGroupConfiguration(Context context, Group groupConfiguration) throws Exception {
        if(groupConfiguration==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(groupConfiguration,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesOfGroupConfiguration(Context context,Group groupConfiguration) throws Exception {
        InputStream inputStream=getOutputStreamOfGroupConfiguration(context,groupConfiguration);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public  static String  getStringOfGroupConfiguration(Context context,Group groupConfiguration) throws Exception {
        return new String(getBytesOfGroupConfiguration(context,groupConfiguration)).trim();
    }
    //END group

    //INIT resoult-list

    protected static ResourceLists getResourceLists(String string) throws Exception {
        return getResourceLists(string.getBytes());
    }

    private static ResourceLists getResourceLists(byte[] bytes) throws Exception {
        return getResourceLists(new ByteArrayInputStream(bytes));
    }
    private static ResourceLists getResourceLists(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        return serializer.read(ResourceLists.class,stream);
    }


    private static InputStream getOutputStreamOfResourceLists(Context context, ResourceLists resourceLists) throws Exception {
        if(resourceLists==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(resourceLists,outputFile);
        return new FileInputStream(outputFile);
    }

    private static byte[] getBytesStreamOfResourceLists(Context context,ResourceLists resourceLists) throws Exception {
        InputStream inputStream=getOutputStreamOfResourceLists(context,resourceLists);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    public  static String  getStringOfResourceLists(Context context,ResourceLists resourceLists) throws Exception {
        return new String(getBytesStreamOfResourceLists(context,resourceLists)).trim();
    }
    //END resoult-list

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
}
