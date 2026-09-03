# Project-specific R8 rules for production release builds.
#
# Android framework entry points, Compose, Firebase, and Play Core already ship
# consumer rules through their dependencies. Keep this file focused on app code
# that can be reached dynamically or is needed for readable crash reporting.

# Preserve source locations so Crashlytics can deobfuscate release stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations/signatures that libraries may inspect at runtime.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Manifest/service entry points. The Android Gradle Plugin usually protects
# these, but keeping them explicit avoids fragile release-only startup issues.
-keep class com.maruf.bdtaxcalculator.BDTaxApplication { *; }
-keep class com.maruf.bdtaxcalculator.firebase.TaxProMessagingService { *; }
-keep class com.maruf.bdtaxcalculator.tiktok.TikTokEventsTracker { *; }

# TikTok App Events SDK uses runtime-loaded event/config classes.
-keep class com.tiktok.** { *; }
-keep class com.android.billingclient.api.** { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn com.tiktok.**

# Keep Parcelable creators if Parcelable models are added later.
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Remove verbose/debug/info logs from release builds. Keep warnings/errors for
# production diagnostics and Play pre-launch report readability.
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
