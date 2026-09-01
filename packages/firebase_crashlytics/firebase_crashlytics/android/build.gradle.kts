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

val rootCompileSdk: Int by extra
val rootMinSdk: Int by extra
val rootJavaVersion: JavaVersion by extra

android {
    namespace = "io.flutter.plugins.firebase.crashlytics"

    compileSdk = rootCompileSdk

    defaultConfig {
        minSdk = rootMinSdk
        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = rootJavaVersion
        targetCompatibility = rootJavaVersion
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
