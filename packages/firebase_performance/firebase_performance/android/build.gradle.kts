import com.android.Version
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "io.flutter.plugins.firebaseperformance"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

apply(from = "local-config.gradle.kts")

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

rootProject.allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val firebaseCoreProject =
    findProject(":firebase_core")
        ?: throw GradleException(
            "Could not find the firebase_core FlutterFire plugin, " +
                "have you added it as a dependency in your pubspec?"
        )

if (!firebaseCoreProject.properties.containsKey("FirebaseSDKVersion")) {
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
        ?: firebaseCoreProject.properties[name]
        ?: throw GradleException("Property '$name' not found.")
}

val projectCompileSdk = extra["compileSdk"] as Int
val projectMinSdk = extra["minSdk"] as Int
val projectJavaVersion = extra["javaVersion"] as JavaVersion

android {

    namespace = "io.flutter.plugins.firebase.performance"

    compileSdk = projectCompileSdk

    defaultConfig {
        minSdk = projectMinSdk

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = projectJavaVersion

        targetCompatibility = projectJavaVersion
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/kotlin")
        }

        getByName("test") {
            java.srcDir("src/test/kotlin")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        disable += "InvalidPackage"
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

    implementation("com.google.firebase:firebase-perf")

    implementation("androidx.annotation:annotation:1.7.0")
}

plugins.withId("org.jetbrains.kotlin.android") {

    kotlin {
        compilerOptions {
            jvmTarget.set(
                JvmTarget.fromTarget(
                    projectJavaVersion.majorVersion
                )
            )
        }
    }
}

apply(from = "./user-agent.gradle.kts")
