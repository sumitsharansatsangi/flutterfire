import java.util.regex.Pattern

var libraryVersionName = "UNKNOWN"
val libraryName = "flutter-fire-perf"

val pubspec = project.projectDir.parentFile.resolve("pubspec.yaml")

if (pubspec.exists()) {
    val yaml = pubspec.readText()

    val matcher = Pattern.compile(
        "^version:\\s*['\"]?([^\\n'\"]*)['\"]?$",
        Pattern.MULTILINE
    ).matcher(yaml)

    if (matcher.find()) {
        libraryVersionName = matcher.group(1).replace("+", "-")
    }
}

android {
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