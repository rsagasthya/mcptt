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
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;



@Root(strict=false, name="affiliation")
@Namespace(reference = "urn:3gpp:ns:mcpttPresInfo:1.0")
public class AffiliationType {


    @Attribute(required = false,name = "group")
    protected String group;
    @Attribute(required = false ,name = "client")
    protected String client;
    @Attribute(required = false ,name = "status")
    protected StatusType status;
    @Attribute(required = false ,name = "expires")
    protected String expires;

    private static final SimpleDateFormat FORMAT_DATE= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private static final SimpleDateFormat FORMAT_DATE2= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat FORMAT_DATE3= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public AffiliationType() {
    }

    public AffiliationType(String group) {
        this.group = group;
    }

    public AffiliationType(String group, StatusType status, String expires) {
        this.group = group;
        this.status = status;
        this.expires = expires;
    }


    public String getGroup() {return group;}


    public void setGroup(String value) {
        this.group = value;
    }


    public String getClient() {
        return client;
    }


    public void setClient(String value) {
        this.client = value;
    }


    public StatusType getStatus() {
        return status;
    }


    public void setStatus(StatusType value) {
        this.status = value;
    }


    public String getExpires() {
        return expires;
    }




    public void setExpires(String value) {
        this.expires = value;
    }




    public void setExpiresDate(Date value) {

        this.expires = FORMAT_DATE.format(value);
    }


    public Date getExpiresDate(){
        return getDate(this.expires);
    }

    public static Date getDate(String date){
        FORMAT_DATE3.setTimeZone(TimeZone.getTimeZone("UTC"));
        FORMAT_DATE2.setTimeZone(TimeZone.getTimeZone("UTC"));
        FORMAT_DATE.setTimeZone(TimeZone.getTimeZone("UTC"));
        if(date==null || date.isEmpty())return null;
        try {
            if(date==null)return null;
            return FORMAT_DATE.parse(date.trim());
        } catch (ParseException e) {
            try {
                Date date2=FORMAT_DATE3.parse(date.trim());
                if(date2==null)return null;
                return date2;
            } catch (ParseException e1) {
                try {
                    return FORMAT_DATE2.parse(date.trim());
                } catch (ParseException e2) {
                    System.out.println("fall");

                    return null;
                }
            }

        }
    }
}
