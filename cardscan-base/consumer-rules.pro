# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


#********************* TensorFlow Rule *********************
-dontwarn org.tensorflow.lite.**
-keep class org.tensorflow.lite.** { *; }
-keepclassmembers class org.tensorflow.lite.** {
   public *;
   private *;
   protected *;
}
# keep class BuildConfig
-keep public class **.BuildConfig { *; }

# keep class members of R
-keepclassmembers class **.R$* {public static <fields>;}

-keepclassmembernames class * {
    public protected private <methods>;
}

#********************* TensorFlow Rule End *********************
