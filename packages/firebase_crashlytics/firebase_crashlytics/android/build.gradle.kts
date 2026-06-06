group = "io.flutter.plugins.firebase.crashlytics"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

apply(from = "local-config.gradle.kts")

repositories {
    google()
    mavenCentral()
}

val firebaseCoreProject = findProject(":firebase_core")
    ?: throw GradleException(
        "Could not find the firebase_core FlutterFire plugin, have you added it as a dependency in your pubspec?"
    )

if (!firebaseCoreProject.properties.containsKey("FirebaseSDKVersion")) {
    throw GradleException(
        "A newer version of the firebase_core FlutterFire plugin is required, please update your firebase_core pubspec dependency."
    )
}

fun getRootProjectExtOrCoreProperty(
    name: String,
    firebaseCoreProject: Project,
): Any {
    val flutterFire =
        rootProject.extensions.extraProperties
            .properties["FlutterFire"] as? Map<*, *>

    return flutterFire?.get(name)
        ?: firebaseCoreProject.properties[name]
        ?: throw GradleException("Property '$name' not found")
}

val compileSdk: Int by extra
val minSdk: Int by extra
val javaVersion: JavaVersion by extra

android {
    namespace = "io.flutter.plugins.firebase.crashlytics"

    compileSdk = compileSdk

    defaultConfig {
        minSdk = minSdk
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        disable += "InvalidPackage"
    }
}

dependencies {
    implementation(
        platform(
            "com.google.firebase:firebase-bom:${
                getRootProjectExtOrCoreProperty(
                    "FirebaseSDKVersion",
                    firebaseCoreProject
                )
            }"
        )
    )

    implementation("com.google.firebase:firebase-crashlytics")
    implementation("androidx.annotation:annotation:1.9.1")
}

apply(from = "user-agent.gradle.kts")