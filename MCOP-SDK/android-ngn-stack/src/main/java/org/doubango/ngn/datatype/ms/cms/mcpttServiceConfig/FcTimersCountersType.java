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




package org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name =  "fc-timers-countersType")
public class FcTimersCountersType {

    @Element(required = false , name = "T1-end-of-rtp-media")
    protected String t1EndOfRtpMedia;
    @Element(required = false , name = "T3-stop-talking-grace")
    protected String t3StopTalkingGrace;
    @Element(required = false , name = "T7-floor-idle")
    protected String t7FloorIdle;
    @Element(required = false , name = "T8-floor-revoke")
    protected String t8FloorRevoke;
    @Element(required = false , name = "T11-end-of-RTP-dual")
    protected String t11EndOfRTPDual;
    @Element(required = false , name = "T12-stop-talking-dual")
    protected String t12StopTalkingDual;
    @Element(required = false , name = "T15-conversation")
    protected String t15Conversation;
    @Element(required = false , name = "T16-map-group-to-bearer")
    protected String t16MapGroupToBearer;
    @Element(required = false , name = "T17-unmap-group-to-bearer")
    protected String t17UnmapGroupToBearer;
    @Element(required = false , name = "T20-floor-granted")
    protected String t20FloorGranted;
    @Element(required = false , name = "T55-connect")
    protected String t55Connect;
    @Element(required = false , name = "T56-disconnect")
    protected String t56Disconnect;
    @Element(required = false , name = "C7-floor-idle")
    protected int c7FloorIdle;
    @Element(required = false , name = "C17-unmap-group-to-bearer")
    protected int c17UnmapGroupToBearer;
    @Element(required = false , name = "C20-floor-granted")
    protected int c20FloorGranted;
    @Element(required = false , name = "C55-connect")
    protected int c55Connect;
    @Element(required = false , name = "C56-disconnect")
    protected int c56Disconnect;


   


    public String getT1EndOfRtpMedia() {
        return t1EndOfRtpMedia;
    }


    public void setT1EndOfRtpMedia(String value) {
        this.t1EndOfRtpMedia = value;
    }


    public String getT3StopTalkingGrace() {
        return t3StopTalkingGrace;
    }


    public void setT3StopTalkingGrace(String value) {
        this.t3StopTalkingGrace = value;
    }


    public String getT7FloorIdle() {
        return t7FloorIdle;
    }


    public void setT7FloorIdle(String value) {
        this.t7FloorIdle = value;
    }


    public String getT8FloorRevoke() {
        return t8FloorRevoke;
    }


    public void setT8FloorRevoke(String value) {
        this.t8FloorRevoke = value;
    }


    public String getT11EndOfRTPDual() {
        return t11EndOfRTPDual;
    }


    public void setT11EndOfRTPDual(String value) {
        this.t11EndOfRTPDual = value;
    }


    public String getT12StopTalkingDual() {
        return t12StopTalkingDual;
    }


    public void setT12StopTalkingDual(String value) {
        this.t12StopTalkingDual = value;
    }


    public String getT15Conversation() {
        return t15Conversation;
    }


    public void setT15Conversation(String value) {
        this.t15Conversation = value;
    }


    public String getT16MapGroupToBearer() {
        return t16MapGroupToBearer;
    }


    public void setT16MapGroupToBearer(String value) {
        this.t16MapGroupToBearer = value;
    }


    public String getT17UnmapGroupToBearer() {
        return t17UnmapGroupToBearer;
    }


    public void setT17UnmapGroupToBearer(String value) {
        this.t17UnmapGroupToBearer = value;
    }


    public String getT20FloorGranted() {
        return t20FloorGranted;
    }


    public void setT20FloorGranted(String value) {
        this.t20FloorGranted = value;
    }


    public String getT55Connect() {
        return t55Connect;
    }


    public void setT55Connect(String value) {
        this.t55Connect = value;
    }


    public String getT56Disconnect() {
        return t56Disconnect;
    }


    public void setT56Disconnect(String value) {
        this.t56Disconnect = value;
    }


    public int getC7FloorIdle() {
        return c7FloorIdle;
    }


    public void setC7FloorIdle(int value) {
        this.c7FloorIdle = value;
    }


    public int getC17UnmapGroupToBearer() {
        return c17UnmapGroupToBearer;
    }


    public void setC17UnmapGroupToBearer(int value) {
        this.c17UnmapGroupToBearer = value;
    }


    public int getC20FloorGranted() {
        return c20FloorGranted;
    }


    public void setC20FloorGranted(int value) {
        this.c20FloorGranted = value;
    }


    public int getC55Connect() {
        return c55Connect;
    }


    public void setC55Connect(int value) {
        this.c55Connect = value;
    }


    public int getC56Disconnect() {
        return c56Disconnect;
    }


    public void setC56Disconnect(int value) {
        this.c56Disconnect = value;
    }
}
