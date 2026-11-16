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

-keep class dev.johnoreilly.** { *; }
-dontwarn okhttp3.internal.Util

# ML Kit (its bundled firebase-components) creates component registrars by reflection from
# manifest metadata, so R8 sees their no-arg constructors as unused and removes them. ML Kit then
# starts with no components, and Generation.getClient() throws on Android 16+ (1.0.17 crashed
# at launch: "Could not instantiate com.google.mlkit.common.internal.CommonComponentRegistrar").
-keep class * implements com.google.firebase.components.ComponentRegistrar { <init>(); }
