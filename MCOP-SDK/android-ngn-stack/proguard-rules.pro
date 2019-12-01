

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.ofscript.interface.for.webview {
#   public *;
#}
# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

#For simpleXML
-dontwarn org.simpleframework.xml.**
-keep class org.simpleframework.xml.** {*;}
-keep interface org.simpleframework.xml.** {*;}

#For NgnSipPrefrences
-keepclassmembers class * { *;}
-keep class org.doubango.ngn.sip.NgnSipPrefrences { public *; }
-keep interface org.doubango.ngn.sip.** {
   public *;
}

#For Listener
-keepclassmembers interface org.doubango.** {*;}
-keepclassmembers class org.doubango.** {*;}

#For datatype
-keep class org.doubango.ngn.datatype.*** { public *;}
-keep enum org.doubango.ngn.datatype.*** { public *; }
-keep interface org.doubango.ngn.datatype.*** { public *;}
#For services
-keep interface org.doubango.ngn.services.** { public *; }

-keep class org.doubango.ngn.utils.** { public *; }
-keep class org.doubango.ngn.NgnEngine { public *; }

-keep public class org.doubango.tinyWRAP.** { *; }
-dontwarn org.doubango.tinyWRAP.**

-keep class org.doubango.ngn.sip.NgnAVSession {
  public *;
}

#-keep enum org.doubango.** {
#    *;
#}
#-keep interface org.doubango.** {
#    *;
#}
#For code native
-keep class * {
    native <methods>;
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}





-keep enum org.doubango.ngn.** {
   public *;
}
-keep class org.doubango.ngn.services.impl.profiles.ProfilesUtils {
   public *;
}


-keep class org.doubango.** {
    public static *;
    public ** getMediaType();
}
#-keep class org.doubango.** extends NgnEventArgs {
#    public *;
#}

-keep class org.doubango.ngn.events.** {
    public *;
}



-keep class org.doubango.ngn.events.NgnRegistrationEventArgs {
    public *;
}




