


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

package org.doubango.ngn.datatype.mo; 
 import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;



@Root(strict=false, name = "Type")
public class Type {

   
        @ElementList(required=false,inline=true,entry = "MIME", type = MIME.class)
        protected List<MIME> mime ;
    @ElementList(required=false,inline=true,entry = "DDFName", type = DDFName.class)
        protected List<DDFName> ddfName ;

    public List<MIME> getMime() {
        return mime;
    }

    public void setMime(List<MIME> mime) {
        this.mime = mime;
    }

    public List<DDFName> getDdfName() {
        return ddfName;
    }

    public void setDdfName(List<DDFName> ddfName) {
        this.ddfName = ddfName;
    }
}
