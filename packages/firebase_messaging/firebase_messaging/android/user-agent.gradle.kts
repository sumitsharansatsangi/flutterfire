import com.android.build.api.dsl.LibraryExtension
import java.io.File

val libraryName = "flutter-fire-fcm"

val libraryVersionName = run {
    val pubspec = File(project.projectDir.parentFile, "pubspec.yaml")

    if (!pubspec.exists()) {
        "UNKNOWN"
    } else {
        Regex("""^version:\s*['"]?([^\n'"]*)['"]?$""", RegexOption.MULTILINE)
            .find(pubspec.readText())
            ?.groupValues
            ?.getOrNull(1)
            ?.replace("+", "-")
            ?: "UNKNOWN"
    }
}

extensions.configure<LibraryExtension> {
    defaultConfig {
        buildConfigField("String", "LIBRARY_VERSION", "\"$libraryVersionName\"")
        buildConfigField("String", "LIBRARY_NAME", "\"$libraryName\"")
    }
}
