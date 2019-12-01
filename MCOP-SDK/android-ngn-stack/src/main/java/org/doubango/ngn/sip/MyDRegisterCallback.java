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

package org.doubango.ngn.sip;

import android.content.Context;
import android.content.ServiceConnection;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.utils.Utils;

import java.nio.ByteBuffer;

//import org.mcopenplatform.iapi.Constants;
//import org.mcopenplatform.iapi.ISimService;

/**
 */

public class MyDRegisterCallback extends org.doubango.tinyWRAP.DRegisterCallback {
    private static final String TAG = Utils.getTAG(MyDRegisterCallback.class.getCanonicalName());
    public static final int SIZE_BUFFER_DATA=400;
    public java.nio.ByteBuffer dataResponseRegisterCallback=java.nio.ByteBuffer.allocateDirect(SIZE_BUFFER_DATA);
    private Context context=null;
    private ServiceConnection mConnection=null;
    private OnRegisterCallBackListener onRegisterCallBackListener;

    public MyDRegisterCallback(Context context) {
        super();
        if(context==null){
            Log.e(TAG,"Error in start CallBack");
            return;
        }else if(BuildConfig.DEBUG){
            Log.d(TAG,"StarStart MyDRegisterCallbackt MyDRegisterCallback");
        }
        this.context=context;
    }


    @Override
    public int onAuthRegister(String message) {
        if(BuildConfig.DEBUG)Log.d(TAG,"execute: "+"onAuthRegister");
        try{
            String response="";
            int result=-1;
           if(message!=null && !message.isEmpty()){
                if(onRegisterCallBackListener!=null){
                    response=onRegisterCallBackListener.onAuthRegister(message);
                    if(BuildConfig.DEBUG)Log.d(TAG,"Response authentication SIM:"+response);
                    result=1;
                }
            }else{
                Log.e(TAG,"It doesn´t have response from service authentication");
                result=-1;
            }
            if(response==null)response="";
            ByteBuffer buf = ByteBuffer.wrap(response.getBytes());
            transferAsMuchAsPossible(dataResponseRegisterCallback,buf);
            return result;
        }catch (Exception e) {
            Log.e(TAG,"Error in authentication SIM");
        return -2;
        }

    }

    private static int transferAsMuchAsPossible(ByteBuffer bbuf_dest, ByteBuffer bbuf_src)
    {
        bbuf_dest.clear();
        int nTransfer = Math.min(bbuf_dest.remaining(), bbuf_src.remaining());
        if (nTransfer > 0)
        {
            bbuf_dest.put(bbuf_src.array(),
                    bbuf_src.arrayOffset()+bbuf_src.position(),
                    nTransfer);
            bbuf_src.position(bbuf_src.position()+nTransfer);
        }
        return nTransfer;
    }

    public  interface  OnRegisterCallBackListener{
        String onAuthRegister(String nonce);
    }

    public  void setOnRegisterCallBackListener(OnRegisterCallBackListener onRegisterCallBackListener){
        this.onRegisterCallBackListener=onRegisterCallBackListener;
    }


}
