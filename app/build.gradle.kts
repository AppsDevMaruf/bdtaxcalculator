import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.isFile) {
        localPropertiesFile.inputStream().use(::load)
    }
}

fun secretProperty(name: String): String {
    return providers.gradleProperty(name)
        .orElse(localProperties.getProperty(name) ?: "")
        .get()
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
}

android {
    namespace = "com.maruf.bdtaxcalculator"
    compileSdk = 37

    @Suppress("UnstableApiUsage")
    androidResources {
        generateLocaleConfig = true
        localeFilters += listOf("en", "bn")
    }

    defaultConfig {
        applicationId = "com.maruf.bdtaxcalculator"
        minSdk = 24
        targetSdk = 37
        versionCode = 24
        versionName = "1.1.24"

        buildConfigField("String", "TIKTOK_APP_ID", "\"${secretProperty("TIKTOK_APP_ID")}\"")
        buildConfigField("String", "TIKTOK_TT_APP_ID", "\"${secretProperty("TIKTOK_TT_APP_ID")}\"")
        buildConfigField("String", "TIKTOK_APP_SECRET", "\"${secretProperty("TIKTOK_APP_SECRET")}\"")
        buildConfigField("String", "TIKTOK_TEST_EVENT_CODE", "\"${secretProperty("TIKTOK_TEST_EVENT_CODE")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}


dependencies {

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.config)
    implementation(libs.play.app.update)
    implementation(libs.play.app.update.ktx)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    implementation(libs.tiktok.business.sdk)
    implementation(libs.android.installreferrer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
