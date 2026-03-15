plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.docvault.test.utils"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android.testing)
    implementation(libs.room.testing)

    implementation(project(":data"))
    implementation(project(":libraries:lib-security"))
    implementation(project(":core:core-navigation"))
    implementation(project(":core:core-navigation"))

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(project(":test-utils"))
    kspAndroidTest(libs.hilt.compiler)
}