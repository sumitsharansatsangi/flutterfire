rootProject.name = "firebase_crashlytics"

apply(from = "local-config.gradle.kts")

val androidGradlePluginVersion: String by extra

pluginManagement {
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