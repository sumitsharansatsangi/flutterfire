import com.android.build.api.dsl.LibraryExtension

group = "io.flutter.plugins.firebase.core"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

apply(from = "local-config.gradle.kts")

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

val rootCompileSdk = getRootOrProjectExt("compileSdk").toString().toInt()
val rootMinSdk = getRootOrProjectExt("minSdk").toString().toInt()
val rootJavaVersion = JavaVersion.toVersion(getRootOrProjectExt("javaVersion"))

fun getRootProjectExtOrDefaultProperty(name: String): String {
    val extra = rootProject.extensions.extraProperties

    if (!extra.has("FlutterFire")) {
        return project.findProperty(name)?.toString()
            ?: error("Property '$name' not found")
    }

    val flutterFire = extra["FlutterFire"] as? Map<*, *>

    return flutterFire?.get(name)?.toString()
        ?: project.findProperty(name)?.toString()
        ?: error("Property '$name' not found")
}

extensions.configure<LibraryExtension> {
    namespace = "io.flutter.plugins.firebase.core"

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
                getRootProjectExtOrDefaultProperty("FirebaseSDKVersion")
            }"
        )
    )

    implementation("com.google.firebase:firebase-common")
    implementation("androidx.annotation:annotation:1.7.0")
}

apply(from = "user-agent.gradle.kts")
