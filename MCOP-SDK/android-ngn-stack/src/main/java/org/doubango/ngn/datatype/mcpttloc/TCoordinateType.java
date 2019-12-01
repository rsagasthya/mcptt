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


package org.doubango.ngn.datatype.mcpttloc;


import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "CoordinateType")
@Namespace(reference = "urn:3gpp:ns:mcpttLocationInfo:1.0") // Add your reference here!
public class TCoordinateType {






    @Element(required=false,name="threebytes")
    protected long threebytes;
    @Attribute(required=false,name = "type")
    protected ProtectionType type;


    public TCoordinateType() {
    }

    public TCoordinateType(long threebytes, ProtectionType type) {
        this.threebytes = threebytes;
        this.type = type;
    }


    public long getThreebytes() {
        return threebytes;
    }

    public void setThreebytes(long threebytes) {
        this.threebytes = threebytes;
    }

    public ProtectionType getType() {
        return type;
    }

    public void setType(ProtectionType type) {
        this.type = type;
    }


}
