plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.gideon.shambaiq"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.gideon.shambaiq"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



//        buildFeatures {
//            compose = true
//        }
//
//        composeOptions {
//            kotlinCompilerExtensionVersion = "1.5.8"
//        }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.loopj.android:android-async-http:1.4.11")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services-location:21.4.0")




    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")


//
//
//        // Core Android lifecycle tools
//        implementation("androidx.core:core-ktx:1.13.1")
//        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
//        implementation("androidx.activity:activity-compose:1.9.0")
//
//    TestImplementation(composeBom)
////
//    implementation("androidx.compose.ui:ui")
//    implementation("androidx.compose.ui:ui-graphics")
//    implementation("androidx.compose.ui:ui-tooling-preview")
//    implementation("androidx.compose.material3:material3")
//
//    // Debug tools
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")     // Jetpack Compose Platform & UI Libraries
//        val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
//        implementation(composeBom)
//        android

}