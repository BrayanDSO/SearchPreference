plugins {
    id("com.android.application")
    id("maven-publish")
    id("org.jetbrains.kotlin.android") version "2.1.0"
}

android {
    namespace = "com.bytehamster.preferencesearch"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bytehamster.preferencesearch"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            proguardFiles(
                    getDefaultProguardFile("proguard-android.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    implementation(project(":lib"))
}
