# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Eduardo\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class com.google.code.gson.** { *; }
-keep interface com.google.code.gson.** { *; }
-keep class org.ietf.** { *; }
-keep interface org.ietf.** { *; }
-keep class javax.xml.** { *; }
-keep interface javax.xml.** { *; }

-dontwarn org.slf4j.impl.**
-keep public class org.simpleframework.**{ *; }
-keep class org.simpleframework.xml.**{ *; }
-keep class org.simpleframework.xml.core.**{ *; }
-keep class org.simpleframework.xml.util.**{ *; }
# (1)Annotations and signatures
-keepattributes *Annotation*
-keepattributes Signature
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-dontwarn javax.xml.stream.**
#For doubango
-keep public class org.doubango.tinyWRAP.** { *; }
-dontwarn org.doubango.tinyWRAP.**
#For datatype
-keep class org.doubango.ngn.datatype.** { public *;}
-keep enum org.doubango.ngn.datatype.** { public *; }
-keep interface org.doubango.ngn.datatype.** { public *;}
-keep class org.doubango.ngn.sip.NgnSipPrefrences {public *;}
