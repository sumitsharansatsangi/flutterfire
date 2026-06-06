import com.android.build.api.dsl.LibraryExtension

val libraryName = "flutter-fire-analytics"

val libraryVersionName = project.projectDir.parentFile
    .resolve("pubspec.yaml")
    .takeIf { it.exists() }
    ?.readText()
    ?.let { yaml ->
        Regex(
            "^version:\\s*['\"]?([^\\n'\"]*)['\"]?$",
            RegexOption.MULTILINE
        )
            .find(yaml)
            ?.groupValues
            ?.get(1)
            ?.replace("+", "-")
    }
    ?: "UNKNOWN"

extensions.configure<LibraryExtension>("android") {
    defaultConfig {
        buildConfigField("String", "LIBRARY_VERSION", "\"$libraryVersionName\"")
        buildConfigField("String", "LIBRARY_NAME", "\"$libraryName\"")
    }
}
