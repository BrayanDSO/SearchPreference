plugins {
    id("com.android.library")
    id("maven-publish")
    id("org.jetbrains.kotlin.android") version "2.1.0"
}

android {
    namespace = "com.bytehamster.lib.preferencesearch"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        targetSdk = 35
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.txt",
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
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("org.apache.commons:commons-text:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
}

// Install Git pre-commit hook
tasks.register<Copy>("installGitHook") {
    from(file("${rootProject.rootDir}/pre-commit"))
    into(file("${rootProject.rootDir}/.git/hooks"))
    filePermissions {
        user {
            read = true
            write = true
            execute = true
        }
    }
}

tasks.named("preBuild") {
    dependsOn("installGitHook")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.BrayanDSO"
                artifactId = "SearchPreference"
                version = "3.0.2"
            }
        }
    }
}
