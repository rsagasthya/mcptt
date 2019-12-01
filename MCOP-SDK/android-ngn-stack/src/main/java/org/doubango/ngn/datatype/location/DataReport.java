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


package org.doubango.ngn.datatype.location;

import java.util.Set;

public class DataReport {
    private Set<InfoReport> infoReport;
    private long minimumIntervalLength = -1;
    private int numNeighbour = 0;

    public DataReport(long minimumIntervalLength) {
        this.minimumIntervalLength = minimumIntervalLength;
    }

    public DataReport(Set<InfoReport> infoReport, long minimumIntervalLength) {
        this.infoReport = infoReport;
        this.minimumIntervalLength = minimumIntervalLength;
    }

    public DataReport(Set<InfoReport> infoReport, long minimumIntervalLength, int numNeighbour) {
        this.infoReport = infoReport;
        this.minimumIntervalLength = minimumIntervalLength;
        this.numNeighbour = numNeighbour;
    }

    public Set<InfoReport> getInfoReport() {
        return infoReport;
    }

    public void setInfoReport(Set<InfoReport> infoReport) {
        this.infoReport = infoReport;
    }

    public long getMinimumIntervalLength() {
        return minimumIntervalLength;
    }

    public void setMinimumIntervalLength(long minimumIntervalLength) {
        this.minimumIntervalLength = minimumIntervalLength;
    }

    public int getNumNeighbour() {
        return numNeighbour;
    }

    public void setNumNeighbour(int numNeighbour) {
        this.numNeighbour = numNeighbour;
    }
}
