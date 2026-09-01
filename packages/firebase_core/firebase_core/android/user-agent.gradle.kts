import com.android.build.api.dsl.LibraryExtension
import java.io.File

val libraryName = "flutter-fire-core"

val libraryVersionName = run {
    val pubspec = File(project.projectDir.parentFile, "pubspec.yaml")

    if (pubspec.exists()) {
        val match = Regex(
            "^version:\\s*['\"]?([^\\n'\"]*)['\"]?$",
            RegexOption.MULTILINE
        ).find(pubspec.readText())

        match?.groupValues?.get(1)?.replace("+", "-") ?: "UNKNOWN"
    } else {
        "UNKNOWN"
    }
}

extensions.configure<LibraryExtension> {
    defaultConfig {
        buildConfigField(
            "String",
            "LIBRARY_VERSION",
            "\"$libraryVersionName\""
        )

        buildConfigField(
            "String",
            "LIBRARY_NAME",
            "\"$libraryName\""
        )
    }
}
