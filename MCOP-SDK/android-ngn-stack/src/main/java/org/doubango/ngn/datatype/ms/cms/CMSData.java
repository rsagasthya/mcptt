

package org.doubango.ngn.datatype.ms.cms;

import org.doubango.ngn.datatype.ms.cms.mcpttServiceConfig.ServiceConfigurationInfoType;
import org.doubango.ngn.datatype.ms.cms.mcpttUEConfig.McpttUEConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUEInitConfig.McpttUEInitialConfiguration;
import org.doubango.ngn.datatype.ms.cms.mcpttUserProfile.McpttUserProfile;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(strict=false, name = "cms-data")
public class CMSData {
    @Element(required = false , name = "etag")
    protected String etag;
    @Element(required = false , name = "path")
    protected String path;
    @Element(required = false, name = "mcptt-UE-initial-configuration")
    protected McpttUEInitialConfiguration dataMcpttUEInitialConfiguration;
    @Element(required = false, name = "mcptt-UE-configuration")
    protected McpttUEConfiguration dataMcpttUEConfiguration;
    @Element(required = false, name = "mcptt-user-profile")
    protected McpttUserProfile dataMcpttUserProfile;
    @Element(required = false,name =  "service-configuration-info")
    protected ServiceConfigurationInfoType dataServiceConfigurationInfoType;




    @Element(required = false,name= "date")
    protected String date;
    @Attribute(required = false , name = "id")
    protected String id;

    public CMSData() {
    }

    public CMSData(String etag,String path, Object dataMCPTTCMS) {
        setPath(path);
        setEtag(etag);
        setDataMCPTTCMS(dataMCPTTCMS);
    }



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path.trim().replaceAll("\"","");
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Object getDataMCPTTCMS() {
        if(dataMcpttUEInitialConfiguration!=null)return dataMcpttUEInitialConfiguration;
        if(dataMcpttUEConfiguration!=null)return dataMcpttUEConfiguration;
        if(dataMcpttUserProfile!=null)return dataMcpttUserProfile;
        if(dataServiceConfigurationInfoType!=null)return dataServiceConfigurationInfoType;



        return null;
    }

    public void setDataMCPTTCMS(Object dataMCPTTCMS) {
        if(dataMCPTTCMS instanceof McpttUEInitialConfiguration)this.dataMcpttUEInitialConfiguration=(McpttUEInitialConfiguration) dataMCPTTCMS;
        if(dataMCPTTCMS instanceof McpttUEConfiguration)this.dataMcpttUEConfiguration=(McpttUEConfiguration) dataMCPTTCMS;
        if(dataMCPTTCMS instanceof McpttUserProfile)this.dataMcpttUserProfile=(McpttUserProfile) dataMCPTTCMS;
        if(dataMCPTTCMS instanceof ServiceConfigurationInfoType)this.dataServiceConfigurationInfoType=(ServiceConfigurationInfoType) dataMCPTTCMS;



    }

    public McpttUEInitialConfiguration getMcpttUEInitialConfiguration() {
        return dataMcpttUEInitialConfiguration;
    }

    public McpttUEConfiguration getMcpttUEConfiguration() {
        return dataMcpttUEConfiguration;
    }

    public McpttUserProfile getMcpttUserProfile() {
        return dataMcpttUserProfile;
    }





    public ServiceConfigurationInfoType getServiceConfigurationInfoType() {
        return dataServiceConfigurationInfoType;
    }






    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
