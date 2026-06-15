# Retrofit rules
-keepattributes Signature, InnerClasses, AnnotationDefault
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp3 rules
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**

# Gson rules
-keepattributes *Annotation*, Signature
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# App Models rules
-keep class com.halombg.mobile.model.** { *; }
-keep class com.halombg.mobile.data.api.** { *; }
