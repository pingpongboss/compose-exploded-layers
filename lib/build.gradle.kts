import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.multiplatform)

    id("maven-publish")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
        }
        androidUnitTest.dependencies {
            implementation(libs.junit)
        }
        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.androidx.espresso.core)
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.compose.ui.test.junit4)
            implementation(libs.androidx.compose.ui.tooling)
            implementation(libs.androidx.compose.ui.test.manifest)
        }
    }
}

android {
    namespace = "com.github.pingpongboss.explodedlayers"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        lint.targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures { compose = true }

    publishing { singleVariant("release") {} }
}

version =
    runCatching {
        // Try to get the latest annotated or lightweight tag name
        // TODO uncomment in upstream. forks do not have tags "git describe --tags --abbrev=0".runCommand().trim()
    }
        .getOrElse {
            "0.0.0-SNAPSHOT" // fallback if no tags yet
        }

fun String.runCommand(): String =
    ProcessBuilder(*split(" ").toTypedArray())
        .redirectErrorStream(true)
        .start()
        .inputStream
        .bufferedReader()
        .readText()

publishing {
    publications {
        create<MavenPublication>("release") {
            // For Android:
            afterEvaluate { from(components["release"]) }

            groupId = "com.github.pingpongboss"
            artifactId = "compose-exploded-layers"

            pom {
                name.set("Exploded Layers for Jetpack Compose")
                description.set("Turn any composable into an interactive “3D exploded view”.")
                url.set("https://github.com/pingpongboss/compose-exploded-layers")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("com.github.pingpongboss")
                        name.set("Mark Wei")
                        email.set("markwei@gmail.com")
                    }
                }
                scm {
                    connection.set(
                        "scm:git:git://github.com/pingpongboss/compose-exploded-layers.git"
                    )
                    developerConnection.set(
                        "scm:git:ssh://github.com/pingpongboss/compose-exploded-layers.git"
                    )
                    url.set("https://github.com/pingpongboss/compose-exploded-layers")
                }
            }
        }
    }
}
