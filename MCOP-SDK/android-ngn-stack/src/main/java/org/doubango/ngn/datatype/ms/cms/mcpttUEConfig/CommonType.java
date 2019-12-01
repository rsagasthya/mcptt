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




package org.doubango.ngn.datatype.ms.cms.mcpttUEConfig;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Root(strict=false, name = "CommonType")
public class CommonType {

    @Element(required = false , name = "private-call")
    protected CommonType.PrivateCall privateCall;
    @Element(required = false , name = "MCPTT-Group-Call")
    protected CommonType.MCPTTGroupCall mcpttGroupCall;


    @Attribute(required = false , name = "index")
    protected String index;
   


    public CommonType.PrivateCall getPrivateCall() {
        return privateCall;
    }


    public void setPrivateCall(CommonType.PrivateCall value) {
        this.privateCall = value;
    }


    public CommonType.MCPTTGroupCall getMCPTTGroupCall() {
        return mcpttGroupCall;
    }


    public void setMCPTTGroupCall(CommonType.MCPTTGroupCall value) {
        this.mcpttGroupCall = value;
    }

    

    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }

    



    @Root(strict=false, name = "")
    public static class MCPTTGroupCall {

        @Element(required = false , name = "Max-Simul-Call-N4")
        protected BigInteger maxSimulCallN4;
        @Element(required = false , name = "Max-Simul-Trans-N5")
        protected BigInteger maxSimulTransN5;
        @Element(required = false , name = "Prioritized-MCPTT-Group")
        protected CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup prioritizedMCPTTGroup;


        public BigInteger getMaxSimulCallN4() {
            return maxSimulCallN4;
        }


        public void setMaxSimulCallN4(BigInteger value) {
            this.maxSimulCallN4 = value;
        }


        public BigInteger getMaxSimulTransN5() {
            return maxSimulTransN5;
        }


        public void setMaxSimulTransN5(BigInteger value) {
            this.maxSimulTransN5 = value;
        }


        public CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup getPrioritizedMCPTTGroup() {
            return prioritizedMCPTTGroup;
        }


        public void setPrioritizedMCPTTGroup(CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup value) {
            this.prioritizedMCPTTGroup = value;
        }



        @Root(strict=false, name = "")
        public static class PrioritizedMCPTTGroup {

            @ElementList(inline=true,entry="MCPTT-Group-Priority")
            protected List<CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority> mcpttGroupPriority;


            public List<CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority> getMCPTTGroupPriority() {
                if (mcpttGroupPriority == null) {
                    mcpttGroupPriority = new ArrayList<CommonType.MCPTTGroupCall.PrioritizedMCPTTGroup.MCPTTGroupPriority>();
                }
                return this.mcpttGroupPriority;
            }

            public void setMcpttGroupPriority(List<MCPTTGroupPriority> mcpttGroupPriority) {
                this.mcpttGroupPriority = mcpttGroupPriority;
            }

            @Root(strict=false, name = "")
            public static class MCPTTGroupPriority {

                @Element(required = false , name = "MCPTT-Group-ID")
                protected String mcpttGroupID;
                @Element(required = false , name = "group-priority-hierarchy")
                protected BigInteger groupPriorityHierarchy;


                public String getMCPTTGroupID() {
                    return mcpttGroupID;
                }


                public void setMCPTTGroupID(String value) {
                    this.mcpttGroupID = value;
                }


                public BigInteger getGroupPriorityHierarchy() {
                    return groupPriorityHierarchy;
                }


                public void setGroupPriorityHierarchy(BigInteger value) {
                    this.groupPriorityHierarchy = value;
                }

            }

        }

    }



    @Root(strict=false, name = "")
    public static class PrivateCall {

        @Element(required = false , name = "Max-Simul-Call-N10")
        protected BigInteger maxSimulCallN10;


        public BigInteger getMaxSimulCallN10() {
            return maxSimulCallN10;
        }


        public void setMaxSimulCallN10(BigInteger value) {
            this.maxSimulCallN10 = value;
        }

    }

}
