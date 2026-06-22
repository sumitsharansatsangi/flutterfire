import com.android.build.api.dsl.LibraryExtension

group = "io.flutter.plugins.firebasemessaging"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

apply(from = "local-config.gradle.kts")

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

val firebaseCoreProject = findProject(":firebase_core")
    ?: throw GradleException(
        "Could not find the firebase_core FlutterFire plugin, have you added it as a dependency in your pubspec?"
    )

if (!firebaseCoreProject.properties.containsKey("FirebaseSDKVersion")) {
    throw GradleException(
        "A newer version of the firebase_core FlutterFire plugin is required, please update your firebase_core pubspec dependency."
    )
}

fun getRootOrProjectExt(name: String): Any {
    val rootExtra = rootProject.extensions.extraProperties
    val projectExtra = project.extensions.extraProperties

    return when {
        rootExtra.has(name) -> rootExtra[name]
        projectExtra.has(name) -> projectExtra[name]
        project.hasProperty(name) -> project.property(name)
        else -> error("Property '$name' not found")
    } ?: error("Property '$name' is null")
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

val rootCompileSdk = getRootOrProjectExt("compileSdk").toString().toInt()
val rootMinSdk = getRootOrProjectExt("minSdk").toString().toInt()
val rootJavaVersion = JavaVersion.toVersion(getRootOrProjectExt("javaVersion"))

extensions.configure<LibraryExtension> {
    namespace = "io.flutter.plugins.firebase.messaging"

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
    implementation("com.google.firebase:firebase-messaging")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("androidx.annotation:annotation:1.7.1")
}

apply(from = "user-agent.gradle.kts")
