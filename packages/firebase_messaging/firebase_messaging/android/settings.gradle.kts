pluginManagement {
    val androidGradlePluginVersion = "9.2.1"

    plugins {
        id("com.android.application") version androidGradlePluginVersion
        id("com.android.library") version androidGradlePluginVersion
    }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "firebase_messaging"

apply(from = "local-config.gradle.kts")
