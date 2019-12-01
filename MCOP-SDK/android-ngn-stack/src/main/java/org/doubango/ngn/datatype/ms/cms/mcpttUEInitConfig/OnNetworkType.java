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
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(strict=false, name = "On-networkType")
public class OnNetworkType {

    @Element(required = false , name = "Timers")
    protected OnNetworkType.Timers timers;
    @Element(required = false , name = "HPLM")
    protected OnNetworkType.HPLM hplm;
    @Element(required = false , name = "App-Server-Info")
    protected OnNetworkType.AppServerInfo appServerInfo;
    @Element(required = false , name = "GMS-URI")
    protected String gmsuri;
    @Element(required = false , name = "group-creation-XUI")
    protected String groupCreationXUI;
    @Element(required = false , name = "GMS-XCAP-root-URI")
    protected String gmsxcapRootURI;
    @Element(required = false , name = "CMS-XCAP-root-URI")
    protected String cmsxcapRootURI;
    @Element(required = false , name = "integrity-protection-enabled")
    protected Boolean integrityProtectionEnabled;
    @Element(required = false , name = "confidentiality-protection-enabled")
    protected Boolean confidentialityProtectionEnabled;


    @Attribute(required = false , name = "index")
    protected String index;


    public Boolean getIntegrityProtectionEnabled() {
        return integrityProtectionEnabled;
    }

    public void setIntegrityProtectionEnabled(Boolean integrityProtectionEnabled) {
        this.integrityProtectionEnabled = integrityProtectionEnabled;
    }

    public Boolean getConfidentialityProtectionEnabled() {
        return confidentialityProtectionEnabled;
    }

    public void setConfidentialityProtectionEnabled(Boolean confidentialityProtectionEnabled) {
        this.confidentialityProtectionEnabled = confidentialityProtectionEnabled;
    }

    public OnNetworkType.Timers getTimers() {
        return timers;
    }


    public void setTimers(OnNetworkType.Timers value) {
        this.timers = value;
    }


    public OnNetworkType.HPLM getHPLM() {
        return hplm;
    }


    public void setHPLM(OnNetworkType.HPLM value) {
        this.hplm = value;
    }


    public OnNetworkType.AppServerInfo getAppServerInfo() {
        return appServerInfo;
    }


    public void setAppServerInfo(OnNetworkType.AppServerInfo value) {
        this.appServerInfo = value;
    }


    public String getGMSURI() {
        return gmsuri;
    }


    public void setGMSURI(String value) {
        this.gmsuri = value;
    }


    public String getGroupCreationXUI() {
        return groupCreationXUI;
    }


    public void setGroupCreationXUI(String value) {
        this.groupCreationXUI = value;
    }


    public String getGMSXCAPRootURI() {
        return gmsxcapRootURI;
    }


    public void setGMSXCAPRootURI(String value) {
        this.gmsxcapRootURI = value;
    }


    public String getCMSXCAPRootURI() {
        return cmsxcapRootURI;
    }


    public void setCMSXCAPRootURI(String value) {
        this.cmsxcapRootURI = value;
    }


    public String getIndex() {
        return index;
    }


    public void setIndex(String value) {
        this.index = value;
    }



    @Root(strict=false, name = "AppServerInfo")
    public static class AppServerInfo {

        @Element(required = false , name = "idms-auth-endpoint")
            protected String idmsAuthEndpoint;
        @Element(required = false , name = "http-proxy")
            protected String httpProxy;
        @Element(required = false , name = "idms-token-endpoint")
            protected String idmsTokenEndpoint;
        @Element(required = false,name="gms")
            protected String gms;
        @Element(required = false,name="cms")
            protected String cms;
        @Element(required = false,name="kms")
            protected String kms;
        @Element(required = false,name="tls-tunnel-auth-method")
        protected AuthMethodType tlsTunnelAuthMethod;


        public AuthMethodType getTlsTunnelAuthMethod() {
            return tlsTunnelAuthMethod;
        }

        public void setTlsTunnelAuthMethod(AuthMethodType tlsTunnelAuthMethod) {
            this.tlsTunnelAuthMethod = tlsTunnelAuthMethod;
        }

        public String getHttpProxy() {
            return httpProxy;
        }

        public void setHttpProxy(String httpProxy) {
            this.httpProxy = httpProxy;
        }

        public String getIdmsAuthEndpoint() {
            return idmsAuthEndpoint;
        }


        public void setIdmsAuthEndpoint(String value) {
            this.idmsAuthEndpoint = value;
        }


        public String getIdmsTokenEndpoint() {
            return idmsTokenEndpoint;
        }


        public void setIdmsTokenEndpoint(String value) {
            this.idmsTokenEndpoint = value;
        }


        public String getGms() {
            return gms;
        }


        public void setGms(String value) {
            this.gms = value;
        }


        public String getCms() {
            return cms;
        }


        public void setCms(String value) {
            this.cms = value;
        }


        public String getKms() {
            return kms;
        }


        public void setKms(String value) {
            this.kms = value;
        }

    }

    @Root(strict=false, name = "tls-tunnel-auth-method")
    public static class AuthMethodType {

        @Element(required = false,name="key")
        protected String key;
        @Element(required = false,name="x509")
        protected String x509;
        @Element(required = false,name="mutual-authentication")
        protected boolean mutualAuthentication;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getX509() {
            return x509;
        }

        public void setX509(String x509) {
            this.x509 = x509;
        }

        public boolean isMutualAuthentication() {
            return mutualAuthentication;
        }

        public void setMutualAuthentication(boolean mutualAuthentication) {
            this.mutualAuthentication = mutualAuthentication;
        }
    }



    @Root(strict=false, name = "HPLM")
    public static class HPLM {

        @Element(required = false , name = "service")
        protected ServiceType service;
        @ElementList(required=false,inline=true,entry = "VPLM")
        protected List<VPLMType> vplm;
        @Attribute(required = false , name = "PLMN")
        protected String plmn;


        public ServiceType getService() {
            return service;
        }


        public void setService(ServiceType value) {
            this.service = value;
        }


        public List<VPLMType> getVPLM() {
            if (vplm == null) {
                vplm = new ArrayList<VPLMType>();
            }
            return this.vplm;
        }


        public String getPLMN() {
            return plmn;
        }


        public void setPLMN(String value) {
            this.plmn = value;
        }

    }



    @Root(strict=false, name = "Timers")
    public static class Timers {

        @Element(required = false , name = "T100")
        protected short t100;
        @Element(required = false , name = "T101")
        protected short t101;
        @Element(required = false , name = "T103")
        protected short t103;
        @Element(required = false , name = "T104")
        protected short t104;
        @Element(required = false , name = "T132")
        protected short t132;


        public short getT100() {
            return t100;
        }


        public void setT100(short value) {
            this.t100 = value;
        }


        public short getT101() {
            return t101;
        }


        public void setT101(short value) {
            this.t101 = value;
        }


        public short getT103() {
            return t103;
        }


        public void setT103(short value) {
            this.t103 = value;
        }


        public short getT104() {
            return t104;
        }


        public void setT104(short value) {
            this.t104 = value;
        }


        public short getT132() {
            return t132;
        }


        public void setT132(short value) {
            this.t132 = value;
        }


    }

}
