


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



/**
 * 
 */

@Root(strict=false, name = "Occurrence")
public class Occurrence {

    
        @ElementList(required=false,inline=true,entry = "One", type = One.class)
        protected List<One> one;
        @ElementList(required=false,inline=true,entry = "ZeroOrOne", type = ZeroOrOne.class)
        protected List<ZeroOrOne> zeroOrOne;
        @ElementList(required=false,inline=true,entry = "ZeroOrMore", type = ZeroOrMore.class)
        protected List<ZeroOrMore> zeroOrMore;
        @ElementList(required=false,inline=true,entry = "OneOrMore", type = OneOrMore.class)
        protected List<OneOrMore>  oneOrMore;
        @ElementList(required=false,inline=true,entry = "ZeroOrN", type = ZeroOrN.class)
        protected List<ZeroOrN> zeroOrN ;
        @ElementList(required=false,inline=true,entry = "OneOrN", type = OneOrN.class)
        protected List<OneOrN> oneOrN ;

    public List<One> getOne() {
        return one;
    }

    public void setOne(List<One> one) {
        this.one = one;
    }

    public List<ZeroOrOne> getZeroOrOne() {
        return zeroOrOne;
    }

    public void setZeroOrOne(List<ZeroOrOne> zeroOrOne) {
        this.zeroOrOne = zeroOrOne;
    }

    public List<ZeroOrMore> getZeroOrMore() {
        return zeroOrMore;
    }

    public void setZeroOrMore(List<ZeroOrMore> zeroOrMore) {
        this.zeroOrMore = zeroOrMore;
    }

    public List<OneOrMore> getOneOrMore() {
        return oneOrMore;
    }

    public void setOneOrMore(List<OneOrMore> oneOrMore) {
        this.oneOrMore = oneOrMore;
    }

    public List<ZeroOrN> getZeroOrN() {
        return zeroOrN;
    }

    public void setZeroOrN(List<ZeroOrN> zeroOrN) {
        this.zeroOrN = zeroOrN;
    }

    public List<OneOrN> getOneOrN() {
        return oneOrN;
    }

    public void setOneOrN(List<OneOrN> oneOrN) {
        this.oneOrN = oneOrN;
    }
}
