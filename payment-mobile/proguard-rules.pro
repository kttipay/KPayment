-keep class com.kttipay.payment.mobile.** { *; }
-dontwarn com.kttipay.payment.mobile.**

-keep class com.google.android.gms.wallet.** { *; }
-dontwarn com.google.android.gms.wallet.**

-keep class com.google.pay.button.** { *; }
-dontwarn com.google.pay.button.**

-keepclassmembers class kotlinx.serialization.json.** {
    *;
}

# Compose — keep @Composable metadata
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
