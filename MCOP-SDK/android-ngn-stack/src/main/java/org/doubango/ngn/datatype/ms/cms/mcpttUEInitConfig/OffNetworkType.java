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




package org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


@Root(strict=false, name = "Off-networkType")
public class OffNetworkType {

    @Element(required = false , name = "Timers")
    protected OffNetworkType.Timers timers;
    @Element(required = false , name = "Counters")
    protected OffNetworkType.Counters counters;
    @Attribute(required = false , name = "index")
    protected String index;



    public OffNetworkType.Timers getTimers() {
        return timers;
    }


    public void setTimers(OffNetworkType.Timers value) {
        this.timers = value;
    }


    public OffNetworkType.Counters getCounters() {
        return counters;
    }


    public void setCounters(OffNetworkType.Counters value) {
        this.counters = value;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }


    @Root(strict=false, name = "Counters")
    public static class Counters {

        @Element(required = false , name = "CFP1")
        protected short cfp1;
        @Element(required = false , name = "CFP3")
        protected short cfp3;
        @Element(required = false , name = "CFP4")
        protected short cfp4;
        @Element(required = false , name = "CFP6")
        protected short cfp6;
        @Element(required = false , name = "CFG11")
        protected short cfg11;
        @Element(required = false , name = "CFG12")
        protected short cfg12;
        @Element(required = false , name = "C201")
        protected short c201;
        @Element(required = false , name = "C204")
        protected short c204;
        @Element(required = false , name = "C205")
        protected short c205;


        public short getCFP1() {
            return cfp1;
        }


        public void setCFP1(short value) {
            this.cfp1 = value;
        }


        public short getCFP3() {
            return cfp3;
        }


        public void setCFP3(short value) {
            this.cfp3 = value;
        }


        public short getCFP4() {
            return cfp4;
        }


        public void setCFP4(short value) {
            this.cfp4 = value;
        }


        public short getCFP6() {
            return cfp6;
        }


        public void setCFP6(short value) {
            this.cfp6 = value;
        }


        public short getCFG11() {
            return cfg11;
        }


        public void setCFG11(short value) {
            this.cfg11 = value;
        }


        public short getCFG12() {
            return cfg12;
        }


        public void setCFG12(short value) {
            this.cfg12 = value;
        }


        public short getC201() {
            return c201;
        }


        public void setC201(short value) {
            this.c201 = value;
        }


        public short getC204() {
            return c204;
        }


        public void setC204(short value) {
            this.c204 = value;
        }


        public short getC205() {
            return c205;
        }


        public void setC205(short value) {
            this.c205 = value;
        }

    }



    @Root(strict=false, name = "Timers")
    public static class Timers {

        @Element(required = false , name = "TFG1")
            protected int tfg1;
        @Element(required = false , name = "TFG2")
            protected int tfg2;
        @Element(required = false , name = "TFG3")
            protected int tfg3;
        @Element(required = false , name = "TFG4")
        protected short tfg4;
        @Element(required = false , name = "TFG5")
        protected short tfg5;
        @Element(required = false , name = "TFG11")
        protected short tfg11;
        @Element(required = false , name = "TFG12")
        protected short tfg12;
        @Element(required = false , name = "TFG13")
        protected short tfg13;
        @Element(required = false , name = "TFG14")
        protected short tfg14;
        @Element(required = false , name = "TFP1")
            protected int tfp1;
        @Element(required = false , name = "TFP2")
        protected short tfp2;
        @Element(required = false , name = "TFP3")
            protected int tfp3;
        @Element(required = false , name = "TFP4")
            protected int tfp4;
        @Element(required = false , name = "TFP5")
            protected int tfp5;
        @Element(required = false , name = "TFP6")
            protected int tfp6;
        @Element(required = false , name = "TFP7")
        protected short tfp7;
        @Element(required = false , name = "TFB1")
            protected int tfb1;
        @Element(required = false , name = "TFB2")
        protected short tfb2;
        @Element(required = false , name = "TFB3")
        protected short tfb3;
        @Element(required = false , name = "T201")
        protected short t201;
        @Element(required = false , name = "T203")
        protected short t203;
        @Element(required = false , name = "T204")
        protected short t204;
        @Element(required = false , name = "T205")
        protected short t205;
        @Element(required = false , name = "T230")
        protected short t230;
        @Element(required = false , name = "T233")
        protected short t233;
        @Element(required = false , name = "TFE1")
        protected short tfe1;
        @Element(required = false , name = "TFE2")
        protected short tfe2;

        public short getTFG13() {
            return tfg13;
        }

        public void setTFG13(short tfg13) {
            this.tfg13 = tfg13;
        }

        public short getTFG14() {
            return tfg14;
        }

        public void setTFG14(short tfg14) {
            this.tfg14 = tfg14;
        }

        public int getTFG1() {
            return tfg1;
        }


        public void setTFG1(int value) {
            this.tfg1 = value;
        }


        public int getTFG2() {
            return tfg2;
        }


        public void setTFG2(int value) {
            this.tfg2 = value;
        }


        public int getTFG3() {
            return tfg3;
        }


        public void setTFG3(int value) {
            this.tfg3 = value;
        }


        public short getTFG4() {
            return tfg4;
        }


        public void setTFG4(short value) {
            this.tfg4 = value;
        }


        public short getTFG5() {
            return tfg5;
        }


        public void setTFG5(short value) {
            this.tfg5 = value;
        }


        public short getTFG11() {
            return tfg11;
        }


        public void setTFG11(short value) {
            this.tfg11 = value;
        }


        public short getTFG12() {
            return tfg12;
        }


        public void setTFG12(short value) {
            this.tfg12 = value;
        }


        public int getTFP1() {
            return tfp1;
        }


        public void setTFP1(int value) {
            this.tfp1 = value;
        }


        public short getTFP2() {
            return tfp2;
        }


        public void setTFP2(short value) {
            this.tfp2 = value;
        }


        public int getTFP3() {
            return tfp3;
        }


        public void setTFP3(int value) {
            this.tfp3 = value;
        }


        public int getTFP4() {
            return tfp4;
        }


        public void setTFP4(int value) {
            this.tfp4 = value;
        }


        public int getTFP5() {
            return tfp5;
        }


        public void setTFP5(int value) {
            this.tfp5 = value;
        }


        public int getTFP6() {
            return tfp6;
        }


        public void setTFP6(int value) {
            this.tfp6 = value;
        }


        public short getTFP7() {
            return tfp7;
        }


        public void setTFP7(short value) {
            this.tfp7 = value;
        }


        public int getTFB1() {
            return tfb1;
        }


        public void setTFB1(int value) {
            this.tfb1 = value;
        }


        public short getTFB2() {
            return tfb2;
        }


        public void setTFB2(short value) {
            this.tfb2 = value;
        }


        public short getTFB3() {
            return tfb3;
        }


        public void setTFB3(short value) {
            this.tfb3 = value;
        }


        public short getT201() {
            return t201;
        }


        public void setT201(short value) {
            this.t201 = value;
        }


        public short getT203() {
            return t203;
        }


        public void setT203(short value) {
            this.t203 = value;
        }


        public short getT204() {
            return t204;
        }


        public void setT204(short value) {
            this.t204 = value;
        }


        public short getT205() {
            return t205;
        }


        public void setT205(short value) {
            this.t205 = value;
        }


        public short getT230() {
            return t230;
        }


        public void setT230(short value) {
            this.t230 = value;
        }


        public short getT233() {
            return t233;
        }


        public void setT233(short value) {
            this.t233 = value;
        }


        public short getTFE1() {
            return tfe1;
        }


        public void setTFE1(short value) {
            this.tfe1 = value;
        }


        public short getTFE2() {
            return tfe2;
        }


        public void setTFE2(short value) {
            this.tfe2 = value;
        }

    }

}
