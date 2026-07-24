plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.aexon"
    compileSdk = 36
    

    defaultConfig {
        applicationId = "com.aexon"
        minSdk = 26
        targetSdk = 36
        versionCode = 2017
        versionName = "1.3"
     }

    buildFeatures { viewBinding = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
     }

    kotlinOptions { jvmTarget = "17" }

        
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
