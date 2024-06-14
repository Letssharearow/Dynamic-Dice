plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsKotlinAndroid)
  id("com.google.gms.google-services")
  id("org.jetbrains.kotlin.plugin.serialization")
}

android {
  namespace = "com.example.dynamicdiceprototype"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.dynamicdiceprototype"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.font.awesome)
  testImplementation(libs.junit)
  testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)
  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  implementation("androidx.compose.runtime:runtime-livedata:${libs.androidx.compose.bom}")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
  //  implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
  //  implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
  implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
  implementation("com.google.firebase:firebase-analytics")
  // Declare the dependency for the Cloud Firestore library
  // When using the BoM, you don't specify versions in Firebase library dependencies
  implementation("com.google.firebase:firebase-firestore")
  implementation("io.coil-kt:coil-compose:1.4.0")

  // Flow
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
  implementation("com.github.skydoves:colorpicker-compose:1.0.7")

  // datastore
  implementation(libs.kotlinx.collections.immutable)
  implementation(libs.androidx.datastore)
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
