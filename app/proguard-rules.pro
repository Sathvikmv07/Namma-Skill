# Firebase ProGuard rules
-keep class com.google.firebase.** { *; }
-keep class com.nammaskill.app.data.model.** { *; }

# Glide ProGuard rules
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Lottie ProGuard rules
-keep class com.airbnb.lottie.** { *; }
