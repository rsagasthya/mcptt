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




package org.doubango.ngn.datatype.mbms;


import android.util.Log;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.math.BigInteger;

import static android.content.ContentValues.TAG;


@Root(strict=false, name = "announcement")
public class AnnouncementTypeParams {

    @Element(name="TMGI")
    protected String tmgi;
    @Element(required = false, name = "QCI")
    protected BigInteger qci;
    @Element(required = false, name = "frequency")
    protected String frequency;
    @Element(required = false, name = "mbms-service-areas")
    protected String mbmsServiceAreas;


    public String getTMGI() {
        return tmgi;
    }


    public BigInteger getTMGIBigInteger() {
        try{
            return new BigInteger(tmgi, 16);
        }catch (Exception ex){
            Log.e(TAG,"Error get Data service area :"+ex.getMessage());
        }
        return BigInteger.valueOf(-1);
    }


    public void setTMGI(String value) {
        this.tmgi = value;
    }


    public BigInteger getQCI() {
        return qci;
    }


    public void setQCI(BigInteger value) {
        this.qci = value;
    }


    public String getFrequency() {
        return frequency;
    }

    public int[] getFrequencyArrayInteger() {
        return hexStringToIntArray(frequency);
    }

    public BigInteger getFrequencyBigInterger() {
        try{
            return new BigInteger(frequency, 16);
        }catch (Exception ex){
            Log.e(TAG,"Error get Data service area :"+ex.getMessage());
        }
        return BigInteger.valueOf(-1);
    }

    public void setFrequency(String value) {
        this.frequency = value;
    }


    public String getMbmsServiceAreas() {
        return mbmsServiceAreas;
    }


    public BigInteger getMbmsServiceAreasBigInterger() {
        try{
            return new BigInteger(mbmsServiceAreas, 16);
        }catch (Exception ex){
            Log.e(TAG,"Error get Data service area :"+ex.getMessage());
        }
            return BigInteger.valueOf(-1);
    }

    public int[] getMbmsServiceAreasArrayInteger() {
        return hexStringToIntArray(mbmsServiceAreas);
    }


    private static int[] hexStringToIntArray(String s) {
        int result[]=null;
        if(s==null || s.isEmpty())return null;
        try {
            byte[] data=hexStringToByteArray(s);
            if(data!=null && data.length>0){
                int size=((data[0] & 0xff)+1);
                result=new int[size];
                if(data.length==(size*2+1))
                    for(int con=0;con<size;con++){
                        result[con]=((data[(con*2)+1] & 0xff) << 8) | (data[(con*2)+2] & 0xff);
                    }

            }
        }catch (Exception ex){
            Log.e(TAG,"Error get Data service area 2:"+ex.getMessage()+" string:"+s);
        }

        return result;
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public void setMbmsServiceAreas(String value) {
        this.mbmsServiceAreas = value;
    }

}
