// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

version =
    runCatching {
            // Try to get the latest annotated or lightweight tag name
            "git describe --tags --abbrev=0".runCommand().trim()
        }
        .getOrElse {
            "0.0.0-SNAPSHOT" // fallback if no tags yet
        }

private fun String.runCommand(): String =
    ProcessBuilder(*split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()
