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

package org.doubango.ngn.services.impl.ms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


class PathData {
    private int codeResult;
    private String path;
    private Map<String,String> parameters;
    private String responseBody;
    private Map<String,List<String>> responseParameters;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }



    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public PathData(String path, Map<String, String> parameters) {
        this.path = path;
        this.parameters = parameters;
    }

    public PathData(int codeResult,Map<String, List<String>> responseParameters,String responseBody) {
        this.codeResult=codeResult;
        this.responseBody = responseBody;
        this.responseParameters = responseParameters;
    }

    public PathData(String path) {
        this.path = path;
        this.parameters=new HashMap<>();
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Map<String, List<String>> getResponseParameters() {
        return responseParameters;
    }

    public void setResponseParameters(Map<String, List<String>> responseParameters) {
        this.responseParameters = responseParameters;
    }

    public int getCodeResult() {
        return codeResult;
    }

    public void setCodeResult(int codeResult) {
        this.codeResult = codeResult;
    }
}
