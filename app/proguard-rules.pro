# ProGuard / R8 Keep Rules for Notora

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt & Dagger Keep Rules
-keepclassmembers,allowobfuscation class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# KotlinX Serialization Keep Rules
-keepattributes *Annotation*,ElementValueAttribute,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keepclassmembers class *$$serializer {
    *;
}

# Coil Keep Rules
-keep class io.coilkt.** { *; }
-dontwarn io.coilkt.**
