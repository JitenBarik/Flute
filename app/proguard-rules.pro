# Proguard rules for Flute
-keepattributes *Annotation*

# Retrofit
-keepattributes Signature
-keepattributes Exceptions
-keep class com.flute.music.data.model.** { *; }
-keep class com.flute.music.data.api.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Media3
-keep class androidx.media3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
