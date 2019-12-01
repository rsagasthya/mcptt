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
package org.doubango.ngn.datatype.profiles;

import android.util.Log;

import org.doubango.ngn.sip.NgnSipPrefrences;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

@Root(strict=false, name="Profiles")
public class Profiles {
    private final static String TAG = Profiles.class.getCanonicalName();
    @ElementList(required=false, inline=true)
    private List<NgnSipPrefrences> profiles;

    public Profiles() {
    }
    public List<NgnSipPrefrences> getProfiles() {
        return profiles;
    }
    public void setProfiles(List<NgnSipPrefrences> profiles) {
        this.profiles = profiles;
    }
    public boolean addNewProfile(NgnSipPrefrences profile){
        if(profile==null)return false;
        if(profiles==null)profiles=new ArrayList<>();
        profiles.add(profile);
        return true;
    }

    public boolean isEmpty(){
        if(profiles==null)return true;
        return profiles.isEmpty();
    }

    public int size(){
        if(profiles==null)return -1;
        return profiles.size();
    }
}
