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


package org.doubango.ngn.datatype.location;

public class DataCell {
    private String eci;
    private String mcc;
    private String mnc;
    private String tac;
    private boolean isRegister;


    public DataCell(String eci, String mcc, String mnc, String tac, boolean isRegister) {
        this.eci = eci;
        this.mcc = mcc;
        this.mnc = mnc;
        this.tac = tac;
        this.isRegister = isRegister;
    }

    public DataCell(String eci, String mcc, String mnc, String tac) {
        this.eci = eci;
        this.mcc = mcc;
        this.mnc = mnc;
        this.tac = tac;
    }



    public String getEci() {
        return eci;
    }

    public void setEci(String eci) {
        this.eci = eci;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getTac() {
        return tac;
    }

    public void setTac(String tac) {
        this.tac = tac;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }

    public String getECGI(){
        return mcc+mnc+eci;
    }

    public String getTrackingArea(){
        return mcc+mnc+tac;
    }

    public String getPLMNId(){
        return mcc+mnc;
    }

}
