package org.doubango.ngn.datatype.ms.gms.ns;

import org.doubango.ngn.datatype.ms.gms.ns.list_service.Group;

public class GMSData {

    private Group group;
    private String etag;

    public GMSData(Group group, String etag) {
        this.group = group;
        this.etag = etag;
    }


    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
