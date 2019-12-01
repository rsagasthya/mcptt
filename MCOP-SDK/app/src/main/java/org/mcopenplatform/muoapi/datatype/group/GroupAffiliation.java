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

import org.doubango.ngn.datatype.affiliation.pidf.StatusType;
import org.mcopenplatform.muoapi.ConstantsMCOP;


public class GroupAffiliation {
    private String groupID;
    private ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum stateAffiliation;

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum getStateAffiliation() {
        return stateAffiliation;
    }

    public void setStateAffiliation(ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum stateAffiliation) {
        this.stateAffiliation = stateAffiliation;
    }

    public GroupAffiliation(String groupID, ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum stateAffiliation) {
        this.groupID = groupID;
        this.stateAffiliation = stateAffiliation;
    }

    public GroupAffiliation(String groupID, StatusType stateAffiliation) {
        this.groupID = groupID;
        this.stateAffiliation = StatusTypeToGroupAffiliationStateEnum(stateAffiliation);
    }

    public GroupAffiliation() {
    }

    private static ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum StatusTypeToGroupAffiliationStateEnum(StatusType stateAffiliation){
        ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum groupAffiliationStateEnum=null;
        if(stateAffiliation!=null){
            switch (stateAffiliation) {
                case noaffiliated:
                    groupAffiliationStateEnum=ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.notaffiliated;
                    break;
                case affiliating:
                    groupAffiliationStateEnum=ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.affiliating;
                    break;
                case affiliated:
                    groupAffiliationStateEnum=ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.affiliated;
                    break;
                case deaffiliating:
                    groupAffiliationStateEnum=ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationStateEnum.deaffiliating;
                    break;
            }
        }
        return groupAffiliationStateEnum;
    }
}