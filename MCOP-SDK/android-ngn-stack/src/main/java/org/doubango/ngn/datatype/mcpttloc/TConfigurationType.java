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


package org.doubango.ngn.datatype.mcpttloc;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;




@Root(strict=false, name = "tConfigurationType")
public class TConfigurationType {

    @Element(required=false,name = "NonEmergencyLocationInformation")
    protected TRequestedLocationType nonEmergencyLocationInformation;
    @Element(required=false,name = "EmergencyLocationInformation")
    protected TRequestedLocationType emergencyLocationInformation;
    @Element(required=false,name = "TriggeringCriteria")
    protected TriggeringCriteriaType triggeringCriteria;
    
    @Attribute(required=false,name = "ConfigScope")
    protected String configScope;
    

    public TRequestedLocationType getNonEmergencyLocationInformation() {
        return nonEmergencyLocationInformation;
    }


    public void setNonEmergencyLocationInformation(TRequestedLocationType value) {
        this.nonEmergencyLocationInformation = value;
    }


    public TRequestedLocationType getEmergencyLocationInformation() {
        return emergencyLocationInformation;
    }


    public void setEmergencyLocationInformation(TRequestedLocationType value) {
        this.emergencyLocationInformation = value;
    }


    public TriggeringCriteriaType getTriggeringCriteria() {
        return triggeringCriteria;
    }


    public void setTriggeringCriteria(TriggeringCriteriaType value) {
        this.triggeringCriteria = value;
    }


    public String getConfigScope() {
        return configScope;
    }


    public void setConfigScope(String value) {
        this.configScope = value;
    }

}
