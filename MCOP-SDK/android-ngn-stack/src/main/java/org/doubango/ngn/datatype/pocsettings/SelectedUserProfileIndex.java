package org.doubango.ngn.datatype.pocsettings;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

@Root(strict=false, name = "selected-user-profile-index")
@Namespace(reference = "urn:3gpp:mcsSettings:1.0") // Add your reference here!
public class SelectedUserProfileIndex {

    @Element(required=false,name = "user-profile-index")
    private int userProfileIndex;

    public int getUserProfileIndex() {
        return userProfileIndex;
    }

    public void setUserProfileIndex(int userProfileIndex) {
        this.userProfileIndex = userProfileIndex;
    }
}
