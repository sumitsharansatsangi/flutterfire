import com.android.build.api.dsl.LibraryExtension
import com.android.Version
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "io.flutter.plugins.firebase.analytics"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

apply(from = "local-config.gradle.kts")

val compileSdkValue = extra["compileSdk"] as Int
val minSdkValue = extra["minSdk"] as Int
val javaVersion: JavaVersion by extra

val agpMajor =
    Version.ANDROID_GRADLE_PLUGIN_VERSION.substringBefore('.').toInt()

val builtInKotlin =
    providers.gradleProperty("android.builtInKotlin")
        .map(String::toBoolean)
        .orElse(agpMajor >= 9)
        .get()

if (agpMajor < 9 || !builtInKotlin) {
    apply(plugin = "org.jetbrains.kotlin.android")
}

repositories {
    google()
    mavenCentral()
}

val firebaseCoreProject =
    findProject(":firebase_core")
        ?: throw GradleException(
            "Could not find the firebase_core FlutterFire plugin, " +
                "have you added it as a dependency in your pubspec?"
        )

if (firebaseCoreProject.findProperty("FirebaseSDKVersion") == null) {
    throw GradleException(
        "A newer version of the firebase_core FlutterFire plugin is required, " +
            "please update your firebase_core pubspec dependency."
    )
}

fun getRootProjectExtOrCoreProperty(
    name: String,
    firebaseCoreProject: Project
): Any {
    val flutterFire = rootProject.extensions.extraProperties
        .takeIf { it.has("FlutterFire") }
        ?.get("FlutterFire") as? Map<*, *>

    return flutterFire?.get(name)
        ?: firebaseCoreProject.findProperty(name)
        ?: throw GradleException("Property '$name' not found.")
}

extensions.configure<LibraryExtension>("android") {
    namespace = "io.flutter.plugins.firebase.analytics"

    compileSdk =
        compileSdkValue

    defaultConfig {
        minSdk =
            minSdkValue

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility =
            javaVersion

        targetCompatibility =
            javaVersion
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        disable += "InvalidPackage"
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(
                JvmTarget.fromTarget(
                    javaVersion.majorVersion
                )
            )
        }
    }
}

dependencies {
    api(firebaseCoreProject)

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

    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.annotation:annotation:1.7.0")
}

apply(from = "./user-agent.gradle.kts")
