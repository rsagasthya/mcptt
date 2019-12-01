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
package org.doubango.ngn.datatype.openId;

import android.support.annotation.NonNull;

import org.doubango.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

//import com.fasterxml.jackson.databind.ObjectMapper;


public class CampsType implements Serializable{
    private final static String TAG = Utils.getTAG(CampsType.class.getCanonicalName());


    public final static String MCPTT_ID="mcptt_id";
    public final static String SUB="sub";
    public final static String AZP="azp";
    public final static String ISS="iss";
    public final static String EXP="exp";
    public final static String IAT="iat";
    public final static String JTI="jti";
    public final static String CLIENT_ID="client_id";
    public final static String ACCESS_TOKEN="accessToken";
    public final static String ID_TOKEN="idToken";
    public final static String REFRESH_TOKEN="refreshToken";


    private String mcptt_id;
    private String sub;
    private String azp;
    private String iss;
    private String exp;
    private String iat;
    private String jti;
    private String client_id;
    private String accessToken;
    private String idToken;
    private String refreshToken;

    public CampsType() {
    }

    public CampsType(String mcptt_id, String sub, String azp, String iss, String iat, String exp, String jti, String client_id) {
        this.mcptt_id = mcptt_id;
        this.sub = sub;
        this.azp = azp;
        this.iss = iss;
        this.iat = iat;
        this.exp = exp;
        this.jti = jti;
        this.client_id = client_id;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getMcptt_id() {
        return mcptt_id;
    }

    public void setMcptt_id(String mcptt_id) {
        this.mcptt_id = mcptt_id;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getAzp() {
        return azp;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getIat() {
        return iat;
    }

    public void setIat(String iat) {
        this.iat = iat;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }


    public static CampsType jsonDeserialize(@NonNull String campsTypeString) throws IOException, JSONException{
        if(campsTypeString==null)throw new IOException();
        /*
        //Jackson
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in String
        return mapper.readValue(campsTypeString, CampsType.class);
        */
        JSONObject reader = null;
        CampsType campsType=new CampsType();
            reader = new JSONObject(campsTypeString);
            if(!reader.isNull(MCPTT_ID))
            campsType.setMcptt_id(reader.getString(MCPTT_ID));
            if(!reader.isNull(SUB))
            campsType.setSub(reader.getString(SUB));
            if(!reader.isNull(AZP))
            campsType.setAzp(reader.getString(AZP));
            if(!reader.isNull(ISS))
            campsType.setIss(reader.getString(ISS));
            if(!reader.isNull(IAT))
            campsType.setIat(reader.getString(IAT));
            if(!reader.isNull(EXP))
            campsType.setExp(reader.getString(EXP));
            if(!reader.isNull(JTI))
            campsType.setJti(reader.getString(JTI));
            if(!reader.isNull(CLIENT_ID))
            campsType.setClient_id(reader.getString(CLIENT_ID));
            if(!reader.isNull(ACCESS_TOKEN))
                campsType.setAccessToken(reader.getString(ACCESS_TOKEN));
            if(!reader.isNull(ID_TOKEN))
                campsType.setIdToken(reader.getString(ID_TOKEN));
            if(!reader.isNull(REFRESH_TOKEN))
                campsType.setRefreshToken(reader.getString(REFRESH_TOKEN));
        return campsType;

    }

    public static String jsonSerialize(@NonNull CampsType campsType) throws IOException, JSONException {
        /*
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in String
        return mapper.writeValueAsString(campsType);
        */
        if(campsType==null)return null;
        JSONObject jsonObject=new JSONObject();
        jsonObject.put(MCPTT_ID,campsType.getMcptt_id());
        jsonObject.put(SUB,campsType.getSub());
        jsonObject.put(AZP,campsType.getAzp());
        jsonObject.put(ISS,campsType.getIss());
        jsonObject.put(IAT,campsType.getIat());
        jsonObject.put(EXP,campsType.getExp());
        jsonObject.put(JTI,campsType.getJti());
        jsonObject.put(CLIENT_ID,campsType.getClient_id());
        jsonObject.put(ACCESS_TOKEN,campsType.getAccessToken());
        jsonObject.put(ID_TOKEN,campsType.getIdToken());
        jsonObject.put(REFRESH_TOKEN,campsType.getRefreshToken());
        return jsonObject.toString();
    }

    public String jsonSerializeString() throws IOException, JSONException {
        return jsonSerialize(this);
    }


    public boolean isEmpty(){
        /*
        private String mcptt_id;
    private String sub;
    private String azp;
    private String iss;
    private String exp;
    private String iat;
    private String jti;
    private String client_id;
    private String accessToken;
    private String idToken;
    private String refreshToken;
         */
        boolean result=true;
        if((sub!=null && !sub.trim().isEmpty()) ||
                (azp!=null && !azp.trim().isEmpty())||
                (iss!=null && !iss.trim().isEmpty())||
                (exp!=null && !exp.trim().isEmpty())||
                (iat!=null && !iat.trim().isEmpty())||
                (jti!=null && !jti.trim().isEmpty())||
                (client_id!=null && !client_id.trim().isEmpty())||
                (accessToken!=null && !accessToken.trim().isEmpty())||
                (idToken!=null && !idToken.trim().isEmpty())||
                (refreshToken!=null && !refreshToken.trim().isEmpty())){
            result=false;
        }
        return result;
    }



}
