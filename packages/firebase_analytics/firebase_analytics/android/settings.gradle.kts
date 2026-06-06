pluginManagement {
    val androidGradlePluginVersion =
        providers.gradleProperty("androidGradlePluginVersion")
            .orElse("9.2.1")
            .get()

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version androidGradlePluginVersion
        id("com.android.library") version androidGradlePluginVersion
        id("org.jetbrains.kotlin.android") version "2.3.21"
    }
}

rootProject.name = "firebase_analytics"
