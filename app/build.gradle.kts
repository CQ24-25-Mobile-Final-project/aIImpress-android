/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Suppress("DSL_SCOPE_VIOLATION") // Remove when fixed https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.0.21"
//    id("com.google.gms.google-services") version "4.4.2"
}

android {
    namespace = "com.hcmus"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hcmus"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.hcmus.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Enable room auto-migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.ui.text.google.fonts)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.google.code.gson:gson:2.11.0")
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt and instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    // Hilt and Robolectric tests.
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)

    // Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    // Tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    // Instrumented tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests: jUnit rules and runners

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit)

    implementation(libs.serializable)
    implementation(libs.retrofit.kotlin.serialization)

//    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
//    implementation("com.google.firebase:firebase-auth")
//    implementation("com.firebaseui:firebase-ui-auth:7.2.0")

    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.compose.ui:ui:1.4.0")
    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.navigation:navigation-compose:2.5.0")
    implementation ("androidx.activity:activity-compose:1.7.0")
    implementation ("com.google.accompanist:accompanist-insets:0.28.0")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation ("androidx.compose.foundation:foundation:1.5.0")// or the latest version available
    implementation ("androidx.compose.ui:ui:1.5.0") // Make sure these are up to date
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.5.0")
    implementation ("androidx.compose.material:material-icons-extended:1.4.0")

    implementation ("androidx.compose.material:material-icons-core:1.4.0")
}
