


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
 import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;




@Root(strict=false, name = "DFFormat")
public class DFFormat {

        @Element(required = false , name = "b64", type = B64 .class)
        protected B64 b64;
        @Element(required = false , name = "bin", type = Bin.class)
        protected Bin bin;
        @Element(required = false , name = "bool", type = Bool.class)
        protected Bool ibool;
        @Element(required = false , name = "chr", type = Chr.class)
        protected Chr chr;
        @Element(required = false , name = "int", type = Int.class)
        protected Int iint;
        @Element(required = false , name = "node", type = Nodie.class)
        protected Nodie nodie;
        @Element(required = false , name = "null", type = Null.class)
        protected Null inull;
        @Element(required = false , name = "xml", type = Xml.class)
        protected Xml xml;
        @Element(required = false , name = "date", type = Date.class)
        protected Date date;
        @Element(required = false , name = "time", type = Time.class)
        protected Time time;
        @Element(required = false , name = "float", type = Float.class)
        protected Float ifloat;

    public B64 getB64() {
        return b64;
    }

    public void setB64(B64 b64) {
        this.b64 = b64;
    }

    public Bin getBin() {
        return bin;
    }

    public void setBin(Bin bin) {
        this.bin = bin;
    }

    public Bool getBool() {
        return ibool;
    }

    public void setBool(Bool ibool) {
        this.ibool = ibool;
    }

    public Chr getChr() {
        return chr;
    }

    public void setChr(Chr chr) {
        this.chr = chr;
    }

    public Int getIint() {
        return iint;
    }

    public void setInt(Int iint) {
        this.iint = iint;
    }

    public Nodie getNodie() {
        return nodie;
    }

    public void setNodie(Nodie nodie) {
        this.nodie = nodie;
    }

    public Null getNull() {
        return inull;
    }

    public void setNull(Null inull) {
        this.inull = inull;
    }

    public Xml getXml() {
        return xml;
    }

    public void setXml(Xml xml) {
        this.xml = xml;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Float getFloat() {
        return ifloat;
    }

    public void setFloat(Float ifloat) {
        this.ifloat = ifloat;
    }
}
