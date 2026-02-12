# Public API — sealed classes, enums, data classes used by consumers
-keep class com.kttipay.payment.api.** { *; }
-keep class com.kttipay.payment.model.** { *; }
-keep class com.kttipay.payment.capability.** { *; }

# kotlinx.serialization — keep generated serializers for @Serializable classes
-keepattributes *Annotation*, InnerClasses
-keepclassmembers class com.kttipay.payment.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.kttipay.payment.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.kttipay.payment.model.**$$serializer { *; }

# kotlinx.serialization runtime
-keepclassmembers class kotlinx.serialization.json.** { *; }
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
