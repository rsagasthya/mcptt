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
package org.doubango.ngn.services.impl.affiliation;

import android.content.Context;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.affiliation.affiliationcommand.CommandList;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnStringUtils;
import org.doubango.ngn.utils.NgnUriUtils;
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
import java.util.List;


class AffiliationUtils {
    private final static String TAG = Utils.getTAG(AffiliationUtils.class.getCanonicalName());

    //INIT PRESENCE
    protected static Presence getPresence(String string) throws Exception {
        return getPresence(string.getBytes());
    }

    protected static Presence getPresence(byte[] bytes) throws Exception {
        return getPresence(new ByteArrayInputStream(bytes));
    }
    private static Presence getPresence(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        Presence presence=serializer.read(Presence.class,stream);
        return presence;
    }


    private static InputStream getOutputStreamOfPresenceForAffiliation(Context context, Presence presence) throws Exception {
        if(presence==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(presence,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);
        return inputStream;
    }

    protected static byte[] getBytesOfPresenceForAffiliation(Context context,Presence presence) throws Exception {
        if(presence==null)return null;
        InputStream inputStream=getOutputStreamOfPresenceForAffiliation(context,presence);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected   static String  getStringOfPresenceForAffiliation(Context context,Presence presence) throws Exception {
        return new String(getBytesOfPresenceForAffiliation(context,presence)).trim();
    }
    //END PRESENCE
    //INIT COMMAND

    protected static CommandList getCommandList(String string) throws Exception {
        return getCommandList(string.getBytes());
    }

    protected static CommandList getCommandList(byte[] bytes) throws Exception {
        return getCommandList(new ByteArrayInputStream(bytes));
    }
    private static CommandList getCommandList(InputStream stream) throws Exception {
        if(stream==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        CommandList CommandList=serializer.read(CommandList.class,stream);
        return CommandList;
    }


    private static InputStream getOutputStreamOfCommandListForAffiliation(Context context, CommandList CommandList) throws Exception {
        if(CommandList==null)return null;
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        File outputDir = context.getCacheDir(); // context being the Activity pointer
        File outputFile = File.createTempFile(String.valueOf(Calendar.getInstance().getTimeInMillis()), "txt", outputDir);
        serializer.write(CommandList,outputFile);
        InputStream inputStream = new FileInputStream(outputFile);
        return inputStream;
    }

    private static byte[] getBytesOfCommandListForAffiliation(Context context,CommandList CommandList) throws Exception {
        InputStream inputStream=getOutputStreamOfCommandListForAffiliation(context,CommandList);
        if(inputStream==null)return null;
        return readBytes(inputStream);
    }

    protected   static String  getStringOfCommandListForAffiliation(Context context,CommandList CommandList) throws Exception {
        return new String(getBytesOfCommandListForAffiliation(context,CommandList)).trim();
    }

    //END COMMAND
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


    protected static boolean isValidURISIP(String uriSIP){
        if(uriSIP==null || uriSIP.trim().isEmpty() || NgnStringUtils.isNullOrEmpty(uriSIP) || !NgnUriUtils.isValidSipUri(uriSIP))return false;
        return true;
    }

    protected static List<String> isValidURIsSIP(ArrayList<String> urisSIP){
        if(urisSIP==null)return urisSIP;
        for(int con=0;con<urisSIP.size();con++){
            if(!isValidURISIP(urisSIP.get(con))){
                urisSIP.remove(urisSIP.get(con));
            }
        }
        return urisSIP;
    }



    protected static boolean isSelfAffiliation(Context context){
        NgnSipPrefrences profileNow=NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profileNow==null || profileNow.isMcpttIsSelfAffiliation()==null)return false;
        return profileNow.isMcpttIsSelfAffiliation();
    }

}
