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
package org.doubango.utils;

import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.sip.NgnSipPrefrences;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Utils {
    private final static String TAG = Utils.getTAG(Utils.class.getCanonicalName());
    private static boolean isDebug=BuildConfig.LOG_SHOW;

    private static final Pattern PATTERN = Pattern.compile(
            "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");



    public static Calendar getCalendarCompilation(){
        Date buildDate = new Date(BuildConfig.TIMESTAMP);
        Calendar cal = Calendar.getInstance();
        cal.setTime(buildDate);
        return cal;
    }



    public static boolean isDeploy(){
        return !isDebug;
    }

    public static String getTAG(String tag){
        if(tag!=null && org.doubango.ngn.BuildConfig.LOG_SHOW){
            return tag;
        }else{
            return org.doubango.ngn.BuildConfig.APPLICATION_ID;
        }

    }

    public static String[] stringToArray(String string){
        if(string==null || string.trim().isEmpty())return null;
        String lines[] = string.split("\\r?\\n");
        return lines;
    }

    public static List<InterfaceAddress> getIPAddress(boolean useIPv4) {
        ArrayList<InterfaceAddress> iPs=new ArrayList<>();
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                for (InterfaceAddress address : intf.getInterfaceAddresses()) {
                    InetAddress inetAddress = address.getAddress();
                    if (inetAddress!=null && !inetAddress.isLoopbackAddress()) {
                        String sAddr=inetAddress.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;
                        if (useIPv4) {
                            if (isIPv4){
                                /*if(address!=null && address.getAddress()!=null &&
                                        address.getAddress().getHostAddress()!=null &&
                                        address.getNetworkPrefixLength()>0)*/
                                    iPs.add(address);
                            }
                        } else {
                            if (!isIPv4) {
                                Log.d(TAG,"The Address ("+address+") IPv6 isn´t use now");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG,"Error getting system IPs");
        } // for now eat exceptions
        return iPs;
    }
    public static boolean checkFormatIp(final String ip) {
        if(ip==null)return false;
        return PATTERN.matcher(ip).matches();
    }


    public static List<String> parseAccountToEntry(List<NgnSipPrefrences.EntryType> entryTypes){
        if(entryTypes==null)return null;
        ArrayList<String> accounts=new ArrayList<>();
        for(NgnSipPrefrences.EntryType entryType:entryTypes){
            //String name, String sipURI, TypeAccount TYPE
            accounts.add(entryType.getUriEntry());
        }
        return accounts;
    }



}
