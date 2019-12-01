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




package org.doubango.ngn.datatype.mcpttlocOld;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "tMbsfnAreaChangeType")
public class TMbsfnAreaChangeType {

    @Element(required=false,name = "EnterSpecificMbsfnArea")
    protected TMbsfnAreaIdentity enterSpecificMbsfnArea;
    @Element(required=false,name = "ExitSpecificMbsfnArea")
    protected TMbsfnAreaIdentity exitSpecificMbsfnArea;
    
    


    public TMbsfnAreaIdentity getEnterSpecificMbsfnArea() {
        return enterSpecificMbsfnArea;
    }


    public void setEnterSpecificMbsfnArea(TMbsfnAreaIdentity value) {
        this.enterSpecificMbsfnArea = value;
    }


    public TMbsfnAreaIdentity getExitSpecificMbsfnArea() {
        return exitSpecificMbsfnArea;
    }


    public void setExitSpecificMbsfnArea(TMbsfnAreaIdentity value) {
        this.exitSpecificMbsfnArea = value;
    }   

}
