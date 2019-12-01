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



package org.doubango.ngn.datatype.affiliation.pidf;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;



@Root(strict=false, name = "tuple")
public class Tuple {

    @Element(required = false, name = "status")
    protected Status status;
    @Element(required = false, name = "contact")
    protected Contact contact;
    @ElementList(entry = "note", required=false, inline=true)
    protected List<Note> note;
    @Element(required = false, name = "timestamp")
    protected String timestamp;
    @Attribute(name = "id")
    protected String id;


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status value) {
        this.status = value;
    }




    public Contact getContact() {
        return contact;
    }


    public void setContact(Contact value) {
        this.contact = value;
    }


    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<Note>();
        }
        return this.note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }


    public String getId() {
        return id;
    }


    public void setId(String value) {
        this.id = value;
    }

}
