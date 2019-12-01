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

package org.mcopenplatform.muoapi.datatype.group;

import org.mcopenplatform.muoapi.ConstantsMCOP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class GroupInfo {
    private String groupID;
    private String DisplayName;
    private List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowList;
    private ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo;
    private Integer maxDataSizeForSDS;
    private Integer maxDataSizeForFD;
    private Integer maxDataSizeAutoRecv;
    private List<String[]> participantsListArray;
    private List<String> participantsList;
    private List<String> participantsListDisplay;
    private List<String> participantsListType;



    public GroupInfo() {
    }


    public GroupInfo(String groupID,
                     String displayName,
                     List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowList,
                     ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo,
                     Integer maxDataSizeForSDS,
                     Integer maxDataSizeForFD,
                     Integer maxDataSizeAutoRecv,
                     List<String[]> participantsListArray) {
        this.groupID = groupID;
        DisplayName = displayName;
        this.allowList = allowList;
        this.actionRealTimeVideo = actionRealTimeVideo;
        this.maxDataSizeForSDS = maxDataSizeForSDS;
        this.maxDataSizeForFD = maxDataSizeForFD;
        this.maxDataSizeAutoRecv = maxDataSizeAutoRecv;
        this.participantsListArray = participantsListArray;
    }


    public GroupInfo(String groupID,
                     String displayName,
                     List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowList,
                     ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo,
                     Integer maxDataSizeForSDS,
                     Integer maxDataSizeForFD,
                     Integer maxDataSizeAutoRecv,
                     List<String> participantsList,
                     List<String> participantsListDisplay,
                     List<String> participantsListType) {
        this.groupID = groupID;
        DisplayName = displayName;
        this.allowList = allowList;
        this.actionRealTimeVideo = actionRealTimeVideo;
        this.maxDataSizeForSDS = maxDataSizeForSDS;
        this.maxDataSizeForFD = maxDataSizeForFD;
        this.maxDataSizeAutoRecv = maxDataSizeAutoRecv;
        this.participantsList = participantsList;
        this.participantsListDisplay = participantsListDisplay;
        this.participantsListType= participantsListType;
    }

    public GroupInfo(String groupID,
                     String displayName,
                     Set<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowSet,
                     ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo,
                     Integer maxDataSizeForSDS,
                     Integer maxDataSizeForFD,
                     Integer maxDataSizeAutoRecv,
                     List<String> participantsList,
                     List<String> participantsListDisplay,
                     List<String> participantsListType) {
        this.groupID = groupID;
        DisplayName = displayName;
        this.allowList = new ArrayList<>(allowSet);
        this.actionRealTimeVideo = actionRealTimeVideo;
        this.maxDataSizeForSDS = maxDataSizeForSDS;
        this.maxDataSizeForFD = maxDataSizeForFD;
        this.maxDataSizeAutoRecv = maxDataSizeAutoRecv;
        this.participantsList = participantsList;
        this.participantsListDisplay = participantsListDisplay;
        this.participantsListType= participantsListType;
    }

    public GroupInfo(String groupID,
                     String displayName,
                     Set<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowSet,
                     ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo,
                     Integer maxDataSizeForSDS,
                     Integer maxDataSizeForFD,
                     Integer maxDataSizeAutoRecv,
                     List<String[]> participantsListArray
                     ) {
        this.groupID = groupID;
        DisplayName = displayName;
        this.allowList = new ArrayList<>(allowSet);
        this.actionRealTimeVideo = actionRealTimeVideo;
        this.maxDataSizeForSDS = maxDataSizeForSDS;
        this.maxDataSizeForFD = maxDataSizeForFD;
        this.maxDataSizeAutoRecv = maxDataSizeAutoRecv;
        this.participantsListArray = participantsListArray;

    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> getAllowList() {
        return allowList;
    }

    public void setAllowList(List<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowList) {
        this.allowList = allowList;
    }

    public ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType getActionRealTimeVideo() {
        return actionRealTimeVideo;
    }

    public void setActionRealTimeVideo(ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo) {
        this.actionRealTimeVideo = actionRealTimeVideo;
    }

    public Integer getMaxDataSizeForSDS() {
        return maxDataSizeForSDS;
    }

    public void setMaxDataSizeForSDS(Integer maxDataSizeForSDS) {
        this.maxDataSizeForSDS = maxDataSizeForSDS;
    }

    public Integer getMaxDataSizeForFD() {
        return maxDataSizeForFD;
    }

    public void setMaxDataSizeForFD(Integer maxDataSizeForFD) {
        this.maxDataSizeForFD = maxDataSizeForFD;
    }

    public Integer getMaxDataSizeAutoRecv() {
        return maxDataSizeAutoRecv;
    }

    public void setMaxDataSizeAutoRecv(Integer maxDataSizeAutoRecv) {
        this.maxDataSizeAutoRecv = maxDataSizeAutoRecv;
    }

    public List<String> getParticipantsList() {
        if(participantsList==null)new ArrayList<>();
        return participantsList;
    }

    public void setParticipantsList(List<String> participantsList) {

        this.participantsList = participantsList;
    }

    public List<String> getParticipantsListDisplay() {
        if(participantsListDisplay==null)new ArrayList<>();
        return participantsListDisplay;
    }

    public void setParticipantsListDisplay(List<String> participantsListDisplay) {
        this.participantsListDisplay = participantsListDisplay;
    }

    public List<String> getParticipantsListType() {
        if(participantsListType==null)new ArrayList<>();
        return participantsListType;
    }

    public void setParticipantsListType(List<String> participantsListType) {
        this.participantsListType = participantsListType;
    }

    public List<String[]> getParticipantsListArray() {
        if(participantsListArray==null)return participantsListArray;
        return participantsListArray;
    }

    public void setParticipantsListArray(List<String[]> participantsListArray) {
        this.participantsListArray = participantsListArray;
    }
}