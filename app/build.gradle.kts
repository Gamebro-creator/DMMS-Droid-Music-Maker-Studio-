plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.dmms.app"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.dmms.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
    }
}
dependencies {
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.2.0")
}